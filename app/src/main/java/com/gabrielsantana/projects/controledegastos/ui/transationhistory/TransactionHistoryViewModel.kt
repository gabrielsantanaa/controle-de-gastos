package com.gabrielsantana.projects.controledegastos.ui.transationhistory

import androidx.lifecycle.ViewModel
import com.gabrielsantana.projects.controledegastos.domain.usecase.ObserveTransactionsByDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    observeTransactionsByDateUseCase: ObserveTransactionsByDateUseCase
) : ViewModel() {

    val transactions = observeTransactionsByDateUseCase.invoke(Date())

}