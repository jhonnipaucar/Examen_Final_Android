package com.example.examen_final

import android.app.Application
import androidx.room.Room
import com.example.examen_final.data.local.ExpenseDatabase
import com.example.examen_final.data.local.UserPreferences
import com.example.examen_final.domain.ExpenseRepository

class ExpenseApplication : Application() {

    // Usamos 'lazy' para que la base de datos solo se construya cuando se necesite por primera vez
    val database by lazy {
        Room.databaseBuilder(
            this,
            ExpenseDatabase::class.java,
            "expense_database"
        ).build()
    }

    val userPreferences by lazy { UserPreferences(this) }

    // Nuestro "gerente" que le pasaremos al ViewModel
    val repository by lazy {
        ExpenseRepository(database.expenseDao(), userPreferences)
    }
}