package com.gabrielsantana.projects.controledegastos.domain.usecase

import com.gabrielsantana.projects.controledegastos.data.repository.TransactionRepository
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import java.util.*
import javax.inject.Inject

interface GetTotalAmountByTransactionTypeUseCase {
    suspend operator fun invoke(transactionType: TransactionType, date: Date): Double
}

class GetTotalAmountByTransactionTypeUseCaseImpl @Inject constructor(
    private val repository: TransactionRepository
) : GetTotalAmountByTransactionTypeUseCase {
    override suspend fun invoke(transactionType: TransactionType, date: Date): Double {
        return repository.getTotalAmountByTransactionTypeUseCase(transactionType, date)
    }
}