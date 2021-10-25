package com.gabrielsantana.projects.controledegastos.domain.usecase

import androidx.paging.PagingSource
import com.gabrielsantana.projects.controledegastos.data.repository.TransactionRepository
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import java.util.*
import javax.inject.Inject

interface ObserveTransactionsByDateUseCase {
    operator fun invoke(date: Date): PagingSource<Int, Transaction>
}

class ObserveTransactionsByDateUseCaseImpl @Inject constructor(
    private val repository: TransactionRepository
) : ObserveTransactionsByDateUseCase {
    override fun invoke(date: Date): PagingSource<Int, Transaction> {
        return repository.observeTransactionsByDate(date)
    }

}