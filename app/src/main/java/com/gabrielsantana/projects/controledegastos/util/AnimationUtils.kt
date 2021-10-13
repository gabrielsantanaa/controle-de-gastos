package com.gabrielsantana.projects.controledegastos.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewAnimationUtils
import androidx.transition.Transition
import androidx.transition.TransitionSet
import kotlin.math.hypot

fun circularAnimation(
    view: View,
    isHide: Boolean,
    duration: Long,
    onAnimationEnd: () -> Unit
) {
    view.visibility = View.VISIBLE

    // get the center for the clipping circle
    val cx = view.width / 2
    val cy = view.height / 2

    // get the initial radius for the clipping circle
    val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

    // create the animation (the final radius is zero)

    val anim =
        if (isHide) {
            ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0f)
        } else {
            ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, initialRadius)
        }
    anim.duration = duration
    // make the view invisible when the animation is done
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            view.visibility = if(isHide) View.GONE else View.VISIBLE
            onAnimationEnd()
        }
    })
    // start the animation
    anim.start()
}

fun TransitionSet.addTransitionEndListener(end: () -> Unit) {
    addListener(object : Transition.TransitionListener {
        override fun onTransitionStart(transition: Transition) {

        }

        override fun onTransitionEnd(transition: Transition) {
            end()
        }

        override fun onTransitionCancel(transition: Transition) {

        }

        override fun onTransitionPause(transition: Transition) {

        }

        override fun onTransitionResume(transition: Transition) {

        }

    })
}
