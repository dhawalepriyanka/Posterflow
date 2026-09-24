package com.example.templates

import android.content.Context
import android.util.AtomicFile
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Types
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface AdminAuthorizer { suspend fun isAdmin(): Boolean }

/** Assigned only by a trusted Firebase Admin SDK environment, never by this APK. */
class FirebaseAdminAuthorizer : AdminAuthorizer {
    override suspend fun isAdmin(): Boolean {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser ?: return false
        val token = suspendCancellableCoroutine<com.google.firebase.auth.GetTokenResult> { continuation ->
            user.getIdToken(true).addOnSuccessListener { if (continuation.isActive) continuation.resume(it) }
                .addOnFailureListener { if (continuation.isActive) continuation.resumeWithException(it) }
        }
        return auth.currentUser?.uid == user.uid && token.claims["admin"] == true
    }
}

interface TemplateRepository {
    val templates: StateFlow<List<PosterTemplate>>
    suspend fun load()
    suspend fun save(template: PosterTemplate): PosterTemplate
    suspend fun duplicate(template: PosterTemplate): PosterTemplate
    suspend fun setStatus(template: PosterTemplate, status: TemplateStatus)
    suspend fun move(template: PosterTemplate, direction: Int)
}

/** Device-local only. A remote implementation must also enforce claims in server-side rules. */
class LocalTemplateRepository(context: Context, private val authorizer: AdminAuthorizer) : TemplateRepository {
    private val file = AtomicFile(File(context.filesDir, "poster_templates_v1.json"))
    private val adapter = TemplateJson.moshi.adapter<List<PosterTemplate>>(
        Types.newParameterizedType(List::class.java, PosterTemplate::class.java))
    private val mutable = MutableStateFlow(StarterTemplates.all)
    override val templates = mutable.asStateFlow()
    private val mutex = Mutex()
    private var loaded = false
    override suspend fun load() = withContext(Dispatchers.IO) { mutex.withLock { loadLocked() } }
    private fun loadLocked() {
        if (loaded) return
        if (file.baseFile.exists() || File(file.baseFile.path + ".bak").exists()) {
            val saved = file.openRead().bufferedReader().use { adapter.fromJson(it.readText()) }
                ?: error("Template library could not be read. Your existing file has been preserved.")
            mutable.value = (saved + StarterTemplates.all.filter { starter -> saved.none { it.id == starter.id } }).sortedBy { it.order }
        }
        loaded = true
    }
    private fun persist(next: List<PosterTemplate>) {
        val stream = file.startWrite()
        try {
            stream.write(adapter.toJson(next).toByteArray(Charsets.UTF_8))
            file.finishWrite(stream)
            mutable.value = next.sortedBy { it.order }
        } catch (error: Exception) {
            file.failWrite(stream)
            throw error
        }
    }
    override suspend fun save(template: PosterTemplate): PosterTemplate {
        check(authorizer.isAdmin()) { "Administrator access is required. Sign in with an authorized account." }
        template.validationError()?.let { error(it) }
        return withContext(Dispatchers.IO) { mutex.withLock {
            loadLocked()
            val old = mutable.value.firstOrNull { it.id == template.id }
            val saved = template.copy(id = if (template.id == 0) (mutable.value.maxOfOrNull { it.id } ?: 0).coerceAtLeast(0) + 1 else template.id,
                createdAt = old?.createdAt ?: System.currentTimeMillis(), updatedAt = System.currentTimeMillis())
            persist(mutable.value.filterNot { it.id == saved.id } + saved)
            saved
        } }
    }
    override suspend fun duplicate(template: PosterTemplate) = save(template.copy(id = 0, name = "${template.name} copy", status = TemplateStatus.INACTIVE,
        order = mutable.value.filter { it.category == template.category }.maxOfOrNull { it.order }?.plus(1) ?: 0))
    override suspend fun setStatus(template: PosterTemplate, status: TemplateStatus) { save(template.copy(status = status)) }
    override suspend fun move(template: PosterTemplate, direction: Int) {
        check(authorizer.isAdmin()) { "Administrator access is required." }
        withContext(Dispatchers.IO) { mutex.withLock {
            loadLocked()
            val category = mutable.value.filter { it.category == template.category && it.status != TemplateStatus.DELETED }.sortedBy { it.order }.toMutableList()
            val index = category.indexOfFirst { it.id == template.id }
            if (index < 0 || index + direction !in category.indices) return@withLock
            java.util.Collections.swap(category, index, index + direction)
            val ordered = category.mapIndexed { i, item -> item.copy(order = i, updatedAt = System.currentTimeMillis()) }.associateBy { it.id }
            persist(mutable.value.map { ordered[it.id] ?: it })
        } }
    }
}
