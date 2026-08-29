package com.example.examen_final.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.examen_final.data.local.ExpenseEntity
import com.example.examen_final.domain.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // 1. Exponemos la lista de gastos
    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Exponemos el estado del modo oscuro
    val isDarkMode: StateFlow<Boolean> = repository.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // 3. Variable para guardar el gasto seleccionado
    private val _selectedExpense = MutableStateFlow<ExpenseEntity?>(null)
    val selectedExpense: StateFlow<ExpenseEntity?> = _selectedExpense.asStateFlow()

    // --- ¡NUEVAS VARIABLES PARA CUMPLIR CON EL SÍLABO! ---

    // Estado de "Cargando"
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de "Error de API"
    private val _apiError = MutableStateFlow<String?>(null)
    val apiError: StateFlow<String?> = _apiError.asStateFlow()

    // Estado para notificar a la UI cuando el guardado finalice
    private val _saveCompleted = MutableStateFlow(false)
    val saveCompleted: StateFlow<Boolean> = _saveCompleted.asStateFlow()

    // Función para limpiar el error una vez mostrado
    fun clearError() {
        _apiError.value = null
    }

    fun resetSaveCompleted() {
        _saveCompleted.value = false
    }
    // -----------------------------------------------------

    fun selectExpense(expense: ExpenseEntity) {
        _selectedExpense.value = expense
    }

    fun addExpense(
        title: String,
        originalAmount: Double,
        originalCurrency: String,
        convertedAmount: Double, // Mantenemos tu parámetro para no romper tu UI
        receiptPhotoUri: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true // ¡Iniciamos el estado de carga!
            _saveCompleted.value = false // Reiniciar estado de finalización
            var finalConvertedAmount = originalAmount

            try {
                if (originalCurrency.uppercase() != "USD") {
                    val myApiKey = "930d364baaab31d987e82312"

                    val response = com.example.examen_final.network.RetrofitClient.api.getRates(
                        apiKey = myApiKey,
                        currency = originalCurrency.uppercase()
                    )

                    val rateToUsd = response.conversion_rates["USD"]
                    if (rateToUsd != null) {
                        finalConvertedAmount = originalAmount * rateToUsd
                    }
                }
            } catch (e: java.net.UnknownHostException) {
                // Falta de conexión a internet o DNS no resuelto
                _apiError.value = "No se pudo realizar la conversión porque no tienes conexión a Internet. El gasto se guardó con el monto original de $originalAmount $originalCurrency."
                e.printStackTrace()
            } catch (e: java.io.IOException) {
                // Error de Entrada/Salida de red general
                _apiError.value = "Hubo un problema de conexión de red al convertir la moneda. El gasto se guardó con el monto original de $originalAmount $originalCurrency."
                e.printStackTrace()
            } catch (e: retrofit2.HttpException) {
                // Error de servidor (ej. API Key incorrecta o caída de servicio)
                _apiError.value = "El servicio de conversión de divisas no está disponible temporalmente. El gasto se guardó con el monto original de $originalAmount $originalCurrency."
                e.printStackTrace()
            } catch (e: Exception) {
                // Cualquier otro error genérico
                _apiError.value = "Ocurrió un error inesperado al intentar convertir la moneda. El gasto se guardó con el monto original de $originalAmount $originalCurrency."
                e.printStackTrace()
            } finally {
                // Guardamos el gasto (haya habido éxito o error con el internet)
                val expense = ExpenseEntity(
                    title = title,
                    originalAmount = originalAmount,
                    originalCurrency = originalCurrency.uppercase(),
                    convertedAmount = finalConvertedAmount,
                    receiptPhotoUri = receiptPhotoUri,
                    dateTimestamp = System.currentTimeMillis()
                )
                repository.addExpense(expense)

                _isLoading.value = false // ¡Apagamos el estado de carga al terminar!
                _saveCompleted.value = true // Notificar que se completó el registro
            }
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            repository.toggleDarkMode(isDark)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteExpense(expense)
        }
    }
}

// --- FACTORY ---
class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}