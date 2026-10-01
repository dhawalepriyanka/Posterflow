package com.example.templates

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import java.io.File
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/** Creates a durable transparent portrait asset without changing the imported original. */
object PhotoBackgroundRemover {
    suspend fun remove(context: Context, source: String): String {
        val bitmap = withContext(Dispatchers.IO) {
            TemplateImages.read(context, source)
                ?: error("The selected photo is no longer available.")
        }
        val options = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .build()
        val segmenter = Segmentation.getClient(options)
        val mask = try {
            suspendCancellableCoroutine { continuation ->
                segmenter.process(InputImage.fromBitmap(bitmap, 0))
                    .addOnSuccessListener { result ->
                        if (continuation.isActive) continuation.resume(result)
                    }
                    .addOnFailureListener { error ->
                        if (continuation.isActive) continuation.resumeWithException(error)
                    }
            }
        } finally {
            segmenter.close()
        }

        return withContext(Dispatchers.Default) {
            val transparent = applyMask(bitmap, mask)
            try {
                withContext(Dispatchers.IO) {
                    val directory = File(context.filesDir, "template_assets").apply { mkdirs() }
                    val destination = File(directory, "removed_${UUID.randomUUID()}.png")
                    destination.outputStream().use {
                        check(transparent.compress(Bitmap.CompressFormat.PNG, 100, it)) {
                            "Unable to save the transparent photo."
                        }
                    }
                    destination.absolutePath
                }
            } finally {
                transparent.recycle()
            }
        }
    }

    internal fun applyMask(source: Bitmap, mask: SegmentationMask): Bitmap {
        val width = source.width
        val height = source.height
        val maskWidth = mask.width
        val maskHeight = mask.height
        val confidences = FloatArray(maskWidth * maskHeight)
        mask.buffer.apply { rewind(); asFloatBuffer().get(confidences) }

        val sourcePixels = IntArray(width * height)
        val outputPixels = IntArray(width * height)
        source.getPixels(sourcePixels, 0, width, 0, 0, width, height)

        fun confidenceAt(x: Int, y: Int): Float {
            if (maskWidth == width && maskHeight == height) return confidences[y * maskWidth + x]
            val mx = if (width == 1) 0f else x.toFloat() * (maskWidth - 1) / (width - 1)
            val my = if (height == 1) 0f else y.toFloat() * (maskHeight - 1) / (height - 1)
            val x0 = mx.toInt().coerceIn(0, maskWidth - 1)
            val y0 = my.toInt().coerceIn(0, maskHeight - 1)
            val x1 = (x0 + 1).coerceAtMost(maskWidth - 1)
            val y1 = (y0 + 1).coerceAtMost(maskHeight - 1)
            val fx = mx - x0
            val fy = my - y0
            val top = confidences[y0 * maskWidth + x0] * (1f - fx) + confidences[y0 * maskWidth + x1] * fx
            val bottom = confidences[y1 * maskWidth + x0] * (1f - fx) + confidences[y1 * maskWidth + x1] * fx
            return top * (1f - fy) + bottom * fy
        }

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                // A broad, smooth transition retains fine hair while suppressing hard halos.
                val normalized = ((confidenceAt(x, y) - .10f) / .78f).coerceIn(0f, 1f)
                val matte = normalized * normalized * (3f - 2f * normalized)
                val originalAlpha = Color.alpha(sourcePixels[index])
                val alpha = (originalAlpha * matte).toInt().coerceIn(0, 255)
                outputPixels[index] = (sourcePixels[index] and 0x00FFFFFF) or (alpha shl 24)
            }
        }

        return Bitmap.createBitmap(outputPixels, width, height, Bitmap.Config.ARGB_8888)
    }
}
