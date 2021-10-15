package com.gabrielsantana.projects.controledegastos.domain.usecase

import com.gabrielsantana.projects.controledegastos.data.repository.TransactionRepository
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import javax.inject.Inject

interface DeleteTransactionUseCase {
    suspend operator fun invoke(transaction: Transaction)
}

class DeleteTransactionUseCaseImpl @Inject constructor(
    private val repository: TransactionRepository
) : DeleteTransactionUseCase {

    override suspend fun invoke(transaction: Transaction) {
        repository.deleteTransaction(transaction)
    }
}
