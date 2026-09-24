package com.example.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import androidx.core.content.FileProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.util.Locale
import kotlin.math.roundToLong

internal data class VideoCustomizationState(
    val companyLogoUri: Uri? = null,
    val companyName: String = "",
    val website: String = "",
    val profilePhotoUri: Uri? = null,
    val personName: String = "",
    val designation: String = "",
    val title: String = "",
    val wishMessage: String = "",
    val animationStyle: String = "Fade",
    val musicSelection: String = "No music selected",
    val musicResourceName: String? = null,
    val musicPhoneUri: Uri? = null,
    val musicStartSecond: Int = 0,
    val duration: String = "15 Seconds"
)

internal fun defaultVideoTitle(category: String): String = when (category) {
    "Birthday" -> "Happy Birthday"
    "Achievement" -> "Congratulations"
    "Welcome" -> "Welcome To The Team"
    "Income" -> "Income Success"
    else -> "Your Story"
}

internal fun defaultVideoMessage(category: String): String = wishMessagesForCategory(category).first()

private const val VIDEO_EXPORT_WIDTH = 720
private const val VIDEO_EXPORT_HEIGHT = 1280
private const val VIDEO_EXPORT_FRAME_RATE = 24

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VideoCustomizationScreen(
    templateName: String,
    category: String,
    colors: List<Color>,
    state: VideoCustomizationState,
    onStateChange: (VideoCustomizationState) -> Unit,
    onBack: () -> Unit,
    onOpenMusicLibrary: () -> Unit,
    onSaveImage: () -> Unit,
    onGenerate: () -> Unit
) {
    var showAnimationSheet by remember { mutableStateOf(false) }
    var showDurationSheet by remember { mutableStateOf(false) }
    var activeEditorSheet by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Customize Video",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onSaveImage) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Save")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimatedVideoPreview(
                    state = state,
                    colors = colors,
                    category = category
                )
            }
            item {
                VideoPreviewTimeline(duration = state.duration)
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { VideoToolButton("Photo", Icons.Default.PhotoCamera) { activeEditorSheet = "Photo" } }
                    item { VideoToolButton("Text", Icons.Default.TextFields) { activeEditorSheet = "Text" } }
                    item { VideoToolButton("Elements", Icons.Default.AutoAwesome) { activeEditorSheet = "Elements" } }
                    item { VideoToolButton("Bg", Icons.Default.Animation) { activeEditorSheet = "Bg" } }
                    item { VideoToolButton("Colors", Icons.Default.Palette) { activeEditorSheet = "Colors" } }
                }
            }
            item {
                VideoSettingsCard(
                    state = state,
                    onMusicClick = onOpenMusicLibrary,
                    onAnimationClick = { showAnimationSheet = true },
                    onDurationClick = { showDurationSheet = true }
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSaveImage,
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF7C3AED))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save",
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF7C3AED), Color(0xFF2563EB))
                                )
                            )
                            .clickable { onGenerate() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            Text(
                                text = "Generate Video (MP4)",
                                color = Color.White,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                            )
                        }
                    }
                }
            }
        }
    }

    when (activeEditorSheet) {
        "Photo" -> VideoEditorSheet("Photo", onDismiss = { activeEditorSheet = null }) {
            ProfilePhotoUploader(
                imageUri = state.profilePhotoUri,
                onImageSelected = { onStateChange(state.copy(profilePhotoUri = it)) }
            )
            PosterTextField(
                value = state.personName,
                onValueChange = { onStateChange(state.copy(personName = it)) },
                label = "Person Name",
                leadingIcon = Icons.Default.Person
            )
            PosterTextField(
                value = state.designation,
                onValueChange = { onStateChange(state.copy(designation = it)) },
                label = "Designation (Optional)",
                leadingIcon = Icons.Default.Business
            )
        }
        "Text" -> VideoEditorSheet("Text", onDismiss = { activeEditorSheet = null }) {
            val wishOptions = remember(category) { wishMessagesForCategory(category) }
            PosterTextField(
                value = state.title,
                onValueChange = { onStateChange(state.copy(title = it)) },
                label = "Title",
                leadingIcon = Icons.Default.TextFields
            )
            VideoWishMessageSelector(
                category = category,
                options = wishOptions,
                selectedMessage = state.wishMessage.ifBlank { wishOptions.first() },
                onSelectedMessageChange = { onStateChange(state.copy(wishMessage = it)) }
            )
            PosterTextField(
                value = state.wishMessage,
                onValueChange = { onStateChange(state.copy(wishMessage = it)) },
                label = "Custom Message (Optional)",
                leadingIcon = Icons.Default.AutoAwesome,
                minLines = 3
            )
        }
        "Elements" -> VideoEditorSheet("Elements", onDismiss = { activeEditorSheet = null }) {
            Text(
                text = "Template decorative elements are included in the selected video design.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        "Bg" -> VideoEditorSheet("Background", onDismiss = { activeEditorSheet = null }) {
            Text(
                text = "$templateName background is applied from the selected template.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        "Colors" -> VideoEditorSheet("Colors", onDismiss = { activeEditorSheet = null }) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    )
                }
            }
        }
    }

    if (showAnimationSheet) {
        VideoOptionSheet(
            title = "Animation Style",
            options = listOf("Fade", "Zoom", "Slide", "Scale", "Pop", "Ken Burns"),
            selected = state.animationStyle,
            onDismiss = { showAnimationSheet = false },
            onSelected = {
                onStateChange(state.copy(animationStyle = it))
                showAnimationSheet = false
            }
        )
    }
    if (showDurationSheet) {
        VideoOptionSheet(
            title = "Video Duration",
            options = listOf("15 Seconds", "20 Seconds", "30 Seconds", "45 Seconds", "60 Seconds"),
            selected = state.duration,
            onDismiss = { showDurationSheet = false },
            onSelected = {
                onStateChange(state.copy(duration = it))
                showDurationSheet = false
            }
        )
    }
}

