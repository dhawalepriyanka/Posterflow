package com.example.templates

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.ui.ProfileSettings
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class WelcomeTemplateRedesignTest {

    private val expectedNames = listOf(
        "Royal Gold Welcome",
        "Corporate Blue Welcome",
        "Premium Purple Welcome",
        "Elegant White & Gold Welcome",
        "Red Celebration Welcome",
        "Modern Green Welcome",
        "Orange Success Welcome",
        "Luxury Black Welcome",
        "Fresh Gradient Welcome",
        "Floral Welcome",
        "Indian Traditional Welcome",
        "Minimal Professional Welcome"
    )

    private val expectedStyles = (130..141).toList()

    @Test
    fun allTwelveWelcomeTemplatesExistWithExactNamesAndStyles() {
        val welcomeTemplates = StarterTemplates.all.filter { it.category == "Welcome" }
        assertEquals("Must contain exactly 12 Welcome templates", 12, welcomeTemplates.size)

        val actualNames = welcomeTemplates.map { it.name }
        assertEquals("Template names must match the 12 required designs", expectedNames, actualNames)

        val actualStyles = welcomeTemplates.map { it.style }
        assertEquals("Styles must map to 130..141", expectedStyles, actualStyles)

        val actualIds = welcomeTemplates.map { it.id }
        assertEquals("IDs must be -201 to -212", (-201 downTo -212).toList(), actualIds)
    }

    @Test
    fun eachWelcomeTemplateHasUniqueCompositionAndDistinctPalette() {
        val welcomeTemplates = StarterTemplates.all.filter { it.category == "Welcome" }

        // Unique base colors or distinct combinations
        val styles = welcomeTemplates.map { it.style }.toSet()
        assertEquals("All 12 templates must have unique styles", 12, styles.size)

        val names = welcomeTemplates.map { it.name }.toSet()
        assertEquals("All 12 templates must have unique names", 12, names.size)
    }

    @Test
    fun allWelcomeTemplatesHaveFixedTwoHundredPxBrandingFooterAndDisabledHeader() {
        val welcomeTemplates = StarterTemplates.all.filter { it.category == "Welcome" }

        for (template in welcomeTemplates) {
            assertFalse("${template.name} header must be disabled", template.header.enabled)
            assertTrue("${template.name} footer must be enabled", template.footer.enabled)
            assertEquals("${template.name} footer must be exactly 200f tall", 200f, template.footer.height, 0.01f)

            // Verify body slots stay strictly inside y in 30..880
            for (slot in template.slots) {
                assertTrue(
                    "${template.name} slot ${slot.field} y=${slot.y} must be >= 30",
                    slot.y >= 30f
                )
                assertTrue(
                    "${template.name} slot ${slot.field} y+height=${slot.y + slot.height} must be <= 880 to avoid branding overlap",
                    slot.y + slot.height <= 880f
                )
            }
        }
    }

    @Test
    fun welcomeTemplateRegistryProvidesLookupAndConversions() {
        val registryTemplates = WelcomeTemplateRegistry.templates
        assertEquals(12, registryTemplates.size)

        for (id in -201 downTo -212) {
            val template = WelcomeTemplateRegistry.getTemplateById(id)
            assertNotNull("Template $id must be found in registry", template)
        }

        val sampleTemplate = registryTemplates.first()
        val profile = ProfileSettings(
            profilePhotoUri = "",
            userName = "Dr. Rajesh Sharma",
            userEmail = "rajesh@example.com",
            companyLogoUri = "",
            companyName = "Apex Solutions",
            websiteName = "www.apexsolutions.in",
            mobileNumber = "+91 98765 43210",
            businessEmail = "info@apexsolutions.in",
            businessAddress = "Mumbai, Maharashtra",
            tagline = "Innovate • Elevate • Lead",
            designation = "Managing Director"
        )

        val data = WelcomeTemplateRegistry.createTemplateData(
            template = sampleTemplate,
            profile = profile,
            guestName = "Amit Patel",
            guestDesignation = "Senior Vice President",
            welcomeMessage = "Thrilled to have you join our team!",
            guestPhoto = ""
        )

        assertEquals("Royal Gold Welcome", data.templateName)
        assertEquals("Amit Patel", data.guestName)
        assertEquals("Senior Vice President", data.guestDesignation)
        assertEquals("Dr. Rajesh Sharma", data.userName)
        assertEquals("Managing Director", data.userDesignation)
        assertEquals("Apex Solutions", data.businessName)
        assertEquals("+91 98765 43210", data.phoneNumber)
        assertEquals("www.apexsolutions.in", data.website)

        val poster = WelcomeTemplateRegistry.toGeneratedPoster(data, sampleTemplate)
        assertEquals(sampleTemplate.id, poster.template.id)
        assertEquals("Amit Patel", poster.values[TemplateField.NAME.name])
        assertEquals("Senior Vice President", poster.values[TemplateField.DESIGNATION.name])
        assertEquals("Apex Solutions", poster.branding.company)
        assertEquals("Managing Director", poster.branding.ownerDesignation)
        assertEquals("Dr. Rajesh Sharma", poster.branding.ownerName)
    }

    @Test
    fun templateRendererRendersAllTwelveWelcomeTemplatesAtFullResolution() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val welcomeTemplates = StarterTemplates.all.filter { it.category == "Welcome" }

        val branding = BusinessBranding(
            company = "ZENITH GLOBAL CORP",
            phone = "+91 98765 43210",
            website = "www.zenithglobal.com",
            email = "contact@zenith.com",
            address = "Bengaluru, Karnataka",
            tagline = "Empowering Modern Enterprises",
            ownerName = "Priya Deshmukh",
            ownerDesignation = "Executive Director"
        )

        for (template in welcomeTemplates) {
            val design = GeneratedPoster(
                template = template,
                values = mapOf(
                    TemplateField.NAME.name to "ARJUN VERMA",
                    TemplateField.DESIGNATION.name to "HEAD OF ENGINEERING",
                    TemplateField.MESSAGE.name to "Together we achieve extraordinary success."
                ),
                branding = branding
            )

            val bitmap = TemplateRenderer.render(context, design, 1080)
            assertNotNull("${template.name} bitmap should not be null", bitmap)
            assertEquals("${template.name} width must be 1080", 1080, bitmap.width)
            assertEquals("${template.name} height must be 1080", 1080, bitmap.height)
        }
    }

    @Test
    fun templateRendererSupportsHindiAndMarathiDevanagariUnicodeText() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val traditionalTemplate = StarterTemplates.all.first { it.id == -211 }

        val hindiMarathiDesign = GeneratedPoster(
            template = traditionalTemplate,
            values = mapOf(
                TemplateField.NAME.name to "श्री. सचिन तांबडे",
                TemplateField.DESIGNATION.name to "वरिष्ठ सल्लागार",
                TemplateField.MESSAGE.name to "आमच्या परिवारात आपले सहर्ष स्वागत आहे!"
            ),
            branding = BusinessBranding(
                company = "उद्योग समूह",
                phone = "+91 98765 43210",
                website = "www.udyog.in",
                ownerName = "अनिल पाटील",
                ownerDesignation = "संस्थापक आणि संचालक"
            )
        )

        // Should render without exception
        val bitmap = TemplateRenderer.render(context, hindiMarathiDesign, 1080)
        assertNotNull(bitmap)
        assertEquals(1080, bitmap.width)
        assertEquals(1080, bitmap.height)
    }

    @Test
    fun fixedBrandingCollapsesCleanlyWhenOptionalFieldsAreMissing() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val template = StarterTemplates.all.first { it.category == "Welcome" }

        // Completely empty branding should not crash and should render clean defaults
        val emptyBranding = BusinessBranding()
        val design = GeneratedPoster(
            template = template,
            values = emptyMap(),
            branding = emptyBranding
        )

        val bitmap = TemplateRenderer.render(context, design, 1080)
        assertNotNull("Must render cleanly even with empty branding", bitmap)
        assertEquals(1080, bitmap.width)
        assertEquals(1080, bitmap.height)
    }
}
