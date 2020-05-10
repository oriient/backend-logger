package me.oriient.backendlogger.services.android.log

import me.oriient.backendlogger.services.os.log.Log
import io.ktor.client.features.logging.Logger


@Suppress("unused")
internal class LogImpl: Log, Logger {
    override fun d(tag: String, message: String) {
        android.util.Log.d(tag, message)
    }

    override fun i(tag: String, message: String) {
        android.util.Log.i(tag, message)
    }

    override fun w(tag: String, message: String) {
        android.util.Log.w(tag, message)
    }

    override fun e(tag: String, message: String) {
        android.util.Log.e(tag, message)
    }

    override fun log(message: String) {
        android.util.Log.d("REST", message)
    }
}