@Composable
private fun VideoPreviewTimeline(duration: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        Text(
            text = "00:00",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Slider(
            value = 0.58f,
            onValueChange = {},
            enabled = false,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "00:${duration.substringBefore(" ").padStart(2, '0')}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun VideoToolButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(74.dp)
            .height(68.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoEditorSheet(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
            content()
        }
    }
}

@Composable
private fun VideoWishMessageSelector(
    category: String,
    options: List<String>,
    selectedMessage: String,
    onSelectedMessageChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF7C3AED))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$category Message",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedMessage,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedVideoPreview(
    state: VideoCustomizationState,
    colors: List<Color>,
    category: String
) {
    var kenBurnsExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(state.animationStyle) {
        while (state.animationStyle == "Ken Burns") {
            kenBurnsExpanded = !kenBurnsExpanded
            delay(2400)
        }
        kenBurnsExpanded = false
    }
    val kenBurnsScale by animateFloatAsState(
        targetValue = if (kenBurnsExpanded) 1.08f else 1f,
        animationSpec = tween(2200),
        label = "ken_burns_scale"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        val previewModifier = if (maxWidth * 16f / 9f <= maxHeight) {
            Modifier.fillMaxWidth().aspectRatio(9f / 16f)
        } else {
            Modifier.height(maxHeight).aspectRatio(9f / 16f)
        }
        Card(
            modifier = previewModifier.shadow(18.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            AnimatedContent(
                targetState = state,
                transitionSpec = {
                    when (targetState.animationStyle) {
                        "Zoom" -> (fadeIn(tween(450)) + scaleIn(initialScale = 0.82f)) togetherWith
                            (fadeOut(tween(300)) + scaleOut(targetScale = 1.12f))
                        "Slide" -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                        "Scale" -> scaleIn(tween(450), initialScale = 0.7f) togetherWith scaleOut(tween(300))
                        "Pop" -> (fadeIn() + scaleIn(spring(), initialScale = 0.45f)) togetherWith
                            (fadeOut() + scaleOut(targetScale = 0.8f))
                        else -> fadeIn(tween(450)) togetherWith fadeOut(tween(300))
                    }
                },
                label = "video_preview_content"
            ) { previewState ->
                VideoPreviewContent(
                    state = previewState,
                    colors = colors,
                    category = category,
                    modifier = Modifier.graphicsLayer(
                        scaleX = if (previewState.animationStyle == "Ken Burns") kenBurnsScale else 1f,
                        scaleY = if (previewState.animationStyle == "Ken Burns") kenBurnsScale else 1f
                    )
                )
            }
        }
    }
}

@Composable
private fun VideoPreviewContent(
    state: VideoCustomizationState,
    colors: List<Color>,
    category: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.TopEnd)
                .offset(x = 42.dp, y = (-38).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.13f))
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.companyLogoUri != null) {
                        AsyncImage(
                            model = state.companyLogoUri,
                            contentDescription = "Company logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.Business, contentDescription = null, tint = Color.White)
                    }
                }
                Crossfade(targetState = state.companyName, label = "company_name") {
                    Text(
                        text = it.ifBlank { "Company Name" },
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.title.ifBlank { defaultVideoTitle(category) },
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .size(166.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.17f))
                    .border(2.dp, Color.White.copy(alpha = 0.72f), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (state.profilePhotoUri != null) {
                    AsyncImage(
                        model = state.profilePhotoUri,
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.sample_business_man),
                        contentDescription = "Sample profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = state.personName.ifBlank { "Person Name" },
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            AnimatedVisibility(
                visible = state.designation.isNotBlank(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Text(
                    text = state.designation,
                    color = Color.White.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            AdaptiveWishText(
                message = state.wishMessage.ifBlank { defaultVideoMessage(category) },
                color = Color.White.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
                maxLines = 6,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.Black.copy(alpha = 0.22f))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.companyName.ifBlank { "Company Name" },
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = state.website.ifBlank { "www.company.com" },
                    color = Color.White.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun VideoSectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFF9F7AEA),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
    )
}

@Composable
private fun VideoSettingsCard(
    state: VideoCustomizationState,
    onMusicClick: () -> Unit,
    onAnimationClick: () -> Unit,
    onDurationClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Video Settings",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            VideoSettingRow(
                icon = Icons.Default.MusicNote,
                title = "Background Music",
                value = state.musicSelection,
                onClick = onMusicClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            VideoSettingRow(
                icon = Icons.Default.Animation,
                title = "Animation Style",
                value = state.animationStyle,
                onClick = onAnimationClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            VideoSettingRow(
                icon = Icons.Default.Timer,
                title = "Duration",
                value = state.duration,
                onClick = onDurationClick
            )
        }
    }
}

@Composable
private fun VideoSettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
        Icon(icon, contentDescription = null, tint = Color(0xFF8B5CF6))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoOptionSheet(
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
                    Text(text = option, modifier = Modifier.weight(1f))
                    if (selected == option) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = Color(0xFF7C3AED)
                        )
                    }
                }
            }
        }
    }
}

internal data class MusicTrack(
    val name: String,
    val duration: String,
    val category: String,
    val resourceName: String? = null,
    val resourceId: Int? = null,
    val durationSeconds: Int = durationToSeconds(duration)
)

private fun loadRawMusicTracks(context: Context): List<MusicTrack> {
    val rawClass = runCatching { Class.forName("${context.packageName}.R\$raw") }.getOrNull()
        ?: return emptyList()
    return rawClass.fields
        .mapNotNull { field ->
            val resourceId = runCatching { field.getInt(null) }.getOrNull() ?: return@mapNotNull null
            val resourceName = field.name
            val durationSeconds = context.rawAudioDurationSeconds(resourceId)
            MusicTrack(
                name = resourceName.toMusicTitle(),
                duration = durationSeconds.toClockTime(),
                category = resourceName.toMusicCategory(),
                resourceName = resourceName,
                resourceId = resourceId,
                durationSeconds = durationSeconds
            )
        }
        .sortedBy { it.name }
}

private fun String.toMusicTitle(): String = split("_")
    .filter { it.isNotBlank() }
    .joinToString(" ") { word -> word.replaceFirstChar { it.titlecase(Locale.getDefault()) } }

private fun String.toMusicCategory(): String = when {
    contains("birthday", ignoreCase = true) || contains("celebration", ignoreCase = true) -> "Birthday"
    contains("achievement", ignoreCase = true) || contains("success", ignoreCase = true) ||
        contains("victory", ignoreCase = true) || contains("award", ignoreCase = true) -> "Achievement"
    contains("welcome", ignoreCase = true) || contains("joining", ignoreCase = true) ||
        contains("fresh", ignoreCase = true) -> "Welcome"
    contains("income", ignoreCase = true) || contains("business", ignoreCase = true) ||
        contains("growth", ignoreCase = true) -> "Income"
    else -> "Instrumental"
}

private fun Context.rawAudioDurationSeconds(resourceId: Int): Int {
    val afd = resources.openRawResourceFd(resourceId) ?: return 30
    return afd.use {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(it.fileDescriptor, it.startOffset, it.length)
            val millis = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
            ((millis ?: 30000L) / 1000L).toInt().coerceAtLeast(1)
        } catch (_: Exception) {
            30
        } finally {
            retriever.release()
        }
    }
}

private fun durationToSeconds(duration: String): Int {
    val parts = duration.split(":")
    return if (parts.size == 2) {
        ((parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)).coerceAtLeast(1)
    } else {
        30
    }
}

private fun Int.toClockTime(): String = "%02d:%02d".format(this / 60, this % 60)

