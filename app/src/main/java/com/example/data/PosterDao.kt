package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PosterDao {
    @Query("SELECT * FROM posters ORDER BY timestamp DESC")
    fun getAllPosters(): Flow<List<PosterEntity>>

    @Query("SELECT * FROM posters WHERE id = :id")
    suspend fun getPosterById(id: Int): PosterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoster(poster: PosterEntity): Long

    @Query("DELETE FROM posters WHERE id = :id")
    suspend fun deletePosterById(id: Int)
}
