package com.fresheats.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fresheats.app.navigation.Screen
import com.fresheats.app.ui.screens.home.HomeScreen
import com.fresheats.app.ui.screens.login.LoginScreen
import com.fresheats.app.ui.theme.FreshEatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge: la UI se extiende detrás de barras del sistema
        enableEdgeToEdge()
        setContent {
            FreshEatsTheme {
                FreshEatsNavGraph()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FreshEatsNavGraph
// Configura la navegación completa de la app.
// startDestination → Login (cuando el usuario no está autenticado)
// TODO: Evaluar si el usuario ya tiene sesión activa (DataStore/SharedPrefs)
//       y saltar directamente a Home si corresponde.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun FreshEatsNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = Screen.LOGIN
    ) {
        // ── Pantalla de Login ──────────────────────────────────────────────
        composable(route = Screen.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.HOME) {
                        // Elimina Login del back-stack: el usuario no puede
                        // volver al login con el botón "atrás" desde Home
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Pantalla Principal ─────────────────────────────────────────────
        composable(route = Screen.HOME) {
            HomeScreen()
        }

        // ── Aquí agrega nuevas rutas en el futuro ─────────────────────────
        // composable(Screen.DETAIL + "/{foodId}") { backStack ->
        //     val id = backStack.arguments?.getString("foodId")
        //     FoodDetailScreen(foodId = id)
        // }
    }
}
