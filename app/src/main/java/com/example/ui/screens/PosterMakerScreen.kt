package com.example.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.media.MediaMetadataRetriever
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Gradient
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Poster
import com.example.R
import com.example.ui.ProfileSettings
import com.example.templates.*
import com.example.ui.PosterViewModel
import com.example.ui.branding.BrandWordmark
import android.media.MediaPlayer
import com.example.ui.theme.ThemeManager
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage

private val DashboardBackground = Color(0xFF080A12)
private val GlassSurface = Color(0xB21B1E2B)
private val GlassSurfaceStrong = Color(0xE6242736)
private val TextPrimary = Color(0xFFF7F7FB)
private val TextMuted = Color(0xFFC9CAD8)
private val TemplateToolbarHeight = 56.dp

@Composable
private fun appIsDarkTheme(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

@Composable
private fun appBackgroundColor(): Color {
    return if (appIsDarkTheme()) DashboardBackground else Color(0xFFF8F7FC)
}

@Composable
private fun appGradientColors(): List<Color> {
    return if (appIsDarkTheme()) {
        listOf(DashboardBackground, Color(0xFF101321), DashboardBackground)
    } else {
        listOf(Color(0xFFF8F7FC), Color(0xFFFFFFFF), Color(0xFFF2ECFF))
    }
}

@Composable
private fun appGlassSurface(): Color {
    return if (appIsDarkTheme()) GlassSurface else Color.White.copy(alpha = 0.92f)
}

@Composable
private fun appGlassSurfaceStrong(): Color {
    return if (appIsDarkTheme()) GlassSurfaceStrong else Color.White
}

@Composable
private fun appTextPrimary(): Color {
    return if (appIsDarkTheme()) TextPrimary else Color(0xFF17151F)
}

@Composable
private fun appTextMuted(): Color {
    return if (appIsDarkTheme()) TextMuted else Color(0xFF655F70)
}

@Composable
private fun appBorderColor(alpha: Float = 0.09f): Color {
    return if (appIsDarkTheme()) Color.White.copy(alpha = alpha) else Color(0xFF1D1B20).copy(alpha = alpha.coerceAtLeast(0.12f))
}

private data class HeroCardSpec(
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: ImageVector,
    val colors: List<Color>
)

private data class CategorySpec(
    val name: String,
    val icon: ImageVector,
    val colors: List<Color>
)

private data class BottomTabSpec(
    val key: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val TemplateCardHeight = 288.dp
private val TemplateCardPadding = 12.dp
private val TemplatePreviewHeight = 196.dp
private val TemplateTitleHeight = 40.dp
private val TemplateActionHeight = 18.dp
private val TemplateCardCornerRadius = 18.dp
private val TemplatePreviewCornerRadius = 14.dp

private enum class SmartAiOutputType {
    Poster,
    Video
}

private data class SmartAiDraft(
    val outputType: SmartAiOutputType,
    val category: String,
    val templateId: Int,
    val templateTitle: String,
    val wishMessage: String,
    val customMessage: String,
    val themeName: String,
    val animationStyle: String,
    val musicSelection: String,
    val musicResourceName: String?
) {
    val finalWishMessage: String
        get() = customMessage.ifBlank { wishMessage }
}

private data class SmartAiParseResult(
    val outputType: SmartAiOutputType,
    val category: String
)

private interface SmartAiRequestParser {
    fun parse(prompt: String): SmartAiParseResult?
}

private object LocalSmartAiRequestParser : SmartAiRequestParser {
    private val supportedCategories = mapOf(
        "birthday" to "Birthday",
        "welcome" to "Welcome",
        "achievement" to "Achievement",
        "award" to "Achievement",
        "income" to "Income",
        "earning" to "Income",
        "festival" to "Festival",
        "anniversary" to "Anniversary",
        "motivation" to "Motivation",
        "motivational" to "Motivation",
        "business" to "Business",
        "service" to "Business",
        "offer" to "Business",
        "morning" to "Good Morning",
        "prabhat" to "Good Morning"
    )

    override fun parse(prompt: String): SmartAiParseResult? {
        val normalized = prompt.lowercase(Locale.getDefault())
        val category = supportedCategories.entries.firstOrNull { (keyword, _) ->
            normalized.contains(keyword)
        }?.value ?: return null
        val outputType = when {
            normalized.contains("video") || normalized.contains("mp4") -> SmartAiOutputType.Video
            normalized.contains("poster") || normalized.contains("image") || normalized.contains("photo") -> SmartAiOutputType.Poster
            else -> SmartAiOutputType.Poster
        }
        return SmartAiParseResult(outputType = outputType, category = category)
    }
}

internal data class VideoTemplateSpec(
    val name: String,
    val colors: List<Color>,
    val icon: ImageVector
)

private val videoTemplatesByCategory = mapOf(
    "Birthday" to listOf(
        VideoTemplateSpec("Royal Hero", listOf(Color(0xFF070707), Color(0xFF9A6500)), Icons.Default.Cake),
        VideoTemplateSpec("Luxury Black Gold", listOf(Color(0xFF020202), Color(0xFFD29A1D)), Icons.Default.WorkspacePremium),
        VideoTemplateSpec("Elegant Photo", listOf(Color(0xFFF43F7C), Color(0xFF7C3AED)), Icons.Default.PhotoCamera),
        VideoTemplateSpec("Celebration", listOf(Color(0xFFEC4899), Color(0xFF9333EA)), Icons.Default.AutoAwesome),
        VideoTemplateSpec("Premium Gold", listOf(Color(0xFF171109), Color(0xFFB97800)), Icons.Default.WorkspacePremium),
        VideoTemplateSpec("Full Portrait Story", listOf(Color(0xFF7C3AED), Color(0xFFDB2777)), Icons.Default.Person)
    ),
    "Achievement" to listOf(
        VideoTemplateSpec("Hall of Success Hero", listOf(Color(0xFF071B3A), Color(0xFFC58A16)), Icons.Default.EmojiEvents),
        VideoTemplateSpec("Rank Advancement", listOf(Color(0xFF123663), Color(0xFFE6A817)), Icons.AutoMirrored.Filled.TrendingUp),
        VideoTemplateSpec("Champion Portrait", listOf(Color(0xFF111827), Color(0xFFD97706)), Icons.Default.WorkspacePremium),
        VideoTemplateSpec("Milestone Celebration", listOf(Color(0xFF172554), Color(0xFFF59E0B)), Icons.Default.AutoAwesome),
        VideoTemplateSpec("Leadership Bonus", listOf(Color(0xFF292524), Color(0xFFB45309)), Icons.Default.EmojiEvents),
        VideoTemplateSpec("First Day Recognition", listOf(Color(0xFF0F172A), Color(0xFFCA8A04)), Icons.Default.WorkspacePremium)
    ),
    "Welcome" to listOf(
        VideoTemplateSpec("Welcome Hero", listOf(Color(0xFF0F766E), Color(0xFF2563EB)), Icons.Default.Groups),
        VideoTemplateSpec("New Team Member", listOf(Color(0xFF0891B2), Color(0xFF22C55E)), Icons.Default.Person),
        VideoTemplateSpec("Corporate Welcome", listOf(Color(0xFF155E75), Color(0xFF0D9488)), Icons.Default.Business),
        VideoTemplateSpec("Success Journey", listOf(Color(0xFF2563EB), Color(0xFF14B8A6)), Icons.AutoMirrored.Filled.TrendingUp),
        VideoTemplateSpec("Business Welcome", listOf(Color(0xFF0369A1), Color(0xFF10B981)), Icons.Default.Groups),
        VideoTemplateSpec("Leadership Welcome", listOf(Color(0xFF0F6170), Color(0xFF49B891)), Icons.Default.WorkspacePremium)
    ),
    "Income" to listOf(
        VideoTemplateSpec("Income Success", listOf(Color(0xFF3B176B), Color(0xFFD97706)), Icons.AutoMirrored.Filled.TrendingUp),
        VideoTemplateSpec("Rank Income", listOf(Color(0xFF312E81), Color(0xFFB45309)), Icons.Default.WorkspacePremium),
        VideoTemplateSpec("Monthly Achievement", listOf(Color(0xFF581C87), Color(0xFFEA580C)), Icons.Default.EmojiEvents),
        VideoTemplateSpec("Financial Growth", listOf(Color(0xFF172554), Color(0xFF7E22CE)), Icons.AutoMirrored.Filled.TrendingUp),
        VideoTemplateSpec("Bonus Celebration", listOf(Color(0xFF4C1D95), Color(0xFFF59E0B)), Icons.Default.AutoAwesome),
        VideoTemplateSpec("Dream Income", listOf(Color(0xFF1E1B4B), Color(0xFF9333EA)), Icons.Default.WorkspacePremium)
    )
)

private data class CustomPosterState(
    val companyLogoUri: Uri? = null,
    val profilePhotoUri: Uri? = null,
    val photoScale: Float = 1f,
    val photoOffsetX: Float = 0f,
    val photoOffsetY: Float = 0f,
    val personName: String = "Rahul Sharma",
    val wishesMessage: String = "Happy Birthday\nWishing You Happiness, Success and Prosperity",
    val companyName: String = "ABC Marketing Pvt Ltd",
    val websiteName: String = "www.abcmarketing.com",
    val mobileNumber: String = ""
)

private data class PhotoAdjustment(
    val scale: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f
)

private fun CustomPosterState.signatureContent() = SignaturePosterContent(
    name = personName, message = wishesMessage, company = companyName,
    website = websiteName, phone = mobileNumber, photo = profilePhotoUri,
    logo = companyLogoUri, photoScale = photoScale, photoX = photoOffsetX, photoY = photoOffsetY
)

private fun CustomPosterState.welcomeProject(templateId: Int): WelcomePosterState {
    val base = newWelcomePosterState(
        templateId = templateId,
        companyName = companyName,
        logoUri = companyLogoUri?.toString().orEmpty(),
        website = websiteName,
        phone = mobileNumber,
        memberName = personName
    )
    return base.copy(elements = base.elements.map { element ->
        when (element.role) {
            // A Welcome recipient is independent of the business profile.
            // Keep the template's editable member-photo layer empty until selected.
            "message", "welcomeMessage" -> element.copy(text = wishesMessage)
            else -> element
        }
    })
}

private data class VideoColorStyle(
    val titleColor: Color = Color.White,
    val nameColor: Color = Color.White,
    val messageColor: Color = Color.White.copy(alpha = 0.9f),
    val footerColor: Color = Color.White,
    val borderColor: Color = Color(0xFFFFC83D),
    val accentColor: Color = Color(0xFFFFC83D),
    val backgroundColors: List<Color>? = null
)

private data class VideoBackgroundStyle(
    val mode: String = "Template",
    val galleryImageUri: Uri? = null,
    val colors: List<Color>? = null
)

private data class VideoColorPreset(
    val name: String,
    val style: VideoColorStyle
)

private val defaultVideoColorStyle = VideoColorStyle()
private val LocalPhotoAdjustment = compositionLocalOf { PhotoAdjustment() }
private val LocalPhotoAdjustmentChange = compositionLocalOf<(PhotoAdjustment) -> Unit> { {} }
private val LocalSamplePortraitRes = compositionLocalOf { R.drawable.sample_business_man }
private val LocalTemplateTitle = compositionLocalOf { "" }
private val LocalVideoColorStyle = compositionLocalOf { defaultVideoColorStyle }

private val dashboardCategories = listOf(
    CategorySpec("Welcome", Icons.Default.Groups, listOf(Color(0xFF0F9F9A), Color(0xFF2563A8))),
    CategorySpec("Birthday", Icons.Default.Cake, listOf(Color(0xFFFF4FA3), Color(0xFF8B5CF6))),
    CategorySpec("Achievement", Icons.Default.EmojiEvents, listOf(Color(0xFF071B3A), Color(0xFFB77900))),
    CategorySpec("Income", Icons.AutoMirrored.Filled.TrendingUp, listOf(Color(0xFF3B176B), Color(0xFF111D67))),
    CategorySpec("Festival", Icons.Default.AutoAwesome, listOf(Color(0xFF341148), Color(0xFFB77900))),
    CategorySpec("Motivation", Icons.Default.Bolt, listOf(Color(0xFF15303B), Color(0xFFD56836))),
    CategorySpec("Business", Icons.Default.Business, listOf(Color(0xFF0C2444), Color(0xFF00D2D3))),
    CategorySpec("Good Morning", Icons.Default.WbSunny, listOf(Color(0xFFB45309), Color(0xFFF59E0B))),
    CategorySpec("Good Night", Icons.Default.NightsStay, listOf(Color(0xFF1E1B4B), Color(0xFF4338CA))),
    CategorySpec("Anniversary", Icons.Default.Favorite, listOf(Color(0xFF831843), Color(0xFFF472B6))),
    CategorySpec("Offers", Icons.Default.LocalOffer, listOf(Color(0xFFB91C1C), Color(0xFFEA580C))),
    CategorySpec("Events", Icons.Default.Event, listOf(Color(0xFF4338CA), Color(0xFF7C3AED))),
    CategorySpec("Political", Icons.Default.HowToVote, listOf(Color(0xFF1E3A8A), Color(0xFFEA580C))),
    CategorySpec("Real Estate", Icons.Default.Home, listOf(Color(0xFF065F46), Color(0xFF0D9488))),
    CategorySpec("Restaurant", Icons.Default.Restaurant, listOf(Color(0xFF9A3412), Color(0xFFD97706))),
    CategorySpec("Healthcare", Icons.Default.LocalHospital, listOf(Color(0xFF155E75), Color(0xFF06B6D4))),
    CategorySpec("Education", Icons.Default.School, listOf(Color(0xFF1E40AF), Color(0xFF3B82F6))),
    CategorySpec("Job Vacancy", Icons.Default.Work, listOf(Color(0xFF374151), Color(0xFF6B7280))),
    CategorySpec("Quotes", Icons.Default.FormatQuote, listOf(Color(0xFF581C87), Color(0xFF9333EA))),
    CategorySpec("Devotional", Icons.Default.SelfImprovement, listOf(Color(0xFF78350F), Color(0xFFD97706)))
)

private val heroCards = listOf(
    HeroCardSpec(
        title = "Welcome Posters",
        subtitle = "Welcome New Team Members Professionally",
        category = "Welcome",
        icon = Icons.Default.Groups,
        colors = listOf(Color(0xFF2563EB), Color(0xFF7C3AED))
    ),
    HeroCardSpec(
        title = "Achievement Posters",
        subtitle = "Celebrate Success and Milestones",
        category = "Achievement",
        icon = Icons.Default.WorkspacePremium,
        colors = listOf(Color(0xFFEAB308), Color(0xFFF97316))
    ),
    HeroCardSpec(
        title = "Birthday Wishes",
        subtitle = "Personalized Birthday Posters",
        category = "Birthday",
        icon = Icons.Default.Cake,
        colors = listOf(Color(0xFFEC4899), Color(0xFF9333EA))
    ),
    HeroCardSpec(
        title = "Income & Reward Posters",
        subtitle = "Showcase Team Earnings and Recognition",
        category = "Income",
        icon = Icons.AutoMirrored.Filled.TrendingUp,
        colors = listOf(Color(0xFF16A34A), Color(0xFF047857))
    )
)

private val bottomTabs = listOf(
    BottomTabSpec("dashboard", "Dashboard", Icons.Filled.Home, Icons.Outlined.Home),
    BottomTabSpec("templates", "Templates", Icons.Filled.GridView, Icons.Outlined.GridView),
    BottomTabSpec("designs", "My Designs", Icons.Filled.FolderSpecial, Icons.Outlined.FolderSpecial),
    BottomTabSpec("profile", "Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
)

private fun Bitmap.scaledSoftwareCopy(width: Int, height: Int): Bitmap {
    val scaled = Bitmap.createScaledBitmap(this, width, height, true)
    return if (scaled.config == Bitmap.Config.ARGB_8888 && scaled.isMutable) {
        scaled
    } else {
        val software = scaled.copy(Bitmap.Config.ARGB_8888, true)
        if (software !== scaled) scaled.recycle()
        software
    }
}

private fun NavHostController.navigateBackOr(fallback: () -> Unit) {
    if (!popBackStack()) {
        fallback()
    }
}

private fun rootTabForRoute(route: String?): String {
    return when {
        route == null -> "dashboard"
        route.startsWith("templates") -> "templates"
        route.startsWith("designs") -> "designs"
        route.startsWith("profile") -> "profile"
        route.startsWith("video_maker") -> "video_maker"
        route.startsWith("editor") -> "editor"
        else -> "dashboard"
    }
}

@Composable
fun PosterMakerScreen(
    viewModel: PosterViewModel,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val activePoster by viewModel.activePoster.collectAsStateWithLifecycle()
    val selectedElementId by viewModel.selectedElementId.collectAsStateWithLifecycle()
    val savedPosters by viewModel.savedPosters.collectAsStateWithLifecycle()
    val statusBarMsg by viewModel.statusBarMessage.collectAsStateWithLifecycle()
    val profileSettings by viewModel.profileSettings.collectAsStateWithLifecycle()
    val templateDefinitions by viewModel.templates.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val availablePresets = templateDefinitions.filter { it.status == TemplateStatus.ACTIVE }.map { it.asPoster() }
    androidx.lifecycle.compose.LifecycleEventEffect(androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
        viewModel.refreshAdminAccess()
    }

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = rootTabForRoute(currentBackStackEntry?.destination?.route)
    var viewingSavedPoster by remember { mutableStateOf(false) }
    var smartAiPosterDraft by remember { mutableStateOf<SmartAiDraft?>(null) }
    var smartAiVideoDraft by remember { mutableStateOf<SmartAiDraft?>(null) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(statusBarMsg) {
        statusBarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusBarMessage()
        }
    }

    fun navigateToRoot(route: String) {
        if (route != currentBackStackEntry?.destination?.route) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    fun navigateBackOrDashboard() {
        navController.navigateBackOr {
            navController.navigate("dashboard") {
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = appBackgroundColor(),
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentTab != "editor" && !viewingSavedPoster) {
                PremiumBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = {
                        viewingSavedPoster = false
                        when (it) {
                            "templates" -> {
                                smartAiPosterDraft = null
                                navigateToRoot("templates")
                            }
                            "dashboard" -> navigateToRoot("dashboard")
                            "designs" -> navigateToRoot("designs")
                            "profile" -> navigateToRoot("profile")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(appGradientColors())
                )
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("dashboard") {
                    DashboardTab(
                        presets = availablePresets,
                        onOpenCategory = { category ->
                            smartAiPosterDraft = null
                            navigateToRoot("templates/$category")
                        },
                        onOpenTemplates = {
                            smartAiPosterDraft = null
                            navigateToRoot("templates")
                        },
                        onOpenDesigns = { navigateToRoot("designs") },
                        onOpenProfile = { navigateToRoot("profile") },
                        onOpenVideoMaker = {
                            smartAiVideoDraft = null
                            navigateToRoot("video_maker")
                        },
                        onSmartAiGenerated = { draft ->
                            when (draft.outputType) {
                                SmartAiOutputType.Poster -> {
                                    smartAiPosterDraft = draft
                                    smartAiVideoDraft = null
                                    navigateToRoot("templates/${draft.category}")
                                }
                                SmartAiOutputType.Video -> {
                                    smartAiVideoDraft = draft
                                    smartAiPosterDraft = null
                                    navigateToRoot("video_maker")
                                }
                            }
                        }
                    )
                }
                composable("templates") {
                    ReadyTemplateLibrary(
                        viewModel = viewModel,
                        profile = profileSettings,
                        initialCategory = null,
                        initialPresetId = smartAiPosterDraft?.templateId,
                        initialMessage = smartAiPosterDraft?.finalWishMessage,
                        onProfile = { navigateToRoot("profile") },
                        onBack = {
                            smartAiPosterDraft = null
                            navigateBackOrDashboard()
                        }
                    )
                }
                composable(
                    route = "templates/{category}",
                    arguments = listOf(navArgument("category") { type = NavType.StringType })
                ) { entry ->
                    val category = entry.arguments?.getString("category")
                    ReadyTemplateLibrary(
                        viewModel = viewModel,
                        profile = profileSettings,
                        initialCategory = category,
                        initialPresetId = smartAiPosterDraft?.templateId,
                        initialMessage = smartAiPosterDraft?.finalWishMessage,
                        onProfile = { navigateToRoot("profile") },
                        onBack = {
                            smartAiPosterDraft = null
                            navigateBackOrDashboard()
                        }
                    )
                }
                composable("designs") {
                    SavedPostersTab(
                        viewModel = viewModel,
                        onProfile = { navigateToRoot("profile") },
                        savedPosters = savedPosters,
                        profileSettings = profileSettings,
                        onViewingPosterChanged = { viewingSavedPoster = it },
                        onUpdatePoster = { viewModel.updateSavedPosterRecord(it) },
                        onDeletePoster = { viewModel.deleteSavedPoster(it.id) }
                    )
                }
                composable("profile") {
                    ProfileTab(
                        viewModel = viewModel,
                        isAdmin = isAdmin,
                        onManageTemplates = { navigateToRoot("template_admin") },
                        profileSettings = profileSettings,
                        onCompanyLogoSelected = { viewModel.updateCompanyLogo(it) },
                        onCompanyNameChange = { viewModel.updateCompanyName(it) },
                        onWebsiteNameChange = { viewModel.updateWebsiteName(it) },
                        onThemeModeChange = { viewModel.updateThemeMode(it) },
                        onNotificationChange = { key, enabled -> viewModel.updateNotificationPreference(key, enabled) },
                        onShowMessage = { viewModel.showStatusMessage(it) },
                        onLogout = onLogout
                    )
                }
                composable("editor") {
                    ReadyTemplateLibrary(viewModel, profileSettings, onBack = { navigateBackOrDashboard() }, onProfile = { navigateToRoot("profile") })
                }
                composable("template_admin") {
                    TemplateAdminScreen(viewModel, onBack = { navigateBackOrDashboard() })
                }
                composable("video_maker") {
                    ReadyTemplateLibrary(
                        viewModel = viewModel,
                        profile = profileSettings,
                        initialCategory = smartAiVideoDraft?.category,
                        initialPresetId = smartAiVideoDraft?.templateId,
                        initialMessage = smartAiVideoDraft?.finalWishMessage,
                        videoMode = true,
                        onProfile = { navigateToRoot("profile") },
                        onBack = { navigateBackOrDashboard() }
                    )
                }
            }
        }
    }

}

@Composable
private fun DashboardTab(
    presets: List<Poster>,
    onOpenCategory: (String) -> Unit,
    onOpenTemplates: () -> Unit,
    onOpenDesigns: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenVideoMaker: () -> Unit,
    onSmartAiGenerated: (SmartAiDraft) -> Unit
) {
    var categorySearch by remember { mutableStateOf("") }
    var showSmartAiSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val dashboardHeaderBarHeight = 68.dp
    val dashboardHeaderTopInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val dashboardHeaderHeight = dashboardHeaderTopInset + dashboardHeaderBarHeight
    val headerHasElevation by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    
    val filteredCategories = dashboardCategories.filter {
        categorySearch.isBlank() || it.name.contains(categorySearch, ignoreCase = true)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = dashboardHeaderHeight + 6.dp, bottom = 12.dp)
        ) {
            item {
                DashboardIntro()
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                AutoSlidingHeroCards()
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                DashboardSearchField(
                    value = categorySearch,
                    onValueChange = { categorySearch = it },
                    placeholder = "Search Categories..."
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                QuickActionsRow(
                    onDesigns = onOpenDesigns,
                    onProfile = onOpenProfile
                )
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item {
                CreateSection(
                    onOpenPosterMaker = onOpenTemplates,
                    onOpenVideoMaker = onOpenVideoMaker
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                SectionTitle("Categories")
                Spacer(modifier = Modifier.height(16.dp))
                CategoryGrid(categories = filteredCategories, presets = presets, onCreateCategory = onOpenCategory)
            }
        }

        DashboardStickyHeader(
            hasElevation = headerHasElevation,
            onAiClick = { showSmartAiSheet = true },
            modifier = Modifier
                .fillMaxWidth()
        )
    }

    if (showSmartAiSheet) {
        SmartAiAssistantSheet(
            presets = presets,
            onDismiss = { showSmartAiSheet = false },
            onGenerated = { draft ->
                showSmartAiSheet = false
                onSmartAiGenerated(draft)
            }
        )
    }
}

@Composable
private fun DashboardStickyHeader(
    hasElevation: Boolean,
    onAiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val headerBarHeight = 68.dp

    Surface(
        modifier = modifier.height(topInset + headerBarHeight),
        color = appBackgroundColor(),
        shadowElevation = if (hasElevation) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(topInset + headerBarHeight)
                .padding(top = topInset)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrandWordmark()
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF22D3EE))))
                    .clickable { onAiClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Smart AI Assistant", tint = Color.White)
            }
        }
    }
}

@Composable
private fun DashboardIntro() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Dashboard",
                color = appTextPrimary(),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )
            Text(
                text = "Create premium posters faster",
                color = appTextMuted(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmartAiAssistantSheet(
    presets: List<Poster>,
    onDismiss: () -> Unit,
    onGenerated: (SmartAiDraft) -> Unit
) {
    var prompt by rememberSaveable { mutableStateOf("") }
    var customMessage by rememberSaveable { mutableStateOf("") }
    var wishIndex by rememberSaveable { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val parseResult = remember(prompt) { LocalSmartAiRequestParser.parse(prompt) }
    val wishOptions = remember(parseResult?.category) {
        parseResult?.category?.let { smartAiWishesForCategory(it) }.orEmpty()
    }
    val selectedWish = wishOptions.getOrNull(wishIndex % wishOptions.size.coerceAtLeast(1)).orEmpty()
    val template = remember(parseResult, presets) {
        parseResult?.let { selectSmartAiTemplate(presets, it.category) }
    }

    LaunchedEffect(parseResult?.category) {
        wishIndex = 0
        errorMessage = null
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = appGlassSurfaceStrong(),
        tonalElevation = 8.dp
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "\u2728 Smart AI Assistant",
                        color = appTextPrimary(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "What would you like to create today?",
                        color = appTextMuted(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            item {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = {
                        prompt = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 132.dp),
                    placeholder = {
                        Text(
                            text = "Examples:\nCreate Birthday Poster\nCreate Welcome Poster\nCreate Achievement Poster\nCreate Income Poster\nCreate Birthday Video\nCreate Welcome Video\nCreate Festival Poster"
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFBFA7FF))
                    },
                    shape = RoundedCornerShape(18.dp),
                    minLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = appBorderColor(0.18f)
                    )
                )
            }
            if (parseResult != null) {
                item {
                    SmartAiDetectedCard(
                        result = parseResult,
                        templateTitle = template?.title,
                        themeName = smartAiThemeName(parseResult.category),
                        animationStyle = smartAiAnimationFor(parseResult.category)
                    )
                }
                item {
                    SmartAiWishCard(
                        message = selectedWish,
                        onRefresh = {
                            if (wishOptions.isNotEmpty()) {
                                wishIndex = (wishIndex + 1) % wishOptions.size
                            }
                        }
                    )
                }
                item {
                    OutlinedTextField(
                        value = customMessage,
                        onValueChange = { customMessage = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Custom Message") },
                        leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null) },
                        minLines = 2,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8B5CF6),
                            unfocusedBorderColor = appBorderColor(0.18f)
                        )
                    )
                }
            }
            errorMessage?.let { message ->
                item {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            item {
                Button(
                    onClick = {
                        val parsed = LocalSmartAiRequestParser.parse(prompt)
                        val selectedTemplate = parsed?.let { selectSmartAiTemplate(presets, it.category) }
                        when {
                            parsed == null -> {
                                errorMessage = "Try examples like: Create Birthday Poster, Create Welcome Poster, Create Achievement Video."
                            }
                            selectedTemplate == null -> {
                                errorMessage = "${parsed.category} templates are not available yet."
                            }
                            parsed.outputType == SmartAiOutputType.Video &&
                                parsed.category !in listOf("Birthday", "Welcome", "Achievement", "Income") -> {
                                errorMessage = "${parsed.category} video templates are not available yet."
                            }
                            else -> {
                                val music = smartAiMusicFor(parsed.category)
                                onGenerated(
                                    SmartAiDraft(
                                        outputType = parsed.outputType,
                                        category = parsed.category,
                                        templateId = selectedTemplate.id,
                                        templateTitle = selectedTemplate.title,
                                        wishMessage = selectedWish.ifBlank { smartAiWishesForCategory(parsed.category).first() },
                                        customMessage = customMessage,
                                        themeName = smartAiThemeName(parsed.category),
                                        animationStyle = smartAiAnimationFor(parsed.category),
                                        musicSelection = music.first,
                                        musicResourceName = music.second
                                    )
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SmartAiDetectedCard(
    result: SmartAiParseResult,
    templateTitle: String?,
    themeName: String,
    animationStyle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurface()),
        border = BorderStroke(1.dp, appBorderColor(0.12f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmartAiPill(result.outputType.name)
                SmartAiPill(result.category)
            }
            Text(
                text = templateTitle ?: "No matching premium template found",
                color = appTextPrimary(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Theme: $themeName" + if (result.outputType == SmartAiOutputType.Video) " • Animation: $animationStyle" else "",
                color = appTextMuted(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SmartAiPill(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFF8B5CF6).copy(alpha = 0.16f),
        border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.28f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            color = appTextPrimary(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun SmartAiWishCard(
    message: String,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurface()),
        border = BorderStroke(1.dp, appBorderColor(0.12f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFBFA7FF))
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = appTextPrimary(),
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = onRefresh) {
                Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Refresh Wish")
            }
        }
    }
}

private fun selectSmartAiTemplate(presets: List<Poster>, category: String): Poster? {
    val categoryPresets = presets.filter { it.category.equals(category, ignoreCase = true) }
    if (categoryPresets.isEmpty()) return null
    val preferredKeywords = when (category) {
        "Birthday" -> listOf("Royal Hero Birthday", "Royal Hero", "Premium", "Gold")
        "Welcome" -> listOf("Premium Team Welcome", "Premium", "Corporate", "Executive")
        "Achievement" -> listOf("Gold Achievement", "Champion", "Hall", "Premium", "Gold")
        "Income" -> listOf("Business Success", "Growth Leader", "Income", "Luxury", "Reward")
        "Festival" -> listOf("Festival Premium", "Festival", "Premium")
        "Anniversary" -> listOf("Anniversary", "Premium")
        "Motivation" -> listOf("Motivation", "Premium")
        else -> listOf("Premium")
    }
    return categoryPresets.maxByOrNull { preset ->
        preferredKeywords.mapIndexed { index, keyword ->
            if (preset.title.contains(keyword, ignoreCase = true)) preferredKeywords.size - index else 0
        }.sum()
    } ?: categoryPresets.first()
}

private fun smartAiTemplateIndex(presets: List<Poster>, draft: SmartAiDraft?): Int? {
    if (draft == null) return null
    val categoryPresets = presets.filter { it.category.equals(draft.category, ignoreCase = true) }.take(6)
    return categoryPresets.indexOfFirst { it.id == draft.templateId }.takeIf { it >= 0 }
}

private fun smartAiThemeName(category: String): String = when (category) {
    "Birthday" -> "Purple, Gold, Pink"
    "Welcome" -> "Blue, Sky"
    "Achievement" -> "Black Gold, Royal Blue"
    "Income" -> "Green, Gold"
    "Festival" -> "Colorful"
    "Anniversary" -> "Rose Gold, White"
    "Motivation" -> "Dark Premium, Neon"
    else -> "Premium"
}

private fun smartAiAnimationFor(category: String): String = when (category) {
    "Birthday" -> "Zoom In"
    "Achievement" -> "Fade"
    "Welcome" -> "Slide Right"
    "Income" -> "Scale"
    "Festival" -> "Pulse"
    "Anniversary" -> "Fade"
    "Motivation" -> "Slide Up"
    else -> "Fade"
}

private fun smartAiColorStyleFor(category: String): VideoColorStyle {
    val presets = videoColorPresets()
    val preferred = when (category) {
        "Birthday" -> listOf("Purple", "Gold", "Pink")
        "Welcome" -> listOf("Sky Blue", "Corporate Blue", "Royal Blue")
        "Achievement" -> listOf("Black Gold", "Royal Blue", "Luxury Gold")
        "Income" -> listOf("Green", "Luxury Gold", "Emerald")
        "Festival" -> listOf("Sunset", "Neon", "Fire")
        "Anniversary" -> listOf("Rose Gold", "Minimal White", "Gold")
        "Motivation" -> listOf("Dark Premium", "Neon", "Corporate Blue")
        else -> listOf("Luxury Gold")
    }
    return preferred.firstNotNullOfOrNull { name -> presets.firstOrNull { it.name == name }?.style }
        ?: defaultVideoColorStyle
}

private fun smartAiMusicFor(category: String): Pair<String, String?> = when (category) {
    "Birthday" -> "Happy Birthday Celebration" to "birthday_song1"
    "Welcome" -> "Welcome Celebration" to "welcome_song1"
    "Achievement" -> "Achievement Success" to "achievement_song1"
    "Income" -> "Income Success Theme" to "income_song1"
    else -> "No music selected" to null
}

private fun smartAiWishesForCategory(category: String): List<String> {
    val seeds = when (category) {
        "Birthday" -> listOf(
            "Happy Birthday! Wishing you health, happiness and success.",
            "May this birthday bring endless joy, prosperity and bright new opportunities.",
            "Wishing you a wonderful year filled with growth, success and celebration.",
            "Stay blessed, keep inspiring everyone and enjoy a beautiful birthday.",
            "Have a fantastic birthday and an amazing future ahead.",
            "May your special day be filled with smiles, love and memorable moments.",
            "Wishing you courage, confidence and success in the year ahead.",
            "May this new year of life bring fresh achievements and happiness.",
            "Sending warm birthday wishes for prosperity, peace and progress.",
            "Celebrate your day with pride, joy and a heart full of gratitude."
        )
        "Welcome" -> listOf(
            "Welcome to the team! Wishing you great success.",
            "Together we will achieve amazing milestones.",
            "Excited to have you with us and ready for a successful journey.",
            "Welcome aboard! Your journey starts today.",
            "Best wishes for a bright and successful future with us.",
            "We are delighted to welcome you to our growing team.",
            "Your talent adds new energy to our shared mission.",
            "A warm welcome and best wishes for meaningful achievements.",
            "Welcome to a place where your ideas and efforts matter.",
            "We look forward to building success together."
        )
        "Achievement" -> listOf(
            "Congratulations on your outstanding achievement.",
            "Your hard work, focus and consistency have paid off.",
            "Wishing you continued success and greater milestones ahead.",
            "Keep reaching new milestones and inspiring everyone around you.",
            "Proud of your incredible accomplishment and dedication.",
            "This achievement reflects your passion, discipline and leadership.",
            "Your success is well deserved and truly inspiring.",
            "Congratulations on turning effort into excellence.",
            "A proud moment earned through commitment and perseverance.",
            "May this achievement open the door to even bigger wins."
        )
        "Income" -> listOf(
            "Congratulations on your income milestone.",
            "Your dedication is creating meaningful success.",
            "Wishing you greater financial achievements and steady growth.",
            "Keep growing, leading and inspiring others.",
            "Success follows consistent effort and smart action.",
            "Your growth reflects discipline, focus and strong leadership.",
            "Celebrating your financial progress and business momentum.",
            "May this income milestone be the start of bigger success.",
            "Your performance is building a powerful success story.",
            "Congratulations on achieving a rewarding business milestone."
        )
        "Festival" -> listOf(
            "Wishing you a joyful festival filled with happiness and prosperity.",
            "May this festival bring light, peace and success to your life.",
            "Celebrate the season with gratitude, joy and togetherness.",
            "Warm festive wishes for a bright and prosperous future.",
            "May every celebration bring new hope and beautiful memories.",
            "Wishing you festive moments filled with love and positivity.",
            "May this festival inspire happiness, harmony and success.",
            "Celebrate with joy and welcome a season of good fortune.",
            "Sending warm wishes for a colorful and meaningful festival.",
            "May the festive spirit bring peace, progress and prosperity."
        )
        "Anniversary" -> listOf(
            "Wishing you a wonderful anniversary filled with love and success.",
            "Celebrating a journey of trust, togetherness and beautiful memories.",
            "May this anniversary bring more happiness and shared achievements.",
            "Congratulations on another year of meaningful togetherness.",
            "Wishing you many more years of joy, growth and celebration.",
            "May your bond continue to grow stronger with every milestone.",
            "Celebrating love, commitment and a beautiful journey together.",
            "Warm anniversary wishes for continued happiness and prosperity.",
            "May every year ahead be brighter than the last.",
            "Congratulations on a special milestone worth celebrating."
        )
        "Motivation" -> listOf(
            "Success begins with the courage to take the next step.",
            "Small consistent actions create remarkable results.",
            "Believe in your vision and keep moving forward.",
            "Discipline turns dreams into achievements.",
            "Your effort today builds the confidence of tomorrow.",
            "Stay focused, stay positive and keep growing.",
            "Every milestone starts with one determined decision.",
            "Great progress comes from showing up every day.",
            "Your potential grows when you refuse to give up.",
            "Dream boldly, work honestly and rise steadily."
        )
        else -> listOf("Wishing you success, happiness and prosperity.")
    }
    return List(30) { index -> seeds[index % seeds.size] }
}

@Composable
private fun AutoSlidingHeroCards() {
    var selectedHero by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            selectedHero = (selectedHero + 1) % heroCards.size
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AnimatedContent(
            targetState = selectedHero,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 650)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(durationMillis = 650)
                )
            },
            label = "hero_horizontal_slide"
        ) { index ->
            HeroCard(hero = heroCards[index])
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            heroCards.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = if (index == selectedHero) 22.dp else 8.dp, height = 8.dp)
                        .clip(CircleShape)
                        .background(if (index == selectedHero) Color.White else Color.White.copy(alpha = 0.28f))
                )
            }
        }
    }
}

@Composable
private fun HeroCard(hero: HeroCardSpec) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp)
            .shadow(18.dp, RoundedCornerShape(24.dp), clip = false)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(hero.colors)),
    ) {
        Box(
            modifier = Modifier
                .size(190.dp)
                .offset(x = 220.dp, y = 54.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.16f))
        )
        Box(
            modifier = Modifier
                .size(92.dp)
                .offset(x = 250.dp, y = 22.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.14f))
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(22.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = hero.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = hero.title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 30.sp
                    )
                )
                Text(
                    text = hero.subtitle,
                    color = Color.White.copy(alpha = 0.86f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DashboardSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = { Text(placeholder, color = appTextMuted()) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = appTextMuted())
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = appTextMuted())
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = darkFieldColors()
    )
}

