package com.getaride.android.util.network

import kotlinx.coroutines.experimental.channels.SubscriptionReceiveChannel

interface NetworkObserver {
    fun observe(): SubscriptionReceiveChannel<Boolean>

    fun onError(message: String, exception: Exception)

    fun tryToUnregisterReceivers()
//    suspend fun waitForConnection(coroutineContext : CoroutineContext): ReceiveChannel<Boolean>

}