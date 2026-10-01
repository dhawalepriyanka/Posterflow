package com.example.templates

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import java.io.File
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import kotlin.math.max
import kotlin.math.min

/**
 * Full-canvas artwork for the bundled Welcome collection.
 *
 * The preview, thumbnail and export paths all call [TemplateRenderer], which delegates here for
 * Welcome templates. Keeping this renderer bitmap-only avoids the usual Compose-preview/export
 * drift and makes every element scale from the same 1080 x 1080 coordinate system.
 */
internal object WelcomeArtworkRenderer {
    private data class Palette(
        val top: Int,
        val bottom: Int,
        val accent: Int,
        val text: Int = Color.WHITE,
        val muted: Int = Color.rgb(225, 215, 235),
        val panel: Int = Color.rgb(21, 7, 38),
        val ribbon: Int = Color.WHITE,
        val ribbonText: Int = Color.rgb(27, 14, 30)
    )

    fun supports(template: PosterTemplate): Boolean =
        template.category.equals("Welcome", ignoreCase = true) && (template.style in 130..145 || template.style in 147..155)

    fun draw(context: Context, canvas: Canvas, design: GeneratedPoster) {
        val style = design.template.style
        val palette = palette(style)
        drawBackground(canvas, style, palette)

        when (style) {
            141 -> greenModernWelcome(context, canvas, design, palette)
            142 -> cyanCoralWelcome(context, canvas, design, palette)
            143 -> purpleGoldPremium(context, canvas, design, palette)
            144 -> royalBlueCorporate(context, canvas, design, palette)
            147 -> purpleGlowWelcome(context, canvas, design, palette)
            148 -> goldenWaveWelcome(context, canvas, design, palette)
            149 -> corporateBlueWaveWelcome(context, canvas, design, palette)
            150 -> botanicalGreenWelcome(context, canvas, design, palette)
            151 -> dynamicMarathiWelcome(context, canvas, design, palette)
            else -> purpleGoldPremium(context, canvas, design, palette)
        }
    }

    private fun palette(style: Int): Palette = when (style) {
        130 -> Palette(rgb("#150022"), rgb("#5A075D"), rgb("#F5C85B"), panel = rgb("#22032E"))
        131 -> Palette(rgb("#04152E"), rgb("#123F79"), rgb("#65D4FF"), panel = rgb("#071D3D"), ribbon = rgb("#EAF8FF"), ribbonText = rgb("#061D3E"))
        132 -> Palette(rgb("#210038"), rgb("#770A75"), rgb("#FFD66E"), panel = rgb("#2C0646"))
        133 -> Palette(rgb("#FFFDF6"), rgb("#EFE4CF"), rgb("#B98220"), text = rgb("#2D1D16"), muted = rgb("#675347"), panel = Color.WHITE, ribbon = rgb("#2D1D16"), ribbonText = Color.WHITE)
        134 -> Palette(rgb("#360008"), rgb("#A40D2C"), rgb("#FFD15C"), panel = rgb("#4A0714"))
        135 -> Palette(rgb("#021C15"), rgb("#07634A"), rgb("#6DE5B6"), panel = rgb("#032B21"), ribbon = rgb("#E9FFF6"), ribbonText = rgb("#06382A"))
        136 -> Palette(rgb("#170A00"), rgb("#7C2D06"), rgb("#FF9F32"), panel = rgb("#281106"), ribbon = rgb("#FFF4E7"), ribbonText = rgb("#351505"))
        137 -> Palette(rgb("#050505"), rgb("#1B1710"), rgb("#E7BE55"), panel = rgb("#11100D"))
        138 -> Palette(rgb("#071A35"), rgb("#29205D"), rgb("#26D8FF"), panel = rgb("#0A2342"), ribbon = rgb("#E9FBFF"), ribbonText = rgb("#071D38"))
        139 -> Palette(rgb("#FFF8FA"), rgb("#F1DCEB"), rgb("#C42C73"), text = rgb("#4A1830"), muted = rgb("#765465"), panel = rgb("#FFF9FC"), ribbon = rgb("#7D174A"), ribbonText = Color.WHITE)
        140 -> Palette(rgb("#3A050C"), rgb("#8B250D"), rgb("#FFCD62"), panel = rgb("#4A0911"), ribbon = rgb("#FFF3D3"), ribbonText = rgb("#4A130D"))
        // 143 – Purple + Gold Premium: dark purple/magenta glow, metallic gold headline, right portrait, white ribbon
        143 -> Palette(rgb("#12002A"), rgb("#4A0060"), rgb("#F6C957"), panel = rgb("#1E0036"), ribbon = Color.WHITE, ribbonText = rgb("#1A0030"))
        // 144 – Royal Blue + Gold Corporate: deep navy premium, gold accents, left portrait, gold ribbon
        144 -> Palette(rgb("#020D1E"), rgb("#0D2E5A"), rgb("#FFD46A"), panel = rgb("#06192F"), ribbon = rgb("#FFD46A"), ribbonText = rgb("#040F1E"))
        // 145 – Black + Red + Gold Luxury: obsidian with crimson light, gold headline, red ribbon
        145 -> Palette(rgb("#060606"), rgb("#1A0808"), rgb("#F5C030"), panel = rgb("#110404"), ribbon = rgb("#B80C1A"), ribbonText = Color.WHITE)
        // 147 – Purple Glow Welcome (Hindi)
        147 -> Palette(rgb("#18042B"), rgb("#4A0060"), rgb("#E879F9"), panel = rgb("#1E0036"), ribbon = Color.WHITE, ribbonText = rgb("#1A0030"))
        // 148 – Golden Wave Welcome (Hindi)
        148 -> Palette(rgb("#0F0F10"), rgb("#1C1917"), rgb("#FBBF24"), panel = rgb("#18181B"), ribbon = rgb("#FBBF24"), ribbonText = rgb("#18181B"))
        // 149 – Corporate Blue Wave Welcome (Hindi)
        149 -> Palette(rgb("#031E3D"), rgb("#0A325E"), rgb("#38BDF8"), panel = rgb("#06192F"), ribbon = Color.WHITE, ribbonText = rgb("#031E3D"))
        // 150 – Botanical Green Welcome (Hindi)
        150 -> Palette(rgb("#022B18"), rgb("#0A4A28"), rgb("#22C55E"), panel = rgb("#032B21"), ribbon = Color.WHITE, ribbonText = rgb("#022616"))
        // 151 – Dynamic Marathi Welcome (Marathi)
        151 -> Palette(rgb("#05142E"), rgb("#0A244E"), rgb("#F97316"), panel = rgb("#031633"), ribbon = Color.WHITE, ribbonText = rgb("#05142E"))
        else -> Palette(rgb("#F7F8FB"), rgb("#E6EAF2"), rgb("#5D49D8"), text = rgb("#131827"), muted = rgb("#586174"), panel = Color.WHITE, ribbon = rgb("#131827"), ribbonText = Color.WHITE)
    }

