package com.bbtest.perform.press

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.uiautomator.UiObject2
import com.bbtest.common.PhxCommon
import com.bbtest.common.ShellCommon.amStartApp
import com.bbtest.common.ShellCommon.isAppBackstage
import com.bbtest.utils.CommonUtil.getCurTimeForFile
import com.bbtest.utils.CommonUtil.getCurTimeForLog
import com.bbtest.utils.CommonUtil.getExceptionMsg
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.writeStrToFile
import com.bbtest.utils.ShellCommand.execCmdByUiDevice
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * @author onuszhao
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class UIPressureOld : PhxCommon() {
    private val perFolder = File(rootFolder, "perform")
    private val resultFolder = File(perFolder, "press")
    var resultFile: File = File(resultFolder, "press.txt")

    @Before
    public override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(perFolder)
        createFolder(resultFolder)
        createFile(resultFile)
    }

    /**
     * 调试时用gradle，正式则用adb，需在Build -> Testing -> Run Android Instrumented Tests using Gradle下切换
     * gradle app:testUIPressure --tests=com.bbtest.perform.press.UIPressure
     */
    @Test
    fun testUIPressure() {
        try {
//           testUIPressureOld();
            testUIPressureNew()
            //            testUIPressureNovel();
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile(getCurTimeForLog() + "  PressureTest:Exception" + "\n", resultFile)
            writeStrToFile(getExceptionMsg(e), resultFile)
            screenshot(resultFolder.toString() + "/press_" + getCurTimeForFile() + ".jpg")
        }
    }

    private fun testUIPressureNew() {
        // 启动应用
        amStartApp(device, activity, null)
        sleep(TIMEOUT_LONG.toLong())
        execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -d https://www.qq.com")
        sleep(TIMEOUT_MEDIUM.toLong())
        getUiObject2ByDesc("toolbar menu")?.click()
        sleep(TIMEOUT_SHORT.toLong())
        getUiObject2ByText("Add to bookmark")?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        getUiObject2ByDesc("toolbar menu")?.click()
        sleep(TIMEOUT_SHORT.toLong())
        getUiObject2ByText("Add to speed dial")?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        getUiObject2ByDesc("toolbar menu")?.click()
        sleep(TIMEOUT_SHORT.toLong())
        getUiObject2ByText("Add to home screen")?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        getUiObject2ByText("Explore")?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        getUiObject2ByText("Files")?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        getUiObject2ByText("Images")?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        getUiObject2ByText("Documents")?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        getUiObject2ByText("DOC")?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        getUiObject2ByText("Me")?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        getUiObject2ByText("History")?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        getUiObject2ByText("Tabs")?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        //            getUiObject2s("android.widget.ImageView", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.7, 1)?.get(0)?.click();
//            sleep(TIMEOUT_MEDIUM);
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        getUiObject2ByText("Home")?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        back()
        sleep(TIMEOUT_MEDIUM.toLong())
    }

    private fun testUIPressureOld() {
        // 先保证回到主页
        backToHome()

        // 打开扫一扫
        waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_MEDIUM.toLong())?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        skipAppDialog()

        // 打开快链HotSites
        waitUiObject2ByText("Google", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        back()
        sleep(TIMEOUT_SHORT.toLong())
        skipAppDialog()

        // 打开网址百度
        clickSearchBox(false)
        sleep(TIMEOUT_SHORT.toLong())
        setTextAndGo("www.baidu.com")
        sleep(TIMEOUT_MEDIUM.toLong())
        val accept = waitUiObject2ByText("Accept", TIMEOUT_SHORT)
        if (accept != null) {
            accept.click()
        }
        val continueDialog = waitUiObject2ByText("Continue", TIMEOUT_SHORT)
        if (continueDialog != null) {
            back()
        }
        val rateUs5star = waitUiObject2ByText("Rate us 5 stars", TIMEOUT_SHORT)
        if (rateUs5star != null) {
            back()
        }
        sleep(TIMEOUT_VERY_SHORT.toLong())
        waitUiObject2ByRes("index-kw", TIMEOUT_MEDIUM.toLong())?.click()
        sleep(TIMEOUT_SHORT.toLong())
        waitUiObject2ByRes("index-kw", TIMEOUT_MEDIUM.toLong())?.setText("test" ?: "")
        sleep(TIMEOUT_SHORT.toLong())
        waitUiObject2ByRes("index-bn", TIMEOUT_MEDIUM.toLong())?.click()
        sleep(TIMEOUT_SHORT.toLong())
        waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        waitUiObject2ByText("Share", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        skipAppDialog()
        skipOtherDialog()
        // 后退
        waitUiObject2ByDesc("toolbar back", TIMEOUT_MEDIUM.toLong())?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        backToHome()

        // 进入书签
        waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        waitUiObject2ByText("Bookmarks", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 进入历史
        waitUiObject2ByText("History", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 进入我的视频
        waitUiObject2ByText("My Video", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 进入我的音乐
        waitUiObject2ByText("My Music", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 进入广告拦截
        var adBlock = waitUiObject2ByRes("com.transsion.phoenix:id/adBlocked", TIMEOUT_MEDIUM.toLong())
        if (adBlock == null) {
            adBlock = waitUiObject2ByText("Ad Blocker", TIMEOUT_VERY_SHORT)
        }
        adBlock?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())

        // 进入设置
        waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 搜索引擎
        waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        waitUiObject2ByText("Bing", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 图片
        waitUiObject2ByText("Image", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        waitUiObject2ByText("Always no image", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 字体大小
        waitUiObject2ByText("Font size", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val seekBar = waitUiObject2sByClazz("android.widget.SeekBar", TIMEOUT_MEDIUM.toLong()).get(0)
        swip(seekBar, "left")
        sleep(TIMEOUT_VERY_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 语言
        waitUiObject2ByText("Language", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 主页
        waitUiObject2ByText("Homepage", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val homeSwitch = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM.toLong()).get(0)
        if (homeSwitch.isChecked()) {
            homeSwitch.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 下载
        waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        waitUiObject2ByText("Download simultaneously", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        waitUiObject2ByText("6", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        waitUiObject2ByText("Download location", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val storage = waitUiObject2ByText("Internal Storage", TIMEOUT_SHORT)
        if (storage != null) {
            storage.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        waitUiObject2ByText("Choose it", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 通知栏
        waitUiObject2ByText("Notifications", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val notificationSwitchs = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM.toLong())
        for (notificationSwitch in notificationSwitchs) {
            if (notificationSwitch.isChecked()) {
                notificationSwitch.click()
            }
        }
        sleep(TIMEOUT_VERY_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 清理数据
        waitUiObject2ByText("Clear data", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        waitUiObject2ByText("Account and password", TIMEOUT_MEDIUM)?.click()
        waitUiObject2ByText("Video and Document browsing history", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        var cleanPhoenix: UiObject2? = null
        val tmpCleanPhoenixs = waitUiObject2sByTextContains("Clean Up", TIMEOUT_MEDIUM)
        for (tmpCleanPhoenix in tmpCleanPhoenixs) {
            if (tmpCleanPhoenix.isClickable()) {
                cleanPhoenix = tmpCleanPhoenix
                break
            }
        }
        if (cleanPhoenix != null && cleanPhoenix.isEnabled()) {
            cleanPhoenix.click()
            sleep(TIMEOUT_LONG.toLong())
            // 关闭广告
            closeAdDialog()
            for (i in 0..9) {
                val cleanPhoenixBacks = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)
                if (cleanPhoenixBacks == null || cleanPhoenixBacks.size == 0) {
                    back()
                } else {
                    // uiautomator有时点击时会报异常
                    try {
                        cleanPhoenixBacks.get(0)!!.click()
                    } catch (e: Exception) {
                        back()
                    }
                }

                if (waitUiObject2ByText("Clear data", TIMEOUT_VERY_SHORT) != null) {
                    break
                }
            }
        } else {
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
        }
        // 设置默认浏览器
        waitUiObject2ByText("Set as default browser", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        for (i in 0..2) {
            if (waitUiObject2ByText("Set as default browser", TIMEOUT_SHORT) == null) {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            } else {
                break
            }
        }
        // 上滑
        swip(0.5, 0.8, 0.5, 0.2)
        sleep(TIMEOUT_SHORT.toLong())
        // 检查更新
        waitUiObject2ByText("Check for updates", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        for (i in 0..2) {
            if (waitUiObject2ByText("Check for updates", TIMEOUT_SHORT) == null) {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            } else {
                break
            }
        }

        // 关于
        waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // Facebook
        waitUiObject2ByTextContains("Join our Facebook page", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        if (isAppBackstage(device, pkgName)) {
            amStartApp(device, activity, null)
            sleep(TIMEOUT_VERY_SHORT.toLong())
        } else {
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        // 用户反馈
        waitUiObject2ByTextContains("Feedback", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 评分
        waitUiObject2ByTextContains("Rate us", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        if (isAppBackstage(device, pkgName)) {
            amStartApp(device, activity, null)
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        // 恢复默认
        waitUiObject2ByTextContains("Reset to default settings", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        waitUiObject2ByTextContains("Restore", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())

        // 进入文件
        waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        waitUiObject2ByText("Documents", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        waitUiObject2ByText("Images", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())

        // 多窗口
        waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        waitUiObject2ByText("Home", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        getUiObject2s("android.widget.ImageView", true, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.7, 1.0)?.get(0)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        skipAppDialog()

        // 退出浏览器
        backExitBrowser()
    }

    private fun testUIPressureNovel() {
        val pkgName = "com.cloudview.novel"
        val activity = "com.cloudview.novel/com.cloudview.novel.MainActivity"

        // 启动应用
//        ShellCommon.amStartApp(device, activity, null);
//        sleep(TIMEOUT_LONG);
//        click(width * 2 / 5, height * 2 / 5);
//        sleep(TIMEOUT_MEDIUM);
        getUiObject2ByText("Add to library")?.click()
        sleep(TIMEOUT_SHORT.toLong())
        swip(0.7, 0.5, 0.3, 0.5)
        sleep(TIMEOUT_SHORT.toLong())
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        getUiObject2ByText("Genres")?.click()
        sleep(TIMEOUT_MEDIUM.toLong())
        //        repeat(3) { index ->
//                println("This is iteration $index")
//        }
        swip(0.5, 0.7, 0.5, 0.3)
        sleep(TIMEOUT_SHORT.toLong())
        swip(0.5, 0.7, 0.5, 0.3)
        sleep(TIMEOUT_SHORT.toLong())
    }

    @After
    public override fun afterTest() {
        super.afterTest()
    }
}
