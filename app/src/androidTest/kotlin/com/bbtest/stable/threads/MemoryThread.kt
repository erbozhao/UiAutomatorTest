package com.bbtest.stable.threads

import androidx.test.uiautomator.UiDevice
import com.bbtest.tools.DeviceInfo
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.FileUtil
import com.bbtest.utils.ShellCommand
import java.io.File

class MemoryThread : Thread {
    private val runTime: Float
    private val pkgName: String
    private val device: UiDevice
    private val resultFile: File
    private var service = ""
    private var serviceFile: File? = null
    private val intervalSecond: Int
    private var isTimeOver = false

    constructor(runTime: Float, pkgName: String, device: UiDevice, resultFile: File, intervalSecond: Int) {
        this.runTime = runTime
        this.pkgName = pkgName
        this.device = device
        this.resultFile = resultFile
        FileUtil.createFile(resultFile)
        this.intervalSecond = intervalSecond
    }

    constructor(
        runTime: Float,
        pkgName: String,
        device: UiDevice,
        model: String,
        resultFolder: File,
        intervalSecond: Int,
        isOpenService: Boolean,
    ) {
        this.runTime = runTime
        this.pkgName = pkgName
        this.device = device
        this.resultFile = File(resultFolder, "Memoinfo_${model}_${pkgName}_${CommonUtil.getCurTimeForFile()}.txt")
        FileUtil.createFile(resultFile)
        if (isOpenService) {
            service = "$pkgName:service"
            serviceFile =
                File(resultFolder, "Memoinfo_${model}_${service.replace(":", ".")}_${CommonUtil.getCurTimeForFile()}.txt")
            serviceFile?.let { FileUtil.createFile(it) }
        }
        this.intervalSecond = intervalSecond
    }

    override fun run() {
        try {
            FileUtil.writeStrToFile(
                "${CommonUtil.getCurTimeForLog()}  ******************start record memory info******************\n",
                resultFile,
            )
            serviceFile?.let {
                FileUtil.writeStrToFile(
                    "${CommonUtil.getCurTimeForLog()}  ******************start record memory info******************\n",
                    it,
                )
            }

            val startTime = System.currentTimeMillis()
            loop@ while (true) {
                dumpMem(pkgName, resultFile)
                serviceFile?.let { dumpMem(service, it) }

                val endTime = System.currentTimeMillis()
                val costTime = endTime - startTime
                if (runTime > 0 && costTime > runTime * 60 * 60 * 1000) {
                    dumpMem(pkgName, resultFile)
                    serviceFile?.let { dumpMem(service, it) }
                    break
                } else if (intervalSecond == -1 || intervalSecond == 0) {
                    for (i in 0 until 8) {
                        if (isTimeOver) {
                            dumpMem(pkgName, resultFile)
                            serviceFile?.let { dumpMem(service, it) }
                            break@loop
                        } else {
                            val deviceInfo = DeviceInfo(device)
                            val mediaVolume = deviceInfo.mediaVolume
                            if (mediaVolume > 0) {
                                deviceInfo.setMediaVolumeZero()
                            }
                        }
                    }
                } else {
                    for (i in 0 until intervalSecond) {
                        if (isTimeOver) {
                            dumpMem(pkgName, resultFile)
                            serviceFile?.let { dumpMem(service, it) }
                            break@loop
                        } else {
                            sleep(1000)
                        }
                    }
                }
            }

            FileUtil.writeStrToFile(
                "${CommonUtil.getCurTimeForLog()}  ******************end record memory info******************\n",
                resultFile,
            )
            serviceFile?.let {
                FileUtil.writeStrToFile(
                    "${CommonUtil.getCurTimeForLog()}  ******************end record memory info******************\n",
                    it,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dumpMem(pkgName: String, memFilePath: File) {
        FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  dumpsys meminfo $pkgName\n", memFilePath)
        val memoInfo = ShellCommand.execCmdByUiDevice(device, "dumpsys meminfo $pkgName")
        val memoInfoLines = memoInfo.split("\n")
        for (memoInfoLine in memoInfoLines) {
            FileUtil.writeStrToFile("$memoInfoLine\n", memFilePath)
        }
        for (i in 0..3) {
            FileUtil.writeStrToFile("\r\n", memFilePath)
        }
    }

    fun setIsTimeOver(isTimeOver: Boolean) {
        this.isTimeOver = isTimeOver
    }
}
