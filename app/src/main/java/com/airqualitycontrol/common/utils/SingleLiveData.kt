package com.airqualitycontrol.common.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private var hasBeenChanged = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { t ->
            if (hasBeenChanged.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    override fun setValue(data: T) {
        hasBeenChanged.set(true)

        super.setValue(data)
    }

    override fun postValue(data: T) {
        hasBeenChanged.set(true)

        super.postValue(data)
    }
}
