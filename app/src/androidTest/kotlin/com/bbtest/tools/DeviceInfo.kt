package com.bbtest.tools

import androidx.test.uiautomator.UiDevice
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.ShellCommand

class DeviceInfo(private val device: UiDevice) {
    val model: String
        get() {
            var model = ""
            try {
                val result = ShellCommand.execCmdByUiDevice(device, "getprop ro.product.model")
                val resultLines = result.split("\n")
                for (resultLine in resultLines) {
                    if (resultLine.isNotEmpty()) {
                        model = resultLine.trim().replace("\\s".toRegex(), "-")
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return model
        }

    val version: String
        get() {
            var version = ""
            try {
                val result = ShellCommand.execCmdByUiDevice(device, "getprop ro.build.version.release")
                val resultLines = result.split("\n")
                for (resultLine in resultLines) {
                    if (resultLine.isNotEmpty()) {
                        version = resultLine.trim().replace("\\s".toRegex(), "_")
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return version
        }

    val resolution: String
        get() {
            var resolution = ""
            try {
                val result = ShellCommand.execCmdByUiDevice(device, "dumpsys window")
                val resultLines = result.split("\n")
                for (resultLine in resultLines) {
                    if (resultLine.contains("init") && resultLine.contains("cur") && resultLine.contains("app")) {
                        val cur = resultLine.trim().split("\\s+".toRegex())[2].trim()
                        resolution = cur.split("=")[1].trim().replace("\\s".toRegex(), "_")
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return resolution
        }

    val ram: String
        get() {
            var ram = ""
            try {
                val result = ShellCommand.execCmdByUiDevice(device, "cat /proc/meminfo | grep MemTotal")
                val resultLines = result.split("\n")
                for (resultLine in resultLines) {
                    val resultLineParts = resultLine.trim().split("\\s+".toRegex())
                    if (resultLineParts.size > 1) {
                        val tmpRam = resultLineParts[1].trim()
                        if (CommonUtil.isNumer(tmpRam)) {
                            ram = (tmpRam.toLong() / 1024).toString()
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ram
        }

    val sdk: Int
        get() {
            var sdk = 0
            try {
                val result = ShellCommand.execCmdByUiDevice(device, "getprop ro.build.version.sdk")
                val resultLines = result.split("\n")
                for (resultLine in resultLines) {
                    if (resultLine.isNotEmpty() && CommonUtil.isNumer(resultLine.trim())) {
                        sdk = resultLine.trim().toInt()
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return sdk
        }

    fun setMediaVolumeMute() {
        ShellCommand.execCmdByUiDevice(device, "input keyevent 164")
    }

    fun setMediaVolumeZero() {
        when (val sdk = sdk) {
            22 -> ShellCommand.execCmdByUiDevice(device, "service call audio 4 i32 3 i32 0 i32 1")
            in 23..25 -> ShellCommand.execCmdByUiDevice(device, "service call audio 3 i32 3 i32 0 i32 1")
            30 -> ShellCommand.execCmdByUiDevice(device, "service call audio 10 i32 3 i32 0 i32 1")
            else -> ShellCommand.execCmdByUiDevice(device, "media volume --set 0")
        }
    }

    val mediaVolume: Int
        get() {
            var mediaVolume = -1
            when (sdk) {
                22 -> {
                    val result = ShellCommand.execCmdByUiDevice(device, "service call audio 13 i32 3")
                    val resultLines = result.split("\n")
                    for (resultLine in resultLines) {
                        if (resultLine.isNotEmpty() && resultLine.contains("Result")) {
                            val parts = resultLine.trim().split("\\s+".toRegex())
                            mediaVolume = parts[2].trim().toInt(16)
                            break
                        }
                    }
                }

                23 -> {
                    val result = ShellCommand.execCmdByUiDevice(device, "service call audio 9 i32 3")
                    val resultLines = result.split("\n")
                    for (resultLine in resultLines) {
                        if (resultLine.isNotEmpty() && resultLine.contains("Result")) {
                            val parts = resultLine.trim().split("\\s+".toRegex())
                            mediaVolume = parts[2].trim().toInt(16)
                            break
                        }
                    }
                }

                24 -> {
                    val result = ShellCommand.execCmdByUiDevice(device, "service call audio 8 i32 3")
                    val resultLines = result.split("\n")
                    for (resultLine in resultLines) {
                        if (resultLine.isNotEmpty() && resultLine.contains("Result")) {
                            val parts = resultLine.trim().split("\\s+".toRegex())
                            mediaVolume = parts[2].trim().toInt(16)
                            break
                        }
                    }
                }

                30 -> {
                    val result = ShellCommand.execCmdByUiDevice(device, "service call audio 16 i32 3")
                    val resultLines = result.split("\n")
                    for (resultLine in resultLines) {
                        if (resultLine.isNotEmpty() && resultLine.contains("Result")) {
                            val parts = resultLine.trim().split("\\s+".toRegex())
                            mediaVolume = parts[2].trim().toInt(16)
                            break
                        }
                    }
                }

                else -> {
                    val result = ShellCommand.execCmdByUiDevice(device, "media volume --get")
                    val resultLines = result.split("\n")
                    for (resultLine in resultLines) {
                        if (resultLine.isNotEmpty() && resultLine.contains("volume") && resultLine.contains("in range")) {
                            val parts = resultLine.trim().split("\\s+".toRegex())
                            val tmpValue = parts[3].trim()
                            if (CommonUtil.isNumer(tmpValue)) {
                                mediaVolume = tmpValue.toInt()
                            }
                            break
                        }
                    }
                }
            }
            return mediaVolume
        }

    fun isRoot(): Boolean {
        var isRoot = false
        val result = ShellCommand.execCmdBySh("su -v")
        val resultLines = result.split("\n")
        for (resultLine in resultLines) {
            if (resultLine.isNotEmpty()) {
                isRoot = true
                break
            }
        }
        return isRoot
    }

    fun unlockScreen(width: Int, height: Int) {
        try {
            ShellCommand.execCmdByUiDevice(device, "input keyevent 224")
            Thread.sleep(5000)
            ShellCommand.execCmdByUiDevice(device, "input keyevent 3")
            Thread.sleep(5000)
            ShellCommand.execCmdByUiDevice(device, "input keyevent 82")
            Thread.sleep(5000)
            ShellCommand.execCmdByUiDevice(device, "input swipe ${width / 2} ${height * 4 / 5} ${width / 2} ${height * 1 / 5}")
            Thread.sleep(5000)
            ShellCommand.execCmdByUiDevice(device, "input swipe ${width * 4 / 5} ${height / 2} ${width * 1 / 5} ${height / 2}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun backToHomeScreen() {
        try {
            ShellCommand.execCmdByUiDevice(device, "input keyevent 4")
            Thread.sleep(5000)
            ShellCommand.execCmdByUiDevice(device, "input keyevent 3")
            Thread.sleep(5000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
