package com.example.templates

import android.content.Context
import android.graphics.*
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.model.Poster
import com.example.ui.ProfileSettings
import java.io.File
import kotlin.math.max
import kotlin.math.min

/**
 * Dedicated high-fidelity renderer for the 5 curated Birthday base-template posters:
 *  - 201: Hindi Royal Navy Birthday (bday_hindi_navy_base.jpg)
 *  - 202: Hindi Purple Festive Birthday (bday_hindi_purple_base.jpg)
 *  - 203: Marathi Teal Birthday (bday_marathi_teal_base.jpg)
 *  - 204: English Blue Royal Birthday (bday_english_blue_base.jpg)
 *  - 205: English Pink Glitter Birthday (bday_english_pink_base.jpg)
 */
object BirthdayArtworkRenderer {

    fun supports(template: PosterTemplate): Boolean =
        template.category.equals("Birthday", ignoreCase = true) && template.style in 201..205

    fun draw(context: Context, canvas: Canvas, design: GeneratedPoster) {
        when (design.template.style) {
            201 -> hindiRoyalNavyBirthday(context, canvas, design)
            202 -> hindiPurpleFestiveBirthday(context, canvas, design)
            203 -> marathiTealBirthday(context, canvas, design)
            204 -> englishBlueRoyalBirthday(context, canvas, design)
            205 -> englishPinkGlitterBirthday(context, canvas, design)
            else -> hindiRoyalNavyBirthday(context, canvas, design)
        }
    }

    /**
     * Style 201 — Hindi Royal Navy Birthday (bday_hindi_navy_base.jpg)
     * Right circle portrait, name pill below circle, clean white footer card with user branding.
     */
    private fun hindiRoyalNavyBirthday(context: Context, c: Canvas, d: GeneratedPoster) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val baseBmp = loadBaseImage(context, "bday_hindi_navy_base")
        if (baseBmp != null) {
            c.drawBitmap(baseBmp, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            c.drawColor(rgb("#0D2344"))
        }

        // 1. TOP-LEFT LOGO (Cover demo logo area)
        drawLogoArea(context, c, d, RectF(28f, 22f, 260f, 100f), rgb("#0D2344"), light = true)

        // 2. MAIN BIRTHDAY PERSON PHOTO (Circle on the Right)
        val photoBox = RectF(606f, 140f, 1030f, 564f)
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
        // Gold accent ring
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 5.5f; color = rgb("#F5B738")
        }
        c.drawOval(photoBox, ringPaint)

