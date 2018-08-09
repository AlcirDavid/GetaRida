package com.getaride.android.coroutines

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.experimental.Job
import timber.log.Timber

class AndroidJob(lifecycle: Lifecycle) : Job by Job(), LifecycleObserver {

    init {
        Timber.e("init AndroidJob")
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun destroy() = cancel()
    fun destroy() {
        Timber.e("CALL cancel()")
        cancel()
    }
}


interface JobHolder {
    val job: Job
}