@Composable
private fun QuickActionsRow(
    onDesigns: () -> Unit,
    onProfile: () -> Unit
) {
    val actions = listOf(
        Triple("My\nDesigns", Icons.Default.FolderSpecial, onDesigns),
        Triple("Profile", Icons.Default.Person, onProfile)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        actions.forEach { (label, icon, action) ->
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .clickable { action() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(icon, contentDescription = null, tint = Color(0xFFBFA7FF), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = label,
                        color = appTextPrimary(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryGrid(categories: List<CategorySpec>, presets: List<Poster>, onCreateCategory: (String) -> Unit) {
    if (categories.isEmpty()) {
        Text("No categories found.", color = appTextMuted())
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        categories.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { category ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(112.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Brush.linearGradient(category.colors))
                            .clickable { onCreateCategory(category.name) }
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(end = 28.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                category.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = category.name,
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${presets.count { it.category == category.name }} Templates",
                                color = Color.White.copy(alpha = 0.82f),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.82f),
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(20.dp)
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CreateSection(
    onOpenPosterMaker: () -> Unit,
    onOpenVideoMaker: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Create")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CreateModuleCard(
                title = "Poster Maker",
                subtitle = "Create professional posters",
                icon = Icons.Default.Image,
                colors = listOf(Color(0xFF7C3AED), Color(0xFFEC4899)),
                onClick = onOpenPosterMaker,
                modifier = Modifier.weight(1f)
            )
            CreateModuleCard(
                title = "Video Maker",
                subtitle = "Create MP4 videos with music",
                icon = Icons.Default.PlayCircle,
                colors = listOf(Color(0xFF2563EB), Color(0xFF06B6D4)),
                onClick = onOpenVideoMaker,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CreateModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    colors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(164.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(colors))
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.86f),
                    style = MaterialTheme.typography.bodySmall,
                    minLines = 2,
                    maxLines = 2
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "Create",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun VideoMakerModule(
    viewModel: PosterViewModel,
    presets: List<Poster>,
    profileSettings: ProfileSettings,
    smartAiDraft: SmartAiDraft? = null,
    onBackToDashboard: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    var videoDraft by remember { mutableStateOf(VideoCustomizationState()) }
    var videoForm by remember { mutableStateOf(defaultPosterStateFor("Birthday")) }
    var activeVideoPreset by remember { mutableStateOf<Poster?>(null) }
    var activeVideoLayoutIndex by remember { mutableStateOf(1) }
    var activeVideoTemplateKey by remember { mutableStateOf("") }
    var activeVideoCategory by remember { mutableStateOf("") }
    var activeVideoTemplateName by remember { mutableStateOf("") }
    var activeVideoColors by remember { mutableStateOf(listOf(Color(0xFF7C3AED), Color(0xFF2563EB))) }
    var generatedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var videoColorStyle by remember { mutableStateOf(defaultVideoColorStyle) }
    var videoBackgroundStyle by remember { mutableStateOf(VideoBackgroundStyle()) }
    var capturedVideoPosterBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val smartAiTemplatePosition = remember(smartAiDraft?.templateId, presets) {
        smartAiTemplateIndex(presets, smartAiDraft)
    }
    val videoStartDestination = smartAiDraft
        ?.takeIf { it.outputType == SmartAiOutputType.Video && smartAiTemplatePosition != null }
        ?.let { "video_template/${it.category}/$smartAiTemplatePosition" }
        ?: "video_categories"

    fun navigateBackInVideoModule() {
        if (navController.currentDestination?.route == "video_categories") {
            onBackToDashboard()
        } else {
            navController.navigateBackOr(onBackToDashboard)
        }
    }

    BackHandler(enabled = true, onBack = ::navigateBackInVideoModule)

    NavHost(
        navController = navController,
        startDestination = videoStartDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable("video_categories") {
            VideoCategoriesScreen(
                onBack = ::navigateBackInVideoModule,
                onOpenCategory = { category ->
                    navController.navigate("video_templates/${category.lowercase()}")
                }
            )
        }
        composable(
            route = "video_templates/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { entry ->
            val category = entry.arguments?.getString("category")
                ?.replaceFirstChar { it.uppercase() }
                .orEmpty()
            val categoryPresets = presets.filter { it.category == category }.take(6)
            VideoTemplatesScreen(
                category = category,
                templates = categoryPresets,
                onBack = ::navigateBackInVideoModule,
                onSelectTemplate = { index ->
                    navController.navigate("video_template/$category/$index")
                }
            )
        }
        composable(
            route = "video_template/{category}/{templateIndex}",
            arguments = listOf(
                navArgument("category") { type = NavType.StringType },
                navArgument("templateIndex") { type = NavType.IntType }
            )
        ) { entry ->
            val category = entry.arguments?.getString("category").orEmpty()
            val templateIndex = entry.arguments?.getInt("templateIndex") ?: -1
            val categoryPresets = presets.filter { it.category == category }.take(6)
            val template = categoryPresets.getOrNull(templateIndex)
            val templateName = template?.title.orEmpty()
            val templateKey = "$category/$templateIndex"
            val aiForTemplate = smartAiDraft?.takeIf {
                it.outputType == SmartAiOutputType.Video &&
                    it.category.equals(category, ignoreCase = true) &&
                    it.templateId == template?.id
            }
            activeVideoCategory = category
            activeVideoTemplateName = templateName
            activeVideoPreset = template
            activeVideoLayoutIndex = (templateIndex + 1).coerceAtLeast(1)
            activeVideoColors = categoryTemplateColors(category, activeVideoLayoutIndex)
            LaunchedEffect(templateKey, aiForTemplate?.finalWishMessage) {
                if (activeVideoTemplateKey != templateKey) {
                    activeVideoTemplateKey = templateKey
                    val defaultForm = defaultPosterStateFor(category).copy(
                        companyLogoUri = profileSettings.companyLogoUri.takeIf { it.isNotBlank() }?.let(Uri::parse),
                        companyName = profileSettings.companyName.ifBlank { "ABC Marketing Pvt Ltd" },
                        websiteName = profileSettings.websiteName.ifBlank { "www.abcmarketing.com" },
                        mobileNumber = profileSettings.mobileNumber,
                        wishesMessage = aiForTemplate?.finalWishMessage ?: defaultWishesFor(category)
                    )
                    val music = aiForTemplate?.let { it.musicSelection to it.musicResourceName }
                        ?: ("No music selected" to null)
                    videoForm = defaultForm
                    videoDraft = videoDraftFromPosterForm(
                        form = defaultForm,
                        category = category,
                        previous = VideoCustomizationState(
                            companyLogoUri = defaultForm.companyLogoUri,
                            companyName = defaultForm.companyName,
                            website = defaultForm.websiteName,
                            personName = "Rahul Sharma",
                            title = defaultVideoTitle(category),
                            wishMessage = aiForTemplate?.finalWishMessage ?: defaultVideoMessage(category),
                            animationStyle = aiForTemplate?.animationStyle ?: "Fade",
                            musicSelection = music.first,
                            musicResourceName = music.second
                        )
                    )
                    videoColorStyle = aiForTemplate?.let { smartAiColorStyleFor(category) } ?: defaultVideoColorStyle
                    videoBackgroundStyle = VideoBackgroundStyle()
                    generatedVideoUri = null
                }
            }
            LaunchedEffect(
                templateKey,
                profileSettings.companyLogoUri,
                profileSettings.companyName,
                profileSettings.websiteName,
                profileSettings.mobileNumber
            ) {
                if (activeVideoTemplateKey == templateKey) {
                    val profileLogo = profileSettings.companyLogoUri.takeIf { it.isNotBlank() }?.let(Uri::parse)
                    val profileCompanyName = profileSettings.companyName.ifBlank { "ABC Marketing Pvt Ltd" }
                    val profileWebsite = profileSettings.websiteName.ifBlank { "www.abcmarketing.com" }
                    videoForm = videoForm.copy(
                        companyLogoUri = profileLogo,
                        companyName = profileCompanyName,
                        websiteName = profileWebsite,
                        mobileNumber = profileSettings.mobileNumber
                    )
                    videoDraft = videoDraft.copy(
                        companyLogoUri = profileLogo,
                        companyName = profileCompanyName,
                        website = profileWebsite
                    )
                    generatedVideoUri = null
                }
            }
            if (template != null) {
                VideoPosterCustomizationScreen(
                    preset = template,
                    form = videoForm,
                    videoState = videoDraft,
                    colorStyle = videoColorStyle,
                    backgroundStyle = videoBackgroundStyle,
                    layoutIndex = activeVideoLayoutIndex,
                    onFormChange = { updatedForm ->
                        videoForm = updatedForm
                        videoDraft = videoDraftFromPosterForm(updatedForm, category, videoDraft)
                        generatedVideoUri = null
                        capturedVideoPosterBitmap?.recycle()
                        capturedVideoPosterBitmap = null
                    },
                    onVideoStateChange = {
                        videoDraft = it
                        generatedVideoUri = null
                    },
                    onColorStyleChange = {
                        videoColorStyle = it
                        generatedVideoUri = null
                    },
                    onBackgroundStyleChange = {
                        videoBackgroundStyle = it
                        generatedVideoUri = null
                    },
                    onBack = ::navigateBackInVideoModule,
                    onOpenMusicLibrary = { navController.navigate("music_library") },
                    onSaveImage = { bitmap ->
                        capturedVideoPosterBitmap?.recycle()
                        capturedVideoPosterBitmap = bitmap
                        navController.navigate("video_processing")
                    },
                    onShare = {
                        val uri = generatedVideoUri
                        if (uri == null) {
                            viewModel.showStatusMessage("Generate the video first.")
                        } else {
                            context.shareGeneratedVideo(uri)
                        }
                    },
                    onGenerate = { bitmap ->
                        capturedVideoPosterBitmap?.recycle()
                        capturedVideoPosterBitmap = bitmap
                        navController.navigate("video_preview")
                    }
                )
            } else {
                VideoTemplatesScreen(
                    category = category,
                    templates = categoryPresets,
                    onBack = ::navigateBackInVideoModule,
                    onSelectTemplate = { index ->
                        navController.navigate("video_template/$category/$index")
                    }
                )
            }
        }
        composable("music_library") {
            MusicLibraryScreen(
                selectedState = videoDraft,
                currentCategory = activeVideoCategory,
                onBack = ::navigateBackInVideoModule,
                onUseMusic = { updatedDraft ->
                    videoDraft = updatedDraft
                    generatedVideoUri = null
                    navController.popBackStack()
                }
            )
        }
        composable("video_preview") {
            val template = activeVideoPreset
            if (template != null) {
                VideoPosterPreviewScreen(
                    preset = template,
                    form = videoForm,
                    videoState = videoDraft,
                    colorStyle = videoColorStyle,
                    backgroundStyle = videoBackgroundStyle,
                    layoutIndex = activeVideoLayoutIndex,
                    onBack = ::navigateBackInVideoModule,
                    onSaveImage = { bitmap ->
                        capturedVideoPosterBitmap?.recycle()
                        capturedVideoPosterBitmap = bitmap
                        coroutineScope.launch {
                            savePosterFromVideoTemplate(
                                context = context,
                                poster = template,
                                form = videoForm,
                                layoutIndex = activeVideoLayoutIndex
                            )
                        }
                    },
                    onGenerate = { bitmap ->
                        capturedVideoPosterBitmap?.recycle()
                        capturedVideoPosterBitmap = bitmap
                        navController.navigate("video_processing")
                    }
                )
            } else {
                VideoPreviewScreen(
                    templateName = activeVideoTemplateName,
                    category = activeVideoCategory,
                    colors = activeVideoColors,
                    state = videoDraft,
                    onBack = ::navigateBackInVideoModule,
                    onSaveImage = {
                        coroutineScope.launch {
                            saveVideoTemplateImageToGallery(
                                context = context,
                                state = videoDraft,
                                colors = activeVideoColors,
                                category = activeVideoCategory
                            )
                        }
                    },
                    onGenerate = { navController.navigate("video_processing") }
                )
            }
        }
        composable("video_processing") {
            val processingVideoDraft = videoDraftFromPosterForm(
                form = videoForm,
                category = activeVideoCategory,
                previous = videoDraft
            )
            val handleVideoCompleted: (Uri?) -> Unit = { uri ->
                generatedVideoUri = uri
                if (uri != null) {
                    coroutineScope.launch {
                        runCatching {
                            val title = activeVideoPreset?.title
                                ?: processingVideoDraft.title.ifBlank { defaultVideoTitle(activeVideoCategory) }
                            val fallbackFrame = capturedVideoPosterBitmap?.let {
                                it.scaledSoftwareCopy(720, 1280)
                            }
                            val thumbnailPath = try {
                                saveVideoFirstFrameThumbnail(
                                    context = context,
                                    videoUri = uri,
                                    title = title,
                                    fallbackBitmap = fallbackFrame
                                )
                            } finally {
                                fallbackFrame?.recycle()
                            }
                            saveGeneratedVideoDesignRecord(
                                viewModel = viewModel,
                                preset = activeVideoPreset,
                                category = activeVideoCategory,
                                form = videoForm,
                                videoState = processingVideoDraft,
                                videoUri = uri,
                                thumbnailPath = thumbnailPath,
                                colorStyle = videoColorStyle,
                                backgroundStyle = videoBackgroundStyle,
                                layoutIndex = activeVideoLayoutIndex
                            )
                        }.onSuccess {
                            viewModel.showStatusMessage("Video saved successfully.")
                        }.onFailure {
                            viewModel.showStatusMessage("Video saved to Gallery, but unable to add it to My Designs.")
                        }
                        navController.navigate("video_generated") {
                            popUpTo("video_processing") { inclusive = true }
                        }
                    }
                } else {
                    viewModel.showStatusMessage("Unable to generate video.")
                    navController.navigate("video_generated") {
                        popUpTo("video_processing") { inclusive = true }
                    }
                }
            }
            activeVideoPreset?.let { preset ->
                VideoPosterProcessingScreen(
                    preset = preset,
                    form = videoForm,
                    videoState = processingVideoDraft,
                    colorStyle = videoColorStyle,
                    backgroundStyle = videoBackgroundStyle,
                    layoutIndex = activeVideoLayoutIndex,
                    capturedPosterBitmap = capturedVideoPosterBitmap,
                    onCompleted = handleVideoCompleted
                )
            } ?: VideoProcessingScreen(
                state = processingVideoDraft,
                colors = videoBackgroundStyle.colors ?: videoColorStyle.backgroundColors ?: activeVideoColors,
                category = activeVideoCategory,
                onCompleted = handleVideoCompleted
            )
        }
        composable("video_generated") {
            VideoGeneratedScreen(
                videoUri = generatedVideoUri,
                onDone = {
                    navController.navigate("video_categories") {
                        popUpTo("video_categories") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun VideoPosterProcessingScreen(
    preset: Poster,
    form: CustomPosterState,
    videoState: VideoCustomizationState,
    colorStyle: VideoColorStyle,
    backgroundStyle: VideoBackgroundStyle,
    layoutIndex: Int,
    capturedPosterBitmap: Bitmap?,
    onCompleted: (Uri?) -> Unit
) {
    val context = LocalContext.current
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val uri = runCatching {
            withContext(Dispatchers.IO) {
                val finalPosterBitmap = capturedPosterBitmap?.let {
                    it.scaledSoftwareCopy(720, 1280)
                } ?: renderPosterBitmap(
                        context = context,
                        poster = preset,
                        form = form,
                        layoutIndex = layoutIndex,
                        colorStyle = colorStyle,
                        backgroundStyle = backgroundStyle
                    )
                try {
                    exportPosterBitmapVideoToGallery(
                        context = context,
                        posterBitmap = finalPosterBitmap,
                        state = videoState,
                        title = preset.title,
                        onProgress = { progress = it.coerceIn(progress, 0.98f) }
                    )
                } finally {
                    finalPosterBitmap.recycle()
                }
            }
        }.onFailure {
            Log.e("PosterVideoExport", "Unable to generate poster video", it)
        }.getOrNull()
        progress = 1f
        delay(350)
        onCompleted(uri)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(76.dp),
                color = Color(0xFF7C3AED),
                strokeWidth = 7.dp
            )
            Text(
                text = "Generating Video...",
                color = appTextPrimary(),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = Color(0xFF7C3AED),
                trackColor = appGlassSurfaceStrong()
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                color = appTextPrimary(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun VideoCategoriesScreen(
    onBack: () -> Unit,
    onOpenCategory: (String) -> Unit
) {
    val categories = listOf(
        CategorySpec("Birthday", Icons.Default.Cake, listOf(Color(0xFFFF4FA3), Color(0xFF8B5CF6))),
        CategorySpec("Achievement", Icons.Default.EmojiEvents, listOf(Color(0xFFFF7A1A), Color(0xFFFFC857))),
        CategorySpec("Welcome", Icons.Default.Groups, listOf(Color(0xFF2563EB), Color(0xFF06B6D4))),
        CategorySpec("Income", Icons.AutoMirrored.Filled.TrendingUp, listOf(Color(0xFF22C55E), Color(0xFF059669)))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = appTextPrimary()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Video Maker",
                        color = appTextPrimary(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Choose a category to create your video",
                        color = appTextMuted(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        items(categories) { category ->
            VideoCategoryCard(
                category = category,
                onClick = { onOpenCategory(category.name) }
            )
        }
    }
}

@Composable
private fun VideoCategoryCard(
    category: CategorySpec,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp)
            .shadow(9.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(category.colors))
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(94.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f))
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = category.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "6 Templates",
                        color = Color.White.copy(alpha = 0.84f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Browse",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoTemplatesScreen(
    category: String,
    templates: List<Poster>,
    onBack: () -> Unit,
    onSelectTemplate: (Int) -> Unit
) {
    val gridState = rememberLazyGridState()
    val headerVisible by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset < 12
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        AnimatedVisibility(
            visible = headerVisible,
            enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { -it },
            exit = fadeOut(tween(250)) + slideOutVertically(tween(250)) { -it }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = appTextPrimary()
                    )
                }
                Column {
                    Text(
                        text = "$category Video Templates",
                        color = appTextPrimary(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "6 Templates Available",
                        color = appTextMuted(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = headerVisible,
            enter = fadeIn(tween(280)),
            exit = fadeOut(tween(250))
        ) {
            Spacer(modifier = Modifier.height(18.dp))
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            itemsIndexed(templates) { index, template ->
                TemplateCard(
                    preset = template,
                    layoutIndex = index + 1,
                    onClick = { onSelectTemplate(index) }
                )
            }
        }
    }
}

@Composable
private fun VideoTemplateCard(
    template: VideoTemplateSpec,
    category: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(TemplateCardHeight)
            .shadow(8.dp, RoundedCornerShape(TemplateCardCornerRadius))
            .clickable { onClick() },
        shape = RoundedCornerShape(TemplateCardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(TemplateCardPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(TemplatePreviewHeight),
                contentAlignment = Alignment.Center
            ) {
                VideoTemplateThumbnail(
                    template = template,
                    category = category,
                    modifier = Modifier
                        .requiredHeight(TemplatePreviewHeight)
                        .aspectRatio(9f / 16f)
                        .clip(RoundedCornerShape(TemplatePreviewCornerRadius))
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = template.name,
                color = appTextPrimary(),
                modifier = Modifier.requiredHeight(TemplateTitleHeight),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Tap to customize",
                color = Color(0xFFBFA7FF),
                modifier = Modifier.requiredHeight(TemplateActionHeight),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun VideoTemplateThumbnail(
    template: VideoTemplateSpec,
    category: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Brush.linearGradient(template.colors))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .align(Alignment.TopEnd)
                .offset(x = 24.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.14f))
        )
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = category.uppercase(),
                color = Color.White.copy(alpha = 0.84f),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
        Box(
            modifier = Modifier
                .size(86.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f))
                .border(2.dp, Color.White.copy(alpha = 0.72f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = template.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(42.dp)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = template.name,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                maxLines = 3,
                overflow = TextOverflow.Clip
            )
            HorizontalDivider(color = Color.White.copy(alpha = 0.38f))
            Text(
                text = "Premium video story",
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun VideoTemplatePlaceholderScreen(
    templateName: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = appTextPrimary()
                )
            }
            Text(
                text = "$templateName Video",
                color = appTextPrimary(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Customization screen will be implemented in the next step.",
                color = appTextMuted(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun videoDraftFromPosterForm(
    form: CustomPosterState,
    category: String,
    previous: VideoCustomizationState
): VideoCustomizationState {
    return previous.copy(
        companyLogoUri = form.companyLogoUri,
        companyName = form.companyName,
        website = form.websiteName,
        profilePhotoUri = form.profilePhotoUri,
        personName = form.personName,
        title = defaultVideoTitle(category),
        wishMessage = form.wishesMessage
    )
}

private fun videoColorPresets(): List<VideoColorPreset> {
    fun preset(name: String, accent: Color, border: Color = accent, title: Color = Color.White, bg: List<Color>? = null) =
        VideoColorPreset(
            name = name,
            style = VideoColorStyle(
                titleColor = title,
                nameColor = title,
                messageColor = title.copy(alpha = 0.9f),
                footerColor = accent,
                borderColor = border,
                accentColor = accent,
                backgroundColors = bg
            )
        )
    return listOf(
        preset("Gold", Color(0xFFFFC83D), Color(0xFFD4AF37)),
        preset("Black Gold", Color(0xFFFFC83D), Color(0xFFD4AF37), bg = listOf(Color.Black, Color(0xFF1C1405), Color(0xFF020202))),
        preset("Royal Blue", Color(0xFF60A5FA), Color(0xFF2563EB), bg = listOf(Color(0xFF06172F), Color(0xFF1D4ED8))),
        preset("Purple", Color(0xFFC084FC), Color(0xFF7C3AED)),
        preset("Pink", Color(0xFFF472B6), Color(0xFFEC4899)),
        preset("Orange", Color(0xFFFB923C), Color(0xFFF97316)),
        preset("Red", Color(0xFFF87171), Color(0xFFDC2626)),
        preset("Green", Color(0xFF4ADE80), Color(0xFF16A34A)),
        preset("Emerald", Color(0xFF34D399), Color(0xFF059669)),
        preset("Teal", Color(0xFF2DD4BF), Color(0xFF0D9488)),
        preset("Sky Blue", Color(0xFF38BDF8), Color(0xFF0284C7)),
        preset("Navy", Color(0xFF93C5FD), Color(0xFF1E3A8A), bg = listOf(Color(0xFF020617), Color(0xFF172554))),
        preset("White", Color.White, Color(0xFFE5E7EB), title = Color.White),
        preset("Silver", Color(0xFFE5E7EB), Color(0xFF94A3B8)),
        preset("Rose Gold", Color(0xFFF9A8D4), Color(0xFFFB7185)),
        preset("Sunset", Color(0xFFFBBF24), Color(0xFFEC4899), bg = listOf(Color(0xFF7C2D12), Color(0xFFDB2777))),
        preset("Ocean", Color(0xFF67E8F9), Color(0xFF0891B2), bg = listOf(Color(0xFF083344), Color(0xFF0E7490))),
        preset("Fire", Color(0xFFFDE047), Color(0xFFEF4444), bg = listOf(Color(0xFF450A0A), Color(0xFFEA580C))),
        preset("Neon", Color(0xFFA3E635), Color(0xFF22D3EE), bg = listOf(Color.Black, Color(0xFF312E81))),
        preset("Dark Premium", Color(0xFFFFC83D), Color(0xFF475569), bg = listOf(Color(0xFF020617), Color(0xFF111827))),
        preset("Minimal White", Color(0xFF111827), Color(0xFFE5E7EB), title = Color(0xFF111827), bg = listOf(Color.White, Color(0xFFF8FAFC))),
        preset("Corporate Blue", Color(0xFF2563EB), Color(0xFF60A5FA), bg = listOf(Color(0xFF0F172A), Color(0xFF1D4ED8))),
        preset("Luxury Gold", Color(0xFFFFD700), Color(0xFFB8860B), bg = listOf(Color.Black, Color(0xFF2A1F06)))
    )
}

private fun videoAnimationOptions(): List<String> = listOf(
    "Fade",
    "Zoom In",
    "Zoom Out",
    "Slide Left",
    "Slide Right",
    "Slide Up",
    "Slide Down",
    "Scale",
    "Bounce",
    "Rotate",
    "Flip",
    "Pulse",
    "None"
)

private suspend fun savePosterFromVideoTemplate(
    context: Context,
    poster: Poster,
    form: CustomPosterState,
    layoutIndex: Int
) {
    val bitmap = renderPosterBitmap(context, poster, form, layoutIndex)
    saveBitmapToGallery(context, bitmap, poster.title)
}

private fun Context.shareGeneratedVideo(uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "video/mp4"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(intent, "Share video"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoPosterCustomizationScreen(
    preset: Poster,
    form: CustomPosterState,
    videoState: VideoCustomizationState,
    colorStyle: VideoColorStyle,
    backgroundStyle: VideoBackgroundStyle,
    layoutIndex: Int,
    onFormChange: (CustomPosterState) -> Unit,
    onVideoStateChange: (VideoCustomizationState) -> Unit,
    onColorStyleChange: (VideoColorStyle) -> Unit,
    onBackgroundStyleChange: (VideoBackgroundStyle) -> Unit,
    onBack: () -> Unit,
    onOpenMusicLibrary: () -> Unit,
    onSaveImage: (Bitmap?) -> Unit,
    onShare: () -> Unit,
    onGenerate: (Bitmap?) -> Unit
) {
    val previewCaptureLayer = rememberGraphicsLayer()
    val captureScope = rememberCoroutineScope()

    suspend fun captureCurrentPreview(): Bitmap? {
        return runCatching {
            previewCaptureLayer.toImageBitmap().asAndroidBitmap()
        }.getOrNull()
    }

    var activeSheet by remember { mutableStateOf<String?>(null) }
    var showAnimationSheet by remember { mutableStateOf(false) }
    var showDurationSheet by remember { mutableStateOf(false) }
    val formListState = rememberLazyListState()
    val videoScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val headerExpanded by remember {
        derivedStateOf {
            formListState.firstVisibleItemIndex == 0 &&
                formListState.firstVisibleItemScrollOffset < 32 &&
                videoScrollBehavior.state.collapsedFraction < 0.08f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
            .nestedScroll(videoScrollBehavior.nestedScrollConnection)
    ) {
        TopAppBar(
            modifier = Modifier.statusBarsPadding(),
            title = {
                AnimatedVisibility(
                    visible = headerExpanded,
                    enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { -it / 3 },
                    exit = fadeOut(tween(250)) + slideOutVertically(tween(250)) { -it / 3 }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            text = preset.title,
                            color = appTextPrimary(),
                            fontSize = 16.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${preset.category} Video Template",
                            color = appTextMuted(),
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = appTextPrimary())
                }
            },
            actions = {
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFFEC4899))
                }
                IconButton(onClick = {
                    captureScope.launch { onSaveImage(captureCurrentPreview()) }
                }) {
                    Icon(Icons.Default.Download, contentDescription = "Download", tint = Color(0xFFEC4899))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = appBackgroundColor()),
            windowInsets = WindowInsets(0.dp),
            expandedHeight = TemplateToolbarHeight,
            scrollBehavior = videoScrollBehavior
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = formListState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(330.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val previewModifier = if (maxWidth * 16f / 9f <= maxHeight) {
                        Modifier.fillMaxWidth().aspectRatio(9f / 16f)
                    } else {
                        Modifier.height(maxHeight).aspectRatio(9f / 16f)
                    }
                    AnimatedVideoPosterPreview(
                        preset = preset,
                        form = form,
                        videoState = videoState,
                        colorStyle = colorStyle,
                        backgroundStyle = backgroundStyle,
                        layoutIndex = layoutIndex,
                        onPhotoAdjustmentChange = { adjustment ->
                            onFormChange(
                                form.copy(
                                    photoScale = adjustment.scale,
                                    photoOffsetX = adjustment.offsetX,
                                    photoOffsetY = adjustment.offsetY
                                )
                            )
                        },
                        modifier = previewModifier.drawWithContent {
                            previewCaptureLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawContent()
                        }
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = headerExpanded,
                    enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { -it / 3 },
                    exit = fadeOut(tween(250)) + slideOutVertically(tween(250)) { -it / 3 }
                ) {
                    VideoPosterPlayerTimeline(
                        videoState = videoState,
                        duration = videoState.duration
                    )
                }
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { VideoPosterToolButton("Photo", Icons.Default.PhotoCamera) { activeSheet = "Photo" } }
                    item { VideoPosterToolButton("Text", Icons.Default.TextFields) { activeSheet = "Text" } }
                    item { VideoPosterToolButton("Bg", Icons.Default.Image) { activeSheet = "Background" } }
                    item { VideoPosterToolButton("Colors", Icons.Default.Palette) { activeSheet = "Colors" } }
                }
            }
            item {
                VideoPosterSettingsCard(
                    videoState = videoState,
                    onAnimationClick = { showAnimationSheet = true },
                    onDurationClick = { showDurationSheet = true },
                    onMusicClick = onOpenMusicLibrary
                )
            }
        }
    }

    VideoPosterEditSheet(
        activeSheet = activeSheet,
        preset = preset,
        form = form,
        colorStyle = colorStyle,
        backgroundStyle = backgroundStyle,
        onDismiss = { activeSheet = null },
        onFormChange = onFormChange,
        onColorStyleChange = onColorStyleChange,
        onBackgroundStyleChange = onBackgroundStyleChange
    )

    if (showAnimationSheet) {
        VideoPosterOptionSheet(
            title = "Animation Style",
            options = videoAnimationOptions(),
            selected = videoState.animationStyle,
            onDismiss = { showAnimationSheet = false },
            onSelected = {
                onVideoStateChange(videoState.copy(animationStyle = it))
                showAnimationSheet = false
            }
        )
    }
    if (showDurationSheet) {
        VideoPosterOptionSheet(
            title = "Video Duration",
            options = listOf("15 Seconds", "20 Seconds", "30 Seconds", "45 Seconds", "60 Seconds"),
            selected = videoState.duration,
            onDismiss = { showDurationSheet = false },
            onSelected = {
                onVideoStateChange(videoState.copy(duration = it))
                showDurationSheet = false
            }
        )
    }
}

@Composable
private fun AnimatedVideoPosterPreview(
    preset: Poster,
    form: CustomPosterState,
    videoState: VideoCustomizationState,
    colorStyle: VideoColorStyle,
    backgroundStyle: VideoBackgroundStyle,
    layoutIndex: Int,
    onPhotoAdjustmentChange: (PhotoAdjustment) -> Unit,
    modifier: Modifier = Modifier
) {
    var kenBurnsExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(videoState.animationStyle) {
        while (videoState.animationStyle == "Ken Burns" || videoState.animationStyle == "Pulse") {
            kenBurnsExpanded = !kenBurnsExpanded
            delay(2400)
        }
        kenBurnsExpanded = false
    }
    val kenBurnsScale by animateFloatAsState(
        targetValue = if (kenBurnsExpanded) 1.08f else 1f,
        animationSpec = tween(2200),
        label = "video_poster_ken_burns_scale"
    )

    AnimatedContent(
        targetState = videoState.animationStyle,
        transitionSpec = {
            when (targetState) {
                "Zoom", "Zoom In" -> (fadeIn(tween(450)) + scaleIn(initialScale = 0.82f)) togetherWith
                    (fadeOut(tween(300)) + scaleOut(targetScale = 1.12f))
                "Zoom Out" -> (fadeIn(tween(450)) + scaleIn(initialScale = 1.18f)) togetherWith
                    (fadeOut(tween(300)) + scaleOut(targetScale = 0.92f))
                "Slide", "Slide Left" -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                "Slide Right" -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                "Slide Up" -> slideInVertically { it } togetherWith slideOutVertically { -it }
                "Slide Down" -> slideInVertically { -it } togetherWith slideOutVertically { it }
                "Scale" -> scaleIn(tween(450), initialScale = 0.7f) togetherWith scaleOut(tween(300))
                "Pop", "Bounce" -> (fadeIn() + scaleIn(spring(), initialScale = 0.45f)) togetherWith
                    (fadeOut() + scaleOut(targetScale = 0.8f))
                "None" -> fadeIn(tween(1)) togetherWith fadeOut(tween(1))
                else -> fadeIn(tween(450)) togetherWith fadeOut(tween(300))
            }
        },
        label = "video_poster_preview_animation"
    ) { animationStyle ->
        LivePosterPreview(
            preset = preset,
            form = form,
            layoutIndex = layoutIndex,
            colorStyle = colorStyle,
            backgroundStyle = backgroundStyle,
            onPhotoAdjustmentChange = onPhotoAdjustmentChange,
            modifier = modifier.graphicsLayer(
                scaleX = when (animationStyle) {
                    "Ken Burns", "Pulse" -> kenBurnsScale
                    "Flip" -> 0.96f
                    else -> 1f
                },
                scaleY = if (animationStyle == "Ken Burns" || animationStyle == "Pulse") kenBurnsScale else 1f,
                rotationZ = if (animationStyle == "Rotate") 3.5f else 0f,
                rotationY = if (animationStyle == "Flip") 18f else 0f
            )
        )
    }
}

@Composable
private fun VideoPosterPlayerTimeline(
    videoState: VideoCustomizationState,
    duration: String
) {
    val context = LocalContext.current
    val durationSeconds = remember(duration) {
        duration.substringBefore(" ").toIntOrNull()?.coerceAtLeast(1) ?: 15
    }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var currentSecond by remember { mutableStateOf(0) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }

    fun releasePlayer() {
        player?.runCatching { stop() }
        player?.release()
        player = null
    }

    fun startMusicAt(second: Int) {
        releasePlayer()
        val newPlayer = runCatching {
            val resourceName = videoState.musicResourceName
            val phoneUri = videoState.musicPhoneUri
            when {
                resourceName != null -> {
                    val id = context.resources.getIdentifier(resourceName, "raw", context.packageName)
                    if (id == 0) null else MediaPlayer.create(context, id)
                }
                phoneUri != null -> MediaPlayer.create(context, phoneUri)
                else -> null
            }
        }.getOrNull()
        player = newPlayer
        newPlayer?.runCatching {
            val startMs = ((videoState.musicStartSecond + second) * 1000).coerceAtLeast(0)
            seekTo(startMs)
            start()
        }
    }

    LaunchedEffect(isPlaying, durationSeconds) {
        while (isPlaying) {
            delay(100L)
            progress = (progress + 0.1f / durationSeconds).coerceAtMost(1f)
            currentSecond = (progress * durationSeconds).toInt().coerceIn(0, durationSeconds)
            if (progress >= 1f) {
                isPlaying = false
                releasePlayer()
                currentSecond = durationSeconds
            }
        }
    }

    LaunchedEffect(videoState.musicResourceName, videoState.musicPhoneUri, videoState.musicStartSecond) {
        if (isPlaying) startMusicAt(currentSecond)
    }

    DisposableEffect(Unit) {
        onDispose { releasePlayer() }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IconButton(
            onClick = {
                if (isPlaying) {
                    isPlaying = false
                    player?.pause()
                } else {
                    if (progress >= 1f) {
                        progress = 0f
                        currentSecond = 0
                    }
                    isPlaying = true
                    startMusicAt(currentSecond)
                }
            },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = when {
                    isPlaying -> Icons.Default.Pause
                    progress >= 1f -> Icons.Default.Replay
                    else -> Icons.Default.PlayCircle
                },
                contentDescription = if (isPlaying) "Pause preview" else "Play preview",
                tint = appTextPrimary()
            )
        }
        Text(formatVideoSecond(currentSecond), color = appTextMuted(), style = MaterialTheme.typography.bodySmall)
        Slider(
            value = progress,
            onValueChange = {
                progress = it.coerceIn(0f, 1f)
                currentSecond = (progress * durationSeconds).toInt().coerceIn(0, durationSeconds)
                if (isPlaying) startMusicAt(currentSecond)
            },
            modifier = Modifier.weight(1f)
        )
        Text(formatVideoSecond(durationSeconds), color = appTextMuted(), style = MaterialTheme.typography.bodySmall)
    }
}

private fun formatVideoSecond(second: Int): String = "00:${second.coerceAtLeast(0).toString().padStart(2, '0')}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoPosterPreviewScreen(
    preset: Poster,
    form: CustomPosterState,
    videoState: VideoCustomizationState,
    colorStyle: VideoColorStyle,
    backgroundStyle: VideoBackgroundStyle,
    layoutIndex: Int,
    onBack: () -> Unit,
    onSaveImage: (Bitmap?) -> Unit,
    onGenerate: (Bitmap?) -> Unit
) {
    val previewCaptureLayer = rememberGraphicsLayer()
    val captureScope = rememberCoroutineScope()
    suspend fun captureCurrentPreview(): Bitmap? {
        return runCatching {
            previewCaptureLayer.toImageBitmap().asAndroidBitmap()
        }.getOrNull()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Video Preview",
                    color = appTextPrimary(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = appTextPrimary())
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = appBackgroundColor())
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val previewModifier = if (maxWidth * 16f / 9f <= maxHeight) {
                        Modifier.fillMaxWidth().aspectRatio(9f / 16f)
                    } else {
                        Modifier.height(maxHeight).aspectRatio(9f / 16f)
                    }
                    LivePosterPreview(
                        preset = preset,
                        form = form,
                        layoutIndex = layoutIndex,
                        colorStyle = colorStyle,
                        backgroundStyle = backgroundStyle,
                        onPhotoAdjustmentChange = {},
                        modifier = previewModifier.drawWithContent {
                            previewCaptureLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawContent()
                        }
                    )
                }
            }
            item { VideoPosterTimeline(videoState.duration) }
            item {
                VideoPosterDetailsCard(
                    category = preset.category,
                    template = preset.title,
                    duration = videoState.duration,
                    animation = videoState.animationStyle,
                    music = videoState.musicSelection
                )
            }
            item {
                VideoPosterActionRow(
                    onSave = { captureScope.launch { onSaveImage(captureCurrentPreview()) } },
                    onGenerate = { captureScope.launch { onGenerate(captureCurrentPreview()) } }
                )
            }
        }
    }
}

@Composable
private fun VideoPosterTimeline(duration: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = appTextPrimary())
        Text("00:00", color = appTextMuted(), style = MaterialTheme.typography.bodySmall)
        Slider(
            value = 0.58f,
            onValueChange = {},
            enabled = false,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "00:${duration.substringBefore(" ").padStart(2, '0')}",
            color = appTextMuted(),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun VideoPosterToolButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(74.dp)
            .height(68.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = appTextPrimary())
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = label,
                color = appTextPrimary(),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun VideoPosterSettingsCard(
    videoState: VideoCustomizationState,
    onAnimationClick: () -> Unit,
    onDurationClick: () -> Unit,
    onMusicClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Video Settings",
                color = appTextPrimary(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            VideoPosterSettingRow(Icons.Default.AutoAwesome, "Animation Style", videoState.animationStyle, onAnimationClick)
            HorizontalDivider(color = appBorderColor(0.08f))
            VideoPosterSettingRow(Icons.Default.Timer, "Duration", videoState.duration, onDurationClick)
            HorizontalDivider(color = appBorderColor(0.08f))
            VideoPosterSettingRow(Icons.Default.MusicNote, "Background Music", videoState.musicSelection, onMusicClick)
        }
    }
}

@Composable
private fun VideoPosterSettingRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFBFA7FF))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = appTextPrimary(), fontWeight = FontWeight.SemiBold)
            Text(text = value, color = appTextMuted(), style = MaterialTheme.typography.bodySmall)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = appTextMuted())
    }
}

@Composable
private fun VideoPosterActionRow(
    onSave: () -> Unit,
    onGenerate: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onSave,
            modifier = Modifier
                .weight(1f)
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = appGlassSurfaceStrong()),
            border = BorderStroke(1.dp, Color(0xFFEC4899))
        ) {
            Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFFEC4899))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save", color = appTextPrimary(), maxLines = 1)
        }
        Button(
            onClick = onGenerate,
            modifier = Modifier
                .weight(1.55f)
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))
        ) {
            Text(
                text = "Generate Video (MP4)",
                color = Color.White,
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoPosterEditSheet(
    activeSheet: String?,
    preset: Poster,
    form: CustomPosterState,
    colorStyle: VideoColorStyle,
    backgroundStyle: VideoBackgroundStyle,
    onDismiss: () -> Unit,
    onFormChange: (CustomPosterState) -> Unit,
    onColorStyleChange: (VideoColorStyle) -> Unit,
    onBackgroundStyleChange: (VideoBackgroundStyle) -> Unit
) {
    if (activeSheet == null) return
    val bgPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            onBackgroundStyleChange(
                backgroundStyle.copy(
                    mode = "Gallery Image",
                    galleryImageUri = uri,
                    colors = null
                )
            )
        }
    }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = activeSheet,
                color = appTextPrimary(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
            when (activeSheet) {
                "Photo" -> {
                    ProfilePhotoUploader(form.profilePhotoUri) { onFormChange(form.copy(profilePhotoUri = it)) }
                    PhotoAdjustmentControls(
                        adjustment = PhotoAdjustment(form.photoScale, form.photoOffsetX, form.photoOffsetY),
                        onAdjustmentChange = {
                            onFormChange(form.copy(photoScale = it.scale, photoOffsetX = it.offsetX, photoOffsetY = it.offsetY))
                        }
                    )
                    PosterTextField(form.personName, { onFormChange(form.copy(personName = it)) }, "Name", Icons.Default.Person)
                }
                "Text" -> {
                    val wishOptions = remember(preset.category) { wishMessagesForCategory(preset.category) }
                    WishMessageSelector(
                        category = preset.category,
                        options = wishOptions,
                        selectedMessage = form.wishesMessage.ifBlank { wishOptions.first() },
                        onSelectedMessageChange = { onFormChange(form.copy(wishesMessage = it)) }
                    )
                    PosterTextField(
                        value = form.wishesMessage,
                        onValueChange = { onFormChange(form.copy(wishesMessage = it)) },
                        label = "Custom Message (Optional)",
                        leadingIcon = Icons.Default.TextFields,
                        minLines = 3
                    )
                }
                "Background" -> VideoBackgroundEditor(
                    backgroundStyle = backgroundStyle,
                    onPickGallery = { bgPicker.launch("image/*") },
                    onBackgroundStyleChange = onBackgroundStyleChange
                )
                "Colors" -> VideoColorEditor(
                    colorStyle = colorStyle,
                    onColorStyleChange = onColorStyleChange
                )
            }
        }
    }
}

@Composable
private fun VideoBackgroundEditor(
    backgroundStyle: VideoBackgroundStyle,
    onPickGallery: () -> Unit,
    onBackgroundStyleChange: (VideoBackgroundStyle) -> Unit
) {
    val options = listOf(
        "Template" to null,
        "Solid Color" to listOf(Color(0xFF050505), Color(0xFF050505)),
        "Gradient" to listOf(Color(0xFF111827), Color(0xFF7C3AED), Color(0xFFEC4899)),
        "Black Gold" to listOf(Color(0xFF020202), Color(0xFF1C1405), Color(0xFFD4AF37)),
        "Royal Blue" to listOf(Color(0xFF06172F), Color(0xFF1D4ED8), Color(0xFF38BDF8)),
        "Premium Purple" to listOf(Color(0xFF160B2E), Color(0xFF6D28D9), Color(0xFFDB2777))
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VideoSheetOptionButton("Gallery", Icons.Default.Image, onPickGallery)
            VideoSheetOptionButton("Premium", Icons.Default.Wallpaper) {
                onBackgroundStyleChange(VideoBackgroundStyle(mode = "Template"))
            }
        }
        options.forEach { (name, colors) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        onBackgroundStyleChange(
                            VideoBackgroundStyle(
                                mode = name,
                                galleryImageUri = null,
                                colors = colors
                            )
                        )
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(colors ?: listOf(Color(0xFF111827), Color(0xFF020617))))
                        .border(1.dp, appBorderColor(0.12f), RoundedCornerShape(12.dp))
                )
                Text(name, color = appTextPrimary(), modifier = Modifier.weight(1f))
                if (backgroundStyle.mode == name) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFEC4899))
                }
            }
        }
    }
}

