package com.bbtest.debug

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.uiautomator.UiObject2
import com.bbtest.common.PhxCommon
import com.bbtest.common.ShellCommon
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.FileUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Random

/**
 * @author onuszhao
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class Debug : PhxCommon() {
    private val resultFolder = File(rootFolder, "debug")
    private val debugFile = File(resultFolder, "debug.txt")

    @Before
    override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder)
        FileUtil.deleteFile(debugFile)
        FileUtil.createFile(debugFile)
    }

    @Test
    fun testDebug() {
        try {
            val startTime = System.currentTimeMillis()
            screenshot("${resultFolder}/debug_start_${CommonUtil.getCurTimeForFile()}.jpg")
            FileUtil.writeStrToFile("start time=$startTime", debugFile)

            // 启动应用
            ShellCommon.amStartApp(device, activity, null)
            sleep(TIMEOUT_LONG.toLong())

            clickSearchBox(false)
            sleep(TIMEOUT_SHORT.toLong())
            setTextAndGo("helo")
            sleep(TIMEOUT_MEDIUM.toLong())

            openNotification()
            sleep(TIMEOUT_MEDIUM.toLong())

            val object2 = getUiObject2ByTextContains("APP Uninstalled")
            if (object2 != null) {
                object2.click()
            }
            sleep(TIMEOUT_MEDIUM.toLong())

            // getUiObject2sByClazz("android.widget.Button")?.getOrNull(0)?.let {
            //     it.click()
            // }
//
//            if (getUiObject2sByClazz("android.view.View").size() > 0) {
//                addSites = getUiObject2s("android.view.View", true, 0.4, 0.6, 0.02, 0.2, 0, 1, 0.2, 0.9);
//                break;
//            }

            val endTime = System.currentTimeMillis()
            val costTime = (endTime - startTime) / 1000
            println("耗时: ${costTime}s")
            screenshot("${resultFolder}/debug_end_${CommonUtil.getCurTimeForFile()}.jpg")
            FileUtil.writeStrToFile("end time=$costTime", debugFile)
        } catch (e: Exception) {
            e.printStackTrace()
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}Debug:Exception\n", debugFile)
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), debugFile)
            screenshot("${resultFolder}/debug_${CommonUtil.getCurTimeForFile()}.jpg")
        }
    }

    private fun getRootUiObject2(rootContent: UiObject2): UiObject2? {
        for (children in rootContent.children) {
            val curRect = children.visibleBounds
            val curWidth = curRect.right - curRect.left
            val curHeight = curRect.bottom - curRect.top
            Log.i("onuszhao", "curWidth:$curWidth,curHeight:$curHeight")
            if (curWidth == width && curHeight == height) {
                return children
            }
        }
        return null
    }

    @Test
    fun testSmallVideo() {
        startApp("com.transsion.phoenix")
        sleep(TIMEOUT_LONG.toLong())
        waitUiObject2ByText("Videos", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        waitUiObject2ByText("Short Video", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())

        val startTime = System.currentTimeMillis()
        var endTime = 0L
        while (true) {
            endTime = System.currentTimeMillis()
            val costTime = endTime - startTime
            if (costTime > 1 * 60 * 60 * 1000) {
                break
            } else {
                swip(0.5, 0.6, 0.5, 0.3)
                sleep(TIMEOUT_SHORT.toLong())
                device.click((width * 0.6).toInt(), (height * 0.4).toInt())
                sleep(TIMEOUT_LONG.toLong())
                for (i in 0 until Random().nextInt(50)) {
                    swip(0.5, 0.6, 0.5, 0.3)
                    sleep(TIMEOUT_MEDIUM.toLong())
                }
                device.pressBack()
                sleep(TIMEOUT_SHORT.toLong())
            }
        }
    }

    @Test
    fun testIgnoringBatteryOptimization() {
        /**
         * 设置后台限制
         */
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val powerManager = ApplicationProvider.getApplicationContext<Context>().getSystemService(Context.POWER_SERVICE) as PowerManager
                //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框
                val hasIgnored = powerManager.isIgnoringBatteryOptimizations(pkgName)
                FileUtil.writeStrToFile("hasIgnored:$hasIgnored\n", debugFile)

                // 开始设置
                val intent = Intent()
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.setData(Uri.fromParts("package", pkgName, null))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ApplicationProvider.getApplicationContext<Context>().startActivity(intent)
                sleep(TIMEOUT_SHORT.toLong())
                val batteryScrollableClazz = getScrollableClazz()
                var battery = waitScrollableUiObjectByText(batteryScrollableClazz, "Battery", false)
                if (battery == null) {
                    val advanced = waitUiObject2ByText("Advanced", TIMEOUT_MEDIUM)
                    if (advanced != null) {
                        advanced.click()
                        sleep(TIMEOUT_SHORT.toLong())
                        battery = waitScrollableUiObjectByText(batteryScrollableClazz, "Battery", false)
                    }
                }
                battery?.click()
                sleep(TIMEOUT_SHORT.toLong())
                var batteryOptimization = waitScrollableUiObjectByText(batteryScrollableClazz, "Battery optimisation", false)
                if (batteryOptimization == null) {
                    batteryOptimization = waitScrollableUiObjectByText(batteryScrollableClazz, "Battery optimization", false)
                }
                if (batteryOptimization == null) {
                    batteryOptimization = waitScrollableUiObjectByText(batteryScrollableClazz, "Optimise battery usage", false)
                }
                batteryOptimization?.click()
                sleep(TIMEOUT_SHORT.toLong())
                waitUiObject2ByRes("com.android.settings:id/filter_spinner", TIMEOUT_VERY_LONG.toLong())?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                var allApps = waitUiObject2ByText("All apps", TIMEOUT_MEDIUM)
                if (allApps == null) {
                    allApps = waitUiObject2ByText("All", TIMEOUT_VERY_SHORT)
                }
                allApps?.click()
                sleep(TIMEOUT_MEDIUM.toLong())
                val appScrollableClazz = getScrollableClazz()
                waitScrollableUiObjectByText(appScrollableClazz, "Phoenix", false)
                sleep(TIMEOUT_SHORT.toLong())
                screenshot("${resultFolder}/result_${CommonUtil.getCurTimeForFile()}.jpg")
                swip(0.5, 0.6, 0.5, 0.4)
                sleep(TIMEOUT_SHORT.toLong())
                screenshot("${resultFolder}/result_${CommonUtil.getCurTimeForFile()}.jpg")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}testIgnoringBatteryOptimization:Exception\n", debugFile)
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), debugFile)
            screenshot("${resultFolder}/testIgnoringBatteryOptimization_${CommonUtil.getCurTimeForFile()}.jpg")
        }
    }

    @Test
    fun testClearRecentApps() {
        openRecentApps()
        sleep(5000)
        for (i in 0..9) {
            swip(0.5, 0.7, 0.5, 0.3)
            sleep(3000)
        }
        home()
        sleep(3000)
        openRecentApps()
        sleep(5000)
        for (i in 0..9) {
            swip(0.3, 0.5, 0.7, 0.5)
            sleep(3000)
        }
    }

    @Test
    fun testGetGuid() {
        startApp(pkgName)
        sleep(TIMEOUT_MEDIUM.toLong())
        backToHome()
        val mostVisited = waitUiObject2ByText("Most Visited", TIMEOUT_MEDIUM)
        if (mostVisited == null) {
            skipFeedsGuide()
        } else {
            skipSniffVideosGuide()
        }
        collectGuid()
        backExitBrowser()
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }

    /**
     * 获取渠道ID
     */
    private fun collectGuid() {
        try {
            val me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM)
            if (me != null) {
                me.click()
            } else {
                waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            }
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Product features", TIMEOUT_MEDIUM)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val phxIcon = getUiObject2s("android.widget.ImageView", true, 0.2, 0.5, 0.1, 0.5, 0.2, 0.8, 0.02, 0.5).firstOrNull()
            repeat(5) {
                phxIcon?.click()
            }
            waitUiObject2ByText("Basic Info", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            val guidInfo = waitUiObject2ByTextContains("guid", TIMEOUT_MEDIUM)?.text
            val guid = guidInfo?.trim()?.split("\\s+".toRegex())?.getOrNull(1).orEmpty()
            FileUtil.writeStrToFile(guid, debugFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
