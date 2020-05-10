package me.oriient.backendlogger.services.messages

import me.oriient.backendlogger.services.database.DatabaseData

data class Message(val timeReceivedMilli: Long, val url: String, val data: Map<String, Any>, var retries: Int = 0) {
    fun toDatabaseData(): DatabaseData {
        return DatabaseData(timeReceivedMilli, url, data, retries.toLong())
    }
}