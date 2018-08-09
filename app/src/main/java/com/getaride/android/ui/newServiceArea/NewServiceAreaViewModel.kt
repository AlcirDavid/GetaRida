package com.getaride.android.ui.newServiceArea

//import android.arch.lifecycle.ViewModel
import com.getaride.android.AppCoroutineContexts
import com.getaride.android.repository.UserRepository
import com.getaride.android.util.GetaRideViewModel
import com.getaride.android.util.network.NetworkObserver
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.withContext
import timber.log.Timber

class NewServiceAreaViewModel(private val userRepository : UserRepository,
                              private val appCoroutineContexts : AppCoroutineContexts,
                              private val networkObserver: NetworkObserver) : GetaRideViewModel() {
    // TODO: Implement the ViewModel

    fun loadUser() {
        Timber.e("TEST INSIDE loadUser()")

        val receiveChannel : ReceiveChannel<Boolean> = networkObserver.observe()
        Timber.e("TEST CALL receiveChannel --> $receiveChannel")

        receiveConnectivityStatus(receiveChannel)
    }

    private fun receiveConnectivityStatus(channel: ReceiveChannel<Boolean>) : Job = launchWithParentTryCatch(
            appCoroutineContexts.mainThread(),
            {
                channel.consumeEach { isConnected ->
                    Timber.e("TEST INSIDE receiveConnectivityStatus  consumeEach")
                    Timber.e("TEST onReceive isConnected = $isConnected")

                    if (isConnected) {
                        withContext(appCoroutineContexts.networkIO()) {
                            Timber.e("TEST CALL userRepository.loadUser()")
//                            makeTrackTrendingNetworkCall()
                            val dataSubscriptionChannel = userRepository
                                    .loadUser("AlcirDavid", viewModelJob)
                            Timber.e("TEST INSIDE consumeEach isClosedForReceive = " +
                                    "${dataSubscriptionChannel.isClosedForReceive}")
                            Timber.e("TEST INSIDE consumeEach dataSubscriptionChannel" +
                                    ".isEmpty = ${dataSubscriptionChannel.isEmpty}")
                        }
                    }
                    Timber.e("TEST INSIDE consumeEach isClosedForReceive = ${channel.isClosedForReceive}")
                }
            }, {}, false)
}

