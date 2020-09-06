package me.oriient.backendlogger.services.rest.tests

import android.util.Log
import me.oriient.backendlogger.services.os.rest.RestProvider
import me.oriient.backendlogger.services.rest.RestDataSerializer
import me.oriient.backendlogger.services.rest.RestServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.fullPath
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.fail


class BasicTests {

    @Test
    fun testSendingMessage() {

        val sendUrl = "https://www.my.domain/api/route"

        val log = mockk<Log>(relaxed = true)
        every { log.e(any(), any()) } answers {
            println("$${arg<String>(0)}: ${arg<String>(1)}")
            0
        }
        val serializer = mockk<RestDataSerializer>()

        val restProvider = mockk<RestProvider>()
        var called = 0
        val httpClient = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    called++
                    when(request.url.toString()) {
                        sendUrl -> {
                            respond("")
                        }
                        else -> fail("Unknown url: ${request.url.fullPath}")
                    }
                }
            }
        }

        every { restProvider.getClient() } returns httpClient

        every { serializer.serialize(any()) } answers { TextContent("Some text", ContentType.Application.Any) }

        val restService = RestServiceImpl(restProvider)

        runBlocking { assert(restService.sendMessage(sendUrl, mapOf(), serializer)) { "Failed to send message. See logs." } }

        assert(called == 1) { "Post called $called times" }

        verify(exactly = 1) { restProvider.getClient() }
    }

}
