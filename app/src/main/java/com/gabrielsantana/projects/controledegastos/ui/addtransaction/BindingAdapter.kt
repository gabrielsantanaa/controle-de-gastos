package com.gabrielsantana.projects.controledegastos.ui.addtransaction

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.gabrielsantana.projects.controledegastos.databinding.AddTransactionFragmentBinding
import com.gabrielsantana.projects.controledegastos.util.CurrencyTextWatcher

class BindingAdapter(
    private val viewModel: AddTransactionViewModel,
    private val binding: AddTransactionFragmentBinding
) {

    init {
        setOnClickListeners()
        setFieldListeners()
        setupDescriptionField()
    }

    private fun setFieldListeners() {
        setTitleFieldListener()
        setDescriptionFieldListener()
        setAmountSpentFieldListener()
        setCategoryFieldListener()
        setTypeFieldListener()
    }

    private fun setOnClickListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                viewModel.onBackPressed()
            }
            buttonSave.setOnClickListener {
                viewModel.showDatePicker()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDescriptionField() {
        //fix text field scroll when it is focused
        binding.textInputEditTextDescription.apply {
            setOnTouchListener(View.OnTouchListener { v, event ->
                if (hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return@OnTouchListener true
                        }
                    }
                }
                false
            })
        }
    }

    private fun setTitleFieldListener() =
        binding.textInputEditTextTitle.addTextChangedListener {
            viewModel.setTransactionTitle(it.toString())
        }

    private fun setDescriptionFieldListener() =
        binding.textInputEditTextDescription.addTextChangedListener {
            viewModel.setTransactionDescription(it.toString())
        }

    private fun setAmountSpentFieldListener() {
        binding.textInputEditTextAmountSpent.apply {
            addTextChangedListener(CurrencyTextWatcher(this, viewModel::setTransactionAmountSpent))
        }
    }

    private fun setTypeFieldListener() =
        binding.toggleButtonTransactionType.addOnButtonCheckedListener { _, checkedId, _ ->
            val toggleButton = binding.root.findViewById<CustomButton>(checkedId)
            viewModel.setTransactionType(toggleButton.getTransactionType())
        }

    private fun setCategoryFieldListener() =
        binding.chipGroupCategory.setOnCheckedChangeListener { _, checkedId ->
            val chip = binding.root.findViewById<CustomChip?>(checkedId)
            viewModel.setTransactionCategory(chip.getTransactionCategory())
        }


}


