package me.oriient.backendlogger.services.database

import me.oriient.backendlogger.services.os.database.DatabaseDriverProvider
import me.oriient.backendlogger.services.os.log.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

interface DatabaseService {
    fun upsert(data: DatabaseData)
    fun getOldest(): DatabaseData?
    fun remove(time: Long)
    fun count(): Int
    fun countForUrl(url: String): Int
    fun removeAll()
    fun removeOldest()
}

@Suppress("unused")
internal class DatabaseServiceImpl(private val logger: Log, driverProvider: DatabaseDriverProvider): DatabaseService {

    private val database = BackendLoggerDatabase(driverProvider.driver)

    private fun serializeData(data: Map<String, Any>): ByteArray {
        try {
            ByteArrayOutputStream().use { byteStream ->
                ObjectOutputStream(byteStream).use { objStream ->
                    objStream.writeObject(data)
                    objStream.flush()
                    return byteStream.toByteArray()
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to serialize data to send: ${e.message}")
        }
    }

    private fun deserializeData(data: ByteArray): Map<String, Any> {
        try {
            ByteArrayInputStream(data).use { byteStream ->
                ObjectInputStream(byteStream).use { objStream ->
                    @Suppress("UNCHECKED_CAST")
                    return objStream.readObject() as Map<String, Any>
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to de-serialize data to send: ${e.message}")
        }
    }

    override fun upsert(data: DatabaseData) {
        database.backendLoggerQueries.upsert(data.timeReceivedMilli, data.url, serializeData(data.message), data.retries)
    }

    override fun getOldest(): DatabaseData? {
        val data = database.backendLoggerQueries.getOldest().executeAsOneOrNull()
        data?.run {
            return DatabaseData(time, url, deserializeData(message), retries)
        }
        return null
    }

    override fun remove(time: Long) {
        database.backendLoggerQueries.remove(time)
    }

    override fun count(): Int {
        return database.backendLoggerQueries.count().executeAsOne().toInt()
    }

    override fun countForUrl(url: String): Int {
        return database.backendLoggerQueries.countForUrl(url).executeAsOne().toInt()
    }

    override fun removeAll() {
        database.backendLoggerQueries.removeAll()
    }

    override fun removeOldest() {
        database.backendLoggerQueries.removeOldest()
    }
}