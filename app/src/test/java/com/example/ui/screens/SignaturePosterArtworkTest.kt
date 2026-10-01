package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.test.core.app.ApplicationProvider
import com.example.model.TemplatePresets
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class SignaturePosterArtworkTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test fun originalsAreReachableAndHaveUniqueIds() {
        val presets = TemplatePresets.presets
        assertEquals(presets.size, presets.map { it.id }.distinct().size)
        // Legacy renderer remains only for previously saved designs, not the active template library.
        assertEquals(setOf("Birthday", "Festival", "Motivation"), presets.filter { isSignatureTemplate(it.id) }.map { it.category }.toSet())
        assertEquals(13, presets.count { it.category == "Birthday" })
        assertEquals(4, presets.count { it.category == "Welcome" })
    }

    @Test fun renderOriginalsAndLongTextWithoutClippingOutsideCanvas() {
        val output = File("build/reports/template-previews").apply { mkdirs() }
        val sheet = Bitmap.createBitmap(1080, 640, Bitmap.Config.ARGB_8888)
        val sheetCanvas = Canvas(sheet)
        var column = 0
        for ((id, file) in listOf(-101 to "midnight-celebration", -102 to "divine-craft", -103 to "rise-every-day")) {
            val content = SignaturePosterContent(
                name = "Aarav Mehta", company = "NOVA COLLECTIVE",
                website = "www.example.com", phone = "+91 90000 00000",
                message = when (id) {
                    -101 -> "Wishing you a year of happiness, good health and wonderful new beginnings."
                    -102 -> wishMessagesForCategory("Festival").first()
                    else -> wishMessagesForCategory("Motivation").first()
                }
            )
            val bitmap = renderSignaturePosterBitmap(context, id, content)
            assertEquals(1080, bitmap.width)
            assertEquals(1920, bitmap.height)
            assertEquals(255, android.graphics.Color.alpha(bitmap.getPixel(0, 0)))
            File(output, "$file.png").outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }

            // Preview is the exact same composition rendered at a smaller resolution.
            val preview = Bitmap.createBitmap(360, 640, Bitmap.Config.ARGB_8888)
            drawSignaturePoster(Canvas(preview), 360f, 640f, id, content, loadSignatureImages(context, id, content))
            sheetCanvas.drawBitmap(preview, (column++ * 360).toFloat(), 0f, null)
            assertEquals(bitmap.getPixel(0, 0), preview.getPixel(0, 0))
            val longText = renderSignaturePosterBitmap(context, id, content.copy(
                name = "A very long personalised name with multiple family names",
                company = "A long organisation name that should fit within the brand header",
                message = "हर दिन एक नया अवसर है। ".repeat(15)
            ))
            File(output, "$file-long-text.png").outputStream().use { longText.compress(Bitmap.CompressFormat.PNG, 100, it) }
            assertFalse("Edits must appear in exported artwork", bitmap.sameAs(longText))
            for (scale in listOf(0.7f, 4f)) {
                renderSignaturePosterBitmap(context, id, content.copy(photoScale = scale, photoX = 600f, photoY = -600f)).recycle()
            }
            bitmap.recycle()
            preview.recycle()
            longText.recycle()
        }
        File(output, "original-template-collection.png").outputStream().use { sheet.compress(Bitmap.CompressFormat.PNG, 100, it) }
        sheet.recycle()
    }
}
