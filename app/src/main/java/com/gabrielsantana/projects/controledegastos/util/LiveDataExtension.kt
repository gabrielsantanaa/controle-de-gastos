package com.gabrielsantana.projects.controledegastos.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> {
    return this
}

fun <T> MutableLiveData<T>.notifyObservers() {
    value = value

}