@Composable
private fun VideoColorEditor(
    colorStyle: VideoColorStyle,
    onColorStyleChange: (VideoColorStyle) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Premium Presets", color = appTextMuted(), style = MaterialTheme.typography.labelLarge)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(videoColorPresets()) { preset ->
                Column(
                    modifier = Modifier
                        .width(86.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onColorStyleChange(preset.style) }
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(preset.style.accentColor, preset.style.borderColor)))
                            .border(1.dp, appBorderColor(0.12f), CircleShape)
                    )
                    Text(
                        preset.name,
                        color = appTextPrimary(),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Text("Fine Tune", color = appTextMuted(), style = MaterialTheme.typography.labelLarge)
        VideoColorSwatchRow("Title Color", colorStyle.titleColor) { onColorStyleChange(colorStyle.copy(titleColor = it)) }
        VideoColorSwatchRow("Name Color", colorStyle.nameColor) { onColorStyleChange(colorStyle.copy(nameColor = it)) }
        VideoColorSwatchRow("Message Color", colorStyle.messageColor) { onColorStyleChange(colorStyle.copy(messageColor = it)) }
        VideoColorSwatchRow("Footer Color", colorStyle.footerColor) { onColorStyleChange(colorStyle.copy(footerColor = it)) }
        VideoColorSwatchRow("Border Color", colorStyle.borderColor) { onColorStyleChange(colorStyle.copy(borderColor = it)) }
        VideoColorSwatchRow("Accent Color", colorStyle.accentColor) { onColorStyleChange(colorStyle.copy(accentColor = it)) }
    }
}

