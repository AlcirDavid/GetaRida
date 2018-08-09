package com.getaride.android

import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.asCoroutineDispatcher
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.util.concurrent.Executors
import kotlin.coroutines.experimental.CoroutineContext

open class AppCoroutineContexts(
        private val diskIO: CoroutineDispatcher,
        private val networkIO: CoroutineDispatcher,
        private val mainThread: CoroutineDispatcher
) {

    constructor() : this(
            newSingleThreadContext("diskIO"),
            Executors.newFixedThreadPool(3).asCoroutineDispatcher(),
            UI
    )

    fun diskIO(): CoroutineDispatcher {
        return diskIO
    }

    fun networkIO(): CoroutineDispatcher {
        return networkIO
    }

    fun mainThread(): CoroutineDispatcher {
        return mainThread
    }

}