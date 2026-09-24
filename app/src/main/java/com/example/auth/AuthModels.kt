package com.example.auth

import android.net.Uri

enum class LoginType {
    GOOGLE,
    MOBILE
}

data class AuthUser(
    val id: String,
    val loginType: LoginType,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profilePhotoUrl: String = "",
    val accessToken: String = ""
)

data class UserProfile(
    val userId: String = "",
    val loginType: LoginType = LoginType.GOOGLE,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profilePhoto: String = "",
    val companyLogo: String = "",
    val companyName: String = "",
    val website: String = "",
    val whatsapp: String = "",
    val facebook: String = "",
    val instagram: String = "",
    val youtube: String = "",
    val businessAddress: String = "",
    val businessEmail: String = "",
    val tagline: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val createdDate: String = "",
    val lastLogin: String = ""
) {
    val isComplete: Boolean
        get() = companyName.isNotBlank() && name.isNotBlank()
}

data class CompleteProfileForm(
    val profilePhotoUri: Uri? = null,
    val companyLogoUri: Uri? = null,
    val name: String = "",
    val companyName: String = "",
    val website: String = "",
    val mobileNumber: String = "",
    val whatsappNumber: String = "",
    val facebook: String = "",
    val instagram: String = "",
    val youtube: String = "",
    val businessAddress: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = ""
)

data class AuthUiState(
    val splashComplete: Boolean = false,
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false,
    val requiresProfile: Boolean = false,
    val user: AuthUser? = null,
    val profile: UserProfile? = null,
    val errorMessage: String? = null
)
