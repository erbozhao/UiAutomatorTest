package com.bbtest.other

import com.bbtest.common.PerCommon
import com.bbtest.common.ShellCommon.forceStopApp
import com.bbtest.utils.FileUtil.createFolder
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class MemFilesTraverse : PerCommon() {
    private val resultFolder = File(perFolder, "common")

    @Before
    override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
    }

    @Test
    fun testFilesTraverseMem() {
        try {
            // 先强制停止
            forceStopApp(device, BROWSER_PHX, null)
            sleep(3000)

            // 启动浏览器
            startApp(BROWSER_PHX)
            sleep((60 * 1000).toLong())

            // 开始监控
            startMonitorMainMem(resultFolder, "mem_filestraverse", BROWSER_PHX)
            startMonitorSubMem(resultFolder, "mem_filestraverse", BROWSER_PHX)

            // 测试前: 等待30s
            sleep((30 * 1000).toLong())

            // 测试场景:遍历Files界面
            val texts: MutableList<String> = ArrayList<String>()
            val toolbarMenu = waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())
            if (toolbarMenu != null) {
                toolbarMenu.click()
                sleep(TIMEOUT_SHORT.toLong())
                texts.add("Internal Storage")
                texts.add("WhatsApp")
                texts.add("Instagram")
                texts.add("Videos")
                texts.add("Music")
                texts.add("Images")
                texts.add("Documents")
                texts.add("Apps")
                texts.add("Archives")
                texts.add("Offline pages")
                texts.add("Others")
                texts.add("junk files")
                texts.add("Cleaner for WhatsApp")
                texts.add("WhatsApp status saver")
            } else {
                texts.add("Downloads")
                texts.add("Status saver")
                texts.add("WhatsApp")
                texts.add("Telegram")
                texts.add("Videos")
                texts.add("Music")
                texts.add("Images")
                texts.add("Documents")
                texts.add("Storage")
                texts.add("More")
                texts.add("Archives")
                texts.add("Instagram")
                texts.add("Offline pages")
                texts.add("Apps")
                texts.add("Others")
                texts.add("Junk files")
                texts.add("Phone boost")
                texts.add("Clean Up WhatsApp")
                texts.add("Clean Up Videos")
                texts.add("Clean Up Phoenix")
                texts.add("Wallpaper")
                texts.add("Ringtones")
                texts.add("Compress files")
                texts.add("Unzip files")
            }
            val files = waitUiObject2sByText("Files", TIMEOUT_MEDIUM)
            if (files.size > 1) {
                files.get(1)!!.click()
            } else {
                files.get(0)!!.click()
            }
            sleep(TIMEOUT_SHORT.toLong())
            for (text in texts) {
                if (text == "Clean Up WhatsApp") {
                    swip(0.5, 0.7, 0.5, 0.3)
                    sleep(TIMEOUT_SHORT.toLong())
                }
                var textUiObject2 = waitUiObject2ByText(text, TIMEOUT_SHORT)
                if (textUiObject2 == null) {
                    textUiObject2 = waitUiObject2ByTextContains(text, TIMEOUT_VERY_SHORT)
                }
                if (textUiObject2 != null) {
                    textUiObject2.click()
                    sleep(TIMEOUT_SHORT.toLong())
                    if (text != "More") {
                        back()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                    }
                }
            }

            // 测试后: 等待30s
            sleep((30 * 1000).toLong())
            screenshot(resultFolder.toString() + "mem_filestraverse_" + BROWSER_PHX + ".jpg")

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
