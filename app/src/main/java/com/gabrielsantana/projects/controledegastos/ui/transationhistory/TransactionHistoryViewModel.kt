package com.gabrielsantana.projects.controledegastos.ui.transationhistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.gabrielsantana.projects.controledegastos.data.db.TransactionDatabaseDao
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.usecase.ObserveTransactionsByDateUseCase
import com.gabrielsantana.projects.controledegastos.domain.usecase.ObserveTransactionsByTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    observeTransactionsByDateUseCase: ObserveTransactionsByDateUseCase,
    observeTransactionsByTitleUseCase: ObserveTransactionsByTitleUseCase,

    ) : ViewModel() {

    sealed class TransactionFilter {
        class TransactionsByDate(val date: Date) : TransactionFilter()
        class TransactionsByTitle(val query: String) : TransactionFilter()
    }

    private val _transactionFilter = MutableLiveData<TransactionFilter>(
        TransactionFilter.TransactionsByDate(Date())
    )

    val transactions: LiveData<PagingData<Transaction>> = _transactionFilter.switchMap { filter ->
        Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                maxSize = 100
            )
        ) {
            when (filter) {
                is TransactionFilter.TransactionsByDate -> {
                    observeTransactionsByDateUseCase.invoke(filter.date)
                }
                is TransactionFilter.TransactionsByTitle -> {
                    observeTransactionsByTitleUseCase.invoke(filter.query)
                }
            }
        }.liveData
    }


    fun filterTransactionByTitle(title: String) {
        _transactionFilter.value = TransactionFilter.TransactionsByTitle(title)
    }

    fun filterTransactionsByDate() {
        _transactionFilter.value = TransactionFilter.TransactionsByDate(Date())
    }

}