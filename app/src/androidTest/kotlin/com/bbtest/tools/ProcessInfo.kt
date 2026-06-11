package com.bbtest.tools

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.uiautomator.UiDevice
import com.bbtest.utils.ShellCommand

class ProcessInfo(
    private val device: UiDevice,
    private val pkgName: String,
) {
    val pid: Int
        get() {
            var pid = 0
            try {
                val result = ShellCommand.execCmdByUiDevice(device, "ps -ef")
                val resultLines = result.split("\n")
                for (resultLine in resultLines) {
                    if (resultLine.isNotEmpty() && resultLine.contains(pkgName)) {
                        val parts = resultLine.trim().split("\\s+".toRegex())
                        if (parts[parts.size - 1].trim() == pkgName) {
                            pid = parts[1].trim().toInt()
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return pid
        }

    fun getUid(pid: Int): Int {
        var uid = 0
        try {
            val result = ShellCommand.execCmdByUiDevice(device, "cat /proc/$pid/status")
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine.isNotEmpty() && resultLine.contains("Uid:")) {
                    uid = resultLine.split("\\s+".toRegex())[1].trim().toInt()
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uid
    }

    fun getUid(): Int {
        var uid = -1
        val context: Context = getApplicationContext()
        val packageManager = context.packageManager
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(pkgName, PackageManager.GET_META_DATA)
            uid = packageInfo.applicationInfo?.uid ?: -1
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uid
    }

    fun getSubProcess(): List<String> {
        val subProcess = ArrayList<String>()
        try {
            val result = ShellCommand.execCmdByUiDevice(device, "ps -ef")
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine.isNotEmpty() && resultLine.contains(pkgName)) {
                    val parts = resultLine.trim().split("\\s+".toRegex())
                    if (parts[parts.size - 1].trim() != pkgName) {
                        subProcess.add(parts[parts.size - 1].trim())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return subProcess
    }
}
