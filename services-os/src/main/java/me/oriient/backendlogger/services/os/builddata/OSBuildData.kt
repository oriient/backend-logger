package me.oriient.backendlogger.services.os.builddata

import me.oriient.backendlogger.utils.DIProvidable


private const val TAG = "OsBuildData"

interface OSBuildData: DIProvidable {
    val isDebug: Boolean
}