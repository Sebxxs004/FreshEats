package com.fresheats.app.ui.screens.favorites

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fresheats.app.data.remote.model.RecipeByIngredientsDto
import com.fresheats.app.ui.components.RecipeCard
import com.fresheats.app.ui.components.ShimmerRecipeCard
import com.fresheats.app.ui.theme.GrayMid
import com.fresheats.app.ui.theme.GreenDark
import com.fresheats.app.ui.theme.GreenPrimary
import com.fresheats.app.ui.theme.GreenSurface
import com.fresheats.app.ui.theme.OrangePrimary
import com.fresheats.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "Mis Favoritos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 22.sp
                        ),
                        color = White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = GreenPrimary,
                    titleContentColor = White
                )
            )
        },
        containerColor = GreenSurface
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "favoritesContent"
            ) { state ->
                when (state) {
                    is FavoritesUiState.Loading -> {
                        LazyColumn(
                            contentPadding   = PaddingValues(vertical = 8.dp),
                            userScrollEnabled = false,
                            modifier         = Modifier.fillMaxSize()
                        ) {
                            items(4) { ShimmerRecipeCard() }
                        }
                    }

                    is FavoritesUiState.Success -> {
                        if (state.favorites.isEmpty()) {
                            EmptyFavoritesState()
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(vertical = 8.dp),
                                modifier       = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = state.favorites,
                                    key   = { it.id }
                                ) { favorite ->
                                    // Mapeamos el FavoriteRecipeDto a RecipeByIngredientsDto 
                                    // para reutilizar RecipeCard de la HomeScreen.
                                    val mappedRecipe = RecipeByIngredientsDto(
                                        id = favorite.id,
                                        title = favorite.title,
                                        image = favorite.image,
                                        imageType = "",
                                        likes = 0,
                                        usedIngredientCount = 0,
                                        missedIngredientCount = 0,
                                        usedIngredients = emptyList(),
                                        missedIngredients = emptyList(),
                                        unusedIngredients = emptyList()
                                    )

                                    RecipeCard(
                                        recipe = mappedRecipe,
                                        isFavorite = true, // Siempre es favorito aquí
                                        onFavoriteClick = { 
                                            // Si le vuelve a dar clic al corazón, se elimina
                                            viewModel.removeFavorite(favorite.id)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    is FavoritesUiState.Error -> {
                        ErrorFavoritesState(state.message)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Estado Vacío (Sin favoritos guardados)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmptyFavoritesState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 🖼️ PLACEHOLDER: Reemplazar este icono por una ilustración tipo "ic_empty_heart.xml"
        Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = OrangePrimary.copy(alpha = 0.5f),
            modifier = Modifier.size(100.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Sin recetas favoritas",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = GreenDark,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Aún no tienes recetas favoritas.\n¡Ve a buscar algunas y guárdalas!",
            style = MaterialTheme.typography.bodyMedium,
            color = GrayMid,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Estado Error (Fallo de red al escuchar Firestore)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ErrorFavoritesState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error al cargar favoritos",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = GreenDark,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = GrayMid,
            textAlign = TextAlign.Center
        )
    }
}
