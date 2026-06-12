package com.bbtest.perform.memoryleak

import com.bbtest.common.PerCommon
import com.bbtest.common.ShellCommon.amStartApp
import com.bbtest.common.ShellCommon.forceStopApp
import com.bbtest.common.ShellCommon.getActivity
import com.bbtest.common.ShellCommon.pressHome
import com.bbtest.stable.threads.DumpheapThread
import com.bbtest.stable.threads.MemoryThread
import com.bbtest.tools.DeviceInfo
import com.bbtest.utils.CommonUtil.getCurTimeForFile
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.deleteFile
import com.bbtest.utils.ShellCommand.execCmdByUiDevice
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class BrowseOneWindowDetailPage : PerCommon() {
    private val resultFolder = File(perFolder, "memoryleak")
    private val resultFile = File(resultFolder, "memoryleak_48_browseonewindow-detailpage.txt")

    @Before
    override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
        deleteFile(resultFile)
        createFile(resultFile)
    }

    @Test
    fun testBrowseOneWindowDetailPage() {
        // 获取设备名
        val deviceInfo = DeviceInfo(device)
        val model = deviceInfo.model
        try {
            // 先强制停止
            forceStopApp(device, BROWSER_PHX, null)
            sleep(3000)

            // 启动浏览器
            startApp(BROWSER_PHX)
            sleep((60 * 1000).toLong())

            // 开始监控
            val memoryThread = MemoryThread(0.5f, BROWSER_PHX, device, resultFile, 1)
            memoryThread.start()
            val startDumpheapThread = DumpheapThread(BROWSER_PHX, device, model + "_BrowseOneWindow-DetailPage-Start", resultFolder)
            startDumpheapThread.start()
            startDumpheapThread.join()

            // 测试前: 等待30s
            sleep((30 * 1000).toLong())

            // 测试场景: 单窗口连续浏览网页20次
            for (i in 0..19) {
                execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://www.baidu.com")
                sleep(5000)
                waitUiObject2ByDesc("toolbar home", TIMEOUT_MEDIUM.toLong())?.click()
                sleep(3000)
            }

            // 测试后: 压后台，并等待30s
            sleep((30 * 1000).toLong())
            pressHome(device, null)
            sleep((30 * 1000).toLong())

            // 结束监控
            memoryThread.setIsTimeOver(true)
            memoryThread.join()
            val endDumpheapThread = DumpheapThread(BROWSER_PHX, device, model + "_BrowseOneWindow-DetailPage-End", resultFolder)
            endDumpheapThread.start()
            endDumpheapThread.join()

            // 调回前台
            val activity = getActivity(device, BROWSER_PHX, null)
            amStartApp(device, activity, null)
            sleep(3000)

            // 回到主页->关闭所有多窗口->退出浏览器->强制停止
            backToHome()
            closeAllTabs(BROWSER_PHX)
            exitBrowser(BROWSER_PHX)
            forceStopApp(device, BROWSER_PHX, null)
        } catch (e: Exception) {
            e.printStackTrace()
            screenshot("${resultFolder}/Img_${model}_BrowseOneWindow-DetailPage-Exception_${BROWSER_PHX}_${getCurTimeForFile()}.jpg")
        }
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }
}
