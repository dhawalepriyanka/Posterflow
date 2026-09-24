package com.example.data

import androidx.room.TypeConverter
import com.example.model.PosterElement
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class PosterConverters {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, PosterElement::class.java)
    private val adapter = moshi.adapter<List<PosterElement>>(listType)

    @TypeConverter
    fun fromElementsList(elements: List<PosterElement>?): String {
        return adapter.toJson(elements ?: emptyList())
    }

    @TypeConverter
    fun toElementsList(value: String?): List<PosterElement> {
        if (value.isNullOrEmpty()) return emptyList()
        return adapter.fromJson(value) ?: emptyList()
    }
}
