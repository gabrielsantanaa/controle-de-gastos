package com.gabrielsantana.projects.controledegastos.domain.model

import androidx.annotation.StringRes
import com.gabrielsantana.projects.controledegastos.R

enum class TransactionCategory(@StringRes val nameStringRes: Int, val drawableResId: Int) {
    FOOD(R.string.category_food, R.drawable.selector_round_food_bank),
    FASTFOOD(R.string.category_fastfood, R.drawable.selector_round_fastfood),
    TRANSPORT(R.string.category_transport, R.drawable.selector_round_bus),
    SALARY(R.string.category_salary, R.drawable.ic_round_money_24),
    SEVERAL(R.string.category_several, R.drawable.ic_round_several_24)
}