package com.gabrielsantana.projects.controledegastos.domain.usecase

import androidx.lifecycle.LiveData
import com.gabrielsantana.projects.controledegastos.data.repository.TransactionRepository
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import javax.inject.Inject

interface ObserveTransactionsByTitleUseCase {
    operator fun invoke(query: String): LiveData<List<Transaction>>
}

class ObserveTransactionsByTitleUseCaseImpl @Inject constructor(
    private val repository: TransactionRepository
) : ObserveTransactionsByTitleUseCase {
    override fun invoke(query: String): LiveData<List<Transaction>> {
        return repository.observeTransactionsByTitle(query)
    }

}