package com.fresheats.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fresheats.app.data.local.entity.FavoriteRecipeEntity
import kotlinx.coroutines.flow.Flow

// ─────────────────────────────────────────────────────────────────────────────
// FavoriteRecipeDao — Data Access Object
// Define las operaciones para interactuar con la tabla "favorite_recipes".
// ─────────────────────────────────────────────────────────────────────────────
@Dao
interface FavoriteRecipeDao {

    // Obtener todas las recetas favoritas (para la pantalla de Favoritos)
    @Query("SELECT * FROM favorite_recipes")
    fun getAllFavorites(): Flow<List<FavoriteRecipeEntity>>

    // Obtener solo los IDs de las recetas favoritas (útil para la HomeScreen)
    @Query("SELECT id FROM favorite_recipes")
    fun getFavoriteIds(): Flow<List<Int>>

    // Insertar una nueva receta favorita
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(recipe: FavoriteRecipeEntity)

    // Eliminar una receta favorita
    @Delete
    suspend fun deleteFavorite(recipe: FavoriteRecipeEntity)
}
