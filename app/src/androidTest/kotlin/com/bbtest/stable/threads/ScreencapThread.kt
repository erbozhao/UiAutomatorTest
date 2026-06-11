package com.bbtest.stable.threads

import androidx.test.uiautomator.UiDevice
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.FileUtil
import com.bbtest.utils.ShellCommand
import java.io.File

class ScreencapThread(
    private val runTime: Float,
    private val device: UiDevice,
    private val model: String,
    rootFolder: String,
    resultFolder: String,
) : Thread() {
    private val tmpFolder = File(rootFolder, "img")
    private val resultFolder = File(resultFolder, "img")
    private var isTimeOver = false
    private var isCrash = false

    override fun run() {
        try {
            FileUtil.deleteFolder(tmpFolder)
            FileUtil.createFolder(tmpFolder)
            FileUtil.deleteFolder(resultFolder)
            FileUtil.createFolder(resultFolder)

            val startTime = System.currentTimeMillis()
            var currentStartTime = startTime
            while (true) {
                var imgFile = ""
                if (isCrash) {
                    imgFile = "Img_${model}_${CommonUtil.getCurTimeForFile()}.png"
                    screencap(imgFile)
                    copyImg(imgFile)
                    isCrash = false
                }

                if (imgFile.isNotEmpty()) {
                    delImg(imgFile)
                }

                val endTime = System.currentTimeMillis()
                val costTime = endTime - startTime
                val currentCostTime = endTime - currentStartTime
                if (runTime > 0 && costTime > runTime * 60 * 60 * 1000 - 10 * 1000) {
                    break
                } else if (isTimeOver) {
                    break
                } else if (currentCostTime > 60 * 1000) {
                    currentStartTime = endTime
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun screencap(imgFile: String) {
        val tmpImgPath = "$tmpFolder/$imgFile"
        ShellCommand.execCmdByUiDevice(device, "screencap $tmpImgPath")
    }

    private fun copyImg(imgFile: String) {
        val srcImgPath = "$tmpFolder/$imgFile"
        val dstImgPath = "$resultFolder/$imgFile"
        ShellCommand.execCmdByUiDevice(device, "cp $srcImgPath $dstImgPath")
    }

    private fun delImg(imgFile: String) {
        val tmpImgPath = "$tmpFolder/$imgFile"
        ShellCommand.execCmdByUiDevice(device, "rm -rf $tmpImgPath")
    }

    fun setIsTimeOver(isTimeOver: Boolean) {
        this.isTimeOver = isTimeOver
    }

    fun setIsCrash(isCrash: Boolean) {
        this.isCrash = isCrash
    }
}
