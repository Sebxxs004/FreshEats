package com.fresheats.app.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fresheats.app.data.model.FavoriteRecipeDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

// ─────────────────────────────────────────────────────────────────────────────
// Estado UI de Favoritos
// ─────────────────────────────────────────────────────────────────────────────
sealed class FavoritesUiState {
    data object Loading : FavoritesUiState()
    data class Success(val favorites: List<FavoriteRecipeDto>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}

// ─────────────────────────────────────────────────────────────────────────────
// FavoritesViewModel
// ─────────────────────────────────────────────────────────────────────────────
class FavoritesViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // ── Flujo en tiempo real de los Favoritos (objetos completos) ──
    val uiState: StateFlow<FavoritesUiState> = getFavoritesListFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesUiState.Loading
        )

    private fun getFavoritesListFlow() = callbackFlow {
        val user = auth.currentUser
        if (user == null) {
            trySend(FavoritesUiState.Error("Usuario no autenticado"))
            close()
            return@callbackFlow
        }

        // Emitimos Loading al iniciar la conexión (opcional, ya que el estado inicial es Loading)
        trySend(FavoritesUiState.Loading)

        val listener: ListenerRegistration = firestore
            .collection("users").document(user.uid)
            .collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(FavoritesUiState.Error(error.localizedMessage ?: "Error al obtener favoritos"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Firebase Firestore puede mapear automáticamente los documentos a Data Classes 
                    // siempre que los nombres de los campos coincidan y tengan valores por defecto.
                    val favoritesList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(FavoriteRecipeDto::class.java)
                    }
                    trySend(FavoritesUiState.Success(favoritesList))
                } else {
                    trySend(FavoritesUiState.Success(emptyList()))
                }
            }

        awaitClose { listener.remove() }
    }

    /** Permite eliminar un favorito directamente desde la pantalla de Favoritos */
    fun removeFavorite(recipeId: Int) {
        val user = auth.currentUser ?: return
        firestore
            .collection("users").document(user.uid)
            .collection("favorites").document(recipeId.toString())
            .delete()
    }
}
