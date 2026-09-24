package com.example.templates

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

object TemplateImages {
    private val cache = object : LruCache<String, Bitmap>(20 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap) = value.allocationByteCount
    }
    fun read(context: Context, source: String): Bitmap? {
        if (source.isBlank()) return null
        cache.get(source)?.let { return it }
        val result = runCatching {
            fun stream() = when {
                source.startsWith("res:") -> context.resources.openRawResource(context.resources.getIdentifier(source.removePrefix("res:"), "drawable", context.packageName))
                File(source).isAbsolute -> File(source).inputStream()
                else -> context.contentResolver.openInputStream(Uri.parse(source)) ?: error("Image unavailable")
            }
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            stream().use { BitmapFactory.decodeStream(it, null, bounds) }
            val options = BitmapFactory.Options().apply {
                inSampleSize = 1
                while (max(bounds.outWidth, bounds.outHeight) / inSampleSize > 2048) inSampleSize *= 2
            }
            val bitmap = stream().use { BitmapFactory.decodeStream(it, null, options) } ?: error("Invalid image")
            val orientation = runCatching { stream().use { ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1) } }.getOrDefault(1)
            val matrix = Matrix().apply {
                when (orientation) {
                    2 -> setScale(-1f, 1f)
                    3 -> setRotate(180f)
                    4 -> setScale(1f, -1f)
                    5 -> { setRotate(90f); postScale(-1f, 1f) }
                    6 -> setRotate(90f)
                    7 -> { setRotate(270f); postScale(-1f, 1f) }
                    8 -> setRotate(270f)
                }
            }
            if (matrix.isIdentity) bitmap else Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also { bitmap.recycle() }
        }.getOrNull()
        if (result != null) cache.put(source, result)
        return result
    }
    /** Copy and normalize picked assets, so saved designs do not rely on temporary picker grants. */
    suspend fun import(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val bitmap = read(context, uri.toString()) ?: error("Please choose a readable JPG, PNG or WebP image.")
        val directory = File(context.filesDir, "template_assets").apply { mkdirs() }
        val destination = File(directory, "${UUID.randomUUID()}.png")
        destination.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        destination.absolutePath
    }
}

@Composable
fun ReadyPosterPreview(design: GeneratedPoster, modifier: Modifier = Modifier, resolution: Int = 720) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(null, design, resolution) {
        value = withContext(Dispatchers.Default) { TemplateRenderer.render(context, design, resolution) }
    }
    bitmap?.let {
        Image(it.asImageBitmap(), "${design.template.name} poster preview", modifier.fillMaxWidth().aspectRatio(1f), contentScale = ContentScale.Fit)
    } ?: androidx.compose.foundation.layout.Box(modifier.fillMaxWidth().aspectRatio(1f))
}

/**
 * Shows the raw (white background) version of a Welcome template for grid thumbnails.
 * The full colored design is only shown after the user selects the template in detail view.
 */
@Composable
fun ReadyPosterRawPreview(design: GeneratedPoster, modifier: Modifier = Modifier, resolution: Int = 360) {
    ReadyPosterPreview(design = design, modifier = modifier, resolution = resolution)
}

object TemplateRenderer {
    fun render(context: Context, design: GeneratedPoster, size: Int = 1080): Bitmap {
        require(size in 64..2160)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.scale(size / 1080f, size / 1080f)
        draw(context, canvas, design)
        return bitmap
    }

