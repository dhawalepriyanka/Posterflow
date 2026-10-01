package com.example.templates

import android.content.Context
import android.graphics.*
import java.io.File
import kotlin.math.max
import kotlin.math.min

/**
 * Dedicated high-fidelity renderer for the 5 curated Achievement base-template posters:
 *  - 301: Hindi Blue Achievement (achieve_hindi_blue_base.jpg)
 *  - 302: Hindi Red Royal Crown Achievement (achieve_hindi_red_base.jpg)
 *  - 303: Marathi Mint & Gold Salute (achieve_marathi_mint_base.jpg)
 *  - 304: Marathi Saffron/Orange & Gold (achieve_marathi_orange_base.jpg)
 *  - 305: English Proud Moment Burgundy (achieve_english_purple_base.jpg)
 */
object AchievementArtworkRenderer {

    fun supports(template: PosterTemplate): Boolean =
        template.category.equals("Achievement", ignoreCase = true) && template.style in 301..305

    fun draw(context: Context, canvas: Canvas, design: GeneratedPoster) {
        when (design.template.style) {
            301 -> hindiBlueAchievement(context, canvas, design)
            302 -> hindiRedRoyalAchievement(context, canvas, design)
            303 -> marathiMintSaluteAchievement(context, canvas, design)
            304 -> marathiOrangeGoldAchievement(context, canvas, design)
            305 -> englishProudMomentAchievement(context, canvas, design)
            else -> hindiBlueAchievement(context, canvas, design)
        }
    }

