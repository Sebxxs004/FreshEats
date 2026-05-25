package com.fresheats.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fresheats.app.data.remote.model.IngredientDto
import com.fresheats.app.data.remote.model.RecipeByIngredientsDto
import com.fresheats.app.ui.theme.ErrorRed
import com.fresheats.app.ui.theme.FreshEatsTheme
import com.fresheats.app.ui.theme.GreenBright
import com.fresheats.app.ui.theme.GreenDark
import com.fresheats.app.ui.theme.GreenLight
import com.fresheats.app.ui.theme.GreenPrimary
import com.fresheats.app.ui.theme.OrangeBright
import com.fresheats.app.ui.theme.White

// ─────────────────────────────────────────────────────────────────────────────
// RecipeCard — Tarjeta visual de receta
//
// Diseño:
//   ┌────────────────────────────────────────┐
//   │                                    [♡] │  ← IconButton favorito (top-right)
//   │          AsyncImage (Coil)             │  ← Imagen de la receta (200dp)
//   │                                        │
//   │▓▓▓▓▓  Gradiente oscuro  ▓▓▓▓▓▓▓▓▓▓▓▓▓│  ← Overlay para legibilidad
//   │ Título de la receta                    │  ← Texto en blanco sobre gradiente
//   ├────────────────────────────────────────┤
//   │ [✅ 3 ingredientes] [❌ 2 faltantes]   │  ← Badges de ingredientes
//   └────────────────────────────────────────┘
//
// El corazón alterna entre vacío (outline) y lleno (rojo) al tocar.
// TODO: Conectar onFavoriteClick con Room (FavoriteDao) en próxima iteración.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun RecipeCard(
    recipe:          RecipeByIngredientsDto,
    isFavorite:      Boolean,
    onFavoriteClick: (RecipeByIngredientsDto) -> Unit = {},
    modifier:        Modifier = Modifier
) {

    Card(
        modifier  = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(
            defaultElevation  = 4.dp,
            pressedElevation  = 8.dp
        )
    ) {
        Column {

            // ── ZONA DE IMAGEN ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                // Imagen de la receta cargada con Coil
                AsyncImage(
                    model             = recipe.image,
                    contentDescription = recipe.title,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                )

                // Gradiente oscuro en la parte inferior de la imagen
                // para que el título sea legible en blanco
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.72f)
                                )
                            )
                        )
                )

                // ── Título sobre el gradiente ──────────────────────────────
                Text(
                    text     = recipe.title,
                    style    = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp
                    ),
                    color    = White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 14.dp, end = 56.dp, bottom = 12.dp)
                )

                // ── Botón de favorito (corazón) ────────────────────────────
                // Posicionado en la esquina superior derecha de la imagen
                IconButton(
                    onClick   = {
                        onFavoriteClick(recipe)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(44.dp)
                        .background(
                            color  = Color.Black.copy(alpha = 0.35f),
                            shape  = RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        imageVector        = if (isFavorite)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite)
                            "Quitar de favoritos"
                        else
                            "Agregar a favoritos",
                        tint = if (isFavorite) ErrorRed else White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // ── Badge de "likes" (esquina superior izquierda) ──────────
                if (recipe.likes > 0) {
                    Surface(
                        shape = RoundedCornerShape(topStart = 20.dp, bottomEnd = 12.dp),
                        color = GreenPrimary.copy(alpha = 0.88f),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text     = "❤ ${recipe.likes}",
                            style    = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color    = White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // ── ZONA DE BADGES DE INGREDIENTES ────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Badge: ingredientes que el usuario YA TIENE ✅
                IngredientBadge(
                    count     = recipe.usedIngredientCount,
                    label     = "usados",
                    color     = GreenBright,
                    emoji     = "✅"
                )

                // Badge: ingredientes que le FALTAN ❌
                IngredientBadge(
                    count     = recipe.missedIngredientCount,
                    label     = "faltantes",
                    color     = OrangeBright,
                    emoji     = "🛒"
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// IngredientBadge — Pill de conteo de ingredientes
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun IngredientBadge(
    count: Int,
    label: String,
    color: Color,
    emoji: String
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = emoji, fontSize = 12.sp)
            Text(
                text  = "$count $label",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color      = color
                )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
private fun RecipeCardPreview() {
    FreshEatsTheme {
        RecipeCard(
            recipe = RecipeByIngredientsDto(
                id                   = 1,
                title                = "Pasta de tomate con albahaca fresca",
                image                = "https://img.spoonacular.com/recipes/73420-312x231.jpg",
                imageType            = "jpg",
                likes                = 12,
                usedIngredientCount  = 3,
                missedIngredientCount = 2,
                usedIngredients      = emptyList(),
                missedIngredients    = emptyList(),
                unusedIngredients    = emptyList()
            ),
            isFavorite = false
        )
    }
}
