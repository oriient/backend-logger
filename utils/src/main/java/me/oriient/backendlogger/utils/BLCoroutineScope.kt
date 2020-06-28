package me.oriient.backendlogger.utils

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object BLCoroutineScope: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}