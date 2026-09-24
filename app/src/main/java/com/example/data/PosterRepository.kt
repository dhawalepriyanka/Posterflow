package com.example.data

import com.example.model.Poster
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PosterRepository(private val posterDao: PosterDao) {
    val allPosters: Flow<List<Poster>> = posterDao.getAllPosters().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getPosterById(id: Int): Poster? {
        return posterDao.getPosterById(id)?.toDomain()
    }

    suspend fun insertPoster(poster: Poster): Poster {
        val entity = PosterEntity.fromDomain(poster)
        val insertedId = posterDao.insertPoster(entity)
        return poster.copy(id = insertedId.toInt())
    }

    suspend fun deletePoster(id: Int) {
        posterDao.deletePosterById(id)
    }
}
