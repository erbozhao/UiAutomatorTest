package com.bbtest.other

import com.bbtest.common.PerCommon
import com.bbtest.common.ShellCommon.amStartApp
import com.bbtest.common.ShellCommon.forceStopApp
import com.bbtest.common.ShellCommon.getActivity
import com.bbtest.common.ShellCommon.isAppBackstage
import com.bbtest.stable.threads.MemoryThread
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.deleteFile
import com.bbtest.utils.ShellCommand.execCmdByUiDevice
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class MemWebpageMenu : PerCommon() {
    private val resultFile = File(perFolder, "mem_webpage-menu.txt")

    @Before
    override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        deleteFile(resultFile)
        createFile(resultFile)
    }

    @Test
    fun testMemWebpageMenu() {
        testBrowser(BROWSER_PHX)
    }

    private fun testBrowser(pkgName: String) {
        try {
            // 先强制停止
            forceStopApp(device, pkgName, null)
            sleep(3000)

            // 启动浏览器
            startApp(pkgName)
            sleep((60 * 1000).toLong())

            // 开始监控
            val memoryThread = MemoryThread(0.5f, BROWSER_PHX, device, resultFile, 1)
            memoryThread.start()

            // 测试前: 等待30s
            sleep((30 * 1000).toLong())

            // 测试场景:打开网页->遍历工具栏
            execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com")
            sleep(TIMEOUT_MEDIUM.toLong())
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val bookmarkHistory = waitUiObject2ByText("Bookmark\n" + "&history", TIMEOUT_MEDIUM)
            if (bookmarkHistory == null) {
                var bookmark = waitUiObject2ByText("Bookmark", TIMEOUT_MEDIUM)
                if (bookmark == null) {
                    bookmark = waitUiObject2ByText("Bookmarks", TIMEOUT_SHORT)
                }
                bookmark?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                waitUiObject2ByText("History", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            } else {
                bookmarkHistory.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("My video", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("My music", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
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
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var darkLight = waitUiObject2ByText("Dark", TIMEOUT_MEDIUM)
            if (darkLight == null) {
                darkLight = waitUiObject2ByText("Light", TIMEOUT_SHORT)
            }
            if (darkLight == null) {
                darkLight = waitUiObject2ByText("Night", TIMEOUT_SHORT)
                if (darkLight == null) {
                    darkLight = waitUiObject2ByText("Normal", TIMEOUT_SHORT)
                }
                if (darkLight != null) {
                    darkLight.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    waitUiObject2ByText("Dark theme", TIMEOUT_SHORT)?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    if (isAppBackstage(device, pkgName)) {
                        val activity = getActivity(device, pkgName, null)
                        amStartApp(device, activity, null)
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                    }
                }
            } else {
                darkLight.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Settings", TIMEOUT_SHORT)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())

            // 测试后: 等待30s
            sleep((30 * 1000).toLong())

            // 结束监控
            memoryThread.setIsTimeOver(true)
            memoryThread.join()

            // 回到首页
            backToHome()

            // 关闭所有多窗口->退出浏览器->强制停止
            closeAllTabs(pkgName)
            exitBrowser(pkgName)
            forceStopApp(device, pkgName, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }
}
