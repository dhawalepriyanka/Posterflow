package com.example.model

import com.example.templates.StarterTemplates

/** Compatibility adapter for Dashboard/Smart AI. Definitions use the new reusable model. */
object TemplatePresets {
    val presets: List<Poster> by lazy { StarterTemplates.all.map { it.asPoster() } }
}
