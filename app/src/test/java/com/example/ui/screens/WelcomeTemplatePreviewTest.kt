package com.example.ui.screens

import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.templates.StarterTemplates
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class WelcomeTemplatePreviewTest {
    @Test
    fun renderOnePortraitPngForEachWelcomeTemplate() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val output = File("build/reports/welcome-previews").apply { 
            mkdirs()
            listFiles()?.forEach { it.delete() }
        }

        StarterTemplates.all.filter { it.category == "Welcome" }.forEachIndexed { index, template ->
            val state = newWelcomePosterState(
                template.id, "YOUR COMPANY", "", "www.yourcompany.com", "+91 98765 43210"
            )
            val bitmap = renderWelcomePosterBitmap(context, state)
            assertTrue("Preview must be 4:5", bitmap.width == 1080 && bitmap.height == 1350)
            FileOutputStream(File(output, "%02d-%s.png".format(index + 1, template.name.lowercase().replace(' ', '-')))).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }

        assertTrue(output.listFiles { file -> file.extension == "png" }?.size == 9)
    }

    @Test
    fun renderReadyWelcomeTemplatesViaTemplateRenderer() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val output = File("build/reports/ready-welcome-previews").apply { 
            mkdirs()
            listFiles()?.forEach { it.delete() }
        }

        StarterTemplates.all.filter { it.category == "Welcome" }.forEachIndexed { index, template ->
            val isReferenceLedTemplate = template.id == -214
            val design = com.example.templates.GeneratedPoster(
                template = template,
                photo = if (isReferenceLedTemplate) "res:sample_business_woman" else "res:sample_business_man",
                values = if (isReferenceLedTemplate) mapOf(
                    com.example.templates.TemplateField.NAME.name to "ANAYA SHARMA",
                    com.example.templates.TemplateField.DESIGNATION.name to "SOFTWARE DEVELOPER",
                    com.example.templates.TemplateField.QUOTE.name to "TOGETHER WE GROW",
                    com.example.templates.TemplateField.MESSAGE.name to "Welcome to our team. Together, we will create a new success story."
                ) else emptyMap(),
                branding = com.example.templates.BusinessBranding(
                    company = "POSTERFLOW STUDIO",
                    phone = "+91 98765 43210",
                    website = "www.posterflow.app",
                    profilePhoto = if (isReferenceLedTemplate) "res:sample_business_man" else "",
                    ownerName = if (isReferenceLedTemplate) "ARJUN MEHTA" else "",
                    ownerDesignation = if (isReferenceLedTemplate) "TEAM LEADER" else ""
                )
            )
            val bitmap = com.example.templates.TemplateRenderer.render(context, design, 1080)
            assertTrue("Preview must be 1080x1080", bitmap.width == 1080 && bitmap.height == 1080)
            FileOutputStream(File(output, "%02d-%s.png".format(index + 1, template.name.lowercase().replace(' ', '-')))).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }

        assertTrue(output.listFiles { file -> file.extension == "png" }?.size == 9)
    }

    @Test
    fun renderRawWelcomeTemplates() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val output = File("build/reports/raw-welcome-previews").apply {
            mkdirs()
            listFiles()?.forEach { it.delete() }
        }

        StarterTemplates.all.filter { it.category == "Welcome" }.forEachIndexed { index, template ->
            val design = com.example.templates.GeneratedPoster(
                template = template,
                photo = "res:sample_business_man",
                branding = com.example.templates.BusinessBranding(
                    company = "POSTERFLOW STUDIO",
                    phone = "+91 98765 43210",
                    website = "www.posterflow.app"
                )
            )
            val bitmap = com.example.templates.TemplateRenderer.renderRaw(context, design, 1080)
            assertTrue("Raw preview must be 1080x1080", bitmap.width == 1080 && bitmap.height == 1080)
            FileOutputStream(File(output, "%02d-%s-raw.png".format(index + 1, template.name.lowercase().replace(' ', '-')))).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }

        assertTrue(output.listFiles { file -> file.extension == "png" }?.size == 9)
    }

    @Test
    fun renderPurpleGoldWithCompanyLogoAndMultipleLeaders() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val template = StarterTemplates.all.single { it.id == -214 }
        val design = com.example.templates.GeneratedPoster(
            template = template,
            photo = "res:sample_business_woman",
            values = mapOf(
                com.example.templates.TemplateField.NAME.name to "DHAWALE PRIYANKA",
                com.example.templates.TemplateField.DESIGNATION.name to "SOFTWARE DEVELOPERS",
                com.example.templates.TemplateField.QUOTE.name to "मुझ में है दम",
                com.example.templates.TemplateField.MESSAGE.name to "हमारी ग्रेट टीम में आपका हार्दिक स्वागत है,\nसाथ मिलकर हम नई सफलता की कहानी लिखेंगे।"
            ),
            branding = com.example.templates.BusinessBranding(
                logo = "res:sample_business_man",
                company = "RIYANSH MULTITRADE PVT. LTD.",
                phone = "9075607350",
                website = "www.riyansh.com",
                profilePhoto = "res:sample_business_man,res:sample_business_woman,res:sample_business_man",
                ownerName = "MR. JAGDISH TAUR",
                ownerDesignation = "RUBY"
            )
        )
        val bitmap = com.example.templates.TemplateRenderer.render(context, design, 1080)
        assertTrue(bitmap.width == 1080 && bitmap.height == 1080)
        val file = File("build/reports/ready-welcome-previews/13-purple-gold-with-logo-and-leaders.png")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        assertTrue(file.exists())
    }
}
