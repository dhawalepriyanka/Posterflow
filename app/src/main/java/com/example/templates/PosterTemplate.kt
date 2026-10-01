package com.example.templates

import com.example.model.Poster
import com.example.ui.ProfileSettings
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

enum class TemplateField { PHOTO, NAME, DESIGNATION, MESSAGE, AMOUNT, ACHIEVEMENT, DATE, QUOTE, COMPANY }
enum class BrandingField { LOGO, BUSINESS_NAME, PROFILE_PHOTO, PERSON_NAME, DESIGNATION, PHONE, WHATSAPP, WEBSITE, ADDRESS, SOCIAL }
enum class TemplateStatus { ACTIVE, INACTIVE, DELETED }

data class TemplateSlot(
    val field: TemplateField, val enabled: Boolean = true, val required: Boolean = false,
    val x: Float = 80f, val y: Float = 760f, val width: Float = 920f, val height: Float = 70f,
    val shape: String = "rounded", val borderColor: String = "#F3CD76", val borderWidth: Float = 5f,
    val font: String = "sans-serif", val fontSize: Float = 48f, val bold: Boolean = true,
    val italic: Boolean = false, val color: String = "#FFFFFF", val alignment: String = "center",
    val maxLines: Int = 2, val defaultScale: Float = 1f
)

/** These flags describe branding, never a particular customer's business values. */
data class BrandingBand(
    val enabled: Boolean = true, val height: Float = 110f,
    val backgroundColor: String = "#091C30", val textColor: String = "#FFFFFF",
    val layout: String = "left", val showLogo: Boolean = true, val showCompanyName: Boolean = true,
    val showTagline: Boolean = false, val showPhone: Boolean = false,
    val showWebsite: Boolean = false, val showEmail: Boolean = false, val showAddress: Boolean = false
)

data class StaticText(
    val text: String, val x: Float, val y: Float, val width: Float, val height: Float,
    val size: Float = 58f, val color: String = "#F3CD76", val bold: Boolean = true,
    val alignment: String = "left", val maxLines: Int = 4,
    /** Words in this list are rendered in accent color at slightly larger size for visual hierarchy. */
    val highlightWords: List<String> = emptyList(),
    /** If true, this text participates in placeholder substitution ({{USER_NAME}} etc.). */
    val usesPlaceholders: Boolean = false
)

data class PosterTemplate(
    val id: Int, val name: String, val category: String,
    val backgroundArtwork: String = "", val canvasWidth: Int = 1080, val canvasHeight: Int = 1080,
    val header: BrandingBand = BrandingBand(),
    val footer: BrandingBand = BrandingBand(height = 145f, showLogo = false, showPhone = true, showWebsite = true),
    val slots: List<TemplateSlot> = emptyList(), val staticTexts: List<StaticText> = emptyList(),
    val baseColor: String = "#12233E", val accentColor: String = "#F3CD76", val style: Int = 0,
    val status: TemplateStatus = TemplateStatus.ACTIVE, val order: Int = 0,
    val createdAt: Long = 1789603200000L, val updatedAt: Long = createdAt,
    /**
     * Hints to the renderer where the primary person photo slot is positioned in the composition.
     * Values: "center" | "right" | "left" | "bottom_right" | "bottom_left"
     * Used when the template has a split layout (person photo on one side, content on the other).
     */
    val photoPosition: String = "center",
    /**
     * Specific branding fields this template supports (e.g. LOGO + BUSINESS_NAME + PHONE).
     * If empty, automatically derived from header/footer/slots.
     */
    val supportedBranding: Set<BrandingField> = emptySet()
) {
    val requiredFields get() = slots.filter { it.enabled && it.required }.map { it.field }
    val visibleFields get() = slots.filter { it.enabled }.map { it.field }.distinct()
    val activeBranding: Set<BrandingField> get() {
        if (supportedBranding.isNotEmpty()) return supportedBranding
        val result = mutableSetOf<BrandingField>()
        if ((header.enabled && header.showLogo) || (footer.enabled && footer.showLogo)) result += BrandingField.LOGO
        if ((header.enabled && header.showCompanyName) || (footer.enabled && footer.showCompanyName)) result += BrandingField.BUSINESS_NAME
        if ((header.enabled && header.showPhone) || (footer.enabled && footer.showPhone)) result += BrandingField.PHONE
        if ((header.enabled && header.showWebsite) || (footer.enabled && footer.showWebsite)) result += BrandingField.WEBSITE
        if ((header.enabled && header.showAddress) || (footer.enabled && footer.showAddress)) result += BrandingField.ADDRESS
        if (slots.any { it.enabled && it.field == TemplateField.PHOTO }) result += BrandingField.PROFILE_PHOTO
        if (slots.any { it.enabled && it.field == TemplateField.NAME }) result += BrandingField.PERSON_NAME
        if (slots.any { it.enabled && it.field == TemplateField.DESIGNATION }) result += BrandingField.DESIGNATION
        return result
    }
    fun asPoster() = Poster(id = id, title = name, category = category, backgroundType = "ready_template",
        backgroundImageRes = TemplateJson.encodeTemplate(this), backgroundColorHex = baseColor)
}

data class BusinessBranding(
    val logo: String = "", val company: String = "", val phone: String = "", val website: String = "",
    val email: String = "", val address: String = "", val tagline: String = "",
    val profilePhoto: String = "", val ownerName: String = "", val ownerDesignation: String = ""
)

fun ProfileSettings.businessBranding() = BusinessBranding(companyLogoUri, companyName, mobileNumber,
    websiteName, businessEmail, businessAddress, tagline,
    leaderImageUri.ifBlank { profilePhotoUri }, userName, designation)

