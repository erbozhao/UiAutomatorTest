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
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class BrowseMutiWindowDetailPage : PerCommon() {
    private val resultFolder = File(perFolder, "memoryleak")
    private val resultFile = File(resultFolder, "memoryleak_49_browsemutiwindow-detailpage.txt")

    @Before
    override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
        deleteFile(resultFile)
        createFile(resultFile)
    }

    @Test
    fun testBrowseMutiWindowDetailPage() {
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
            val startDumpheapThread = DumpheapThread(BROWSER_PHX, device, model + "_BrowseMutiWindow-DetailPage-Start", resultFolder)
            startDumpheapThread.start()
            startDumpheapThread.join()

            // 测试前: 等待30s
            sleep((30 * 1000).toLong())

            // 测试场景: 单窗口连续浏览网页30次
            for (i in 0..19) {
                if (i > 0) {
                    var multiWindow1 = waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM)
                    if (multiWindow1 == null) {
                        multiWindow1 = waitUiObject2ByDesc("toolbar multiWindow", TIMEOUT_SHORT.toLong())
                    }
                    multiWindow1?.click()
                    sleep(3000)
                    getUiObject2s("android.widget.ImageView", true, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.7, 1.0)?.firstOrNull()?.click()
                    sleep(3000)
                }
                clickSearchBox(BROWSER_PHX, false)
                sleep(3000)
                setTextAndGo(BROWSER_PHX, "www.baidu.com")
                sleep(5000)
                if (i == 0) {
                    skipAppDialog()
                    skipOtherDialog()
                }
            }
            closeAllTabs(BROWSER_PHX)

            // 测试后: 压后台，并等待30s
            sleep((30 * 1000).toLong())
            pressHome(device, null)
            sleep((30 * 1000).toLong())

            // 结束监控
            memoryThread.setIsTimeOver(true)
            memoryThread.join()
            val endDumpheapThread = DumpheapThread(BROWSER_PHX, device, model + "_BrowseMutiWindow-DetailPage-End", resultFolder)
            endDumpheapThread.start()
            endDumpheapThread.join()

            // 调回前台
            val activity = getActivity(device, BROWSER_PHX, null)
            amStartApp(device, activity, null)
            sleep(3000)

            // 回到主页->关闭所有多窗口->退出浏览器->强制停止
            backToHome()
            exitBrowser(BROWSER_PHX)
            forceStopApp(device, BROWSER_PHX, null)
        } catch (e: Exception) {
            e.printStackTrace()
            screenshot("${resultFolder}/Img_${model}_BrowseMutiWindow-DetailPage-Exception_${BROWSER_PHX}_${getCurTimeForFile()}.jpg")
        }
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }
}
