package com.getaride.android.util.network.networkObservers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.NetworkRequest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.getaride.android.AppCoroutineContexts
import com.getaride.android.di.testAppModules
import kotlinx.coroutines.experimental.channels.first
import kotlinx.coroutines.experimental.launch
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class AndroidNetworkObserverTest : KoinTest {

    lateinit var networkObserver: AndroidNetworkObserver
    val appCoroutineContexts: AppCoroutineContexts by inject()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock lateinit var appContext: Context
    @Mock lateinit var connectivityManager :ConnectivityManager
    @Mock lateinit var networkRequest :NetworkRequest
    @Mock lateinit var networkInfo : NetworkInfo

    //    @Mock
    //    lateinit var networkRequestBuilder :NetworkRequest.Builder

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        startKoin(testAppModules)
        mockMethods()
        networkObserver = AndroidNetworkObserver(appContext, networkRequest)
    }

    @After
    fun tearDown() {
        closeKoin()
    }

    @Test
    fun `observe available connection`() {
        Mockito.`when`(connectivityManager.activeNetworkInfo?.isConnected)
                .thenReturn(true)

        launch(appCoroutineContexts.diskIO()) {
            val status = networkObserver.observe().first()
            println("\nNETWORK STATUS = $status\n")
            assertTrue(status)
        }
    }

    @Test
    fun `observe no connection`() {
        Mockito.`when`(connectivityManager.activeNetworkInfo?.isConnected)
                .thenReturn(false)

        launch(appCoroutineContexts.diskIO()) {
            val status = networkObserver.observe().first()
            println("\nNETWORK STATUS = $status\n")
            assertFalse(status)
        }
    }

    @Test
    fun `error with exception`() {
        val exception = Mockito.mock(Exception::class.java)
        networkObserver.onError("ERROR", exception)
        // ensure Exception.cause is called exactly once
        verify(exception).cause
        // ensure Exception.message is never called
        verify(exception, never()).message
    }

    private fun mockMethods() {
        Mockito.`when`(appContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(connectivityManager)

        Mockito.`when`(connectivityManager.activeNetworkInfo)
                .thenReturn(networkInfo)

//        Mockito.`when`(networkRequestBuilder
//                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
//                .thenReturn(networkRequestBuilder)
//
//        Mockito.`when`(networkRequestBuilder
//                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED))
//                .thenReturn(networkRequestBuilder)
//
//        Mockito.`when`(networkRequestBuilder.build())
//                .thenReturn(networkRequest)
    }

//    @Test
//    fun `say hello with mock`() {
//        declareMock<HelloRepository>()
//        // retrieve the HelloRepository mock
//        val mock = get<HelloRepository>()
//        // retrieve actual presenter (injected with mock)
//        val presenter = get<MySimplePresenter>()
//        presenter.sayHello()
//
//        verify(mock, times(1)).giveHello()
//    }
}