    private fun drawBackground(c: Canvas, style: Int, p: Palette) {
        if (style in 141..155) return
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = LinearGradient(0f, 0f, 1080f, 1080f, p.top, p.bottom, Shader.TileMode.CLAMP)
        c.drawRect(0f, 0f, 1080f, 1080f, paint)
        paint.shader = null

        when (style) {
            130 -> {
                glow(c, 790f, 360f, 560f, rgb("#E315A9"), 150)
                glow(c, 180f, 690f, 360f, rgb("#6C24CE"), 90)
                particles(c, p.accent, 34, 130)
                cornerBrackets(c, p.accent, 34f)
            }
            131 -> {
                val path = Path().apply { moveTo(0f, 0f); lineTo(665f, 0f); lineTo(405f, 1080f); lineTo(0f, 1080f); close() }
                paint.color = rgb("#061128"); paint.alpha = 225; c.drawPath(path, paint)
                paint.style = Paint.Style.STROKE; paint.strokeWidth = 5f; paint.color = p.accent; paint.alpha = 160
                c.drawLine(665f, 0f, 405f, 1080f, paint)
                dotGrid(c, p.accent, 760f, 70f, 5, 10, 52f, 34)
            }
            132 -> {
                glow(c, 540f, 465f, 540f, rgb("#E51A9C"), 95)
                paint.style = Paint.Style.STROKE; paint.color = p.accent
                listOf(275f, 330f, 390f).forEachIndexed { i, radius ->
                    paint.strokeWidth = if (i == 1) 4f else 1.5f; paint.alpha = 95 - i * 22
                    c.drawCircle(540f, 470f, radius, paint)
                }
                particles(c, p.accent, 26, 105)
            }
            133 -> {
                paint.style = Paint.Style.STROKE; paint.color = p.accent; paint.alpha = 140; paint.strokeWidth = 2f
                c.drawRect(30f, 30f, 1050f, 1050f, paint)
                c.drawRect(44f, 44f, 1036f, 1036f, paint.apply { alpha = 55 })
                leafSpray(c, 75f, 95f, p.accent, false)
                leafSpray(c, 1005f, 985f, p.accent, true)
            }
            134 -> {
                glow(c, 770f, 390f, 520f, rgb("#FF315D"), 90)
                rays(c, 190f, 520f, p.accent, 20, 34)
                particles(c, p.accent, 44, 165)
                paint.style = Paint.Style.FILL; paint.color = rgb("#240007"); paint.alpha = 150
                c.drawRect(0f, 875f, 1080f, 1080f, paint)
            }
            135 -> {
                glow(c, 260f, 470f, 480f, rgb("#20D79A"), 75)
                paint.style = Paint.Style.STROKE; paint.color = p.accent; paint.strokeWidth = 2f; paint.alpha = 60
                for (i in 0..3) {
                    val wave = Path().apply { moveTo(-40f, 620f + i * 50f); cubicTo(230f, 450f - i * 20f, 710f, 790f, 1130f, 540f + i * 25f) }
                    c.drawPath(wave, paint)
                }
                dotGrid(c, p.accent, 650f, 80f, 7, 7, 54f, 30)
            }
            136 -> {
                glow(c, 795f, 350f, 500f, rgb("#FF6A00"), 105)
                paint.style = Paint.Style.FILL; paint.color = p.accent; paint.alpha = 22
                for (i in -2..5) {
                    val band = Path().apply { moveTo(i * 220f, 0f); lineTo(i * 220f + 100f, 0f); lineTo(i * 220f - 100f, 1080f); lineTo(i * 220f - 220f, 1080f); close() }
                    c.drawPath(band, paint)
                }
                chevrons(c, 50f, 830f, p.accent)
            }
            137 -> {
                paint.style = Paint.Style.STROKE; paint.color = p.accent; paint.strokeWidth = 2.5f; paint.alpha = 190
                c.drawRect(30f, 30f, 1050f, 1050f, paint)
                c.drawRect(45f, 45f, 1035f, 1035f, paint.apply { alpha = 70; strokeWidth = 1f })
                artDecoCorner(c, 52f, 52f, p.accent, false)
                artDecoCorner(c, 1028f, 1028f, p.accent, true)
                glow(c, 540f, 430f, 440f, p.accent, 32)
            }
            138 -> {
                glow(c, 250f, 300f, 470f, rgb("#00C8FF"), 85)
                glow(c, 890f, 710f, 430f, rgb("#A12EFF"), 78)
                paint.style = Paint.Style.FILL; paint.color = Color.WHITE; paint.alpha = 16
                c.drawRoundRect(RectF(40f, 45f, 1040f, 865f), 42f, 42f, paint)
                paint.style = Paint.Style.STROKE; paint.color = Color.WHITE; paint.alpha = 45; paint.strokeWidth = 2f
                c.drawRoundRect(RectF(40f, 45f, 1040f, 865f), 42f, 42f, paint)
            }
            139 -> {
                flower(c, 70f, 80f, 70f, rgb("#E95A9C"))
                flower(c, 1005f, 875f, 96f, rgb("#B87BD7"))
                paint.style = Paint.Style.STROKE; paint.strokeWidth = 3f; paint.color = p.accent; paint.alpha = 80
                val vine = Path().apply { moveTo(0f, 240f); cubicTo(180f, 90f, 260f, 250f, 390f, 120f) }
                c.drawPath(vine, paint)
            }
            140 -> {
                glow(c, 540f, 440f, 520f, rgb("#E96D16"), 80)
                mandala(c, 540f, 450f, 400f, p.accent)
                paint.style = Paint.Style.STROKE; paint.strokeWidth = 4f; paint.color = p.accent; paint.alpha = 170
                val arch = Path().apply { moveTo(160f, 900f); lineTo(160f, 390f); arcTo(RectF(160f, 90f, 920f, 670f), 180f, 180f, false); lineTo(920f, 900f) }
                c.drawPath(arch, paint)
            }
            141 -> {
                paint.style = Paint.Style.FILL; paint.color = p.accent
                c.drawRect(0f, 0f, 22f, 1080f, paint)
                paint.color = rgb("#171B2A")
                val block = Path().apply { moveTo(590f, 0f); lineTo(1080f, 0f); lineTo(1080f, 1080f); lineTo(445f, 1080f); close() }
                c.drawPath(block, paint)
                paint.style = Paint.Style.STROKE; paint.strokeWidth = 1f; paint.color = rgb("#D5DAE5")
                for (i in 1..7) c.drawLine(22f, i * 135f, 590f, i * 135f, paint)
            }
            // ---- PREMIUM TRILOGY (143-145): Reference-grade cinematic poster backgrounds ----
            143 -> {
                // Layered cinematic purple field: rich enough to support gold type, but restrained
                // around the editable copy and portrait.
                paint.shader = RadialGradient(790f, 430f, 790f,
                    intArrayOf(rgb("#82107F"), rgb("#45005F"), rgb("#18002F"), rgb("#090013")),
                    floatArrayOf(0f, .38f, .76f, 1f), Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 1080f, paint); paint.shader = null
                glow(c, 850f, 390f, 580f, rgb("#EF169F"), 122)
                glow(c, 250f, 400f, 420f, rgb("#7E25D5"), 92)
                glow(c, 960f, 760f, 430f, rgb("#175BFF"), 88)
                glow(c, 120f, 840f, 330f, rgb("#FF087F"), 90)
                glow(c, 965f, 95f, 245f, rgb("#D80AC4"), 72)
                premiumLightStreaks(c)
                premiumBokeh(c, p.accent)
                particles(c, p.accent, 28, 125)
                paint.shader = RadialGradient(
                    540f, 510f, 765f,
                    intArrayOf(Color.TRANSPARENT, Color.argb(28, 0, 0, 0), Color.argb(192, 0, 0, 0)),
                    floatArrayOf(0f, .68f, 1f), Shader.TileMode.CLAMP
                )
                paint.style = Paint.Style.FILL
                c.drawRect(0f, 0f, 1080f, 1080f, paint)
                paint.shader = null
                cornerBrackets(c, p.accent, 30f)
                // Inner gold border ring
                paint.style = Paint.Style.STROKE; paint.strokeWidth = 1.5f; paint.color = p.accent; paint.alpha = 55
                c.drawRect(44f, 44f, 1036f, 1036f, paint)
            }
            144 -> {
                // Royal Blue: Deep navy to sapphire radial glow, left-side electric cyan streak,
                // horizontal light sweep bands, dot grid overlay, subtle gold bokeh
                paint.shader = RadialGradient(270f, 430f, 660f,
                    intArrayOf(rgb("#0F3A72"), rgb("#071E42"), rgb("#020D1E")),
                    floatArrayOf(0f, 0.55f, 1f), Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, 1080f, 1080f, paint); paint.shader = null
                glow(c, 230f, 400f, 580f, rgb("#1565C0"), 110)
                glow(c, 900f, 700f, 380f, rgb("#00308F"), 75)
                // Horizontal light-streak bands (glass-light effect)
                paint.style = Paint.Style.FILL
                for (i in 0..5) {
                    val yb = 80f + i * 172f
                    paint.shader = LinearGradient(0f, yb, 1080f, yb + 4f,
                        intArrayOf(Color.TRANSPARENT, (0x14FFFFFF), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                    c.drawRect(0f, yb, 1080f, yb + 4f, paint)
                }
                paint.shader = null
                dotGrid(c, p.accent, 720f, 60f, 7, 9, 50f, 30)
                particles(c, p.accent, 36, 140)
                cornerBrackets(c, p.accent, 30f)
                // Gold double border
                paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f; paint.color = p.accent; paint.alpha = 75
                c.drawRect(34f, 34f, 1046f, 1046f, paint)
            }
            145 -> {
                // Black + Red + Gold: Obsidian black, dramatic crimson spotlight on right portrait area,
                // golden ray burst from center-left, art-deco double border frame, bokeh dust
                paint.style = Paint.Style.FILL; paint.color = rgb("#060606"); paint.alpha = 255
                c.drawRect(0f, 0f, 1080f, 1080f, paint)
                glow(c, 760f, 430f, 580f, rgb("#B0040E"), 120)
                glow(c, 230f, 540f, 340f, rgb("#7A0308"), 70)
                // Gold ray burst from upper-left origin
                rays(c, 140f, 140f, p.accent, 16, 28)
                particles(c, p.accent, 48, 150)
                // Art-deco double border
                paint.style = Paint.Style.STROKE; paint.strokeWidth = 3f; paint.color = p.accent; paint.alpha = 180
                c.drawRect(28f, 28f, 1052f, 1052f, paint)
                paint.strokeWidth = 1f; paint.alpha = 75
                c.drawRect(40f, 40f, 1040f, 1040f, paint)
                artDecoCorner(c, 54f, 54f, p.accent, false)
                artDecoCorner(c, 1026f, 1026f, p.accent, true)
                // Thin crimson inner band at portrait-seam
                paint.style = Paint.Style.FILL; paint.color = rgb("#8C040B"); paint.alpha = 120
                c.drawRect(582f, 0f, 590f, 1080f, paint)
            }
        }
    }

    private fun royalSpotlight(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        // Reference-like top hierarchy, populated only with this user's own brand and portrait.
        brandMark(context, c, d, RectF(28f, 24f, 340f, 108f), p, light = false)
        brandMark(context, c, d, RectF(740f, 24f, 1052f, 108f), p, light = false)
        supportPortrait(context, c, d, RectF(480f, 18f, 580f, 118f), p.accent)

        goldTitle(c, "WELCOME", RectF(22f, 128f, 600f, 285f), 104f, "left")
        label(c, "TO", RectF(245f, 275f, 365f, 325f), 32f, p.accent, true, "center", 1, 1f)

        ribbon(c, RectF(12f, 330f, 610f, 472f), p.ribbon, p.accent)
        personCopy(c, d, RectF(55f, 344f, 570f, 459f), p.ribbonText, rgb("#5B3510"), "center", showRole = true)

        goldTitle(c, "IN OUR GREAT", RectF(28f, 485f, 575f, 590f), 65f, "center")
        goldTitle(c, "PLATFORM", RectF(22f, 575f, 590f, 705f), 88f, "center")

        // Dominant welcomed-person portrait and slogan treatment on the right.
        portrait(context, c, d, RectF(605f, 150f, 1048f, 775f), "arch", p.accent, 5f)
        val slogan = d.branding.tagline
        if (slogan.isNotBlank()) {
            ribbon(c, RectF(570f, 700f, 1065f, 792f), rgb("#D7192D"), rgb("#FFB643"))
            label(c, slogan.uppercase(), RectF(615f, 716f, 1025f, 775f), 22f, Color.WHITE, true, "center", 1, 1f)
        }

        val quote = d.values[TemplateField.MESSAGE.name].orEmpty()
        if (quote.isNotBlank()) {
            label(c, "”", RectF(765f, 785f, 860f, 840f), 54f, p.accent, true, "center", 1)
            label(c, quote, RectF(555f, 830f, 1040f, 920f), 19f, Color.WHITE, true, "center", 3)
        }

        // Supporting leader and central business identity, omitted cleanly when unavailable.
        supportPortrait(context, c, d, RectF(28f, 760f, 255f, 987f), p.accent)
        businessLogo(context, c, d, RectF(315f, 805f, 475f, 895f), p)

        // High-contrast identity/contact footer, following the reference hierarchy without its data.
        val footer = Path().apply {
            moveTo(0f, 944f); quadTo(540f, 900f, 1080f, 944f); lineTo(1080f, 1080f); lineTo(0f, 1080f); close()
        }
        c.drawPath(footer, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
        val owner = d.branding.ownerName.ifBlank { d.branding.company }
        val ownerRole = d.branding.ownerDesignation
        if (owner.isNotBlank()) {
            label(c, owner.uppercase(), RectF(250f, 955f, 720f, 1012f), 30f, rgb("#17121A"), true, "center", 1, 1f)
            if (ownerRole.isNotBlank()) label(c, ownerRole.uppercase(), RectF(250f, 1008f, 720f, 1043f), 16f, rgb("#6B506C"), true, "center", 1)
        }
        val contact = d.branding.phone.ifBlank { d.branding.website }
        if (contact.isNotBlank()) {
            val contactBox = RectF(735f, 965f, 1052f, 1042f)
            c.drawRoundRect(contactBox, 38f, 38f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#08080B") })
            label(c, contact, RectF(765f, 978f, 1025f, 1030f), 21f, Color.WHITE, true, "center", 1)
        }
    }

    private fun corporateDiagonal(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        brandMark(context, c, d, RectF(45f, 35f, 400f, 115f), p, light = false)
        supportPortrait(context, c, d, RectF(900f, 28f, 1015f, 143f), p.accent)
        portrait(context, c, d, RectF(35f, 170f, 492f, 815f), "arch", p.accent, 6f)
        goldTitle(c, "WELCOME", RectF(500f, 145f, 1045f, 270f), 76f, "center")
        label(c, "TO OUR TEAM", RectF(570f, 265f, 975f, 310f), 24f, Color.WHITE, true, "center", 1, 4f)
        ribbon(c, RectF(470f, 330f, 1065f, 455f), p.ribbon, p.accent)
        personCopy(c, d, RectF(515f, 343f, 1020f, 442f), p.ribbonText, rgb("#0D4779"), "center", showRole = true)
        goldTitle(c, "JOIN OUR", RectF(505f, 470f, 1035f, 560f), 56f, "center")
        goldTitle(c, "GREAT TEAM", RectF(500f, 555f, 1040f, 660f), 67f, "center")
        welcomeMessage(c, d, RectF(530f, 685f, 1010f, 790f), p, "YOUR EXPERTISE MAKES OUR MISSION STRONGER")
        businessPanel(context, c, d, RectF(25f, 875f, 1055f, 1055f), p, dark = true, centered = false)
    }

    private fun violetPortal(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        brandMark(context, c, d, RectF(30f, 28f, 350f, 105f), p, light = false)
        brandMark(context, c, d, RectF(735f, 28f, 1050f, 105f), p, light = false)
        supportPortrait(context, c, d, RectF(480f, 22f, 590f, 132f), p.accent)
        goldTitle(c, "WELCOME", RectF(90f, 120f, 990f, 250f), 88f, "center")
        label(c, "TO OUR GREAT FAMILY", RectF(210f, 245f, 870f, 290f), 23f, Color.WHITE, true, "center", 1, 4f)
        goldTitle(c, "GROW WITH", RectF(35f, 325f, 560f, 415f), 55f, "center")
        goldTitle(c, "THE BEST", RectF(35f, 405f, 560f, 505f), 67f, "center")
        ribbon(c, RectF(20f, 525f, 590f, 650f), p.ribbon, p.accent)
        personCopy(c, d, RectF(65f, 539f, 545f, 637f), p.ribbonText, rgb("#8C4C08"), "center", showRole = true)
        portrait(context, c, d, RectF(585f, 290f, 1045f, 810f), "arch", p.accent, 7f)
        welcomeMessage(c, d, RectF(55f, 680f, 550f, 805f), p, "TOGETHER WE CREATE A BRIGHTER FUTURE")
        businessPanel(context, c, d, RectF(40f, 875f, 1040f, 1050f), p, dark = true, centered = true)
    }

    private fun ivoryEditorial(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        brandMark(context, c, d, RectF(45f, 35f, 420f, 115f), p, light = true)
        businessLogo(context, c, d, RectF(850f, 30f, 1035f, 120f), p)
        goldTitle(c, "WELCOME", RectF(45f, 145f, 590f, 270f), 76f, "left")
        label(c, "TO OUR PLATFORM", RectF(55f, 265f, 565f, 315f), 24f, p.text, true, "left", 1, 4f)
        ribbon(c, RectF(20f, 340f, 610f, 465f), p.ribbon, p.accent)
        personCopy(c, d, RectF(65f, 354f, 565f, 452f), p.ribbonText, p.accent, "center", showRole = false)
        goldTitle(c, "CREATE. LEAD.", RectF(45f, 490f, 585f, 575f), 49f, "center")
        label(c, "SUCCEED TOGETHER", RectF(55f, 575f, 575f, 640f), 34f, p.text, true, "center", 1, 2f)
        welcomeMessage(c, d, RectF(70f, 675f, 555f, 805f), p, "NEW IDEAS. SHARED PURPOSE. LASTING IMPACT.")
        portrait(context, c, d, RectF(600f, 135f, 1040f, 815f), "arch", p.accent, 7f)
        supportPortrait(context, c, d, RectF(520f, 690f, 650f, 820f), p.accent)
        businessPanel(context, c, d, RectF(35f, 875f, 1045f, 1050f), p, dark = false, centered = false)
    }

    private fun crimsonCelebration(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        brandMark(context, c, d, RectF(35f, 30f, 390f, 110f), p, light = false)
        supportPortrait(context, c, d, RectF(465f, 25f, 575f, 135f), p.accent)
        brandMark(context, c, d, RectF(715f, 30f, 1045f, 110f), p, light = false)
        goldTitle(c, "WELCOME", RectF(30f, 145f, 600f, 270f), 78f, "center")
        label(c, "TO A GREAT BEGINNING", RectF(70f, 265f, 565f, 315f), 22f, Color.WHITE, true, "center", 1, 3f)
        ribbon(c, RectF(15f, 340f, 610f, 470f), p.ribbon, p.accent)
        personCopy(c, d, RectF(60f, 354f, 565f, 457f), p.ribbonText, rgb("#8A1627"), "center", showRole = true)
        goldTitle(c, "DREAM BIG", RectF(35f, 495f, 585f, 585f), 59f, "center")
        goldTitle(c, "WIN TOGETHER", RectF(25f, 575f, 600f, 675f), 59f, "center")
        portrait(context, c, d, RectF(610f, 145f, 1045f, 810f), "arch", p.accent, 7f)
        welcomeMessage(c, d, RectF(65f, 710f, 570f, 820f), p, "TOGETHER WE TURN AMBITION INTO ACHIEVEMENT")
        businessPanel(context, c, d, RectF(25f, 875f, 1055f, 1055f), p, dark = true, centered = false)
    }

    private fun emeraldGrowth(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        brandMark(context, c, d, RectF(45f, 30f, 410f, 110f), p, light = false)
        supportPortrait(context, c, d, RectF(905f, 25f, 1020f, 140f), p.accent)
        portrait(context, c, d, RectF(35f, 150f, 500f, 815f), "arch", p.accent, 6f)
        goldTitle(c, "WELCOME", RectF(495f, 155f, 1040f, 280f), 75f, "center")
        label(c, "TO OUR GROWING FAMILY", RectF(535f, 275f, 1005f, 322f), 21f, Color.WHITE, true, "center", 1, 3f)
        goldTitle(c, "BUILD THE", RectF(505f, 350f, 1035f, 435f), 53f, "center")
        goldTitle(c, "FUTURE", RectF(505f, 425f, 1035f, 530f), 72f, "center")
        ribbon(c, RectF(470f, 545f, 1065f, 670f), p.ribbon, p.accent)
        personCopy(c, d, RectF(515f, 559f, 1020f, 657f), p.ribbonText, rgb("#087051"), "center", showRole = true)
        welcomeMessage(c, d, RectF(535f, 700f, 1010f, 815f), p, "GROW WITH PURPOSE. LEAD WITH COURAGE.")
        businessPanel(context, c, d, RectF(25f, 875f, 1055f, 1055f), p, dark = true, centered = false)
    }

    private fun amberMomentum(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        brandMark(context, c, d, RectF(35f, 30f, 400f, 110f), p, light = false)
        businessLogo(context, c, d, RectF(850f, 25f, 1040f, 120f), p)
        goldTitle(c, "WELCOME", RectF(30f, 145f, 605f, 270f), 78f, "center", amber = true)
        label(c, "SUCCESS STARTS TOGETHER", RectF(75f, 270f, 560f, 315f), 20f, Color.WHITE, true, "center", 1, 3f)
        ribbon(c, RectF(15f, 345f, 610f, 470f), p.ribbon, p.accent)
        personCopy(c, d, RectF(60f, 359f, 565f, 457f), p.ribbonText, rgb("#A13B08"), "center", showRole = true)
        goldTitle(c, "YOUR NEXT", RectF(35f, 495f, 585f, 580f), 54f, "center", amber = true)
        goldTitle(c, "BIG WIN", RectF(35f, 570f, 585f, 675f), 70f, "center", amber = true)
        portrait(context, c, d, RectF(610f, 145f, 1045f, 810f), "hex", p.accent, 7f)
        supportPortrait(context, c, d, RectF(535f, 690f, 665f, 820f), p.accent)
        welcomeMessage(c, d, RectF(65f, 710f, 545f, 820f), p, "BRING YOUR IDEAS. BUILD YOUR MOMENTUM.")
        businessPanel(context, c, d, RectF(25f, 875f, 1055f, 1055f), p, dark = true, centered = false)
    }

    private fun blackTieWelcome(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        brandMark(context, c, d, RectF(30f, 28f, 360f, 105f), p, light = false)
        brandMark(context, c, d, RectF(730f, 28f, 1050f, 105f), p, light = false)
        supportPortrait(context, c, d, RectF(485f, 20f, 595f, 130f), p.accent)
        goldTitle(c, "WELCOME", RectF(80f, 125f, 1000f, 245f), 84f, "center")
        label(c, "TO A WORLD OF EXCELLENCE", RectF(205f, 240f, 875f, 285f), 21f, Color.WHITE, true, "center", 1, 4f)
        goldTitle(c, "PREMIUM PEOPLE", RectF(25f, 325f, 565f, 415f), 48f, "center")
        goldTitle(c, "PREMIUM FUTURE", RectF(20f, 410f, 570f, 505f), 49f, "center")
        ribbon(c, RectF(15f, 530f, 600f, 650f), p.ribbon, p.accent)
        personCopy(c, d, RectF(60f, 543f, 555f, 637f), p.ribbonText, rgb("#8E6614"), "center", showRole = false)
        portrait(context, c, d, RectF(595f, 300f, 1045f, 815f), "arch", p.accent, 6f)
        businessPanel(context, c, d, RectF(30f, 875f, 1050f, 1050f), p, dark = true, centered = true)
    }

    private fun electricGlass(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        brandMark(context, c, d, RectF(45f, 35f, 410f, 115f), p, light = false)
        supportPortrait(context, c, d, RectF(905f, 28f, 1020f, 143f), p.accent)
        portrait(context, c, d, RectF(35f, 155f, 500f, 815f), "rounded", p.accent, 5f)
        goldTitle(c, "WELCOME", RectF(495f, 145f, 1040f, 270f), 74f, "center")
        label(c, "LET'S CREATE THE FUTURE", RectF(535f, 270f, 1005f, 315f), 19f, Color.WHITE, true, "center", 1, 3f)
        goldTitle(c, "FRESH IDEAS", RectF(505f, 345f, 1035f, 435f), 53f, "center")
        goldTitle(c, "BOLD VISION", RectF(500f, 425f, 1040f, 520f), 57f, "center")
        ribbon(c, RectF(470f, 545f, 1065f, 670f), p.ribbon, p.accent)
        personCopy(c, d, RectF(515f, 559f, 1020f, 657f), p.ribbonText, rgb("#087896"), "center", showRole = true)
        welcomeMessage(c, d, RectF(535f, 700f, 1010f, 815f), p, "FRESH THINKING. BOLD VISION. ONE TEAM.")
        businessPanel(context, c, d, RectF(25f, 875f, 1055f, 1055f), p, dark = true, centered = false)
    }

    private fun floralInvitation(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        brandMark(context, c, d, RectF(40f, 32f, 405f, 112f), p, light = true)
        businessLogo(context, c, d, RectF(850f, 27f, 1040f, 122f), p)
        goldTitle(c, "WELCOME", RectF(35f, 145f, 600f, 270f), 77f, "center")
        label(c, "WITH WARMTH AND JOY", RectF(95f, 270f, 555f, 315f), 20f, p.text, true, "center", 1, 4f)
        ribbon(c, RectF(15f, 345f, 610f, 470f), p.ribbon, p.accent)
        personCopy(c, d, RectF(60f, 359f, 565f, 457f), p.ribbonText, p.accent, "center", showRole = false)
        label(c, "BLOOM WITH", RectF(50f, 500f, 575f, 575f), 45f, p.text, true, "center", 1, 2f)
        goldTitle(c, "POSSIBILITY", RectF(30f, 565f, 600f, 670f), 61f, "center")
        portrait(context, c, d, RectF(605f, 145f, 1040f, 815f), "arch", p.accent, 6f)
        supportPortrait(context, c, d, RectF(535f, 690f, 665f, 820f), p.accent)
        welcomeMessage(c, d, RectF(65f, 705f, 545f, 820f), p, "MAY THIS NEW BEGINNING BLOOM WITH POSSIBILITY")
        businessPanel(context, c, d, RectF(30f, 875f, 1050f, 1050f), p, dark = false, centered = true)
    }

    private fun heritageArch(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        brandMark(context, c, d, RectF(30f, 28f, 350f, 105f), p, light = false)
        brandMark(context, c, d, RectF(735f, 28f, 1050f, 105f), p, light = false)
        supportPortrait(context, c, d, RectF(480f, 20f, 590f, 130f), p.accent)
        goldTitle(c, "WELCOME", RectF(80f, 125f, 1000f, 245f), 84f, "center")
        label(c, "हार्दिक स्वागत • TO OUR FAMILY", RectF(180f, 240f, 900f, 290f), 23f, Color.WHITE, true, "center", 1, 2f)
        goldTitle(c, "A PROUD", RectF(30f, 335f, 565f, 425f), 58f, "center")
        goldTitle(c, "NEW STORY", RectF(25f, 420f, 575f, 520f), 64f, "center")
        ribbon(c, RectF(15f, 545f, 600f, 670f), p.ribbon, p.accent)
        personCopy(c, d, RectF(60f, 559f, 555f, 657f), p.ribbonText, rgb("#9A3A10"), "center", showRole = true)
        portrait(context, c, d, RectF(595f, 300f, 1045f, 815f), "arch", p.accent, 7f)
        welcomeMessage(c, d, RectF(60f, 705f, 555f, 820f), p, "TOGETHER WE CREATE A PROUD NEW STORY")
        businessPanel(context, c, d, RectF(25f, 875f, 1055f, 1055f), p, dark = true, centered = true)
    }

    /**
     * Template 143 – Purple Gold Premium Welcome (Fixed Base Template).
     *
     * The supplied poster template image (welcome_purple_gold_base) is used as the fixed base layer.
     * All decorative elements, headlines, ribbons, and footer structure remain fixed as part of the image.
     *
     * Layers rendered in order:
     *   1. FIXED BASE TEMPLATE IMAGE (1080x1080)
     *   2. FIXED BRANDING OVERLAYS (auto-filled from app Profile / Settings, not typed in editor):
     *      - Company Logo (upper-left card)
     *      - Leader Photo (bottom-left circular frame)
     *      - Leader Name & Designation / Company Name (bottom footer text)
     *      - Phone & Website (bottom-right contact area)
     *   3. EDITABLE USER FIELDS (only 3 dynamic controls):
     *      - Main User Photo (right portrait area, cutout / pan / zoom / crop)
     *      - User Name (centered in white ribbon)
     *      - User Role / Designation (centered in purple pill below ribbon)
     */
    /**
     * Template 141 – Modern Green Welcome (Fixed Base Template from media_1790660817907).
     */
    private fun greenModernWelcome(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. FIXED BASE TEMPLATE IMAGE
        val baseBitmap = TemplateImages.read(context, "res:welcome_green_base")
            ?: runCatching {
                val f = File("app/src/main/res/drawable/welcome_green_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()
            ?: runCatching {
                val f = File("d:/poster-maker/app/src/main/res/drawable/welcome_green_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()

        if (baseBitmap != null) {
            c.drawBitmap(baseBitmap, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            paint.shader = LinearGradient(0f, 0f, 1080f, 1080f, p.top, p.bottom, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, 1080f, 1080f, paint)
            paint.shader = null
        }

        // 2. BRANDING OVERLAYS (dynamically populated from App Settings / Profile)
        // (a) Top-Left Company Logo / Name
        val logoSource = d.branding.logo.trim()
        val logoBmp = if (logoSource.isNotBlank()) TemplateImages.read(context, logoSource) else null
        val logoBox = RectF(45f, 35f, 270f, 108f)
        if (logoBmp != null) {
            drawBitmapContained(c, logoBmp, logoBox, d.logoCrop)
        } else if (d.branding.company.isNotBlank()) {
            label(c, d.branding.company.uppercase(), logoBox, 20f, rgb("#022616"), true, "center", 2, 0.5f)
        }

        // (b) Bottom-Left Leader Photo (Circular with clean green accent ring)
        val leaderSource = d.branding.profilePhoto.trim().takeIf { it.isNotBlank() }
        val leaderBox = RectF(22f, 850f, 252f, 1064f)
        val circlePath = Path().apply { addOval(leaderBox, Path.Direction.CW) }
        c.save()
        c.clipPath(circlePath)
        c.drawColor(Color.WHITE)
        if (!leaderSource.isNullOrBlank()) {
            val leaderBmp = TemplateImages.read(context, leaderSource)
            if (leaderBmp != null) {
                drawBitmapCover(c, leaderBmp, leaderBox, d.leaderCrop)
            }
        }
        c.restore()
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 3.5f; color = rgb("#10B981")
        }
        c.drawOval(leaderBox, ringPaint)

        // (c) Bottom Leader Name & Designation / Company Name (from App Settings)
        val leaderName = d.branding.ownerName.trim().ifBlank { d.branding.company.trim() }
        if (leaderName.isNotBlank()) {
            label(c, leaderName.uppercase(), RectF(265f, 908f, 660f, 950f), 24f, rgb("#022616"), true, "left", 1, 0.5f)
        }
        val leaderDesig = d.branding.ownerDesignation.trim()
        val subText = if (leaderDesig.isNotBlank()) leaderDesig else if (d.branding.company.isNotBlank() && d.branding.company != leaderName) d.branding.company else ""
        if (subText.isNotBlank()) {
            label(c, subText.uppercase(), RectF(265f, 952f, 660f, 990f), 18f, rgb("#10B981"), true, "left", 1, 0.5f)
        }

        // (d) Bottom-Right Contact: Phone & Website (from App Settings, natively integrated on dark wave)
        val phone = d.branding.phone.trim()
        if (phone.isNotBlank()) {
            val iconBox = RectF(668f, 932f, 706f, 970f)
            val iconBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = rgb("#10B981")
                style = Paint.Style.FILL
            }
            c.drawOval(iconBox, iconBgPaint)
            drawPhoneHandsetIcon(c, RectF(674f, 938f, 700f, 964f), Color.WHITE)
            label(c, phone, RectF(716f, 935f, 1055f, 970f), 20f, Color.WHITE, true, "left", 1)
        }
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            val iconBox = RectF(668f, 982f, 706f, 1020f)
            val iconBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = rgb("#10B981")
                style = Paint.Style.FILL
            }
            c.drawOval(iconBox, iconBgPaint)
            drawGlobeIcon(c, RectF(674f, 988f, 700f, 1014f), Color.WHITE)
            label(c, website, RectF(716f, 985f, 1055f, 1020f), 17f, rgb("#A7F3D0"), false, "left", 1)
        }

        // 3. EDITABLE USER PHOTO (Arched frame on the right)
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBitmap = TemplateImages.read(context, photoSource)
                ?: TemplateImages.read(context, "res:sample_business_woman")
                ?: TemplateImages.read(context, "res:sample_business_man")
            if (photoBitmap != null) {
                val photoBox = RectF(535f, 70f, 985f, 620f)
                val isCutout = d.backgroundRemoved || hasNativeTransparency(photoBitmap)
                if (isCutout) {
                    glow(c, 760f, 380f, 400f, rgb("#10B981"), 80)
                    drawPortraitEffects(c, photoBitmap, photoBox, d.crop, rgb("#059669"))
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = false, fadeBottom = true, fadeTop = false, fadeRight = false, ovalFeather = false)
                } else {
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = true, fadeBottom = true, fadeTop = true, fadeRight = true, ovalFeather = true)
                }
            }
        }

        // 4. EDITABLE NAME (Inside white rounded box)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "ANAYA SHARMA" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(60f, 400f, 560f, 482f)
            label(c, name.uppercase(), nameBox, 36f, rgb("#022616"), true, "center", 1, 0.5f)
        }

        // 5. EDITABLE ROLE / DESIGNATION (Inside green 3D bevel below name)
        val role = d.values[TemplateField.DESIGNATION.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "SOFTWARE DEVELOPER" else ""
        }
        if (role.isNotBlank()) {
            val roleBox = RectF(70f, 502f, 555f, 552f)
            label(c, role.uppercase(), roleBox, 20f, Color.WHITE, true, "center", 1, 0.8f)
        }

        // 6. EDITABLE WELCOME MESSAGE (Inside quote card — only rendered if user typed one)
        val message = d.values[TemplateField.MESSAGE.name].orEmpty().ifBlank {
            d.values[TemplateField.QUOTE.name].orEmpty()
        }
        if (message.isNotBlank()) {
            val quoteBox = RectF(650f, 775f, 930f, 865f)
            label(c, message, quoteBox, 18f, rgb("#2D473B"), false, "left", 3, 0.2f)
        }
    }

    /**
     * Template 142 – Ocean Cyan Welcome (Fixed Base Template from media_1790660829195).
     */
    private fun cyanCoralWelcome(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. FIXED BASE TEMPLATE IMAGE
        val baseBitmap = TemplateImages.read(context, "res:welcome_cyan_base")
            ?: runCatching {
                val f = File("app/src/main/res/drawable/welcome_cyan_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()
            ?: runCatching {
                val f = File("d:/poster-maker/app/src/main/res/drawable/welcome_cyan_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()

        if (baseBitmap != null) {
            c.drawBitmap(baseBitmap, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            paint.shader = LinearGradient(0f, 0f, 1080f, 1080f, p.top, p.bottom, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, 1080f, 1080f, paint)
            paint.shader = null
        }

        // 2. FIXED BRANDING OVERLAYS
        val logoSource = d.branding.logo.trim()
        val logoBmp = if (logoSource.isNotBlank()) TemplateImages.read(context, logoSource) else null
        if (logoBmp != null) {
            drawBitmapContained(c, logoBmp, RectF(52f, 42f, 274f, 122f))
        } else if (d.branding.company.isNotBlank()) {
            label(c, d.branding.company.uppercase(), RectF(52f, 42f, 274f, 122f), 20f, rgb("#051A30"), true, "center", 2, 0.5f)
        }

        val leaderSource = d.branding.profilePhoto.trim().takeIf { it.isNotBlank() }
        if (!leaderSource.isNullOrBlank()) {
            val leaderBmp = TemplateImages.read(context, leaderSource)
            if (leaderBmp != null) {
                val leaderBox = RectF(25f, 795f, 165f, 935f)
                val path = Path().apply { addOval(leaderBox, Path.Direction.CW) }
                c.save(); c.clipPath(path); c.drawColor(rgb("#051A30"))
                drawBitmapCover(c, leaderBmp, leaderBox, d.leaderCrop)
                c.restore()
                val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE; strokeWidth = 3.5f; color = rgb("#06B6D4")
                }
                c.drawOval(leaderBox, ringPaint)
            }
        }

        val leaderName = d.branding.ownerName.trim().ifBlank { d.branding.company.trim() }
        if (leaderName.isNotBlank()) {
            label(c, leaderName.uppercase(), RectF(180f, 862f, 660f, 902f), 24f, rgb("#051A30"), true, "left", 1, 0.5f)
        }
        val leaderDesig = d.branding.ownerDesignation.trim()
        val subText = if (leaderDesig.isNotBlank()) leaderDesig else if (d.branding.company.isNotBlank() && d.branding.company != leaderName) d.branding.company else ""
        if (subText.isNotBlank()) {
            label(c, subText.uppercase(), RectF(180f, 902f, 660f, 942f), 18f, rgb("#0891B2"), true, "left", 1, 0.5f)
        }

        val phone = d.branding.phone.trim()
        if (phone.isNotBlank()) {
            label(c, phone, RectF(740f, 855f, 1045f, 895f), 20f, rgb("#051A30"), true, "right", 1)
        }
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            label(c, website, RectF(740f, 905f, 1045f, 945f), 17f, rgb("#0891B2"), false, "right", 1)
        }

        // 3. EDITABLE USER PHOTO (Inside tilted card)
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBitmap = TemplateImages.read(context, photoSource)
                ?: TemplateImages.read(context, "res:sample_business_woman")
                ?: TemplateImages.read(context, "res:sample_business_man")
            if (photoBitmap != null) {
                val photoBox = RectF(535f, 95f, 985f, 570f)
                val isCutout = d.backgroundRemoved || hasNativeTransparency(photoBitmap)
                if (isCutout) {
                    glow(c, 760f, 330f, 400f, rgb("#06B6D4"), 80)
                    drawPortraitEffects(c, photoBitmap, photoBox, d.crop, rgb("#0284C7"))
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = false, fadeBottom = true, fadeTop = false, fadeRight = false, ovalFeather = false)
                } else {
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = true, fadeBottom = true, fadeTop = true, fadeRight = true, ovalFeather = true)
                }
            }
        }

        // 4. EDITABLE NAME (Inside white ribbon)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "ANAYA SHARMA" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(75f, 382f, 530f, 464f)
            label(c, name.uppercase(), nameBox, 36f, rgb("#051A30"), true, "center", 1, 0.5f)
        }

        // 5. EDITABLE ROLE / DESIGNATION (Inside dark pill below ribbon)
        val role = d.values[TemplateField.DESIGNATION.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "SOFTWARE DEVELOPER" else ""
        }
        if (role.isNotBlank()) {
            val roleBox = RectF(120f, 488f, 480f, 534f)
            label(c, role.uppercase(), roleBox, 20f, Color.WHITE, true, "center", 1, 0.8f)
        }
    }

