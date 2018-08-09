package com.getaride.android.util.network.networkObservers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.PowerManager
import android.util.Log
import com.getaride.android.util.AndroidNetworkDetector
import com.getaride.android.util.network.NetworkObserver
import kotlinx.coroutines.experimental.channels.ArrayBroadcastChannel
import kotlinx.coroutines.experimental.channels.SubscriptionReceiveChannel
import timber.log.Timber


class AndroidNetworkObserver constructor(
        private val context: Context,
        networkRequest :NetworkRequest
) : NetworkObserver {

    companion object {
        const val TAG = "AndroidNetworkDetector"
        const val ERROR_MSG_NETWORK_CALLBACK = "could not unregister network callback"
        const val ERROR_MSG_RECEIVER = "could not unregister broadcastReceiver"
    }

    private val broadcastReceiver :BroadcastReceiver
    private val broadcastChannel :ArrayBroadcastChannel<Boolean>
    private val connectivityManager :ConnectivityManager
    private val networkCallback :ConnectivityManager.NetworkCallback

    init {
        broadcastReceiver = createBroadcastReceiver()
        broadcastChannel = ArrayBroadcastChannel<Boolean>(1)
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        registerReceiver()

        networkCallback = initializeNetworkCallback()

//        val request = NetworkRequest.Builder()
//                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
//                .build()

        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    private fun getConnectivityStatus() :Boolean =
            connectivityManager.activeNetworkInfo?.isConnected == true

    override fun observe() :SubscriptionReceiveChannel<Boolean> {
        Timber.d("TEST INSIDE observe() CALL openSubscription()")
        //        val sub = observe()

        //use a delegate to print something when cancel is called on the subscription
//        val channel: SubscriptionReceiveChannel<Boolean> = object
//            :SubscriptionReceiveChannel<Boolean> by broadcastChannel.openSubscription() {
//            override fun cancel(cause: Throwable?): Boolean {
//                val result = this.cancel(cause)
//                Timber.e("cancelling subscribtion with cause: $cause")
//                Timber.e("cancelled subscribtion isClosedForReceive: ${this.isClosedForReceive}")
//                return result
//            }
//        }
        val channel = broadcastChannel.openSubscription()

        // emit the starting element
        val offer = broadcastChannel.offer(getConnectivityStatus())
        Timber.d("TEST INSIDE observe() offer = $offer")

        return channel
    }

    override fun onError(message: String, exception: Exception) {
        Timber.e("Error: $message\nCaused by: ${exception.cause}")
    }

//    override suspend fun waitForConnection(coroutineContext : CoroutineContext): ReceiveChannel<Boolean> {
////    override suspend fun waitForConnection(coroutineContext : CoroutineContext): Boolean {
////    override suspend fun waitForConnection(): Boolean {
//        Timber.e("TEST INSIDE waitForConnection() coroutine --> $coroutineContext")
//
//        val receiveChannel = observe()
//
//
//        val cancellableJob : Job? = coroutineContext[Job]
//        Timber.e("TEST INSIDE waitForConnection() coroutine[Job] --> ${cancellableJob}")
//        // try to unregister BroadcastReceiver
//        cancellableJob?.invokeOnCompletion {
////            receiveChannel.cancel()
//            Timber.e("TEST INSIDE waitForConnection() try to unregister BroadcastReceiver")
//            tryToUnregisterCallback()
//            tryToUnregisterReceiver()
//        }
//
//
////        val receiveChannel = observe().filter(coroutineContext) { it }
//
//        Timber.e("TEST INSIDE waitForConnection() isEmpty = ${receiveChannel.isEmpty}")
//
//        Timber.e("TEST INSIDE waitForConnection() B4 return")
//
//
////        return status
//        return receiveChannel
//    }

    override fun tryToUnregisterReceivers() {
        Timber.e("TEST try to unregister BroadcastReceiver")
        tryToUnregisterCallback()
        tryToUnregisterReceiver()
    }

    private fun initializeNetworkCallback() : ConnectivityManager.NetworkCallback {
        return object :ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(AndroidNetworkDetector.TAG, "TEST requestNetwork onAvailable()")
                var offer = broadcastChannel.offer(getConnectivityStatus())
                Log.d(AndroidNetworkDetector.TAG, "TEST INSIDE onAvailable() offer = $offer")
            }

            override fun onLost(network: Network) {
                Log.d(AndroidNetworkDetector.TAG, "TEST requestNetwork onLost()")
                val offer = broadcastChannel.offer(getConnectivityStatus())
                Log.d(AndroidNetworkDetector.TAG, "TEST INSIDE onLost() offer = $offer")
            }
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED)
        context.registerReceiver(broadcastReceiver, filter)
    }

    private fun createBroadcastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Timber.e("TEST INSIDE onReceive() of createBroadcastReceiver()")
                broadcastChannel.offer(getConnectivityStatus())
            }
        }
    }

    private fun tryToUnregisterCallback(manager: ConnectivityManager = connectivityManager) {
        try {
            manager.unregisterNetworkCallback(networkCallback)
        } catch (exception: Exception) {
            onError(ERROR_MSG_NETWORK_CALLBACK, exception)
        }
    }

    private fun tryToUnregisterReceiver(context: Context = this.context) {
        try {
            context.unregisterReceiver(broadcastReceiver)
        } catch (exception: Exception) {
            onError(ERROR_MSG_RECEIVER, exception)
        }
    }
}