package com.gabrielsantana.projects.controledegastos.ui.transationhistory

import androidx.lifecycle.*
import androidx.paging.*
import com.gabrielsantana.projects.controledegastos.data.db.TransactionDatabaseDao
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.usecase.ObserveTransactionsByDateUseCase
import com.gabrielsantana.projects.controledegastos.domain.usecase.ObserveTransactionsByTitleUseCase
import com.gabrielsantana.projects.controledegastos.util.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    observeTransactionsByDateUseCase: ObserveTransactionsByDateUseCase,
    observeTransactionsByTitleUseCase: ObserveTransactionsByTitleUseCase,
): ViewModel() {

    sealed class Event {
        object ClearSelection: Event()
    }

    sealed class TransactionFilter {
        class TransactionsByDate(val date: Date) : TransactionFilter()
        class TransactionsByTitle(val query: String) : TransactionFilter()
    }

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _selectedTransactionIds = MutableLiveData<List<Long>>()
    val selectedTransactionIds = _selectedTransactionIds.asLiveData()

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
        }.liveData.cachedIn(viewModelScope)
    }


    fun filterTransactionByTitle(title: String) {
        _transactionFilter.value = TransactionFilter.TransactionsByTitle(title)
    }

    fun filterTransactionsByDate() {
        _transactionFilter.value = TransactionFilter.TransactionsByDate(Date())
    }

    fun updateSelectedTransactions(value: List<Long>) {
        _selectedTransactionIds.value = value
    }

    fun clearSelection() = viewModelScope.launch {
        _eventChannel.send(Event.ClearSelection)
    }

}