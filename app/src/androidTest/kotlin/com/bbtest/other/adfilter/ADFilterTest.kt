package com.bbtest.other.adfilter

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.uiautomator.UiObject2
import com.bbtest.common.PhxCommon
import com.bbtest.common.ShellCommon.amStartApp
import com.bbtest.common.ShellCommon.forceStopApp
import com.bbtest.common.ShellCommon.isAppBackstage
import com.bbtest.utils.CommonUtil.getCurTime
import com.bbtest.utils.ShellCommand.execCmdByUiDevice
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author onuszhao
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class ADFilterTest : PhxCommon() {
    private val imgFolder = "/sdcard/Download/PHXDownloads"

    @Before
    override fun beforeTest() {
        super.beforeTest()
    }

    @Test
    fun testLongScreenshot1() {
        // 先等待5s，并跳过闪屏
        sleep(TIMEOUT_MEDIUM.toLong())
        skipSplash()

        // 再等待5s，跳过应用弹窗
        sleep(TIMEOUT_MEDIUM.toLong())
        skipAppDialog()
        skipOtherDialog()

        // 最后等待10s，跳过页面内弹窗
        sleep(TIMEOUT_LONG.toLong())
        val clickableUiObjects = getClickableUiObject2s(getRootObject())
        for (i in clickableUiObjects.indices) {
            val clickableUiObject = clickableUiObjects.get(i)
            val clickableUiObjectClass = clickableUiObject.getClassName()
            var clickableUiObjectText = clickableUiObject.getText()
            if (clickableUiObjectText == null || clickableUiObjectText == "") {
                clickableUiObjectText = clickableUiObject.getContentDescription()
            }
            if (clickableUiObjectText != null && clickableUiObjectText == "Quit Tour") {
                clickableUiObject.click()
                sleep(TIMEOUT_SHORT.toLong())
                break
            } else if (clickableUiObjectText != null && clickableUiObjectText == "Not Now") {
                clickableUiObject.click()
                sleep(TIMEOUT_SHORT.toLong())
                swip(0.5, 0.2, 0.5, 0.8)
                sleep(TIMEOUT_SHORT.toLong())
                break
            } else if (clickableUiObjectText != null && clickableUiObjectText == "I'm Over 18") {
                clickableUiObject.click()
                sleep(TIMEOUT_SHORT.toLong())
                swip(0.5, 0.2, 0.5, 0.8)
                sleep(TIMEOUT_SHORT.toLong())
                break
            } else if (clickableUiObjectText != null && clickableUiObjectText == "YES") {
                clickableUiObject.click()
                sleep(TIMEOUT_SHORT.toLong())
                swip(0.5, 0.2, 0.5, 0.8)
                sleep(TIMEOUT_SHORT.toLong())
                break
            } else if ((clickableUiObjectClass != "android.webkit.WebView") && (clickableUiObjectClass != "android.widget.EditText") && clickableUiObjectText != null && (clickableUiObjectText == "استمرار" || clickableUiObjectText == "CONTINUER")) {
                clickableUiObject.click()
                sleep(TIMEOUT_SHORT.toLong())
                break
            }
        }

        //长截图
        var addressbar = waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT.toLong())
        if (addressbar == null) {
            for (i in 0..2) {
                if (i == 0) {
                    device.click((width * 0.9).toInt(), (height * 0.5).toInt()) // 点击靠右边边缘区域，取消遮罩弹窗等
                    sleep(TIMEOUT_SHORT.toLong())
                } else if (i == 1) {
                    swip(0.5, 0.3, 0.5, 0.6) // 下滑
                    sleep(TIMEOUT_SHORT.toLong())
                } else {
                    back() // 弹到其他应用，则back回到页面
                    sleep(TIMEOUT_SHORT.toLong())
                }

                addressbar = waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT.toLong())
                if (addressbar != null) {
                    break
                }
            }
        }
        if (addressbar != null) {
            addressbar.click()
            sleep(TIMEOUT_SHORT.toLong())
            var snapshot: UiObject2? = null
            for (i in 0..2) {
                swip(0.8, 0.7, 0.8, 0.3)
                sleep(TIMEOUT_SHORT.toLong())
                snapshot = waitUiObject2ByText("Snapshot whole page", TIMEOUT_SHORT)
                if (snapshot == null) {
                    back()
                    sleep(TIMEOUT_SHORT.toLong())
                    openSnapshot()
                } else {
                    break
                }
            }
            snapshot?.click()
            sleep(TIMEOUT_VERY_LONG.toLong())

            // 长截图失败时，截屏
            if (!this.isLongScreenSuccess) {
                screenshot("$imgFolder/${getCurTime()}.jpg")
            }
        } else {
            screenshot("$imgFolder/${getCurTime()}.jpg")
        }
    }

    @Test
    fun testLongScreenshot2() {
        // 上滑
        swip(0.5, 0.8, 0.5, 0.2)
        sleep(TIMEOUT_LONG.toLong())

        // 判断滑动成功没
        var isSlideSuccess = false
        if (waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT.toLong()) == null) {
            isSlideSuccess = true
        }

        // 点击跳转二级页面
        val preRootContent = getRootObject()
        val clickableUiObjects = getClickableUiObject2s(preRootContent)
        val allUiObjects: MutableList<UiObject2>?
        var preAllTexts: MutableList<String> = mutableListOf()
        if (!isSlideSuccess) {
            allUiObjects = getAllUiObject2s(preRootContent)
            preAllTexts = getTexts(allUiObjects)
        }

        var isClick = false
        // 先找较宽，且略高的点击
        for (i in clickableUiObjects.indices) {
            val uiObject = clickableUiObjects.get(i)
            val uiObjectClass = uiObject.getClassName()
            val uiObjectTop = uiObject.getVisibleBounds().top
            val uiObjectWidth = uiObject.getVisibleBounds().right - uiObject.getVisibleBounds().left
            val uiObjectHeight = uiObject.getVisibleBounds().bottom - uiObjectTop
            // 过滤通知栏以下
            if ((uiObjectClass != "android.webkit.WebView") && (uiObjectClass != "android.widget.EditText") && uiObjectTop >= (height * 0.2) && uiObjectWidth >= (width * 0.8) && uiObjectHeight >= (height * 0.3)) {
                uiObject.click()
                sleep(TIMEOUT_VERY_LONG.toLong())
                // 判断浏览器是否在前台
                if (isAppBackstage(device, pkgName)) {
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    amStartApp(device, activity, null)
                    sleep(TIMEOUT_SHORT.toLong())
                }
                if (!isSlideSuccess) {
                    val curAllUiObjects = getAllUiObject2s(getRootObject())
                    val curAllTexts = getTexts(curAllUiObjects)
                    if (isSamePage(preAllTexts, curAllTexts ?: mutableListOf())) {
                        continue
                    } else {
                        isClick = true
                        break
                    }
                } else {
                    if (waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT.toLong()) == null) {
                        continue
                    } else {
                        break
                    }
                }
            }
        }

        // 再找略宽，且略高的点击
        if (!isClick) {
            for (i in clickableUiObjects.indices) {
                val uiObject = clickableUiObjects.get(i)
                val uiObjectClass = uiObject.getClassName()
                val uiObjectTop = uiObject.getVisibleBounds().top
                val uiObjectWidth = uiObject.getVisibleBounds().right - uiObject.getVisibleBounds().left
                val uiObjectHeight = uiObject.getVisibleBounds().bottom - uiObjectTop
                // 过滤通知栏以下
                if ((uiObjectClass != "android.webkit.WebView") && (uiObjectClass != "android.widget.EditText") && uiObjectTop >= (height * 0.2) && uiObjectWidth >= (width * 0.4) && uiObjectHeight >= (height * 0.15)) {
                    uiObject.click()
                    sleep(TIMEOUT_VERY_LONG.toLong())
                    // 判断浏览器是否在前台
                    if (isAppBackstage(device, pkgName)) {
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        amStartApp(device, activity, null)
                        sleep(TIMEOUT_SHORT.toLong())
                    }
                    if (!isSlideSuccess) {
                        val curAllUiObjects = getAllUiObject2s(getRootObject())
                        val curAllTexts = getTexts(curAllUiObjects)
                        if (isSamePage(preAllTexts, curAllTexts ?: mutableListOf())) {
                            continue
                        } else {
                            isClick = true
                            break
                        }
                    } else {
                        if (waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT.toLong()) == null) {
                            continue
                        } else {
                            break
                        }
                    }
                }
            }
        }

        // 找不到则点击坐标
        if (!isClick) {
            for (i in 1..3) {
                device.click((width * 0.6).toInt(), (height * (0.4 + 0.1 * i)).toInt())
                sleep(TIMEOUT_VERY_LONG.toLong())
                // 判断浏览器是否在前台
                if (isAppBackstage(device, pkgName)) {
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    amStartApp(device, activity, null)
                    sleep(TIMEOUT_SHORT.toLong())
                }
                if (!isSlideSuccess) {
                    val curAllUiObjects = getAllUiObject2s(getRootObject())
                    val curAllTexts = getTexts(curAllUiObjects)
                    if (isSamePage(preAllTexts, curAllTexts ?: mutableListOf())) {
                        continue
                    } else {
                        break
                    }
                } else {
                    if (waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT.toLong()) == null) {
                        continue
                    } else {
                        break
                    }
                }
            }
        }

        //判断弹窗
        skipAppDialog()
        skipOtherDialog()
        for (i in 0..9) {
            val ok = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT)
            if (ok != null) {
                ok.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                break
            }
        }

        // 长截图
        var addressbar = waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT.toLong())
        if (addressbar == null) {
            for (i in 0..2) {
                if (i == 0) {
                    device.click((width * 0.9).toInt(), (height * 0.5).toInt()) // 点击靠右边边缘区域，取消遮罩弹窗等
                    sleep(TIMEOUT_SHORT.toLong())
                } else if (i == 1) {
                    swip(0.5, 0.3, 0.5, 0.6) // 下滑
                    sleep(TIMEOUT_SHORT.toLong())
                } else {
                    back() // 弹到其他应用，则back回到页面
                    sleep(TIMEOUT_SHORT.toLong())
                }

                addressbar = waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT.toLong())
                if (addressbar != null) {
                    break
                }
            }
        }
        if (addressbar != null) {
            addressbar.click()
            sleep(TIMEOUT_SHORT.toLong())
            var snapshot: UiObject2? = null
            for (i in 0..2) {
                swip(0.8, 0.7, 0.8, 0.3)
                sleep(TIMEOUT_SHORT.toLong())
                snapshot = waitUiObject2ByText("Snapshot whole page", TIMEOUT_SHORT)
                if (snapshot == null) {
                    back()
                    sleep(TIMEOUT_SHORT.toLong())
                    openSnapshot()
                } else {
                    break
                }
            }
            snapshot?.click()
            sleep(TIMEOUT_VERY_LONG.toLong())

            // 长截图失败时，截屏
            if (!this.isLongScreenSuccess) {
                screenshot("$imgFolder/${getCurTime()}.jpg")
            }
        } else {
            screenshot("$imgFolder/${getCurTime()}.jpg")
        }
    }

    @Test
    fun testOpenSnapshot() {
        execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com")
        sleep(TIMEOUT_MEDIUM.toLong())
        waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM.toLong())?.click()
        sleep(TIMEOUT_SHORT.toLong())
        var snapshot: UiObject2? = null
        for (i in 0..2) {
            swip(0.8, 0.7, 0.8, 0.3)
            sleep(TIMEOUT_SHORT.toLong())
            snapshot = waitUiObject2ByText("Snapshot whole page", TIMEOUT_SHORT)
            if (snapshot == null) {
                back()
                sleep(TIMEOUT_SHORT.toLong())
                openSnapshot()
            } else {
                back()
                sleep(TIMEOUT_SHORT.toLong())
                break
            }
        }
        // 退出浏览器
        waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
        sleep(TIMEOUT_SHORT.toLong())
        waitUiObject2ByText("Exit", TIMEOUT_MEDIUM)?.click()
        sleep(TIMEOUT_SHORT.toLong())
    }

    @Test
    fun testCloseAllTabs() {
        var toolbarMultiWindow = waitUiObject2ByDesc("toolbar multiWindow", TIMEOUT_SHORT.toLong())
        if (toolbarMultiWindow == null) {
            toolbarMultiWindow = waitUiObject2ByText("Tabs", TIMEOUT_SHORT)
        }
        if (toolbarMultiWindow != null) {
            toolbarMultiWindow.click()
            sleep(TIMEOUT_SHORT.toLong())
            skipAppDialog()
            skipOtherDialog()
            val moreIcon = getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1.0, 0.03, 0.15)?.firstOrNull()
            if (moreIcon != null) {
                moreIcon.click()
                sleep(TIMEOUT_SHORT.toLong())
                waitUiObject2ByText("Close all tabs", TIMEOUT_SHORT)?.click()
                sleep(TIMEOUT_SHORT.toLong())
            }
        }
    }

    @Test
    fun testExitBrowser() {
        backExitBrowser()
        forceStopApp(device, pkgName, null)
        sleep(3000)
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }

    private fun openSnapshot() {
        val toolbarMenu = waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())
        if (toolbarMenu != null) {
            toolbarMenu.click()
            sleep(TIMEOUT_SHORT.toLong())
            // 由于页面中有Settings，直接通过文本找，会存在问题
//            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)?.click();
            getUiObject2ByChildText("android.widget.LinearLayout", "Settings")?.click()
            sleep(TIMEOUT_SHORT.toLong())
            waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM)
            sleep(TIMEOUT_SHORT.toLong())
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_SHORT.toLong())
            waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_SHORT.toLong())
            waitUiObject2ByText("Product features", TIMEOUT_MEDIUM)
            sleep(TIMEOUT_SHORT.toLong())
            val phxIcon = getUiObject2s("android.widget.ImageView", true, 0.2, 0.5, 0.1, 0.5, 0.2, 0.8, 0.02, 0.5)?.firstOrNull()
            for (i in 0..4) {
                phxIcon?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            sleep(TIMEOUT_SHORT.toLong())
            val developerOptions = waitUiObject2ByText("Developer Options", TIMEOUT_MEDIUM)
            if (developerOptions != null) {
                developerOptions.click()
                sleep(TIMEOUT_SHORT.toLong())
            }
            val snapshotSwitch = getUiObject2ByChildText("android.widget.LinearLayout", true, "Snapshot Whole Page", "android.widget.Switch")
            if (snapshotSwitch?.isChecked == false) {
                snapshotSwitch.click()
                sleep(TIMEOUT_SHORT.toLong())
            }
            var addressbar: UiObject2? = null
            for (i in 0..9) {
                addressbar = waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT.toLong())
                if (addressbar == null) {
                    back()
                    sleep(TIMEOUT_SHORT.toLong())
                } else {
                    break
                }
            }
            addressbar?.click()
            sleep(TIMEOUT_SHORT.toLong())
        }
    }

    private val isLongScreenSuccess: Boolean
        get() {
            var isLongScreenSuccess = false
            val result = execCmdByUiDevice(device, "ls " + imgFolder)
            val resultLines = result.split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (resultLine in resultLines) {
                if (resultLine != "" && resultLine.endsWith(".png")) {
                    isLongScreenSuccess = true
                    break
                }
            }
            return isLongScreenSuccess
        }
}
