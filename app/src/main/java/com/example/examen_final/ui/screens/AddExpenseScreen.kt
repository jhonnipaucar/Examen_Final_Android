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
import androidx.compose.material.icons.filled.Image
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
    // Observamos los estados de la API desde el ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val apiError by viewModel.apiError.collectAsState()

    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }

    // Variables para el Menú Desplegable de Monedas
    var currency by remember { mutableStateOf("USD") }
    var expanded by remember { mutableStateOf(false) }
    val currencyOptions = listOf("USD", "EUR", "MXN", "COP", "PEN", "ARS", "CLP", "GBP")

    // Herramientas para la cámara y galería
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) } // Foto final (de cámara o galería)
    var tempUri by remember { mutableStateOf<Uri?>(null) }  // Ruta temporal para la cámara

    // Lanzador 1: Para tomar foto con la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                photoUri = tempUri
            }
        }
    )

    // Lanzador 2: Para escoger foto de la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                photoUri = uri // Si el usuario escoge una foto, la guardamos
            }
        }
    )

    // Lanzador 3: ¡NUEVO! Para pedir permiso de la cámara explícitamente
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Si acepta, preparamos el archivo y abrimos la cámara
                val file = createImageFile(context)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                tempUri = uri
                cameraLauncher.launch(uri)
            } else {
                // Si rechaza, mostramos el mensaje de error
                android.widget.Toast.makeText(context, "Permiso de cámara denegado. Puedes usar la galería.", android.widget.Toast.LENGTH_LONG).show()
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

            // Menú Desplegable de Monedas
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = currency,
                    onValueChange = {},
                    readOnly = true,
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

            Text("Comprobante (Opcional)", style = MaterialTheme.typography.labelLarge)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ¡BOTÓN DE CÁMARA ACTUALIZADO!
                OutlinedButton(
                    onClick = {
                        // Ahora pedimos permiso al sistema operativo primero
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Cámara", modifier = Modifier.padding(end = 4.dp))
                    Text("Cámara")
                }

                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Image, contentDescription = "Galería", modifier = Modifier.padding(end = 4.dp))
                    Text("Galería")
                }
            }

            if (photoUri != null) {
                Text(
                    text = "¡Imagen adjuntada con éxito!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lógica para mostrar el error de la API (Toast)
            LaunchedEffect(apiError) {
                if (apiError != null) {
                    android.widget.Toast.makeText(context, apiError, android.widget.Toast.LENGTH_LONG).show()
                    viewModel.clearError() // Limpiamos el error para que no vuelva a salir al rotar la pantalla
                }
            }

            // BOTÓN INTELIGENTE: Cambia su aspecto y desactiva los clicks si está cargando
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
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // ¡Evita el doble click mientras carga!
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Convirtiendo moneda...")
                } else {
                    Text("Guardar Gasto")
                }
            }
        }
    }
}