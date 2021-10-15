package com.gabrielsantana.projects.controledegastos.domain.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gabrielsantana.projects.controledegastos.R
import java.io.Serializable
import java.util.*

@Entity(tableName = "transactions_table")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "transaction_category")
    val transactionCategory: TransactionCategory,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "date_creation")
    val dateCreation: Date,
    @ColumnInfo(name = "amount_spent")
    val amountSpent: Double,
    @ColumnInfo(name = "transaction_type")
    val transactionType: TransactionType
) : Serializable {
    @ColorRes
    fun getColorRes() =
        if (transactionType == TransactionType.EXPENSE) R.color.red_expenses
        else R.color.green_incomes

    @DrawableRes
    fun getTypeIcon() =
        if (transactionType == TransactionType.EXPENSE) R.drawable.ic_arrow_expenses
        else R.drawable.ic_arrow_incomes
}


