package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PosterEntity::class], version = 3, exportSchema = false)
@TypeConverters(PosterConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun posterDao(): PosterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE posters ADD COLUMN galleryImageUri TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE posters ADD COLUMN thumbnailPath TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE posters ADD COLUMN photoScale REAL NOT NULL DEFAULT 1.0")
                db.execSQL("ALTER TABLE posters ADD COLUMN photoOffsetX REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE posters ADD COLUMN photoOffsetY REAL NOT NULL DEFAULT 0.0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "poster_maker_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
