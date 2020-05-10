package me.oriient.backendlogger.services.database

data class DatabaseData(val timeReceivedMilli: Long, val url: String, val message: Map<String, Any>, var retries: Long)