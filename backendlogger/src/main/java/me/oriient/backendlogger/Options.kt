@file:Suppress("unused")

package me.oriient.backendlogger

import android.support.annotation.Keep
import me.oriient.backendlogger.services.os.scheduler.ScheduleOptions

/**
 * Describes when the data will be sent
 */
@Keep
sealed class State

/**
 * The data will be sent whenever possible.
 */
@Keep
class Online(val allowMeteredNetworks: Boolean = true) : State()
/**
 * The data will only be sent when the state will turn into Online or from the scheduled task.
 */
@Keep
object Scheduled : State()


/**
 * Global configuration options.
 */
@Keep
class GlobalOptions internal constructor() {
    var sizeLimit = 100
    var retries = 10
    var state : State = Online()
    var scheduleOptions = ScheduleOptions()
}

/**
 * Instance configuration options.
 */
@Keep
class InstanceOptions internal constructor() {
    var sizeLimit: Int? = null
    var retries: Int? = null
    var state : State? = null
    var scheduleOptions: ScheduleOptions? = null
}

