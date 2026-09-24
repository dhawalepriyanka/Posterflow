package com.example.ui

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PosterRepository
import com.example.auth.UserProfile
import com.example.model.Poster
import com.example.model.PosterElement
import com.example.model.TemplatePresets
import com.example.ui.theme.ThemeManager
import com.example.templates.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Context.profileDataStore by preferencesDataStore(name = "poster_profile_settings")

data class ProfileSettings(
    val profilePhotoUri: String = "",
    val userName: String = "PosterFlow Creator",
    val userEmail: String = "creator@postermaker.app",
    val companyLogoUri: String = "",
    val companyName: String = "",
    val websiteName: String = "",
    val mobileNumber: String = "",
    val businessEmail: String = "",
    val businessAddress: String = "",
    val tagline: String = "",
    val designation: String = "",
    val themeMode: String = ThemeManager.SYSTEM_DEFAULT,
    val newTemplateNotifications: Boolean = true,
    val festivalTemplateUpdates: Boolean = true,
    val appUpdates: Boolean = true
)

class PosterViewModel(
    private val repository: PosterRepository,
    private val appContext: Context
) : ViewModel() {

    // Saved posters from database
    val savedPosters: StateFlow<List<Poster>> = repository.allPosters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current poster in the editor
    private val _activePoster = MutableStateFlow<Poster>(createBlankPoster("My First Poster", "Welcome"))
    val activePoster: StateFlow<Poster> = _activePoster.asStateFlow()

    // Selected element's ID for editing properties
    private val _selectedElementId = MutableStateFlow<String?>(null)
    val selectedElementId: StateFlow<String?> = _selectedElementId.asStateFlow()

    // Static presets list dynamically loaded for hot-reload support
    private val adminAuthorizer = FirebaseAdminAuthorizer()
    val templateRepository: TemplateRepository = LocalTemplateRepository(appContext, adminAuthorizer)
    val templates = templateRepository.templates
    val presets: List<Poster> get() = templates.value.filter { it.status == TemplateStatus.ACTIVE }.map { it.asPoster() }
    private val mutableAdmin = MutableStateFlow(false)
    val isAdmin = mutableAdmin.asStateFlow()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val authListener = FirebaseAuth.AuthStateListener { refreshAdminAccess() }

    init {
        firebaseAuth.addAuthStateListener(authListener)
        viewModelScope.launch {
            runCatching { templateRepository.load() }.onFailure { showStatusMessage(it.message ?: "Cannot load templates") }
        }
    }

    fun refreshAdminAccess() {
        mutableAdmin.value = false
        viewModelScope.launch { mutableAdmin.value = runCatching { adminAuthorizer.isAdmin() }.getOrDefault(false) }
    }

    override fun onCleared() {
        firebaseAuth.removeAuthStateListener(authListener)
        super.onCleared()
    }

    // Status message for showing saved notification
    private val _statusBarMessage = MutableStateFlow<String?>(null)
    val statusBarMessage: StateFlow<String?> = _statusBarMessage.asStateFlow()

    val profileSettings: StateFlow<ProfileSettings> = appContext.profileDataStore.data
        .map { preferences ->
            ProfileSettings(
                profilePhotoUri = preferences[ProfilePreferenceKeys.ProfilePhotoUri].orEmpty(),
                userName = preferences[ProfilePreferenceKeys.UserName] ?: "PosterFlow Creator",
                userEmail = preferences[ProfilePreferenceKeys.UserEmail] ?: "creator@postermaker.app",
                companyLogoUri = preferences[ProfilePreferenceKeys.CompanyLogoUri].orEmpty(),
                companyName = preferences[ProfilePreferenceKeys.CompanyName].orEmpty(),
                websiteName = preferences[ProfilePreferenceKeys.WebsiteName].orEmpty(),
                mobileNumber = preferences[ProfilePreferenceKeys.MobileNumber].orEmpty(),
                businessEmail = preferences[ProfilePreferenceKeys.BusinessEmail].orEmpty(),
                businessAddress = preferences[ProfilePreferenceKeys.BusinessAddress].orEmpty(),
                tagline = preferences[ProfilePreferenceKeys.Tagline].orEmpty(),
                designation = preferences[ProfilePreferenceKeys.Designation].orEmpty(),
                themeMode = preferences[ProfilePreferenceKeys.ThemeMode] ?: ThemeManager.SYSTEM_DEFAULT,
                newTemplateNotifications = preferences[ProfilePreferenceKeys.NewTemplateNotifications] ?: true,
                festivalTemplateUpdates = preferences[ProfilePreferenceKeys.FestivalTemplateUpdates] ?: true,
                appUpdates = preferences[ProfilePreferenceKeys.AppUpdates] ?: true
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileSettings()
        )

    fun clearStatusBarMessage() {
        _statusBarMessage.value = null
    }

    fun showStatusMessage(message: String) {
        _statusBarMessage.value = message
    }

    fun updateCompanyLogo(uri: String) {
        viewModelScope.launch {
            runCatching {
                val path = TemplateImages.import(appContext, android.net.Uri.parse(uri))
                updateStringPreference(ProfilePreferenceKeys.CompanyLogoUri, path)
            }.onFailure { showStatusMessage(it.message ?: "Could not import logo") }
        }
    }

    fun updateCompanyName(value: String) {
        updateStringPreference(ProfilePreferenceKeys.CompanyName, value)
    }

    fun updateWebsiteName(value: String) {
        updateStringPreference(ProfilePreferenceKeys.WebsiteName, value)
    }

    fun updateDesignation(value: String) {
        updateStringPreference(ProfilePreferenceKeys.Designation, value)
    }

    fun updateBusinessContact(field: String, value: String) {
        val key = when(field) {
            "Phone" -> ProfilePreferenceKeys.MobileNumber
            "Business email" -> ProfilePreferenceKeys.BusinessEmail
            "Address" -> ProfilePreferenceKeys.BusinessAddress
            "Tagline" -> ProfilePreferenceKeys.Tagline
            "Designation" -> ProfilePreferenceKeys.Designation
            else -> return
        }
        updateStringPreference(key, value)
    }

    fun updateThemeMode(value: String) {
        updateStringPreference(ProfilePreferenceKeys.ThemeMode, value)
    }

    fun updateNotificationPreference(key: String, enabled: Boolean) {
        val preferenceKey = when (key) {
            "new_templates" -> ProfilePreferenceKeys.NewTemplateNotifications
            "festival_updates" -> ProfilePreferenceKeys.FestivalTemplateUpdates
            "app_updates" -> ProfilePreferenceKeys.AppUpdates
            else -> return
        }
        updateBooleanPreference(preferenceKey, enabled)
    }

    fun applyAuthenticatedProfile(profile: UserProfile) {
        viewModelScope.launch {
            appContext.profileDataStore.edit { preferences ->
                // Profile edits must survive app restarts instead of being overwritten by login data.
                if (preferences[ProfilePreferenceKeys.Owner] == profile.userId) return@edit
                preferences[ProfilePreferenceKeys.Owner] = profile.userId
                preferences[ProfilePreferenceKeys.ProfilePhotoUri] = profile.profilePhoto
                preferences[ProfilePreferenceKeys.UserName] = profile.name.ifBlank { "PosterFlow Creator" }
                preferences[ProfilePreferenceKeys.UserEmail] = profile.email
                preferences[ProfilePreferenceKeys.CompanyLogoUri] = profile.companyLogo
                preferences[ProfilePreferenceKeys.CompanyName] = profile.companyName
                preferences[ProfilePreferenceKeys.WebsiteName] = profile.website
                preferences[ProfilePreferenceKeys.MobileNumber] = profile.phone
                preferences[ProfilePreferenceKeys.BusinessEmail] = profile.businessEmail.ifBlank { profile.email }
                preferences[ProfilePreferenceKeys.BusinessAddress] = profile.businessAddress
                preferences[ProfilePreferenceKeys.Tagline] = profile.tagline
            }
        }
    }

    private fun updateStringPreference(key: androidx.datastore.preferences.core.Preferences.Key<String>, value: String) {
        viewModelScope.launch {
            appContext.profileDataStore.edit { preferences ->
                preferences[key] = value
            }
            val store = com.example.auth.AuthSessionStore(appContext)
            store.restoreProfile()?.let { current ->
                val updated = when(key) {
                    ProfilePreferenceKeys.CompanyLogoUri -> current.copy(companyLogo=value)
                    ProfilePreferenceKeys.CompanyName -> current.copy(companyName=value)
                    ProfilePreferenceKeys.WebsiteName -> current.copy(website=value)
                    ProfilePreferenceKeys.MobileNumber -> current.copy(phone=value)
                    ProfilePreferenceKeys.BusinessEmail -> current.copy(businessEmail=value)
                    ProfilePreferenceKeys.BusinessAddress -> current.copy(businessAddress=value)
                    ProfilePreferenceKeys.Tagline -> current.copy(tagline=value)
                    else -> current
                }
                store.saveProfile(updated)
            }
        }
    }

    private fun updateBooleanPreference(key: androidx.datastore.preferences.core.Preferences.Key<Boolean>, value: Boolean) {
        viewModelScope.launch {
            appContext.profileDataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    private object ProfilePreferenceKeys {
        val Owner = stringPreferencesKey("brandingOwner")
        val BusinessEmail = stringPreferencesKey("businessEmail")
        val BusinessAddress = stringPreferencesKey("businessAddress")
        val Tagline = stringPreferencesKey("businessTagline")
        val Designation = stringPreferencesKey("designation")
        val ProfilePhotoUri = stringPreferencesKey("profilePhotoUri")
        val UserName = stringPreferencesKey("userName")
        val UserEmail = stringPreferencesKey("userEmail")
        val CompanyLogoUri = stringPreferencesKey("companyLogoUri")
        val CompanyName = stringPreferencesKey("companyName")
        val WebsiteName = stringPreferencesKey("websiteName")
        val MobileNumber = stringPreferencesKey("mobileNumber")
        val ThemeMode = stringPreferencesKey("themeMode")
        val NewTemplateNotifications = booleanPreferencesKey("newTemplateNotifications")
        val FestivalTemplateUpdates = booleanPreferencesKey("festivalTemplateUpdates")
        val AppUpdates = booleanPreferencesKey("appUpdates")
    }

    private fun createBlankPoster(title: String, category: String): Poster {
        return Poster(
            id = 0,
            title = title,
            category = category,
            backgroundType = "solid",
            backgroundColorHex = defaultColorForCategory(category),
            elements = listOf(
                PosterElement(
                    type = "text",
                    textValue = "ENTER YOUR TITLE",
                    textColorHex = "#FFFFFF",
                    fontSizeSp = 28,
                    isBold = true,
                    xOffset = 0.5f,
                    yOffset = 0.3f
                ),
                PosterElement(
                    type = "text",
                    textValue = "Drag elements to reposition. Tap to edit properties.",
                    textColorHex = "#B0BEC5",
                    fontSizeSp = 14,
                    xOffset = 0.5f,
                    yOffset = 0.7f
                )
            )
        )
    }

    fun selectElement(id: String?) {
        _selectedElementId.value = id
    }

    fun loadPresetToEditor(preset: Poster) {
        // Copy preset to editor, reset ID to 0 so it saves as a new user design unless user is editing an existing draft
        _activePoster.value = preset.copy(
            id = 0,
            title = "My Copy of ${preset.title}",
            timestamp = System.currentTimeMillis()
        )
        // Auto-select first element if available
        _selectedElementId.value = preset.elements.firstOrNull()?.id
    }

    fun loadPosterToEditor(poster: Poster) {
        _activePoster.value = poster
        _selectedElementId.value = poster.elements.firstOrNull()?.id
    }

    fun startNewPoster(title: String, category: String) {
        val defaultColor = defaultColorForCategory(category)
        _activePoster.value = Poster(
            id = 0,
            title = title,
            category = category,
            backgroundType = "solid",
            backgroundColorHex = defaultColor,
            elements = listOf(
                PosterElement(
                    type = "text",
                    textValue = when (category) {
                        "Welcome" -> "Welcome to the team"
                        "Achievement" -> "Congratulations on your milestone"
                        "Birthday" -> "Wishing you a wonderful birthday"
                        "Income" -> "Celebrating your reward and recognition"
                        else -> "Tap here to change slogan"
                    },
                    textColorHex = "#FFFFFF",
                    fontSizeSp = 22,
                    isBold = true,
                    xOffset = 0.5f,
                    yOffset = 0.4f
                )
            )
        )
        _selectedElementId.value = _activePoster.value.elements.firstOrNull()?.id
    }

    private fun defaultColorForCategory(category: String): String {
        return when (category) {
            "Welcome" -> "#2563EB"
            "Achievement" -> "#F59E0B"
            "Birthday" -> "#EC4899"
            "Income" -> "#059669"
            else -> "#121212"
        }
    }

    fun updateActivePosterTitle(newTitle: String) {
        _activePoster.value = _activePoster.value.copy(title = newTitle)
    }

    fun changeBackgroundType(type: String, value: String) {
        _activePoster.value = if (type == "image") {
            _activePoster.value.copy(
                backgroundType = "image",
                backgroundImageRes = value
            )
        } else {
            _activePoster.value.copy(
                backgroundType = "solid",
                backgroundColorHex = value
            )
        }
    }

    fun addTextElement() {
        val newElement = PosterElement(
            type = "text",
            textValue = "Double-tap to Edit",
            textColorHex = "#FFFFFF",
            fontSizeSp = 22,
            xOffset = 0.5f,
            yOffset = 0.5f
        )
        val updatedElements = _activePoster.value.elements + newElement
        _activePoster.value = _activePoster.value.copy(elements = updatedElements)
        _selectedElementId.value = newElement.id
    }

    fun addStickerElement(stickerName: String) {
        val newElement = PosterElement(
            type = "sticker",
            stickerIcon = stickerName,
            xOffset = 0.5f,
            yOffset = 0.5f,
            scale = 1.2f
        )
        val updatedElements = _activePoster.value.elements + newElement
        _activePoster.value = _activePoster.value.copy(elements = updatedElements)
        _selectedElementId.value = newElement.id
    }

    fun updateElementPosition(elementId: String, dxFraction: Float, dyFraction: Float) {
        val updated = _activePoster.value.elements.map { el ->
            if (el.id == elementId) {
                el.copy(
                    xOffset = (el.xOffset + dxFraction).coerceIn(0.01f, 0.99f),
                    yOffset = (el.yOffset + dyFraction).coerceIn(0.01f, 0.99f)
                )
            } else el
        }
        _activePoster.value = _activePoster.value.copy(elements = updated)
    }

    fun updateSelectedText(text: String) {
        val selId = _selectedElementId.value ?: return
        val updated = _activePoster.value.elements.map { el ->
            if (el.id == selId) el.copy(textValue = text) else el
        }
        _activePoster.value = _activePoster.value.copy(elements = updated)
    }

    fun updateSelectedColor(hexColor: String) {
        val selId = _selectedElementId.value ?: return
        val updated = _activePoster.value.elements.map { el ->
            if (el.id == selId) el.copy(textColorHex = hexColor) else el
        }
        _activePoster.value = _activePoster.value.copy(elements = updated)
    }

    fun updateSelectedFontSize(sizeSp: Int) {
        val selId = _selectedElementId.value ?: return
        val updated = _activePoster.value.elements.map { el ->
            if (el.id == selId) el.copy(fontSizeSp = sizeSp.coerceIn(8, 72)) else el
        }
        _activePoster.value = _activePoster.value.copy(elements = updated)
    }

    fun toggleSelectedBold() {
        val selId = _selectedElementId.value ?: return
        val updated = _activePoster.value.elements.map { el ->
            if (el.id == selId) el.copy(isBold = !el.isBold) else el
        }
        _activePoster.value = _activePoster.value.copy(elements = updated)
    }

    fun toggleSelectedItalic() {
        val selId = _selectedElementId.value ?: return
        val updated = _activePoster.value.elements.map { el ->
            if (el.id == selId) el.copy(isItalic = !el.isItalic) else el
        }
        _activePoster.value = _activePoster.value.copy(elements = updated)
    }

    fun deleteSelectedElement() {
        val selId = _selectedElementId.value ?: return
        val updated = _activePoster.value.elements.filter { it.id != selId }
        _activePoster.value = _activePoster.value.copy(elements = updated)
        _selectedElementId.value = updated.firstOrNull()?.id
    }

    fun duplicateSelectedElement() {
        val selId = _selectedElementId.value ?: return
        val itemToDuplicate = _activePoster.value.elements.find { it.id == selId } ?: return
        val duplicated = itemToDuplicate.copy(
            id = java.util.UUID.randomUUID().toString(),
            xOffset = (itemToDuplicate.xOffset + 0.05f).coerceIn(0.1f, 0.9f),
            yOffset = (itemToDuplicate.yOffset + 0.05f).coerceIn(0.1f, 0.9f)
        )
        _activePoster.value = _activePoster.value.copy(elements = _activePoster.value.elements + duplicated)
        _selectedElementId.value = duplicated.id
    }

    fun increaseSelectedScale() {
        val selId = _selectedElementId.value ?: return
        val updated = _activePoster.value.elements.map { el ->
            if (el.id == selId) el.copy(scale = (el.scale + 0.1f).coerceIn(0.3f, 4.0f)) else el
        }
        _activePoster.value = _activePoster.value.copy(elements = updated)
    }

    fun decreaseSelectedScale() {
        val selId = _selectedElementId.value ?: return
        val updated = _activePoster.value.elements.map { el ->
            if (el.id == selId) el.copy(scale = (el.scale - 0.1f).coerceIn(0.3f, 4.0f)) else el
        }
        _activePoster.value = _activePoster.value.copy(elements = updated)
    }

    fun rotateSelectedClockwise() {
        val selId = _selectedElementId.value ?: return
        val updated = _activePoster.value.elements.map { el ->
            if (el.id == selId) el.copy(rotation = (el.rotation + 15f) % 360f) else el
        }
        _activePoster.value = _activePoster.value.copy(elements = updated)
    }

    fun saveCurrentPoster() {
        viewModelScope.launch {
            val toSave = _activePoster.value.copy(timestamp = System.currentTimeMillis())
            val savedResult = repository.insertPoster(toSave)
            _activePoster.value = savedResult
            _statusBarMessage.value = "Poster '${savedResult.title}' saved successfully!"
        }
    }

    fun savePosterRecord(poster: Poster, galleryImageUri: String, thumbnailPath: String) {
        viewModelScope.launch {
            savePosterRecordNow(poster, galleryImageUri, thumbnailPath)
            _statusBarMessage.value = "Poster saved successfully"
        }
    }

    suspend fun savePosterRecordNow(poster: Poster, galleryImageUri: String, thumbnailPath: String): Poster {
        return repository.insertPoster(
            poster.copy(
                id = 0,
                timestamp = System.currentTimeMillis(),
                galleryImageUri = galleryImageUri,
                thumbnailPath = thumbnailPath
            )
        )
    }

    fun updateSavedPosterRecord(poster: Poster) {
        viewModelScope.launch {
            repository.insertPoster(poster)
            _statusBarMessage.value = "Design updated successfully"
        }
    }

    suspend fun updateSavedPosterNow(poster: Poster): Poster = repository.insertPoster(poster)

    fun deleteSavedPoster(id: Int) {
        viewModelScope.launch {
            repository.deletePoster(id)
            _statusBarMessage.value = "Design deleted successfully"
        }
    }
}
