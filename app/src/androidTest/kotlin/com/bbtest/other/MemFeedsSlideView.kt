package com.bbtest.other

import com.bbtest.common.PerCommon
import com.bbtest.common.ShellCommon.forceStopApp
import com.bbtest.utils.FileUtil.createFolder
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class MemFeedsSlideView : PerCommon() {
    private val resultFolder = File(perFolder, "common")

    @Before
    override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
    }

    @Test
    fun testFeedsSlideViewMem() {
        try {
            // 先强制停止
            forceStopApp(device, BROWSER_PHX, null)

            // 启动浏览器
            startApp(BROWSER_PHX)

            // 等待90s
            sleep((60 * 1000).toLong())

            // 开始监控
            startMonitorMainMem(resultFolder, "mem_feeds-slideview", BROWSER_PHX)
            startMonitorSubMem(resultFolder, "mem_feeds-slideview", BROWSER_PHX)

            // 测试前: 等待30s
            sleep((30 * 1000).toLong())

            // 测试场景: 滑动查看
            for (i in 0..19) {
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(3000)
                val linearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.1, 0.8, 0.0, 1.0, 0.02, 0.9)
                if (linearLayouts != null && linearLayouts.size > 0) {
                    val firstlinearLayout = linearLayouts.first()
                    val firstVideo =
                        getChildUiObject2(firstlinearLayout, false, "android.widget.ImageView", 0.8, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, true)
                    if (firstVideo != null) {
                        val firstVideoBottom =
                            getChildUiObject2(firstlinearLayout, false, "android.widget.LinearLayout", 0.8, 1.0, 0.0, 0.1, 0.0, 1.0, 0.0, 1.0, false)
                        firstVideoBottom?.click()
                    } else {
                        val firstSmallVideo =
                            getChildUiObject2(firstlinearLayout, false, "android.widget.ImageView", 0.4, 0.6, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, true)
                        if (firstSmallVideo != null) {
                            firstSmallVideo.click()
                        } else {
                            firstlinearLayout.click()
                        }
                    }
                }
                sleep(5000)
                back()
                sleep(3000)
            }

            // 测试后: 等待30s
            sleep((30 * 1000).toLong())
            screenshot("${resultFolder}mem_feeds-slideview_${BROWSER_PHX}.jpg")

            // 结束监控
            stopMonitorMainMem()
            stopMonitorSubMem()

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
