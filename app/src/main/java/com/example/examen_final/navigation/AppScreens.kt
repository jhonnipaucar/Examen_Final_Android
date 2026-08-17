package com.example.examen_final.navigation

sealed class AppScreens(val route: String) {
    object Dashboard : AppScreens("dashboard_screen")
    object AddExpense : AppScreens("add_expense_screen")
    object ExpenseHistory : AppScreens("history_screen")
    object ExpenseDetail : AppScreens("detail_screen/{expenseId}")
    object Settings : AppScreens("settings_screen")
}