@Composable
private fun VideoColorSwatchRow(
    label: String,
    selected: Color,
    onSelected: (Color) -> Unit
) {
    val swatches = listOf(
        Color.White, Color(0xFFFFC83D), Color(0xFFD4AF37), Color(0xFFFF8A00),
        Color(0xFFEF4444), Color(0xFFEC4899), Color(0xFFA855F7), Color(0xFF3B82F6),
        Color(0xFF0EA5E9), Color(0xFF14B8A6), Color(0xFF22C55E), Color(0xFFE5E7EB),
        Color(0xFF111827), Color(0xFF94A3B8)
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = appTextPrimary(), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(swatches) { color ->
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (color == selected) 3.dp else 1.dp,
                            color = if (color == selected) Color(0xFFEC4899) else appBorderColor(0.16f),
                            shape = CircleShape
                        )
                        .clickable { onSelected(color) }
                )
            }
        }
    }
}

@Composable
private fun VideoSheetOptionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = appGlassSurfaceStrong())
    ) {
        Icon(icon, contentDescription = null, tint = appTextPrimary())
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, color = appTextPrimary())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoPosterOptionSheet(
    title: String,
    options: List<String>,
    selected: String,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp)
        ) {
            Text(
                text = title,
                color = appTextPrimary(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(modifier = Modifier.height(12.dp))
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelected(option) }
                        .padding(horizontal = 12.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = option, color = appTextPrimary(), modifier = Modifier.weight(1f))
                    if (selected == option) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = Color(0xFFEC4899))
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoPosterDetailsCard(
    category: String,
    template: String,
    duration: String,
    animation: String,
    music: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.08f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Video Details", color = appTextPrimary(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))
            VideoPosterDetailRow("Category", category)
            VideoPosterDetailRow("Template", template)
            VideoPosterDetailRow("Duration", duration)
            VideoPosterDetailRow("Animation", animation)
            VideoPosterDetailRow("Music", music)
        }
    }
}

