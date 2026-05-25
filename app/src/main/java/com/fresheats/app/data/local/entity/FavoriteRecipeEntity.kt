package com.fresheats.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ─────────────────────────────────────────────────────────────────────────────
// FavoriteRecipeEntity — Entidad de Room
// Representa la tabla "favorite_recipes" en la base de datos local.
// ─────────────────────────────────────────────────────────────────────────────
@Entity(tableName = "favorite_recipes")
data class FavoriteRecipeEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val image: String
)
