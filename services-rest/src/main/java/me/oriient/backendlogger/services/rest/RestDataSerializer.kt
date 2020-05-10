package me.oriient.backendlogger.services.rest

import android.support.annotation.Keep
import me.oriient.backendlogger.utils.DIProvidable
import io.ktor.http.content.OutgoingContent

@Keep
interface RestDataSerializer: DIProvidable {
    val acceptedClasses: List<Class<Any>>
    fun serialize(data: Map<String, Any>): OutgoingContent
}