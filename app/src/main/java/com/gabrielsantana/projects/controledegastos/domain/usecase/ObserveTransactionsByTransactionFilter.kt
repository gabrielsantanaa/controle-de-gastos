package com.gabrielsantana.projects.controledegastos.domain.usecase

import androidx.paging.PagingSource
import com.gabrielsantana.projects.controledegastos.data.repository.TransactionRepository
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.ui.transationhistory.TransactionHistoryViewModel.TransactionFilter
import javax.inject.Inject

interface ObserveTransactionsByTransactionFilterUseCase {
    operator fun invoke(transactionFilter: TransactionFilter): PagingSource<Int, Transaction>
}

class ObserveTransactionsByTransactionFilterUseCaseImpl @Inject constructor(
    private val repository: TransactionRepository
) : ObserveTransactionsByTransactionFilterUseCase {
    override fun invoke(transactionFilter: TransactionFilter): PagingSource<Int, Transaction> {
        return repository.observeTransactionsByTransactionFilter(transactionFilter)
    }

}