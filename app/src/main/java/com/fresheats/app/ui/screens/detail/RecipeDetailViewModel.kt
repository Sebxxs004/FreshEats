package com.fresheats.app.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fresheats.app.data.remote.model.RecipeInformationDto
import com.fresheats.app.data.remote.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Estado de UI de Detalles de Receta
// ─────────────────────────────────────────────────────────────────────────────
sealed class RecipeDetailUiState {
    data object Loading : RecipeDetailUiState()
    data class Success(val recipe: RecipeInformationDto) : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
}

// ─────────────────────────────────────────────────────────────────────────────
// RecipeDetailViewModel
// ─────────────────────────────────────────────────────────────────────────────
class RecipeDetailViewModel(
    private val recipeId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        fetchRecipeDetails()
    }

    fun fetchRecipeDetails() {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState.Loading
            try {
                val details = NetworkModule.spoonacularApiService.getRecipeInformation(
                    id = recipeId,
                    apiKey = NetworkModule.SPOONACULAR_API_KEY
                )
                _uiState.value = RecipeDetailUiState.Success(details)
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
