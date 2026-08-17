package com.example.examen_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.examen_final.navigation.AppScreens
import com.example.examen_final.ui.ExpenseViewModel
import com.example.examen_final.ui.ExpenseViewModelFactory
import com.example.examen_final.ui.theme.Examen_FinalTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 1. Obtenemos el "traductor" (ViewModel) PRIMERO
            val app = LocalContext.current.applicationContext as ExpenseApplication
            val viewModel: ExpenseViewModel = viewModel(
                factory = ExpenseViewModelFactory(app.repository)
            )

            // 2. Observamos si el modo oscuro está activado en tu base de datos
            // (Si marca rojo, presiona Alt+Enter sobre collectAsState y getValue para importarlos)
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            // 3. ¡La Magia! Le pasamos ese estado al Tema de la app
            Examen_FinalTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Inicializamos el controlador de navegación
                    val navController = rememberNavController()

                    // Mapa de navegación (NavHost)
                    NavHost(
                        navController = navController,
                        startDestination = AppScreens.Dashboard.route
                    ) {
                        composable(route = AppScreens.Dashboard.route) {
                            com.example.examen_final.ui.screens.DashboardScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                        composable(route = AppScreens.AddExpense.route) {
                            com.example.examen_final.ui.screens.AddExpenseScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                        composable(route = AppScreens.ExpenseHistory.route) {
                            PlaceholderScreen(title = "Pantalla 3: Historial de Gastos")
                        }
                        composable(route = AppScreens.ExpenseDetail.route) {
                            PlaceholderScreen(title = "Pantalla 4: Detalle")
                        }
                        composable(route = AppScreens.Settings.route) {
                            com.example.examen_final.ui.screens.SettingsScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

// Un diseño temporal (Placeholder) para comprobar que la navegación funciona
@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}