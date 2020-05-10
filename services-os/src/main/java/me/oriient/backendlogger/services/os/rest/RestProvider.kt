package me.oriient.backendlogger.services.os.rest

import android.support.annotation.Keep
import io.ktor.client.HttpClient


@Keep
@Suppress
interface RestProvider {
    fun getClient(): HttpClient
}