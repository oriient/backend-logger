package me.oriient.backendlogger

import android.util.Log
import me.oriient.backendlogger.services.os.scheduler.Work


private const val TAG = "ScheduledWork"

internal class ScheduledWork: Work {

    override suspend fun doWork(): Boolean {
        Log.d(TAG, "doWork() called")
        BackendLogger.trySendingMessages()
        return BackendLogger.globalUnsentMessagesCount == 0
    }
}