package com.bbtest.compatible

import androidx.test.uiautomator.UiObject2
import com.bbtest.common.PhxCommon
import com.bbtest.common.ShellCommon.amStartApp
import com.bbtest.common.ShellCommon.forceStopApp
import com.bbtest.common.ShellCommon.grantApkPermission
import com.bbtest.common.ShellCommon.isAppBackstage
import com.bbtest.utils.CommonUtil.getCurTimeForFile
import com.bbtest.utils.CommonUtil.getExceptionMsg
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.deleteFile
import com.bbtest.utils.FileUtil.writeStrToFile
import com.bbtest.utils.ShellCommand.execCmdByUiDevice
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class MainPathTest : PhxCommon() {
    private val resultFolder = File(rootFolder, "compatible")
    private val mainPathFile = File(resultFolder, "mainpath.txt")

    @Before
    public override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
        deleteFile(mainPathFile)
    }

    @Test
    fun testStartApp() {
        try {
            // 先授权
            grantApkPermission(device, pkgName)

            // 再启动应用->跳过闪屏->切换语言->停止应用
            startApp(pkgName)
            sleep(TIMEOUT_LONG.toLong())
            backToHome()
            skipFilesGuide()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val forYou = waitUiObject2ByText("For you", TIMEOUT_MEDIUM)
            if (forYou == null) {
                switchLanguage("NG", "en")
            }
            backToHome()
            backExitBrowser()
            forceStopApp(device, pkgName, null)

            // 再次启动应用->跳过Feeds上滑->停止应用
            startApp(pkgName)
            sleep(TIMEOUT_LONG.toLong())
            backToHome()
            skipFeedsGuide()
            backToHome()
            backExitBrowser()
            forceStopApp(device, pkgName, null)

            //启动应用
            startApp(pkgName)
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("StartApp:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/StartApp_" + getCurTimeForFile() + ".jpg")
            e.printStackTrace()
        }
    }

    /**
     * 设置默认浏览器
     */
    @Test
    fun testSetDefaultBrowser() {
        try {
            var me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM)
            if (me == null) {
                me = waitUiObject2ByDesc("toolbar menu", TIMEOUT_SHORT.toLong())
            }
            me?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var isSetDefaultSuccess =
                getUiObject2ByChildText("android.widget.LinearLayout", true, "Set as default browser", "android.widget.Switch")?.isChecked
                    ?: false
            if (!isSetDefaultSuccess) {
                waitUiObject2ByText("Set as default browser", TIMEOUT_MEDIUM)?.click()
                var next = waitUiObject2ByText("Continue", TIMEOUT_MEDIUM)
                if (next == null) {
                    next = waitUiObject2ByText("Next", TIMEOUT_SHORT)
                }
                next?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                val dialogPHXBrowser = waitUiObject2ByText("Phoenix", TIMEOUT_MEDIUM)
                if (dialogPHXBrowser != null) {
                    dialogPHXBrowser.click()
                    var setAsDefault = waitUiObject2ByText("Set as default", TIMEOUT_SHORT)
                    if (setAsDefault == null) {
                        setAsDefault = waitUiObject2ByText("SET AS DEFAULT", TIMEOUT_SHORT)
                    }
                    setAsDefault?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    waitUiObject2ByText("Set as default browser", TIMEOUT_MEDIUM)
                } else {
                    val browserApp = waitUiObject2ByText("Browser app", TIMEOUT_MEDIUM)
                    if (browserApp != null) {
                        browserApp.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        var phx = waitUiObject2ByText("Phoenix", TIMEOUT_MEDIUM)
                        if (phx == null) {
                            phx = waitUiObject2ByText("PHX Browser", TIMEOUT_MEDIUM)
                        }
                        phx?.click()
                        if (isAppBackstage(device, pkgName)) {
                            amStartApp(device, activity, null)
                        }
                    }
                }
                isSetDefaultSuccess =
                    getUiObject2ByChildText("android.widget.LinearLayout", true, "Set as default browser", "android.widget.Switch")?.isChecked
                        ?: false
            }
            if (isSetDefaultSuccess) {
                writeStrToFile("SetDefaultBrowser:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("SetDefaultBrowser:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/SetDefaultBrowser_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SetDefaultBrowser:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SetDefaultBrowser_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-登录
     */
    @Test
    fun testMeLogin() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Login", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Continue with Google", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            waitUiObject2ByTextContains("@gmail.com", TIMEOUT_LONG)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            val isSigned = isUiObject2ExistByText("Signed in with Google", TIMEOUT_LONG)
            if (isSigned) {
                writeStrToFile("MeLogin:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("MeLogin:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/MeLogin_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeLogin:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeLogin_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 常规操作天气
     */
    @Test
    fun testOperateWeather() {
        try {
            var weathers = getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.4, 0.0, 0.2, 0.0, 0.4, 0.02, 0.2)
            if (weathers == null || weathers.size == 0) {
                backToHome()
                sleep(TIMEOUT_LONG.toLong())
                weathers = getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.4, 0.0, 0.2, 0.0, 0.4, 0.02, 0.2)
            }
            if (weathers != null && weathers.size > 0) {
                weathers?.get(0)?.click()
                sleep(TIMEOUT_MEDIUM.toLong())
                waitUiObject2ByText("Air quality", TIMEOUT_LONG)
                var images = getUiObject2s("android.widget.ImageView", true, 0.0, 0.5, 0.0, 0.5, 0.8, 1.0, 0.02, 0.2)
                if (images == null || images.size == 0) {
                    images = getUiObject2s("android.widget.ImageView", true, 0.0, 0.5, 0.0, 0.5, 0.8, 1.0, 0.02, 0.2)
                }
                images?.get(0)?.click()
                waitUiObject2ByText("Manage city", TIMEOUT_MEDIUM)?.click()
                waitUiObject2ByText("Add city", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_SHORT.toLong())
                device.click(width / 2, height / 2)
                sleep(TIMEOUT_SHORT.toLong())
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                swip(0.5, 0.3, 0.5, 0.7)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                swip(0.7, 0.5, 0.3, 0.5)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                swip(0.3, 0.5, 0.7, 0.5)
                sleep(TIMEOUT_SHORT.toLong())
                var imageViews = getUiObject2s("android.widget.ImageView", true, 0.0, 0.5, 0.0, 0.5, 0.0, 0.2, 0.02, 0.2)
                for (i in 0..2) {
                    if (imageViews != null && imageViews.size > 0) {
                        break
                    } else {
                        sleep(TIMEOUT_SHORT.toLong())
                        imageViews = getUiObject2s("android.widget.ImageView", true, 0.0, 0.5, 0.0, 0.5, 0.0, 0.2, 0.02, 0.2)
                    }
                }
                imageViews!!.get(0).click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                writeStrToFile("OperateWeather:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("OperateWeather:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/OperateWeather_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("OperateWeather:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/OperateWeather_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 搜索关键词
     */
    @Test
    fun testSearchKeywords() {
        try {
            clickSearchBox(false)
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.5, 0.0, 0.5, 0.0, 0.2, 0.02, 0.2)?.get(0)?.click()
            waitUiObject2ByText("Yahoo", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.3, 0.5, 0.0, 0.5, 0.0, 0.6, 0.1, 0.4)?.get(0)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            val isSearchKeywordSuccess = isUiObject2ExistByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())
            if (isSearchKeywordSuccess) {
                writeStrToFile("SearchKeywords:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("SearchKeywords:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/SearchKeywords_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SearchKeywords:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SearchKeywords_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 打开网页
     */
    @Test
    fun testOpenWebpage() {
        try {
            clickSearchBox(false)
            sleep(TIMEOUT_SHORT.toLong())
            setTextAndGo("www.qq.com")
            sleep(TIMEOUT_SHORT.toLong())
            skipAppDialog()
            skipOtherDialog()
            writeStrToFile("OpenWebpage:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("OpenWebpage:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/OpenWebpage_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 网页长按弹框
     */
    @Test
    fun testWebviewLongpressDialog() {
        try {
            execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com")
            sleep(TIMEOUT_LONG.toLong())

            // 网页长按弹窗
            var index = 5
            var i = 7
            while (i < 3) {
                longClick(width / 2, height * i / 9)
                if (waitUiObject2ByText("Open in new tab", TIMEOUT_SHORT) != null) {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    index = i
                    break
                }
                i--
            }
            longClick(width / 2, height * index / 9)
            waitUiObject2ByText("Open in new tab", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            longClick(width / 2, height * index / 9)
            waitUiObject2ByText("Open in incognito tab", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            longClick(width / 2, height * index / 9)
            waitUiObject2ByText("Copy link", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            longClick(width / 2, height * index / 9)
            waitUiObject2ByText("Copy link text", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            longClick(width / 2, height * index / 9)
            waitUiObject2ByText("Share link", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("WebviewLongpressDialog:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("WebviewLongpressDialog:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/WebviewLongpressDialog_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 网页更多菜单
     */
    @Test
    fun testWebpageMoreMenu() {
        try {
            execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com")
            sleep(TIMEOUT_LONG.toLong())

            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Add to bookmark", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Add to speed dial", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Add to home screen", TIMEOUT_MEDIUM)?.click()
            var addHomeOk = waitUiObject2ByText("Add automatically", TIMEOUT_SHORT)
            if (addHomeOk == null) {
                addHomeOk = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT)
            }
            if (addHomeOk == null) {
                addHomeOk = waitUiObject2ByText("ADD", TIMEOUT_VERY_SHORT)
            }
            if (addHomeOk != null) {
                addHomeOk.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Share", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Save page for offline", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Save as PDF", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_LONG.toLong())?.click()
            waitUiObject2ByText("Find in page", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Cancel", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Translate page", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Switch to desktop site", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Switch to mobile site", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Screenshot", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Crop region", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.5, 0.0, 0.5, 0.7, 1.0, 0.02, 0.15)?.get(0)?.click()
            waitUiObject2ByText("Save", TIMEOUT_MEDIUM)?.click()
            writeStrToFile("WebpageMoreMenu:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("WebpageMoreMenu:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/WebpageMoreMenu_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 网页工具栏(含主菜单)
     */
    @Test
    fun testWebpageMenu() {
        try {
            execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com")
            sleep(TIMEOUT_LONG.toLong())

            // 前进后退
            sleep(TIMEOUT_SHORT.toLong())
            click(width / 2, height * 5 / 9)
            sleep(TIMEOUT_SHORT.toLong())
            waitUiObject2ByDesc("toolbar back", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByDesc("toolbar forward", TIMEOUT_MEDIUM.toLong())?.click()

            // 进入菜单-书签
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            var bookmark = waitUiObject2ByText("Bookmarks", TIMEOUT_MEDIUM)
            if (bookmark == null) {
                bookmark = waitUiObject2ByText("Bookmark", TIMEOUT_SHORT)
            }
            bookmark?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 进入历史
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("History", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 进入下载
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 进入文件
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 进入我的视频
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("My video", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 进入我的音乐
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("My music", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 进入广告拦截
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            var adBlock = waitUiObject2ByRes("com.transsion.phoenix:id/adBlocked", TIMEOUT_MEDIUM.toLong())
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Adblocker", TIMEOUT_VERY_SHORT)
            }
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Ads blocked", TIMEOUT_VERY_SHORT)
            }
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Ad block", TIMEOUT_VERY_SHORT)
            }
            adBlock?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 进入日(夜)间模式
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            var darkLight = waitUiObject2ByText("Dark", TIMEOUT_SHORT)
            if (darkLight == null) {
                darkLight = waitUiObject2ByText("Light", TIMEOUT_SHORT)
            }
            darkLight?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 设置
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Settings", TIMEOUT_SHORT)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 多窗口
            waitUiObject2ByDesc("toolbar multiWindow", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.7, 1.0)?.get(0)?.click()
            writeStrToFile("WebpageMenu:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("WebpageMenu:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/WebpageMenu_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 扫一扫
     */
    @Test
    fun testScan() {
        try {
            waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.5, 0.0, 0.5, 0.0, 0.3, 0.8, 1.0)?.get(0)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.5, 0.15, 0.5, 0.0, 1.0, 0.15, 1.0)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("Scan:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("Scan:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/Scan_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 快链跳转
     */
    @Test
    fun testSpeedDialAccess() {
        try {
            // 点击快链进入
            waitUiObject2ByText("All Sites", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_MEDIUM.toLong())
            for (i in 0..2) {
                val allSites = getUiObject2s("android.view.View", true, 0.4, 0.6, 0.02, 0.2, 0.0, 1.0, 0.2, 0.9)
                if (allSites.isNotEmpty()) {
                    allSites.getOrNull(0)?.click()
                    sleep(TIMEOUT_SHORT.toLong())
                    waitUiObject2ByDesc("toolbar home", TIMEOUT_MEDIUM.toLong())?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    writeStrToFile("SpeedDialAccess:PASS" + "\n", mainPathFile)
                    break
                } else {
                    sleep(TIMEOUT_SHORT.toLong())
                    if (i == 2) {
                        back()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        writeStrToFile("SpeedDialAccess:FAILED" + "\n", mainPathFile)
                        screenshot(resultFolder.toString() + "/SpeedDialAccess_" + getCurTimeForFile() + ".jpg")
                    }
                }
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SpeedDialAccess:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SpeedDialAccess_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 添加满快链
     */
    @Test
    fun testSpeedDialAdds() {
        try {
            val curSpeedDials = getUiObject2sByClazz("android.widget.TextView")
            val curSpeedDialTexts = getTexts(curSpeedDials)
            for (i in 0..19) {
                // 进入添加快链
                val linearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0.15, 0.25, 0.02, 0.5, 0.0, 1.0, 0.2, 0.8)
                val lastLinearLayout = linearLayouts.getOrNull(linearLayouts.size - 1) ?: break
                val lastLinearLayoutText = getTextOrDesc(lastLinearLayout, true)
                if (lastLinearLayoutText == null || lastLinearLayoutText == "" || lastLinearLayoutText == "Add") {
                    lastLinearLayout.click()
                    sleep(TIMEOUT_SHORT.toLong())
                } else {
                    break
                }

                // 点击未添加快链添加
                var addSites: MutableList<UiObject2> = ArrayList<UiObject2>()
                for (j in 0..2) {
                    if (getUiObject2sByClazz("android.view.View").isNotEmpty()) {
                        addSites = getUiObject2s("android.view.View", true, 0.4, 0.6, 0.02, 0.2, 0.0, 1.0, 0.2, 0.9)
                        break
                    }
                }
                var isClickAddSite = false
                for (addSite in addSites) {
                    val addSiteText = getTextOrDesc(addSite, true)
                    if (addSiteText != null && !curSpeedDialTexts.contains(addSiteText)) {
                        addSite.click()
                        sleep(TIMEOUT_SHORT.toLong())
                        curSpeedDialTexts.add(addSiteText)
                        isClickAddSite = true
                        break
                    }
                }
                if (!isClickAddSite) {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    break
                }

                // 判断是否添加满
                val gotIt = waitUiObject2ByText("Got it", TIMEOUT_VERY_SHORT)
                if (gotIt != null) {
                    gotIt.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    break
                }
            }
            writeStrToFile("SpeedDialAdds:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SpeedDialAdds:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SpeedDialAdds_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 删除所有快链
     */
    @Test
    fun testSpeedDialDels() {
        try {
            for (i in 0..18) {
                sleep(TIMEOUT_VERY_SHORT.toLong())
                val secondSpeedDial = getUiObject2s("android.widget.LinearLayout", true, 0.15, 0.25, 0.02, 0.5, 0.0, 1.0, 0.2, 0.8).getOrNull(1)
                    ?: break
                val secondSpeedDialText = getTextOrDesc(secondSpeedDial, true)
                if (secondSpeedDialText == null || secondSpeedDialText == "" || secondSpeedDialText == "Add") {
                    break
                } else {
                    longClick(secondSpeedDial)
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    waitUiObject2ByText("Delete", TIMEOUT_SHORT)?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
            writeStrToFile("SpeedDialDels:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SpeedDialDels:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SpeedDialDels_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * Feeds删除所有tab
     */
    @Test
    fun testFeedsTabDels() {
        try {
            // 删除所有tab
            getUiObject2s("android.widget.FrameLayout", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.1, 0.8)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Edit", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            for (i in 0..19) {
                val addChannel = waitUiObject2ByText("Add channel", TIMEOUT_SHORT)
                var maxY = 0.9
                if (addChannel != null) {
                    maxY = addChannel.getVisibleBounds().top.toDouble() / height
                }
                val tabs = getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0.0, 0.2, 0.0, 1.0, 0.1, maxY)
                val lastTab = tabs.getOrNull(tabs.size - 1) ?: break
                val lastTabText = getText(lastTab, true)
                if (lastTabText != null && lastTabText == "Video") {
                    break
                } else {
                    lastTab.click()
                }
            }
            waitUiObject2ByText("Done", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.1, 0.3, 0.05, 0.2, 0.0, 0.5, 0.02, 0.5)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FeedsTabDels:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FeedsTabDels:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FeedsTabDels_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * Feeds添加所有tab
     */
    @Test
    fun testFeedsTabAdds() {
        try {
            getUiObject2s("android.widget.FrameLayout", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.1, 0.8)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            for (i in 0..19) {
                val addChannel = waitUiObject2ByText("Add channel", TIMEOUT_SHORT)
                if (addChannel == null) {
                    break
                } else {
                    val minY = addChannel.getVisibleBounds().bottom.toDouble() / height
                    val tabs = getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0.0, 0.2, 0.0, 1.0, minY, 1.0)
                    println(tabs.size)
                    for (tab in tabs) {
                        val tabTxt = getText(tab, true)
                        println(tab.getVisibleBounds().toString() + "," + tabTxt)
                        if (tabTxt != null && tabTxt != "") {
                            tab.click()
                            break
                        }
                    }
                }
            }
            waitUiObject2ByText("Done", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FeedsTabAdds:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FeedsTabAdds:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FeedsTabAdds_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * Feeds新闻
     */
    @Test
    fun testFeedsNews() {
        try {
            // 打开Feeds新闻
            switchFeedsTab("Lifestyle")
            sleep(TIMEOUT_MEDIUM.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.1, 0.5, 0.0, 1.0, 0.02, 0.8)?.get(0)?.click()
            sleep(TIMEOUT_MEDIUM.toLong())

            // Feeds新闻-省流
            var topRightImgs = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.02, 0.2, 0.6, 1.0, 0.02, 0.3)
            for (i in 0..2) {
                if (topRightImgs != null && topRightImgs.size >= 2) {
                    break
                } else {
                    sleep(TIMEOUT_SHORT.toLong())
                    topRightImgs = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.02, 0.2, 0.6, 1.0, 0.02, 0.3)
                }
            }
            topRightImgs!!.get(0).click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2ByChildText("android.widget.LinearLayout", true, "Prompt saving result", "android.widget.Switch")?.click()
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.2, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // Feeds新闻-更多按钮
            if (waitUiObject2ByText("Home", TIMEOUT_SHORT) != null) {
                getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.1, 0.5, 0.0, 1.0, 0.02, 0.8)?.get(0)?.click()
                sleep(TIMEOUT_MEDIUM.toLong())
            }
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.02, 0.2, 0.6, 1.0, 0.02, 0.3)?.get(1)?.click()
            waitUiObject2ByText("Add to favorites", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.02, 0.2, 0.6, 1.0, 0.02, 0.3)?.get(1)?.click()
            waitUiObject2ByText("Share", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.02, 0.2, 0.6, 1.0, 0.02, 0.3)?.get(1)?.click()
            var dartLight = waitUiObject2ByText("Dark mode", TIMEOUT_SHORT)
            if (dartLight == null) {
                dartLight = waitUiObject2ByText("Light mode", TIMEOUT_SHORT)
            }
            dartLight?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.02, 0.2, 0.6, 1.0, 0.02, 0.3)?.get(1)?.click()
            waitUiObject2ByText("Report", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.02, 0.2, 0.6, 1.0, 0.02, 0.3)?.get(1)?.click()
            waitUiObject2ByText("Font size", TIMEOUT_MEDIUM)?.click()
            val seekBar = waitUiObject2sByClazz("android.widget.SeekBar", TIMEOUT_MEDIUM.toLong()).getOrNull(0) ?: return
            swip(seekBar, "right")
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // Feeds新闻-底部工具栏
            val frameLayouts = getUiObject2s("android.widget.FrameLayout", true, 0.0, 0.19, 0.0, 0.2, 0.0, 1.0, 0.8, 1.0)
            if (frameLayouts != null) {
                for (i in frameLayouts.indices) {
                    frameLayouts.get(i).click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    if (i == 0) {
                        back()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                    } else if (i == 1) {
                        swip(0.5, 0.6, 0.5, 0.8)
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        break
                    }
                }
            }
            val linearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.8, 0.0, 0.2, 0.0, 1.0, 0.8, 1.0)
            if (linearLayouts != null && linearLayouts.size > 0) {
                linearLayouts?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }

            val imageViews = getUiObject2s("android.widget.ImageView", true, 0.0, 0.19, 0.0, 0.2, 0.0, 1.0, 0.88, 1.0)
            if (imageViews != null) {
                imageViews?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            writeStrToFile("FeedsNews:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FeedsNews:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FeedsNews_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * Feeds图片
     */
    @Test
    fun testFeedsImg() {
        try {
            switchFeedsTab("Hot Girl")
            sleep(TIMEOUT_MEDIUM.toLong())
            var bigImgLinearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.2, 0.8, 0.0, 1.0, 0.02, 1.0)
            if (bigImgLinearLayouts == null || bigImgLinearLayouts.size == 0) {
                bigImgLinearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0.4, 0.6, 0.1, 0.5, 0.0, 1.0, 0.02, 1.0)
            }
            bigImgLinearLayouts?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            if (waitUiObject2ByText("Save", TIMEOUT_SHORT) == null) {
                val bigImgs = getUiObject2s("android.widget.FrameLayout", true, 0.8, 1.0, 0.5, 0.8, 0.0, 1.0, 0.02, 1.0)
                if (bigImgs != null && bigImgs.size > 0) {
                    bigImgs?.get(0)?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }

                // 底部工具栏
                val frameLayouts = getUiObject2s("android.widget.FrameLayout", true, 0.0, 0.19, 0.0, 0.2, 0.0, 1.0, 0.8, 1.0)
                if (frameLayouts != null) {
                    for (i in frameLayouts.indices) {
                        frameLayouts.get(i).click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        if (i == 0) {
                            back()
                            sleep(TIMEOUT_VERY_SHORT.toLong())
                        } else if (i == 1) {
                            swip(0.5, 0.6, 0.5, 0.8)
                            sleep(TIMEOUT_VERY_SHORT.toLong())
                            break
                        }
                    }
                }
                val linearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.8, 0.0, 0.2, 0.0, 1.0, 0.8, 1.0)
                if (linearLayouts != null && linearLayouts.size > 0) {
                    linearLayouts?.get(0)?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }

                val imageViews = getUiObject2s("android.widget.ImageView", true, 0.0, 0.19, 0.0, 0.2, 0.0, 1.0, 0.88, 1.0)
                if (imageViews != null && imageViews.size > 0) {
                    imageViews?.get(0)?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            } else {
                for (i in 0..10) {
                    swip(0.7, 0.5, 0.3, 0.5)
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
            writeStrToFile("FeedsImg:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FeedsImg:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FeedsImg_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * Feeds视频
     */
    @Test
    fun testFeedsVideos() {
        try {
            // Feeds视频-打开
            switchFeedsTab("Video")
            sleep(TIMEOUT_MEDIUM.toLong())
            val firstVideo = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.2, 0.8, 0.0, 1.0, 0.02, 0.9).get(0)
            val firstVideoBottom = getChildUiObject2(firstVideo, false, "android.widget.LinearLayout", 0.8, 1.0, 0.0, 0.1, 0.0, 1.0, 0.0, 1.0, false)
            firstVideoBottom?.click()
            sleep(TIMEOUT_SHORT.toLong())

            // Feeds视频-底部工具栏
            val frameLayouts = getUiObject2s("android.widget.FrameLayout", true, 0.0, 0.19, 0.0, 0.2, 0.0, 1.0, 0.8, 1.0)
            if (frameLayouts != null) {
                for (i in frameLayouts.indices) {
                    frameLayouts.get(i).click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    if (i == 0) {
                        back()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                    } else if (i == 1) {
                        swip(0.5, 0.6, 0.5, 0.8)
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        break
                    }
                }
            }
            val linearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.8, 0.0, 0.2, 0.0, 1.0, 0.8, 1.0)
            if (linearLayouts != null && linearLayouts.size > 0) {
                linearLayouts?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }

            val imageViews = getUiObject2s("android.widget.ImageView", true, 0.0, 0.19, 0.0, 0.2, 0.0, 1.0, 0.88, 1.0)
            if (imageViews != null) {
                imageViews?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            writeStrToFile("FeedsVideos:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FeedsVideos:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FeedsVideos_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * Feeds小视频
     */
    @Test
    fun testFeedsSmallVideo() {
        try {
            // Feeds小视频
            switchFeedsTab("Short Video")
            sleep(TIMEOUT_MEDIUM.toLong())
            getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.8, 0.0, 1.0, 0.02, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            val swipeToast = waitUiObject2ByText("Swipe up for more", TIMEOUT_SHORT)
            if (swipeToast != null) {
                swipeToast.click()
            }
            swip(0.5, 0.7, 0.5, 0.3)
            sleep(TIMEOUT_SHORT.toLong())
            swip(0.5, 0.3, 0.5, 0.7)
            sleep(TIMEOUT_SHORT.toLong())

            // Feeds小视频-右侧工具栏
            getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.3, 1.0)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.3, 1.0)?.get(1)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.2, 1.0)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.3, 1.0)?.get(2)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.3, 1.0)?.get(3)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Download", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            val downloadDialog = waitUiObject2ByText("Video saved", TIMEOUT_VERY_LONG)
            if (downloadDialog != null) {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            writeStrToFile("FeedsSmallVideo:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FeedsSmallVideo:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FeedsSmallVideo_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-消息
     */
    @Test
    fun testMeMsg() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            val login = waitUiObject2ByText("Login", TIMEOUT_MEDIUM)
            if (login != null) {
                login.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                waitUiObject2ByText("Continue with Google", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_SHORT.toLong())
                waitUiObject2ByTextContains("@gmail.com", TIMEOUT_LONG)?.click()
                sleep(TIMEOUT_SHORT.toLong())
                waitUiObject2ByText("Signed in with Google", TIMEOUT_LONG)
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.01, 0.2, 0.8, 1.0, 0.02, 0.2)?.get(0)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            var comments = getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.1, 0.8, 0.0, 1.0, 0.02, 0.9)
            for (i in 0..2) {
                if (comments == null || comments.size == 0) {
                    back()
                    sleep(TIMEOUT_SHORT.toLong())
                    getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.01, 0.2, 0.8, 1.0, 0.02, 0.2)?.get(0)?.click()
                    sleep(TIMEOUT_SHORT.toLong())
                    comments = getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.1, 0.8, 0.0, 1.0, 0.02, 0.9)
                } else {
                    break
                }
            }
            comments!!.get(0).click()
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.FrameLayout", true, 0.8, 1.0, 0.1, 0.8, 0.0, 1.0, 0.02, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            for (i in 0..4) {
                if (waitUiObject2ByText("Share Phoenix", TIMEOUT_SHORT) != null) {
                    break
                } else {
                    getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
            writeStrToFile("MeMsg:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeMsg:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeMsg_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-书签
     */
    @Test
    fun testMeBookmark() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var bookmark = waitUiObject2ByText("Bookmarks", TIMEOUT_MEDIUM)
            if (bookmark == null) {
                bookmark = waitUiObject2ByText("Bookmark", TIMEOUT_SHORT)
            }
            bookmark?.click()
            waitUiObject2ByText("New Folder", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Title", TIMEOUT_MEDIUM)?.setText("Test" ?: "")
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var save = waitUiObject2ByText("Save", TIMEOUT_MEDIUM)
            if (save == null) {
                save = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.02, 0.2).get(0)
            }
            save?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Test", TIMEOUT_MEDIUM)?.let {
                longClick(it)
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val bookmarkMore = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.02, 0.9)
            if (bookmarkMore != null && bookmarkMore.size > 0) {
                bookmarkMore?.get(0)?.click()
                waitUiObject2ByText("Delete", TIMEOUT_MEDIUM)?.click()
            }
            waitUiObject2ByText("Sync", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            if (waitUiObject2ByText("Continue with Google", TIMEOUT_SHORT) != null) {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.3, 0.0, 0.3, 0.0, 0.5, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("MeBookmark:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeBookmark:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeBookmark_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-历史
     */
    @Test
    fun testMeHistory() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("History", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.02, 0.9)?.get(0)?.click()
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Clear", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Clear", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val isCleared = isUiObject2ExistByText("No history", TIMEOUT_MEDIUM)
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.3, 0.0, 0.3, 0.0, 0.5, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            if (isCleared) {
                writeStrToFile("MeHistory:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("MeHistory:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/meHistory_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeHistory:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeHistory_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-我的视频
     */
    @Test
    fun testMeMyVideo() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("My Video", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Local videos", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.8, 0.0, 1.0, 0.02, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            click(width / 2, height / 2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByTextContains("Watched", TIMEOUT_MEDIUM)?.let {
                longClick(it)
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Remove", TIMEOUT_MEDIUM)?.click()
            val isRemoved = isUiObject2ExistByTextContains("No history", TIMEOUT_MEDIUM)
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.3, 0.0, 0.3, 0.0, 0.5, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            if (isRemoved) {
                writeStrToFile("MeMyVideo:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("MeMyVideo:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/meMyVideo_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeMyVideo:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeMyVideo_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-我的音乐
     */
    @Test
    fun testMeMyMusic() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("My Music", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Local music", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.5, 0.0, 1.0, 0.02, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(1)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("New playlist", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2("New playlist", "android.widget.EditText", 0.0, 0.0)?.setText("Test")
            waitUiObject2ByText("Done", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Add songs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.1, 0.5, 0.0, 1.0, 0.02, 0.9)?.get(0)?.click()
            waitUiObject2ByText("Add", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.3, 0.0, 0.3, 0.0, 0.5, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Test", TIMEOUT_MEDIUM)?.let {
                longClick(it)
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.3, 0.0, 0.3, 0.0, 0.5, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("MeMyMusic:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeMyMusic:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeMyMusic_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-广告过滤
     */
    @Test
    fun testMeAdblock() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var adBlock = waitUiObject2ByRes("com.transsion.phoenix:id/adBlocked", TIMEOUT_MEDIUM.toLong())
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Adblocker", TIMEOUT_VERY_SHORT)
            }
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Ads blocked", TIMEOUT_VERY_SHORT)
            }
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Ad block", TIMEOUT_VERY_SHORT)
            }
            adBlock?.click()
            waitUiObject2ByDesc("Ad block", TIMEOUT_MEDIUM.toLong())?.let { adBlockRoot ->
                getChildUiObject2(
                    adBlockRoot,
                    true,
                    "android.widget.Switch",
                    0.0,
                    1.0,
                    0.0,
                    1.0,
                    0.5,
                    1.0,
                    0.5,
                    1.0,
                    true
                )?.click()
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Clear", TIMEOUT_MEDIUM)?.click()
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("MeAdblock:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeAdblock:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeAdblock_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-日(夜)间模式
     */
    @Test
    fun testMeDarkLight() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var darkLight = waitUiObject2ByText("Dark", TIMEOUT_MEDIUM)
            if (darkLight == null) {
                darkLight = waitUiObject2ByText("Light", TIMEOUT_MEDIUM)
            }
            darkLight?.click()
            writeStrToFile("MeDarkLight:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeDarkLight:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeDarkLight_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-Facebook
     */
    @Test
    fun testMeFacebook() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Like us on Facebook", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            if (isAppBackstage(device, pkgName)) {
                amStartApp(device, activity, null)
                sleep(TIMEOUT_VERY_SHORT.toLong())
            } else {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            writeStrToFile("MeFacebook:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeFacebook:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeFacebook_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-分享
     */
    @Test
    fun testMeShare() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Share Phoenix", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            for (i in 0..4) {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                if (waitUiObject2ByText("Share via", TIMEOUT_SHORT) == null) {
                    break
                }
            }
            writeStrToFile("MeShare:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeShare:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeShare_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-帮助反馈
     */
    @Test
    fun testMeHelpFeedback() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 上滑
            swip(0.5, 0.7, 0.5, 0.3)
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 帮助中心
            waitUiObject2ByText("Help and feedback", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_LONG.toLong())
            var views = getUiObject2s("android.view.View", true, 0.8, 1.0, 0.05, 0.2, 0.0, 1.0, 0.02, 0.9)
            for (i in 0..2) {
                if (views == null) {
                    sleep(TIMEOUT_SHORT.toLong())
                    views = getUiObject2s("android.view.View", true, 0.8, 1.0, 0.05, 0.2, 0.0, 1.0, 0.02, 0.9)
                } else {
                    break
                }
            }
            views!!.get(1).click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            for (i in 0..9) {
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                if (waitUiObject2ByText("Helpful", TIMEOUT_SHORT) != null) {
                    break
                }
            }
            swip(0.5, 0.7, 0.5, 0.3)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var helpful = waitUiObject2ByText("Helpful", TIMEOUT_SHORT)
            if (helpful == null) {
                helpful = waitUiObject2ByDesc("Helpful", TIMEOUT_VERY_SHORT.toLong())
            }
            helpful?.click()
            var whatsAppGroup = waitUiObject2ByText("Join WhatsApp group to feedback", TIMEOUT_SHORT)
            if (whatsAppGroup == null) {
                whatsAppGroup = waitUiObject2ByDesc("Join WhatsApp group to feedback", TIMEOUT_VERY_SHORT.toLong())
            }
            whatsAppGroup?.click()
            sleep(TIMEOUT_SHORT.toLong())
            if (isAppBackstage(device, pkgName)) {
                amStartApp(device, activity, null)
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            var likeFacebook = waitUiObject2ByText("Like us on Facebook", TIMEOUT_SHORT)
            if (likeFacebook == null) {
                likeFacebook = waitUiObject2ByDesc("Like us on Facebook", TIMEOUT_VERY_SHORT.toLong())
            }
            likeFacebook?.click()
            sleep(TIMEOUT_SHORT.toLong())
            if (isAppBackstage(device, pkgName)) {
                amStartApp(device, activity, null)
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            waitUiObject2ByDesc("toolbar home", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Help and feedback", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_MEDIUM.toLong())
            waitUiObject2ByRes("J_Feedback_Btn", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("J_Email", TIMEOUT_MEDIUM.toLong())?.setText("test@qq.com" ?: "")
            waitUiObject2ByRes("J_Desc", TIMEOUT_MEDIUM.toLong())?.setText("test" ?: "")
            swip(0.5, 0.7, 0.5, 0.3)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("J_UploadTrigger", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0.1, 0.3, 0.0, 1.0, 0.02, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("J_Btn", TIMEOUT_MEDIUM.toLong())?.click()
            val isSuccess = isUiObject2ExistByText("Thank you for the feedback!", TIMEOUT_MEDIUM)
            if (isSuccess) {
                writeStrToFile("MeHelpFeedback:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("MeHelpFeedback:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/MeHelpFeedback_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeHelpFeedback:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeHelpFeedback_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-打赏
     */
    @Test
    fun testMeReward() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Support developer", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Coffee", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val isCallSuccess = isUiObject2ExistByRes("com.android.vending:id/0_resource_name_obfuscated", TIMEOUT_LONG.toLong())
            if (isCallSuccess) {
                writeStrToFile("MeReward:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("MeReward:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/MeReward_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeReward:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeReward_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-搜索引擎
     */
    @Test
    fun testSettingSearchEngine() {
        try {
            // 进入设置
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 搜索引擎
            waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Bing", TIMEOUT_MEDIUM)?.click()
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            writeStrToFile("SettingSearchEngine:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingSearchEngine:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingSearchEngine_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-图片
     */
    @Test
    fun testSettingImage() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 图片
            waitUiObject2ByText("Image", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Always no image", TIMEOUT_MEDIUM)?.click()
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            writeStrToFile("SettingImage:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingImage:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingImage_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-字体大小
     */
    @Test
    fun testSettingFontSize() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 字体大小
            waitUiObject2ByText("Font size", TIMEOUT_MEDIUM)?.click()
            val seekBar = waitUiObject2sByClazz("android.widget.SeekBar", TIMEOUT_MEDIUM.toLong()).get(0)
            swip(seekBar, "left")
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            writeStrToFile("SettingFontSize:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingFontSize:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingFontSize_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-语言
     */
    @Test
    fun testSettingLanguage() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 语言
            waitUiObject2ByText("Language", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            writeStrToFile("SettingLanguage:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingLanguage:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingLanguage_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-主页(含最常访问)
     */
    @Test
    fun testSettingHomepage() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 主页
            waitUiObject2ByText("Homepage", TIMEOUT_MEDIUM)?.click()
            val homeSwitch = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM.toLong()).getOrNull(0) ?: return
            if (homeSwitch.isChecked) {
                homeSwitch.click()
            }
            backToHome()
            val mostVisited = waitUiObject2ByText("Most Visited", TIMEOUT_SHORT)
            var topY = 0.3
            var bottomY = 0.5
            if (mostVisited != null) {
                topY = mostVisited.getVisibleBounds().top.toDouble() / height
                bottomY = mostVisited.getVisibleBounds().bottom.toDouble() / height
            }
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.3, 0.0, 1.0, bottomY, 0.9).getOrNull(0)?.let {
                longClick(it)
            } ?: return
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.3, 0.0, 0.3, 0.8, 1.0, topY - 0.02, bottomY + 0.02)?.get(0)?.click()
            waitUiObject2ByText("Clear", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("SettingHomepage:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingHomepage:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingHomepage_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-下载
     */
    @Test
    fun testSettingDownloads() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 下载
            waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Concurrent downloads", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("6", TIMEOUT_MEDIUM)?.click()
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            waitUiObject2ByText("Download folder", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val storage = getUiObject2("Internal Storage", "android.widget.TextView", 0.8, 0.0)
            if (storage != null) {
                storage.click()
            }
            waitUiObject2ByText("Choose it", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            writeStrToFile("SettingDownloads:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingDownloads:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingDownloads_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-通知栏
     */
    @Test
    fun testSettingNotification() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 通知栏
            waitUiObject2ByText("Notification", TIMEOUT_MEDIUM)?.click()
            val notificationSwitchs = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM.toLong())
            for (notificationSwitch in notificationSwitchs) {
                if (notificationSwitch.isChecked()) {
                    notificationSwitch.click()
                }
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            writeStrToFile("SettingNotification:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingNotification:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingNotification_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-清理数据
     */
    @Test
    fun testSettingClearData() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 清理数据
            waitUiObject2ByText("Clear data", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Account and password", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Video and Document browsing history", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            var cleanPhoenix: UiObject2? = null
            val tmpCleanPhoenixs = waitUiObject2sByTextContains("Clean up", TIMEOUT_MEDIUM)
            for (tmpCleanPhoenix in tmpCleanPhoenixs) {
                if (tmpCleanPhoenix.isClickable()) {
                    cleanPhoenix = tmpCleanPhoenix
                    break
                }
            }
            if (cleanPhoenix != null && cleanPhoenix.isEnabled()) {
                cleanPhoenix.click()
                for (i in 0..9) {
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    val cleanPhoenixBacks = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)
                    if (cleanPhoenixBacks == null || cleanPhoenixBacks.size == 0) {
                        back()
                    } else {
                        // uiautomator有时点击时会报异常
                        try {
                            cleanPhoenixBacks?.get(0)?.click()
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
            writeStrToFile("SettingClearData:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingClearData:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingClearData_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-检查更新
     */
    @Test
    fun testSettingCheckUpdates() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 检查更新
            waitUiObject2ByText("Check for updates", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            if (waitUiObject2ByText("Check for updates", TIMEOUT_SHORT) == null) {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            writeStrToFile("SettingCheckUpdates:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingCheckUpdates:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingCheckUpdates_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-关于
     */
    @Test
    fun testSettingAbout() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 关于
            waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Product features", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val aboutSwitchs = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM.toLong())
            for (aboutSwitch in aboutSwitchs) {
                if (aboutSwitch.isChecked()) {
                    aboutSwitch.click()
                }
            }
            waitUiObject2ByTextContains("Terms of service", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByTextContains("Privacy policy", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            writeStrToFile("SettingAbout:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingAbout:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingAbout_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-Facebook
     */
    @Test
    fun testSettingFacebook() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // Facebook
            waitUiObject2ByText("Like us on Facebook", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            if (isAppBackstage(device, pkgName)) {
                amStartApp(device, activity, null)
                sleep(TIMEOUT_VERY_SHORT.toLong())
            } else {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            writeStrToFile("SettingFacebook:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingFacebook:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingFacebook_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-Feedback
     */
    @Test
    fun testSettingFeedback() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 用户反馈
            waitUiObject2ByText("Feedback", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("SettingFeedback:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingFeedback:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingFeedback_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-评分
     */
    @Test
    fun testSettingRateUs() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 评分
            waitUiObject2ByText("Rate us", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            if (isAppBackstage(device, pkgName)) {
                amStartApp(device, activity, null)
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            writeStrToFile("SettingRateUs:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingRateUs:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingRateUs_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 设置-恢复默认设置
     */
    @Test
    fun testSettingReset() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 恢复默认
            waitUiObject2ByText("Reset to default settings", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Restore", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            backToHome()
            writeStrToFile("SettingReset:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("SettingReset:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/SettingReset_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 多窗口-普通窗口
     */
    @Test
    fun testTabsNormal() {
        try {
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val normal = waitUiObject2ByText("Normal", TIMEOUT_SHORT)
            val incognito = waitUiObject2ByText("Incognito", TIMEOUT_SHORT)
            if (normal != null && incognito != null) {
                normal.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            } else {
                if (normal == null) {
                    swip(0.3, 0.5, 0.7, 0.5)
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
            getUiObject2s("android.widget.ImageView", true, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.7, 1.0)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            if (normal != null && incognito != null) {
                getUiObject2s("android.widget.ImageView", true, 0.01, 0.3, 0.01, 0.5, 0.5, 1.0, 0.7, 1.0)?.get(0)?.click()
            } else {
                getUiObject2s("android.widget.FrameLayout", false, 0.01, 0.3, 0.01, 0.5, 0.5, 1.0, 0.7, 1.0)?.get(0)?.click()
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            swip(0.5, 0.7, 0.5, 0.3)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.5, 0.3, 0.5, 0.7)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val normalImageViews = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.7, 1.0, 0.2, 0.8)
            if (normalImageViews != null && normalImageViews.size > 0) {
                normalImageViews?.get(0)?.click()
            } else {
                swip(0.3, 0.5, 0.7, 0.5)
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("TabsNormal:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("TabsNormal:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/TabsNormal_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 多窗口-隐私窗口
     */
    @Test
    fun testTabsIncognito() {
        try {
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val normal = waitUiObject2ByText("Normal", TIMEOUT_SHORT)
            val incognito = waitUiObject2ByText("Incognito", TIMEOUT_SHORT)
            if (normal != null && incognito != null) {
                incognito.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            } else {
                getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1.0, 0.03, 0.15)?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                waitUiObject2ByText("New incognito tab", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_SHORT.toLong())
                val dialog = waitUiObject2ByText("OK", TIMEOUT_SHORT)
                if (dialog != null) {
                    dialog.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
                waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            getUiObject2s("android.widget.ImageView", true, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.7, 1.0)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            if (normal != null && incognito != null) {
                getUiObject2s("android.widget.ImageView", true, 0.01, 0.3, 0.01, 0.5, 0.5, 1.0, 0.7, 1.0)?.get(0)?.click()
            } else {
                getUiObject2s("android.widget.FrameLayout", false, 0.01, 0.3, 0.01, 0.5, 0.5, 1.0, 0.7, 1.0)?.get(0)?.click()
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            swip(0.5, 0.7, 0.5, 0.3)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.5, 0.3, 0.5, 0.7)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val incognitoImageViews = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.7, 1.0, 0.2, 0.8)
            if (incognitoImageViews != null && incognitoImageViews.size > 0) {
                incognitoImageViews?.get(0)?.click()
            } else {
                swip(0.7, 0.5, 0.3, 0.5)
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            if (normal != null && incognito != null) {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            writeStrToFile("TabsIncognito:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("TabsIncognito:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/TabsIncognito_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 多窗口-更多操作
     */
    @Test
    fun testTabsMore() {
        try {
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val normal = waitUiObject2ByText("Normal", TIMEOUT_SHORT)
            val incognito = waitUiObject2ByText("Incognito", TIMEOUT_SHORT)
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1.0, 0.03, 0.15)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("New normal tab", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1.0, 0.03, 0.15)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("New incognito tab", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_MEDIUM.toLong())
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1.0, 0.03, 0.15)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Close all incognito tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            if (normal == null || incognito == null) {
                waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1.0, 0.03, 0.15)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Close all tabs", TIMEOUT_MEDIUM)?.click()
            writeStrToFile("TabsMore:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("TabsMore:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/TabsMore_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-下载
     */
    @Test
    fun testFilesDownloads() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 下滑
            swip(0.5, 0.3, 0.5, 0.7)
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 下载
            waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1.0, 0.03, 0.15)?.get(0)?.click()
            waitUiObject2ByText("Add download link", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2sByClazz("android.widget.EditText", TIMEOUT_MEDIUM.toLong()).getOrNull(0)
                ?.setText("https://dldir1.qq.com/weixin/android/weixin7021android1800_arm64.apk")
            waitUiObject2ByText("Download", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Download", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1.0, 0.03, 0.15)?.get(0)?.click()
            waitUiObject2ByText("Start all", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1.0, 0.03, 0.15)?.get(0)?.click()
            waitUiObject2ByText("Pause all", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1.0, 0.03, 0.15)?.get(0)?.click()
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesDownloads:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesDownloads:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesDownloads_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Status saver
     */
    @Test
    fun testFilesStatusSaver() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var statusSaver = waitUiObject2ByText("Status saver", TIMEOUT_MEDIUM)
            if (statusSaver == null) {
                statusSaver = waitUiObject2ByText("Status & Sticker", TIMEOUT_VERY_SHORT)
            }
            statusSaver?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            for (i in 0..2) {
                swip(0.7, 0.5, 0.3, 0.5)
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            val whatsAppTips = waitUiObject2ByText("Manage WhatsApp files here", TIMEOUT_SHORT)
            if (whatsAppTips != null) {
                whatsAppTips.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesStatusSaver:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesStatusSaver:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesStatusSaver_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-WhatsApp
     */
    @Test
    fun testFilesWhatsApp() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("WhatsApp", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            for (i in 0..2) {
                swip(0.7, 0.5, 0.3, 0.5)
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesWhatsApp:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesWhatsApp:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesWhatsApp_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Telegram
     */
    @Test
    fun testFilesTelegram() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val telegram = waitUiObject2ByText("Telegram", TIMEOUT_MEDIUM)
            if (telegram != null) {
                telegram.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                for (i in 0..2) {
                    swip(0.7, 0.5, 0.3, 0.5)
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                writeStrToFile("FilesTelegram:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FilesTelegram:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/FilesTelegram_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesTelegram:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesTelegram_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Videos
     */
    @Test
    fun testFilesVideos() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Videos", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.7, 0.5, 0.3, 0.5)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.3, 0.5, 0.7, 0.5)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.5, 0.0, 1.0, 0.1, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            horizontalScreen()
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesVideos:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesVideos:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesVideos_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Music
     */
    @Test
    fun testFilesMusic() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Music", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.7, 0.5, 0.3, 0.5)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.3, 0.5, 0.7, 0.5)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.3, 0.0, 1.0, 0.1, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(1)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesMusic:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesMusic:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesMusic_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Images
     */
    @Test
    fun testFilesImages() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Images", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.7, 0.5, 0.3, 0.5)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.3, 0.5, 0.7, 0.5)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0.1, 0.3, 0.0, 1.0, 0.1, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            for (i in 0..9) {
                swip(0.7, 0.5, 0.3, 0.5)
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesImages:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesImages:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesImages_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Documents
     */
    @Test
    fun testFilesDocuments() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Documents", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            for (i in 0..5) {
                swip(0.7, 0.5, 0.3, 0.5)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                val firstDoc = getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.3, 0.0, 1.0, 0.1, 0.9).get(0)
                if (firstDoc != null) {
                    firstDoc.click()
                    sleep(TIMEOUT_SHORT.toLong())
                    getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesDocuments:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesDocuments:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesDocuments_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Storage
     */
    @Test
    fun testFilesStorage() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var storage = waitUiObject2ByText("Storage", TIMEOUT_MEDIUM)
            if (storage == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                storage = waitUiObject2ByText("Storage", TIMEOUT_SHORT)
            }
            storage?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesStorage:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesStorage:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesStorage_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Archives
     */
    @Test
    fun testFilesArchives() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var archives = waitUiObject2ByText("Archives", TIMEOUT_MEDIUM)
            if (archives == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                archives = waitUiObject2ByText("Archives", TIMEOUT_SHORT)
            }
            archives?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.3, 0.0, 1.0, 0.1, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_MEDIUM.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesArchives:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesArchives:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesArchives_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Instagram
     */
    @Test
    fun testFilesInstagram() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var instagram = waitUiObject2ByText("Instagram", TIMEOUT_MEDIUM)
            if (instagram == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                instagram = waitUiObject2ByText("Instagram", TIMEOUT_SHORT)
            }
            instagram?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.7, 0.5, 0.3, 0.5)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesInstagram:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesInstagram:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesInstagram_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Offline pages
     */
    @Test
    fun testFilesOfflinePages() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var offlinePages = waitUiObject2ByText("Offline pages", TIMEOUT_MEDIUM)
            if (offlinePages == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                offlinePages = waitUiObject2ByText("Offline pages", TIMEOUT_SHORT)
            }
            offlinePages?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesOfflinePages:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesOfflinePages:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesOfflinePages_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Apps
     */
    @Test
    fun testFilesApps() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var apps = waitUiObject2ByText("Apps", TIMEOUT_MEDIUM)
            if (apps == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                apps = waitUiObject2ByText("Apps", TIMEOUT_SHORT)
            }
            apps?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesApps:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesApps:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesApps_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Others
     */
    @Test
    fun testFilesOthers() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var others = waitUiObject2ByText("Others", TIMEOUT_MEDIUM)
            if (others == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                others = waitUiObject2ByText("Others", TIMEOUT_SHORT)
            }
            others?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesOthers:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesOthers:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesOthers_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Junk files
     */
    @Test
    fun testFilesJunkfiles() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Junk files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var cleanJunkFiles = waitUiObject2ByTextContains("Clean up", TIMEOUT_MEDIUM)
            if (cleanJunkFiles == null) {
                cleanJunkFiles = waitUiObject2ByTextContains("Safe clean", TIMEOUT_MEDIUM)
            }
            if (cleanJunkFiles != null && cleanJunkFiles.isEnabled()) {
                cleanJunkFiles.click()
                sleep(TIMEOUT_LONG.toLong())
                // 关闭广告
                closeAdDialog()
                for (i in 0..9) {
                    // 返回
                    val junkFilesBacks = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)
                    if (junkFilesBacks == null || junkFilesBacks.size == 0) {
                        // 处理退出清理确认弹窗
                        val exit = waitUiObject2ByText("Exit", TIMEOUT_SHORT)
                        if (exit != null) {
                            exit.click()
                            sleep(TIMEOUT_VERY_SHORT.toLong())
                        } else {
                            back()
                        }
                    } else {
                        // uiautomator有时点击时会报异常
                        try {
                            junkFilesBacks?.get(0)?.click()
                        } catch (e: Exception) {
                            back()
                        }
                    }
                    sleep(TIMEOUT_VERY_SHORT.toLong())

                    if (waitUiObject2ByText("Me", TIMEOUT_VERY_SHORT) != null) {
                        break
                    }
                }
            } else {
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                // 处理退出确认弹窗
                val exit = waitUiObject2ByText("Exit", TIMEOUT_SHORT)
                if (exit != null) {
                    exit.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
            writeStrToFile("FilesJunkfiles:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesJunkfiles:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesJunkfiles_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Phone boost
     */
    @Test
    fun testFilesPhoneBoost() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Phone boost", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_LONG.toLong())
            // 关闭广告
            closeAdDialog()
            // 处理添加至主页弹窗
            if (waitUiObject2ByText("Add", TIMEOUT_MEDIUM) != null) {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            for (i in 0..9) {
                val boostBacks = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)
                if (boostBacks == null || boostBacks.size == 0) {
                    back()
                } else {
                    // uiautomator有时点击时会报异常
                    try {
                        boostBacks?.get(0)?.click()
                    } catch (e: Exception) {
                        back()
                    }
                }
                sleep(TIMEOUT_VERY_SHORT.toLong())
                if (waitUiObject2ByTextContains("Me", TIMEOUT_VERY_SHORT) != null) {
                    break
                }
            }
            writeStrToFile("FilesPhoneBoost:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesPhoneBoost:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesPhoneBoost_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Clean Up Phoenix
     */
    @Test
    fun testFilesCleanPhoenix() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            var cleanPhoenix = waitUiObject2ByText("Clean Up Phoenix", TIMEOUT_MEDIUM)
            if (cleanPhoenix == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                cleanPhoenix = waitUiObject2ByText("Clean Up Phoenix", TIMEOUT_MEDIUM)
            }
            cleanPhoenix?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Account and password", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            var cleanUp: UiObject2? = null
            val tmpCleanPhoenixs = waitUiObject2sByTextContains("Clean up", TIMEOUT_MEDIUM)
            for (tmpCleanPhoenix in tmpCleanPhoenixs) {
                if (tmpCleanPhoenix.isClickable()) {
                    cleanUp = tmpCleanPhoenix
                    break
                }
            }
            if (cleanUp != null && cleanUp.isEnabled()) {
                cleanUp.click()
                sleep(TIMEOUT_LONG.toLong())
                // 关闭广告
                closeAdDialog()
                for (i in 0..9) {
                    val whatsAppBacks = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)
                    if (whatsAppBacks == null || whatsAppBacks.size == 0) {
                        back()
                    } else {
                        // uiautomator有时点击时会报异常
                        try {
                            whatsAppBacks?.get(0)?.click()
                        } catch (e: Exception) {
                            back()
                        }
                    }
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    if (waitUiObject2ByText("Me", TIMEOUT_VERY_SHORT) != null) {
                        break
                    }
                }
            } else {
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            writeStrToFile("FilesCleanPhoenix:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesCleanPhoenix:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesCleanPhoenix_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Large File Cleanup
     */
    @Test
    fun testFilesCleanLargeFile() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var cleanLargeFile = waitUiObject2ByText("Large File Cleanup", TIMEOUT_MEDIUM)
            if (cleanLargeFile == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                cleanLargeFile = waitUiObject2ByText("Large File Cleanup", TIMEOUT_MEDIUM)
            }
            cleanLargeFile?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var cleanUp = waitUiObject2ByTextContains("Clean up", TIMEOUT_MEDIUM)
            if (cleanUp != null && !cleanUp.isEnabled()) {
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.5, 1.0, 0.02, 0.5)?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                cleanUp = waitUiObject2ByTextContains("Clean up", TIMEOUT_MEDIUM)
            }
            cleanUp?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Clean", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_LONG.toLong())
            // 关闭广告
            closeAdDialog()
            for (i in 0..9) {
                val videosBacks = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)
                if (videosBacks == null || videosBacks.size == 0) {
                    back()
                } else {
                    // uiautomator有时点击时会报异常
                    try {
                        videosBacks?.get(0)?.click()
                    } catch (e: Exception) {
                        back()
                    }
                }

                sleep(TIMEOUT_VERY_SHORT.toLong())
                if (waitUiObject2ByText("Me", TIMEOUT_VERY_SHORT) != null) {
                    break
                }
            }
            writeStrToFile("FilesCleanLargeFile:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesCleanLargeFile:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesCleanLargeFile_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Clean Up Videos
     */
    @Test
    fun testFilesCleanVideos() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var cleanVideos = waitUiObject2ByText("Clean Up Videos", TIMEOUT_MEDIUM)
            if (cleanVideos == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                cleanVideos = waitUiObject2ByText("Clean Up WhatsApp", TIMEOUT_MEDIUM)
            }
            cleanVideos?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Other videos", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val firstVideo = getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0.1, 0.5, 0.0, 1.0, 0.1, 0.9).get(0)
            if (firstVideo != null) {
                firstVideo.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                val checkBox = waitUiObject2sByClazz("android.widget.CheckBox", TIMEOUT_MEDIUM.toLong()).get(0)
                if (!checkBox.isChecked()) {
                    checkBox.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            } else {
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val cleanUp = waitUiObject2ByTextContains("Clean up", TIMEOUT_MEDIUM)
            if (cleanUp != null && cleanUp.isEnabled()) {
                cleanUp.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                waitUiObject2ByText("Delete", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_LONG.toLong())
                // 关闭广告
                closeAdDialog()
                for (i in 0..9) {
                    val videosBacks = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)
                    if (videosBacks == null || videosBacks.size == 0) {
                        back()
                    } else {
                        // uiautomator有时点击时会报异常
                        try {
                            videosBacks?.get(0)?.click()
                        } catch (e: Exception) {
                            back()
                        }
                    }

                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    if (waitUiObject2ByText("Me", TIMEOUT_VERY_SHORT) != null) {
                        break
                    }
                }
            } else {
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            writeStrToFile("FilesCleanVideos:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesCleanVideos:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesCleanVideos_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Clean Up WhatsApp
     */
    @Test
    fun testFilesCleanWhatsApp() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var cleanWhatsApp = waitUiObject2ByText("Clean Up WhatsApp", TIMEOUT_MEDIUM)
            if (cleanWhatsApp == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                cleanWhatsApp = waitUiObject2ByText("Clean Up WhatsApp", TIMEOUT_MEDIUM)
            }
            if (cleanWhatsApp != null) {
                cleanWhatsApp.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                val cleanUp = waitUiObject2ByTextContains("Clean up", TIMEOUT_MEDIUM)
                if (cleanUp != null && cleanUp.isEnabled()) {
                    cleanUp.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    for (i in 0..9) {
                        val whatsAppBacks = getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)
                        if (whatsAppBacks == null || whatsAppBacks.size == 0) {
                            back()
                        } else {
                            // uiautomator有时点击时会报异常
                            try {
                                whatsAppBacks?.get(0)?.click()
                            } catch (e: Exception) {
                                back()
                            }
                        }

                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        if (waitUiObject2ByText("Me", TIMEOUT_VERY_SHORT) != null) {
                            break
                        }
                    }
                } else {
                    getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
                writeStrToFile("FilesCleanWhatsApp:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FilesCleanWhatsApp:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/FilesCleanWhatsApp_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesCleanWhatsApp:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesCleanWhatsApp_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Clean Telegram
     */
    @Test
    fun testFilesCleanTelegram() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var cleanTelegram = waitUiObject2ByText("Clean Telegram", TIMEOUT_MEDIUM)
            if (cleanTelegram == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                cleanTelegram = waitUiObject2ByText("Clean Telegram", TIMEOUT_MEDIUM)
            }
            if (cleanTelegram != null) {
                cleanTelegram.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                writeStrToFile("FilesCleanTelegram:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FilesCleanTelegram:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/filesCleanTelegram_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesCleanTelegram:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesCleanTelegram_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Recent documents
     */
    @Test
    fun testFilesRecentDocuments() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var recentDocuments = waitUiObject2ByText("Recent documents", TIMEOUT_MEDIUM)
            if (recentDocuments == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                recentDocuments = waitUiObject2ByText("Recent documents", TIMEOUT_MEDIUM)
            }
            // 最近打开文档
            if (recentDocuments != null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                writeStrToFile("FilesRecentDocuments:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FilesRecentDocuments:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/FilesRecentDocuments_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesRecentDocuments:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesRecentDocuments_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Wallpaper
     */
    @Test
    fun testFilesWallpaper() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var wallpaper = waitUiObject2ByText("Wallpaper", TIMEOUT_MEDIUM)
            if (wallpaper == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                wallpaper = waitUiObject2ByText("Wallpaper", TIMEOUT_MEDIUM)
            }
            wallpaper?.click()
            waitUiObject2ByText("Select wallpaper", TIMEOUT_MEDIUM)?.click()
            for (i in 0..19) {
                sleep(TIMEOUT_SHORT.toLong())
                val firstImgs = getUiObject2s("android.view.View", true, 0.2, 0.4, 0.2, 0.5, 0.0, 1.0, 0.05, 0.9)
                if (firstImgs != null && firstImgs.size > 6) {
                    firstImgs?.get(0)?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                    break
                }
            }
            waitUiObject2ByText("Set as", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Both", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            waitUiObject2ByText("Download", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesWallpaper:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesWallpaper:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesWallpaper_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Ringtones
     */
    @Test
    fun testFilesRingtones() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var ringtones = waitUiObject2ByText("Ringtones", TIMEOUT_MEDIUM)
            if (ringtones == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                ringtones = waitUiObject2ByText("Ringtones", TIMEOUT_MEDIUM)
            }
            ringtones?.click()
            waitUiObject2ByText("Select ringtone", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.3, 0.0, 1.0, 0.1, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val uiObject2s = waitUiObject2sByText("Set", TIMEOUT_MEDIUM)
            if (uiObject2s != null && uiObject2s.size > 0) {
                for (i in uiObject2s.indices) {
                    val set = uiObject2s.get(i)
                    if (i == 0) {
                        set.click()
                        val goSetting = waitUiObject2ByText("Go to settings", TIMEOUT_SHORT)
                        if (goSetting != null) {
                            goSetting.click()
                            val settingSwitch = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM.toLong()).get(0)
                            if (!settingSwitch.isChecked()) {
                                settingSwitch.click()
                            }
                            back()
                            sleep(TIMEOUT_VERY_SHORT.toLong())
                        }
                    } else {
                        set.click()
                    }
                }
            }
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("FilesRingtones:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesRingtones:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesRingtones_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Compress files
     */
    @Test
    fun testFilesCompressFiles() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var compressFiles = waitUiObject2ByText("Compress files", TIMEOUT_MEDIUM)
            if (compressFiles == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                compressFiles = waitUiObject2ByText("Compress files", TIMEOUT_MEDIUM)
            }
            compressFiles?.click()
            waitUiObject2ByText("Select files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Images", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val checkBox = waitUiObject2sByClazz("android.widget.CheckBox", TIMEOUT_MEDIUM.toLong()).getOrNull(0) ?: return
            if (!checkBox.isChecked) {
                checkBox.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Compress 1 file", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Done", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_MEDIUM.toLong())
            val compressed = waitUiObject2ByTextContains("have been compressed", TIMEOUT_LONG)
            if (compressed != null) {
                writeStrToFile("FilesCompressFiles:PASS" + "\n", mainPathFile)
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            } else {
                writeStrToFile("FilesCompressFiles:FAILED" + "\n", mainPathFile)
                screenshot(resultFolder.toString() + "/FilesCompressFiles_" + getCurTimeForFile() + ".jpg")
            }
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesCompressFiles:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesCompressFiles_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 文件-Unzip files
     */
    @Test
    fun testFilesUnzipFiles() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var unzipFiles = waitUiObject2ByText("Unzip files", TIMEOUT_MEDIUM)
            if (unzipFiles == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                unzipFiles = waitUiObject2ByText("Unzip files", TIMEOUT_MEDIUM)
            }
            unzipFiles?.click()
            waitUiObject2ByText("Select files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.3, 0.0, 1.0, 0.1, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val unzip = waitUiObject2ByText("Unzip", TIMEOUT_MEDIUM)
            if (unzip != null) {
                unzip.click()
                sleep(TIMEOUT_SHORT.toLong())
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            val unzipView = waitUiObject2ByText("Unzip and view", TIMEOUT_MEDIUM)
            if (unzipView != null && unzipView.isEnabled()) {
                unzipView.click()
                sleep(TIMEOUT_SHORT.toLong())
                getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.5, 0.3, 0.5, 0.7)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Unzipped files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.3, 0.0, 1.0, 0.1, 0.9).getOrNull(0)?.let {
                longClick(it)
            } ?: return
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val selectAll = waitUiObject2ByText("Select all", TIMEOUT_SHORT)
            if (selectAll != null) {
                selectAll.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Home", TIMEOUT_MEDIUM)?.click()
            writeStrToFile("FilesUnzipFiles:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FilesUnzipFiles:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FilesUnzipFiles_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 个人中心-退出登录
     */
    @Test
    fun testMeLogout() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 下滑
            swip(0.5, 0.3, 0.5, 0.7)
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 退出登录
            waitUiObject2ByText("Signed in with Google", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.02, 0.2)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Sign out", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Sign out", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            writeStrToFile("MeLogout:PASS" + "\n", mainPathFile)
            backToHome()
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeLogout:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeLogout_" + getCurTimeForFile() + ".jpg")
            backToApp()
            backToHome()
        }
    }

    /**
     * 退出-网页工具栏
     */
    @Test
    fun testWebpageMenuExit() {
        try {
            // 启动浏览器
            if (isAppBackstage(device, pkgName)) {
                startApp(pkgName)
                sleep(TIMEOUT_MEDIUM.toLong())
            }

            execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com")
            sleep(TIMEOUT_SHORT.toLong())
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            waitUiObject2ByText("Exit", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            execCmdByUiDevice(device, "am force-stop " + pkgName)
            writeStrToFile("WebpageMenuExit:PASS" + "\n", mainPathFile)
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("WebpageMenuExit:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/WebpageMenuExit_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 退出-Me
     */
    @Test
    fun testMeExit() {
        try {
            // 启动浏览器
            if (isAppBackstage(device, pkgName)) {
                startApp(pkgName)
                sleep(TIMEOUT_MEDIUM.toLong())
            }

            // 个人中心退出
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            waitUiObject2ByText("Exit", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            execCmdByUiDevice(device, "am force-stop " + pkgName)
            writeStrToFile("MeExit:PASS" + "\n", mainPathFile)
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("MeExit:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/MeExit_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 退出-Back
     */
    @Test
    fun testBackExit() {
        try {
            // 启动浏览器
            if (isAppBackstage(device, pkgName)) {
                startApp(pkgName)
                sleep(TIMEOUT_MEDIUM.toLong())
            }

            // 硬件back弹窗退出
            backToHome()
            backExitBrowser()
            writeStrToFile("BackExit:PASS" + "\n", mainPathFile)
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("BackExit:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/BackExit_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开网页
     */
    @Test
    fun testFirstThirdCallWebpage() {
        try {
            var isSuccess = testThirdCall("webpage", true)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/FirstThirdCallWebpage_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("FirstThirdCallWebpage:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FirstThirdCallWebpage:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/FirstThirdCallWebpage_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FirstThirdCallWebpage:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FirstThirdCallWebpage_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开视频
     */
    @Test
    fun testFirstThirdCallVideo() {
        try {
            var isSuccess = testThirdCall("video", true)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/FirstThirdCallVideo_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("FirstThirdCallVideo:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FirstThirdCallVideo:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/FirstThirdCallVideo_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FirstThirdCallVideo:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FirstThirdCallVideo_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开音乐
     */
    @Test
    fun testFirstThirdCallMusic() {
        try {
            var isSuccess = testThirdCall("music", true)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/FirstThirdCallMusic_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("FirstThirdCallMusic:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FirstThirdCallMusic:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/FirstThirdCallMusic_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FirstThirdCallMusic:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FirstThirdCallMusic_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开doc
     */
    @Test
    fun testFirstThirdCallDoc() {
        try {
            var isSuccess = testThirdCall("doc", true)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/FirstThirdCallDoc_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("FirstThirdCallDoc:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FirstThirdCallDoc:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/FirstThirdCallDoc_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FirstThirdCallDoc:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FirstThirdCallDoc_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开ppt
     */
    @Test
    fun testFirstThirdCallPpt() {
        try {
            var isSuccess = testThirdCall("ppt", true)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/FirstThirdCallPpt_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("FirstThirdCallPpt:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FirstThirdCallPpt:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/FirstThirdCallPpt_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FirstThirdCallPpt:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FirstThirdCallPpt_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开xls
     */
    @Test
    fun testFirstThirdCallXls() {
        try {
            var isSuccess = testThirdCall("xls", true)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/FirstThirdCallXls_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("FirstThirdCallXls:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FirstThirdCallXls:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/FirstThirdCallXls_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FirstThirdCallXls:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FirstThirdCallXls_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开pdf
     */
    @Test
    fun testFirstThirdCallPdf() {
        try {
            var isSuccess = testThirdCall("pdf", true)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/FirstThirdCallPdf_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("FirstThirdCallPdf:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FirstThirdCallPdf:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/FirstThirdCallPdf_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FirstThirdCallPdf:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FirstThirdCallPdf_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开epub
     */
    @Test
    fun testFirstThirdCallEpub() {
        try {
            var isSuccess = testThirdCall("epub", true)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/FirstThirdCallEpub_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("FirstThirdCallEpub:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FirstThirdCallEpub:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/FirstThirdCallEpub_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FirstThirdCallEpub:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FirstThirdCallEpub_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开img
     */
    @Test
    fun testFirstThirdCallImg() {
        try {
            var isSuccess = testThirdCall("img", true)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/FirstThirdCallImg_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("FirstThirdCallImg:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FirstThirdCallImg:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/FirstThirdCallImg_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FirstThirdCallImg:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FirstThirdCallImg_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开zip
     */
    @Test
    fun testFirstThirdCallZip() {
        try {
            var isSuccess = testThirdCall("zip", true)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/FirstThirdCallZip_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("FirstThirdCallZip:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FirstThirdCallZip:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/FirstThirdCallZip_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FirstThirdCallZip:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FirstThirdCallZip_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开txt
     */
    @Test
    fun testFirstThirdCallTxt() {
        try {
            var isSuccess = testThirdCall("txt", true)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/FirstThirdCallTxt_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("FirstThirdCallTxt:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("FirstThirdCallTxt:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/FirstThirdCallTxt_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("FirstThirdCallTxt:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/FirstThirdCallTxt_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方打开网页
     */
    @Test
    fun testThirdCallWebpage() {
        try {
            var isSuccess = testThirdCall("webpage", false)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/ThirdCallWebpage_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("ThirdCallWebpage:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("ThirdCallWebpage:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/ThirdCallWebpage_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("ThirdCallWebpage:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/ThirdCallWebpage_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开视频
     */
    @Test
    fun testThirdCallVideo() {
        try {
            var isSuccess = testThirdCall("video", false)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/ThirdCallVideo_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("ThirdCallVideo:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("ThirdCallVideo:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/ThirdCallVideo_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("ThirdCallVideo:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/ThirdCallVideo_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开音乐
     */
    @Test
    fun testThirdCallMusic() {
        try {
            var isSuccess = testThirdCall("music", false)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/ThirdCallMusic_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("ThirdCallMusic:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("ThirdCallMusic:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/ThirdCallMusic_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("ThirdCallMusic:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/ThirdCallMusic_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开doc
     */
    @Test
    fun testThirdCallDoc() {
        try {
            var isSuccess = testThirdCall("doc", false)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/ThirdCallMusic_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("ThirdCallDoc:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("ThirdCallDoc:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/ThirdCallDoc_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("ThirdCallDoc:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/ThirdCallDoc_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开ppt
     */
    @Test
    fun testThirdCallPpt() {
        try {
            var isSuccess = testThirdCall("ppt", false)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/ThirdCallPpt_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("ThirdCallPpt:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("ThirdCallPpt:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/ThirdCallPpt_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("ThirdCallPpt:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/ThirdCallPpt_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开xls
     */
    @Test
    fun testThirdCallXls() {
        try {
            var isSuccess = testThirdCall("xls", false)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/ThirdCallXls_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("ThirdCallXls:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("ThirdCallXls:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/ThirdCallXls_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("ThirdCallXls:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/ThirdCallXls_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开pdf
     */
    @Test
    fun testThirdCallPdf() {
        try {
            var isSuccess = testThirdCall("pdf", false)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/ThirdCallXls_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("ThirdCallPdf:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("ThirdCallPdf:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/ThirdCallPdf_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("ThirdCallPdf:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/ThirdCallPdf_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开epub
     */
    @Test
    fun testThirdCallEpub() {
        try {
            var isSuccess = testThirdCall("epub", false)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/ThirdCallEpub_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("ThirdCallEpub:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("ThirdCallEpub:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/ThirdCallEpub_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("ThirdCallEpub:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/ThirdCallEpub_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开img
     */
    @Test
    fun testThirdCallImg() {
        try {
            var isSuccess = testThirdCall("img", false)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/ThirdCallImg_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("ThirdCallImg:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("ThirdCallImg:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/ThirdCallImg_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("ThirdCallImg:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/ThirdCallImg_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开zip
     */
    @Test
    fun testThirdCallZip() {
        try {
            var isSuccess = testThirdCall("zip", false)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/ThirdCallZip_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("ThirdCallZip:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("ThirdCallZip:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/ThirdCallZip_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("ThirdCallZip:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/ThirdCallZip_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 第三方首次打开txt
     */
    @Test
    fun testThirdCallTxt() {
        try {
            var isSuccess = testThirdCall("txt", false)

            // 失败截图
            var isScreenshot = false
            if (!isSuccess) {
                screenshot(resultFolder.toString() + "/ThirdCallTxt_" + getCurTimeForFile() + ".jpg")
                isScreenshot = true
            }

            // 回到首页->退出浏览器
            backToHome()
            if (isSuccess) {
                isSuccess = backExitBrowser()
            } else {
                backExitBrowser()
            }

            // 处理日志信息
            if (isSuccess) {
                writeStrToFile("ThirdCallTxt:PASS" + "\n", mainPathFile)
            } else {
                writeStrToFile("ThirdCallTxt:FAILED" + "\n", mainPathFile)
                if (!isScreenshot) {
                    screenshot(resultFolder.toString() + "/ThirdCallTxt_" + getCurTimeForFile() + ".jpg")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("ThirdCallTxt:Exception" + "\n", mainPathFile)
            writeStrToFile(getExceptionMsg(e), mainPathFile)
            screenshot(resultFolder.toString() + "/ThirdCallTxt_" + getCurTimeForFile() + ".jpg")
        }
    }

    private fun testThirdCall(file: String, isFirst: Boolean): Boolean {
        // 强制结束，避免anr弹窗遮挡
        execCmdByUiDevice(device, "am force-stop " + pkgName)

        if (isFirst) {
            // 清理数据
            execCmdByUiDevice(device, "pm clear " + pkgName)
        }

        // 执行第三方调用
        if (file == "webpage") {
            execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -d https://qq.com")
        } else if (file == "video") {
            execCmdByUiDevice(
                device,
                "am start -a android.intent.action.VIEW -p " + pkgName + " -t video/* -d file:///sdcard/testfile/video_mp4_youku.mp4"
            )
        } else if (file == "music") {
            execCmdByUiDevice(
                device,
                "am start -a android.intent.action.VIEW -p " + pkgName + " -t audio/flac -d file:///sdcard/testfile/music_flac_不为谁而作的歌.flac"
            )
        } else if (file == "doc") {
            execCmdByUiDevice(
                device,
                "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/msword -d file:///sdcard/testfile/document_docx_1MB.docx"
            )
        } else if (file == "ppt") {
            execCmdByUiDevice(
                device,
                "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/vnd.ms-powerpoint -d file:///sdcard/testfile/document_pptx.pptx"
            )
        } else if (file == "xls") {
            execCmdByUiDevice(
                device,
                "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/vnd.ms-excel -d file:///sdcard/testfile/document_xlsx_5000.xlsx"
            )
        } else if (file == "pdf") {
            execCmdByUiDevice(
                device,
                "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/pdf -d file:///sdcard/testfile/document_pdf_keph101.pdf"
            )
        } else if (file == "epub") {
            execCmdByUiDevice(
                device,
                "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/epub+zip -d file:///sdcard/testfile/document_epub_doupocangqiong.epub"
            )
        } else if (file == "img") {
            execCmdByUiDevice(
                device,
                "am start -a android.intent.action.VIEW -p " + pkgName + " -t image/png -d file:///sdcard/testfile/img_png_640x960_529k.png"
            )
        } else if (file == "zip") {
            execCmdByUiDevice(
                device,
                "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/zip -d file:///sdcard/testfile/archive_zip_10MB.zip"
            )
        } else if (file == "txt") {
            execCmdByUiDevice(
                device,
                "am start -a android.intent.action.VIEW -p " + pkgName + " -t text/richtext -d file:///sdcard/testfile/document_txt_1MB.txt"
            )
        }
        sleep(TIMEOUT_LONG.toLong())

        // 先跳过闪屏
        if (isNeedSkipSplash()) {
            skipSplash()
        }
        sleep(TIMEOUT_SHORT.toLong())

        // 再处理各业务
        var isSuccess = false
        if (file == "webpage") {
            if (waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong()) != null) {
                isSuccess = true
            }
        } else {
            if (isFirst) {
                // 处理弹窗
                var continueBtn = waitUiObject2ByText("Continue", TIMEOUT_MEDIUM)
                if (continueBtn == null) {
                    continueBtn = waitUiObject2ByText("متابعة", TIMEOUT_VERY_SHORT)
                }
                continueBtn?.click()
                var allow = waitUiObject2ByText("Allow", TIMEOUT_MEDIUM)
                if (allow == null) {
                    allow = waitUiObject2ByText("ALLOW", TIMEOUT_MEDIUM)
                }
                allow?.click()
                sleep(TIMEOUT_SHORT.toLong())
            }

            // 再处理业务
            if (file == "video") {
                sleep(TIMEOUT_LONG.toLong())
                horizontalScreen()
                isSuccess = isThirdCallSuccess("video")
            } else if (file == "music") {
                sleep(TIMEOUT_LONG.toLong())
                isSuccess = isThirdCallSuccess("music")
            } else if (file == "doc") {
                // Edit Fit screen  Search  Save as
                var fitScreen = waitUiObject2ByText("Fit screen", TIMEOUT_VERY_LONG)
                if (fitScreen == null) {
                    fitScreen = waitUiObject2ByText("ملائمة الشاشة", TIMEOUT_VERY_SHORT)
                }
                if (fitScreen != null) {
                    isSuccess = true
                }
            } else if (file == "ppt") {
                // Edit Fullscreen  Save as
                var fullScreen = waitUiObject2ByText("Fullscreen", TIMEOUT_VERY_LONG)
                if (fullScreen == null) {
                    fullScreen = waitUiObject2ByText("ملء الشاشة", TIMEOUT_VERY_SHORT)
                }
                if (fullScreen != null) {
                    isSuccess = true
                }
            } else if (file == "xls") {
                // Edit Search  Save as
                var search = waitUiObject2ByText("Search", TIMEOUT_VERY_LONG)
                if (search == null) {
                    search = waitUiObject2ByText("بحث", TIMEOUT_VERY_SHORT)
                }
                if (search != null) {
                    isSuccess = true
                }
            } else if (file == "pdf") {
                // Search Fullscreen
                var fullScreen = waitUiObject2ByText("Fullscreen", TIMEOUT_VERY_LONG)
                if (fullScreen == null) {
                    fullScreen = waitUiObject2ByText("ملء الشاشة", TIMEOUT_VERY_SHORT)
                }
                if (fullScreen != null) {
                    isSuccess = true
                }
            } else if (file == "epub") {
                sleep(TIMEOUT_LONG.toLong())
                isSuccess = isThirdCallSuccess("epub")
            } else if (file == "img") {
                // Share Info Delete
                var info = waitUiObject2ByText("Info", TIMEOUT_VERY_LONG)
                if (info == null) {
                    info = waitUiObject2ByText("معلومات", TIMEOUT_VERY_SHORT)
                }
                if (info != null) {
                    isSuccess = true
                }
            } else if (file == "zip") {
                var unzipView = waitUiObject2ByText("Unzip and view", TIMEOUT_VERY_LONG)
                if (unzipView == null) {
                    unzipView = waitUiObject2ByText("قم بفك الضغط ثم العرض", TIMEOUT_VERY_SHORT)
                }
                if (unzipView != null) {
                    isSuccess = true
                }
            } else if (file == "txt") {
                sleep(TIMEOUT_LONG.toLong())
                isSuccess = isThirdCallSuccess("txt")
            }
        }
        return isSuccess
    }

    @After
    public override fun afterTest() {
        super.afterTest()
    }
}
