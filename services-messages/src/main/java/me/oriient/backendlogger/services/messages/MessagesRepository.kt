package me.oriient.backendlogger.services.messages

import android.util.Log
import me.oriient.backendlogger.services.database.DatabaseService
import me.oriient.backendlogger.utils.DIProvidable

private const val TAG = "MessagesRepository"

interface MessagesRepository: DIProvidable {
    fun enqueueMessage(message: Message)
    fun upsert(message: Message)
    fun getMessagesCount(): Int
    fun getMessagesCount(url: String): Int
    fun getOldest(): Message?
    fun removeAll()
    fun removeOldest()
    fun remove(timeReceivedMilli: Long)
}

@Suppress("unused")
internal class MessageRepositoryImpl(
    private val databaseService: DatabaseService
): MessagesRepository {

    override fun enqueueMessage(message: Message) {
        Log.d(TAG, "enqueueMessage() called with: message = [$message]")
        databaseService.upsert(message.toDatabaseData())
    }

    override fun upsert(message: Message) {
        Log.d(TAG, "upsert() called with: message = [$message]")
        databaseService.upsert(message.toDatabaseData())
    }

    override fun getMessagesCount(): Int {
        return databaseService.count()
    }

    override fun getMessagesCount(url: String): Int {
        return databaseService.countForUrl(url)
    }

    override fun getOldest(): Message? {
        return databaseService.getOldest()?.toMessage()
    }

    override fun removeAll() {
        Log.d(TAG, "removeAll() called")
        databaseService.removeAll()
    }

    override fun removeOldest() {
        Log.d(TAG, "removeOldest() called")
        databaseService.removeOldest()
    }

    override fun remove(timeReceivedMilli: Long) {
        Log.d(TAG, "remove() called with: timeReceivedMilli = [$timeReceivedMilli]")
        databaseService.remove(timeReceivedMilli)
    }
}