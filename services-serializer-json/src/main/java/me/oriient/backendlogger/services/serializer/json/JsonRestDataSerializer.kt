package me.oriient.backendlogger.services.serializer.json

import androidx.annotation.Keep
import me.oriient.backendlogger.services.os.log.Log
import me.oriient.backendlogger.services.os.rest.RestProvider
import me.oriient.backendlogger.services.rest.RestDataSerializer
import me.oriient.backendlogger.utils.DIProvidable
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import kotlinx.serialization.json.*


private const val TAG = "JsonRestDataSerializer"

@Keep
@Suppress("unused")
class JsonRestDataSerializer(private val logger: Log, restProvider: RestProvider): RestDataSerializer, DIProvidable {

    init {
        restProvider.getClient().config {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    override val acceptedClasses: List<Class<Any>>
        get() = acceptedClasses

    override fun serialize(data: Map<String, Any>): OutgoingContent {
        val jsonElements = mutableMapOf<String, JsonElement>()
        for (entry in data.entries) {
            // TODO: 19/02/2020 support more types
            when (entry.value) {
                is String -> {
                    jsonElements[entry.key] = JsonPrimitive(entry.value as String)
                }
                is Number -> {
                    jsonElements[entry.key] = JsonPrimitive(entry.value as Number)
                }
                is Boolean -> {
                    jsonElements[entry.key] = JsonPrimitive(entry.value as Boolean)
                }
                else -> {
                    logger.e(TAG, "Data of type ${entry.value::class} is not supported")
                }
            }
        }
        return TextContent(JsonObject(jsonElements).toString(), ContentType.Application.Json)
    }

    companion object {
        private val acceptedClasses = listOf<Class<Any>>(String.javaClass, Int.javaClass, Long.javaClass, Float.javaClass, Double.javaClass, Boolean.javaClass)
    }
}