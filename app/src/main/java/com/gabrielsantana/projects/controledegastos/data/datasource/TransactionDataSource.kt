package com.gabrielsantana.projects.controledegastos.data.datasource

import androidx.lifecycle.LiveData
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.*

interface TransactionDataSource {

    suspend fun createTransaction(transaction: Transaction)

    fun observeTransactionsByDate(date: Date): LiveData<List<Transaction>>

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun getTotalAmountByTransactionTypeUseCase(transactionType: TransactionType, date: Date): Double

}