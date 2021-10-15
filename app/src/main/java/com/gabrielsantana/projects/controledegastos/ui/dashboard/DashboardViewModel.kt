package com.gabrielsantana.projects.controledegastos.ui.dashboard

import androidx.lifecycle.*
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import com.gabrielsantana.projects.controledegastos.domain.usecase.DeleteTransactionUseCase
import com.gabrielsantana.projects.controledegastos.domain.usecase.GetTotalAmountByTransactionTypeUseCase
import com.gabrielsantana.projects.controledegastos.domain.usecase.ObserveTransactionsByDateUseCase
import com.gabrielsantana.projects.controledegastos.domain.usecase.ObserveTransactionsByTitleUseCase
import com.gabrielsantana.projects.controledegastos.util.asLiveData
import com.gabrielsantana.projects.controledegastos.util.decreaseYear
import com.gabrielsantana.projects.controledegastos.util.increaseYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getTotalAmountByTransactionTypeUseCase: GetTotalAmountByTransactionTypeUseCase,
    private val observeTransactionsByDateUseCase: ObserveTransactionsByDateUseCase,
    private val observeTransactionsByTitleUseCase: ObserveTransactionsByTitleUseCase
) : ViewModel() {

    sealed class Event {
        object NavigateToAddTransaction : Event()
        class ShowDeletionSnackbar(val transaction: Transaction) : Event()
        class ShowTransactionDetails(val transaction: Transaction) : Event()
        object ShowSelectDateDialog : Event()
    }

    sealed class Filter(val date: Date) {
        class AllTransactions(date: Date) : Filter(date)
        class SearchTransactions(val query: String) : Filter(Date())
    }

    class SearchMode(val isActive: Boolean, val animate: Boolean)

    private val _filter = MutableLiveData<Filter>(Filter.AllTransactions(Date()))
    val filter = _filter.asLiveData()

    val transactions: LiveData<List<Transaction>> = _filter.switchMap { filter ->
        when (filter) {
            is Filter.AllTransactions -> {
                fetchAmounts()
                observeTransactionsByDateUseCase.invoke(filter.date)
            }
            is Filter.SearchTransactions -> {
                observeTransactionsByTitleUseCase.invoke("%${filter.query}%")
            }
        }
    }

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _amountsVisibility = MutableLiveData(true)
    val amountsVisibility = _amountsVisibility.asLiveData()

    private val _searchMode = MutableLiveData(SearchMode(isActive = false, animate = false))
    val searchMode = _searchMode.asLiveData()

    private val _currentBalance = MutableLiveData(0.0)
    val currentBalance = _currentBalance.asLiveData()

    private val _currentExpenses = MutableLiveData(0.0)
    val currentExpenses = _currentExpenses.asLiveData()

    private val _currentIncomes = MutableLiveData(0.0)
    val currentIncomes = _currentIncomes.asLiveData()

    private val _fabsVisibility = MutableLiveData(true)
    val fabsVisibility = _fabsVisibility.asLiveData()


    /* functions to call events */

    fun showSelectDateDialog() = viewModelScope.launch {
        _eventChannel.send(Event.ShowSelectDateDialog)
    }

    fun navigateToAddTransaction() = viewModelScope.launch {
        _eventChannel.send(Event.NavigateToAddTransaction)
    }

    fun showTransactionDetails(transaction: Transaction) = viewModelScope.launch {
        _eventChannel.send(Event.ShowTransactionDetails(transaction))
    }

    fun showDeletionSnackbar(transaction: Transaction) = viewModelScope.launch {
        _eventChannel.send(Event.ShowDeletionSnackbar(transaction))
    }

    /* functions for state change */

    fun updateAmountsVisibility() {
        _amountsVisibility.value = !(_amountsVisibility.value!!)
    }

    fun updateFabsVisibility(show: Boolean) {
        _fabsVisibility.value = show
    }

    fun updateSearchMode(show: Boolean, animate: Boolean) {
        _searchMode.value = SearchMode(show, animate)
    }

    fun filterTransactionsByTitle(query: String) {
        _filter.value = Filter.SearchTransactions(query)
    }

    fun decreaseYearFromDate() {
        _filter.value = Filter.AllTransactions(filter.value!!.date.decreaseYear())
    }

    fun increaseYearFromDate() {
        _filter.value = Filter.AllTransactions(filter.value!!.date.increaseYear())
    }

    fun updateDate(date: Date) {
        _filter.value = Filter.AllTransactions(date)
    }

    /* use cases */

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        deleteTransactionUseCase.invoke(transaction)
        fetchAmounts()
    }

    fun fetchAmounts() {
        val date = _filter.value!!.date
        viewModelScope.launch(Dispatchers.IO) {
            val expenses = getTotalAmountByTransactionTypeUseCase
                .invoke(TransactionType.EXPENSE, date)
            val incomes = getTotalAmountByTransactionTypeUseCase
                .invoke(TransactionType.INCOME, date)
            val balance = incomes - expenses
            _currentExpenses.postValue(expenses)
            _currentIncomes.postValue(incomes)
            _currentBalance.postValue(balance)
        }
    }


}