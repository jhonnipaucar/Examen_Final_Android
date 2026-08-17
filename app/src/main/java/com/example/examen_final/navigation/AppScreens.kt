package com.example.examen_final.navigation

// Usamos una 'sealed class' para tener rutas seguras y evitar errores de tipeo
sealed class AppScreens(val route: String) {
    object Dashboard : AppScreens("dashboard_screen")
    object AddExpense : AppScreens("add_expense_screen")
    object ExpenseHistory : AppScreens("expense_history_screen")
    object ExpenseDetail : AppScreens("expense_detail_screen")
    object Settings : AppScreens("settings_screen")
}