    /**
     * Style 301 — Hindi Blue & Gold Trophy Achievement (achieve_hindi_blue_base.jpg)
     */
    private fun hindiBlueAchievement(context: Context, c: Canvas, d: GeneratedPoster) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val baseBmp = loadBaseImage(context, "achieve_hindi_blue_base")
        if (baseBmp != null) {
            c.drawBitmap(baseBmp, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            c.drawColor(rgb("#0A2558"))
        }

        // 1. TOP-LEFT LOGO
        drawLogoArea(context, c, d, RectF(28f, 22f, 268f, 110f), Color.WHITE, light = false)

        // 2. MAIN ACHIEVER PHOTO (Circle on the Left)
        val photoBox = RectF(35f, 160f, 465f, 590f)
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBmp = TemplateImages.read(context, photoSource)
            if (photoBmp != null) {
                val circlePath = Path().apply { addOval(photoBox, Path.Direction.CW) }
                c.save()
                c.clipPath(circlePath)
                c.drawColor(Color.WHITE)
                drawBitmapCover(c, photoBmp, photoBox, d.crop)
                c.restore()
            }
        }
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 5.5f; color = rgb("#F5B738")
        }
        c.drawOval(photoBox, ringPaint)

        // 3. EDITABLE NAME & ROLE (Pill card below circle)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "राहुल शर्मा" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(50f, 570f, 545f, 645f)
            val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
            c.drawRoundRect(nameBox, 32f, 32f, cardPaint)
            label(c, name, RectF(nameBox.left + 10f, nameBox.top + 4f, nameBox.right - 10f, nameBox.bottom - 4f), 34f, rgb("#0F172A"), true, "center", 1)
        }

        val role = d.values[TemplateField.DESIGNATION.name].orEmpty().ifBlank {
            d.values[TemplateField.ACHIEVEMENT.name].orEmpty()
        }
        if (role.isNotBlank()) {
            val roleBox = RectF(160f, 650f, 440f, 695f)
            val tealPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#0D9488") }
            c.drawRoundRect(roleBox, 22f, 22f, tealPaint)
            label(c, role, RectF(roleBox.left + 8f, roleBox.top + 2f, roleBox.right - 8f, roleBox.bottom - 2f), 18f, Color.WHITE, true, "center", 1)
        }

        // 4. CLEAN BOTTOM FOOTER (White card)
        drawWhiteFooter(c, d, RectF(15f, 895f, 1065f, 980f))
    }

    /**
     * Style 302 — Hindi Red Royal Crown Achievement (achieve_hindi_red_base.jpg)
     */
    private fun hindiRedRoyalAchievement(context: Context, c: Canvas, d: GeneratedPoster) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val baseBmp = loadBaseImage(context, "achieve_hindi_red_base")
        if (baseBmp != null) {
            c.drawBitmap(baseBmp, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            c.drawColor(rgb("#7A0C18"))
        }

        // 1. TOP-LEFT LOGO (Circular medallion)
        drawLogoArea(context, c, d, RectF(25f, 20f, 185f, 175f), rgb("#FFFBF2"), light = false, circular = true)

        // 2. MAIN PHOTO (Gold Rectangular Picture Frame on the Right)
        val photoBox = RectF(620f, 195f, 955f, 605f)
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBmp = TemplateImages.read(context, photoSource)
            if (photoBmp != null) {
                val framePath = Path().apply { addRoundRect(photoBox, 10f, 10f, Path.Direction.CW) }
                c.save()
                c.clipPath(framePath)
                c.drawColor(Color.WHITE)
                drawBitmapCover(c, photoBmp, photoBox, d.crop)
                c.restore()
            }
        }
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 5.5f; color = rgb("#D4AF37")
        }
        c.drawRoundRect(photoBox, 10f, 10f, ringPaint)

        // 3. EDITABLE NAME & ROLE (Left banner)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "राहुल शर्मा" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(90f, 465f, 490f, 550f)
            val bannerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#FDF6E2") }
            c.drawRoundRect(nameBox, 24f, 24f, bannerPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2.5f; color = rgb("#C59B27")
            }
            c.drawRoundRect(nameBox, 24f, 24f, borderPaint)
            label(c, name, RectF(nameBox.left + 10f, nameBox.top + 4f, nameBox.right - 10f, nameBox.bottom - 4f), 34f, rgb("#5C0A14"), true, "center", 1)
        }

        val role = d.values[TemplateField.DESIGNATION.name].orEmpty().ifBlank {
            d.values[TemplateField.ACHIEVEMENT.name].orEmpty()
        }
        if (role.isNotBlank()) {
            val roleBox = RectF(140f, 554f, 440f, 600f)
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#4A060E") }
            c.drawRoundRect(roleBox, 22f, 22f, pillPaint)
            label(c, role, RectF(roleBox.left + 8f, roleBox.top + 2f, roleBox.right - 8f, roleBox.bottom - 2f), 18f, Color.WHITE, true, "center", 1)
        }

        // 4. CLEAN BOTTOM FOOTER (Dark red bar)
        drawDarkFooter(c, d, RectF(0f, 905f, 1080f, 975f), rgb("#4A060E"), rgb("#D4AF37"))
    }

    /**
     * Style 303 — Marathi Mint & Gold Salute (achieve_marathi_mint_base.jpg)
     */
    private fun marathiMintSaluteAchievement(context: Context, c: Canvas, d: GeneratedPoster) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val baseBmp = loadBaseImage(context, "achieve_marathi_mint_base")
        if (baseBmp != null) {
            c.drawBitmap(baseBmp, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            c.drawColor(rgb("#F2F9F6"))
        }

        // 1. TOP-LEFT LOGO
        drawLogoArea(context, c, d, RectF(25f, 18f, 380f, 125f), rgb("#F2F9F6"), light = false)

        // 2. MAIN PHOTO (Arched Capsule on the Right)
        val photoBox = RectF(560f, 120f, 985f, 550f)
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBmp = TemplateImages.read(context, photoSource)
            if (photoBmp != null) {
                val archPath = shapePath(photoBox, "arch")
                c.save()
                c.clipPath(archPath)
                c.drawColor(Color.WHITE)
                drawBitmapCover(c, photoBmp, photoBox, d.crop)
                c.restore()
            }
        }
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 5.5f; color = rgb("#D4AF37")
        }
        c.drawPath(shapePath(photoBox, "arch"), ringPaint)

        // 3. EDITABLE NAME & ROLE (Right side below arch)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "राहुल सावंत" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(530f, 552f, 955f, 635f)
            val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#FFFBF0") }
            c.drawRoundRect(nameBox, 24f, 24f, boxPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2.5f; color = rgb("#D4AF37")
            }
            c.drawRoundRect(nameBox, 24f, 24f, borderPaint)
            label(c, name, RectF(nameBox.left + 10f, nameBox.top + 4f, nameBox.right - 10f, nameBox.bottom - 4f), 34f, rgb("#0E3F3B"), true, "center", 1)
        }

        val role = d.values[TemplateField.DESIGNATION.name].orEmpty().ifBlank {
            d.values[TemplateField.ACHIEVEMENT.name].orEmpty()
        }
        if (role.isNotBlank()) {
            val roleBox = RectF(595f, 628f, 885f, 672f)
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#0E3F3B") }
            c.drawRoundRect(roleBox, 22f, 22f, pillPaint)
            label(c, role, RectF(roleBox.left + 6f, roleBox.top + 2f, roleBox.right - 6f, roleBox.bottom - 2f), 18f, Color.WHITE, true, "center", 1)
        }

        // 4. CLEAN BOTTOM FOOTER (Dark emerald bar)
        drawDarkFooter(c, d, RectF(0f, 890f, 1080f, 965f), rgb("#072A22"), rgb("#48BB78"))
    }

    /**
     * Style 304 — Marathi Saffron/Orange & Gold (achieve_marathi_orange_base.jpg)
     */
    private fun marathiOrangeGoldAchievement(context: Context, c: Canvas, d: GeneratedPoster) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val baseBmp = loadBaseImage(context, "achieve_marathi_orange_base")
        if (baseBmp != null) {
            c.drawBitmap(baseBmp, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            c.drawColor(rgb("#FFF6EA"))
        }

        // 1. TOP-LEFT LOGO
        drawLogoArea(context, c, d, RectF(25f, 22f, 215f, 115f), Color.WHITE, light = false)

        // 2. MAIN PHOTO (Circle with Gold Wreath on the Left)
        val photoBox = RectF(55f, 130f, 480f, 555f)
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBmp = TemplateImages.read(context, photoSource)
            if (photoBmp != null) {
                val circlePath = Path().apply { addOval(photoBox, Path.Direction.CW) }
                c.save()
                c.clipPath(circlePath)
                c.drawColor(Color.WHITE)
                drawBitmapCover(c, photoBmp, photoBox, d.crop)
                c.restore()
            }
        }
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 5.5f; color = rgb("#E5A932")
        }
        c.drawOval(photoBox, ringPaint)

        // 3. EDITABLE NAME & ROLE (Maroon ribbon on right)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "राहुल सावंत" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(490f, 478f, 925f, 570f)
            val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#660A14") }
            c.drawRoundRect(nameBox, 28f, 28f, ribbonPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2.5f; color = rgb("#D4AF37")
            }
            c.drawRoundRect(nameBox, 28f, 28f, borderPaint)
            label(c, name, RectF(nameBox.left + 10f, nameBox.top + 4f, nameBox.right - 10f, nameBox.bottom - 4f), 34f, Color.WHITE, true, "center", 1)
        }

        val role = d.values[TemplateField.DESIGNATION.name].orEmpty().ifBlank {
            d.values[TemplateField.ACHIEVEMENT.name].orEmpty()
        }
        if (role.isNotBlank()) {
            val roleBox = RectF(525f, 568f, 890f, 615f)
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#FDF5E6") }
            c.drawRoundRect(roleBox, 22f, 22f, pillPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2f; color = rgb("#660A14")
            }
            c.drawRoundRect(roleBox, 22f, 22f, borderPaint)
            label(c, role, RectF(roleBox.left + 8f, roleBox.top + 2f, roleBox.right - 8f, roleBox.bottom - 2f), 18f, rgb("#660A14"), true, "center", 1)
        }

        // 4. CLEAN BOTTOM FOOTER (Maroon bar)
        drawDarkFooter(c, d, RectF(0f, 895f, 1080f, 975f), rgb("#660A14"), rgb("#F5B738"))
    }

    /**
     * Style 305 — English Proud Moment Burgundy (achieve_english_purple_base.jpg)
     */
    private fun englishProudMomentAchievement(context: Context, c: Canvas, d: GeneratedPoster) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val baseBmp = loadBaseImage(context, "achieve_english_purple_base")
        if (baseBmp != null) {
            c.drawBitmap(baseBmp, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            c.drawColor(rgb("#1A0318"))
        }

        // 1. TOP-LEFT LOGO
        drawLogoArea(context, c, d, RectF(25f, 22f, 210f, 90f), rgb("#1A0318"), light = true)

        // 2. MAIN PHOTO (Circle on the Left)
        val photoBox = RectF(65f, 100f, 510f, 545f)
        val photoSource = d.photo.trim()
        if (photoSource.isNotBlank()) {
            val photoBmp = TemplateImages.read(context, photoSource)
            if (photoBmp != null) {
                val circlePath = Path().apply { addOval(photoBox, Path.Direction.CW) }
                c.save()
                c.clipPath(circlePath)
                c.drawColor(Color.WHITE)
                drawBitmapCover(c, photoBmp, photoBox, d.crop)
                c.restore()
            }
        }
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 5.5f; color = rgb("#F5B738")
        }
        c.drawOval(photoBox, ringPaint)

        // 3. EDITABLE NAME & ROLE (Burgundy ribbon)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "ALEXANDER SMITH" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(60f, 585f, 555f, 675f)
            val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#5E083B") }
            c.drawRoundRect(nameBox, 16f, 16f, ribbonPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2.5f; color = rgb("#F5B738")
            }
            c.drawRoundRect(nameBox, 16f, 16f, borderPaint)
            label(c, name.uppercase(), RectF(nameBox.left + 10f, nameBox.top + 4f, nameBox.right - 10f, nameBox.bottom - 4f), 34f, Color.WHITE, true, "center", 1, 0.5f)
        }

        val role = d.values[TemplateField.DESIGNATION.name].orEmpty().ifBlank {
            d.values[TemplateField.ACHIEVEMENT.name].orEmpty()
        }
        if (role.isNotBlank()) {
            val roleBox = RectF(150f, 672f, 460f, 720f)
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#150212") }
            c.drawRoundRect(roleBox, 22f, 22f, pillPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2f; color = rgb("#F5B738")
            }
            c.drawRoundRect(roleBox, 22f, 22f, borderPaint)
            label(c, role.uppercase(), RectF(roleBox.left + 8f, roleBox.top + 2f, roleBox.right - 8f, roleBox.bottom - 2f), 18f, rgb("#F5B738"), true, "center", 1)
        }

        // 4. CLEAN BOTTOM FOOTER (Dark maroon bar)
        drawDarkFooter(c, d, RectF(15f, 920f, 1065f, 985f), rgb("#120110"), rgb("#F5B738"))
    }

    // ── HELPER METHODS ─────────────────────────────────────────────────────

    private fun loadBaseImage(context: Context, resName: String): Bitmap? {
        return TemplateImages.read(context, "res:$resName")
            ?: runCatching {
                val f = File("app/src/main/res/drawable/$resName.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()
            ?: runCatching {
                val f = File("d:/poster-maker/app/src/main/res/drawable/$resName.jpg")
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            }.getOrNull()
    }

    private fun drawLogoArea(context: Context, c: Canvas, d: GeneratedPoster, box: RectF, bg: Int, light: Boolean, circular: Boolean = false) {
        val logoSource = d.branding.logo.trim()
        val logoBmp = if (logoSource.isNotBlank()) TemplateImages.read(context, logoSource) else null
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bg; alpha = 240 }
        if (circular) c.drawOval(box, p) else c.drawRoundRect(box, 14f, 14f, p)
        if (logoBmp != null) {
            val inset = if (circular) 14f else 6f
            drawBitmapContained(c, logoBmp, RectF(box.left + inset, box.top + inset, box.right - inset, box.bottom - inset), d.logoCrop)
        } else if (d.branding.company.isNotBlank()) {
            val textColor = if (light) Color.WHITE else rgb("#1E293B")
            label(c, d.branding.company.uppercase(), RectF(box.left + 8f, box.top + 6f, box.right - 8f, box.bottom - 6f), 18f, textColor, true, "center", 2)
        }
    }

    private fun drawWhiteFooter(c: Canvas, d: GeneratedPoster, box: RectF) {
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            setShadowLayer(8f, 0f, 2f, Color.argb(40, 0, 0, 0))
        }
        c.drawRoundRect(box, 36f, 36f, cardPaint)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 2f; color = rgb("#E2E8F0")
        }
        c.drawRoundRect(box, 36f, 36f, borderPaint)

        // Left: Company name + tagline
        val company = d.branding.company.ifBlank { "BizFlow" }
        drawBuildingIcon(c, RectF(box.left + 22f, box.top + 16f, box.left + 62f, box.top + 64f), rgb("#F59E0B"))
        label(c, company.uppercase(), RectF(box.left + 72f, box.top + 12f, box.left + 360f, box.top + 48f), 22f, rgb("#0F172A"), true, "left", 1)
        val tagline = d.branding.tagline.ifBlank { "Smart Business Platform" }
        label(c, tagline, RectF(box.left + 72f, box.top + 48f, box.left + 360f, box.top + 76f), 14f, rgb("#64748B"), false, "left", 1)

        // Divider 1
        val divPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#CBD5E1"); strokeWidth = 1.5f }
        c.drawLine(box.left + 375f, box.top + 15f, box.left + 375f, box.bottom - 15f, divPaint)

        // Center: Website
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            drawGlobeIcon(c, RectF(box.left + 395f, box.centerY() - 16f, box.left + 427f, box.centerY() + 16f), rgb("#0D9488"))
            label(c, website, RectF(box.left + 435f, box.centerY() - 18f, box.left + 700f, box.centerY() + 18f), 17f, rgb("#0F172A"), false, "left", 1)
        }

        // Divider 2
        c.drawLine(box.left + 715f, box.top + 15f, box.left + 715f, box.bottom - 15f, divPaint)

        // Right: Phone
        val phone = d.branding.phone.trim()
        if (phone.isNotBlank()) {
            val phoneBg = RectF(box.left + 735f, box.centerY() - 18f, box.left + 771f, box.centerY() + 18f)
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#F59E0B") }
            c.drawOval(phoneBg, p)
            drawPhoneHandsetIcon(c, RectF(phoneBg.left + 7f, phoneBg.top + 7f, phoneBg.right - 7f, phoneBg.bottom - 7f), Color.WHITE)
            label(c, phone, RectF(box.left + 780f, box.centerY() - 18f, box.right - 20f, box.centerY() + 18f), 18f, rgb("#0F172A"), true, "left", 1)
        }
    }

    private fun drawDarkFooter(c: Canvas, d: GeneratedPoster, box: RectF, bg: Int, accent: Int) {
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bg; style = Paint.Style.FILL }
        c.drawRoundRect(box, 30f, 30f, cardPaint)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 2.5f; color = accent
        }
        c.drawRoundRect(box, 30f, 30f, borderPaint)

        // Left: Company name + tagline
        val company = d.branding.company.ifBlank { "BizFlow" }
        drawBuildingIcon(c, RectF(box.left + 22f, box.top + 14f, box.left + 62f, box.top + 60f), accent)
        label(c, company.uppercase(), RectF(box.left + 72f, box.top + 10f, box.left + 360f, box.top + 44f), 22f, Color.WHITE, true, "left", 1)
        val tagline = d.branding.tagline.ifBlank { "Smart Business Platform" }
        label(c, tagline, RectF(box.left + 72f, box.top + 44f, box.left + 360f, box.top + 72f), 14f, accent, false, "left", 1)

        // Divider 1
        val divPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accent; alpha = 120; strokeWidth = 1.5f }
        c.drawLine(box.left + 375f, box.top + 15f, box.left + 375f, box.bottom - 15f, divPaint)

        // Center: Website
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            drawGlobeIcon(c, RectF(box.left + 395f, box.centerY() - 16f, box.left + 427f, box.centerY() + 16f), accent)
            label(c, website, RectF(box.left + 435f, box.centerY() - 18f, box.left + 700f, box.centerY() + 18f), 17f, Color.WHITE, false, "left", 1)
        }

        // Divider 2
        c.drawLine(box.left + 715f, box.top + 15f, box.left + 715f, box.bottom - 15f, divPaint)

        // Right: Phone
        val phone = d.branding.phone.trim()
        if (phone.isNotBlank()) {
            val phoneBg = RectF(box.left + 735f, box.centerY() - 18f, box.left + 771f, box.centerY() + 18f)
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accent }
            c.drawOval(phoneBg, p)
            drawPhoneHandsetIcon(c, RectF(phoneBg.left + 7f, phoneBg.top + 7f, phoneBg.right - 7f, phoneBg.bottom - 7f), Color.BLACK)
            label(c, phone, RectF(box.left + 780f, box.centerY() - 18f, box.right - 20f, box.centerY() + 18f), 18f, Color.WHITE, true, "left", 1)
        }
    }

    private fun drawBitmapCover(
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

    private fun drawBitmapContained(c: Canvas, bitmap: Bitmap, box: RectF, crop: PhotoCrop = PhotoCrop()) {
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
            "arch" -> {
                moveTo(box.left, box.bottom)
                lineTo(box.left, box.top + box.width() / 2f)
                arcTo(RectF(box.left, box.top, box.right, box.top + box.width()), 180f, 180f, false)
                lineTo(box.right, box.bottom)
                close()
            }
            else -> addRoundRect(box, 30f, 30f, Path.Direction.CW)
        }
    }

    private fun label(
        c: Canvas,
        value: String,
        box: RectF,
        requestedSize: Float,
        color: Int,
        bold: Boolean,
        align: String,
        maxLines: Int = 1,
        letterSpacing: Float = 0f
    ) {
        if (value.isBlank()) return
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            typeface = if (bold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
            if (letterSpacing > 0f) this.letterSpacing = letterSpacing / 10f
        }
        var size = requestedSize
        paint.textSize = size
        val words = value.trim().split("\\s+".toRegex())
        val lines = mutableListOf<String>()
        if (maxLines == 1) {
            while (size > 10f && paint.measureText(value) > box.width()) {
                size -= 1.5f
                paint.textSize = size
            }
            lines.add(value)
        } else {
            var currentLine = ""
            for (w in words) {
                val candidate = if (currentLine.isEmpty()) w else "$currentLine $w"
                if (paint.measureText(candidate) <= box.width()) {
                    currentLine = candidate
                } else {
                    if (currentLine.isNotEmpty()) lines.add(currentLine)
                    currentLine = w
                    if (lines.size >= maxLines - 1) break
                }
            }
            if (currentLine.isNotEmpty()) lines.add(currentLine)
        }
        val lineHeight = paint.fontSpacing
        val totalH = lines.size * lineHeight
        var startY = box.centerY() - totalH / 2f + paint.textSize * 0.82f
        for (line in lines) {
            val x = when (align) {
                "center" -> box.centerX() - paint.measureText(line) / 2f
                "right" -> box.right - paint.measureText(line)
                else -> box.left
            }
            c.drawText(line, x, startY, paint)
            startY += lineHeight
        }
    }

    private fun drawPhoneHandsetIcon(c: Canvas, box: RectF, color: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = max(2.2f, box.width() * 0.12f)
            strokeCap = Paint.Cap.ROUND
        }
        val path = Path().apply {
            moveTo(box.left + box.width() * 0.25f, box.top + box.height() * 0.15f)
            quadTo(box.left + box.width() * 0.5f, box.top + box.height() * 0.05f, box.right - box.width() * 0.2f, box.top + box.height() * 0.35f)
            lineTo(box.right - box.width() * 0.32f, box.top + box.height() * 0.48f)
            quadTo(box.left + box.width() * 0.52f, box.top + box.height() * 0.52f, box.left + box.width() * 0.48f, box.bottom - box.height() * 0.32f)
            lineTo(box.left + box.width() * 0.35f, box.bottom - box.height() * 0.2f)
            quadTo(box.left + box.width() * 0.05f, box.bottom - box.height() * 0.5f, box.left + box.width() * 0.25f, box.top + box.height() * 0.15f)
        }
        c.drawPath(path, paint)
    }

    private fun drawGlobeIcon(c: Canvas, box: RectF, color: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 2.2f
        }
        c.drawOval(box, paint)
        c.drawLine(box.left, box.centerY(), box.right, box.centerY(), paint)
        c.drawLine(box.centerX(), box.top, box.centerX(), box.bottom, paint)
        val inner = RectF(box.centerX() - box.width() * 0.25f, box.top, box.centerX() + box.width() * 0.25f, box.bottom)
        c.drawOval(inner, paint)
    }

    private fun drawBuildingIcon(c: Canvas, box: RectF, color: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
        }
        c.drawRect(RectF(box.left + box.width() * 0.1f, box.top + box.height() * 0.35f, box.left + box.width() * 0.48f, box.bottom), paint)
        c.drawRect(RectF(box.left + box.width() * 0.52f, box.top + box.height() * 0.15f, box.right - box.width() * 0.1f, box.bottom), paint)
    }

    private fun rgb(hex: String): Int = Color.parseColor(hex)
}
