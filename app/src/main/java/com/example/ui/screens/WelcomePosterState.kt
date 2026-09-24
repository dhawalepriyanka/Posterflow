package com.example.ui.screens

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

internal object WelcomeProjectStateStore {
    private const val Prefix = "welcome_editor_v3:"
    private const val LegacyPrefix = "welcome_editor_v2:"
    private val adapter = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        .adapter(WelcomePosterState::class.java)

    fun encode(state: WelcomePosterState): String = Prefix + adapter.toJson(state)
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
 * Generates the full MLM Welcome poster layers inspired by the reference image.
 * All 12 templates share this high-production hierarchy while taking on distinct themes.
 */
private fun referenceWelcomeLayers(
    templateId: Int,
    companyName: String,
    logoUri: String,
    website: String,
    phone: String,
    memberName: String,
    address: String
): List<WelcomePosterElement> {
    val theme = welcomeTheme("template", templateId)
    val member = memberName.ifBlank { "NEW TEAM MEMBER" }
    val comp = companyName.ifBlank { "YOUR COMPANY" }
    val layers = mutableListOf<WelcomePosterElement>()

    // 1. Master Background Panel & Glow
    layers += e("backgroundPanel", "shape", 0f, 0f, 1080f, 1350f, role = "background", z = 1, color = theme.panel, locked = true)
    layers += e("backgroundGlow", "shape", 380f, 220f, 680f, 680f, role = "glow", z = 2, color = theme.glow, shape = "circle", opacity = 0.38f, locked = true)

    // 2. Top Header Brand & Leadership Band
    layers += e("topBrandBand", "shape", 20f, 20f, 1040f, 135f, role = "shape", z = 3, color = "#00000000", shape = "roundRect", radius = 24f, borderColor = theme.accent, borderWidth = 2f, locked = true)

    // Left Company Logo & Name
    layers += e("companyLogo", "logo", 34f, 30f, 105f, 90f, role = "companyLogo", z = 10, shape = "roundRect", radius = 14f, borderColor = theme.accent, borderWidth = 2.5f).copy(imageUri = logoUri)
    layers += e("companyName", "text", 145f, 36f, 260f, 40f, comp, "companyName", 11, theme.text, size = 20f, bold = true, align = "start")
    layers += e("companyTagline", "text", 147f, 78f, 260f, 24f, "Excellence & Growth", "companyTagline", 11, theme.accent, size = 10f, bold = true, align = "start")

    // 3 Leadership Circles at Top Center
    listOf(0, 1, 2).forEach { index ->
        val x = 420f + index * 115f
        layers += e("leaderPhoto${index + 1}", "photo", x, 28f, 96f, 96f, role = "leaderPhoto${index + 1}", z = 11, shape = "circle", borderColor = theme.accent, borderWidth = 5f)
        layers += e("leaderName${index + 1}", "text", x - 10f, 126f, 116f, 18f, "LEADER ${index + 1}", "leaderName${index + 1}", 11, theme.text, size = 11f, bold = true)
    }

    // Right Company Logo
    layers += e("companyLogoRight", "logo", 940f, 30f, 105f, 90f, role = "companyLogoRight", z = 10, shape = "roundRect", radius = 14f, borderColor = theme.accent, borderWidth = 2.5f).copy(imageUri = logoUri)

    // 3. Main 3D Gold Welcome Headings
    layers += e("welcomeHeadline", "text", 25f, 160f, 570f, 140f, "WELCOME", "welcomeHeadline", 12, theme.accent, size = 96f, bold = true, align = "start", shadow = true)
    layers += e("toSubheading", "text", 210f, 282f, 160f, 48f, "To", "toSubheading", 12, theme.accent, size = 42f, bold = true, italic = true)

    // 4. Member Nameplate White Banner Ribbon
    layers += e("nameRibbon", "shape", 12f, 345f, 600f, 122f, role = "decorativeRibbon", z = 10, color = "#FFFFFF", shape = "ribbon", borderColor = theme.accent, borderWidth = 4f, locked = true)
    layers += e("memberName", "text", 45f, 366f, 535f, 44f, member, "memberName", 12, "#111111", size = 32f, bold = true)
    layers += e("designation", "text", 65f, 412f, 495f, 30f, "SOFTWARE DEVELOPERS", "memberDesignation", 12, "#242424", size = 18f, bold = true)

    // 5. 3D Gold Platform Heading
    layers += e("platformLine", "text", 30f, 485f, 555f, 140f, "IN OUR GREAT\nPLATFORM", "welcomeMessage", 12, theme.accent, size = 52f, bold = true, align = "start", shadow = true)

    // 6. Right Side Welcomed Member Photo
    layers += e("memberFrame", "shape", 585f, 175f, 470f, 580f, role = "shape", z = 3, color = "#00000000", shape = "roundRect", radius = 48f, borderColor = theme.accent, borderWidth = 6f, locked = true)
    layers += e("memberPhoto", "photo", 600f, 190f, 440f, 550f, role = "memberPhoto", z = 9, shape = "roundRect", radius = 40f)

    // 7. Slogan Ribbon under Member Photo (Red Slogan Ribbon)
    layers += e("decorativeRibbon", "shape", 565f, 765f, 495f, 76f, role = "decorativeRibbon", z = 11, color = theme.ribbonColor, shape = "ribbon", borderColor = theme.ribbonBorder, borderWidth = 3f, locked = true)
    layers += e("sloganText", "text", 585f, 782f, 455f, 42f, theme.sloganText, "sloganText", 12, "#FFFFFF", size = 26f, bold = true)

    // 8. Quote Marks and Inspiring Hindi Quote
    layers += e("quoteMarks", "text", 760f, 845f, 80f, 48f, "””", "quoteMarks", 12, theme.accent, size = 52f, bold = true)
    layers += e("quote", "text", 505f, 895f, 555f, 92f, theme.hindiQuote, "quote", 12, theme.text, size = 19f, bold = true, align = "center")

    // 9. Host / Team Leader Section (Bottom Left)
    layers += e("hostHalo", "shape", 18f, 885f, 292f, 312f, role = "shape", z = 4, color = "#00000000", shape = "roundRect", radius = 38f, borderColor = theme.accent, borderWidth = 4f, locked = true)
    layers += e("hostPhoto", "photo", 30f, 897f, 268f, 288f, role = "hostPhoto", z = 9, shape = "roundRect", radius = 30f, borderColor = theme.accent, borderWidth = 3f)
    layers += e("hostName", "text", 335f, 920f, 420f, 46f, "TEAM LEADER", "hostName", 12, theme.text, size = 32f, bold = true, align = "start")
    layers += e("hostDesignation", "text", 338f, 968f, 420f, 32f, "SENIOR PARTNER", "hostDesignation", 12, theme.accent, size = 18f, bold = true, align = "start")
    layers += e("hostCompany", "text", 338f, 1002f, 420f, 28f, comp, "hostCompany", 12, theme.text, size = 17f, align = "start")
    layers += e("trophyIcon", "icon", 400f, 1045f, 80f, 80f, "🏆", "trophyIcon", 12, theme.accent, size = 58f, bold = true)
    layers += e("companyLogoBottom", "logo", 335f, 1045f, 68f, 68f, role = "companyLogo", z = 10, shape = "roundRect", radius = 12f, borderColor = theme.accent, borderWidth = 2f).copy(imageUri = logoUri)

    // 10. Bottom White Curve & Contact CTA Pill
    layers += e("bottomCurve", "shape", 270f, 1135f, 810f, 215f, role = "shape", z = 6, color = "#FFFFFF", shape = "roundRect", radius = 80f, locked = true)
    layers += e("contactPanel", "shape", 705f, 1175f, 355f, 90f, role = "contactInformation", z = 8, color = "#0A0D14", shape = "roundRect", radius = 45f, borderColor = theme.accent, borderWidth = 2.5f, locked = true)
    layers += e("phoneNumber", "text", 720f, 1187f, 325f, 26f, "FOR SUCCESS CALL ON", "phoneNumber", 12, theme.accent, size = 14f, bold = true, align = "center")
    layers += e("whatsAppNumber", "text", 720f, 1215f, 325f, 40f, phone.ifBlank { "+91 XXXXX XXXXX" }, "WhatsAppNumber", 12, "#FFFFFF", size = 26f, bold = true, align = "center")
    layers += e("website", "text", 320f, 1285f, 420f, 28f, website.ifBlank { "www.yourcompany.com" }, "website", 12, "#1A1A1A", size = 16f, bold = true, align = "start")
    layers += e("address", "text", 715f, 1285f, 340f, 28f, address.ifBlank { "Maharashtra, India" }, "address", 12, "#1A1A1A", size = 15f, align = "end")
    layers += e("successTagline", "text", 20f, 1315f, 1040f, 28f, "SAME VISION  |  SAME TEAM  |  BRIGHTER TOMORROW", "successTagline", 12, theme.accent, size = 16f, bold = true, align = "center")

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
        elements = referenceWelcomeLayers(templateId, companyName, logoUri, website, phone, memberName, address)
    )
}

internal fun WelcomePosterState.element(id: String) = elements.firstOrNull { it.id == id }
internal fun WelcomePosterState.updateElement(id: String, update: (WelcomePosterElement) -> WelcomePosterElement) =
    copy(elements = elements.map { if (it.id == id) update(it) else it })
