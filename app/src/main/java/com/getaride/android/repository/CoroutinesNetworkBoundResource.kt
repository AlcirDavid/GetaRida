package com.getaride.android.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.getaride.android.AppCoroutineContexts
import com.getaride.android.api.ApiEmptyResponse
import com.getaride.android.api.ApiErrorResponse
import com.getaride.android.api.ApiResponse
import com.getaride.android.api.ApiSuccessResponse
import com.getaride.android.vo.Resource
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.experimental.channels.SubscriptionReceiveChannel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import timber.log.Timber

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */
abstract class CoroutinesNetworkBoundResource<ResultType, RequestType>
@MainThread constructor(private val appExecutors: AppCoroutineContexts, private val parentJob: Job) {

//    private val result = MediatorLiveData<Resource<ResultType>>()
//    private var resultRight : Resource<ResultType>

    //    private val apiResponseChannel : Channel<ApiResponse<RequestType>>
//    private val dbChannel : Channel<Resource<ResultType>>
    private val resultChannel: ConflatedBroadcastChannel<Resource<ResultType>>

    init {
//        apiResponseChannel = Channel<ApiResponse<RequestType>>()
//        dbChannel = Channel()
//        resultRight = Resource.loading(null)
        resultChannel = ConflatedBroadcastChannel(Resource.loading(null))

//        launchWithParentTryCatch(appExecutors.mainThread(), parentJob,
        launch(appExecutors.mainThread(), parent = parentJob) {
            //            dbChannel.consumeEach { data ->
//                resultRight = data
//            }

            withContext(appExecutors.diskIO()) {
                //                @Suppress("LeakingThis")
                val dbSource = loadFromDb()
                Timber.e("TEST INSIDE CoroutinesNetworkBoundResource init{} dbSource = $dbSource")

                withContext(appExecutors.mainThread()) {
                    @Suppress("IMPLICIT_CAST_TO_ANY")
                    if (shouldFetch(dbSource)) {
                        fetchFromNetwork(dbSource)
                    } else {
                        resultChannel.offer(Resource.success(dbSource))
//                      dbChannel.offer(Resource.success(dbSource))
                    }
                }
            }

        }
//                }
        //,{}, false)
    }

//    fun produceNumbers(context: CoroutineContext, side: SendChannel<Int>) = produce<Int>(context) {
//        for (num in 1..10) { // produce 10 numbers from 1 to 10
//            delay(100) // every 100 ms
//            select<Unit> {
//                if (this@produce.isActive)
//                    onSend(num) {} // Send to the primary channel
//                else
//                    side.onSend(num) {} // or to the side channel
//            }
//        }
//    }


    private suspend fun fetchFromNetwork(dbSource: ResultType) = withContext(
            appExecutors.networkIO()) {
        val apiResponse = createCall().receive()
        Timber.e("TEST fetchFromNetwork apiResponse = $apiResponse")

//        apiResponseChannel.offer(apiResponse)
//        (apiResponseChannel as ReceiveChannel<ApiResponse<RequestType>>).consumeEach<ApiResponse<RequestType>> {
//        apiResponseChannel.consumeEach<ApiResponse<RequestType>> { response ->
//        val result = apiResponse.execute().body()
//        if (result != null) {
//            val response = result as ApiResponse<RequestType>
//            when (response) {
        when (apiResponse) {
            is ApiSuccessResponse -> {
                withContext(appExecutors.diskIO()) {
                    saveCallResult(processResponse(apiResponse))
//                        saveCallResult(processResponse(response))
                    val newData = loadFromDb();
                    withContext(appExecutors.mainThread()) {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
//                            dbChannel.offer(Resource.success(newData))
                        resultChannel.offer(Resource.success(newData))
                    }
                }
            }
            is ApiEmptyResponse -> {
                withContext(appExecutors.diskIO()) {
                    val newData = loadFromDb();
                    withContext(appExecutors.mainThread()) {
                        // reload from disk whatever we had
//                            dbChannel.offer(Resource.success(newData))
                        resultChannel.offer(Resource.success(newData))
                    }
                }
            }
            is ApiErrorResponse -> {
                onFetchFailed()
                withContext(appExecutors.mainThread()) {
                    // resultChannel.offer(Resource.error(response.errorMessage, dbSource))
                    resultChannel.offer(Resource.error(apiResponse.errorMessage, dbSource))
                }
            }
            else -> {
            }
        }
//        }
//        }
    }

    protected open fun onFetchFailed() {}

    //    fun asChannel() = resultRight as Channel<Resource<ResultType>>
    fun asChannel(): SubscriptionReceiveChannel<Resource<ResultType>> = resultChannel.openSubscription()

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected abstract suspend fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract suspend fun loadFromDb(): ResultType

    @MainThread
    protected abstract suspend fun createCall(): Channel<ApiResponse<RequestType>>
//    protected abstract suspend fun createCall(): Call<ApiResponse<RequestType>>
//    protected abstract suspend fun createCall(): ApiResponse<RequestType>
}
