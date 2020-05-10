@file:Suppress("unused")

package me.oriient.backendlogger.services.android.rest

import me.oriient.backendlogger.BuildConfig
import me.oriient.backendlogger.services.android.log.LogImpl
import me.oriient.backendlogger.services.os.rest.RestProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging

private const val TAG = "RestProvider"

internal class RestProviderImpl: RestProvider {
    override fun getClient(): HttpClient {
        return HttpClient(Android) {
            install(Logging) {
                logger = LogImpl()
                level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
            }
        }
    }
}