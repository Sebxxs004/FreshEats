package com.fresheats.app.navigation

// ─────────────────────────────────────────────────────────────────────────────
// FreshEats — Grafo de Navegación
// Define todas las rutas de la app con Navigation Compose.
// ─────────────────────────────────────────────────────────────────────────────

/** Rutas tipadas para evitar strings sueltos en el código. */
object Screen {
    const val LOGIN = "login"
    const val HOME  = "home"
    const val REGISTER = "register"
    const val FAVORITES = "favorites"
    const val INVENTORY = "inventory"
    const val PROFILE = "profile"
    const val DETAIL = "detail/{recipeId}"
    fun createDetailRoute(recipeId: Int) = "detail/$recipeId"
}
