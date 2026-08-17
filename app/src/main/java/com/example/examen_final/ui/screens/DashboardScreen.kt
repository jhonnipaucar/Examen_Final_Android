package com.example.examen_final.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.examen_final.data.local.ExpenseEntity
import com.example.examen_final.navigation.AppScreens
import com.example.examen_final.ui.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: ExpenseViewModel) {
    // Observamos la lista de gastos desde el ViewModel en tiempo real
    val expenses by viewModel.expenses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Gastos") },
                actions = {
                    IconButton(onClick = { navController.navigate(AppScreens.Settings.route) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Configuración")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(AppScreens.AddExpense.route)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Gasto")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (expenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes gastos registrados.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                Text(
                    text = "Resumen (${expenses.size} registros)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // Botón para ir al Historial Completo
                OutlinedButton(
                    onClick = { navController.navigate(AppScreens.ExpenseHistory.route) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text("Ver Historial Completo")
                }

                // Aquí está la magia: La lista deslizable
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre tarjetas
                ) {
                    items(expenses) { expense ->
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

// Diseño individual para cada tarjeta de gasto
@Composable
fun ExpenseCard(expense: ExpenseEntity, onClick: () -> Unit) {
    // Formateamos la fecha para que se vea bonita (ej. "16/08/2026")
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(expense.dateTimestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$${expense.originalAmount} ${expense.originalCurrency}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}