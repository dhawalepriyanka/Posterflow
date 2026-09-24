package com.example.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PosterElement(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String, // "text" or "sticker"
    val textValue: String = "",
    val textColorHex: String = "#FFFFFF",
    val fontSizeSp: Int = 20,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val stickerIcon: String = "", // e.g., "cake", "star", "trophy", "team", "reward"
    // Coordinates as fraction of container (0.0 to 1.0)
    val xOffset: Float = 0.5f,
    val yOffset: Float = 0.5f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f
)

@JsonClass(generateAdapter = true)
data class Poster(
    val id: Int = 0,
    val title: String,
    val category: String, // "Welcome", "Achievement", "Birthday", "Income"
    val backgroundType: String, // "image" or "solid"
    val backgroundImageRes: String = "",
    val backgroundColorHex: String = "#121212",
    val elements: List<PosterElement> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val galleryImageUri: String = "",
    val thumbnailPath: String = "",
    val photoScale: Float = 1f,
    val photoOffsetX: Float = 0f,
    val photoOffsetY: Float = 0f
)
