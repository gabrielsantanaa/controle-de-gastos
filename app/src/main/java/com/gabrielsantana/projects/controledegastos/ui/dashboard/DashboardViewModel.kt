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
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getTotalAmountByTransactionTypeUseCase: GetTotalAmountByTransactionTypeUseCase,
    private val observeTransactionsByDateUseCase: ObserveTransactionsByDateUseCase,
    private val observeTransactionsByTitleUseCase: ObserveTransactionsByTitleUseCase,
    private val deleteManyTransactionsByIdUseCase: DeleteManyTransactionsByIdUseCase
) : ViewModel() {

    sealed class Event {
        object NavigateToAddTransaction : Event()
        class ShowDeletionSnackbar(val transaction: Transaction) : Event()
        class ShowTransactionDetails(val transaction: Transaction) : Event()
        object ShowSelectDateDialog : Event()
        object CloseTransactionSelection : Event()
    }

    sealed class TransactionFilter {
        class TransactionByDate(val date: Date) : TransactionFilter()
        class TransactionByTitle(val query: String) : TransactionFilter()
    }

    sealed class UiModel(var animateUiChange: Boolean) {
        class Search(animateUiChange: Boolean = true) : UiModel(animateUiChange)
        class Dashboard(animateUiChange: Boolean = true) : UiModel(animateUiChange)
    }

    private val _uiModel = MutableLiveData<UiModel>(
        //do not anim the first UiModel state
        UiModel.Dashboard(animateUiChange = false)
    )

    val uiModel = _uiModel.asLiveData()

    private val _selectedDate = MutableLiveData(Date())
    val selectedDate = _selectedDate.asLiveData()

    private val _transactionFilter = MutableLiveData<TransactionFilter>(
        TransactionFilter.TransactionByDate(_selectedDate.value!!)
    )

    val transactions: LiveData<List<Transaction>> = _transactionFilter.switchMap { filter ->
        when (filter) {
            is TransactionFilter.TransactionByDate -> {
                observeTransactionsByDateUseCase.invoke(filter.date)
            }
            is TransactionFilter.TransactionByTitle -> {
                observeTransactionsByTitleUseCase.invoke(filter.query)
            }
        }
    }

    private val _selectedTransactionIds = MutableLiveData(listOf<Long>())
    val selectedTransactionIds = _selectedTransactionIds.asLiveData()

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

    fun closeTransactionSelection() = viewModelScope.launch {
        _eventChannel.send(Event.CloseTransactionSelection)
    }

    /* functions for state change */

    fun updateAmountsVisibility() {
        _amountsVisibility.value = !(_amountsVisibility.value!!)
    }

    fun updateFabsVisibility(show: Boolean) {
        _fabsVisibility.value = show
    }

    fun updateSelectedTransactionIds(list: List<Long>) {
        _selectedTransactionIds.value = list
    }

    fun filterTransactionsByTitle(query: String) {
        _transactionFilter.value = TransactionFilter.TransactionByTitle(query)
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

    /* UiModel update */
    fun updateUiModel(uiModel: UiModel) {
        //each UiModel update must close the transaction selection
        closeTransactionSelection()
        _uiModel.value = uiModel
    }

    fun disableAnimationOnUiModelChange() {
        _uiModel.value!!.animateUiChange = false
    }

    /* use cases */

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        deleteTransactionUseCase.invoke(transaction)
        fetchAmounts()
    }

    fun deleteSelectedTransactions() = viewModelScope.launch(Dispatchers.IO) {
        deleteManyTransactionsByIdUseCase.invoke(_selectedTransactionIds.value!!)
        closeTransactionSelection()
        fetchAmounts()
    }

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