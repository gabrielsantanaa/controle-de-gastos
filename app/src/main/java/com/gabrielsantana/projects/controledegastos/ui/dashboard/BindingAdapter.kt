package com.gabrielsantana.projects.controledegastos.ui.dashboard

import android.animation.Animator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.util.setTextWithFadeAnimation
import com.gabrielsantana.projects.controledegastos.util.toCurrency
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView


@BindingAdapter("app:iconType")
fun setIconType(view: MaterialButton,value: Boolean) {
    view.icon = if (value) {
        ContextCompat.getDrawable(view.context, R.drawable.ic_outline_open_eye_24)
    } else {
        ContextCompat.getDrawable(view.context, R.drawable.ic_round_closed_eye)
    }
}

@BindingAdapter("app:fabVisibility")
fun setFabVisibility(view: FloatingActionButton,value: Boolean) {
    if (value) {
        view.show()
    } else {
       view.hide()
    }
}

/////////////////////////////////

@BindingAdapter(value = ["app:amountVisibility", "app:amountValue"], requireAll = true)
fun MaterialTextView.setAmountVisibility(visible: Boolean, amount: Double) {
    Log.d("amountVisibility", "setAmountVisibility: ")
    if(visible) {
        setTextWithFadeAnimation(amount.toCurrency())
    } else {
        setTextWithFadeAnimation("*********")
    }
}



