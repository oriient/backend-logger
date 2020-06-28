package me.oriient.backendlogger

import android.util.Log
import me.oriient.backendlogger.di.DI
import me.oriient.backendlogger.utils.DIProvidable

private const val TAG = "BackendLoggerDI"

internal lateinit var di: DI

internal fun initializeDi() {
    if (!::di.isInitialized) {
        di = Class.forName("me.oriient.backendlogger.di.DIImpl").newInstance() as DI
        Log.d(TAG, "DI initialized")
    }
}

internal inline fun <reified T: DIProvidable> get(): T {
    initializeDi()
    return di.get(T::class)
}

internal inline fun <reified T: DIProvidable> inject(): Lazy<T> {
    initializeDi()
    return di.inject(T::class)
}