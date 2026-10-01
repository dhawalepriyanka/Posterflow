package com.example.ui.screens

import com.example.templates.welcomeBrandingConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

internal const val WelcomeCanvasSize = 1080f

@JsonClass(generateAdapter = true)
internal data class WelcomePosterElement(
    val id: String,
    val type: String,
    val role: String = "",
    val text: String = "",
    val imageUri: String = "",
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val rotation: Float = 0f,
    val opacity: Float = 1f,
    val zIndex: Int = 1,
    val locked: Boolean = false,
    val visible: Boolean = true,
    val color: String = "#FFFFFF",
    val paletteRole: String = "",
    val borderColor: String = "#00000000",
    val borderWidth: Float = 0f,
    val cornerRadius: Float = 0f,
    val shape: String = "rectangle",
    val fontSize: Float = 42f,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val alignment: String = "center",
    val letterSpacing: Float = 0f,
    val lineSpacing: Float = 1f,
    val fontFamily: String = "sans-serif",
    val shadow: Boolean = false,
    val cropScale: Float = 1f,
    val cropX: Float = 0f,
    val cropY: Float = 0f,
    val flipHorizontal: Boolean = false
)

@JsonClass(generateAdapter = true)
internal data class WelcomePosterState(
    val templateId: Int,
    val canvasWidth: Int = 1080,
    /** Portrait 4:5 master canvas. Coordinates are in this normalized canvas space. */
    val canvasHeight: Int = 1350,
    val themeId: String = "template",
    val elements: List<WelcomePosterElement> = emptyList()
)

/**
 * Element roles that belong exclusively to global Settings branding.
 * These are NEVER stored in the saved WelcomePosterState and are always
 * re-injected from the live ProfileSettings when the editor opens.
 */
internal val BRANDING_ELEMENT_ROLES = setOf(
    "companyName", "website", "phoneNumber", "address",
    "topBrand",    // top-left / top-right company name labels
    "companyLogo", // logo image element
    "brandingBar"  // the branding band background shape
)

/**
 * Returns a copy of this state with all branding elements removed.
 * Call this before persisting so saved designs never store stale branding.
 */
internal fun WelcomePosterState.stripBrandingElements(): WelcomePosterState =
    copy(elements = elements.filter { it.role !in BRANDING_ELEMENT_ROLES })

/**
 * Returns a copy of this state with fresh branding elements built from the
 * supplied profile values injected at the correct z-layers.
 * Call this when reopening a saved design so it always shows current branding.
 */
internal fun WelcomePosterState.injectBrandingElements(
    companyName: String,
    logoUri: String,
    website: String,
    phone: String,
    address: String
): WelcomePosterState {
    // Remove any stale branding first, then rebuild using the same factory
    val withoutBranding = stripBrandingElements()
    val fresh = newWelcomePosterState(templateId, companyName, logoUri, website, phone, address = address)
    val brandingOnly = fresh.elements.filter { it.role in BRANDING_ELEMENT_ROLES }
    return withoutBranding.copy(elements = withoutBranding.elements + brandingOnly)
}

internal object WelcomeProjectStateStore {
    private const val Prefix = "welcome_editor_v3:"
    private const val LegacyPrefix = "welcome_editor_v2:"
    private val adapter = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        .adapter(WelcomePosterState::class.java)

    /**
     * Encodes a WelcomePosterState for persistence.
     * Branding elements are STRIPPED before encoding so saved designs never
     * contain stale company/phone/website data.
     */
    fun encode(state: WelcomePosterState): String =
        Prefix + adapter.toJson(state.stripBrandingElements())

    /**
     * Decodes a saved WelcomePosterState and immediately injects the current
     * branding from Settings so the editor always shows live values.
     */
    fun decodeWithBranding(
        value: String,
        companyName: String,
        logoUri: String,
        website: String,
        phone: String,
        address: String
    ): WelcomePosterState? = decode(value)?.injectBrandingElements(
        companyName, logoUri, website, phone, address
    )

    /** Raw decode without branding injection — for isProject checks only. */
    fun decode(value: String): WelcomePosterState? = when {
        value.startsWith(Prefix) -> value.removePrefix(Prefix)
        value.startsWith(LegacyPrefix) -> value.removePrefix(LegacyPrefix)
        else -> null
    }?.let { runCatching { adapter.fromJson(it) }.getOrNull() }

