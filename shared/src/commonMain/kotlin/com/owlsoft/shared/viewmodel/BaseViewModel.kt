package com.owlsoft.shared.viewmodel

import kotlinx.coroutines.CoroutineScope

expect open class BaseViewModel() {
    val scope: CoroutineScope

    protected open fun onCleared()
}