@Composable
private fun VideoPosterDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = appTextMuted(), style = MaterialTheme.typography.bodySmall)
        Text(
            value,
            color = appTextPrimary(),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TemplatesTab(
    presets: List<Poster>,
    viewModel: PosterViewModel,
    profileSettings: ProfileSettings,
    initialCategory: String?,
    initialPresetId: Int? = null,
    initialWishMessage: String? = null,
    onBackToDashboard: () -> Unit
) {
    val navController = rememberNavController()
    val startRoute = initialPresetId
        ?.takeIf { presetId -> presets.any { it.id == presetId } }
        ?.let { "template_customize/$it" }
        ?: initialCategory
            ?.takeIf { category -> dashboardCategories.any { it.name == category } }
            ?.let { "template_category/$it" }
        ?: "template_categories"

    fun navigateBackInTemplates() {
        val route = navController.currentBackStackEntry?.destination?.route
        if (route == "template_categories" || navController.previousBackStackEntry == null) {
            onBackToDashboard()
        } else {
            navController.navigateBackOr(onBackToDashboard)
        }
    }

    BackHandler(enabled = true, onBack = ::navigateBackInTemplates)

    NavHost(
        navController = navController,
        startDestination = startRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        composable("template_categories") {
            TemplateCategoriesScreen(
                presets = presets,
                onOpenCategory = { category ->
                    navController.navigate("template_category/$category")
                }
            )
        }
        composable(
            route = "template_category/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { entry ->
            val category = entry.arguments?.getString("category").orEmpty()
            TemplateGridScreen(
                category = category,
                presets = presets.filter { it.category == category },
                onBack = ::navigateBackInTemplates,
                onSelectPreset = { preset ->
                    navController.navigate("template_customize/${preset.id}")
                }
            )
        }
        composable(
            route = "template_customize/{presetId}",
            arguments = listOf(navArgument("presetId") { type = NavType.IntType })
        ) { entry ->
            val presetId = entry.arguments?.getInt("presetId") ?: 0
            val preset = presets.firstOrNull { it.id == presetId }
            if (preset != null) {
                PosterCustomizationScreen(
                    preset = preset,
                    categoryPresets = presets.filter { it.category == preset.category },
                    viewModel = viewModel,
                    profileSettings = profileSettings,
                    initialWishMessage = initialWishMessage?.takeIf { preset.id == initialPresetId },
                    onBack = ::navigateBackInTemplates
                )
            } else {
                TemplateCategoriesScreen(
                    presets = presets,
                    onOpenCategory = { category -> navController.navigate("template_category/$category") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PosterCustomizationScreen(
    preset: Poster,
    categoryPresets: List<Poster>,
    viewModel: PosterViewModel,
    profileSettings: ProfileSettings,
    initialWishMessage: String? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    if (preset.category.equals("Welcome", ignoreCase = true)) {
        WelcomePosterEditor(
            preset = preset,
            profile = profileSettings,
            onBack = onBack,
            onSave = { project, bitmap ->
                coroutineScope.launch {
                    runCatching {
                        saveWelcomePosterProject(context, viewModel, preset, project, bitmap)
                    }.onFailure { viewModel.showStatusMessage("Unable to save Welcome poster") }
                }
            },
            onShare = { bitmap -> sharePosterBitmap(context, bitmap, preset.title) },
            onCreateVideo = { bitmap ->
                coroutineScope.launch {
                    viewModel.showStatusMessage("Creating video from your customized poster…")
                    runCatching {
                        exportPosterBitmapVideoToGallery(
                            context = context,
                            posterBitmap = bitmap,
                            state = VideoCustomizationState(title = preset.title, animationStyle = "Fade", duration = "15 Seconds"),
                            title = preset.title,
                            onProgress = {}
                        )
                    }.onSuccess { uri ->
                        viewModel.showStatusMessage(if (uri != null) "Customized poster video saved to Gallery" else "Unable to create video")
                    }.onFailure { viewModel.showStatusMessage("Unable to create video") }
                }
            }
        )
        return
    }
    val previewCaptureLayer = rememberGraphicsLayer()
    val wishOptions = remember(preset.category) { wishMessagesForCategory(preset.category) }
    val wishPrefs = remember(context) {
        context.getSharedPreferences("poster_wish_message_preferences", Context.MODE_PRIVATE)
    }
    var selectedWishMessage by remember(preset.category) {
        val saved = wishPrefs.getString("selected_${preset.category}", null)
        mutableStateOf(initialWishMessage ?: saved?.takeIf { it in wishOptions } ?: wishOptions.first())
    }
    var isSavingPoster by remember { mutableStateOf(false) }
    var isSharingPoster by remember { mutableStateOf(false) }
    var form by remember(preset.id) {
        mutableStateOf(
            CustomPosterState(
                companyLogoUri = profileSettings.companyLogoUri.takeIf { it.isNotBlank() }?.let(Uri::parse),
                wishesMessage = selectedWishMessage,
                personName = defaultNameFor(preset.category),
                companyName = profileSettings.companyName.ifBlank { "ABC Marketing Pvt Ltd" },
                websiteName = profileSettings.websiteName.ifBlank { "www.abcmarketing.com" },
                mobileNumber = profileSettings.mobileNumber
            )
        )
    }
    val layoutIndex = (categoryPresets.indexOfFirst { it.id == preset.id }.takeIf { it >= 0 } ?: 0) + 1
    val posterScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val formScrollState = rememberScrollState()
    val headerExpanded by remember {
        derivedStateOf {
            formScrollState.value < 24 && posterScrollBehavior.state.collapsedFraction < 0.08f
        }
    }

    LaunchedEffect(
        profileSettings.companyLogoUri,
        profileSettings.companyName,
        profileSettings.websiteName,
        profileSettings.mobileNumber
    ) {
        form = form.copy(
            companyLogoUri = profileSettings.companyLogoUri.takeIf { it.isNotBlank() }?.let(Uri::parse),
            companyName = profileSettings.companyName.ifBlank { "ABC Marketing Pvt Ltd" },
            websiteName = profileSettings.websiteName.ifBlank { "www.abcmarketing.com" },
            mobileNumber = profileSettings.mobileNumber
        )
    }

    fun savePoster() {
        if (isSavingPoster) return
        coroutineScope.launch {
            isSavingPoster = true
            try {
                runCatching {
                    previewCaptureLayer.toImageBitmap().asAndroidBitmap()
                }.onSuccess { bitmap ->
                    savePosterBitmap(
                        context = context,
                        viewModel = viewModel,
                        poster = preset,
                        form = form,
                        bitmap = bitmap
                    )
                }.onFailure {
                    viewModel.showStatusMessage("Unable to save poster")
                }
            } finally {
                isSavingPoster = false
            }
        }
    }

    fun sharePoster() {
        if (isSharingPoster) return
        coroutineScope.launch {
            isSharingPoster = true
            try {
                runCatching {
                    previewCaptureLayer.toImageBitmap().asAndroidBitmap()
                }.onSuccess { bitmap ->
                    sharePosterBitmap(
                        context = context,
                        bitmap = bitmap,
                        title = preset.title
                    )
                }.onFailure {
                    viewModel.showStatusMessage("Unable to share poster")
                }
            } finally {
                isSharingPoster = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
            .nestedScroll(posterScrollBehavior.nestedScrollConnection)
    ) {
        TopAppBar(
            modifier = Modifier.statusBarsPadding(),
            title = {
                AnimatedVisibility(
                    visible = headerExpanded,
                    enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { -it / 3 },
                    exit = fadeOut(tween(250)) + slideOutVertically(tween(250)) { -it / 3 }
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Text(
                            text = preset.title,
                            color = appTextPrimary(),
                            fontSize = 16.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${preset.category} Template",
                            color = appTextMuted(),
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = appTextPrimary()
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { sharePoster() },
                    enabled = !isSharingPoster
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share poster",
                        tint = if (isSharingPoster) appTextMuted() else Color(0xFF6D5DF6)
                    )
                }
                IconButton(
                    onClick = { savePoster() },
                    enabled = !isSavingPoster
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download poster",
                        tint = if (isSavingPoster) appTextMuted() else Color(0xFF6D5DF6)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = appBackgroundColor(),
                scrolledContainerColor = appBackgroundColor()
            ),
            windowInsets = WindowInsets(0.dp),
            expandedHeight = TemplateToolbarHeight,
            scrollBehavior = posterScrollBehavior
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(formScrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                val previewModifier = if (maxWidth * 16f / 9f <= maxHeight) {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(9f / 16f)
                } else {
                    Modifier
                        .height(maxHeight)
                        .aspectRatio(9f / 16f)
                }
                Box(modifier = previewModifier) {
                    LivePosterPreview(
                        preset = preset,
                        form = form,
                        layoutIndex = layoutIndex,
                        onPhotoAdjustmentChange = { adjustment ->
                            form = form.copy(
                                photoScale = adjustment.scale,
                                photoOffsetX = adjustment.offsetX,
                                photoOffsetY = adjustment.offsetY
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                previewCaptureLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawContent()
                            }
                    )
                }
            }
            SectionTitle("Customization Form")
            FormSectionTitle("Person Section")
            ProfilePhotoUploader(
                imageUri = form.profilePhotoUri,
                onImageSelected = {
                    form = form.copy(
                        profilePhotoUri = it,
                        photoScale = 1f,
                        photoOffsetX = 0f,
                        photoOffsetY = 0f
                    )
                }
            )
            if (form.profilePhotoUri != null) {
                PhotoAdjustmentControls(
                    adjustment = PhotoAdjustment(
                        scale = form.photoScale,
                        offsetX = form.photoOffsetX,
                        offsetY = form.photoOffsetY
                    ),
                    onAdjustmentChange = { adjustment ->
                        form = form.copy(
                            photoScale = adjustment.scale,
                            photoOffsetX = adjustment.offsetX,
                            photoOffsetY = adjustment.offsetY
                        )
                    }
                )
            }
            PosterTextField(
                value = form.personName,
                onValueChange = { form = form.copy(personName = it) },
                label = "Person Name",
                leadingIcon = Icons.Default.Person
            )
            WishMessageSelector(
                category = preset.category,
                options = wishOptions,
                selectedMessage = selectedWishMessage,
                onSelectedMessageChange = { message ->
                    selectedWishMessage = message
                    wishPrefs.edit().putString("selected_${preset.category}", message).apply()
                    form = form.copy(wishesMessage = message)
                }
            )
            if (isSignatureTemplate(preset.id)) {
                PosterTextField(
                    value = form.wishesMessage,
                    onValueChange = {
                        form = form.copy(wishesMessage = it)
                        selectedWishMessage = it
                    },
                    label = "Your own message",
                    leadingIcon = Icons.Default.TextFields,
                    minLines = 3
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun TemplateCategoriesScreen(
    presets: List<Poster>,
    onOpenCategory: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val categoryOrder = listOf("Birthday", "Festival", "Motivation", "Achievement", "Welcome", "Income")
    val filteredCategories = categoryOrder.filter {
        searchQuery.isBlank() || it.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Templates",
                color = appTextPrimary(),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
        }
        item {
            DashboardSearchField(searchQuery, { searchQuery = it }, "Search Templates...")
        }
        items(filteredCategories) { category ->
            TemplateCategoryCard(
                category = category,
                templateCount = presets.count { it.category == category },
                onClick = { onOpenCategory(category) }
            )
        }
    }
}

@Composable
private fun TemplateCategoryCard(
    category: String,
    templateCount: Int,
    onClick: () -> Unit
) {
    val spec = categorySpecFor(category)
    val emoji = when (category) {
        "Birthday" -> "🎉"
        "Achievement" -> "🏆"
        "Welcome" -> "🤝"
        "Income" -> "💰"
        else -> "✨"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(spec.colors))
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.13f))
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = spec.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = category,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "$templateCount Templates Available",
                        color = Color.White.copy(alpha = 0.86f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun TemplateGridScreen(
    category: String,
    presets: List<Poster>,
    onBack: () -> Unit,
    onSelectPreset: (Poster) -> Unit
) {
    val gridState = rememberLazyGridState()
    val headerVisible by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset < 12
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        AnimatedVisibility(
            visible = headerVisible,
            enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { -it },
            exit = fadeOut(tween(250)) + slideOutVertically(tween(250)) { -it }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = appTextPrimary()
                    )
                }
                Column {
                    Text(
                        text = "$category Templates",
                        color = appTextPrimary(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "${presets.size} Templates Available",
                        color = appTextMuted(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = headerVisible,
            enter = fadeIn(tween(280)),
            exit = fadeOut(tween(250))
        ) {
            Spacer(modifier = Modifier.height(18.dp))
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            itemsIndexed(presets) { index, preset ->
                TemplateCard(
                    preset = preset,
                    layoutIndex = index + 1,
                    onClick = { onSelectPreset(preset) }
                )
            }
        }
    }
}

@Composable
private fun TemplateCard(
    preset: Poster,
    layoutIndex: Int,
    onClick: () -> Unit
) {
    val sampleForm = remember(preset.id) {
        defaultPosterStateFor(preset.category)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(TemplateCardHeight)
            .shadow(8.dp, RoundedCornerShape(TemplateCardCornerRadius))
            .clickable { onClick() },
        shape = RoundedCornerShape(TemplateCardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(TemplateCardPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(TemplatePreviewHeight),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .requiredHeight(TemplatePreviewHeight)
                        .aspectRatio(if (preset.category == "Welcome") 4f / 5f else 9f / 16f)
                        .clip(RoundedCornerShape(TemplatePreviewCornerRadius))
                ) {
                    LivePosterPreview(
                        preset = preset,
                        form = sampleForm,
                        layoutIndex = layoutIndex,
                        onPhotoAdjustmentChange = {},
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = preset.title,
                color = appTextPrimary(),
                modifier = Modifier.requiredHeight(TemplateTitleHeight),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Tap to customize",
                color = Color(0xFFBFA7FF),
                modifier = Modifier.requiredHeight(TemplateActionHeight),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
internal fun CompanyLogoUploader(
    imageUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    ImageUploadCard(
        title = "Company Logo Upload",
        subtitle = "Select JPG or PNG from gallery",
        imageUri = imageUri,
        icon = Icons.Default.Business,
        onImageSelected = onImageSelected
    )
}

@Composable
internal fun ProfilePhotoUploader(
    imageUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    ImageUploadCard(
        title = "Profile Photo Upload",
        subtitle = "Select profile image from gallery",
        imageUri = imageUri,
        icon = Icons.Default.PhotoCamera,
        onImageSelected = onImageSelected
    )
}

@Composable
private fun PhotoAdjustmentControls(
    adjustment: PhotoAdjustment,
    onAdjustmentChange: (PhotoAdjustment) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.08f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Adjust Profile Photo",
                    color = appTextPrimary(),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse profile photo controls" else "Expand profile photo controls",
                    tint = appTextMuted()
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(240)) + fadeIn(tween(180)),
                exit = shrinkVertically(animationSpec = tween(220)) + fadeOut(tween(160))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PhotoControlButton(
                            icon = Icons.Default.ArrowLeft,
                            description = "Move photo left"
                        ) {
                            onAdjustmentChange(adjustment.copy(offsetX = adjustment.offsetX - 16f))
                        }
                        PhotoControlButton(
                            icon = Icons.Default.ArrowUpward,
                            description = "Move photo up"
                        ) {
                            onAdjustmentChange(adjustment.copy(offsetY = adjustment.offsetY - 16f))
                        }
                        PhotoControlButton(
                            icon = Icons.Default.ArrowDownward,
                            description = "Move photo down"
                        ) {
                            onAdjustmentChange(adjustment.copy(offsetY = adjustment.offsetY + 16f))
                        }
                        PhotoControlButton(
                            icon = Icons.Default.ArrowRight,
                            description = "Move photo right"
                        ) {
                            onAdjustmentChange(adjustment.copy(offsetX = adjustment.offsetX + 16f))
                        }
                    }
                    HorizontalDivider(color = appBorderColor(0.08f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PhotoControlButton(
                            icon = Icons.Default.ZoomOut,
                            description = "Zoom photo out"
                        ) {
                            onAdjustmentChange(adjustment.copy(scale = (adjustment.scale - 0.1f).coerceIn(0.7f, 4f)))
                        }
                        Text(
                            text = "${(adjustment.scale * 100).toInt()}%",
                            color = appTextPrimary(),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        PhotoControlButton(
                            icon = Icons.Default.ZoomIn,
                            description = "Zoom photo in"
                        ) {
                            onAdjustmentChange(adjustment.copy(scale = (adjustment.scale + 0.1f).coerceIn(0.7f, 4f)))
                        }
                        PhotoControlButton(
                            icon = Icons.Default.RestartAlt,
                            description = "Reset photo"
                        ) {
                            onAdjustmentChange(PhotoAdjustment())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoControlButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF6D5DF6).copy(alpha = 0.16f))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = Color(0xFFBFA7FF)
        )
    }
}

@Composable
private fun ImageUploadCard(
    title: String,
    subtitle: String,
    imageUri: Uri?,
    icon: ImageVector,
    onImageSelected: (Uri?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onImageSelected(uri)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.08f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFFBFA7FF),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    color = appTextPrimary(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = subtitle,
                    color = appTextMuted(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(
                onClick = { launcher.launch("image/*") },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D5DF6))
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
internal fun PosterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Color(0xFFBFA7FF)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        shape = RoundedCornerShape(18.dp),
        colors = darkFieldColors()
    )
}

@Composable
private fun FooterSection(
    companyName: String,
    websiteName: String,
    mobileNumber: String,
    onCompanyNameChange: (String) -> Unit,
    onWebsiteNameChange: (String) -> Unit,
    onMobileNumberChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PosterTextField(
            value = companyName,
            onValueChange = onCompanyNameChange,
            label = "Company Name",
            leadingIcon = Icons.Default.Business
        )
        PosterTextField(
            value = websiteName,
            onValueChange = onWebsiteNameChange,
            label = "Website Name",
            leadingIcon = Icons.Default.Language
        )
        PosterTextField(
            value = mobileNumber,
            onValueChange = onMobileNumberChange,
            label = "Mobile Number (Optional)",
            leadingIcon = Icons.Default.Phone
        )
    }
}

@Composable
private fun WishMessageSelector(
    category: String,
    options: List<String>,
    selectedMessage: String,
    onSelectedMessageChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormSectionTitle("Wish Message")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
            border = BorderStroke(1.dp, appBorderColor(0.12f))
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFBFA7FF))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$category Message",
                            color = appTextMuted(),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedMessage,
                            color = appTextPrimary(),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = appTextMuted(),
                        modifier = Modifier.graphicsLayer(rotationZ = if (expanded) 90f else 0f)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.86f)
                ) {
                    options.forEachIndexed { index, message ->
                        Text(
                            text = "${index + 1}. $message",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expanded = false
                                    onSelectedMessageChange(message)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            color = appTextPrimary(),
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormSectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFFBFA7FF),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
    )
}

private fun CustomPosterState.readyDocument(template: PosterTemplate) = GeneratedPoster(
    template = template,
    values = mapOf("NAME" to personName, "MESSAGE" to wishesMessage),
    photo = profilePhotoUri?.toString().orEmpty(),
    branding = BusinessBranding(logo = companyLogoUri?.toString().orEmpty(), company = companyName, phone = mobileNumber, website = websiteName)
)

@Composable
private fun LivePosterPreview(
    preset: Poster,
    form: CustomPosterState,
    layoutIndex: Int,
    colorStyle: VideoColorStyle = defaultVideoColorStyle,
    backgroundStyle: VideoBackgroundStyle = VideoBackgroundStyle(),
    onPhotoAdjustmentChange: (PhotoAdjustment) -> Unit,
    modifier: Modifier = Modifier
) {
    TemplateJson.template(preset.backgroundImageRes)?.let { template ->
        ReadyPosterPreview(form.readyDocument(template), modifier)
        return
    }
    if (preset.category.equals("Welcome", ignoreCase = true)) {
        WelcomePosterPreview(state = form.welcomeProject(preset.id), modifier = modifier)
        return
    }
    if (isSignatureTemplate(preset.id)) {
        SignaturePosterArtwork(
            id = preset.id,
            content = form.signatureContent(),
            onPhotoAdjustment = { scale, x, y -> onPhotoAdjustmentChange(PhotoAdjustment(scale, x, y)) },
            modifier = modifier
        )
        return
    }
    val spec = categorySpecFor(preset.category)
    val posterColors = when (preset.id) {
        -21 ->
            listOf(Color(0xFF020202), Color(0xFF120D03), Color(0xFF030303))
        -22 ->
            listOf(Color(0xFF8B3FD1), Color(0xFF50208E), Color(0xFF241044))
        -9 ->
            listOf(Color(0xFF020202), Color(0xFF151005), Color(0xFF030303))
        -10 ->
            listOf(Color(0xFFF32D76), Color(0xFFC12688), Color(0xFF6020A4))
        -12 ->
            listOf(Color(0xFF090909), Color(0xFF171109), Color(0xFF050505))
        else -> categoryTemplateColors(preset.category, layoutIndex)
    }
    val backgroundColors = backgroundStyle.colors ?: colorStyle.backgroundColors ?: posterColors
    CompositionLocalProvider(
        LocalPhotoAdjustment provides PhotoAdjustment(
            scale = form.photoScale,
            offsetX = form.photoOffsetX,
            offsetY = form.photoOffsetY
        ),
        LocalPhotoAdjustmentChange provides onPhotoAdjustmentChange,
        LocalTemplateTitle provides preset.title,
        LocalVideoColorStyle provides colorStyle,
        LocalSamplePortraitRes provides if (preset.id % 2 == 0) {
            R.drawable.sample_business_woman
        } else {
            R.drawable.sample_business_man
        }
    ) {
        Card(
            modifier = modifier
                .aspectRatio(9f / 16f)
                .shadow(18.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val baseWidth = 360.dp
                val baseHeight = 640.dp
                val scale = minOf(maxWidth / baseWidth, maxHeight / baseHeight)
                Box(
                    modifier = Modifier
                        .requiredSize(baseWidth, baseHeight)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .padding(18.dp)
                ) {
                    if (backgroundStyle.galleryImageUri != null) {
                        AsyncImage(
                            model = backgroundStyle.galleryImageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(22.dp))
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(22.dp))
                                .background(Color.Black.copy(alpha = 0.28f))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Brush.linearGradient(backgroundColors))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 42.dp, y = (-42).dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                    )
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .align(Alignment.BottomStart)
                            .offset(x = (-40).dp, y = 34.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.14f))
                    )
                    CategoryPosterDecorations(
                        category = preset.category,
                        layoutIndex = layoutIndex
                    )
                    when (preset.id) {
                        -21 -> RoyalHeroBirthdayLayout(form)
                        -22 -> FullPortraitStoryBirthdayLayout(form)
                        -9 -> LuxuryBlackGoldBirthdayLayout(form)
                        -10 -> ElegantPhotoBirthdayLayout(form)
                        -11 -> CelebrationBirthdayLayout(form)
                        -12 -> PremiumGoldBirthdayLayout(form)
                        else -> when (layoutIndex) {
                            2 -> PreviewLayoutTwo(preset.category, form)
                            3 -> PreviewLayoutThree(preset.category, form)
                            4 -> PreviewLayoutFour(preset.category, form)
                            5 -> PreviewLayoutFive(preset.category, form)
                            6 -> PreviewLayoutSix(preset.category, form)
                            else -> PreviewLayoutOne(preset.category, form)
                        }
                    }
                }
            }
        }
    }
}

private fun categoryTemplateColors(category: String, layoutIndex: Int): List<Color> {
    val variants = when (category) {
        "Achievement" -> listOf(
            listOf(Color(0xFF06172F), Color(0xFF123663), Color(0xFFB47A00)),
            listOf(Color(0xFF0A2144), Color(0xFF1B4B80), Color(0xFFE6A817)),
            listOf(Color(0xFF121212), Color(0xFF233B62), Color(0xFF9D6B00)),
            listOf(Color(0xFF081A36), Color(0xFF334E7D), Color(0xFFC78A0B)),
            listOf(Color(0xFF17120A), Color(0xFF4A3514), Color(0xFFD69B1A)),
            listOf(Color(0xFF050B18), Color(0xFF172B50), Color(0xFF8B6410))
        )
        "Welcome" -> listOf(
            listOf(Color(0xFF087F7B), Color(0xFF19A89E), Color(0xFF2D73A5)),
            listOf(Color(0xFF075F71), Color(0xFF149AA2), Color(0xFF4BBF9B)),
            listOf(Color(0xFF0A6B78), Color(0xFF228EAE), Color(0xFF77C7B2)),
            listOf(Color(0xFF08765F), Color(0xFF1AA58A), Color(0xFF3D89B6)),
            listOf(Color(0xFF155E75), Color(0xFF0D9488), Color(0xFF6BC7A2)),
            listOf(Color(0xFF0F6170), Color(0xFF147D9D), Color(0xFF49B891))
        )
        "Income" -> listOf(
            listOf(Color(0xFF32115D), Color(0xFF68227A), Color(0xFFC36A18)),
            listOf(Color(0xFF241052), Color(0xFF502183), Color(0xFFE28724)),
            listOf(Color(0xFF42115F), Color(0xFF8B2868), Color(0xFFD39119)),
            listOf(Color(0xFF171450), Color(0xFF49307D), Color(0xFFBA7511)),
            listOf(Color(0xFF28105A), Color(0xFF6D2387), Color(0xFFEF8A27)),
            listOf(Color(0xFF15113D), Color(0xFF3C246C), Color(0xFFBF7D13))
        )
        else -> listOf(categorySpecFor(category).colors)
    }
    return variants[(layoutIndex - 1).coerceAtLeast(0) % variants.size]
}

@Composable
private fun PreviewLayoutOne(category: String, form: CustomPosterState) {
    if (category == "Birthday") {
        LuxuryBlackGoldBirthdayLayout(form)
        return
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(12.dp))
        PreviewHeadline(category)
        Spacer(modifier = Modifier.height(16.dp))
        PreviewHeroPhoto(form.profilePhotoUri, Modifier.size(202.dp), CircleShape)
        Spacer(modifier = Modifier.height(12.dp))
        PreviewName(form.personName)
        Spacer(modifier = Modifier.height(4.dp))
        PreviewWishes(
            message = form.wishesMessage,
            modifier = Modifier.height(76.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        PreviewFooter(form)
    }
}

@Composable
private fun BoxScope.CategoryPosterDecorations(
    category: String,
    layoutIndex: Int
) {
    if (category == "Birthday") return
    val accent = when (category) {
        "Achievement" -> Color(0xFFFFC83D)
        "Welcome" -> Color(0xFF8EF0D0)
        "Income" -> Color(0xFFFFC857)
        else -> Color.White
    }
    val icon = when (category) {
        "Achievement" -> when (layoutIndex % 3) {
            0 -> Icons.Default.WorkspacePremium
            1 -> Icons.Default.EmojiEvents
            else -> Icons.Default.AutoAwesome
        }
        "Welcome" -> when (layoutIndex % 3) {
            0 -> Icons.Default.Groups
            1 -> Icons.Default.AccountCircle
            else -> Icons.Default.AutoAwesome
        }
        "Income" -> when (layoutIndex % 3) {
            0 -> Icons.AutoMirrored.Filled.TrendingUp
            1 -> Icons.Default.WorkspacePremium
            else -> Icons.Default.AutoAwesome
        }
        else -> Icons.Default.AutoAwesome
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = accent.copy(alpha = 0.22f),
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .offset(x = 18.dp, y = if (layoutIndex % 2 == 0) 84.dp else (-54).dp)
            .size(if (layoutIndex >= 5) 128.dp else 92.dp)
    )
    repeat(5) { index ->
        Box(
            modifier = Modifier
                .align(if (index % 2 == 0) Alignment.TopEnd else Alignment.BottomStart)
                .offset(
                    x = if (index % 2 == 0) (-10 - index * 9).dp else (8 + index * 7).dp,
                    y = if (index % 2 == 0) (58 + index * 18).dp else (-45 - index * 13).dp
                )
                .size(if (category == "Welcome") 10.dp else 7.dp)
                .clip(if (category == "Welcome") CircleShape else RoundedCornerShape(2.dp))
                .background(accent.copy(alpha = 0.48f))
        )
    }
    if (category == "Income") {
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 96.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(22.dp, 34.dp, 48.dp, 64.dp).forEach { height ->
                Box(
                    modifier = Modifier
                        .width(9.dp)
                        .height(height)
                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                        .background(accent.copy(alpha = 0.35f))
                )
            }
        }
    }
}

@Composable
private fun PreviewLayoutTwo(category: String, form: CustomPosterState) {
    if (category == "Birthday") {
        ElegantPhotoBirthdayLayout(form)
        return
    }
    Column(modifier = Modifier.fillMaxSize()) {
        PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PreviewFramedPhoto(
                form.profilePhotoUri,
                Modifier
                    .width(178.dp)
                    .height(238.dp),
                RoundedCornerShape(22.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                PreviewHeadline(category, textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(10.dp))
                PreviewName(form.personName, textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(5.dp))
                PreviewWishes(
                    message = form.wishesMessage,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.height(92.dp)
                )
            }
        }
        PreviewFooter(form)
    }
}

@Composable
private fun PreviewLayoutThree(category: String, form: CustomPosterState) {
    if (category == "Birthday") {
        CelebrationBirthdayLayout(form)
        return
    }
    if (category == "Achievement") {
        AchievementSideCertificateLayout(form)
        return
    }
    Column(modifier = Modifier.fillMaxSize()) {
        PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(26.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier.weight(0.82f),
                verticalArrangement = Arrangement.Center
            ) {
                PreviewHeadline(category, textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(8.dp))
                PreviewName(form.personName, textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(5.dp))
                PreviewWishes(
                    message = form.wishesMessage,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.height(96.dp)
                )
            }
            PreviewHeroPhoto(form.profilePhotoUri, Modifier.size(190.dp), CircleShape)
        }
        Spacer(modifier = Modifier.weight(1f))
        PreviewFooter(form)
    }
}

@Composable
private fun AchievementSideCertificateLayout(form: CustomPosterState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(18.dp))
        AchievementCertificateTitle()
        Spacer(modifier = Modifier.height(14.dp))
        PreviewHeroPhoto(form.profilePhotoUri, Modifier.size(188.dp), CircleShape)
        Spacer(modifier = Modifier.height(12.dp))
        AchievementCertificateName(form.personName)
        Spacer(modifier = Modifier.height(8.dp))
        PreviewWishes(
            message = form.wishesMessage,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .height(116.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        PreviewFooter(form)
    }
}

@Composable
private fun AchievementCertificateTitle() {
    val style = LocalVideoColorStyle.current
    val title = LocalTemplateTitle.current.ifBlank { "Top Performer Certificate" }.uppercase()
    Text(
        text = title,
        color = style.titleColor,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            lineHeight = 18.sp
        ),
        textAlign = TextAlign.Center,
        maxLines = 3,
        softWrap = true,
        overflow = TextOverflow.Clip,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}

@Composable
private fun AchievementCertificateName(name: String) {
    val style = LocalVideoColorStyle.current
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp)
            .height(58.dp),
        contentAlignment = Alignment.Center
    ) {
        val displayName = name.ifBlank { "Person Name" }
        val textMeasurer = rememberTextMeasurer()
        val baseStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
        val fittedFontSize = remember(displayName, constraints.maxWidth, constraints.maxHeight, baseStyle) {
            fitWishFontSize(
                message = displayName,
                textMeasurer = textMeasurer,
                baseStyle = baseStyle,
                maxWidth = constraints.maxWidth.coerceAtLeast(1),
                maxHeight = constraints.maxHeight.coerceAtLeast(1),
                maxLines = 2,
                maxFontSize = 24f,
                minFontSize = 12f
            )
        }
        Text(
            text = displayName,
            color = style.nameColor,
            style = baseStyle.copy(
                fontSize = fittedFontSize.sp,
                lineHeight = (fittedFontSize * 1.08f).sp
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            softWrap = true,
            overflow = TextOverflow.Clip,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PreviewLayoutFour(category: String, form: CustomPosterState) {
    if (category == "Birthday") {
        PremiumGoldBirthdayLayout(form)
        return
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(12.dp))
        PreviewHeadline(category)
        Spacer(modifier = Modifier.height(16.dp))
        CategoryCircleSquarePhoto(
            uri = form.profilePhotoUri,
            category = category
        )
        Spacer(modifier = Modifier.height(12.dp))
        PreviewName(form.personName)
        Spacer(modifier = Modifier.height(4.dp))
        PreviewWishes(
            message = form.wishesMessage,
            modifier = Modifier.height(78.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        PreviewFooter(form)
    }
}

@Composable
private fun CategoryCircleSquarePhoto(
    uri: Uri?,
    category: String
) {
    val frameColors = when (category) {
        "Achievement" -> listOf(Color(0xFFFFE08A), Color(0xFFFFB300), Color(0xFF9A6200))
        "Welcome" -> listOf(Color(0xFFB7FFF0), Color(0xFF20BFA9), Color(0xFF087B86))
        "Income" -> listOf(Color(0xFFFFD878), Color(0xFFB56DE2), Color(0xFF5A2389))
        else -> categorySpecFor(category).colors
    }
    val innerBorder = frameColors.getOrElse(1) { Color.White }

    Box(
        modifier = Modifier
            .size(220.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Brush.linearGradient(frameColors))
            .padding(7.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.28f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(198.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .border(3.dp, innerBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Profile photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .adjustableProfilePhoto(uri)
                )
            } else {
                SamplePortraitImage(contentScale = ContentScale.Crop)
            }
        }
    }
}

@Composable
private fun RoyalHeroBirthdayLayout(form: CustomPosterState) {
    val style = LocalVideoColorStyle.current
    val gold = style.accentColor
    Box(modifier = Modifier.fillMaxSize()) {
        BirthdayGoldDecorations()
        Column(modifier = Modifier.fillMaxSize()) {
            PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFF2A0), gold, Color(0xFF8A5200))
                        )
                    )
                    .padding(6.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(Color.Black)
                    .border(1.dp, gold.copy(alpha = 0.72f), RoundedCornerShape(23.dp))
            ) {
                PreviewPhoto(
                    uri = form.profilePhotoUri,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(23.dp)
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.96f))
                            )
                        )
                        .padding(start = 18.dp, end = 18.dp, top = 96.dp, bottom = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "HAPPY",
                        color = style.titleColor,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 4.sp
                        )
                    )
                    Text(
                        text = "BIRTHDAY",
                        color = gold,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
                    )
                    Text(
                        text = form.personName.ifBlank { "Person Name" },
                        color = style.nameColor,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Clip
                    )
                    AdaptiveWishText(
                        message = birthdayWishText(form.wishesMessage),
                        color = style.messageColor,
                        maxLines = 4,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Icon(
                        Icons.Default.Cake,
                        contentDescription = null,
                        tint = gold,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            PreviewFooter(form, accentColor = gold)
        }
    }
}

@Composable
private fun FullPortraitStoryBirthdayLayout(form: CustomPosterState) {
    val gold = Color(0xFFFFD86B)
    Column(modifier = Modifier.fillMaxSize()) {
        PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF2A123F))
                .border(2.dp, Color.White.copy(alpha = 0.42f), RoundedCornerShape(28.dp))
        ) {
            PreviewPhoto(
                uri = form.profilePhotoUri,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(28.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 14.dp, y = (-14).dp)
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(gold.copy(alpha = 0.17f))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xFF2C0F46).copy(alpha = 0.96f))
                        )
                    )
                    .padding(start = 18.dp, end = 18.dp, top = 88.dp, bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Happy",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                )
                Text(
                    text = "BIRTHDAY",
                    color = gold,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = form.personName.ifBlank { "Person Name" },
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Clip
                )
                AdaptiveWishText(
                    message = birthdayWishText(form.wishesMessage),
                    color = Color.White.copy(alpha = 0.92f),
                    maxLines = 4,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PreviewFooter(form, accentColor = gold)
    }
}

@Composable
private fun LuxuryBlackGoldBirthdayLayout(form: CustomPosterState) {
    val gold = Color(0xFFFFC83D)
    Box(modifier = Modifier.fillMaxSize()) {
        BirthdayGoldDecorations()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            BirthdayHeadline(color = gold)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(224.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFF0A0), gold, Color(0xFF9A5B00))
                        )
                    )
                    .padding(7.dp)
            ) {
                PreviewPhoto(
                    uri = form.profilePhotoUri,
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = form.personName.ifBlank { "Person Name" },
                color = gold,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Clip
            )
            BirthdayDivider(gold)
            AdaptiveWishText(
                message = birthdayWishText(form.wishesMessage),
                color = Color.White.copy(alpha = 0.96f),
                maxLines = 5,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            PreviewFooter(form, accentColor = gold)
        }
    }
}

@Composable
private fun ElegantPhotoBirthdayLayout(form: CustomPosterState) {
    val highlight = Color(0xFFFFD05A)
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .width(260.dp)
                .height(300.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(7.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(22.dp))
        ) {
            PreviewPhoto(
                uri = form.profilePhotoUri,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(22.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.72f))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        BirthdayHeadline(color = Color.White)
        Text(
            text = form.personName.ifBlank { "Person Name" },
            color = highlight,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Clip
        )
        BirthdayDivider(Color.White.copy(alpha = 0.82f))
        AdaptiveWishText(
            message = birthdayWishText(form.wishesMessage),
            color = Color.White.copy(alpha = 0.94f),
            maxLines = 5,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        PreviewFooter(form)
    }
}

@Composable
private fun CelebrationBirthdayLayout(form: CustomPosterState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        BirthdayHeadline(color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        PreviewPhoto(form.profilePhotoUri, Modifier.size(176.dp), CircleShape)
        Spacer(modifier = Modifier.height(39.dp))
        Text(
            text = form.personName.ifBlank { "Person Name" },
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Clip
        )
        Spacer(modifier = Modifier.height(8.dp))
        AdaptiveWishText(
            message = form.wishesMessage.ifBlank { "Wishing You Happiness, Success and Prosperity" },
            color = Color.White.copy(alpha = 0.94f),
            maxLines = 4,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 14.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        PreviewFooter(form)
    }
}

@Composable
private fun BirthdayGoldDecorations() {
    Box(modifier = Modifier.fillMaxSize()) {
        listOf(
            24.dp to 150.dp,
            315.dp to 110.dp,
            30.dp to 430.dp,
            312.dp to 390.dp
        ).forEachIndexed { index, position ->
            Box(
                modifier = Modifier
                    .offset(x = position.first, y = position.second)
                    .size(if (index % 2 == 0) 30.dp else 22.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFE88A), Color(0xFFB87800))
                        )
                    )
            )
        }
    }
}

private fun birthdayWishText(message: String): String {
    val normalized = message
        .replace("Happy Birthday", "", ignoreCase = true)
        .trim()
        .replace("\n", " ")
    return normalized.ifBlank { "Wishing You Happiness, Success & Prosperity" }
}

@Composable
private fun PremiumGoldBirthdayLayout(form: CustomPosterState) {
    val gold = Color(0xFFFFC83D)
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(6.dp))
        BirthdayHeadline(color = gold)
        Spacer(modifier = Modifier.height(7.dp))
        GoldCircleSquarePhoto(form.profilePhotoUri, size = 190.dp)
        Spacer(modifier = Modifier.height(39.dp))
        Text(
            text = form.personName.ifBlank { "Person Name" },
            color = gold,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Clip
        )
        Spacer(modifier = Modifier.height(8.dp))
        AdaptiveWishText(
            message = form.wishesMessage.ifBlank { "Wishing You Happiness, Success and Prosperity" },
            color = gold.copy(alpha = 0.94f),
            maxLines = 4,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 18.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        PreviewFooter(form, accentColor = gold)
    }
}

@Composable
private fun BirthdayHeadline(color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Happy",
            color = color,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Medium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        )
        Text(
            text = "BIRTHDAY",
            color = color,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun BirthdayDivider(color: Color) {
    Row(
        modifier = Modifier
            .width(150.dp)
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = color.copy(alpha = 0.45f))
        Box(
            modifier = Modifier
                .padding(horizontal = 7.dp)
                .size(7.dp)
                .graphicsLayer(rotationZ = 45f)
                .background(color)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = color.copy(alpha = 0.45f))
    }
}

@Composable
private fun GoldCircleSquarePhoto(uri: Uri?, size: Dp = 210.dp) {
    val gold = Color(0xFFFFC83D)
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFE07A), gold, Color(0xFFB97800))
                )
            )
            .padding(7.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF252525)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size - 20.dp)
                .clip(CircleShape)
                .background(Color(0xFF3A3A3A))
                .border(2.dp, gold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Profile photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .adjustableProfilePhoto(uri)
                )
            } else {
                SamplePortraitImage(contentScale = ContentScale.Crop)
            }
        }
    }
}

@Composable
private fun PreviewLayoutFive(category: String, form: CustomPosterState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Black.copy(alpha = 0.18f))
                .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(28.dp))
        ) {
            PreviewDominantPhoto(
                uri = form.profilePhotoUri,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(28.dp)
            )
            PreviewCategoryAccent(
                category = category,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f))
                        )
                    )
                    .padding(start = 18.dp, end = 18.dp, top = 72.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PreviewHeadline(category, textAlign = TextAlign.Start)
                PreviewName(form.personName, textAlign = TextAlign.Start)
                AdaptiveWishText(
                    message = form.wishesMessage.ifBlank { "Wishing you continued success" },
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Start,
                    maxLines = 4
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        PreviewFooter(form)
    }
}

@Composable
private fun PreviewLayoutSix(category: String, form: CustomPosterState) {
    Column(modifier = Modifier.fillMaxSize()) {
        PreviewLogo(form.companyLogoUri, form.companyName, Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Black.copy(alpha = 0.2f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(28.dp))
        ) {
            PreviewDominantPhoto(
                uri = form.profilePhotoUri,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(28.dp)
            )
            PreviewCategoryAccent(
                category = category,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))
                        )
                    )
                    .padding(start = 18.dp, end = 18.dp, top = 64.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                PreviewHeadline(category, textAlign = TextAlign.Start)
                PreviewName(form.personName, textAlign = TextAlign.Start)
                AdaptiveWishText(
                    message = form.wishesMessage.ifBlank { "Wishing you continued success" },
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Start,
                    maxLines = 4
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        PreviewFooter(form)
    }
}

@Composable
private fun PreviewLogo(uri: Uri?, companyName: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f))
                .border(1.dp, Color.White.copy(alpha = 0.42f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Company logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = companyName.ifBlank { "Company Name" },
            color = Color.White,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PreviewPhoto(uri: Uri?, modifier: Modifier, shape: androidx.compose.ui.graphics.Shape) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = 0.18f))
            .border(2.dp, Color.White.copy(alpha = 0.7f), shape),
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .adjustableProfilePhoto(uri)
            )
        } else {
            SamplePortraitImage(contentScale = ContentScale.Crop)
        }
    }
}

@Composable
private fun PreviewHeroPhoto(uri: Uri?, modifier: Modifier, shape: androidx.compose.ui.graphics.Shape) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.16f))
            .border(1.dp, Color.White.copy(alpha = 0.24f), shape)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        PreviewPhoto(uri = uri, modifier = modifier, shape = shape)
    }
}

@Composable
private fun PreviewFramedPhoto(uri: Uri?, modifier: Modifier, shape: androidx.compose.ui.graphics.Shape) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color.Black.copy(alpha = 0.18f))
            .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(28.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        PreviewPhoto(uri = uri, modifier = modifier, shape = shape)
    }
}

@Composable
private fun PreviewWidePhoto(uri: Uri?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(222.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color.Black.copy(alpha = 0.16f))
            .border(1.dp, Color.White.copy(alpha = 0.24f), RoundedCornerShape(26.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        PreviewPhoto(
            uri = uri,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun PreviewDominantPhoto(
    uri: Uri?,
    modifier: Modifier,
    shape: androidx.compose.ui.graphics.Shape
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = "Profile photo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .adjustableProfilePhoto(uri)
            )
        } else {
            SamplePortraitImage(contentScale = ContentScale.Crop)
        }
    }
}

@Composable
private fun SamplePortraitImage(contentScale: ContentScale) {
    Image(
        painter = painterResource(LocalSamplePortraitRes.current),
        contentDescription = "Sample profile photo",
        contentScale = contentScale,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun Modifier.adjustableProfilePhoto(uri: Uri?): Modifier {
    if (uri == null) return this
    val adjustment = LocalPhotoAdjustment.current
    val onAdjustmentChange = LocalPhotoAdjustmentChange.current
    return this
        .graphicsLayer(
            scaleX = adjustment.scale,
            scaleY = adjustment.scale,
            translationX = adjustment.offsetX,
            translationY = adjustment.offsetY
        )
        .pointerInput(uri, adjustment) {
            detectTransformGestures { _, pan, zoom, _ ->
                onAdjustmentChange(
                    adjustment.copy(
                        scale = (adjustment.scale * zoom).coerceIn(0.7f, 4f),
                        offsetX = (adjustment.offsetX + pan.x).coerceIn(-600f, 600f),
                        offsetY = (adjustment.offsetY + pan.y).coerceIn(-800f, 800f)
                    )
                )
            }
        }
}

@Composable
private fun PreviewCategoryAccent(category: String, modifier: Modifier = Modifier) {
    val spec = categorySpecFor(category)
    val label = when (category) {
        "Welcome" -> "TEAM"
        "Achievement" -> "AWARD"
        "Birthday" -> "CELEBRATE"
        "Income" -> "GROWTH"
        else -> category.uppercase()
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.42f))
            .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
            .padding(horizontal = 11.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = spec.icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold)
        )
    }
}

