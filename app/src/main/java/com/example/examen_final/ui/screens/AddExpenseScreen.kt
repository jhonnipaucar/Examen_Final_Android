package com.example.examen_final.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.examen_final.ui.ExpenseViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Función auxiliar: Crea un archivo vacío en la carpeta segura
fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images")
    if (!storageDir.exists()) storageDir.mkdirs()
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavController, viewModel: ExpenseViewModel) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }

    // Variables para el Menú Desplegable de Monedas
    var currency by remember { mutableStateOf("USD") }
    var expanded by remember { mutableStateOf(false) }
    val currencyOptions = listOf("USD", "EUR", "MXN", "COP", "PEN", "ARS", "CLP", "GBP")

    // Herramientas para la cámara
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) } // Foto final
    var tempUri by remember { mutableStateOf<Uri?>(null) }  // Ruta temporal

    // El lanzador mágico que abre la cámara y espera el resultado
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                photoUri = tempUri // Si el usuario tomó la foto y aceptó, guardamos la ruta
            }
        }
    )

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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Descripción del Gasto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // ¡EL NUEVO MENÚ DESPLEGABLE DE MONEDAS!
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = currency,
                    onValueChange = {},
                    readOnly = true, // Evita que el usuario escriba a mano
                    label = { Text("Moneda") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    currencyOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                currency = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Botón para la cámara
            OutlinedButton(
                onClick = {
                    val file = createImageFile(context)
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    tempUri = uri
                    cameraLauncher.launch(uri)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (photoUri != null) Icons.Filled.Check else Icons.Filled.CameraAlt,
                    contentDescription = "Cámara",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(if (photoUri != null) "¡Foto capturada con éxito!" else "Tomar foto del recibo")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amount > 0.0) {
                        viewModel.addExpense(
                            title = title,
                            originalAmount = amount,
                            originalCurrency = currency,
                            convertedAmount = amount,
                            receiptPhotoUri = photoUri?.toString()
                        )
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