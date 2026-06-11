package com.bbtest.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkSpecifier
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.PatternMatcher
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.test.uiautomator.UiDevice

class WifiTools(
    val device: UiDevice,
    private val context: Context,
) {
    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun connectWifi(ssid: String, password: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectByP2P(ssid, password)
        } else {
            connectByConfig(ssid, password)
        }
    }

    fun isNetworkConnected(): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isAvailable == true
    }

    fun isNetworkAvailable(): Boolean {
        val info = connectivityManager.activeNetworkInfo
        return info != null && info.isConnected && info.state == android.net.NetworkInfo.State.CONNECTED
    }

    fun isWifiConnected(): Boolean {
        val wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return wifiNetworkInfo?.isAvailable == true
    }

    fun openWifi() {
        if (!wifiManager.isWifiEnabled) {
            ShellCommand.execCmdByUiDevice(device, "svc wifi enable")
        }
    }

    fun startScantWifi() {
        val wifiScanReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val scanResults: List<ScanResult> = wifiManager.scanResults
                }
            }
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)
        wifiManager.startScan()
    }

    private fun connectByConfig(ssid: String, password: String) {
        val config = WifiConfiguration()
        config.SSID = "\"$ssid\""
        config.preSharedKey = "\"$password\""
        config.hiddenSSID = true
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
        config.status = WifiConfiguration.Status.ENABLED
        wifiManager.addNetwork(config)
        wifiManager.saveConfiguration()
        wifiManager.disconnect()
        wifiManager.enableNetwork(config.networkId, true)
        wifiManager.reconnect()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectByP2P(ssid: String, password: String) {
        val specifier: NetworkSpecifier =
            WifiNetworkSpecifier.Builder()
                .setSsidPattern(PatternMatcher("\"$ssid\"", PatternMatcher.PATTERN_PREFIX))
                .setWpa2Passphrase(password)
                .build()

        val request =
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifier)
                .build()

        val networkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                }

                override fun onUnavailable() {
                    Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
                }
            }
        connectivityManager.requestNetwork(request, networkCallback)
    }

    private fun connectBySug(ssid: String, password: String) {
        val suggestion1 =
            WifiNetworkSuggestion.Builder()
                .setSsid("\"$ssid\"")
                .setIsAppInteractionRequired(true)
                .build()
        val suggestion2 =
            WifiNetworkSuggestion.Builder()
                .setSsid("\"$ssid\"")
                .setWpa2Passphrase("\"$password\"")
                .setIsAppInteractionRequired(true)
                .build()
        val suggestion3 =
            WifiNetworkSuggestion.Builder()
                .setSsid("\"$ssid\"")
                .setWpa3Passphrase("\"$password\"")
                .setIsAppInteractionRequired(true)
                .build()

        val suggestionsList = ArrayList<WifiNetworkSuggestion>()
        suggestionsList.add(suggestion1)
        suggestionsList.add(suggestion2)
        suggestionsList.add(suggestion3)

        val status = wifiManager.addNetworkSuggestions(suggestionsList)
        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
        }

        val intentFilter = IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)
        val broadcastReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action != WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION) {
                        return
                    }
                }
            }
        context.registerReceiver(broadcastReceiver, intentFilter)
    }
}
