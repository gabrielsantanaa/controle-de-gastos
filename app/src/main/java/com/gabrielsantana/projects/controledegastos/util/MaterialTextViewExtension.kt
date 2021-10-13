package com.gabrielsantana.projects.controledegastos.util

import android.animation.Animator
import androidx.core.content.ContextCompat
import com.gabrielsantana.projects.controledegastos.domain.model.Transaction
import com.google.android.material.textview.MaterialTextView

fun MaterialTextView.setTextColorByTransactionType(transaction: Transaction) {
    setTextColor(ContextCompat.getColor(this.context, transaction.getColorRes()))
}
fun MaterialTextView.setTextAnimation(text: String, duration: Long = 300) {
    fadOutAnimation(duration) {
        this.text = text
        fadInAnimation(duration) {
        }
    }
}
fun MaterialTextView.setTextWithFadeAnimation(text: String) {
    animate()
        .setDuration(150)
        .alpha(0F)
        .withEndAction {
            setText(text)
            animate()
                .setDuration(150)
                .alpha(1F)
                .start()
        }
        .start()
}