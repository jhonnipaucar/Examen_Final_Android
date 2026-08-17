package com.example.examen_final.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    // Usamos 'suspend' porque la rúbrica exige corrutinas para operaciones asíncronas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    // Usamos 'Flow' para que la UI se actualice automáticamente cuando agregues un gasto
    @Query("SELECT * FROM expenses_table ORDER BY dateTimestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>
}
}