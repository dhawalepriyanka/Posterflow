package com.example.auth

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.time.Instant
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthRepository(
    private val context: Context,
    private val sessionStore: AuthSessionStore = AuthSessionStore(context),
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun restoreSession(): Pair<AuthUser, UserProfile?>? {
        val firebaseUser = firebaseAuth.currentUser ?: run {
            sessionStore.clear()
            return null
        }
        val user = firebaseUser.toAuthUser()
        val profile = sessionStore.restoreProfile(user.id)
        sessionStore.save(user, profile)
        return user to profile
    }

    suspend fun signInWithGoogle(activity: Activity): Result<Pair<AuthUser, UserProfile?>> {
        return runCatching {
            val webClientId = readDefaultWebClientId()
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = CredentialManager.create(activity).getCredential(activity, request)
            val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("Firebase Google sign-in did not return a user.")
            val user = firebaseUser.toAuthUser()
            val profile = sessionStore.restoreProfile(user.id)
            sessionStore.save(user, profile)
            user to profile
        }
    }

    suspend fun saveCompletedProfile(profile: UserProfile): Result<UserProfile> {
        return runCatching {
            val completeProfile = profile.copy(
                companyLogo = if (profile.companyLogo.startsWith("content:")) com.example.templates.TemplateImages.import(context, android.net.Uri.parse(profile.companyLogo)) else profile.companyLogo,
                profilePhoto = if (profile.profilePhoto.startsWith("content:")) com.example.templates.TemplateImages.import(context, android.net.Uri.parse(profile.profilePhoto)) else profile.profilePhoto,
                createdDate = profile.createdDate.ifBlank { nowIso() },
                lastLogin = nowIso()
            )
            sessionStore.saveProfile(completeProfile)
            completeProfile
        }
    }

    suspend fun logout() {
        firebaseAuth.signOut()
        sessionStore.clear()
    }

    private fun readDefaultWebClientId(): String {
        val resourceId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        val clientId = if (resourceId != 0) context.getString(resourceId) else ""
        if (clientId.isBlank()) {
            throw IllegalStateException(
                "Firebase Web Client ID is missing. Add SHA-1/SHA-256 fingerprints in Firebase, enable Google sign-in, download a fresh google-services.json, and rebuild."
            )
        }
        return clientId
    }

    private fun FirebaseUser.toAuthUser(): AuthUser {
        return AuthUser(
            id = uid,
            loginType = LoginType.GOOGLE,
            name = displayName.orEmpty().ifBlank { email.orEmpty().substringBefore("@") },
            email = email.orEmpty(),
            phone = phoneNumber.orEmpty(),
            profilePhotoUrl = photoUrl?.toString().orEmpty()
        )
    }

    private fun nowIso(): String = Instant.now().toString()
}

private suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result -> continuation.resume(result) }
        addOnFailureListener { exception -> continuation.resumeWithException(exception) }
        addOnCanceledListener { continuation.cancel() }
    }
}
