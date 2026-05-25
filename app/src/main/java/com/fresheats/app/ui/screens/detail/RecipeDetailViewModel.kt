package com.fresheats.app.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fresheats.app.data.model.InventoryItemDto
import com.fresheats.app.data.remote.model.ExtendedIngredientDto
import com.fresheats.app.data.remote.model.RecipeInformationDto
import com.fresheats.app.data.remote.network.NetworkModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Clases de Datos para UI
// ─────────────────────────────────────────────────────────────────────────────
sealed class IngredientStatus {
    abstract val ingredient: ExtendedIngredientDto
    data class Complete(override val ingredient: ExtendedIngredientDto) : IngredientStatus()
    data class Missing(override val ingredient: ExtendedIngredientDto, val missingAmount: Double) : IngredientStatus()
}

data class RecipeDetailData(
    val recipe: RecipeInformationDto,
    val ingredientStatuses: List<IngredientStatus>
)

sealed class RecipeDetailUiState {
    data object Loading : RecipeDetailUiState()
    data class Success(val data: RecipeDetailData) : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
}

// ─────────────────────────────────────────────────────────────────────────────
// RecipeDetailViewModel
// ─────────────────────────────────────────────────────────────────────────────
class RecipeDetailViewModel(
    private val recipeId: Int
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    private val recipeFlow = MutableStateFlow<RecipeInformationDto?>(null)

    init {
        fetchRecipeDetails()
        observeInventoryAndRecipe()
    }

    private fun observeInventoryAndRecipe() {
        viewModelScope.launch {
            combine(getInventoryIngredientsFlow(), recipeFlow) { inventory, recipe ->
                if (recipe == null) return@combine null

                val statuses = recipe.extendedIngredients.map { reqIng ->
                    // Buscamos coincidencia exacta o ignorando mayúsculas
                    val userItem = inventory.find { it.nombre.equals(reqIng.name, ignoreCase = true) }
                    
                    if (userItem != null) {
                        val missing = reqIng.amount - userItem.amount
                        if (missing <= 0) {
                            IngredientStatus.Complete(reqIng)
                        } else {
                            IngredientStatus.Missing(reqIng, missing)
                        }
                    } else {
                        IngredientStatus.Missing(reqIng, reqIng.amount)
                    }
                }
                RecipeDetailData(recipe, statuses)
            }.collect { data ->
                if (data != null) {
                    _uiState.value = RecipeDetailUiState.Success(data)
                }
            }
        }
    }

    private fun getInventoryIngredientsFlow(): Flow<List<InventoryItemDto>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users").document(userId)
            .collection("inventory")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(InventoryItemDto::class.java)
                } ?: emptyList()
                trySend(items)
            }
        
        awaitClose { listener.remove() }
    }

    fun fetchRecipeDetails() {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState.Loading
            try {
                val details = NetworkModule.spoonacularApiService.getRecipeInformation(
                    id = recipeId,
                    apiKey = com.fresheats.app.BuildConfig.SPOONACULAR_API_KEY
                )
                recipeFlow.value = details
            } catch (e: Exception) {
                val message = when {
                    e.message?.contains("Unable to resolve host") == true -> "Sin conexión a internet."
                    e.message?.contains("401") == true -> "API Key inválida."
                    e.message?.contains("402") == true -> "Límite de peticiones alcanzado."
                    e.message?.contains("404") == true -> "Receta no encontrada."
                    else -> "Error al cargar la receta: ${e.localizedMessage}"
                }
                _uiState.value = RecipeDetailUiState.Error(message)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Factory para proveer el recipeId al ViewModel
// ─────────────────────────────────────────────────────────────────────────────
class RecipeDetailViewModelFactory(
    private val recipeId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeDetailViewModel(recipeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
