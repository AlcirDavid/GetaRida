package com.getaride.android.util

import io.reactivex.Observable
import io.reactivex.Single

interface NetworkDetector {

    fun observe(): Observable<Boolean>

    fun waitForConnection(): Single<Boolean>
}
