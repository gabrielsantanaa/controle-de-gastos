package com.gabrielsantana.projects.controledegastos.util

import android.view.View

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