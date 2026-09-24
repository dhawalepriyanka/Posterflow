package com.example.auth

import android.content.Context
import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class SupabaseAuthRepository(
    private val context: Context,
    private val sessionStore: AuthSessionStore = AuthSessionStore(context)
) {
    private val client = OkHttpClient()
    private val jsonType = "application/json; charset=utf-8".toMediaType()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val profileAdapter = moshi.adapter(SupabaseProfileDto::class.java)
    private val authResponseAdapter = moshi.adapter(SupabaseAuthResponseDto::class.java)

    fun restoreSession(): Pair<AuthUser, UserProfile?>? {
        val user = sessionStore.restoreUser() ?: return null
        return user to sessionStore.restoreProfile()
    }

    fun isLocalAuthMode(): Boolean {
        return !hasSupabaseConfig()
    }

    suspend fun signInWithGoogle(activity: Activity): Result<Pair<AuthUser, UserProfile?>> {
        return runCatching {
            if (!isGoogleConfigured() || !hasSupabaseConfig()) {
                return@runCatching createLocalGoogleSession()
            }
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = CredentialManager.create(activity).getCredential(activity, request)
            val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val session = exchangeGoogleToken(credential.idToken)
            val supabaseUser = session.user ?: throw IOException("Supabase Google session did not include a user.")
            val user = AuthUser(
                id = supabaseUser.id,
                loginType = LoginType.GOOGLE,
                name = supabaseUser.displayName().ifBlank { credential.displayName.orEmpty() },
                email = supabaseUser.email.orEmpty().ifBlank { credential.id },
                profilePhotoUrl = supabaseUser.avatarUrl().ifBlank { credential.profilePictureUri?.toString().orEmpty() },
                accessToken = session.access_token.orEmpty()
            )
            val profile = upsertAndLoadProfile(user)
            sessionStore.save(user, profile)
            user to profile
        }
    }

    suspend fun saveCompletedProfile(profile: UserProfile): Result<UserProfile> {
        return runCatching {
            val completeProfile = profile.copy(
                createdDate = profile.createdDate.ifBlank { nowIso() },
                lastLogin = nowIso()
            )
            upsertProfileRemote(completeProfile, sessionStore.restoreUser()?.accessToken.orEmpty())
            sessionStore.saveProfile(completeProfile)
            completeProfile
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) { sessionStore.clear() }
    }

    private fun createLocalGoogleSession(): Pair<AuthUser, UserProfile?> {
        val user = AuthUser(
            id = "local_google_user",
            loginType = LoginType.GOOGLE,
            name = "Google User",
            email = "google.user@local.app"
        )
        sessionStore.save(user, null)
        return user to null
    }

    private suspend fun upsertAndLoadProfile(user: AuthUser): UserProfile? {
        val existing = loadProfileRemote(user.id, user.accessToken)
        val profile = existing ?: UserProfile(
            userId = user.id,
            loginType = user.loginType,
            name = user.name,
            email = user.email,
            phone = user.phone,
            profilePhoto = user.profilePhotoUrl,
            createdDate = nowIso(),
            lastLogin = nowIso()
        )
        upsertProfileRemote(profile.copy(lastLogin = nowIso()), user.accessToken)
        return profile
    }

    private suspend fun loadProfileRemote(userId: String, accessToken: String): UserProfile? = withContext(Dispatchers.IO) {
        if (!hasSupabaseConfig()) return@withContext null
        val request = baseRequest("/rest/v1/user_profiles?user_id=eq.$userId&select=*")
            .auth(accessToken)
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext null
            val body = response.body?.string().orEmpty().trim()
            if (body.length <= 2) return@withContext null
            val item = body.removePrefix("[").removeSuffix("]").takeIf { it.isNotBlank() } ?: return@withContext null
            profileAdapter.fromJson(item)?.toUserProfile()
        }
    }

    private suspend fun upsertProfileRemote(profile: UserProfile, accessToken: String) = withContext(Dispatchers.IO) {
        if (!hasSupabaseConfig()) return@withContext
        val body = SupabaseProfileDto.from(profile).let(profileAdapter::toJson)
        val request = baseRequest("/rest/v1/user_profiles")
            .auth(accessToken)
            .addHeader("Prefer", "resolution=merge-duplicates")
            .post(body.toRequestBody(jsonType))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Supabase profile save failed: ${response.code}")
        }
    }

    private suspend fun supabasePost(path: String, body: String) = withContext(Dispatchers.IO) {
        val request = baseRequest(path)
            .post(body.toRequestBody(jsonType))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Supabase request failed: ${response.code}")
        }
    }

    private suspend fun supabasePostForBody(path: String, body: String): String = withContext(Dispatchers.IO) {
        val request = baseRequest(path)
            .post(body.toRequestBody(jsonType))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Supabase request failed: ${response.code}")
            response.body?.string().orEmpty()
        }
    }

    private suspend fun exchangeGoogleToken(idToken: String): SupabaseAuthResponseDto {
        ensureSupabaseConfigured()
        val body = """{"provider":"google","id_token":"$idToken"}"""
        return supabasePostForBody("/auth/v1/token?grant_type=id_token", body)
            .let(authResponseAdapter::fromJson)
            ?: throw IOException("Supabase Google login failed.")
    }

    private fun baseRequest(path: String): Request.Builder {
        return Request.Builder()
            .url(BuildConfig.SUPABASE_URL.trimEnd('/') + path)
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            .addHeader("Content-Type", "application/json")
    }

    private fun Request.Builder.auth(accessToken: String): Request.Builder {
        if (accessToken.isNotBlank()) {
            header("Authorization", "Bearer $accessToken")
        }
        return this
    }

    private fun ensureSupabaseConfigured() {
        if (!hasSupabaseConfig()) {
            throw IllegalStateException("Supabase is not configured. Add SUPABASE_URL and SUPABASE_ANON_KEY in .env, then rebuild the app.")
        }
    }

    private fun ensureGoogleConfigured() {
        if (!isGoogleConfigured()) {
            throw IllegalStateException("Google Web Client ID is not configured. Add GOOGLE_WEB_CLIENT_ID in .env, then rebuild the app.")
        }
    }

    private fun isGoogleConfigured(): Boolean {
        val clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        return clientId.isNotBlank() &&
            !clientId.startsWith("YOUR_", ignoreCase = true) &&
            !clientId.contains("your-", ignoreCase = true)
    }

    private fun hasSupabaseConfig(): Boolean {
        val url = BuildConfig.SUPABASE_URL
        val key = BuildConfig.SUPABASE_ANON_KEY
        return url.startsWith("https://") &&
            !url.contains("your-project", ignoreCase = true) &&
            key.isNotBlank() &&
            !key.startsWith("YOUR_", ignoreCase = true)
    }

    private fun nowIso(): String = Instant.now().toString()
}

