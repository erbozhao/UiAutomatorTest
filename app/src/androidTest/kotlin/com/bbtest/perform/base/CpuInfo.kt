package com.bbtest.perform.base

import androidx.test.uiautomator.UiDevice
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.ShellCommand
import java.util.ArrayList
import java.util.regex.Pattern

class CpuInfo(device: UiDevice, pid: Int) {
    init {
        Companion.device = device
        Companion.pid = pid
    }

    fun getCpuName(): String {
        var cpuName = ""
        try {
            val uiDevice = requireNotNull(device) { "UiDevice is not initialized" }
            val result = ShellCommand.execCmdByUiDevice(uiDevice, "cat /proc/cpuinfo")
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine.isNotEmpty()) {
                    val resultLineParts = resultLine.trim().split(":")
                    if (resultLineParts[0].trim() == "Hardware") {
                        cpuName = resultLineParts[1].trim()
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cpuName
    }

    fun getCpuNum(): Int {
        var cpuNum = 0
        try {
            val keywords = "cpu[0-9]"
            val uiDevice = requireNotNull(device) { "UiDevice is not initialized" }
            val result = ShellCommand.execCmdByUiDevice(uiDevice, "ls /sys/devices/system/cpu/")
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine.isNotEmpty() && Pattern.compile(keywords).matcher(resultLine).find()) {
                    cpuNum++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cpuNum
    }

    fun getTotalCpu(): Double {
        var totalCpu = 0.0
        try {
            val cpuTime1 = getTotalCpuInfo()
            var totalCpuTime1 = 0L
            for (tmpCpuTime1 in cpuTime1) {
                totalCpuTime1 += tmpCpuTime1
            }
            val idleCpuTime1 = cpuTime1[3]

            Thread.sleep(1000)

            val cpuTime2 = getTotalCpuInfo()
            var totalCpuTime2 = 0L
            for (tmpCpuTime2 in cpuTime2) {
                totalCpuTime2 += tmpCpuTime2
            }
            val idleCpuTime2 = cpuTime2[3]

            val totalCpuTime = totalCpuTime2 - totalCpuTime1
            val idleCpuTime = idleCpuTime2 - idleCpuTime1
            val tempTotalCpu = (totalCpuTime - idleCpuTime).toDouble() * 100 / totalCpuTime
            totalCpu = CommonUtil.keepDecimalPoint(tempTotalCpu, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return totalCpu
    }

    fun getProCpu(): Double {
        var processCpu = 0.0
        try {
            val cpuTime1 = getTotalCpuInfo()
            val proCpuTime1 = getProCpuInfo()
            var totalCpuTime1 = 0L
            for (tmpCpuTime1 in cpuTime1) {
                totalCpuTime1 += tmpCpuTime1
            }
            var totalProCpuTime1 = 0L
            for (tmpProCpuTime1 in proCpuTime1) {
                totalProCpuTime1 += tmpProCpuTime1
            }

            Thread.sleep(1000)

            val cpuTime2 = getTotalCpuInfo()
            val proCpuTime2 = getProCpuInfo()
            var totalCpuTime2 = 0L
            for (tmpCpuTime2 in cpuTime2) {
                totalCpuTime2 += tmpCpuTime2
            }
            var totalProCpuTime2 = 0L
            for (tmpProCpuTime2 in proCpuTime2) {
                totalProCpuTime2 += tmpProCpuTime2
            }

            val totalCpuTime = totalCpuTime2 - totalCpuTime1
            val proCpuTime = totalProCpuTime2 - totalProCpuTime1
            val tempProCpu = proCpuTime.toDouble() * 100 / totalCpuTime
            processCpu = CommonUtil.keepDecimalPoint(tempProCpu, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return processCpu
    }

    companion object {
        private var device: UiDevice? = null
        private var pid = 0

        private fun getTotalCpuInfo(): List<Long> {
            val cpuInfo = ArrayList<Long>()
            try {
                val uiDevice = requireNotNull(device) { "UiDevice is not initialized" }
                val result = ShellCommand.execCmdByUiDevice(uiDevice, "cat /proc/stat")
                val resultLines = result.split("\n")
                for (resultLine in resultLines) {
                    if (resultLine.isNotEmpty() && resultLine.trim().startsWith("cpu ")) {
                        val temp = resultLine.split("\\s+".toRegex())
                        cpuInfo.add(temp[1].toLong())
                        cpuInfo.add(temp[2].toLong())
                        cpuInfo.add(temp[3].toLong())
                        cpuInfo.add(temp[4].toLong())
                        cpuInfo.add(temp[5].toLong())
                        cpuInfo.add(temp[6].toLong())
                        cpuInfo.add(temp[7].toLong())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return cpuInfo
        }

        private fun getProCpuInfo(): List<Long> {
            val cpuInfo = ArrayList<Long>()
            try {
                val uiDevice = requireNotNull(device) { "UiDevice is not initialized" }
                val result = ShellCommand.execCmdByUiDevice(uiDevice, "cat /proc/$pid/stat")
                val resultLines = result.split("\n")
                for (resultLine in resultLines) {
                    if (resultLine.isNotEmpty()) {
                        val temp = resultLine.split("\\s+".toRegex())
                        cpuInfo.add(temp[13].toLong())
                        cpuInfo.add(temp[14].toLong())
                        cpuInfo.add(temp[15].toLong())
                        cpuInfo.add(temp[16].toLong())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return cpuInfo
        }
    }
}
