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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fresheats.app.ui.screens.favorites.FavoritesScreen
import com.fresheats.app.ui.theme.GrayMid
import com.fresheats.app.ui.theme.GreenPrimary
import com.fresheats.app.ui.theme.GreenSurface
import com.fresheats.app.ui.theme.White
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

    // Obtenemos la ruta actual para saber si mostramos el BottomNav y qué pestaña resaltar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Solo mostramos el BottomNav cuando estamos en HOME o FAVORITES
    val showBottomBar = currentRoute == Screen.HOME || currentRoute == Screen.FAVORITES

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = White,
                    contentColor = GreenPrimary
                ) {
                    // Pestaña: Buscar (Home)
                    val isHome = currentRoute == Screen.HOME
                    NavigationBarItem(
                        selected = isHome,
                        onClick = {
                            navController.navigate(Screen.HOME) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar"
                            )
                        },
                        label = {
                            Text(
                                text = "Buscar",
                                fontWeight = if (isHome) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenPrimary,
                            unselectedIconColor = GrayMid,
                            selectedTextColor = GreenPrimary,
                            unselectedTextColor = GrayMid,
                            indicatorColor = GreenSurface
                        )
                    )

                    // Pestaña: Favoritos
                    val isFavorites = currentRoute == Screen.FAVORITES
                    NavigationBarItem(
                        selected = isFavorites,
                        onClick = {
                            navController.navigate(Screen.FAVORITES) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isFavorites) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favoritos"
                            )
                        },
                        label = {
                            Text(
                                text = "Favoritos",
                                fontWeight = if (isFavorites) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenPrimary,
                            unselectedIconColor = GrayMid,
                            selectedTextColor = GreenPrimary,
                            unselectedTextColor = GrayMid,
                            indicatorColor = GreenSurface
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding)
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

        // ── Pantalla de Favoritos ──────────────────────────────────────────
        composable(route = Screen.FAVORITES) {
            FavoritesScreen()
        }

    }
}
