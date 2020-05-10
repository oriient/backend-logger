package me.oriient.backendlogger.services.messages

import me.oriient.backendlogger.services.database.DatabaseData

internal fun DatabaseData.toMessage(): Message {
    return Message(timeReceivedMilli, url, message, retries.toInt())
}