package com.bbtest.perform.base

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.telephony.TelephonyManager
import android.util.Log
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.uiautomator.UiDevice
import com.bbtest.MainActivity
import com.bbtest.utils.ShellCommand
import java.util.Calendar

class Flowinfo(device: UiDevice, uid: Int) {
    init {
        Companion.device = device
        Companion.uid = uid
        readFlowInfo()
    }

    fun readFlowInfo() {
        val cmd = "cat /proc/net/xt_qtaguid/stats"
        flowInfo = ShellCommand.execCmdByUiDevice(device!!, cmd)
        if (flowInfo.isEmpty() || (flowInfo.contains("Permission") && flowInfo.contains("denied"))) {
            flowInfo = ShellCommand.execCmdBySu(cmd)
        }
    }

    fun getFlow(): Long {
        var backFlow = 0L
        var foreFlow = 0L
        if (uid != 0) {
            val flowInfoLines = flowInfo.split("\n")
            for (flowInfoLine in flowInfoLines) {
                if (flowInfoLine.contains("0x0") && flowInfoLine.contains("$uid 0")) {
                    val flowInfoPart = flowInfoLine.split("\\s+".toRegex())
                    val backRcvFlow = flowInfoPart[5].toLong()
                    val backSndFlow = flowInfoPart[7].toLong()
                    backFlow += backRcvFlow + backSndFlow
                } else if (flowInfoLine.contains("0x0") && flowInfoLine.contains("$uid 1")) {
                    val tmpFlow = flowInfoLine.split("\\s+".toRegex())
                    val foreRcvFlow = tmpFlow[5].toLong()
                    val foreSndFlow = tmpFlow[7].toLong()
                    foreFlow += foreRcvFlow + foreSndFlow
                }
            }
        }
        return backFlow + foreFlow
    }

    fun getSndFlow(): Long {
        var backSndFlow = 0L
        var foreSndFlow = 0L
        if (uid != 0) {
            val flowInfoLines = flowInfo.split("\n")
            for (flowInfoLine in flowInfoLines) {
                if (flowInfoLine.contains("0x0") && flowInfoLine.contains("$uid 0")) {
                    backSndFlow += flowInfoLine.split("\\s+".toRegex())[7].toLong()
                } else if (flowInfoLine.contains("0x0") && flowInfoLine.contains("$uid 1")) {
                    foreSndFlow += flowInfoLine.split("\\s+".toRegex())[7].toLong()
                }
            }
        }
        return backSndFlow + foreSndFlow
    }

    fun getRcvFlow(): Long {
        var backRcvFlow = 0L
        var foreRcvFlow = 0L
        if (uid != 0) {
            val flowInfoLines = flowInfo.split("\n")
            for (flowInfoLine in flowInfoLines) {
                if (flowInfoLine.contains("0x0") && flowInfoLine.contains("$uid 0")) {
                    backRcvFlow += flowInfoLine.split("\\s+".toRegex())[5].toLong()
                } else if (flowInfoLine.contains("0x0") && flowInfoLine.contains("$uid 1")) {
                    foreRcvFlow += flowInfoLine.split("\\s+".toRegex())[5].toLong()
                }
            }
        }
        return backRcvFlow + foreRcvFlow
    }

    fun getWifiFlow(): Long {
        var wifiBackFlow = 0L
        var wifiForeFlow = 0L
        if (uid != 0) {
            val flowInfoLines = flowInfo.split("\n")
            for (flowInfoLine in flowInfoLines) {
                if (flowInfoLine.contains("0x0")) {
                    if (flowInfoLine.contains("$uid 0") && flowInfoLine.contains("wlan0")) {
                        val tmpFlow = flowInfoLine.split("\\s+".toRegex())
                        val wifiBackRcvFlow = tmpFlow[5].toLong()
                        val wifiBackSndFlow = tmpFlow[7].toLong()
                        wifiBackFlow = wifiBackRcvFlow + wifiBackSndFlow
                    } else if (flowInfoLine.contains("$uid 1") && flowInfoLine.contains("wlan0")) {
                        val tmpFlow = flowInfoLine.split("\\s+".toRegex())
                        val wifiForeRcvFlow = tmpFlow[5].toLong()
                        val wifiForeSndFlow = tmpFlow[7].toLong()
                        wifiForeFlow = wifiForeRcvFlow + wifiForeSndFlow
                    }
                }
            }
        }
        return wifiBackFlow + wifiForeFlow
    }

    fun getWifiSndFlow(): Long {
        var wifiBackSndFlow = 0L
        var wifiForeSndFlow = 0L
        if (uid != 0) {
            val flowInfoLines = flowInfo.split("\n")
            for (flowInfoLine in flowInfoLines) {
                if (flowInfoLine.contains("0x0")) {
                    if (flowInfoLine.contains("$uid 0") && flowInfoLine.contains("wlan0")) {
                        wifiBackSndFlow = flowInfoLine.split("\\s+".toRegex())[7].toLong()
                    } else if (flowInfoLine.contains("$uid 1") && flowInfoLine.contains("wlan0")) {
                        wifiForeSndFlow = flowInfoLine.split("\\s+".toRegex())[7].toLong()
                    }
                }
            }
        }
        return wifiBackSndFlow + wifiForeSndFlow
    }

