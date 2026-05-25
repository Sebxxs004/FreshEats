package com.fresheats.app.ui.screens.inventory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fresheats.app.BuildConfig
import com.fresheats.app.data.model.InventoryItemDto
import com.fresheats.app.data.remote.model.AutocompleteIngredientDto
import com.fresheats.app.data.remote.network.NetworkModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class InventoryViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ── ESTADO DEL INVENTARIO (Firestore) ───────────────────────────────────

    // Flujo que escucha en tiempo real la colección de inventario del usuario actual
    val inventoryItems: StateFlow<List<InventoryItemDto>> = callbackFlow {
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
                    Log.e("InventoryVM", "Error al escuchar el inventario", error)
                    return@addSnapshotListener
                }
                
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(InventoryItemDto::class.java)
                } ?: emptyList()
                
                // Ordenar por fecha de agregado de manera descendente (los más recientes primero)
                val sortedItems = items.sortedByDescending { it.fechaAgregado }
                trySend(sortedItems)
            }
            
        awaitClose { listener.remove() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // ── ESTADO DE BÚSQUEDA Y AUTOCOMPLETADO (Spoonacular) ───────────────────

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    // Flujo que reacciona a los cambios en _searchQuery usando debounce
    val searchResults: StateFlow<List<AutocompleteIngredientDto>> = _searchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .mapLatest { query ->
            if (query.isBlank()) {
                emptyList()
            } else {
                try {
                    _isSearching.value = true
                    NetworkModule.spoonacularApiService.autocompleteIngredient(
                        query = query,
                        apiKey = BuildConfig.SPOONACULAR_API_KEY
                    )
                } catch (e: Exception) {
                    Log.e("InventoryVM", "Error al buscar autocompletado", e)
                    emptyList()
                } finally {
                    _isSearching.value = false
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // ── AGREGAR AL INVENTARIO ───────────────────────────────────────────────

    fun addIngredientToInventory(name: String, image: String?, amount: Double, unit: String) {
        val userId = auth.currentUser?.uid ?: return
        
        // Usamos el nombre del ingrediente como ID del documento (limpio de espacios o minúsculas si prefieres)
        val docId = name.lowercase().replace(" ", "_")
        val item = InventoryItemDto(
            nombre = name,
            fechaAgregado = System.currentTimeMillis(),
            imagenUrl = image,
            amount = amount,
            unit = unit
        )

        firestore.collection("users").document(userId)
            .collection("inventory").document(docId)
            .set(item)
            .addOnSuccessListener {
                Log.d("InventoryVM", "Ingrediente $name agregado exitosamente")
                // Limpiamos la barra de búsqueda después de agregar
                _searchQuery.value = ""
            }
            .addOnFailureListener { e ->
                Log.e("InventoryVM", "Error al agregar el ingrediente", e)
            }
    }

    // ── ELIMINAR O ACTUALIZAR DEL INVENTARIO ───────────────────────────────

    fun removeIngredient(item: InventoryItemDto) {
        val userId = auth.currentUser?.uid ?: return
        val docId = item.nombre.lowercase().replace(" ", "_")

        firestore.collection("users").document(userId)
            .collection("inventory").document(docId)
            .delete()
    }

    fun updateIngredientAmount(item: InventoryItemDto, newAmount: Double) {
        val userId = auth.currentUser?.uid ?: return
        val docId = item.nombre.lowercase().replace(" ", "_")

        if (newAmount <= 0) {
            removeIngredient(item)
        } else {
            firestore.collection("users").document(userId)
                .collection("inventory").document(docId)
                .update("amount", newAmount)
        }
    }
}
