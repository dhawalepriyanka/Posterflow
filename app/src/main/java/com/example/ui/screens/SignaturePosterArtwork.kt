package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.compose.foundation.Canvas as ComposeCanvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.R
import kotlin.math.max
import kotlin.math.min

internal fun isSignatureTemplate(id: Int) = id in setOf(-101, -102, -103)

internal data class SignaturePosterContent(
    val name: String,
    val message: String,
    val company: String,
    val website: String = "",
    val phone: String = "",
    val photo: Uri? = null,
    val logo: Uri? = null,
    val photoScale: Float = 1f,
    val photoX: Float = 0f,
    val photoY: Float = 0f
)

internal data class SignatureImages(val portrait: Bitmap?, val logo: Bitmap?, val festival: Bitmap?)

// Decode at a bounded resolution; the same assets and drawing code power preview and export.
internal fun loadSignatureImages(context: Context, id: Int, content: SignaturePosterContent): SignatureImages {
    fun loadUri(uri: Uri?): Bitmap? = uri?.let {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return@runCatching ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it)) { decoder, info, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    val factor = (max(info.size.width, info.size.height) / 1400f).coerceAtLeast(1f)
                    decoder.setTargetSize((info.size.width / factor).toInt().coerceAtLeast(1), (info.size.height / factor).toInt().coerceAtLeast(1))
                }
            }
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(it)?.use { stream -> BitmapFactory.decodeStream(stream, null, options) }
            options.inSampleSize = 1
            while (max(options.outWidth, options.outHeight) / options.inSampleSize > 1400) options.inSampleSize *= 2
            options.inJustDecodeBounds = false
            context.contentResolver.openInputStream(it)?.use { stream -> BitmapFactory.decodeStream(stream, null, options) }
        }.getOrNull()
    }
    val resourceOptions = BitmapFactory.Options().apply { inScaled = false }
    return SignatureImages(
        portrait = loadUri(content.photo) ?: BitmapFactory.decodeResource(context.resources, R.drawable.sample_business_man, resourceOptions),
        logo = loadUri(content.logo),
        festival = if (id == -102) BitmapFactory.decodeResource(context.resources, R.drawable.studio_vishwakarma, resourceOptions) else null
    )
}

