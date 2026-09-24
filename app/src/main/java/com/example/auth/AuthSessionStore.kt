package com.example.auth

import android.content.Context
import androidx.core.content.edit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class AuthSessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("auth_session", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val userAdapter = moshi.adapter(AuthUser::class.java)
    private val profileAdapter = moshi.adapter(UserProfile::class.java)

    fun save(user: AuthUser, profile: UserProfile?) {
        prefs.edit {
            putString(KEY_USER, userAdapter.toJson(user))
            if (profile != null) {
                putString(KEY_PROFILE, profileAdapter.toJson(profile))
                putString("profile_${profile.userId}", profileAdapter.toJson(profile))
            } else remove(KEY_PROFILE)
            putBoolean(KEY_AUTHENTICATED, true)
        }
    }

    fun saveProfile(profile: UserProfile) {
        prefs.edit {
            putString(KEY_PROFILE, profileAdapter.toJson(profile))
            putString("profile_${profile.userId}", profileAdapter.toJson(profile))
        }
    }

    fun restoreUser(): AuthUser? {
        if (!prefs.getBoolean(KEY_AUTHENTICATED, false)) return null
        return prefs.getString(KEY_USER, null)?.let { userAdapter.fromJson(it) }
    }

    fun restoreProfile(userId: String? = null): UserProfile? {
        val raw = userId?.let { prefs.getString("profile_$it", null) } ?: prefs.getString(KEY_PROFILE, null)
        return raw?.let { runCatching { profileAdapter.fromJson(it) }.getOrNull() }?.takeIf { userId == null || it.userId == userId }
    }

    fun clear() {
        // Keep local per-account profiles, but remove the authenticated session.
        prefs.edit { remove(KEY_USER); remove(KEY_PROFILE); remove(KEY_AUTHENTICATED) }
    }

    private companion object {
        const val KEY_USER = "user"
        const val KEY_PROFILE = "profile"
        const val KEY_AUTHENTICATED = "authenticated"
    }
}
