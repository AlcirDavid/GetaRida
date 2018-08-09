package com.getaride.android.ui.movies

import com.getaride.android.AppCoroutineContexts
import com.getaride.android.util.GetaRideViewModel
import com.getaride.android.util.network.NetworkObserver
import com.uwetrottmann.trakt5.TraktV2
import com.uwetrottmann.trakt5.entities.TrendingShow
import com.uwetrottmann.trakt5.enums.Extended
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.withContext
import timber.log.Timber


class MoviesViewModel(val appCoroutineContexts : AppCoroutineContexts,
                      val networkObserver: NetworkObserver) : GetaRideViewModel() {
    // TODO: Implement the ViewModel

    fun getMovies() {
        Timber.e("TEST CALL launchWithParentTryCatch")

        val receiveChannel : ReceiveChannel<Boolean> = networkObserver.observe()
        Timber.e("TEST CALL receiveChannel --> $receiveChannel")

        receiveConnectivityStatus(receiveChannel)
    }

    private fun receiveConnectivityStatus(channel: ReceiveChannel<Boolean>) = launchWithParentTryCatch(
            appCoroutineContexts.mainThread(),
            {
                channel.consumeEach { isConnected ->
                    Timber.e("TEST INSIDE receiveConnectivityStatus  consumeEach")
                    Timber.e("TEST onReceive isConnected = $isConnected")

                    if (isConnected) {
                        withContext(appCoroutineContexts.networkIO()) {
                            Timber.e("TEST CALL makeTrackTrendingNetworkCall")
                            makeTrackTrendingNetworkCall()
                        }
                    }
                    Timber.e("TEST INSIDE consumeEach isClosedForReceive = ${channel.isClosedForReceive}")
                }
            }, {}, false)


    private fun makeTrackTrendingNetworkCall() {
//        val trakt = TraktV2("api_key")
        val trakt = TraktV2("063f5a8fd74f6404dca2e8cd29ce0cef46981b1b78c79e152d02008ca9c492cf")
        val traktShows = trakt.shows()
        try {
            // Get trending shows
            val response = traktShows.trending(1, null, Extended.FULL).execute()
            if (response.isSuccessful) {
                val shows: List<TrendingShow>? = response.body()
                Timber.e("TEST response.body(): ${response.body()}")

                for (trending in shows!!.iterator()) {
                    println("Title: " + trending.show.title)
                }
            } else {
                Timber.e("TEST This is an error response.body(): ${response.body()}")
                Timber.e("TEST This is an error response.code(): ${response.code()}")
                Timber.e("TEST This is an error response.message(): ${response.message()}")

                if (response.code() == 401) {
                    // authorization required, supply a valid OAuth access token
                } else {
                    // the request failed for some other reason
                }
            }
        } catch (e: Exception) {
            Timber.e("TEST This is an exception: ${e.message}")
            // see execute() javadoc
        }

    }
}
