package com.bbtest.other

import com.bbtest.common.PerCommon
import com.bbtest.common.ShellCommon.forceStopApp
import com.bbtest.utils.FileUtil.createFolder
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class MemHomeMenu : PerCommon() {
    private val resultFolder = File(perFolder, "common")

    @Before
    override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
    }

    @Test
    fun testMemHomeMenu() {
        try {
            // 先强制停止
            forceStopApp(device, BROWSER_PHX, null)
            sleep(3000)

            // 启动浏览器
            startApp(BROWSER_PHX)
            sleep((60 * 1000).toLong())

            // 开始监控
            startMonitorMainMem(resultFolder, "mem_home-menu", BROWSER_PHX)
            startMonitorSubMem(resultFolder, "mem_home-menu", BROWSER_PHX)

            // 测试前: 等待30s
            sleep((30 * 1000).toLong())

            // 测试场景:遍历菜单
            val toolbarMenu = waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())
            if (toolbarMenu == null) {
                waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_SHORT.toLong())
                waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_SHORT.toLong())
                waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_SHORT.toLong())
                waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_SHORT.toLong())
                back()
                sleep(TIMEOUT_SHORT.toLong())
            } else {
                waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
                sleep(TIMEOUT_SHORT.toLong())
                back()
                sleep(TIMEOUT_SHORT.toLong())
                waitUiObject2ByDesc("toolbar multiWindow", TIMEOUT_MEDIUM.toLong())?.click()
                sleep(TIMEOUT_SHORT.toLong())
                back()
                sleep(TIMEOUT_SHORT.toLong())
            }

            // 测试后: 等待30s
            sleep((30 * 1000).toLong())
            screenshot("${resultFolder}mem_home-menu_${BROWSER_PHX}.jpg")

            // 结束监控
            stopMonitorMainMem()
            stopMonitorSubMem()

            // 回到首页
            backToHome()

            // 关闭所有多窗口->退出浏览器->强制停止
            closeAllTabs(BROWSER_PHX)
            exitBrowser(BROWSER_PHX)
            forceStopApp(device, BROWSER_PHX, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }
}
