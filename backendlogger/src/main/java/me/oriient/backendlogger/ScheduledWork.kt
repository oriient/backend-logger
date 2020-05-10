package me.oriient.backendlogger

import me.oriient.backendlogger.services.os.log.Log
import me.oriient.backendlogger.services.os.scheduler.Work
import kotlinx.coroutines.runBlocking


private const val TAG = "ScheduledWork"

internal class ScheduledWork: Work {

    override suspend fun doWork(): Boolean {
        val log: Log = get()
        log.d(TAG, "doWork() called")
        BackendLogger.trySendingMessages()
        return BackendLogger.globalUnsentMessagesCount == 0
    }
}