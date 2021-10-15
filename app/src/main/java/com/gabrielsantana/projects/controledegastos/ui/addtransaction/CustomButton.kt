package com.gabrielsantana.projects.controledegastos.ui.addtransaction

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.gabrielsantana.projects.controledegastos.domain.model.TransactionType
import com.google.android.material.button.MaterialButton

@SuppressLint("AppCompatCustomView")
class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialButton(context, attrs, defStyleAttr) {

    private var transactionType = TransactionType.INCOME

    fun setTransactionType(transactionType: TransactionType) {
        this.transactionType = transactionType
        setText(transactionType.nameStringRes)
    }

    fun getTransactionType() = transactionType

}