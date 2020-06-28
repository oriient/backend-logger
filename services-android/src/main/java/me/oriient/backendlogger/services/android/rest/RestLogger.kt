package me.oriient.backendlogger.services.android.rest

import android.util.Log
import io.ktor.client.features.logging.Logger

private const val TAG = "REST"

class RestLogger: Logger {
    override fun log(message: String) {
        Log.d(TAG, message)
    }
}