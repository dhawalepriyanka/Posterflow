package com.example.templates

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.newWelcomePosterState
import com.example.ui.screens.renderWelcomePosterBitmap
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
class RenderDebugPostersTest {

    @Test
    fun renderDebugTemplates() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val outDir = File("build/reports/debug_renders").apply { mkdirs() }

        val brandingWithData = BusinessBranding(
            company = "Apex Technologies",
            phone = "9876543210",
            website = "www.apextech.com",
            profilePhoto = "res:sample_business_woman",
            logo = "res:sample_business_man",
            ownerName = "Rahul Sharma",
            ownerDesignation = "Director"
        )

        val brandingEmpty = BusinessBranding()

        // Render style 147 (Purple Glow) with branding
        val t147 = StarterTemplates.all.first { it.id == -217 }
        val posterWithData = GeneratedPoster(
            template = t147,
            photo = "res:sample_business_woman",
            values = mapOf("NAME" to "Pooja Hegde", "DESIGNATION" to "Product Manager"),
            branding = brandingWithData
        )
        val bmpWithData = TemplateRenderer.render(context, posterWithData, 1080)
        FileOutputStream(File(outDir, "147_with_branding.png")).use {
            bmpWithData.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        // Render style 147 with EMPTY branding
        val posterEmpty = GeneratedPoster(
            template = t147,
            photo = "",
            values = emptyMap(),
            branding = brandingEmpty
        )
        val bmpEmpty = TemplateRenderer.render(context, posterEmpty, 1080)
        FileOutputStream(File(outDir, "147_empty_branding.png")).use {
            bmpEmpty.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        // Render style 148 (Golden Wave) with branding
        val t148 = StarterTemplates.all.first { it.id == -218 }
        val poster148 = GeneratedPoster(
            template = t148,
            photo = "res:sample_business_woman",
            values = mapOf("NAME" to "Pooja Hegde", "DESIGNATION" to "Product Manager"),
            branding = brandingWithData
        )
        val bmp148 = TemplateRenderer.render(context, poster148, 1080)
        FileOutputStream(File(outDir, "148_with_branding.png")).use {
            bmp148.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        // Also render editable WelcomePosterState for -201 and -217
        val state201 = newWelcomePosterState(
            -201, "Apex Technologies", "", "www.apextech.com", "9876543210", "Pooja Hegde"
        )
        val bmpState201 = renderWelcomePosterBitmap(context, state201)
        FileOutputStream(File(outDir, "state_201.png")).use {
            bmpState201.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        println("All debug renders saved to build/reports/debug_renders")
    }
}
