package com.fresheats.app.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.RiceBowl
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fresheats.app.data.remote.model.RecipeByIngredientsDto
import com.fresheats.app.ui.components.RecipeCard
import com.fresheats.app.ui.components.ShimmerRecipeCard
import com.fresheats.app.ui.theme.FreshEatsTheme
import com.fresheats.app.ui.theme.GreenBright
import com.fresheats.app.ui.theme.GreenDark
import com.fresheats.app.ui.theme.GreenLight
import com.fresheats.app.ui.theme.GreenPrimary
import com.fresheats.app.ui.theme.GreenSurface
import com.fresheats.app.ui.theme.GrayMid
import com.fresheats.app.ui.theme.OrangePrimary
import com.fresheats.app.ui.theme.White

// ╔══════════════════════════════════════════════════════════════════════════╗
// ║  FreshEats — HomeScreen                                                ║
// ║                                                                        ║
// ║  RECOMENDACIONES DE ICONOS / ILUSTRACIONES:                            ║
// ║                                                                        ║
// ║  🖼️ ESTADO VACÍO (Idle):                                              ║
// ║     → Ilustración vectorial: tazón de ensalada con tenedor             ║
// ║     → Archivo sugerido: res/drawable/ic_empty_bowl.xml (Vector Asset)  ║
// ║     → Por ahora: Icons.Outlined.RiceBowl de Material Icons Extended    ║
// ║     → Para más impacto visual, usar una ilustración Lottie (.json)     ║
// ║       con la librería com.airbnb.android:lottie-compose               ║
// ║                                                                        ║
// ║  🖼️ ESTADO SIN RESULTADOS:                                            ║
// ║     → Ilustración: plato vacío con interrogación                       ║
// ║     → Archivo sugerido: res/drawable/ic_no_results.xml                 ║
// ║     → Por ahora: Icons.Outlined.SearchOff                              ║
// ║                                                                        ║
// ║  🖼️ ESTADO ERROR DE RED:                                              ║
// ║     → Ilustración: nube con rayos y desconexión                        ║
// ║     → Archivo sugerido: res/drawable/ic_no_wifi.xml                    ║
// ║     → Por ahora: Icons.Outlined.WifiOff                               ║
// ╚══════════════════════════════════════════════════════════════════════════╝

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRecipeClick: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel()

    val uiState by viewModel.uiState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "Cocina con lo que tienes",
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

            // ── Área de contenido principal (con AnimatedContent) ─────────
            AnimatedContent(
                targetState   = uiState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label         = "homeContent"
            ) { state ->
                when (state) {

                    // ── Estado inicial — bienvenida / sin búsqueda ─────────
                    is HomeUiState.Idle -> {
                        IdleState()
                    }

                    // ── Cargando — tarjetas shimmer ────────────────────────
                    is HomeUiState.Loading -> {
                        LazyColumn(
                            contentPadding        = PaddingValues(vertical = 8.dp),
                            userScrollEnabled      = false,
                            modifier              = Modifier.fillMaxSize()
                        ) {
                            // Muestra 5 tarjetas shimmer mientras carga
                            items(5) { ShimmerRecipeCard() }
                        }
                    }

                    // ── Éxito — lista de recetas ───────────────────────────
                    is HomeUiState.Success -> {
                        if (state.recipes.isEmpty()) {
                            EmptyResultsState()
                        } else {
                            RecipeList(
                                recipes         = state.recipes,
                                favoriteIds     = favoriteIds,
                                onFavoriteClick = viewModel::toggleFavorite,
                                onRecipeClick   = onRecipeClick
                            )
                        }
                    }

                    // ── Error de red / API ─────────────────────────────────
                    is HomeUiState.Error -> {
                        ErrorState(
                            message   = state.message,
                            onRetry   = viewModel::retrySearch
                        )
                    }
                }
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// RecipeList — LazyColumn con RecipeCards
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RecipeList(
    recipes:         List<RecipeByIngredientsDto>,
    favoriteIds:     Set<Int>,
    onFavoriteClick: (RecipeByIngredientsDto) -> Unit,
    onRecipeClick:   (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 0.dp),
        modifier       = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        // ── Header con conteo de resultados ────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text  = "${recipes.size} recetas encontradas",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color      = GrayMid,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        // ── Tarjetas de receta ─────────────────────────────────────────────
        items(
            items = recipes,
            key   = { it.id }  // key estable para que Compose no recomponga todo
        ) { recipe ->
            RecipeCard(
                recipe          = recipe,
                isFavorite      = favoriteIds.contains(recipe.id),
                onFavoriteClick = onFavoriteClick,
                onClick         = { onRecipeClick(recipe.id) }
            )
        }

        // ── Espacio al final de la lista ───────────────────────────────────
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// IdleState — Estado inicial / bienvenida
//
// 🖼️ PLACEHOLDER DE ILUSTRACIÓN:
//    Por ahora usa Icons.Outlined.RiceBowl de Material Icons Extended.
//    Para una experiencia más premium, reemplaza el Icon por:
//
//    OPCIÓN A — Ilustración vectorial propia:
//      Image(
//          painter = painterResource(R.drawable.ic_empty_bowl),
//          contentDescription = null,
//          modifier = Modifier.size(180.dp)
//      )
//      → Archivo: res/drawable/ic_empty_bowl.xml (Android Vector Asset)
//      → Estilo sugerido: flat illustration, colores GreenBright + OrangeBright
//
//    OPCIÓN B — Animación Lottie (muy recomendado para "wow factor"):
//      val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cooking_animation))
//      LottieAnimation(composition, iterations = LottieConstants.IterateForever, modifier = Modifier.size(220.dp))
//      → Archivo: res/raw/cooking_animation.json
//      → Descarga gratis en: https://lottiefiles.com/search?q=cooking&category=animations
//      → Dependencia extra: implementation("com.airbnb.android:lottie-compose:6.4.0")
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun IdleState() {
    Column(
        modifier              = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center
    ) {
        // 🖼️ PLACEHOLDER — Reemplazar por ilustración propia o Lottie
        Icon(
            imageVector        = Icons.Outlined.RiceBowl,
            contentDescription = null,
            tint               = GreenBright.copy(alpha = 0.5f),
            modifier           = Modifier.size(120.dp)
        )
        /* ─── OPCIÓN B: Lottie (descomentar cuando añadas la dependencia) ───
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.cooking_animation)
        )
        LottieAnimation(
            composition = composition,
            iterations  = LottieConstants.IterateForever,
            modifier    = Modifier.size(220.dp)
        )
        ─────────────────────────────────────────────────────────────────── */

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text      = "¡Descubre qué cocinar!",
            style     = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color     = GreenDark,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text      = "Tu nevera está vacía. ¡Ve a la pestaña de Inventario y agrega lo que tienes para recibir deliciosas sugerencias!",
            style     = MaterialTheme.typography.bodyMedium,
            color     = GrayMid,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EmptyResultsState — Búsqueda sin resultados
//
// 🖼️ PLACEHOLDER DE ILUSTRACIÓN:
//    Reemplazar el Icon por:
//      Image(painterResource(R.drawable.ic_no_results), ...)
//    → Estilo: plato vacío con signo "?"
//    → Colores: OrangePrimary / GrayMid
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmptyResultsState() {
    EmptyOrErrorColumn(
        icon    = Icons.Default.Search,  // 🖼️ Reemplazar por R.drawable.ic_no_results
        iconTint = OrangePrimary.copy(alpha = 0.5f),
        title   = "Sin recetas",
        message = "No encontramos recetas para los ingredientes actuales de tu inventario.",
        action  = null
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// ErrorState — Error de red o API
//
// 🖼️ PLACEHOLDER DE ILUSTRACIÓN:
//    Reemplazar el Icon por:
//      Image(painterResource(R.drawable.ic_no_wifi), ...)
//    → Estilo: nube con rayos + cable desconectado
//    → Colores: GrayMid / ErrorRed
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    EmptyOrErrorColumn(
        icon    = Icons.Outlined.WifiOff,  // 🖼️ Reemplazar por R.drawable.ic_no_wifi
        iconTint = GrayMid.copy(alpha = 0.5f),
        title   = "Ups, algo salió mal",
        message = message,
        action  = {
            Button(
                onClick = onRetry,
                colors  = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape   = RoundedCornerShape(12.dp)
            ) {
                Text("Reintentar", color = White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// EmptyOrErrorColumn — Layout reutilizable para estados vacíos/error
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmptyOrErrorColumn(
    icon:    ImageVector,
    iconTint: Color,
    title:   String,
    message: String,
    action:  (@Composable () -> Unit)?
) {
    Column(
        modifier              = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center
    ) {
        // 🖼️ Ícono/Ilustración placeholder
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = iconTint,
            modifier           = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text      = title,
            style     = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color     = GreenDark,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text       = message,
            style      = MaterialTheme.typography.bodyMedium,
            color      = GrayMid,
            textAlign  = TextAlign.Center,
            lineHeight = 22.sp
        )
        if (action != null) {
            Spacer(modifier = Modifier.height(28.dp))
            action()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────────────────────
@Preview(name = "HomeScreen — Estado Idle", showSystemUi = true)
@Composable
private fun HomeScreenIdlePreview() {
    FreshEatsTheme { HomeScreen() }
}
