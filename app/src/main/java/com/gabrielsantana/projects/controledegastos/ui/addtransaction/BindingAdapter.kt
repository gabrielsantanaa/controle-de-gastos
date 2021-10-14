package com.gabrielsantana.projects.controledegastos.ui.addtransaction

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.core.view.children
import com.gabrielsantana.projects.controledegastos.databinding.AddTransactionFragmentBinding
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionCategory
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.ChipGroup

class BindingAdapter(private val viewModel: AddTransactionViewModel, private val binding: AddTransactionFragmentBinding) {

    init {
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                viewModel.onBackPressed()
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

    fun MaterialToolbar.setOnNavigationIconClick(onClick: () -> Unit) {
        setNavigationOnClickListener {
            onClick()
        }
    }

    fun ChipGroup.setTransactionCategory(category: TransactionCategory?) {
        category?.let {
            val chip = children.single { (it as CustomChip).getTransactionCategory() == category } as CustomChip
            chip.isCheckable = true
            chip.isChecked = true
        }

    }
    fun ChipGroup.getTransactionCategory(): TransactionCategory {
        val chip = findViewById<CustomChip>(checkedChipId)
        return chip.getTransactionCategory()
    }

/////////////////////////////////

    fun MaterialButtonToggleGroup.setTransactionCategory(type: TransactionType) {
        val chip = children.single { (it as CustomButton).getTransactionType() == type } as CustomButton
        chip.isCheckable = true
        chip.isChecked = true
    }

    fun MaterialButtonToggleGroup.getTransactionCategory(): TransactionType {
        val chip = findViewById<CustomButton>(checkedButtonId)
        return chip.getTransactionType()
    }
}


