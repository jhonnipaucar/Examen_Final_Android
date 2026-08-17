package com.example.examen_final.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.examen_final.ui.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavController, viewModel: ExpenseViewModel) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("USD") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Nuevo Gasto") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo para el título del gasto
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Descripción del Gasto") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo para el monto numérico
            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón para guardar en la base de datos Room
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amount > 0.0) {
                        // Llamamos a la función del ViewModel que guarda en segundo plano
                        viewModel.addExpense(
                            title = title,
                            originalAmount = amount,
                            originalCurrency = currency,
                            convertedAmount = amount, // De momento guardamos el mismo valor
                            receiptPhotoUri = null
                        )
                        // Regresamos al Dashboard tras guardar exitosamente
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Gasto")
            }
        }
    }
}