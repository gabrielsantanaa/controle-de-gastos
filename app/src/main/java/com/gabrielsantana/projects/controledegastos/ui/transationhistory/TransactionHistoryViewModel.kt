package com.gabrielsantana.projects.controledegastos.ui.transationhistory

import androidx.lifecycle.*
import androidx.paging.*
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.usecase.DeleteManyTransactionsByIdUseCase
import com.gabrielsantana.projects.controledegastos.domain.usecase.ObserveTransactionsByTransactionFilterUseCase
import com.gabrielsantana.projects.controledegastos.util.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    observeTransactionsByTransactionFilterUseCase: ObserveTransactionsByTransactionFilterUseCase,
    private val deleteManyTransactionsByIdUseCase: DeleteManyTransactionsByIdUseCase
) : ViewModel() {

    sealed class Event {
        object ClearSelection : Event()
        object SelectAllTransactions : Event()
        object ShowTransactionsDeletionConfirmation : Event()
    }

    sealed class TransactionFilter {
        class TransactionsByDate(val date: Date) : TransactionFilter()
        class TransactionsByTitle(val query: String) : TransactionFilter()
    }

    var isFirstInit: Boolean = true

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
            observeTransactionsByTransactionFilterUseCase.invoke(filter)
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

    fun showTransactionsDeletionConfirmation() = viewModelScope.launch {
        _eventChannel.send(Event.ShowTransactionsDeletionConfirmation)
    }

    fun deleteSelectedTransactions() = viewModelScope.launch(Dispatchers.IO) {
        deleteManyTransactionsByIdUseCase.invoke(_selectedTransactionIds.value!!)
    }

    fun selectAllTransactions() = viewModelScope.launch {
        _eventChannel.send(Event.SelectAllTransactions)
    }

}