    fun isProject(value: String) = value.startsWith(Prefix) || value.startsWith(LegacyPrefix)
}

internal data class WelcomeTheme(
    val id: String,
    val name: String,
    val top: String,
    val bottom: String,
    val accent: String,
    val text: String,
    val panel: String,
    val glow: String = "#A81498",
    val ribbonColor: String = "#D1182A",
    val ribbonBorder: String = "#F6C957",
    val sloganText: String = "मुझ में है दम",
    val hindiQuote: String = "“हमारी ग्रेट टीम में आपका हार्दिक स्वागत है,\nसाथ मिलकर हम नई सफलता की कहानी लिखेंगे।”"
)

internal val welcomeThemes = listOf(
    WelcomeTheme("template", "Template Original", "#1B022B", "#48094C", "#F6C957", "#FFFFFF", "#2C053B", glow = "#A81498", ribbonColor = "#D1182A", ribbonBorder = "#F6C957", sloganText = "मुझ में है दम", hindiQuote = "“हमारी ग्रेट टीम में आपका हार्दिक स्वागत है,\nसाथ मिलकर हम नई सफलता की कहानी लिखेंगे।”"),
    WelcomeTheme("purple_magenta_gold", "Royal Purple & Magenta Gold", "#1B022B", "#48094C", "#F6C957", "#FFFFFF", "#2C053B", glow = "#A81498", ribbonColor = "#D1182A", ribbonBorder = "#F6C957", sloganText = "मुझ में है दम", hindiQuote = "“हमारी ग्रेट टीम में आपका हार्दिक स्वागत है,\nसाथ मिलकर हम नई सफलता की कहानी लिखेंगे।”"),
    WelcomeTheme("midnight_sapphire_gold", "Midnight Sapphire & Gold", "#051329", "#0D2D5B", "#FFD56B", "#FFFFFF", "#081E3D", glow = "#165CB5", ribbonColor = "#C41834", ribbonBorder = "#FFD56B", sloganText = "जीत हमारी तय है", hindiQuote = "“हमारी ग्रेट टीम में आपका हार्दिक स्वागत है,\nसाथ मिलकर हम नई सफलता की कहानी लिखेंगे।”"),
    WelcomeTheme("imperial_emerald_gold", "Imperial Emerald & Gold", "#031E14", "#0A4631", "#F7CD5C", "#FFFFFF", "#063022", glow = "#0EA36F", ribbonColor = "#B81D24", ribbonBorder = "#F7CD5C", sloganText = "लक्ष्य हमारा, सफलता आपकी", hindiQuote = "“सपनों को दें नई उड़ान, आपकी मेहनत लाएगी नया मुकाम।\nहार्दिक स्वागत है!”"),
    WelcomeTheme("obsidian_black_gold", "Obsidian Black & Luxury Gold", "#080808", "#1A150D", "#F3BE44", "#FFFFFF", "#12100C", glow = "#78561C", ribbonColor = "#A81320", ribbonBorder = "#F3BE44", sloganText = "सपनों को दें नई उड़ान", hindiQuote = "“एक नई शुरुआत, सुनहरे भविष्य के साथ।\nहमारी टीम में आपका स्वागत है!”"),
    WelcomeTheme("royal_ruby_gold", "Royal Ruby & Champagne Gold", "#2B030E", "#5E0B22", "#FCE092", "#FFFFFF", "#3B0515", glow = "#B51642", ribbonColor = "#8C0C1B", ribbonBorder = "#FCE092", sloganText = "मुझ में है दम", hindiQuote = "“कामयाबी का यह सफर आपके साथ और भी शानदार होगा।\nहार्दिक स्वागत है!”"),
    WelcomeTheme("ocean_teal_gold", "Ocean Teal & Radiant Gold", "#021A20", "#06424D", "#F9D266", "#FFFFFF", "#042D35", glow = "#0FA1B8", ribbonColor = "#D63418", ribbonBorder = "#F9D266", sloganText = "हम होंगे कामयाब", hindiQuote = "“मजबूत इरादों से मिलकर हम नई ऊंचाइयों को छूएंगे।\nस्वागत है आपका!”"),
    WelcomeTheme("sunset_navy_gold", "Sunset Navy & Vibrant Amber", "#08152B", "#16315C", "#FFB938", "#FFFFFF", "#0E2142", glow = "#FF7B1C", ribbonColor = "#D92525", ribbonBorder = "#FFB938", sloganText = "जीत का संकल्प", hindiQuote = "“नई सोच और नई ऊर्जा के साथ एक नए अध्याय की शुरुआत।\nहार्दिक स्वागत!”"),
    WelcomeTheme("electric_violet_gold", "Electric Violet & Neon Gold", "#140324", "#3B0B5E", "#FFDA73", "#FFFFFF", "#24053B", glow = "#8F1CD6", ribbonColor = "#E81C5C", ribbonBorder = "#FFDA73", sloganText = "मुझ में है दम", hindiQuote = "“हमारी ग्रेट टीम में आपका हार्दिक स्वागत है,\nसाथ मिलकर हम नई सफलता की कहानी लिखेंगे।”"),
    WelcomeTheme("cobalt_blue_gold", "Cobalt Blue & Platinum Gold", "#051634", "#0F326D", "#F0CA65", "#FFFFFF", "#0A214D", glow = "#1F6FE0", ribbonColor = "#C91624", ribbonBorder = "#F0CA65", sloganText = "सफलता का नया सफर", hindiQuote = "“विश्वास और साझेदारी से हम रचेंगे इतिहास।\nहार्दिक अभिनंदन!”"),
    WelcomeTheme("espresso_bronze_gold", "Espresso Bronze & Regal Gold", "#160D08", "#362015", "#F5CA5B", "#FFFFFF", "#24150E", glow = "#8C5029", ribbonColor = "#B8281B", ribbonBorder = "#F5CA5B", sloganText = "श्रेष्ठता की ओर अग्रसर", hindiQuote = "“शानदार व्यक्तित्व और असीम क्षमता का हमारी टीम में स्वागत है!”"),
    WelcomeTheme("grand_maroon_gold", "Grand Maroon & Sparkling Gold", "#240412", "#520E2C", "#FFCE52", "#FFFFFF", "#35071C", glow = "#A61456", ribbonColor = "#C41426", ribbonBorder = "#FFCE52", sloganText = "मुझ में है दम", hindiQuote = "“हमारी ग्रेट टीम में आपका हार्दिक स्वागत है,\nसाथ मिलकर हम नई सफलता की कहानी लिखेंगे।”"),
    WelcomeTheme("midnight_cyber_gold", "Midnight Cyber Gold & Silver", "#080D17", "#171D2D", "#F7D46D", "#FFFFFF", "#0E1422", glow = "#4A618C", ribbonColor = "#D41C2B", ribbonBorder = "#F7D46D", sloganText = "भविष्य हमारा है", hindiQuote = "“आपकी प्रतिभा और लगन हमारी सबसे बड़ी ताकत है।\nस्वागतम!”")
)