@Composable
internal fun SignaturePosterArtwork(
    id: Int,
    content: SignaturePosterContent,
    onPhotoAdjustment: (Float, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val images = remember(id, content.photo, content.logo) { loadSignatureImages(context, id, content) }
    val latestContent = rememberUpdatedState(content)
    val latestAdjustment = rememberUpdatedState(onPhotoAdjustment)
    ComposeCanvas(modifier.aspectRatio(9f / 16f)
        .semantics { contentDescription = "Personalised poster for ${content.name}" }
        .pointerInput(id) {
            detectTransformGestures { _, pan, zoom, _ ->
                val current = latestContent.value
                val factor = 360f / size.width.coerceAtLeast(1)
                latestAdjustment.value(
                    (current.photoScale * zoom).coerceIn(0.7f, 4f),
                    (current.photoX + pan.x * factor).coerceIn(-160f, 160f),
                    (current.photoY + pan.y * factor).coerceIn(-160f, 160f)
                )
            }
        }) {
        drawIntoCanvas { drawSignaturePoster(it.nativeCanvas, size.width, size.height, id, content, images) }
    }
}

internal fun renderSignaturePosterBitmap(context: Context, id: Int, content: SignaturePosterContent): Bitmap {
    val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    drawSignaturePoster(Canvas(bitmap), 1080f, 1920f, id, content, loadSignatureImages(context, id, content))
    return bitmap
}

internal fun drawSignaturePoster(canvas: Canvas, width: Float, height: Float, id: Int, content: SignaturePosterContent, images: SignatureImages) {
    val checkpoint = canvas.save()
    canvas.scale(width / 360f, height / 640f)
    canvas.clipRect(0f, 0f, 360f, 640f)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    fun color(hex: String) = Color.parseColor(hex)
    fun rect(x: Float, y: Float, w: Float, h: Float, hex: String, radius: Float = 0f) {
        paint.shader = null
        paint.color = color(hex)
        canvas.drawRoundRect(RectF(x, y, x + w, y + h), radius, radius, paint)
    }
    fun text(value: String, x: Float, y: Float, w: Float, h: Float, fontSize: Float, hex: String,
             bold: Boolean = false, serif: Boolean = false, centered: Boolean = false) {
        val tp = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color(hex)
            typeface = Typeface.create(if (serif) "serif" else "sans-serif", if (bold) Typeface.BOLD else Typeface.NORMAL)
        }
        fun layout(size: Float): StaticLayout {
            tp.textSize = size
            return StaticLayout.Builder.obtain(value, 0, value.length, tp, w.toInt().coerceAtLeast(1))
                .setAlignment(if (centered) Layout.Alignment.ALIGN_CENTER else Layout.Alignment.ALIGN_NORMAL)
                .setIncludePad(false).setLineSpacing(2f, 1f).build()
        }
        var size = fontSize
        var block = layout(size)
        while (block.height > h && size > 7f) { size -= 0.5f; block = layout(size) }
        if (block.height > h) {
            block = StaticLayout.Builder.obtain(value, 0, value.length, tp, w.toInt().coerceAtLeast(1))
                .setIncludePad(false).setMaxLines((h / (tp.fontSpacing + 2f)).toInt().coerceAtLeast(1))
                .setEllipsize(TextUtils.TruncateAt.END).build()
        }
        canvas.save()
        canvas.translate(x, y)
        canvas.clipRect(0f, 0f, w, h)
        block.draw(canvas)
        canvas.restore()
    }
    fun gradient(top: String, bottom: String) {
        paint.shader = LinearGradient(0f, 0f, 360f, 640f, color(top), color(bottom), Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, 360f, 640f, paint)
        paint.shader = null
    }
    fun image(bitmap: Bitmap?, area: RectF, rounded: Float = 0f, portrait: Boolean = false) {
        if (bitmap == null) return
        canvas.save()
        canvas.clipPath(Path().apply { addRoundRect(area, rounded, rounded, Path.Direction.CW) })
        val scale = max(area.width() / bitmap.width, area.height() / bitmap.height) * if (portrait) content.photoScale.coerceIn(0.7f, 4f) else 1f
        val w = bitmap.width * scale
        val h = bitmap.height * scale
        val maxX = max(0f, (w - area.width()) / 2)
        val maxY = max(0f, (h - area.height()) / 2)
        val left = area.centerX() - w / 2 + if (portrait) content.photoX.coerceIn(-maxX, maxX) else 0f
        val top = area.centerY() - h / 2 + if (portrait) content.photoY.coerceIn(-maxY, maxY) else 0f
        paint.color = Color.WHITE
        canvas.drawBitmap(bitmap, null, RectF(left, top, left + w, top + h), paint)
        canvas.restore()
    }
    fun brand(ink: String) {
        if (images.logo != null) {
            rect(24f, 22f, 38f, 38f, "#FFFFFF", 9f)
            val bitmap = images.logo
            val ratio = min(30f / bitmap.width, 30f / bitmap.height)
            val w = bitmap.width * ratio
            val h = bitmap.height * ratio
            canvas.drawBitmap(bitmap, null, RectF(43f - w / 2, 41f - h / 2, 43f + w / 2, 41f + h / 2), paint)
            text(content.company, 72f, 29f, 260f, 28f, 12f, ink, true)
        } else {
            rect(24f, 28f, 4f, 20f, ink, 2f)
            text(content.company, 38f, 29f, 295f, 28f, 12f, ink, true)
        }
    }
    fun footer(background: String, ink: String, accent: String) {
        rect(0f, 550f, 360f, 90f, background)
        rect(24f, 550f, 38f, 3f, accent)
        text(content.name.ifBlank { "Your name" }, 24f, 566f, 312f, 30f, 22f, ink, true)
        val contact = listOf(content.phone, content.website).filter { it.isNotBlank() }.joinToString("  •  ")
        text(contact.ifBlank { content.company }, 24f, 609f, 312f, 18f, 10f, ink)
    }
    when (id) {
        -101 -> {
            gradient("#38101F", "#120F1A")
            repeat(34) { i ->
                paint.color = Color.argb(18 + (i * 11 % 55), 245, 188, 107)
                canvas.drawCircle((i * 83 % 360).toFloat(), (i * 137 % 540).toFloat(), (3 + i * 7 % 19).toFloat(), paint)
            }
            brand("#F8DBA7")
            text("A DAY TO CELEBRATE", 24f, 81f, 312f, 20f, 10f, "#E3B777", true, centered = true)
            text("Happy\nBirthday", 24f, 109f, 312f, 115f, 48f, "#FFE6B6", serif = true, centered = true)
            rect(74f, 239f, 212f, 235f, "#DAB477", 100f)
            image(images.portrait, RectF(78f, 243f, 282f, 470f), 98f, true)
            text(content.message, 28f, 489f, 304f, 45f, 13f, "#FFF2DF", centered = true)
            footer("#190E19", "#FFF0D5", "#DAB477")
        }
        -102 -> {
            gradient("#341148", "#140F2B")
            // Preserve the generated square composition rather than stretching the deity.
            image(images.festival, RectF(0f, 83f, 360f, 443f))
            brand("#FFE8BB")
            text("सृजन का उत्सव", 24f, 126f, 151f, 35f, 18f, "#FFF1D2")
            text("विश्वकर्मा\nजयंती", 22f, 178f, 158f, 113f, 35f, "#FFD77A", true)
            text("शुभकामनाएँ", 24f, 305f, 151f, 35f, 21f, "#FFFFFF")
            rect(22f, 414f, 88f, 117f, "#E6BB74", 17f)
            image(images.portrait, RectF(25f, 417f, 107f, 528f), 14f, true)
            text(content.message, 126f, 440f, 207f, 80f, 16f, "#FFF1DA")
            footer("#170F29", "#FFF1DA", "#E6BB74")
        }
        -103 -> {
            gradient("#FCF7EC", "#E8E7DF")
            paint.color = color("#F4D4AA")
            canvas.drawCircle(327f, 254f, 113f, paint)
            brand("#15303B")
            rect(24f, 88f, 104f, 23f, "#D56836", 11f)
            text("DAILY INSPIRATION", 32f, 94f, 88f, 13f, 8f, "#FFFFFF", true)
            text("Rise.\nEvery day.", 24f, 131f, 310f, 118f, 48f, "#15303B", true)
            rect(24f, 274f, 4f, 132f, "#D56836", 2f)
            text(content.message, 39f, 273f, 140f, 147f, 24f, "#15303B", true)
            rect(194f, 278f, 143f, 250f, "#15303B", 68f)
            image(images.portrait, RectF(199f, 283f, 332f, 523f), 64f, true)
            text("PROGRESS STARTS WITH YOU", 24f, 456f, 150f, 42f, 11f, "#A14B28", true)
            footer("#15303B", "#FCF7EC", "#ED9B65")
        }
        -1 -> {
            gradient("#174A69", "#102C51")
            paint.color = Color.argb(40, 112, 239, 235)
            canvas.drawCircle(333f, 58f, 98f, paint)
            brand("#E9FFFC")
            text("HELLO, FUTURE", 24f, 101f, 310f, 19f, 12f, "#6DF0D4", true, centered = true)
            text("Welcome to\nthe team", 24f, 135f, 312f, 90f, 38f, "#FFFFFF", true, centered = true)
            rect(69f, 240f, 222f, 224f, "#6DF0D4", 112f)
            image(images.portrait, RectF(75f, 246f, 285f, 458f), 106f, true)
            text(content.name.ifBlank { "Your name" }, 24f, 476f, 312f, 42f, 29f, "#FFFFFF", true, centered = true)
            text(content.message, 42f, 524f, 276f, 20f, 12f, "#D5F9F5", centered = true)
            footer("#0C2747", "#F0FFFD", "#6DF0D4")
        }
        -2 -> {
            gradient("#344D81", "#1E315A")
            paint.color = Color.argb(45, 255, 222, 160)
            canvas.drawCircle(49f, 460f, 123f, paint)
            brand("#FFF9E7")
            rect(24f, 92f, 95f, 23f, "#FFCD70", 11f)
            text("NEW ALLIANCE", 33f, 98f, 80f, 13f, 8f, "#3A2854", true)
            text("Welcome,\npartner.", 24f, 136f, 160f, 92f, 38f, "#FFFFFF", true)
            rect(190f, 129f, 144f, 258f, "#FFF2C9", 29f)
            image(images.portrait, RectF(195f, 134f, 329f, 382f), 24f, true)
            text(content.name.ifBlank { "Your name" }, 24f, 263f, 156f, 80f, 29f, "#FFF8E8", true)
            text(content.message, 24f, 365f, 156f, 116f, 15f, "#E7EAFE")
            text("WE BUILD BETTER TOGETHER", 24f, 506f, 312f, 19f, 10f, "#FFDC93", true, centered = true)
            footer("#19284D", "#FFF8E8", "#FFCD70")
        }
        -3 -> {
            gradient("#0B7E82", "#075A70")
            paint.color = Color.argb(36, 255, 255, 255)
            canvas.drawCircle(298f, 103f, 102f, paint)
            canvas.drawCircle(54f, 435f, 46f, paint)
            brand("#E6FFFA")
            text("YOU BELONG HERE", 24f, 94f, 312f, 21f, 13f, "#ABFFF0", true, centered = true)
            rect(38f, 135f, 284f, 242f, "#B2FFF1", 42f)
            image(images.portrait, RectF(44f, 141f, 316f, 371f), 36f, true)
            text("Business family\nwelcome", 24f, 399f, 312f, 63f, 31f, "#FFFFFF", true, centered = true)
            text(content.name.ifBlank { "Your name" }, 24f, 474f, 312f, 36f, 25f, "#D8FFF8", true, centered = true)
            text(content.message, 38f, 516f, 284f, 25f, 11f, "#D8FFF8", centered = true)
            footer("#06465C", "#F1FFFD", "#ABFFF0")
        }
        -4 -> {
            gradient("#5E3D75", "#2D2457")
            paint.color = Color.argb(50, 255, 187, 135)
            canvas.drawCircle(331f, 69f, 86f, paint)
            brand("#FFF2E8")
            text("FIRST DAY", 24f, 96f, 312f, 21f, 13f, "#FFCE9E", true, centered = true)
            text("Make your mark.", 24f, 130f, 312f, 42f, 31f, "#FFFFFF", true, centered = true)
            rect(81f, 192f, 198f, 214f, "#FFCE9E", 32f)
            image(images.portrait, RectF(87f, 198f, 273f, 400f), 27f, true)
            rect(32f, 423f, 296f, 79f, "#FFFFFF", 20f)
            text(content.name.ifBlank { "Your name" }, 46f, 438f, 268f, 30f, 26f, "#402B60", true, centered = true)
            text(content.message, 48f, 474f, 264f, 19f, 11f, "#624B74", centered = true)
            footer("#261D4B", "#FFF5EF", "#FFCE9E")
        }
        -17 -> {
            gradient("#24495A", "#102C42")
            paint.color = Color.argb(36, 175, 241, 224)
            canvas.drawCircle(24f, 137f, 86f, paint)
            canvas.drawCircle(347f, 445f, 111f, paint)
            brand("#E7FFFB")
            rect(24f, 96f, 125f, 22f, "#76E1CE", 11f)
            text("EXECUTIVE ENTRY", 34f, 102f, 108f, 12f, 8f, "#163C50", true)
            text("A seat at\nthe table.", 24f, 140f, 160f, 94f, 39f, "#FFFFFF", true)
            text(content.message, 24f, 252f, 151f, 82f, 14f, "#CFEFEB")
            rect(191f, 135f, 143f, 256f, "#BDEFE3", 16f)
            image(images.portrait, RectF(196f, 140f, 329f, 386f), 12f, true)
            text(content.name.ifBlank { "Your name" }, 24f, 416f, 312f, 40f, 29f, "#FFFFFF", true, centered = true)
            text("YOUR EXPERTISE MAKES A DIFFERENCE", 24f, 467f, 312f, 18f, 10f, "#76E1CE", true, centered = true)
            footer("#0C2537", "#F1FFFC", "#76E1CE")
        }
        -18 -> {
            gradient("#4C396B", "#2B2158")
            paint.color = Color.argb(38, 255, 210, 158)
            canvas.drawCircle(301f, 96f, 126f, paint)
            brand("#FFF2E8")
            text("LEADERSHIP", 24f, 96f, 312f, 21f, 13f, "#FFD29E", true, centered = true)
            text("Welcome\naboard", 24f, 126f, 312f, 93f, 40f, "#FFFFFF", true, centered = true)
            rect(62f, 238f, 236f, 207f, "#FFDCB7", 33f)
            image(images.portrait, RectF(68f, 244f, 292f, 433f), 27f, true)
            text(content.name.ifBlank { "Your name" }, 24f, 461f, 312f, 38f, 28f, "#FFFFFF", true, centered = true)
            text(content.message, 40f, 509f, 280f, 30f, 12f, "#F0DFEE", centered = true)
            footer("#211845", "#FFF6ED", "#FFD29E")
        }
        -104 -> {
            gradient("#0A4661", "#072F52")
            paint.color = Color.argb(42, 128, 236, 226)
            canvas.drawCircle(323f, 38f, 94f, paint)
            paint.color = Color.argb(26, 255, 255, 255)
            canvas.drawCircle(311f, 50f, 66f, paint)
            brand("#E2FFF8")
            rect(24f, 91f, 99f, 22f, "#43D7C2", 11f)
            text("A NEW CHAPTER", 34f, 97f, 82f, 12f, 8f, "#083A5A", true)
            text("Welcome\naboard", 24f, 135f, 180f, 98f, 42f, "#FFFFFF", true)
            text(content.message, 24f, 249f, 147f, 94f, 15f, "#CEF7F3")
            rect(191f, 129f, 143f, 258f, "#43D7C2", 35f)
            image(images.portrait, RectF(196f, 134f, 329f, 382f), 30f, true)
            text(content.name.ifBlank { "Your name" }, 25f, 397f, 308f, 46f, 29f, "#FFFFFF", true, centered = true)
            text("THE TEAM IS GLAD YOU ARE HERE", 25f, 453f, 308f, 18f, 10f, "#63E0D1", true, centered = true)
            footer("#062B49", "#F1FFFF", "#43D7C2")
        }
        -105 -> {
            gradient("#15928E", "#115F80")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            paint.color = Color.argb(100, 202, 255, 244)
            canvas.drawCircle(180f, 280f, 150f, paint)
            canvas.drawCircle(180f, 280f, 130f, paint)
            paint.style = Paint.Style.FILL
            brand("#E9FFFB")
            text("WELCOME TO THE", 24f, 98f, 312f, 24f, 14f, "#C5FFF5", true, centered = true)
            text("CIRCLE", 24f, 126f, 312f, 52f, 42f, "#FFFFFF", true, centered = true)
            rect(68f, 185f, 224f, 224f, "#C7FFF5", 112f)
            image(images.portrait, RectF(74f, 191f, 286f, 403f), 106f, true)
            text(content.name.ifBlank { "Your name" }, 24f, 431f, 312f, 44f, 30f, "#FFFFFF", true, centered = true)
            text(content.message, 42f, 486f, 276f, 43f, 13f, "#D5FFF9", centered = true)
            footer("#10496A", "#F1FFFC", "#9BFFE6")
        }
        -106 -> {
            gradient("#5B2D71", "#281C58")
            paint.color = Color.argb(60, 255, 180, 135)
            canvas.drawCircle(26f, 82f, 60f, paint)
            canvas.drawCircle(332f, 418f, 92f, paint)
            brand("#FFF1E6")
            text("THE NEXT\nGREAT PARTNERSHIP", 24f, 104f, 178f, 80f, 26f, "#FFFFFF", true)
            rect(24f, 200f, 129f, 5f, "#FFB178", 3f)
            text("STARTS HERE", 24f, 216f, 160f, 28f, 15f, "#FFCF9E", true)
            rect(194f, 118f, 142f, 289f, "#FFB178", 28f)
            image(images.portrait, RectF(199f, 123f, 331f, 402f), 23f, true)
            text(content.name.ifBlank { "Your name" }, 24f, 292f, 153f, 72f, 31f, "#FFFFFF", true)
            text(content.message, 24f, 388f, 154f, 108f, 15f, "#F8DCE6")
            text("WELCOME TO THE JOURNEY", 24f, 504f, 312f, 19f, 11f, "#FFD2A8", true, centered = true)
            footer("#211842", "#FFF4EE", "#FFB178")
        }
        else -> error("Unknown signature template: $id")
    }
    canvas.restoreToCount(checkpoint)
}
