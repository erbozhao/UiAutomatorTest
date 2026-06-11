package com.bbtest.tools

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import androidx.test.core.app.ApplicationProvider.getApplicationContext

class NetworkInfo {
    private val context: Context = getApplicationContext()

    fun closeWifi() {
        operateWifi(false)
    }

    fun openWifi() {
        operateWifi(true)
    }

    fun operateWifi(isOpenWifi: Boolean) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (isOpenWifi) {
            if (!wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = true
            }
        } else if (wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = false
        }
    }

    fun isWiFiEnabled(context: Context): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return try {
            val method = wifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
            method.isAccessible = true
            method.invoke(wifiManager) as Boolean
        } catch (_: Throwable) {
            false
        }
    }

    fun configApState(context: Context): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiConfiguration: WifiConfiguration? = null
        return try {
            if (isWiFiEnabled(context)) {
                wifiManager.isWifiEnabled = false
            }
            val method = wifiManager.javaClass.getMethod(
                "setWifiApEnabled",
                WifiConfiguration::class.java,
                Boolean::class.javaPrimitiveType,
            )
            method.invoke(wifiManager, wifiConfiguration, !isWiFiEnabled(context))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
