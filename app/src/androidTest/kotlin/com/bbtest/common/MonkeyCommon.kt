package com.bbtest.common

import androidx.test.uiautomator.UiObject2
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.FileUtil
import com.bbtest.utils.ShellCommand
import org.junit.After
import org.junit.Before
import java.io.File
import java.util.Collections
import java.util.Random

open class MonkeyCommon : BaseCommon() {
    companion object {
        private const val START_Y = 10
    }

    @Before
    override fun beforeTest() {
        super.beforeTest()
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }

    fun gotoHomeTool(pkgName: String, activity: String, logFile: File): UiObject2? {
        if (isAppBackstage(pkgName, logFile)) {
            if (isProcessExist(pkgName, logFile)) {
                startActivity(1000, activity, logFile)
            } else {
                startActivity(5000, activity, logFile)
            }
        }

        backToHome()

        var tools = waitUiObject2ByText("Tools", TIMEOUT_MEDIUM)
        if (tools == null) {
            tools = waitUiObject2ByText("أدوات", TIMEOUT_VERY_SHORT)
        }
        if (tools == null) {
            var me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM)
            if (me == null) {
                me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT)
            }
            me?.click()
            var settings = waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)
            if (settings == null) {
                settings = waitUiObject2ByText("الإعدادات", TIMEOUT_VERY_SHORT)
            }
            settings?.click()
            var homepage = waitUiObject2ByText("Homepage", TIMEOUT_MEDIUM)
            if (homepage == null) {
                homepage = waitUiObject2ByText("الصفحة الرئيسية", TIMEOUT_VERY_SHORT)
            }
            homepage?.click()
            val homeSwitch = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM.toLong())?.firstOrNull()
            if (homeSwitch?.isChecked == true) {
                homeSwitch.click()
            }
            backToHome()
        }

        val home = waitUiObject2ByText("Home", TIMEOUT_MEDIUM)
        home?.click()
        return home
    }

    fun gotoSscenes(scenes: String, logFile: File) {
        if (scenes == "notification") {
            wakeUp()
            sleep(TIMEOUT_SHORT.toLong())
        } else {
            val cmd = "am start -a android.intent.action.VIEW -p com.transsion.phoenix -d $scenes"
            ShellCommand.execCmdByUiDevice(device, cmd)
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  $cmd\n", logFile)
        }
    }

    fun gotoNovel(pkgName: String, activity: String, logFile: File): UiObject2? {
        if (isAppBackstage(pkgName, logFile)) {
            if (isProcessExist(pkgName, logFile)) {
                startActivity(1000, activity, logFile)
            } else {
                startActivity(5000, activity, logFile)
            }
        }

        backToHome()

        var me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM)
        if (me == null) {
            me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT)
        }
        me?.click()

        val novel = waitUiObject2ByRes("com.transsion.phoenix:id/it_novel_more", TIMEOUT_MEDIUM.toLong())
        novel?.click()
        return novel
    }

    fun backToHome() {
        for (i in 0 until 30) {
            if (waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_SHORT.toLong()) != null) {
                back()
                var sureExit0 = waitUiObject2ByText("Sure to exit now?", TIMEOUT_SHORT)
                if (sureExit0 == null) {
                    sureExit0 = waitUiObject2ByText("تأكيد الخروج الآن ؟", TIMEOUT_VERY_SHORT)
                }
                if (sureExit0 != null) {
                    back()
                    break
                }
                var sureExit1 = waitUiObject2ByText("Do you have any difficulties While using Phoenix Browser？", TIMEOUT_VERY_SHORT)
                if (sureExit1 == null) {
                    sureExit1 = waitUiObject2ByText("هل واجهتط صعوبات أثناء رحلتك مع فينيكس", TIMEOUT_VERY_SHORT)
                }
                if (sureExit1 != null) {
                    back()
                    break
                }
                val clearExit0 = waitUiObject2ByText("Clear history on exit?", TIMEOUT_VERY_SHORT)
                if (clearExit0 != null) {
                    back()
                    break
                }
                val clearExit1 = waitUiObject2ByText("Clear History When Exit ?", TIMEOUT_VERY_SHORT)
                if (clearExit1 != null) {
                    back()
                    break
                }
            } else {
                if (i > 8) {
                    var dialog = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT)
                    if (dialog == null) {
                        dialog = waitUiObject2ByText("موافق", TIMEOUT_VERY_SHORT)
                    }
                    if (dialog != null) {
                        dialog.click()
                        continue
                    }
                    var yesDialog = waitUiObject2ByText("Yes", TIMEOUT_VERY_SHORT)
                    if (yesDialog == null) {
                        yesDialog = waitUiObject2ByText("نعم", TIMEOUT_VERY_SHORT)
                    }
                    if (yesDialog != null) {
                        yesDialog.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var continueDialog = waitUiObject2ByText("Continue", TIMEOUT_VERY_SHORT)
                    if (continueDialog == null) {
                        continueDialog = waitUiObject2ByText("متابعة", TIMEOUT_VERY_SHORT)
                    }
                    if (continueDialog != null) {
                        continueDialog.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var allowDialog = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT)
                    if (allowDialog == null) {
                        allowDialog = waitUiObject2ByText("السماح", TIMEOUT_VERY_SHORT)
                    }
                    if (allowDialog != null) {
                        allowDialog.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var customDialog = waitUiObject2ByText("You can customize your news feeds in just two steps", TIMEOUT_VERY_SHORT)
                    if (customDialog == null) {
                        customDialog = waitUiObject2ByText("يمكنك تخصيص موجز الأخبار الخاص بك فى خطوتين", TIMEOUT_VERY_SHORT)
                    }
                    if (customDialog != null) {
                        getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 1.0, 0.1, 0.5)?.firstOrNull()?.click()
                        continue
                    }
                    var skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/closeButton", TIMEOUT_VERY_SHORT.toLong())
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/close", TIMEOUT_VERY_SHORT.toLong())
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/trybuttom", TIMEOUT_VERY_SHORT.toLong())
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByText("Try", TIMEOUT_VERY_SHORT)
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByText("جَرّبه الآن", TIMEOUT_VERY_SHORT)
                    }
                    if (skipButton != null) {
                        skipButton.click()
                        continue
                    }
                    back()
                } else {
                    back()
                }
            }
        }
    }

    fun skipDialog() {
        val cancel = waitUiObject2ByText("Cancel", TIMEOUT_VERY_SHORT)
        if (cancel != null) {
            cancel.click()
            return
        }
        val ok = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT)
        if (ok != null) {
            ok.click()
            return
        }
        val deny = waitUiObject2ByText("Deny", TIMEOUT_VERY_SHORT)
        if (deny != null) {
            deny.click()
            return
        }
        val accept = waitUiObject2ByText("Accept", TIMEOUT_VERY_SHORT)
        if (accept != null) {
            accept.click()
            return
        }
        val agree = waitUiObject2ByText("AGREE", TIMEOUT_VERY_SHORT)
        if (agree != null) {
            agree.click()
            return
        }
        val allow = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT)
        if (allow != null) {
            allow.click()
        }
    }

    fun initRandomEvent(
        clickRate: Int,
        swipeUpRate: Int,
        swipeDownRate: Int,
        swipeLeftRate: Int,
        swipeRightRate: Int,
        pressBackRate: Int,
        pressHomeRate: Int,
        longClickRate: Int,
        typeRate: Int,
        dragRate: Int,
    ): List<String> {
        val events = ArrayList<String>()
        repeat(clickRate * 2) { events.add("click") }
        repeat(swipeUpRate * 2) { events.add("swipeUp") }
        repeat(swipeDownRate * 2) { events.add("swipeDown") }
        repeat(swipeLeftRate * 2) { events.add("swipeLeft") }
        repeat(swipeRightRate * 2) { events.add("swipeRight") }
        repeat(pressBackRate * 2) { events.add("pressBack") }
        repeat(pressHomeRate * 2) { events.add("pressHome") }
        repeat(longClickRate * 2) { events.add("longClick") }
        repeat(typeRate * 2) { events.add("type") }
        repeat(dragRate * 2) { events.add("drag") }
        Collections.shuffle(events)
        return events
    }

    fun clickByUiObject(logFile: File) {
        var isClickError = false
        try {
            val clickableUiObject2s = getClickableUiObject2s(getRootObject(), ArrayList())
            val clickIndex = Random().nextInt(clickableUiObject2s.size)
            val clickUiObject2s = clickableUiObject2s[clickIndex]
            clickUiObject2s.click()
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  click ${clickUiObject2s.visibleBounds}\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
            isClickError = true
        }
        if (isClickError) {
            click(logFile)
        }
    }

    fun click(logFile: File) {
        val x = width * CommonUtil.randomInt(0, 100) / 100
        val y = height * CommonUtil.randomInt(START_Y, 100) / 100
        try {
            device.click(x, y)
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  click (${x},${y})\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun swipeUp(logFile: File) {
        val x = width * CommonUtil.randomInt(0, 100) / 100
        val fromY = height * CommonUtil.randomInt(60, 100) / 100
        val toY = height * CommonUtil.randomInt(START_Y, 40) / 100
        try {
            device.swipe(x, fromY, x, toY, 10)
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  swipe (${x},${fromY}) up to (${x},${toY})\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun swipeDown(logFile: File) {
        val x = width * CommonUtil.randomInt(0, 100) / 100
        val fromY = height * CommonUtil.randomInt(START_Y, 40) / 100
        val toY = height * CommonUtil.randomInt(60, 100) / 100
        try {
            device.swipe(x, fromY, x, toY, 10)
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  swipe (${x},${fromY}) down to (${x},${toY})\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun swipeLeft(logFile: File) {
        val y = height * CommonUtil.randomInt(START_Y, 100) / 100
        val fromX = width * CommonUtil.randomInt(60, 100) / 100
        val toX = width * CommonUtil.randomInt(0, 40) / 100
        try {
            device.swipe(fromX, y, toX, y, 10)
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  swipe (${fromX},${y}) left to (${toX},${y})\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun swipeRight(logFile: File) {
        val y = height * CommonUtil.randomInt(START_Y, 100) / 100
        val fromX = width * CommonUtil.randomInt(0, 40) / 100
        val toX = width * CommonUtil.randomInt(60, 100) / 100
        try {
            device.swipe(fromX, y, toX, y, 10)
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  swipe (${fromX},${y}) right to (${toX},${y})\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun drag(logFile: File) {
        val fromX = width * CommonUtil.randomInt(0, 100) / 100
        val fromY = height * CommonUtil.randomInt(START_Y, 100) / 100
        val toX = width * CommonUtil.randomInt(0, 100) / 100
        val toY = height * CommonUtil.randomInt(8, 100) / 100
        try {
            device.drag(fromX, fromY, toX, toY, 10)
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  drag (${fromX},${fromY}) to (${toX},${toY})\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun longClick(logFile: File) {
        val x = width * CommonUtil.randomInt(0, 100) / 100
        val y = height * CommonUtil.randomInt(START_Y, 100) / 100
        try {
            device.swipe(x, y, x, y, 300)
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  longClick (${x},${y})\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun type(logFile: File) {
        try {
            ShellCommon.type(device, CommonUtil.randomStr(50), logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pressHome(logFile: File) {
        try {
            device.pressHome()
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  press home\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pressBack(logFile: File) {
        try {
            device.pressBack()
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  press back\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dpadLeft(logFile: File) {
        try {
            device.pressDPadLeft()
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  move the cursor to the left\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dpadRight(logFile: File) {
        try {
            device.pressDPadRight()
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  move the cursor to the right\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun del(logFile: File) {
        try {
            device.pressDelete()
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  del text\n", logFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startActivity(waitMills: Long, activity: String, logFile: File) {
        try {
            ShellCommon.amStartApp(device, activity, logFile)
            Thread.sleep(waitMills)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isAppBackstage(pkgName: String, logFile: File): Boolean {
        val isAppBackstage = ShellCommon.isAppBackstage(device, pkgName)
        FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  isAppBackstage=${isAppBackstage}\n", logFile)
        return isAppBackstage
    }

    fun isProcessExist(pkgName: String, logFile: File): Boolean {
        val isProcessExist = ShellCommon.isProcessExist(device, pkgName)
        FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  isProcessExist=${isProcessExist}\n", logFile)
        return isProcessExist
    }
}
