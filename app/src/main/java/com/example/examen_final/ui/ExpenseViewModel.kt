package com.example.examen_final.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.examen_final.data.local.ExpenseEntity
import com.example.examen_final.domain.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    // 3. Función para agregar un gasto nuevo (se ejecuta en segundo plano)
    fun addExpense(
        title: String,
        originalAmount: Double,
        originalCurrency: String,
        convertedAmount: Double,
        receiptPhotoUri: String?
    ) {
        viewModelScope.launch {
            val newExpense = ExpenseEntity(
                title = title,
                originalAmount = originalAmount,
                originalCurrency = originalCurrency,
                convertedAmount = convertedAmount,
                dateTimestamp = System.currentTimeMillis(), // Guarda la fecha y hora exacta actual
                receiptPhotoUri = receiptPhotoUri
            )
            repository.addExpense(newExpense)
        }
    }

    // 4. Función para cambiar el modo oscuro
    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            repository.toggleDarkMode(isDark)
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