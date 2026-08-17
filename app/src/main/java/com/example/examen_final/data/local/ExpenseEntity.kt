package com.example.examen_final.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses_table")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val originalAmount: Double,
    val originalCurrency: String,
    val convertedAmount: Double,
    val dateTimestamp: Long,
    val receiptPhotoUri: String?
)