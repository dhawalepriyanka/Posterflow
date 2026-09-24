package com.example.templates

/**
 * Original, bundled professional starter definitions.
 * Admin-created templates are stored as data in the local repository.
 */
object StarterTemplates {
    val categories = listOf("Welcome", "Birthday", "Achievement", "Income", "Festival", "Motivation", "Business", "Good Morning", "Good Night", "Anniversary", "Offers", "Events", "Political", "Real Estate", "Restaurant", "Healthcare", "Education", "Job Vacancy", "Quotes", "Devotional")

    val all: List<PosterTemplate> by lazy {
        buildList {
            // WELCOME (12 original professional templates)
            addAll(welcomeTemplates)
            // BIRTHDAY (8 professional templates)
            addAll(birthdayTemplates)
            // ACHIEVEMENT (6 professional templates)
            addAll(achievementTemplates)
            // INCOME (6 professional recognition templates)
            addAll(incomeTemplates)
            // FESTIVAL (8 rich cultural festival templates)
            addAll(festivalTemplates)
            // MOTIVATION (9 professional motivational templates: pinnacle leadership + split person-photo + quote)
            addAll(motivationTemplates)
            // BUSINESS (3 professional business & service promotion templates)
            addAll(businessTemplates)
            // GOOD MORNING (3 warm sunrise & daily greeting templates)
            addAll(goodMorningTemplates)
            // ANNIVERSARY (3 elegant celebration & milestone templates)
            addAll(anniversaryTemplates)
            // OFFERS (2 promotional discount & sales templates)
            addAll(offersTemplates)
            // EVENTS (2 conference & seminar templates)
            addAll(eventsTemplates)
            // GOOD NIGHT (3 calming evening greeting templates)
            addAll(goodNightTemplates)
            // POLITICAL (3 campaign & leadership templates)
            addAll(politicalTemplates)
            // REAL ESTATE (3 property promotion templates)
            addAll(realEstateTemplates)
            // RESTAURANT (3 food & dining promotion templates)
            addAll(restaurantTemplates)
            // HEALTHCARE (3 doctor & clinic templates)
            addAll(healthcareTemplates)
            // EDUCATION (3 institute & course templates)
            addAll(educationTemplates)
            // JOB VACANCY (3 hiring & recruitment templates)
            addAll(jobVacancyTemplates)
            // QUOTES (3 inspirational quote templates)
            addAll(quotesTemplates)
            // DEVOTIONAL (3 spiritual & devotional templates)
            addAll(devotionalTemplates)
        }
    }

