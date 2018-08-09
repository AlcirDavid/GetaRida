@file:Suppress("INACCESSIBLE_TYPE")

package com.getaride.android.di

import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.google.gson.Gson
import com.getaride.android.api.GithubService
import com.getaride.android.util.ChannelCallAdapterFactory
import com.getaride.android.util.network.NetworkObserver
import com.getaride.android.util.network.networkObservers.AndroidNetworkObserver
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule : Module = module {
    // provide web components
    bean { createOkHttpClient() }

    // provide gson component
    bean { Gson() }
    // Fill property

    bean { provideRestAdapterBuilder(get(), get()) }
    // Fill property

    bean { createWebService<GithubService>(get()) }

    // provide NetworkRequest
    bean { provideNetworkRequest() }

    // provide NetworkObserver
    single { AndroidNetworkObserver(androidContext(), get()) as NetworkObserver}

    // Fill property
//    bean { createWebService<WeatherDatasource>(get(), getProperty(SERVER_URL)) }
}


fun createOkHttpClient(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder()
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor).build()
}

 fun provideRestAdapterBuilder(client: OkHttpClient, gson: Gson): Retrofit.Builder {
    return Retrofit.Builder()
            .client(client)
//            .addConverterFactory(AipResponseConverter())
//            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
//            .addCallAdapterFactory(AppCallAdapterFactory())
            .addCallAdapterFactory(ChannelCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
//            .addCallAdapterFactory(LiveDataCallAdapterFactory())
}


inline fun <reified T> createWebService(builder: Retrofit.Builder): T {
    return builder
//            .baseUrl(url)
            .baseUrl("https://api.github.com/")
            .build().create(T::class.java)
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    return retrofit.create(T::class.java)
}

fun provideNetworkRequest() = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
        .build()