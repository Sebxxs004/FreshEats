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
import com.fresheats.app.data.remote.model.RecipeByIngredientsDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch

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
// ─────────────────────────────────────────────────────────────────────────────
class HomeViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // ── Estado de Favoritos ──────────────────────────────────────────────────
    /** Set de IDs de las recetas favoritas, observado desde Cloud Firestore en tiempo real. */
    val favoriteIds: StateFlow<Set<Int>> = getFavoritesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    private fun getFavoritesFlow() = callbackFlow {
        val user = auth.currentUser
        if (user == null) {
            trySend(emptySet())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = firestore
            .collection("users").document(user.uid)
            .collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptySet())
                    return@addSnapshotListener
                }
                
                val ids = snapshot?.documents?.mapNotNull { it.id.toIntOrNull() }?.toSet() ?: emptySet()
                trySend(ids)
            }

        awaitClose { listener.remove() }
    }

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
        val user = auth.currentUser ?: return // Si no hay usuario, no hacer nada

        val docRef = firestore
            .collection("users").document(user.uid)
            .collection("favorites").document(recipe.id.toString())

        // Si la receta ya está en favoritos (la UI reacciona a favoriteIds.value)
        if (favoriteIds.value.contains(recipe.id)) {
            // Eliminar de forma segura ese documento específico en Firestore
            docRef.delete()
        } else {
            // Guardar los datos esenciales en Firestore
            val favoriteData = hashMapOf(
                "id" to recipe.id,
                "title" to recipe.title,
                "image" to recipe.image
            )
            docRef.set(favoriteData)
        }
    }
}
