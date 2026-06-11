package com.bbtest.stable.threads

import androidx.test.uiautomator.UiDevice
import com.bbtest.tools.ProcessInfo
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.FileUtil
import com.bbtest.utils.ShellCommand
import java.io.File

class NativeFdThread(
    private val runTime: Float,
    private val pkgName: String,
    private val device: UiDevice,
    private val model: String,
    private val resultFolder: String,
    private val screenrecordThread: ScreenrecordThread,
) : Thread() {
    private var isTimeOver = false
    private var isCrash = false

    override fun run() {
        try {
            val fdFile = File(resultFolder, "Fd_${model}_${CommonUtil.getCurTimeForFile()}.log")
            FileUtil.createFile(fdFile)

            val startTime = System.currentTimeMillis()
            var appThreshold = 300
            var appServiceThreshold = 200
            while (true) {
                if (isCrash) {
                    appThreshold = 300
                    appServiceThreshold = 200
                    isCrash = false
                }

                val pid = ProcessInfo(device, pkgName).pid
                val fdNum = getFdNum(pid)
                FileUtil.writeStrToFile(
                    "${CommonUtil.getCurTimeForLog()}  pkgName=$pkgName,pid=$pid,fdNum=$fdNum\n",
                    fdFile,
                )
                if (fdNum > appThreshold) {
                    getFdInfo(pid, fdFile)
                    screenrecordThread.setFdNum(fdNum)
                    appThreshold += 100
                }

                val servicePid = ProcessInfo(device, "$pkgName:service").pid
                val serviceFdNum = getFdNum(servicePid)
                FileUtil.writeStrToFile(
                    "${CommonUtil.getCurTimeForLog()}  pkgName=$pkgName:service,pid=$servicePid,fdNum=$serviceFdNum\n",
                    fdFile,
                )
                if (serviceFdNum > appServiceThreshold) {
                    getFdInfo(pid, fdFile)
                    screenrecordThread.setFdNum(fdNum)
                    appServiceThreshold += 100
                }

                val endTime = System.currentTimeMillis()
                val costTime = endTime - startTime
                if (runTime > 0 && costTime > runTime * 60 * 60 * 1000) {
                    break
                } else if (isTimeOver) {
                    break
                } else {
                    sleep(60 * 1000)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFdNum(pid: Int): Int {
        var fdNum = 0
        val result = ShellCommand.execCmdBySu("ls /proc/$pid/fd")
        val resultLines = result.split("\\n|\\s+".toRegex())
        for (resultLine in resultLines) {
            if (resultLine.trim().isNotEmpty()) {
                fdNum++
            }
        }
        return fdNum
    }

    private fun getFdInfo(pid: Int, filePath: File) {
        val result = ShellCommand.execCmdBySu("ls -al /proc/$pid/fd")
        val resultLines = result.split("\n")
        for (resultLine in resultLines) {
            if (resultLine.isNotEmpty()) {
                FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", filePath)
            }
        }
    }

    fun setIsTimeOver(isTimeOver: Boolean) {
        this.isTimeOver = isTimeOver
    }

    fun setIsCrash(isCrash: Boolean) {
        this.isCrash = isCrash
    }
}