    private fun purpleGoldPremium(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. FIXED TEMPLATE IMAGE
        val baseBitmap = TemplateImages.read(context, "res:welcome_purple_gold_base")
            ?: runCatching {
                val f = File("app/src/main/res/drawable/welcome_purple_gold_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()
            ?: runCatching {
                val f = File("d:/poster-maker/app/src/main/res/drawable/welcome_purple_gold_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()

        if (baseBitmap != null) {
            c.drawBitmap(baseBitmap, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            // Fallback gradient in test environments if asset is missing
            paint.shader = LinearGradient(0f, 0f, 1080f, 1080f, p.top, p.bottom, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, 1080f, 1080f, paint)
            paint.shader = null
        }

        // 2. FIXED BRANDING OVERLAYS (from Profile / Settings data)
        // (a) Upper-Left Company Logo / Branding
        val logoSource = d.branding.logo.trim()
        val logoBmp = if (logoSource.isNotBlank()) TemplateImages.read(context, logoSource) else null
        if (logoBmp != null) {
            drawBitmapContained(c, logoBmp, RectF(52f, 42f, 274f, 122f))
        } else if (d.branding.company.isNotBlank()) {
            label(
                c = c,
                value = d.branding.company.uppercase(),
                box = RectF(52f, 42f, 274f, 122f),
                requestedSize = 20f,
                color = rgb("#1A0030"),
                bold = true,
                align = "center",
                maxLines = 2,
                letterSpacing = 0.5f
            )
        }

        // (b) Bottom Leader Photo (Circular with metallic ring)
        val leaderSource = d.branding.profilePhoto.trim().takeIf { it.isNotBlank() }
        if (!leaderSource.isNullOrBlank()) {
            val leaderBmp = TemplateImages.read(context, leaderSource)
            if (leaderBmp != null) {
                val leaderBox = RectF(40f, 870f, 180f, 1010f)
                val path = Path().apply { addOval(leaderBox, Path.Direction.CW) }
                c.save()
                c.clipPath(path)
                c.drawColor(rgb("#2B0B3D"))
                drawBitmapCover(c, leaderBmp, leaderBox, d.leaderCrop)
                c.restore()
                val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 3.5f
                    color = rgb("#F6C957")
                }
                c.drawOval(leaderBox, ringPaint)
            }
        }

        // (c) Bottom Leader Name & Designation / Company Name
        val leaderName = d.branding.ownerName.trim().ifBlank { d.branding.company.trim() }
        if (leaderName.isNotBlank()) {
            label(
                c = c,
                value = leaderName.uppercase(),
                box = RectF(195f, 885f, 680f, 935f),
                requestedSize = 26f,
                color = Color.WHITE,
                bold = true,
                align = "left",
                maxLines = 1,
                letterSpacing = 0.5f
            )
        }
        val leaderDesig = d.branding.ownerDesignation.trim()
        val subText = if (leaderDesig.isNotBlank()) leaderDesig else if (d.branding.company.isNotBlank() && d.branding.company != leaderName) d.branding.company else ""
        if (subText.isNotBlank()) {
            label(
                c = c,
                value = subText.uppercase(),
                box = RectF(195f, 938f, 680f, 978f),
                requestedSize = 18f,
                color = rgb("#F6C957"),
                bold = true,
                align = "left",
                maxLines = 1,
                letterSpacing = 0.5f
            )
        }

        // (d) Bottom Contact: Phone & Website
        val phone = d.branding.phone.trim()
        if (phone.isNotBlank()) {
            drawPhoneHandsetIcon(c, RectF(708f, 890f, 736f, 918f), rgb("#F6C957"))
            label(
                c = c,
                value = phone,
                box = RectF(742f, 880f, 1045f, 928f),
                requestedSize = 20f,
                color = Color.WHITE,
                bold = true,
                align = "right",
                maxLines = 1
            )
        }
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            label(
                c = c,
                value = website,
                box = RectF(700f, 935f, 1045f, 975f),
                requestedSize = 18f,
                color = rgb("#F6C957"),
                bold = false,
                align = "right",
                maxLines = 1
            )
        }

        // 3. EDITABLE USER PHOTO
        // Positioned over the portrait area on the right with cutout / zoom / pan support
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBitmap = TemplateImages.read(context, photoSource)
                ?: TemplateImages.read(context, "res:sample_business_woman")
                ?: TemplateImages.read(context, "res:sample_business_man")
            if (photoBitmap != null) {
                val photoBox = RectF(520f, 75f, 1045f, 830f)
                val isCutout = d.backgroundRemoved || hasNativeTransparency(photoBitmap)
                if (isCutout) {
                    glow(c, 780f, 450f, 430f, rgb("#702CFF"), 90)
                    drawPortraitEffects(c, photoBitmap, photoBox, d.crop, rgb("#6726FF"))
                    drawSoftPortrait(
                        c, photoBitmap, photoBox, d.crop,
                        fadeLeft = false,
                        fadeBottom = true,
                        fadeTop = false,
                        fadeRight = false,
                        ovalFeather = false
                    )
                } else {
                    drawSoftPortrait(
                        c, photoBitmap, photoBox, d.crop,
                        fadeLeft = true,
                        fadeBottom = true,
                        fadeTop = true,
                        fadeRight = true,
                        ovalFeather = true
                    )
                }
            }
        }

        // 4. EDITABLE NAME
        // Centered over the white ribbon name area, bold, auto-resizing to prevent overflow
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "ANAYA SHARMA" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(60f, 486f, 555f, 576f)
            label(
                c = c,
                value = name.uppercase(),
                box = nameBox,
                requestedSize = 36f,
                color = rgb("#150A21"),
                bold = true,
                align = "center",
                maxLines = 1,
                letterSpacing = 0.5f
            )
        }

        // 5. EDITABLE ROLE / DESIGNATION
        // Centered directly below the name in purple pill, auto-fit
        val role = d.values[TemplateField.DESIGNATION.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "SOFTWARE DEVELOPER" else ""
        }
        if (role.isNotBlank()) {
            val roleBox = RectF(120f, 603f, 485f, 645f)
            label(
                c = c,
                value = role.uppercase(),
                box = roleBox,
                requestedSize = 20f,
                color = Color.WHITE,
                bold = true,
                align = "center",
                maxLines = 1,
                letterSpacing = 0.8f
            )
        }
    }

    /**
     * Template 144 – Royal Blue + Gold Corporate (reference-accurate).
     * Same composition as the reference but with deep royal blue color scheme:
     *  • Navy blue bokeh gradient (drawBackground handles this)
     *  • Dual logos top corners + 3 leader circles top center
     *  • Full-width gold "WELCOME" + "To" script
     *  • White scroll ribbon: guest name + designation
     *  • "JOIN OUR" + "GREAT TEAM" in gold block text
     *  • Guest photo RIGHT side, cutout style
     *  • Secondary person bottom-LEFT, cutout
     *  • Logo center-bottom
     *  • Blue banner with motivational text
     *  • White footer: name left, phone pill right
     */
    private fun royalBlueCorporate(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. FIXED BASE TEMPLATE IMAGE
        val baseBitmap = TemplateImages.read(context, "res:welcome_blue_base")
            ?: runCatching {
                val f = File("app/src/main/res/drawable/welcome_blue_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()
            ?: runCatching {
                val f = File("d:/poster-maker/app/src/main/res/drawable/welcome_blue_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()

        if (baseBitmap != null) {
            c.drawBitmap(baseBitmap, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            paint.shader = LinearGradient(0f, 0f, 1080f, 1080f, p.top, p.bottom, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, 1080f, 1080f, paint)
            paint.shader = null
        }

        // 2. FIXED BRANDING OVERLAYS
        val logoSource = d.branding.logo.trim()
        val logoBmp = if (logoSource.isNotBlank()) TemplateImages.read(context, logoSource) else null
        if (logoBmp != null) {
            drawBitmapContained(c, logoBmp, RectF(52f, 42f, 274f, 122f))
        } else if (d.branding.company.isNotBlank()) {
            label(c, d.branding.company.uppercase(), RectF(52f, 42f, 274f, 122f), 20f, rgb("#03142B"), true, "center", 2, 0.5f)
        }

        val leaderSource = d.branding.profilePhoto.trim().takeIf { it.isNotBlank() }
        if (!leaderSource.isNullOrBlank()) {
            val leaderBmp = TemplateImages.read(context, leaderSource)
            if (leaderBmp != null) {
                val leaderBox = RectF(50f, 790f, 190f, 930f)
                val path = Path().apply { addOval(leaderBox, Path.Direction.CW) }
                c.save(); c.clipPath(path); c.drawColor(rgb("#020D1E"))
                drawBitmapCover(c, leaderBmp, leaderBox, d.leaderCrop)
                c.restore()
                val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE; strokeWidth = 3.5f; color = rgb("#3B82F6")
                }
                c.drawOval(leaderBox, ringPaint)
            }
        }

        val leaderName = d.branding.ownerName.trim().ifBlank { d.branding.company.trim() }
        if (leaderName.isNotBlank()) {
            label(c, leaderName.uppercase(), RectF(205f, 860f, 660f, 900f), 24f, rgb("#03142B"), true, "left", 1, 0.5f)
        }
        val leaderDesig = d.branding.ownerDesignation.trim()
        val subText = if (leaderDesig.isNotBlank()) leaderDesig else if (d.branding.company.isNotBlank() && d.branding.company != leaderName) d.branding.company else ""
        if (subText.isNotBlank()) {
            label(c, subText.uppercase(), RectF(205f, 900f, 660f, 940f), 18f, rgb("#1D4ED8"), true, "left", 1, 0.5f)
        }

        val phone = d.branding.phone.trim()
        if (phone.isNotBlank()) {
            label(c, phone, RectF(745f, 855f, 1045f, 895f), 20f, rgb("#03142B"), true, "right", 1)
        }
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            label(c, website, RectF(745f, 905f, 1045f, 945f), 17f, rgb("#1D4ED8"), false, "right", 1)
        }

        // 3. EDITABLE USER PHOTO (Inside glass frame)
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBitmap = TemplateImages.read(context, photoSource)
                ?: TemplateImages.read(context, "res:sample_business_woman")
                ?: TemplateImages.read(context, "res:sample_business_man")
            if (photoBitmap != null) {
                val photoBox = RectF(575f, 75f, 980f, 705f)
                val isCutout = d.backgroundRemoved || hasNativeTransparency(photoBitmap)
                if (isCutout) {
                    glow(c, 780f, 390f, 400f, rgb("#3B82F6"), 80)
                    drawPortraitEffects(c, photoBitmap, photoBox, d.crop, rgb("#2563EB"))
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = false, fadeBottom = true, fadeTop = false, fadeRight = false, ovalFeather = false)
                } else {
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = true, fadeBottom = true, fadeTop = true, fadeRight = true, ovalFeather = true)
                }
            }
        }

        // 4. EDITABLE NAME (Inside silver ribbon)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "ANAYA SHARMA" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(65f, 400f, 565f, 480f)
            label(c, name.uppercase(), nameBox, 36f, rgb("#03142B"), true, "center", 1, 0.5f)
        }

        // 5. EDITABLE ROLE / DESIGNATION (Inside dark pill below ribbon)
        val role = d.values[TemplateField.DESIGNATION.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "SOFTWARE DEVELOPER" else ""
        }
        if (role.isNotBlank()) {
            val roleBox = RectF(110f, 508f, 530f, 554f)
            label(c, role.uppercase(), roleBox, 20f, Color.WHITE, true, "center", 1, 0.8f)
        }
    }

