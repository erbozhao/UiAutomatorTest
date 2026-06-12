package com.bbtest.perform.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.uiautomator.UiDevice
import com.bbtest.tools.DeviceInfo
import com.bbtest.utils.ShellCommand

class Batinfo {
    private var device: UiDevice? = null
    private var context: Context? = null
    private var batteryManager: BatteryManager? = null

    constructor(device: UiDevice) {
        this.device = device
    }

    constructor(context: Context) {
        this.context = context
        batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }

    fun getBattery(): Map<String, String> {
        val battery = HashMap<String, String>()

        var level = 0
        var scale = 0
        var totalBatt: String
        var current = ""
        var voltage = ""
        var temperature = ""

        val currentDevice = requireNotNull(device)
        val sdk = DeviceInfo(currentDevice).sdk
        val cmd = if (sdk > 22) "dumpsys battery" else "dumpsys batterymanager"
        val result = ShellCommand.execCmdByUiDevice(currentDevice, cmd)
        val resultLines = result.split("\n")
        for (resultLine in resultLines) {
            if (resultLine.isNotEmpty()) {
                if (resultLine.contains("level:")) {
                    val temp = resultLine.trim().split("\\s+".toRegex())
                    level = temp[1].trim().toInt()
                } else if (resultLine.contains("scale:")) {
                    val temp = resultLine.trim().split("\\s+".toRegex())
                    scale = temp[1].trim().toInt()
                } else if (resultLine.contains("current now:")) {
                    val temp = resultLine.trim().split("\\s+".toRegex())
                    current = (temp[2].trim().toInt() * 1.0 / 1000).toString()
                } else if (resultLine.contains("voltage:") && !resultLine.contains("Max")) {
                    val temp = resultLine.trim().split("\\s+".toRegex())
                    voltage = (temp[1].trim().toInt() * 1.0 / 1000).toString()
                } else if (resultLine.contains("temperature:")) {
                    val temp = resultLine.trim().split("\\s+".toRegex())
                    temperature = (temp[1].trim().toInt() * 1.0 / 10).toString()
                }
            }
        }

        totalBatt = if (scale != 0) {
            (level * 100 / scale).toString()
        } else {
            level.toString()
        }

        battery["totalBatt"] = totalBatt
        battery["current"] = current
        battery["voltage"] = voltage
        battery["temperature"] = temperature
        return battery
    }

    fun getBatteryRemaining(): Int = requireNotNull(batteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)

    fun getBatteryCurrent(): Int = requireNotNull(batteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)

    fun getBatteryCurrentAvg(): Int = requireNotNull(batteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)

    fun getBatteryPercentage(): Int = requireNotNull(batteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    fun getBatteryRemainingNavat(): Int = requireNotNull(batteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER)

    fun getBatteryCapacity(context: Context): Double {
        var batteryCapacity = 0.0
        val powerProfileClass = "com.android.internal.os.PowerProfile"
        try {
            val powerProfile = Class.forName(powerProfileClass).getConstructor(Context::class.java).newInstance(context)
            batteryCapacity = Class.forName(powerProfileClass).getMethod("getBatteryCapacity").invoke(powerProfile) as Double
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return batteryCapacity
    }

    private fun demo() {
        val context = getApplicationContext<Context>()
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, ifilter) ?: return
        val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        val chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batLevel = level.toFloat() / scale.toFloat()
    }
}

class BatteryInfoBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BATTERY_CHANGED == intent.action) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val battery = (level * 100 / scale).toString()
            val voltage = (intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) * 1.0 / 1000).toString()
            val temperature = (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) * 1.0 / 10).toString()
        }
    }
}
