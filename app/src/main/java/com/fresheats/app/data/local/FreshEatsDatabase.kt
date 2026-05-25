package com.fresheats.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fresheats.app.data.local.dao.FavoriteRecipeDao
import com.fresheats.app.data.local.entity.FavoriteRecipeEntity

// ─────────────────────────────────────────────────────────────────────────────
// FreshEatsDatabase — Base de datos local
// ─────────────────────────────────────────────────────────────────────────────
@Database(
    entities = [FavoriteRecipeEntity::class],
    version = 1,
    exportSchema = true
)
abstract class FreshEatsDatabase : RoomDatabase() {

    abstract fun favoriteRecipeDao(): FavoriteRecipeDao

    companion object {
        @Volatile
        private var Instance: FreshEatsDatabase? = null

        fun getDatabase(context: Context): FreshEatsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FreshEatsDatabase::class.java,
                    "fresheats_db"
                )
                .build()
                .also { Instance = it }
            }
        }
    }
}
