package me.oriient.backendlogger

import android.support.annotation.Keep
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import me.oriient.backendlogger.services.messages.Message
import me.oriient.backendlogger.services.messages.MessagesRepository
import me.oriient.backendlogger.services.os.network.NetworkManager
import me.oriient.backendlogger.services.os.scheduler.ScheduleOptions
import me.oriient.backendlogger.services.os.scheduler.Scheduler
import me.oriient.backendlogger.services.rest.RestDataSerializer
import me.oriient.backendlogger.services.rest.RestService
import me.oriient.backendlogger.utils.BLCoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private const val TAG = "BackendLogger"

/**
 * This library provides a way to log information to a remote server.
 * The messages are persisted until sent and a retry mechanism is implemented if the operation fails.
 * @see {@link PublicOptions} and {@link LocalOptions} for configuration options.
 */

@FlowPreview
@ExperimentalCoroutinesApi
@Suppress("unused", "MemberVisibilityCanBePrivate")
@Keep
class BackendLogger(val url: String) {

    private val localOptions = InstanceOptions()
    private val options = OptionsResolver(localOptions, globalOptions)
    private var connected: Boolean = false
    private var metered: Boolean = true

    private val messagesRepository = inject<MessagesRepository>()
    private val restService = inject<RestService>()
    private val scheduler = inject<Scheduler>()
    private val serializer = inject<RestDataSerializer>()
    private val networkManager = inject<NetworkManager>()

    val unsentMessagesCount: Int
        get() { return messagesRepository.value.getMessagesCount(url) }

    constructor(url: String, block: InstanceOptions.() -> Unit = {}) : this(url) {
        configure(block)
    }

    init {
        BLCoroutineScope.launch {
            networkManager.value.isConnected.asFlow().collect {
                Log.d(TAG, "connection state changed to $it")
                connected = it
            }
            networkManager.value.isConnectionMetered.asFlow().collect {
                Log.d(TAG, "connection state metered changed to $it")
                metered = it
            }
        }
    }

    /**
     * Apply configuration to this instance of {@link BackendLogger}.
     * Any configuration applied here will override global configurations.
     */
    fun configure(block: InstanceOptions.() -> Unit = {}) {
        val stateBeforeConfig = options.state

        this.localOptions.apply(block)

        if (stateBeforeConfig is Scheduled && options.state is Online) {
            GlobalScope.launch(Dispatchers.IO) {
                onStateOnline()
            }
        }
    }

    /**
     * Send a message to with the provided information to the endpoint specified by the URL
     * provided in the constructor.
     *
     * @param messageData the information to be sent.
     */
    @Synchronized
    fun sendMessage(messageData: Map<String, Any>) {
        Log.d(TAG, "sendMessage() called with: messageData = [$messageData]")

        if (messageData.isEmpty()) {
            Log.e(TAG, "Not sending messages with empty data")
            return
        }

        if (messagesRepository.value.getMessagesCount(url) == options.sizeLimit || messagesRepository.value.getMessagesCount() == globalOptions.sizeLimit) {
            Log.d(TAG, "sendMessage: queue is full")
            messagesRepository.value.removeOldest()
        }

        messagesRepository.value.enqueueMessage(
            Message(
                System.currentTimeMillis(),
                url,
                messageData,
                options.retries
            )
        )

        // TODO: 28/04/2020 check network connection and listen to network state

        // TODO: 24/05/2020 retries

        GlobalScope.launch(Dispatchers.IO) {
            when(options.state) {
                is Online -> {
                    onStateOnline()
                }
            }
        }
    }

    private suspend fun onStateOnline() {
        if (!trySendingMessages()) {
            scheduler.value.schedule(ScheduledWork::class.java)
        }
    }

    /**
     * @param clazz the class to be checked.
     * @return {@code true} if the provided class is supported by the serializer.
     */
    fun isSupportedDataType(clazz: Class<Any>): Boolean {
        return serializer.value.acceptedClasses.contains(clazz)
    }

    private class OptionsResolver(private val instanceOptions: InstanceOptions, private val globalOptions: GlobalOptions) {
        val sizeLimit: Int
            get() { return instanceOptions.sizeLimit ?: globalOptions.sizeLimit }
        val retries: Int
            get() { return instanceOptions.retries ?: globalOptions.retries }
        val state: State
            get() { return instanceOptions.state ?: globalOptions.state }
        val scheduleOptions: ScheduleOptions
            get() { return instanceOptions.scheduleOptions ?: globalOptions.scheduleOptions }
    }

    companion object {
        private val globalOptions = GlobalOptions()

        internal val globalUnsentMessagesCount: Int
            get() { return inject<MessagesRepository>().value.getMessagesCount() }

        /**
         * Apply configuration to all the instances of {@link BackendLogger}.
         * Instance configuration will override this configuration.
         */
        fun configureGlobal(block: GlobalOptions.() -> Unit = {}) {
            this.globalOptions.apply(block)
        }

        internal suspend fun trySendingMessages(): Boolean {
            Log.d(TAG, "trySendingMessages() called")

            val messagesRepository: Lazy<MessagesRepository> = inject()
            val restService: Lazy<RestService> = inject()
            val serializer: Lazy<RestDataSerializer> = inject()

            while (messagesRepository.value.getMessagesCount() > 0) {
                messagesRepository.value.getOldest()?.run {
                    if (retries <= 0L) {
                        messagesRepository.value.remove(timeReceivedMilli)
                    } else if (restService.value.sendMessage(url, data, serializer.value)) {
                        messagesRepository.value.remove(timeReceivedMilli)
                    } else {
                        retries--
                        if (retries <= 0L) {
                            messagesRepository.value.remove(timeReceivedMilli)
                        } else {
                            messagesRepository.value.upsert(this)
                        }
                        return false
                    }
                }
            }
            return messagesRepository.value.getMessagesCount() == 0
        }
    }
}
