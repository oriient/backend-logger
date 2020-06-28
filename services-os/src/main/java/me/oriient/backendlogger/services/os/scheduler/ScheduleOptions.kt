package me.oriient.backendlogger.services.os.scheduler

import android.support.annotation.Keep


private const val TAG = "ScheduleOptions"

@Keep
data class ScheduleOptions(val allowMeteredNetworks: Boolean = false, val requireCharger: Boolean = true)