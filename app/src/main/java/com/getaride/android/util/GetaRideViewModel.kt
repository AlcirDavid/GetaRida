package com.getaride.android.util

import androidx.lifecycle.ViewModel
import com.getaride.android.coroutines.CoroutinesUtils.Companion.tryCatch
import com.getaride.android.coroutines.CoroutinesUtils.Companion.tryCatchFinally
import com.getaride.android.coroutines.CoroutinesUtils.Companion.tryFinally
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Simple ViewModel which exposes a [CompositeDisposable] and
 * [Job] which are automatically cleared/stopped when the ViewModel is cleared.
 */
open class GetaRideViewModel() : ViewModel() {
    val viewModelJob = Job()
//    val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
//        disposables.clear()
        Timber.e("TEST CALL viewModelJob.cancel()")
        viewModelJob.cancel()
    }

    fun launchWithParent(
            context: CoroutineContext = DefaultDispatcher,
            block: suspend CoroutineScope.() -> Unit) : Job =
            launch(context = context, parent = viewModelJob, block = block)


    @Synchronized
    fun launchWithParentTryCatch(
            context: CoroutineContext = DefaultDispatcher,
            tryBlock: suspend CoroutineScope.() -> Unit,
            catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
            handleCancellationExceptionManually: Boolean) : Job {
        return launchWithParent(context) {
            tryCatch(tryBlock, catchBlock, handleCancellationExceptionManually)
        }
    }

    @Synchronized
     fun launchWithParentTryCatchFinally(
            context: CoroutineContext = DefaultDispatcher,
            tryBlock: suspend CoroutineScope.() -> Unit,
            catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
            finallyBlock: suspend CoroutineScope.() -> Unit,
            handleCancellationExceptionManually: Boolean) : Job {
        return launchWithParent(context) {
            tryCatchFinally(tryBlock, catchBlock, finallyBlock,
                    handleCancellationExceptionManually)
        }
    }

    @Synchronized
     fun launchWithParentTryFinally(
            context: CoroutineContext = DefaultDispatcher,
            tryBlock: suspend CoroutineScope.() -> Unit,
            finallyBlock: suspend CoroutineScope.() -> Unit,
            suppressCancellationException: Boolean) : Job {
        return launchWithParent(context) {
            tryFinally(tryBlock, finallyBlock, suppressCancellationException)
        }
    }

}