@Composable
private fun PreviewHeadline(category: String, textAlign: TextAlign = TextAlign.Center) {
    val style = LocalVideoColorStyle.current
    val templateTitle = LocalTemplateTitle.current
    val headline = if (category == "Birthday" || templateTitle.isBlank()) {
        posterHeadlineFor(category)
    } else {
        templateTitle.uppercase()
    }
    val headlineStyle = if (category == "Birthday") {
        MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
    } else {
        MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (headline.length > 22) 18.sp else 22.sp,
            lineHeight = if (headline.length > 22) 20.sp else 24.sp
        )
    }
    Text(
        text = headline,
        color = style.titleColor,
        style = headlineStyle,
        textAlign = textAlign,
        maxLines = 3,
        overflow = TextOverflow.Clip
    )
}

@Composable
private fun PreviewName(name: String, textAlign: TextAlign = TextAlign.Center) {
    val style = LocalVideoColorStyle.current
    Text(
        text = name.ifBlank { "Person Name" },
        color = style.nameColor,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
        textAlign = textAlign,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PreviewWishes(
    message: String,
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier
) {
    val style = LocalVideoColorStyle.current
    AdaptiveWishText(
        message = message.ifBlank { "Your message goes here" },
        color = style.messageColor,
        textAlign = textAlign,
        maxLines = 3,
        modifier = modifier
    )
}

@Composable
internal fun AdaptiveWishText(
    message: String,
    color: Color,
    textAlign: TextAlign = TextAlign.Center,
    maxLines: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val textMeasurer = rememberTextMeasurer()
        val minFontSize = 6f
        val maxFontSize = adaptiveWishFontSize(message).toFloat()
        val targetWidth = constraints.maxWidth.coerceAtLeast(1)
        val targetHeight = if (constraints.hasBoundedHeight) constraints.maxHeight else Int.MAX_VALUE / 4
        val baseStyle = MaterialTheme.typography.bodyLarge
        val fittedFontSize = remember(message, targetWidth, targetHeight, maxLines, baseStyle) {
            fitWishFontSize(
                message = message,
                textMeasurer = textMeasurer,
                baseStyle = baseStyle,
                maxWidth = targetWidth,
                maxHeight = targetHeight,
                maxLines = maxLines,
                maxFontSize = maxFontSize,
                minFontSize = minFontSize
            )
        }
        Text(
            text = message,
            color = color,
            style = baseStyle.copy(
                fontSize = fittedFontSize.sp,
                lineHeight = (fittedFontSize * 1.12f).sp
            ),
            textAlign = textAlign,
            maxLines = maxLines,
            softWrap = true,
            overflow = TextOverflow.Clip
        )
    }
}

private fun adaptiveWishFontSize(message: String): Int {
    return when {
        message.length > 210 -> 8
        message.length > 170 -> 9
        message.length > 125 -> 10
        message.length > 90 -> 11
        else -> 14
    }
}

private fun fitWishFontSize(
    message: String,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    baseStyle: TextStyle,
    maxWidth: Int,
    maxHeight: Int,
    maxLines: Int,
    maxFontSize: Float,
    minFontSize: Float
): Float {
    var size = maxFontSize
    while (size >= minFontSize) {
        val result = textMeasurer.measure(
            text = AnnotatedString(message),
            style = baseStyle.copy(
                fontSize = size.sp,
                lineHeight = (size * 1.12f).sp
            ),
            softWrap = true,
            maxLines = Int.MAX_VALUE,
            constraints = Constraints(maxWidth = maxWidth)
        )
        if (!result.hasVisualOverflow && result.lineCount <= maxLines && result.size.height <= maxHeight) {
            return size
        }
        size -= 0.5f
    }
    return minFontSize
}

@Composable
private fun PreviewFooter(
    form: CustomPosterState,
    accentColor: Color = Color.Transparent
) {
    val style = LocalVideoColorStyle.current
    val effectiveAccent = if (accentColor != Color.Transparent) accentColor else style.borderColor
    val effectiveText = if (accentColor != Color.Transparent) accentColor else style.footerColor
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.22f))
            .then(
                if (accentColor != Color.Transparent) {
                    Modifier.border(2.dp, effectiveAccent, RoundedCornerShape(18.dp))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = form.companyName.ifBlank { "Company Name" },
            color = effectiveText,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = form.websiteName.ifBlank { "www.company.com" },
            color = effectiveText.copy(alpha = 0.86f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (form.mobileNumber.isNotBlank()) {
            Text(
                text = form.mobileNumber,
                color = effectiveText.copy(alpha = 0.86f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun posterHeadlineFor(category: String): String {
    return when (category) {
        "Birthday" -> "Happy Birthday"
        "Achievement" -> "Congratulations"
        "Welcome" -> "Welcome"
        "Income" -> "Reward Recognition"
        else -> category
    }
}

private fun defaultNameFor(category: String): String {
    return when (category) {
        "Income" -> "Amit Verma"
        "Achievement" -> "Rahul Sharma"
        "Welcome" -> "Priya Singh"
        else -> "Rahul Sharma"
    }
}

private fun defaultWishesFor(category: String): String {
    return wishMessagesForCategory(category).first()
}

internal fun wishMessagesForCategory(category: String): List<String> {
    return WishMessageRepository.forCategory(category)
}

private object WishMessageRepository {
    private val defaultWishes = professionalWishes(
        "Wishing you continued success and meaningful progress in every step ahead.",
        "May this moment bring confidence, inspiration, and new opportunities for growth.",
        "Sending warm wishes for prosperity, happiness, and achievements worth celebrating.",
        "May your journey ahead be filled with positive milestones and lasting success.",
        "Wishing you strength, clarity, and the courage to keep moving toward bigger goals.",
        "May every effort bring rewarding results and every challenge become a lesson.",
        "Wishing you a future filled with progress, purpose, and moments of genuine pride.",
        "May this occasion bring renewed energy, fresh ideas, and meaningful achievements.",
        "Sending best wishes for happiness, professional growth, and continued excellence.",
        "May your dedication create new possibilities and lead you toward greater success.",
        "Wishing you a bright path ahead with confidence, balance, and steady progress.",
        "May this message bring encouragement, appreciation, and motivation for the journey.",
        "Wishing you success that grows stronger with every thoughtful action you take.",
        "May your goals turn into achievements and your hard work into proud memories.",
        "Sending sincere wishes for peace, prosperity, and a future filled with promise."
    )

    private val wishesByCategory = mapOf(
        "festival" to listOf(
            "सृजन, कौशल और समृद्धि की शुभकामनाएँ।",
            "भगवान विश्वकर्मा आपके जीवन में सुख, शांति और सफलता लाएँ।",
            "May your work bring joy, purpose and prosperity."
        ),
        "motivation" to listOf(
            "छोटे कदम, हर दिन।\nयही बड़ी सफलता की शुरुआत है।",
            "सपनों को दिशा दो।\nहर दिन एक नया कदम बढ़ाओ।",
            "Small steps. Every day.\nThat is how great things begin."
        ),
        "birthday" to professionalWishes(
            "Wishing you a wonderful birthday filled with happiness, good health, and endless success.",
            "Happy Birthday! May this special day bring blessings, memories, and achievements all year.",
            "May your birthday begin a year of confidence, prosperity, and beautiful moments.",
            "Wishing you joy today and success in every dream you continue to pursue.",
            "May this new year of life bring peace, progress, and countless reasons to smile.",
            "Happy Birthday! May your hard work turn into victories and your hopes into reality.",
            "Wishing you a celebration filled with love and a future filled with proud milestones.",
            "May your special day bring renewed energy, meaningful happiness, and lasting success.",
            "Happy Birthday to someone truly valued, respected, and appreciated by everyone.",
            "May this birthday open doors to growth, prosperity, and memorable achievements.",
            "Wishing you health, happiness, and the courage to keep reaching higher.",
            "May your year ahead be bright, balanced, successful, and full of blessings.",
            "Happy Birthday! May every new opportunity bring you closer to your goals.",
            "Wishing you a beautiful day and a powerful year of growth and success.",
            "May your birthday bring joy to your heart and inspiration for the journey ahead."
        ),
        "achievement" to professionalWishes(
            "Congratulations on this achievement; your dedication and discipline have created a proud milestone.",
            "Your hard work has delivered meaningful success and inspired everyone around you.",
            "Celebrating your achievement with pride and wishing you many more victories ahead.",
            "This milestone reflects your focus, consistency, and commitment to excellence.",
            "Congratulations! May this success become the foundation for even greater accomplishments.",
            "Your achievement proves that patience, effort, and belief can create powerful results.",
            "Well done on reaching a milestone that truly deserves recognition and appreciation.",
            "May this proud moment bring confidence, motivation, and fresh goals for the future.",
            "Congratulations for setting an example of dedication, growth, and strong performance.",
            "Your success story continues to inspire others to work with purpose and courage.",
            "This achievement is a celebration of your effort, resilience, and positive attitude.",
            "May your recognition today lead to bigger dreams and many more proud moments.",
            "Congratulations on turning commitment into success and ambition into achievement.",
            "Your milestone shows the power of focused action and a determined mindset.",
            "Wishing you continued excellence as you move toward your next great achievement."
        ),
        "welcome" to professionalWishes(
            "Welcome to the team; may this new journey bring learning, growth, and success.",
            "We are excited to have you with us and look forward to achieving great things together.",
            "Welcome aboard! May your new beginning be rewarding, inspiring, and full of progress.",
            "Your presence adds new energy, ideas, and possibilities to our growing team.",
            "Wishing you a warm welcome and a confident start to this meaningful journey.",
            "May your time with us bring strong relationships, valuable learning, and proud achievements.",
            "Welcome to a place where teamwork, trust, and shared success truly matter.",
            "We are delighted to welcome you and excited to see your contribution shine.",
            "May this new chapter bring confidence, opportunity, and many moments of growth.",
            "Welcome to the family; together we will build progress, success, and impact.",
            "Your journey starts here with support, encouragement, and opportunities to grow.",
            "Wishing you a successful beginning and a future filled with meaningful accomplishments.",
            "Welcome aboard! May each day bring clarity, confidence, and positive experiences.",
            "We are proud to have you here and excited for the journey ahead.",
            "May this welcome mark the beginning of strong teamwork and lasting success."
        ),
        "income" to professionalWishes(
            "Congratulations on this income milestone; your consistency and effort are creating real progress.",
            "Your financial achievement reflects focus, discipline, and a strong commitment to growth.",
            "Celebrating your income success and wishing you even greater rewards ahead.",
            "May this milestone bring confidence, prosperity, and motivation for bigger goals.",
            "Your dedication is turning ambition into measurable and meaningful business success.",
            "Congratulations on building momentum through smart action and consistent effort.",
            "May this income achievement open doors to new opportunities and stronger growth.",
            "Your success proves that persistence, belief, and daily discipline create results.",
            "Wishing you continued financial progress and many more milestones worth celebrating.",
            "This reward is a proud reflection of your hard work and positive mindset.",
            "May your growth journey continue with confidence, leadership, and prosperity.",
            "Congratulations on reaching a milestone that inspires others to believe and act.",
            "Your income success is a celebration of effort, vision, and steady progress.",
            "May this achievement strengthen your goals and bring even greater possibilities.",
            "Wishing you more success, stronger results, and continued financial growth ahead."
        ),
        "anniversary" to professionalWishes(
            "Wishing you a beautiful anniversary filled with gratitude, memories, and continued togetherness.",
            "May this special milestone celebrate love, trust, and the journey you have built together.",
            "Happy Anniversary! May your bond grow stronger with every passing year.",
            "Wishing you lasting happiness, meaningful memories, and many more milestones ahead.",
            "May this anniversary bring warmth, appreciation, and renewed joy to your journey.",
            "Celebrating your togetherness and wishing you continued peace, love, and prosperity.",
            "May every year ahead bring deeper understanding and countless beautiful moments.",
            "Wishing you a day filled with smiles and a future filled with shared dreams.",
            "Happy Anniversary! May your story continue with love, respect, and happiness.",
            "May this milestone remind you of every blessing your journey has brought.",
            "Wishing you strength in togetherness and joy in every new chapter ahead.",
            "May your anniversary be as meaningful as the memories you continue to create.",
            "Sending warm wishes for love, harmony, and many more years of happiness.",
            "May your partnership continue to inspire everyone with grace and commitment.",
            "Wishing you a wonderful anniversary and a future filled with beautiful memories."
        ),
        "festival" to professionalWishes(
            "Wishing you a joyful festival filled with happiness, prosperity, and togetherness.",
            "May this festive season bring peace, success, and beautiful moments with loved ones.",
            "Sending warm festive wishes for health, happiness, and a bright future ahead.",
            "May the spirit of this festival fill your life with hope and positivity.",
            "Wishing you celebrations full of light, laughter, and meaningful memories.",
            "May this festival bring new energy, fresh blessings, and continued success.",
            "Sending heartfelt wishes for prosperity, harmony, and joyful celebrations.",
            "May your home and heart be filled with peace, gratitude, and happiness.",
            "Wishing you a festival that brings people closer and dreams brighter.",
            "May this season of celebration open doors to progress and abundance.",
            "Wishing you warmth, positivity, and countless reasons to celebrate.",
            "May every festive moment bring smiles, strength, and renewed inspiration.",
            "Sending wishes for a bright festival and a successful journey ahead.",
            "May this celebration bring balance, blessings, and beautiful new beginnings.",
            "Wishing you festive joy, meaningful connections, and lasting prosperity."
        ),
        "business" to professionalWishes(
            "Wishing your business continued growth, strong partnerships, and lasting success.",
            "May every decision bring clarity, every effort bring progress, and every goal bring results.",
            "Sending best wishes for innovation, leadership, and meaningful business achievements.",
            "May your brand continue to grow with trust, quality, and customer confidence.",
            "Wishing you success in every project and strength in every challenge ahead.",
            "May your business journey be filled with opportunity, progress, and prosperity.",
            "Wishing you strong results, valuable connections, and continued professional excellence.",
            "May your hard work build momentum and create success that lasts.",
            "Sending wishes for smart growth, wise decisions, and rewarding outcomes.",
            "May every milestone reflect your vision, commitment, and business leadership.",
            "Wishing your team confidence, collaboration, and powerful results ahead.",
            "May your business continue to inspire trust and create meaningful impact.",
            "Wishing you progress, profitability, and a future filled with opportunity.",
            "May your goals turn into achievements through focus, quality, and dedication.",
            "Sending best wishes for growth, stability, and continued business success."
        ),
        "motivation" to professionalWishes(
            "Believe in your journey; every small step can create a meaningful future.",
            "Keep moving forward with courage, focus, and faith in your own potential.",
            "Success grows through discipline, patience, and the willingness to begin again.",
            "Your effort today can become the achievement you celebrate tomorrow.",
            "Stay consistent, trust the process, and let progress build your confidence.",
            "Every challenge carries a lesson and every lesson can strengthen your path.",
            "Keep your goals clear, your mindset strong, and your actions steady.",
            "Great results come from small choices repeated with purpose and belief.",
            "You are capable of growth, success, and meaningful change every day.",
            "Let your dedication speak louder than doubt and your progress inspire others.",
            "Stay focused on what matters and keep building the future you deserve.",
            "Every new day is a chance to improve, rise, and move ahead.",
            "Your commitment can turn dreams into plans and plans into achievements.",
            "Keep going with confidence; the journey rewards those who refuse to stop.",
            "Believe in your work, honor your progress, and keep reaching higher."
        )
    )

    fun forCategory(category: String): List<String> {
        return wishesByCategory[category.trim().lowercase()] ?: defaultWishes
    }

    private fun professionalWishes(vararg firstLines: String): List<String> {
        return firstLines.map { firstLine ->
            "$firstLine\nMay the days ahead bring confidence, happiness, and achievements worth remembering."
        }
    }
}

private fun captureViewBounds(
    rootView: android.view.View,
    bounds: androidx.compose.ui.geometry.Rect
): Bitmap {
    val fullBitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(fullBitmap)
    rootView.draw(canvas)

    val left = bounds.left.toInt().coerceIn(0, fullBitmap.width - 1)
    val top = bounds.top.toInt().coerceIn(0, fullBitmap.height - 1)
    val right = bounds.right.toInt().coerceIn(left + 1, fullBitmap.width)
    val bottom = bounds.bottom.toInt().coerceIn(top + 1, fullBitmap.height)
    return Bitmap.createBitmap(fullBitmap, left, top, right - left, bottom - top)
}

private suspend fun savePosterBitmap(
    context: Context,
    viewModel: PosterViewModel,
    poster: Poster,
    form: CustomPosterState,
    bitmap: Bitmap
) {
    try {
        val storyBitmap = withContext(Dispatchers.Default) {
            Bitmap.createScaledBitmap(bitmap, 1080, 1920, true)
        }
        val galleryUri = withContext(Dispatchers.IO) {
            saveBitmapToGallery(context, storyBitmap, poster.title)
        }
        val thumbnailPath = withContext(Dispatchers.IO) {
            saveThumbnailToLocalStorage(context, storyBitmap, poster.title)
        }
        viewModel.savePosterRecord(
            poster = poster.copy(
                timestamp = System.currentTimeMillis(),
                photoScale = form.photoScale,
                photoOffsetX = form.photoOffsetX,
                photoOffsetY = form.photoOffsetY
            ),
            galleryImageUri = galleryUri.toString(),
            thumbnailPath = thumbnailPath
        )
    } catch (e: Exception) {
        viewModel.showStatusMessage("Unable to save poster")
    }
}

/** Saves the export plus the independent Welcome editor state for My Designs. */
private suspend fun saveWelcomePosterProject(
    context: Context,
    viewModel: PosterViewModel,
    preset: Poster,
    project: WelcomePosterState,
    bitmap: Bitmap
) {
    val export = withContext(Dispatchers.Default) {
        Bitmap.createScaledBitmap(bitmap, project.canvasWidth, project.canvasHeight, true)
    }
    val galleryUri = withContext(Dispatchers.IO) { saveBitmapToGallery(context, export, preset.title) }
    val thumbnailPath = withContext(Dispatchers.IO) { saveThumbnailToLocalStorage(context, export, preset.title) }
    viewModel.savePosterRecordNow(
        poster = preset.copy(
            id = 0,
            timestamp = System.currentTimeMillis(),
            backgroundType = "welcome_project",
            backgroundImageRes = WelcomeProjectStateStore.encode(project),
            photoScale = project.elements.firstOrNull { it.role == "member" }?.cropScale ?: 1f,
            photoOffsetX = project.elements.firstOrNull { it.role == "member" }?.cropX ?: 0f,
            photoOffsetY = project.elements.firstOrNull { it.role == "member" }?.cropY ?: 0f
        ),
        galleryImageUri = galleryUri.toString(),
        thumbnailPath = thumbnailPath
    )
    viewModel.showStatusMessage("Welcome poster saved — reopen it from My Designs to edit")
}

private suspend fun updateWelcomePosterProject(
    context: Context,
    existing: Poster,
    project: WelcomePosterState,
    bitmap: Bitmap
): Poster {
    val export = withContext(Dispatchers.Default) { Bitmap.createScaledBitmap(bitmap, project.canvasWidth, project.canvasHeight, true) }
    val galleryUri = withContext(Dispatchers.IO) { saveBitmapToGallery(context, export, existing.title) }
    val thumbnailPath = withContext(Dispatchers.IO) { saveThumbnailToLocalStorage(context, export, existing.title) }
    return existing.copy(
        timestamp = System.currentTimeMillis(),
        backgroundType = "welcome_project",
        backgroundImageRes = WelcomeProjectStateStore.encode(project),
        galleryImageUri = galleryUri.toString(),
        thumbnailPath = thumbnailPath,
        photoScale = project.elements.firstOrNull { it.role == "member" }?.cropScale ?: 1f,
        photoOffsetX = project.elements.firstOrNull { it.role == "member" }?.cropX ?: 0f,
        photoOffsetY = project.elements.firstOrNull { it.role == "member" }?.cropY ?: 0f
    )
}

private suspend fun viewModelStatusVideoExport(
    context: Context,
    bitmap: Bitmap,
    title: String
) {
    exportPosterBitmapVideoToGallery(
        context = context,
        posterBitmap = bitmap,
        state = VideoCustomizationState(title = title, animationStyle = "Fade", duration = "15 Seconds"),
        title = title,
        onProgress = {}
    )
}

private suspend fun savePosterFromTemplate(
    context: Context,
    viewModel: PosterViewModel,
    poster: Poster,
    form: CustomPosterState?,
    layoutIndex: Int
) {
    try {
        val bitmap = withContext(Dispatchers.IO) {
            renderPosterBitmap(context, poster, form ?: defaultPosterStateFor(poster.category), layoutIndex)
        }
        val galleryUri = withContext(Dispatchers.IO) {
            saveBitmapToGallery(context, bitmap, poster.title)
        }
        val thumbnailPath = withContext(Dispatchers.IO) {
            saveThumbnailToLocalStorage(context, bitmap, poster.title)
        }
        viewModel.savePosterRecord(
            poster = poster.copy(timestamp = System.currentTimeMillis()),
            galleryImageUri = galleryUri.toString(),
            thumbnailPath = thumbnailPath
        )
    } catch (e: Exception) {
        viewModel.showStatusMessage("Unable to save poster")
    }
}

private fun defaultPosterStateFor(category: String): CustomPosterState {
    return CustomPosterState(
        personName = defaultNameFor(category),
        wishesMessage = defaultWishesFor(category),
        companyName = "ABC Marketing Pvt Ltd",
        websiteName = "www.abcmarketing.com"
    )
}

private fun renderPosterBitmap(
    context: Context,
    poster: Poster,
    form: CustomPosterState,
    layoutIndex: Int,
    colorStyle: VideoColorStyle = defaultVideoColorStyle,
    backgroundStyle: VideoBackgroundStyle = VideoBackgroundStyle()
): Bitmap {
    TemplateJson.template(poster.backgroundImageRes)?.let { template ->
        return TemplateRenderer.render(context, form.readyDocument(template))
    }
    if (poster.category.equals("Welcome", ignoreCase = true)) {
        return renderWelcomePosterBitmap(context, form.welcomeProject(poster.id))
    }
    if (isSignatureTemplate(poster.id)) {
        return renderSignaturePosterBitmap(context, poster.id, form.signatureContent())
    }
    val width = 1080
    val height = 1920
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val colors = backgroundStyle.colors ?: colorStyle.backgroundColors ?: categorySpecFor(poster.category).colors
    val startColor = colors.first().toArgb()
    val endColor = colors.last().toArgb()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), startColor, endColor, Shader.TileMode.CLAMP)
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    paint.shader = null
    paint.color = android.graphics.Color.argb(34, 255, 255, 255)
    canvas.drawOval(RectF(690f, -120f, 1220f, 420f), paint)
    paint.color = android.graphics.Color.argb(30, 0, 0, 0)
    canvas.drawOval(RectF(-170f, 1030f, 290f, 1510f), paint)

    when (layoutIndex) {
        2 -> drawPosterLayoutTwo(context, canvas, poster.category, form, width, height)
        3 -> drawPosterLayoutThree(context, canvas, poster, form, width, height)
        4 -> drawPosterLayoutFour(context, canvas, poster.category, form, width, height)
        5 -> drawPosterLayoutFive(context, canvas, poster.category, form, width, height)
        6 -> drawPosterLayoutSix(context, canvas, poster.category, form, width, height)
        else -> drawPosterLayoutOne(context, canvas, poster.category, form, width, height)
    }

    return bitmap
}

private fun drawPosterLayoutOne(context: Context, canvas: Canvas, category: String, form: CustomPosterState, width: Int, height: Int) {
    drawLogo(context, canvas, form, 90f, 90f)
    drawText(canvas, posterHeadlineFor(category), width / 2f, 250f, 72f, true, android.graphics.Color.WHITE, Paint.Align.CENTER)
    drawImageSlot(context, canvas, form.profilePhotoUri, RectF(340f, 360f, 740f, 760f), true)
    drawText(canvas, form.personName.ifBlank { "Person Name" }, width / 2f, 865f, 62f, true, android.graphics.Color.WHITE, Paint.Align.CENTER)
    drawMultilineText(canvas, form.wishesMessage.ifBlank { "Your message goes here" }, width / 2f, 955f, 42f, android.graphics.Color.WHITE, Paint.Align.CENTER, 760f)
    drawFooter(canvas, form, 105f, height - 250f, width - 210f)
}

private fun drawPosterLayoutTwo(context: Context, canvas: Canvas, category: String, form: CustomPosterState, width: Int, height: Int) {
    drawLogo(context, canvas, form, 90f, 90f)
    drawImageSlot(context, canvas, form.profilePhotoUri, RectF(95f, 405f, 485f, 900f), false)
    drawText(canvas, posterHeadlineFor(category), 545f, 430f, 54f, true, android.graphics.Color.WHITE, Paint.Align.LEFT)
    drawMultilineText(canvas, form.personName.ifBlank { "Person Name" }, 545f, 565f, 58f, true, android.graphics.Color.WHITE, Paint.Align.LEFT, 420f)
    drawMultilineText(canvas, form.wishesMessage.ifBlank { "Your message goes here" }, 545f, 735f, 38f, android.graphics.Color.WHITE, Paint.Align.LEFT, 430f)
    drawFooter(canvas, form, 105f, height - 250f, width - 210f)
}

private fun drawPosterLayoutThree(context: Context, canvas: Canvas, poster: Poster, form: CustomPosterState, width: Int, height: Int) {
    val category = poster.category
    drawLogo(context, canvas, form, 90f, 90f)
    if (category == "Achievement") {
        drawMultilineText(
            canvas = canvas,
            text = poster.title.uppercase(),
            x = width / 2f,
            baseline = 270f,
            size = 52f,
            bold = true,
            color = android.graphics.Color.WHITE,
            align = Paint.Align.CENTER,
            maxWidth = 760f
        )
        drawImageSlot(context, canvas, form.profilePhotoUri, RectF(340f, 430f, 740f, 830f), true)
        drawMultilineText(
            canvas = canvas,
            text = form.personName.ifBlank { "Person Name" },
            x = width / 2f,
            baseline = 935f,
            size = 66f,
            bold = true,
            color = android.graphics.Color.WHITE,
            align = Paint.Align.CENTER,
            maxWidth = 800f
        )
        drawMultilineText(
            canvas = canvas,
            text = form.wishesMessage.ifBlank { "Your message goes here" },
            x = width / 2f,
            baseline = 1045f,
            size = 42f,
            color = android.graphics.Color.WHITE,
            align = Paint.Align.CENTER,
            maxWidth = 820f
        )
        drawFooter(canvas, form, 105f, height - 250f, width - 210f)
        return
    }
    drawText(canvas, posterHeadlineFor(category), 100f, 430f, 58f, true, android.graphics.Color.WHITE, Paint.Align.LEFT)
    drawMultilineText(canvas, form.personName.ifBlank { "Person Name" }, 100f, 600f, 70f, true, android.graphics.Color.WHITE, Paint.Align.LEFT, 410f)
    drawMultilineText(canvas, form.wishesMessage.ifBlank { "Your message goes here" }, 100f, 720f, 38f, android.graphics.Color.WHITE, Paint.Align.LEFT, 430f)
    drawImageSlot(context, canvas, form.profilePhotoUri, RectF(570f, 390f, 960f, 780f), true)
    drawFooter(canvas, form, 105f, height - 250f, width - 210f)
}

private fun drawPosterLayoutFour(context: Context, canvas: Canvas, category: String, form: CustomPosterState, width: Int, height: Int) {
    drawLogo(context, canvas, form, width / 2f - 220f, 90f)
    drawText(canvas, posterHeadlineFor(category), width / 2f, 245f, 66f, true, android.graphics.Color.WHITE, Paint.Align.CENTER)
    drawImageSlot(context, canvas, form.profilePhotoUri, RectF(330f, 345f, 750f, 765f), false)
    drawText(canvas, form.personName.ifBlank { "Person Name" }, width / 2f, 870f, 60f, true, android.graphics.Color.WHITE, Paint.Align.CENTER)
    drawMultilineText(canvas, form.wishesMessage.ifBlank { "Your message goes here" }, width / 2f, 960f, 40f, android.graphics.Color.WHITE, Paint.Align.CENTER, 760f)
    drawFooter(canvas, form, 105f, height - 250f, width - 210f)
}

private fun drawPosterLayoutFive(context: Context, canvas: Canvas, category: String, form: CustomPosterState, width: Int, height: Int) {
    drawLogo(context, canvas, form, 90f, 70f)
    drawImageSlotFit(context, canvas, form.profilePhotoUri, RectF(70f, 230f, 1010f, 1370f), 54f)
    drawText(canvas, posterHeadlineFor(category), 105f, 1260f, 72f, true, android.graphics.Color.WHITE, Paint.Align.LEFT)
    drawText(canvas, form.personName.ifBlank { "Person Name" }, width / 2f, 1480f, 66f, true, android.graphics.Color.WHITE, Paint.Align.CENTER)
    drawMultilineText(canvas, form.wishesMessage.ifBlank { "Your message goes here" }, width / 2f, 1570f, 38f, android.graphics.Color.WHITE, Paint.Align.CENTER, 820f)
    drawFooter(canvas, form, 105f, height - 240f, width - 210f)
}

private fun drawPosterLayoutSix(context: Context, canvas: Canvas, category: String, form: CustomPosterState, width: Int, height: Int) {
    drawLogo(context, canvas, form, 90f, 60f)
    drawImageSlotFit(context, canvas, form.profilePhotoUri, RectF(55f, 210f, 1025f, 1600f), 54f)
    drawText(canvas, posterHeadlineFor(category), 100f, 1370f, 76f, true, android.graphics.Color.WHITE, Paint.Align.LEFT)
    drawText(canvas, form.personName.ifBlank { "Person Name" }, 100f, 1460f, 62f, true, android.graphics.Color.WHITE, Paint.Align.LEFT)
    drawFooter(canvas, form, 105f, height - 240f, width - 210f)
}

private fun drawLogo(context: Context, canvas: Canvas, form: CustomPosterState, x: Float, y: Float) {
    drawImageSlot(context, canvas, form.companyLogoUri, RectF(x, y, x + 120f, y + 120f), true)
    drawText(canvas, form.companyName.ifBlank { "Company Name" }, x + 150f, y + 76f, 38f, true, android.graphics.Color.WHITE, Paint.Align.LEFT)
}

private fun drawFooter(canvas: Canvas, form: CustomPosterState, x: Float, y: Float, width: Float) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = android.graphics.Color.argb(58, 0, 0, 0)
    canvas.drawRoundRect(RectF(x, y, x + width, y + 185f), 42f, 42f, paint)
    drawText(canvas, form.companyName.ifBlank { "Company Name" }, x + width / 2f, y + 72f, 40f, true, android.graphics.Color.WHITE, Paint.Align.CENTER)
    drawText(canvas, form.websiteName.ifBlank { "www.company.com" }, x + width / 2f, y + 123f, 32f, false, android.graphics.Color.WHITE, Paint.Align.CENTER)
    if (form.mobileNumber.isNotBlank()) {
        drawText(canvas, form.mobileNumber, x + width / 2f, y + 164f, 28f, false, android.graphics.Color.WHITE, Paint.Align.CENTER)
    }
}

private fun drawImageSlot(context: Context, canvas: Canvas, uri: Uri?, rect: RectF, circular: Boolean) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = android.graphics.Color.argb(46, 255, 255, 255)
    if (circular) canvas.drawOval(rect, paint) else canvas.drawRoundRect(rect, 42f, 42f, paint)
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 8f
    paint.color = android.graphics.Color.argb(190, 255, 255, 255)
    if (circular) canvas.drawOval(rect, paint) else canvas.drawRoundRect(rect, 42f, 42f, paint)
    paint.style = Paint.Style.FILL

    val source = uri?.let { decodeUriBitmap(context, it) }
    if (source != null) {
        canvas.drawBitmap(source, null, rect, Paint(Paint.ANTI_ALIAS_FLAG))
    } else {
        drawText(canvas, if (rect.width() < 150f) "LOGO" else "PHOTO", rect.centerX(), rect.centerY() + 14f, 30f, true, android.graphics.Color.WHITE, Paint.Align.CENTER)
    }
}

private fun drawImageSlotFit(context: Context, canvas: Canvas, uri: Uri?, rect: RectF, cornerRadius: Float) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = android.graphics.Color.argb(46, 255, 255, 255)
    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

    val source = uri?.let { decodeUriBitmap(context, it) }
    if (source != null) {
        val sourceRatio = source.width.toFloat() / source.height.toFloat()
        val targetRatio = rect.width() / rect.height()
        val destination = if (sourceRatio > targetRatio) {
            val fittedHeight = rect.width() / sourceRatio
            RectF(rect.left, rect.centerY() - fittedHeight / 2f, rect.right, rect.centerY() + fittedHeight / 2f)
        } else {
            val fittedWidth = rect.height() * sourceRatio
            RectF(rect.centerX() - fittedWidth / 2f, rect.top, rect.centerX() + fittedWidth / 2f, rect.bottom)
        }
        canvas.drawBitmap(source, null, destination, Paint(Paint.ANTI_ALIAS_FLAG))
    } else {
        drawText(canvas, "PHOTO", rect.centerX(), rect.centerY() + 14f, 42f, true, android.graphics.Color.WHITE, Paint.Align.CENTER)
    }

    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 8f
    paint.color = android.graphics.Color.argb(190, 255, 255, 255)
    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
}

private fun decodeUriBitmap(context: Context, uri: Uri): Bitmap? {
    return runCatching {
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
    }.getOrNull()
}

private fun drawText(
    canvas: Canvas,
    text: String,
    x: Float,
    baseline: Float,
    size: Float,
    bold: Boolean,
    color: Int,
    align: Paint.Align
) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = color
    paint.textSize = size
    paint.textAlign = align
    paint.typeface = if (bold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    canvas.drawText(text, x, baseline, paint)
}

private fun drawMultilineText(
    canvas: Canvas,
    text: String,
    x: Float,
    baseline: Float,
    size: Float,
    color: Int,
    align: Paint.Align,
    maxWidth: Float
) {
    drawMultilineText(canvas, text, x, baseline, size, false, color, align, maxWidth)
}

private fun drawMultilineText(
    canvas: Canvas,
    text: String,
    x: Float,
    baseline: Float,
    size: Float,
    bold: Boolean,
    color: Int,
    align: Paint.Align,
    maxWidth: Float
) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = color
    paint.textAlign = align
    paint.typeface = if (bold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    var effectiveSize = size
    var lines: List<String>
    do {
        paint.textSize = effectiveSize
        lines = wrapTextForPaint(text, paint, maxWidth)
        if (lines.size <= 5 && lines.none { paint.measureText(it) > maxWidth }) break
        effectiveSize -= 2f
    } while (effectiveSize >= size * 0.62f)

    val lineHeight = effectiveSize * 1.18f
    lines.take(5).forEachIndexed { index, value ->
        canvas.drawText(value, x, baseline + index * lineHeight, paint)
    }
}

private fun wrapTextForPaint(text: String, paint: Paint, maxWidth: Float): List<String> {
    val lines = mutableListOf<String>()
    text.split("\n").forEach { paragraph ->
        var line = ""
        paragraph.split(Regex("\\s+")).filter { it.isNotBlank() }.forEach { word ->
            val candidate = if (line.isBlank()) word else "$line $word"
            if (paint.measureText(candidate) > maxWidth && line.isNotBlank()) {
                lines += line.trim()
                line = word
            } else {
                line = candidate
            }
        }
        if (line.isNotBlank()) lines += line.trim()
    }
    return lines
}

fun saveBitmapToGallery(context: Context, bitmap: Bitmap, title: String): Uri {
    val resolver = context.contentResolver
    val safeTitle = title.replace(Regex("[^A-Za-z0-9_-]"), "_")
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "${safeTitle}_${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/PosterFlow")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        ?: error("Gallery insert failed")
    try {
        resolver.openOutputStream(uri)?.use { stream ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) { "Image encoding failed" }
        } ?: error("Gallery output stream failed")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
    } catch (error: Exception) {
        runCatching { resolver.delete(uri, null, null) }
        throw error
    }
    return uri
}

