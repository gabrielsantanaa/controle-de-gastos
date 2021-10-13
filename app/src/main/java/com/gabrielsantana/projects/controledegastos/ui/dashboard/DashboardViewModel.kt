package com.gabrielsantana.projects.controledegastos.ui.dashboard

import androidx.lifecycle.*
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import com.gabrielsantana.projects.controledegastos.domain.usecase.DeleteTransactionUseCase
import com.gabrielsantana.projects.controledegastos.domain.usecase.GetTotalAmountByTransactionTypeUseCase
import com.gabrielsantana.projects.controledegastos.domain.usecase.ObserveTransactionsByDateUseCase
import com.gabrielsantana.projects.controledegastos.util.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getTotalAmountByTransactionTypeUseCase: GetTotalAmountByTransactionTypeUseCase,
    observeTransactionsByDateUseCase: ObserveTransactionsByDateUseCase
) : ViewModel() {

    sealed class Event {
        object NavigateToAddTransaction : Event()
        class ShowDeletionSnackbar(val transaction: Transaction): Event()
        class ShowTransactionDetails(val transaction: Transaction): Event()
        object ShowSelectDateDialog: Event()
        object ShowSearchBar: Event()
        object CloseSearchBar: Event()
    }

    private val _currentDate = MutableLiveData(Date())
    val currentDate = _currentDate.asLiveData()

    val transactions: LiveData<List<Transaction>> = currentDate.switchMap {
        observeTransactionsByDateUseCase.invoke(it)
    }

    fun updateCurrentDate(date: Date) {
        _currentDate.value = date
    }


    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _balanceVisibility = MutableLiveData(true)
    val balanceVisibility: LiveData<Boolean> = _balanceVisibility

    private val _currentBalance = MutableLiveData(0.0)
    val currentBalance: LiveData<Double> = _currentBalance

    private val _currentExpenses = MutableLiveData(0.0)
    val currentExpenses: LiveData<Double> = _currentExpenses

    private val _currentIncomes = MutableLiveData(0.0)
    val currentIncomes: LiveData<Double> = _currentIncomes

    private val _fabsVisibility = MutableLiveData(true)
    val fabsVisibility = _fabsVisibility.asLiveData()

    init {
        fetchAmounts()
    }

    /* functions to call events */

    fun showSearchBar() = viewModelScope.launch {
        _eventChannel.send(Event.ShowSearchBar)
    }

    fun closeSearchBar() = viewModelScope.launch {
        _eventChannel.send(Event.CloseSearchBar)
    }

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
        _balanceVisibility.value = !(_balanceVisibility.value!!)
    }

    fun updateFabsVisibility(show: Boolean) {
        _fabsVisibility.value = show
    }

    /* use cases */

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        deleteTransactionUseCase.invoke(transaction)
        fetchAmounts()
    }

    fun fetchAmounts() {
        viewModelScope.launch(Dispatchers.IO) {
            val expenses = getTotalAmountByTransactionTypeUseCase
                .invoke(TransactionType.EXPENSE, currentDate.value!!)
            val incomes = getTotalAmountByTransactionTypeUseCase
                .invoke(TransactionType.INCOME, currentDate.value!!)
            val balance = incomes - expenses
            _currentExpenses.postValue(expenses)
            _currentIncomes.postValue(incomes)
            _currentBalance.postValue(balance)
        }
    }

}