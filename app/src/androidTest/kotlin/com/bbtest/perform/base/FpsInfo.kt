package com.bbtest.perform.base

import androidx.test.uiautomator.UiDevice
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.ShellCommand
import java.util.ArrayList
import java.util.regex.Pattern
import kotlin.math.ceil
import kotlin.math.floor

class FpsInfo {
    private val device: UiDevice
    private val pkgName: String
    private var topActivity = ""

    private var totalFrames = 0L
    private var jankFrames = 0L
    private var extraJankTimes = 0
    private var fps = 0.0
    private var lostFrameRate = 0.0

    constructor(device: UiDevice, pkgName: String) {
        this.device = device
        this.pkgName = pkgName
        getFpsByGfxinfo()
    }

    constructor(device: UiDevice, pkgName: String, topActivity: String) {
        this.device = device
        this.pkgName = pkgName
        this.topActivity = topActivity
    }

    fun getFps(): Double = CommonUtil.keepDecimalPoint(fps, 2)

    fun getJankFrames(): Long = jankFrames

    fun getLostFrameRate(): Double = CommonUtil.keepDecimalPoint(lostFrameRate, 2)

    private fun clearBfr() {
        ShellCommand.execCmdByUiDevice(device, "dumpsys SurfaceFlinger --latency-clear")
    }

    private fun getFpsBySurfaceFlinger() {
        val frameData = ShellCommand.execCmdByUiDevice(device, "dumpsys SurfaceFlinger --latency $topActivity")
        val frameLines = frameData.split("\n")
        val frameCount = frameLines.size - 1
        val startTime = frameLines[1].trim().split("\\s+".toRegex())[0].trim().toLong()
        val endTime = frameLines[frameLines.size - 1].trim().split("\\s+".toRegex())[0].trim().toLong()
        fps = frameCount * 1000 / ((endTime - startTime) / 1000000.0)

        val refreshPeriod = frameLines[0].trim().toLong()
        val firstDiffValues = ArrayList<Double>()
        var firstUpTime = 0L
        for (i in 1 until frameLines.size) {
            val framePart = frameLines[i].split("\\s+".toRegex())
            if (i == 1) {
                firstUpTime = framePart[1].trim().toLong()
            } else {
                val firstCurTime = framePart[1].trim().toLong()
                val diffValue = (firstCurTime - firstUpTime).toDouble() / refreshPeriod
                if (diffValue > 0.5) {
                    firstDiffValues.add(diffValue)
                }
                firstUpTime = firstCurTime
            }
        }

        var secondUpTime = 0.0
        for (i in 1 until firstDiffValues.size) {
            if (i == 1) {
                secondUpTime = firstDiffValues[i]
            } else {
                val secondCurTime = firstDiffValues[i]
                val jankiness = ceil((secondCurTime - secondUpTime) / refreshPeriod).toInt()
                if (jankiness < 0 || jankiness >= 20) {
                    jankFrames++
                }
                secondUpTime = secondCurTime
            }
        }
        lostFrameRate = jankFrames.toDouble() / totalFrames
    }

    private fun getFpsByGfxinfo() {
        val frameData = ShellCommand.execCmdByUiDevice(device, "dumpsys gfxinfo $pkgName reset")
        val frameLines = frameData.split("\n")
        var isStart = false
        val startKeywords1 = ".*Draw\\s+Prepare\\s+Process\\s+Execute.*"
        val startKeywords2 = ".*Draw\\s+Process\\s+Execute.*"
        val endKeywords1 = ".*Stats\\s+since"
        val endKeywords2 = ".*View hierarchy:.*"
        val endKeywords3 = ".*$pkgName.*"
        for (frameLine in frameLines) {
            if (frameLine.isNotEmpty()) {
                if (Pattern.compile(endKeywords1).matcher(frameLine).find() ||
                    Pattern.compile(endKeywords2).matcher(frameLine).find() ||
                    Pattern.compile(endKeywords3).matcher(frameLine).find()
                ) {
                    isStart = false
                }

                if (isStart) {
                    val framePart = frameLine.trim().split("\\s+".toRegex())
                    totalFrames += 1
                    val onceRenderTime =
                        if (framePart.size == 3) {
                            framePart[0].toFloat() + framePart[1].toFloat() + framePart[2].toFloat()
                        } else {
                            framePart[0].toFloat() + framePart[1].toFloat() + framePart[2].toFloat() + framePart[3].toFloat()
                        }
                    if (onceRenderTime > 16.67) {
                        jankFrames += 1
                        extraJankTimes += if (onceRenderTime % 16.67 == 0.0) {
                            (onceRenderTime / 16.67 - 1).toInt()
                        } else {
                            floor(onceRenderTime / 16.67).toInt()
                        }
                    }
                }

                if (Pattern.compile(startKeywords1).matcher(frameLine).find() ||
                    Pattern.compile(startKeywords2).matcher(frameLine).find()
                ) {
                    isStart = true
                }
            }
        }

        if (totalFrames > 0) {
            fps = totalFrames.toDouble() / (totalFrames + extraJankTimes) * 60
            lostFrameRate = jankFrames.toDouble() / totalFrames
        } else {
            fps = 60.0
        }
    }
}
