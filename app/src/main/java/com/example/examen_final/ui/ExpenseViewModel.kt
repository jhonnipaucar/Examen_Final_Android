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

    // 1. Exponemos la lista de gastos como StateFlow para que la UI la observe en tiempo real
    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Exponemos el estado del modo oscuro como StateFlow
    val isDarkMode: StateFlow<Boolean> = repository.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false

        )
    // Variable para guardar el gasto que el usuario seleccione para ver en detalle
    // Variable para guardar el gasto que el usuario seleccione para ver en detalle
    private val _selectedExpense = MutableStateFlow<ExpenseEntity?>(null)
    val selectedExpense: StateFlow<ExpenseEntity?> = _selectedExpense.asStateFlow()

    // Función que llamaremos cuando toques una tarjeta
    fun selectExpense(expense: ExpenseEntity) {
        _selectedExpense.value = expense
    }

    fun addExpense(
        title: String,
        originalAmount: Double,
        originalCurrency: String,
        convertedAmount: Double,
        receiptPhotoUri: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var finalConvertedAmount = originalAmount

            try {
                // Si la moneda NO es USD, llamamos a la API comercial para hacer la conversión
                if (originalCurrency.uppercase() != "USD") {

                    // AQUÍ PONES TU API KEY REAL ENTRE LAS COMILLAS
                    val myApiKey = "930d364baaab31d987e82312"

                    // Nos conectamos a internet consultando tu clave y la moneda
                    val response = com.example.examen_final.network.RetrofitClient.api.getRates(
                        apiKey = "930d364baaab31d987e82312",
                        currency = originalCurrency.uppercase()
                    )

                    // Buscamos a cuánto equivale en USD
                    val rateToUsd = response.conversion_rates["USD"]
                    if (rateToUsd != null) {
                        finalConvertedAmount = originalAmount * rateToUsd
                    }
                }
            } catch (e: Exception) {
                // Si no hay internet o la clave está mal, capturamos el error para que la app no se cierre
                e.printStackTrace()
            }

            // Finalmente, guardamos el gasto en la Base de Datos Local (Room)
            val expense = ExpenseEntity(
                title = title,
                originalAmount = originalAmount,
                originalCurrency = originalCurrency.uppercase(),
                convertedAmount = finalConvertedAmount, // ¡Aquí va el monto ya convertido por la API!
                receiptPhotoUri = receiptPhotoUri,
                dateTimestamp = System.currentTimeMillis()
            )
            repository.addExpense(expense)
        }
    }

    // 4. Función para cambiar el modo oscuro
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
// Esta clase extra es necesaria para enseñarle a Android cómo construir
// nuestro ViewModel, ya que le estamos pasando el Repositorio como parámetro.
class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}