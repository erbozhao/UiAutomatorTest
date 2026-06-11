package com.bbtest.init

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.bbtest.common.PhxCommon
import com.bbtest.common.ShellCommon.forceStopApp
import com.bbtest.common.ShellCommon.isAppBackstage
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.CommonUtil.getCurTimeForFile
import com.bbtest.utils.CommonUtil.getCurTimeForLog
import com.bbtest.utils.CommonUtil.getExceptionMsg
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.deleteFolder
import com.bbtest.utils.FileUtil.writeStrToFile
import com.bbtest.utils.WifiTools
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
class InitTest : PhxCommon() {
    private val resultFolder = File(rootFolder, "init")
    private val initFile = File(resultFolder, "init.txt")
    private val ignoreFile = File(rootFolder, "ignore.txt")

    @Before
    public override fun beforeTest() {
        super.beforeTest()
        // 初始化目录
        deleteFolder(resultFolder)
        createFolder(resultFolder)
        // 初始化网络
        val wifiTools = WifiTools(device, ApplicationProvider.getApplicationContext<Context?>())
        if (!wifiTools.isNetworkConnected() && !wifiTools.isNetworkAvailable()) {
            wifiTools.openWifi()
            wifiTools.startScantWifi()
            CommonUtil.sleep(5000)
            wifiTools.connectWifi("YLKJ", "phxbrowser2020")
            CommonUtil.sleep(5000)
            if (!wifiTools.isNetworkConnected() && !wifiTools.isNetworkAvailable()) {
                wifiTools.connectWifi("YLKJ-2.4G", "phxbrowser2020")
                CommonUtil.sleep(5000)
            }
        }
    }

    @Test
    fun testInitBrowserDefault() {
        initCountryLanguage(null, null)
    }

    @Test
    fun testInitBrowserNG() {
        initCountryLanguage("NG", "en")
    }

    @Test
    fun testInitBrowserEG() {
        initCountryLanguage("EG", "ar")
    }

    @Test
    fun testInitReleaseEnv() {
        try {
            if (isAppBackstage(device, pkgName)) {
                writeStrToFile(getCurTimeForLog() + "InitReleaseEnv:启动应用" + "\n", initFile)
                startApp(pkgName)
                sleep(TIMEOUT_LONG.toLong())
            }
            writeStrToFile(getCurTimeForLog() + "InitReleaseEnv:回到首页" + "\n", initFile)
            backToHome()
            writeStrToFile(getCurTimeForLog() + "InitReleaseEnv:切换正式环境" + "\n", initFile)
            switchGrayEnv(false)
            if (isAppBackstage(device, pkgName)) {
                writeStrToFile(getCurTimeForLog() + "InitReleaseEnv:启动应用" + "\n", initFile)
                startApp(pkgName)
                sleep(TIMEOUT_LONG.toLong())
            }
            writeStrToFile(getCurTimeForLog() + "InitReleaseEnv:回到首页" + "\n", initFile)
            backToHome()
            screenshot(resultFolder.toString() + "/init_release_" + getCurTimeForFile() + ".jpg")
            writeStrToFile(getCurTimeForLog() + "InitReleaseEnv:back退出应用" + "\n", initFile)
            backExitBrowser()
            writeStrToFile(getCurTimeForLog() + "InitReleaseEnv:强杀进程" + "\n", initFile)
            forceStopApp(device, pkgName, null)
            sleep(3000)

            writeStrToFile(getCurTimeForLog() + "InitReleaseEnv:Success" + "\n", initFile)
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile(getCurTimeForLog() + "InitReleaseEnv:Exception" + "\n", initFile)
            writeStrToFile(getExceptionMsg(e), initFile)
            screenshot(resultFolder.toString() + "/init_release_" + getCurTimeForFile() + ".jpg")
        }
    }

    @Test
    fun testInitGrayEnv() {
        try {
            if (isAppBackstage(device, pkgName)) {
                writeStrToFile(getCurTimeForLog() + "InitGrayEnv:启动应用" + "\n", initFile)
                startApp(pkgName)
                sleep(TIMEOUT_LONG.toLong())
            }
            writeStrToFile(getCurTimeForLog() + "InitGrayEnv:回到首页" + "\n", initFile)
            backToHome()
            writeStrToFile(getCurTimeForLog() + "InitGrayEnv:切换灰度环境" + "\n", initFile)
            switchGrayEnv(true)
            if (isAppBackstage(device, pkgName)) {
                writeStrToFile(getCurTimeForLog() + "InitGrayEnv:启动应用" + "\n", initFile)
                startApp(pkgName)
                sleep(TIMEOUT_LONG.toLong())
            }
            writeStrToFile(getCurTimeForLog() + "InitGrayEnv:回到首页" + "\n", initFile)
            backToHome()
            screenshot(resultFolder.toString() + "/init_gray_" + getCurTimeForFile() + ".jpg")
            writeStrToFile(getCurTimeForLog() + "InitGrayEnv:back退出应用" + "\n", initFile)
            backExitBrowser()
            writeStrToFile(getCurTimeForLog() + "InitGrayEnv:强杀进程" + "\n", initFile)
            forceStopApp(device, pkgName, null)
            sleep(3000)

            writeStrToFile(getCurTimeForLog() + "InitGrayEnv:Success" + "\n", initFile)
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile(getCurTimeForLog() + "InitGrayEnv:Exception" + "\n", initFile)
            writeStrToFile(getExceptionMsg(e), initFile)
            screenshot(resultFolder.toString() + "/init_gray_" + getCurTimeForFile() + ".jpg")
        }
    }