data class PhotoCrop(val scale: Float = 1f, val panX: Float = 0f, val panY: Float = 0f)

/** A complete immutable snapshot; editing/deleting an admin template cannot alter a saved poster. */
data class GeneratedPoster(
    val template: PosterTemplate, val values: Map<String, String> = emptyMap(),
    val photo: String = "",
    /** The durable, unmodified import used when background removal is turned off or fails. */
    val originalPhoto: String = "",
    /** Cached transparent PNG so toggling the option does not rerun segmentation. */
    val backgroundRemovedPhoto: String = "",
    val backgroundRemoved: Boolean = false,
    val crop: PhotoCrop = PhotoCrop(),
    val leaderCrop: PhotoCrop = PhotoCrop(),
    val logoCrop: PhotoCrop = PhotoCrop(),
    val branding: BusinessBranding = BusinessBranding(),
    val schemaVersion: Int = 1
) {
    fun missingFields() = template.requiredFields.filter {
        if (it == TemplateField.PHOTO) photo.isBlank() else values[it.name].isNullOrBlank()
    }
    fun missingBranding(): List<String> = buildList {
        val bands = listOf(template.header, template.footer).filter { it.enabled }
        if (bands.any { it.showLogo } && branding.logo.isBlank()) add("Company logo")
        if (bands.any { it.showCompanyName } && branding.company.isBlank()) add("Company name")
        if (bands.any { it.showPhone } && branding.phone.isBlank()) add("Phone")
        if (bands.any { it.showWebsite } && branding.website.isBlank()) add("Website")
        if (bands.any { it.showEmail } && branding.email.isBlank()) add("Business email")
        if (bands.any { it.showAddress } && branding.address.isBlank()) add("Address")
        if (bands.any { it.showTagline } && branding.tagline.isBlank()) add("Tagline")
    }
}

object TemplateJson {
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val templateAdapter = moshi.adapter(PosterTemplate::class.java)
    private val designAdapter = moshi.adapter(GeneratedPoster::class.java)
    fun encodeTemplate(value: PosterTemplate) = "template_v1:" + templateAdapter.toJson(value)
    fun template(value: String): PosterTemplate? = if (!value.startsWith("template_v1:")) null else
        runCatching { templateAdapter.fromJson(value.removePrefix("template_v1:")) }.getOrNull()
    fun encodeDesign(value: GeneratedPoster) = "generated_v1:" + designAdapter.toJson(value)
    fun design(value: String): GeneratedPoster? = if (!value.startsWith("generated_v1:")) null else
        runCatching { designAdapter.fromJson(value.removePrefix("generated_v1:")) }.getOrNull()
}

fun PosterTemplate.validationError(): String? {
    if (name.isBlank()) return "Enter a template name."
    if (category !in StarterTemplates.categories) return "Choose a supported category."
    if (canvasWidth != 1080 || canvasHeight != 1080) return "Templates must use a 1080 × 1080 canvas."
    if (slots.map { it.field }.distinct().size != slots.size) return "Use only one slot per field."
    val top = if (header.enabled) header.height else 0f
    val bottom = 1080f - if (footer.enabled) footer.height else 0f
    for (band in listOf(header, footer)) {
        if (band.height !in 60f..240f) return "Branding height must be 60–240."
        if (band.enabled && band.showLogo && band.layout == "center" && band.height < 180f) return "Centered logo bands need at least 180 height to keep text readable."
        if (!validHex(band.backgroundColor) || !validHex(band.textColor)) return "Use #RRGGBB colors."
    }
    for (slot in slots.filter { it.enabled }) {
        if (!listOf(slot.x, slot.y, slot.width, slot.height, slot.fontSize, slot.borderWidth).all { it.isFinite() }) return "Invalid slot dimensions."
        if (slot.width < 40f || slot.height < 30f || slot.x < 20f || slot.y < top + 10f ||
            slot.x + slot.width > 1060f || slot.y + slot.height > bottom - 10f) return "${slot.field}: keep the slot inside the body, clear of header and footer."
        if (!validHex(slot.color) || !validHex(slot.borderColor)) return "Use #RRGGBB colors."
        if (slot.field == TemplateField.PHOTO && slot.shape == "circle" && kotlin.math.abs(slot.width-slot.height) > 1f) return "A circle photo slot must have equal width and height."
        if (slot.fontSize !in 12f..160f || slot.maxLines !in 1..8) return "Text size must be 12–160 and lines 1–8."
    }
    return null
}

/** Preserve the body composition when an admin chooses taller branding bands. */
fun PosterTemplate.withBand(value: BrandingBand, isHeader: Boolean): PosterTemplate {
    val oldTop = if(header.enabled) header.height else 0f
    val oldBottom = 1080f - if(footer.enabled) footer.height else 0f
    val next = if(isHeader) copy(header=value) else copy(footer=value)
    val newTop = if(next.header.enabled) next.header.height else 0f
    val newBottom = 1080f - if(next.footer.enabled) next.footer.height else 0f
    val scale = (newBottom-newTop-20)/(oldBottom-oldTop-20)
    fun y(old:Float) = newTop+10+(old-oldTop-10)*scale
    return next.copy(slots=slots.map { slot ->
        val height=slot.height*scale
        if(slot.field==TemplateField.PHOTO && slot.shape=="circle") {
            val side=minOf(slot.width,height)
            slot.copy(x=slot.x+(slot.width-side)/2,y=y(slot.y),width=side,height=side)
        } else slot.copy(y=y(slot.y),height=height)
    },staticTexts=staticTexts.map { it.copy(y=y(it.y),height=it.height*scale,size=it.size*minOf(1f,scale)) })
}

fun validHex(value: String) = Regex("#[0-9a-fA-F]{6}([0-9a-fA-F]{2})?").matches(value)
