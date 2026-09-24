package com.example.ui.screens

import com.example.templates.StarterTemplates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WelcomeTemplateStructureTest {
    @Test
    fun welcomeCatalogProvidesTwelveDistinctPortraitTemplates() {
        val templates = StarterTemplates.all.filter { it.category == "Welcome" }

        assertEquals(12, templates.size)
        assertEquals(12, templates.map { it.name }.toSet().size)
    }

    @Test
    fun eachWelcomeTemplateCreatesIndependentRequiredLayers() {
        val requiredLayerIds = setOf(
            "companyLogo", "companyName", "companyTagline",
            "memberPhoto", "memberName", "designation",
            "leaderPhoto1", "leaderName1", "leaderPhoto2", "leaderName2", "leaderPhoto3", "leaderName3",
            "hostPhoto", "hostName", "hostDesignation", "hostCompany",
            "phoneNumber", "whatsAppNumber", "website", "address",
            "quote", "successTagline", "trophyIcon", "decorativeRibbon"
        )

        StarterTemplates.all.filter { it.category == "Welcome" }.forEach { template ->
            val project = newWelcomePosterState(template.id, "Acme Ltd", "", "acme.example", "+91 98765 43210")
            assertEquals("${template.name} must use 4:5 poster dimensions", 1080, project.canvasWidth)
            assertEquals("${template.name} must use 4:5 poster dimensions", 1350, project.canvasHeight)
            assertTrue("${template.name} is missing editable layers", project.elements.map { it.id }.containsAll(requiredLayerIds))
            assertEquals("${template.name} reuses layer IDs", project.elements.size, project.elements.map { it.id }.toSet().size)
        }
    }
}
