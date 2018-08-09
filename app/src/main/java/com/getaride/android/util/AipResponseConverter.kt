package com.getaride.android.util

import com.google.gson.reflect.TypeToken
import com.getaride.android.api.ApiResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import java.lang.reflect.Type

class AipResponseConverter : Converter.Factory() {

    override fun responseBodyConverter(
        type :Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ) :Converter<ResponseBody,*>? {

        Timber.e("TEST getRawType(type) = ${getRawType(type)}")
        if (getRawType(type) != ApiResponse::class.java) {
            return null
        }
//        val apiResponsetype :Type = TypeToken.get(ApiResponse::class.java).type
        val apiResponsetype :Type = TypeToken.get(type).type

        val delegate :Converter<ResponseBody, ApiResponse<*>> =
                retrofit.nextResponseBodyConverter(this, apiResponsetype, annotations)

        return Converter<ResponseBody, ApiResponse<*>> { value ->
            val  apiResponse : ApiResponse<*> = delegate.convert(value)
            apiResponse
        }
    }
}