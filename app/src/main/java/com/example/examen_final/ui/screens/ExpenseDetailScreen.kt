package com.example.examen_final.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Gasto") },
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

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Fecha de Registro", style = MaterialTheme.typography.labelMedium)
                        Text(dateString, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                // ¡AQUÍ ESTÁ LA MAGIA VISUAL! Si hay una foto guardada, la mostramos.
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
                            contentScale = ContentScale.Crop // Recorta la foto para que se vea estética
                        )
                    }
                }
            } else {
                Text("Error al cargar los detalles del gasto.")
            }
        }
    }
}