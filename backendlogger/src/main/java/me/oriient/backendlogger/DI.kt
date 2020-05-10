package me.oriient.backendlogger

import me.oriient.backendlogger.di.DI
import me.oriient.backendlogger.utils.DIProvidable

private const val TAG = "BackendLoggerDI"

internal lateinit var di: DI

internal fun initializeDi() {
    if (!::di.isInitialized) {
        di = Class.forName("me.oriient.backendlogger.di.DIImpl").newInstance() as DI
        logd(TAG, "DI initialized")
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

// Logging

fun logd(tag: String, message: String) {
    di.log.d(tag, message)
}
fun logi(tag: String, message: String) {
    di.log.i(tag, message)
}
fun logw(tag: String, message: String) {
    di.log.w(tag, message)
}
fun loge(tag: String, message: String) {
    di.log.e(tag, message)
}