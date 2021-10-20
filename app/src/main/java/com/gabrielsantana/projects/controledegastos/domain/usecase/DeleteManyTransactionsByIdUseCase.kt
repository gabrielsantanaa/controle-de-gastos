package com.gabrielsantana.projects.controledegastos.domain.usecase

import com.gabrielsantana.projects.controledegastos.data.repository.TransactionRepository
import javax.inject.Inject

interface DeleteManyTransactionsByIdUseCase {
    suspend operator fun invoke(
        transactions: List<Long>
    )
}

class DeleteManyTransactionsByIdUseCaseImpl @Inject constructor(
    private val repository: TransactionRepository
) : DeleteManyTransactionsByIdUseCase {

    override suspend fun invoke(transactions: List<Long>) {
        repository.deleteManyTransactionsById(transactions)
    }

}