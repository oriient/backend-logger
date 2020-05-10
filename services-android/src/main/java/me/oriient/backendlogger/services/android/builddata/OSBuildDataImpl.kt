package me.oriient.backendlogger.services.android.builddata

import me.oriient.backendlogger.BuildConfig
import me.oriient.backendlogger.services.os.builddata.OSBuildData


private const val TAG = "OsBuildData"

class OSBuildDataImpl: OSBuildData {
    override val isDebug = BuildConfig.DEBUG
}