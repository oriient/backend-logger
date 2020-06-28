package me.oriient.backendlogger.services.os.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import me.oriient.backendlogger.utils.DIProvidable

@ExperimentalCoroutinesApi
interface NetworkManager: DIProvidable {

    val isConnected: BroadcastChannel<Boolean>
    val isConnectionMetered: BroadcastChannel<Boolean>
}