    /* ---------------------------------------------------------------------- */
    /* 1. WELCOME TEMPLATES (10 distinct layouts)                             */
    /* ---------------------------------------------------------------------- */
    private val welcomeTemplates = listOf(
        // 01 — ROYAL GOLD WELCOME: Black/deep-purple, gold concentric rings, confetti particles, gold name ribbon
        PosterTemplate(
            id = -201, name = "Royal Gold Welcome", category = "Welcome",
            baseColor = "#0A0520", accentColor = "#F5C542", style = 130, order = 0, photoPosition = "right",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#F8F4E8", textColor = "#1A0F30", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("✦ WELCOME ✦", 40f, 35f, 500f, 80f, size = 56f, color = "#F5C542", alignment = "left"),
                StaticText("TO OUR TEAM", 40f, 115f, 500f, 50f, size = 32f, color = "#FFFFFF", alignment = "left", bold = false),
                StaticText("We are thrilled to have you join us.\nYour journey to greatness begins here.", 40f, 540f, 500f, 90f, size = 20f, color = "#D4C8A0", alignment = "left", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 540f, y = 40f, width = 500f, height = 560f, shape = "rounded", borderColor = "#F5C542", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 40f, y = 660f, width = 1000f, height = 65f, fontSize = 46f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 40f, y = 730f, width = 1000f, height = 45f, fontSize = 26f, color = "#F5C542", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 40f, y = 790f, width = 1000f, height = 60f, fontSize = 20f, color = "#E0D8C0", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 02 — CORPORATE BLUE WELCOME: Navy/royal-blue, diagonal split, geometric lines, guest right
        PosterTemplate(
            id = -202, name = "Corporate Blue Welcome", category = "Welcome",
            baseColor = "#0A1A3A", accentColor = "#3B82F6", style = 131, order = 1, photoPosition = "right",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#0D2451", textColor = "#FFFFFF", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WELCOME", 50f, 50f, 480f, 85f, size = 68f, color = "#3B82F6", alignment = "left"),
                StaticText("TO THE TEAM", 50f, 135f, 480f, 55f, size = 38f, color = "#FFFFFF", alignment = "left"),
                StaticText("Innovation • Integrity • Impact", 50f, 210f, 480f, 40f, size = 20f, color = "#93C5FD", alignment = "left", bold = false),
                StaticText("Your expertise strengthens our mission.\nTogether, we build excellence.", 50f, 520f, 470f, 80f, size = 20f, color = "#BFDBFE", alignment = "left", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 560f, y = 40f, width = 480f, height = 560f, shape = "rounded", borderColor = "#3B82F6", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 50f, y = 640f, width = 980f, height = 60f, fontSize = 44f, color = "#FFFFFF", alignment = "left"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 50f, y = 705f, width = 980f, height = 45f, fontSize = 26f, color = "#60A5FA", alignment = "left"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 50f, y = 775f, width = 980f, height = 60f, fontSize = 20f, color = "#DBEAFE", alignment = "left", bold = false, maxLines = 2)
            )
        ),
        // 03 — PREMIUM PURPLE WELCOME: Purple/magenta/dark-blue, metallic gold title, central composition
        PosterTemplate(
            id = -203, name = "Premium Purple Welcome", category = "Welcome",
            baseColor = "#1A0535", accentColor = "#D4A843", style = 132, order = 2, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#120328", textColor = "#E8D5A3", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WELCOME", 80f, 35f, 920f, 90f, size = 72f, color = "#D4A843", alignment = "center"),
                StaticText("TO THE FAMILY", 80f, 125f, 920f, 45f, size = 30f, color = "#E8B4F8", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 300f, y = 190f, width = 480f, height = 420f, shape = "rounded", borderColor = "#D4A843", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 60f, y = 640f, width = 960f, height = 60f, fontSize = 46f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 60f, y = 710f, width = 960f, height = 45f, fontSize = 26f, color = "#D4A843", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 80f, y = 780f, width = 920f, height = 60f, fontSize = 20f, color = "#D8C8E8", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 04 — ELEGANT WHITE AND GOLD WELCOME: Cream/white bg, gold borders, serif-style, circular portrait
        PosterTemplate(
            id = -204, name = "Elegant White & Gold Welcome", category = "Welcome",
            baseColor = "#FAF6ED", accentColor = "#B8860B", style = 133, order = 3, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#FFFFFF", textColor = "#3D2B1F", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("— WELCOME —", 80f, 40f, 920f, 75f, size = 52f, color = "#B8860B", alignment = "center"),
                StaticText("We are honoured to have you with us", 100f, 120f, 880f, 40f, size = 22f, color = "#6B5B3E", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 185f, width = 400f, height = 400f, shape = "circle", borderColor = "#B8860B", borderWidth = 6f),
                TemplateSlot(TemplateField.NAME, required = true, x = 60f, y = 620f, width = 960f, height = 60f, fontSize = 44f, color = "#2C1810", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 60f, y = 690f, width = 960f, height = 45f, fontSize = 24f, color = "#B8860B", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 80f, y = 760f, width = 920f, height = 70f, fontSize = 20f, color = "#5C4A3A", alignment = "center", bold = false, maxLines = 3)
            )
        ),
        // 05 — RED CELEBRATION WELCOME: Red/maroon gradients, gold confetti, ribbon, guest cutout
        PosterTemplate(
            id = -205, name = "Red Celebration Welcome", category = "Welcome",
            baseColor = "#3D0C0C", accentColor = "#F5C542", style = 134, order = 4, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#FFF5F5", textColor = "#5C1010", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WELCOME", 80f, 35f, 920f, 90f, size = 72f, color = "#F5C542", alignment = "center"),
                StaticText("A CELEBRATION OF NEW BEGINNINGS", 80f, 130f, 920f, 40f, size = 22f, color = "#FCA5A5", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 290f, y = 190f, width = 500f, height = 440f, shape = "rounded", borderColor = "#F5C542", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 40f, y = 660f, width = 1000f, height = 60f, fontSize = 46f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 40f, y = 730f, width = 1000f, height = 45f, fontSize = 26f, color = "#F5C542", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 60f, y = 790f, width = 960f, height = 60f, fontSize = 20f, color = "#FEE2E2", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 06 — MODERN GREEN WELCOME: Emerald/dark-green, curved shapes, professional portrait
        PosterTemplate(
            id = -206, name = "Modern Green Welcome", category = "Welcome",
            baseColor = "#062B20", accentColor = "#34D399", style = 135, order = 5, photoPosition = "left",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#F0FDF9", textColor = "#064E3B", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WELCOME", 530f, 50f, 510f, 85f, size = 64f, color = "#34D399", alignment = "left"),
                StaticText("ABOARD", 530f, 135f, 510f, 55f, size = 40f, color = "#FFFFFF", alignment = "left"),
                StaticText("Growth • Excellence • Success", 530f, 210f, 510f, 40f, size = 20f, color = "#6EE7B7", alignment = "left", bold = false),
                StaticText("Your vision and leadership will\nhelp us reach new heights.", 530f, 500f, 500f, 80f, size = 20f, color = "#A7F3D0", alignment = "left", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 40f, y = 40f, width = 470f, height = 560f, shape = "rounded", borderColor = "#34D399", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 40f, y = 640f, width = 1000f, height = 60f, fontSize = 44f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 40f, y = 710f, width = 1000f, height = 45f, fontSize = 26f, color = "#34D399", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 60f, y = 780f, width = 960f, height = 60f, fontSize = 20f, color = "#D1FAE5", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 07 — ORANGE SUCCESS WELCOME: Orange/black/gold, achievement theme, trophy outline, bold
        PosterTemplate(
            id = -207, name = "Orange Success Welcome", category = "Welcome",
            baseColor = "#1A0A00", accentColor = "#F97316", style = 136, order = 6, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#1C1004", textColor = "#FDBA74", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WELCOME", 80f, 35f, 920f, 90f, size = 72f, color = "#F97316", alignment = "center"),
                StaticText("SUCCESS BEGINS HERE", 80f, 130f, 920f, 40f, size = 24f, color = "#FED7AA", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 300f, y = 195f, width = 480f, height = 430f, shape = "rounded", borderColor = "#F97316", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 40f, y = 660f, width = 1000f, height = 60f, fontSize = 46f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 40f, y = 730f, width = 1000f, height = 45f, fontSize = 26f, color = "#F97316", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 60f, y = 790f, width = 960f, height = 60f, fontSize = 20f, color = "#FFEDD5", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 08 — LUXURY BLACK WELCOME: Black bg, gold double borders, dramatic lighting, premium type
        PosterTemplate(
            id = -208, name = "Luxury Black Welcome", category = "Welcome",
            baseColor = "#080808", accentColor = "#D4AF37", style = 137, order = 7, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#0F0F0F", textColor = "#D4AF37", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WELCOME", 80f, 40f, 920f, 85f, size = 70f, color = "#D4AF37", alignment = "center"),
                StaticText("TO EXCELLENCE", 80f, 130f, 920f, 45f, size = 30f, color = "#FFFFFF", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 240f, y = 200f, width = 600f, height = 420f, shape = "rounded", borderColor = "#D4AF37", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 40f, y = 655f, width = 1000f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 40f, y = 728f, width = 1000f, height = 45f, fontSize = 26f, color = "#D4AF37", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 60f, y = 790f, width = 960f, height = 60f, fontSize = 20f, color = "#C8B88A", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 09 — FRESH GRADIENT WELCOME: Blue/cyan/violet gradient, glass panels, full-height cutout
        PosterTemplate(
            id = -209, name = "Fresh Gradient Welcome", category = "Welcome",
            baseColor = "#0F0A2A", accentColor = "#06B6D4", style = 138, order = 8, photoPosition = "left",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#0A0520CC", textColor = "#E0F2FE", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WELCOME", 520f, 50f, 520f, 85f, size = 64f, color = "#22D3EE", alignment = "left"),
                StaticText("ON BOARD", 520f, 140f, 520f, 50f, size = 36f, color = "#FFFFFF", alignment = "left"),
                StaticText("Fresh ideas, bold vision.\nLet's create the future together.", 520f, 460f, 500f, 80f, size = 20f, color = "#A5F3FC", alignment = "left", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 40f, y = 40f, width = 460f, height = 560f, shape = "rounded", borderColor = "#06B6D4", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 40f, y = 640f, width = 1000f, height = 60f, fontSize = 44f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 40f, y = 710f, width = 1000f, height = 45f, fontSize = 26f, color = "#22D3EE", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 60f, y = 780f, width = 960f, height = 60f, fontSize = 20f, color = "#CFFAFE", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 10 — FLORAL WELCOME: Soft pastel pink/lavender, floral corners, elegant name frame
        PosterTemplate(
            id = -210, name = "Floral Welcome", category = "Welcome",
            baseColor = "#FDF2F8", accentColor = "#DB2777", style = 139, order = 9, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#FFFFFF", textColor = "#831843", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("Welcome", 80f, 35f, 920f, 80f, size = 58f, color = "#DB2777", alignment = "center"),
                StaticText("With warmth and joy", 80f, 118f, 920f, 40f, size = 22f, color = "#9D174D", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 310f, y = 185f, width = 460f, height = 420f, shape = "rounded", borderColor = "#F472B6", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 60f, y = 640f, width = 960f, height = 60f, fontSize = 44f, color = "#831843", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 60f, y = 710f, width = 960f, height = 45f, fontSize = 24f, color = "#DB2777", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 80f, y = 775f, width = 920f, height = 65f, fontSize = 20f, color = "#9D174D", alignment = "center", bold = false, maxLines = 3)
            )
        ),
        // 11 — INDIAN TRADITIONAL WELCOME: Maroon/saffron/gold, traditional motifs, decorative heading
        PosterTemplate(
            id = -211, name = "Indian Traditional Welcome", category = "Welcome",
            baseColor = "#3D0A0A", accentColor = "#F59E0B", style = 140, order = 10, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#FFF7ED", textColor = "#7C2D12", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("स्वागत", 80f, 35f, 920f, 85f, size = 68f, color = "#F59E0B", alignment = "center"),
                StaticText("WELCOME TO THE FAMILY", 80f, 125f, 920f, 40f, size = 24f, color = "#FCD34D", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 310f, y = 190f, width = 460f, height = 420f, shape = "rounded", borderColor = "#F59E0B", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 40f, y = 645f, width = 1000f, height = 60f, fontSize = 46f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 40f, y = 715f, width = 1000f, height = 45f, fontSize = 26f, color = "#F59E0B", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 60f, y = 785f, width = 960f, height = 60f, fontSize = 20f, color = "#FDE68A", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 12 — MINIMAL PROFESSIONAL WELCOME: White/charcoal/accent, strong grid, large portrait
        PosterTemplate(
            id = -212, name = "Minimal Professional Welcome", category = "Welcome",
            baseColor = "#FAFAFA", accentColor = "#1F2937", style = 141, order = 11, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = true, height = 200f, backgroundColor = "#F3F4F6", textColor = "#1F2937", showLogo = true, showCompanyName = true, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WELCOME", 60f, 45f, 960f, 80f, size = 64f, color = "#1F2937", alignment = "left"),
                StaticText("We believe great people build great companies.", 60f, 130f, 960f, 40f, size = 20f, color = "#6B7280", alignment = "left", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 60f, y = 195f, width = 960f, height = 400f, shape = "rounded", borderColor = "#D1D5DB", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = true, x = 60f, y = 630f, width = 960f, height = 60f, fontSize = 44f, color = "#111827", alignment = "left"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 60f, y = 700f, width = 960f, height = 45f, fontSize = 24f, color = "#4B5563", alignment = "left"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 60f, y = 770f, width = 960f, height = 65f, fontSize = 20f, color = "#6B7280", alignment = "left", bold = false, maxLines = 3)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 2. BIRTHDAY TEMPLATES (12 distinct professional compositions)         */
    /* ---------------------------------------------------------------------- */
    private val birthdayTemplates = listOf(
        // 01 — ROYAL BIRTHDAY: Dark navy + gold celebration effects, circular portrait, gift elements, name ribbon
        PosterTemplate(
            id = -301, name = "Royal Birthday", category = "Birthday",
            baseColor = "#12133B", accentColor = "#F9D162", style = 201, order = 0,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#090A22", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFF9E6", textColor = "#12133B", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY BIRTHDAY", 80f, 135f, 920f, 85f, size = 70f, color = "#F9D162", alignment = "center"),
                StaticText("WISHING YOU SUCCESS, HAPPINESS & HEALTH", 80f, 225f, 920f, 40f, size = 22f, color = "#FFFFFF", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 285f, width = 400f, height = 400f, shape = "circle", borderColor = "#F9D162", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 720f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 790f, width = 920f, height = 45f, fontSize = 26f, color = "#F9D162", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 80f, y = 845f, width = 920f, height = 75f, fontSize = 22f, color = "#E2E5F8", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 02 — GOLDEN WISHES: Black + champagne gold, portrait right, large birthday typography left
        PosterTemplate(
            id = -302, name = "Golden Wishes", category = "Birthday",
            baseColor = "#12100E", accentColor = "#F5C75D", style = 202, order = 1,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0A0908", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0A0908", textColor = "#F5C75D", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY\nBIRTHDAY", 70f, 180f, 460f, 210f, size = 68f, color = "#F5C75D", alignment = "left"),
                StaticText("MAY ALL YOUR DREAMS COME TRUE", 70f, 405f, 460f, 60f, size = 22f, color = "#E8D8B8", alignment = "left", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 550f, y = 160f, width = 450f, height = 530f, shape = "rounded", borderColor = "#F5C75D", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 70f, y = 725f, width = 940f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "left"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 70f, y = 795f, width = 940f, height = 45f, fontSize = 26f, color = "#F5C75D", alignment = "left"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 70f, y = 850f, width = 940f, height = 70f, fontSize = 22f, color = "#DFD7CE", alignment = "left", bold = false, maxLines = 2)
            )
        ),
        // 03 — BLUE CELEBRATION: Navy + royal blue + gold, portrait left, birthday message panel right
        PosterTemplate(
            id = -303, name = "Blue Celebration", category = "Birthday",
            baseColor = "#0A1D37", accentColor = "#E6C687", style = 203, order = 2,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#061224", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#F3F7FC", textColor = "#0A1D37", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WARMEST\nWISHES", 540f, 180f, 470f, 200f, size = 64f, color = "#E6C687", alignment = "left"),
                StaticText("ON YOUR SPECIAL DAY", 540f, 395f, 470f, 50f, size = 24f, color = "#B0CBEA", alignment = "left", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 70f, y = 160f, width = 440f, height = 530f, shape = "rounded", borderColor = "#E6C687", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 70f, y = 725f, width = 940f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "left"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 70f, y = 795f, width = 940f, height = 45f, fontSize = 26f, color = "#E6C687", alignment = "left"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 70f, y = 850f, width = 940f, height = 70f, fontSize = 22f, color = "#CBDDF2", alignment = "left", bold = false, maxLines = 2)
            )
        ),
        // 04 — EXECUTIVE BIRTHDAY: Clean premium business look, white + navy + gold, large portrait
        PosterTemplate(
            id = -304, name = "Executive Birthday", category = "Birthday",
            baseColor = "#0C2340", accentColor = "#C59B27", style = 204, order = 3,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#071629", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#F8FAFC", textColor = "#0C2340", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY BIRTHDAY", 80f, 135f, 920f, 80f, size = 62f, color = "#C59B27", alignment = "center"),
                StaticText("EXECUTIVE LEADERSHIP • EXCELLENCE • VISION", 80f, 220f, 920f, 40f, size = 20f, color = "#FFFFFF", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 330f, y = 280f, width = 420f, height = 420f, shape = "rounded", borderColor = "#C59B27", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 725f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 795f, width = 920f, height = 45f, fontSize = 26f, color = "#C59B27", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 850f, width = 880f, height = 70f, fontSize = 22f, color = "#E0EAF5", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 05 — PURPLE CELEBRATION: Purple + violet + gold, curved glow effects, celebration graphics
        PosterTemplate(
            id = -305, name = "Purple Celebration", category = "Birthday",
            baseColor = "#2C083D", accentColor = "#FFC857", style = 205, order = 4,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#1C0428", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFF8ED", textColor = "#2C083D", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("BIRTHDAY CELEBRATION", 80f, 135f, 920f, 85f, size = 64f, color = "#FFC857", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 250f, width = 400f, height = 450f, shape = "rounded", borderColor = "#FFC857", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 725f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 795f, width = 920f, height = 45f, fontSize = 26f, color = "#FFC857", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 850f, width = 880f, height = 70f, fontSize = 22f, color = "#F6E0FF", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 06 — BURGUNDY BIRTHDAY: Deep burgundy + gold, luxury gift elements, soft lighting
        PosterTemplate(
            id = -306, name = "Burgundy Birthday", category = "Birthday",
            baseColor = "#360A14", accentColor = "#E6B800", style = 206, order = 5,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#20050B", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#20050B", textColor = "#E6B800", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WISHING YOU A JOYOUS BIRTHDAY", 80f, 140f, 920f, 75f, size = 50f, color = "#E6B800", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 245f, width = 400f, height = 460f, shape = "oval", borderColor = "#E6B800", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 730f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 800f, width = 920f, height = 45f, fontSize = 26f, color = "#E6B800", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 855f, width = 880f, height = 65f, fontSize = 22f, color = "#F7E1E6", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 07 — EMERALD WISHES: Dark green + gold, elegant frame, portrait, premium greeting area
        PosterTemplate(
            id = -307, name = "Emerald Wishes", category = "Birthday",
            baseColor = "#0B291B", accentColor = "#D4AF37", style = 207, order = 6,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#05180F", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#05180F", textColor = "#D4AF37", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("CELEBRATING YOUR LIFE", 80f, 140f, 920f, 75f, size = 52f, color = "#D4AF37", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 250f, width = 400f, height = 450f, shape = "rounded", borderColor = "#D4AF37", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 730f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 800f, width = 920f, height = 45f, fontSize = 26f, color = "#D4AF37", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 855f, width = 880f, height = 65f, fontSize = 22f, color = "#E0EFE6", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 08 — MIDNIGHT CELEBRATION (Preserves legacy ID -101!): Midnight blue + gold particles
        PosterTemplate(
            id = -101, name = "Midnight Celebration", category = "Birthday",
            baseColor = "#091024", accentColor = "#FFD166", style = 208, order = 7,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#040813", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#040813", textColor = "#FFD166", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY BIRTHDAY", 80f, 135f, 920f, 85f, size = 68f, color = "#FFD166", alignment = "center"),
                StaticText("WISHING YOU ENDLESS INSPIRATION & HAPPINESS", 80f, 225f, 920f, 40f, size = 22f, color = "#FFFFFF", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 285f, width = 400f, height = 400f, shape = "circle", borderColor = "#FFD166", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 725f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 795f, width = 920f, height = 45f, fontSize = 26f, color = "#FFD166", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 850f, width = 880f, height = 70f, fontSize = 22f, color = "#DFE7F8", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 09 — FESTIVE GOLD: Warm orange-amber + gold, celebration fireworks, professional message
        PosterTemplate(
            id = -309, name = "Festive Gold", category = "Birthday",
            baseColor = "#2E1504", accentColor = "#FFAA00", style = 209, order = 8,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#1A0B02", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFF8F0", textColor = "#2E1504", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WARMEST BIRTHDAY GREETINGS", 80f, 140f, 920f, 75f, size = 52f, color = "#FFAA00", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 320f, y = 240f, width = 440f, height = 470f, shape = "rounded", borderColor = "#FFAA00", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 735f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 805f, width = 920f, height = 45f, fontSize = 26f, color = "#FFAA00", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 860f, width = 880f, height = 60f, fontSize = 22f, color = "#F6EAE1", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 10 — CORPORATE BIRTHDAY: Blue + white, modern corporate clean diagonal composition
        PosterTemplate(
            id = -310, name = "Corporate Birthday", category = "Birthday",
            baseColor = "#0E243A", accentColor = "#00A8E8", style = 210, order = 9,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#071421", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#F1F7FC", textColor = "#0E243A", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY BIRTHDAY", 80f, 135f, 920f, 80f, size = 64f, color = "#00A8E8", alignment = "center"),
                StaticText("VALUED TEAM MEMBER • BIGGER DREAMS AHEAD", 80f, 220f, 920f, 40f, size = 20f, color = "#FFFFFF", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 330f, y = 280f, width = 420f, height = 420f, shape = "rounded", borderColor = "#00A8E8", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 725f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 795f, width = 920f, height = 45f, fontSize = 26f, color = "#00A8E8", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 850f, width = 880f, height = 70f, fontSize = 22f, color = "#DBECF8", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 11 — BLACK ELITE: Obsidian black + champagne gold, executive visual style, large portrait
        PosterTemplate(
            id = -311, name = "Black Elite", category = "Birthday",
            baseColor = "#0C0C0D", accentColor = "#D4AF37", style = 211, order = 10,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#050505", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#050505", textColor = "#D4AF37", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("EXECUTIVE BIRTHDAY WISHES", 80f, 140f, 920f, 75f, size = 50f, color = "#D4AF37", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 320f, y = 235f, width = 440f, height = 480f, shape = "rounded", borderColor = "#D4AF37", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 735f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 805f, width = 920f, height = 45f, fontSize = 26f, color = "#D4AF37", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 860f, width = 880f, height = 60f, fontSize = 22f, color = "#E8E2D5", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 12 — BRIGHT CELEBRATION: Royal blue + magenta purple, glowing graphics, strong headline
        PosterTemplate(
            id = -312, name = "Bright Celebration", category = "Birthday",
            baseColor = "#160C38", accentColor = "#FF3366", style = 212, order = 11,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0C0620", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFF0F4", textColor = "#160C38", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("CELEBRATE YOUR SPECIAL DAY", 80f, 140f, 920f, 80f, size = 52f, color = "#FF3366", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 330f, y = 250f, width = 420f, height = 450f, shape = "rounded", borderColor = "#FF3366", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 725f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 795f, width = 920f, height = 45f, fontSize = 26f, color = "#FF3366", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 850f, width = 880f, height = 70f, fontSize = 22f, color = "#FBE2EA", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 13 — BIRTHDAY GRATITUDE SCROLL: Midnight purple + fireworks + rolled gold scroll + diyas + lotus + dual pill badges
        PosterTemplate(
            id = -313, name = "Birthday Gratitude Scroll", category = "Birthday",
            baseColor = "#150325", accentColor = "#FFCF48", style = 220, order = 12, photoPosition = "left",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.PHONE),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0D0219", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0D0219", textColor = "#FFCF48", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = emptyList(),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 30f, y = 140f, width = 480f, height = 660f, shape = "rounded", borderColor = "#FFCF48", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = true, x = 30f, y = 825f, width = 530f, height = 45f, fontSize = 32f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 30f, y = 870f, width = 530f, height = 37f, fontSize = 18f, color = "#F472B6", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 540f, y = 205f, width = 470f, height = 180f, fontSize = 22f, color = "#2B0B04", alignment = "center", bold = true, maxLines = 4)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 3. ACHIEVEMENT TEMPLATES (6 professional recognition templates)       */
    /* ---------------------------------------------------------------------- */
    private val achievementTemplates = listOf(
        // Star Performer
        PosterTemplate(
            id = -401, name = "Star Performer", category = "Achievement",
            baseColor = "#0D1B2A", accentColor = "#E0A96D", style = 301, order = 0,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#070E16", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFF8F0", textColor = "#0D1B2A", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("STAR PERFORMER", 80f, 135f, 920f, 85f, size = 68f, color = "#E0A96D", alignment = "center"),
                StaticText("OUTSTANDING DEDICATION & EXCELLENCE", 80f, 220f, 920f, 40f, size = 22f, color = "#FFFFFF", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 280f, width = 400f, height = 400f, shape = "circle", borderColor = "#E0A96D", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 715f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.ACHIEVEMENT, required = true, x = 80f, y = 785f, width = 920f, height = 55f, fontSize = 32f, color = "#E0A96D", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 850f, width = 880f, height = 65f, fontSize = 22f, color = "#CFD8DC", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // Top Achiever
        PosterTemplate(
            id = -402, name = "Top Achiever", category = "Achievement",
            baseColor = "#16161A", accentColor = "#72757E", style = 302, order = 1,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0A0A0C", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0A0A0C", textColor = "#2CB67D", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("TOP ACHIEVER", 80f, 140f, 920f, 80f, size = 64f, color = "#2CB67D", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 320f, y = 240f, width = 440f, height = 460f, shape = "rounded", borderColor = "#2CB67D", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 725f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.ACHIEVEMENT, required = true, x = 80f, y = 795f, width = 920f, height = 55f, fontSize = 32f, color = "#2CB67D", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 860f, width = 880f, height = 55f, fontSize = 22f, color = "#94A1B2", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // Congratulations
        PosterTemplate(
            id = -403, name = "Congratulations", category = "Achievement",
            baseColor = "#2A0E35", accentColor = "#FFD166", style = 303, order = 2,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#17061E", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFFDF5", textColor = "#2A0E35", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("CONGRATULATIONS", 80f, 140f, 920f, 85f, size = 66f, color = "#FFD166", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 330f, y = 245f, width = 420f, height = 455f, shape = "rounded", borderColor = "#FFD166", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 725f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.ACHIEVEMENT, required = true, x = 80f, y = 795f, width = 920f, height = 55f, fontSize = 32f, color = "#FFD166", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 860f, width = 880f, height = 60f, fontSize = 22f, color = "#F0DDF6", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // Achievement Unlocked
        PosterTemplate(
            id = -404, name = "Achievement Unlocked", category = "Achievement",
            baseColor = "#0B1E36", accentColor = "#00F5D4", style = 304, order = 3,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#050F1C", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#050F1C", textColor = "#00F5D4", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("ACHIEVEMENT UNLOCKED", 80f, 140f, 920f, 80f, size = 54f, color = "#00F5D4", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 250f, width = 400f, height = 450f, shape = "rounded", borderColor = "#00F5D4", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 725f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.ACHIEVEMENT, required = true, x = 80f, y = 795f, width = 920f, height = 55f, fontSize = 32f, color = "#00F5D4", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 860f, width = 880f, height = 55f, fontSize = 22f, color = "#CCFBF4", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // New Milestone
        PosterTemplate(
            id = -405, name = "New Milestone", category = "Achievement",
            baseColor = "#1B2A1E", accentColor = "#F9C74F", style = 305, order = 4,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0D1710", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFFDF5", textColor = "#1B2A1E", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("NEW MILESTONE", 80f, 140f, 920f, 80f, size = 60f, color = "#F9C74F", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 250f, width = 400f, height = 400f, shape = "circle", borderColor = "#F9C74F", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 715f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.ACHIEVEMENT, required = true, x = 80f, y = 785f, width = 920f, height = 55f, fontSize = 32f, color = "#F9C74F", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 850f, width = 880f, height = 65f, fontSize = 22f, color = "#DDEEE1", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // Business Champion
        PosterTemplate(
            id = -406, name = "Business Champion", category = "Achievement",
            baseColor = "#300D18", accentColor = "#E9C46A", style = 306, order = 5,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#1A050B", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#1A050B", textColor = "#E9C46A", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("BUSINESS CHAMPION", 80f, 140f, 920f, 80f, size = 58f, color = "#E9C46A", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 320f, y = 240f, width = 440f, height = 460f, shape = "rounded", borderColor = "#E9C46A", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 725f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.ACHIEVEMENT, required = true, x = 80f, y = 795f, width = 920f, height = 55f, fontSize = 32f, color = "#E9C46A", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 860f, width = 880f, height = 55f, fontSize = 22f, color = "#F7D8E0", alignment = "center", bold = false, maxLines = 2)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 4. INCOME TEMPLATES (6 professional recognition templates)            */
    /* ---------------------------------------------------------------------- */
    private val incomeTemplates = listOf(
        // Income Achievement
        PosterTemplate(
            id = -501, name = "Income Achievement", category = "Income",
            baseColor = "#1D0C33", accentColor = "#F9C74F", style = 401, order = 0,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0F051D", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFFDF5", textColor = "#1D0C33", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("INCOME ACHIEVEMENT", 80f, 135f, 920f, 80f, size = 58f, color = "#F9C74F", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 240f, width = 400f, height = 400f, shape = "circle", borderColor = "#F9C74F", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 675f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.AMOUNT, required = true, x = 80f, y = 745f, width = 920f, height = 80f, fontSize = 54f, color = "#F9C74F", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 840f, width = 880f, height = 75f, fontSize = 20f, color = "#D0C4E8", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // New Milestone
        PosterTemplate(
            id = -502, name = "New Milestone", category = "Income",
            baseColor = "#0B2545", accentColor = "#F7D070", style = 402, order = 1,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#051224", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#051224", textColor = "#F7D070", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("NEW MILESTONE", 80f, 140f, 920f, 80f, size = 60f, color = "#F7D070", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 330f, y = 240f, width = 420f, height = 450f, shape = "rounded", borderColor = "#F7D070", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 715f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.AMOUNT, required = true, x = 80f, y = 785f, width = 920f, height = 75f, fontSize = 52f, color = "#F7D070", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 865f, width = 880f, height = 55f, fontSize = 20f, color = "#C4D8EC", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // Congratulations
        PosterTemplate(
            id = -503, name = "Congratulations", category = "Income",
            baseColor = "#11141E", accentColor = "#E5B94E", style = 403, order = 2,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#080A10", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFFDF7", textColor = "#11141E", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("CONGRATULATIONS", 80f, 135f, 920f, 85f, size = 66f, color = "#E5B94E", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 240f, width = 400f, height = 400f, shape = "circle", borderColor = "#E5B94E", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 675f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.AMOUNT, required = true, x = 80f, y = 745f, width = 920f, height = 80f, fontSize = 54f, color = "#E5B94E", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 840f, width = 880f, height = 75f, fontSize = 20f, color = "#CCD3DC", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // Business Success
        PosterTemplate(
            id = -504, name = "Business Success", category = "Income",
            baseColor = "#0A2833", accentColor = "#00D2D3", style = 404, order = 3,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#05151B", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#05151B", textColor = "#00D2D3", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("BUSINESS SUCCESS", 80f, 140f, 920f, 80f, size = 58f, color = "#00D2D3", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 330f, y = 240f, width = 420f, height = 450f, shape = "rounded", borderColor = "#00D2D3", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 715f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.AMOUNT, required = true, x = 80f, y = 785f, width = 920f, height = 75f, fontSize = 52f, color = "#00D2D3", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 865f, width = 880f, height = 55f, fontSize = 20f, color = "#CEF4F4", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // Reward Recognition
        PosterTemplate(
            id = -505, name = "Reward Recognition", category = "Income",
            baseColor = "#2C081A", accentColor = "#F9C74F", style = 405, order = 4,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#17030C", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFFDF5", textColor = "#2C081A", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("REWARD RECOGNITION", 80f, 140f, 920f, 80f, size = 56f, color = "#F9C74F", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 240f, width = 400f, height = 400f, shape = "circle", borderColor = "#F9C74F", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 675f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.AMOUNT, required = true, x = 80f, y = 745f, width = 920f, height = 80f, fontSize = 54f, color = "#F9C74F", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 840f, width = 880f, height = 75f, fontSize = 20f, color = "#ECD0DC", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // Performance Highlight
        PosterTemplate(
            id = -506, name = "Performance Highlight", category = "Income",
            baseColor = "#0F1A30", accentColor = "#E2A03F", style = 406, order = 5,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#070C16", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#070C16", textColor = "#E2A03F", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("PERFORMANCE HIGHLIGHT", 80f, 140f, 920f, 80f, size = 54f, color = "#E2A03F", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 330f, y = 240f, width = 420f, height = 450f, shape = "rounded", borderColor = "#E2A03F", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 715f, width = 920f, height = 65f, fontSize = 48f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.AMOUNT, required = true, x = 80f, y = 785f, width = 920f, height = 75f, fontSize = 52f, color = "#E2A03F", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 865f, width = 880f, height = 55f, fontSize = 20f, color = "#D0D9EB", alignment = "center", bold = false, maxLines = 2)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 5. FESTIVAL TEMPLATES (8 rich festival templates, zero manual input)   */
    /* ---------------------------------------------------------------------- */
    private val festivalTemplates = listOf(
        // Vishwakarma Jayanti (Legacy ID -102 retained!)
        PosterTemplate(
            id = -102, name = "Vishwakarma Jayanti", category = "Festival",
            baseColor = "#2C1204", accentColor = "#FFB703", style = 501, order = 0,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#170902", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFF9ED", textColor = "#2C1204", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("विश्वकर्मा जयंती", 70f, 180f, 940f, 120f, size = 78f, color = "#FFB703", alignment = "center"),
                StaticText("सृजन, कौशल और समृद्धि की हार्दिक शुभकामनाएँ", 70f, 320f, 940f, 70f, size = 34f, color = "#FFFFFF", alignment = "center", bold = false),
                StaticText("CELEBRATING CRAFT, CREATION & PROGRESS", 70f, 760f, 940f, 50f, size = 26f, color = "#FFB703", alignment = "center")
            ),
            slots = emptyList() // No manual input needed! Profile branding automatically renders!
        ),
        // Diwali Celebration
        PosterTemplate(
            id = -602, name = "Diwali Celebration", category = "Festival",
            baseColor = "#33080A", accentColor = "#FFD166", style = 502, order = 1,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#1A0405", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFFBF0", textColor = "#33080A", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY DIWALI", 70f, 170f, 940f, 110f, size = 76f, color = "#FFD166", alignment = "center"),
                StaticText("FESTIVAL OF LIGHTS & PROSPERITY", 70f, 290f, 940f, 55f, size = 28f, color = "#FFFFFF", alignment = "center"),
                StaticText("May the divine light illuminate your path with\nhealth, wealth, and boundless success.", 80f, 580f, 920f, 120f, size = 32f, color = "#F8EBD0", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = emptyList()
        ),
        // Ganesh Festival
        PosterTemplate(
            id = -603, name = "Ganesh Festival", category = "Festival",
            baseColor = "#3B1405", accentColor = "#F4A261", style = 503, order = 2,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#1E0A02", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#1E0A02", textColor = "#F4A261", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY GANESH CHATURTHI", 70f, 170f, 940f, 100f, size = 66f, color = "#F4A261", alignment = "center"),
                StaticText("गणपति बप्पा मोरया", 70f, 280f, 940f, 75f, size = 44f, color = "#FFFFFF", alignment = "center"),
                StaticText("May Lord Ganesha remove all obstacles\nand bless your business with prosperity.", 80f, 580f, 920f, 120f, size = 32f, color = "#FCE8DC", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = emptyList()
        ),
        // Dussehra Wishes
        PosterTemplate(
            id = -604, name = "Dussehra Wishes", category = "Festival",
            baseColor = "#1D0D38", accentColor = "#FFB703", style = 504, order = 3,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0E061C", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFFDF5", textColor = "#1D0D38", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY DUSSEHRA", 70f, 170f, 940f, 100f, size = 70f, color = "#FFB703", alignment = "center"),
                StaticText("VIJAYADASHAMI GREETINGS", 70f, 280f, 940f, 50f, size = 30f, color = "#FFFFFF", alignment = "center"),
                StaticText("Celebrating the victory of good over evil.\nWishing you triumphs in every endeavor.", 80f, 590f, 920f, 110f, size = 30f, color = "#E8DCF8", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = emptyList()
        ),
        // Holi Festival
        PosterTemplate(
            id = -605, name = "Holi Festival", category = "Festival",
            baseColor = "#260633", accentColor = "#FF4D6D", style = 505, order = 4,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#14031B", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFF0F5", textColor = "#260633", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY HOLI", 70f, 170f, 940f, 110f, size = 80f, color = "#FF4D6D", alignment = "center"),
                StaticText("FESTIVAL OF VIBRANT COLOURS & JOY", 70f, 290f, 940f, 55f, size = 28f, color = "#FFD166", alignment = "center"),
                StaticText("May your life be painted with colours of\nhappiness, success, and prosperity.", 80f, 590f, 920f, 110f, size = 30f, color = "#FBE2EC", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = emptyList()
        ),
        // New Year Celebration
        PosterTemplate(
            id = -606, name = "New Year Celebration", category = "Festival",
            baseColor = "#0B0C10", accentColor = "#F2A900", style = 506, order = 5,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#050507", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#050507", textColor = "#F2A900", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY NEW YEAR", 70f, 170f, 940f, 110f, size = 74f, color = "#F2A900", alignment = "center"),
                StaticText("NEW HOPES • NEW ASPIRATIONS • NEW MILESTONES", 70f, 290f, 940f, 50f, size = 24f, color = "#FFFFFF", alignment = "center", bold = false),
                StaticText("Wishing you a year filled with new achievements,\ngrowth, and glorious breakthroughs.", 80f, 590f, 920f, 110f, size = 30f, color = "#E0D7C6", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = emptyList()
        ),
        // Independence Day
        PosterTemplate(
            id = -607, name = "Independence Day", category = "Festival",
            baseColor = "#071B33", accentColor = "#FF9933", style = 507, order = 6,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#030E1C", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#F4F8FC", textColor = "#071B33", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY INDEPENDENCE DAY", 70f, 170f, 940f, 100f, size = 62f, color = "#FF9933", alignment = "center"),
                StaticText("HONOURING OUR HERITAGE & PRIDE", 70f, 280f, 940f, 50f, size = 28f, color = "#138808", alignment = "center"),
                StaticText("Let us salute the nation and pledge to build\na stronger, more prosperous future together.", 80f, 590f, 920f, 110f, size = 30f, color = "#DDE7F4", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = emptyList()
        ),
        // Republic Day
        PosterTemplate(
            id = -608, name = "Republic Day", category = "Festival",
            baseColor = "#0A2239", accentColor = "#FF9933", style = 508, order = 7,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#04111E", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#04111E", textColor = "#FF9933", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY REPUBLIC DAY", 70f, 170f, 940f, 100f, size = 66f, color = "#FF9933", alignment = "center"),
                StaticText("JUSTICE • LIBERTY • EQUALITY • FRATERNITY", 70f, 280f, 940f, 50f, size = 26f, color = "#138808", alignment = "center"),
                StaticText("Proud citizens of a great democratic nation.\nWishing every Indian a joyful Republic Day.", 80f, 590f, 920f, 110f, size = 30f, color = "#E0EAF6", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = emptyList()
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 6. MOTIVATION TEMPLATES (pinnacle leadership + split person-photo + quote) */
    /* ---------------------------------------------------------------------- */
    private val motivationTemplates = listOf(
        // 00 — EXECUTIVE LEADERSHIP PINNACLE (Mountain landscape, dual badges, 4 icon cards, brush mask)
        PosterTemplate(
            id = -720, name = "Executive Leadership Pinnacle", category = "Motivation",
            baseColor = "#0B1B30", accentColor = "#F9C74F", style = 620, order = 0, photoPosition = "right",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.PROFILE_PHOTO, BrandingField.PERSON_NAME, BrandingField.DESIGNATION, BrandingField.BUSINESS_NAME, BrandingField.PHONE),
            header = BrandingBand(enabled = false),
            footer = BrandingBand(enabled = false),
            staticTexts = listOf(
                StaticText("★ समय बदलता है ★", 40f, 130f, 490f, 45f, size = 32f, color = "#081E38", bold = true, alignment = "center"),
                StaticText("— उन लोगों का, —", 40f, 180f, 490f, 40f, size = 26f, color = "#081E38", bold = false, alignment = "center"),
                StaticText("जो खुद को", 160f, 225f, 250f, 46f, size = 28f, color = "#FFFFFF", bold = true, alignment = "center"),
                StaticText("बदलने", 40f, 275f, 490f, 130f, size = 96f, color = "#061834", bold = true, alignment = "center", highlightWords = listOf("बदलने")),
                StaticText("का साहस रखते हैं।", 40f, 410f, 490f, 50f, size = 36f, color = "#081E38", bold = true, alignment = "center"),
                StaticText("• आज मेहनत करें, •", 40f, 470f, 490f, 40f, size = 26f, color = "#081E38", bold = true, alignment = "center"),
                StaticText("कल अपनी कहानी", 130f, 515f, 310f, 50f, size = 30f, color = "#FFFFFF", bold = true, alignment = "center"),
                StaticText("➤ लिखें ! ⮜", 40f, 570f, 490f, 65f, size = 52f, color = "#C89828", bold = true, alignment = "center", highlightWords = listOf("लिखें !"))
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 490f, y = 140f, width = 570f, height = 680f, shape = "rounded", borderColor = "#00000000", borderWidth = 0f),
                TemplateSlot(TemplateField.NAME, required = false, x = 480f, y = 820f, width = 550f, height = 45f, fontSize = 32f, color = "#FFFFFF", alignment = "center", bold = true),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 480f, y = 866f, width = 550f, height = 35f, fontSize = 18f, color = "#F9C74F", alignment = "center", bold = true)
            )
        ),
        // 01 — Leadership Vision (photo right, quote left, bold hierarchy)
        PosterTemplate(
            id = -711, name = "Leadership Vision", category = "Motivation",
            baseColor = "#0A192F", accentColor = "#E5B94E", style = 611, order = 0, photoPosition = "right",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#050C18", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#050C18", textColor = "#E5B94E", showLogo = false, showPhone = true, showWebsite = true, showEmail = true),
            staticTexts = listOf(
                StaticText("LEADERSHIP & VISION", 60f, 150f, 480f, 40f, size = 22f, color = "#E5B94E", alignment = "left"),
                StaticText("GREAT LEADERS\nDON'T SET OUT TO BE\nA LEADER.", 60f, 205f, 480f, 160f, size = 38f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("LEADERS", "LEADER")),
                StaticText("THEY SET OUT TO MAKE\nA DIFFERENCE.", 60f, 375f, 480f, 90f, size = 32f, color = "#E5B94E", alignment = "left", highlightWords = listOf("DIFFERENCE")),
                StaticText("• Lead by example\n• Empower your people\n• Create lasting value", 80f, 580f, 440f, 110f, size = 18f, color = "#E5B94E", alignment = "left", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 550f, y = 140f, width = 470f, height = 580f, shape = "rounded", borderColor = "#E5B94E", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 540f, y = 735f, width = 490f, height = 55f, fontSize = 36f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 540f, y = 795f, width = 490f, height = 45f, fontSize = 22f, color = "#E5B94E", alignment = "center"),
                TemplateSlot(TemplateField.QUOTE, required = false, x = 60f, y = 710f, width = 460f, height = 130f, fontSize = 20f, color = "#FFFFFF", alignment = "left", bold = false, maxLines = 3)
            )
        ),
        // 02 — Rise to Greatness (photo left, quote right)
        PosterTemplate(
            id = -712, name = "Rise to Greatness", category = "Motivation",
            baseColor = "#1A1308", accentColor = "#F4A261", style = 612, order = 1, photoPosition = "left",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0D0A04", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0D0A04", textColor = "#F4A261", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("MINDSET MATTERS", 560f, 150f, 460f, 40f, size = 22f, color = "#F4A261", alignment = "left"),
                StaticText("DREAM WITHOUT\nLIMITS. EXECUTE\nWITHOUT FEAR.", 560f, 205f, 460f, 170f, size = 38f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("DREAM", "LIMITS", "EXECUTE")),
                StaticText("Your potential is limitless when you refuse to surrender.", 560f, 390f, 460f, 80f, size = 22f, color = "#E0D0C0", alignment = "left", bold = false, maxLines = 2)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 60f, y = 140f, width = 460f, height = 580f, shape = "rounded", borderColor = "#F4A261", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 50f, y = 735f, width = 480f, height = 55f, fontSize = 36f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 50f, y = 795f, width = 480f, height = 45f, fontSize = 22f, color = "#F4A261", alignment = "center"),
                TemplateSlot(TemplateField.QUOTE, required = false, x = 560f, y = 600f, width = 460f, height = 160f, fontSize = 22f, color = "#FFFFFF", alignment = "left", bold = false, maxLines = 4)
            )
        ),
        // 03 — Strive For Excellence (center photo, top headline)
        PosterTemplate(
            id = -713, name = "Strive For Excellence", category = "Motivation",
            baseColor = "#18120B", accentColor = "#D4AF37", style = 613, order = 2, photoPosition = "center",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0C0905", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFFDF8", textColor = "#18120B", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("CHAMPION'S MINDSET", 80f, 140f, 920f, 40f, size = 26f, color = "#D4AF37", alignment = "center"),
                StaticText("STRIVE FOR EXCELLENCE", 80f, 185f, 920f, 75f, size = 52f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("EXCELLENCE")),
                StaticText("Consistency creates capability. Every effort counts.", 100f, 265f, 880f, 60f, size = 24f, color = "#E8D8C8", alignment = "center", bold = false, maxLines = 2)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 350f, width = 400f, height = 400f, shape = "circle", borderColor = "#D4AF37", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 770f, width = 920f, height = 60f, fontSize = 42f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 835f, width = 920f, height = 45f, fontSize = 24f, color = "#D4AF37", alignment = "center")
            )
        ),
        // Dream Big
        PosterTemplate(
            id = -701, name = "Dream Big", category = "Motivation",
            baseColor = "#141E30", accentColor = "#E0A96D", style = 601, order = 3,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0B101B", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0B101B", textColor = "#E0A96D", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("DREAM BIG.\nSTART SMALL.\nACT NOW.", 80f, 220f, 920f, 320f, size = 84f, color = "#E0A96D", alignment = "left"),
                StaticText("Big achievements are built through daily persistence.\nNever underestimate the compound effect of small wins.", 80f, 580f, 920f, 160f, size = 34f, color = "#FFFFFF", alignment = "left", bold = false, maxLines = 4)
            ),
            slots = emptyList()
        ),
        // Rise Every Day (Legacy ID -103 retained!)
        PosterTemplate(
            id = -103, name = "Rise Every Day", category = "Motivation",
            baseColor = "#1B1725", accentColor = "#F9C74F", style = 602, order = 4,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0E0C13", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFFDF5", textColor = "#1B1725", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("RISE EVERY DAY.", 80f, 230f, 920f, 130f, size = 86f, color = "#F9C74F", alignment = "center"),
                StaticText("CONQUER EVERY GOAL.", 80f, 370f, 920f, 110f, size = 68f, color = "#FFFFFF", alignment = "center"),
                StaticText("Consistency creates capability.\nKeep learning. Keep growing. Keep moving forward.", 80f, 560f, 920f, 160f, size = 32f, color = "#E4DCD3", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = emptyList()
        ),
        // Keep Moving Forward
        PosterTemplate(
            id = -703, name = "Keep Moving Forward", category = "Motivation",
            baseColor = "#0A2239", accentColor = "#00F5D4", style = 603, order = 5,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#04111E", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#04111E", textColor = "#00F5D4", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("KEEP MOVING\nFORWARD", 80f, 220f, 920f, 240f, size = 84f, color = "#00F5D4", alignment = "left"),
                StaticText("Obstacles are what you see when you take your eyes off the goal.\nStay focused on where you are going.", 80f, 550f, 920f, 180f, size = 34f, color = "#FFFFFF", alignment = "left", bold = false, maxLines = 4)
            ),
            slots = emptyList()
        ),
        // Build Your Future
        PosterTemplate(
            id = -704, name = "Build Your Future", category = "Motivation",
            baseColor = "#22092C", accentColor = "#FF70A6", style = 604, order = 6,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#130419", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFF0F5", textColor = "#22092C", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("BUILD YOUR\nFUTURE TODAY", 80f, 220f, 920f, 260f, size = 82f, color = "#FF70A6", alignment = "center"),
                StaticText("The best way to predict the future is to create it.\nTake the step today that your future self will thank you for.", 80f, 560f, 920f, 180f, size = 32f, color = "#F6E2EC", alignment = "center", bold = false, maxLines = 4)
            ),
            slots = emptyList()
        ),
        // Success Starts Today
        PosterTemplate(
            id = -705, name = "Success Starts Today", category = "Motivation",
            baseColor = "#1A1A24", accentColor = "#FFD166", style = 605, order = 7,
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0D0D12", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0D0D12", textColor = "#FFD166", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("SUCCESS STARTS\nTODAY", 80f, 220f, 920f, 240f, size = 84f, color = "#FFD166", alignment = "left"),
                StaticText("Do not wait for extraordinary circumstances.\nSeize ordinary occasions and make them great.", 80f, 550f, 920f, 180f, size = 34f, color = "#FFFFFF", alignment = "left", bold = false, maxLines = 4)
            ),
            slots = emptyList()
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 7. BUSINESS TEMPLATES (3 professional promotion & brand templates)      */
    /* ---------------------------------------------------------------------- */
    private val businessTemplates = listOf(
        // 01 — Special Offer & Promotion
        PosterTemplate(
            id = -801, name = "Special Offer & Services", category = "Business",
            baseColor = "#08162B", accentColor = "#00D2D3", style = 701, order = 0, photoPosition = "right",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#040B16", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#040B16", textColor = "#00D2D3", showLogo = false, showPhone = true, showWebsite = true, showAddress = true),
            staticTexts = listOf(
                StaticText("EXCLUSIVE BUSINESS OFFER", 50f, 140f, 480f, 40f, size = 22f, color = "#00D2D3", alignment = "left"),
                StaticText("GROW YOUR\nBUSINESS\nFASTER", 50f, 190f, 480f, 170f, size = 42f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("BUSINESS", "FASTER")),
                StaticText("Professional solutions tailored to accelerate your commercial growth.", 50f, 375f, 440f, 85f, size = 20f, color = "#BCE8E8", alignment = "left", bold = false, maxLines = 3),
                StaticText("✓ Premium Quality\n✓ Expert Consultation\n✓ 24/7 Dedicated Support", 50f, 510f, 440f, 110f, size = 18f, color = "#00D2D3", alignment = "left", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 530f, y = 140f, width = 490f, height = 580f, shape = "rounded", borderColor = "#00D2D3", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 520f, y = 735f, width = 510f, height = 55f, fontSize = 36f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 520f, y = 795f, width = 510f, height = 45f, fontSize = 22f, color = "#00D2D3", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 50f, y = 690f, width = 450f, height = 140f, fontSize = 20f, color = "#FFFFFF", alignment = "left", bold = false, maxLines = 3)
            )
        ),
        // 02 — Executive Business Services
        PosterTemplate(
            id = -802, name = "Executive Services", category = "Business",
            baseColor = "#111720", accentColor = "#F5C75D", style = 702, order = 1, photoPosition = "center",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#080B10", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#080B10", textColor = "#F5C75D", showLogo = false, showPhone = true, showWebsite = true, showEmail = true),
            staticTexts = listOf(
                StaticText("PREMIUM CONSULTING", 80f, 135f, 920f, 40f, size = 24f, color = "#F5C75D", alignment = "center"),
                StaticText("TRUSTED BUSINESS PARTNER", 80f, 180f, 920f, 75f, size = 48f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("TRUSTED", "PARTNER")),
                StaticText("Transforming enterprises with modern strategy and measurable impact.", 100f, 260f, 880f, 55f, size = 22f, color = "#DCD5C5", alignment = "center", bold = false, maxLines = 2)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 330f, y = 330f, width = 420f, height = 440f, shape = "rounded", borderColor = "#F5C75D", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 785f, width = 920f, height = 55f, fontSize = 40f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 845f, width = 920f, height = 45f, fontSize = 24f, color = "#F5C75D", alignment = "center")
            )
        ),
        // 03 — Real Estate & Business Growth
        PosterTemplate(
            id = -803, name = "Business Growth & Scale", category = "Business",
            baseColor = "#0B2038", accentColor = "#48CAE4", style = 703, order = 2, photoPosition = "left",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#05101C", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#05101C", textColor = "#48CAE4", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("INNOVATION & EXCELLENCE", 560f, 150f, 460f, 40f, size = 22f, color = "#48CAE4", alignment = "left"),
                StaticText("SCALE YOUR\nVENTURE TO\nNEW HEIGHTS", 560f, 205f, 460f, 170f, size = 42f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("SCALE", "HEIGHTS")),
                StaticText("Comprehensive business advisory, investments, and scalable solutions.", 560f, 390f, 460f, 80f, size = 20f, color = "#CBE9F4", alignment = "left", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 60f, y = 140f, width = 460f, height = 580f, shape = "rounded", borderColor = "#48CAE4", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 50f, y = 735f, width = 480f, height = 55f, fontSize = 36f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 50f, y = 795f, width = 480f, height = 45f, fontSize = 22f, color = "#48CAE4", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 560f, y = 560f, width = 460f, height = 180f, fontSize = 22f, color = "#FFFFFF", alignment = "left", bold = false, maxLines = 4)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 8. GOOD MORNING TEMPLATES (3 warm greeting & sunrise inspiration)      */
    /* ---------------------------------------------------------------------- */
    private val goodMorningTemplates = listOf(
        // 01 — Golden Sunrise (Bilingual Devanagari + English)
        PosterTemplate(
            id = -851, name = "Golden Sunrise", category = "Good Morning",
            baseColor = "#1E0F05", accentColor = "#FFB703", style = 801, order = 0, photoPosition = "center",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0F0702", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFFDF5", textColor = "#1E0F05", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("शुभ प्रभात", 80f, 150f, 920f, 80f, size = 68f, color = "#FFB703", alignment = "center"),
                StaticText("GOOD MORNING", 80f, 240f, 920f, 50f, size = 32f, color = "#FFFFFF", alignment = "center"),
                StaticText("Every sunrise is an invitation to brighten someone's day.\nMay your day be filled with joy, peace, and great achievements.", 80f, 310f, 920f, 90f, size = 24f, color = "#F8DEBA", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 360f, y = 430f, width = 360f, height = 360f, shape = "circle", borderColor = "#FFB703", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = false, x = 100f, y = 810f, width = 880f, height = 55f, fontSize = 38f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 100f, y = 870f, width = 880f, height = 45f, fontSize = 22f, color = "#FFB703", alignment = "center")
            )
        ),
        // 02 — Peaceful Morning Floral
        PosterTemplate(
            id = -852, name = "Peaceful Morning", category = "Good Morning",
            baseColor = "#250D2B", accentColor = "#FF85A1", style = 802, order = 1, photoPosition = "center",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#130616", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFF0F5", textColor = "#250D2B", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAVE A BLESSED DAY", 80f, 150f, 920f, 45f, size = 26f, color = "#FF85A1", alignment = "center"),
                StaticText("GOOD MORNING", 80f, 205f, 920f, 75f, size = 62f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("MORNING")),
                StaticText("Start your day with a smile and a grateful heart.\nNew hopes, new dreams, new opportunities await you.", 80f, 295f, 920f, 85f, size = 24f, color = "#FCE0EA", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 360f, y = 410f, width = 360f, height = 360f, shape = "circle", borderColor = "#FF85A1", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = false, x = 100f, y = 800f, width = 880f, height = 55f, fontSize = 38f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 100f, y = 860f, width = 880f, height = 45f, fontSize = 22f, color = "#FF85A1", alignment = "center")
            )
        ),
        // 03 — Corporate Daybreak
        PosterTemplate(
            id = -853, name = "Daily Inspiration Morning", category = "Good Morning",
            baseColor = "#0D1826", accentColor = "#E2A03F", style = 803, order = 2, photoPosition = "center",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#060C13", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#060C13", textColor = "#E2A03F", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("NEW DAY • NEW OPPORTUNITY", 80f, 150f, 920f, 40f, size = 24f, color = "#E2A03F", alignment = "center"),
                StaticText("MAKE TODAY COUNT", 80f, 200f, 920f, 75f, size = 56f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("TODAY", "COUNT")),
                StaticText("Your dedication today determines your success tomorrow.\nWishing you a productive and rewarding day.", 80f, 290f, 920f, 85f, size = 24f, color = "#D6E0EC", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 360f, y = 410f, width = 360f, height = 360f, shape = "rounded", borderColor = "#E2A03F", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = false, x = 100f, y = 800f, width = 880f, height = 55f, fontSize = 38f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 100f, y = 860f, width = 880f, height = 45f, fontSize = 22f, color = "#E2A03F", alignment = "center")
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 9. ANNIVERSARY TEMPLATES (3 celebration & milestone templates)          */
    /* ---------------------------------------------------------------------- */
    private val anniversaryTemplates = listOf(
        // 01 — Golden Anniversary
        PosterTemplate(
            id = -901, name = "Golden Anniversary", category = "Anniversary",
            baseColor = "#1C1405", accentColor = "#F9C74F", style = 901, order = 0, photoPosition = "center",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0E0A02", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFFDF5", textColor = "#1C1405", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("HAPPY ANNIVERSARY", 80f, 140f, 920f, 80f, size = 64f, color = "#F9C74F", alignment = "center", highlightWords = listOf("ANNIVERSARY")),
                StaticText("CELEBRATING YEARS OF TOGETHERNESS & LOVE", 80f, 230f, 920f, 45f, size = 26f, color = "#FFFFFF", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 300f, width = 400f, height = 400f, shape = "circle", borderColor = "#F9C74F", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 725f, width = 920f, height = 60f, fontSize = 44f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 795f, width = 880f, height = 95f, fontSize = 22f, color = "#F0E4CE", alignment = "center", bold = false, maxLines = 3)
            )
        ),
        // 02 — Corporate Milestone Anniversary
        PosterTemplate(
            id = -902, name = "Corporate Milestone Anniversary", category = "Anniversary",
            baseColor = "#0C1425", accentColor = "#64DFDF", style = 902, order = 1, photoPosition = "right",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#060A13", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#060A13", textColor = "#64DFDF", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("FOUNDATION MILESTONE", 50f, 150f, 480f, 40f, size = 22f, color = "#64DFDF", alignment = "left"),
                StaticText("CELEBRATING\nEXCELLENCE &\nGROWTH", 50f, 205f, 480f, 170f, size = 40f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("EXCELLENCE", "GROWTH")),
                StaticText("Honoring another milestone year of innovation, trust, and shared success.", 50f, 390f, 450f, 85f, size = 20f, color = "#C4EEEE", alignment = "left", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 540f, y = 140f, width = 480f, height = 580f, shape = "rounded", borderColor = "#64DFDF", borderWidth = 4f),
                TemplateSlot(TemplateField.COMPANY, required = true, x = 530f, y = 735f, width = 500f, height = 55f, fontSize = 36f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 50f, y = 580f, width = 460f, height = 180f, fontSize = 22f, color = "#FFFFFF", alignment = "left", bold = false, maxLines = 4)
            )
        ),
        // 03 — Warm Wishes Anniversary
        PosterTemplate(
            id = -903, name = "Warm Wishes Anniversary", category = "Anniversary",
            baseColor = "#260D1A", accentColor = "#FF758F", style = 903, order = 2, photoPosition = "center",
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#14060E", textColor = "#FFFFFF"),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#FFF2F5", textColor = "#260D1A", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WARMEST CONGRATULATIONS", 80f, 140f, 920f, 45f, size = 26f, color = "#FF758F", alignment = "center"),
                StaticText("HAPPY ANNIVERSARY", 80f, 195f, 920f, 75f, size = 60f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("ANNIVERSARY"))
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 340f, y = 300f, width = 400f, height = 400f, shape = "rounded", borderColor = "#FF758F", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 725f, width = 920f, height = 60f, fontSize = 44f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 795f, width = 880f, height = 95f, fontSize = 22f, color = "#F6D6E2", alignment = "center", bold = false, maxLines = 3)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 10. OFFERS TEMPLATES (2 promotional discount & sale templates)         */
    /* ---------------------------------------------------------------------- */
    private val offersTemplates = listOf(
        PosterTemplate(
            id = -951, name = "Mega Flash Sale", category = "Offers",
            baseColor = "#0F172A", accentColor = "#F59E0B", style = 951, order = 0, photoPosition = "right",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#020617", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#020617", textColor = "#F59E0B", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("LIMITED TIME SPECIAL", 60f, 160f, 480f, 40f, size = 24f, color = "#F59E0B", alignment = "left"),
                StaticText("MEGA\nFLASH SALE", 60f, 210f, 480f, 170f, size = 58f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("SALE")),
                StaticText("UP TO 50% OFF", 60f, 395f, 460f, 60f, size = 36f, color = "#EF4444", alignment = "left"),
                StaticText("Exclusive deals across all services.\nGrab this limited opportunity today!", 60f, 465f, 460f, 85f, size = 20f, color = "#94A3B8", alignment = "left", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 540f, y = 160f, width = 480f, height = 540f, shape = "rounded", borderColor = "#F59E0B", borderWidth = 3f),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 60f, y = 730f, width = 960f, height = 90f, fontSize = 26f, color = "#FFFFFF", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        PosterTemplate(
            id = -952, name = "Special Festival Offer", category = "Offers",
            baseColor = "#1A0B2E", accentColor = "#EC4899", style = 952, order = 1, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0D0517", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0D0517", textColor = "#EC4899", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("FESTIVE SEASON DEALS", 80f, 150f, 920f, 45f, size = 26f, color = "#EC4899", alignment = "center"),
                StaticText("BIG SAVINGS FEST", 80f, 205f, 920f, 75f, size = 62f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("SAVINGS")),
                StaticText("Flat discounts and premium gifts on early bookings", 100f, 720f, 880f, 60f, size = 24f, color = "#CBD5E1", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 320f, y = 300f, width = 440f, height = 400f, shape = "rounded", borderColor = "#EC4899", borderWidth = 4f),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 790f, width = 880f, height = 90f, fontSize = 22f, color = "#F472B6", alignment = "center", bold = false, maxLines = 2)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 11. EVENTS TEMPLATES (2 conference & seminar templates)               */
    /* ---------------------------------------------------------------------- */
    private val eventsTemplates = listOf(
        PosterTemplate(
            id = -961, name = "Business Summit 2026", category = "Events",
            baseColor = "#0A192F", accentColor = "#64FFDA", style = 961, order = 0, photoPosition = "right",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#020C1B", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#020C1B", textColor = "#64FFDA", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("ANNUAL LEADERSHIP FORUM", 60f, 150f, 480f, 40f, size = 22f, color = "#64FFDA", alignment = "left"),
                StaticText("BUSINESS\nSUMMIT 2026", 60f, 200f, 480f, 160f, size = 54f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("SUMMIT")),
                StaticText("KEYNOTE SPEAKER", 550f, 150f, 470f, 35f, size = 20f, color = "#64FFDA", alignment = "center"),
                StaticText("Join industry visionaries, founders,\nand investors shaping tomorrow.", 60f, 430f, 460f, 90f, size = 20f, color = "#8892B0", alignment = "left", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 550f, y = 195f, width = 470f, height = 520f, shape = "rounded", borderColor = "#64FFDA", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = true, x = 540f, y = 730f, width = 490f, height = 50f, fontSize = 32f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 540f, y = 785f, width = 490f, height = 40f, fontSize = 20f, color = "#64FFDA", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 60f, y = 730f, width = 460f, height = 100f, fontSize = 20f, color = "#CCD6F6", alignment = "left", bold = false, maxLines = 3)
            )
        ),
        PosterTemplate(
            id = -962, name = "Tech Conference & Expo", category = "Events",
            baseColor = "#180D2B", accentColor = "#A855F7", style = 962, order = 1, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0B0517", textColor = "#FFFFFF", showLogo = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0B0517", textColor = "#A855F7", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("GLOBAL INNOVATION EXPO", 80f, 150f, 920f, 45f, size = 26f, color = "#A855F7", alignment = "center"),
                StaticText("TECH FORWARD 2026", 80f, 205f, 920f, 75f, size = 60f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("TECH", "FORWARD")),
                StaticText("AI • Cloud • Cybersecurity • Next-Gen Robotics", 80f, 720f, 920f, 50f, size = 24f, color = "#E2E8F0", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 330f, y = 300f, width = 420f, height = 400f, shape = "rounded", borderColor = "#A855F7", borderWidth = 4f),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 100f, y = 785f, width = 880f, height = 95f, fontSize = 22f, color = "#C084FC", alignment = "center", bold = false, maxLines = 2)
            )
        )
    )

    fun starter(id: Int, name: String, category: String, index: Int = 0): PosterTemplate {
        val existing = all.firstOrNull { it.id == id }
        if (existing != null) return existing
        val matchingCategory = all.filter { it.category == category }
        if (matchingCategory.isNotEmpty()) {
            val template = matchingCategory[index.coerceIn(matchingCategory.indices)]
            return template.copy(id = id, name = name, order = index)
        }
        return welcomeTemplates.first().copy(id = id, name = name, category = category, order = index)
    }

    /* ---------------------------------------------------------------------- */
    /* 12. GOOD NIGHT TEMPLATES (3 calming evening greeting templates)        */
    /* ---------------------------------------------------------------------- */
    private val goodNightTemplates = listOf(
        // 01 — MIDNIGHT STARS: Deep navy starfield with crescent moon, soft glow
        PosterTemplate(
            id = -2001, name = "Midnight Stars", category = "Good Night",
            baseColor = "#050A1A", accentColor = "#C4A832", style = 2001, order = 0, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME),
            header = BrandingBand(enabled = true, height = 90f, backgroundColor = "#020510", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = false, height = 80f, backgroundColor = "#020510", textColor = "#C4A832"),
            staticTexts = listOf(
                StaticText("✨  GOOD NIGHT  ✨", 80f, 180f, 920f, 70f, size = 52f, color = "#C4A832", alignment = "center"),
                StaticText("May the stars watch over you\nand the moon light your dreams.", 100f, 290f, 880f, 120f, size = 30f, color = "#D0D8F0", alignment = "center", bold = false, maxLines = 3),
                StaticText("Rest well. Tomorrow is a new beginning.", 100f, 700f, 880f, 55f, size = 26f, color = "#8899BB", alignment = "center", bold = false),
                StaticText("🌙", 480f, 440f, 120f, 120f, size = 80f, color = "#C4A832", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.NAME, required = false, x = 100f, y = 795f, width = 880f, height = 50f, fontSize = 28f, color = "#C4A832", alignment = "center", bold = false)
            )
        ),
        // 02 — PURPLE DREAMS: Soft purple lavender gradient, elegant night quote
        PosterTemplate(
            id = -2002, name = "Purple Dreams", category = "Good Night",
            baseColor = "#1A0B2E", accentColor = "#E879F9", style = 2002, order = 1, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME),
            header = BrandingBand(enabled = true, height = 90f, backgroundColor = "#110722", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = false, height = 80f, backgroundColor = "#110722", textColor = "#E879F9"),
            staticTexts = listOf(
                StaticText("GOOD NIGHT", 80f, 170f, 920f, 80f, size = 62f, color = "#E879F9", alignment = "center"),
                StaticText("Sweet dreams and peaceful rest.", 100f, 285f, 880f, 60f, size = 30f, color = "#F3E8FF", alignment = "center", bold = false),
                StaticText("\"Sleep is the golden chain that binds\nhealth and our bodies together.\"", 100f, 480f, 880f, 130f, size = 28f, color = "#C4B5FD", alignment = "center", bold = false, maxLines = 3),
                StaticText("— Thomas Dekker", 100f, 640f, 880f, 45f, size = 22f, color = "#A78BFA", alignment = "center", bold = false),
                StaticText("🌸", 490f, 370f, 100f, 100f, size = 70f, color = "#E879F9", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.NAME, required = false, x = 100f, y = 800f, width = 880f, height = 50f, fontSize = 28f, color = "#E879F9", alignment = "center", bold = false)
            )
        ),
        // 03 — GOLDEN TWILIGHT: Warm amber & dark brown, professional night close
        PosterTemplate(
            id = -2003, name = "Golden Twilight", category = "Good Night",
            baseColor = "#1C1008", accentColor = "#F59E0B", style = 2003, order = 2, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE),
            header = BrandingBand(enabled = true, height = 100f, backgroundColor = "#0F0804", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 100f, backgroundColor = "#0F0804", textColor = "#F59E0B", showLogo = false, showPhone = true),
            staticTexts = listOf(
                StaticText("GOOD NIGHT", 80f, 190f, 920f, 85f, size = 66f, color = "#F59E0B", alignment = "center"),
                StaticText("Wishing you a peaceful night\nand a fresh start tomorrow.", 100f, 310f, 880f, 120f, size = 30f, color = "#FDE68A", alignment = "center", bold = false, maxLines = 3),
                StaticText("🌙  Rest. Recharge. Rise.  🌟", 100f, 740f, 880f, 55f, size = 26f, color = "#F59E0B", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 340f, y = 440f, width = 400f, height = 280f, shape = "rounded", borderColor = "#F59E0B", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = false, x = 100f, y = 740f, width = 880f, height = 50f, fontSize = 28f, color = "#F59E0B", alignment = "center", bold = false)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 13. POLITICAL TEMPLATES (3 campaign & leadership templates)            */
    /* ---------------------------------------------------------------------- */
    private val politicalTemplates = listOf(
        // 01 — LEADER'S VISION: Tricolor-inspired, bold split, dual portrait layout
        PosterTemplate(
            id = -2101, name = "Leader's Vision", category = "Political",
            baseColor = "#0C2340", accentColor = "#FF9933", style = 2101, order = 0, photoPosition = "right",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#061526", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#061526", textColor = "#FF9933", showLogo = false, showPhone = true, showWebsite = false),
            staticTexts = listOf(
                StaticText("LEADERSHIP", 50f, 150f, 480f, 45f, size = 26f, color = "#FF9933", alignment = "left"),
                StaticText("FOR THE\nPEOPLE", 50f, 210f, 480f, 180f, size = 72f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("PEOPLE")),
                StaticText("Building a better tomorrow through\nservice, vision, and unity.", 50f, 430f, 460f, 100f, size = 22f, color = "#90B8D8", alignment = "left", bold = false, maxLines = 3),
                StaticText("JAY HIND • JAY BHARAT", 50f, 700f, 460f, 45f, size = 20f, color = "#FF9933", alignment = "left", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 550f, y = 150f, width = 490f, height = 600f, shape = "rounded", borderColor = "#FF9933", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 550f, y = 765f, width = 490f, height = 55f, fontSize = 36f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 550f, y = 825f, width = 490f, height = 40f, fontSize = 22f, color = "#FF9933", alignment = "center")
            )
        ),
        // 02 — DEMOCRATIC PRIDE: Saffron & green patriotic, centered composition
        PosterTemplate(
            id = -2102, name = "Democratic Pride", category = "Political",
            baseColor = "#1A0A00", accentColor = "#22C55E", style = 2102, order = 1, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0D0500", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0D0500", textColor = "#22C55E", showLogo = false, showPhone = true),
            staticTexts = listOf(
                StaticText("VOTE FOR CHANGE", 80f, 155f, 920f, 55f, size = 36f, color = "#22C55E", alignment = "center"),
                StaticText("TOGETHER\nWE RISE", 80f, 230f, 920f, 150f, size = 72f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("RISE")),
                StaticText("Progress • Unity • Development", 80f, 700f, 920f, 50f, size = 26f, color = "#BBF7D0", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 290f, y = 390f, width = 500f, height = 300f, shape = "rounded", borderColor = "#22C55E", borderWidth = 5f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 760f, width = 920f, height = 55f, fontSize = 40f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 820f, width = 920f, height = 40f, fontSize = 24f, color = "#22C55E", alignment = "center")
            )
        ),
        // 03 — POWER MANIFESTO: Deep blue & gold, strong typographic campaign poster
        PosterTemplate(
            id = -2103, name = "Power Manifesto", category = "Political",
            baseColor = "#02091A", accentColor = "#EAB308", style = 2103, order = 2, photoPosition = "left",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#010611", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#010611", textColor = "#EAB308", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("YOUR VOICE. YOUR FUTURE.", 550f, 150f, 480f, 45f, size = 22f, color = "#EAB308", alignment = "left"),
                StaticText("THE PEOPLE'S\nCHAMPION", 550f, 215f, 480f, 180f, size = 56f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("CHAMPION")),
                StaticText("Committed to Development,\nTransparency and Equal Rights.", 550f, 440f, 460f, 110f, size = 24f, color = "#CBD5E1", alignment = "left", bold = false, maxLines = 3),
                StaticText("ELECT • EMPOWER • EVOLVE", 550f, 700f, 460f, 45f, size = 20f, color = "#EAB308", alignment = "left", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 50f, y = 150f, width = 470f, height = 600f, shape = "rounded", borderColor = "#EAB308", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 550f, y = 765f, width = 480f, height = 55f, fontSize = 36f, color = "#FFFFFF", alignment = "left"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 550f, y = 825f, width = 480f, height = 40f, fontSize = 22f, color = "#EAB308", alignment = "left")
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 14. REAL ESTATE TEMPLATES (3 property promotion templates)             */
    /* ---------------------------------------------------------------------- */
    private val realEstateTemplates = listOf(
        // 01 — LUXURY PROPERTY: Premium dark navy & gold, property showcase
        PosterTemplate(
            id = -2201, name = "Luxury Property", category = "Real Estate",
            baseColor = "#0A1628", accentColor = "#D4AF37", style = 2201, order = 0, photoPosition = "right",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#050D1A", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#050D1A", textColor = "#D4AF37", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("PREMIUM LISTING", 50f, 150f, 480f, 42f, size = 24f, color = "#D4AF37", alignment = "left"),
                StaticText("YOUR DREAM\nHOME AWAITS", 50f, 210f, 480f, 170f, size = 56f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("DREAM")),
                StaticText("🏠  3 BHK  |  2 Bath  |  1,200 sq.ft", 50f, 430f, 460f, 50f, size = 22f, color = "#D4AF37", alignment = "left", bold = false),
                StaticText("📍 Prime Location • Ready to Move", 50f, 495f, 460f, 45f, size = 20f, color = "#94A3B8", alignment = "left", bold = false),
                StaticText("CONTACT NOW →", 50f, 710f, 300f, 55f, size = 26f, color = "#0A1628", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 550f, y = 150f, width = 490f, height = 590f, shape = "rounded", borderColor = "#D4AF37", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = false, x = 50f, y = 565f, width = 460f, height = 50f, fontSize = 30f, color = "#D4AF37", alignment = "left"),
                TemplateSlot(TemplateField.AMOUNT, required = false, x = 50f, y = 625f, width = 460f, height = 60f, fontSize = 44f, color = "#FFFFFF", alignment = "left"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 550f, y = 760f, width = 490f, height = 90f, fontSize = 20f, color = "#94A3B8", alignment = "center", bold = false, maxLines = 3)
            )
        ),
        // 02 — MODERN APARTMENT: Clean white & teal, minimal property card
        PosterTemplate(
            id = -2202, name = "Modern Apartment", category = "Real Estate",
            baseColor = "#0F2830", accentColor = "#2DD4BF", style = 2202, order = 1, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#081820", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#081820", textColor = "#2DD4BF", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("FOR SALE / FOR RENT", 80f, 155f, 920f, 42f, size = 24f, color = "#2DD4BF", alignment = "center"),
                StaticText("BEAUTIFUL LIVING\nSTARTS HERE", 80f, 215f, 920f, 150f, size = 54f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("LIVING")),
                StaticText("✅ 24/7 Security   ✅ Parking   ✅ Gym", 80f, 730f, 920f, 50f, size = 22f, color = "#67E8F9", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 100f, y = 390f, width = 880f, height = 320f, shape = "rounded", borderColor = "#2DD4BF", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = false, x = 80f, y = 790f, width = 920f, height = 50f, fontSize = 30f, color = "#2DD4BF", alignment = "center"),
                TemplateSlot(TemplateField.AMOUNT, required = false, x = 80f, y = 845f, width = 920f, height = 50f, fontSize = 40f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 80f, y = 380f, width = 920f, height = 40f, fontSize = 22f, color = "#94D8D4", alignment = "center", bold = false)
            )
        ),
        // 03 — COMMERCIAL SPACE: Corporate orange & dark, office/shop promotion
        PosterTemplate(
            id = -2203, name = "Commercial Space", category = "Real Estate",
            baseColor = "#1A0E00", accentColor = "#F97316", style = 2203, order = 2, photoPosition = "left",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0D0700", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0D0700", textColor = "#F97316", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("COMMERCIAL PROPERTY", 550f, 150f, 480f, 42f, size = 22f, color = "#F97316", alignment = "left"),
                StaticText("PRIME\nBUSINESS\nSPACE", 550f, 205f, 480f, 210f, size = 52f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("PRIME")),
                StaticText("📍 High footfall area • Ground floor", 550f, 445f, 460f, 45f, size = 20f, color = "#FED7AA", alignment = "left", bold = false),
                StaticText("Ideal for Showroom, Restaurant\nor Corporate Office", 550f, 505f, 460f, 100f, size = 22f, color = "#94A3B8", alignment = "left", bold = false, maxLines = 3),
                StaticText("ENQUIRE NOW", 550f, 710f, 380f, 55f, size = 28f, color = "#1A0E00", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 50f, y = 150f, width = 470f, height = 590f, shape = "rounded", borderColor = "#F97316", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = false, x = 550f, y = 625f, width = 460f, height = 50f, fontSize = 28f, color = "#F97316", alignment = "left"),
                TemplateSlot(TemplateField.AMOUNT, required = false, x = 550f, y = 680f, width = 460f, height = 55f, fontSize = 40f, color = "#FFFFFF", alignment = "left")
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 15. RESTAURANT TEMPLATES (3 food & dining promotion templates)         */
    /* ---------------------------------------------------------------------- */
    private val restaurantTemplates = listOf(
        // 01 — SPICE DELIGHT: Warm red & gold, vibrant food promotion
        PosterTemplate(
            id = -2301, name = "Spice Delight", category = "Restaurant",
            baseColor = "#1A0505", accentColor = "#EF4444", style = 2301, order = 0, photoPosition = "right",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0D0202", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0D0202", textColor = "#EF4444", showLogo = false, showPhone = true, showWebsite = false),
            staticTexts = listOf(
                StaticText("🍽️  TODAY'S SPECIAL", 50f, 150f, 480f, 45f, size = 26f, color = "#EF4444", alignment = "left"),
                StaticText("TASTE THE\nDIFFERENCE", 50f, 215f, 480f, 170f, size = 60f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("TASTE")),
                StaticText("Freshly prepared with authentic\nrecipes & premium ingredients.", 50f, 440f, 460f, 100f, size = 22f, color = "#FCA5A5", alignment = "left", bold = false, maxLines = 3),
                StaticText("ORDER NOW  →", 50f, 700f, 340f, 60f, size = 28f, color = "#1A0505", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 550f, y = 150f, width = 490f, height = 500f, shape = "rounded", borderColor = "#EF4444", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = false, x = 550f, y = 665f, width = 490f, height = 50f, fontSize = 30f, color = "#EF4444", alignment = "center"),
                TemplateSlot(TemplateField.AMOUNT, required = false, x = 550f, y = 720f, width = 490f, height = 60f, fontSize = 46f, color = "#FDE68A", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 550f, y = 785f, width = 490f, height = 85f, fontSize = 20f, color = "#FCA5A5", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 02 — CAFÉ ELEGANCE: Dark espresso & amber, café/bakery branding
        PosterTemplate(
            id = -2302, name = "Café Elegance", category = "Restaurant",
            baseColor = "#1C0F05", accentColor = "#D97706", style = 2302, order = 1, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0E0702", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0E0702", textColor = "#D97706", showLogo = false, showPhone = true, showWebsite = false),
            staticTexts = listOf(
                StaticText("☕  CAFÉ EXPERIENCE", 80f, 155f, 920f, 45f, size = 28f, color = "#D97706", alignment = "center"),
                StaticText("BREW YOUR\nPERFECT MOMENT", 80f, 220f, 920f, 150f, size = 54f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("PERFECT")),
                StaticText("Artisan coffee • Freshly baked goods\nCozy ambiance • Free WiFi", 80f, 740f, 920f, 100f, size = 22f, color = "#FDE68A", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 190f, y = 390f, width = 700f, height = 330f, shape = "rounded", borderColor = "#D97706", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = false, x = 80f, y = 790f, width = 920f, height = 50f, fontSize = 30f, color = "#D97706", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 80f, y = 375f, width = 920f, height = 40f, fontSize = 22f, color = "#FDE68A", alignment = "center", bold = false),
                TemplateSlot(TemplateField.AMOUNT, required = false, x = 80f, y = 845f, width = 920f, height = 50f, fontSize = 38f, color = "#FFFFFF", alignment = "center")
            )
        ),
        // 03 — GREEN GARDEN KITCHEN: Fresh green & white, healthy/vegan restaurant
        PosterTemplate(
            id = -2303, name = "Green Garden Kitchen", category = "Restaurant",
            baseColor = "#0A1F0A", accentColor = "#4ADE80", style = 2303, order = 2, photoPosition = "left",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#051005", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#051005", textColor = "#4ADE80", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("🌿  HEALTHY EATING", 550f, 150f, 480f, 45f, size = 26f, color = "#4ADE80", alignment = "left"),
                StaticText("EAT FRESH,\nLIVE WELL", 550f, 215f, 480f, 170f, size = 60f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("FRESH")),
                StaticText("100% Organic • Vegan Friendly\nFarm-to-Table Experience", 550f, 440f, 460f, 100f, size = 22f, color = "#BBF7D0", alignment = "left", bold = false, maxLines = 3),
                StaticText("ORDER NOW", 550f, 700f, 320f, 60f, size = 28f, color = "#0A1F0A", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 50f, y = 150f, width = 470f, height = 500f, shape = "rounded", borderColor = "#4ADE80", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = false, x = 550f, y = 620f, width = 460f, height = 50f, fontSize = 28f, color = "#4ADE80", alignment = "left"),
                TemplateSlot(TemplateField.AMOUNT, required = false, x = 550f, y = 675f, width = 460f, height = 60f, fontSize = 42f, color = "#FFFFFF", alignment = "left"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 50f, y = 665f, width = 470f, height = 95f, fontSize = 20f, color = "#86EFAC", alignment = "center", bold = false, maxLines = 3)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 16. HEALTHCARE TEMPLATES (3 doctor & clinic templates)                 */
    /* ---------------------------------------------------------------------- */
    private val healthcareTemplates = listOf(
        // 01 — CARING HANDS: Clean teal & white, doctor profile card
        PosterTemplate(
            id = -2401, name = "Caring Hands", category = "Healthcare",
            baseColor = "#031B1B", accentColor = "#0D9488", style = 2401, order = 0, photoPosition = "right",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.ADDRESS, BrandingField.WEBSITE),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#010E0E", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#010E0E", textColor = "#0D9488", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("YOUR HEALTH. OUR PRIORITY.", 50f, 150f, 480f, 45f, size = 22f, color = "#0D9488", alignment = "left"),
                StaticText("EXPERT\nMEDICAL\nCARE", 50f, 210f, 480f, 210f, size = 58f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("EXPERT")),
                StaticText("🏥 Book Appointment Today", 50f, 460f, 460f, 45f, size = 24f, color = "#99F6E4", alignment = "left", bold = false),
                StaticText("Compassionate. Experienced. Trusted.", 50f, 520f, 460f, 45f, size = 20f, color = "#5EEAD4", alignment = "left", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 550f, y = 150f, width = 490f, height = 580f, shape = "rounded", borderColor = "#0D9488", borderWidth = 4f),
                TemplateSlot(TemplateField.NAME, required = true, x = 50f, y = 700f, width = 480f, height = 55f, fontSize = 36f, color = "#FFFFFF", alignment = "left"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 50f, y = 760f, width = 480f, height = 40f, fontSize = 22f, color = "#0D9488", alignment = "left"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 550f, y = 745f, width = 490f, height = 95f, fontSize = 20f, color = "#99F6E4", alignment = "center", bold = false, maxLines = 3)
            )
        ),
        // 02 — CLINIC SPOTLIGHT: Blue medical, centered clinic promotion
        PosterTemplate(
            id = -2402, name = "Clinic Spotlight", category = "Healthcare",
            baseColor = "#03122A", accentColor = "#3B82F6", style = 2402, order = 1, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#010916", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#010916", textColor = "#3B82F6", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("MULTI-SPECIALITY CLINIC", 80f, 155f, 920f, 45f, size = 26f, color = "#3B82F6", alignment = "center"),
                StaticText("HEALING WITH\nHEART & SCIENCE", 80f, 220f, 920f, 150f, size = 52f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("HEALING")),
                StaticText("🩺 General Medicine  🦷 Dental  🦴 Ortho", 80f, 720f, 920f, 50f, size = 22f, color = "#93C5FD", alignment = "center", bold = false),
                StaticText("📅 MON–SAT  |  9 AM – 8 PM", 80f, 775f, 920f, 45f, size = 20f, color = "#60A5FA", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 290f, y = 390f, width = 500f, height = 310f, shape = "rounded", borderColor = "#3B82F6", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 810f, width = 920f, height = 50f, fontSize = 36f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 865f, width = 920f, height = 40f, fontSize = 22f, color = "#3B82F6", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 80f, y = 370f, width = 920f, height = 40f, fontSize = 22f, color = "#93C5FD", alignment = "center", bold = false)
            )
        ),
        // 03 — WELLNESS PREMIUM: Purple & white, premium wellness/hospital brand
        PosterTemplate(
            id = -2403, name = "Wellness Premium", category = "Healthcare",
            baseColor = "#100B1F", accentColor = "#8B5CF6", style = 2403, order = 2, photoPosition = "left",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#080512", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#080512", textColor = "#8B5CF6", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WELLNESS & CARE CENTER", 550f, 150f, 480f, 45f, size = 22f, color = "#8B5CF6", alignment = "left"),
                StaticText("LIVE\nHEALTHY.\nLIVE LONG.", 550f, 210f, 480f, 210f, size = 52f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("HEALTHY")),
                StaticText("Advanced treatments with\npersonalized care plans.", 550f, 460f, 460f, 100f, size = 22f, color = "#C4B5FD", alignment = "left", bold = false, maxLines = 3),
                StaticText("✅ NABH Accredited  |  ✅ Insurance Accepted", 550f, 590f, 460f, 45f, size = 18f, color = "#A78BFA", alignment = "left", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = true, x = 50f, y = 150f, width = 470f, height = 560f, shape = "rounded", borderColor = "#8B5CF6", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = true, x = 550f, y = 680f, width = 480f, height = 55f, fontSize = 36f, color = "#FFFFFF", alignment = "left"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 550f, y = 740f, width = 480f, height = 40f, fontSize = 22f, color = "#8B5CF6", alignment = "left"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 50f, y = 725f, width = 470f, height = 100f, fontSize = 20f, color = "#C4B5FD", alignment = "center", bold = false, maxLines = 3)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 17. EDUCATION TEMPLATES (3 institute & course admission templates)     */
    /* ---------------------------------------------------------------------- */
    private val educationTemplates = listOf(
        // 01 — CAMPUS EXCELLENCE: Navy & gold, academic institution prestige
        PosterTemplate(
            id = -2501, name = "Campus Excellence", category = "Education",
            baseColor = "#06102A", accentColor = "#F59E0B", style = 2501, order = 0, photoPosition = "right",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#030819", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#030819", textColor = "#F59E0B", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("ADMISSIONS OPEN 2025–26", 50f, 150f, 480f, 45f, size = 24f, color = "#F59E0B", alignment = "left"),
                StaticText("SHAPE YOUR\nFUTURE TODAY", 50f, 215f, 480f, 170f, size = 56f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("FUTURE")),
                StaticText("✅ Industry-Expert Faculty\n✅ 100% Placement Support\n✅ Globally Recognized Degree", 50f, 440f, 460f, 140f, size = 21f, color = "#FDE68A", alignment = "left", bold = false, maxLines = 4),
                StaticText("ENROLL NOW →", 50f, 710f, 340f, 60f, size = 28f, color = "#06102A", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 550f, y = 150f, width = 490f, height = 510f, shape = "rounded", borderColor = "#F59E0B", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = true, x = 550f, y = 675f, width = 490f, height = 55f, fontSize = 34f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 550f, y = 735f, width = 490f, height = 40f, fontSize = 22f, color = "#F59E0B", alignment = "center"),
                TemplateSlot(TemplateField.AMOUNT, required = false, x = 50f, y = 600f, width = 460f, height = 55f, fontSize = 36f, color = "#F59E0B", alignment = "left"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 50f, y = 660f, width = 460f, height = 40f, fontSize = 20f, color = "#94A3B8", alignment = "left", bold = false)
            )
        ),
        // 02 — SKILLS ACADEMY: Vibrant purple & cyan, professional course promotion
        PosterTemplate(
            id = -2502, name = "Skills Academy", category = "Education",
            baseColor = "#120828", accentColor = "#06B6D4", style = 2502, order = 1, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#080418", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#080418", textColor = "#06B6D4", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("NEW BATCH STARTING SOON", 80f, 155f, 920f, 45f, size = 26f, color = "#06B6D4", alignment = "center"),
                StaticText("MASTER YOUR\nSKILLS NOW", 80f, 220f, 920f, 150f, size = 58f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("MASTER")),
                StaticText("💻 Coding  📊 Data Science  🎨 Design  📱 App Dev", 80f, 710f, 920f, 50f, size = 22f, color = "#67E8F9", alignment = "center", bold = false),
                StaticText("Online & Offline Batches Available", 80f, 765f, 920f, 45f, size = 22f, color = "#A5F3FC", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 290f, y = 390f, width = 500f, height = 300f, shape = "rounded", borderColor = "#06B6D4", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 810f, width = 920f, height = 50f, fontSize = 34f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.AMOUNT, required = false, x = 80f, y = 865f, width = 920f, height = 45f, fontSize = 32f, color = "#06B6D4", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 80f, y = 375f, width = 920f, height = 40f, fontSize = 22f, color = "#67E8F9", alignment = "center", bold = false)
            )
        ),
        // 03 — KNOWLEDGE GATEWAY: Green & white, school/university admission poster
        PosterTemplate(
            id = -2503, name = "Knowledge Gateway", category = "Education",
            baseColor = "#041A0A", accentColor = "#16A34A", style = 2503, order = 2, photoPosition = "left",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#020E05", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#020E05", textColor = "#16A34A", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("🎓 ADMISSIONS OPEN", 550f, 155f, 480f, 45f, size = 26f, color = "#16A34A", alignment = "left"),
                StaticText("YOUR JOURNEY\nTO SUCCESS\nSTARTS HERE", 550f, 220f, 480f, 210f, size = 48f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("SUCCESS")),
                StaticText("🏆 Top Ranked Institute\n📚 Expert Faculty | Small Batches\n🌐 100% Digital Learning Tools", 550f, 465f, 460f, 140f, size = 20f, color = "#BBF7D0", alignment = "left", bold = false, maxLines = 4),
                StaticText("Last Date: 31st October", 550f, 625f, 460f, 45f, size = 22f, color = "#4ADE80", alignment = "left", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 50f, y = 150f, width = 470f, height = 560f, shape = "rounded", borderColor = "#16A34A", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = true, x = 550f, y = 680f, width = 480f, height = 55f, fontSize = 32f, color = "#FFFFFF", alignment = "left"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 550f, y = 740f, width = 480f, height = 40f, fontSize = 20f, color = "#16A34A", alignment = "left"),
                TemplateSlot(TemplateField.AMOUNT, required = false, x = 50f, y = 730f, width = 470f, height = 50f, fontSize = 32f, color = "#4ADE80", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 50f, y = 785f, width = 470f, height = 45f, fontSize = 20f, color = "#86EFAC", alignment = "center", bold = false)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 18. JOB VACANCY TEMPLATES (3 hiring & recruitment templates)           */
    /* ---------------------------------------------------------------------- */
    private val jobVacancyTemplates = listOf(
        // 01 — HIRING NOW: Bold red & dark, urgent hiring campaign
        PosterTemplate(
            id = -2601, name = "Hiring Now", category = "Job Vacancy",
            baseColor = "#150306", accentColor = "#DC2626", style = 2601, order = 0, photoPosition = "right",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#0A0103", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#0A0103", textColor = "#DC2626", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("🔥  WE'RE HIRING!", 50f, 150f, 480f, 55f, size = 32f, color = "#DC2626", alignment = "left"),
                StaticText("JOIN OUR\nWINNING\nTEAM", 50f, 225f, 480f, 210f, size = 60f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("WINNING")),
                StaticText("📋 Requirements:\n• 1-3 Years Experience\n• Strong Communication Skills\n• Team Player & Problem Solver", 50f, 475f, 460f, 160f, size = 20f, color = "#FCA5A5", alignment = "left", bold = false, maxLines = 5),
                StaticText("APPLY NOW →", 50f, 710f, 340f, 60f, size = 28f, color = "#150306", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 550f, y = 150f, width = 490f, height = 520f, shape = "rounded", borderColor = "#DC2626", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = true, x = 550f, y = 685f, width = 490f, height = 55f, fontSize = 36f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 550f, y = 745f, width = 490f, height = 40f, fontSize = 22f, color = "#DC2626", alignment = "center"),
                TemplateSlot(TemplateField.AMOUNT, required = false, x = 550f, y = 790f, width = 490f, height = 50f, fontSize = 30f, color = "#FCA5A5", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 50f, y = 648f, width = 460f, height = 42f, fontSize = 22f, color = "#DC2626", alignment = "left", bold = false)
            )
        ),
        // 02 — CAREER LAUNCH: Professional navy & gold, corporate vacancy
        PosterTemplate(
            id = -2602, name = "Career Launch", category = "Job Vacancy",
            baseColor = "#080F20", accentColor = "#FBBF24", style = 2602, order = 1, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#040911", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#040911", textColor = "#FBBF24", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("JOB OPPORTUNITY", 80f, 155f, 920f, 50f, size = 32f, color = "#FBBF24", alignment = "center"),
                StaticText("BUILD YOUR\nCARREER WITH US", 80f, 225f, 920f, 150f, size = 54f, color = "#FFFFFF", alignment = "center", highlightWords = listOf("CAREER")),
                StaticText("📍 Location: Your City\n⏰ Timing: 9AM – 6PM\n💰 Salary: As per Experience", 80f, 700f, 920f, 130f, size = 22f, color = "#FDE68A", alignment = "center", bold = false, maxLines = 4)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 290f, y = 395f, width = 500f, height = 290f, shape = "rounded", borderColor = "#FBBF24", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = true, x = 80f, y = 395f, width = 920f, height = 55f, fontSize = 36f, color = "#FBBF24", alignment = "center"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 80f, y = 455f, width = 920f, height = 40f, fontSize = 24f, color = "#FFFFFF", alignment = "center"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 80f, y = 825f, width = 920f, height = 75f, fontSize = 20f, color = "#94A3B8", alignment = "center", bold = false, maxLines = 2)
            )
        ),
        // 03 — TALENT HUNT: Teal & dark, modern start-up hiring poster
        PosterTemplate(
            id = -2603, name = "Talent Hunt", category = "Job Vacancy",
            baseColor = "#011F1A", accentColor = "#14B8A6", style = 2603, order = 2, photoPosition = "left",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE, BrandingField.WEBSITE, BrandingField.ADDRESS),
            header = BrandingBand(enabled = true, height = 110f, backgroundColor = "#000F0D", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 145f, backgroundColor = "#000F0D", textColor = "#14B8A6", showLogo = false, showPhone = true, showWebsite = true),
            staticTexts = listOf(
                StaticText("WE ARE GROWING!", 550f, 155f, 480f, 45f, size = 26f, color = "#14B8A6", alignment = "left"),
                StaticText("FIND YOUR\nDREAM JOB\nHERE", 550f, 220f, 480f, 210f, size = 52f, color = "#FFFFFF", alignment = "left", highlightWords = listOf("DREAM")),
                StaticText("✅ Flexible Hours  ✅ Great Culture\n✅ Growth Opportunities\n✅ Competitive Salary", 550f, 470f, 460f, 140f, size = 20f, color = "#99F6E4", alignment = "left", bold = false, maxLines = 5),
                StaticText("SEND YOUR CV NOW", 550f, 705f, 420f, 60f, size = 26f, color = "#011F1A", alignment = "center")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 50f, y = 150f, width = 470f, height = 560f, shape = "rounded", borderColor = "#14B8A6", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = true, x = 550f, y = 680f, width = 480f, height = 55f, fontSize = 34f, color = "#FFFFFF", alignment = "left"),
                TemplateSlot(TemplateField.DESIGNATION, required = false, x = 550f, y = 740f, width = 480f, height = 40f, fontSize = 22f, color = "#14B8A6", alignment = "left"),
                TemplateSlot(TemplateField.AMOUNT, required = false, x = 550f, y = 785f, width = 480f, height = 50f, fontSize = 30f, color = "#5EEAD4", alignment = "left"),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 50f, y = 725f, width = 470f, height = 90f, fontSize = 20f, color = "#99F6E4", alignment = "center", bold = false, maxLines = 3)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 19. QUOTES TEMPLATES (3 inspirational quote templates)                 */
    /* ---------------------------------------------------------------------- */
    private val quotesTemplates = listOf(
        // 01 — GOLDEN WISDOM: Premium dark & gold, elegant quote poster
        PosterTemplate(
            id = -2701, name = "Golden Wisdom", category = "Quotes",
            baseColor = "#0D0A02", accentColor = "#D97706", style = 2701, order = 0, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME),
            header = BrandingBand(enabled = true, height = 90f, backgroundColor = "#080601", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = false, height = 80f, backgroundColor = "#080601", textColor = "#D97706"),
            staticTexts = listOf(
                StaticText("❝", 80f, 175f, 200f, 120f, size = 110f, color = "#D97706", alignment = "left"),
                StaticText("❞", 800f, 650f, 200f, 120f, size = 110f, color = "#D97706", alignment = "right")
            ),
            slots = listOf(
                TemplateSlot(TemplateField.QUOTE, required = true, x = 80f, y = 290f, width = 920f, height = 380f, fontSize = 42f, color = "#FFFFFF", alignment = "center", bold = false, maxLines = 6),
                TemplateSlot(TemplateField.NAME, required = false, x = 80f, y = 790f, width = 920f, height = 55f, fontSize = 30f, color = "#D97706", alignment = "center", bold = false)
            )
        ),
        // 02 — MIDNIGHT INSPIRE: Deep navy, minimal typographic quote layout
        PosterTemplate(
            id = -2702, name = "Midnight Inspire", category = "Quotes",
            baseColor = "#020918", accentColor = "#6366F1", style = 2702, order = 1, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME),
            header = BrandingBand(enabled = true, height = 90f, backgroundColor = "#010510", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = false, height = 80f, backgroundColor = "#010510", textColor = "#6366F1"),
            staticTexts = listOf(
                StaticText("DAILY INSPIRATION", 80f, 175f, 920f, 45f, size = 24f, color = "#6366F1", alignment = "center"),
                StaticText("— ✦ —", 80f, 800f, 920f, 45f, size = 28f, color = "#6366F1", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.QUOTE, required = true, x = 80f, y = 240f, width = 920f, height = 540f, fontSize = 44f, color = "#FFFFFF", alignment = "center", bold = false, maxLines = 7),
                TemplateSlot(TemplateField.NAME, required = false, x = 80f, y = 855f, width = 920f, height = 50f, fontSize = 26f, color = "#A5B4FC", alignment = "center", bold = false)
            )
        ),
        // 03 — ROSE ELEGANCE: Blush pink & dark, lifestyle quote card
        PosterTemplate(
            id = -2703, name = "Rose Elegance", category = "Quotes",
            baseColor = "#1A0510", accentColor = "#EC4899", style = 2703, order = 2, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME),
            header = BrandingBand(enabled = true, height = 90f, backgroundColor = "#0D0208", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = false, height = 80f, backgroundColor = "#0D0208", textColor = "#EC4899"),
            staticTexts = listOf(
                StaticText("✿ ────── ✿ ────── ✿", 80f, 175f, 920f, 45f, size = 26f, color = "#EC4899", alignment = "center", bold = false),
                StaticText("✿ ────── ✿ ────── ✿", 80f, 815f, 920f, 45f, size = 26f, color = "#EC4899", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.QUOTE, required = true, x = 80f, y = 240f, width = 920f, height = 530f, fontSize = 42f, color = "#FFFFFF", alignment = "center", bold = false, maxLines = 7),
                TemplateSlot(TemplateField.NAME, required = false, x = 80f, y = 790f, width = 920f, height = 50f, fontSize = 28f, color = "#F9A8D4", alignment = "center", bold = false)
            )
        )
    )

    /* ---------------------------------------------------------------------- */
    /* 20. DEVOTIONAL TEMPLATES (3 spiritual & devotional greeting templates) */
    /* ---------------------------------------------------------------------- */
    private val devotionalTemplates = listOf(
        // 01 — DIVINE BLESSING: Deep saffron & gold, Hindu devotional poster
        PosterTemplate(
            id = -2801, name = "Divine Blessing", category = "Devotional",
            baseColor = "#1A0900", accentColor = "#F97316", style = 2801, order = 0, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE),
            header = BrandingBand(enabled = true, height = 100f, backgroundColor = "#0D0400", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 115f, backgroundColor = "#0D0400", textColor = "#F97316", showLogo = false, showPhone = true),
            staticTexts = listOf(
                StaticText("🙏  JAY SHREE KRISHNA  🙏", 80f, 170f, 920f, 60f, size = 36f, color = "#F97316", alignment = "center"),
                StaticText("May the divine blessings of\nthe almighty shower upon you\nand your family always.", 80f, 390f, 920f, 190f, size = 30f, color = "#FDE68A", alignment = "center", bold = false, maxLines = 4),
                StaticText("ॐ नमः शिवाय", 80f, 605f, 920f, 65f, size = 44f, color = "#F97316", alignment = "center"),
                StaticText("🌸  Blessings & Love  🌸", 80f, 700f, 920f, 50f, size = 28f, color = "#FED7AA", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 340f, y = 250f, width = 400f, height = 120f, shape = "rounded", borderColor = "#F97316", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = false, x = 80f, y = 760f, width = 920f, height = 55f, fontSize = 30f, color = "#F97316", alignment = "center", bold = false)
            )
        ),
        // 02 — SACRED LIGHT: Deep maroon & gold, spiritual meditation poster
        PosterTemplate(
            id = -2802, name = "Sacred Light", category = "Devotional",
            baseColor = "#1A0208", accentColor = "#EF4444", style = 2802, order = 1, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE),
            header = BrandingBand(enabled = true, height = 100f, backgroundColor = "#0D0104", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 115f, backgroundColor = "#0D0104", textColor = "#EF4444", showLogo = false, showPhone = true),
            staticTexts = listOf(
                StaticText("✝  GOD'S GRACE  ✝", 80f, 170f, 920f, 60f, size = 36f, color = "#EF4444", alignment = "center"),
                StaticText("\"For I know the plans I have\nfor you, plans to prosper\nyou and not to harm you.\"", 80f, 360f, 920f, 230f, size = 32f, color = "#FFFFFF", alignment = "center", bold = false, maxLines = 4),
                StaticText("— Jeremiah 29:11", 80f, 615f, 920f, 45f, size = 24f, color = "#EF4444", alignment = "center", bold = false),
                StaticText("God Bless You Always  🕊️", 80f, 700f, 920f, 55f, size = 28f, color = "#FCA5A5", alignment = "center", bold = false)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 340f, y = 228f, width = 400f, height = 118f, shape = "rounded", borderColor = "#EF4444", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = false, x = 80f, y = 770f, width = 920f, height = 55f, fontSize = 30f, color = "#FCA5A5", alignment = "center", bold = false)
            )
        ),
        // 03 — MORNING PRAYER: Soft amber & cream, universal spiritual greeting
        PosterTemplate(
            id = -2803, name = "Morning Prayer", category = "Devotional",
            baseColor = "#1A1000", accentColor = "#CA8A04", style = 2803, order = 2, photoPosition = "center",
            supportedBranding = setOf(BrandingField.LOGO, BrandingField.BUSINESS_NAME, BrandingField.PHONE),
            header = BrandingBand(enabled = true, height = 100f, backgroundColor = "#0D0800", textColor = "#FFFFFF", showLogo = true, showCompanyName = true),
            footer = BrandingBand(enabled = true, height = 115f, backgroundColor = "#0D0800", textColor = "#CA8A04", showLogo = false, showPhone = true),
            staticTexts = listOf(
                StaticText("🌅  MORNING BLESSINGS  🌅", 80f, 170f, 920f, 60f, size = 34f, color = "#CA8A04", alignment = "center"),
                StaticText("Start your day with\ngratitude and faith.\nBlessings are on their way.", 80f, 380f, 920f, 210f, size = 36f, color = "#FFFFFF", alignment = "center", bold = false, maxLines = 4),
                StaticText("🙏 May your day be filled\nwith peace and joy 🙏", 80f, 630f, 920f, 110f, size = 28f, color = "#FDE68A", alignment = "center", bold = false, maxLines = 3)
            ),
            slots = listOf(
                TemplateSlot(TemplateField.PHOTO, required = false, x = 340f, y = 233f, width = 400f, height = 122f, shape = "rounded", borderColor = "#CA8A04", borderWidth = 3f),
                TemplateSlot(TemplateField.NAME, required = false, x = 80f, y = 770f, width = 920f, height = 55f, fontSize = 30f, color = "#CA8A04", alignment = "center", bold = false),
                TemplateSlot(TemplateField.MESSAGE, required = false, x = 80f, y = 835f, width = 920f, height = 45f, fontSize = 22f, color = "#FDE68A", alignment = "center", bold = false)
            )
        )
    )
}
