package com.example.templates

/**
 * Per-template branding element positioning on the 1080×1350 canvas.
 * All coordinates are in poster-space (1080-wide, 1350-tall).
 * Scale is applied automatically when rendering at different resolutions.
 */
data class BrandingElementConfig(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val fontSize: Float = 24f,
    val maxLines: Int = 1,
    val alignment: String = "start",
    val shape: String = "roundRect",
    val cornerRadius: Float = 14f
)

data class WelcomeBrandingConfig(
    val bandX: Float,
    val bandY: Float,
    val bandWidth: Float,
    val bandHeight: Float,
    val bandRadius: Float = 28f,
    val logo: BrandingElementConfig,
    val brandingPhoto: BrandingElementConfig?,   // null = not shown in this template
    val companyName: BrandingElementConfig,
    val phone: BrandingElementConfig,
    val website: BrandingElementConfig,
    val address: BrandingElementConfig? = null
)

/**
 * Returns per-template branding config for the editable Welcome templates.
 * style: the 0-based style index (0=style -201, 1=style -202, ... 14=style -216)
 * hasLogo: whether user has a logo URI set
 *
 * Canvas is 1080 x 1350.
 * Branding band sits near the bottom.
 */
fun welcomeBrandingConfig(style: Int, hasLogo: Boolean): WelcomeBrandingConfig {
    // Band always starts at y=1115, height=210, spanning near-full width.
    // Left column (logo + company name + website) and right column (phone + address)
    // are separated by a clear gap at x=610.
    val bandTop = 1115f
    val bandH   = 210f
    val logoSz  = 88f
    val logoPad = 40f
    val logoX   = logoPad
    val logoY   = bandTop + (bandH - logoSz) / 2f
    val leftStart = if (hasLogo) logoX + logoSz + 18f else logoPad
    val leftWidth = 570f - leftStart
    val rightX = 640f
    val rightW = 400f
    val rightEnd = rightX + rightW   // = 1040

    return WelcomeBrandingConfig(
        bandX       = 20f,
        bandY       = bandTop,
        bandWidth   = 1040f,
        bandHeight  = bandH,
        bandRadius  = 28f,
        logo        = BrandingElementConfig(x = logoX, y = logoY, width = logoSz, height = logoSz, shape = "roundRect", cornerRadius = 14f),
        brandingPhoto = null,    // editable templates handle branding photo separately
        companyName = BrandingElementConfig(x = leftStart, y = bandTop + 26f, width = leftWidth, height = 52f, fontSize = 26f, maxLines = 1, alignment = "start"),
        website     = BrandingElementConfig(x = leftStart, y = bandTop + 88f, width = leftWidth, height = 36f, fontSize = 17f, maxLines = 1, alignment = "start"),
        phone       = BrandingElementConfig(x = rightX, y = bandTop + 26f, width = rightW, height = 52f, fontSize = 22f, maxLines = 1, alignment = "end"),
        address     = BrandingElementConfig(x = rightX, y = bandTop + 88f, width = rightW, height = 36f, fontSize = 15f, maxLines = 1, alignment = "end")
    )
}
