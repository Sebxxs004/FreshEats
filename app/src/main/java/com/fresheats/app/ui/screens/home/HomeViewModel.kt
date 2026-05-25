package com.fresheats.app.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fresheats.app.data.remote.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import com.fresheats.app.data.local.dao.FavoriteRecipeDao
import com.fresheats.app.data.local.entity.FavoriteRecipeEntity
import com.fresheats.app.data.remote.model.RecipeByIngredientsDto

// ─────────────────────────────────────────────────────────────────────────────
// HomeViewModel
//
// Responsabilidades:
//   1. Mantener el texto del campo de búsqueda (searchQuery)
//   2. Ejecutar la llamada a Spoonacular en un coroutine de viewModelScope
//   3. Exponer el estado de la UI como StateFlow<HomeUiState>
//
// Patrón: sin Hilt (se puede migrar fácilmente con @HiltViewModel + @Inject)
// La instancia de Retrofit se obtiene directamente de NetworkModule.
//
// TODO: Cuando implementes inyección de dependencias (Hilt), mueve la
//       instancia del servicio al constructor con @Inject.
// ─────────────────────────────────────────────────────────────────────────────
class HomeViewModel(
    private val favoriteDao: FavoriteRecipeDao
) : ViewModel() {

    // ── Estado de Favoritos ──────────────────────────────────────────────────
    /** Set de IDs de las recetas favoritas, observado desde Room. */
    val favoriteIds: StateFlow<Set<Int>> = favoriteDao.getFavoriteIds()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // ── Estado de la UI ───────────────────────────────────────────────────────
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)

    /**
     * La pantalla se suscribe a este Flow para recomponerse reactivamente
     * cada vez que el estado cambia.
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // ── Estado del campo de texto ─────────────────────────────────────────────
    /**
     * Texto que el usuario escribe en el campo de ingredientes.
     * Ej: "tomate, cebolla, pollo"
     *
     * Se usa `mutableStateOf` en lugar de StateFlow porque es más eficiente
     * para valores de texto que cambian con cada tecla.
     */
    var searchQuery by mutableStateOf("")
        private set

    // ── Evento: el usuario modificó el campo de búsqueda ─────────────────────
    fun onQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    // ── Evento: el usuario presionó "Buscar" ──────────────────────────────────
    /**
     * Valida la query y lanza la petición a Spoonacular.
     * Transiciona los estados: Idle/Error → Loading → Success | Error
     */
    fun searchRecipes() {
        // Validación: no buscar con campo vacío
        val query = searchQuery.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                val results = NetworkModule.spoonacularApiService
                    .findRecipesByIngredients(
                        ingredients  = query,
                        number       = 15,      // Máx resultados por búsqueda
                        ranking      = 1,       // Maximizar ingredientes usados
                        ignorePantry = true,    // Ignorar sal, agua, harina, etc.
                        apiKey       = NetworkModule.SPOONACULAR_API_KEY
                    )

                _uiState.value = HomeUiState.Success(recipes = results)

            } catch (e: Exception) {
                // Manejo de errores con mensajes amigables
                val message = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "Sin conexión a internet. Revisa tu red."
                    e.message?.contains("401") == true ->
                        "API Key inválida. Verifica tu configuración."
                    e.message?.contains("402") == true ->
                        "Límite de la API alcanzado. Intenta mañana."
                    e.message?.contains("timeout") == true ->
                        "Tiempo de espera agotado. Intenta de nuevo."
                    else -> "Error al buscar recetas: ${e.message}"
                }
                _uiState.value = HomeUiState.Error(message = message)
            }
        }
    }

    // ── Reiniciar al estado inicial ───────────────────────────────────────────
    /** Vuelve al estado Idle (útil para el botón "Limpiar" o borrar query). */
    fun resetSearch() {
        searchQuery = ""
        _uiState.value = HomeUiState.Idle
    }

    // ── Alternar favorito ────────────────────────────────────────────────────
    fun toggleFavorite(recipe: RecipeByIngredientsDto) {
        viewModelScope.launch {
            val entity = FavoriteRecipeEntity(
                id = recipe.id,
                title = recipe.title,
                image = recipe.image
            )
            if (favoriteIds.value.contains(recipe.id)) {
                favoriteDao.deleteFavorite(entity)
            } else {
                favoriteDao.insertFavorite(entity)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Factory para proveer el DAO sin inyección de dependencias como Hilt
// ─────────────────────────────────────────────────────────────────────────────
class HomeViewModelFactory(
    private val favoriteDao: FavoriteRecipeDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(favoriteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
