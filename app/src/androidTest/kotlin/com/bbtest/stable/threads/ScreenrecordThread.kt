package com.bbtest.stable.threads

import androidx.test.uiautomator.UiDevice
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.FileUtil
import com.bbtest.utils.ShellCommand
import java.io.File

class ScreenrecordThread(
    private val runTime: Float,
    private val device: UiDevice,
    private val model: String,
    rootFolder: String,
    resultFolder: String,
) : Thread() {
    private val tmpFolder = File(rootFolder, "video")
    private val resultFolder = File(resultFolder, "video")
    private val fdFolder = File(resultFolder, "fdvideo")

    private var isTimeOver = false
    private var isCrash = false
    private var fdNum = -1
    private var videoFile = ""

    override fun run() {
        try {
            FileUtil.deleteFolder(tmpFolder)
            FileUtil.createFolder(tmpFolder)
            FileUtil.deleteFolder(resultFolder)
            FileUtil.createFolder(resultFolder)
            FileUtil.deleteFolder(fdFolder)
            FileUtil.createFolder(fdFolder)

            val startTime = System.currentTimeMillis()
            while (true) {
                videoFile = "Video_${model}_${CommonUtil.getCurTimeForFile()}.mp4"
                screenrecord(videoFile)

                if (fdNum != -1) {
                    copyFdVideo(videoFile, fdNum)
                    fdNum = -1
                }

                if (isCrash) {
                    copyVideo(videoFile)
                    isCrash = false
                }
                delVideo(videoFile)

                val endTime = System.currentTimeMillis()
                val costTime = endTime - startTime
                if (runTime > 0 && costTime > runTime * 60 * 60 * 1000 - 10 * 1000) {
                    break
                } else if (isTimeOver) {
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun screenrecord(videoFile: String) {
        val tmpVideoPath = "$tmpFolder/$videoFile"
        ShellCommand.execCmdByUiDevice(device, "screenrecord --size 480x800 --time-limit 180 $tmpVideoPath")
    }

    private fun copyVideo(videoFile: String) {
        val srcVideoPath = "$tmpFolder/$videoFile"
        val dstVideoPath = "$resultFolder/$videoFile"
        ShellCommand.execCmdByUiDevice(device, "cp $srcVideoPath $dstVideoPath")
    }

    private fun copyFdVideo(videoFile: String, fdNum: Int) {
        val srcVideoPath = "$tmpFolder/$videoFile"
        val dstVideoPath =
            "$fdFolder/${videoFile.substring(0, videoFile.lastIndexOf(".")).trim()}_${fdNum}.mp4"
        ShellCommand.execCmdByUiDevice(device, "cp $srcVideoPath $dstVideoPath")
    }

    private fun delVideo(videoFile: String) {
        val tmpVideoPath = "$tmpFolder/$videoFile"
        ShellCommand.execCmdByUiDevice(device, "rm -rf $tmpVideoPath")
    }

    fun setIsTimeOver(isTimeOver: Boolean) {
        this.isTimeOver = isTimeOver
    }

    fun setIsCrash(isCrash: Boolean) {
        this.isCrash = isCrash
    }

    fun setFdNum(fdNum: Int) {
        this.fdNum = fdNum
    }
}
