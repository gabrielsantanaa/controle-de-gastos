package com.gabrielsantana.projects.controledegastos.domain.usecase

import com.gabrielsantana.projects.controledegastos.data.repository.TransactionRepository
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionCategory
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import java.util.*
import javax.inject.Inject

interface CreateTransactionUseCase {
    suspend operator fun invoke(
        title: String,
        description: String,
        transactionCategory: TransactionCategory,
        amountSpent: Double,
        type: TransactionType,
        date: Date
    )
}

class CreateTransactionUseCaseImpl @Inject constructor(
    private val repository: TransactionRepository
): CreateTransactionUseCase {
    override suspend fun invoke(
        title: String,
        description: String,
        transactionCategory: TransactionCategory,
        amountSpent: Double,
        type: TransactionType,
        date: Date
    ) {
        val transaction = Transaction(
            title = title,
            transactionCategory = transactionCategory,
            description = description,
            amountSpent = amountSpent,
            transactionType = type,
            dateCreation = date
        )
        repository.createTransaction(transaction)
    }

}