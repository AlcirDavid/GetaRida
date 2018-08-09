package com.getaride.android.util

import com.getaride.android.api.ApiResponse
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.RendezvousChannel
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 * @param <R>
</R> */
class ChannelCallAdapter<R>(private val responseType: Type) :
        CallAdapter<R, Channel<ApiResponse<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): Channel<ApiResponse<R>> {
        return object : RendezvousChannel<ApiResponse<R>>() {
            private var started = AtomicBoolean(false)

            /**
             * Invoked when receiver is successfully enqueued to the queue of waiting receivers.
             * @suppress **This is unstable API and it is subject to change.**
             */
            override fun onReceiveEnqueued() {
                super.onReceiveEnqueued()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            offer(ApiResponse.create(response))
                        }

                        override fun onFailure(call: Call<R>, throwable: Throwable) {
                            offer(ApiResponse.create(throwable))
                        }
                    })
                }
            }
        }
    }
}