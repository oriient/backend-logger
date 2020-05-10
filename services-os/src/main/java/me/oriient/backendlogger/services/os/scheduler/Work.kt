package me.oriient.backendlogger.services.os.scheduler

interface Work {
    suspend fun doWork(): Boolean
}