package com.example.examen_final.domain

import com.example.examen_final.data.local.ExpenseDao
import com.example.examen_final.data.local.ExpenseEntity
import com.example.examen_final.data.local.UserPreferences
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val userPreferences: UserPreferences
) {
    // --- LÓGICA DE LA BASE DE DATOS (GASTOS) ---

    // Obtiene la lista de gastos actualizada en tiempo real
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    // Inserta un nuevo gasto
    suspend fun addExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    // --- LÓGICA DE PREFERENCIAS (MODO OSCURO) ---

    // Lee si el modo oscuro está activado
    val isDarkMode: Flow<Boolean> = userPreferences.isDarkMode

    // Guarda el cambio cuando el usuario active/desactive el modo oscuro
    suspend fun toggleDarkMode(isDark: Boolean) {
        userPreferences.saveDarkMode(isDark)
    }
}