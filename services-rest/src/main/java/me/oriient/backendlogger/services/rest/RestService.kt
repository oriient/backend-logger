package me.oriient.backendlogger.services.rest

import me.oriient.backendlogger.services.os.log.Log
import me.oriient.backendlogger.services.os.rest.RestProvider
import me.oriient.backendlogger.utils.DIProvidable
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse

private const val TAG = "RestService"

@Suppress
interface RestService: DIProvidable {
    suspend fun sendMessage(url: String, message: Map<String, Any>, serializer: RestDataSerializer): Boolean
}

@Suppress("unused")
internal class RestServiceImpl(private val logger: Log, provider: RestProvider): RestService {

    private val client: HttpClient = provider.getClient()

    override suspend fun sendMessage(url: String, message: Map<String, Any>, serializer: RestDataSerializer): Boolean {
        logger.d(TAG, "sendMessage() called with: url = [$url], message = [$message], serializer = [$serializer]")

        return try {
            val response = client.post<HttpResponse> {
                url(url)
                body = serializer.serialize(message)
            }
            logger.d(TAG, "sendMessage: response is $response")
            response.status.value in 200..299
        } catch (cause: Throwable) {
            logger.e(TAG, "postString: ${cause.message}")
            false
        }
    }
}