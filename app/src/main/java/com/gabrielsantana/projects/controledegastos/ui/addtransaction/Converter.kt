package com.gabrielsantana.projects.controledegastos.ui.addtransaction

import androidx.databinding.InverseMethod
import com.gabrielsantana.projects.controledegastos.util.fromCurrency
import com.gabrielsantana.projects.controledegastos.util.toCurrency
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView



@InverseMethod("priceToString")
 fun priceToString(
    view: TextInputEditText,
    value: Double
): String {
    return value.toCurrency()
}

fun stringToPrice(
    view: TextInputEditText,
   oldValue: String,
   value: String
): Double {
    return oldValue.fromCurrency()
}
