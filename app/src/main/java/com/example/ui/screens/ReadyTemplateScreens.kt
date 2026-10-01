package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.Poster
import com.example.templates.*
import com.example.ui.PosterViewModel
import com.example.ui.ProfileSettings
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun ReadyTemplateLibrary(
    viewModel: PosterViewModel, profile: ProfileSettings, initialCategory: String? = null,
    initialPresetId: Int? = null, initialMessage: String? = null, videoMode: Boolean = false,
    onBack: () -> Unit, onProfile: () -> Unit
) {
    val all by viewModel.templates.collectAsStateWithLifecycle()
    val templates = remember(all) { all.filter { it.status == TemplateStatus.ACTIVE } }
    var category by rememberSaveable(initialCategory) { mutableStateOf(initialCategory ?: "Birthday") }

    FastPosterBrowsingScreen(
        category = category,
        onCategoryChange = { category = it },
        templates = templates.filter { it.category == category }.sortedBy { it.order },
        initialPresetId = initialPresetId,
        initialMessage = initialMessage,
        viewModel = viewModel,
        profile = profile,
        videoMode = videoMode,
        onBack = onBack,
        onProfile = onProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FastPosterBrowsingScreen(
    category: String,
    onCategoryChange: (String) -> Unit,
    templates: List<PosterTemplate>,
    initialPresetId: Int? = null,
    initialMessage: String? = null,
    viewModel: PosterViewModel,
    profile: ProfileSettings,
    videoMode: Boolean = false,
    onBack: () -> Unit,
    onProfile: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State: viewingDetail controls Level 1 (Category Grid) vs Level 2 (Detail Preview + Switcher)
    var viewingDetail by rememberSaveable(category, initialPresetId) {
        mutableStateOf(initialPresetId != null)
    }

    // 1. Current Selected Template ID (instant switching!)
    var selectedTemplateId by rememberSaveable(category, initialPresetId) {
        mutableStateOf(
            if (initialPresetId != null && templates.any { it.id == initialPresetId }) initialPresetId
            else templates.firstOrNull()?.id ?: 0
        )
    }

    // Keep selected template valid if category changes
    LaunchedEffect(templates, category) {
        if (templates.isNotEmpty() && templates.none { it.id == selectedTemplateId }) {
            selectedTemplateId = templates.first().id
        }
    }

    val selectedTemplate = remember(templates, selectedTemplateId) {
        templates.firstOrNull { it.id == selectedTemplateId } ?: templates.firstOrNull()
    }

    // 2. Person Photo Selection — poster-specific, NOT tied to profile photo.
    //    Starts empty; user picks their own photo for each poster.
    //    Changing profile photo in Settings must NOT reset this.
    var selectedPersonPhoto by rememberSaveable { mutableStateOf("") }
    var backgroundRemovedPhoto by rememberSaveable { mutableStateOf("") }
    var useBackgroundRemovedPhoto by rememberSaveable { mutableStateOf(false) }
    // Person name and designation — poster-specific overrides
    // For Welcome templates, the leader branding already carries the owner's name/title from App Settings,
    // so the welcomee slots start empty and ready for the user to edit.
    val isGreetCategory = category.equals("Welcome", ignoreCase = true) ||
            category.equals("Birthday", ignoreCase = true) ||
            category.equals("Achievement", ignoreCase = true)
    var customPersonName by rememberSaveable(category) {
        mutableStateOf(if (isGreetCategory) "" else profile.userName)
    }
    var customDesignation by rememberSaveable(category) {
        mutableStateOf(if (isGreetCategory) "" else profile.tagline)
    }
    var customMessage by rememberSaveable(initialMessage) { mutableStateOf(initialMessage.orEmpty()) }
    var customAmount by rememberSaveable { mutableStateOf("") }
    var customAchievement by rememberSaveable { mutableStateOf("") }
    var customQuote by rememberSaveable { mutableStateOf("") }
    var customCompany by rememberSaveable(profile.companyName) { mutableStateOf(profile.companyName) }
    // Photo crop/pan/zoom — reset when photo changes
    var customCropScale by rememberSaveable(selectedPersonPhoto) { mutableFloatStateOf(1f) }
    var customCropPanX by rememberSaveable(selectedPersonPhoto) { mutableFloatStateOf(0f) }
    var customCropPanY by rememberSaveable(selectedPersonPhoto) { mutableFloatStateOf(0f) }

    var leaderCropScale by rememberSaveable(profile.leaderImageUri, profile.profilePhotoUri) { mutableFloatStateOf(1f) }
    var leaderCropPanX by rememberSaveable(profile.leaderImageUri, profile.profilePhotoUri) { mutableFloatStateOf(0f) }
    var leaderCropPanY by rememberSaveable(profile.leaderImageUri, profile.profilePhotoUri) { mutableFloatStateOf(0f) }

    var logoCropScale by rememberSaveable(profile.companyLogoUri) { mutableFloatStateOf(1f) }
    var logoCropPanX by rememberSaveable(profile.companyLogoUri) { mutableFloatStateOf(0f) }
    var logoCropPanY by rememberSaveable(profile.companyLogoUri) { mutableFloatStateOf(0f) }

    var showEditSheet by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var progress by remember { mutableFloatStateOf(0f) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val imported = TemplateImages.import(context, uri)
                    selectedPersonPhoto = imported
                    backgroundRemovedPhoto = ""
                    useBackgroundRemovedPhoto = false
                    snackbarMessage = "Photo added. Choose the original or remove its background."
                } catch (e: Exception) {
                    snackbarMessage = e.message ?: "Unable to load photo."
                }
            }
        }
    }

    // Build current design document using currently selected template + person + profile branding
    val currentDesign = remember(selectedTemplate, selectedPersonPhoto, backgroundRemovedPhoto, useBackgroundRemovedPhoto, customPersonName, customDesignation, customMessage, customAmount, customAchievement, customQuote, customCompany, customCropScale, customCropPanX, customCropPanY, leaderCropScale, leaderCropPanX, leaderCropPanY, logoCropScale, logoCropPanX, logoCropPanY, profile) {
        if (selectedTemplate == null) null
        else {
            val values = mutableMapOf<String, String>()
            if (customPersonName.isNotBlank()) values[TemplateField.NAME.name] = customPersonName
            if (customDesignation.isNotBlank()) values[TemplateField.DESIGNATION.name] = customDesignation
            if (customMessage.isNotBlank()) values[TemplateField.MESSAGE.name] = customMessage
            if (customAmount.isNotBlank()) values[TemplateField.AMOUNT.name] = customAmount
            if (customAchievement.isNotBlank()) values[TemplateField.ACHIEVEMENT.name] = customAchievement
            if (customQuote.isNotBlank()) values[TemplateField.QUOTE.name] = customQuote
            if (customCompany.isNotBlank()) values[TemplateField.COMPANY.name] = customCompany

            val removalApplies = selectedTemplate.id == -214 && useBackgroundRemovedPhoto && backgroundRemovedPhoto.isNotBlank()
            GeneratedPoster(
                template = selectedTemplate,
                values = values,
                photo = if (removalApplies) backgroundRemovedPhoto else selectedPersonPhoto,
                originalPhoto = selectedPersonPhoto,
                backgroundRemovedPhoto = backgroundRemovedPhoto,
                backgroundRemoved = removalApplies,
                crop = PhotoCrop(customCropScale, customCropPanX, customCropPanY),
                leaderCrop = PhotoCrop(leaderCropScale, leaderCropPanX, leaderCropPanY),
                logoCrop = PhotoCrop(logoCropScale, logoCropPanX, logoCropPanY),
                branding = profile.businessBranding()
            )
        }
    }

    fun useOriginalPhoto() {
        useBackgroundRemovedPhoto = false
        snackbarMessage = "Using the original photo."
    }

    fun removeSelectedPhotoBackground() {
        if (busy || selectedPersonPhoto.isBlank()) return
        if (backgroundRemovedPhoto.isNotBlank()) {
            useBackgroundRemovedPhoto = true
            snackbarMessage = "Transparent photo selected."
            return
        }
        busy = true
        scope.launch {
            try {
                backgroundRemovedPhoto = PhotoBackgroundRemover.remove(context, selectedPersonPhoto)
                useBackgroundRemovedPhoto = true
                snackbarMessage = "Background removed. You can still switch back to the original."
            } catch (_: Exception) {
                useBackgroundRemovedPhoto = false
                snackbarMessage = "Background removal was not available. Using the original photo with soft edge blending."
            } finally {
                busy = false
            }
        }
    }

    suspend fun renderAndSave(design: GeneratedPoster): Bitmap {
        val bitmap = withContext(Dispatchers.Default) { TemplateRenderer.render(context, design, 1080) }
        val path = withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, "poster_thumbnails").apply { mkdirs() }
            File(dir, "ready_${UUID.randomUUID()}.png").also { out ->
                out.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            }.absolutePath
        }
        val record = design.template.asPoster().copy(
            title = design.template.name,
            backgroundType = "generated_template",
            backgroundImageRes = TemplateJson.encodeDesign(design),
            thumbnailPath = path,
            timestamp = System.currentTimeMillis()
        )
        viewModel.savePosterRecordNow(record, "", path)
        return bitmap
    }

    fun handleDownload() {
        val design = currentDesign ?: return
        busy = true
        scope.launch {
            try {
                val bitmap = renderAndSave(design)
                withContext(Dispatchers.IO) { saveBitmapToGallery(context, bitmap, design.template.name) }
                snackbarMessage = "Poster saved to Gallery (Pictures/PosterFlow) & My Designs!"
            } catch (e: Exception) {
                snackbarMessage = e.message ?: "Failed to save poster."
            } finally {
                busy = false
            }
        }
    }

    fun handleShare() {
        val design = currentDesign ?: return
        busy = true
        scope.launch {
            try {
                val bitmap = renderAndSave(design)
                sharePosterBitmap(context, bitmap, design.template.name)
            } catch (e: Exception) {
                snackbarMessage = e.message ?: "Failed to share."
            } finally {
                busy = false
            }
        }
    }

    fun handleVideo() {
        val design = currentDesign ?: return
        busy = true
        scope.launch {
            try {
                val bitmap = renderAndSave(design)
                val defaultAnimation = if (design.template.category.equals("Welcome", ignoreCase = true)) "Mix Up" else "Fade"
                val state = VideoCustomizationState(title = design.template.name, duration = "10 Seconds", animationStyle = defaultAnimation)
                val uri = exportPosterBitmapVideoToGallery(context, bitmap, state, design.template.name) { progress = it }
                    ?: error("Video export failed.")
                val thumb = withContext(Dispatchers.IO) {
                    val dir = File(context.filesDir, "poster_thumbnails").apply { mkdirs() }
                    File(dir, "video_${UUID.randomUUID()}.png").also { file ->
                        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                    }.absolutePath
                }
                viewModel.savePosterRecordNow(
                    design.template.asPoster().copy(title = "Video - ${design.template.name}", backgroundType = "video", backgroundImageRes = TemplateJson.encodeDesign(design)),
                    uri.toString(),
                    thumb
                )
                snackbarMessage = "Video created and saved in My Designs!"
            } catch (e: Exception) {
                snackbarMessage = e.message ?: "Failed to create video."
            } finally {
                busy = false
            }
        }
    }

    BackHandler(enabled = true) {
        if (viewingDetail) {
            viewingDetail = false
        } else {
            onBack()
        }
    }

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Business Profile") },
            text = {
                Text("Customize your company name, logo, phone, and website anytime in Settings.")
            },
            confirmButton = {
                Button(onClick = { showProfileDialog = false; onProfile() }) { Text("Go to Profile") }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) { Text("Close") }
            }
        )
    }



    if (showEditSheet) {
        ModalBottomSheet(onDismissRequest = { showEditSheet = false }) {
            val visibleFields = selectedTemplate?.visibleFields ?: emptyList()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Edit Details for this Poster", style = MaterialTheme.typography.titleLarge)
                if (TemplateField.NAME in visibleFields) {
                    OutlinedTextField(
                        value = customPersonName,
                        onValueChange = { customPersonName = it },
                        label = { Text("Person Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (TemplateField.DESIGNATION in visibleFields) {
                    OutlinedTextField(
                        value = customDesignation,
                        onValueChange = { customDesignation = it },
                        label = { Text("Designation / Role") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (TemplateField.AMOUNT in visibleFields) {
                    OutlinedTextField(
                        value = customAmount,
                        onValueChange = { customAmount = it },
                        label = { Text("Amount (e.g. ₹50,000)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (TemplateField.ACHIEVEMENT in visibleFields) {
                    OutlinedTextField(
                        value = customAchievement,
                        onValueChange = { customAchievement = it },
                        label = { Text("Achievement (e.g. STAR PERFORMER)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (TemplateField.COMPANY in visibleFields) {
                    OutlinedTextField(
                        value = customCompany,
                        onValueChange = { customCompany = it },
                        label = { Text("Company / Business Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (TemplateField.QUOTE in visibleFields) {
                    OutlinedTextField(
                        value = customQuote,
                        onValueChange = { customQuote = it },
                        label = { Text("Inspiring Quote / Slogan") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                if (TemplateField.MESSAGE in visibleFields) {
                    OutlinedTextField(
                        value = customMessage,
                        onValueChange = { customMessage = it },
                        label = { Text("Custom Message (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Business Branding (From App Settings)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Your Logo, Company Name, Mobile Number, Website, and Leader Photo are configured in App Settings.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = {
                                showEditSheet = false
                                onProfile()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Edit App Settings")
                        }
                    }
                }
                Button(
                    onClick = { showEditSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }

    if (!viewingDetail) {
        // ====================================================================
        // STEP 1: CATEGORY GRID VIEW (Visual Grid of Ready-made Raw Templates)
        // ====================================================================
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "$category Templates",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = onProfile) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings / Profile")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            snackbarHost = {
                if (snackbarMessage.isNotBlank()) {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            TextButton(onClick = { snackbarMessage = "" }) { Text("OK", color = Color.White) }
                        }
                    ) {
                        Text(snackbarMessage)
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .navigationBarsPadding()
            ) {
                // Category Filter Tabs
                ChoiceRow(
                    selected = category,
                    choices = StarterTemplates.categories,
                    onSelect = onCategoryChange
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${templates.size} Ready Templates",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Tap a template to customize",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // 2-column grid showing every template individually across all categories
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
                ) {
                    items(templates, key = { it.id }) { t ->
                        val thumbDesign = remember(t.id, selectedPersonPhoto, profile) {
                            GeneratedPoster(
                                template = t,
                                photo = selectedPersonPhoto,
                                branding = profile.businessBranding()
                            )
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTemplateId = t.id
                                    viewingDetail = true
                                },
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                ) {
                                    ReadyPosterPreview(design = thumbDesign, resolution = 360)
                                }
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                                    Text(
                                        text = t.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    val brandingSummary = if (t.supportedBranding.isNotEmpty()) {
                                        t.supportedBranding.take(3).joinToString(" • ") { it.name.lowercase().replaceFirstChar(Char::uppercase) }
                                    } else "Auto-Branded"
                                    Text(
                                        text = brandingSummary,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // ====================================================================
        // STEP 2: TEMPLATE PREVIEW & HORIZONTALLY SCROLLABLE THUMBNAIL STRIP
        // ====================================================================
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "FOR ${category.uppercase()} WISHES",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewingDetail = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Templates")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewingDetail = false }) {
                            Icon(Icons.Default.GridView, contentDescription = "View Grid")
                        }
                        IconButton(onClick = onProfile) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings / Profile")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            snackbarHost = {
                if (snackbarMessage.isNotBlank()) {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            TextButton(onClick = { snackbarMessage = "" }) { Text("OK", color = Color.White) }
                        }
                    ) {
                        Text(snackbarMessage)
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .navigationBarsPadding()
            ) {
                // Category Selector Tabs
                ChoiceRow(
                    selected = category,
                    choices = StarterTemplates.categories,
                    onSelect = onCategoryChange
                )

                Spacer(Modifier.height(8.dp))

                // 1. TOP SECTION: LARGE PROFESSIONAL POSTER PREVIEW
                // When a photo is loaded, the user can directly touch + drag / pinch on
                // the template preview to adjust the person photo position and zoom.
                var previewSizePx by remember { mutableIntStateOf(1) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.08f)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentDesign != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                                .onSizeChanged { previewSizePx = it.width.coerceAtLeast(1) }
                                .pointerInput(selectedTemplate?.id, selectedPersonPhoto, profile.companyLogoUri, profile.leaderImageUri) {
                                    detectTransformGestures { centroid, pan, zoom, _ ->
                                        val touchCanvasX = (centroid.x / previewSizePx) * 1080f
                                        val touchCanvasY = (centroid.y / previewSizePx) * 1080f

                                        val isLogoTouch = touchCanvasX < 320f && touchCanvasY < 180f && profile.companyLogoUri.isNotBlank()
                                        val isLeaderTouch = selectedTemplate?.category.equals("Welcome", ignoreCase = true) &&
                                                touchCanvasX < 300f && touchCanvasY > 780f &&
                                                (profile.leaderImageUri.isNotBlank() || profile.profilePhotoUri.isNotBlank())

                                        val panFactor = 3.2f / previewSizePx

                                        if (isLogoTouch) {
                                            logoCropScale = (logoCropScale * zoom).coerceIn(0.5f, 5f)
                                            logoCropPanX = (logoCropPanX + pan.x * panFactor).coerceIn(-1.5f, 1.5f)
                                            logoCropPanY = (logoCropPanY + pan.y * panFactor).coerceIn(-1.5f, 1.5f)
                                        } else if (isLeaderTouch) {
                                            leaderCropScale = (leaderCropScale * zoom).coerceIn(0.5f, 5f)
                                            leaderCropPanX = (leaderCropPanX + pan.x * panFactor).coerceIn(-1.5f, 1.5f)
                                            leaderCropPanY = (leaderCropPanY + pan.y * panFactor).coerceIn(-1.5f, 1.5f)
                                        } else if (selectedPersonPhoto.isNotBlank()) {
                                            customCropScale = (customCropScale * zoom).coerceIn(0.5f, 5f)
                                            customCropPanX = (customCropPanX + pan.x * panFactor).coerceIn(-1.5f, 1.5f)
                                            customCropPanY = (customCropPanY + pan.y * panFactor).coerceIn(-1.5f, 1.5f)
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            ReadyPosterPreview(design = currentDesign, resolution = 720)
                        }
                    } else {
                        Text("No template available in this category.")
                    }
                }

                Spacer(Modifier.height(10.dp))

                // 2. MIDDLE SECTION: PERSON SELECTOR & ACTIONS ROW (Matching Screenshot)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Person / Photo Selector Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // [ My Photo ] item
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { photoPicker.launch(arrayOf("image/*")) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedPersonPhoto.isNotBlank()) {
                                val contextBmp = TemplateImages.read(context, selectedPersonPhoto)
                                if (contextBmp != null) {
                                    Image(
                                        bitmap = contextBmp.asImageBitmap(),
                                        contentDescription = "My Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                                    Text("Photo", fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        // [ Edit Details ] button
                        OutlinedButton(
                            onClick = { showEditSheet = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Edit", fontSize = 12.sp)
                        }

                        // Share button
                        IconButton(onClick = { handleShare() }, enabled = !busy) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }

                        // Video button
                        IconButton(onClick = { handleVideo() }, enabled = !busy) {
                            Icon(Icons.Default.Videocam, contentDescription = "Create Video")
                        }
                    }

                    // Download Button (Distinctive yellow/gold container matching reference screenshot)
                    Button(
                        onClick = { handleDownload() },
                        enabled = !busy,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB703),
                            contentColor = Color(0xFF1E1E1E)
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download", modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("DOWNLOAD", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                if (selectedTemplate?.category == "Welcome" && selectedPersonPhoto.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = !useBackgroundRemovedPhoto,
                            onClick = ::useOriginalPhoto,
                            enabled = !busy,
                            label = { Text("Use Original") },
                            leadingIcon = if (!useBackgroundRemovedPhoto) { { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) } } else null
                        )
                        FilterChip(
                            selected = useBackgroundRemovedPhoto,
                            onClick = ::removeSelectedPhotoBackground,
                            enabled = !busy,
                            label = { Text(if (busy) "Removing…" else "Remove Background") },
                            leadingIcon = if (useBackgroundRemovedPhoto) { { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) } } else null
                        )
                    }
                }

                if (busy) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                }

                Spacer(Modifier.height(10.dp))

                // 3. BOTTOM SECTION: HORIZONTALLY SCROLLABLE STRIP OF TEMPLATE THUMBNAILS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Choose Design (${templates.size})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(
                            onClick = { viewingDetail = false },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("View All", fontSize = 12.sp)
                        }
                    }

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        items(templates, key = { it.id }) { t ->
                            val isSelected = t.id == selectedTemplateId
                            val thumbDesign = remember(t.id, selectedPersonPhoto, profile) {
                                GeneratedPoster(
                                    template = t,
                                    photo = selectedPersonPhoto,
                                    branding = profile.businessBranding()
                                )
                            }

                            Card(
                                modifier = Modifier
                                    .size(86.dp)
                                    .aspectRatio(1f)
                                    .clickable { selectedTemplateId = t.id },
                                shape = RoundedCornerShape(12.dp),
                                border = if (isSelected) BorderStroke(3.dp, Color(0xFFFFB703))
                                else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    ReadyPosterPreview(design = thumbDesign, resolution = 200)
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(3.dp)
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFFB703)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.Black,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun TemplateDetailPreviewScreen(
    template: PosterTemplate,
    profile: ProfileSettings,
    onUseTemplate: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                TextButton(onClick = onBack) { Text("← Templates") }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                ReadyPosterPreview(GeneratedPoster(template, branding = profile.businessBranding()), resolution = 720)
            }
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(template.name, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                val reqFields = template.requiredFields.map { it.name.lowercase().replaceFirstChar(Char::uppercase) }
                Text(
                    if (reqFields.isEmpty()) "Instant Design • Profile Branding Auto-Applied"
                    else "Requires: ${reqFields.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            Button(
                onClick = onUseTemplate,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("USE THIS TEMPLATE", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
internal fun ChoiceRow(selected: String, choices: List<String>, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(choices) { option -> FilterChip(selected == option, onClick = { onSelect(option) }, label = { Text(option) }) }
    }
}

@Composable
internal fun ReadyPosterCustomize(
    initial: GeneratedPoster, viewModel: PosterViewModel, profile: ProfileSettings,
    existing: Poster? = null, onUpdated: (Poster) -> Unit = {}, videoMode: Boolean = false,
    onBack: () -> Unit, onProfile: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var encoded by rememberSaveable(initial.template.id, existing?.id) { 
        mutableStateOf(TemplateJson.encodeDesign(initial.copy(branding = profile.businessBranding()))) 
    }
    val document = remember(encoded, profile) { 
        val decoded = TemplateJson.design(encoded) ?: initial
        // ALWAYS apply current Settings branding — poster photo & user data are preserved,
        // but branding is never stored/restored from saved poster JSON.
        decoded.copy(branding = profile.businessBranding())
    }
    val latest by rememberUpdatedState(document)
    var generated by remember { mutableStateOf<Bitmap?>(null) }
    var savedRecord by remember { mutableStateOf(existing) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var progress by remember { mutableFloatStateOf(0f) }
    var cropControls by rememberSaveable { mutableStateOf(false) }
    var duration by rememberSaveable { mutableStateOf("10 Seconds") }
    var animation by rememberSaveable {
        mutableStateOf(if (initial.template.category.equals("Welcome", ignoreCase = true)) "Mix Up" else "Fade")
    }
    var musicUri by rememberSaveable { mutableStateOf<String?>(null) }
    var musicResource by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(profile.businessBranding()) { generated = null }
    fun change(next: GeneratedPoster) { encoded = TemplateJson.encodeDesign(next); generated = null; message = "" }
    fun action(block: suspend () -> Unit) {
        if(busy) return
        busy = true
        scope.launch {
            try { block() } catch(e: Exception) { message = e.message ?: "Unable to complete this action." }
            finally { busy = false }
        }
    }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if(uri != null) action {
            val source = TemplateImages.import(context, uri)
            change(latest.copy(
                photo = source,
                originalPhoto = source,
                backgroundRemovedPhoto = "",
                backgroundRemoved = false,
                crop = PhotoCrop()
            ))
            message = "Photo added. Choose the original or remove its background."
        }
    }
    val musicPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if(uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            musicUri = uri.toString()
            musicResource = null
        }
    }
    fun saveGallery() = action {
        val bitmap = generated ?: error("Generate the poster first.")
        withContext(Dispatchers.IO) { saveBitmapToGallery(context, bitmap, latest.template.name) }
        message = "Saved to Gallery → Pictures / PosterFlow."
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if(granted) saveGallery() else message = "Storage permission is needed to save on this Android version. You can still share the poster."
    }
    fun useOriginalPhoto() {
        val original = latest.originalPhoto.ifBlank { latest.photo }
        if (original.isNotBlank()) {
            change(latest.copy(photo = original, originalPhoto = original, backgroundRemoved = false))
            message = "Using the original photo."
        }
    }
    fun removePhotoBackground() {
        if (busy) return
        val original = latest.originalPhoto.ifBlank { latest.photo }
        if (original.isBlank()) return
        if (latest.backgroundRemovedPhoto.isNotBlank()) {
            change(latest.copy(
                photo = latest.backgroundRemovedPhoto,
                originalPhoto = original,
                backgroundRemoved = true
            ))
            message = "Transparent photo selected."
            return
        }
        action {
            try {
                val processed = PhotoBackgroundRemover.remove(context, original)
                change(latest.copy(
                    photo = processed,
                    originalPhoto = original,
                    backgroundRemovedPhoto = processed,
                    backgroundRemoved = true
                ))
                message = "Background removed. Crop, zoom and position are preserved."
            } catch (_: Exception) {
                change(latest.copy(photo = original, originalPhoto = original, backgroundRemoved = false))
                message = "Background removal failed. Using the original photo with soft edge blending."
            }
        }
    }
    suspend fun persist(draft: GeneratedPoster, bitmap: Bitmap): Poster {
        val path = withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, "poster_thumbnails").apply { mkdirs() }
            File(dir,"ready_${UUID.randomUUID()}.png").also { out -> out.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) } }.absolutePath
        }
        val base = draft.template.asPoster().copy(id = savedRecord?.id ?: 0, title = savedRecord?.title ?: draft.template.name,
            backgroundType = "generated_template", backgroundImageRes = TemplateJson.encodeDesign(draft), thumbnailPath = path,
            timestamp = System.currentTimeMillis())
        return if(savedRecord == null) viewModel.savePosterRecordNow(base,"",path) else viewModel.updateSavedPosterNow(base)
    }
    var showProfileDialog by remember { mutableStateOf(false) }
    BackHandler(enabled = !busy, onBack = onBack)
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Complete your business profile") },
            text = {
                val missing = latest.missingBranding().joinToString()
                Text("To generate this poster with your branding, please complete: $missing in your profile.")
            },
            confirmButton = {
                Button(onClick = {
                    showProfileDialog = false
                    onProfile()
                }) {
                    Text("Go to Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding().imePadding(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            TextButton(onClick = onBack, enabled = !busy) { Text("← Templates") }
            Text("Customize poster", style = MaterialTheme.typography.headlineSmall)
            Text(document.template.name, style = MaterialTheme.typography.titleMedium)
            Text("Layout by your template designer • Branding from Profile", style = MaterialTheme.typography.bodySmall)
        }
        item {
            var previewWidth by remember { mutableFloatStateOf(1f) }
            val slot = document.template.slots.firstOrNull { it.enabled && it.field == TemplateField.PHOTO }
            var movingPhoto by remember { mutableStateOf(false) }
            ReadyPosterPreview(document, Modifier.onSizeChanged { previewWidth = it.width.toFloat() }.pointerInput(slot, busy) {
                if(slot != null && !busy) detectDragGestures(
                    onDragStart = { offset ->
                        val x=offset.x/previewWidth*1080; val y=offset.y/previewWidth*1080
                        movingPhoto=x in slot.x..slot.x+slot.width && y in slot.y..slot.y+slot.height
                    }, onDragEnd = { movingPhoto=false }, onDragCancel = { movingPhoto=false },
                    onDrag = { event, delta -> if(movingPhoto && latest.photo.isNotBlank()) {
                        event.consume()
                        change(latest.copy(crop=latest.crop.copy(panX=(latest.crop.panX+delta.x/previewWidth*4).coerceIn(-1f,1f),panY=(latest.crop.panY+delta.y/previewWidth*4).coerceIn(-1f,1f))))
                    } }
                )
            })
        }
        if(TemplateField.PHOTO in document.template.visibleFields) item {
        Button(onClick = { photoPicker.launch(arrayOf("image/*")) }, enabled = !busy, modifier=Modifier.fillMaxWidth()) { Text(if(document.photo.isBlank()) "Choose photo" else "Change photo") }
        if(document.photo.isNotBlank()) {
            if (document.template.id == -214) {
                Text("Photo background", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !document.backgroundRemoved,
                        onClick = ::useOriginalPhoto,
                        enabled = !busy,
                        label = { Text("Use Original") },
                        leadingIcon = if (!document.backgroundRemoved) { { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) } } else null
                    )
                    FilterChip(
                        selected = document.backgroundRemoved,
                        onClick = ::removePhotoBackground,
                        enabled = !busy,
                        label = { Text(if (busy) "Removing…" else "Remove Background") },
                        leadingIcon = if (document.backgroundRemoved) { { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) } } else null
                    )
                }
            }
            TextButton(onClick={cropControls=!cropControls},enabled=!busy) { Text(if(cropControls)"Done adjusting photo" else "Adjust photo") }
            if(cropControls) {
            Text("Adjust your photo inside the fixed frame. Drag to pan.")
            Text("Zoom")
            Slider(document.crop.scale, onValueChange={change(document.copy(crop=document.crop.copy(scale=it)))}, valueRange=1f..4f, enabled=!busy)
            Text("Horizontal position")
            Slider(document.crop.panX, onValueChange={change(document.copy(crop=document.crop.copy(panX=it)))}, valueRange=-1f..1f, enabled=!busy)
            Text("Vertical position")
            Slider(document.crop.panY, onValueChange={change(document.copy(crop=document.crop.copy(panY=it)))}, valueRange=-1f..1f, enabled=!busy)
            TextButton(onClick={change(document.copy(crop=PhotoCrop()))}, enabled=!busy) { Text("Reset crop") }
            }
        }
    }
    items(document.template.visibleFields.filter { it != TemplateField.PHOTO }, key = { it.name }) { field ->
        val label=field.name.lowercase().replaceFirstChar { it.uppercase() }
        OutlinedTextField(value=document.values[field.name].orEmpty(),onValueChange={ value -> change(document.copy(values=document.values + (field.name to value.take(if(field==TemplateField.MESSAGE) 240 else 100)))) },
            label={Text(label + if(field in document.template.requiredFields) " *" else " (optional)")}, enabled=!busy, modifier=Modifier.fillMaxWidth(), minLines=if(field==TemplateField.MESSAGE)2 else 1, maxLines=if(field==TemplateField.MESSAGE)4 else 2)
    }
    item {
        Button(onClick={
            val draft=latest
            action {
                check(draft.missingFields().isEmpty()) { "Please add: ${draft.missingFields().joinToString { it.name.lowercase() }}" }
                check(draft.photo.isBlank() || TemplateImages.read(context,draft.photo)!=null) { "Your photo is no longer available. Choose it again." }
                check(draft.branding.logo.isBlank() || TemplateImages.read(context,draft.branding.logo)!=null) { "Your logo is no longer available. Update it in Profile." }
                val frozen = if(draft.branding.logo.startsWith("content:")) draft.copy(branding=draft.branding.copy(logo=TemplateImages.import(context, android.net.Uri.parse(draft.branding.logo)))) else draft
                val bitmap=withContext(Dispatchers.Default){TemplateRenderer.render(context,frozen)}
                savedRecord=persist(frozen,bitmap); onUpdated(savedRecord!!)
                encoded=TemplateJson.encodeDesign(frozen); generated=bitmap
                message="Poster generated and saved in My Designs."
            }
        }, enabled=!busy, modifier=Modifier.fillMaxWidth()) { Text("Generate poster") }
        if(message.isNotBlank()) Text(message, modifier=Modifier.padding(vertical=8.dp))
        if(busy) LinearProgressIndicator(modifier=Modifier.fillMaxWidth())
    }
        if(generated != null) item {
            Text("Your poster is ready", style=MaterialTheme.typography.titleLarge)
            Button(onClick={
                if(Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) else saveGallery()
            },enabled=!busy,modifier=Modifier.fillMaxWidth()){Text("Save to Gallery")}
            OutlinedButton(onClick={ action { sharePosterBitmap(context,generated!!,document.template.name) } }, enabled=!busy,modifier=Modifier.fillMaxWidth()){Text("Share")}
            OutlinedButton(onClick={message="This poster is already saved in My Designs."},enabled=!busy,modifier=Modifier.fillMaxWidth()){Text("Saved to My Designs ✓")}
            Text("Create video",style=MaterialTheme.typography.titleMedium,modifier=Modifier.padding(top=12.dp))
            ChoiceRow(duration,listOf("5 Seconds","10 Seconds","15 Seconds")){if(!busy)duration=it}
            ChoiceRow(animation,listOf("Mix Up","Fade","None","Zoom In")){if(!busy)animation=it}
            val musicCategory=document.template.category.lowercase().takeIf{it in listOf("welcome","birthday","achievement","income")} ?: "welcome"
            ChoiceRow(musicResource?.substringAfterLast("song")?.let{"Track $it"} ?: "No music",listOf("No music","Track 1","Track 2","Track 3")){choice->if(!busy){musicUri=null;musicResource=if(choice=="No music")null else "${musicCategory}_song${choice.last()}"}}
            TextButton(onClick={musicPicker.launch(arrayOf("audio/*"))},enabled=!busy){Text(if(musicUri==null)"Use music from phone (optional)" else "Change phone music")}
            if(musicUri!=null)TextButton(onClick={musicUri=null},enabled=!busy){Text("Remove music")}
            Button(onClick={action {
                val bitmap=generated!!
                val state=VideoCustomizationState(title=document.template.name,duration=duration,animationStyle=animation,musicPhoneUri=musicUri?.let(android.net.Uri::parse),musicResourceName=musicResource)
                val uri=exportPosterBitmapVideoToGallery(context,bitmap,state,document.template.name){progress=it}
                    ?: error("Video export failed on this device.")
                val thumbnail=withContext(Dispatchers.IO){
                    val dir=File(context.filesDir,"poster_thumbnails").apply{mkdirs()}
                    File(dir,"video_${UUID.randomUUID()}.png").also{file->file.outputStream().use{bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}}.absolutePath
                }
                viewModel.savePosterRecordNow(document.template.asPoster().copy(title="Video - ${document.template.name}",backgroundType="video",backgroundImageRes=TemplateJson.encodeDesign(document)),uri.toString(),thumbnail)
                message="Video saved in My Designs. A Gallery copy is also created when storage is available (Movies / PosterFlow)."
            }},enabled=!busy,modifier=Modifier.fillMaxWidth()){Text(if(videoMode)"Generate video" else "Create video")}
            if(busy && progress>0) Text("Video ${(progress*100).toInt()}%")
        }
    }
}
