package com.fresheats.app.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fresheats.app.data.remote.model.AnalyzedInstructionDto
import com.fresheats.app.data.remote.model.ExtendedIngredientDto
import com.fresheats.app.data.remote.model.RecipeInformationDto
import com.fresheats.app.ui.theme.ErrorRed
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
fun RecipeDetailScreen(
    viewModel: RecipeDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(White.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = GreenDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = GreenDark,
                    navigationIconContentColor = GreenDark
                )
            )
        },
        containerColor = GreenSurface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp) // Permite que la imagen suba hasta arriba si se hace transparente el TopAppBar
        ) {
            when (val state = uiState) {
                is RecipeDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = GreenPrimary
                    )
                }

                is RecipeDetailUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = ErrorRed,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.message,
                            color = GreenDark,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                is RecipeDetailUiState.Success -> {
                    val recipe = state.data.recipe
                    val statuses = state.data.ingredientStatuses

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 32.dp)
                    ) {
                        // ── CABECERA ──────────────────────────────────────────────────────────
                        item {
                            RecipeHeader(recipe = recipe)
                        }

                        // ── CHECKLIST DE INGREDIENTES ─────────────────────────────────────────
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Ingredientes",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = GreenDark,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        items(statuses) { status ->
                            IngredientStatusRow(status = status)
                        }

                        // ── PREPARACIÓN ──────────────────────────────────────────────────────
                        if (recipe.analyzedInstructions.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                                Text(
                                    text = "Preparación",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = GreenDark,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            val steps = recipe.analyzedInstructions.first().steps
                            items(steps) { step ->
                                InstructionStepRow(
                                    number = step.number,
                                    text = step.step
                                )
                            }
                        } else {
                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                                Text(
                                    text = "Instrucciones no disponibles para esta receta.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GrayMid,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeHeader(recipe: RecipeInformationDto) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = GreenDark
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tiempo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Tiempo",
                        tint = GreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${recipe.readyInMinutes} min",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = GreenDark
                    )
                }
                
                // Calorías
                val calories = recipe.nutrition?.nutrients?.find { it.name.equals("Calories", ignoreCase = true) }
                if (calories != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Calorías",
                            tint = OrangePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${calories.amount.toInt()} kcal",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = GreenDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IngredientStatusRow(status: IngredientStatus) {
    val isComplete = status is IngredientStatus.Complete
    val ingredientName = status.ingredient.name.replaceFirstChar { it.uppercase() }
    
    val containerColor = if (isComplete) GreenLight.copy(alpha = 0.3f) else OrangePrimary.copy(alpha = 0.1f)
    val iconTint = if (isComplete) GreenPrimary else OrangePrimary
    val iconVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.Error
    
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = if (isComplete) "Completo" else "Faltante",
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = ingredientName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = GreenDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                val statusText = if (isComplete) {
                    "Tienes suficiente (${status.ingredient.amount} ${status.ingredient.unit})"
                } else {
                    val missingAmount = (status as IngredientStatus.Missing).missingAmount
                    "Faltan ${if (missingAmount % 1.0 == 0.0) missingAmount.toInt() else missingAmount} ${status.ingredient.unit}"
                }
                
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isComplete) GreenPrimary else OrangePrimary
                )
            }
        }
    }
}

@Composable
private fun InstructionStepRow(number: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(GreenPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = GreenDark,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
