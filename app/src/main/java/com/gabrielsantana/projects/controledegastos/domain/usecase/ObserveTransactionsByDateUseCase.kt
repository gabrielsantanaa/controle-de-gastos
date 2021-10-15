package com.gabrielsantana.projects.controledegastos.domain.usecase

import androidx.lifecycle.LiveData
import com.gabrielsantana.projects.controledegastos.data.repository.TransactionRepository
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import java.util.*
import javax.inject.Inject

interface ObserveTransactionsByDateUseCase {
    operator fun invoke(date: Date): LiveData<List<Transaction>>
}

class ObserveTransactionsByDateUseCaseImpl @Inject constructor(
    private val repository: TransactionRepository
) : ObserveTransactionsByDateUseCase {
    override fun invoke(date: Date): LiveData<List<Transaction>> {
        return repository.observeTransactionsByDate(date)
    }

}