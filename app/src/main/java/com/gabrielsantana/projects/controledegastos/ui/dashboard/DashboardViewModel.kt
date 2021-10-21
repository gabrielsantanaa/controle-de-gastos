package com.gabrielsantana.projects.controledegastos.ui.dashboard

import androidx.lifecycle.*
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import com.gabrielsantana.projects.controledegastos.domain.usecase.*
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
    private val getTotalAmountByTransactionTypeUseCase: GetTotalAmountByTransactionTypeUseCase,
    private val observeTransactionsByDateUseCase: ObserveTransactionsByDateUseCase
) : ViewModel() {

    sealed class Event {
        object ShowSelectDateDialog : Event()

    }

    private val _selectedDate = MutableLiveData(Date())
    val selectedDate = _selectedDate.asLiveData()

    val transactions: LiveData<List<Transaction>> = _selectedDate.switchMap { date ->
        observeTransactionsByDateUseCase(date)
    }

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _amountsVisibility = MutableLiveData(true)
    val amountsVisibility = _amountsVisibility.asLiveData()

    private val _currentBalance = MutableLiveData(0.0)
    val currentBalance = _currentBalance.asLiveData()

    private val _currentExpenses = MutableLiveData(0.0)
    val currentExpenses = _currentExpenses.asLiveData()

    private val _currentIncomes = MutableLiveData(0.0)
    val currentIncomes = _currentIncomes.asLiveData()

    /* functions to call events */

    fun showSelectDateDialog() = viewModelScope.launch {
        _eventChannel.send(Event.ShowSelectDateDialog)
    }

    /* functions for state change */

    fun updateAmountsVisibility() {
        _amountsVisibility.value = !(_amountsVisibility.value!!)
    }

    fun decreaseYearFromDate() {
        _selectedDate.value = _selectedDate.value!!.decreaseYear()
    }

    fun increaseYearFromDate() {
        _selectedDate.value = _selectedDate.value!!.increaseYear()
    }

    fun updateDate(date: Date) {
        _selectedDate.value = date
    }


    /* use cases */

    fun fetchAmounts() {
        val date = _selectedDate.value!!
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