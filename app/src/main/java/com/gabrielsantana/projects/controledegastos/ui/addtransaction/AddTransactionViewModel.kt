package com.gabrielsantana.projects.controledegastos.ui.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionCategory
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import com.gabrielsantana.projects.controledegastos.domain.usecase.CreateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val createTransactionUseCase: CreateTransactionUseCase
) : ViewModel() {

    sealed class Event {
        object NavigateToDashboardFragment : Event()
        object InvalidFields : Event()
        object ShowDatePicker : Event()
        object ShowClosingConfirmationDialog : Event()
    }

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private var _transactionTitle: String? = null

    private var _transactionDescription: String? = null

    private var _transactionAmountSpent: Double? = null

    private var _transactionType: TransactionType? = null

    private var _transactionCategory: TransactionCategory? = null

    /* update data */

    fun setTransactionTitle(value: String) {
        _transactionTitle = value
    }

    fun setTransactionDescription(value: String) {
        _transactionDescription = value
    }

    fun setTransactionAmountSpent(value: Double) {
        _transactionAmountSpent = value
    }

    fun setTransactionType(value: TransactionType) {
        _transactionType = value
    }

    fun setTransactionCategory(value: TransactionCategory) {
        _transactionCategory = value
    }

    /* functions to trigger events*/

    fun navigateToDashboardFragment() = viewModelScope.launch {
        _eventChannel.send(Event.NavigateToDashboardFragment)
    }

    fun showDatePicker() = viewModelScope.launch {
        _eventChannel.send(if (fieldsAreNotEmpty()) Event.ShowDatePicker else Event.InvalidFields)
    }

    fun onBackPressed() = viewModelScope.launch {
        if (fieldsAreEmpty()) {
            navigateToDashboardFragment()
        } else {
            _eventChannel.send(Event.ShowClosingConfirmationDialog)
        }
    }

    private fun fieldsAreEmpty(): Boolean {
        return _transactionTitle.isNullOrEmpty() &&
                _transactionDescription.isNullOrEmpty() &&
                _transactionAmountSpent == null &&
                _transactionType == null &&
                _transactionCategory == null
    }

    private fun fieldsAreNotEmpty(): Boolean {
        return !_transactionTitle.isNullOrEmpty() &&
                !_transactionDescription.isNullOrEmpty() &&
                _transactionAmountSpent != null &&
                _transactionType != null &&
                _transactionCategory != null

    }

    /* use cases */

    fun saveTransaction(date: Date) = viewModelScope.launch(Dispatchers.IO) {
        createTransactionUseCase.invoke(
            title = _transactionTitle!!,
            description = _transactionDescription!!,
            transactionCategory = _transactionCategory!!,
            amountSpent = _transactionAmountSpent!!,
            type = _transactionType!!,
            date = date
        )
        navigateToDashboardFragment()
    }


}