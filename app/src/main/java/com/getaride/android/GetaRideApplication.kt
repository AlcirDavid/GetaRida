package com.getaride.android

import android.app.Application
import com.getaride.android.di.getaRideAppModules
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree


class GetaRideApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin(this, getaRideAppModules)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(Timber.asTree())
        }
    }
}