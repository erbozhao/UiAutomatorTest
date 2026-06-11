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
    var resultFolder: File = File(rootFolder, "crawler")
    var crawlerFile: File = File(resultFolder, "crawler.txt")
    private val monkeyInfoFile = File(downloadsDir, "monkey.txt")
    private var pkgName = ""
    private var activity = ""

    @Before
    public override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
        createFile(crawlerFile)
        // 获取需要跑的包相关信息
        pkgName = readFile(monkeyInfoFile).trim { it <= ' ' }
        if (pkgName == "") {
            pkgName = "com.transsion.phoenix"
        }
        // 获取activity(解决一个应用存在多个主activity)
        val mainActivities: List<String> = getActivities(device, pkgName, null)
        for (mainActivity in mainActivities) {
            amStartApp(device, mainActivity, crawlerFile)
            CommonUtil.sleep(5000)
            if (!isAppBackstage(device, pkgName)) {
                activity = mainActivity
                break
            }
        }
        writeStrToFile(getCurTimeForLog() + "  " + pkgName + "\n", crawlerFile)
        // 初始化一点击元素
        hasClickedElements = ArrayList<String?>()
        secondHasClickedElements = ArrayList<String?>()
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
            // 循环遍历页面
            Log.i("onuszhao", "*********** start traverse! *************")
            traverseFirstPage(scenes)
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile(getCurTimeForLog() + "CrawlerTest:Exception" + "\n", crawlerFile)
            writeStrToFile(getExceptionMsg(e), crawlerFile)
            screenshot(resultFolder.toString() + "/crawler_" + getCurTimeForFile() + ".jpg")
        }
    }

    /**
     * 只遍历一级菜单，然后再分场景，否则遍历层级太深，容易跳出场景
     */
    private fun traverseFirstPage(scenes: String) {
        var entryUiObject2: UiObject2? = null
        if (scenes == "HomeTool") {
            entryUiObject2 = gotoHomeTool(pkgName, activity, crawlerFile)
        } else if (scenes == "Novel") {
            entryUiObject2 = gotoNovel(pkgName, activity, crawlerFile)
        }

        //遍历所有控件
        sleep(TIMEOUT_MEDIUM.toLong())
        val clickableUiObject2s = getClickableUiObject2s(getRootObject())
        Log.i("onuszhao", "FirstPage --> totalSize:" + clickableUiObject2s.size)
        try {
            for (i in clickableUiObject2s.indices) {
                // 最后一个对象时，上滑页面
                if (i == clickableUiObject2s.size - 1) {
                    swip(0.5, 0.7, 0.5, 0.3)
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }

                // 通过类名+宽高+文案定位元素
                val clickableUiObject2 = clickableUiObject2s.get(i)
                val curObjectClass = clickableUiObject2.getClassName()
                val curUiObjectRect = clickableUiObject2.getVisibleBounds()
                val curObjectWidth = curUiObjectRect.right - curUiObjectRect.left
                val curObjectHeight = curUiObjectRect.bottom - curUiObjectRect.top
                val curObjectText = getText(clickableUiObject2, true)
                val curElement = curObjectClass + "-" + curObjectWidth + "-" + curObjectHeight + "-" + curObjectText
                Log.i(
                    "onuszhao",
                    "FirstPage --> curElement:" + curElement + ",hasClicked:" + hasClickedElements!!.contains(curElement) + ",hasClickedSize:" + hasClickedElements!!.size
                )
                // 点击未点击的对象
                if (!hasClickedElements!!.contains(curElement)) {
                    clickableUiObject2.click()
                    sleep(TIMEOUT_SHORT.toLong())

                    // 判断是否back回入口页面
                    if (isContainsUiobject2(entryUiObject2)) {
                        entryUiObject2!!.click()
                        sleep(TIMEOUT_SHORT.toLong())
                        continue
                    }

                    // 记录已遍历对象
                    traverseSecondPage(scenes, clickableUiObject2, false)

                    // 返回，并记录已点击
                    back()
                    hasClickedElements!!.add(curElement)
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
        // 二级页面遍历错误时，先回到一级界面
        if (isClickError) {
            if (scenes == "HomeTool") {
                gotoHomeTool(pkgName, activity, crawlerFile)
            } else if (scenes == "Novel") {
                gotoNovel(pkgName, activity, crawlerFile)
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

        //遍历所有控件
        sleep(TIMEOUT_MEDIUM.toLong())
        val clickableUiObject2s = getClickableUiObject2s(getRootObject())
        Log.i("onuszhao", "SecondPage --> totalSize:" + clickableUiObject2s.size)
        try {
            for (i in clickableUiObject2s.indices) {
                // 最后一个对象时，上滑页面
                if (i == clickableUiObject2s.size - 1) {
                    swip(0.5, 0.7, 0.5, 0.3)
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }

                // 通过类名+宽高+文案定位元素
                val clickableUiObject2 = clickableUiObject2s.get(i)
                val curObjectClass = clickableUiObject2.getClassName()
                val curUiObjectRect = clickableUiObject2.getVisibleBounds()
                val curObjectWidth = curUiObjectRect.right - curUiObjectRect.left
                val curObjectHeight = curUiObjectRect.bottom - curUiObjectRect.top
                val curObjectText = getText(clickableUiObject2, true)
                val curElement = curObjectClass + "-" + curObjectWidth + "-" + curObjectHeight + "-" + curObjectText
                Log.i(
                    "onuszhao",
                    "SecondPage --> curElement:" + curElement + ",hasClicked:" + secondHasClickedElements!!.contains(curElement) + ",hasClickedSize:" + secondHasClickedElements!!.size
                )
                // 点击未点击的对象
                if (!secondHasClickedElements!!.contains(curElement)) {
                    clickableUiObject2.click()
                    sleep(TIMEOUT_SHORT.toLong())

                    // 返回，并记录已点击
                    back()
                    secondHasClickedElements!!.add(curElement)
                }
            }
        } catch (e: Exception) {
            Log.i("onuszhao", "SecondPage --> traverse again")
            traverseSecondPage(scenes, firstUiObject2, true)
        }
    }

    private fun isContainsUiobject2(uiObject2: UiObject2?): Boolean {
        var isContainsUiobject2 = false
        sleep(TIMEOUT_SHORT.toLong())
        val clickableUiObject2s = getClickableUiObject2s(getRootObject())
        try {
            for (clickableUiObject2 in clickableUiObject2s) {
                if (clickableUiObject2 == uiObject2) {
                    isContainsUiobject2 = true
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isContainsUiobject2
    }

    @After
    public override fun afterTest() {
        super.afterTest()
    }

    companion object {
        // 全局记录已遍历对象，避免重复点击，提高效率
        private var hasClickedElements: MutableList<String?>? = null
        private var secondHasClickedElements: MutableList<String?>? = null
    }
}
