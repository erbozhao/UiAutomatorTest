package com.bbtest.compatible

import com.bbtest.common.PhxCommon
import com.bbtest.common.ShellCommon.amStartApp
import com.bbtest.common.ShellCommon.isAppBackstage
import com.bbtest.utils.CommonUtil.getCurTimeForFile
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.deleteFile
import com.bbtest.utils.FileUtil.writeStrToFile
import com.bbtest.utils.ShellCommand.execCmdByUiDevice
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class ChannelTest : PhxCommon() {
    private val resultFolder = File(rootFolder, "compatible")

    private val channelFile = File(resultFolder, "channel.txt")

    @Before
    override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
        deleteFile(channelFile)
        createFile(channelFile)
    }

    @Test
    fun testStartOldPhx() {
        try {
            // 启动应用(3.6以前版本)
            startApp(pkgName)
            sleep(TIMEOUT_MEDIUM.toLong())
            // 跳过升级弹窗
            var update = waitUiObject2ByText("UPDATE", TIMEOUT_MEDIUM)
            if (update == null) {
                update = waitUiObject2ByText("Update", TIMEOUT_VERY_SHORT)
            }
            if (update != null) {
                update.click()
                sleep(TIMEOUT_SHORT.toLong())
                if (isAppBackstage(device, pkgName)) {
                    amStartApp(device, activity, null)
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
            // 退出应用
            val menu = waitUiObject2ByRes("com.transsion.phoenix:id/vd", TIMEOUT_MEDIUM.toLong())
            if (menu != null) {
                menu.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                waitUiObject2ByRes("com.transsion.phoenix:id/lw", TIMEOUT_MEDIUM.toLong())?.click()
                sleep(TIMEOUT_SHORT.toLong())
            }
            execCmdByUiDevice(device, "am force-stop $pkgName")
        } catch (e: Exception) {
            e.printStackTrace()
            screenshot("${resultFolder}/startOldPhx_${getCurTimeForFile()}.jpg")
        }
    }

    @Test
    fun testStartNewPhx() {
        try {
            // 启动应用(3.6及以后版本)
            startApp(pkgName)
            sleep(TIMEOUT_LONG.toLong())
            skipSplash()
            backToHome()
            backExitBrowser()
            execCmdByUiDevice(device, "am force-stop $pkgName")
        } catch (e: Exception) {
            e.printStackTrace()
            screenshot("${resultFolder}/startNewPhx_${getCurTimeForFile()}.jpg")
        }
    }

    @Test
    fun testGetChid() {
        try {
            // 启动应用(覆盖安装3.6、4.6等版本，会存在闪屏)
            startApp(pkgName)
            sleep(TIMEOUT_LONG.toLong())
            skipSplash()
            backToHome()
            this.chid
            backExitBrowser()
        } catch (e: Exception) {
            e.printStackTrace()
            screenshot("${resultFolder}/getChid_${getCurTimeForFile()}.jpg")
        }
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }

    val chid: Unit
        /**
         * 获取渠道ID
         */
        get() {
            try {
                var me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM)
                if (me == null) {
                    me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT)
                }
                if (me != null) {
                    me.click()
                } else {
                    waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
                }
                var settings = waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)
                if (settings == null) {
                    settings = waitUiObject2ByText("الإعدادات", TIMEOUT_VERY_SHORT)
                }
                settings?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                skipAppDialog()
                val searchEngine = waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM)
                if (searchEngine == null) {
                    waitUiObject2ByText("محرك البحث", TIMEOUT_VERY_SHORT)
                }
                sleep(TIMEOUT_VERY_SHORT.toLong())
                swip(0.5, 0.8, 0.5, 0.2)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                var aboutPhoenix = waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM)
                if (aboutPhoenix == null) {
                    aboutPhoenix = waitUiObject2ByText("حول Phoenix", TIMEOUT_VERY_SHORT)
                }
                aboutPhoenix?.click()
                val productFeatures = waitUiObject2ByText("Product features", TIMEOUT_MEDIUM)
                if (productFeatures == null) {
                    waitUiObject2ByText("خواص المنتج", TIMEOUT_VERY_SHORT)
                }
                sleep(TIMEOUT_VERY_SHORT.toLong())
                val phxIcon = getUiObject2s("android.widget.ImageView", true, 0.2, 0.5, 0.1, 0.5, 0.2, 0.8, 0.02, 0.5).getOrNull(0)
                for (i in 0..4) {
                    phxIcon?.click()
                }
                waitUiObject2ByText("Basic Info", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_SHORT.toLong())
                // 将获取到的渠道id存入本地
                var activeCHID = ""
                var currentCHID = ""
                val chidInfo = waitUiObject2ByTextContains("countrycode", TIMEOUT_MEDIUM)?.getText() ?: return
                val chidInfoParts: List<String> = chidInfo.split("|")
                for (chidInfoPartRaw in chidInfoParts) {
                    val chidInfoPart = chidInfoPartRaw.trim()
                    if (chidInfoPart.contains("activeCHID")) {
                        activeCHID = chidInfoPart.substring(chidInfoPart.indexOf("activeCHID=") + 11)
                    } else if (chidInfoPart.contains("currentCHID")) {
                        currentCHID = chidInfoPart.substring(chidInfoPart.indexOf("currentCHID") + 11)
                    }
                }
                writeStrToFile("activeCHID=$activeCHID,currentCHID=$currentCHID", channelFile)
                backToHome()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}
