package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas as ComposeCanvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Poster
import com.example.ui.ProfileSettings
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

internal data class WelcomeRenderAssets(val images: Map<String, Bitmap?>)

private fun decodeWelcomeBitmap(context: Context, value: String): Bitmap? = value.takeIf { it.isNotBlank() }?.let {
    runCatching { context.contentResolver.openInputStream(Uri.parse(it))?.use(BitmapFactory::decodeStream) }.getOrNull()
}

internal fun loadWelcomeRenderAssets(context: Context, state: WelcomePosterState): WelcomeRenderAssets {
    val images = state.elements.filter { it.type == "photo" || it.type == "logo" }.associate { element ->
        val fallback = when (element.role) {
            "member", "memberPhoto" -> BitmapFactory.decodeResource(context.resources, R.drawable.sample_business_woman)
            "leaderPhoto1", "leaderPhoto2", "leaderPhoto3", "hostPhoto" -> BitmapFactory.decodeResource(context.resources, R.drawable.sample_business_man)
            else -> null
        }
        element.id to (decodeWelcomeBitmap(context, element.imageUri) ?: fallback)
    }
    return WelcomeRenderAssets(images)
}

internal fun renderWelcomePosterBitmap(context: Context, state: WelcomePosterState): Bitmap {
    val bitmap = Bitmap.createBitmap(state.canvasWidth, state.canvasHeight, Bitmap.Config.ARGB_8888)
    drawWelcomePoster(Canvas(bitmap), state.canvasWidth.toFloat(), state.canvasHeight.toFloat(), state, loadWelcomeRenderAssets(context, state))
    return bitmap
}

@Composable
internal fun WelcomePosterThumbnail(preset: Poster, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val state = remember(preset.id) { newWelcomePosterState(preset.id, "YOUR COMPANY", "", "www.yourcompany.com", "90000 00000") }
    val assets = remember(state) { loadWelcomeRenderAssets(context, state) }
    ComposeCanvas(modifier.aspectRatio(state.canvasWidth.toFloat() / state.canvasHeight)) {
        drawIntoCanvas { drawWelcomePoster(it.nativeCanvas, size.width, size.height, state, assets) }
    }
}

