package com.gabrielsantana.projects.controledegastos.util

import androidx.core.content.ContextCompat
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.google.android.material.imageview.ShapeableImageView

fun ShapeableImageView.setTransactionCategoryIcon(transaction: Transaction) {
    setImageResource(transaction.transactionCategory.drawableResId)
    setColorFilter(
        ContextCompat.getColor(this.context, transaction.getColorRes()),
        android.graphics.PorterDuff.Mode.MULTIPLY
    )
}

fun ShapeableImageView.setTransactionTypeIcon(transaction: Transaction) =
    setImageResource(transaction.getTypeIcon())
