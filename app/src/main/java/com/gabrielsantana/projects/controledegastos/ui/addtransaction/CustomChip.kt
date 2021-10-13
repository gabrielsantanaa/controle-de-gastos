package com.gabrielsantana.projects.controledegastos.ui.addtransaction

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.ViewCompat
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionCategory
import com.gabrielsantana.projects.controledegastos.util.themeColor
import com.google.android.material.chip.Chip

class CustomChip @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): Chip(context, attrs, defStyleAttr)  {

    private var transactionCategory = TransactionCategory.FASTFOOD

    fun setTransactionCategory(category: TransactionCategory) {
        id = ViewCompat.generateViewId()
        setText(category.nameStringRes)
        transactionCategory = category
        setChipIconResource(category.drawableResId)
    }

    fun getTransactionCategory() = transactionCategory
}