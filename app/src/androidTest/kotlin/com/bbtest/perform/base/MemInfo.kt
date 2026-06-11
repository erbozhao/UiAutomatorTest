package com.bbtest.perform.base

import androidx.test.uiautomator.UiDevice
import com.bbtest.utils.ShellCommand

class MemInfo(
    private val device: UiDevice,
    private val pkgName: String,
) {
    private var totalMem = 0L
    private var availabelMem = 0L
    private var totalPss = 0L
    private var dalvilkPss = 0L
    private var nativePss = 0L
    private var views = 0L
    private var webviews = 0L
    private var activities = 0L

    init {
        readMeminfo()
        readRaminfo()
    }

    fun getTotalMem(): Long = totalMem

    fun getAvailabelMem(): Long = availabelMem

    fun getTotalPss(): Long = totalPss

    fun getDalvilkPss(): Long = dalvilkPss

    fun getNativePss(): Long = nativePss

    fun getViews(): Long = views

    fun getWebviews(): Long = webviews

    fun getActivities(): Long = activities

    private fun readMeminfo() {
        val memInfo = ShellCommand.execCmdByUiDevice(device, "dumpsys meminfo $pkgName")
        val memInfoLines = memInfo.split("\n")
        for (line in memInfoLines) {
            val memInfoLine = line.trim()
            if (memInfoLine.startsWith("TOTAL") &&
                !memInfoLine.contains("Size") &&
                !memInfoLine.contains("Free") &&
                !memInfoLine.contains("PSS")
            ) {
                totalPss = memInfoLine.split("\\s+".toRegex())[1].trim().toLong()
            } else if (memInfoLine.startsWith("Dalvik") && memInfoLine.contains("Heap") && !memInfoLine.contains(":")) {
                dalvilkPss = memInfoLine.split("\\s+".toRegex())[2].trim().toLong()
            } else if (memInfoLine.startsWith("Native") && memInfoLine.contains("Heap") && !memInfoLine.contains(":")) {
                nativePss = memInfoLine.split("\\s+".toRegex())[2].trim().toLong()
            } else if (memInfoLine.startsWith("Views:")) {
                views = memInfoLine.split("\\s+".toRegex())[1].trim().toLong()
            } else if (memInfoLine.startsWith("WebViews:")) {
                webviews = memInfoLine.split("\\s+".toRegex())[1].trim().toLong()
            } else if (memInfoLine.startsWith("AppContexts:") && memInfoLine.contains("Activities:")) {
                activities = memInfoLine.split("\\s+".toRegex())[3].trim().toLong()
            }
        }
    }

    private fun readRaminfo() {
        val ramInfo = ShellCommand.execCmdByUiDevice(device, "cat /proc/meminfo")
        val ramInfoLines = ramInfo.split("\n")
        for (line in ramInfoLines) {
            val ramInfoLine = line.trim()
            if (ramInfoLine.startsWith("MemTotal:")) {
                totalMem = ramInfoLine.split("\\s+".toRegex())[1].trim().toLong()
            } else if (ramInfoLine.startsWith("MemAvailable:") || ramInfoLine.startsWith("MemFree:")) {
                availabelMem = ramInfoLine.split("\\s+".toRegex())[1].trim().toLong()
            }
        }
    }
}