    private fun initCountryLanguage(country: String?, language: String?) {
        try {
            // 初始化文件
            createFile(initFile)

            //启动应用->跳过闪屏->切换语言->停止应用
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:启动应用" + "\n", initFile)
            startApp(pkgName)
            sleep(TIMEOUT_LONG.toLong())
            if (!isAppBackstage(device, pkgName)) {
                writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:跳过闪屏" + "\n", initFile)
                skipSplash()
            }
            var isClickIgnoreLimit = false
            for (i in 0..2) {
                if (isAppBackstage(device, pkgName)) {
                    startApp(pkgName)
                }

                if (!isClickIgnoreLimit) {
                    writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:忽略限制" + "\n", initFile)
                    var cnLimit = waitUiObject2ByText(
                        "Sorry, the service is unavailable for policy reasons. The browser will quit in 3 seconds.",
                        TIMEOUT_MEDIUM
                    )
                    if (cnLimit == null) {
                        cnLimit = waitUiObject2ByText(
                            "Sorry, the service is unavailable for policy reasons. The browser will quit in 3 seconds.",
                            TIMEOUT_MEDIUM
                        )
                    }
                    if (cnLimit != null) {
                        val x = cnLimit.getVisibleBounds().centerX()
                        val y = cnLimit.getVisibleBounds().centerY()
                        for (j in 0..10) {
                            click(x, y)
                        }
                        if (!isAppBackstage(device, pkgName)) {
                            isClickIgnoreLimit = true
                            sleep(TIMEOUT_SHORT.toLong())
                        }
                    } else {
                        if (isClickIgnoreLimit) {
                            break
                        }
                    }
                } else {
                    break
                }
            }
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile)
            backToHome()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:跳过文件引导" + "\n", initFile)
            skipFilesGuide()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile)
            backToHome()
            if (country != null && language != null) {
                if (language == "en") {
                    val forYou = waitUiObject2ByText("For you", TIMEOUT_MEDIUM)
                    if (forYou == null) {
                        writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:切换英语" + "\n", initFile)
                        switchLanguage(country, language)
                    }
                } else if (language == "ar") {
                    val forYou = waitUiObject2ByText("مُختار لك", TIMEOUT_MEDIUM)
                    if (forYou == null) {
                        writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:切换阿语" + "\n", initFile)
                        switchLanguage(country, language)
                    }
                }
            }
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile)
            backToHome()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:back退出应用" + "\n", initFile)
            backExitBrowser()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:强杀进程" + "\n", initFile)
            forceStopApp(device, pkgName, null)
            sleep(3000)

            //再次启动应用->跳过Feeds上滑->停止应用
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:启动应用" + "\n", initFile)
            startApp(pkgName)
            sleep(TIMEOUT_LONG.toLong())
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile)
            backToHome()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:跳过Feeds引导" + "\n", initFile)
            skipFeedsGuide()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile)
            backToHome()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:back退出应用" + "\n", initFile)
            backExitBrowser()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:强杀进程" + "\n", initFile)
            forceStopApp(device, pkgName, null)
            sleep(3000)

            //再次启动应用->跳过视频嗅探引导->停止应用
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:启动应用" + "\n", initFile)
            startApp(pkgName)
            sleep(TIMEOUT_LONG.toLong())
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile)
            backToHome()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:跳过快链引导" + "\n", initFile)
            skipSniffVideosGuide()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile)
            backToHome()
            screenshot(resultFolder.toString() + "/init_" + getCurTimeForFile() + ".jpg")
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:back退出应用" + "\n", initFile)
            backExitBrowser()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:强杀进程" + "\n", initFile)
            forceStopApp(device, pkgName, null)
            sleep(3000)

            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:Success" + "\n", initFile)
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile(getCurTimeForLog() + "InitCountryLanguage:Exception" + "\n", initFile)
            writeStrToFile(getExceptionMsg(e), initFile)
            screenshot(resultFolder.toString() + "/init_" + getCurTimeForFile() + ".jpg")
        }
    }

    @Test
    fun testIgnoringBatteryOptimization() {
        /**
         * 设置后台限制
         */
        try {
            // 初始化文件
            createFile(ignoreFile)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 初始化需要忽略app的包名
                val ignorePkgNames: MutableList<String?> = ArrayList<String?>()
                ignorePkgNames.add(ApplicationProvider.getApplicationContext<Context?>().getPackageName())
                ignorePkgNames.add(ApplicationProvider.getApplicationContext<Context?>().getPackageName() + ".test")

                // 开始设置
                val powerManager = ApplicationProvider.getApplicationContext<Context?>().getSystemService(Context.POWER_SERVICE) as PowerManager
                for (ignorePkgName in ignorePkgNames) {
                    //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框
                    val hasIgnored = powerManager.isIgnoringBatteryOptimizations(ignorePkgName)
                    if (!hasIgnored) {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        intent.setData(Uri.parse("package:" + ignorePkgName))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        ApplicationProvider.getApplicationContext<Context?>().startActivity(intent)
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        var allow = waitUiObject2ByText("ALLOW", TIMEOUT_MEDIUM)
                        if (allow == null) {
                            allow = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT)
                        }
                        allow?.click()
                        sleep(TIMEOUT_SHORT.toLong())
                    }
                }
            }
            writeStrToFile(getCurTimeForLog() + "backgroundRestriction:Success" + "\n", ignoreFile)
            screenshot(resultFolder.toString() + "/backgroundRestriction_" + getCurTimeForFile() + ".jpg")
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile(getCurTimeForLog() + "backgroundRestriction:Exception" + "\n", ignoreFile)
            writeStrToFile(getExceptionMsg(e), initFile)
            screenshot(resultFolder.toString() + "/backgroundRestriction_" + getCurTimeForFile() + ".jpg")
        }

        /**
         * 设置电池优化
         */
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 初始化需要忽略app的包名
                val ignoreAppNames: MutableList<String?> = ArrayList<String?>()
                ignoreAppNames.add("BBTest")
                ignoreAppNames.add("BBTest Test")

                // 开始设置
                val intent = Intent()
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.setData(Uri.fromParts("package", ApplicationProvider.getApplicationContext<Context?>().getPackageName(), null))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ApplicationProvider.getApplicationContext<Context?>().startActivity(intent)
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
                battery!!.click()
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
                sleep(TIMEOUT_SHORT.toLong())
                val appScrollableClazz = getScrollableClazz()
                if (appScrollableClazz == "android.view.ViewGroup") {
                    for (ignoreAppName in ignoreAppNames) {
                        waitScrollableUiObjectByText(appScrollableClazz, ignoreAppName, false)
                        sleep(TIMEOUT_SHORT.toLong())
                        val appSwitch = getUiObject2ByChildText("android.widget.LinearLayout", true, "BBTest", "android.widget.Switch")
                        if (appSwitch?.isChecked() == true) {
                            appSwitch.click()
                            sleep(TIMEOUT_SHORT.toLong())
                        }
                    }
                } else {
                    for (ignoreAppName in ignoreAppNames) {
                        waitScrollableUiObjectByText(appScrollableClazz, ignoreAppName, false)?.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        var notOptimized = waitUiObject2ByText("Don’t optimise", TIMEOUT_MEDIUM)
                        if (notOptimized == null) {
                            notOptimized = waitUiObject2ByText("Don’t optimize", TIMEOUT_VERY_SHORT)
                        }
                        if (notOptimized == null) {
                            notOptimized = waitUiObject2ByText("Not optimized", TIMEOUT_VERY_SHORT)
                        }
                        notOptimized?.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        var done = waitUiObject2ByText("Done", TIMEOUT_MEDIUM)
                        if (done == null) {
                            done = waitUiObject2ByText("DONE", TIMEOUT_VERY_SHORT)
                        }
                        done?.click()
                        sleep(TIMEOUT_SHORT.toLong())
                    }
                }
            }

            writeStrToFile(getCurTimeForLog() + "batteryOptimization:Success" + "\n", ignoreFile)
            screenshot(resultFolder.toString() + "/batteryOptimization_" + getCurTimeForFile() + ".jpg")
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile(getCurTimeForLog() + "batteryOptimization:Exception" + "\n", ignoreFile)
            writeStrToFile(getExceptionMsg(e), ignoreFile)
            screenshot(resultFolder.toString() + "/batteryOptimization_" + getCurTimeForFile() + ".jpg")
        }
    }

    @Test
    fun testClearAllNotifications() {
        clearAllNotifications()
    }

    @After
    public override fun afterTest() {
        super.afterTest()
    }
}