data class SupabaseAuthResponseDto(
    val access_token: String?,
    val token_type: String?,
    val expires_in: Long?,
    val refresh_token: String?,
    val user: SupabaseAuthUserDto?
)

data class SupabaseAuthUserDto(
    val id: String,
    val email: String?,
    val phone: String?,
    val user_metadata: Map<String, Any?>?
) {
    fun displayName(): String {
        return stringMeta("full_name")
            .ifBlank { stringMeta("name") }
            .ifBlank { email.orEmpty().substringBefore("@") }
    }

    fun avatarUrl(): String {
        return stringMeta("avatar_url").ifBlank { stringMeta("picture") }
    }

    private fun stringMeta(key: String): String {
        return user_metadata?.get(key)?.toString().orEmpty()
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseProfileDto(
    val user_id: String,
    val login_type: String,
    val name: String?,
    val email: String?,
    val phone: String?,
    val profile_photo: String?,
    val company_logo: String?,
    val company_name: String?,
    val website: String?,
    val whatsapp: String?,
    val facebook: String?,
    val instagram: String?,
    val youtube: String?,
    val business_address: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val created_date: String?,
    val last_login: String?
) {
    fun toUserProfile(): UserProfile {
        return UserProfile(
            userId = user_id,
            loginType = runCatching { LoginType.valueOf(login_type) }.getOrDefault(LoginType.MOBILE),
            name = name.orEmpty(),
            email = email.orEmpty(),
            phone = phone.orEmpty(),
            profilePhoto = profile_photo.orEmpty(),
            companyLogo = company_logo.orEmpty(),
            companyName = company_name.orEmpty(),
            website = website.orEmpty(),
            whatsapp = whatsapp.orEmpty(),
            facebook = facebook.orEmpty(),
            instagram = instagram.orEmpty(),
            youtube = youtube.orEmpty(),
            businessAddress = business_address.orEmpty(),
            city = city.orEmpty(),
            state = state.orEmpty(),
            country = country.orEmpty(),
            createdDate = created_date.orEmpty(),
            lastLogin = last_login.orEmpty()
        )
    }

    companion object {
        fun from(profile: UserProfile): SupabaseProfileDto {
            return SupabaseProfileDto(
                user_id = profile.userId.ifBlank { UUID.randomUUID().toString() },
                login_type = profile.loginType.name,
                name = profile.name,
                email = profile.email,
                phone = profile.phone,
                profile_photo = profile.profilePhoto,
                company_logo = profile.companyLogo,
                company_name = profile.companyName,
                website = profile.website,
                whatsapp = profile.whatsapp,
                facebook = profile.facebook,
                instagram = profile.instagram,
                youtube = profile.youtube,
                business_address = profile.businessAddress,
                city = profile.city,
                state = profile.state,
                country = profile.country,
                created_date = profile.createdDate,
                last_login = profile.lastLogin
            )
        }
    }
}
