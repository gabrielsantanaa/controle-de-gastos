package com.gabrielsantana.projects.controledegastos.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import java.util.*

@Dao
interface TransactionDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg transactions: Transaction)

    @Query("SELECT * FROM transactions_table WHERE strftime('%Y %m', datetime(date_creation / 1000, 'unixepoch')) = strftime('%Y %m', datetime(:date / 1000, 'unixepoch')) ORDER BY id DESC")
    fun observeTransactionsByDate(date: Long): LiveData<List<Transaction>>

    @Query("SELECT SUM(amount_spent) FROM transactions_table WHERE transaction_type = :transactionType and strftime('%Y %m', datetime(date_creation / 1000, 'unixepoch')) = strftime('%Y %m', datetime(:date / 1000, 'unixepoch'))")
    suspend fun getTotalAmountByTransactionTypeUseCase(transactionType: TransactionType, date: Date): Double

    @Query("SELECT * FROM transactions_table WHERE title LIKE :query")
    fun observeTransactionsByTitle(query: String): LiveData<List<Transaction>>


}