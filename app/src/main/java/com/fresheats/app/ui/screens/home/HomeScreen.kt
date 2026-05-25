package com.fresheats.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.fresheats.app.ui.theme.FreshEatsTheme
import com.fresheats.app.ui.theme.GreenSurface

// ─────────────────────────────────────────────────────────────────────────────
// HomeScreen — Pantalla principal (placeholder)
// TODO: Implementar el listado de platillos con Retrofit + LazyColumn
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun HomeScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(GreenSurface)
    ) {
        Text(
            text  = "🥗 ¡Bienvenido a Fresh Eats!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    FreshEatsTheme { HomeScreen() }
}
