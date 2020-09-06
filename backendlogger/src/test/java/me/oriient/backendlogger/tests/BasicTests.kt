package me.oriient.backendlogger.tests

import me.oriient.backendlogger.*
import me.oriient.backendlogger.di.DI
import me.oriient.backendlogger.services.messages.Message
import me.oriient.backendlogger.services.messages.MessagesRepository
//import me.oriient.backendlogger.services.os.log.Log
import me.oriient.backendlogger.services.os.scheduler.Scheduler
import me.oriient.backendlogger.services.rest.RestDataSerializer
import me.oriient.backendlogger.services.rest.RestService
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class BasicTests {

    @Test
    fun testSendingMessage() {

        // TODO: 05/05/2020 break into smaller tests

        val url = "testUrl"
        val retriesCount = 1
        val limit = 5  // not less than 2
        var countResponse = limit
        val messageDataMap = mutableMapOf<String, Any>()
        messageDataMap["message"] = "Test message"
        val message = Message(0L, url, messageDataMap, 2)

        val diMock = mockk<DI>()

        val messagesRepository = mockk<MessagesRepository>()
        val lazyMessagesRepository = mockk<Lazy<MessagesRepository>>()
        val restService = mockk<RestService>()
        val lazyRestService = mockk<Lazy<RestService>>()
        val scheduler = mockk<Scheduler>()
        val lazyScheduler = mockk<Lazy<Scheduler>>()
        val serializer = mockk<RestDataSerializer>()
        val lazySerializer = mockk<Lazy<RestDataSerializer>>()

        mockkStatic("me.oriient.backendlogger.DIKt")
        every { initializeDi() } answers { nothing }
        every { di } returns diMock

//        every { loge(any(), any()) } answers {
//            println("$${arg<String>(0)}: ${arg<String>(1)}")
//            nothing
//        }
//        every { logd(any(), any()) } answers { nothing }
//        every { logi(any(), any()) } answers { nothing }
//        every { logw(any(), any()) } answers { nothing }

        every { inject<MessagesRepository>() } returns lazyMessagesRepository
        every { lazyMessagesRepository.value } returns messagesRepository
        every { inject<Scheduler>() } returns lazyScheduler
        every { lazyScheduler.value } returns scheduler
        every { inject<RestService>() } returns lazyRestService
        every { lazyRestService.value } returns restService
        every { inject<RestDataSerializer>() } returns lazySerializer
        every { lazySerializer.value } returns serializer

        every { messagesRepository.getMessagesCount(any()) } answers {
            val urlInCall = arg<String>(0)
            assert(urlInCall == url)
            limit
        }
        every { messagesRepository.getMessagesCount() } answers {
            countResponse -= 1
            if (countResponse < 0) {
                countResponse = 0
            }
            countResponse
        }
        every { messagesRepository.enqueueMessage(any()) } answers {
            val innerMessage = arg<Message>(0)
            assert(innerMessage.data == messageDataMap)
            assert(innerMessage.url == url)
            assert(innerMessage.retries == retriesCount)
            nothing
        }
        every { messagesRepository.getOldest() } returns message
        every { messagesRepository.removeOldest() } answers { nothing }
        every { messagesRepository.remove(any()) } answers { nothing }
        every { messagesRepository.upsert(any()) } answers { nothing }
        coEvery { restService.sendMessage(any(), any(), any()) } answers {
            countResponse > 1
        }
        every { scheduler.schedule(ScheduledWork::class.java) } answers { nothing }

        val backendLogger = BackendLogger(url) {
            sizeLimit = limit
            retries = retriesCount
        }

        backendLogger.sendMessage(messageDataMap)

        verify(exactly = 1) { messagesRepository.removeOldest() }
        verify(exactly = 1) { messagesRepository.enqueueMessage(any()) }
        coVerify(exactly = limit - 1) { restService.sendMessage(any(), any(), any()) }
        verify(exactly = limit - 2) { messagesRepository.remove(any()) }
        verify(exactly = 1) { messagesRepository.upsert(any()) }
        verify(exactly = 1) { scheduler.schedule(ScheduledWork::class.java) }
    }

}
