package com.gabrielsantana.projects.controledegastos.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.databinding.DashboardFragmentBinding
import com.gabrielsantana.projects.controledegastos.util.*
import com.google.android.material.transition.MaterialContainerTransform


class BindingAdapter(
    private val viewModel: DashboardViewModel,
    private val binding: DashboardFragmentBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context
) {

    init {

        setOnClickListeners()
        setLiveDataObservers()
    }

    private fun setOnClickListeners() = binding.apply {
        buttonSelectDate.setOnClickListener {
            viewModel.showSelectDateDialog()
        }
        buttonBalanceVisibility.setOnClickListener {
            viewModel.updateAmountsVisibility()

        }
    }


    private fun setLiveDataObservers() = viewModel.apply {
        currentBalance.observe(lifecycleOwner) {
            binding.textViewBalance.text = it.toCurrency()
        }
        currentExpenses.observe(lifecycleOwner) {
            binding.textViewExpenses.text = it.toCurrency()
        }
        currentIncomes.observe(lifecycleOwner) {
            binding.textViewIncomes.text = it.toCurrency()
        }
        selectedDate.observe(lifecycleOwner) {
            binding.buttonSelectDate.text = it.formatMonthAndYear(LocaleListCompat.getDefault()[0])
        }
        amountsVisibility.observe(lifecycleOwner) {
            binding.apply {
                if (it) {
                    showAmounts()
                    buttonBalanceVisibility.icon = ContextCompat.getDrawable(
                        buttonBalanceVisibility.context,
                        R.drawable.ic_outline_open_eye_24
                    )
                } else {
                    buttonBalanceVisibility.icon = ContextCompat.getDrawable(
                        buttonBalanceVisibility.context,
                        R.drawable.ic_round_closed_eye
                    )
                    hideAmounts()
                }

            }
        }

    }



    private fun showAmounts() = binding.apply {
        textViewIncomes.text = viewModel.currentIncomes.value!!.toCurrency()
        textViewBalance.text = viewModel.currentBalance.value!!.toCurrency()
        textViewExpenses.text = viewModel.currentExpenses.value!!.toCurrency()
    }


    private fun hideAmounts() = binding.apply {
        textViewIncomes.text = "*".repeat(viewModel.currentIncomes.value!!.toString().length)
        textViewBalance.text = "*".repeat(viewModel.currentBalance.value!!.toString().length)
        textViewExpenses.text = "*".repeat(viewModel.currentExpenses.value!!.toString().length)
    }


}


