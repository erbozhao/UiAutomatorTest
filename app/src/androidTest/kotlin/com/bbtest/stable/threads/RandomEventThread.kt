package com.bbtest.stable.threads

import androidx.test.uiautomator.UiDevice
import com.bbtest.common.ShellCommon
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.FileUtil
import java.io.File
import java.util.Collections
import java.util.Random

class RandomEventThread(
    private val runTime: Float,
    private val pkgName: String,
    private val activity: String,
    private val device: UiDevice,
    private val width: Int,
    private val height: Int,
    private val runLog: File?,
) : Thread() {
    private var isCrash = false
    private var isAnr = false
    private var topActivity = ""

    override fun run() {
        try {
            val events = mutableListOf<String>()
            repeat(30 * 2) { events.add("click") }
            repeat(25) { events.add("swipeUp") }
            repeat(25) { events.add("swipeDown") }
            repeat(25) { events.add("swipeLeft") }
            repeat(25) { events.add("swipeRight") }
            repeat(10 * 2) { events.add("pressBack") }
            repeat(4 * 2) { events.add("pressHome") }
            repeat(3 * 2) { events.add("longClick") }
            repeat(2 * 2) { events.add("type") }
            repeat(2) { events.add("drag") }
            Collections.shuffle(events)

            ShellCommon.killAllApp(device, runLog)
            startActivity(30 * 1000L)
            topActivity = ShellCommon.getTopActivity(device, runLog)

            val startTime = System.currentTimeMillis()
            while (true) {
                if (isAnr || isCrash) {
                    stopApp(3000)
                    isAnr = false
                    isCrash = false
                }

                if (isAppBackstage()) {
                    if (isProcessExist()) {
                        startActivity(1000)
                    } else {
                        startActivity(5000)
                    }
                    for (i in 0 until 3) {
                        if (isAppBackstage()) {
                            pressBack()
                        } else {
                            break
                        }
                    }
                }

                when (events[Random().nextInt(events.size)]) {
                    "click" -> click()
                    "swipeUp" -> swipeUp()
                    "swipeDown" -> swipeDown()
                    "swipeLeft" -> swipeLeft()
                    "swipeRight" -> swipeRight()
                    "drag" -> drag()
                    "longClick" -> longClick()
                    "type" -> {
                        type()
                        dpadLeft()
                        dpadRight()
                        del()
                    }
                    "pressHome" -> {
                        pressHome()
                        startActivity(1000)
                    }
                    "pressBack" -> pressBack()
                }

                val costTime = System.currentTimeMillis() - startTime
                if (costTime > runTime * 60 * 60 * 1000) {
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun click() {
        val x = width * CommonUtil.randomInt(0, 100) / 100
        val y = height * CommonUtil.randomInt(8, 100) / 100
        try {
            device.click(x, y)
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  click (${x},${y})\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun swipeUp() {
        val x = width * CommonUtil.randomInt(0, 100) / 100
        val fromY = height * CommonUtil.randomInt(60, 100) / 100
        val toY = height * CommonUtil.randomInt(8, 40) / 100
        try {
            device.swipe(x, fromY, x, toY, 10)
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  swipe (${x},${fromY}) up to (${x},${toY})\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun swipeDown() {
        val x = width * CommonUtil.randomInt(0, 100) / 100
        val fromY = height * CommonUtil.randomInt(8, 40) / 100
        val toY = height * CommonUtil.randomInt(60, 100) / 100
        try {
            device.swipe(x, fromY, x, toY, 10)
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  swipe (${x},${fromY}) down to (${x},${toY})\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun swipeLeft() {
        val y = height * CommonUtil.randomInt(8, 100) / 100
        val fromX = width * CommonUtil.randomInt(60, 100) / 100
        val toX = width * CommonUtil.randomInt(0, 40) / 100
        try {
            device.swipe(fromX, y, toX, y, 10)
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  swipe (${fromX},${y}) left to (${toX},${y})\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun swipeRight() {
        val y = height * CommonUtil.randomInt(8, 100) / 100
        val fromX = width * CommonUtil.randomInt(0, 40) / 100
        val toX = width * CommonUtil.randomInt(60, 100) / 100
        try {
            device.swipe(fromX, y, toX, y, 10)
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  swipe (${fromX},${y}) right to (${toX},${y})\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun drag() {
        val fromX = width * CommonUtil.randomInt(0, 100) / 100
        val fromY = height * CommonUtil.randomInt(8, 100) / 100
        val toX = width * CommonUtil.randomInt(0, 100) / 100
        val toY = height * CommonUtil.randomInt(8, 100) / 100
        try {
            device.drag(fromX, fromY, toX, toY, 10)
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  drag (${fromX},${fromY}) to (${toX},${toY})\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun longClick() {
        val x = width * CommonUtil.randomInt(0, 100) / 100
        val y = height * CommonUtil.randomInt(8, 100) / 100
        try {
            device.swipe(x, y, x, y, 300)
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  longClick (${x},${y})\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun type() {
        try {
            ShellCommon.type(device, CommonUtil.randomStr(50), runLog)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pressHome() {
        try {
            device.pressHome()
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  press home\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pressBack() {
        try {
            device.pressBack()
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  press back\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dpadLeft() {
        try {
            device.pressDPadLeft()
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  move the cursor to the left\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dpadRight() {
        try {
            device.pressDPadRight()
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  move the cursor to the right\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun del() {
        try {
            device.pressDelete()
            runLog?.let { FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  del text\n", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startActivity(waitMills: Long) {
        try {
            ShellCommon.amStartApp(device, activity, runLog)
            sleep(waitMills)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopApp(waitMills: Long) {
        try {
            ShellCommon.forceStopApp(device, pkgName, runLog)
            sleep(waitMills)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isAppBackstage(): Boolean = ShellCommon.isAppBackstage(device, pkgName)

    private fun isProcessExist(): Boolean = ShellCommon.isProcessExist(device, pkgName)

    fun setIsCrash(isCrash: Boolean) {
        this.isCrash = isCrash
    }

    fun setIsAnr(isAnr: Boolean) {
        this.isAnr = isAnr
    }
}
