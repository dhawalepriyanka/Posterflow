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

        assertTrue(output.listFiles { file -> file.extension == "png" }?.size == 12)
    }

    @Test
    fun renderReadyWelcomeTemplatesViaTemplateRenderer() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val output = File("build/reports/ready-welcome-previews").apply { 
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
            val bitmap = com.example.templates.TemplateRenderer.render(context, design, 1080)
            assertTrue("Preview must be 1080x1080", bitmap.width == 1080 && bitmap.height == 1080)
            FileOutputStream(File(output, "%02d-%s.png".format(index + 1, template.name.lowercase().replace(' ', '-')))).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }

        assertTrue(output.listFiles { file -> file.extension == "png" }?.size == 12)
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

        assertTrue(output.listFiles { file -> file.extension == "png" }?.size == 12)
    }
}
