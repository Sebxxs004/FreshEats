package com.fresheats.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// FreshEats — Material 3 Color Schemes
// Mapeamos la paleta custom de Color.kt a los roles semánticos de Material 3.
// ─────────────────────────────────────────────────────────────────────────────

/** Esquema de color CLARO — mercado luminoso y apetitoso */
private val FreshEatsLightColorScheme = lightColorScheme(
    // ── Primarios (verde) ──────────────────────────────────────────────────
    primary             = GreenPrimary,       // Botones, FAB, barra de navegación activa
    onPrimary           = White,              // Texto/iconos sobre primario
    primaryContainer    = GreenLight,         // Fondos de chips y elementos seleccionados
    onPrimaryContainer  = GreenDark,          // Texto sobre primaryContainer

    // ── Secundarios (naranja) ──────────────────────────────────────────────
    secondary           = OrangePrimary,      // Rating, badges de precio, labels
    onSecondary         = White,
    secondaryContainer  = OrangeLight,        // Fondos de etiquetas de ingredientes
    onSecondaryContainer= OrangeDark,

    // ── Terciario (ámbar) ──────────────────────────────────────────────────
    tertiary            = OrangeBright,       // Estrellas de valoración, destacados
    onTertiary          = GrayDark,
    tertiaryContainer   = WhiteWarm,
    onTertiaryContainer = OrangeDark,

    // ── Error ──────────────────────────────────────────────────────────────
    error               = ErrorRed,
    onError             = White,
    errorContainer      = Color(0xFFFFCDD2),
    onErrorContainer    = Color(0xFFB71C1C),

    // ── Fondos y Surfaces ──────────────────────────────────────────────────
    background          = GreenSurface,       // Fondo general de la app
    onBackground        = GrayDark,
    surface             = White,              // Tarjetas, bottom sheets
    onSurface           = GrayDark,
    surfaceVariant      = GraySurface,        // Input fields, dividers
    onSurfaceVariant    = GrayMid,
    surfaceTint         = GreenPrimary,

    // ── Outline / Borde ────────────────────────────────────────────────────
    outline             = Color(0xFF8BC34A),  // Bordes de inputs y cards
    outlineVariant      = GreenLight,
)

/** Esquema de color OSCURO — experiencia nocturna sofisticada */
private val FreshEatsDarkColorScheme = darkColorScheme(
    // ── Primarios (verde sobre oscuro) ────────────────────────────────────
    primary             = GreenOnDark,
    onPrimary           = GreenDark,
    primaryContainer    = GreenPrimary,
    onPrimaryContainer  = GreenLight,

    // ── Secundarios (naranja sobre oscuro) ────────────────────────────────
    secondary           = OrangeOnDark,
    onSecondary         = OrangeDark,
    secondaryContainer  = Color(0xFF7F4000),
    onSecondaryContainer= OrangeLight,

    // ── Terciario ─────────────────────────────────────────────────────────
    tertiary            = OrangeBright,
    onTertiary          = GrayDark,
    tertiaryContainer   = Color(0xFF5D4000),
    onTertiaryContainer = OrangeLight,

    // ── Error ─────────────────────────────────────────────────────────────
    error               = Color(0xFFEF9A9A),
    onError             = Color(0xFF690005),
    errorContainer      = Color(0xFF93000A),
    onErrorContainer    = Color(0xFFFFDAD6),

    // ── Fondos y Surfaces ─────────────────────────────────────────────────
    background          = DarkBackground,
    onBackground        = Color(0xFFE6F4EA),
    surface             = DarkSurface,
    onSurface           = Color(0xFFE6F4EA),
    surfaceVariant      = DarkSurfaceVar,
    onSurfaceVariant    = Color(0xFFBDBDBD),
    surfaceTint         = GreenOnDark,

    // ── Outline ───────────────────────────────────────────────────────────
    outline             = Color(0xFF388E3C),
    outlineVariant      = Color(0xFF2E7D32),
)

// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun FreshEatsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        FreshEatsDarkColorScheme
    } else {
        FreshEatsLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = FreshEatsTypography,
        content     = content
    )
}