    /**
     * Template 147 – Purple Glow Welcome (Hindi).
     * "नई शुरुआत में आपका स्वागत है"
     */
    private fun purpleGlowWelcome(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. Fixed Base Image
        val baseBitmap = TemplateImages.read(context, "res:welcome_purple_glow_base")
            ?: runCatching {
                val f = File("app/src/main/res/drawable/welcome_purple_glow_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()
            ?: runCatching {
                val f = File("d:/poster-maker/app/src/main/res/drawable/welcome_purple_glow_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()

        if (baseBitmap != null) {
            c.drawBitmap(baseBitmap, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            paint.shader = LinearGradient(0f, 0f, 1080f, 1080f, p.top, p.bottom, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, 1080f, 1080f, paint); paint.shader = null
        }

        // 2. Fixed Branding Overlays
        // (a) Top-Left Logo
        val logoSource = d.branding.logo.trim()
        val logoBmp = if (logoSource.isNotBlank()) TemplateImages.read(context, logoSource) else null
        val logoBox = RectF(35f, 25f, 240f, 120f)
        paint.color = Color.WHITE; paint.style = Paint.Style.FILL
        c.drawRoundRect(logoBox, 18f, 18f, paint)
        if (logoBmp != null) {
            drawBitmapContained(c, logoBmp, RectF(logoBox.left + 10f, logoBox.top + 8f, logoBox.right - 10f, logoBox.bottom - 8f))
        } else if (d.branding.company.isNotBlank()) {
            label(c, d.branding.company.uppercase(), logoBox, 20f, rgb("#1A0030"), true, "center", 2, 0.5f)
        }

        // (b) Bottom Leader Photo
        val leaderSource = d.branding.profilePhoto.trim().takeIf { it.isNotBlank() }
        val leaderBox = RectF(22f, 848f, 198f, 1024f)
        val circlePath = Path().apply { addOval(leaderBox, Path.Direction.CW) }
        c.save(); c.clipPath(circlePath); c.drawColor(rgb("#18042B"))
        if (!leaderSource.isNullOrBlank()) {
            val leaderBmp = TemplateImages.read(context, leaderSource)
            if (leaderBmp != null) drawBitmapCover(c, leaderBmp, leaderBox, d.leaderCrop)
        }
        c.restore()
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 3.5f; color = rgb("#E879F9")
        }
        c.drawOval(leaderBox, ringPaint)

        // (c) Bottom Branding Pill (Company, Phone, Website)
        val pillBox = RectF(210f, 850f, 1020f, 980f)
        paint.color = Color.WHITE; c.drawRoundRect(pillBox, 28f, 28f, paint)
        val companyName = d.branding.company.trim().ifBlank { d.branding.ownerName.trim() }
        if (companyName.isNotBlank()) {
            label(c, companyName.uppercase(), RectF(230f, 875f, 420f, 955f), 24f, rgb("#1A0030"), true, "left", 2, 0.5f)
        }
        paint.color = rgb("#E2E8F0"); paint.strokeWidth = 2f
        c.drawLine(425f, 870f, 425f, 960f, paint)
        c.drawLine(675f, 870f, 675f, 960f, paint)

        val phone = d.branding.phone.trim()
        if (phone.isNotBlank()) {
            drawPhoneHandsetIcon(c, RectF(440f, 885f, 495f, 940f), rgb("#702CFF"))
            label(c, phone, RectF(505f, 875f, 665f, 950f), 21f, rgb("#1A0030"), true, "left", 1)
        }
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            drawGlobeIcon(c, RectF(690f, 885f, 745f, 940f), rgb("#702CFF"))
            label(c, website, RectF(755f, 875f, 1005f, 950f), 17f, rgb("#702CFF"), false, "left", 1)
        }

        // 3. User Photo (Right side)
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBitmap = TemplateImages.read(context, photoSource)
                ?: TemplateImages.read(context, "res:sample_business_woman")
                ?: TemplateImages.read(context, "res:sample_business_man")
            if (photoBitmap != null) {
                val photoBox = RectF(490f, 120f, 1040f, 770f)
                val isCutout = d.backgroundRemoved || hasNativeTransparency(photoBitmap)
                if (isCutout) {
                    glow(c, 780f, 390f, 400f, rgb("#E879F9"), 80)
                    drawPortraitEffects(c, photoBitmap, photoBox, d.crop, rgb("#A21CAF"))
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = false, fadeBottom = true, fadeTop = false, fadeRight = false, ovalFeather = false)
                } else {
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = true, fadeBottom = true, fadeTop = true, fadeRight = true, ovalFeather = true)
                }
            }
        }

        // 4. Name Ribbon
        val name = d.values[TemplateField.NAME.name].orEmpty()
        if (name.isNotBlank()) {
            val ribbonCover = RectF(95f, 690f, 605f, 770f)
            paint.color = Color.WHITE; c.drawRoundRect(ribbonCover, 16f, 16f, paint)
            label(c, name.uppercase(), RectF(110f, 695f, 590f, 765f), 34f, rgb("#1A0030"), true, "center", 1, 0.5f)
        }

        // 5. Role / Designation Pill
        val role = d.values[TemplateField.DESIGNATION.name].orEmpty()
        if (role.isNotBlank()) {
            val pillCover = RectF(120f, 745f, 550f, 800f)
            paint.color = rgb("#702CFF"); c.drawRoundRect(pillCover, 22f, 22f, paint)
            label(c, role.uppercase(), RectF(125f, 748f, 545f, 797f), 19f, Color.WHITE, true, "center", 1, 0.8f)
        }
    }

    /**
     * Template 148 – Golden Wave Welcome (Hindi).
     * "नए सदस्य का हार्दिक स्वागत"
     */
    private fun goldenWaveWelcome(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. Fixed Base Image
        val baseBitmap = TemplateImages.read(context, "res:welcome_yellow_wave_base")
            ?: runCatching {
                val f = File("app/src/main/res/drawable/welcome_yellow_wave_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()
            ?: runCatching {
                val f = File("d:/poster-maker/app/src/main/res/drawable/welcome_yellow_wave_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()

        if (baseBitmap != null) {
            c.drawBitmap(baseBitmap, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            paint.shader = LinearGradient(0f, 0f, 1080f, 1080f, p.top, p.bottom, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, 1080f, 1080f, paint); paint.shader = null
        }

        // 2. Branding Overlays
        val logoSource = d.branding.logo.trim()
        val logoBmp = if (logoSource.isNotBlank()) TemplateImages.read(context, logoSource) else null
        val logoBox = RectF(35f, 25f, 240f, 120f)
        paint.color = Color.WHITE; paint.style = Paint.Style.FILL
        c.drawRoundRect(logoBox, 18f, 18f, paint)
        if (logoBmp != null) {
            drawBitmapContained(c, logoBmp, RectF(logoBox.left + 10f, logoBox.top + 8f, logoBox.right - 10f, logoBox.bottom - 8f))
        } else if (d.branding.company.isNotBlank()) {
            label(c, d.branding.company.uppercase(), logoBox, 20f, rgb("#18181B"), true, "center", 2, 0.5f)
        }

        // Leader photo
        val leaderSource = d.branding.profilePhoto.trim().takeIf { it.isNotBlank() }
        val leaderBox = RectF(28f, 852f, 202f, 1026f)
        val circlePath = Path().apply { addOval(leaderBox, Path.Direction.CW) }
        c.save(); c.clipPath(circlePath); c.drawColor(rgb("#18181B"))
        if (!leaderSource.isNullOrBlank()) {
            val leaderBmp = TemplateImages.read(context, leaderSource)
            if (leaderBmp != null) drawBitmapCover(c, leaderBmp, leaderBox, d.leaderCrop)
        }
        c.restore()
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 3.5f; color = rgb("#FBBF24")
        }
        c.drawOval(leaderBox, ringPaint)

        // Dark bar footer
        val barBox = RectF(210f, 855f, 1050f, 985f)
        paint.color = rgb("#18181B"); c.drawRoundRect(barBox, 24f, 24f, paint)
        val companyName = d.branding.company.trim().ifBlank { d.branding.ownerName.trim() }
        if (companyName.isNotBlank()) {
            label(c, companyName.uppercase(), RectF(230f, 868f, 530f, 920f), 24f, Color.WHITE, true, "left", 1, 0.5f)
        }
        val tagline = d.branding.tagline.trim()
        if (tagline.isNotBlank()) {
            label(c, tagline, RectF(230f, 920f, 530f, 955f), 14f, rgb("#A1A1AA"), false, "left", 1)
        }
        paint.color = rgb("#3F3F46"); paint.strokeWidth = 2f
        c.drawLine(535f, 875f, 535f, 965f, paint)
        c.drawLine(735f, 875f, 735f, 965f, paint)

        val phone = d.branding.phone.trim()
        if (phone.isNotBlank()) {
            drawPhoneHandsetIcon(c, RectF(545f, 888f, 595f, 938f), rgb("#FBBF24"))
            label(c, phone, RectF(605f, 878f, 730f, 948f), 21f, Color.WHITE, true, "left", 1)
        }
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            drawGlobeIcon(c, RectF(745f, 888f, 795f, 938f), rgb("#FBBF24"))
            label(c, website, RectF(805f, 878f, 1040f, 948f), 17f, Color.WHITE, false, "left", 1)
        }

        // 3. User Photo
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBitmap = TemplateImages.read(context, photoSource)
                ?: TemplateImages.read(context, "res:sample_business_woman")
                ?: TemplateImages.read(context, "res:sample_business_man")
            if (photoBitmap != null) {
                val photoBox = RectF(530f, 180f, 1030f, 770f)
                val isCutout = d.backgroundRemoved || hasNativeTransparency(photoBitmap)
                if (isCutout) {
                    glow(c, 780f, 400f, 400f, rgb("#FBBF24"), 80)
                    drawPortraitEffects(c, photoBitmap, photoBox, d.crop, rgb("#D97706"))
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = false, fadeBottom = true, fadeTop = false, fadeRight = false, ovalFeather = false)
                } else {
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = true, fadeBottom = true, fadeTop = true, fadeRight = true, ovalFeather = true)
                }
            }
        }

        // 4. Name Card
        val name = d.values[TemplateField.NAME.name].orEmpty()
        if (name.isNotBlank()) {
            val nameCard = RectF(45f, 530f, 545f, 610f)
            paint.color = rgb("#FBBF24"); c.drawRoundRect(nameCard, 20f, 20f, paint)
            label(c, name.uppercase(), RectF(60f, 540f, 530f, 605f), 32f, rgb("#18181B"), true, "center", 1, 0.5f)
        }

        // 5. Designation Pill
        val role = d.values[TemplateField.DESIGNATION.name].orEmpty()
        if (role.isNotBlank()) {
            val roleCard = RectF(45f, 598f, 545f, 646f)
            paint.color = rgb("#18181B"); c.drawRoundRect(roleCard, 14f, 14f, paint)
            label(c, role.uppercase(), RectF(60f, 600f, 530f, 644f), 19f, Color.WHITE, true, "center", 1, 0.8f)
        }
    }

