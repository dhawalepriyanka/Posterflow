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
        "Modern Green Welcome",
        "Ocean Cyan Welcome",
        "Royal Blue Corporate Welcome",
        "Purple Gold Premium Welcome",
        "Purple Glow Welcome",
        "Golden Wave Welcome",
        "Corporate Blue Wave Welcome",
        "Botanical Green Welcome",
        "Dynamic Marathi Welcome"
    )

    private val expectedStyles = listOf(141, 142, 144, 143, 147, 148, 149, 150, 151)
    private val expectedIds = listOf(-201, -202, -215, -214, -217, -218, -219, -220, -221)

    @Test
    fun allWelcomeTemplatesExistWithExactNamesAndStyles() {
        val welcomeTemplates = StarterTemplates.all.filter { it.category == "Welcome" }
        assertEquals("Must contain 9 Welcome templates", 9, welcomeTemplates.size)

        val actualNames = welcomeTemplates.map { it.name }
        assertEquals("Template names must match the required designs", expectedNames, actualNames)

        val actualStyles = welcomeTemplates.map { it.style }
        assertEquals("Styles must map correctly", expectedStyles, actualStyles)

        val actualIds = welcomeTemplates.map { it.id }
        assertEquals("IDs must map correctly", expectedIds, actualIds)
    }

    @Test
    fun eachWelcomeTemplateHasUniqueCompositionAndDistinctPalette() {
        val welcomeTemplates = StarterTemplates.all.filter { it.category == "Welcome" }

        val styles = welcomeTemplates.map { it.style }.toSet()
        assertEquals("All 9 templates must have unique styles", 9, styles.size)

        val names = welcomeTemplates.map { it.name }.toSet()
        assertEquals("All 9 templates must have unique names", 9, names.size)

        // All 4 templates expose exactly the 3 dynamic fields: Photo, Name, Designation
        for (template in welcomeTemplates) {
            assertEquals(
                listOf(TemplateField.PHOTO, TemplateField.NAME, TemplateField.DESIGNATION),
                template.visibleFields
            )
        }
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
        assertEquals(9, registryTemplates.size)

        for (id in expectedIds) {
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

        assertEquals("Modern Green Welcome", data.templateName)
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
    fun templateRendererRendersAllFifteenWelcomeTemplatesAtFullResolution() {
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
        val traditionalTemplate = StarterTemplates.all.first { it.category == "Welcome" }

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

    @Test
    fun style143KeepsItsExistingIdentityAndEditableContract() {
        val template = StarterTemplates.all.single { it.id == -214 }

        assertEquals("Welcome", template.category)
        assertEquals(143, template.style)
        assertEquals(1080, template.canvasWidth)
        assertEquals(1080, template.canvasHeight)
        assertEquals(
            listOf(
                TemplateField.PHOTO,
                TemplateField.NAME,
                TemplateField.DESIGNATION
            ),
            template.visibleFields
        )
    }

    @Test
    fun style143SavedDesignRoundTripPreservesTextPhotoCropBrandingAndTemplateIdentity() {
        val template = StarterTemplates.all.single { it.id == -214 }
        val original = GeneratedPoster(
            template = template,
            values = mapOf(
                TemplateField.NAME.name to "Aarav Mehta",
                TemplateField.DESIGNATION.name to "Product Designer",
                TemplateField.QUOTE.name to "Together We Grow",
                TemplateField.MESSAGE.name to "Welcome to a new chapter of shared success."
            ),
            photo = "/private/template_assets/member-transparent.png",
            originalPhoto = "/private/template_assets/member.jpg",
            backgroundRemovedPhoto = "/private/template_assets/member-transparent.png",
            backgroundRemoved = true,
            crop = PhotoCrop(scale = 2.2f, panX = .35f, panY = -.2f),
            branding = BusinessBranding(
                logo = "/private/template_assets/logo.png",
                company = "Northstar Studio",
                phone = "+91 90000 00000",
                website = "northstar.example",
                profilePhoto = "/private/template_assets/leader.png",
                ownerName = "Mira Shah",
                ownerDesignation = "Team Leader"
            )
        )

        val restored = TemplateJson.design(TemplateJson.encodeDesign(original))
        assertNotNull(restored)
        restored!!
        assertEquals(-214, restored.template.id)
        assertEquals(143, restored.template.style)
        assertEquals(original.values, restored.values)
        assertEquals(original.photo, restored.photo)
        assertEquals(original.originalPhoto, restored.originalPhoto)
        assertEquals(original.backgroundRemovedPhoto, restored.backgroundRemovedPhoto)
        assertTrue(restored.backgroundRemoved)
        assertEquals(original.crop.scale, restored.crop.scale, .001f)
        assertEquals(original.crop.panX, restored.crop.panX, .001f)
        assertEquals(original.crop.panY, restored.crop.panY, .001f)
        assertEquals(original.branding, restored.branding)
    }

    @Test
    fun style143RendersAtExportResolutionWithEmptyOptionalBranding() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val template = StarterTemplates.all.single { it.id == -214 }
        val design = GeneratedPoster(
            template = template,
            values = mapOf(
                TemplateField.NAME.name to "NEW MEMBER",
                TemplateField.QUOTE.name to "WELCOME ABOARD",
                TemplateField.MESSAGE.name to "We are glad to have you with us."
            ),
            photo = "res:sample_business_woman",
            branding = BusinessBranding()
        )

        val bitmap = TemplateRenderer.render(context, design, 1080)
        assertEquals(1080, bitmap.width)
        assertEquals(1080, bitmap.height)
    }
}
