package com.fresheats.app.ui.screens.inventory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.fresheats.app.data.model.InventoryItemDto
import com.fresheats.app.data.remote.model.AutocompleteIngredientDto
import com.fresheats.app.ui.theme.FreshEatsTheme
import com.fresheats.app.ui.theme.GrayMid
import com.fresheats.app.ui.theme.GraySurface
import com.fresheats.app.ui.theme.GreenDark
import com.fresheats.app.ui.theme.GreenLight
import com.fresheats.app.ui.theme.GreenPrimary
import com.fresheats.app.ui.theme.GreenSurface
import com.fresheats.app.ui.theme.OrangePrimary
import com.fresheats.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val inventoryItems by viewModel.inventoryItems.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    var showQuantityDialog by remember { mutableStateOf(false) }
    var selectedIngredient by remember { mutableStateOf<AutocompleteIngredientDto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Inventario",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        ),
                        color = White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimary,
                    titleContentColor = White
                )
            )
        },
        containerColor = GreenSurface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ── BUSCADOR DE INGREDIENTES ──────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .zIndex(2f) // Asegura que el Dropdown quede por encima del grid
                ) {
                    Column {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar ingrediente...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = GreenPrimary)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                        Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = GrayMid)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = White,
                                unfocusedContainerColor = White,
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            singleLine = true
                        )

                        // Resultados del Autocompletado (Flotante)
                        AnimatedVisibility(
                            visible = searchResults.isNotEmpty() && searchQuery.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = White),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.heightIn(max = 250.dp) // Limitar altura para que no ocupe toda la pantalla
                                ) {
                                    items(searchResults) { ingredient ->
                                        IngredientSuggestionItem(
                                            ingredient = ingredient,
                                            onClick = {
                                                selectedIngredient = ingredient
                                                showQuantityDialog = true
                                            }
                                        )
                                        HorizontalDivider(color = GraySurface)
                                    }
                                }
                            }
                        }
                    }
                }

                // ── CUADRÍCULA DEL INVENTARIO ─────────────────────────────────
                if (inventoryItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tu inventario está vacío.\n¡Busca y agrega ingredientes!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = GrayMid,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize().zIndex(1f)
                    ) {
                        items(inventoryItems) { item ->
                            InventoryItemCard(
                                item = item,
                                onDelete = { viewModel.removeIngredient(item) },
                                onIncrease = { viewModel.updateIngredientAmount(item, item.amount + 1.0) },
                                onDecrease = { viewModel.updateIngredientAmount(item, item.amount - 1.0) }
                            )
                        }
                    }
                }
            }
        }
        
        // ── DIÁLOGO PARA CANTIDAD Y UNIDAD ────────────────────────────────
        if (showQuantityDialog && selectedIngredient != null) {
            var amountText by remember { mutableStateOf("") }
            var unitText by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showQuantityDialog = false },
                title = { Text(
                    text = "Agregar ${selectedIngredient?.name?.replaceFirstChar { it.uppercase() }}",
                    fontWeight = FontWeight.Bold
                ) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = { Text("Cantidad (ej. 3)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = unitText,
                            onValueChange = { unitText = it },
                            label = { Text("Unidad (ej. unidades, gramos)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val amount = amountText.replace(",", ".").toDoubleOrNull() ?: 1.0
                            val unit = unitText.ifBlank { "unidades" }
                            viewModel.addIngredientToInventory(
                                name = selectedIngredient!!.name,
                                image = selectedIngredient!!.image,
                                amount = amount,
                                unit = unit
                            )
                            showQuantityDialog = false
                        }
                    ) {
                        Text("Agregar", color = GreenPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showQuantityDialog = false }) {
                        Text("Cancelar", color = GrayMid)
                    }
                },
                containerColor = White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun IngredientSuggestionItem(
    ingredient: AutocompleteIngredientDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Spoonacular base URL for ingredients: https://spoonacular.com/cdn/ingredients_100x100/
        val imageUrl = "https://spoonacular.com/cdn/ingredients_100x100/${ingredient.image}"
        
        AsyncImage(
            model = imageUrl,
            contentDescription = ingredient.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(GraySurface)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = ingredient.name.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = GreenDark
        )
    }
}

@Composable
private fun InventoryItemCard(
    item: InventoryItemDto,
    onDelete: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GraySurface),
                contentAlignment = Alignment.Center
            ) {
                if (item.imagenUrl != null) {
                    val imageUrl = "https://spoonacular.com/cdn/ingredients_100x100/${item.imagenUrl}"
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = item.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("?", color = GrayMid)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = item.nombre.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = GreenDark,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Text(
                text = "${if (item.amount % 1.0 == 0.0) item.amount.toInt() else item.amount} ${item.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = GrayMid,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(24.dp).background(GraySurface, RoundedCornerShape(4.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Reducir",
                            tint = GreenDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(24.dp).background(GreenLight.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar",
                            tint = GreenDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = OrangePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
