package me.oriient.backendlogger.services.os.scheduler

import me.oriient.backendlogger.utils.DIProvidable

interface Scheduler: DIProvidable {
    fun <T: Work> schedule(clazz: Class<T>)
}