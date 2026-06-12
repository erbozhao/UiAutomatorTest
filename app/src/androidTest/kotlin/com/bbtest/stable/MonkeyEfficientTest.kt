package com.bbtest.stable

import android.text.TextUtils
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.bbtest.common.MonkeyCommon
import com.bbtest.common.ShellCommon.amStartApp
import com.bbtest.common.ShellCommon.getActivities
import com.bbtest.common.ShellCommon.isAppBackstage
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.CommonUtil.getCurTimeForLog
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.readFile
import com.bbtest.utils.FileUtil.writeStrToFile
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Random

/**
 * @Author: onuszhao
 * @Date: 2021/1/8 11:37
 * @Description: 写长线程跑，容易被系统杀掉，且加入电池优化策略也没啥用，故切换至5分钟跑一次
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class MonkeyEfficientTest : MonkeyCommon() {
    private val resultFolder = File(rootFolder, "monkey")
    private val monkeyFile = File(resultFolder, "monkey.txt")
    private val monkeyInfoFile = File(downloadsDir, "monkey.txt")

    private var pkgName = ""
    private var activity = ""

    // 定义场景
    private var scenes = ""

    // 定义是否坐标点击
    private var isPointClick = false

    @Before
    override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
        createFile(monkeyFile)
        // 获取需要跑的包相关信息
        val testInfo = readFile(monkeyInfoFile).trim { it <= ' ' }
        if (TextUtils.isEmpty(testInfo)) {
            pkgName = "com.transsion.phoenix"
            scenes = "qb://home/feeds"
        } else {
            val testInfoParts = testInfo.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            // 默认包名
            pkgName = testInfoParts[0]
            if (TextUtils.isEmpty(pkgName)) {
                pkgName = "com.transsion.phoenix"
            }
            // 默认场景：主页
            if (testInfoParts.size >= 2) {
                scenes = testInfoParts[1]
            } else {
                scenes = "qb://home/feeds"
            }
            // 默认坐标点击
            if (testInfoParts.size >= 3) {
                try {
                    isPointClick = testInfoParts[2].toBoolean()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        // 获取activity(解决一个应用存在多个主activity)
        val mainActivities: List<String> = getActivities(device, pkgName, null)
        for (mainActivity in mainActivities) {
            amStartApp(device, mainActivity, monkeyFile)
            CommonUtil.sleep(5000)
            if (!isAppBackstage(device, pkgName)) {
                activity = mainActivity
                break
            }
        }
        writeStrToFile("${getCurTimeForLog()}  $pkgName\n", monkeyFile)
    }

    @Test
    fun testMonkey() {
        scenes = "qb://mymusic" // qb://camera,qb://bookmark,qb://download_add_link,qb://video/feedsvideo,qb://video/minivideo
        startMonkey(3)
    }

    @Test
    fun testMonkeyShort() {
        startMonkey(1)
    }

    @Test
    fun testMonkeyMedium() {
        startMonkey(3)
    }

    @Test
    fun testMonkeyLong() {
        startMonkey(5)
    }

    private fun startMonkey(minute: Long) {
        try {
            /**
             * 初始化事件，控制概率
             */
            val events: List<String> = initRandomEvent(60, 10, 8, 10, 8, 0, 0, 4, 0, 0)

            // 进入指定场景
            gotoSscenes(scenes, monkeyFile)

            if (scenes == "notification") {
                testNotification()
            } else {
                // 网页通过点击坐标测试
                if (scenes.startsWith("https://") || scenes.startsWith("http://")) {
                    isPointClick = true
                }

                val startTime = System.currentTimeMillis()
                var endTime: Long = 0
                var preScenesNum = 0
                while (true) {
                    //  先确认是否在前台
                    if (isAppBackstage(pkgName, monkeyFile)) {
                        //  再判断浏览器进程是否存在，并切到前台/启动浏览器
                        if (isProcessExist(pkgName, monkeyFile)) {
                            startActivity(1000, activity, monkeyFile)
                        } else {
                            startActivity(5000, activity, monkeyFile)
                        }
                    }

                    // 发起模拟事件
                    val event = events.get(Random().nextInt(events.size))
                    if (event == "click") {
                        if (isPointClick) {
                            click(monkeyFile)
                        } else {
                            clickByUiObject(monkeyFile)
                        }
                    } else if (event == "swipeUp") {
                        swipeUp(monkeyFile)
                    } else if (event == "swipeDown") {
                        swipeDown(monkeyFile)
                    } else if (event == "swipeLeft") {
                        swipeLeft(monkeyFile)
                    } else if (event == "swipeRight") {
                        swipeRight(monkeyFile)
                    } else if (event == "longClick") {
                        longClick(monkeyFile)
                    }

                    // 计算时间
                    endTime = System.currentTimeMillis()
                    val costTime = endTime - startTime
                    if (costTime > minute * 60 * 1000) {
                        break
                    } else {
                        var curScenesNum = 0
                        if (isPointClick) {
                            curScenesNum = (costTime / (10 * 1000)).toInt()
                        } else {
                            curScenesNum = (costTime / (20 * 1000)).toInt()
                        }
                        if (curScenesNum > preScenesNum) {
                            // 进入指定场景
                            gotoSscenes(scenes, monkeyFile)
                            preScenesNum = curScenesNum
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun testNotification() {
        // 常驻新闻通知
        openNotification()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val residentNews = waitUiObject2ByRes("com.transsion.phoenix:id/news_frame", TIMEOUT_MEDIUM.toLong())
        if (residentNews != null) {
            swip(residentNews, "down")
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val newsPre = waitUiObject2ByRes("com.transsion.phoenix:id/news_pre", TIMEOUT_MEDIUM.toLong())
            if (newsPre != null) {
                newsPre.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            val newsNext = waitUiObject2ByRes("com.transsion.phoenix:id/news_next", TIMEOUT_MEDIUM.toLong())
            if (newsNext != null) {
                newsNext.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            val newsContent = waitUiObject2ByRes("com.transsion.phoenix:id/news_content", TIMEOUT_MEDIUM.toLong())
            if (newsContent != null) {
                newsContent.click()
                sleep(TIMEOUT_SHORT.toLong())
            }
        }
        // 常驻清理通知
        openNotification()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val newWeatherContainer2 = waitUiObject2ByRes("com.transsion.phoenix:id/newWeatherContainer2", TIMEOUT_MEDIUM.toLong())
        if (newWeatherContainer2 != null) {
            newWeatherContainer2.click()
            sleep(TIMEOUT_SHORT.toLong())
        }
        openNotification()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val cleanContainer = waitUiObject2ByRes("com.transsion.phoenix:id/cleanContainer", TIMEOUT_MEDIUM.toLong())
        if (cleanContainer != null) {
            cleanContainer.click()
            sleep(TIMEOUT_SHORT.toLong())
        }
        openNotification()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val status = waitUiObject2ByRes("com.transsion.phoenix:id/tv_status_text", TIMEOUT_MEDIUM.toLong())
        if (status != null) {
            status.click()
            sleep(TIMEOUT_SHORT.toLong())
        }
        openNotification()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val sticker = waitUiObject2ByRes("com.transsion.phoenix:id/layout_sticker_part_normal", TIMEOUT_MEDIUM.toLong())
        if (sticker != null) {
            sticker.click()
            sleep(TIMEOUT_SHORT.toLong())
        }
        // 清理通知
        openNotification()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val fileCleanJunkInfo = waitUiObject2ByRes("com.transsion.phoenix:id/file_clean_tv_junk_info", TIMEOUT_MEDIUM.toLong())
        if (fileCleanJunkInfo != null) {
            fileCleanJunkInfo.click()
            sleep(TIMEOUT_SHORT.toLong())
        }
        // 退出通知栏
        back()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        // 回到主页
        if (!isAppBackstage(pkgName, monkeyFile)) {
            backToHome()
        }
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }
}