private val bundledMusicTracks = listOf(
    MusicTrack(
        name = "Happy Birthday Celebration",
        duration = "3:12",
        category = "Birthday",
        resourceName = "birthday_song1",
        resourceId = R.raw.birthday_song1,
        durationSeconds = 192
    ),
    MusicTrack(
        name = "Birthday Party Music",
        duration = "2:48",
        category = "Birthday",
        resourceName = "birthday_song2",
        resourceId = R.raw.birthday_song2,
        durationSeconds = 168
    ),
    MusicTrack(
        name = "Birthday Wishes Instrumental",
        duration = "3:40",
        category = "Birthday",
        resourceName = "birthday_song3",
        resourceId = R.raw.birthday_song3,
        durationSeconds = 220
    ),
    MusicTrack(
        name = "Achievement Success",
        duration = "3:12",
        category = "Achievement",
        resourceName = "achievement_song1",
        resourceId = R.raw.achievement_song1,
        durationSeconds = 192
    ),
    MusicTrack(
        name = "Leadership Award",
        duration = "2:48",
        category = "Achievement",
        resourceName = "achievement_song2",
        resourceId = R.raw.achievement_song2,
        durationSeconds = 168
    ),
    MusicTrack(
        name = "Champion Celebration",
        duration = "3:40",
        category = "Achievement",
        resourceName = "achievement_song3",
        resourceId = R.raw.achievement_song3,
        durationSeconds = 220
    ),
    MusicTrack(
        name = "Welcome Celebration",
        duration = "3:12",
        category = "Welcome",
        resourceName = "welcome_song1",
        resourceId = R.raw.welcome_song1,
        durationSeconds = 192
    ),
    MusicTrack(
        name = "New Member Welcome",
        duration = "2:48",
        category = "Welcome",
        resourceName = "welcome_song2",
        resourceId = R.raw.welcome_song2,
        durationSeconds = 168
    ),
    MusicTrack(
        name = "Corporate Welcome Theme",
        duration = "3:40",
        category = "Welcome",
        resourceName = "welcome_song3",
        resourceId = R.raw.welcome_song3,
        durationSeconds = 220
    ),
    MusicTrack(
        name = "Income Success Theme",
        duration = "3:12",
        category = "Income",
        resourceName = "income_song1",
        resourceId = R.raw.income_song1,
        durationSeconds = 192
    ),
    MusicTrack(
        name = "Financial Growth",
        duration = "2:48",
        category = "Income",
        resourceName = "income_song2",
        resourceId = R.raw.income_song2,
        durationSeconds = 168
    ),
    MusicTrack(
        name = "Business Achievement",
        duration = "3:40",
        category = "Income",
        resourceName = "income_song3",
        resourceId = R.raw.income_song3,
        durationSeconds = 220
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MusicLibraryScreen(
    selectedState: VideoCustomizationState,
    currentCategory: String,
    onBack: () -> Unit,
    onUseMusic: (VideoCustomizationState) -> Unit
) {
    val context = LocalContext.current
    var selectedSong by rememberSaveable {
        mutableStateOf(selectedState.musicSelection.takeUnless { it == "No music selected" }.orEmpty())
    }
    var selectedResourceName by rememberSaveable { mutableStateOf(selectedState.musicResourceName) }
    var selectedPhoneUri by rememberSaveable { mutableStateOf(selectedState.musicPhoneUri?.toString()) }
    var selectedStartSecond by rememberSaveable { mutableStateOf(selectedState.musicStartSecond) }
    var playingSong by rememberSaveable { mutableStateOf<String?>(null) }
    var isPaused by rememberSaveable { mutableStateOf(false) }
    var playbackProgress by remember { mutableStateOf(0f) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val bundledTracks = remember { bundledMusicTracks }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    val musicPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            selectedSong = context.resolveAudioFileName(uri)
            selectedResourceName = null
            selectedPhoneUri = uri.toString()
            selectedStartSecond = 0
            playingSong = null
            playbackProgress = 0f
            isPaused = false
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    LaunchedEffect(playingSong, isPaused, selectedStartSecond) {
        if (playingSong != null && !isPaused && mediaPlayer != null) {
            while (playingSong != null && !isPaused && mediaPlayer?.isPlaying == true) {
                val player = mediaPlayer ?: break
                val startMs = selectedStartSecond * 1000
                val windowMs = 30_000
                playbackProgress = ((player.currentPosition - startMs).toFloat() / windowMs).coerceIn(0f, 1f)
                if (player.currentPosition >= startMs + windowMs || playbackProgress >= 1f) {
                    player.pause()
                    player.seekTo(startMs)
                    playingSong = null
                    playbackProgress = 0f
                    isPaused = false
                    break
                }
                delay(220)
            }
        }
    }

    val filteredBundledTracks = bundledTracks.filter { track ->
        track.category.equals(currentCategory, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Background Music",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Text(
                        text = "Choose music for your video",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PhoneMusicCard(
                    selectedFileName = selectedSong.takeIf { it.startsWith("Phone: ") }?.removePrefix("Phone: "),
                    onClick = { musicPickerLauncher.launch(arrayOf("audio/*")) }
                )
            }
            if (filteredBundledTracks.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Text(
                            text = "$currentCategory Collection",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 10.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
            items(filteredBundledTracks, key = { it.resourceName ?: it.name }) { track ->
                BirthdayBundledMusicCard(
                    track = track,
                    selected = selectedResourceName == track.resourceName,
                    playing = playingSong == track.name && !isPaused,
                    onPreview = {
                        val resourceId = track.resourceId ?: return@BirthdayBundledMusicCard
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer.create(context, resourceId)?.apply { start() }
                        playingSong = track.name
                        isPaused = false
                        playbackProgress = 0f
                    },
                    onSelect = {
                        selectedSong = track.name
                        selectedResourceName = track.resourceName
                        selectedPhoneUri = null
                        selectedStartSecond = 0
                    }
                )
            }
            if (filteredBundledTracks.isEmpty()) {
                item {
                    Text(
                        text = "No songs found",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF8B5CF6), Color(0xFF2563EB))
                    )
                )
                .clickable(enabled = selectedSong.isNotBlank()) {
                    onUseMusic(
                        selectedState.copy(
                            musicSelection = selectedSong.ifBlank { "No music selected" },
                            musicResourceName = selectedResourceName,
                            musicPhoneUri = selectedPhoneUri?.let(Uri::parse),
                            musicStartSecond = selectedStartSecond
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Use Music",
                color = Color.White.copy(alpha = if (selectedSong.isBlank()) 0.58f else 1f),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
        }
    }
}

@Composable
private fun BirthdayBundledMusicCard(
    track: MusicTrack,
    selected: Boolean,
    playing: Boolean,
    onPreview: () -> Unit,
    onSelect: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(primary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = primary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (selected) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = track.duration,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            TextButton(onClick = onPreview) {
                Icon(
                    imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Preview",
                    tint = primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Preview")
            }
        }
    }
}

@Composable
private fun MusicTrackCard(
    track: MusicTrack,
    selected: Boolean,
    favorite: Boolean,
    playing: Boolean,
    paused: Boolean,
    progress: Float,
    startSecond: Int,
    maxStartSecond: Int,
    onTogglePlay: () -> Unit,
    onSelect: () -> Unit,
    onStartSecondChange: (Int) -> Unit,
    onToggleFavorite: () -> Unit
) {
    val border = if (selected) {
        BorderStroke(1.5.dp, Color(0xFF8B5CF6))
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clickable { onSelect() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = border
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${track.duration} • ${track.category}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    AnimatedVisibility(visible = selected) {
                        Text(
                            text = "Selected",
                            color = Color(0xFF8B5CF6),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (favorite) Color(0xFFEC4899) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onTogglePlay) {
                    Icon(
                        imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playing) "Pause" else "Play",
                        tint = Color(0xFF8B5CF6)
                    )
                }
            }
            AnimatedVisibility(visible = playing || paused) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Slider(
                        value = progress,
                        onValueChange = {},
                        enabled = false
                    )
                    Text(
                        text = "${track.progressTime(progress, startSecond)} / ${(startSecond + 30).coerceAtMost(track.durationSeconds).toClockTime()} ${if (paused) "• Paused" else ""}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            AnimatedVisibility(visible = selected) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "30-second clip starts at ${startSecond.toClockTime()}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Slider(
                        value = startSecond.toFloat(),
                        onValueChange = { onStartSecondChange(it.toInt()) },
                        valueRange = 0f..maxStartSecond.toFloat().coerceAtLeast(0f),
                        enabled = maxStartSecond > 0
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneMusicCard(
    selectedFileName: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8B5CF6).copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AudioFile, contentDescription = null, tint = Color(0xFF8B5CF6))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = selectedFileName ?: "Use Music From Phone",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (selectedFileName == null) "Choose MP3, WAV or M4A from device" else "Selected from phone",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color(0xFF8B5CF6))
        }
    }
}

private fun MusicTrack.progressTime(progress: Float, startSecond: Int): String {
    val endSecond = (startSecond + 30).coerceAtMost(durationSeconds)
    val current = (startSecond + ((endSecond - startSecond) * progress).toInt()).coerceIn(startSecond, endSecond)
    return current.toClockTime()
}

private fun previewToneFor(songName: String, beat: Int): Int {
    val birthdayPattern = listOf(
        ToneGenerator.TONE_DTMF_1,
        ToneGenerator.TONE_DTMF_3,
        ToneGenerator.TONE_DTMF_5,
        ToneGenerator.TONE_DTMF_8
    )
    val achievementPattern = listOf(
        ToneGenerator.TONE_DTMF_7,
        ToneGenerator.TONE_DTMF_9,
        ToneGenerator.TONE_DTMF_6,
        ToneGenerator.TONE_DTMF_9
    )
    val welcomePattern = listOf(
        ToneGenerator.TONE_DTMF_2,
        ToneGenerator.TONE_DTMF_4,
        ToneGenerator.TONE_DTMF_6,
        ToneGenerator.TONE_DTMF_8
    )
    val incomePattern = listOf(
        ToneGenerator.TONE_DTMF_3,
        ToneGenerator.TONE_DTMF_6,
        ToneGenerator.TONE_DTMF_9,
        ToneGenerator.TONE_DTMF_0
    )
    val pattern = when {
        songName.contains("Birthday", ignoreCase = true) ||
            songName.contains("Celebration", ignoreCase = true) ||
            songName.contains("Royal", ignoreCase = true) ||
            songName.contains("Luxury", ignoreCase = true) -> birthdayPattern
        songName.contains("Victory", ignoreCase = true) ||
            songName.contains("Champion", ignoreCase = true) ||
            songName.contains("Award", ignoreCase = true) ||
            songName.contains("Achievement", ignoreCase = true) ||
            songName.contains("Success Story", ignoreCase = true) -> achievementPattern
        songName.contains("Welcome", ignoreCase = true) ||
            songName.contains("Beginning", ignoreCase = true) ||
            songName.contains("Joining", ignoreCase = true) ||
            songName.contains("Fresh", ignoreCase = true) -> welcomePattern
        else -> incomePattern
    }
    return pattern[beat % pattern.size]
}

private fun android.content.Context.resolveAudioFileName(uri: Uri): String {
    val displayName = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
        }
        ?: uri.lastPathSegment
        ?: "Selected audio"
    return "Phone: $displayName"
}

private suspend fun exportVideoToGallery(
    context: Context,
    state: VideoCustomizationState,
    colors: List<Color>,
    category: String,
    onProgress: (Float) -> Unit
): Uri? = withContext(Dispatchers.IO) {
    val durationSeconds = state.duration.substringBefore(" ").toIntOrNull()?.coerceAtLeast(1) ?: 15
    val silentVideo = File(context.cacheDir, "poster_video_${System.currentTimeMillis()}_silent.mp4")
    val finalVideo = File(context.cacheDir, "poster_video_${System.currentTimeMillis()}.mp4")
    val profileBitmap = state.profilePhotoUri?.let { decodeVideoUriBitmap(context, it) }
    val logoBitmap = state.companyLogoUri?.let { decodeVideoUriBitmap(context, it) }
    try {
        runCatching {
            encodeSilentTemplateVideo(
                outputFile = silentVideo,
                state = state,
                colors = colors,
                category = category,
                durationSeconds = durationSeconds,
                profileBitmap = profileBitmap,
                logoBitmap = logoBitmap,
                onProgress = onProgress
            )
            require(isUsableVideoFile(silentVideo)) { "Encoded template video has no visible frame" }
        }.getOrElse { error ->
            Log.e("VideoExport", "MediaCodec template export failed, trying MediaRecorder", error)
            silentVideo.delete()
            encodeSilentTemplateVideoWithRecorder(
                outputFile = silentVideo,
                state = state,
                colors = colors,
                category = category,
                durationSeconds = durationSeconds,
                profileBitmap = profileBitmap,
                logoBitmap = logoBitmap,
                onProgress = onProgress
            )
            require(isUsableVideoFile(silentVideo)) { "Fallback template video has no visible frame" }
        }
        val muxed = if (state.musicResourceName != null || state.musicPhoneUri != null) {
            runCatching {
                muxAudioClip(
                    context = context,
                    videoFile = silentVideo,
                    outputFile = finalVideo,
                    state = state,
                    clipSeconds = 30
                )
            }.getOrDefault(false)
        } else {
            false
        }
        val sourceFile = if (muxed) finalVideo else silentVideo
        saveVideoFileToGallery(context, sourceFile, state.title.ifBlank { defaultVideoTitle(category) }).also {
            silentVideo.delete()
            finalVideo.delete()
        }
    } finally {
        profileBitmap?.recycle()
        logoBitmap?.recycle()
    }
}

internal suspend fun exportPosterBitmapVideoToGallery(
    context: Context,
    posterBitmap: Bitmap,
    state: VideoCustomizationState,
    title: String,
    onProgress: (Float) -> Unit
): Uri? = withContext(Dispatchers.IO) {
    val durationSeconds = state.duration.substringBefore(" ").toIntOrNull()?.coerceAtLeast(1) ?: 15
    val silentVideo = File(context.cacheDir, "poster_video_${System.currentTimeMillis()}_silent.mp4")
    val finalVideo = File(context.cacheDir, "poster_video_${System.currentTimeMillis()}.mp4")
    try {
        runCatching {
            encodePosterBitmapVideo(
                outputFile = silentVideo,
                posterBitmap = posterBitmap,
                animationStyle = state.animationStyle,
                durationSeconds = durationSeconds,
                onProgress = onProgress
            )
            require(isUsableVideoFile(silentVideo)) { "Encoded poster video has no visible frame" }
        }.getOrElse { error ->
            Log.e("VideoExport", "MediaCodec poster export failed, trying MediaRecorder", error)
            silentVideo.delete()
            encodePosterBitmapVideoWithRecorder(
                outputFile = silentVideo,
                posterBitmap = posterBitmap,
                animationStyle = state.animationStyle,
                durationSeconds = durationSeconds,
                onProgress = onProgress
            )
            require(isUsableVideoFile(silentVideo)) { "Fallback poster video has no visible frame" }
        }
        val muxed = if (state.musicResourceName != null || state.musicPhoneUri != null) {
            runCatching {
                muxAudioClip(
                    context = context,
                    videoFile = silentVideo,
                    outputFile = finalVideo,
                    state = state,
                    clipSeconds = durationSeconds
                )
            }.getOrDefault(false)
        } else {
            false
        }
        val sourceFile = if (muxed) finalVideo else silentVideo
        check((state.musicResourceName == null && state.musicPhoneUri == null) || muxed) {
            "The selected music could not be added. Choose another track or remove music and try again."
        }
        saveVideoFileToGallery(context, sourceFile, title.ifBlank { state.title.ifBlank { "Poster Video" } }).also {
            silentVideo.delete()
            finalVideo.delete()
        }
    } finally {
        silentVideo.delete()
        finalVideo.delete()
    }
}

internal fun fittedPosterRect(sourceWidth: Int, sourceHeight: Int, width: Int, height: Int): RectF {
    val scale = minOf(width.toFloat() / sourceWidth, height.toFloat() / sourceHeight)
    val w = sourceWidth * scale
    val h = sourceHeight * scale
    return RectF((width-w)/2, (height-h)/2, (width+w)/2, (height+h)/2)
}

private fun encodePosterBitmapVideo(
    outputFile: File,
    posterBitmap: Bitmap,
    animationStyle: String,
    durationSeconds: Int,
    onProgress: (Float) -> Unit
) {
    val width = VIDEO_EXPORT_WIDTH
    val height = if (posterBitmap.width == posterBitmap.height) width else VIDEO_EXPORT_HEIGHT
    val frameRate = VIDEO_EXPORT_FRAME_RATE
    val totalFrames = durationSeconds * frameRate
    val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height).apply {
        setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        setInteger(MediaFormat.KEY_BIT_RATE, 6_000_000)
        setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
    }
    val codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
    var inputSurface: Surface? = null
    var muxer: MediaMuxer? = null
    try {
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        inputSurface = codec.createInputSurface()
        codec.start()
        muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        val trackState = mutableMapOf("track" to -1, "started" to 0)
        drainEncoder(codec, muxer, false, 0, trackState)
        for (frame in 0 until totalFrames) {
            val canvas = inputSurface.lockCanvas(null)
            canvas.drawColor(android.graphics.Color.BLACK)
            val saveCount = applyExportAnimationTransform(
                canvas = canvas,
                animationStyle = animationStyle,
                seconds = frame.toFloat() / frameRate,
                width = width,
                height = height
            )
            canvas.drawBitmap(
                posterBitmap,
                null,
                fittedPosterRect(posterBitmap.width, posterBitmap.height, width, height),
                Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            )
            canvas.restoreToCount(saveCount)
            inputSurface.unlockCanvasAndPost(canvas)
            drainEncoder(codec, muxer, false, frame, trackState)
            if (frame % frameRate == 0) onProgress(frame.toFloat() / totalFrames * 0.72f)
        }
        drainEncoder(codec, muxer, true, totalFrames, trackState)
    } finally {
        inputSurface?.release()
        codec.stop()
        codec.release()
        runCatching { muxer?.stop() }
        muxer?.release()
    }
}

private fun encodePosterBitmapVideoWithRecorder(
    outputFile: File,
    posterBitmap: Bitmap,
    animationStyle: String,
    durationSeconds: Int,
    onProgress: (Float) -> Unit
) {
    encodeCanvasVideoWithRecorder(outputFile, durationSeconds, onProgress,
        outputHeight = if (posterBitmap.width == posterBitmap.height) VIDEO_EXPORT_WIDTH else VIDEO_EXPORT_HEIGHT) { canvas, seconds, width, height ->
        canvas.drawColor(android.graphics.Color.BLACK)
        val saveCount = applyExportAnimationTransform(
            canvas = canvas,
            animationStyle = animationStyle,
            seconds = seconds,
            width = width,
            height = height
        )
        canvas.drawBitmap(
            posterBitmap,
            null,
            fittedPosterRect(posterBitmap.width, posterBitmap.height, width, height),
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        )
        canvas.restoreToCount(saveCount)
    }
}

private fun encodeSilentTemplateVideo(
    outputFile: File,
    state: VideoCustomizationState,
    colors: List<Color>,
    category: String,
    durationSeconds: Int,
    profileBitmap: Bitmap?,
    logoBitmap: Bitmap?,
    onProgress: (Float) -> Unit
) {
    val width = VIDEO_EXPORT_WIDTH
    val height = VIDEO_EXPORT_HEIGHT
    val frameRate = VIDEO_EXPORT_FRAME_RATE
    val totalFrames = durationSeconds * frameRate
    val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height).apply {
        setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        setInteger(MediaFormat.KEY_BIT_RATE, 6_000_000)
        setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
    }
    val codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
    var inputSurface: Surface? = null
    var muxer: MediaMuxer? = null
    try {
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        inputSurface = codec.createInputSurface()
        codec.start()
        muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        val trackState = mutableMapOf("track" to -1, "started" to 0)
        drainEncoder(codec, muxer, false, 0, trackState)
        for (frame in 0 until totalFrames) {
            val canvas = inputSurface.lockCanvas(null)
            drawVideoFrame(
                canvas = canvas,
                state = state,
                colors = colors,
                category = category,
                seconds = frame.toFloat() / frameRate,
                width = width,
                height = height,
                profileBitmap = profileBitmap,
                logoBitmap = logoBitmap
            )
            inputSurface.unlockCanvasAndPost(canvas)
            drainEncoder(codec, muxer, false, frame, trackState)
            if (frame % frameRate == 0) onProgress(frame.toFloat() / totalFrames * 0.72f)
        }
        drainEncoder(codec, muxer, true, totalFrames, trackState)
    } finally {
        inputSurface?.release()
        codec.stop()
        codec.release()
        runCatching { muxer?.stop() }
        muxer?.release()
    }
}

private fun encodeSilentTemplateVideoWithRecorder(
    outputFile: File,
    state: VideoCustomizationState,
    colors: List<Color>,
    category: String,
    durationSeconds: Int,
    profileBitmap: Bitmap?,
    logoBitmap: Bitmap?,
    onProgress: (Float) -> Unit
) {
    encodeCanvasVideoWithRecorder(outputFile, durationSeconds, onProgress) { canvas, seconds, width, height ->
        drawVideoFrame(
            canvas = canvas,
            state = state,
            colors = colors,
            category = category,
            seconds = seconds,
            width = width,
            height = height,
            profileBitmap = profileBitmap,
            logoBitmap = logoBitmap
        )
    }
}

@Suppress("DEPRECATION")
private fun encodeCanvasVideoWithRecorder(
    outputFile: File,
    durationSeconds: Int,
    onProgress: (Float) -> Unit,
    outputHeight: Int = VIDEO_EXPORT_HEIGHT,
    drawFrame: (Canvas, Float, Int, Int) -> Unit
) {
    val width = VIDEO_EXPORT_WIDTH
    val height = outputHeight
    val frameRate = 12
    val totalFrames = (durationSeconds.coerceAtLeast(1) * frameRate).coerceAtLeast(1)
    val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder()
    } else {
        MediaRecorder()
    }
    var surface: Surface? = null
    try {
        recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setOutputFile(outputFile.absolutePath)
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        recorder.setVideoSize(width, height)
        recorder.setVideoFrameRate(frameRate)
        recorder.setVideoEncodingBitRate(5_000_000)
        recorder.prepare()
        surface = recorder.surface
        recorder.start()

        val frameDurationNanos = 1_000_000_000L / frameRate
        val startNanos = System.nanoTime()
        for (frame in 0 until totalFrames) {
            val seconds = frame.toFloat() / frameRate
            val canvas = surface.lockCanvas(null)
            drawFrame(canvas, seconds, width, height)
            surface.unlockCanvasAndPost(canvas)
            onProgress(frame.toFloat() / totalFrames * 0.72f)

            val targetNanos = startNanos + ((frame + 1) * frameDurationNanos)
            val sleepMillis = ((targetNanos - System.nanoTime()) / 1_000_000.0).roundToLong()
            if (sleepMillis > 0) Thread.sleep(sleepMillis)
        }
    } finally {
        runCatching { recorder.stop() }
        recorder.release()
    }
}

private fun isUsableVideoFile(file: File): Boolean {
    if (!file.exists() || file.length() <= 0L) return false
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(file.absolutePath)
        val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull()
            ?.coerceAtLeast(1L)
            ?: 1L
        val sampleTimesUs = listOf(
            500_000L,
            1_000_000L,
            (durationMs * 400L),
            (durationMs * 700L)
        ).map { it.coerceAtMost(durationMs * 1000L - 1L).coerceAtLeast(0L) }
        sampleTimesUs.any { timeUs ->
            val frame = runCatching {
                retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)
            }.getOrNull()
            frame?.let {
                try {
                    isUsableVideoFrame(it)
                } finally {
                    it.recycle()
                }
            } == true
        }
    } catch (e: Exception) {
        Log.e("VideoExport", "Unable to validate generated video", e)
        false
    } finally {
        retriever.release()
    }
}

