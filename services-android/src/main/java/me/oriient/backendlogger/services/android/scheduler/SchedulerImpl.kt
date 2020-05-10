package me.oriient.backendlogger.services.android.scheduler

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.runBlocking
import me.oriient.backendlogger.services.android.context
import me.oriient.backendlogger.services.os.log.Log
import me.oriient.backendlogger.services.os.scheduler.Scheduler
import me.oriient.backendlogger.services.os.scheduler.Work


private const val TAG = "BackendLoggerWorker"

private const val WORKER_CLASS_NAME_KEY = "workerClassName"

@Suppress("unused")
internal class SchedulerImpl(private val logger: Log): Scheduler {
    override fun <T : Work> schedule(clazz: Class<T>) {
        logger.d(TAG, "schedule() called with: clazz = [$clazz]")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<WorkWrapper>()
            .setConstraints(constraints)
            .setInputData(Data.Builder().putString(WORKER_CLASS_NAME_KEY, clazz.canonicalName).build())
            .build()
        WorkManager.getInstance(context).enqueue(uploadWorkRequest)
    }
}

internal class WorkWrapper(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val result = runBlocking { (Class.forName(inputData.getString(WORKER_CLASS_NAME_KEY)!!, false, applicationContext.classLoader).constructors.first().newInstance() as Work).doWork() }

        return if (result) Result.success() else Result.failure()
    }
}