    /** Renders the design matching full preview fidelity. */
    fun renderRaw(context: Context, design: GeneratedPoster, size: Int = 1080): Bitmap {
        return render(context, design, size)
    }
    private fun color(value: String) = runCatching { Color.parseColor(value) }.getOrDefault(Color.WHITE)
    private fun paint(value: String) = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = color(value) }
    /** Resolve {{PLACEHOLDER}} tokens using branding and values from the design. */
    private fun resolvePlaceholders(text: String, d: GeneratedPoster): String {
        val b = d.branding
        val v = d.values
        return text
            .replace("{{USER_NAME}}", v[TemplateField.NAME.name]?.ifBlank { b.company } ?: b.company)
            .replace("{{NAME}}", v[TemplateField.NAME.name].orEmpty())
            .replace("{{DESIGNATION}}", v[TemplateField.DESIGNATION.name].orEmpty())
            .replace("{{BUSINESS_NAME}}", b.company)
            .replace("{{COMPANY}}", b.company)
            .replace("{{PHONE}}", b.phone)
            .replace("{{WEBSITE}}", b.website)
            .replace("{{ADDRESS}}", b.address)
            .replace("{{TAGLINE}}", b.tagline)
            .replace("{{QUOTE}}", v[TemplateField.QUOTE.name].orEmpty())
    }

    /**
     * Renders text where specific words are drawn in accent color at a larger size,
     * enabling professional typography hierarchy.
     */
    private fun textWithHighlights(
        c: Canvas, value: String, box: RectF, size: Float, normalColor: String, accentColorHex: String,
        bold: Boolean, align: String, maxLines: Int, highlightWords: List<String>
    ) {
        if (value.isBlank() || box.width() <= 0 || box.height() <= 0) return
        val lines = value.split("\n")
        val lineCount = lines.size.coerceAtMost(maxLines)
        val heightPerLine = box.height() / lineCount.coerceAtLeast(1)
        var currentY = box.top
        for ((_, lineText) in lines.take(lineCount).withIndex()) {
            val words = lineText.split(" ")
            val isHighlightLine = words.any { w -> highlightWords.any { hw -> w.trim().equals(hw, ignoreCase = true) } }
            val lineSize = if (isHighlightLine) (size * 1.28f) else size
            val lineColor = if (isHighlightLine) accentColorHex else normalColor
            val lineBox = RectF(box.left, currentY, box.right, currentY + heightPerLine)
            text(c, lineText, lineBox, lineSize, lineColor, bold, false, "sans-serif", align, 1,
                lineColor.equals(accentColorHex, ignoreCase = true))
            currentY += heightPerLine
        }
    }

    private fun draw(context: Context, c: Canvas, d: GeneratedPoster) {
        val t = d.template
        c.drawColor(color(t.baseColor))
        val artwork = TemplateImages.read(context, t.backgroundArtwork)
        if (artwork != null) image(c, artwork, RectF(0f, 0f, 1080f, 1080f), contain = true)
        else {
            background(c, t)
            if (t.category == "Festival" && t.id == -102) {
                TemplateImages.read(context, "res:studio_vishwakarma")?.let { image(c, it, RectF(0f, 0f, 1080f, 1080f), true) }
            }
            t.staticTexts.forEach { st ->
                val resolvedText = if (st.usesPlaceholders) resolvePlaceholders(st.text, d) else st.text
                val isGold = st.color.equals(t.accentColor, ignoreCase = true) || st.color == "#F3CD76" || st.color == "#F5C75D"
                if (st.highlightWords.isNotEmpty()) {
                    textWithHighlights(c, resolvedText,
                        RectF(st.x, st.y, st.x + st.width, st.y + st.height),
                        st.size, st.color, t.accentColor, st.bold, st.alignment, st.maxLines, st.highlightWords)
                } else {
                    text(c, resolvedText, RectF(st.x, st.y, st.x + st.width, st.y + st.height),
                        st.size, st.color, st.bold, false, "sans-serif", st.alignment, st.maxLines, isGold)
                }
            }
            // Skip standard name-plate for split-layout templates and Welcome templates (130-141)
            val skipNameplate = (t.photoPosition in listOf("right", "left") && t.style in (611..920)) || t.style == 220 || t.style in 130..141
            if (!skipNameplate) {
                t.slots.firstOrNull { it.enabled && it.field == TemplateField.NAME }?.let { s ->
                    val r = RectF(s.x - 12, s.y - 6, s.x + s.width + 12, s.y + s.height + 6)
                    val plate = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        shader = LinearGradient(r.left, r.top, r.right, r.bottom,
                            intArrayOf(color(t.baseColor), Color.rgb(10, 15, 24), color(t.baseColor)),
                            null, Shader.TileMode.CLAMP)
                    }
                    c.drawRoundRect(r, 16f, 16f, plate)
                    c.drawRoundRect(r, 16f, 16f, paint(t.accentColor).apply { style = Paint.Style.STROKE; strokeWidth = 2.5f })
                    val tabW = 20f
                    val tabP = paint(t.accentColor)
                    c.drawRect(r.left - tabW, r.centerY() - 12f, r.left, r.centerY() + 12f, tabP)
                    c.drawRect(r.right, r.centerY() - 12f, r.right + tabW, r.centerY() + 12f, tabP)
                }
            }
        }
        t.slots.filter { it.enabled }.forEach { slot ->
            val box = RectF(slot.x, slot.y, slot.x + slot.width, slot.y + slot.height)
            if (slot.field == TemplateField.PHOTO) photo(context, c, d, slot, box)
            else text(c, d.values[slot.field.name].orEmpty().ifBlank {
                if (!slot.required) "" else when (slot.field) {
                    TemplateField.NAME -> "YOUR NAME"
                    TemplateField.DESIGNATION -> "DESIGNATION"
                    TemplateField.ACHIEVEMENT -> "ACHIEVEMENT"
                    TemplateField.AMOUNT -> "AMOUNT"
                    TemplateField.QUOTE -> "YOUR INSPIRING QUOTE"
                    TemplateField.COMPANY -> "YOUR COMPANY"
                    else -> ""
                }
            }, box, slot.fontSize, slot.color, slot.bold, slot.italic, slot.font, slot.alignment, slot.maxLines)
        }
        if (t.style == 620) {
            drawMotivationMountainElements(context, c, d)
        }
        if (t.style == 220) {
            drawParchmentGratitudeElements(context, c, d)
        }
        if (t.header.enabled) band(context, c, t.header, 0f, d.branding)
        if (t.footer.enabled) {
            if (t.style in 130..141) drawFixedBrandingFooter(context, c, t, d)
            else band(context, c, t.footer, 1080f - t.footer.height, d.branding)
        }
    }

    private fun background(c: Canvas, t: PosterTemplate) {
        val accent = color(t.accentColor)
        val base = color(t.baseColor)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)

        // 1. Deep multi-layer base gradient
        p.shader = LinearGradient(0f, 0f, 1080f, 1080f,
            intArrayOf(base, Color.rgb(8, 12, 20), base),
            floatArrayOf(0f, 0.55f, 1f), Shader.TileMode.CLAMP)
        c.drawRect(0f, 0f, 1080f, 1080f, p)

        // 2. Radial focal spotlight
        p.shader = RadialGradient(540f, 480f, 650f,
            intArrayOf((accent and 0x00FFFFFF) or 0x28000000, Color.TRANSPARENT),
            null, Shader.TileMode.CLAMP)
        c.drawRect(0f, 100f, 1080f, 960f, p)
        p.shader = null

        // 3. Style-specific decorative compositions
        when (t.style) {
            101 -> { // Royal Purple Welcome: Luxury gold dual rings, particles, corner flourishes
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 140
                c.drawCircle(540f, 480f, 235f, p)
                p.strokeWidth = 1.5f; p.alpha = 70
                c.drawCircle(540f, 480f, 255f, p)
                p.style = Paint.Style.FILL; p.alpha = 200
                for (i in 0 until 18) {
                    val angle = (i * 20) * (Math.PI / 180.0)
                    val px = 540f + (245f * Math.cos(angle)).toFloat()
                    val py = 480f + (245f * Math.sin(angle)).toFloat()
                    c.drawCircle(px, py, if (i % 2 == 0) 5f else 3f, p)
                }
            }
            102 -> { // Executive Blue: Diagonal corporate split geometry
                p.style = Paint.Style.FILL; p.color = Color.rgb(15, 35, 65); p.alpha = 180
                val path = Path().apply { moveTo(0f, 110f); lineTo(580f, 110f); lineTo(320f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(path, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 200
                c.drawLine(580f, 110f, 320f, 935f, p)
            }
            103 -> { // Black Gold Elite: Double gold border framing with mitered corners
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 180
                c.drawRect(35f, 130f, 1045f, 915f, p)
                c.drawRect(45f, 140f, 1035f, 905f, p)
                // Gold corner diamonds
                p.style = Paint.Style.FILL; p.alpha = 240
                for (cx in listOf(40f, 1040f)) {
                    for (cy in listOf(135f, 910f)) {
                        val diamond = Path().apply { moveTo(cx, cy - 8f); lineTo(cx + 8f, cy); lineTo(cx, cy + 8f); lineTo(cx - 8f, cy); close() }
                        c.drawPath(diamond, p)
                    }
                }
            }
            104 -> { // Purple Success: Dynamic curved wave arcs
                p.style = Paint.Style.STROKE; p.strokeWidth = 3.5f; p.color = accent; p.alpha = 160
                val wave = Path().apply { moveTo(-40f, 380f); cubicTo(280f, 220f, 780f, 540f, 1120f, 320f) }
                c.drawPath(wave, p)
                val wave2 = Path().apply { moveTo(-40f, 440f); cubicTo(320f, 620f, 800f, 280f, 1120f, 520f) }
                p.strokeWidth = 2f; p.alpha = 100
                c.drawPath(wave2, p)
            }
            105 -> { // Emerald Growth: Growth-inspired geometric crest
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = accent; p.alpha = 140
                c.drawCircle(540f, 490f, 225f, p)
                p.style = Paint.Style.FILL; p.alpha = 50
                val crest = Path().apply { moveTo(540f, 230f); lineTo(760f, 490f); lineTo(540f, 750f); lineTo(320f, 490f); close() }
                c.drawPath(crest, p)
            }
            106 -> { // Burgundy Journey: Ribbon banner system
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 35
                c.drawRect(0f, 215f, 1080f, 245f, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 180
                c.drawLine(0f, 215f, 1080f, 215f, p)
                c.drawLine(0f, 245f, 1080f, 245f, p)
            }
            107 -> { // Corporate Cyan: Clean geometric polygon cuts
                p.style = Paint.Style.FILL; p.color = Color.rgb(10, 35, 60); p.alpha = 180
                val poly = Path().apply { moveTo(0f, 110f); lineTo(480f, 110f); lineTo(560f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(poly, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 220
                c.drawLine(480f, 110f, 560f, 935f, p)
            }
            108 -> { // Team Power: Energetic diagonal power chevrons
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 25
                for (i in 0..3) {
                    val chev = Path().apply { moveTo(-100f + i * 320f, 110f); lineTo(100f + i * 320f, 110f); lineTo(0f + i * 320f, 935f); lineTo(-200f + i * 320f, 935f); close() }
                    c.drawPath(chev, p)
                }
            }
            109 -> { // Golden Beginning: Ambient rays & gold dust
                p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f; p.color = accent; p.alpha = 60
                for (i in 0 until 12) {
                    val angle = (i * 30) * (Math.PI / 180.0)
                    val endX = 540f + (550f * Math.cos(angle)).toFloat()
                    val endY = 470f + (550f * Math.sin(angle)).toFloat()
                    c.drawLine(540f, 470f, endX, endY, p)
                }
            }
            110 -> { // Brighter Future: Modern corporate grid pillars
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 20
                for (i in 0..4) {
                    c.drawRoundRect(RectF(120f + i * 180f, 160f, 220f + i * 180f, 900f), 20f, 20f, p)
                }
            }

            // ---- NEW WELCOME TEMPLATES (130–141) ----
            130 -> { // Royal Gold Welcome: Gold concentric rings, confetti particles, corner flourishes
                // Full-bleed deep purple-to-black gradient
                p.shader = RadialGradient(540f, 400f, 700f,
                    intArrayOf(Color.rgb(35, 10, 60), Color.rgb(10, 5, 20)),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Gold concentric ring set
                p.style = Paint.Style.STROKE; p.color = accent; p.alpha = 100; p.strokeWidth = 2.5f
                c.drawCircle(540f, 350f, 280f, p)
                p.strokeWidth = 1.5f; p.alpha = 60; c.drawCircle(540f, 350f, 310f, p)
                p.alpha = 35; c.drawCircle(540f, 350f, 340f, p)
                // Gold confetti particles scattered
                p.style = Paint.Style.FILL; p.alpha = 180
                for (i in 0..30) {
                    val cx = 40f + ((i * 211) % 1000); val cy = 30f + ((i * 137) % 850)
                    val sz = if (i % 4 == 0) 5f else if (i % 2 == 0) 3f else 2f
                    c.drawCircle(cx, cy, sz, p)
                }
                // Corner ornamental flourishes
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.alpha = 140
                val cs = 50f
                c.drawLine(20f, 20f, 20f + cs, 20f, p); c.drawLine(20f, 20f, 20f, 20f + cs, p)
                c.drawLine(1060f, 20f, 1060f - cs, 20f, p); c.drawLine(1060f, 20f, 1060f, 20f + cs, p)
                c.drawLine(20f, 860f, 20f + cs, 860f, p); c.drawLine(20f, 860f, 20f, 860f - cs, p)
                c.drawLine(1060f, 860f, 1060f - cs, 860f, p); c.drawLine(1060f, 860f, 1060f, 860f - cs, p)
            }
            131 -> { // Corporate Blue Welcome: Diagonal split panel, geometric grid, corporate glow
                // Dark navy full fill
                p.style = Paint.Style.FILL; p.color = Color.rgb(8, 18, 42)
                c.drawRect(0f, 0f, 1080f, 880f, p)
                // Diagonal split panel (left side)
                p.color = Color.rgb(12, 30, 65); p.alpha = 220
                val split = Path().apply { moveTo(0f, 0f); lineTo(530f, 0f); lineTo(470f, 880f); lineTo(0f, 880f); close() }
                c.drawPath(split, p)
                // Accent divider line
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 200
                c.drawLine(530f, 0f, 470f, 880f, p)
                // Subtle horizontal grid lines
                p.strokeWidth = 0.5f; p.alpha = 40
                for (i in 1..6) c.drawLine(0f, i * 120f, 1080f, i * 120f, p)
                // Radial glow at top-right
                p.style = Paint.Style.FILL
                p.shader = RadialGradient(780f, 200f, 350f,
                    intArrayOf((accent and 0x00FFFFFF) or 0x20000000, Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
            }
            132 -> { // Premium Purple Welcome: Multi-gradient, metallic frame, sparkle effects
                // Purple/magenta/navy multi-point gradient
                p.shader = RadialGradient(540f, 400f, 650f,
                    intArrayOf(Color.rgb(80, 20, 100), Color.rgb(26, 5, 53)),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Secondary magenta radial
                p.shader = RadialGradient(200f, 250f, 400f,
                    intArrayOf(Color.argb(60, 200, 50, 180), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Gold decorative frame
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = accent; p.alpha = 160
                c.drawRect(30f, 25f, 1050f, 855f, p)
                p.strokeWidth = 1f; p.alpha = 80
                c.drawRect(40f, 35f, 1040f, 845f, p)
                // Sparkle effects
                p.style = Paint.Style.FILL; p.color = Color.WHITE; p.alpha = 180
                for (i in 0..15) {
                    val sx = 50f + ((i * 197) % 980); val sy = 30f + ((i * 143) % 820)
                    c.drawCircle(sx, sy, if (i % 3 == 0) 3f else 1.5f, p)
                }
            }
            133 -> { // Elegant White & Gold: Cream/ivory base, double gold border, corner ornaments
                // Warm ivory full-canvas fill
                p.style = Paint.Style.FILL; p.color = Color.rgb(250, 246, 237)
                c.drawRect(0f, 0f, 1080f, 880f, p)
                // Subtle warm texture gradient
                p.shader = RadialGradient(540f, 440f, 600f,
                    intArrayOf(Color.rgb(255, 252, 245), Color.rgb(245, 238, 220)),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Double gold border frame
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 200
                c.drawRect(35f, 25f, 1045f, 855f, p)
                p.strokeWidth = 1.5f; p.alpha = 120
                c.drawRect(45f, 35f, 1035f, 845f, p)
                // Gold corner ornament diamonds
                p.style = Paint.Style.FILL; p.alpha = 230
                for (cx in listOf(40f, 1040f)) {
                    for (cy in listOf(30f, 850f)) {
                        val d = Path().apply { moveTo(cx, cy - 10f); lineTo(cx + 10f, cy); lineTo(cx, cy + 10f); lineTo(cx - 10f, cy); close() }
                        c.drawPath(d, p)
                    }
                }
                // Decorative horizontal divider line
                p.style = Paint.Style.STROKE; p.strokeWidth = 1f; p.alpha = 100
                c.drawLine(120f, 165f, 960f, 165f, p)
            }
            134 -> { // Red Celebration: Red/maroon gradient, confetti burst, ribbon strips
                // Deep red/maroon gradient
                p.shader = LinearGradient(0f, 0f, 1080f, 880f,
                    intArrayOf(Color.rgb(80, 10, 10), Color.rgb(40, 5, 5)),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Radial highlight
                p.shader = RadialGradient(540f, 350f, 500f,
                    intArrayOf(Color.argb(50, 220, 40, 40), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Gold confetti burst
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 160
                for (i in 0..40) {
                    val cx = 30f + ((i * 223) % 1020); val cy = 20f + ((i * 151) % 840)
                    if (i % 5 == 0) {
                        val star = Path().apply { moveTo(cx, cy - 4f); lineTo(cx + 2f, cy - 1f); lineTo(cx + 5f, cy); lineTo(cx + 2f, cy + 1f); moveTo(cx, cy + 4f); lineTo(cx - 2f, cy + 1f); lineTo(cx - 5f, cy); lineTo(cx - 2f, cy - 1f) }
                        c.drawPath(star, p)
                    } else c.drawCircle(cx, cy, if (i % 3 == 0) 3f else 1.5f, p)
                }
                // Angled ribbon strips
                p.style = Paint.Style.FILL; p.alpha = 30
                c.drawRect(0f, 160f, 1080f, 175f, p)
                p.alpha = 20; c.drawRect(0f, 180f, 1080f, 190f, p)
            }
            135 -> { // Modern Green: Emerald gradient, curved organic shapes, growth arcs
                // Deep emerald gradient
                p.shader = LinearGradient(0f, 0f, 1080f, 880f,
                    intArrayOf(Color.rgb(6, 43, 32), Color.rgb(3, 20, 15)),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Radial green glow
                p.shader = RadialGradient(300f, 350f, 450f,
                    intArrayOf(Color.argb(40, 52, 211, 153), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Curved organic wave arcs
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = accent; p.alpha = 80
                val wave1 = Path().apply { moveTo(-40f, 500f); cubicTo(250f, 350f, 700f, 600f, 1120f, 400f) }
                c.drawPath(wave1, p)
                p.strokeWidth = 1.5f; p.alpha = 45
                val wave2 = Path().apply { moveTo(-40f, 560f); cubicTo(300f, 700f, 750f, 350f, 1120f, 550f) }
                c.drawPath(wave2, p)
                // Vertical left accent bar
                p.style = Paint.Style.FILL; p.alpha = 60
                c.drawRoundRect(RectF(520f, 30f, 528f, 870f), 4f, 4f, p)
            }
            136 -> { // Orange Success: Orange-to-black, trophy outline, power chevrons
                // Orange to dark gradient
                p.shader = RadialGradient(540f, 300f, 600f,
                    intArrayOf(Color.rgb(45, 20, 0), Color.rgb(15, 5, 0)),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Orange spotlight
                p.shader = RadialGradient(540f, 350f, 400f,
                    intArrayOf(Color.argb(45, 249, 115, 22), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Trophy outline (large, background watermark)
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 30
                c.drawArc(RectF(420f, 350f, 660f, 520f), 0f, 180f, false, p)
                c.drawLine(540f, 520f, 540f, 580f, p)
                c.drawLine(480f, 580f, 600f, 580f, p)
                c.drawLine(470f, 585f, 610f, 585f, p)
                // Power chevron strips
                p.style = Paint.Style.FILL; p.alpha = 15
                for (i in 0..4) {
                    val chev = Path().apply { moveTo(-50f + i * 260f, 0f); lineTo(100f + i * 260f, 0f); lineTo(20f + i * 260f, 880f); lineTo(-130f + i * 260f, 880f); close() }
                    c.drawPath(chev, p)
                }
            }
            137 -> { // Luxury Black: Pure black, thin gold double frame, corner diamonds
                // Pure black fill
                p.style = Paint.Style.FILL; p.color = Color.rgb(8, 8, 8)
                c.drawRect(0f, 0f, 1080f, 880f, p)
                // Subtle dark texture grid
                p.style = Paint.Style.STROKE; p.strokeWidth = 0.5f; p.color = Color.rgb(25, 25, 25)
                for (i in 0..10) {
                    c.drawLine(0f, i * 88f, 1080f, i * 88f, p)
                    c.drawLine(i * 108f, 0f, i * 108f, 880f, p)
                }
                // Double gold border frame
                p.color = accent; p.strokeWidth = 2f; p.alpha = 200
                c.drawRect(30f, 25f, 1050f, 855f, p)
                p.strokeWidth = 1f; p.alpha = 100
                c.drawRect(40f, 35f, 1040f, 845f, p)
                // Gold corner diamonds
                p.style = Paint.Style.FILL; p.alpha = 240
                for (cx in listOf(35f, 1045f)) {
                    for (cy in listOf(30f, 850f)) {
                        val dm = Path().apply { moveTo(cx, cy - 9f); lineTo(cx + 9f, cy); lineTo(cx, cy + 9f); lineTo(cx - 9f, cy); close() }
                        c.drawPath(dm, p)
                    }
                }
                // Dramatic spotlight glow
                p.style = Paint.Style.FILL
                p.shader = RadialGradient(540f, 400f, 450f,
                    intArrayOf(Color.argb(25, 212, 175, 55), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
            }
            138 -> { // Fresh Gradient: Blue/cyan/violet multi-color gradient, glass panels
                // Multi-color gradient base
                p.shader = LinearGradient(0f, 0f, 1080f, 880f,
                    intArrayOf(Color.rgb(15, 10, 42), Color.rgb(10, 50, 80), Color.rgb(20, 10, 50)),
                    floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Cyan radial glow
                p.shader = RadialGradient(350f, 350f, 450f,
                    intArrayOf(Color.argb(50, 6, 182, 212), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Violet secondary glow
                p.shader = RadialGradient(800f, 600f, 350f,
                    intArrayOf(Color.argb(35, 139, 92, 246), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Glass-morphism panels
                p.style = Paint.Style.FILL; p.color = Color.WHITE; p.alpha = 12
                c.drawRoundRect(RectF(500f, 200f, 1050f, 650f), 24f, 24f, p)
                p.alpha = 8
                c.drawRoundRect(RectF(480f, 220f, 1060f, 670f), 28f, 28f, p)
                // Panel border glow
                p.style = Paint.Style.STROKE; p.strokeWidth = 1f; p.color = accent; p.alpha = 50
                c.drawRoundRect(RectF(500f, 200f, 1050f, 650f), 24f, 24f, p)
            }
            139 -> { // Floral Welcome: Pastel pink/lavender base, floral corner paths, vine accents
                // Soft pastel pink-to-lavender base
                p.shader = LinearGradient(0f, 0f, 1080f, 880f,
                    intArrayOf(Color.rgb(253, 242, 248), Color.rgb(245, 235, 255), Color.rgb(253, 242, 248)),
                    floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Floral decorations (top-left corner)
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 50
                c.drawCircle(60f, 60f, 50f, p); c.drawCircle(30f, 100f, 35f, p); c.drawCircle(100f, 30f, 35f, p)
                // Vine curve (top-left)
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.alpha = 80
                val vine1 = Path().apply { moveTo(0f, 160f); cubicTo(80f, 120f, 120f, 160f, 160f, 100f) }
                c.drawPath(vine1, p)
                // Floral decorations (bottom-right corner)
                p.style = Paint.Style.FILL; p.alpha = 50
                c.drawCircle(1020f, 820f, 50f, p); c.drawCircle(1050f, 780f, 35f, p); c.drawCircle(980f, 850f, 35f, p)
                // Vine curve (bottom-right)
                p.style = Paint.Style.STROKE; p.alpha = 80
                val vine2 = Path().apply { moveTo(1080f, 720f); cubicTo(1000f, 760f, 960f, 720f, 920f, 780f) }
                c.drawPath(vine2, p)
                // Soft inner border
                p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f; p.alpha = 60
                c.drawRoundRect(RectF(25f, 20f, 1055f, 860f), 20f, 20f, p)
                // Small petal accents
                p.style = Paint.Style.FILL; p.alpha = 40
                for (i in 0..8) {
                    val px = 80f + ((i * 127) % 920); val py = 80f + ((i * 97) % 780)
                    c.drawCircle(px, py, 8f, p)
                }
            }
            140 -> { // Indian Traditional: Maroon/saffron gradient, arch frame, paisley border motifs
                // Maroon/saffron gradient
                p.shader = LinearGradient(0f, 0f, 0f, 880f,
                    intArrayOf(Color.rgb(80, 15, 15), Color.rgb(50, 8, 8)),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Saffron radial glow
                p.shader = RadialGradient(540f, 350f, 500f,
                    intArrayOf(Color.argb(40, 245, 158, 11), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 880f, p); p.shader = null
                // Traditional arch outline
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 160
                val arch = Path().apply {
                    moveTo(200f, 870f); lineTo(200f, 300f)
                    arcTo(RectF(200f, 100f, 880f, 500f), 180f, 180f, false)
                    lineTo(880f, 870f)
                }
                c.drawPath(arch, p)
                // Inner arch
                p.strokeWidth = 1.5f; p.alpha = 80
                val innerArch = Path().apply {
                    moveTo(220f, 870f); lineTo(220f, 310f)
                    arcTo(RectF(220f, 120f, 860f, 500f), 180f, 180f, false)
                    lineTo(860f, 870f)
                }
                c.drawPath(innerArch, p)
                // Decorative border dots (top)
                p.style = Paint.Style.FILL; p.alpha = 140
                for (i in 0..20) c.drawCircle(45f + i * 48f, 15f, 4f, p)
                // Paisley-inspired curve elements at corners
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.alpha = 100
                val paisle = Path().apply { moveTo(30f, 50f); cubicTo(50f, 20f, 90f, 20f, 80f, 60f); cubicTo(70f, 90f, 30f, 80f, 30f, 50f) }
                c.drawPath(paisle, p)
                c.save(); c.scale(-1f, 1f, 1080f / 2f, 0f); c.drawPath(paisle, p); c.restore()
            }
            141 -> { // Minimal Professional: Clean white/gray base, bold accent bar, grid typography
                // Clean white fill
                p.style = Paint.Style.FILL; p.color = Color.rgb(250, 250, 250)
                c.drawRect(0f, 0f, 1080f, 880f, p)
                // Bold accent bar at left edge
                p.color = accent
                c.drawRect(0f, 0f, 8f, 880f, p)
                // Secondary thin bar
                p.color = Color.rgb(209, 213, 219); p.alpha = 80
                c.drawRect(14f, 0f, 16f, 880f, p)
                // Subtle horizontal grid
                p.style = Paint.Style.STROKE; p.strokeWidth = 0.5f; p.color = Color.rgb(229, 231, 235)
                for (i in 1..6) c.drawLine(0f, i * 125f, 1080f, i * 125f, p)
                // Bold horizontal accent line at top
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 15
                c.drawRect(0f, 180f, 1080f, 185f, p)
            }

            201 -> { // 01 Royal Birthday: Spotlight halo & gift ornament accents
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 25
                c.drawCircle(540f, 485f, 320f, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.alpha = 140
                c.drawCircle(540f, 485f, 340f, p)
            }
            202 -> { // 02 Golden Wishes: Diagonal luxury split with right-anchored framing
                p.style = Paint.Style.FILL; p.color = Color.rgb(24, 20, 16); p.alpha = 200
                val poly = Path().apply { moveTo(500f, 110f); lineTo(1080f, 110f); lineTo(1080f, 935f); lineTo(420f, 935f); close() }
                c.drawPath(poly, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 180
                c.drawLine(500f, 110f, 420f, 935f, p)
            }
            203 -> { // 03 Blue Celebration: Left-anchored portrait panel and right message glow
                p.style = Paint.Style.FILL; p.color = Color.rgb(8, 22, 45); p.alpha = 190
                val leftCard = RectF(50f, 140f, 500f, 710f)
                c.drawRoundRect(leftCard, 24f, 24f, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 130
                c.drawRoundRect(leftCard, 24f, 24f, p)
            }
            204 -> { // 04 Executive Birthday: Clean dual horizontal ribbons
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 25
                c.drawRect(0f, 210f, 1080f, 265f, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 150
                c.drawLine(0f, 210f, 1080f, 210f, p)
                c.drawLine(0f, 265f, 1080f, 265f, p)
            }
            205 -> { // 05 Purple Celebration: Concentric celebratory radiant rings
                p.style = Paint.Style.STROKE; p.color = accent
                for (r in listOf(240f, 310f, 380f)) {
                    p.strokeWidth = if (r == 310f) 3f else 1.5f; p.alpha = (160 - r * 0.25f).toInt().coerceAtLeast(30)
                    c.drawCircle(540f, 475f, r, p)
                }
            }
            206 -> { // 06 Burgundy Birthday: Filigree luxury corner brackets
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 160
                val inset = 40f
                c.drawRect(inset, 130f, 1080f - inset, 915f, p)
                c.drawRect(inset + 10f, 140f, 1080f - inset - 10f, 905f, p)
            }
            207 -> { // 07 Emerald Wishes: Hexagonal ornamental frame
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = accent; p.alpha = 140
                c.drawCircle(540f, 475f, 260f, p)
                p.style = Paint.Style.FILL; p.alpha = 30
                c.drawCircle(540f, 475f, 260f, p)
            }
            208 -> { // 08 Midnight Celebration: Golden starlight constellation and deep spotlight
                p.style = Paint.Style.FILL; p.color = Color.rgb(20, 35, 75); p.alpha = 120
                c.drawCircle(540f, 490f, 300f, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 160
                c.drawCircle(540f, 490f, 300f, p)
            }
            209 -> { // 09 Festive Gold: Sunburst celebration rays
                p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f; p.color = accent; p.alpha = 50
                for (i in 0 until 16) {
                    val rad = (i * 22.5) * (Math.PI / 180.0)
                    c.drawLine(540f, 475f, 540f + (550f * Math.cos(rad)).toFloat(), 475f + (550f * Math.sin(rad)).toFloat(), p)
                }
            }
            210 -> { // 10 Corporate Birthday: Modern corporate diagonal accents
                p.style = Paint.Style.FILL; p.color = Color.rgb(18, 48, 78); p.alpha = 170
                val poly = Path().apply { moveTo(0f, 110f); lineTo(380f, 110f); lineTo(260f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(poly, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 200
                c.drawLine(380f, 110f, 260f, 935f, p)
            }
            211 -> { // 11 Black Elite: Double obsidian champagne bevels
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = accent; p.alpha = 190
                c.drawRect(45f, 135f, 1035f, 910f, p)
                p.style = Paint.Style.FILL; p.alpha = 25
                c.drawRect(45f, 135f, 1035f, 910f, p)
            }
            212 -> { // 12 Bright Celebration: Vibrant celebration aura & neon rings
                p.style = Paint.Style.STROKE; p.strokeWidth = 3.5f; p.color = accent; p.alpha = 160
                c.drawCircle(540f, 475f, 280f, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = Color.rgb(100, 160, 255); p.alpha = 130
                c.drawCircle(540f, 475f, 310f, p)
            }
            111 -> { // Minimal Typography: Gold underline accent + subtle side bars
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 220
                c.drawRect(50f, 582f, 560f, 590f, p)
                p.alpha = 80; p.color = accent
                c.drawRect(50f, 596f, 400f, 600f, p)
                // Right-side vertical accent for portrait zone
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.alpha = 140
                c.drawLine(660f, 140f, 660f, 580f, p)
            }
            112 -> { // Modern Frame: Double border with decorative corner brackets
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 200
                c.drawRect(50f, 130f, 1030f, 920f, p)
                p.strokeWidth = 1.5f; p.alpha = 100
                c.drawRect(62f, 142f, 1018f, 908f, p)
                // Corner L-brackets (top-left, top-right, bottom-left, bottom-right)
                p.strokeWidth = 4f; p.alpha = 255
                val bLen = 45f
                for ((cx, cy) in listOf(50f to 130f, 1030f to 130f, 50f to 920f, 1030f to 920f)) {
                    val dx = if (cx < 540f) bLen else -bLen
                    val dy = if (cy < 540f) bLen else -bLen
                    c.drawLine(cx, cy, cx + dx, cy, p)
                    c.drawLine(cx, cy, cx, cy + dy, p)
                }
            }

            // ---- PROFESSIONAL WELCOME TEMPLATES (120-123) ----
            120 -> { // Modern Abstract Split: Indigo & cyan diagonal geometric panels
                p.style = Paint.Style.FILL; p.color = Color.rgb(12, 28, 58); p.alpha = 220
                val panel = Path().apply { moveTo(0f, 110f); lineTo(520f, 110f); lineTo(460f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(panel, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 4f; p.color = accent; p.alpha = 240
                c.drawLine(520f, 110f, 460f, 935f, p)
                // Decorative cyan underline and dots
                p.style = Paint.Style.FILL; p.color = Color.rgb(0, 210, 211); p.alpha = 200
                c.drawRect(50f, 370f, 430f, 376f, p)
                p.color = accent; p.alpha = 180
                for (i in 0..4) { c.drawCircle(60f + i * 28f, 395f, 4f, p) }
            }
            121 -> { // Executive Dark Gold Luxury: Obsidian background with mitered gold frame
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 220
                c.drawRect(40f, 125f, 1040f, 920f, p)
                p.strokeWidth = 1f; p.alpha = 120
                c.drawRect(52f, 137f, 1028f, 908f, p)
                // Corner diamonds
                p.style = Paint.Style.FILL; p.alpha = 240; p.color = accent
                for ((cx, cy) in listOf(46f to 131f, 1034f to 131f, 46f to 914f, 1034f to 914f)) {
                    val dm = Path().apply { moveTo(cx, cy - 10f); lineTo(cx + 10f, cy); lineTo(cx, cy + 10f); lineTo(cx - 10f, cy); close() }
                    c.drawPath(dm, p)
                }
                // Halo circle around portrait
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.alpha = 150
                c.drawCircle(540f, 480f, 225f, p)
                p.strokeWidth = 1f; p.alpha = 80
                c.drawCircle(540f, 480f, 245f, p)
            }
            122 -> { // Vibrant Coral & Indigo Gradient Mesh
                p.style = Paint.Style.FILL
                p.shader = LinearGradient(0f, 0f, 1080f, 1080f,
                    intArrayOf(Color.rgb(67, 56, 202), Color.rgb(126, 34, 206), Color.rgb(244, 63, 94)),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 1080f, p)
                p.shader = null
                // Soft wave arcs
                p.style = Paint.Style.STROKE; p.strokeWidth = 4f; p.color = Color.WHITE; p.alpha = 70
                val wv = Path().apply { moveTo(-60f, 450f); cubicTo(300f, 320f, 750f, 580f, 1140f, 380f) }
                c.drawPath(wv, p)
                // Translucent card under content
                p.style = Paint.Style.FILL; p.color = Color.BLACK; p.alpha = 85
                c.drawRoundRect(RectF(60f, 150f, 1020f, 915f), 32f, 32f, p)
            }
            123 -> { // Corporate Minimal Clean: Premium ivory with royal blue corporate header
                p.style = Paint.Style.FILL; p.color = Color.rgb(248, 250, 252)
                c.drawRect(0f, 0f, 1080f, 1080f, p)
                // Royal blue corporate top banner
                p.color = Color.rgb(30, 58, 138)
                val topBanner = Path().apply { moveTo(0f, 0f); lineTo(1080f, 0f); lineTo(1080f, 170f); lineTo(0f, 130f); close() }
                c.drawPath(topBanner, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 4f; p.color = accent; p.alpha = 220
                c.drawLine(0f, 130f, 1080f, 170f, p)
                // Subtle sidebar
                p.style = Paint.Style.FILL; p.color = Color.rgb(238, 242, 246)
                c.drawRect(0f, 130f, 480f, 935f, p)
            }

            // ---- MOTIVATION PERSON-PHOTO LAYOUTS (611-613) ----
            611 -> { // motivation_001: Blue/Navy split — content LEFT, photo RIGHT
                p.style = Paint.Style.FILL
                p.shader = LinearGradient(0f, 110f, 560f, 935f,
                    intArrayOf(Color.rgb(5, 12, 28), Color.rgb(8, 18, 40)),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 110f, 560f, 935f, p)
                p.shader = null
                p.color = Color.rgb(10, 22, 52); p.alpha = 160
                c.drawRect(540f, 110f, 1080f, 935f, p)
                // Diagonal separator line
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 200
                c.drawLine(555f, 110f, 525f, 935f, p)
                // Gold accent bars in content area
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 200
                c.drawRect(60f, 548f, 500f, 554f, p)
                p.alpha = 120; c.drawRect(60f, 560f, 360f, 564f, p)
                // Small gold bullet squares
                p.alpha = 200
                for (i in 0..2) { c.drawRect(60f, 580f + i * 38f, 73f, 593f + i * 38f, p) }
                // Top-left corner accent bracket
                p.style = Paint.Style.STROKE; p.strokeWidth = 3.5f; p.alpha = 200
                c.drawLine(60f, 128f, 108f, 128f, p); c.drawLine(60f, 128f, 60f, 176f, p)
            }
            612 -> { // motivation_002: Dark slate/amber — photo LEFT, quote RIGHT
                p.style = Paint.Style.FILL
                p.shader = LinearGradient(520f, 110f, 1080f, 935f,
                    intArrayOf(Color.rgb(22, 16, 6), Color.rgb(35, 25, 8)),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(520f, 110f, 1080f, 935f, p)
                p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 180
                c.drawLine(525f, 110f, 555f, 935f, p)
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 200
                c.drawRect(560f, 548f, 1020f, 554f, p)
                p.alpha = 120; c.drawRect(560f, 560f, 860f, 564f, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 3.5f; p.color = accent; p.alpha = 200
                c.drawLine(972f, 128f, 1020f, 128f, p); c.drawLine(1020f, 128f, 1020f, 176f, p)
            }
            613 -> { // motivation_003: Warm cream/brown — center-bottom large photo
                p.style = Paint.Style.FILL
                p.shader = LinearGradient(0f, 0f, 0f, 1080f,
                    intArrayOf(color(t.baseColor), Color.rgb(30, 20, 10), color(t.baseColor)),
                    floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 1080f, p)
                p.shader = null
                p.color = accent; p.alpha = 200
                c.drawRect(80f, 418f, 1000f, 424f, p)
                p.alpha = 80; c.drawRect(80f, 430f, 800f, 434f, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = accent; p.alpha = 160
                c.drawArc(RectF(240f, 430f, 840f, 970f), 180f, 180f, false, p)
            }

            620 -> { // Executive Leadership Pinnacle: Mountain landscape + sky + hiker silhouette + brush mask
                // Sky base gradient: Soft cyan to deep blue
                p.style = Paint.Style.FILL
                p.shader = LinearGradient(0f, 0f, 0f, 850f,
                    intArrayOf(Color.rgb(235, 246, 255), Color.rgb(175, 215, 245), Color.rgb(36, 101, 157)),
                    floatArrayOf(0f, 0.45f, 1f), Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 1080f, p)
                p.shader = null

                // Distant soft blue mountain silhouettes
                p.style = Paint.Style.FILL; p.color = Color.rgb(32, 68, 105); p.alpha = 180
                val distantMt = Path().apply {
                    moveTo(0f, 820f)
                    lineTo(120f, 750f); lineTo(280f, 800f); lineTo(460f, 720f)
                    lineTo(620f, 770f); lineTo(820f, 710f); lineTo(980f, 760f); lineTo(1080f, 730f)
                    lineTo(1080f, 1080f); lineTo(0f, 1080f); close()
                }
                c.drawPath(distantMt, p)

                // Foreground dark craggy mountain ridges
                p.color = Color.rgb(11, 27, 48); p.alpha = 240
                val closeMt = Path().apply {
                    moveTo(0f, 890f)
                    lineTo(85f, 850f); lineTo(220f, 910f); lineTo(380f, 840f)
                    lineTo(560f, 900f); lineTo(760f, 830f); lineTo(920f, 870f); lineTo(1080f, 840f)
                    lineTo(1080f, 1080f); lineTo(0f, 1080f); close()
                }
                c.drawPath(closeMt, p)

                // Tiny hiker silhouette with backpack on peak (x = 85f, y = 850f)
                p.color = Color.rgb(8, 16, 30); p.alpha = 255
                c.drawCircle(85f, 816f, 5f, p)
                c.drawRect(82f, 822f, 88f, 836f, p)
                c.drawRect(78f, 824f, 82f, 832f, p)
                c.drawLine(83f, 836f, 80f, 848f, p)
                c.drawLine(87f, 836f, 90f, 848f, p)

                // Torn paper / brush feather transition on photo's left side
                p.style = Paint.Style.FILL; p.color = Color.rgb(235, 246, 255); p.alpha = 190
                for (i in 0..18) {
                    val by = 160f + i * 36f
                    val bx = 495f + ((i * 17) % 35)
                    c.drawCircle(bx, by, 18f, p)
                }
            }

            // ---- BUSINESS TEMPLATES (701-703) ----
            701 -> { // business_001: Service promotion — bold left panel
                p.style = Paint.Style.FILL; p.color = Color.rgb(5, 14, 36); p.alpha = 220
                val lp = Path().apply { moveTo(0f, 110f); lineTo(500f, 110f); lineTo(440f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(lp, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 4f; p.color = accent; p.alpha = 220
                c.drawLine(500f, 110f, 440f, 935f, p)
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 200
                c.drawRect(50f, 475f, 430f, 481f, p)
                p.alpha = 80; c.drawRect(50f, 487f, 320f, 491f, p)
            }
            702 -> { // business_002: Executive services — double-border with diamond corners
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = accent; p.alpha = 160
                c.drawRect(40f, 125f, 1040f, 920f, p)
                p.strokeWidth = 1f; p.alpha = 80
                c.drawRect(52f, 137f, 1028f, 908f, p)
                p.style = Paint.Style.FILL; p.alpha = 240; p.color = accent
                for ((cx, cy) in listOf(46f to 130f, 1034f to 130f, 46f to 915f, 1034f to 915f)) {
                    val dm = Path().apply { moveTo(cx, cy - 10f); lineTo(cx + 10f, cy); lineTo(cx, cy + 10f); lineTo(cx - 10f, cy); close() }
                    c.drawPath(dm, p)
                }
                p.color = accent; p.alpha = 180
                c.drawRect(80f, 500f, 1000f, 506f, p)
            }
            703 -> { // business_003: Growth/real estate — diagonal modern split
                p.style = Paint.Style.FILL; p.color = Color.rgb(4, 18, 40); p.alpha = 200
                val diag = Path().apply { moveTo(0f, 110f); lineTo(640f, 110f); lineTo(1080f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(diag, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 200
                c.drawLine(640f, 110f, 1080f, 935f, p)
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 200
                c.drawRect(60f, 480f, 580f, 486f, p)
                p.alpha = 100; c.drawRect(60f, 492f, 420f, 496f, p)
            }

            // ---- GOOD MORNING TEMPLATES (801-803) ----
            801 -> { // good_morning_001: Sunrise warm gradient + sun rays
                p.style = Paint.Style.FILL
                p.shader = LinearGradient(0f, 0f, 0f, 1080f,
                    intArrayOf(Color.rgb(15, 12, 30), Color.rgb(80, 30, 10), Color.rgb(200, 80, 20), Color.rgb(240, 140, 40)),
                    floatArrayOf(0f, 0.3f, 0.65f, 1f), Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 1080f, p)
                p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = Color.rgb(255, 200, 80); p.alpha = 50
                for (i in 0 until 18) {
                    val angle = (i * 20) * (Math.PI / 180.0)
                    c.drawLine(540f, 980f, 540f + (780f * Math.cos(angle)).toFloat(), 980f + (780f * Math.sin(angle)).toFloat(), p)
                }
                p.style = Paint.Style.FILL
                p.shader = RadialGradient(540f, 980f, 420f, intArrayOf(Color.argb(180, 255, 160, 50), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 600f, 1080f, 1080f, p)
                p.shader = null
            }
            802 -> { // good_morning_002: Soft floral warm — gentle wave layers
                p.style = Paint.Style.FILL
                p.shader = LinearGradient(0f, 0f, 1080f, 1080f,
                    intArrayOf(Color.rgb(30, 12, 42), Color.rgb(80, 25, 55), Color.rgb(160, 60, 40)),
                    null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 1080f, p)
                p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 100
                val wv1 = Path().apply { moveTo(-80f, 600f); cubicTo(300f, 400f, 780f, 750f, 1160f, 500f) }
                c.drawPath(wv1, p)
                p.alpha = 50
                val wv2 = Path().apply { moveTo(-80f, 650f); cubicTo(320f, 820f, 800f, 420f, 1160f, 650f) }
                c.drawPath(wv2, p)
                p.style = Paint.Style.FILL
                p.shader = RadialGradient(540f, 420f, 320f, intArrayOf(Color.argb(40, Color.red(accent), Color.green(accent), Color.blue(accent)), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 1080f, p)
                p.shader = null
            }
            803 -> { // good_morning_003: Corporate morning — vertical pillar grid
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 18
                for (i in 0..5) { c.drawRoundRect(RectF(60f + i * 170f, 130f, 140f + i * 170f, 915f), 16f, 16f, p) }
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 120
                c.drawRect(45f, 130f, 1035f, 915f, p)
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 200
                c.drawRect(80f, 496f, 1000f, 502f, p)
                p.alpha = 80; c.drawRect(80f, 508f, 780f, 512f, p)
            }

            // ---- ANNIVERSARY TEMPLATES (901-903) ----
            901 -> { // anniversary_001: Gold celebration concentric rings with dot particles
                p.style = Paint.Style.STROKE; p.color = accent
                for (r in listOf(260f, 310f, 360f)) {
                    p.strokeWidth = if (r == 310f) 3.5f else 1.5f
                    p.alpha = (200 - r * 0.35f).toInt().coerceAtLeast(40)
                    c.drawCircle(540f, 480f, r, p)
                }
                p.style = Paint.Style.FILL; p.alpha = 180
                for (i in 0 until 16) {
                    val angle = (i * 22.5) * (Math.PI / 180.0)
                    c.drawCircle(540f + (345f * Math.cos(angle)).toFloat(), 480f + (345f * Math.sin(angle)).toFloat(), if (i % 2 == 0) 5f else 3f, p)
                }
            }
            902 -> { // anniversary_002: Business milestone — diagonal panel
                p.style = Paint.Style.FILL; p.color = Color.rgb(4, 10, 26); p.alpha = 200
                val panel = Path().apply { moveTo(0f, 110f); lineTo(480f, 110f); lineTo(560f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(panel, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 3.5f; p.color = accent; p.alpha = 220
                c.drawLine(480f, 110f, 560f, 935f, p)
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 200
                c.drawRect(50f, 495f, 430f, 501f, p)
                p.alpha = 80; c.drawRect(50f, 507f, 320f, 511f, p)
            }
            903 -> { // anniversary_003: Personal warm — floral ring with petal dots
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 160
                c.drawCircle(540f, 475f, 270f, p)
                p.strokeWidth = 1f; p.alpha = 80
                c.drawCircle(540f, 475f, 290f, p)
                p.style = Paint.Style.FILL; p.alpha = 200
                for (i in 0 until 12) {
                    val angle = (i * 30) * (Math.PI / 180.0)
                    c.drawCircle(540f + (280f * Math.cos(angle)).toFloat(), 475f + (280f * Math.sin(angle)).toFloat(), 5f, p)
                }
                p.color = accent; p.alpha = 180
                c.drawRect(80f, 556f, 1000f, 562f, p)
            }

            // ---- OFFERS TEMPLATES (951-952) ----
            951 -> { // Mega Flash Sale: Dynamic diagonal discount panels & badge
                p.style = Paint.Style.FILL; p.color = Color.rgb(30, 41, 59); p.alpha = 220
                val panel = Path().apply { moveTo(0f, 110f); lineTo(520f, 110f); lineTo(460f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(panel, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 4f; p.color = accent; p.alpha = 220
                c.drawLine(520f, 110f, 460f, 935f, p)
                p.style = Paint.Style.FILL; p.color = Color.rgb(239, 68, 68); p.alpha = 220
                c.drawRoundRect(RectF(60f, 335f, 260f, 385f), 12f, 12f, p)
            }
            952 -> { // Special Festival Offer: Glowing circular rings and promo badge
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 160
                c.drawCircle(540f, 500f, 270f, p)
                p.strokeWidth = 1f; p.alpha = 100
                c.drawCircle(540f, 500f, 290f, p)
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 200
                c.drawRect(80f, 675f, 1000f, 681f, p)
            }

            // ---- EVENTS TEMPLATES (961-962) ----
            961 -> { // Business Summit 2026: Tech panel cut & geometric grid
                p.style = Paint.Style.FILL; p.color = Color.rgb(17, 34, 64); p.alpha = 220
                val panel = Path().apply { moveTo(0f, 110f); lineTo(520f, 110f); lineTo(460f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(panel, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 3.5f; p.color = accent; p.alpha = 220
                c.drawLine(520f, 110f, 460f, 935f, p)
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 220
                c.drawRect(60f, 380f, 440f, 386f, p)
            }
            962 -> { // Tech Conference: Futuristic hexagon / tech ring grid
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = accent; p.alpha = 140
                c.drawCircle(540f, 500f, 270f, p)
                p.strokeWidth = 1f; p.alpha = 70
                c.drawCircle(540f, 500f, 300f, p)
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 180
                c.drawRect(80f, 665f, 1000f, 671f, p)
            }

            // ---- BIRTHDAY GRATITUDE ROYAL SCROLL (220) ----
            220 -> { // Midnight purple gradient + bokeh + fireworks
                p.style = Paint.Style.FILL
                p.shader = LinearGradient(0f, 0f, 1080f, 1080f,
                    intArrayOf(Color.rgb(21, 3, 37), Color.rgb(53, 11, 80), Color.rgb(14, 1, 26)),
                    floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 1080f, p)
                p.shader = null

                // Festive bokeh circles
                for (i in 0..24) {
                    val bx = (i * 83 + 45) % 1080
                    val by = (i * 59 + 60) % 1080
                    val br = 15f + (i % 5) * 12f
                    p.color = if (i % 2 == 0) Color.rgb(255, 215, 120) else Color.rgb(240, 150, 200)
                    p.alpha = 30 + (i % 4) * 25
                    c.drawCircle(bx.toFloat(), by.toFloat(), br, p)
                }

                // Fireworks starburst at top right
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = Color.rgb(255, 215, 100); p.alpha = 180
                val fx = 850f; val fy = 120f
                for (i in 0 until 16) {
                    val angle = (i * 22.5) * (Math.PI / 180.0)
                    val r = 40f + (i % 3) * 20f
                    c.drawLine(fx, fy, fx + (r * Math.cos(angle)).toFloat(), fy + (r * Math.sin(angle)).toFloat(), p)
                }
            }

            else -> { // Category defaults
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 80
                c.drawCircle(540f, 480f, 280f, p)
            }
        }

        // 4. Category-specific celebratory & recognition elements
        p.style = Paint.Style.FILL
        when (t.category) {
            "Birthday" -> {
                // Festive sparkle stars and confetti
                for (i in 0..40) {
                    p.color = if (i % 3 == 0) Color.WHITE else accent
                    p.alpha = 100 + (i % 4) * 35
                    val x = 40f + ((i * 137 + t.style * 19) % 1000)
                    val y = 140f + ((i * 71) % 760)
                    c.save(); c.rotate((i * 27).toFloat(), x, y)
                    c.drawRoundRect(RectF(x, y, x + 6f, y + 16f), 3f, 3f, p)
                    c.restore()
                }
            }
            "Achievement" -> {
                // Star clusters and laurels
                p.color = accent; p.alpha = 70
                for (i in 0..6) {
                    val starX = 140f + i * 135f
                    val starY = 220f + (if (i % 2 == 0) 10f else -10f)
                    c.drawCircle(starX, starY, 6f, p)
                }
                for (i in 0..8) {
                    val angle = (i * 20 - 80) * (Math.PI / 180.0)
                    val lx = 160f + (80f * Math.cos(angle)).toFloat()
                    val ly = 500f + (160f * Math.sin(angle)).toFloat()
                    c.drawOval(RectF(lx - 12f, ly - 5f, lx + 12f, ly + 5f), p)
                }
            }
            "Income" -> {
                // Growth momentum indicators
                p.color = accent; p.alpha = 45
                for (i in 0..5) {
                    val barH = 50f + i * 35f
                    c.drawRoundRect(RectF(780f + i * 38f, 720f - barH, 805f + i * 38f, 720f), 6f, 6f, p)
                }
            }
            "Festival" -> {
                // Auspicious festive radiance
                p.color = accent; p.alpha = 75
                for (i in 0..15) {
                    val angle = (i * 22.5) * (Math.PI / 180.0)
                    val fx = 540f + (360f * Math.cos(angle)).toFloat()
                    val fy = 450f + (360f * Math.sin(angle)).toFloat()
                    c.drawCircle(fx, fy, 7f, p)
                }
            }
            "Motivation" -> {
                // Only add border bracket for text-only (center) motivation templates
                if (t.photoPosition == "center") {
                    p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = accent; p.alpha = 90
                    c.drawRect(50f, 150f, 1030f, 900f, p)
                }
            }
            "Anniversary" -> {
                // Subtle sparkle dots around the poster
                p.color = accent; p.alpha = 100
                for (i in 0..20) {
                    val sx = 60f + ((i * 153) % 960)
                    val sy = 140f + ((i * 89) % 760)
                    c.drawCircle(sx, sy, if (i % 3 == 0) 4f else 2.5f, p)
                }
            }
            "Business" -> {
                // Subtle grid dots in the background
                p.color = accent; p.alpha = 25
                for (i in 0..8) for (j in 0..8) {
                    c.drawCircle(120f + i * 110f, 170f + j * 90f, 2.5f, p)
                }
            }
            "Good Morning" -> {
                // Small dot sparkles
                p.color = Color.WHITE; p.alpha = 60
                for (i in 0..12) {
                    val sx = 80f + ((i * 179) % 920)
                    val sy = 150f + ((i * 97) % 580)
                    c.drawCircle(sx, sy, if (i % 2 == 0) 3f else 2f, p)
                }
            }
        }

        // 5. New category style-specific compositions (Good Night, Political, Real Estate, etc.)
        p.style = Paint.Style.FILL
        when (t.style) {
            // ---- GOOD NIGHT TEMPLATES (2001–2003) ----
            2001 -> { // Midnight Stars: Star field + crescent moon glow
                p.style = Paint.Style.FILL; p.color = Color.WHITE; p.alpha = 130
                for (i in 0..30) {
                    val sx = 50f + ((i * 193) % 980)
                    val sy = 120f + ((i * 137) % 840)
                    c.drawCircle(sx, sy, if (i % 4 == 0) 3.5f else if (i % 2 == 0) 2f else 1f, p)
                }
                // Moon glow
                p.shader = RadialGradient(800f, 350f, 200f,
                    intArrayOf(0x40FFDF80.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawCircle(800f, 350f, 200f, p); p.shader = null
            }
            2002 -> { // Purple Dreams: Soft lavender gradient + scattered stars
                p.style = Paint.Style.FILL
                p.shader = RadialGradient(540f, 480f, 600f,
                    intArrayOf(0x40E879F9.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 100f, 1080f, 960f, p); p.shader = null
                p.color = 0xFFE879F9.toInt(); p.alpha = 80
                for (i in 0..20) {
                    val sx = 80f + ((i * 211) % 920); val sy = 130f + ((i * 113) % 820)
                    c.drawCircle(sx, sy, if (i % 3 == 0) 3f else 1.5f, p)
                }
            }
            2003 -> { // Golden Twilight: Warm amber spotlight + subtle horizon line
                p.style = Paint.Style.FILL
                p.shader = LinearGradient(0f, 960f, 0f, 100f,
                    intArrayOf(0x60F59E0B.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 100f, 1080f, 960f, p); p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 60
                c.drawLine(0f, 680f, 1080f, 680f, p)
            }

            // ---- POLITICAL TEMPLATES (2101–2103) ----
            2101 -> { // Leader's Vision: Diagonal tricolor-inspired split
                p.style = Paint.Style.FILL; p.color = Color.rgb(18, 50, 80); p.alpha = 200
                val split = Path().apply { moveTo(0f, 110f); lineTo(510f, 110f); lineTo(450f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(split, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 4f; p.color = accent; p.alpha = 220
                c.drawLine(510f, 110f, 450f, 935f, p)
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 40
                c.drawRect(0f, 110f, 1080f, 125f, p)
            }
            2102 -> { // Democratic Pride: Patriotic radial burst
                p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f; p.color = accent; p.alpha = 50
                for (i in 0 until 16) {
                    val rad = (i * 22.5) * (Math.PI / 180.0)
                    c.drawLine(540f, 490f, 540f + (560f * Math.cos(rad)).toFloat(), 490f + (560f * Math.sin(rad)).toFloat(), p)
                }
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.alpha = 120
                c.drawCircle(540f, 490f, 280f, p)
            }
            2103 -> { // Power Manifesto: Bold left-panel split
                p.style = Paint.Style.FILL; p.color = Color.rgb(5, 20, 50); p.alpha = 210
                val panel = Path().apply { moveTo(0f, 110f); lineTo(530f, 110f); lineTo(530f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(panel, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 5f; p.color = accent; p.alpha = 240
                c.drawLine(530f, 110f, 530f, 935f, p)
            }

            // ---- REAL ESTATE TEMPLATES (2201–2203) ----
            2201 -> { // Luxury Property: Gold corner frames + subtle diagonal lines
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = accent; p.alpha = 160
                c.drawRect(40f, 125f, 1040f, 920f, p)
                p.strokeWidth = 1f; p.alpha = 70; c.drawRect(52f, 137f, 1028f, 908f, p)
                p.style = Paint.Style.FILL; p.alpha = 240
                for ((cx, cy) in listOf(46f to 131f, 1034f to 131f, 46f to 914f, 1034f to 914f)) {
                    val dm = Path().apply { moveTo(cx, cy - 9f); lineTo(cx + 9f, cy); lineTo(cx, cy + 9f); lineTo(cx - 9f, cy); close() }
                    c.drawPath(dm, p)
                }
            }
            2202 -> { // Modern Apartment: Teal geometric grid
                p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f; p.color = accent; p.alpha = 35
                for (i in 0..5) c.drawLine(0f, 190f + i * 130f, 1080f, 190f + i * 130f, p)
                for (i in 0..6) c.drawLine(120f + i * 140f, 110f, 120f + i * 140f, 935f, p)
            }
            2203 -> { // Commercial Space: Bold orange diagonal accent stripe
                p.style = Paint.Style.FILL; p.color = accent; p.alpha = 35
                val stripe = Path().apply { moveTo(0f, 110f); lineTo(80f, 110f); lineTo(80f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(stripe, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.alpha = 200
                c.drawLine(80f, 110f, 80f, 935f, p)
            }

            // ---- RESTAURANT TEMPLATES (2301–2303) ----
            2301 -> { // Spice Delight: Warm radial glow + diagonal split
                p.style = Paint.Style.FILL; p.color = Color.rgb(40, 10, 10); p.alpha = 190
                val foodPanel = Path().apply { moveTo(0f, 110f); lineTo(530f, 110f); lineTo(530f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(foodPanel, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 4f; p.color = accent; p.alpha = 200
                c.drawLine(530f, 110f, 530f, 935f, p)
                p.style = Paint.Style.FILL
                p.shader = RadialGradient(540f, 540f, 500f, intArrayOf(0x30EF4444.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 110f, 1080f, 935f, p); p.shader = null
            }
            2302 -> { // Café Elegance: Warm coffee circles + amber glow
                p.shader = RadialGradient(540f, 480f, 550f,
                    intArrayOf(0x35D97706.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 110f, 1080f, 935f, p); p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 60
                c.drawCircle(540f, 490f, 300f, p); c.drawCircle(540f, 490f, 380f, p)
            }
            2303 -> { // Green Garden Kitchen: Nature leaf grid
                p.style = Paint.Style.STROKE; p.strokeWidth = 1f; p.color = accent; p.alpha = 25
                for (i in 0..7) c.drawLine(120f + i * 120f, 110f, 120f + i * 120f, 935f, p)
                p.style = Paint.Style.FILL; p.alpha = 30
                c.drawRect(0f, 110f, 1080f, 160f, p)
            }

            // ---- HEALTHCARE TEMPLATES (2401–2403) ----
            2401 -> { // Caring Hands: Teal panel + medical cross accent
                p.style = Paint.Style.FILL; p.color = Color.rgb(5, 30, 30); p.alpha = 200
                val medPanel = Path().apply { moveTo(0f, 110f); lineTo(530f, 110f); lineTo(530f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(medPanel, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 4f; p.color = accent; p.alpha = 200
                c.drawLine(530f, 110f, 530f, 935f, p)
                // Cross symbol
                p.style = Paint.Style.FILL; p.alpha = 80
                c.drawRoundRect(RectF(260f, 780f, 300f, 870f), 8f, 8f, p)
                c.drawRoundRect(RectF(230f, 810f, 330f, 840f), 8f, 8f, p)
            }
            2402 -> { // Clinic Spotlight: Blue medical radial + subtle grid
                p.shader = RadialGradient(540f, 480f, 500f,
                    intArrayOf(0x353B82F6.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 110f, 1080f, 935f, p); p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f; p.color = accent; p.alpha = 30
                for (i in 0..5) c.drawLine(0f, 200f + i * 120f, 1080f, 200f + i * 120f, p)
            }
            2403 -> { // Wellness Premium: Purple elegant frame
                p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = accent; p.alpha = 130
                c.drawRect(40f, 125f, 1040f, 920f, p)
                p.style = Paint.Style.FILL; p.alpha = 30; c.drawRect(40f, 125f, 1040f, 175f, p)
            }

            // ---- EDUCATION TEMPLATES (2501–2503) ----
            2501 -> { // Campus Excellence: Navy left panel + gold divider
                p.style = Paint.Style.FILL; p.color = Color.rgb(8, 20, 55); p.alpha = 200
                val eduPanel = Path().apply { moveTo(0f, 110f); lineTo(530f, 110f); lineTo(530f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(eduPanel, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 4f; p.color = accent; p.alpha = 220
                c.drawLine(530f, 110f, 530f, 935f, p)
            }
            2502 -> { // Skills Academy: Purple to cyan gradient + diamond grid
                p.style = Paint.Style.FILL
                p.shader = LinearGradient(0f, 110f, 1080f, 935f,
                    intArrayOf(Color.rgb(18, 8, 40), Color.rgb(10, 35, 60)), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 110f, 1080f, 935f, p); p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 1f; p.color = accent; p.alpha = 25
                for (i in 0..7) c.drawLine(130f + i * 115f, 110f, 130f + i * 115f, 935f, p)
            }
            2503 -> { // Knowledge Gateway: Green diagonal accent
                p.style = Paint.Style.FILL; p.color = Color.rgb(5, 30, 10); p.alpha = 200
                val gPanel = Path().apply { moveTo(0f, 110f); lineTo(530f, 110f); lineTo(530f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(gPanel, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 4f; p.color = accent; p.alpha = 210
                c.drawLine(530f, 110f, 530f, 935f, p)
            }

            // ---- JOB VACANCY TEMPLATES (2601–2603) ----
            2601 -> { // Hiring Now: Bold red left panel
                p.style = Paint.Style.FILL; p.color = Color.rgb(35, 5, 5); p.alpha = 210
                val hrPanel = Path().apply { moveTo(0f, 110f); lineTo(530f, 110f); lineTo(530f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(hrPanel, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 5f; p.color = accent; p.alpha = 220
                c.drawLine(530f, 110f, 530f, 935f, p)
            }
            2602 -> { // Career Launch: Navy grid + gold spotlight
                p.shader = RadialGradient(540f, 490f, 520f,
                    intArrayOf(0x35FBBF24.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 110f, 1080f, 935f, p); p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 1f; p.color = accent; p.alpha = 20
                for (i in 0..7) c.drawLine(100f + i * 125f, 110f, 100f + i * 125f, 935f, p)
            }
            2603 -> { // Talent Hunt: Teal panel
                p.style = Paint.Style.FILL; p.color = Color.rgb(5, 35, 30); p.alpha = 210
                val tPanel = Path().apply { moveTo(0f, 110f); lineTo(530f, 110f); lineTo(530f, 935f); lineTo(0f, 935f); close() }
                c.drawPath(tPanel, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = 4f; p.color = accent; p.alpha = 210
                c.drawLine(530f, 110f, 530f, 935f, p)
            }

            // ---- QUOTES TEMPLATES (2701–2703) ----
            2701 -> { // Golden Wisdom: Luxury dark + gold subtle radial
                p.shader = RadialGradient(540f, 540f, 600f,
                    intArrayOf(0x40D97706.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 90f, 1080f, 980f, p); p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f; p.color = accent; p.alpha = 60
                c.drawRect(50f, 120f, 1030f, 960f, p)
            }
            2702 -> { // Midnight Inspire: Subtle indigo radial + center glow
                p.shader = RadialGradient(540f, 500f, 550f,
                    intArrayOf(0x406366F1.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 90f, 1080f, 980f, p); p.shader = null
            }
            2703 -> { // Rose Elegance: Pink blush radial
                p.shader = RadialGradient(540f, 500f, 580f,
                    intArrayOf(0x40EC4899.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 90f, 1080f, 980f, p); p.shader = null
            }

            // ---- DEVOTIONAL TEMPLATES (2801–2803) ----
            2801 -> { // Divine Blessing: Saffron radial glow + subtle rays
                p.shader = RadialGradient(540f, 490f, 520f,
                    intArrayOf(0x50F97316.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 100f, 1080f, 965f, p); p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 1f; p.color = accent; p.alpha = 35
                for (i in 0 until 12) {
                    val rad = (i * 30) * (Math.PI / 180.0)
                    c.drawLine(540f, 490f, 540f + (560f * Math.cos(rad)).toFloat(), 490f + (560f * Math.sin(rad)).toFloat(), p)
                }
            }
            2802 -> { // Sacred Light: Crimson radial cross glow
                p.shader = RadialGradient(540f, 490f, 520f,
                    intArrayOf(0x45EF4444.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 100f, 1080f, 965f, p); p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = accent; p.alpha = 60
                c.drawLine(540f, 120f, 540f, 960f, p); c.drawLine(120f, 490f, 960f, 490f, p)
            }
            2803 -> { // Morning Prayer: Amber sunrise gradient
                p.style = Paint.Style.FILL
                p.shader = LinearGradient(0f, 965f, 0f, 100f,
                    intArrayOf(0x55CA8A04.toInt(), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                c.drawRect(0f, 100f, 1080f, 965f, p); p.shader = null
                p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f; p.color = accent; p.alpha = 50
                c.drawLine(0f, 700f, 1080f, 700f, p)
            }
        }
        p.alpha = 255
    }
    private fun photo(context: Context, c: Canvas, d: GeneratedPoster, s: TemplateSlot, r: RectF) {
        if (!s.required && d.photo.isBlank()) return
        val path = Path().apply {
            when(s.shape) {
                "circle", "oval" -> addOval(r, Path.Direction.CW)
                "rectangle" -> addRect(r, Path.Direction.CW)
                else -> addRoundRect(r, 38f, 38f, Path.Direction.CW)
            }
        }
        c.save(); c.clipPath(path); c.drawColor(Color.rgb(226,231,235))
        val photo = TemplateImages.read(context, d.photo)
        if (photo != null) image(c, photo, r, false, d.crop)
        else {
            val samplePhoto = TemplateImages.read(context, "res:sample_business_man")
                ?: TemplateImages.read(context, "res:sample_business_woman")
            if (samplePhoto != null) {
                image(c, samplePhoto, r, false, d.crop)
            } else {
                val p = paint("#AFBAC4")
                c.drawCircle(r.centerX(),r.top+r.height()*0.35f,r.width()*0.14f,p)
                c.drawOval(RectF(r.left+r.width()*.17f,r.top+r.height()*.54f,r.right-r.width()*.17f,r.bottom+r.height()*.18f),p)
                text(c,"+ PHOTO",RectF(r.left+15,r.centerY()-10,r.right-15,r.centerY()+45),30f,"#253C51",true,false,"sans-serif","center",1)
            }
        }
        c.restore()
        if(s.borderWidth>0) c.drawPath(path,paint(s.borderColor).apply { style=Paint.Style.STROKE; strokeWidth=s.borderWidth.coerceIn(0f,24f) })
    }
    private fun image(c: Canvas, bitmap: Bitmap, r: RectF, contain: Boolean, crop: PhotoCrop = PhotoCrop()) {
        val scale = (if(contain) min(r.width()/bitmap.width,r.height()/bitmap.height) else max(r.width()/bitmap.width,r.height()/bitmap.height)) * if(contain) 1f else crop.scale.coerceIn(1f,4f)
        val w=bitmap.width*scale; val h=bitmap.height*scale
        val dx=if(contain) 0f else crop.panX.coerceIn(-1f,1f)*max(0f,(w-r.width())/2)
        val dy=if(contain) 0f else crop.panY.coerceIn(-1f,1f)*max(0f,(h-r.height())/2)
        val dest=RectF(r.centerX()-w/2+dx,r.centerY()-h/2+dy,r.centerX()+w/2+dx,r.centerY()+h/2+dy)
        c.drawBitmap(bitmap,null,dest,Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
    }
    private fun band(context: Context,c: Canvas,b: BrandingBand,y: Float,brand: BusinessBranding) {
        c.drawRect(0f,y,1080f,y+b.height,paint(b.backgroundColor))
        val padding=40f
        val logoSize=min(90f,b.height-28f)
        var left=padding; var right=1080-padding
        var textTop=y+16f
        if(b.showLogo) {
            val logoX=when(b.layout){"right"->1080-padding-logoSize;"center"->540-logoSize/2;else->padding}
            val box=RectF(logoX,y+14f,logoX+logoSize,y+14f+logoSize)
            TemplateImages.read(context,brand.logo)?.let { image(c,it,box,true) } ?: run {
                c.drawRoundRect(box,12f,12f,paint(b.textColor).apply{alpha=22})
                text(c,"LOGO",box,17f,b.textColor,true,false,"sans-serif","center",1)
            }
            when(b.layout){"right"->right=logoX-26;"center"->textTop=y+logoSize+18;else->left=logoX+logoSize+26}
        }
        val lines=buildList {
            val companyName = brand.company.ifBlank { "YOUR BUSINESS" }
            if(b.showCompanyName) add(companyName)
            if(b.showTagline && brand.tagline.isNotBlank()) add(brand.tagline)
            val contact=buildList {
                if(b.showPhone && brand.phone.isNotBlank()) add(brand.phone)
                if(b.showWebsite && brand.website.isNotBlank()) add(brand.website)
                if(b.showEmail && brand.email.isNotBlank()) add(brand.email)
            }
            if(contact.isNotEmpty()) add(contact.joinToString("   •   "))
            else if(b.showPhone || b.showWebsite) add("+91 98765 43210   •   www.yourbusiness.com")
            if(b.showAddress && brand.address.isNotBlank()) add(brand.address)
        }
        val available=(y+b.height-14-textTop).coerceAtLeast(16f)
        val lineHeight=available / lines.size.coerceAtLeast(1)
        lines.forEachIndexed { i,line -> text(c,line,RectF(left,textTop+i*lineHeight,right,textTop+(i+1)*lineHeight),if(i==0&&b.showCompanyName) 35f else 25f,b.textColor,i==0&&b.showCompanyName,false,"sans-serif",if(b.layout=="center"||b.layout=="company center")"center" else "left",1) }
    }

    /**
     * Fixed branding footer for Welcome templates (styles 130–141).
     * Layout (200px tall, y=880–1080):
     *   Row 1 (y=880–990): Profile photo circle (left) | Name + Designation (center-left) | Business logo (right)
     *   Row 2 (y=990–1040): Contact strip with icons: phone, website, email
     *   Row 3 (y=1040–1070): Business name centered
     *   Separator line at y=880
     * Colors adapt to the template's footer backgroundColor and textColor.
     */
    private fun drawFixedBrandingFooter(context: Context, c: Canvas, t: PosterTemplate, d: GeneratedPoster) {
        val f = t.footer
        val b = d.branding
        val y = 1080f - f.height  // 880f for 200px footer
        val bgColor = color(f.backgroundColor)
        val textClr = f.textColor
        val accentClr = t.accentColor

        // Background fill
        c.drawRect(0f, y, 1080f, 1080f, paint(f.backgroundColor))

        // Top separator line
        val sepPaint = paint(accentClr).apply { alpha = 80; strokeWidth = 2f }
        c.drawLine(0f, y, 1080f, y, sepPaint)

        // ---- Row 1: Profile photo + Name/Designation + Logo (y to y+110) ----
        val row1Top = y + 12f
        val row1Bottom = y + 108f
        val photoSize = 80f
        var contentLeft = 30f

        // Profile photo (circular)
        val profileBmp = TemplateImages.read(context, b.profilePhoto)
        if (profileBmp != null) {
            val photoRect = RectF(contentLeft, row1Top + 5f, contentLeft + photoSize, row1Top + 5f + photoSize)
            val photoPath = Path().apply { addOval(photoRect, Path.Direction.CW) }
            c.save(); c.clipPath(photoPath)
            image(c, profileBmp, photoRect, false)
            c.restore()
            // Photo border ring
            c.drawPath(photoPath, paint(accentClr).apply { style = Paint.Style.STROKE; strokeWidth = 2.5f; alpha = 180 })
            contentLeft += photoSize + 16f
        }

        // Name + Designation block
        val ownerName = b.ownerName.ifBlank { b.company.ifBlank { "YOUR NAME" } }
        val ownerDesig = b.ownerDesignation.ifBlank { "" }
        val nameBlockRight = 780f
        val nameY = if (ownerDesig.isBlank()) row1Top + 25f else row1Top + 12f
        text(c, ownerName, RectF(contentLeft, nameY, nameBlockRight, nameY + 40f),
            28f, textClr, bold = true, italic = false, font = "sans-serif", align = "left", maxLines = 1)
        if (ownerDesig.isNotBlank()) {
            text(c, ownerDesig, RectF(contentLeft, nameY + 42f, nameBlockRight, nameY + 72f),
                20f, accentClr, bold = false, italic = false, font = "sans-serif", align = "left", maxLines = 1)
        }

        // Business logo (right side)
        val logoSize = 72f
        val logoX = 1080f - 30f - logoSize
        val logoY = row1Top + 10f
        val logoBmp = TemplateImages.read(context, b.logo)
        if (logoBmp != null) {
            val logoRect = RectF(logoX, logoY, logoX + logoSize, logoY + logoSize)
            image(c, logoBmp, logoRect, contain = true)
        } else if (b.company.isNotBlank()) {
            // Placeholder logo: colored circle with first letter
            val logoRect = RectF(logoX, logoY, logoX + logoSize, logoY + logoSize)
            val logoBg = paint(accentClr).apply { alpha = 30 }
            c.drawRoundRect(logoRect, 14f, 14f, logoBg)
            text(c, b.company.take(1).uppercase(), logoRect,
                32f, accentClr, bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)
        }

        // ---- Row 2: Contact strip with icons (y+112 to y+148) ----
        val contactY = y + 114f
        val contactH = 32f
        val iconR = 8f
        var cx = 30f
        val contactItems = buildList {
            if (b.phone.isNotBlank()) add("phone" to b.phone)
            if (b.website.isNotBlank()) add("web" to b.website)
            if (b.email.isNotBlank()) add("email" to b.email)
        }

        if (contactItems.isNotEmpty()) {
            val iconPaint = paint(accentClr).apply { alpha = 200 }
            val dotPaint = paint(textClr).apply { alpha = 80 }
            for ((idx, pair) in contactItems.withIndex()) {
                val (type, value) = pair
                // Draw icon circle
                val iconCy = contactY + contactH / 2
                c.drawCircle(cx + iconR, iconCy, iconR, iconPaint)
                // Draw icon symbol inside
                val symPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = bgColor; strokeWidth = 1.5f; style = Paint.Style.STROKE
                }
                when (type) {
                    "phone" -> {
                        c.drawArc(RectF(cx + iconR - 4f, iconCy - 4f, cx + iconR + 4f, iconCy + 4f), -140f, 100f, false, symPaint)
                    }
                    "web" -> {
                        c.drawCircle(cx + iconR, iconCy, 5f, symPaint)
                        c.drawLine(cx + iconR - 5f, iconCy, cx + iconR + 5f, iconCy, symPaint)
                    }
                    "email" -> {
                        c.drawRect(cx + iconR - 5f, iconCy - 3f, cx + iconR + 5f, iconCy + 4f, symPaint)
                        c.drawLine(cx + iconR - 5f, iconCy - 3f, cx + iconR, iconCy + 1f, symPaint)
                        c.drawLine(cx + iconR + 5f, iconCy - 3f, cx + iconR, iconCy + 1f, symPaint)
                    }
                }
                // Draw value text
                val textLeft = cx + iconR * 2 + 8f
                val textRight = (textLeft + 280f).coerceAtMost(1050f)
                text(c, value, RectF(textLeft, contactY, textRight, contactY + contactH),
                    18f, textClr, bold = false, italic = false, font = "sans-serif", align = "left", maxLines = 1)
                cx = textRight + 20f
                // Dot separator (except last)
                if (idx < contactItems.size - 1 && cx < 1000f) {
                    c.drawCircle(cx - 8f, iconCy, 2.5f, dotPaint)
                }
            }
        } else {
            // Fallback: show placeholder contact
            text(c, "+91 98765 43210   •   www.yourbusiness.com",
                RectF(30f, contactY, 1050f, contactY + contactH),
                18f, textClr, bold = false, italic = false, font = "sans-serif", align = "left", maxLines = 1)
        }

        // ---- Row 3: Business name centered (y+152 to y+185) ----
        val bizName = b.company.ifBlank { "YOUR BUSINESS" }
        val bizNameY = y + 155f
        text(c, bizName.uppercase(), RectF(30f, bizNameY, 1050f, bizNameY + 32f),
            22f, textClr, bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)

        // Tagline (if available)
        if (b.tagline.isNotBlank()) {
            text(c, b.tagline, RectF(80f, bizNameY + 30f, 1000f, bizNameY + 52f),
                14f, textClr, bold = false, italic = true, font = "sans-serif", align = "center", maxLines = 1)
        }
    }

    private fun drawMotivationMountainElements(context: Context, c: Canvas, d: GeneratedPoster) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        val company = d.branding.company.ifBlank { "YOUR COMPANY" }

        // 1. Dual Top Branding Logos
        val logoBmp = TemplateImages.read(context, d.branding.logo)
        for (isRight in listOf(false, true)) {
            val logoX = if (isRight) 820f else 35f
            if (logoBmp != null) {
                image(c, logoBmp, RectF(logoX, 30f, logoX + 60f, 90f), contain = true)
                text(c, company, RectF(logoX + 68f, 32f, logoX + 220f, 92f), 17f, "#0A2540", bold = true, italic = false, font = "sans-serif", align = "left", maxLines = 2)
            } else {
                p.style = Paint.Style.FILL; p.color = Color.rgb(0, 140, 220)
                c.drawRoundRect(RectF(logoX, 34f, logoX + 48f, 82f), 10f, 10f, p)
                text(c, company.take(1).ifBlank { "C" }, RectF(logoX, 34f, logoX + 48f, 82f), 28f, "#FFFFFF", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)
                text(c, company, RectF(logoX + 54f, 32f, logoX + 220f, 70f), 15f, "#0A2540", bold = true, italic = false, font = "sans-serif", align = "left", maxLines = 1)
                text(c, "LEADERSHIP EXCELLENCE", RectF(logoX + 54f, 68f, logoX + 220f, 88f), 11f, "#557799", bold = false, italic = false, font = "sans-serif", align = "left", maxLines = 1)
            }
        }

        // 2. Checkmark highlight banner (x = 35f..530f, y = 640f..695f)
        p.style = Paint.Style.FILL; p.color = Color.rgb(22, 160, 133)
        c.drawCircle(65f, 665f, 18f, p)
        p.style = Paint.Style.STROKE; p.strokeWidth = 3.5f; p.color = Color.WHITE
        val checkPath = Path().apply { moveTo(57f, 665f); lineTo(63f, 672f); lineTo(74f, 658f) }
        c.drawPath(checkPath, p)
        text(c, "आज का संघर्ष,", RectF(95f, 642f, 530f, 668f), 21f, "#FFFFFF", bold = true, italic = false, font = "sans-serif", align = "left", maxLines = 1)
        text(c, "कल की सफलता की नींव है।", RectF(95f, 668f, 530f, 694f), 20f, "#F9C74F", bold = true, italic = false, font = "sans-serif", align = "left", maxLines = 1)

        // 3. Four Icon Feature Cards (y = 715f..825f)
        val cards = listOf(
            Triple("लक्ष्य तय करो", "और उस पर\nडटे रहो।", "target"),
            Triple("रोज थोड़ा आगे", "बढ़ो, बड़ी सफलता\nमिलेगी।", "runner"),
            Triple("मुश्किलें आएँगी,", "लेकिन हार मानना\nमत छोड़ो।", "mountain"),
            Triple("सफलता उन्हीं को", "मिलती है, जो कभी\nरुकते नहीं।", "trophy")
        )
        cards.forEachIndexed { i, (line1, line2, iconType) ->
            val cx = 24f + i * 125f
            val cardRect = RectF(cx, 715f, cx + 118f, 825f)
            p.style = Paint.Style.FILL; p.color = Color.rgb(7, 24, 48); p.alpha = 210
            c.drawRoundRect(cardRect, 14f, 14f, p)
            p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f; p.color = Color.rgb(0, 180, 216); p.alpha = 140
            c.drawRoundRect(cardRect, 14f, 14f, p)
            p.style = Paint.Style.STROKE; p.color = Color.rgb(249, 199, 79); p.strokeWidth = 2f
            val iconY = 735f
            val iconX = cardRect.centerX()
            when (iconType) {
                "target" -> {
                    c.drawCircle(iconX, iconY, 12f, p)
                    c.drawCircle(iconX, iconY, 6f, p)
                    p.style = Paint.Style.FILL; c.drawCircle(iconX, iconY, 2.5f, p)
                }
                "runner" -> {
                    p.style = Paint.Style.FILL; c.drawCircle(iconX, iconY - 6f, 3.5f, p)
                    p.style = Paint.Style.STROKE; p.strokeWidth = 2f
                    c.drawLine(iconX, iconY - 2f, iconX + 2f, iconY + 5f, p)
                    c.drawLine(iconX + 2f, iconY + 5f, iconX - 5f, iconY + 12f, p)
                    c.drawLine(iconX + 2f, iconY + 5f, iconX + 7f, iconY + 10f, p)
                }
                "mountain" -> {
                    val mPath = Path().apply { moveTo(iconX - 9f, iconY + 8f); lineTo(iconX, iconY - 6f); lineTo(iconX + 9f, iconY + 8f); close() }
                    c.drawPath(mPath, p)
                    c.drawLine(iconX, iconY - 6f, iconX, iconY - 14f, p)
                    p.style = Paint.Style.FILL
                    val flag = Path().apply { moveTo(iconX, iconY - 14f); lineTo(iconX + 6f, iconY - 11f); lineTo(iconX, iconY - 8f); close() }
                    c.drawPath(flag, p)
                }
                "trophy" -> {
                    p.style = Paint.Style.STROKE; p.strokeWidth = 2f
                    c.drawArc(RectF(iconX - 7f, iconY - 8f, iconX + 7f, iconY + 4f), 0f, 180f, false, p)
                    c.drawLine(iconX, iconY + 4f, iconX, iconY + 9f, p)
                    c.drawLine(iconX - 6f, iconY + 9f, iconX + 6f, iconY + 9f, p)
                }
            }
            text(c, line1, RectF(cardRect.left + 4f, 756f, cardRect.right - 4f, 776f), 13f, "#FFFFFF", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)
            text(c, line2, RectF(cardRect.left + 4f, 778f, cardRect.right - 4f, 820f), 11f, "#C8DCEF", bold = false, italic = false, font = "sans-serif", align = "center", maxLines = 2)
        }

        // 4. Bottom Right Badges: Navy Name Badge + White Phone Badge
        val navyPill = RectF(460f, 815f, 1050f, 902f)
        p.style = Paint.Style.FILL; p.color = Color.rgb(6, 20, 42)
        c.drawRoundRect(navyPill, 36f, 36f, p)
        p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = Color.rgb(249, 199, 79); p.alpha = 200
        c.drawRoundRect(navyPill, 36f, 36f, p)

        val nameText = d.values[TemplateField.NAME.name]?.ifBlank { "YOUR NAME" } ?: "YOUR NAME"
        val roleText = d.values[TemplateField.DESIGNATION.name]?.ifBlank { company } ?: company
        text(c, nameText.uppercase(), RectF(navyPill.left + 16f, 822f, navyPill.right - 16f, 866f), 32f, "#FFFFFF", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)
        text(c, roleText.uppercase(), RectF(navyPill.left + 16f, 868f, navyPill.right - 16f, 896f), 18f, "#F9C74F", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)

        val whitePill = RectF(510f, 915f, 1000f, 978f)
        p.style = Paint.Style.FILL; p.color = Color.WHITE
        c.drawRoundRect(whitePill, 30f, 30f, p)
        p.style = Paint.Style.FILL; p.color = Color.rgb(10, 20, 35)
        c.drawCircle(550f, 946f, 14f, p)
        p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = Color.WHITE
        val phoneArc = RectF(542f, 938f, 558f, 954f)
        c.drawArc(phoneArc, -140f, 100f, false, p)
        val phoneText = d.branding.phone.ifBlank { "" }
        if (phoneText.isNotBlank()) {
            text(c, phoneText, RectF(572f, 922f, whitePill.right - 20f, 972f), 30f, "#06142A", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)
        } else {
            text(c, "CALL FOR DETAILS", RectF(572f, 922f, whitePill.right - 20f, 972f), 22f, "#06142A", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)
        }

        p.style = Paint.Style.FILL; p.color = Color.rgb(150, 180, 200); p.textSize = 14f; p.typeface = Typeface.DEFAULT
        c.save()
        c.rotate(-90f, 1065f, 500f)
        c.drawText("Design By PosterFlow", 980f, 500f, p)
        c.restore()
    }

    private fun drawParchmentGratitudeElements(context: Context, c: Canvas, d: GeneratedPoster) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        val company = d.branding.company.ifBlank { "YOUR COMPANY" }

        // 1. Dual Top Branding Logos
        val logoBmp = TemplateImages.read(context, d.branding.logo)
        for (isRight in listOf(false, true)) {
            val logoX = if (isRight) 800f else 35f
            if (logoBmp != null) {
                image(c, logoBmp, RectF(logoX, 30f, logoX + 60f, 90f), contain = true)
                text(c, company, RectF(logoX + 68f, 32f, logoX + 240f, 92f), 16f, "#FFFFFF", bold = true, italic = false, font = "sans-serif", align = "left", maxLines = 2)
            } else {
                p.style = Paint.Style.FILL; p.color = Color.rgb(0, 140, 220)
                c.drawRoundRect(RectF(logoX, 34f, logoX + 48f, 82f), 10f, 10f, p)
                text(c, company.take(1).ifBlank { "C" }, RectF(logoX, 34f, logoX + 48f, 82f), 28f, "#FFFFFF", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)
                text(c, company, RectF(logoX + 54f, 32f, logoX + 240f, 70f), 15f, "#FFFFFF", bold = true, italic = false, font = "sans-serif", align = "left", maxLines = 1)
                text(c, "LEADERSHIP EXCELLENCE", RectF(logoX + 54f, 68f, logoX + 240f, 88f), 11f, "#F9C74F", bold = false, italic = false, font = "sans-serif", align = "left", maxLines = 1)
            }
        }

        // 2. Vintage Antique Parchment Scroll (x = 520f..1030f, y = 125f..800f)
        val scrollLeft = 520f
        val scrollTop = 125f
        val scrollRight = 1030f
        val scrollBottom = 800f
        val scrollBox = RectF(scrollLeft, scrollTop, scrollRight, scrollBottom)

        // Top Rod & Gold Spherical Finials
        p.style = Paint.Style.FILL
        p.shader = LinearGradient(scrollLeft, 105f, scrollRight, 125f, intArrayOf(Color.rgb(180, 130, 40), Color.rgb(245, 210, 100), Color.rgb(160, 110, 30)), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        c.drawRoundRect(RectF(scrollLeft - 20f, 105f, scrollRight + 20f, 125f), 6f, 6f, p)
        p.shader = null
        p.color = Color.rgb(255, 215, 90)
        c.drawCircle(scrollLeft - 25f, 115f, 15f, p)
        c.drawCircle(scrollRight + 25f, 115f, 15f, p)
        p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = Color.rgb(100, 70, 20)
        c.drawCircle(scrollLeft - 25f, 115f, 15f, p)
        c.drawCircle(scrollRight + 25f, 115f, 15f, p)

        // Bottom Rod & Gold Spherical Finials
        p.style = Paint.Style.FILL
        p.shader = LinearGradient(scrollLeft, 800f, scrollRight, 820f, intArrayOf(Color.rgb(180, 130, 40), Color.rgb(245, 210, 100), Color.rgb(160, 110, 30)), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        c.drawRoundRect(RectF(scrollLeft - 20f, 800f, scrollRight + 20f, 820f), 6f, 6f, p)
        p.shader = null
        p.color = Color.rgb(255, 215, 90)
        c.drawCircle(scrollLeft - 25f, 810f, 15f, p)
        c.drawCircle(scrollRight + 25f, 810f, 15f, p)
        p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = Color.rgb(100, 70, 20)
        c.drawCircle(scrollLeft - 25f, 810f, 15f, p)
        c.drawCircle(scrollRight + 25f, 810f, 15f, p)

        // Scroll Paper Fill & Texture
        p.style = Paint.Style.FILL
        p.shader = LinearGradient(scrollLeft, scrollTop, scrollRight, scrollBottom,
            intArrayOf(Color.rgb(252, 237, 199), Color.rgb(244, 218, 163), Color.rgb(230, 195, 126)),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        c.drawRect(scrollBox, p)
        p.shader = null

        // Scroll Borders & Inner Filigree Line
        p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = Color.rgb(150, 100, 35)
        c.drawRect(scrollBox, p)
        p.strokeWidth = 1.5f; p.alpha = 180
        val innerBox = RectF(scrollLeft + 10f, scrollTop + 10f, scrollRight - 10f, scrollBottom - 10f)
        c.drawRect(innerBox, p)

        // Top & Bottom Parchment Roll Shadow Effect
        p.style = Paint.Style.FILL; p.color = Color.rgb(130, 85, 25); p.alpha = 50
        c.drawRect(scrollLeft, scrollTop, scrollRight, scrollTop + 22f, p)
        c.drawRect(scrollLeft, scrollBottom - 22f, scrollRight, scrollBottom, p)

        // 3. Text Inside Scroll (Traditional Hindi Birthday Gratitude Calligraphy)
        val heading1 = "मेरे जन्म दिवस के अवसर पर"
        val bodyText = d.values[TemplateField.MESSAGE.name]?.ifBlank {
            "विभिन्न डिजिटल माध्यमों द्वारा\nमिले अपार स्नेह, आशीर्वाद\nऔर शुभकामना संदेश के लिए\nआप सभी का सहृदय"
        } ?: "विभिन्न डिजिटल माध्यमों द्वारा\nमिले अपार स्नेह, आशीर्वाद\nऔर शुभकामना संदेश के लिए\nआप सभी का सहृदय"

        text(c, heading1, RectF(scrollLeft + 20f, scrollTop + 30f, scrollRight - 20f, scrollTop + 75f), 24f, "#4A0C0C", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)
        text(c, bodyText, RectF(scrollLeft + 20f, scrollTop + 80f, scrollRight - 20f, scrollTop + 260f), 22f, "#2B0B04", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 4)

        // Ornamental Filigree Line 1
        p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = Color.rgb(160, 100, 30); p.alpha = 200
        c.drawLine(scrollLeft + 50f, scrollTop + 270f, scrollRight - 50f, scrollTop + 270f, p)
        p.style = Paint.Style.FILL; c.drawCircle((scrollLeft + scrollRight) / 2f, scrollTop + 270f, 5f, p)

        // "आभार !" Headline in Gold/Purple Calligraphy
        text(c, "आभार !", RectF(scrollLeft + 20f, scrollTop + 285f, scrollRight - 20f, scrollTop + 380f), 60f, "#2B0948", bold = true, italic = false, font = "serif", align = "center", maxLines = 1, gold = true)

        // Ornamental Filigree Line 2
        p.style = Paint.Style.STROKE; p.strokeWidth = 2f; p.color = Color.rgb(160, 100, 30); p.alpha = 200
        c.drawLine(scrollLeft + 50f, scrollTop + 390f, scrollRight - 50f, scrollTop + 390f, p)

        // "धन्यवाद !" Red Brush Script Headline
        text(c, "धन्यवाद !", RectF(scrollLeft + 20f, scrollTop + 400f, scrollLeft + 310f, scrollTop + 500f), 52f, "#A81212", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)

        // Folded Namaste Prayer Hands Vector Icon (Right side of धन्यवाद !)
        val handX = scrollLeft + 330f
        val handY = scrollTop + 405f
        p.style = Paint.Style.STROKE; p.strokeWidth = 3f; p.color = Color.rgb(168, 18, 18)
        // Left hand palm outline
        val leftHand = Path().apply {
            moveTo(handX + 15f, handY + 70f)
            cubicTo(handX + 10f, handY + 45f, handX + 25f, handY + 15f, handX + 35f, handY + 5f)
            cubicTo(handX + 37f, handY + 15f, handX + 40f, handY + 40f, handX + 35f, handY + 70f)
        }
        c.drawPath(leftHand, p)
        // Right hand palm outline
        val rightHand = Path().apply {
            moveTo(handX + 55f, handY + 70f)
            cubicTo(handX + 60f, handY + 45f, handX + 45f, handY + 15f, handX + 35f, handY + 5f)
            cubicTo(handX + 33f, handY + 15f, handX + 30f, handY + 40f, handX + 35f, handY + 70f)
        }
        c.drawPath(rightHand, p)
        // Wrist cuffs / Bangles
        p.style = Paint.Style.FILL; p.color = Color.rgb(215, 155, 45)
        c.drawRoundRect(RectF(handX + 10f, handY + 70f, handX + 60f, handY + 80f), 4f, 4f, p)

        // 4. Festive Bottom Elements: Diyas + Lotus + Gift Box
        // Glowing clay diyas with radial flame light halo
        val diyaCenterX = 680f
        val diyaCenterY = 920f
        p.style = Paint.Style.FILL
        p.shader = RadialGradient(diyaCenterX, diyaCenterY, 130f, intArrayOf(Color.argb(160, 255, 200, 60), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
        c.drawCircle(diyaCenterX, diyaCenterY, 130f, p)
        p.shader = null

        // Diyas Terracotta Bowls
        for (dx in listOf(570f, 680f, 790f)) {
            val dy = if (dx == 680f) 925f else 940f
            // Terracotta diya base
            p.style = Paint.Style.FILL; p.color = Color.rgb(180, 80, 25)
            c.drawArc(RectF(dx - 35f, dy - 12f, dx + 35f, dy + 25f), 0f, 180f, true, p)
            p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = Color.rgb(255, 215, 90)
            c.drawArc(RectF(dx - 35f, dy - 12f, dx + 35f, dy + 25f), 0f, 180f, false, p)
            // Outer Flame Halo
            p.style = Paint.Style.FILL; p.color = Color.rgb(255, 140, 0)
            val flameOuter = Path().apply {
                moveTo(dx, dy - 32f)
                quadTo(dx + 12f, dy - 12f, dx, dy - 2f)
                quadTo(dx - 12f, dy - 12f, dx, dy - 32f)
            }
            c.drawPath(flameOuter, p)
            // Inner Bright Flame
            p.color = Color.rgb(255, 240, 100)
            val flameInner = Path().apply {
                moveTo(dx, dy - 26f)
                quadTo(dx + 6f, dy - 12f, dx, dy - 4f)
                quadTo(dx - 6f, dy - 12f, dx, dy - 26f)
            }
            c.drawPath(flameInner, p)
        }

        // Pink Lotus Blossom Accent (x = 740f, y = 860f)
        val lotusX = 740f
        val lotusY = 860f
        p.style = Paint.Style.FILL; p.color = Color.rgb(236, 72, 153); p.alpha = 220
        c.drawCircle(lotusX, lotusY, 18f, p)
        for (a in listOf(-45f, -15f, 15f, 45f)) {
            val rad = Math.toRadians(a.toDouble())
            c.drawCircle(lotusX + (22f * Math.sin(rad)).toFloat(), lotusY - (22f * Math.cos(rad)).toFloat(), 12f, p)
        }
        p.color = Color.rgb(255, 240, 150)
        c.drawCircle(lotusX, lotusY - 4f, 6f, p)

        // 3D Gift Box with Golden Satin Bow (x = 840f..1020f, y = 820f..950f)
        val giftBox = RectF(840f, 820f, 1020f, 950f)
        p.style = Paint.Style.FILL; p.color = Color.rgb(110, 20, 140)
        c.drawRoundRect(giftBox, 16f, 16f, p)
        // Gold Ribbon
        p.color = Color.rgb(255, 215, 90)
        c.drawRect(giftBox.centerX() - 14f, giftBox.top, giftBox.centerX() + 14f, giftBox.bottom, p)
        c.drawRect(giftBox.left, giftBox.centerY() - 10f, giftBox.right, giftBox.centerY() + 10f, p)
        // Satin Bow Loops
        c.drawCircle(giftBox.centerX() - 18f, giftBox.top - 8f, 16f, p)
        c.drawCircle(giftBox.centerX() + 18f, giftBox.top - 8f, 16f, p)

        // 5. Bottom Left Dual Pill Badges: Navy Name Badge + Peach Phone Badge
        val navyPill = RectF(20f, 815f, 550f, 902f)
        p.style = Paint.Style.FILL; p.color = Color.rgb(28, 10, 48)
        c.drawRoundRect(navyPill, 36f, 36f, p)
        p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = Color.WHITE; p.alpha = 230
        c.drawRoundRect(navyPill, 36f, 36f, p)

        val nameText = d.values[TemplateField.NAME.name]?.ifBlank { "MR. JAGDISH TAUR" } ?: "MR. JAGDISH TAUR"
        val roleText = d.values[TemplateField.DESIGNATION.name]?.ifBlank { "RUBY, $company" } ?: "RUBY, $company"
        text(c, nameText.uppercase(), RectF(navyPill.left + 16f, 822f, navyPill.right - 16f, 866f), 32f, "#FFFFFF", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)
        text(c, roleText.uppercase(), RectF(navyPill.left + 16f, 868f, navyPill.right - 16f, 896f), 18f, "#F472B6", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)

        val peachPill = RectF(35f, 915f, 490f, 978f)
        p.style = Paint.Style.FILL; p.color = Color.rgb(252, 230, 210)
        c.drawRoundRect(peachPill, 30f, 30f, p)
        p.style = Paint.Style.FILL; p.color = Color.rgb(20, 10, 30)
        c.drawCircle(65f, 946f, 14f, p)
        p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f; p.color = Color.WHITE
        val phoneArc = RectF(57f, 938f, 73f, 954f)
        c.drawArc(phoneArc, -140f, 100f, false, p)
        val phoneText = d.branding.phone.ifBlank { "9075607350" }
        text(c, phoneText, RectF(85f, 922f, peachPill.right - 15f, 972f), 30f, "#140A1E", bold = true, italic = false, font = "sans-serif", align = "center", maxLines = 1)

        p.style = Paint.Style.FILL; p.color = Color.rgb(180, 150, 200); p.textSize = 14f; p.typeface = Typeface.DEFAULT
        c.save()
        c.rotate(-90f, 15f, 500f)
        c.drawText("Design By PosterFlow", -120f, 500f, p)
        c.restore()
    }
    /** Fit complete text into its logical slot, rather than silently clipping names or branding. */
    private fun text(c: Canvas,value: String,box: RectF,size: Float,hex: String,bold: Boolean,italic: Boolean,font: String,align: String,maxLines: Int,gold:Boolean=false) {
        if(value.isBlank() || box.width()<=0 || box.height()<=0) return
        val p=TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color=color(hex); typeface=Typeface.create(font,when{bold&&italic->Typeface.BOLD_ITALIC;bold->Typeface.BOLD;italic->Typeface.ITALIC;else->Typeface.NORMAL}) }
        val alignment=when(align){"left"->Layout.Alignment.ALIGN_NORMAL;"right"->Layout.Alignment.ALIGN_OPPOSITE;else->Layout.Alignment.ALIGN_CENTER}
        var textSize=size
        val words=value.split(Regex("\\s+"))
        fun layout(): StaticLayout { p.textSize=textSize; return StaticLayout.Builder.obtain(value,0,value.length,p,box.width().toInt().coerceAtLeast(1)).setAlignment(alignment).setIncludePad(false).setLineSpacing(2f,1f).build() }
        var result=layout()
        while((result.height>box.height() || result.lineCount>maxLines || words.any { p.measureText(it)>box.width() }) && textSize>6f){textSize-=1f;result=layout()}
        if(gold) {
            p.shader=LinearGradient(0f,0f,0f,result.height.toFloat().coerceAtLeast(1f),intArrayOf(color("#FFF2BA"),color("#F5C75D"),color("#AD6F1D"),color("#FFE5A1")),floatArrayOf(0f,.42f,.65f,1f),Shader.TileMode.CLAMP)
            p.setShadowLayer(2f,0f,3f,Color.BLACK)
        }
        c.save();c.clipRect(box);c.translate(box.left,box.top+max(0f,(box.height()-result.height)/2));result.draw(c);c.restore()
    }
}

fun adminDemo(template: PosterTemplate) = GeneratedPoster(template,
    values = mapOf("NAME" to "PRIYA SHARMA", "DESIGNATION" to "SOFTWARE DEVELOPER", "MESSAGE" to "New ideas. Shared ambition. A brighter tomorrow.", "ACHIEVEMENT" to "STAR PERFORMER", "AMOUNT" to "₹25,000", "DATE" to "17 SEPTEMBER 2026", "QUOTE" to "Success is not final. Courage to continue is what counts.", "COMPANY" to "ABC BUSINESS"),
    photo = "res:sample_business_woman", branding = BusinessBranding(company = "ABC BUSINESS", phone = "9876543210", website = "www.example.com", email = "hello@example.com", address = "Pune, Maharashtra", tagline = "Growing together",
        profilePhoto = "res:sample_business_woman", ownerName = "PRIYA SHARMA", ownerDesignation = "Business Leader"))
