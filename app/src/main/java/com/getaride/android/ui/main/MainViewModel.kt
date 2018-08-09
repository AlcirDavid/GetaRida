package com.getaride.android.ui.main

//import android.arch.lifecycle.ViewModel
import com.getaride.android.AppCoroutineContexts
import com.getaride.android.api.ApiResponse
import com.getaride.android.repository.UserRepository
import com.getaride.android.util.GetaRideViewModel
import com.getaride.android.util.network.NetworkObserver
import com.getaride.android.vo.Authorization
import com.getaride.android.vo.Resource
import com.getaride.android.vo.User
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SubscriptionReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import timber.log.Timber

class MainViewModel(private val userRepository :UserRepository,
                    private val appCoroutineContexts :AppCoroutineContexts,
                    private val networkObserver :NetworkObserver) :GetaRideViewModel() {
    // TODO: Implement the ViewModel
    private val networkSubscriptionChannel :SubscriptionReceiveChannel<Boolean>

    init {
        networkSubscriptionChannel = networkObserver.observe()
        Timber.e("TEST CALL receiveChannel --> $networkSubscriptionChannel")
        receiveConnectivityStatus()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.e("TEST CALL networkObserver.tryToUnregisterReceivers()")
        networkObserver.tryToUnregisterReceivers()

        Timber.e("TEST CALL networkSubscriptionChannel.close()")
        networkSubscriptionChannel.close()  //unsubscribe this channel
    }

    fun authenticateUser(login: String, password: String)
            = userRepository.authenticateUser(login, password)

    fun authenticate(accessToken: String) : Channel<ApiResponse<Authorization>> {
        return  userRepository.authenticate(accessToken)
    }

    fun authenticate() : Channel<ApiResponse<Authorization>> {
        val scopes : Array<String> = arrayOf("public_repo")
        return  userRepository.authenticate(scopes, "basic app")
    }

    fun loadUser() :SubscriptionReceiveChannel<Resource<User>>? {
        Timber.e("TEST INSIDE loadUser()")

        val dataSubscriptionChannel = userRepository
                .loadUser("AlcirDavid", viewModelJob)
//        val receiveChannel: SubscriptionReceiveChannel<Boolean> = networkObserver.observe()
//        Timber.e("TEST CALL receiveChannel --> $receiveChannel")

        return dataSubscriptionChannel
    }

    private fun receiveConnectivityStatus() :SubscriptionReceiveChannel<Resource<User>>? {
        var dataSubscriptionChannel :SubscriptionReceiveChannel<Resource<User>>? = null
        launchWithParentTryCatch(
                appCoroutineContexts.mainThread(),
                {
                    networkSubscriptionChannel.consumeEach { isConnected ->
                        Timber.e("TEST INSIDE receiveConnectivityStatus  consumeEach")
                        Timber.e("TEST onReceive isConnected = $isConnected")

                        if (isConnected) {
//                        val dataSubscriptionChannel = withContext(appCoroutineContexts.networkIO()) {
//                            Timber.e("TEST CALL userRepository.loadUser()")
//                            userRepository
//                                    .loadUser("AlcirDavid", viewModelJob)
////                            Timber.e("TEST INSIDE consumeEach isClosedForReceive = " +
////                                    "${dataSubscriptionChannel.isClosedForReceive}")
////                            Timber.e("TEST INSIDE consumeEach dataSubscriptionChannel" +
////                                    ".isEmpty = ${dataSubscriptionChannel.isEmpty}")
//                        }
                            dataSubscriptionChannel = loadUser()

                            Timber.e("TEST INSIDE consumeEach isClosedForReceive = " +
                                    "${dataSubscriptionChannel?.isClosedForReceive}")
                            Timber.e("TEST INSIDE consumeEach dataSubscriptionChannel" +
                                    ".isEmpty = ${dataSubscriptionChannel?.isEmpty}")
                            Timber.e("TEST INSIDE consumeEach dataSubscriptionChannel = ${dataSubscriptionChannel}")
                        }
                        Timber.e("TEST INSIDE consumeEach isClosedForReceive = ${networkSubscriptionChannel.isClosedForReceive}")
                    }
                }, {}, false)
        return  dataSubscriptionChannel
    }

}
