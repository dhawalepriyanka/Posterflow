package com.example.ui.theme

object ThemeManager {
    const val LIGHT = "Light"
    const val DARK = "Dark"
    const val SYSTEM_DEFAULT = "System Default"

    fun shouldUseDarkTheme(themeMode: String, systemDarkTheme: Boolean): Boolean {
        return when (themeMode) {
            LIGHT -> false
            DARK -> true
            else -> systemDarkTheme
        }
    }
}