    /**
     * Template 149 – Corporate Blue Wave Welcome (Hindi).
     * "हमारी टीम में आपका स्वागत है"
     */
    private fun corporateBlueWaveWelcome(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. Fixed Base Image
        val baseBitmap = TemplateImages.read(context, "res:welcome_sky_blue_base")
            ?: runCatching {
                val f = File("app/src/main/res/drawable/welcome_sky_blue_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()
            ?: runCatching {
                val f = File("d:/poster-maker/app/src/main/res/drawable/welcome_sky_blue_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()

        if (baseBitmap != null) {
            c.drawBitmap(baseBitmap, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            paint.shader = LinearGradient(0f, 0f, 1080f, 1080f, p.top, p.bottom, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, 1080f, 1080f, paint); paint.shader = null
        }

        // 2. Branding Overlays
        val logoSource = d.branding.logo.trim()
        val logoBmp = if (logoSource.isNotBlank()) TemplateImages.read(context, logoSource) else null
        val logoBox = RectF(35f, 25f, 240f, 120f)
        paint.color = Color.WHITE; paint.style = Paint.Style.FILL
        c.drawRoundRect(logoBox, 18f, 18f, paint)
        if (logoBmp != null) {
            drawBitmapContained(c, logoBmp, RectF(logoBox.left + 10f, logoBox.top + 8f, logoBox.right - 10f, logoBox.bottom - 8f))
        } else if (d.branding.company.isNotBlank()) {
            label(c, d.branding.company.uppercase(), logoBox, 20f, rgb("#031E3D"), true, "center", 2, 0.5f)
        }

        // Leader photo
        val leaderSource = d.branding.profilePhoto.trim().takeIf { it.isNotBlank() }
        val leaderBox = RectF(30f, 852f, 200f, 1022f)
        val circlePath = Path().apply { addOval(leaderBox, Path.Direction.CW) }
        c.save(); c.clipPath(circlePath); c.drawColor(rgb("#031E3D"))
        if (!leaderSource.isNullOrBlank()) {
            val leaderBmp = TemplateImages.read(context, leaderSource)
            if (leaderBmp != null) drawBitmapCover(c, leaderBmp, leaderBox, d.leaderCrop)
        }
        c.restore()
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 3.5f; color = rgb("#0284C7")
        }
        c.drawOval(leaderBox, ringPaint)

        // White pill footer
        val pillBox = RectF(210f, 855f, 1050f, 985f)
        paint.color = Color.WHITE; c.drawRoundRect(pillBox, 28f, 28f, paint)
        val companyName = d.branding.company.trim().ifBlank { d.branding.ownerName.trim() }
        if (companyName.isNotBlank()) {
            label(c, companyName.uppercase(), RectF(230f, 870f, 560f, 920f), 24f, rgb("#031E3D"), true, "left", 1, 0.5f)
        }
        val tagline = d.branding.tagline.trim()
        if (tagline.isNotBlank()) {
            label(c, tagline, RectF(230f, 920f, 560f, 955f), 14f, rgb("#64748B"), false, "left", 1)
        }
        paint.color = rgb("#E2E8F0"); paint.strokeWidth = 2f
        c.drawLine(575f, 875f, 575f, 965f, paint)

        val phone = d.branding.phone.trim()
        if (phone.isNotBlank()) {
            drawPhoneHandsetIcon(c, RectF(595f, 875f, 645f, 925f), rgb("#0284C7"))
            label(c, phone, RectF(655f, 868f, 850f, 930f), 20f, rgb("#031E3D"), true, "left", 1)
        }
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            drawGlobeIcon(c, RectF(595f, 930f, 645f, 975f), rgb("#0284C7"))
            label(c, website, RectF(655f, 925f, 1040f, 980f), 16f, rgb("#031E3D"), false, "left", 1)
        }

        // 3. User Photo
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBitmap = TemplateImages.read(context, photoSource)
                ?: TemplateImages.read(context, "res:sample_business_woman")
                ?: TemplateImages.read(context, "res:sample_business_man")
            if (photoBitmap != null) {
                val photoBox = RectF(540f, 110f, 995f, 690f)
                val isCutout = d.backgroundRemoved || hasNativeTransparency(photoBitmap)
                if (isCutout) {
                    glow(c, 760f, 360f, 400f, rgb("#0284C7"), 80)
                    drawPortraitEffects(c, photoBitmap, photoBox, d.crop, rgb("#0369A1"))
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = false, fadeBottom = true, fadeTop = false, fadeRight = false, ovalFeather = false)
                } else {
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = true, fadeBottom = true, fadeTop = true, fadeRight = true, ovalFeather = true)
                }
            }
        }

        // 4. Name Ribbon
        val name = d.values[TemplateField.NAME.name].orEmpty()
        if (name.isNotBlank()) {
            val ribbonCover = RectF(45f, 475f, 555f, 555f)
            paint.color = Color.WHITE; c.drawRoundRect(ribbonCover, 24f, 24f, paint)
            label(c, name.uppercase(), RectF(60f, 482f, 540f, 548f), 32f, rgb("#031E3D"), true, "center", 1, 0.5f)
        }

        // 5. Designation Pill
        val role = d.values[TemplateField.DESIGNATION.name].orEmpty()
        if (role.isNotBlank()) {
            val roleCover = RectF(50f, 545f, 540f, 595f)
            paint.color = rgb("#032B59"); c.drawRoundRect(roleCover, 16f, 16f, paint)
            label(c, role.uppercase(), RectF(60f, 548f, 530f, 592f), 19f, Color.WHITE, true, "center", 1, 0.8f)
        }
    }

    /**
     * Template 150 – Botanical Green Welcome (Hindi).
     * "आपका हार्दिक स्वागत है"
     */
    private fun botanicalGreenWelcome(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. Fixed Base Image
        val baseBitmap = TemplateImages.read(context, "res:welcome_botanical_green_base")
            ?: runCatching {
                val f = File("app/src/main/res/drawable/welcome_botanical_green_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()
            ?: runCatching {
                val f = File("d:/poster-maker/app/src/main/res/drawable/welcome_botanical_green_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()

        if (baseBitmap != null) {
            c.drawBitmap(baseBitmap, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            paint.shader = LinearGradient(0f, 0f, 1080f, 1080f, p.top, p.bottom, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, 1080f, 1080f, paint); paint.shader = null
        }

        // 2. Branding Overlays
        val logoSource = d.branding.logo.trim()
        val logoBmp = if (logoSource.isNotBlank()) TemplateImages.read(context, logoSource) else null
        val logoBox = RectF(35f, 25f, 240f, 120f)
        paint.color = Color.WHITE; paint.style = Paint.Style.FILL
        c.drawRoundRect(logoBox, 18f, 18f, paint)
        if (logoBmp != null) {
            drawBitmapContained(c, logoBmp, RectF(logoBox.left + 10f, logoBox.top + 8f, logoBox.right - 10f, logoBox.bottom - 8f))
        } else if (d.branding.company.isNotBlank()) {
            label(c, d.branding.company.uppercase(), logoBox, 20f, rgb("#022616"), true, "center", 2, 0.5f)
        }

        // Leader photo
        val leaderSource = d.branding.profilePhoto.trim().takeIf { it.isNotBlank() }
        val leaderBox = RectF(26f, 842f, 228f, 1044f)
        val circlePath = Path().apply { addOval(leaderBox, Path.Direction.CW) }
        c.save(); c.clipPath(circlePath); c.drawColor(rgb("#022616"))
        if (!leaderSource.isNullOrBlank()) {
            val leaderBmp = TemplateImages.read(context, leaderSource)
            if (leaderBmp != null) drawBitmapCover(c, leaderBmp, leaderBox, d.leaderCrop)
        }
        c.restore()
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 3.5f; color = rgb("#16A34A")
        }
        c.drawOval(leaderBox, ringPaint)

        // White pill footer
        val pillBox = RectF(230f, 875f, 980f, 985f)
        paint.color = Color.WHITE; c.drawRoundRect(pillBox, 28f, 28f, paint)
        val companyName = d.branding.company.trim().ifBlank { d.branding.ownerName.trim() }
        if (companyName.isNotBlank()) {
            label(c, companyName.uppercase(), RectF(250f, 890f, 500f, 960f), 24f, rgb("#022616"), true, "left", 1, 0.5f)
        }
        paint.color = rgb("#E2E8F0"); paint.strokeWidth = 2f
        c.drawLine(505f, 885f, 505f, 975f, paint)

        val phone = d.branding.phone.trim()
        if (phone.isNotBlank()) {
            drawPhoneHandsetIcon(c, RectF(520f, 880f, 565f, 925f), rgb("#16A34A"))
            label(c, phone, RectF(580f, 872f, 780f, 930f), 20f, rgb("#022616"), true, "left", 1)
        }
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            drawGlobeIcon(c, RectF(520f, 932f, 565f, 975f), rgb("#16A34A"))
            label(c, website, RectF(580f, 928f, 970f, 980f), 16f, rgb("#022616"), false, "left", 1)
        }

        // 3. User Photo (Arched right frame)
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBitmap = TemplateImages.read(context, photoSource)
                ?: TemplateImages.read(context, "res:sample_business_woman")
                ?: TemplateImages.read(context, "res:sample_business_man")
            if (photoBitmap != null) {
                val photoBox = RectF(495f, 115f, 1005f, 630f)
                val isCutout = d.backgroundRemoved || hasNativeTransparency(photoBitmap)
                if (isCutout) {
                    glow(c, 750f, 370f, 400f, rgb("#16A34A"), 80)
                    drawPortraitEffects(c, photoBitmap, photoBox, d.crop, rgb("#15803D"))
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = false, fadeBottom = true, fadeTop = false, fadeRight = false, ovalFeather = false)
                } else {
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = true, fadeBottom = true, fadeTop = true, fadeRight = true, ovalFeather = true)
                }
            }
        }

        // 4. Name Card (Lower right below photo)
        val name = d.values[TemplateField.NAME.name].orEmpty()
        if (name.isNotBlank()) {
            val nameBox = RectF(550f, 610f, 965f, 680f)
            paint.color = Color.WHITE; c.drawRoundRect(nameBox, 18f, 18f, paint)
            paint.style = Paint.Style.STROKE; paint.strokeWidth = 2.5f; paint.color = rgb("#16A34A")
            c.drawRoundRect(nameBox, 18f, 18f, paint); paint.style = Paint.Style.FILL
            label(c, name.uppercase(), RectF(560f, 615f, 955f, 675f), 30f, rgb("#022616"), true, "center", 1, 0.5f)
        }

        // 5. Designation Pill
        val role = d.values[TemplateField.DESIGNATION.name].orEmpty()
        if (role.isNotBlank()) {
            val roleBox = RectF(570f, 675f, 945f, 725f)
            paint.color = rgb("#15803D"); c.drawRoundRect(roleBox, 14f, 14f, paint)
            label(c, role.uppercase(), RectF(575f, 678f, 940f, 722f), 18f, Color.WHITE, true, "center", 1, 0.8f)
        }
    }

