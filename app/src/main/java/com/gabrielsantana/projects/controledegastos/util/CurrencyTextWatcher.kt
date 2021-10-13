package com.gabrielsantana.projects.controledegastos.util

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText

class CurrencyTextWatcher(
    private val editText: EditText,
    private val onCurrencyUpdate: (Double) -> Unit
) : TextWatcher {

    private var currentValue: String = ""

    private var updating = false
    private var count = 0

    override fun afterTextChanged(s: Editable?) {
        if (updating) return
        Log.d("count", "afterTextChanged: $count")
        count++

        val newValue = s.toString()
        if (currentValue != newValue) {

            updating = true

            val doubleValue = newValue.fromCurrency()
            onCurrencyUpdate(doubleValue)
            val formatted = getFormattedValue(doubleValue)

            updateValue(formatted)
        }
    }

    private fun getFormattedValue(value: Double): String =
        if (value == 0.0) ""
        else value.toCurrency()

    private fun updateValue(formatted: String) {

        editText.setText(formatted)
        editText.setSelection(formatted.length)

        updating = false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // no used
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // not used
    }
}