        // 3. EDITABLE NAME & ROLE (Pills below circle)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "राहुल शर्मा" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(615f, 576f, 1020f, 650f)
            // Clean cream pill cover
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#FDF8EE") }
            c.drawRoundRect(nameBox, 38f, 38f, pillPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2.5f; color = rgb("#D4A237")
            }
            c.drawRoundRect(nameBox, 38f, 38f, borderPaint)
            label(c, name, RectF(nameBox.left + 10f, nameBox.top + 4f, nameBox.right - 10f, nameBox.bottom - 4f), 32f, rgb("#132238"), true, "center", 1)
        }

        val role = d.values[TemplateField.DESIGNATION.name].orEmpty()
        if (role.isNotBlank()) {
            val roleBox = RectF(690f, 656f, 945f, 700f)
            val tealPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#0E4B5E") }
            c.drawRoundRect(roleBox, 22f, 22f, tealPaint)
            label(c, role, RectF(roleBox.left + 8f, roleBox.top + 2f, roleBox.right - 8f, roleBox.bottom - 2f), 18f, Color.WHITE, true, "center", 1)
        }

        // 4. CLEAN BOTTOM FOOTER (White card covering placeholder contact info)
        drawWhiteFooter(c, d, RectF(15f, 915f, 1065f, 1060f))
    }

    /**
     * Style 202 — Hindi Purple Festive Birthday (bday_hindi_purple_base.jpg)
     * Left circle portrait, name ribbon under circle, dark purple footer band.
     */
    private fun hindiPurpleFestiveBirthday(context: Context, c: Canvas, d: GeneratedPoster) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val baseBmp = loadBaseImage(context, "bday_hindi_purple_base")
        if (baseBmp != null) {
            c.drawBitmap(baseBmp, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            c.drawColor(rgb("#2A0538"))
        }

        // 1. TOP-LEFT LOGO
        drawLogoArea(context, c, d, RectF(22f, 18f, 160f, 130f), rgb("#2A0538"), light = true)

        // 2. MAIN PHOTO (Circle on the Left)
        val photoBox = RectF(40f, 115f, 490f, 565f)
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
            style = Paint.Style.STROKE; strokeWidth = 5.5f; color = rgb("#F3C64A")
        }
        c.drawOval(photoBox, ringPaint)

        // 3. EDITABLE NAME & ROLE
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "राहुल शर्मा" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(60f, 495f, 470f, 580f)
            val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#FDF5F8") }
            c.drawRoundRect(nameBox, 18f, 18f, ribbonPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2.5f; color = rgb("#C89832")
            }
            c.drawRoundRect(nameBox, 18f, 18f, borderPaint)
            label(c, name, RectF(nameBox.left + 10f, nameBox.top + 4f, nameBox.right - 10f, nameBox.bottom - 4f), 32f, rgb("#4A0638"), true, "center", 1)
        }

        val role = d.values[TemplateField.DESIGNATION.name].orEmpty()
        if (role.isNotBlank()) {
            val roleBox = RectF(105f, 584f, 425f, 630f)
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#380530") }
            c.drawRoundRect(roleBox, 22f, 22f, pillPaint)
            label(c, role, RectF(roleBox.left + 8f, roleBox.top + 2f, roleBox.right - 8f, roleBox.bottom - 2f), 18f, rgb("#F3C64A"), true, "center", 1)
        }

        // 4. CLEAN BOTTOM FOOTER (Dark purple band)
        drawDarkFooter(c, d, RectF(0f, 930f, 1080f, 1080f), rgb("#20022B"), rgb("#F3C64A"))
    }

    /**
     * Style 203 — Marathi Teal Birthday (bday_marathi_teal_base.jpg)
     * Right capsule / arched portrait, left green name box, bottom clean white footer.
     */
    private fun marathiTealBirthday(context: Context, c: Canvas, d: GeneratedPoster) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val baseBmp = loadBaseImage(context, "bday_marathi_teal_base")
        if (baseBmp != null) {
            c.drawBitmap(baseBmp, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            c.drawColor(rgb("#F6F3EB"))
        }

        // 1. TOP-LEFT LOGO
        drawLogoArea(context, c, d, RectF(25f, 15f, 280f, 105f), rgb("#F6F3EB"), light = false)

        // 2. MAIN PHOTO (Arched Capsule on the Right)
        val photoBox = RectF(545f, 130f, 995f, 715f)
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

        // 3. EDITABLE NAME & ROLE (Left side)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "राहुल सावंत" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(55f, 465f, 495f, 555f)
            val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#0E3F3B") }
            c.drawRoundRect(nameBox, 28f, 28f, boxPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2.5f; color = rgb("#D4AF37")
            }
            c.drawRoundRect(nameBox, 28f, 28f, borderPaint)
            label(c, name, RectF(nameBox.left + 10f, nameBox.top + 4f, nameBox.right - 10f, nameBox.bottom - 4f), 34f, Color.WHITE, true, "center", 1)
        }

        val role = d.values[TemplateField.DESIGNATION.name].orEmpty()
        if (role.isNotBlank()) {
            val roleBox = RectF(145f, 560f, 405f, 608f)
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#FFFBF5") }
            c.drawRoundRect(roleBox, 20f, 20f, pillPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2f; color = rgb("#0E3F3B")
            }
            c.drawRoundRect(roleBox, 20f, 20f, borderPaint)
            label(c, role, RectF(roleBox.left + 6f, roleBox.top + 2f, roleBox.right - 6f, roleBox.bottom - 2f), 18f, rgb("#0E3F3B"), true, "center", 1)
        }

        // 4. CLEAN BOTTOM FOOTER (White card)
        drawWhiteFooter(c, d, RectF(15f, 935f, 1065f, 1065f))
    }

    /**
     * Style 204 — English Blue Royal Birthday (bday_english_blue_base.jpg)
     * Right circle portrait, blue ribbon on left for name, deep blue footer.
     */
    private fun englishBlueRoyalBirthday(context: Context, c: Canvas, d: GeneratedPoster) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val baseBmp = loadBaseImage(context, "bday_english_blue_base")
        if (baseBmp != null) {
            c.drawBitmap(baseBmp, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            c.drawColor(rgb("#042364"))
        }

        // 1. TOP-LEFT LOGO
        drawLogoArea(context, c, d, RectF(25f, 15f, 260f, 100f), rgb("#042364"), light = true)

        // 2. MAIN PHOTO (Circle on the Right)
        val photoBox = RectF(525f, 75f, 1030f, 580f)
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
            style = Paint.Style.STROKE; strokeWidth = 5.5f; color = rgb("#F7CA4D")
        }
        c.drawOval(photoBox, ringPaint)

        // 3. EDITABLE NAME & ROLE (Blue ribbon on left)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "ALEXANDER SMITH" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(75f, 442f, 670f, 538f)
            val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#072F7E") }
            c.drawRoundRect(nameBox, 18f, 18f, ribbonPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2.5f; color = rgb("#F7CA4D")
            }
            c.drawRoundRect(nameBox, 18f, 18f, borderPaint)
            label(c, name.uppercase(), RectF(nameBox.left + 10f, nameBox.top + 4f, nameBox.right - 10f, nameBox.bottom - 4f), 34f, Color.WHITE, true, "center", 1, 0.5f)
        }

        val role = d.values[TemplateField.DESIGNATION.name].orEmpty()
        if (role.isNotBlank()) {
            val roleBox = RectF(225f, 545f, 520f, 595f)
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#F0F6FF") }
            c.drawRoundRect(roleBox, 22f, 22f, pillPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2f; color = rgb("#F7CA4D")
            }
            c.drawRoundRect(roleBox, 22f, 22f, borderPaint)
            label(c, role.uppercase(), RectF(roleBox.left + 8f, roleBox.top + 2f, roleBox.right - 8f, roleBox.bottom - 2f), 18f, rgb("#072F7E"), true, "center", 1)
        }

        // 4. CLEAN BOTTOM FOOTER (Dark blue bar)
        drawDarkFooter(c, d, RectF(0f, 955f, 1080f, 1080f), rgb("#031D56"), rgb("#F7CA4D"))
    }

    /**
     * Style 205 — English Pink Glitter Birthday (bday_english_pink_base.jpg)
     * Left circle portrait with gold frame, pink ribbon on right, dark luxury footer.
     */
    private fun englishPinkGlitterBirthday(context: Context, c: Canvas, d: GeneratedPoster) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val baseBmp = loadBaseImage(context, "bday_english_pink_base")
        if (baseBmp != null) {
            c.drawBitmap(baseBmp, null, RectF(0f, 0f, 1080f, 1080f), paint)
        } else {
            c.drawColor(rgb("#180018"))
        }

        // 1. TOP-LEFT LOGO
        drawLogoArea(context, c, d, RectF(30f, 25f, 210f, 115f), rgb("#180018"), light = true)

        // 2. MAIN PHOTO (Circle on the Left)
        val photoBox = RectF(65f, 105f, 545f, 585f)
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
            style = Paint.Style.STROKE; strokeWidth = 5.5f; color = rgb("#E5B842")
        }
        c.drawOval(photoBox, ringPaint)

        // 3. EDITABLE NAME & ROLE (Pink ribbon on right)
        val name = d.values[TemplateField.NAME.name].orEmpty().ifBlank {
            if (photoSource.isNotBlank()) "SOPHIA JOHNSON" else ""
        }
        if (name.isNotBlank()) {
            val nameBox = RectF(525f, 390f, 975f, 490f)
            val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#85064B") }
            c.drawRoundRect(nameBox, 18f, 18f, ribbonPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2.5f; color = rgb("#E5B842")
            }
            c.drawRoundRect(nameBox, 18f, 18f, borderPaint)
            label(c, name.uppercase(), RectF(nameBox.left + 10f, nameBox.top + 4f, nameBox.right - 10f, nameBox.bottom - 4f), 34f, Color.WHITE, true, "center", 1, 0.5f)
        }

        val role = d.values[TemplateField.DESIGNATION.name].orEmpty()
        if (role.isNotBlank()) {
            val roleBox = RectF(595f, 498f, 905f, 545f)
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#150310") }
            c.drawRoundRect(roleBox, 22f, 22f, pillPaint)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2f; color = rgb("#E5B842")
            }
            c.drawRoundRect(roleBox, 22f, 22f, borderPaint)
            label(c, role.uppercase(), RectF(roleBox.left + 8f, roleBox.top + 2f, roleBox.right - 8f, roleBox.bottom - 2f), 18f, rgb("#E5B842"), true, "center", 1)
        }

        // 4. CLEAN BOTTOM FOOTER (Dark luxury bar)
        drawDarkFooter(c, d, RectF(15f, 940f, 1065f, 1065f), rgb("#0E030E"), rgb("#E5B842"))
    }

    // ── HELPER RENDERING METHODS ───────────────────────────────────────────

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

    private fun drawLogoArea(context: Context, c: Canvas, d: GeneratedPoster, box: RectF, bg: Int, light: Boolean) {
        val logoSource = d.branding.logo.trim()
        val logoBmp = if (logoSource.isNotBlank()) TemplateImages.read(context, logoSource) else null
        if (logoBmp != null) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bg; alpha = 240 }
            c.drawRoundRect(box, 14f, 14f, p)
            drawBitmapContained(c, logoBmp, RectF(box.left + 6f, box.top + 6f, box.right - 6f, box.bottom - 6f), d.logoCrop)
        } else if (d.branding.company.isNotBlank()) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bg; alpha = 220 }
            c.drawRoundRect(box, 14f, 14f, p)
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
        drawBuildingIcon(c, RectF(box.left + 22f, box.top + 28f, box.left + 62f, box.top + 76f), rgb("#F59E0B"))
        label(c, company.uppercase(), RectF(box.left + 72f, box.top + 22f, box.left + 360f, box.top + 58f), 22f, rgb("#0F172A"), true, "left", 1)
        val tagline = d.branding.tagline.ifBlank { "Smart Business Platform" }
        label(c, tagline, RectF(box.left + 72f, box.top + 58f, box.left + 360f, box.top + 88f), 14f, rgb("#64748B"), false, "left", 1)

        // Divider 1
        val divPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = rgb("#CBD5E1"); strokeWidth = 1.5f }
        c.drawLine(box.left + 375f, box.top + 22f, box.left + 375f, box.bottom - 22f, divPaint)

        // Center: Website (only if present)
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            drawGlobeIcon(c, RectF(box.left + 395f, box.centerY() - 16f, box.left + 427f, box.centerY() + 16f), rgb("#0D9488"))
            label(c, website, RectF(box.left + 435f, box.centerY() - 18f, box.left + 700f, box.centerY() + 18f), 17f, rgb("#0F172A"), false, "left", 1)
        }

        // Divider 2
        c.drawLine(box.left + 715f, box.top + 22f, box.left + 715f, box.bottom - 22f, divPaint)

        // Right: Phone (only if present)
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
        drawBuildingIcon(c, RectF(box.left + 22f, box.top + 28f, box.left + 62f, box.top + 76f), accent)
        label(c, company.uppercase(), RectF(box.left + 72f, box.top + 22f, box.left + 360f, box.top + 58f), 22f, Color.WHITE, true, "left", 1)
        val tagline = d.branding.tagline.ifBlank { "Smart Business Platform" }
        label(c, tagline, RectF(box.left + 72f, box.top + 58f, box.left + 360f, box.top + 88f), 14f, accent, false, "left", 1)

        // Divider 1
        val divPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accent; alpha = 120; strokeWidth = 1.5f }
        c.drawLine(box.left + 375f, box.top + 22f, box.left + 375f, box.bottom - 22f, divPaint)

        // Center: Website
        val website = d.branding.website.trim()
        if (website.isNotBlank()) {
            drawGlobeIcon(c, RectF(box.left + 395f, box.centerY() - 16f, box.left + 427f, box.centerY() + 16f), accent)
            label(c, website, RectF(box.left + 435f, box.centerY() - 18f, box.left + 700f, box.centerY() + 18f), 17f, Color.WHITE, false, "left", 1)
        }

        // Divider 2
        c.drawLine(box.left + 715f, box.top + 22f, box.left + 715f, box.bottom - 22f, divPaint)

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
        // Base skyscraper shapes
        c.drawRect(RectF(box.left + box.width() * 0.1f, box.top + box.height() * 0.35f, box.left + box.width() * 0.48f, box.bottom), paint)
        c.drawRect(RectF(box.left + box.width() * 0.52f, box.top + box.height() * 0.15f, box.right - box.width() * 0.1f, box.bottom), paint)
    }

    private fun rgb(hex: String): Int = Color.parseColor(hex)
}
