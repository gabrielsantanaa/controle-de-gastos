package com.gabrielsantana.projects.controledegastos.data.repository

import androidx.paging.PagingSource
import com.gabrielsantana.projects.controledegastos.data.datasource.TransactionDataSource
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import com.gabrielsantana.projects.controledegastos.ui.transationhistory.TransactionHistoryViewModel.TransactionFilter
import java.util.*
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val dataSource: TransactionDataSource
) {

    suspend fun createTransaction(transaction: Transaction) =
        dataSource.createTransaction(transaction)

    fun observeTransactionsByTransactionFilter(transactionFilter: TransactionFilter): PagingSource<Int, Transaction> =
        dataSource.observeTransactionsByTransactionFilter(transactionFilter)

    suspend fun deleteTransaction(transaction: Transaction) =
        dataSource.deleteTransaction(transaction)

    suspend fun getTotalAmountByTransactionTypeUseCase(
        transactionType: TransactionType,
        date: Date
    ): Double =
        dataSource.getTotalAmountByTransactionType(transactionType, date)

    fun deleteManyTransactionsById(transactions: List<Long>) {
        dataSource.deleteManyTransactionsById(transactions)
    }


}