package com.bbtest.stable

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.uiautomator.UiObject2
import com.bbtest.common.MonkeyCommon
import com.bbtest.common.ShellCommon.amStartApp
import com.bbtest.common.ShellCommon.getActivities
import com.bbtest.common.ShellCommon.isAppBackstage
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.CommonUtil.getCurTimeForFile
import com.bbtest.utils.CommonUtil.getCurTimeForLog
import com.bbtest.utils.CommonUtil.getExceptionMsg
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.readFile
import com.bbtest.utils.FileUtil.writeStrToFile
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * @author onuszhao
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class UICrawlerTest : MonkeyCommon() {
    private val resultFolder = File(rootFolder, "crawler")
    private val crawlerFile = File(resultFolder, "crawler.txt")
    private val monkeyInfoFile = File(downloadsDir, "monkey.txt")
    private var pkgName = ""
    private var activity = ""

    @Before
    override fun beforeTest() {
        super.beforeTest()
        createFolder(resultFolder)
        createFile(crawlerFile)
        pkgName = readFile(monkeyInfoFile).trim().ifBlank { "com.transsion.phoenix" }
        val mainActivities = getActivities(device, pkgName, null)
        for (mainActivity in mainActivities) {
            amStartApp(device, mainActivity, crawlerFile)
            CommonUtil.sleep(5_000)
            if (!isAppBackstage(device, pkgName)) {
                activity = mainActivity
                break
            }
        }
        writeStrToFile("${getCurTimeForLog()}  $pkgName\n", crawlerFile)
        hasClickedElements = mutableListOf()
        secondHasClickedElements = mutableListOf()
    }

    @Test
    fun testCrawler() {
    }

    @Test
    fun testHomeTool() {
        loopTraverseUI("HomeTool")
    }

    @Test
    fun testFeeds() {
    }

    @Test
    fun testFeedsShortVideo() {
    }

    @Test
    fun testFeedsSmallVideo() {
    }

    @Test
    fun testFeedsImage() {
    }

    @Test
    fun testDownloads() {
    }

    @Test
    fun testMe() {
    }

    @Test
    fun testNovel() {
        loopTraverseUI("Novel")
    }

    @Test
    fun testFiles() {
        loopTraverseUI("Novel")
    }

    @Test
    fun testFilesDocuments() {
    }

    @Test
    fun testFilesImages() {
    }

    @Test
    fun testFilesVideos() {
    }

    @Test
    fun testFilesMusic() {
    }

    @Test
    fun testMultiWindow() {
    }

    @Test
    fun testWebView() {
    }

    private fun loopTraverseUI(scenes: String) {
        try {
            Log.i("onuszhao", "*********** start traverse! *************")
            traverseFirstPage(scenes)
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile("${getCurTimeForLog()}CrawlerTest:Exception\n", crawlerFile)
            writeStrToFile(getExceptionMsg(e), crawlerFile)
            screenshot("${resultFolder}/crawler_${getCurTimeForFile()}.jpg")
        }
    }

    /**
     * 只遍历一级菜单，然后再分场景，否则遍历层级太深，容易跳出场景
     */
    private fun traverseFirstPage(scenes: String) {
        val entryUiObject2 = when (scenes) {
            "HomeTool" -> gotoHomeTool(pkgName, activity, crawlerFile)
            "Novel" -> gotoNovel(pkgName, activity, crawlerFile)
            else -> null
        }

        sleep(TIMEOUT_MEDIUM.toLong())
        val clickableUiObject2s = getClickableUiObject2s(getRootObject())
        Log.i("onuszhao", "FirstPage --> totalSize:${clickableUiObject2s.size}")
        try {
            for (i in clickableUiObject2s.indices) {
                if (i == clickableUiObject2s.size - 1) {
                    swip(0.5, 0.7, 0.5, 0.3)
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }

                val clickableUiObject2 = clickableUiObject2s[i]
                val curElement = buildElementKey(clickableUiObject2)
                Log.i(
                    "onuszhao",
                    "FirstPage --> curElement:$curElement,hasClicked:${hasClickedElements.contains(curElement)},hasClickedSize:${hasClickedElements.size}",
                )
                if (!hasClickedElements.contains(curElement)) {
                    clickableUiObject2.click()
                    sleep(TIMEOUT_SHORT.toLong())

                    // 判断是否back回入口页面
                    if (isContainsUiobject2(entryUiObject2)) {
                        entryUiObject2?.click()
                        sleep(TIMEOUT_SHORT.toLong())
                        continue
                    }

                    traverseSecondPage(scenes, clickableUiObject2, false)

                    back()
                    hasClickedElements.add(curElement)
                }
            }
        } catch (e: Exception) {
            Log.i("onuszhao", "FirstPage --> traverse again")
            traverseFirstPage(scenes)
        }
    }

    /**
     * 只遍历一级菜单，然后再分场景，否则遍历层级太深，容易跳出场景
     */
    private fun traverseSecondPage(scenes: String, firstUiObject2: UiObject2?, isClickError: Boolean) {
        if (isClickError) {
            when (scenes) {
                "HomeTool" -> gotoHomeTool(pkgName, activity, crawlerFile)
                "Novel" -> gotoNovel(pkgName, activity, crawlerFile)
            }

            sleep(TIMEOUT_MEDIUM.toLong())
            val clickableUiObject2s = getClickableUiObject2s(getRootObject())
            try {
                for (clickableUiObject2 in clickableUiObject2s) {
                    if (clickableUiObject2 == firstUiObject2) {
                        clickableUiObject2.click()
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        sleep(TIMEOUT_MEDIUM.toLong())
        val clickableUiObject2s = getClickableUiObject2s(getRootObject())
        Log.i("onuszhao", "SecondPage --> totalSize:${clickableUiObject2s.size}")
        try {
            for (i in clickableUiObject2s.indices) {
                if (i == clickableUiObject2s.size - 1) {
                    swip(0.5, 0.7, 0.5, 0.3)
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }

                val clickableUiObject2 = clickableUiObject2s[i]
                val curElement = buildElementKey(clickableUiObject2)
                Log.i(
                    "onuszhao",
                    "SecondPage --> curElement:$curElement,hasClicked:${secondHasClickedElements.contains(curElement)},hasClickedSize:${secondHasClickedElements.size}",
                )
                if (!secondHasClickedElements.contains(curElement)) {
                    clickableUiObject2.click()
                    sleep(TIMEOUT_SHORT.toLong())

                    // 返回，并记录已点击
                    back()
                    secondHasClickedElements.add(curElement)
                }
            }
        } catch (e: Exception) {
            Log.i("onuszhao", "SecondPage --> traverse again")
            traverseSecondPage(scenes, firstUiObject2, true)
        }
    }

    private fun buildElementKey(uiObject2: UiObject2): String {
        val bounds = uiObject2.visibleBounds
        val width = bounds.right - bounds.left
        val height = bounds.bottom - bounds.top
        val text = getText(uiObject2, true)
        return "${uiObject2.className}-$width-$height-$text"
    }

    private fun isContainsUiobject2(uiObject2: UiObject2?): Boolean {
        sleep(TIMEOUT_SHORT.toLong())
        val clickableUiObject2s = getClickableUiObject2s(getRootObject())
        return try {
            clickableUiObject2s.any { clickableUiObject2 -> clickableUiObject2 == uiObject2 }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }

    companion object {
        // 全局记录已遍历对象，避免重复点击，提高效率
        private var hasClickedElements: MutableList<String> = mutableListOf()
        private var secondHasClickedElements: MutableList<String> = mutableListOf()
    }
}