    /**
     * Template 151 – Dynamic Marathi Welcome (Marathi).
     * "नवीन सदस्याचे हार्दिक स्वागत"
     */
    private fun dynamicMarathiWelcome(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. Fixed Base Image
        val baseBitmap = TemplateImages.read(context, "res:welcome_marathi_navy_base")
            ?: runCatching {
                val f = File("app/src/main/res/drawable/welcome_marathi_navy_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()
            ?: runCatching {
                val f = File("d:/poster-maker/app/src/main/res/drawable/welcome_marathi_navy_base.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()

        if (baseBitmap != null) {
            c.drawBitmap(baseBitmap, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            paint.shader = LinearGradient(0f, 0f, 1080f, 1080f, p.top, p.bottom, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, 1080f, 1080f, paint); paint.shader = null
        }

        // 2. Branding Overlays
        val logoSource = d.branding.logo.trim()
        val logoBmp = if (logoSource.isNotBlank()) TemplateImages.read(context, logoSource) else null
        val logoBox = RectF(35f, 25f, 240f, 120f)
        paint.color = Color.WHITE; paint.style = Paint.Style.FILL
        c.drawRoundRect(logoBox, 18f, 18f, paint)
        if (logoBmp != null) {
            drawBitmapContained(c, logoBmp, RectF(logoBox.left + 10f, logoBox.top + 8f, logoBox.right - 10f, logoBox.bottom - 8f))
        } else if (d.branding.company.isNotBlank()) {
            label(c, d.branding.company.uppercase(), logoBox, 20f, rgb("#05142E"), true, "center", 2, 0.5f)
        }

        // Leader photo
        val leaderSource = d.branding.profilePhoto.trim().takeIf { it.isNotBlank() }
        val leaderBox = RectF(40f, 860f, 210f, 1030f)
        val circlePath = Path().apply { addOval(leaderBox, Path.Direction.CW) }
        c.save(); c.clipPath(circlePath); c.drawColor(rgb("#05142E"))
        if (!leaderSource.isNullOrBlank()) {
            val leaderBmp = TemplateImages.read(context, leaderSource)
            if (leaderBmp != null) drawBitmapCover(c, leaderBmp, leaderBox, d.leaderCrop)
        }
        c.restore()
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 3.5f; color = rgb("#F97316")
        }
        c.drawOval(leaderBox, ringPaint)

        // Dark navy bar footer
        val barBox = RectF(215f, 865f, 1050f, 995f)
        paint.color = rgb("#031633"); c.drawRoundRect(barBox, 24f, 24f, paint)
        val companyName = d.branding.company.trim().ifBlank { d.branding.ownerName.trim() }
        if (companyName.isNotBlank()) {
            label(c, companyName.uppercase(), RectF(240f, 878f, 540f, 930f), 24f, Color.WHITE, true, "left", 1, 0.5f)
        }
        val tagline = d.branding.tagline.trim()
        if (tagline.isNotBlank()) {
            label(c, tagline, RectF(240f, 932f, 540f, 965f), 14f, rgb("#93C5FD"), false, "left", 1)
        }
        paint.color = rgb("#1E3A8A"); paint.strokeWidth = 2f
        c.drawLine(550f, 880f, 550f, 975f, paint)

        val phone = d.branding.phone.trim()
        if (phone.isNotBlank()) {
            drawPhoneHandsetIcon(c, RectF(565f, 885f, 615f, 935f), Color.WHITE)
            label(c, phone, RectF(630f, 875f, 800f, 945f), 21f, Color.WHITE, true, "left", 1)
        }
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            drawGlobeIcon(c, RectF(565f, 940f, 615f, 988f), Color.WHITE)
            label(c, website, RectF(630f, 935f, 1040f, 990f), 17f, Color.WHITE, false, "left", 1)
        }

        // 3. User Photo
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBitmap = TemplateImages.read(context, photoSource)
                ?: TemplateImages.read(context, "res:sample_business_woman")
                ?: TemplateImages.read(context, "res:sample_business_man")
            if (photoBitmap != null) {
                val photoBox = RectF(535f, 155f, 1035f, 755f)
                val isCutout = d.backgroundRemoved || hasNativeTransparency(photoBitmap)
                if (isCutout) {
                    glow(c, 780f, 390f, 400f, rgb("#F97316"), 80)
                    drawPortraitEffects(c, photoBitmap, photoBox, d.crop, rgb("#EA580C"))
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = false, fadeBottom = true, fadeTop = false, fadeRight = false, ovalFeather = false)
                } else {
                    drawSoftPortrait(c, photoBitmap, photoBox, d.crop, fadeLeft = true, fadeBottom = true, fadeTop = true, fadeRight = true, ovalFeather = true)
                }
            }
        }

        // 4. Name Box
        val name = d.values[TemplateField.NAME.name].orEmpty()
        if (name.isNotBlank()) {
            val nameBox = RectF(60f, 530f, 590f, 615f)
            paint.color = Color.WHITE; c.drawRoundRect(nameBox, 18f, 18f, paint)
            label(c, name.uppercase(), RectF(80f, 540f, 570f, 605f), 32f, rgb("#05142E"), true, "center", 1, 0.5f)
        }

        // 5. Designation Pill
        val role = d.values[TemplateField.DESIGNATION.name].orEmpty()
        if (role.isNotBlank()) {
            val roleBox = RectF(78f, 605f, 575f, 655f)
            paint.color = rgb("#031633"); c.drawRoundRect(roleBox, 14f, 14f, paint)
            label(c, role.uppercase(), RectF(85f, 608f, 570f, 652f), 19f, Color.WHITE, true, "center", 1, 0.8f)
        }
    }

    private fun blackRedLuxury(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Logos at both top corners
        brandMark(context, c, d, RectF(18f, 16f, 310f, 106f), p, light = false)
        brandMark(context, c, d, RectF(770f, 16f, 1062f, 106f), p, light = false)

        // 3 circular leader headshots top-center
        val circleSize = 88f; val circleY = 12f; val gap = circleSize + 6f; val startX = 540f - gap
        for (i in 0..2) {
            val cx = startX + i * gap
            supportPortrait(context, c, d, RectF(cx, circleY, cx + circleSize, circleY + circleSize), p.accent)
        }

        // Full-width "WELCOME" headline
        goldTitle(c, "WELCOME", RectF(16f, 110f, 1064f, 290f), 148f, "center")
        label(c, "To", RectF(340f, 278f, 740f, 350f), 52f, p.accent, true, "center", 1, 1f)

        // White scroll ribbon with guest name + designation
        ribbon(c, RectF(14f, 350f, 640f, 510f), p.ribbon, p.accent)
        personCopy(c, d, RectF(50f, 364f, 606f, 497f), p.ribbonText, rgb("#5B0000"), "center", showRole = true)

        // "IN OUR GREAT" + "PLATFORM" gold block text
        goldTitle(c, "IN OUR GREAT", RectF(14f, 524f, 660f, 624f), 72f, "left")
        goldTitle(c, "PLATFORM", RectF(14f, 618f, 680f, 760f), 100f, "left")

        // Guest photo RIGHT — tall cutout, no border
        val photoBox = RectF(580f, 100f, 1080f, 900f)
        val photoBitmap = TemplateImages.read(context, d.photo)
            ?: TemplateImages.read(context, "res:sample_business_woman")
            ?: TemplateImages.read(context, "res:sample_business_man")
        if (photoBitmap != null) {
            c.save(); c.clipRect(580f, 80f, 1080f, 920f)
            drawBitmapFaceSafe(c, photoBitmap, photoBox, d.crop); c.restore()
        }

        // Gold/red motivational banner (right side)
        val bannerLeft = 590f; val bannerTop = 740f; val bannerRight = 1060f; val bannerBot = 800f
        val bannerPath = Path().apply {
            val w = 24f
            moveTo(bannerLeft + w, bannerTop); lineTo(bannerRight - w, bannerTop)
            lineTo(bannerRight, (bannerTop + bannerBot) / 2f); lineTo(bannerRight - w, bannerBot)
            lineTo(bannerLeft + w, bannerBot); lineTo(bannerLeft, (bannerTop + bannerBot) / 2f); close()
        }
        paint.color = rgb("#8B0000"); paint.alpha = 235; c.drawPath(bannerPath, paint)
        val motivational = d.values[TemplateField.MESSAGE.name].orEmpty().ifBlank { "POWER TO SUCCEED" }
        label(c, motivational, RectF(620f, bannerTop + 4f, 1040f, bannerBot - 4f), 24f, Color.WHITE, true, "center", 1)

        // Secondary person bottom-LEFT cutout
        val secBitmap = if (d.branding.profilePhoto.isNotBlank() && d.branding.profilePhoto != d.photo)
            TemplateImages.read(context, d.branding.profilePhoto) else null
        if (secBitmap != null) {
            c.save(); c.clipRect(0f, 700f, 290f, 1080f)
            drawBitmapFaceSafe(c, secBitmap, RectF(-30f, 700f, 320f, 1080f), PhotoCrop()); c.restore()
        }

        // Company logo center-bottom (white card)
        businessLogo(context, c, d, RectF(295f, 750f, 535f, 858f), p)

        // Dark footer bar with gold accents
        paint.color = rgb("#0A0000"); paint.alpha = 240; paint.style = Paint.Style.FILL
        c.drawRect(0f, 930f, 1080f, 1080f, paint)
        paint.color = p.accent; paint.alpha = 180
        c.drawRect(0f, 930f, 1080f, 935f, paint)
        val owner = d.branding.ownerName.ifBlank { d.branding.company }
        if (owner.isNotBlank()) {
            label(c, owner.uppercase(), RectF(28f, 940f, 650f, 1000f), 34f, Color.WHITE, true, "left", 1, .8f)
            val ownerRole = listOf(d.branding.ownerDesignation, d.branding.company).filter { it.isNotBlank() }.joinToString(", ")
            if (ownerRole.isNotBlank()) label(c, ownerRole, RectF(28f, 1002f, 640f, 1052f), 18f, p.accent, false, "left", 1)
        }
        val phone = d.branding.phone.ifBlank { d.branding.website }
        if (phone.isNotBlank()) {
            val pill = RectF(665f, 948f, 1052f, 1040f)
            paint.color = rgb("#200000"); paint.alpha = 255; c.drawRoundRect(pill, 46f, 46f, paint)
            paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f; paint.color = p.accent; paint.alpha = 200
            c.drawRoundRect(pill, 46f, 46f, paint); paint.style = Paint.Style.FILL
            label(c, "FOR SUCCESS CALL ON", RectF(690f, 954f, 1042f, 988f), 14f, p.accent, false, "center", 1)
            label(c, phone, RectF(690f, 988f, 1042f, 1040f), 26f, Color.WHITE, true, "center", 1)
        }
    }


    private fun modernEditorial(context: Context, c: Canvas, d: GeneratedPoster, p: Palette) {
        brandMark(context, c, d, RectF(45f, 35f, 420f, 115f), p, light = true)
        supportPortrait(context, c, d, RectF(470f, 28f, 585f, 143f), p.accent)
        goldTitle(c, "WELCOME", RectF(35f, 155f, 590f, 280f), 75f, "center")
        label(c, "TO THE NEXT CHAPTER", RectF(90f, 280f, 545f, 325f), 21f, p.text, true, "center", 1, 3f)
        ribbon(c, RectF(15f, 350f, 610f, 475f), p.ribbon, p.accent)
        personCopy(c, d, RectF(60f, 364f, 565f, 462f), p.ribbonText, p.accent, "center", showRole = true)
        label(c, "GREAT PEOPLE", RectF(45f, 500f, 580f, 575f), 43f, p.text, true, "center", 1, 2f)
        goldTitle(c, "GREAT FUTURE", RectF(30f, 565f, 600f, 670f), 60f, "center")
        welcomeMessage(c, d, RectF(65f, 705f, 545f, 820f), p, "CLEAR IDEAS. STRONG PARTNERSHIP. REAL PROGRESS.")
        portrait(context, c, d, RectF(605f, 145f, 1045f, 815f), "arch", p.accent, 5f)
        businessPanel(context, c, d, RectF(30f, 875f, 1050f, 1050f), p, dark = false, centered = false)
    }

    private fun personCopy(c: Canvas, d: GeneratedPoster, box: RectF, nameColor: Int, roleColor: Int, align: String, showRole: Boolean) {
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank { "YOUR NAME" }
        val role = d.values[TemplateField.DESIGNATION.name].orEmpty()
        val nameBottom = if (showRole && role.isNotBlank()) box.top + box.height() * .58f else box.bottom
        label(c, name.uppercase(), RectF(box.left, box.top, box.right, nameBottom), min(38f, box.height() * .38f), nameColor, true, align, 1, 1f)
        if (showRole && role.isNotBlank()) {
            label(c, role.uppercase(), RectF(box.left, nameBottom, box.right, box.bottom), min(20f, box.height() * .2f), roleColor, true, align, 1, 2f)
        }
    }

    private fun welcomeMessage(c: Canvas, d: GeneratedPoster, box: RectF, p: Palette, fallback: String) {
        val custom = d.values[TemplateField.MESSAGE.name].orEmpty()
        if (custom.isBlank()) return
        label(c, custom, box, 22f, p.muted, false, "left", 3, .4f)
    }

    private fun businessPanel(context: Context, c: Canvas, d: GeneratedPoster, box: RectF, p: Palette, dark: Boolean, centered: Boolean) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val background = if (dark) p.panel else Color.WHITE
        val foreground = if (dark) Color.WHITE else p.text
        paint.color = background; paint.alpha = if (dark) 235 else 248
        c.drawRoundRect(box, 28f, 28f, paint)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f; paint.color = p.accent
        paint.alpha = if (dark) 115 else 85
        c.drawRoundRect(box, 28f, 28f, paint)
        paint.style = Paint.Style.FILL; paint.alpha = 255

        val hPad = 24f
        val vPad = 16f
        val boxH = box.height()
        val usableH = boxH - vPad * 2f

        // ---- LEFT SECTION: Branding photo (circle) OR company logo ----
        val leftSectionWidth = min(108f, boxH - 24f)
        val leftBox = RectF(box.left + hPad, box.top + vPad, box.left + hPad + leftSectionWidth, box.top + vPad + leftSectionWidth)

        // Prefer branding/leader photo if different from main person photo
        val brandingPhotoSrc = d.branding.profilePhoto.trim().takeIf { it.isNotBlank() }
        val brandingPhotoBmp = if (!brandingPhotoSrc.isNullOrBlank()) TemplateImages.read(context, brandingPhotoSrc) else null
        val logo = TemplateImages.read(context, d.branding.logo.trim())
        val company = d.branding.company.trim()

        var leftColumnUsed = false
        if (brandingPhotoBmp != null) {
            // Draw circular branding person photo
            val path = Path().apply { addOval(leftBox, Path.Direction.CW) }
            c.save(); c.clipPath(path)
            c.drawColor(if (dark) Color.rgb(30, 10, 50) else Color.rgb(220, 220, 230))
            drawBitmapCover(c, brandingPhotoBmp, leftBox, PhotoCrop())
            c.restore()
            val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 3f; color = p.accent
            }
            c.drawOval(leftBox, ringPaint)
            leftColumnUsed = true
        } else if (logo != null) {
            // Draw company logo contained within box
            drawBitmapContained(c, logo, leftBox)
            leftColumnUsed = true
        } else if (company.isNotBlank()) {
            // Fallback: letter monogram tile
            paint.color = p.accent; paint.alpha = 35
            c.drawRoundRect(leftBox, 18f, 18f, paint)
            paint.alpha = 255
            label(c, company.take(1).uppercase(), leftBox, 36f, p.accent, true, "center", 1)
            leftColumnUsed = true
        }

        // ---- RIGHT SECTION: Company info text ----
        val align = if (centered) "center" else "left"
        val textLeft = if (leftColumnUsed && !centered) leftBox.right + 18f else box.left + hPad
        val textRight = box.right - hPad

        // Determine how many rows we have and their heights
        val phone = d.branding.phone.trim()
        val website = d.branding.website.trim()   // NEVER show placeholder - only real data
        val tagline = d.branding.tagline.trim()

        // Row heights: company(top) + tagline (optional) + phone + website
        // Distribute available vertical space
        val rows = buildList {
            if (company.isNotBlank()) add("company" to company.uppercase())
            if (tagline.isNotBlank()) add("tagline" to tagline)
            if (phone.isNotBlank()) add("phone" to phone)
            if (website.isNotBlank()) add("website" to website)
        }

        if (rows.isEmpty()) return  // nothing to show

        val rowH = (usableH / rows.size.coerceAtLeast(1)).coerceAtMost(46f)
        var rowTop = box.top + vPad + (usableH - rowH * rows.size) / 2f