internal fun welcomeTheme(id: String, templateId: Int): WelcomeTheme {
    if (id != "template") {
        welcomeThemes.firstOrNull { it.id == id }?.let { return it }
    }
    return when (templateId) {
        -201 -> welcomeThemes[1]
        -202 -> welcomeThemes[2]
        -203 -> welcomeThemes[3]
        -204 -> welcomeThemes[4]
        -205 -> welcomeThemes[5]
        -206 -> welcomeThemes[6]
        -207 -> welcomeThemes[7]
        -208 -> welcomeThemes[8]
        -209 -> welcomeThemes[9]
        -210 -> welcomeThemes[10]
        -211 -> welcomeThemes[11]
        -212 -> welcomeThemes[12]
        // Premium trilogy: choose accent-matching themes
        -214 -> welcomeThemes[1]   // Purple + Gold: purple_magenta_gold
        -215 -> welcomeThemes[2]   // Royal Blue + Gold: midnight_sapphire_gold
        -216 -> welcomeThemes[4]   // Black + Red + Gold: obsidian_black_gold
        -217 -> welcomeThemes[1]   // Purple Glow: purple_magenta_gold
        -218 -> welcomeThemes[4]   // Golden Wave: obsidian_black_gold
        -219 -> welcomeThemes[2]   // Corporate Blue: midnight_sapphire_gold
        -220 -> welcomeThemes[3]   // Botanical Green: imperial_emerald_gold
        -221 -> welcomeThemes[7]   // Dynamic Marathi: sunset_navy_gold
        else -> welcomeThemes[1]
    }
}

