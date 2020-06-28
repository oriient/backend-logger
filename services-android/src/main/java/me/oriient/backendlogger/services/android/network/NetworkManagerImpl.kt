package me.oriient.backendlogger.services.android.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import me.oriient.backendlogger.services.android.context
import me.oriient.backendlogger.services.os.network.NetworkManager
import me.oriient.backendlogger.utils.BLCoroutineScope


private const val TAG = "NetworkManagerImpl"

@ExperimentalCoroutinesApi
@Suppress("unused")
class NetworkManagerImpl: NetworkManager {

    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val isConnected = ConflatedBroadcastChannel<Boolean>()
    override val isConnectionMetered = ConflatedBroadcastChannel<Boolean>()

    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            Log.d(TAG, "onAvailable() called with: network = [$network]")
            BLCoroutineScope.launch {
                if (!isConnected.value) {
                    isConnected.send(true)
                }
            }
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "onLost() called with: network = [$network]")
            BLCoroutineScope.launch {
                if (isConnected.value) {
                    isConnected.send(false)
                }
            }
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            Log.d(TAG, "onCapabilitiesChanged() called with: network = [$network], networkCapabilities = [$networkCapabilities]")
            val notMetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            if (isConnectionMetered.value && notMetered) {
                BLCoroutineScope.launch {
                    isConnectionMetered.send(false)
                }
            } else if (!isConnectionMetered.value && !notMetered) {
                BLCoroutineScope.launch {
                    isConnectionMetered.send(true)
                }
            }
        }
    }
    private val connectivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive() called with: context = [$context], intent = [$intent]")
            val isDisconnected: Boolean = intent?.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false) ?: false
            if (isConnected.value && isDisconnected) {
                BLCoroutineScope.launch {
                    isConnected.send(false)
                }
            } else if (!isConnected.value && !isDisconnected) {
                BLCoroutineScope.launch {
                    isConnected.send(true)
                }
            }
            sentConnectionMeteredIfChanged()
        }
    }

    private fun sentConnectionMeteredIfChanged() {
        val currentValue = connectivityManager.isActiveNetworkMetered
        if (currentValue != isConnectionMetered.value) {
            BLCoroutineScope.launch {
                isConnectionMetered.send(currentValue)
            }
        }
    }

    init {
        BLCoroutineScope.launch {
            isConnected.send(connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false)
            isConnectionMetered.send(connectivityManager.isActiveNetworkMetered)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(connectivityCallback)
        } else {
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(connectivityReceiver, filter)
        }
    }
}