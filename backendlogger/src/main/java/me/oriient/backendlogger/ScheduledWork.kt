package me.oriient.backendlogger

import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.oriient.backendlogger.services.os.scheduler.Work


private const val TAG = "ScheduledWork"

@ExperimentalCoroutinesApi
@FlowPreview
internal class ScheduledWork: Work {

    override suspend fun doWork(): Boolean {
        Log.d(TAG, "doWork() called")
        BackendLogger.trySendingMessages()
        return BackendLogger.globalUnsentMessagesCount == 0
    }
}