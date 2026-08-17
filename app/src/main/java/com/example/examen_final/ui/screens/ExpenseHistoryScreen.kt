package com.example.examen_final.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.examen_final.navigation.AppScreens
import com.example.examen_final.ui.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseHistoryScreen(navController: NavController, viewModel: ExpenseViewModel) {
    val expenses by viewModel.expenses.collectAsState()

    // Magia de Kotlin: Sumamos automáticamente todos los montos de la base de datos
    val totalAmount = expenses.sumOf { it.originalAmount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial Completo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Tarjeta grande destacando el dinero total gastado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Gastado Histórico", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = "$$totalAmount",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            if (expenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay historial disponible.")
                }
            } else {
                // Reutilizamos la lista de tarjetas que ya creaste antes
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(expenses) { expense ->
                        // Usamos ExpenseCard (que ya existe en tu archivo DashboardScreen)
                        ExpenseCard(
                            expense = expense,
                            onClick = {
                                viewModel.selectExpense(expense)
                                navController.navigate(AppScreens.ExpenseDetail.route)
                            }
                        )
                    }
                }
            }
        }
    }
}