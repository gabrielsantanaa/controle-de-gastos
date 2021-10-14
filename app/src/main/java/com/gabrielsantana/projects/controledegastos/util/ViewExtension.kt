package com.gabrielsantana.projects.controledegastos.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.fadOutAnimation(duration: Long = 300, visibility: Int = View.INVISIBLE, completion: () -> Unit) {
    animate()
        .alpha(0f)
        .setDuration(duration)
        .withEndAction {
            this.visibility = visibility
            completion()
        }
}

fun View.fadInAnimation(duration: Long = 300, completion: () -> Unit) {
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(duration)
        .withEndAction {
            completion()
        }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}