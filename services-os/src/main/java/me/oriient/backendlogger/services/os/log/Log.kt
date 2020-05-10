package me.oriient.backendlogger.services.os.log

import android.support.annotation.Keep
import me.oriient.backendlogger.utils.DIProvidable

@Keep
interface Log: DIProvidable {
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String)
}