package com.airqualitycontrol.common.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.airqualitycontrol.common.utils.SingleLiveEvent

abstract class BaseViewModel: ViewModel() {

    val isLoading: LiveData<Boolean>
        get() = mutableIsLoading

    private val mutableIsLoading = SingleLiveEvent<Boolean>()

    private var callFlow: MutableList<() -> Unit> = mutableListOf()

    fun tryAgain() {
        executeFlow()
    }

    protected fun formCallFlow(vararg call: () -> Unit) {
        callFlow = call.toMutableList()
    }

    protected fun addToCallFlow(call: () -> Unit) {
        callFlow.add(call)
    }

    protected fun onLoading(isLoading: Boolean) = mutableIsLoading.postValue(isLoading)

    private fun executeFlow() {
        for (call in callFlow) call()
    }
}