    fun getWifiRcvFlow(): Long {
        var wifiBackRcvFlow = 0L
        var wifiForeRcvFlow = 0L
        if (uid != 0) {
            val flowInfoLines = flowInfo.split("\n")
            for (flowInfoLine in flowInfoLines) {
                if (flowInfoLine.contains("0x0")) {
                    if (flowInfoLine.contains("$uid 0") && flowInfoLine.contains("wlan0")) {
                        wifiBackRcvFlow = flowInfoLine.split("\\s+".toRegex())[5].toLong()
                    } else if (flowInfoLine.contains("$uid 1") && flowInfoLine.contains("wlan0")) {
                        wifiForeRcvFlow = flowInfoLine.split("\\s+".toRegex())[5].toLong()
                    }
                }
            }
        }
        return wifiBackRcvFlow + wifiForeRcvFlow
    }

    fun getGprsFlow(): Long {
        var backFlow = 0L
        var foreFlow = 0L
        if (uid != 0) {
            val flowInfoLines = flowInfo.split("\n")
            for (flowInfoLine in flowInfoLines) {
                if (flowInfoLine.contains("0x0")) {
                    if (flowInfoLine.contains("$uid 0") && flowInfoLine.contains("rmnet0")) {
                        val tmpFlow = flowInfoLine.split("\\s+".toRegex())
                        val backRcvFlow = tmpFlow[5].toLong()
                        val backSndFlow = tmpFlow[7].toLong()
                        backFlow = backRcvFlow + backSndFlow
                    } else if (flowInfoLine.contains("$uid 1") && flowInfoLine.contains("rmnet0")) {
                        val tmpFlow = flowInfoLine.split("\\s+".toRegex())
                        val foreRcvFlow = tmpFlow[5].toLong()
                        val foreSndFlow = tmpFlow[7].toLong()
                        foreFlow = foreRcvFlow + foreSndFlow
                    }
                }
            }
        }
        return backFlow + foreFlow
    }

    fun getGprsSndFlow(): Long {
        var gprsBackSndFlow = 0L
        var gprsForeSndFlow = 0L
        if (uid != 0) {
            val flowInfoLines = flowInfo.split("\n")
            for (flowInfoLine in flowInfoLines) {
                if (flowInfoLine.contains("0x0")) {
                    if (flowInfoLine.contains("$uid 0") && flowInfoLine.contains("rmnet0")) {
                        gprsBackSndFlow = flowInfoLine.split("\\s+".toRegex())[7].toLong()
                    } else if (flowInfoLine.contains("$uid 1") && flowInfoLine.contains("rmnet0")) {
                        gprsForeSndFlow = flowInfoLine.split("\\s+".toRegex())[7].toLong()
                    }
                }
            }
        }
        return gprsBackSndFlow + gprsForeSndFlow
    }

    fun getGprsRcvFlow(): Long {
        var gprsBackRcvFlow = 0L
        var gprsForeRcvFlow = 0L
        if (uid != 0) {
            val flowInfoLines = flowInfo.split("\n")
            for (flowInfoLine in flowInfoLines) {
                if (flowInfoLine.contains("0x0")) {
                    if (flowInfoLine.contains("$uid 0") && flowInfoLine.contains("rmnet0")) {
                        gprsBackRcvFlow = flowInfoLine.split("\\s+".toRegex())[5].toLong()
                    } else if (flowInfoLine.contains("$uid 1") && flowInfoLine.contains("rmnet0")) {
                        gprsForeRcvFlow = flowInfoLine.split("\\s+".toRegex())[5].toLong()
                    }
                }
            }
        }
        return gprsBackRcvFlow + gprsForeRcvFlow
    }

    private fun demo() {
        val mobileRxBytes = TrafficStats.getMobileRxBytes()
        val mobileRxPackets = TrafficStats.getMobileRxPackets()
        val mobileTxBytes = TrafficStats.getMobileTxBytes()
        val mobileTxPackets = TrafficStats.getMobileTxPackets()
        val totalRxBytes = TrafficStats.getTotalRxBytes()
        val totalRxPackets = TrafficStats.getTotalRxPackets()
        val totalTxBytes = TrafficStats.getTotalTxBytes()
        val totalTxPackets = TrafficStats.getTotalTxPackets()
        val uidRxBytes = TrafficStats.getUidRxBytes(uid)
        val uidTxBytes = TrafficStats.getUidTxBytes(uid)
    }

    private fun demo2() {
        try {
            val context = getApplicationContext<Context>()
            val networkStatsManager =
                context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

            val bucket =
                networkStatsManager.querySummaryForDevice(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    0,
                    System.currentTimeMillis(),
                )

            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val subId = tm.subscriberId

            var summaryRx = 0L
            var summaryTx = 0L
            val summaryBucket = NetworkStats.Bucket()
            var summaryTotal = 0L

            val summaryStats =
                networkStatsManager.querySummary(
                    ConnectivityManager.TYPE_MOBILE,
                    subId,
                    getTimesMonthmorning(),
                    System.currentTimeMillis(),
                )
            do {
                summaryStats.getNextBucket(summaryBucket)
                val summaryUid = summaryBucket.uid
                if (uid == summaryUid) {
                    summaryRx += summaryBucket.rxBytes
                    summaryTx += summaryBucket.txBytes
                }
                Log.i(
                    MainActivity::class.java.simpleName,
                    "uid:${summaryBucket.uid} rx:${summaryBucket.rxBytes} tx:${summaryBucket.txBytes}",
                )
                summaryTotal += summaryBucket.rxBytes + summaryBucket.txBytes
            } while (summaryStats.hasNextBucket())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTimesMonthmorning(): Long {
        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_MONTH] = 1
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.timeInMillis
    }

    companion object {
        private var device: UiDevice? = null
        private var uid = 0
        private var flowInfo = ""
    }
}