        rows.forEach { (role, value) ->
            val rowBox = RectF(textLeft, rowTop, textRight, rowTop + rowH)
            when (role) {
                "company" -> label(c, value, rowBox, min(28f, rowH * 0.62f), foreground, true, align, 1, 1f)
                "tagline" -> label(c, value, rowBox, min(16f, rowH * 0.56f), p.accent, false, align, 1)
                "phone"   -> {
                    // Draw a small phone icon dot before the number
                    drawPhoneHandsetIcon(c, RectF(textLeft, rowTop + rowH * 0.2f, textLeft + rowH * 0.55f, rowTop + rowH * 0.8f), p.accent)
                    val numLeft = textLeft + rowH * 0.62f + 6f
                    label(c, value, RectF(numLeft, rowTop, textRight, rowTop + rowH), min(20f, rowH * 0.56f), foreground, true, align, 1)
                }
                "website" -> {
                    drawGlobeIcon(c, RectF(textLeft, rowTop + rowH * 0.2f, textLeft + rowH * 0.55f, rowTop + rowH * 0.8f), p.accent)
                    val urlLeft = textLeft + rowH * 0.62f + 6f
                    label(c, value, RectF(urlLeft, rowTop, textRight, rowTop + rowH), min(17f, rowH * 0.5f), p.accent, false, align, 1)
                }
            }
            rowTop += rowH
        }
    }

    private fun brandMark(context: Context, c: Canvas, d: GeneratedPoster, box: RectF, p: Palette, light: Boolean, centered: Boolean = false) {
        val logo = TemplateImages.read(context, d.branding.logo)
        val color = if (light) p.text else Color.WHITE
        var left = box.left
        if (logo != null) {
            val side = box.height()
            drawBitmapContained(c, logo, RectF(left, box.top, left + side, box.bottom))
            left += side + 15f
        }
        val company = d.branding.company.trim()
        if (company.isNotBlank()) label(c, company.uppercase(), RectF(left, box.top, box.right, box.bottom), 20f, color, true, if (centered) "center" else "left", 2, 1f)
    }

    private fun businessLogo(context: Context, c: Canvas, d: GeneratedPoster, box: RectF, p: Palette) {
        val logo = TemplateImages.read(context, d.branding.logo)
        if (logo != null) {
            val plate = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; alpha = 245 }
            c.drawRoundRect(box, 18f, 18f, plate)
            drawBitmapContained(c, logo, RectF(box.left + 8f, box.top + 8f, box.right - 8f, box.bottom - 8f))
        } else if (d.branding.company.isNotBlank()) {
            c.drawRoundRect(box, 18f, 18f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; alpha = 245 })
            label(c, d.branding.company.uppercase(), RectF(box.left + 10f, box.top + 8f, box.right - 10f, box.bottom - 8f), 18f, p.ribbonText, true, "center", 2, .5f)
        }
    }

    private fun portrait(context: Context, c: Canvas, d: GeneratedPoster, box: RectF, shape: String, border: Int, borderWidth: Float) {
        val path = shapePath(box, shape)
        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(box.left, box.top, box.right, box.bottom, rgb("#D7DCE4"), rgb("#8E9BAB"), Shader.TileMode.CLAMP)
        }
        c.save(); c.clipPath(path); c.drawPath(path, fill)
        val bitmap = TemplateImages.read(context, d.photo)
            ?: TemplateImages.read(context, "res:sample_business_woman")
            ?: TemplateImages.read(context, "res:sample_business_man")
        if (bitmap != null) drawBitmapFaceSafe(c, bitmap, box, d.crop)
        c.restore()
        if (borderWidth > 0f) c.drawPath(path, Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = borderWidth; color = border })
    }

    private fun supportPortrait(context: Context, c: Canvas, d: GeneratedPoster, box: RectF, border: Int) {
        val source = d.branding.profilePhoto
        if (source.isBlank() || source == d.photo) return
        val bitmap = TemplateImages.read(context, source) ?: return
        val path = Path().apply { addOval(box, Path.Direction.CW) }
        c.save(); c.clipPath(path); c.drawColor(rgb("#DADFE8")); drawBitmapFaceSafe(c, bitmap, box, PhotoCrop()); c.restore()
        c.drawPath(path, Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 5f; color = border })
    }

    private fun supportPortraits(context: Context, c: Canvas, d: GeneratedPoster, boxes: List<RectF>, border: Int) {
        val source = d.branding.profilePhoto
        if (source.isBlank() || source == d.photo) return
        boxes.take(1).forEach { supportPortrait(context, c, d, it, border) }
    }

    private fun premiumBrandMark(context: Context, c: Canvas, d: GeneratedPoster, box: RectF, p: Palette) {
        val logo = TemplateImages.read(context, d.branding.logo)
        val company = d.branding.company.trim()
        if (logo == null && company.isBlank()) return

        val plate = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                box.left, box.top, box.right, box.bottom,
                intArrayOf(Color.argb(205, 15, 0, 28), Color.argb(135, 91, 8, 94)),
                null, Shader.TileMode.CLAMP
            )
            setShadowLayer(14f, 0f, 5f, Color.argb(150, 0, 0, 0))
        }
        c.drawRoundRect(box, 18f, 18f, plate)
        plate.clearShadowLayer()
        plate.shader = null
        plate.style = Paint.Style.STROKE
        plate.strokeWidth = 2f
        plate.color = p.accent
        plate.alpha = 165
        c.drawRoundRect(box, 18f, 18f, plate)

        if (logo != null) {
            val logoBox = if (company.isBlank()) {
                RectF(box.left + 12f, box.top + 8f, box.right - 12f, box.bottom - 8f)
            } else {
                RectF(box.left + 10f, box.top + 8f, box.left + box.height() - 2f, box.bottom - 8f)
            }
            drawBitmapContained(c, logo, logoBox)
            if (company.isNotBlank()) {
                label(c, company.uppercase(), RectF(logoBox.right + 10f, box.top + 8f, box.right - 10f, box.bottom - 8f), 17f, Color.WHITE, true, "left", 2, .7f)
            }
        } else {
            label(c, company.uppercase(), RectF(box.left + 14f, box.top + 8f, box.right - 14f, box.bottom - 8f), 18f, Color.WHITE, true, "center", 2, .8f)
        }
    }

    private fun premiumLightStreaks(c: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val streaks = listOf(
            floatArrayOf(-90f, 740f, 640f, 360f, 32f),
            floatArrayOf(270f, 930f, 1160f, 465f, 22f),
            floatArrayOf(-120f, 470f, 520f, 135f, 14f)
        )
        streaks.forEachIndexed { index, s ->
            paint.shader = LinearGradient(
                s[0], s[1], s[2], s[3],
                intArrayOf(Color.TRANSPARENT, Color.argb(if (index == 1) 34 else 24, 255, 210, 255), Color.TRANSPARENT),
                floatArrayOf(0f, .52f, 1f), Shader.TileMode.CLAMP
            )
            val half = s[4] / 2f
            c.drawPath(Path().apply {
                moveTo(s[0], s[1] - half)
                lineTo(s[2], s[3] - half)
                lineTo(s[2], s[3] + half)
                lineTo(s[0], s[1] + half)
                close()
            }, paint)
        }
        paint.shader = null
    }

    private fun premiumBokeh(c: Canvas, gold: Int) {
        val lights = listOf(
            floatArrayOf(78f, 92f, 15f, 26f), floatArrayOf(305f, 62f, 7f, 34f),
            floatArrayOf(1010f, 400f, 18f, 32f), floatArrayOf(585f, 636f, 11f, 48f),
            floatArrayOf(230f, 722f, 6f, 55f), floatArrayOf(965f, 833f, 9f, 42f),
            floatArrayOf(420f, 858f, 6f, 44f), floatArrayOf(850f, 116f, 5f, 48f)
        )
        lights.forEach { light ->
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = gold
                alpha = light[3].toInt()
                maskFilter = BlurMaskFilter(light[2] * .72f, BlurMaskFilter.Blur.NORMAL)
            }
            c.drawCircle(light[0], light[1], light[2], paint)
            paint.maskFilter = null
            paint.alpha = (light[3] * 1.45f).toInt().coerceAtMost(90)
            c.drawCircle(light[0], light[1], max(1.5f, light[2] * .22f), paint)
        }
    }

    private fun premiumNameRibbon(c: Canvas, box: RectF, gold: Int) {
        val center = RectF(box.left + 38f, box.top + 9f, box.right - 36f, box.bottom - 10f)
        val shadow = Path().apply {
            moveTo(center.left, center.top + 9f)
            cubicTo(center.width() * .24f + center.left, center.top - 2f, center.width() * .72f + center.left, center.top + 3f, center.right, center.top + 10f)
            lineTo(center.right, center.bottom - 4f)
            cubicTo(center.width() * .70f + center.left, center.bottom + 10f, center.width() * .28f + center.left, center.bottom + 7f, center.left, center.bottom - 3f)
            close()
        }
        c.save()
        c.translate(0f, 10f)
        c.drawPath(shadow, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            alpha = 105
            maskFilter = BlurMaskFilter(11f, BlurMaskFilter.Blur.NORMAL)
        })
        c.restore()

        val foldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(box.left, box.top, box.right, box.bottom,
                intArrayOf(rgb("#4B2805"), rgb("#9E6815"), rgb("#E2B84D")), null, Shader.TileMode.CLAMP)
        }
        c.drawPath(Path().apply {
            moveTo(center.left + 48f, center.top + 18f); lineTo(box.left - 5f, box.top + 27f)
            lineTo(box.left + 28f, box.centerY()); lineTo(box.left - 7f, box.bottom - 20f)
            lineTo(center.left + 52f, center.bottom - 8f); close()
        }, foldPaint)
        c.drawPath(Path().apply {
            moveTo(center.right - 48f, center.top + 18f); lineTo(box.right + 5f, box.top + 27f)
            lineTo(box.right - 28f, box.centerY()); lineTo(box.right + 7f, box.bottom - 20f)
            lineTo(center.right - 52f, center.bottom - 8f); close()
        }, Paint(foldPaint).apply {
            shader = LinearGradient(box.right, box.top, center.right - 50f, box.bottom,
                intArrayOf(rgb("#3C1C02"), rgb("#7A4509"), rgb("#C28D28")), null, Shader.TileMode.CLAMP)
        })

        val crease = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#3A1903"); alpha = 190 }
        c.drawPath(Path().apply {
            moveTo(center.left, center.top + 18f); lineTo(center.left + 48f, center.top + 18f)
            lineTo(center.left, center.bottom - 8f); close()
        }, crease)
        c.drawPath(Path().apply {
            moveTo(center.right, center.top + 18f); lineTo(center.right - 48f, center.top + 18f)
            lineTo(center.right, center.bottom - 8f); close()
        }, crease)

        val body = Path().apply {
            moveTo(center.left, center.top + 10f)
            cubicTo(center.left + center.width() * .24f, center.top - 2f, center.left + center.width() * .72f, center.top + 2f, center.right, center.top + 10f)
            lineTo(center.right, center.bottom - 7f)
            cubicTo(center.left + center.width() * .72f, center.bottom + 8f, center.left + center.width() * .28f, center.bottom + 7f, center.left, center.bottom - 7f)
            close()
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, center.top, 0f, center.bottom,
                intArrayOf(rgb("#FFFDF5"), rgb("#F7EEDC"), rgb("#D8CEC3"), rgb("#FFFFFF"), rgb("#D8C9B5")),
                floatArrayOf(0f, .20f, .43f, .68f, 1f), Shader.TileMode.CLAMP
            )
        }
        c.drawPath(body, bodyPaint)
        bodyPaint.shader = null
        bodyPaint.style = Paint.Style.STROKE
        bodyPaint.strokeWidth = 4f
        bodyPaint.color = gold
        c.drawPath(body, bodyPaint)

        bodyPaint.strokeWidth = 1.5f
        bodyPaint.color = Color.WHITE
        bodyPaint.alpha = 190
        c.drawPath(Path().apply {
            moveTo(center.left + 18f, center.top + 14f)
            cubicTo(center.left + center.width() * .32f, center.top + 4f, center.left + center.width() * .70f, center.top + 6f, center.right - 18f, center.top + 15f)
        }, bodyPaint)
        bodyPaint.style = Paint.Style.STROKE
        bodyPaint.strokeWidth = 2f
        bodyPaint.color = rgb("#9B6B18")
        bodyPaint.alpha = 100
        c.drawPath(Path().apply {
            moveTo(center.left + 22f, center.bottom - 13f)
            cubicTo(center.left + center.width() * .30f, center.bottom + 1f, center.left + center.width() * .70f, center.bottom + 1f, center.right - 22f, center.bottom - 13f)
        }, bodyPaint)
    }

    private fun premiumQuoteRibbon(c: Canvas, box: RectF) {
        val body = RectF(box.left + 44f, box.top, box.right - 40f, box.bottom)
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            alpha = 170
            maskFilter = BlurMaskFilter(16f, BlurMaskFilter.Blur.NORMAL)
        }
        c.drawRoundRect(RectF(body.left + 3f, body.top + 14f, body.right + 5f, body.bottom + 16f), 12f, 12f, shadowPaint)

        val foldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(box.left, box.top, box.right, box.bottom,
                intArrayOf(rgb("#2B0105"), rgb("#65050C"), rgb("#980916")), null, Shader.TileMode.CLAMP)
        }
        c.drawPath(Path().apply {
            moveTo(body.left + 22f, body.top + 13f); lineTo(box.left, body.top + 34f)
            lineTo(box.left + 30f, body.bottom + 30f); lineTo(body.left + 58f, body.bottom - 2f); close()
        }, foldPaint)
        c.drawPath(Path().apply {
            moveTo(body.right - 22f, body.top + 13f); lineTo(box.right, body.top + 34f)
            lineTo(box.right - 28f, body.bottom + 30f); lineTo(body.right - 58f, body.bottom - 2f); close()
        }, foldPaint)

        val bodyPath = Path().apply {
            moveTo(body.left + 12f, body.top)
            cubicTo(body.left + body.width() * .30f, body.top - 3f, body.left + body.width() * .70f, body.top - 3f, body.right - 12f, body.top)
            lineTo(body.right, body.centerY())
            lineTo(body.right - 12f, body.bottom)
            cubicTo(body.left + body.width() * .70f, body.bottom + 3f, body.left + body.width() * .30f, body.bottom + 3f, body.left + 12f, body.bottom)
            lineTo(body.left, body.centerY()); close()
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, body.top, 0f, body.bottom,
                intArrayOf(rgb("#F52A38"), rgb("#C60717"), rgb("#89030D"), rgb("#B20816")),
                floatArrayOf(0f, .34f, .74f, 1f), Shader.TileMode.CLAMP
            )
        }
        c.drawPath(bodyPath, paint)
        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = rgb("#FF6A73")
        paint.alpha = 160
        c.drawPath(bodyPath, paint)
        paint.strokeWidth = 1.5f
        paint.color = Color.WHITE
        paint.alpha = 145
        c.drawLine(body.left + 28f, body.top + 10f, body.right - 28f, body.top + 10f, paint)
        paint.color = rgb("#4C0006"); paint.alpha = 145; paint.strokeWidth = 2f
        c.drawLine(body.left + 30f, body.bottom - 8f, body.right - 30f, body.bottom - 8f, paint)
    }

    private fun premiumMessage(c: Canvas, value: String, box: RectF) {
        label(c, value, RectF(box.left + 2f, box.top + 4f, box.right + 2f, box.bottom + 4f), 20f, Color.argb(210, 15, 0, 24), true, "center", 3, .3f)
        label(c, value, box, 20f, Color.WHITE, true, "center", 3, .3f)
        val accent = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(box.left, 0f, box.right, 0f, Color.TRANSPARENT, rgb("#F6C957"), Shader.TileMode.MIRROR)
            alpha = 105
            strokeWidth = 2f
        }
        c.drawLine(box.left + 82f, box.bottom - 3f, box.right - 82f, box.bottom - 3f, accent)
    }

    private fun premiumGoldTitle(c: Canvas, value: String, box: RectF, size: Float, align: String, emphasis: Float = 1f) {
        val dropShadow = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            alpha = (185 * emphasis).toInt().coerceAtMost(225)
            textSize = size
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            letterSpacing = .008f
            maskFilter = BlurMaskFilter(13f * emphasis, BlurMaskFilter.Blur.NORMAL)
        }
        drawTextLayout(c, value, RectF(box.left + 12f * emphasis, box.top + 16f * emphasis, box.right + 12f * emphasis, box.bottom + 16f * emphasis), dropShadow, align, 1)

        val extrusion = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = rgb(if (emphasis > 1.15f) "#351400" else "#482000")
            textSize = size
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            letterSpacing = .008f
        }
        val extrusionPasses = (10f * emphasis).toInt().coerceIn(8, 14)
        for (pass in extrusionPasses downTo 1) {
            val offset = pass * 1.65f
            drawTextLayout(c, value, RectF(box.left + offset, box.top + offset * 1.24f, box.right + offset, box.bottom + offset * 1.24f), extrusion, align, 1)
        }

        val outerGlow = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = rgb("#F3A713")
            alpha = (82 * emphasis).toInt().coerceAtMost(125)
            textSize = size
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            style = Paint.Style.STROKE
            strokeWidth = 8f * emphasis
            maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
        }
        drawTextLayout(c, value, box, outerGlow, align, 1)

        val outline = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = rgb("#4A2600")
            textSize = size
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            letterSpacing = .008f
            style = Paint.Style.STROKE
            strokeWidth = 5.5f * emphasis
        }
        drawTextLayout(c, value, box, outline, align, 1)

        val face = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            letterSpacing = .008f
            shader = LinearGradient(
                0f, box.top, 0f, box.bottom,
                intArrayOf(rgb("#FFF2A6"), rgb("#FFD34A"), rgb("#C77908"), rgb("#713200"), rgb("#E9A916"), rgb("#FFE277"), rgb("#984900")),
                floatArrayOf(0f, .18f, .38f, .56f, .72f, .88f, 1f), Shader.TileMode.CLAMP
            )
        }
        drawTextLayout(c, value, box, face, align, 1)

        val lowerBevel = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = rgb("#5E2700")
            alpha = (150 * emphasis).toInt().coerceAtMost(205)
            textSize = size
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            letterSpacing = .008f
            style = Paint.Style.STROKE
            strokeWidth = 2.4f * emphasis
        }
        c.save()
        c.clipRect(box.left, box.top + box.height() * .54f, box.right + 25f, box.bottom + 25f)
        drawTextLayout(c, value, RectF(box.left + 1.2f, box.top + 1.8f, box.right + 1.2f, box.bottom + 1.8f), lowerBevel, align, 1)
        c.restore()

        val highlight = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = (175 * emphasis).toInt().coerceAtMost(220)
            textSize = size
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            letterSpacing = .008f
            style = Paint.Style.STROKE
            strokeWidth = 1.35f * emphasis
        }
        c.save()
        c.clipRect(box.left, box.top, box.right + 20f, box.top + box.height() * .44f)
        drawTextLayout(c, value, box, highlight, align, 1)
        c.restore()
    }

    private fun hasNativeTransparency(bitmap: Bitmap): Boolean {
        val stepX = max(1, bitmap.width / 48)
        val stepY = max(1, bitmap.height / 48)
        for (y in 0 until bitmap.height step stepY) {
            for (x in 0 until bitmap.width step stepX) {
                if (Color.alpha(bitmap.getPixel(x, y)) < 245) return true
            }
        }
        return false
    }

    private fun drawPortraitEffects(
        c: Canvas,
        bitmap: Bitmap,
        box: RectF,
        crop: PhotoCrop,
        glowColor: Int,
        compact: Boolean = false
    ) {
        fun silhouette(color: Int, alpha: Int, blur: Float, dx: Float, dy: Float) {
            val target = RectF(box.left + dx, box.top + dy, box.right + dx, box.bottom + dy)
            val outset = blur * 2.2f
            val layerBounds = RectF(target.left - outset, target.top - outset, target.right + outset, target.bottom + outset)
            val layer = c.saveLayer(layerBounds, null)
            val effect = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                this.alpha = alpha
                colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                maskFilter = BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL)
            }
            drawBitmapCover(c, bitmap, target, crop, effect)
            c.restoreToCount(layer)
        }
        silhouette(glowColor, if (compact) 78 else 105, if (compact) 17f else 25f, 0f, 0f)
        silhouette(Color.BLACK, if (compact) 105 else 135, if (compact) 13f else 21f, if (compact) 7f else 12f, if (compact) 10f else 17f)
    }

    private fun leaderMedallion(
        context: Context,
        c: Canvas,
        source: String,
        fallback: String,
        box: RectF,
        border: Int
    ) {
        val bitmap = source.takeIf { it.isNotBlank() }?.let { TemplateImages.read(context, it) }
            ?: TemplateImages.read(context, fallback)
            ?: return
        val outer = RectF(box.left - 5f, box.top - 5f, box.right + 5f, box.bottom + 5f)
        c.drawOval(outer, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                outer.left, outer.top, outer.right, outer.bottom,
                intArrayOf(rgb("#FFF4A6"), border, rgb("#8A5100"), rgb("#FFE98A")),
                null, Shader.TileMode.CLAMP
            )
            setShadowLayer(10f, 0f, 4f, Color.BLACK)
        })
        val path = Path().apply { addOval(box, Path.Direction.CW) }
        c.save()
        c.clipPath(path)
        c.drawColor(rgb("#2B0B3D"))
        drawBitmapCover(c, bitmap, box, PhotoCrop())
        c.restore()
        c.drawOval(box, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = rgb("#FFF5B5")
        })
    }

    private fun drawTopLeaderPhotos(
        context: Context,
        c: Canvas,
        d: GeneratedPoster,
        goldColor: Int
    ) {
        val sources = d.branding.profilePhoto
            .split(',', ';', '|')
            .map { it.trim() }
            .filter { it.isNotBlank() && it != d.photo }
            .take(3)
        val bitmaps = sources.mapNotNull { TemplateImages.read(context, it) }
        if (bitmaps.isEmpty()) return

        val count = bitmaps.size
        val size = 78f
        val top = 16f
        val gap = 12f
        val totalWidth = count * size + (count - 1) * gap
        val startX = 540f - totalWidth / 2f

        bitmaps.forEachIndexed { i, bmp ->
            val left = startX + i * (size + gap)
            val box = RectF(left, top, left + size, top + size)
            val cx = box.centerX()
            val cy = box.centerY()
            val radius = size / 2f

            // 1. Subtle gold outer glow
            val glowRadius = radius + 10f
            val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    cx, cy, glowRadius,
                    intArrayOf(Color.argb(110, 246, 201, 87), Color.argb(40, 246, 201, 87), Color.TRANSPARENT),
                    floatArrayOf(0f, 0.65f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            c.drawCircle(cx, cy, glowRadius, glowPaint)

            // 2. Drop shadow under outer rim
            val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(120, 0, 0, 0)
                maskFilter = BlurMaskFilter(6f, BlurMaskFilter.Blur.NORMAL)
            }
            c.drawCircle(cx, cy + 3f, radius + 2f, shadowPaint)

            // 3. Circular photo crop
            val clipPath = Path().apply { addOval(box, Path.Direction.CW) }
            c.save()
            c.clipPath(clipPath)
            c.drawColor(rgb("#2B0B3D"))
            drawBitmapCover(c, bmp, box, PhotoCrop())
            c.restore()

            // 4. White inner ring
            val whiteRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2.5f
                color = Color.WHITE
            }
            c.drawCircle(cx, cy, radius - 1.5f, whiteRingPaint)

            // 5. Metallic gold border (outer rim)
            val goldBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4f
                shader = LinearGradient(
                    box.left, box.top, box.right, box.bottom,
                    intArrayOf(rgb("#FFF8B8"), goldColor, rgb("#8A5100"), rgb("#FFE98A")),
                    null, Shader.TileMode.CLAMP
                )
            }
            c.drawCircle(cx, cy, radius + 1f, goldBorderPaint)
        }
    }

    private fun drawPhoneHandsetIcon(c: Canvas, box: RectF, color: Int) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
        }
        val cx = box.centerX()
        val cy = box.centerY()
        val size = min(box.width(), box.height())
        val path = Path()
        c.save()
        c.translate(cx, cy)
        val scale = size / 24f
        c.scale(scale, scale)
        c.translate(-12f, -12f)
        path.moveTo(6.62f, 10.79f)
        path.cubicTo(8.06f, 13.62f, 10.38f, 15.93f, 13.21f, 17.38f)
        path.lineTo(15.41f, 15.18f)
        path.cubicTo(15.68f, 14.91f, 16.08f, 14.82f, 16.43f, 14.94f)
        path.cubicTo(17.55f, 15.31f, 18.76f, 15.51f, 20f, 15.51f)
        path.cubicTo(20.55f, 15.51f, 21f, 15.96f, 21f, 16.51f)
        path.lineTo(21f, 20f)
        path.cubicTo(21f, 20.55f, 20.55f, 21f, 20f, 21f)
        path.cubicTo(10.61f, 21f, 3f, 13.39f, 3f, 4f)
        path.cubicTo(3f, 3.45f, 3.45f, 3f, 4f, 3f)
        path.lineTo(7.5f, 3f)
        path.cubicTo(8.05f, 3f, 8.5f, 3.45f, 8.5f, 4f)
        path.cubicTo(8.5f, 5.25f, 8.7f, 6.45f, 9.07f, 7.57f)
        path.cubicTo(9.18f, 7.92f, 9.1f, 8.31f, 8.82f, 8.59f)
        path.lineTo(6.62f, 10.79f)
        path.close()
        c.drawPath(path, p)
        c.restore()
    }

    /** Draws a simple globe/language icon (Material Design style) */
    private fun drawGlobeIcon(c: Canvas, box: RectF, color: Int) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 1.8f
        }
        val cx = box.centerX()
        val cy = box.centerY()
        val r = min(box.width(), box.height()) / 2f
        // Outer circle
        c.drawCircle(cx, cy, r, p)
        // Horizontal line
        c.drawLine(cx - r, cy, cx + r, cy, p)
        // Vertical ellipse (meridian)
        c.drawOval(RectF(cx - r * 0.45f, cy - r, cx + r * 0.45f, cy + r), p)
    }

    /**
     * Keeps the original image ratio while allowing a supplied transparent PNG to read as a true
     * cutout. Opaque photos receive four-sided and elliptical feathering so no hard photo card
     * boundary remains visible.
     */
    private fun drawSoftPortrait(
        c: Canvas,
        bitmap: Bitmap,
        box: RectF,
        crop: PhotoCrop,
        fadeLeft: Boolean,
        fadeBottom: Boolean,
        fadeTop: Boolean = false,
        fadeRight: Boolean = false,
        ovalFeather: Boolean = false
    ) {
        val layer = c.saveLayer(box, null)
        drawBitmapCover(c, bitmap, box, crop)
        val mask = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }
        if (fadeLeft || fadeRight) {
            val horizontalColors = when {
                fadeLeft && fadeRight -> intArrayOf(Color.TRANSPARENT, Color.argb(218, 255, 255, 255), Color.WHITE, Color.argb(218, 255, 255, 255), Color.TRANSPARENT)
                fadeLeft -> intArrayOf(Color.TRANSPARENT, Color.argb(225, 255, 255, 255), Color.WHITE, Color.WHITE, Color.WHITE)
                else -> intArrayOf(Color.WHITE, Color.WHITE, Color.WHITE, Color.argb(225, 255, 255, 255), Color.TRANSPARENT)
            }
            mask.shader = LinearGradient(
                box.left, 0f, box.right, 0f,
                horizontalColors,
                floatArrayOf(0f, .18f, .50f, .82f, 1f), Shader.TileMode.CLAMP
            )
            c.drawRect(box, mask)
        }
        if (fadeTop || fadeBottom) {
            val verticalColors = when {
                fadeTop && fadeBottom -> intArrayOf(Color.TRANSPARENT, Color.argb(225, 255, 255, 255), Color.WHITE, Color.argb(225, 255, 255, 255), Color.TRANSPARENT)
                fadeTop -> intArrayOf(Color.TRANSPARENT, Color.argb(230, 255, 255, 255), Color.WHITE, Color.WHITE, Color.WHITE)
                else -> intArrayOf(Color.WHITE, Color.WHITE, Color.WHITE, Color.argb(225, 255, 255, 255), Color.TRANSPARENT)
            }
            mask.shader = LinearGradient(
                0f, box.top, 0f, box.bottom,
                verticalColors,
                floatArrayOf(0f, .12f, .52f, .86f, 1f), Shader.TileMode.CLAMP
            )
            c.drawRect(box, mask)
        }
        if (ovalFeather) {
            val verticalScale = box.height() / box.width()
            val radius = box.width() * .57f
            c.save()
            c.scale(1f, verticalScale, box.centerX(), box.centerY())
            mask.shader = RadialGradient(
                box.centerX(), box.centerY(), radius,
                intArrayOf(Color.WHITE, Color.WHITE, Color.argb(220, 255, 255, 255), Color.argb(105, 255, 255, 255), Color.TRANSPARENT),
                floatArrayOf(0f, .48f, .70f, .86f, 1f), Shader.TileMode.CLAMP
            )
            val halfHeight = box.width() / 2f
            c.drawRect(box.left - 20f, box.centerY() - halfHeight - 20f, box.right + 20f, box.centerY() + halfHeight + 20f, mask)
            c.restore()
        }
        mask.xfermode = null
        mask.shader = null
        c.restoreToCount(layer)
    }

    fun drawBitmapCover(
        c: Canvas,
        bitmap: Bitmap,
        box: RectF,
        crop: PhotoCrop,
        paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    ) {
        val baseScale = max(box.width() / bitmap.width, box.height() / bitmap.height)
        val scale = baseScale * crop.scale.coerceIn(0.5f, 5f)
        val width = bitmap.width * scale
        val height = bitmap.height * scale
        // Direct responsive panning relative to box dimensions
        val panRangeX = max(box.width() * 0.75f, (width - box.width()) / 2f + box.width() * 0.35f)
        val panRangeY = max(box.height() * 0.75f, (height - box.height()) / 2f + box.height() * 0.35f)
        val dx = crop.panX * panRangeX
        val dy = crop.panY * panRangeY
        val target = RectF(
            box.centerX() - width / 2f + dx,
            box.centerY() - height / 2f + dy,
            box.centerX() + width / 2f + dx,
            box.centerY() + height / 2f + dy
        )
        c.drawBitmap(bitmap, null, target, paint)
    }

    /** Fit rather than crop: a user's face and head can never be clipped by a template frame. */
    private fun drawBitmapFaceSafe(c: Canvas, bitmap: Bitmap, box: RectF, crop: PhotoCrop) {
        val scale = min(box.width() / bitmap.width, box.height() / bitmap.height) * crop.scale.coerceIn(1f, 1.15f)
        val width = bitmap.width * scale
        val height = bitmap.height * scale
        val maxDx = max(0f, (box.width() - width) / 2f)
        val maxDy = max(0f, (box.height() - height) / 2f)
        val dx = crop.panX.coerceIn(-1f, 1f) * maxDx
        val dy = crop.panY.coerceIn(-1f, 1f) * maxDy
        val target = RectF(box.centerX() - width / 2f + dx, box.centerY() - height / 2f + dy, box.centerX() + width / 2f + dx, box.centerY() + height / 2f + dy)
        c.drawBitmap(bitmap, null, target, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
    }

    fun drawBitmapContained(c: Canvas, bitmap: Bitmap, box: RectF, crop: PhotoCrop = PhotoCrop()) {
        val baseScale = min(box.width() / bitmap.width, box.height() / bitmap.height)
        val scale = baseScale * crop.scale.coerceIn(0.5f, 5f)
        val width = bitmap.width * scale
        val height = bitmap.height * scale
        val dx = crop.panX * box.width() * 0.5f
        val dy = crop.panY * box.height() * 0.5f
        c.drawBitmap(bitmap, null, RectF(box.centerX() - width / 2f + dx, box.centerY() - height / 2f + dy, box.centerX() + width / 2f + dx, box.centerY() + height / 2f + dy), Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
    }

    private fun shapePath(box: RectF, shape: String): Path = Path().apply {
        when (shape) {
            "circle", "oval" -> addOval(box, Path.Direction.CW)
            "hex" -> {
                val cut = box.width() * .12f
                moveTo(box.left + cut, box.top); lineTo(box.right - cut, box.top); lineTo(box.right, box.centerY())
                lineTo(box.right - cut, box.bottom); lineTo(box.left + cut, box.bottom); lineTo(box.left, box.centerY()); close()
            }
            "arch" -> {
                moveTo(box.left, box.bottom); lineTo(box.left, box.top + box.width() / 2f)
                arcTo(RectF(box.left, box.top, box.right, box.top + box.width()), 180f, 180f, false)
                lineTo(box.right, box.bottom); close()
            }
            else -> addRoundRect(box, 36f, 36f, Path.Direction.CW)
        }
    }

    private fun ribbon(c: Canvas, box: RectF, fill: Int, border: Int) {
        val path = Path().apply {
            val wing = min(55f, box.width() * .08f)
            val notch = box.height() * .28f
            moveTo(box.left + wing, box.top); lineTo(box.right - wing, box.top); lineTo(box.right, box.top + notch)
            lineTo(box.right - wing * .55f, box.centerY()); lineTo(box.right, box.bottom - notch); lineTo(box.right - wing, box.bottom)
            lineTo(box.left + wing, box.bottom); lineTo(box.left, box.bottom - notch); lineTo(box.left + wing * .55f, box.centerY())
            lineTo(box.left, box.top + notch); close()
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        c.save(); c.translate(4f, 7f); paint.color = Color.BLACK; paint.alpha = 75; c.drawPath(path, paint); c.restore()
        paint.color = fill; paint.alpha = 255; c.drawPath(path, paint)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 4f; paint.color = border; c.drawPath(path, paint)
    }

    private fun goldTitle(c: Canvas, value: String, box: RectF, size: Float, align: String, amber: Boolean = false) {
        val shadow = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb(if (amber) "#542000" else "#4A2E04"); textSize = size; typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD); letterSpacing = .01f }
        for (pass in 6 downTo 1) drawTextLayout(c, value, RectF(box.left + pass * 1.8f, box.top + pass * 2.1f, box.right + pass * 1.8f, box.bottom + pass * 2.1f), shadow, align, 1)
        val outline = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = rgb("#5B3300")
            textSize = size
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            letterSpacing = .01f
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        drawTextLayout(c, value, box, outline, align, 1)
        val gold = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size; typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD); letterSpacing = .01f
            shader = LinearGradient(0f, box.top, 0f, box.bottom, intArrayOf(rgb("#FFF7CC"), rgb("#FFD55E"), rgb(if (amber) "#E67813" else "#C98A15"), rgb("#FFF0A1"), rgb("#8B5708")), null, Shader.TileMode.CLAMP)
        }
        drawTextLayout(c, value, box, gold, align, 1)
        val highlight = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = rgb("#FFF8D0")
            alpha = 185
            textSize = size
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            letterSpacing = .01f
            style = Paint.Style.STROKE
            strokeWidth = 1.4f
        }
        drawTextLayout(c, value, box, highlight, align, 1)
    }

    private fun label(c: Canvas, value: String, box: RectF, requestedSize: Float, color: Int, bold: Boolean, align: String, maxLines: Int, letterSpacing: Float = 0f) {
        if (value.isBlank() || box.width() <= 0 || box.height() <= 0) return
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            textSize = requestedSize
            typeface = Typeface.create("sans-serif", if (bold) Typeface.BOLD else Typeface.NORMAL)
            this.letterSpacing = letterSpacing / 100f
        }
        var size = requestedSize
        var layout = makeLayout(value, box.width().toInt(), paint, align)
        while ((layout.height > box.height() || layout.lineCount > maxLines || (maxLines == 1 && paint.measureText(value) > box.width()) || value.split(Regex("\\s+")).any { paint.measureText(it) > box.width() }) && size > 10f) {
            size -= 1f; paint.textSize = size; layout = makeLayout(value, box.width().toInt(), paint, align)
        }
        val y = box.top + max(0f, (box.height() - layout.height) / 2f)
        c.save(); c.clipRect(box); c.translate(box.left, y); layout.draw(c); c.restore()
    }

    private fun drawTextLayout(c: Canvas, value: String, box: RectF, paint: TextPaint, align: String, maxLines: Int) {
        var size = paint.textSize
        var layout = makeLayout(value, box.width().toInt(), paint, align)
        while ((layout.height > box.height() || layout.lineCount > maxLines || paint.measureText(value) > box.width()) && size > 12f) {
            size -= 1f; paint.textSize = size; layout = makeLayout(value, box.width().toInt(), paint, align)
        }
        val y = box.top + max(0f, (box.height() - layout.height) / 2f)
        c.save(); c.clipRect(box); c.translate(box.left, y); layout.draw(c); c.restore()
    }

    private fun makeLayout(value: String, width: Int, paint: TextPaint, align: String): StaticLayout =
        StaticLayout.Builder.obtain(value, 0, value.length, paint, width.coerceAtLeast(1))
            .setAlignment(when (align) { "left" -> Layout.Alignment.ALIGN_NORMAL; "right" -> Layout.Alignment.ALIGN_OPPOSITE; else -> Layout.Alignment.ALIGN_CENTER })
            .setIncludePad(false)
            .setLineSpacing(2f, 1f)
            .build()

    private fun glow(c: Canvas, x: Float, y: Float, radius: Float, color: Int, alpha: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(x, y, radius, intArrayOf((color and 0x00FFFFFF) or (alpha.coerceIn(0, 255) shl 24), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
        }
        c.drawCircle(x, y, radius, paint)
    }

    private fun particles(c: Canvas, color: Int, count: Int, alpha: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
        repeat(count) { i ->
            val x = ((i * 193 + 47) % 1040 + 20).toFloat()
            val y = ((i * 137 + 71) % 850 + 20).toFloat()
            paint.alpha = (alpha - (i % 5) * 15).coerceAtLeast(28)
            c.drawCircle(x, y, if (i % 7 == 0) 5.5f else if (i % 3 == 0) 3f else 1.7f, paint)
        }
    }

    private fun dotGrid(c: Canvas, color: Int, x: Float, y: Float, columns: Int, rows: Int, gap: Float, alpha: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; this.alpha = alpha }
        repeat(columns) { col -> repeat(rows) { row -> c.drawCircle(x + col * gap, y + row * gap, 2.2f, paint) } }
    }

    private fun cornerBrackets(c: Canvas, color: Int, inset: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = 3f; alpha = 170 }
        c.drawLine(inset, inset, inset + 65f, inset, paint); c.drawLine(inset, inset, inset, inset + 65f, paint)
        c.drawLine(1080f - inset, inset, 1015f - inset / 2f, inset, paint); c.drawLine(1080f - inset, inset, 1080f - inset, inset + 65f, paint)
    }

    private fun leafSpray(c: Canvas, x: Float, y: Float, color: Int, mirror: Boolean) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; alpha = 70 }
        repeat(6) { i ->
            val dx = (i * 24f) * if (mirror) -1f else 1f
            c.save(); c.rotate((i * 16f - 30f) * if (mirror) -1f else 1f, x + dx, y + i * 16f)
            c.drawOval(RectF(x + dx - 24f, y + i * 16f - 9f, x + dx + 24f, y + i * 16f + 9f), paint); c.restore()
        }
    }

    private fun rays(c: Canvas, x: Float, y: Float, color: Int, count: Int, alpha: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = 2f; this.alpha = alpha }
        repeat(count) { i ->
            val angle = i * (360.0 / count) * Math.PI / 180.0
            c.drawLine(x, y, x + 820f * kotlin.math.cos(angle).toFloat(), y + 820f * kotlin.math.sin(angle).toFloat(), paint)
        }
    }

    private fun chevrons(c: Canvas, x: Float, y: Float, color: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = 9f; alpha = 130 }
        repeat(3) { i ->
            val left = x + i * 70f
            val path = Path().apply { moveTo(left, y); lineTo(left + 42f, y + 42f); lineTo(left, y + 84f) }
            c.drawPath(path, paint)
        }
    }

    private fun artDecoCorner(c: Canvas, x: Float, y: Float, color: Int, mirror: Boolean) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = 2f; alpha = 150 }
        c.save(); if (mirror) c.rotate(180f, x, y)
        repeat(3) { i ->
            val offset = i * 15f
            c.drawLine(x, y + offset, x + 95f - offset, y + offset, paint)
            c.drawLine(x + offset, y, x + offset, y + 95f - offset, paint)
        }
        c.restore()
    }

    private fun flower(c: Canvas, x: Float, y: Float, radius: Float, color: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; alpha = 45 }
        repeat(7) { i ->
            val angle = i * (Math.PI * 2 / 7)
            c.drawCircle(x + kotlin.math.cos(angle).toFloat() * radius * .55f, y + kotlin.math.sin(angle).toFloat() * radius * .55f, radius * .42f, paint)
        }
        paint.alpha = 75; c.drawCircle(x, y, radius * .28f, paint)
    }

    private fun mandala(c: Canvas, x: Float, y: Float, radius: Float, color: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = 1.5f; alpha = 42 }
        repeat(18) { i ->
            c.save(); c.rotate(i * 20f, x, y)
            c.drawOval(RectF(x - radius * .12f, y - radius, x + radius * .12f, y), paint)
            c.restore()
        }
        c.drawCircle(x, y, radius * .72f, paint)
    }

    private fun rgb(value: String): Int = Color.parseColor(value)
}
