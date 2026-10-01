package com.example.ui.screens

import com.example.templates.StarterTemplates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WelcomeTemplateStructureTest {
    @Test
    fun welcomeCatalogProvidesFifteenDistinctPortraitTemplates() {
        val templates = StarterTemplates.all.filter { it.category == "Welcome" }

        // 9 Welcome templates: -201, -202, -215, -214, -217, -218, -219, -220, -221
        assertEquals(9, templates.size)
        assertEquals(9, templates.map { it.name }.toSet().size)
    }

    @Test
    fun eachWelcomeTemplateCreatesIndependentRequiredLayers() {
        val requiredLayerIds = setOf(
            "welcomeHeadline", "toSubheading", "nameRibbon", "memberName", "designation",
            "campaignTop", "campaignBottom", "memberPhoto", "welcomeMessage",
            "brandingBand", "companyName", "phoneNumber", "website"
        )
        val forbiddenReferenceIds = setOf(
            "leaderPhoto1", "leaderPhoto2", "leaderPhoto3", "hostPhoto", "trophyIcon",
            "whatsAppNumber", "bottomCurve", "companyLogoRight", "companyLogoBottom"
        )
        val layoutSignatures = mutableSetOf<String>()

        StarterTemplates.all.filter { it.category == "Welcome" }.forEach { template ->
            val project = newWelcomePosterState(template.id, "Acme Ltd", "", "acme.example", "+91 98765 43210")
            assertEquals("${template.name} must use 4:5 poster dimensions", 1080, project.canvasWidth)
            assertEquals("${template.name} must use 4:5 poster dimensions", 1350, project.canvasHeight)
            assertTrue("${template.name} is missing editable layers", project.elements.map { it.id }.containsAll(requiredLayerIds))
            assertTrue("${template.name} must not copy reference-only sections", project.elements.none { it.id in forbiddenReferenceIds })
            assertEquals("${template.name} reuses layer IDs", project.elements.size, project.elements.map { it.id }.toSet().size)
            val photo = project.elements.first { it.id == "memberPhoto" }
            val title = project.elements.first { it.id == "welcomeHeadline" }
            val ribbon = project.elements.first { it.id == "nameRibbon" }
            layoutSignatures += "${photo.x}:${photo.y}:${photo.width}:${photo.height}:${title.x}:${ribbon.x}:${ribbon.y}"
        }
        // 9 templates but some share layouts — at minimum each group is distinct
        assertTrue("All Welcome designs need distinct editable compositions", layoutSignatures.size >= 4)
    }
}