private fun isUsableVideoFrame(bitmap: Bitmap): Boolean {
    if (bitmap.width < 8 || bitmap.height < 8) return false
    var minLuma = 255
    var maxLuma = 0
    var opaqueSamples = 0
    val uniqueBuckets = mutableSetOf<Int>()
    val xStep = (bitmap.width / 12).coerceAtLeast(1)
    val yStep = (bitmap.height / 12).coerceAtLeast(1)
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
    return opaqueSamples > 20 && (maxLuma - minLuma) > 18 && uniqueBuckets.size > 4
}

private fun drainEncoder(
    codec: MediaCodec,
    muxer: MediaMuxer,
    endOfStream: Boolean,
    frameIndex: Int,
    trackState: MutableMap<String, Int>?
) {
    if (endOfStream) codec.signalEndOfInputStream()
    val bufferInfo = MediaCodec.BufferInfo()
    var videoTrack = trackState?.get("track") ?: -1
    var muxerStarted = trackState?.get("started") == 1
    while (true) {
        val encoderStatus = codec.dequeueOutputBuffer(bufferInfo, if (endOfStream) 10_000 else 0)
        when {
            encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER -> if (!endOfStream) return
            encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                videoTrack = muxer.addTrack(codec.outputFormat)
                muxer.start()
                muxerStarted = true
                trackState?.put("track", videoTrack)
                trackState?.put("started", 1)
            }
            encoderStatus >= 0 -> {
                val encodedData = codec.getOutputBuffer(encoderStatus)
                if (encodedData != null && bufferInfo.size > 0 && muxerStarted) {
                    bufferInfo.presentationTimeUs = frameIndex * 1_000_000L / VIDEO_EXPORT_FRAME_RATE
                    encodedData.position(bufferInfo.offset)
                    encodedData.limit(bufferInfo.offset + bufferInfo.size)
                    muxer.writeSampleData(videoTrack, encodedData, bufferInfo)
                }
                codec.releaseOutputBuffer(encoderStatus, false)
                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) return
            }
        }
    }
}

