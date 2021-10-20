package com.gabrielsantana.projects.controledegastos.data.datasource

import androidx.lifecycle.LiveData
import com.gabrielsantana.projects.controledegastos.data.db.TransactionDatabaseDao
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import java.util.*
import javax.inject.Inject

class RoomTransactionDataSource @Inject constructor(
    private val dao: TransactionDatabaseDao
) : TransactionDataSource {

    override suspend fun createTransaction(transaction: Transaction) {
        dao.insert(transaction)
    }

    override fun observeTransactionsByDate(date: Date): LiveData<List<Transaction>> {
        return dao.observeTransactionsByDate(date.time)
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

    override fun observeTransactionsByTitle(query: String): LiveData<List<Transaction>> {
        return dao.observeTransactionsByTitle(query)
    }

    override fun deleteManyTransactionsById(transactions: List<Long>) {
        dao.deleteManyTransactionsById(transactions)
    }
}