package com.fresheats.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fresheats.app.navigation.Screen
import com.fresheats.app.ui.screens.home.HomeScreen
import com.fresheats.app.ui.screens.login.AuthViewModel
import com.fresheats.app.ui.screens.login.LoginScreen
import com.fresheats.app.ui.screens.login.RegisterScreen
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
// startDestination → Evalúa si hay un usuario autenticado sincrónicamente.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun FreshEatsNavGraph(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Como Firebase inicializa currentUser sincrónicamente en el cliente, 
    // podemos usarlo directamente para la ruta inicial.
    val startDestination = if (currentUser != null) Screen.HOME else Screen.LOGIN

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        // ── Pantalla de Login ──────────────────────────────────────────────
        composable(route = Screen.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                },
                onNavigateRegister = {
                    navController.navigate(Screen.REGISTER)
                }
            )
        }

        // ── Pantalla de Registro ───────────────────────────────────────────
        composable(route = Screen.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.HOME) {
                        // Limpiamos el backstack para que no pueda volver atrás a Login o Registro
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                },
                onNavigateLogin = {
                    navController.popBackStack()
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