private fun drawVideoFrame(
    canvas: Canvas,
    state: VideoCustomizationState,
    colors: List<Color>,
    category: String,
    seconds: Float,
    width: Int,
    height: Int,
    profileBitmap: Bitmap? = null,
    logoBitmap: Bitmap? = null
) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val first = colors.firstOrNull()?.toArgb() ?: Color(0xFF7C3AED).toArgb()
    val second = colors.getOrNull(1)?.toArgb() ?: Color(0xFF2563EB).toArgb()
    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), first, second, Shader.TileMode.CLAMP)
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    paint.shader = null
    paint.color = android.graphics.Color.argb(34, 255, 255, 255)
    canvas.drawCircle(width - 90f, 120f + kotlin.math.sin(seconds * 1.6f) * 18f, 250f, paint)
    canvas.drawCircle(70f, height - 220f, 210f, paint)

    val animationSaveCount = applyExportAnimationTransform(canvas, state.animationStyle, seconds, width, height)

    logoBitmap?.let {
        drawVideoBitmapInRect(
            canvas = canvas,
            bitmap = it,
            rect = RectF(84f, 88f, 174f, 178f),
            cornerRadius = 24f
        )
    }

    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textAlign = Paint.Align.CENTER
    paint.color = android.graphics.Color.WHITE
    paint.textSize = 74f
    drawCenteredText(canvas, state.title.ifBlank { defaultVideoTitle(category) }, width / 2f, 275f, paint, width - 120f)

    paint.textSize = 54f
    drawCenteredText(canvas, state.personName.ifBlank { "Person Name" }, width / 2f, 1160f, paint, width - 150f)
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 38f
    paint.color = android.graphics.Color.argb(230, 255, 255, 255)
    drawCenteredText(canvas, state.wishMessage.ifBlank { defaultVideoMessage(category) }, width / 2f, 1260f, paint, width - 160f)

    paint.color = android.graphics.Color.argb(46, 255, 255, 255)
    val photoRect = RectF(250f, 425f, 830f, 1005f)
    canvas.drawRoundRect(photoRect, 70f, 70f, paint)
    profileBitmap?.let {
        drawVideoBitmapInRect(
            canvas = canvas,
            bitmap = it,
            rect = photoRect,
            cornerRadius = 70f
        )
    }
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 7f
    paint.color = android.graphics.Color.argb(190, 255, 255, 255)
    canvas.drawRoundRect(photoRect, 70f, 70f, paint)
    paint.style = Paint.Style.FILL

    paint.color = android.graphics.Color.argb(65, 0, 0, 0)
    canvas.drawRoundRect(RectF(92f, height - 250f, width - 92f, height - 92f), 42f, 42f, paint)
    paint.color = android.graphics.Color.WHITE
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 42f
    drawCenteredText(canvas, state.companyName.ifBlank { "Company Name" }, width / 2f, height - 178f, paint, width - 160f)
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 34f
    paint.color = android.graphics.Color.argb(210, 255, 255, 255)
    drawCenteredText(canvas, state.website.ifBlank { "www.company.com" }, width / 2f, height - 124f, paint, width - 160f)

    canvas.restoreToCount(animationSaveCount)
}

