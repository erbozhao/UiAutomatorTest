package com.bbtest.stable.threads

import androidx.test.uiautomator.UiDevice
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.ShellCommand
import java.io.File

class DumpheapThread : Thread {
    private var runTime = -1f
    private val pkgName: String
    private val device: UiDevice
    private val model: String
    private val resultFolder: File

    private var endImgFile = ""
    private var endHprofFile = ""
    private var isTimeOver = false

    constructor(pkgName: String, device: UiDevice, model: String, resultFolder: File) {
        this.pkgName = pkgName
        this.device = device
        this.model = model
        this.resultFolder = resultFolder
    }

    constructor(runTime: Float, pkgName: String, device: UiDevice, model: String, resultFolder: File) {
        this.runTime = runTime
        this.pkgName = pkgName
        this.device = device
        this.model = model
        this.resultFolder = resultFolder
    }

    override fun run() {
        try {
            empty()
            openDumpheap()

            if (runTime <= 0) {
                exportHeapSnapshot()
            } else {
                val startTime = System.currentTimeMillis()
                var currentStartTime = startTime
                while (true) {
                    val endTime = System.currentTimeMillis()
                    val costTime = endTime - startTime
                    val currentCostTime = endTime - currentStartTime
                    if (runTime > 0 && costTime > runTime * 60 * 60 * 1000) {
                        exportHeapSnapshot()
                        break
                    } else if (isTimeOver) {
                        exportHeapSnapshot()
                        break
                    } else {
                        if (currentCostTime > 1 * 60 * 60 * 1000) {
                            exportHeapSnapshot()
                            currentStartTime = endTime
                        }
                        sleep(1000)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun exportHeapSnapshot() {
        endImgFile = "Img_${model}_${pkgName.replace(":", ".")}_${CommonUtil.getCurTimeForFile()}.png"
        screencap(endImgFile)
        copy(endImgFile)
        del(endImgFile)
        endHprofFile = "Hprof_${model}_${pkgName}_${CommonUtil.getCurTimeForFile()}.hprof"
        dumpheap(endHprofFile)
        copy(endHprofFile)
        del(endHprofFile)
    }

    private fun empty() {
        ShellCommand.execCmdByUiDevice(device, "rm -rf /data/local/tmp/*")
    }

    private fun openDumpheap() {
        ShellCommand.execCmdByUiDevice(device, "dumpsys activity log dumpheap on")
    }

    private fun dumpheap(fileName: String) {
        ShellCommand.execCmdByUiDevice(device, "am dumpheap $pkgName /data/local/tmp/$fileName")
    }

    private fun screencap(imgFile: String) {
        ShellCommand.execCmdByUiDevice(device, "screencap /data/local/tmp/$imgFile")
    }

    private fun copy(fileName: String) {
        ShellCommand.execCmdByUiDevice(device, "cp /data/local/tmp/$fileName $resultFolder")
    }

    private fun del(fileName: String) {
        ShellCommand.execCmdByUiDevice(device, "rm -rf /data/local/tmp/$fileName")
    }

    fun setIsTimeOver(isTimeOver: Boolean) {
        this.isTimeOver = isTimeOver
    }
}
