package com.gabrielsantana.projects.controledegastos.data.datasource

import androidx.paging.PagingSource
import com.gabrielsantana.projects.controledegastos.data.db.TransactionDatabaseDao
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import com.gabrielsantana.projects.controledegastos.ui.transationhistory.TransactionHistoryViewModel.TransactionFilter
import java.util.*
import javax.inject.Inject

class RoomTransactionDataSource @Inject constructor(
    private val dao: TransactionDatabaseDao
) : TransactionDataSource {

    override suspend fun createTransaction(transaction: Transaction) {
        dao.insert(transaction)
    }

    override fun observeTransactionsByTransactionFilter(filter: TransactionFilter): PagingSource<Int, Transaction> {
        return when (filter) {
            is TransactionFilter.TransactionsByDate -> {
                dao.observeTransactionsByDate(filter.date.time)
            }
            is TransactionFilter.TransactionsByTitle -> {
                dao.observeTransactionsByTitle("%${filter.query}%")
            }
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        dao.delete(transaction)
    }

    override suspend fun getTotalAmountByTransactionType(
        transactionType: TransactionType,
        date: Date
    ): Double {
        return try {
            dao.getTotalAmountByTransactionTypeUseCase(transactionType, date)
        } catch (e: Exception) {
            0.0
        }
    }

    override fun deleteManyTransactionsById(transactions: List<Long>) {
        dao.deleteManyTransactionsById(transactions)
    }
}