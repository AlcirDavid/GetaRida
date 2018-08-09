package com.getaride.android.util

import com.getaride.android.api.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.Executor

/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 * @param <R>
</R> */
class ApiResponseCallAdapter<R>(private val responseType: Type, private val executor: Executor?) :
        CallAdapter<R, MyCall<ApiResponse<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): MyCall<ApiResponse<R>> {
        return MyCallImpl(call, executor)
    }
}

interface MyCallback<T> {
    fun onResult(response: T)
}

interface MyCall<T> {
    fun cancel()
    fun enqueue(callback: MyCallback<T>)
    fun clone(): MyCall<T>

    // Left as an exercise for the reader...
    // TODO MyResponse<T> execute() throws MyHttpException;
}

class MyCallImpl<T>(private val call: Call<T>, private val executor: Executor?) :MyCall<ApiResponse<T>> {
    override fun enqueue(callback: MyCallback<ApiResponse<T>>) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResult(ApiResponse.create(response))
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                callback.onResult(ApiResponse.create(throwable))
            }
        })
    }

    override fun cancel() {
        call.cancel()
    }

    override fun clone(): MyCall<ApiResponse<T>> {
        return  MyCallImpl<T>(call.clone(), executor)
    }
}