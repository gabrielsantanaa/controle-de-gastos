package com.gabrielsantana.projects.controledegastos.ui.components

import android.content.Context
import android.util.AttributeSet
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionCategory
import com.google.android.material.chip.Chip

class CustomChip @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Chip(context, attrs, defStyleAttr) {

    private var transactionCategory = TransactionCategory.FASTFOOD

    fun setTransactionCategory(category: TransactionCategory) {
        id = category.ordinal
        setText(category.nameStringRes)
        transactionCategory = category
        setChipIconResource(category.drawableResId)
    }

    fun getTransactionCategory() = transactionCategory
}