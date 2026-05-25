package com.fresheats.app.ui.screens.home

import com.fresheats.app.data.remote.model.RecipeByIngredientsDto

// ─────────────────────────────────────────────────────────────────────────────
// HomeUiState — Estados de la pantalla principal
//
// Representa el ciclo de vida completo de una búsqueda de recetas:
//
//   Idle ──► Loading ──► Success(recipes)
//                   └──► Error(message)
//
// El ViewModel expone este sealed class como StateFlow<HomeUiState>.
// La UI reacciona de forma declarativa a cada estado.
// ─────────────────────────────────────────────────────────────────────────────
sealed class HomeUiState {

    /**
     * Estado inicial. El usuario aún no ha realizado ninguna búsqueda.
     * La UI muestra la ilustración de "vacío / bienvenida".
     */
    data object Idle : HomeUiState()

    /**
     * Búsqueda en curso. La UI muestra tarjetas esqueleto (shimmer).
     */
    data object Loading : HomeUiState()

    /**
     * Búsqueda completada con resultados.
     * @param recipes Lista de recetas devueltas por Spoonacular.
     *                Puede estar vacía si ninguna receta coincide.
     */
    data class Success(
        val recipes: List<RecipeByIngredientsDto>
    ) : HomeUiState()

    /**
     * Error de red o de la API.
     * @param message Descripción del error para mostrar al usuario.
     */
    data class Error(
        val message: String
    ) : HomeUiState()
}
