package com.gabrielsantana.projects.controledegastos.data.datasource

import androidx.paging.PagingSource
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import java.util.*

interface TransactionDataSource {

    suspend fun createTransaction(transaction: Transaction)

    fun observeTransactionsByDate(date: Date): PagingSource<Int, Transaction>

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun getTotalAmountByTransactionType(
        transactionType: TransactionType,
        date: Date
    ): Double

    fun observeTransactionsByTitle(query: String): PagingSource<Int, Transaction>

    fun deleteManyTransactionsById(transactions: List<Long>)

}