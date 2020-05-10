package me.oriient.backendlogger.utils.builddata

import me.oriient.backendlogger.utils.DIProvidable

interface BuildData: DIProvidable {
    val isDebug: Boolean
}

internal class BuildDataImpl(override val isDebug: Boolean):
    BuildData