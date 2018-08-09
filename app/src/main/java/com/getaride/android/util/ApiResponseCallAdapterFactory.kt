package com.getaride.android.util

import com.getaride.android.api.ApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.Executor

class ApiResponseCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): CallAdapter<*, *>? {
//        if (CallAdapter.Factory.getRawType(returnType) != ApiResponse::class.java) {
//            return null
//        }
//
//        val delegate :CallAdapter<*, *> = retrofit.nextCallAdapter(this, returnType, annotations)
//
//        return object :CallAdapter<Any, ApiResponse<*>> {
//            override fun adapt(call: Call<Any>?): ApiResponse<*> {
//                val api :ApiResponse<*> = delegate.adapt(call) as ApiResponse<*>
////                val api : Any? = delegate.adapt(call)
//                return api
//            }
//
//            override fun responseType(): Type {
//                return delegate.responseType()
//            }
//        }

        if (CallAdapter.Factory.getRawType(returnType) != MyCall::class.java) {
            return null
        }
        val observableType = CallAdapter.Factory.getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = CallAdapter.Factory.getRawType(observableType)
        if (rawObservableType != ApiResponse::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }
        val bodyType = CallAdapter.Factory.getParameterUpperBound(0, observableType)
        val executor : Executor? = retrofit.callbackExecutor()

        return ApiResponseCallAdapter<Any>(bodyType, executor)
    }
}