private fun decodeVideoUriBitmap(context: Context, uri: Uri): Bitmap? {
    return runCatching {
        context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input)
        }
    }.getOrNull()
}

private fun drawVideoBitmapInRect(
    canvas: Canvas,
    bitmap: Bitmap,
    rect: RectF,
    cornerRadius: Float
) {
    if (bitmap.width <= 0 || bitmap.height <= 0 || rect.width() <= 0f || rect.height() <= 0f) return
    val saveCount = canvas.save()
    val clipPath = Path().apply {
        addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
    }
    canvas.clipPath(clipPath)
    val sourceRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
    val targetRatio = rect.width() / rect.height()
    val drawRect = if (sourceRatio > targetRatio) {
        val drawWidth = rect.height() * sourceRatio
        RectF(
            rect.centerX() - drawWidth / 2f,
            rect.top,
            rect.centerX() + drawWidth / 2f,
            rect.bottom
        )
    } else {
        val drawHeight = rect.width() / sourceRatio
        RectF(
            rect.left,
            rect.centerY() - drawHeight / 2f,
            rect.right,
            rect.centerY() + drawHeight / 2f
        )
    }
    canvas.drawBitmap(bitmap, null, drawRect, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
    canvas.restoreToCount(saveCount)
}

private fun applyExportAnimationTransform(
    canvas: Canvas,
    animationStyle: String,
    seconds: Float,
    width: Int,
    height: Int
): Int {
    val progress = (seconds / 1.2f).coerceIn(0f, 1f)
    val centerX = width / 2f
    val centerY = height / 2f
    val alpha = when (animationStyle) {
        "Fade" -> (progress * 255).toInt().coerceIn(0, 255)
        else -> 255
    }
    val saveCount = canvas.saveLayerAlpha(0f, 0f, width.toFloat(), height.toFloat(), alpha)
    when (animationStyle) {
        "Zoom In", "Zoom" -> {
            val scale = 0.82f + 0.18f * progress
            canvas.scale(scale, scale, centerX, centerY)
        }
        "Zoom Out" -> {
            val scale = 1.18f - 0.18f * progress
            canvas.scale(scale, scale, centerX, centerY)
        }
        "Slide Left", "Slide" -> canvas.translate((-1f + progress) * width, 0f)
        "Slide Right" -> canvas.translate((1f - progress) * width, 0f)
        "Slide Up" -> canvas.translate(0f, (1f - progress) * height)
        "Slide Down" -> canvas.translate(0f, (-1f + progress) * height)
        "Scale" -> {
            val scale = 0.7f + 0.3f * progress
            canvas.scale(scale, scale, centerX, centerY)
        }
        "Bounce" -> {
            val bounce = if (progress < 1f) {
                0.7f + 0.35f * kotlin.math.sin(progress * Math.PI).toFloat()
            } else {
                1f
            }
            canvas.scale(bounce.coerceAtLeast(0.7f), bounce.coerceAtLeast(0.7f), centerX, centerY)
        }
        "Rotate" -> canvas.rotate((1f - progress) * -12f, centerX, centerY)
        "Flip" -> canvas.scale((progress * 2f - 1f).coerceIn(-1f, 1f), 1f, centerX, centerY)
        "Pulse" -> {
            val scale = 1f + 0.035f * kotlin.math.sin(seconds * 5f)
            canvas.scale(scale, scale, centerX, centerY)
        }
    }
    return saveCount
}

private fun drawCenteredText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint, maxWidth: Float) {
    val originalSize = paint.textSize
    var effectiveSize = originalSize
    var lines: List<String>
    do {
        paint.textSize = effectiveSize
        lines = wrapVideoTextForPaint(text, paint, maxWidth)
        if (lines.size <= 5 && lines.none { paint.measureText(it) > maxWidth }) break
        effectiveSize -= 2f
    } while (effectiveSize >= originalSize * 0.62f)

    val lineHeight = effectiveSize * 1.18f
    lines.take(5).forEachIndexed { index, value ->
        canvas.drawText(value, x, y + index * lineHeight, paint)
    }
    paint.textSize = originalSize
}

