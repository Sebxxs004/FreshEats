package com.fresheats.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fresheats.app.ui.theme.GreenLight
import com.fresheats.app.ui.theme.GreenSurface
import com.fresheats.app.ui.theme.White

// ─────────────────────────────────────────────────────────────────────────────
// ShimmerRecipeCard — Tarjeta esqueleto animada
//
// Se muestra durante la carga (HomeUiState.Loading) para dar feedback visual
// inmediato al usuario mientras se espera la respuesta de la API.
//
// Usa un gradiente lineal animado que se desplaza de izquierda a derecha
// (efecto "shimmer" / "skeleton loading").
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ShimmerRecipeCard() {
    // Animación infinita del offset del gradiente
    val shimmerTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue   = -1000f,
        targetValue    = 2000f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    // Gradiente que simula luz deslizándose sobre la superficie
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            GreenSurface,
            GreenLight.copy(alpha = 0.6f),
            Color(0xFFE8F5E9),
            GreenLight.copy(alpha = 0.6f),
            GreenSurface
        ),
        start = Offset(shimmerOffset - 500f, 0f),
        end   = Offset(shimmerOffset + 500f, 0f)
    )

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Placeholder de imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(shimmerBrush)
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Placeholder de título (línea larga)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(shimmerBrush)
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Placeholder de título (línea corta)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(shimmerBrush)
                )
                Spacer(modifier = Modifier.height(14.dp))
                // Placeholder de badges de ingredientes
                Row {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(shimmerBrush)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(shimmerBrush)
                    )
                }
            }
        }
    }
}
