package com.gabrielsantana.projects.controledegastos.domain.model

import androidx.annotation.StringRes
import com.gabrielsantana.projects.controledegastos.R

enum class TransactionType(@StringRes val nameStringRes: Int) {
    INCOME(R.string.type_income),
    EXPENSE(R.string.type_expense)
}