private fun wrapVideoTextForPaint(text: String, paint: Paint, maxWidth: Float): List<String> {
    val lines = mutableListOf<String>()
    text.split("\n").forEach { paragraph ->
        var line = ""
        paragraph.split(Regex("\\s+")).filter { it.isNotBlank() }.forEach { word ->
            val candidate = if (line.isBlank()) word else "$line $word"
            if (paint.measureText(candidate) > maxWidth && line.isNotBlank()) {
                lines += line
                line = word
            } else {
                line = candidate
            }
        }
        if (line.isNotBlank()) lines += line
    }
    return lines
}

private fun muxAudioClip(
    context: Context,
    videoFile: File,
    outputFile: File,
    state: VideoCustomizationState,
    clipSeconds: Int
): Boolean {
    val videoExtractor = MediaExtractor()
    val audioExtractor = MediaExtractor()
    val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    try {
        videoExtractor.setDataSource(videoFile.absolutePath)
        val videoTrackIndex = (0 until videoExtractor.trackCount).firstOrNull {
            videoExtractor.getTrackFormat(it).getString(MediaFormat.KEY_MIME)?.startsWith("video/") == true
        } ?: return false
        videoExtractor.selectTrack(videoTrackIndex)
        val muxVideoTrack = muxer.addTrack(videoExtractor.getTrackFormat(videoTrackIndex))

        val afd = state.musicResourceName?.let {
            val id = context.resources.getIdentifier(it, "raw", context.packageName)
            if (id != 0) context.resources.openRawResourceFd(id) else null
        }
        if (afd != null) {
            audioExtractor.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        } else {
            val uri = state.musicPhoneUri ?: return false
            audioExtractor.setDataSource(context, uri, null)
        }
        val audioTrackIndex = (0 until audioExtractor.trackCount).firstOrNull {
            audioExtractor.getTrackFormat(it).getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true
        } ?: return false
        val audioFormat = audioExtractor.getTrackFormat(audioTrackIndex)
        val audioMime = audioFormat.getString(MediaFormat.KEY_MIME).orEmpty()
        if (!audioMime.startsWith("audio/")) return false
        audioExtractor.selectTrack(audioTrackIndex)
        val muxAudioTrack = runCatching { muxer.addTrack(audioFormat) }.getOrElse { return false }
        muxer.start()
        copyTrack(videoExtractor, muxer, muxVideoTrack, Long.MAX_VALUE, 0L)
        val startUs = state.musicStartSecond * 1_000_000L
        audioExtractor.seekTo(startUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
        copyTrack(audioExtractor, muxer, muxAudioTrack, startUs + clipSeconds * 1_000_000L, startUs)
        afd?.close()
        return true
    } finally {
        videoExtractor.release()
        audioExtractor.release()
        runCatching { muxer.stop() }
        muxer.release()
    }
}

private fun copyTrack(
    extractor: MediaExtractor,
    muxer: MediaMuxer,
    outputTrack: Int,
    endTimeUs: Long,
    timeOffsetUs: Long
) {
    val buffer = ByteBuffer.allocate(1_048_576)
    val info = MediaCodec.BufferInfo()
    while (true) {
        val sampleTime = extractor.sampleTime
        if (sampleTime < 0 || sampleTime > endTimeUs) break
        info.offset = 0
        info.size = extractor.readSampleData(buffer, 0)
        if (info.size < 0) break
        info.presentationTimeUs = (sampleTime - timeOffsetUs).coerceAtLeast(0L)
        info.flags = extractor.sampleFlags
        muxer.writeSampleData(outputTrack, buffer, info)
        extractor.advance()
    }
}

private fun saveVideoFileToGallery(context: Context, file: File, title: String): Uri? {
    val cleanTitle = title.ifBlank { "Poster Video" }.replace(Regex("[^A-Za-z0-9 _-]"), "").take(60)
    val values = ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, "${cleanTitle}_${System.currentTimeMillis()}.mp4")
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/PosterFlow")
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }
    }
    val galleryUri = runCatching {
        val uri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            ?: return@runCatching null
        val copied = context.contentResolver.openOutputStream(uri)?.use { output ->
            file.inputStream().use { input -> input.copyTo(output) }
            true
        } == true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Video.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
        }
        if (copied) uri else null
    }.getOrNull()
    val appUri = saveVideoFileToAppStorage(context, file, cleanTitle)
    return appUri ?: galleryUri
}

