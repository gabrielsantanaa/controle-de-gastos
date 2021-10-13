package com.gabrielsantana.projects.controledegastos.util

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.view.Window
import androidx.annotation.AttrRes


fun Window.setStatusBarColorSmoothly(@AttrRes color: Int) {

    val colorFrom = statusBarColor
    val colorTo =  context.themeColor(color)
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
    colorAnimation.duration = 450 // milliseconds

    colorAnimation.addUpdateListener { animator -> statusBarColor = animator.animatedValue as Int }
    colorAnimation.start()
}