fun sharePosterBitmap(context: Context, bitmap: Bitmap, title: String) {
    val safeTitle = title.replace(Regex("[^A-Za-z0-9_-]"), "_").ifBlank { "poster" }
    val shareDir = File(context.cacheDir, "shared_posters").apply { mkdirs() }
    val shareFile = File(shareDir, "${safeTitle}_${System.currentTimeMillis()}.png")
    FileOutputStream(shareFile).use { stream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    }
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        shareFile
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share poster"))
}

private fun saveThumbnailToLocalStorage(context: Context, bitmap: Bitmap, title: String): String {
    val dir = File(context.filesDir, "poster_thumbnails").apply { mkdirs() }
    val safeTitle = title.replace(Regex("[^A-Za-z0-9_-]"), "_")
    val file = File(dir, "${safeTitle}_${System.currentTimeMillis()}.png")
    file.outputStream().use { stream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    }
    return file.absolutePath
}

private fun formatSavedDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(timestamp))
}

private fun formatSavedTime(timestamp: Long): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
}

private fun savedPosterImageModel(poster: Poster): Any? {
    return when {
        poster.galleryImageUri.isNotBlank() -> Uri.parse(poster.galleryImageUri)
        poster.thumbnailPath.isNotBlank() -> File(poster.thumbnailPath)
        else -> null
    }
}

private fun isSavedVideoDesign(poster: Poster): Boolean {
    return poster.backgroundType == "video" ||
        poster.title.startsWith("Video - ") ||
        poster.galleryImageUri.endsWith(".mp4", ignoreCase = true) ||
        poster.galleryImageUri.contains("video", ignoreCase = true)
}

private fun videoProjectMetadata(
    preset: Poster,
    form: CustomPosterState,
    videoState: VideoCustomizationState,
    videoUri: Uri,
    thumbnailPath: String,
    colorStyle: VideoColorStyle,
    backgroundStyle: VideoBackgroundStyle,
    layoutIndex: Int
): String {
    fun clean(value: String): String = value
        .replace("\\", "\\\\")
        .replace("|", "\\|")
        .replace("\n", "\\n")
    val pairs = listOf(
        "kind" to "video",
        "template" to preset.title,
        "category" to preset.category,
        "logo" to form.companyLogoUri.toString(),
        "photo" to form.profilePhotoUri.toString(),
        "company" to form.companyName,
        "website" to form.websiteName,
        "mobile" to form.mobileNumber,
        "name" to form.personName,
        "message" to form.wishesMessage,
        "music" to videoState.musicSelection,
        "musicResource" to (videoState.musicResourceName ?: ""),
        "musicPhone" to (videoState.musicPhoneUri?.toString() ?: ""),
        "musicStartSecond" to videoState.musicStartSecond.toString(),
        "animation" to videoState.animationStyle,
        "duration" to videoState.duration,
        "layoutIndex" to layoutIndex.toString(),
        "videoUri" to videoUri.toString(),
        "thumbnail" to thumbnailPath,
        "accent" to colorStyle.accentColor.toArgb().toString(),
        "backgroundMode" to backgroundStyle.mode,
        "backgroundGallery" to (backgroundStyle.galleryImageUri?.toString() ?: "")
    )
    return pairs.joinToString("|") { (key, value) -> "$key=${clean(value)}" }
}

private suspend fun saveGeneratedVideoDesignRecord(
    viewModel: PosterViewModel,
    preset: Poster?,
    category: String,
    form: CustomPosterState,
    videoState: VideoCustomizationState,
    videoUri: Uri,
    thumbnailPath: String,
    colorStyle: VideoColorStyle,
    backgroundStyle: VideoBackgroundStyle,
    layoutIndex: Int
): Poster {
    val videoCategory = category.ifBlank { preset?.category ?: "Video" }
    val basePoster = preset ?: Poster(
        title = videoState.title.ifBlank { defaultVideoTitle(videoCategory) },
        category = videoCategory,
        backgroundType = "video"
    )
    return viewModel.savePosterRecordNow(
        poster = basePoster.copy(
            title = "Video - ${basePoster.title.removePrefix("Video - ")}",
            category = videoCategory,
            backgroundType = "video",
            backgroundImageRes = videoProjectMetadata(
                preset = basePoster.copy(category = videoCategory),
                form = form,
                videoState = videoState,
                videoUri = videoUri,
                thumbnailPath = thumbnailPath,
                colorStyle = colorStyle,
                backgroundStyle = backgroundStyle,
                layoutIndex = layoutIndex
            )
        ),
        galleryImageUri = videoUri.toString(),
        thumbnailPath = thumbnailPath
    )
}

private fun savedVideoThumbnailModel(poster: Poster): Any? {
    val metadataThumbnail = savedVideoMetadata(poster)["thumbnail"].orEmpty()
    return when {
        poster.thumbnailPath.isNotBlank() && isUsableThumbnailFile(File(poster.thumbnailPath)) -> File(poster.thumbnailPath)
        metadataThumbnail.isNotBlank() && isUsableThumbnailFile(File(metadataThumbnail)) -> File(metadataThumbnail)
        else -> null
    }
}

private fun isUsableThumbnailFile(file: File): Boolean {
    if (!file.exists() || file.length() <= 0L) return false
    return runCatching {
        BitmapFactory.decodeFile(file.absolutePath)?.useForCheck { bitmap ->
            isUsableThumbnailFrame(bitmap)
        } ?: false
    }.getOrDefault(false)
}

private inline fun <T> Bitmap.useForCheck(block: (Bitmap) -> T): T {
    return try {
        block(this)
    } finally {
        recycle()
    }
}

private fun savedVideoUri(poster: Poster): Uri? {
    return poster.galleryImageUri.takeIf { it.isNotBlank() }?.let(Uri::parse)
        ?: savedVideoMetadata(poster)["videoUri"].orEmpty().takeIf { it.isNotBlank() }?.let(Uri::parse)
}

private fun canReadSavedVideo(context: Context, uri: Uri?): Boolean {
    if (uri == null) return false
    return when (uri.scheme) {
        "file" -> uri.path?.let { File(it).exists() && File(it).length() > 0L } == true
        "content" -> runCatching {
            context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { descriptor ->
                descriptor.length != 0L
            } == true
        }.getOrDefault(false)
        else -> false
    }
}

private fun shouldRequestVideoReadPermission(context: Context, uri: Uri?): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || uri?.scheme != "content") return false
    if (uri.authority == "${context.packageName}.fileprovider") return false
    return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED
}

private fun savedVideoHasAudioTrack(context: Context, uri: Uri?): Boolean {
    if (uri == null) return false
    val extractor = MediaExtractor()
    return try {
        when (uri.scheme) {
            "file" -> extractor.setDataSource(uri.path.orEmpty())
            else -> extractor.setDataSource(context, uri, null)
        }
        (0 until extractor.trackCount).any { index ->
            extractor.getTrackFormat(index)
                .getString(MediaFormat.KEY_MIME)
                ?.startsWith("audio/") == true
        }
    } catch (_: Exception) {
        false
    } finally {
        extractor.release()
    }
}

private fun savedVideoTemplateName(poster: Poster): String {
    return poster.title.removePrefix("Video - ").ifBlank { poster.title }
}

private fun savedVideoMetadata(poster: Poster): Map<String, String> {
    if (poster.backgroundType != "video" || poster.backgroundImageRes.isBlank()) return emptyMap()
    val result = linkedMapOf<String, String>()
    val parts = mutableListOf<String>()
    val builder = StringBuilder()
    var escaped = false
    poster.backgroundImageRes.forEach { char ->
        when {
            escaped -> {
                builder.append(
                    when (char) {
                        'n' -> '\n'
                        else -> char
                    }
                )
                escaped = false
            }
            char == '\\' -> escaped = true
            char == '|' -> {
                parts += builder.toString()
                builder.clear()
            }
            else -> builder.append(char)
        }
    }
    parts += builder.toString()
    parts.forEach { part ->
        val index = part.indexOf('=')
        if (index > 0) result[part.substring(0, index)] = part.substring(index + 1)
    }
    return result
}

private suspend fun repairSavedVideoThumbnail(context: Context, poster: Poster): String? = withContext(Dispatchers.IO) {
    if (savedVideoThumbnailModel(poster) != null) return@withContext null
    savedVideoUri(poster)?.let { uri ->
        saveVideoFirstFrameThumbnail(
            context = context,
            videoUri = uri,
            title = savedVideoTemplateName(poster),
            fallbackBitmap = null
        )
    }
}

private fun customPosterStateFromVideoMetadata(metadata: Map<String, String>): CustomPosterState {
    return CustomPosterState(
        companyLogoUri = metadata["logo"].toUriOrNull(),
        profilePhotoUri = metadata["photo"].toUriOrNull(),
        companyName = metadata["company"].orEmpty().ifBlank { "ABC Marketing Pvt Ltd" },
        websiteName = metadata["website"].orEmpty().ifBlank { "www.abcmarketing.com" },
        mobileNumber = metadata["mobile"].orEmpty(),
        personName = metadata["name"].orEmpty().ifBlank { "Person Name" },
        wishesMessage = metadata["message"].orEmpty().ifBlank { "Wishing you success and prosperity." }
    )
}

private fun String?.toUriOrNull(): Uri? {
    val value = this?.takeIf { it.isNotBlank() && it != "null" } ?: return null
    return runCatching { Uri.parse(value) }.getOrNull()
}

private fun videoColorStyleFromMetadata(metadata: Map<String, String>): VideoColorStyle {
    val accent = metadata["accent"]?.toIntOrNull()?.let { Color(it) } ?: defaultVideoColorStyle.accentColor
    return defaultVideoColorStyle.copy(
        accentColor = accent,
        borderColor = accent,
        footerColor = accent
    )
}

private fun videoBackgroundStyleFromMetadata(metadata: Map<String, String>): VideoBackgroundStyle {
    return VideoBackgroundStyle(
        mode = metadata["backgroundMode"].orEmpty().ifBlank { "Template" },
        galleryImageUri = metadata["backgroundGallery"].toUriOrNull()
    )
}

private fun createSavedVideoMusicPlayer(context: Context, poster: Poster): MediaPlayer? {
    val metadata = savedVideoMetadata(poster)
    val resourceName = metadata["musicResource"].orEmpty().ifBlank { null }
    val phoneUri = metadata["musicPhone"].orEmpty().ifBlank { null }?.let(Uri::parse)
    val startSecond = metadata["musicStartSecond"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val player = runCatching {
        when {
            resourceName != null -> {
                val id = context.resources.getIdentifier(resourceName, "raw", context.packageName)
                if (id == 0) null else MediaPlayer.create(context, id)
            }
            phoneUri != null -> MediaPlayer.create(context, phoneUri)
            else -> null
        }
    }.getOrNull()
    player?.runCatching {
        seekTo(startSecond * 1000)
        isLooping = false
    }
    return player
}

private suspend fun saveVideoFirstFrameThumbnail(
    context: Context,
    videoUri: Uri,
    title: String,
    fallbackBitmap: Bitmap? = null
): String = withContext(Dispatchers.IO) {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, videoUri)
        val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull()
            ?.coerceAtLeast(1L)
            ?: 1L
        var frame: Bitmap? = null
        listOf(
            500_000L,
            1_000_000L,
            2_000_000L,
            durationMs * 400L,
            durationMs * 700L
        ).map { it.coerceAtMost(durationMs * 1000L - 1L).coerceAtLeast(0L) }.forEach { timeUs ->
            if (frame == null) {
                val candidate = runCatching {
                    retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)
                }.getOrNull()
                if (candidate != null) {
                    if (isUsableThumbnailFrame(candidate)) {
                        frame = candidate
                    } else {
                        candidate.recycle()
                    }
                }
            }
        }
        val thumbnailBitmap = frame ?: fallbackBitmap ?: createVideoThumbnailFallback(title)
        saveThumbnailToLocalStorage(context, thumbnailBitmap, "Video_$title").also {
            if (thumbnailBitmap !== fallbackBitmap) {
                thumbnailBitmap.recycle()
            }
        }
    } finally {
        retriever.release()
    }
}

private fun isUsableThumbnailFrame(bitmap: Bitmap): Boolean {
    if (bitmap.width < 8 || bitmap.height < 8) return false
    var minLuma = 255
    var maxLuma = 0
    var opaqueSamples = 0
    val uniqueBuckets = mutableSetOf<Int>()
    val xStep = (bitmap.width / 10).coerceAtLeast(1)
    val yStep = (bitmap.height / 10).coerceAtLeast(1)
    var y = yStep / 2
    while (y < bitmap.height) {
        var x = xStep / 2
        while (x < bitmap.width) {
            val pixel = bitmap.getPixel(x, y)
            val alpha = android.graphics.Color.alpha(pixel)
            if (alpha > 20) {
                val red = android.graphics.Color.red(pixel)
                val green = android.graphics.Color.green(pixel)
                val blue = android.graphics.Color.blue(pixel)
                val luma = ((red * 299) + (green * 587) + (blue * 114)) / 1000
                minLuma = minOf(minLuma, luma)
                maxLuma = maxOf(maxLuma, luma)
                opaqueSamples++
                uniqueBuckets += ((red / 32) shl 6) or ((green / 32) shl 3) or (blue / 32)
            }
            x += xStep
        }
        y += yStep
    }
    return opaqueSamples > 12 && (maxLuma - minLuma) > 18 && uniqueBuckets.size > 4
}

private fun createVideoThumbnailFallback(title: String): Bitmap {
    val bitmap = Bitmap.createBitmap(540, 960, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.shader = LinearGradient(
        0f,
        0f,
        bitmap.width.toFloat(),
        bitmap.height.toFloat(),
        android.graphics.Color.rgb(79, 70, 229),
        android.graphics.Color.rgb(236, 72, 153),
        Shader.TileMode.CLAMP
    )
    canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
    paint.shader = null
    paint.color = android.graphics.Color.argb(48, 255, 255, 255)
    canvas.drawOval(RectF(310f, -70f, 640f, 260f), paint)
    canvas.drawOval(RectF(-120f, 660f, 180f, 960f), paint)
    paint.color = android.graphics.Color.WHITE
    paint.textAlign = Paint.Align.CENTER
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 40f
    canvas.drawText(title.ifBlank { "Generated Video" }, bitmap.width / 2f, bitmap.height / 2f, paint)
    return bitmap
}

private fun videoDurationLabel(context: Context, videoUri: Uri?): String {
    if (videoUri == null) return "15 Seconds"
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, videoUri)
        val millis = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        val totalSeconds = (millis / 1000).toInt().coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        "%d:%02d".format(minutes, seconds)
    } catch (_: Exception) {
        "15 Seconds"
    } finally {
        retriever.release()
    }
}

private fun shareVideo(context: Context, poster: Poster) {
    savedVideoUri(poster)?.let { context.shareGeneratedVideo(it) }
}

private fun shareSavedPoster(context: Context, poster: Poster) {
    val uri = poster.galleryImageUri.takeIf { it.isNotBlank() }?.let(Uri::parse)
        ?: poster.thumbnailPath.takeIf { it.isNotBlank() && File(it).exists() }?.let {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(it))
        }
    uri?.let {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, it)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share poster"))
    }
}

private fun deleteSavedPosterAssets(context: Context, poster: Poster) {
    if (poster.galleryImageUri.isNotBlank()) {
        runCatching {
            val uri = Uri.parse(poster.galleryImageUri)
            if (uri.authority == "${context.packageName}.fileprovider") {
                deleteFileProviderGeneratedVideo(context, uri)
            } else {
                context.contentResolver.delete(uri, null, null)
            }
        }
    }
    if (poster.thumbnailPath.isNotBlank()) {
        runCatching {
            File(poster.thumbnailPath).delete()
        }
    }
}

private fun deleteFileProviderGeneratedVideo(context: Context, uri: Uri) {
    val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: return
    File(File(context.filesDir, "generated_videos"), fileName).delete()
}

private fun categorySpecFor(category: String): CategorySpec {
    return dashboardCategories.firstOrNull { it.name == category }
        ?: CategorySpec(category, Icons.Default.GridView, listOf(Color(0xFF7C3AED), Color(0xFF4F46E5)))
}

