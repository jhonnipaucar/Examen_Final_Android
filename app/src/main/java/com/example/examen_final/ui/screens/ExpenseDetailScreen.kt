package com.example.examen_final.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete // ¡Importante para el ícono de basura!
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // ¡Importante para el diálogo!
import androidx.compose.runtime.remember // ¡Importante para el diálogo!
import androidx.compose.runtime.setValue // ¡Importante para el diálogo!
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.examen_final.ui.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(navController: NavController, viewModel: ExpenseViewModel) {
    val expense by viewModel.selectedExpense.collectAsState()

    // Variable para controlar si el cuadro de advertencia está visible o escondido
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Gasto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                // ¡AQUÍ AGREGAMOS EL BOTÓN DE ELIMINAR A LA DERECHA!
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar Gasto")
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
            if (expense != null) {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val dateString = dateFormat.format(Date(expense!!.dateTimestamp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Descripción", style = MaterialTheme.typography.labelMedium)
                        Text(expense!!.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Monto Pagado", style = MaterialTheme.typography.labelMedium)
                        Text("$${expense!!.originalAmount} ${expense!!.originalCurrency}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)

                        Text("Equivalente en USD", style = MaterialTheme.typography.labelMedium)
                        Text("$${String.format(Locale.US, "%.2f", expense!!.convertedAmount)} USD", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Fecha de Registro", style = MaterialTheme.typography.labelMedium)
                        Text(dateString, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                // Mostrar foto del recibo si existe
                if (!expense!!.receiptPhotoUri.isNullOrEmpty()) {
                    Text("Foto del Recibo:", style = MaterialTheme.typography.titleMedium)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        AsyncImage(
                            model = expense!!.receiptPhotoUri,
                            contentDescription = "Foto del recibo físico",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                Text("Error al cargar los detalles del gasto.")
            }
        }

        // ¡EL CUADRO DE DIÁLOGO DE CONFIRMACIÓN!
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false }, // Cancela si tocas fuera del cuadro
                title = { Text("Eliminar Gasto") },
                text = { Text("¿Estás seguro de que deseas eliminar este registro? Esta acción no se puede deshacer y tu total gastado se actualizará automáticamente.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            expense?.let {
                                viewModel.deleteExpense(it) // Llama a la orden de borrar
                                navController.popBackStack() // Te devuelve al Dashboard
                            }
                        }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error) // Texto en rojo
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}