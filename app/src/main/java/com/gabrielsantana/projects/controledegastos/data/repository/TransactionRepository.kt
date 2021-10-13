package com.gabrielsantana.projects.controledegastos.data.repository

import androidx.lifecycle.LiveData
import com.gabrielsantana.projects.controledegastos.data.datasource.TransactionDataSource
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val dataSource: TransactionDataSource
) {

    suspend fun createTransaction(transaction: Transaction) =
        dataSource.createTransaction(transaction)

    fun observeTransactionsByDate(date: Date): LiveData<List<Transaction>> =
        dataSource.observeTransactionsByDate(date)

    suspend fun deleteTransaction(transaction: Transaction) =
        dataSource.deleteTransaction(transaction)

    suspend fun getTotalAmountByTransactionTypeUseCase(transactionType: TransactionType, date: Date): Double =
        dataSource.getTotalAmountByTransactionTypeUseCase(transactionType, date)

}