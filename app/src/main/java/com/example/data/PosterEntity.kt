package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Poster
import com.example.model.PosterElement

@Entity(tableName = "posters")
data class PosterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val backgroundType: String,
    val backgroundImageRes: String,
    val backgroundColorHex: String,
    val elements: List<PosterElement>,
    val timestamp: Long = System.currentTimeMillis(),
    val galleryImageUri: String = "",
    val thumbnailPath: String = "",
    val photoScale: Float = 1f,
    val photoOffsetX: Float = 0f,
    val photoOffsetY: Float = 0f
) {
    fun toDomain(): Poster = Poster(
        id = id,
        title = title,
        category = category,
        backgroundType = backgroundType,
        backgroundImageRes = backgroundImageRes,
        backgroundColorHex = backgroundColorHex,
        elements = elements,
        timestamp = timestamp,
        galleryImageUri = galleryImageUri,
        thumbnailPath = thumbnailPath,
        photoScale = photoScale,
        photoOffsetX = photoOffsetX,
        photoOffsetY = photoOffsetY
    )

    companion object {
        fun fromDomain(poster: Poster): PosterEntity = PosterEntity(
            id = poster.id,
            title = poster.title,
            category = poster.category,
            backgroundType = poster.backgroundType,
            backgroundImageRes = poster.backgroundImageRes,
            backgroundColorHex = poster.backgroundColorHex,
            elements = poster.elements,
            timestamp = poster.timestamp,
            galleryImageUri = poster.galleryImageUri,
            thumbnailPath = poster.thumbnailPath,
            photoScale = poster.photoScale,
            photoOffsetX = poster.photoOffsetX,
            photoOffsetY = poster.photoOffsetY
        )
    }
}