private fun saveVideoFileToAppStorage(context: Context, file: File, title: String): Uri? {
    return runCatching {
        val dir = File(context.filesDir, "generated_videos").apply { mkdirs() }
        val cleanTitle = title.ifBlank { "Poster Video" }.replace(Regex("[^A-Za-z0-9 _-]"), "").take(60)
        val outputFile = File(dir, "${cleanTitle}_${System.currentTimeMillis()}.mp4")
        file.inputStream().use { input ->
            outputFile.outputStream().use { output -> input.copyTo(output) }
        }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outputFile)
    }.getOrNull()
}

internal suspend fun saveVideoTemplateImageToGallery(
    context: Context,
    state: VideoCustomizationState,
    colors: List<Color>,
    category: String
): Uri? = withContext(Dispatchers.IO) {
    val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val profileBitmap = state.profilePhotoUri?.let { decodeVideoUriBitmap(context, it) }
    val logoBitmap = state.companyLogoUri?.let { decodeVideoUriBitmap(context, it) }
    try {
        drawVideoFrame(
            canvas = Canvas(bitmap),
            state = state,
            colors = colors,
            category = category,
            seconds = 0f,
            width = bitmap.width,
            height = bitmap.height,
            profileBitmap = profileBitmap,
            logoBitmap = logoBitmap
        )
        val title = state.title.ifBlank { defaultVideoTitle(category) }
        val cleanTitle = title.replace(Regex("[^A-Za-z0-9 _-]"), "").take(60).ifBlank { "Poster Image" }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${cleanTitle}_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PosterFlow")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return@withContext null
        context.contentResolver.openOutputStream(uri)?.use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
        }
        uri
    } finally {
        bitmap.recycle()
        profileBitmap?.recycle()
        logoBitmap?.recycle()
    }
}

private fun Context.openVideoUri(uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "video/mp4")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(intent, "Open video"))
}

private fun Context.shareVideoUri(uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "video/mp4"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(intent, "Share video"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VideoPreviewScreen(
    templateName: String,
    category: String,
    colors: List<Color>,
    state: VideoCustomizationState,
    onBack: () -> Unit,
    onSaveImage: () -> Unit,
    onGenerate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Video Preview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onSaveImage) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Save preview")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimatedVideoPreview(
                    state = state,
                    colors = colors,
                    category = category
                )
            }
            item { VideoPreviewTimeline(duration = state.duration) }
            item {
                VideoDetailsCard(
                    category = category,
                    templateName = templateName,
                    duration = state.duration,
                    animation = state.animationStyle,
                    music = state.musicSelection
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSaveImage,
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF7C3AED))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save", color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1.45f)
                            .height(58.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFEC4899), Color(0xFF2563EB))
                                )
                            )
                            .clickable { onGenerate() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            Text(
                                text = "Generate Video (MP4)",
                                color = Color.White,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoDetailsCard(
    category: String,
    templateName: String,
    duration: String,
    animation: String,
    music: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Video Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
            VideoDetailRow("Category", category)
            VideoDetailRow("Template", templateName.ifBlank { "Selected Template" })
            VideoDetailRow("Duration", duration)
            VideoDetailRow("Animation", animation)
            VideoDetailRow("Music", music)
        }
    }
}

@Composable
private fun VideoDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun VideoProcessingScreen(
    state: VideoCustomizationState,
    colors: List<Color>,
    category: String,
    onCompleted: (Uri?) -> Unit
) {
    val context = LocalContext.current
    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        val uri = runCatching {
            exportVideoToGallery(
                context = context,
                state = state,
                colors = colors,
                category = category,
                onProgress = { progress = it.coerceIn(progress, 0.98f) }
            )
        }.onFailure {
            Log.e("VideoExport", "Unable to generate video", it)
        }.getOrNull()
        progress = 1f
        delay(350)
        onCompleted(uri)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = Color(0xFF7C3AED),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
internal fun VideoGeneratedScreen(
    videoUri: Uri?,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500)) + scaleIn(initialScale = 0.65f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (videoUri != null) Color(0xFF22C55E) else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(92.dp)
                )
                Text(
                    text = if (videoUri != null) "Video Saved Successfully" else "Video Save Failed",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    textAlign = TextAlign.Center
                )
                if (videoUri == null) {
                    Text(
                        text = "Please try generating the video again.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { videoUri?.let { context.openVideoUri(it) } },
                        enabled = videoUri != null
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Open Video")
                    }
                    Button(
                        onClick = { videoUri?.let { context.shareVideoUri(it) } },
                        enabled = videoUri != null
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Share Video")
                    }
                }
                TextButton(onClick = onDone) {
                    Text("Done")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleVideoTopScreen(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text(title, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}
