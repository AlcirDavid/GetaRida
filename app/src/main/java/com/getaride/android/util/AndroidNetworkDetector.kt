package com.getaride.android.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.util.Log
import com.cantrowitz.rxbroadcast.RxBroadcast
import io.reactivex.Observable
import io.reactivex.Single


class AndroidNetworkDetector constructor(
        private val context: Context
) : NetworkDetector {

    companion object {
        const val TAG = "AndroidNetworkDetector"
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    }

//    private val connectivityManager = context.getSystemService<ConnectivityManager>()
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    private val connectivityNetworkCallBack = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d(TAG, "requestNetwork onAvailable()")
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            Log.d(TAG, "requestNetwork onCapabilitiesChanged()")
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            Log.d(TAG, "requestNetwork onLinkPropertiesChanged()")
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            Log.d(TAG, "requestNetwork onLosing()")
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "requestNetwork onLost()")
        }
    }

    private fun getConnectivityStatus(): Boolean {
//        return connectivityManager?.activeNetworkInfo?.isConnected == true
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }

    override fun observe(): Observable<Boolean> {
        val request = NetworkRequest.Builder()
        request.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//        connectivityManager?.requestNetwork(request.build(), connectivityNetworkCallBack)
        connectivityManager.requestNetwork(request.build(), connectivityNetworkCallBack)

        return RxBroadcast.fromBroadcast(context, intentFilter)
                .startWith(Intent())
                .map { getConnectivityStatus() }
                .distinctUntilChanged()
    }

    override fun waitForConnection(): Single<Boolean> {
        return observe()
                .filter { it }
                .firstOrError()
    }
}