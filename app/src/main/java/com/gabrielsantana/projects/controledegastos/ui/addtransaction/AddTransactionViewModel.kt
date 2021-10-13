package com.gabrielsantana.projects.controledegastos.ui.addtransaction

import androidx.lifecycle.MutableLiveData
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

    val transactionTitle = MutableLiveData<String>()

    val transactionDescription = MutableLiveData<String>()

    val transactionAmountSpent = MutableLiveData<Double>()

    val transactionType = MutableLiveData<TransactionType>()

    val transactionCategory = MutableLiveData<TransactionCategory>()


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
        return transactionTitle.value.isNullOrEmpty() &&
                transactionDescription.value.isNullOrEmpty() &&
                transactionAmountSpent.value == null &&
                transactionType.value == null &&
                transactionCategory.value == null
    }

    private fun fieldsAreNotEmpty(): Boolean {
        return !transactionTitle.value.isNullOrEmpty() &&
                !transactionDescription.value.isNullOrEmpty() &&
                transactionAmountSpent.value != null &&
                transactionType.value != null &&
                transactionCategory.value != null

    }

    /* use cases */

    fun saveTransaction(date: Date) = viewModelScope.launch(Dispatchers.IO) {
        createTransactionUseCase.invoke(
            title = transactionTitle.value!!,
            description = transactionDescription.value!!,
            transactionCategory = transactionCategory.value!!,
            amountSpent = transactionAmountSpent.value!!,
            type = transactionType.value!!,
            date = date
        )
        navigateToDashboardFragment()
    }


}