@Composable
private fun EditorTab(
    activePoster: Poster,
    selectedElementId: String?,
    viewModel: PosterViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("Dashboard")
            }
            Button(onClick = onSave, shape = RoundedCornerShape(16.dp)) {
                Text("Save Design")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Editing: ${activePoster.title}",
            color = appTextPrimary(),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(3f / 4f)
                .shadow(14.dp, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(posterColor(activePoster.backgroundColorHex))
                    .clickable { viewModel.selectElement(null) }
            ) {
                MiniPosterCanvas(poster = activePoster)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (selectedElementId == null) "Select a poster element to edit." else "Element selected.",
            color = appTextMuted(),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun SavedPostersTab(
    viewModel: PosterViewModel,
    onProfile: () -> Unit,
    savedPosters: List<Poster>,
    profileSettings: ProfileSettings,
    onViewingPosterChanged: (Boolean) -> Unit,
    onUpdatePoster: (Poster) -> Unit,
    onDeletePoster: (Poster) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedPoster by remember { mutableStateOf<Poster?>(null) }
    var editingWelcome by remember { mutableStateOf<Poster?>(null) }
    var pendingDelete by remember { mutableStateOf<Poster?>(null) }
    var selectedVideo by remember { mutableStateOf<Poster?>(null) }
    var renameTarget by remember { mutableStateOf<Poster?>(null) }
    var renameText by remember { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf("All") }

    LaunchedEffect(selectedPoster, selectedVideo, editingWelcome) {
        onViewingPosterChanged(selectedPoster != null || selectedVideo != null || editingWelcome != null)
    }

    editingWelcome?.let { poster ->
        val project = WelcomeProjectStateStore.decode(poster.backgroundImageRes)
        if (project != null) {
            WelcomePosterEditor(
                preset = poster,
                profile = profileSettings,
                savedState = project,
                onBack = { editingWelcome = null },
                onSave = { updatedProject, bitmap ->
                    coroutineScope.launch {
                        runCatching {
                            updateWelcomePosterProject(context, poster, updatedProject, bitmap)
                        }.onSuccess { updated ->
                            onUpdatePoster(updated)
                            editingWelcome = updated
                        }
                    }
                },
                onShare = { bitmap -> sharePosterBitmap(context, bitmap, poster.title) },
                onCreateVideo = { bitmap ->
                    coroutineScope.launch {
                        viewModelStatusVideoExport(
                            context = context,
                            bitmap = bitmap,
                            title = poster.title
                        )
                    }
                }
            )
            return
        }
        editingWelcome = null
    }

    selectedVideo?.let { poster ->
        SavedVideoPreviewScreen(
            poster = poster,
            onBack = { selectedVideo = null },
            onDelete = {
                deleteSavedPosterAssets(context, poster)
                onDeletePoster(poster)
                selectedVideo = null
            }
        )
        return
    }

    selectedPoster?.let { poster ->
        val document = TemplateJson.design(poster.backgroundImageRes)
        if (document != null) {
            ReadyPosterCustomize(document, viewModel, profileSettings, existing = poster,
                onUpdated = { selectedPoster = it }, onBack = { selectedPoster = null }, onProfile = onProfile)
            return
        }
        SavedPosterViewScreen(
            poster = poster,
            onBack = { selectedPoster = null }
        )
        return
    }

    pendingDelete?.let { poster ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            containerColor = appGlassSurfaceStrong(),
            titleContentColor = appTextPrimary(),
            textContentColor = appTextMuted(),
            title = { Text(if (isSavedVideoDesign(poster)) "Delete Video?" else "Delete Poster?") },
            text = { Text(if (isSavedVideoDesign(poster)) "This video will be removed from My Designs." else "This poster will be removed from My Designs.") },
            confirmButton = {
                Button(
                    onClick = {
                        deleteSavedPosterAssets(context, poster)
                        onDeletePoster(poster)
                        pendingDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D67))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    renameTarget?.let { poster ->
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            containerColor = appGlassSurfaceStrong(),
            titleContentColor = appTextPrimary(),
            textContentColor = appTextMuted(),
            title = { Text(if (isSavedVideoDesign(poster)) "Rename Video" else "Rename Poster") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    label = { Text(if (isSavedVideoDesign(poster)) "Video name" else "Poster name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newName = renameText.trim().ifBlank {
                            if (isSavedVideoDesign(poster)) savedVideoTemplateName(poster) else poster.title
                        }
                        onUpdatePoster(
                            if (isSavedVideoDesign(poster)) {
                                poster.copy(title = "Video - $newName")
                            } else {
                                poster.copy(title = newName)
                            }
                        )
                        renameTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { renameTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    val filters = listOf("All", "Posters", "Videos", "Birthday", "Welcome", "Achievement", "Income", "Festival", "Motivation")
    val filteredDesigns = remember(savedPosters, selectedFilter) {
        savedPosters.filter { poster ->
            val isVideo = isSavedVideoDesign(poster)
            when (selectedFilter) {
                "All" -> true
                "Posters" -> !isVideo
                "Videos" -> isVideo
                else -> poster.category.equals(selectedFilter, ignoreCase = true)
            }
        }
    }
    val videos = filteredDesigns.filter(::isSavedVideoDesign)
    val posters = filteredDesigns.filterNot(::isSavedVideoDesign)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            MyDesignsHeader(totalCount = savedPosters.size)
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }
        }
        if (savedPosters.isEmpty()) {
            item {
                MyDesignsEmptyState()
            }
        } else if (filteredDesigns.isEmpty()) {
            item {
                if (selectedFilter == "Posters") {
                    MyDesignsEmptyState(
                        title = "No Posters Yet",
                        subtitle = "Create your first poster."
                    )
                } else {
                    MyDesignsEmptyState(
                        title = "No Matching Designs",
                        subtitle = "Try a different filter."
                    )
                }
            }
        } else {
            if (videos.isNotEmpty()) {
                item { MyDesignsSectionTitle("My Videos", videos.size) }
                items(videos, key = { "video_${it.id}_${it.timestamp}" }) { poster ->
                    LaunchedEffect(poster.id, poster.thumbnailPath, poster.backgroundImageRes) {
                        if (savedVideoThumbnailModel(poster) == null) {
                            repairSavedVideoThumbnail(context, poster)?.let { repairedPath ->
                                onUpdatePoster(poster.copy(thumbnailPath = repairedPath))
                            }
                        }
                    }
                    PremiumSavedVideoCard(
                        poster = poster,
                        onOpen = { selectedVideo = poster },
                        onPlay = { selectedVideo = poster },
                        onShare = { shareVideo(context, poster) },
                        onRename = {
                            renameTarget = poster
                            renameText = savedVideoTemplateName(poster)
                        },
                        onDelete = { pendingDelete = poster }
                    )
                }
            }
            if (posters.isNotEmpty()) {
                item { MyDesignsSectionTitle("My Posters", posters.size) }
                items(posters, key = { "poster_${it.id}_${it.timestamp}" }) { poster ->
                    PremiumSavedPosterCard(
                        poster = poster,
                        onOpen = {
                            // Legacy free-editor documents remain viewable/shareable, not freely editable.
                            selectedPoster = poster
                        },
                        onShare = { shareSavedPoster(context, poster) },
                        onRename = {
                            renameTarget = poster
                            renameText = poster.title
                        },
                        onDelete = { pendingDelete = poster }
                    )
                }
            }
        }
    }
}

@Composable
private fun MyDesignsHeader(totalCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "My Designs",
                color = appTextPrimary(),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )
            Text(
                text = "$totalCount saved ${if (totalCount == 1) "design" else "designs"}",
                color = appTextMuted(),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Surface(
            shape = CircleShape,
            color = Color(0xFF6D5DF6).copy(alpha = 0.16f),
            border = BorderStroke(1.dp, Color(0xFF6D5DF6).copy(alpha = 0.25f))
        ) {
            Icon(
                imageVector = Icons.Default.FolderSpecial,
                contentDescription = null,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.padding(12.dp).size(22.dp)
            )
        }
    }
}

@Composable
private fun MyDesignsSectionTitle(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = appTextPrimary(),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
        )
        Text(
            text = count.toString(),
            color = appTextMuted(),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(appGlassSurfaceStrong())
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun MyDesignsEmptyState(
    title: String = "No Designs Yet",
    subtitle: String = "Create your first poster or video."
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 34.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF6D5DF6), Color(0xFFEC4899)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(46.dp)
                )
            }
            Text(
                text = title,
                color = appTextPrimary(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                color = appTextMuted(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PremiumSavedVideoCard(
    poster: Poster,
    onOpen: () -> Unit,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.09f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(if (TemplateJson.design(poster.backgroundImageRes) != null) 1f else 9f / 16f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black)
            ) {
                val model = remember(poster.thumbnailPath, poster.backgroundImageRes) {
                    savedVideoThumbnailModel(poster)
                }
                if (model != null) {
                    AsyncImage(
                        model = model,
                        contentDescription = poster.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    SavedDesignUnavailablePlaceholder(
                        title = "Preview unavailable",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))))
                )
                IconButton(
                    onClick = onPlay,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.42f))
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(text = { Text("Play") }, onClick = { menuExpanded = false; onPlay() }, leadingIcon = { Icon(Icons.Default.PlayArrow, null) })
                        DropdownMenuItem(text = { Text("Share") }, onClick = { menuExpanded = false; onShare() }, leadingIcon = { Icon(Icons.Default.Share, null) })
                        DropdownMenuItem(text = { Text("Rename") }, onClick = { menuExpanded = false; onRename() }, leadingIcon = { Icon(Icons.Default.TextFields, null) })
                        DropdownMenuItem(text = { Text("Delete") }, onClick = { menuExpanded = false; onDelete() }, leadingIcon = { Icon(Icons.Default.Delete, null) })
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = savedVideoTemplateName(poster),
                        color = appTextPrimary(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = poster.category,
                        color = Color(0xFFBFA7FF),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatSavedDate(poster.timestamp),
                        color = appTextMuted(),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = videoDurationLabel(context, savedVideoUri(poster)),
                    color = appTextPrimary(),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun PremiumSavedPosterCard(
    poster: Poster,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.09f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(112.dp)
                    .aspectRatio(if (TemplateJson.design(poster.backgroundImageRes) != null) 1f else if (WelcomeProjectStateStore.isProject(poster.backgroundImageRes)) 4f / 5f else 9f / 16f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.08f))
            ) {
                val imageModel = savedPosterImageModel(poster)
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = poster.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    SavedDesignUnavailablePlaceholder(
                        title = "Preview unavailable",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = poster.title,
                    color = appTextPrimary(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = poster.category,
                    color = Color(0xFFBFA7FF),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatSavedDate(poster.timestamp),
                    color = appTextMuted(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = appTextMuted())
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(text = { Text("View") }, onClick = { menuExpanded = false; onOpen() }, leadingIcon = { Icon(Icons.AutoMirrored.Filled.OpenInNew, null) })
                    DropdownMenuItem(text = { Text("Share") }, onClick = { menuExpanded = false; onShare() }, leadingIcon = { Icon(Icons.Default.Share, null) })
                    DropdownMenuItem(text = { Text("Rename") }, onClick = { menuExpanded = false; onRename() }, leadingIcon = { Icon(Icons.Default.TextFields, null) })
                    DropdownMenuItem(text = { Text("Delete") }, onClick = { menuExpanded = false; onDelete() }, leadingIcon = { Icon(Icons.Default.Delete, null) })
                }
            }
        }
    }
}

@Composable
private fun SavedDesignUnavailablePlaceholder(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color.Black.copy(alpha = 0.78f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.74f),
                modifier = Modifier.size(34.dp)
            )
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SavedVideoPreviewScreen(
    poster: Poster,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val uri = savedVideoUri(poster)
    val initialThumbnailModel = remember(poster.thumbnailPath, poster.backgroundImageRes) {
        savedVideoThumbnailModel(poster)
    }
    var repairedThumbnailPath by remember(poster.id, poster.galleryImageUri) { mutableStateOf<String?>(null) }
    val thumbnailModel = repairedThumbnailPath?.let { File(it) } ?: initialThumbnailModel
    var permissionRefreshKey by remember { mutableStateOf(0) }
    var playerReady by remember(uri) { mutableStateOf(false) }
    var playerError by remember(uri) { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionRefreshKey++
    }
    val canReadVideo = remember(uri, permissionRefreshKey) { canReadSavedVideo(context, uri) }
    val savedMusicMetadata = remember(poster.backgroundImageRes) { savedVideoMetadata(poster) }
    val hasSelectedMusic = savedMusicMetadata["musicResource"].orEmpty().isNotBlank() ||
        savedMusicMetadata["musicPhone"].orEmpty().isNotBlank()
    val hasEmbeddedAudio = remember(uri, canReadVideo) {
        canReadVideo && savedVideoHasAudioTrack(context, uri)
    }
    val externalMusicPlayer = remember(poster.id, hasSelectedMusic, hasEmbeddedAudio) {
        if (hasSelectedMusic && !hasEmbeddedAudio) createSavedVideoMusicPlayer(context, poster) else null
    }

    LaunchedEffect(uri, canReadVideo) {
        if (!canReadVideo && shouldRequestVideoReadPermission(context, uri)) {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
        }
        if (thumbnailModel == null && uri != null && canReadVideo) {
            repairedThumbnailPath = runCatching {
                saveVideoFirstFrameThumbnail(
                    context = context,
                    videoUri = uri,
                    title = savedVideoTemplateName(poster),
                    fallbackBitmap = null
                )
            }.getOrNull()
        }
    }

    val player = remember(uri, canReadVideo) {
        if (uri == null || !canReadVideo) {
            null
        } else {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(uri))
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            playerReady = true
                        }
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        playerError = error.message ?: "Unable to play this saved video."
                    }
                })
                prepare()
            }
        }
    }

    DisposableEffect(player) {
        onDispose { player?.release() }
    }

    DisposableEffect(player, externalMusicPlayer) {
        val videoPlayer = player
        val musicPlayer = externalMusicPlayer
        if (videoPlayer == null || musicPlayer == null) {
            onDispose {
                musicPlayer?.release()
            }
        } else {
            val musicStartMs = (savedMusicMetadata["musicStartSecond"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0) * 1000
            fun syncMusicToVideo(shouldPlay: Boolean) {
                runCatching {
                    if (shouldPlay) {
                        val target = musicStartMs + videoPlayer.currentPosition.toInt().coerceAtLeast(0)
                        musicPlayer.seekTo(target)
                        musicPlayer.start()
                    } else if (musicPlayer.isPlaying) {
                        musicPlayer.pause()
                    }
                }
            }
            val listener = object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    syncMusicToVideo(isPlaying)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                        runCatching {
                            if (musicPlayer.isPlaying) musicPlayer.pause()
                            musicPlayer.seekTo(musicStartMs)
                        }
                    }
                }
            }
            videoPlayer.addListener(listener)
            syncMusicToVideo(videoPlayer.isPlaying)
            onDispose {
                videoPlayer.removeListener(listener)
                runCatching { musicPlayer.stop() }
                musicPlayer.release()
            }
        }
    }

    BackHandler(onBack = onBack)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = appTextPrimary())
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(savedVideoTemplateName(poster), color = appTextPrimary(), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${poster.category} • ${formatSavedDate(poster.timestamp)} • ${formatSavedTime(poster.timestamp)}", color = appTextMuted(), style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = { shareVideo(context, poster) }) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFFEC4899))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF6B6B))
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth().aspectRatio(if (TemplateJson.design(poster.backgroundImageRes) != null) 1f else 9f / 16f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when {
                    uri == null -> {
                        SavedDesignUnavailablePlaceholder(
                            title = "Saved video path is missing",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    !canReadVideo -> {
                        SavedDesignUnavailablePlaceholder(
                            title = "Saved video file is missing or cannot be opened",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    playerError != null -> {
                        SavedDesignUnavailablePlaceholder(
                            title = playerError ?: "Unable to play saved video",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { viewContext ->
                                PlayerView(viewContext).apply {
                                    useController = true
                                    this.player = player
                                }
                            },
                            update = { view ->
                                view.player = player
                            }
                        )
                    }
                }
                if (!playerReady && thumbnailModel != null && canReadVideo && playerError == null) {
                    Box(Modifier.matchParentSize().background(Color.Black), contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = thumbnailModel,
                            contentDescription = savedVideoTemplateName(poster),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedPosterViewScreen(
    poster: Poster,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = appTextPrimary()
                )
            }
            Column {
                Text(
                    text = poster.title,
                    color = appTextPrimary(),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${poster.category} - ${formatSavedDate(poster.timestamp)}",
                    color = appTextMuted(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9f / 16f)
                    .shadow(18.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
                border = BorderStroke(1.dp, appBorderColor(0.08f))
            ) {
                val imageModel: Any? = savedPosterImageModel(poster)
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = poster.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    )
                } else {
                    SavedDesignUnavailablePlaceholder(
                        title = "Poster preview unavailable",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileTab(
    viewModel: PosterViewModel,
    isAdmin: Boolean,
    onManageTemplates: () -> Unit,
    profileSettings: ProfileSettings,
    onCompanyLogoSelected: (String) -> Unit,
    onCompanyNameChange: (String) -> Unit,
    onWebsiteNameChange: (String) -> Unit,
    onThemeModeChange: (String) -> Unit,
    onNotificationChange: (String, Boolean) -> Unit,
    onShowMessage: (String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionTitle("Profile")
        }
        item {
            ProfileHeaderCard(profileSettings)
        }
        item {
            CompanyInformationCard(
                profileSettings = profileSettings,
                onCompanyLogoSelected = onCompanyLogoSelected,
                onCompanyNameChange = onCompanyNameChange,
                onWebsiteNameChange = onWebsiteNameChange
            )
        }
        item {
            SettingsCard(
                profileSettings = profileSettings,
                onThemeModeChange = onThemeModeChange,
                onNotificationChange = onNotificationChange
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Business contact details", style = MaterialTheme.typography.titleMedium)
                listOf("Phone" to profileSettings.mobileNumber, "Business email" to profileSettings.businessEmail,
                    "Address" to profileSettings.businessAddress, "Tagline" to profileSettings.tagline).forEach { (label, value) ->
                    OutlinedTextField(value, { viewModel.updateBusinessContact(label, it.take(160)) }, label = { Text(label) }, modifier = Modifier.fillMaxWidth())
                }
                Text("Saved automatically. Your templates use these details.", style = MaterialTheme.typography.bodySmall)
            }
        }
        if (isAdmin) item {
            Button(onClick = onManageTemplates, modifier = Modifier.fillMaxWidth()) { Text("Template Management") }
        }
        item {
            SupportCard(
                onSupportClick = { action ->
                    when (action) {
                        "Contact Us" -> openSupportEmail(context, onShowMessage)
                        "Rate App" -> openPlayStore(context, onShowMessage)
                        "Share App" -> shareApp(context, onShowMessage)
                    }
                }
            )
        }
        item {
            AboutAppCard()
        }
        item {
            LogoutCard(onLogout = { showLogoutDialog = true })
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to logout?",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Logout", color = Color(0xFFE54865), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun LogoutCard(onLogout: () -> Unit) {
    ProfileSectionCard(
        title = "Account",
        icon = Icons.Default.Logout
    ) {
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE54865))
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }
    }
}

@Composable
private fun ProfileHeaderCard(profileSettings: ProfileSettings) {
    ProfileSectionCard(
        title = "Personal Information",
        icon = Icons.Default.Person
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF22D3EE)))),
                contentAlignment = Alignment.Center
            ) {
                val imageModel = profileSettings.profilePhotoUri.takeIf { it.isNotBlank() }?.let(Uri::parse)
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(42.dp))
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = profileSettings.userName,
                    color = appTextPrimary(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = profileSettings.userEmail,
                    color = appTextMuted(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun CompanyInformationCard(
    profileSettings: ProfileSettings,
    onCompanyLogoSelected: (String) -> Unit,
    onCompanyNameChange: (String) -> Unit,
    onWebsiteNameChange: (String) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onCompanyLogoSelected(it.toString()) }
    }

    ProfileSectionCard(
        title = "Company Information",
        icon = Icons.Default.Business
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val logoUri = profileSettings.companyLogoUri.takeIf { it.isNotBlank() }?.let(Uri::parse)
                if (logoUri != null) {
                    AsyncImage(
                        model = if (profileSettings.companyLogoUri.startsWith("/")) File(profileSettings.companyLogoUri) else logoUri,
                        contentDescription = "Company Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFFBFA7FF), modifier = Modifier.size(30.dp))
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Company Logo Upload", color = appTextPrimary(), fontWeight = FontWeight.Bold)
                Text("Auto-filled in poster forms", color = appTextMuted(), style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = { launcher.launch("image/*") },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D5DF6))
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        PosterTextField(
            value = profileSettings.companyName,
            onValueChange = onCompanyNameChange,
            label = "Company Name",
            leadingIcon = Icons.Default.Business
        )
        Spacer(modifier = Modifier.height(12.dp))
        PosterTextField(
            value = profileSettings.websiteName,
            onValueChange = onWebsiteNameChange,
            label = "Website Name",
            leadingIcon = Icons.Default.Language
        )
    }
}

@Composable
private fun SettingsCard(
    profileSettings: ProfileSettings,
    onThemeModeChange: (String) -> Unit,
    onNotificationChange: (String, Boolean) -> Unit
) {
    ProfileSectionCard(
        title = "Settings",
        icon = Icons.Default.Palette
    ) {
        SettingOptionGroup(
            title = "Dark Mode",
            icon = Icons.Default.Palette,
            options = listOf(ThemeManager.LIGHT, ThemeManager.DARK, ThemeManager.SYSTEM_DEFAULT),
            selected = profileSettings.themeMode,
            onSelected = onThemeModeChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
        Spacer(modifier = Modifier.height(10.dp))
        NotificationSwitchRow(
            title = "New Template Notifications",
            checked = profileSettings.newTemplateNotifications,
            onCheckedChange = { onNotificationChange("new_templates", it) }
        )
        NotificationSwitchRow(
            title = "Festival Template Updates",
            checked = profileSettings.festivalTemplateUpdates,
            onCheckedChange = { onNotificationChange("festival_updates", it) }
        )
        NotificationSwitchRow(
            title = "App Updates",
            checked = profileSettings.appUpdates,
            onCheckedChange = { onNotificationChange("app_updates", it) }
        )
    }
}

@Composable
private fun SettingOptionGroup(
    title: String,
    icon: ImageVector,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFBFA7FF), modifier = Modifier.size(20.dp))
            Text(title, color = appTextPrimary(), fontWeight = FontWeight.Bold)
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(options) { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelected(option) },
                    label = { Text(option) }
                )
            }
        }
    }
}

@Composable
private fun NotificationSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFBFA7FF), modifier = Modifier.size(20.dp))
        Text(
            text = title,
            color = appTextPrimary(),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SupportCard(onSupportClick: (String) -> Unit) {
    ProfileSectionCard(
        title = "Support",
        icon = Icons.Default.Phone
    ) {
        SupportRow("Contact Us", Icons.Default.Phone, onSupportClick)
        SupportRow("Rate App", Icons.Default.WorkspacePremium, onSupportClick)
        SupportRow("Share App", Icons.AutoMirrored.Filled.OpenInNew, onSupportClick)
    }
}

@Composable
private fun SupportRow(
    label: String,
    icon: ImageVector,
    onSupportClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSupportClick(label) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFBFA7FF), modifier = Modifier.size(22.dp))
        Text(label, color = appTextPrimary(), modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = appTextMuted(), modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun AboutAppCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.08f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFBFA7FF), modifier = Modifier.size(22.dp))
                Text("About App", color = appTextPrimary(), fontWeight = FontWeight.ExtraBold)
            }
            BrandWordmark(
                logoSize = 22.dp,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp
            )
            Text("Version 1.0", color = appTextMuted(), style = MaterialTheme.typography.bodyMedium)
            Text("Developer Information", color = appTextMuted(), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurfaceStrong()),
        border = BorderStroke(1.dp, appBorderColor(0.08f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF6D5DF6).copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color(0xFFBFA7FF), modifier = Modifier.size(20.dp))
                }
                Text(
                    text = title,
                    color = appTextPrimary(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
            content()
        }
    }
}

private fun openSupportEmail(context: Context, onError: (String) -> Unit) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:support@yourapp.com")
        putExtra(Intent.EXTRA_SUBJECT, "PosterFlow Support Request")
    }
    runCatching {
        context.startActivity(intent)
    }.onFailure {
        onError("No email app found")
    }
}

private fun openPlayStore(context: Context, onError: (String) -> Unit) {
    val appPackage = context.packageName
    val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackage"))
    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackage"))
    runCatching {
        if (marketIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(marketIntent)
        } else if (webIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(webIntent)
        } else {
            onError("Coming Soon on Play Store")
        }
    }.onFailure {
        onError("Coming Soon on Play Store")
    }
}

private fun shareApp(context: Context, onError: (String) -> Unit) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "Check out PosterFlow for creating professional business and MLM posters."
        )
    }
    runCatching {
        context.startActivity(Intent.createChooser(intent, "Share App"))
    }.onFailure {
        onError("Unable to open share sheet")
    }
}

@Composable
private fun PremiumBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.Transparent
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(appGlassSurface()),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            bottomTabs.forEach { tab ->
                PremiumNavItem(
                    tab = tab,
                    selected = currentTab == tab.key,
                    onClick = { onTabSelected(tab.key) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_${tab.key}")
                )
            }
        }
    }
}

@Composable
private fun PremiumNavItem(
    tab: BottomTabSpec,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(top = 5.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .height(30.dp)
                .width(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (selected) {
                        Color(0xFF6D5DF6).copy(alpha = if (appIsDarkTheme()) 0.22f else 0.14f)
                    } else {
                        Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                contentDescription = tab.label,
                tint = if (selected) Color(0xFF6D5DF6) else appTextMuted(),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (tab.key == "designs") "My\nDesigns" else tab.label,
            color = if (selected) Color(0xFF6D5DF6) else appTextMuted(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp,
                lineHeight = 10.sp
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = appTextPrimary(),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
        )
        if (action != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(action)
            }
        }
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = appGlassSurface()),
        border = BorderStroke(1.dp, appBorderColor(0.09f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Composable
private fun MiniPosterCanvas(poster: Poster) {
    val gradient = dashboardCategories.firstOrNull { it.name == poster.category }?.colors
        ?: listOf(posterColor(poster.backgroundColorHex), Color(0xFF202334))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(gradient))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.14f))
        )
        Text(
            text = poster.category.uppercase(),
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = poster.title,
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun darkFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = appTextPrimary(),
    unfocusedTextColor = appTextPrimary(),
    focusedBorderColor = Color(0xFF6D5DF6).copy(alpha = 0.8f),
    unfocusedBorderColor = appBorderColor(0.08f),
    cursorColor = Color(0xFF6D5DF6),
    focusedContainerColor = appGlassSurface(),
    unfocusedContainerColor = appGlassSurface()
)

private fun posterColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color(0xFF1B1E2A)
    }
}