@Composable
internal fun WelcomePosterPreview(state: WelcomePosterState, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val assets = remember(state.elements) { loadWelcomeRenderAssets(context, state) }
    ComposeCanvas(modifier.aspectRatio(state.canvasWidth.toFloat() / state.canvasHeight)) {
        drawIntoCanvas { drawWelcomePoster(it.nativeCanvas, size.width, size.height, state, assets) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WelcomePosterEditor(
    preset: Poster,
    profile: ProfileSettings,
    savedState: WelcomePosterState? = null,
    onBack: () -> Unit,
    onSave: (WelcomePosterState, Bitmap) -> Unit,
    onShare: (Bitmap) -> Unit,
    onCreateVideo: (Bitmap) -> Unit = {}
) {
    val context = LocalContext.current
    // For new posters, re-create when branding changes. For reopened posters, savedState is
    // already injected with live branding by decodeWithBranding before arriving here.
    val brandingKey = Triple(profile.companyName, profile.companyLogoUri, profile.mobileNumber)
    val original = remember(preset.id, savedState) {
        savedState ?: newWelcomePosterState(
            preset.id, profile.companyName, profile.companyLogoUri, profile.websiteName,
            profile.mobileNumber, address = profile.businessAddress
        )
    }
    var state by remember(preset.id, savedState) { mutableStateOf(original) }
    var selectedId by remember { mutableStateOf<String?>("memberName") }
    var sheet by remember { mutableStateOf<String?>(null) }
    var undo by remember { mutableStateOf(emptyList<WelcomePosterState>()) }
    var redo by remember { mutableStateOf(emptyList<WelcomePosterState>()) }
    var dragStart by remember { mutableStateOf<WelcomePosterState?>(null) }
    var pendingImageId by remember { mutableStateOf<String?>(null) }
    val latestState by rememberUpdatedState(state)
    val assets = remember(state.elements) { loadWelcomeRenderAssets(context, state) }

    // ─── LIVE BRANDING SYNC ────────────────────────────────────────────────────
    // Whenever the user updates Settings branding (company name, logo, phone,
    // website, address), re-inject fresh branding into the current editor state
    // WITHOUT touching user-specific elements (photo, name, role).
    LaunchedEffect(brandingKey, profile.websiteName, profile.businessAddress) {
        state = state.injectBrandingElements(
            companyName = profile.companyName,
            logoUri     = profile.companyLogoUri,
            website     = profile.websiteName,
            phone       = profile.mobileNumber,
            address     = profile.businessAddress
        )
    }
    // ──────────────────────────────────────────────────────────────────────────

    fun commit(next: WelcomePosterState) {
        if (next == state) return
        undo = listOf(state) + undo.take(39)
        redo = emptyList()
        state = next
    }
    fun updateSelected(update: (WelcomePosterElement) -> WelcomePosterElement) {
        selectedId?.let { id -> commit(state.updateElement(id, update)) }
    }
    fun duplicateSelected() {
        val source = selectedId?.let(state::element) ?: return
        val copy = source.copy(id = "${source.role.ifBlank { source.type }}_${UUID.randomUUID()}", x = (source.x + 28f).coerceAtMost(1030f), y = (source.y + 28f).coerceAtMost(1030f), locked = false, zIndex = (state.elements.maxOfOrNull { it.zIndex } ?: 0) + 1)
        commit(state.copy(elements = state.elements + copy)); selectedId = copy.id
    }
    fun deleteSelected() {
        val id = selectedId ?: return
        val selected = state.element(id) ?: return
        if (selected.locked) return
        commit(state.copy(elements = state.elements.filterNot { it.id == id })); selectedId = null
    }
    fun addElement(type: String, text: String = "") {
        val id = "${type}_${UUID.randomUUID()}"
        val newElement = WelcomePosterElement(
            id = id, type = type, role = if (type == "photo") "addedPhoto" else type,
            text = text, x = 340f, y = 420f, width = if (type == "text") 400f else 260f,
            height = if (type == "text") 100f else 260f, zIndex = (state.elements.maxOfOrNull { it.zIndex } ?: 0) + 1,
            color = if (type == "shape") "#F4C553" else "#FFFFFF", fontSize = 48f,
            bold = type == "text", shape = if (type == "shape") "roundRect" else "rectangle", cornerRadius = 28f
        )
        commit(state.copy(elements = state.elements + newElement)); selectedId = id
        if (type == "photo" || type == "logo") pendingImageId = id
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val target = pendingImageId
        if (uri != null && target != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            commit(state.updateElement(target) { it.copy(imageUri = uri.toString(), cropScale = 1f, cropX = 0f, cropY = 0f) })
        }
        pendingImageId = null
    }
    LaunchedEffect(pendingImageId) {
        if (pendingImageId != null) imagePicker.launch(arrayOf("image/*"))
    }

    Column(Modifier.fillMaxSize().background(Color(0xFF080A12))) {
        TopAppBar(
            title = { Column { Text(preset.title, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("PosterFlow element editor", fontSize = 11.sp, color = Color(0xFFC9CAD8)) } },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
            actions = {
                IconButton(onClick = { if (undo.isNotEmpty()) { redo = listOf(state) + redo; state = undo.first(); undo = undo.drop(1) } }, enabled = undo.isNotEmpty()) { Icon(Icons.Default.Undo, "Undo") }
                IconButton(onClick = { if (redo.isNotEmpty()) { undo = listOf(state) + undo; state = redo.first(); redo = redo.drop(1) } }, enabled = redo.isNotEmpty()) { Icon(Icons.Default.Redo, "Redo") }
                IconButton(onClick = { onShare(renderWelcomePosterBitmap(context, state)) }) { Icon(Icons.Default.Share, "Share") }
                IconButton(onClick = { onCreateVideo(renderWelcomePosterBitmap(context, state)) }) { Icon(Icons.Default.PlayCircle, "Create Video") }
                IconButton(onClick = { onSave(state, renderWelcomePosterBitmap(context, state)) }) { Icon(Icons.Default.Download, "Save") }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF080A12))
        )
        Box(Modifier.fillMaxWidth().weight(1f).padding(12.dp), contentAlignment = Alignment.Center) {
            ComposeCanvas(
                Modifier.fillMaxWidth().aspectRatio(state.canvasWidth.toFloat() / state.canvasHeight)
                    .pointerInput(state.elements) {
                        detectTapGestures { point ->
                            val hit = hitWelcomeElement(point, size.width, size.height, state)
                            val hitElement = hit?.let(state::element)
                            if (hitElement != null && hitElement.role in BRANDING_ELEMENT_ROLES) {
                                // Branding elements are read-only — direct user to Settings
                                selectedId = null
                            } else {
                                selectedId = hit
                            }
                        }
                    }
                    .pointerInput(selectedId) {
                        detectDragGestures(
                            onDragStart = { dragStart = latestState },
                            onDragEnd = { dragStart?.let { undo = listOf(it) + undo.take(39); redo = emptyList() }; dragStart = null },
                            onDragCancel = { dragStart?.let { state = it }; dragStart = null }
                        ) { change, drag ->
                            change.consume()
                            val id = selectedId ?: return@detectDragGestures
                            val current = latestState.element(id) ?: return@detectDragGestures
                            // Never allow dragging branding elements
                            if (current.locked || current.role in BRANDING_ELEMENT_ROLES) return@detectDragGestures
                            val nx = (current.x + drag.x * WelcomeCanvasSize / size.width).coerceIn(-current.width * .8f, WelcomeCanvasSize - current.width * .2f)
                            val ny = (current.y + drag.y * WelcomeCanvasSize / size.height).coerceIn(-current.height * .8f, WelcomeCanvasSize - current.height * .2f)
                            state = latestState.updateElement(id) { it.copy(x = nx, y = ny) }
                        }
                    }
            ) { drawIntoCanvas { drawWelcomePoster(it.nativeCanvas, size.width, size.height, state, assets, selectedId) } }
        }
        selectedId?.let { id ->
            val selected = state.element(id)
            // Do not show an edit toolbar for branding elements — they are read-only
            if (selected != null && selected.role !in BRANDING_ELEMENT_ROLES) {
                LazyRow(Modifier.fillMaxWidth().padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    if (selected.type == "text" || selected.type == "icon") item { EditorChip("Edit", Icons.Default.Edit) { sheet = "text" } }
                    if (selected.type == "photo" || selected.type == "logo") item { EditorChip("Replace", Icons.Default.AddPhotoAlternate) { pendingImageId = id } }
                    item { EditorChip("Size", Icons.Default.FormatSize) { sheet = "position" } }
                    item { EditorChip("Rotate", Icons.Default.RotateRight) { sheet = "position" } }
                    item { EditorChip("Duplicate", Icons.Default.ContentCopy, ::duplicateSelected) }
                    item { EditorChip(if (selected.locked) "Unlock" else "Lock", if (selected.locked) Icons.Default.LockOpen else Icons.Default.Lock) { updateSelected { it.copy(locked = !it.locked) } } }
                    item { EditorChip("Delete", Icons.Default.Delete, ::deleteSelected) }
                }
                Spacer(Modifier.height(6.dp))
            }
        }
        LazyRow(Modifier.fillMaxWidth().padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            item { EditorChip("+ Add", Icons.Default.Add) { sheet = "add" } }
            item { EditorChip("Template", Icons.Default.Image) { sheet = "template" } }
            item { EditorChip("Text", Icons.Default.TextFields) { sheet = "text" } }
            item { EditorChip("Photo", Icons.Default.AddPhotoAlternate) { sheet = "photo" } }
            item { EditorChip("Logo", Icons.Default.Image) { selectedId = state.elements.firstOrNull { it.type == "logo" }?.id; sheet = "photo" } }
            item { EditorChip("Color", Icons.Default.Palette) { sheet = "color" } }
            item { EditorChip("Ribbon", Icons.Default.Palette) { selectedId = state.elements.firstOrNull { it.role == "nameRibbon" || it.id == "nameRibbon" || it.shape == "ribbon" }?.id ?: selectedId; sheet = "color" } }
            item { EditorChip("Background", Icons.Default.Image) { sheet = "background" } }
            item { EditorChip("Elements", Icons.Default.Layers) { sheet = "elements" } }
            item { EditorChip("Layers", Icons.Default.Layers) { sheet = "layers" } }
            item { EditorChip("Undo", Icons.Default.Undo) { if (undo.isNotEmpty()) { redo = listOf(state) + redo; state = undo.first(); undo = undo.drop(1) } } }
            item { EditorChip("Redo", Icons.Default.Redo) { if (redo.isNotEmpty()) { undo = listOf(state) + undo; state = redo.first(); redo = redo.drop(1) } } }
        }
        Spacer(Modifier.height(14.dp))
    }

    sheet?.let { active ->
        WelcomeEditorSheet(
            active = active, state = state, original = original, selectedId = selectedId,
            onSelect = { selectedId = it }, onDismiss = { sheet = null }, onCommit = ::commit,
            onPickImage = { id -> pendingImageId = id }, onAdd = ::addElement,
            onDuplicate = ::duplicateSelected, onDelete = ::deleteSelected,
            onResetTemplate = { commit(original); selectedId = "memberName" }
        )
    }
}

@Composable
private fun EditorChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    AssistChip(onClick = onClick, label = { Text(label) }, leadingIcon = { Icon(icon, null, Modifier.size(17.dp)) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WelcomeEditorSheet(
    active: String,
    state: WelcomePosterState,
    original: WelcomePosterState,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    onCommit: (WelcomePosterState) -> Unit,
    onPickImage: (String) -> Unit,
    onAdd: (String, String) -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onResetTemplate: () -> Unit
) {
    val selected = selectedId?.let(state::element)
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text(active.replaceFirstChar(Char::uppercase), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) }
            when (active) {
                "add" -> item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onAdd("text", "ADD YOUR HEADING") }, Modifier.fillMaxWidth()) { Text("Add Heading") }
                        Button(onClick = { onAdd("text", "Add your subheading") }, Modifier.fillMaxWidth()) { Text("Add Subheading") }
                        Button(onClick = { onAdd("text", "Add body text") }, Modifier.fillMaxWidth()) { Text("Add Body Text") }
                        Button(onClick = { onAdd("photo", "") }, Modifier.fillMaxWidth()) { Text("Add Photo") }
                        Button(onClick = { onAdd("logo", "") }, Modifier.fillMaxWidth()) { Text("Add Logo") }
                        Button(onClick = { onAdd("shape", "") }, Modifier.fillMaxWidth()) { Text("Add Shape") }
                        Button(onClick = { onAdd("icon", "★") }, Modifier.fillMaxWidth()) { Text("Add Icon") }
                    }
                }
                "template" -> item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("This design uses a fixed logical 1080 × 1080 canvas. Every visible item remains an independent layer.")
                        Button(onClick = onResetTemplate, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.RestartAlt, null); Text("Reset entire template") }
                    }
                }
                "elements" -> {
                    item {
                        Text(
                            "🔒 Branding (company, logo, phone, website) is read-only here.\nUpdate it in Settings → Profile to change across all posters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(state.elements.filter { it.role !in BRANDING_ELEMENT_ROLES }.sortedByDescending { it.zIndex }, key = { it.id }) { element ->
                        Card(onClick = { onSelect(element.id) }, colors = CardDefaults.cardColors(containerColor = if (element.id == selectedId) Color(0xFF34245B) else MaterialTheme.colorScheme.surfaceVariant)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(element.role.ifBlank { element.type }.replaceFirstChar(Char::uppercase), fontWeight = FontWeight.Bold)
                                Text(if (element.locked) "Locked" else "Layer ${element.zIndex}")
                            }
                        }
                    }
                }
                "background" -> items(welcomeThemes) { theme ->
                    Card(onClick = { onCommit(state.copy(themeId = theme.id)) }, colors = CardDefaults.cardColors(containerColor = Color(AndroidColor.parseColor(theme.top)))) {
                        Text(theme.name, Modifier.fillMaxWidth().padding(15.dp), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                "text" -> item {
                    if (selected == null || (selected.type != "text" && selected.type != "icon")) {
                        Text("Select a text element on the poster first.")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            OutlinedTextField(selected.text, { value -> onCommit(state.updateElement(selected.id) { it.copy(text = value) }) }, label = { Text("Edit text") }, modifier = Modifier.fillMaxWidth(), minLines = if (selected.role in setOf("message", "quote", "body")) 3 else 1)
                            Text("Font size ${selected.fontSize.toInt()}")
                            Slider(selected.fontSize, { value -> onCommit(state.updateElement(selected.id) { it.copy(fontSize = value) }) }, valueRange = 12f..120f)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(onClick = { onCommit(state.updateElement(selected.id) { it.copy(bold = !it.bold) }) }, label = { Text(if (selected.bold) "Bold ✓" else "Bold") })
                                AssistChip(onClick = { onCommit(state.updateElement(selected.id) { it.copy(italic = !it.italic) }) }, label = { Text(if (selected.italic) "Italic ✓" else "Italic") })
                                AssistChip(onClick = { onCommit(state.updateElement(selected.id) { it.copy(shadow = !it.shadow) }) }, label = { Text(if (selected.shadow) "Shadow ✓" else "Shadow") })
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(onClick = { onCommit(state.updateElement(selected.id) { it.copy(fontFamily = "sans-serif") }) }, label = { Text("Sans") })
                                AssistChip(onClick = { onCommit(state.updateElement(selected.id) { it.copy(fontFamily = "serif") }) }, label = { Text("Serif") })
                                AssistChip(onClick = { onCommit(state.updateElement(selected.id) { it.copy(fontFamily = "monospace") }) }, label = { Text("Mono") })
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("start", "center", "end").forEach { align -> AssistChip(onClick = { onCommit(state.updateElement(selected.id) { it.copy(alignment = align) }) }, label = { Text(align.replaceFirstChar(Char::uppercase)) }) }
                            }
                            Text("Letter spacing"); Slider(selected.letterSpacing, { value -> onCommit(state.updateElement(selected.id) { it.copy(letterSpacing = value) }) }, valueRange = 0f..0.25f)
                            Text("Line spacing"); Slider(selected.lineSpacing, { value -> onCommit(state.updateElement(selected.id) { it.copy(lineSpacing = value) }) }, valueRange = .8f..1.8f)
                        }
                    }
                }
                "photo" -> item {
                    if (selected == null || selected.type !in setOf("photo", "logo")) Text("Select a photo or logo first.") else Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onPickImage(selected.id) }, Modifier.fillMaxWidth()) { Text("Replace image") }
                        Text("Crop zoom ${"%.1f".format(selected.cropScale)}×"); Slider(selected.cropScale, { v -> onCommit(state.updateElement(selected.id) { it.copy(cropScale = v) }) }, valueRange = .7f..4f)
                        Text("Crop left / right"); Slider(selected.cropX, { v -> onCommit(state.updateElement(selected.id) { it.copy(cropX = v) }) }, valueRange = -400f..400f)
                        Text("Crop up / down"); Slider(selected.cropY, { v -> onCommit(state.updateElement(selected.id) { it.copy(cropY = v) }) }, valueRange = -400f..400f)
                        Text("Opacity"); Slider(selected.opacity, { v -> onCommit(state.updateElement(selected.id) { it.copy(opacity = v) }) }, valueRange = .05f..1f)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(onClick = { onCommit(state.updateElement(selected.id) { it.copy(flipHorizontal = !it.flipHorizontal) }) }, label = { Text("Flip") })
                            AssistChip(onClick = { onCommit(state.updateElement(selected.id) { it.copy(cropScale = 1f, cropX = 0f, cropY = 0f, flipHorizontal = false) }) }, label = { Text("Reset crop") })
                        }
                    }
                }
                "position" -> item {
                    if (selected == null) Text("Select an element first.") else Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Width ${selected.width.toInt()}"); Slider(selected.width, { v -> onCommit(state.updateElement(selected.id) { it.copy(width = v) }) }, valueRange = 30f..1080f)
                        Text("Height ${selected.height.toInt()}"); Slider(selected.height, { v -> onCommit(state.updateElement(selected.id) { it.copy(height = v) }) }, valueRange = 25f..1080f)
                        Text("Rotation ${selected.rotation.toInt()}°"); Slider(selected.rotation, { v -> onCommit(state.updateElement(selected.id) { it.copy(rotation = v) }) }, valueRange = -180f..180f)
                        Text("Opacity"); Slider(selected.opacity, { v -> onCommit(state.updateElement(selected.id) { it.copy(opacity = v) }) }, valueRange = .05f..1f)
                    }
                }
                "color" -> item {
                    if (selected == null) {
                        Text("Select an element first.")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            val colors = listOf(
                                "#FFFFFF", "#FFF9E6", "#F6C957", "#DA9C20", "#D91696", "#A81498",
                                "#7B1FA2", "#3F51B5", "#1E88E5", "#0288D1", "#00897B", "#2E7D32",
                                "#D32F2F", "#C2185B", "#E65100", "#140124", "#0D1B2A", "#111111"
                            )
                            Text("Fill / Main Color (${selected.role.ifBlank { selected.type }})", fontWeight = FontWeight.Bold)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(colors) { hex ->
                                    AssistChip(
                                        onClick = { onCommit(state.updateElement(selected.id) { it.copy(color = hex, paletteRole = "") }) },
                                        label = { Text("●", color = Color(AndroidColor.parseColor(hex)), fontSize = 28.sp) }
                                    )
                                }
                            }
                            if (selected.borderWidth > 0f) {
                                Text("Border / Trim Color", fontWeight = FontWeight.Bold)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(colors) { hex ->
                                        AssistChip(
                                            onClick = { onCommit(state.updateElement(selected.id) { it.copy(borderColor = hex) }) },
                                            label = { Text("●", color = Color(AndroidColor.parseColor(hex)), fontSize = 28.sp) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                "layers" -> item {
                    val isBranding = selected != null && selected.role in BRANDING_ELEMENT_ROLES
                    if (selected == null) Text("Select an element first.")
                    else if (isBranding) Text("🔒 Branding elements are managed in Settings → Profile.")
                    else Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onCommit(state.updateElement(selected.id) { it.copy(zIndex = it.zIndex + 1) }) }, Modifier.fillMaxWidth()) { Text("Bring Forward") }
                        Button(onClick = { onCommit(state.updateElement(selected.id) { it.copy(zIndex = it.zIndex - 1) }) }, Modifier.fillMaxWidth()) { Text("Send Backward") }
                        Button(onClick = { onCommit(state.updateElement(selected.id) { it.copy(zIndex = (state.elements.maxOfOrNull { e -> e.zIndex } ?: 0) + 1) }) }, Modifier.fillMaxWidth()) { Text("Bring to Front") }
                        Button(onClick = { onCommit(state.updateElement(selected.id) { it.copy(zIndex = (state.elements.minOfOrNull { e -> e.zIndex } ?: 0) - 1) }) }, Modifier.fillMaxWidth()) { Text("Send to Back") }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(onClick = { onCommit(state.updateElement(selected.id) { it.copy(locked = !it.locked) }) }, label = { Text(if (selected.locked) "Unlock" else "Lock") })
                            AssistChip(onClick = onDuplicate, label = { Text("Duplicate") })
                            AssistChip(onClick = onDelete, label = { Text("Delete") })
                        }
                        val reset = original.element(selected.id)
                        if (reset != null) TextButton(onClick = { onCommit(state.copy(elements = state.elements.map { if (it.id == selected.id) reset else it })) }) { Text("Reset selected element") }
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

private fun hitWelcomeElement(point: Offset, width: Int, height: Int, state: WelcomePosterState): String? {
    val x = point.x * state.canvasWidth / width.coerceAtLeast(1)
    val y = point.y * state.canvasHeight / height.coerceAtLeast(1)
    // Exclude branding elements — they are read-only and managed via Settings
    return state.elements
        .filter { it.visible && it.role !in BRANDING_ELEMENT_ROLES && x in it.x..(it.x + it.width) && y in it.y..(it.y + it.height) }
        .maxByOrNull { it.zIndex }?.id
}

internal fun drawWelcomePoster(canvas: Canvas, width: Float, height: Float, state: WelcomePosterState, assets: WelcomeRenderAssets, selected: String? = null) {
    val save = canvas.save()
    canvas.scale(width / state.canvasWidth, height / state.canvasHeight)
    canvas.clipRect(0f, 0f, state.canvasWidth.toFloat(), state.canvasHeight.toFloat())
    val theme = welcomeTheme(state.themeId, state.templateId)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    fun color(hex: String) = runCatching { AndroidColor.parseColor(hex) }.getOrDefault(AndroidColor.WHITE)
    fun elementColor(element: WelcomePosterElement): Int = color(when (element.paletteRole) { "accent" -> theme.accent; "text" -> theme.text; "panel" -> theme.panel; "background" -> theme.bottom; else -> element.color })

    // 1. Rich vertical atmospheric gradient background
    paint.shader = LinearGradient(0f, 0f, 0f, state.canvasHeight.toFloat(), color(theme.top), color(theme.bottom), Shader.TileMode.CLAMP)
    canvas.drawRect(0f, 0f, state.canvasWidth.toFloat(), state.canvasHeight.toFloat(), paint)
    paint.shader = null

    // 2. Central luminous glow behind member portrait and headings
    val glowColor = color(theme.glow)
    paint.shader = android.graphics.RadialGradient(
        720f, 440f, 540f,
        intArrayOf(
            AndroidColor.argb(95, AndroidColor.red(glowColor), AndroidColor.green(glowColor), AndroidColor.blue(glowColor)),
            AndroidColor.argb(30, AndroidColor.red(glowColor), AndroidColor.green(glowColor), AndroidColor.blue(glowColor)),
            AndroidColor.TRANSPARENT
        ),
        floatArrayOf(0f, 0.65f, 1f),
        Shader.TileMode.CLAMP
    )
    canvas.drawRect(0f, 0f, state.canvasWidth.toFloat(), state.canvasHeight.toFloat(), paint)
    paint.shader = null

    // 3. Sparkling bokeh lights and golden particles
    repeat(32) { i ->
        val bx = (i * 179 + 43) % 1080
        val by = (i * 269 + 67) % 1050
        val radius = (3.5f + (i * 7) % 16)
        paint.color = AndroidColor.argb(18 + (i % 5) * 8, 255, 225, 140)
        canvas.drawCircle(bx.toFloat(), by.toFloat(), radius, paint)
        if (i % 3 == 0) {
            paint.color = AndroidColor.argb(55, 255, 255, 255)
            canvas.drawCircle(bx.toFloat(), by.toFloat(), radius * 0.45f, paint)
        }
    }

    state.elements.filter { it.visible }.sortedBy { it.zIndex }.forEach { element ->
        val rect = RectF(element.x, element.y, element.x + element.width, element.y + element.height)
        canvas.save(); canvas.rotate(element.rotation, rect.centerX(), rect.centerY()); paint.alpha = (255 * element.opacity).toInt().coerceIn(0, 255)
        when (element.type) {
            "shape" -> {
                paint.style = Paint.Style.FILL; paint.color = elementColor(element)
                when (element.shape) {
                    "circle" -> {
                        if (element.role == "glow") {
                            val gc = elementColor(element)
                            paint.shader = android.graphics.RadialGradient(
                                rect.centerX(), rect.centerY(), rect.width() / 2f,
                                intArrayOf(
                                    AndroidColor.argb((220 * element.opacity).toInt().coerceIn(0, 255), AndroidColor.red(gc), AndroidColor.green(gc), AndroidColor.blue(gc)),
                                    AndroidColor.argb((60 * element.opacity).toInt().coerceIn(0, 255), AndroidColor.red(gc), AndroidColor.green(gc), AndroidColor.blue(gc)),
                                    AndroidColor.TRANSPARENT
                                ),
                                floatArrayOf(0f, 0.55f, 1f),
                                Shader.TileMode.CLAMP
                            )
                            canvas.drawOval(rect, paint)
                            paint.shader = null
                        } else {
                            canvas.drawOval(rect, paint)
                        }
                    }
                    "ribbon" -> {
                        val path = Path().apply {
                            val notch = rect.height() * 0.28f
                            val wing = 45f
                            moveTo(rect.left + wing, rect.top)
                            lineTo(rect.right - wing, rect.top)
                            lineTo(rect.right, rect.top + notch)
                            lineTo(rect.right - 28f, rect.centerY())
                            lineTo(rect.right, rect.bottom - notch)
                            lineTo(rect.right - wing, rect.bottom)
                            lineTo(rect.left + wing, rect.bottom)
                            lineTo(rect.left, rect.bottom - notch)
                            lineTo(rect.left + 28f, rect.centerY())
                            lineTo(rect.left, rect.top + notch)
                            close()
                        }
                        canvas.drawPath(path, paint)
                        if (element.borderWidth > 0f) {
                            paint.style = Paint.Style.STROKE
                            paint.strokeWidth = element.borderWidth
                            paint.color = color(element.borderColor)
                            canvas.drawPath(path, paint)
                            paint.style = Paint.Style.FILL
                        }
                    }
                    "slant" -> canvas.drawPath(Path().apply { moveTo(rect.left, rect.top); lineTo(rect.right, rect.top); lineTo(rect.right - 100f, rect.bottom); lineTo(rect.left, rect.bottom); close() }, paint)
                    else -> canvas.drawRoundRect(rect, element.cornerRadius, element.cornerRadius, paint)
                }
                if (element.borderWidth > 0f && element.shape != "ribbon") {
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = element.borderWidth
                    paint.color = color(element.borderColor)
                    canvas.drawRoundRect(rect, max(element.cornerRadius, if (element.shape == "circle") rect.width() / 2 else 0f), max(element.cornerRadius, if (element.shape == "circle") rect.height() / 2 else 0f), paint)
                    paint.style = Paint.Style.FILL
                }
            }
            "photo", "logo" -> {
                val bitmap = assets.images[element.id]
                if (bitmap == null && element.type == "photo") {
                    canvas.restore()
                    return@forEach
                }
                val isLeader = element.id.startsWith("leaderPhoto")
                if (isLeader) {
                    // Double gold minted rim for executive leadership medals
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 7f
                    paint.color = color(theme.accent)
                    canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2 + 5f, paint)
                    paint.strokeWidth = 2f
                    paint.color = AndroidColor.parseColor("#FFF4A8")
                    canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2 + 2f, paint)
                    paint.style = Paint.Style.FILL
                } else if (element.borderWidth > 0f) {
                    paint.style = Paint.Style.FILL
                    paint.color = color(element.borderColor)
                    val outer = RectF(rect.left - element.borderWidth, rect.top - element.borderWidth, rect.right + element.borderWidth, rect.bottom + element.borderWidth)
                    if (element.shape == "circle") canvas.drawOval(outer, paint)
                    else canvas.drawRoundRect(outer, element.cornerRadius + element.borderWidth, element.cornerRadius + element.borderWidth, paint)
                }
                if (bitmap != null) {
                    canvas.save()
                    val clip = Path().apply {
                        if (element.shape == "circle") addOval(rect, Path.Direction.CW)
                        else addRoundRect(rect, element.cornerRadius, element.cornerRadius, Path.Direction.CW)
                    }
                    canvas.clipPath(clip)
                    // Fit the complete uploaded portrait so heads and faces are never clipped by a frame.
                    val scale = min(rect.width() / bitmap.width, rect.height() / bitmap.height) * element.cropScale.coerceIn(1f, 1.15f)
                    val dw = bitmap.width * scale; val dh = bitmap.height * scale
                    val dx = element.cropX.coerceIn(-max(0f, (dw - rect.width()) / 2), max(0f, (dw - rect.width()) / 2))
                    val dy = element.cropY.coerceIn(-max(0f, (dh - rect.height()) / 2), max(0f, (dh - rect.height()) / 2))
                    val target = RectF(rect.centerX() - dw / 2 + dx, rect.centerY() - dh / 2 + dy, rect.centerX() + dw / 2 + dx, rect.centerY() + dh / 2 + dy)
                    if (element.flipHorizontal) canvas.scale(-1f, 1f, rect.centerX(), rect.centerY())
                    canvas.drawBitmap(bitmap, null, target, paint)
                    canvas.restore()
                } else if (element.type == "logo") {
                    paint.color = color(theme.accent)
                    canvas.drawRoundRect(rect, 14f, 14f, paint)
                    val logoText = element.copy(type = "text", text = "RM", fontSize = element.height * 0.42f, bold = true, color = theme.bottom, alignment = "center")
                    drawElementText(canvas, logoText, paint, color(theme.bottom), theme)
                }
            }
            "text", "icon" -> drawElementText(canvas, element, paint, elementColor(element), theme)
        }
        paint.alpha = 255; canvas.restore()
    }
    selected?.let(state::element)?.takeIf { it.visible }?.let { element ->
        val rect = RectF(element.x - 5f, element.y - 5f, element.x + element.width + 5f, element.y + element.height + 5f)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 4f; paint.color = color(theme.accent); canvas.drawRoundRect(rect, 12f, 12f, paint); paint.style = Paint.Style.FILL
        listOf(rect.left to rect.top, rect.right to rect.top, rect.left to rect.bottom, rect.right to rect.bottom).forEach { (x, y) ->
            paint.color = AndroidColor.WHITE; canvas.drawCircle(x, y, 13f, paint)
            paint.style = Paint.Style.STROKE; paint.strokeWidth = 4f; paint.color = color(theme.accent); canvas.drawCircle(x, y, 13f, paint); paint.style = Paint.Style.FILL
        }
        paint.color = color(theme.accent); canvas.drawLine(rect.centerX(), rect.top, rect.centerX(), rect.top - 35f, paint); canvas.drawCircle(rect.centerX(), rect.top - 43f, 12f, paint)
    }
    canvas.restoreToCount(save)
}

private fun drawElementText(canvas: Canvas, element: WelcomePosterElement, paint: Paint, resolvedColor: Int, theme: WelcomeTheme? = null) {
    val isGold3D = element.id == "welcomeHeadline" || element.role == "welcomeHeadline" || element.id == "platformLine"
    val isHindi = element.id == "quote" || element.id == "sloganText"
    val tp = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resolvedColor
        alpha = (255 * element.opacity).toInt()
        textSize = element.fontSize
        typeface = Typeface.create(
            if (isHindi) "sans-serif" else element.fontFamily,
            when {
                element.bold && element.italic -> Typeface.BOLD_ITALIC
                element.bold || isGold3D -> Typeface.BOLD
                element.italic -> Typeface.ITALIC
                else -> Typeface.NORMAL
            }
        )
        letterSpacing = element.letterSpacing
        if (element.shadow && !isGold3D) setShadowLayer(8f, 2f, 4f, AndroidColor.argb(170, 0, 0, 0))
    }
    val layout = StaticLayout.Builder.obtain(element.text, 0, element.text.length, tp, element.width.toInt().coerceAtLeast(1))
        .setAlignment(when (element.alignment) { "start" -> Layout.Alignment.ALIGN_NORMAL; "end" -> Layout.Alignment.ALIGN_OPPOSITE; else -> Layout.Alignment.ALIGN_CENTER })
        .setIncludePad(false).setLineSpacing(2f, element.lineSpacing).build()

    canvas.save()
    canvas.clipRect(element.x - 12f, element.y - 12f, element.x + element.width + 12f, element.y + element.height + 16f)
    val textY = element.y + ((element.height - layout.height) / 2f).coerceAtLeast(0f)

    if (isGold3D) {
        // Multi-pass dark bronze 3D extrusion shadow
        val shadowPaint = TextPaint(tp).apply {
            shader = null
            color = AndroidColor.rgb(55, 33, 4)
            setShadowLayer(0f, 0f, 0f, 0)
        }
        val shadowLayout = StaticLayout.Builder.obtain(element.text, 0, element.text.length, shadowPaint, element.width.toInt().coerceAtLeast(1))
            .setAlignment(when (element.alignment) { "start" -> Layout.Alignment.ALIGN_NORMAL; "end" -> Layout.Alignment.ALIGN_OPPOSITE; else -> Layout.Alignment.ALIGN_CENTER })
            .setIncludePad(false).setLineSpacing(2f, element.lineSpacing).build()

        for (pass in 5 downTo 1) {
            canvas.save()
            canvas.translate(element.x + pass * 1.6f, textY + pass * 1.8f)
            shadowLayout.draw(canvas)
            canvas.restore()
        }

        // Luminous metallic 3D gold gradient face
        tp.shader = LinearGradient(
            0f, textY, 0f, textY + layout.height.toFloat().coerceAtLeast(40f),
            intArrayOf(
                AndroidColor.parseColor("#FFFBE3"),
                AndroidColor.parseColor("#F5C753"),
                AndroidColor.parseColor("#DA9C20"),
                AndroidColor.parseColor("#FFF6B0"),
                AndroidColor.parseColor("#99660A")
            ),
            floatArrayOf(0f, 0.28f, 0.62f, 0.85f, 1f),
            Shader.TileMode.CLAMP
        )
        tp.setShadowLayer(4f, 0f, 2f, AndroidColor.argb(160, 0, 0, 0))
    }

    canvas.translate(element.x, textY)
    layout.draw(canvas)
    canvas.restore()
    paint.alpha = 255
}
