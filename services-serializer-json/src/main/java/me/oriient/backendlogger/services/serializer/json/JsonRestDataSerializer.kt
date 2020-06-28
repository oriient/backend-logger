package me.oriient.backendlogger.services.serializer.json

import android.util.Log
import androidx.annotation.Keep
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
class JsonRestDataSerializer(restProvider: RestProvider): RestDataSerializer, DIProvidable {

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
            when {
                entry.value.javaClass.isPrimitive -> jsonElements[entry.key] = parsePrimitive(entry.value)
                entry.value.javaClass.isArray -> jsonElements[entry.key] = parseArray(entry.value)
                else -> Log.e(TAG, "Data of type ${entry.value::class} is not supported")
            }
        }
        return TextContent(JsonObject(jsonElements).toString(), ContentType.Application.Json)
    }

    private fun parseArray(array: Any): JsonArray {
        val elements = mutableListOf<JsonElement>()
        if (array is Array<*>) {
            for (value in array) {
                elements.add(
                    when {
                        value == null -> JsonNull
                        value.javaClass.isPrimitive -> parsePrimitive(value)
                        value.javaClass.isArray -> parseArray(value)
                        else -> JsonPrimitive(value::class.java.simpleName)
                    }
                )
            }
        }
        return JsonArray(elements)
    }

    private fun parsePrimitive(value: Any): JsonPrimitive {
        return when (value) {
            is String -> JsonPrimitive(value)
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            else -> {
                Log.e(TAG, "${value::class.simpleName} is not a primitive")
                JsonPrimitive("${value::class.simpleName}")
            }
        }
    }

    companion object {
        private val acceptedClasses = listOf<Class<Any>>(String.javaClass, Int.javaClass, Long.javaClass, Float.javaClass, Double.javaClass, Boolean.javaClass)
    }
}