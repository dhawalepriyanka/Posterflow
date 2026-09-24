package com.example

import android.os.Bundle
import android.app.Activity
import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.auth.AuthScaffold
import com.example.auth.AuthViewModel
import com.example.auth.AuthenticationScreen
import com.example.auth.CompleteProfileScreen
import com.example.auth.FirebaseAuthRepository
import com.example.auth.SplashScreen
import com.example.data.AppDatabase
import com.example.data.PosterRepository
import com.example.ui.PosterViewModel
import com.example.ui.screens.PosterMakerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ThemeManager
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        // Initialize SQLite Room Database & Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = PosterRepository(database.posterDao())

        // simple ViewModel factory helper
        val vmFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PosterViewModel(repository, applicationContext) as T
            }
        }

        // Instantiate PosterViewModel with repository injection
        val viewModel = ViewModelProvider(this, vmFactory).get(PosterViewModel::class.java)
        val authFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(FirebaseAuthRepository(applicationContext)) as T
            }
        }
        val authViewModel = ViewModelProvider(this, authFactory).get(AuthViewModel::class.java)

        setContent {
            val profileSettings by viewModel.profileSettings.collectAsStateWithLifecycle()
            val systemDarkTheme = isSystemInDarkTheme()
            val darkTheme = ThemeManager.shouldUseDarkTheme(profileSettings.themeMode, systemDarkTheme)
            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = Color.TRANSPARENT,
                        darkScrim = Color.TRANSPARENT,
                        detectDarkMode = { darkTheme }
                    ),
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = Color.TRANSPARENT,
                        darkScrim = Color.TRANSPARENT,
                        detectDarkMode = { darkTheme }
                    )
                )
            }

            MyApplicationTheme(darkTheme = darkTheme) {
                AppRoot(
                    posterViewModel = viewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

@Composable
private fun AppRoot(
    posterViewModel: PosterViewModel,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    AuthScaffold(
        errorMessage = authState.errorMessage,
        onConsumeError = authViewModel::consumeError
    ) { _ ->
        when {
            !authState.splashComplete -> {
                SplashScreen(onFinished = authViewModel::startSplashCheck)
            }

            authState.isAuthenticated && authState.requiresProfile -> {
                CompleteProfileScreen(
                    user = authState.user,
                    isLoading = authState.isLoading,
                    onSave = authViewModel::completeProfile
                )
            }

            authState.isAuthenticated -> {
                authState.profile?.let { profile ->
                    LaunchedEffect(profile) {
                        posterViewModel.applyAuthenticatedProfile(profile)
                    }
                }
                PosterMakerScreen(
                    viewModel = posterViewModel,
                    modifier = Modifier.fillMaxSize(),
                    onLogout = authViewModel::logout
                )
            }

            else -> {
                AuthNavigation(
                    isLoading = authState.isLoading,
                    onGoogleLogin = { activity -> authViewModel.signInWithGoogle(activity) }
                )
            }
        }
    }
}

@Composable
private fun AuthNavigation(
    isLoading: Boolean,
    onGoogleLogin: (Activity) -> Unit
) {
    val activity = LocalContext.current as? Activity
    AuthenticationScreen(
        isLoading = isLoading,
        onGoogleLogin = { activity?.let(onGoogleLogin) }
    )
}