private fun e(
    id: String, type: String, x: Float, y: Float, width: Float, height: Float,
    text: String = "", role: String = "", z: Int = 3, color: String = "#FFFFFF",
    paletteRole: String = "", size: Float = 42f, bold: Boolean = false, italic: Boolean = false,
    align: String = "center", shape: String = "rectangle", radius: Float = 0f,
    borderColor: String = "#00000000", borderWidth: Float = 0f, locked: Boolean = false,
    shadow: Boolean = false, opacity: Float = 1f
) = WelcomePosterElement(
    id = id, type = type, role = role, text = text, x = x, y = y, width = width, height = height,
    zIndex = z, color = color, paletteRole = paletteRole, fontSize = size, bold = bold, italic = italic,
    alignment = align, shape = shape, cornerRadius = radius, borderColor = borderColor,
    borderWidth = borderWidth, locked = locked, shadow = shadow, opacity = opacity
)

/**
 * Editable 4:5 counterparts of the twelve ready-made Welcome compositions.  They deliberately
 * share semantic roles, not coordinates: changing a design changes the composition while the
 * user's entered content can be copied into the matching roles by the editor workflow.
 */
private fun distinctWelcomeLayers(
    templateId: Int,
    companyName: String,
    logoUri: String,
    website: String,
    phone: String,
    memberName: String,
    address: String
): List<WelcomePosterElement> {
    val theme = welcomeTheme("template", templateId)
    val style = when (templateId) {
        -201 -> 0; -202 -> 1; -203 -> 2; -204 -> 3; -205 -> 4; -206 -> 5
        -207 -> 6; -208 -> 7; -209 -> 8; -210 -> 9; -211 -> 10; -212 -> 11
        -214 -> 12  // Purple Gold Premium
        -215 -> 13  // Royal Blue Corporate
        -216 -> 14  // Black Red Gold Luxury
        -217 -> 12  // Purple Glow Welcome
        -218 -> 14  // Golden Wave Welcome
        -219 -> 13  // Corporate Blue Wave
        -220 -> 0   // Botanical Green
        -221 -> 13  // Dynamic Marathi
        else -> (-templateId - 201).coerceIn(0, 11)
    }
    val member = memberName.ifBlank { "YOUR NAME" }
    val company = companyName.trim()
    val layers = mutableListOf<WelcomePosterElement>()

    layers += e("backgroundPanel", "shape", 0f, 0f, 1080f, 1350f, role = "background", z = 1, color = theme.bottom, locked = true)
    layers += e("backgroundGlow", "shape", if (style % 2 == 0) 420f else 20f, 120f, 720f, 720f, role = "glow", z = 2, color = theme.glow, shape = "circle", opacity = .42f, locked = true)
    if (company.isNotBlank()) {
        layers += e("topCompanyLeft", "text", 35f, 24f, 330f, 48f, company, "topBrand", 12, theme.text, size = 18f, bold = true, align = "start")
        // Premium templates (12-14) and original duals show company on both sides
        if (style in setOf(0, 2, 4, 7, 10, 12, 14)) {
            layers += e("topCompanyRight", "text", 715f, 24f, 330f, 48f, company, "topBrand", 12, theme.text, size = 18f, bold = true, align = "end")
        }
    }

    val photo = when (style) {
        0 -> floatArrayOf(570f, 150f, 470f, 760f)
        1 -> floatArrayOf(45f, 250f, 455f, 720f)
        2 -> floatArrayOf(315f, 300f, 450f, 450f)
        3 -> floatArrayOf(570f, 190f, 420f, 620f)
        4 -> floatArrayOf(355f, 330f, 370f, 430f)
        5 -> floatArrayOf(45f, 145f, 455f, 790f)
        6 -> floatArrayOf(570f, 145f, 460f, 700f)
        7 -> floatArrayOf(300f, 300f, 480f, 480f)
        8 -> floatArrayOf(565f, 135f, 465f, 755f)
        9 -> floatArrayOf(80f, 350f, 440f, 440f)
        10 -> floatArrayOf(340f, 320f, 400f, 480f)
        // Premium templates: large dominant portraits
        12 -> floatArrayOf(600f, 148f, 448f, 740f)  // Purple Gold: right side
        13 -> floatArrayOf(32f, 155f, 466f, 755f)   // Royal Blue: left side
        14 -> floatArrayOf(598f, 148f, 450f, 740f)  // Black Red: right side
        else -> floatArrayOf(555f, 90f, 490f, 800f)
    }
    val photoShape = when (style) { 2, 4, 7, 9 -> "circle"; 12, 13, 14 -> "roundRect"; else -> "roundRect" }
    layers += e("memberFrame", "shape", photo[0] - 12f, photo[1] - 12f, photo[2] + 24f, photo[3] + 24f, role = "portraitFrame", z = 4, color = "#00000000", shape = photoShape, radius = 44f, borderColor = theme.accent, borderWidth = 5f, locked = true)
    layers += e("memberPhoto", "photo", photo[0], photo[1], photo[2], photo[3], role = "memberPhoto", z = 8, shape = photoShape, radius = 38f)

    val titleBox = when (style) {
        0, 6, 8 -> floatArrayOf(35f, 120f, 520f, 130f)
        1, 5 -> floatArrayOf(545f, 135f, 490f, 125f)
        2, 4, 7, 9, 10 -> floatArrayOf(100f, 115f, 880f, 125f)
        3, 11 -> floatArrayOf(50f, 145f, 500f, 125f)
        12, 14 -> floatArrayOf(22f, 118f, 560f, 147f)  // Purple Gold / Black Red: large left
        13 -> floatArrayOf(505f, 140f, 540f, 130f)      // Royal Blue: headline right
        else -> floatArrayOf(70f, 130f, 850f, 125f)
    }
    layers += e("welcomeHeadline", "text", titleBox[0], titleBox[1], titleBox[2], titleBox[3], "WELCOME", "welcomeHeadline", 12, theme.accent, size = if (style in setOf(2, 4, 7, 9, 10)) 88f else if (style in setOf(12, 14)) 102f else 78f, bold = true, align = if (style in setOf(2, 4, 7, 9, 10, 13)) "center" else "start", shadow = style != 11)

    val subtitle = when (style) {
        3 -> "YOU ARE WELCOME HERE"
        5 -> "GROW WITH PURPOSE"
        6 -> "SUCCESS STARTS TOGETHER"
        9 -> "WITH WARMTH AND JOY"
        10 -> "हार्दिक स्वागत"
        11 -> "TO THE NEXT CHAPTER"
        12 -> "TO"
        13 -> "TO OUR TEAM"
        14 -> "TO OUR FAMILY"
        else -> "TO OUR TEAM"
    }
    layers += e("toSubheading", "text", titleBox[0] + 5f, titleBox[1] + titleBox[3], titleBox[2], 48f, subtitle, "subtitle", 12, theme.text, size = 24f, bold = true, align = if (style in setOf(2, 4, 7, 9, 10, 13)) "center" else "start")

    val campaignCopy = listOf(
        "IN OUR GREAT" to "PLATFORM",
        "JOIN OUR" to "GREAT TEAM",
        "GROW WITH" to "THE BEST",
        "CREATE. LEAD." to "SUCCEED TOGETHER",
        "DREAM BIG" to "WIN TOGETHER",
        "BUILD THE" to "FUTURE",
        "YOUR NEXT" to "BIG WIN",
        "PREMIUM PEOPLE" to "PREMIUM FUTURE",
        "FRESH IDEAS" to "BOLD VISION",
        "BLOOM WITH" to "POSSIBILITY",
        "A PROUD" to "NEW STORY",
        "GREAT PEOPLE" to "GREAT FUTURE",
        // Premium trilogy campaign copy
        "IN OUR GREAT" to "FAMILY",       // style 12: Purple Gold
        "JOIN OUR" to "GREAT TEAM",       // style 13: Royal Blue
        "DREAM BIG" to "WIN TOGETHER"     // style 14: Black Red
    )[style]
    val campaignBox = when (style) {
        0, 6, 8 -> floatArrayOf(35f, 300f, 520f, 54f, 35f, 355f, 520f, 68f)
        1, 5 -> floatArrayOf(535f, 300f, 505f, 54f, 535f, 355f, 505f, 68f)
        2, 4, 7, 10 -> floatArrayOf(135f, 1055f, 810f, 38f, 135f, 1090f, 810f, 38f)
        3, 11 -> floatArrayOf(45f, 330f, 475f, 54f, 45f, 385f, 475f, 66f)
        9 -> floatArrayOf(535f, 330f, 500f, 54f, 535f, 385f, 500f, 66f)
        12, 14 -> floatArrayOf(30f, 478f, 540f, 62f, 22f, 540f, 560f, 80f)  // Left side
        13 -> floatArrayOf(508f, 475f, 527f, 62f, 502f, 540f, 538f, 76f)    // Right side
        else -> floatArrayOf(45f, 300f, 480f, 62f, 45f, 360f, 480f, 72f)
    }
    val compactCampaign = style in setOf(2, 4, 7, 10)
    val campaignTopSize = if (compactCampaign) 27f else if (style in setOf(12, 14)) 55f else if (style == 13) 54f else if (campaignCopy.first.length > 13) 37f else 45f
    val campaignBottomSize = when {
        compactCampaign -> 29f
        style in setOf(12, 14) -> 78f
        style == 13 -> 65f
        campaignCopy.second.length > 15 -> 38f
        campaignCopy.second.length > 12 -> 45f
        else -> 55f
    }
    layers += e(
        "campaignTop", "text", campaignBox[0], campaignBox[1], campaignBox[2], campaignBox[3],
        campaignCopy.first, "campaignTop", 12, theme.accent,
        size = campaignTopSize, bold = true, shadow = true
    )
    layers += e(
        "campaignBottom", "text", campaignBox[4], campaignBox[5], campaignBox[6], campaignBox[7],
        campaignCopy.second, "campaignBottom", 12, theme.accent,
        size = campaignBottomSize, bold = true, shadow = true
    )

    val ribbonBox = when (style) {
        0, 6, 8 -> floatArrayOf(20f, 455f, 560f, 145f)
        1, 5 -> floatArrayOf(530f, 455f, 530f, 145f)
        2, 4, 7, 10 -> floatArrayOf(120f, 790f, 840f, 145f)
        3, 9, 11 -> floatArrayOf(525f, 840f, 520f, 145f)
        12 -> floatArrayOf(14f, 322f, 576f, 143f)   // Left ribbon for Purple Gold
        13 -> floatArrayOf(472f, 328f, 590f, 130f)      // Right ribbon for Royal Blue
        14 -> floatArrayOf(14f, 330f, 576f, 143f)       // Left ribbon for Black Red (offset from 12)
        else -> floatArrayOf(45f, 430f, 500f, 145f)
    }
    // Premium templates use white ribbon (12=Purple Gold) or gold (13=Blue) or red (14=Black Red)
    val ribbonFillColor = when (style) {
        12 -> "#FFFFFF"
        13 -> theme.accent
        14 -> theme.ribbonColor
        else -> if (style in setOf(0, 1, 2, 4, 6, 8, 10)) "#FFFFFF" else theme.ribbonColor
    }
    layers += e("nameRibbon", "shape", ribbonBox[0], ribbonBox[1], ribbonBox[2], ribbonBox[3], role = "nameRibbon", z = 10, color = ribbonFillColor, shape = "ribbon", borderColor = theme.accent, borderWidth = 4f)
    val ribbonTextColor = when (style) {
        12 -> "#1A0030"
        13 -> "#040F1E"
        14 -> "#FFFFFF"
        else -> if (style in setOf(0, 1, 2, 4, 6, 8, 10)) "#151018" else "#FFFFFF"
    }
    layers += e("memberName", "text", ribbonBox[0] + 40f, ribbonBox[1] + 18f, ribbonBox[2] - 80f, 52f, member, "memberName", 12, ribbonTextColor, size = 34f, bold = true)
    layers += e("designation", "text", ribbonBox[0] + 50f, ribbonBox[1] + 76f, ribbonBox[2] - 100f, 34f, "YOUR ROLE", "memberDesignation", 12, if (ribbonTextColor == "#FFFFFF") theme.accent else "#5A3A18", size = 18f, bold = true)

    val messageBox = when (style) {
        0, 6, 8 -> floatArrayOf(45f, 640f, 490f, 165f)
        1, 5 -> floatArrayOf(545f, 640f, 480f, 160f)
        2, 4, 7, 10 -> floatArrayOf(145f, 965f, 790f, 105f)
        3, 11 -> floatArrayOf(55f, 570f, 460f, 170f)
        9 -> floatArrayOf(560f, 575f, 455f, 165f)
        12 -> floatArrayOf(550f, 840f, 490f, 80f)   // Purple Gold: below portrait right
        13 -> floatArrayOf(528f, 685f, 487f, 110f)  // Royal Blue: message right
        14 -> floatArrayOf(50f, 730f, 520f, 95f)    // Black Red: message left
        else -> floatArrayOf(55f, 625f, 470f, 160f)
    }
    layers += e("welcomeMessage", "text", messageBox[0], messageBox[1], messageBox[2], messageBox[3], "We are delighted to welcome you.\nTogether, we will achieve remarkable things.", "welcomeMessage", 11, theme.text, size = 23f, align = "start")

    // Supporting portraits: optional secondary photo (bottom-left) for premium templates and a few originals.
    // A blank image URI leaves no placeholder artwork behind.
    if (style in setOf(0, 2, 8, 12, 14)) {
        val supX = when (style) { 2 -> 190f; 12, 14 -> 28f; else -> 480f }
        val supY = when (style) { 2 -> 385f; 12, 14 -> 880f; else -> 820f }
        val supSize = if (style in setOf(12, 14)) 135f else 125f
        layers += e("supportPhoto", "photo", supX, supY, supSize, supSize, role = "supportPhoto", z = 9, shape = "circle", borderColor = theme.accent, borderWidth = 4f)
    }

    // --- Branding Band (template-config driven, collision-free) ---
    val bc = welcomeBrandingConfig(style, logoUri.isNotBlank())
    layers += e("brandingBand", "shape", bc.bandX, bc.bandY, bc.bandWidth, bc.bandHeight,
        role = "brandingBar", z = 5, color = theme.panel, shape = "roundRect",
        radius = bc.bandRadius, borderColor = theme.accent, borderWidth = 2f, locked = true)
    // Logo (only when user has a logo URI)
    if (logoUri.isNotBlank()) {
        val lc = bc.logo
        layers += e("companyLogo", "logo", lc.x, lc.y, lc.width, lc.height,
            role = "companyLogo", z = 10, shape = lc.shape, radius = lc.cornerRadius)
            .copy(imageUri = logoUri)
    }
    // Company name (left column, auto-sizes in renderer, hidden when blank)
    if (company.isNotBlank()) {
        val cc = bc.companyName
        layers += e("companyName", "text", cc.x, cc.y, cc.width, cc.height,
            company, "companyName", 11, theme.text, size = cc.fontSize, bold = true, align = cc.alignment)
    }
    // Website (left column row 2, only shown when user has set a website)
    if (website.isNotBlank()) {
        val wc = bc.website
        layers += e("website", "text", wc.x, wc.y, wc.width, wc.height,
            website, "website", 11, theme.accent, size = wc.fontSize, align = wc.alignment)
    }
    // Phone number (right column, only shown when user has set a phone)
    if (phone.isNotBlank()) {
        val pc = bc.phone
        layers += e("phoneNumber", "text", pc.x, pc.y, pc.width, pc.height,
            phone, "phoneNumber", 11, theme.text, size = pc.fontSize, bold = true, align = pc.alignment)
    }
    // Address (right column row 2, only shown when set)
    if (address.isNotBlank()) {
        val ac = bc.address
        if (ac != null) {
            layers += e("address", "text", ac.x, ac.y, ac.width, ac.height,
                address, "address", 11, theme.accent, size = ac.fontSize, align = ac.alignment)
        }
    }
    return layers
}

internal fun newWelcomePosterState(
    templateId: Int,
    companyName: String,
    logoUri: String,
    website: String,
    phone: String,
    memberName: String = "NEW MEMBER NAME",
    address: String = ""
): WelcomePosterState {
    return WelcomePosterState(
        templateId = templateId,
        elements = distinctWelcomeLayers(templateId, companyName, logoUri, website, phone, memberName, address)
    )
}

internal fun WelcomePosterState.element(id: String) = elements.firstOrNull { it.id == id }
internal fun WelcomePosterState.updateElement(id: String, update: (WelcomePosterElement) -> WelcomePosterElement) =
    copy(elements = elements.map { if (it.id == id) update(it) else it })
