package com.example.auth

import android.app.Activity
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: FirebaseAuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun startSplashCheck() {
        viewModelScope.launch {
            val restored = repository.restoreSession()
            if (restored == null) {
                _uiState.value = AuthUiState(
                    splashComplete = true,
                    isLoading = false,
                    isAuthenticated = false
                )
            } else {
                val (user, profile) = restored
                _uiState.value = AuthUiState(
                    splashComplete = true,
                    isLoading = false,
                    isAuthenticated = true,
                    requiresProfile = profile?.isComplete != true,
                    user = user,
                    profile = profile
                )
            }
        }
    }

    fun signInWithGoogle(activity: Activity) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            repository.signInWithGoogle(activity)
                .onSuccess { (user, profile) -> onAuthenticated(user, profile) }
                .onFailure { error -> showError(authErrorMessage(error)) }
        }
    }

    fun completeProfile(form: CompleteProfileForm) {
        val currentUser = _uiState.value.user ?: return
        if (form.name.isBlank() || form.companyName.isBlank()) {
            showError("Name and company name are required.")
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val existing = _uiState.value.profile
        val profile = UserProfile(
            userId = currentUser.id,
            loginType = currentUser.loginType,
            name = form.name.ifBlank { currentUser.name },
            email = currentUser.email,
            phone = form.mobileNumber.ifBlank { currentUser.phone },
            profilePhoto = form.profilePhotoUri?.toString().orEmpty().ifBlank { currentUser.profilePhotoUrl },
            companyLogo = form.companyLogoUri?.toString().orEmpty(),
            companyName = form.companyName,
            website = form.website,
            whatsapp = form.whatsappNumber,
            facebook = form.facebook,
            instagram = form.instagram,
            youtube = form.youtube,
            businessAddress = form.businessAddress,
            city = form.city,
            state = form.state,
            country = form.country,
            createdDate = existing?.createdDate.orEmpty(),
            lastLogin = existing?.lastLogin.orEmpty()
        )
        viewModelScope.launch {
            repository.saveCompletedProfile(profile)
                .onSuccess { saved ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            requiresProfile = false,
                            profile = saved
                        )
                    }
                }
                .onFailure { error -> showError(error.message ?: "Could not save profile.") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState(
                splashComplete = true,
                isLoading = false,
                isAuthenticated = false
            )
        }
    }

    fun consumeError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun onAuthenticated(user: AuthUser, profile: UserProfile?) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isAuthenticated = true,
                requiresProfile = profile?.isComplete != true,
                user = user,
                profile = profile,
                errorMessage = null
            )
        }
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(isLoading = false, errorMessage = message)
        }
    }

    private fun authErrorMessage(error: Throwable): String {
        return when (error) {
            is GetCredentialCancellationException -> "Google sign-in was cancelled."
            is NoCredentialException -> "No Google account is available. Add a Google account on this device and try again."
            is GetCredentialException -> "Google Account Picker could not start. Update Google Play Services and try again."
            is FirebaseNetworkException -> "No internet connection. Please check your network and try again."
            is FirebaseAuthInvalidCredentialsException -> "Google sign-in credentials were rejected. Please try again."
            is FirebaseAuthInvalidUserException -> "This Firebase user session is no longer valid. Please sign in again."
            is FirebaseAuthException -> error.localizedMessage ?: "Firebase authentication failed. Please try again."
            else -> error.message ?: "Google login failed. Please try again."
        }
    }
}
