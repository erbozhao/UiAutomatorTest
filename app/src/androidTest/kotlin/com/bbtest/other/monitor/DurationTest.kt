package com.bbtest.other.monitor

import android.os.Build
import com.bbtest.common.PhxCommon
import com.bbtest.common.ShellCommon.amStartApp
import com.bbtest.common.ShellCommon.clearBufferCache
import com.bbtest.common.ShellCommon.pressHome
import com.bbtest.utils.CommonUtil.getCurTimeForLog
import com.bbtest.utils.CommonUtil.keepDecimalPoint
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.writeStrToFile
import com.bbtest.utils.ShellCommand.execCmdByUiDevice
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class DurationTest : PhxCommon() {
    private val resultFolder = File(rootFolder, "monitor")
    private val durationFile = File(resultFolder, "duration.txt")

    @Before
    public override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
        createFile(durationFile)
    }

    @After
    public override fun afterTest() {
        super.afterTest()
    }

    @Test
    fun testHome() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testHome ******************" + "\n", durationFile)
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            startTime = System.currentTimeMillis()
            startApp(pkgName)
            sleep(TIMEOUT_LONG.toLong())
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
            endTime = System.currentTimeMillis()
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }

        // 获取时长
        val testTime = getTestTime(startTime, endTime)
        val countTime = getCountTime("end|home|main")
        writeStrToFile("场景:主页,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testHome ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testWeather() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testWeather ******************" + "\n", durationFile)
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            var weathers = getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.4, 0.0, 0.2, 0.0, 0.4, 0.02, 0.2)
            if (weathers == null || weathers.size == 0) {
                backToHome()
                sleep(TIMEOUT_LONG.toLong())
                weathers = getUiObject2s("android.widget.LinearLayout", true, 0.0, 0.4, 0.0, 0.2, 0.0, 0.4, 0.02, 0.2)
            }
            if (weathers != null && weathers.size > 0) {
                startTime = System.currentTimeMillis()
                weathers.get(0)!!.click()
                sleep(TIMEOUT_LONG.toLong())
                waitUiObject2ByText("Air quality", TIMEOUT_MEDIUM)
                back()
                endTime = System.currentTimeMillis()
                sleep(TIMEOUT_MEDIUM.toLong())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }

        // 获取时长
        val testTime = getTestTime(startTime, endTime)
        val countTime = getCountTime("end|weather")
        writeStrToFile("场景:天气,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testWeather ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testSearch() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testSearch ******************" + "\n", durationFile)
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            startTime = System.currentTimeMillis()
            clickSearchBox(false)
            sleep(TIMEOUT_MEDIUM.toLong())
            back()
            endTime = System.currentTimeMillis()
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }

        // 获取时长
        val testTime = getTestTime(startTime, endTime)
        val countTime = getCountTime("end|search")
        writeStrToFile("场景:搜索,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testSearch ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testBrowser() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testBrowser ******************" + "\n", durationFile)
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            startTime = System.currentTimeMillis()
            execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com")
            sleep(TIMEOUT_MEDIUM.toLong())
            back()
            endTime = System.currentTimeMillis()
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }

        // 获取时长
        val testTime = getTestTime(startTime, endTime)
        val countTime = getCountTime("end|browser")
        writeStrToFile("场景:浏览,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testBrowser ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testCamera() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testCamera ******************" + "\n", durationFile)
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            startTime = System.currentTimeMillis()
            waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_MEDIUM.toLong())
            back()
            endTime = System.currentTimeMillis()
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }

        // 获取时长
        val testTime = getTestTime(startTime, endTime)
        val countTime = getCountTime("end|camera")
        writeStrToFile("场景:扫一扫,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testCamera ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFeedsForyou() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFeedsForyou ******************" + "\n", durationFile)
        val testTime = testFeedsTab("For you")

        // 获取时长
        val countTime = getCountTime("end|home|130001")
        writeStrToFile("场景:Feeds-For you-Tab,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFeedsForyou ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFeedsForyouNews() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFeedsForyouNews ******************" + "\n", durationFile)
        val testTime = testFeedsNews("For you")

        // 获取时长
        val countTime = getCountTime("end|news|130001")
        writeStrToFile("场景:Feeds-For you-News,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFeedsForyouNews ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFeedsShortVideo() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFeedsShortVideo ******************" + "\n", durationFile)
        val testTime = testFeedsTab("Short Video")

        // 获取时长
        val countTime = getCountTime("end|home|150006")
        writeStrToFile("场景:Feeds-Short Video-Tab,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFeedsShortVideo ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFeedsShortVideoDetail() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFeedsShortVideoDetail ******************" + "\n", durationFile)
        val testTime = testFeedsMiniVideo("Short Video")

        // 获取时长
        val countTime = getCountTime("end|minivideo|150006")
        writeStrToFile("场景:Feeds-Short Video-Detail,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFeedsShortVideoDetail ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFeedsVideo() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFeedsVideo ******************" + "\n", durationFile)
        val testTime = testFeedsTab("Video")

        // 获取上报时长
        val countTime = getCountTime("end|home|130008")
        writeStrToFile("场景:Feeds-Video-Tab,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFeedsVideo ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFeedsVideoDetail() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFeedsVideoDetail ******************" + "\n", durationFile)
        val testTime = testFeedsVideo("Video")

        // 获取时长
        val countTime = getCountTime("end|feedsvideo_detail|130008")
        writeStrToFile("场景:Feeds-Video-Detail,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFeedsVideoDetail ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFeedsHotGirl() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFeedsHotGirl ******************" + "\n", durationFile)
        val testTime = testFeedsTab("Hot Girl")

        // 获取时长
        val countTime = getCountTime("end|home|130027")
        writeStrToFile("场景:Feeds-Hot Girl-Tab,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFeedsHotGirl ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFeedsHotGirlDetail() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFeedsHotGirlDetail ******************" + "\n", durationFile)
        val testTime = testFeedsImg("Hot Girl")

        // 获取时长
        val countTime = getCountTime("end|image_detail|130027")
        writeStrToFile("场景:Feeds-Hot Girl-Detail,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFeedsHotGirlDetail ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testDownload() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testDownload ******************" + "\n", durationFile)
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            startTime = System.currentTimeMillis()
            waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_MEDIUM.toLong())
            back()
            endTime = System.currentTimeMillis()
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }

        // 获取时长
        val testTime = getTestTime(startTime, endTime)
        val countTime = getCountTime("end|download")
        writeStrToFile("场景:下载,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testDownload ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testMe() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testMe ******************" + "\n", durationFile)
        val testTime = testMeItems("Me")

        // 获取时长
        val countTime = getCountTime("end|user_center|main")
        writeStrToFile("场景:个人中心,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testMe ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testMeUser() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testMeUser ******************" + "\n", durationFile)
        val testTime = testMeItems("User")

        // 获取时长
        val countTime = getCountTime("end|user_center|user")
        writeStrToFile("场景:个人中心-用户,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testMeUser ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testMeMsg() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testMeMsg ******************" + "\n", durationFile)
        val testTime = testMeItems("Msg")

        // 获取时长
        val countTime = getCountTime("end|user_center|message_center")
        writeStrToFile("场景:个人中心-消息,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testMeMsg ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testMeBookmarks() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testMeBookmarks ******************" + "\n", durationFile)
        val testTime = testMeItems("Bookmarks")

        // 获取时长
        val countTime = getCountTime("end|user_center|bookmark")
        writeStrToFile("场景:个人中心-书签,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testMeBookmarks ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testMeHistory() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testMeHistory ******************" + "\n", durationFile)
        val testTime = testMeItems("History")

        // 获取时长
        val countTime = getCountTime("end|user_center|history")
        writeStrToFile("场景:个人中心-历史,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testMeHistory ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testMeFavorites() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testMeFavorites ******************" + "\n", durationFile)
        val testTime = testMeItems("Favorites")

        // 获取时长
        val countTime = getCountTime("end|user_center|favorites")
        writeStrToFile("场景:个人中心-收藏,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testMeFavorites ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testMeMyVideo() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testMeMyVideo ******************" + "\n", durationFile)
        val testTime = testMeItems("My Video")

        // 获取时长
        val countTime = getCountTime("end|user_center|myvideo")
        writeStrToFile("场景:个人中心-我的视频,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testMeMyVideo ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testMeMyMusic() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testMeMyMusic ******************" + "\n", durationFile)
        val testTime = testMeItems("My Music")

        // 获取时长
        val countTime = getCountTime("end|user_center|mymusic")
        writeStrToFile("场景:个人中心-我的音乐,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testMeMyMusic ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testMeAdsBlocked() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testMeAdsBlocked ******************" + "\n", durationFile)
        val testTime = testMeItems("Adblocker")

        // 获取时长
        val countTime = getCountTime("end|settings|adblock")
        writeStrToFile("场景:个人中心-广告过滤,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testMeAdsBlocked ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testMeSettings() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testMeSettings ******************" + "\n", durationFile)
        val testTime = testMeItems("Settings")

        // 获取时长
        val countTime = getCountTime("end|settings|null")
        writeStrToFile("场景:个人中心-设置,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testMeSettings ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFiles() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFiles ******************" + "\n", durationFile)
        val testTime = testFilesItems("Files")

        // 获取时长
        val countTime = getCountTime("end|file|main")
        writeStrToFile("场景:文件,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFiles ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesStatusSaver() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesStatusSaver ******************" + "\n", durationFile)
        val testTime = testFilesItems("Status & Sticker")

        // 获取时长
        val countTime = getCountTime("end|file|status saver")
        writeStrToFile("场景:文件-Status & Sticker,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesStatusSaver ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesWhatsapp() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesWhatsapp ******************" + "\n", durationFile)
        val testTime = testFilesItems("WhatsApp")

        // 获取时长
        val countTime = getCountTime("end|file|whatsapp")
        writeStrToFile("场景:文件-WhatsApp,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesWhatsapp ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesVideos() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesVideos ******************" + "\n", durationFile)
        val testTime = testFilesItems("Videos")

        // 获取上报时长
        val countTime = getCountTime("end|file|video")
        writeStrToFile("场景:文件-Videos,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesVideos ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesVideoPlayer() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesVideoPlayer ******************" + "\n", durationFile)
        val testTime = testFilesItems("VideoPlayer")

        // 获取上报时长
        val countTime = getCountTime("end|videoplayer")
        writeStrToFile("场景:文件-Videos-视频播放器,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesVideoPlayer ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesMusic() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesMusic ******************" + "\n", durationFile)
        val testTime = testFilesItems("Music")

        // 获取上报时长
        val countTime = getCountTime("end|file|music")
        writeStrToFile("场景:文件-Music,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesMusic ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesMusicPlayer() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesMusicPlayer ******************" + "\n", durationFile)
        val testTime = testFilesItems("MusicPlayer")

        // 获取上报时长
        val countTime = getCountTime("end|music_player")
        writeStrToFile("场景:文件-Music-音乐播放器,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesMusicPlayer ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesImages() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesImages ******************" + "\n", durationFile)
        val testTime = testFilesItems("Images")

        // 获取上报时长
        val countTime = getCountTime("end|file|images")
        writeStrToFile("场景:文件-Images,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesImages ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesImageReader() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesImageReader ******************" + "\n", durationFile)
        val testTime = testFilesItems("ImageReader")

        // 获取上报时长
        val countTime = getCountTime("end|image_reader")
        writeStrToFile("场景:文件-Images-图片查看器,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesImageReader ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesDocuments() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesDocuments ******************" + "\n", durationFile)
        val testTime = testFilesItems("Documents")

        // 获取上报时长
        val countTime = getCountTime("end|file|documents")
        writeStrToFile("场景:文件-Documents,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesDocuments ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesDocumentsDOC() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesDocumentsDOC ******************" + "\n", durationFile)
        val testTime = testFilesItems("Documents-DOC")

        // 获取上报时长
        val countTime = getCountTime("end|file_reader|word")
        writeStrToFile("场景:文件-Documents-DOC,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesDocumentsDOC ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesDocumentsPDF() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesDocumentsPDF ******************" + "\n", durationFile)
        val testTime = testFilesItems("Documents-PDF")

        // 获取上报时长
        val countTime = getCountTime("end|file_reader|pdf")
        writeStrToFile("场景:文件-Documents-PDF,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesDocumentsPDF ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesDocumentsTXT() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesDocumentsTXT ******************" + "\n", durationFile)
        val testTime = testFilesItems("Documents-TXT")

        // 获取上报时长
        val countTime = getCountTime("end|file_reader|txt")
        writeStrToFile("场景:文件-Documents-TXT,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesDocumentsTXT ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesDocumentsXLS() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesDocumentsXLS ******************" + "\n", durationFile)
        val testTime = testFilesItems("Documents-XLS")

        // 获取上报时长
        val countTime = getCountTime("end|file_reader|excle")
        writeStrToFile("场景:文件-Documents-XLS,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesDocumentsXLS ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesDocumentsPPT() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesDocumentsPPT ******************" + "\n", durationFile)
        val testTime = testFilesItems("Documents-PPT")

        // 获取上报时长
        val countTime = getCountTime("end|file_reader|ppt")
        writeStrToFile("场景:文件-Documents-PPT,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesDocumentsPPT ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesStorage() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesStorage ******************" + "\n", durationFile)
        val testTime = testFilesItems("Storage")

        // 获取上报时长
        val countTime = getCountTime("end|file|storage")
        writeStrToFile("场景:文件-Storage,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesStorage ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesArchives() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesArchives ******************" + "\n", durationFile)
        val testTime = testFilesItems("Archives")

        // 获取上报时长
        val countTime = getCountTime("end|file|archives")
        writeStrToFile("场景:文件-Archives,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesArchives ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesArchivesUnzip() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesArchivesUnzip ******************" + "\n", durationFile)
        val testTime = testFilesItems("Archives-Unzip")

        // 获取上报时长
        val countTime = getCountTime("end|file|unzip")
        writeStrToFile("场景:文件-Archives-Unzip,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesArchivesUnzip ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesInstagram() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesInstagram ******************" + "\n", durationFile)
        val testTime = testFilesItems("Instagram")

        // 获取上报时长
        val countTime = getCountTime("end|file|instagram")
        writeStrToFile("场景:文件-Instagram,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesInstagram ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesOfflinePage() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesOfflinePage ******************" + "\n", durationFile)
        val testTime = testFilesItems("Offline pages")

        // 获取上报时长
        val countTime = getCountTime("end|file|offline pages")
        writeStrToFile("场景:文件-Offline pages,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesOfflinePage ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesApps() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesApps ******************" + "\n", durationFile)
        val testTime = testFilesItems("Apps")

        // 获取上报时长
        val countTime = getCountTime("end|file|apps")
        writeStrToFile("场景:文件-Apps,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesApps ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesOthers() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesOthers ******************" + "\n", durationFile)
        val testTime = testFilesItems("Others")

        // 获取上报时长
        val countTime = getCountTime("end|file|others")
        writeStrToFile("场景:文件-Others,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesOthers ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesOthersReader() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesOthersReader ******************" + "\n", durationFile)
        val testTime = testFilesItems("Others-Reader")

        // 获取上报时长
        val countTime = getCountTime("end|file_reader|other")
        writeStrToFile("场景:文件-Others-Reader,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesOthersReader ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesJunkFiles() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesJunkFiles ******************" + "\n", durationFile)
        val testTime = testFilesItems("Junk files")

        // 获取上报时长
        val countTime = getCountTime("end|cleaner|basics")
        writeStrToFile("场景:文件-Junk files,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesJunkFiles ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesPhoneBoost() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesPhoneBoost ******************" + "\n", durationFile)
        val testTime = testFilesItems("Phone boost")

        // 获取上报时长
        val countTime = getCountTime("end|cleaner|phoneboost")
        writeStrToFile("场景:文件-Phone boost,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesPhoneBoost ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesCleanVideos() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesCleanVideos ******************" + "\n", durationFile)
        val testTime = testFilesItems("Clean Up Videos")

        // 获取上报时长
        val countTime = getCountTime("end|cleaner|video")
        writeStrToFile("场景:文件-Clean Up Videos,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesCleanVideos ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesCleanPhoenix() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesCleanPhoenix ******************" + "\n", durationFile)
        val testTime = testFilesItems("Clean Up Phoenix")

        // 获取上报时长
        val countTime = getCountTime("end|cleaner|browser")
        writeStrToFile("场景:文件-Clean Up Phoenix,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesCleanPhoenix ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesRecentDocuments() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesRecentDocuments ******************" + "\n", durationFile)
        val testTime = testFilesItems("Recent Documents")

        // 获取上报时长
        val countTime = getCountTime("end|file|recent documents")
        writeStrToFile("场景:文件-Recent Documents,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesRecentDocuments ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesWallpaper() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesWallpaper ******************" + "\n", durationFile)
        val testTime = testFilesItems("Wallpaper")

        // 获取上报时长
        val countTime = getCountTime("end|file|wallpaper")
        writeStrToFile("场景:文件-Wallpaper,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesWallpaper ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesRingtones() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesRingtones ******************" + "\n", durationFile)
        val testTime = testFilesItems("Ringtones")

        // 获取上报时长
        val countTime = getCountTime("end|file|Ringtones")
        writeStrToFile("场景:文件-Ringtones,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesRingtones ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesCompressFiles() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesCompressFiles ******************" + "\n", durationFile)
        val testTime = testFilesItems("Compress files")

        // 获取上报时长
        val countTime = getCountTime("end|file|compression")
        writeStrToFile("场景:文件-Compress files,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesCompressFiles ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesCompressFilesSelector() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesCompressFilesSelector ******************" + "\n", durationFile)
        val testTime = testFilesItems("CompressFiles-selector")

        // 获取上报时长
        val countTime = getCountTime("end|file|selector")
        writeStrToFile("场景:文件-Compress files-Selector,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesCompressFilesSelector ******************" + "\n", durationFile)
        backToHome()
    }

    @Test
    fun testFilesUnzipFiles() {
        // 先清理日志
        clearBufferCache(device, null)
        sleep(TIMEOUT_SHORT.toLong())

        // 执行场景
        writeStrToFile(getCurTimeForLog() + "  ****************** start testFilesUnzipFiles ******************" + "\n", durationFile)
        val testTime = testFilesItems("Unzip files")

        // 获取上报时长
        val countTime = getCountTime("end|file|unzip")
        writeStrToFile("场景:文件-Unzip files,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile)
        writeStrToFile(getCurTimeForLog() + "  ****************** end testFilesUnzipFiles ******************" + "\n", durationFile)
        backToHome()
    }

    /**
     * 计算测试时长
     */
    private fun getTestTime(startTime: Long, endTime: Long): Double {
        return keepDecimalPoint((endTime - startTime).toDouble() / 1000, 2)
    }

    /**
     * 根据场景获取上报的时长
     */
    private fun getCountTime(scenes: String): Double {
        var countTime = 0.0
        var result = ""
        val sdk = Build.VERSION.SDK_INT
        if (sdk <= 23) {
            result = execCmdByUiDevice(device, "logcat -d -v time -s UnitTime:D")
        } else {
            result = execCmdByUiDevice(device, "logcat -d -v time -v year -s UnitTime:D")
        }
        val resultLines = result.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (resultLine in resultLines) {
            if (resultLine != "") {
                if (!resultLine.contains("--------- beginning")) {
                    writeStrToFile(resultLine + "\n", durationFile)
                }

                if (resultLine.contains(scenes)) {
                    val resultLineParts = resultLine.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    countTime = keepDecimalPoint(resultLineParts[resultLineParts.size - 1].trim { it <= ' ' }.toLong().toDouble() / 1000, 2)
                    break
                }
            }
        }
        return countTime
    }

    private fun testFeedsTab(tabName: String): Double {
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            startTime = System.currentTimeMillis()
            switchFeedsTab(tabName)
            back()
            endTime = System.currentTimeMillis()
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }
        return getTestTime(startTime, endTime)
    }

    private fun testFeedsNews(tabName: String): Double {
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            switchFeedsTab(tabName)
            var news = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.1, 0.3, 0.0, 1.0, 0.02, 0.8)
            if (news == null || news.size == 0) {
                for (i in 0..2) {
                    swip(0.5, 0.7, 0.5, 0.3)
                    sleep(TIMEOUT_MEDIUM.toLong())
                    news = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.1, 0.3, 0.0, 1.0, 0.02, 0.8)
                    if (news != null) {
                        break
                    }
                }
            }
            startTime = System.currentTimeMillis()
            news!!.get(0)!!.click()
            sleep(TIMEOUT_MEDIUM.toLong())
            back()
            endTime = System.currentTimeMillis()
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }
        return getTestTime(startTime, endTime)
    }

    private fun testFeedsVideo(tabName: String): Double {
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            switchFeedsTab(tabName)
            val firstVideo = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.3, 0.6, 0.0, 1.0, 0.02, 0.9).get(0)
            val firstVideoBottom = getChildUiObject2(firstVideo, false, "android.widget.LinearLayout", 0.8, 1.0, 0.0, 0.1, 0.0, 1.0, 0.0, 1.0, false)
            startTime = System.currentTimeMillis()
            firstVideoBottom?.click()
            sleep(TIMEOUT_MEDIUM.toLong())
            back()
            endTime = System.currentTimeMillis()
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }
        return getTestTime(startTime, endTime)
    }

    private fun testFeedsMiniVideo(tabName: String): Double {
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            switchFeedsTab(tabName)
            startTime = System.currentTimeMillis()
            getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.8, 0.0, 1.0, 0.02, 0.9)?.get(0)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            val swipeToast = waitUiObject2ByText("Swipe up for more", TIMEOUT_SHORT)
            if (swipeToast != null) {
                swipeToast.click()
            }
            sleep(TIMEOUT_MEDIUM.toLong())
            back()
            endTime = System.currentTimeMillis()
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }
        return getTestTime(startTime, endTime)
    }

    private fun testFeedsImg(tabName: String): Double {
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            switchFeedsTab(tabName)
            startTime = System.currentTimeMillis()
            val linearLayouts = waitUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.2, 0.8, 0.0, 1.0, 0.02, 1.0, 3)
            linearLayouts.get(0)!!.click()
            sleep(TIMEOUT_MEDIUM.toLong())
            back()
            endTime = System.currentTimeMillis()
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }
        return getTestTime(startTime, endTime)
    }

    private fun testMeItems(item: String): Double {
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            if (item == "Me") {
                startTime = System.currentTimeMillis()
                waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_MEDIUM.toLong())
            } else {
                waitUiObject2ByText("Me", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_SHORT.toLong())

                // 判断是否需要登录
                if (item == "User" || item == "Msg") {
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
                }

                // 处理各场景
                startTime = System.currentTimeMillis()
                if (item == "User") {
                    waitUiObject2ByText("Signed in with Google", TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                } else if (item == "Msg") {
                    getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.01, 0.2, 0.8, 1.0, 0.02, 0.2)?.get(0)?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                } else {
                    waitUiObject2ByText(item, TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                }
            }

            back()
            endTime = System.currentTimeMillis()
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }
        return getTestTime(startTime, endTime)
    }

    private fun testFilesItems(item: String): Double {
        var startTime: Long = 0
        var endTime: Long = 0
        try {
            if (item == "Files") {
                startTime = System.currentTimeMillis()
                waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_MEDIUM.toLong())
            } else {
                waitUiObject2ByText("Files", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_SHORT.toLong())

                // 判断是否需要上滑
                if (item == "Clean Up Videos" || item == "Clean Up Phoenix" || item == "Recent Documents" || item == "Wallpaper"
                    || item == "Ringtones" || item == "Compress files" || item == "CompressFiles-selector" || item == "Unzip files"
                ) {
                    swip(0.5, 0.8, 0.5, 0.2)
                    sleep(TIMEOUT_SHORT.toLong())
                } else {
                    swip(0.5, 0.2, 0.5, 0.8)
                    sleep(TIMEOUT_SHORT.toLong())
                }

                // 处理各场景
                if (item == "VideoPlayer") {
                    waitUiObject2ByText("Videos", TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_SHORT.toLong())
                    startTime = System.currentTimeMillis()
                    getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.5, 0.0, 1.0, 0.1, 0.9)?.get(0)?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                } else if (item == "MusicPlayer") {
                    waitUiObject2ByText("Music", TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_SHORT.toLong())
                    startTime = System.currentTimeMillis()
                    getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.3, 0.0, 1.0, 0.1, 0.9)?.get(0)?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                    getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 0.3, 0.02, 0.3)?.get(1)?.click()
                    endTime = System.currentTimeMillis()
                    sleep(TIMEOUT_MEDIUM.toLong())
                } else if (item == "ImageReader") {
                    waitUiObject2ByText("Images", TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_SHORT.toLong())
                    startTime = System.currentTimeMillis()
                    getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0.1, 0.3, 0.0, 1.0, 0.1, 0.9)?.get(0)?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                } else if (item.contains("Documents-")) {
                    var slideNum = 0
                    val type = item.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    if (type == "DOC") {
                        slideNum = 1
                    } else if (type == "PDF") {
                        slideNum = 2
                    } else if (type == "TXT") {
                        slideNum = 3
                    } else if (type == "XLS") {
                        slideNum = 4
                    } else if (type == "PPT") {
                        slideNum = 5
                    } else if (type == "EPUB") {
                        slideNum = 6
                    }
                    waitUiObject2ByText("Documents", TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_SHORT.toLong())
                    for (i in 0..<slideNum) {
                        swip(0.7, 0.5, 0.3, 0.5)
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                    }
                    startTime = System.currentTimeMillis()
                    getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.3, 0.0, 1.0, 0.1, 0.9)?.get(0)?.click()
                    sleep(TIMEOUT_LONG.toLong())
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                } else if (item == "Archives-Unzip") {
                    var archives = waitUiObject2ByText("Archives", TIMEOUT_MEDIUM)
                    if (archives == null) {
                        waitUiObject2ByText("More", TIMEOUT_MEDIUM)?.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        archives = waitUiObject2ByText(item, TIMEOUT_MEDIUM)
                    }
                    archives?.click()
                    sleep(TIMEOUT_SHORT.toLong())
                    startTime = System.currentTimeMillis()
                    getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.3, 0.0, 1.0, 0.1, 0.9)?.get(0)?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                } else if (item == "Archives" || item == "Instagram" || item == "Offline pages" || item == "Apps" || item == "Others") {
                    var itemUiObject2 = waitUiObject2ByText(item, TIMEOUT_MEDIUM)
                    if (itemUiObject2 == null) {
                        waitUiObject2ByText("More", TIMEOUT_MEDIUM)?.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        itemUiObject2 = waitUiObject2ByText(item, TIMEOUT_MEDIUM)
                    }
                    startTime = System.currentTimeMillis()
                    itemUiObject2?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                } else if (item == "Others-Reader") {
                    var others = waitUiObject2ByText("Others", TIMEOUT_MEDIUM)
                    if (others == null) {
                        waitUiObject2ByText("More", TIMEOUT_MEDIUM)?.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        others = waitUiObject2ByText(item, TIMEOUT_MEDIUM)
                    }
                    others?.click()
                    sleep(TIMEOUT_SHORT.toLong())
                    var otherSvg = waitUiObject2ByText("other_svg_30kB.svg", TIMEOUT_MEDIUM)
                    if (otherSvg == null) {
                        otherSvg = getUiObject2s("android.widget.LinearLayout", true, 0.8, 1.0, 0.05, 0.3, 0.0, 1.0, 0.1, 0.9).get(0)
                    }
                    startTime = System.currentTimeMillis()
                    otherSvg.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                } else if (item == "Recent Documents") {
                    startTime = System.currentTimeMillis()
                    waitUiObject2ByText("More", TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                } else if (item == "CompressFiles-selector") {
                    waitUiObject2ByText("Compress files", TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_SHORT.toLong())
                    startTime = System.currentTimeMillis()
                    waitUiObject2ByText("Select files", TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                } else if (item == "Phone boost") {
                    startTime = System.currentTimeMillis()
                    waitUiObject2ByText(item, TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_LONG.toLong())
                    // 关闭广告
                    var closeBtn = waitUiObject2ByRes("close-button-container", TIMEOUT_MEDIUM.toLong())
                    if (closeBtn == null) {
                        closeBtn = waitUiObject2ByText("CLOSE", TIMEOUT_VERY_SHORT)
                    }
                    if (closeBtn != null) {
                        // 处理uiautomator有时点击时会报异常
                        try {
                            closeBtn.click()
                        } catch (e: Exception) {
                            pressHome(device, null)
                            sleep(3000)
                            amStartApp(device, activity, null)
                        }
                        sleep(3000)
                    } else {
                        var closeBtns = getUiObject2s("android.widget.Button", true, 0.05, 0.3, 0.05, 0.3, 0.0, 1.0, 0.0, 0.3)
                        if (closeBtns == null || closeBtns.size == 0) {
                            closeBtns = getUiObject2s("android.widget.ImageButton", true, 0.05, 0.3, 0.05, 0.3, 0.0, 1.0, 0.0, 0.3)
                        }
                        if (closeBtns != null && closeBtns.size > 0) {
                            // 处理uiautomator有时点击时会报异常
                            try {
                                closeBtns.get(0)!!.click()
                            } catch (e: Exception) {
                                pressHome(device, null)
                                sleep(3000)
                                amStartApp(device, activity, null)
                            }
                            sleep(3000)
                        }
                    }
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
                                boostBacks.get(0)!!.click()
                            } catch (e: Exception) {
                                back()
                            }
                        }
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        if (waitUiObject2ByTextContains("Me", TIMEOUT_VERY_SHORT) != null) {
                            break
                        }
                    }
                } else {
                    startTime = System.currentTimeMillis()
                    waitUiObject2ByText(item, TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_MEDIUM.toLong())
                }
            }

            back()
            if (item == "Junk files") {
                val exit = waitUiObject2ByText("Exit", TIMEOUT_MEDIUM)
                if (exit != null) {
                    exit.click()
                }
            }
            if (item != "MusicPlayer") {
                endTime = System.currentTimeMillis()
            }
            sleep(TIMEOUT_MEDIUM.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            backToApp()
            backToHome()
        }
        return getTestTime(startTime, endTime)
    }
}
