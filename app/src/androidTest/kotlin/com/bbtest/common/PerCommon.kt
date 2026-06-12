package com.bbtest.common

import androidx.test.uiautomator.UiObject2
import com.bbtest.common.ShellCommon.amStartApp
import com.bbtest.common.ShellCommon.forceStopApp
import com.bbtest.common.ShellCommon.getActivity
import com.bbtest.common.ShellCommon.getTopActivity
import com.bbtest.common.ShellCommon.pressEnter
import com.bbtest.common.ShellCommon.pressHome
import com.bbtest.perform.base.CpuThread
import com.bbtest.perform.base.Flowinfo
import com.bbtest.perform.base.FpsThread
import com.bbtest.stable.threads.MemoryThread
import com.bbtest.tools.ProcessInfo
import com.bbtest.utils.CommonUtil.keepDecimalPoint
import com.bbtest.utils.FileUtil
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.deleteFile
import org.junit.After
import org.junit.Before
import java.io.File

/**
 * @author onuszhao
 */
open class PerCommon : BaseCommon() {
    protected val perFolder = File(rootFolder, "perform")

    private fun waitAnyText(vararg texts: String, timeout: Int = TIMEOUT_VERY_SHORT): UiObject2? {
        for (text in texts) {
            val target = waitUiObject2ByText(text, timeout)
            if (target != null) {
                return target
            }
        }
        return null
    }

    private fun waitAnyDesc(timeoutMillis: Long = TIMEOUT_SHORT.toLong(), vararg descriptions: String): UiObject2? {
        for (description in descriptions) {
            val target = waitUiObject2ByDesc(description, timeoutMillis)
            if (target != null) {
                return target
            }
        }
        return null
    }

    private fun firstUiObject2(
        clazz: String,
        isClickable: Boolean,
        minWidth: Double,
        maxWidth: Double,
        minHeight: Double,
        maxHeight: Double,
        minX: Double,
        maxX: Double,
        minY: Double,
        maxY: Double,
    ): UiObject2? = getUiObject2s(clazz, isClickable, minWidth, maxWidth, minHeight, maxHeight, minX, maxX, minY, maxY).firstOrNull()

    private fun phoenixTabsButton(): UiObject2? =
        waitAnyText("Tabs", "التبويب", timeout = TIMEOUT_MEDIUM)
            ?: waitAnyDesc(TIMEOUT_SHORT.toLong(), "toolbar multiWindow")

    @Before
    override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(perFolder)
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }

    /**
     * 搜索或访问网址
     */
    fun searchOrUrl(pkgName: String, textOrUrl: String?, isOpenedPage: Boolean) {
        if (pkgName == BROWSER_CHROME) {
            val searchBox = if (isOpenedPage) {
                waitUiObject2ByRes("com.android.chrome:id/url_bar", TIMEOUT_MEDIUM.toLong())
            } else {
                waitUiObject2ByRes("com.android.chrome:id/search_box_text", TIMEOUT_MEDIUM.toLong())
            }
            searchBox?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("com.android.chrome:id/url_bar", TIMEOUT_MEDIUM.toLong())?.setText(textOrUrl)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            pressEnter(device, null)
        } else if (pkgName == BROWSER_OPERA) {
            waitUiObject2ByRes("com.opera.browser:id/url_field", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("com.opera.browser:id/url_field", TIMEOUT_MEDIUM.toLong())?.setText(textOrUrl)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("com.opera.browser:id/right_state_button", TIMEOUT_MEDIUM.toLong())?.click()
        } else if (pkgName == BROWSER_PHX) {
            val searchBoxes = if (isOpenedPage) {
                getUiObject2s("android.widget.LinearLayout", true, 0.5, 1.0, 0.04, 0.5, 0.0, 1.0, 0.02, 0.4)
            } else {
                getUiObject2s("android.widget.TextSwitcher", false, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.02, 0.4)
                    .ifEmpty {
                        getUiObject2s("android.widget.TextView", false, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.02, 0.4)
                    }
            }
            searchBoxes.firstOrNull()?.click()
            sleep(TIMEOUT_SHORT.toLong())
            val textBoxs = getUiObject2s("android.widget.EditText", true, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.02, 0.4)
            textBoxs.firstOrNull()?.setText(textOrUrl)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val go = waitAnyDesc(TIMEOUT_MEDIUM.toLong(), "addressbar go", "addressbar search")
            go?.click()
        } else if (pkgName == BROWSER_UC) {
            val searchBoxs = getUiObject2s("android.widget.TextView", true, 0.5, 1.0, 0.04, 0.5, 0.0, 1.0, 0.02, 0.4)
            searchBoxs.firstOrNull()?.click()
            sleep(TIMEOUT_SHORT.toLong())
            val textBoxs = getUiObject2s("android.widget.EditText", true, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.02, 0.4)
            textBoxs.firstOrNull()?.setText(textOrUrl)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val go = waitUiObject2ByRes("com.UCMobile.intl:id/address_bar_go_search", TIMEOUT_MEDIUM.toLong())
                ?: waitUiObject2ByDesc("Go button", TIMEOUT_SHORT.toLong())
            go?.click()
        } else if (pkgName == BROWSER_FIREFOX) {
            if (isOpenedPage) {
                waitUiObject2ByRes("org.mozilla.firefox:id/mozac_browser_toolbar_url_view", TIMEOUT_MEDIUM.toLong())?.click()
            } else {
                waitUiObject2ByRes("org.mozilla.firefox:id/toolbar", TIMEOUT_MEDIUM.toLong())?.click()
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("org.mozilla.firefox:id/mozac_browser_toolbar_edit_url_view", TIMEOUT_MEDIUM.toLong())?.setText(textOrUrl)
            sleep(TIMEOUT_SHORT.toLong())
            enter()
        }
    }

    /**
     * 进入多窗口
     */
    fun goinTab(pkgName: String) {
        if (pkgName == BROWSER_CHROME) {
            waitUiObject2ByRes("com.android.chrome:id/tab_switcher_button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_SHORT.toLong())
        } else if (pkgName == BROWSER_OPERA) {
            waitUiObject2ByRes("com.opera.browser:id/bottom_navigation_bar_tab_count_button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_SHORT.toLong())
        } else if (pkgName == BROWSER_PHX) {
            phoenixTabsButton()?.click()
            sleep(TIMEOUT_SHORT.toLong())
        } else if (pkgName == BROWSER_UC) {
            waitUiObject2ByDescContains("tap to switch button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_SHORT.toLong())
        } else if (pkgName == BROWSER_FIREFOX) {
            waitUiObject2ByRes("org.mozilla.firefox:id/counter_root", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_SHORT.toLong())
        }
    }

    /**
     * 新建一个窗口
     */
    fun newTab(pkgName: String) {
        if (pkgName == BROWSER_CHROME) {
            waitUiObject2ByRes("com.android.chrome:id/tab_switcher_button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("com.android.chrome:id/new_tab_view", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        } else if (pkgName == BROWSER_OPERA) {
            waitUiObject2ByRes("com.opera.browser:id/bottom_navigation_bar_tab_count_button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("com.opera.browser:id/tab_menu_add_tab", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        } else if (pkgName == BROWSER_PHX) {
            phoenixTabsButton()?.click()
            sleep(TIMEOUT_SHORT.toLong())
            firstUiObject2("android.widget.ImageView", true, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.7, 1.0)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        } else if (pkgName == BROWSER_UC) {
            waitUiObject2ByDescContains("tap to switch button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_SHORT.toLong())
            val addImgs = getUiObject2s("android.widget.ImageView", true, 0.0, 0.3, 0.0, 0.3, 0.0, 1.0, 0.8, 1.0)
            addImgs?.getOrNull(1)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        } else if (pkgName == BROWSER_FIREFOX) {
            waitUiObject2ByRes("org.mozilla.firefox:id/counter_root", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("org.mozilla.firefox:id/new_tab_button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("org.mozilla.firefox:id/toolbar", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("org.mozilla.firefox:id/mozac_browser_toolbar_edit_url_view", TIMEOUT_MEDIUM.toLong())?.setText("about:blank")
            sleep(TIMEOUT_SHORT.toLong())
            enter()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
    }

    /**
     * 关闭所有窗口
     */
    fun closeAllTabs(pkgName: String) {
        if (pkgName == BROWSER_CHROME) {
            waitUiObject2ByRes("com.android.chrome:id/tab_switcher_button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("com.android.chrome:id/menu_button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Close all tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("com.android.chrome:id/new_tab_view", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        } else if (pkgName == BROWSER_OPERA) {
            waitUiObject2ByRes("com.opera.browser:id/bottom_navigation_bar_tab_count_button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("com.opera.browser:id/tab_menu_menu_button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Close all tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("CLOSE", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        } else if (pkgName == BROWSER_PHX) {
            phoenixTabsButton()?.click()
            sleep(TIMEOUT_SHORT.toLong())
            firstUiObject2("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1.0, 0.03, 0.15)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Close all tabs", TIMEOUT_SHORT)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        } else if (pkgName == BROWSER_UC) {
            waitUiObject2ByDescContains("tap to switch button", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_SHORT.toLong())
            val moreIcons = getUiObject2s("android.widget.FrameLayout", true, 0.0, 0.3, 0.0, 0.3, 0.8, 1.0, 0.02, 0.3)
                if (moreIcons.isEmpty()) {
                    firstUiObject2("android.widget.ImageView", true, 0.0, 0.3, 0.0, 0.3, 0.8, 1.0, 0.02, 0.3)?.click()
                } else {
                    moreIcons[0].click()
                }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByText("Close All Tabs", TIMEOUT_MEDIUM)?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        } else if (pkgName == BROWSER_FIREFOX) {
            waitUiObject2ByRes("org.mozilla.firefox:id/counter_root", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("org.mozilla.firefox:id/tab_tray_overflow", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val closeAllTabs = waitUiObject2ByText("Close all tabs", TIMEOUT_MEDIUM)
            if (closeAllTabs != null) {
                closeAllTabs.click()
            } else {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                back()
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
    }

    /**
     * 退出浏览器
     */
    fun exitBrowser(pkgName: String) {
        val topActivity = getTopActivity(device, null)
        for (i in 0..9) {
            back()
            if (pkgName == BROWSER_PHX) {
                val exit = waitUiObject2ByText("Exit", TIMEOUT_VERY_SHORT)
                if (exit != null) {
                    exit.click()
                } else {
                    back()
                }
            } else if (pkgName == BROWSER_UC) {
                val doNotAskAgain = waitUiObject2ByText("Do not ask again", TIMEOUT_VERY_SHORT)
                if (doNotAskAgain != null) {
                    doNotAskAgain.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    waitUiObject2ByText("Exit", TIMEOUT_VERY_SHORT)?.click()
                } else {
                    back()
                }
            }
            sleep(TIMEOUT_SHORT.toLong())
            val curActivity = getTopActivity(device, null)
            if (curActivity != topActivity) {
                break
            }
        }
    }

    /**
     * 向上滑动
     */
    fun swipUp(num: Int, millis: Long) {
        for (j in 0..<num) {
            swip(0.5, 0.7, 0.5, 0.3)
            sleep(millis)
        }
    }

    /**
     * 向下滑动
     */
    fun swipDown(num: Int, millis: Long) {
        for (j in 0..<num) {
            swip(0.5, 0.3, 0.5, 0.7)
            sleep(millis)
        }
    }

    /**
     * 向左滑动
     */
    fun swipLeft(num: Int, millis: Long) {
        for (j in 0..<num) {
            swip(0.7, 0.5, 0.3, 0.5)
            sleep(millis)
        }
    }

    /**
     * 向右滑动
     */
    fun swipRight(num: Int, millis: Long) {
        for (j in 0..<num) {
            swip(0.3, 0.5, 0.7, 0.5)
            sleep(millis)
        }
    }

    /**
     * 保证返回到主页
     */
    fun backToHome() {
        for (i in 0..29) {
            // 先判断，若先返回容易出现弹窗界面去判断
            if (waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_SHORT.toLong()) != null) {
                // 返回判断是否有退出弹窗
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                var sureExit0 = waitUiObject2ByText("Sure to exit now?", TIMEOUT_VERY_SHORT)
                if (sureExit0 == null) {
                    sureExit0 = waitUiObject2ByText("تأكيد الخروج الآن ؟", TIMEOUT_VERY_SHORT)
                }
                if (sureExit0 != null) {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    break
                }
                var sureExit1 = waitUiObject2ByText("Do you have any difficulties While using Phoenix Browser？", TIMEOUT_VERY_SHORT)
                if (sureExit1 == null) {
                    sureExit1 = waitUiObject2ByText("هل واجهتط صعوبات أثناء رحلتك مع فينيكس", TIMEOUT_VERY_SHORT)
                }
                if (sureExit1 != null) {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    break
                }
                val clearExit0 = waitUiObject2ByText("Clear history on exit?", TIMEOUT_VERY_SHORT)
                if (clearExit0 != null) {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    break
                }
                val clearExit1 = waitUiObject2ByText("Clear History When Exit ?", TIMEOUT_VERY_SHORT)
                if (clearExit1 != null) {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    break
                }
            } else {
                if (i > 8) {
                    var dialog = waitUiObject2ByText("OK", TIMEOUT_SHORT)
                    if (dialog == null) {
                        dialog = waitUiObject2ByText("موافق", TIMEOUT_VERY_SHORT)
                    }
                    if (dialog != null) {
                        dialog.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var yesDialog = waitUiObject2ByText("Yes", TIMEOUT_VERY_SHORT)
                    if (yesDialog == null) {
                        yesDialog = waitUiObject2ByText("نعم", TIMEOUT_VERY_SHORT)
                    }
                    if (yesDialog != null) {
                        yesDialog.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var continueDialog = waitUiObject2ByText("Continue", TIMEOUT_VERY_SHORT)
                    if (continueDialog == null) {
                        continueDialog = waitUiObject2ByText("متابعة", TIMEOUT_VERY_SHORT)
                    }
                    if (continueDialog != null) {
                        continueDialog.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var allowDialog = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT)
                    if (allowDialog == null) {
                        allowDialog = waitUiObject2ByText("السماح", TIMEOUT_VERY_SHORT)
                    }
                    if (allowDialog != null) {
                        allowDialog.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var customDialog = waitUiObject2ByText("You can customize your news feeds in just two steps", TIMEOUT_SHORT)
                    if (customDialog == null) {
                        customDialog = waitUiObject2ByText("يمكنك تخصيص موجز الأخبار الخاص بك فى خطوتين", TIMEOUT_VERY_SHORT)
                    }
                    if (customDialog != null) {
                        getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 1.0, 0.1, 0.5)?.firstOrNull()?.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/closeButton", TIMEOUT_VERY_SHORT.toLong())
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/close", TIMEOUT_VERY_SHORT.toLong())
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/trybuttom", TIMEOUT_VERY_SHORT.toLong())
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByText("Try", TIMEOUT_VERY_SHORT)
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByText("جَرّبه الآن", TIMEOUT_VERY_SHORT)
                    }
                    if (skipButton != null) {
                        skipButton.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                } else {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
        }
    }

    /**
     * 跳过应用弹窗
     */
    fun skipAppDialog() {
        val defaultBrowserDialog = waitUiObject2ByText("Continue", TIMEOUT_VERY_SHORT)
        if (defaultBrowserDialog != null) {
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val rateUs = waitUiObject2ByText("Rate us 5 stars", TIMEOUT_VERY_SHORT)
        if (rateUs != null) {
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val offline = waitUiObject2ByText("The network is not connected, check the offline content right now.", TIMEOUT_VERY_SHORT)
        if (offline != null) {
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
    }

    /**
     * 跳过其他弹窗
     */
    fun skipOtherDialog() {
        val agree = waitUiObject2ByText("AGREE", TIMEOUT_VERY_SHORT)
        if (agree != null) {
            agree.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val accept = waitUiObject2ByText("Accept", TIMEOUT_VERY_SHORT)
        if (accept != null) {
            accept.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val allow = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT)
        if (allow != null) {
            allow.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val ok = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT)
        if (ok != null) {
            ok.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val later = waitUiObject2ByText("Later", TIMEOUT_VERY_SHORT)
        if (later != null) {
            later.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
    }

    val scrollableClazz: String?
        get() {
            val preferredScrollableClazzes = listOf(
                "androidx.recyclerview.widget.RecyclerView",
                "android.support.v7.widget.RecyclerView",
                "android.webkit.WebView",
                "android.widget.ListView",
                "android.view.ViewGroup",
            )
            preferredScrollableClazzes.firstOrNull { getScrollableUiObject2(it) != null }?.let { return it }
            return getAllUiObject2s(getRootObject()).firstOrNull { it.isScrollable }?.className
        }

    /**
     * 点击搜索框
     */
    fun clickSearchBox(pkgName: String, isOpenedPage: Boolean) {
        if (pkgName == BROWSER_CHROME) {
            val searchBox = if (isOpenedPage) {
                waitUiObject2ByRes("com.android.chrome:id/url_bar", TIMEOUT_MEDIUM.toLong())
            } else {
                waitUiObject2ByRes("com.android.chrome:id/search_box_text", TIMEOUT_MEDIUM.toLong())
            }
            searchBox?.click()
        } else if (pkgName == BROWSER_OPERA) {
            waitUiObject2ByRes("com.opera.browser:id/url_field", TIMEOUT_MEDIUM.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        } else if (pkgName == BROWSER_PHX) {
            val searchBoxes = if (isOpenedPage) {
                getUiObject2s("android.widget.LinearLayout", true, 0.5, 1.0, 0.04, 0.5, 0.0, 1.0, 0.02, 0.4)
            } else {
                getUiObject2s("android.widget.TextSwitcher", false, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.02, 0.4)
                    .ifEmpty {
                        getUiObject2s("android.widget.TextView", false, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.02, 0.4)
                    }
            }
            searchBoxes.firstOrNull()?.click()
        } else if (pkgName == BROWSER_UC) {
            val searchBoxs = getUiObject2s("android.widget.TextView", true, 0.5, 1.0, 0.04, 0.5, 0.0, 1.0, 0.02, 0.4)
            searchBoxs.firstOrNull()?.click()
        } else if (pkgName == BROWSER_FIREFOX) {
            if (isOpenedPage) {
                waitUiObject2ByRes("org.mozilla.firefox:id/mozac_browser_toolbar_url_view", TIMEOUT_MEDIUM.toLong())?.click()
            } else {
                waitUiObject2ByRes("org.mozilla.firefox:id/toolbar", TIMEOUT_MEDIUM.toLong())?.click()
            }
        }
    }

    /**
     * 搜索框输入文字
     */
    fun setTextAndGo(pkgName: String, textOrUrl: String?) {
        if (pkgName == BROWSER_CHROME) {
            waitUiObject2ByRes("com.android.chrome:id/url_bar", TIMEOUT_MEDIUM.toLong())?.setText(textOrUrl)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            pressEnter(device, null)
        } else if (pkgName == BROWSER_OPERA) {
            waitUiObject2ByRes("com.opera.browser:id/url_field", TIMEOUT_MEDIUM.toLong())?.setText(textOrUrl)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            waitUiObject2ByRes("com.opera.browser:id/right_state_button", TIMEOUT_MEDIUM.toLong())?.click()
        } else if (pkgName == BROWSER_PHX) {
            val textBoxs = getUiObject2s("android.widget.EditText", true, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.02, 0.4)
            textBoxs.firstOrNull()?.setText(textOrUrl)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val go = waitAnyDesc(TIMEOUT_MEDIUM.toLong(), "addressbar go", "addressbar search")
            go?.click()
        } else if (pkgName == BROWSER_UC) {
            val textBoxs = getUiObject2s("android.widget.EditText", true, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.02, 0.4)
            textBoxs.firstOrNull()?.setText(textOrUrl)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val go = waitUiObject2ByRes("com.UCMobile.intl:id/address_bar_go_search", TIMEOUT_MEDIUM.toLong())
                ?: waitUiObject2ByDesc("Go button", TIMEOUT_SHORT.toLong())
            go?.click()
        } else if (pkgName == BROWSER_FIREFOX) {
            waitUiObject2ByRes("org.mozilla.firefox:id/mozac_browser_toolbar_edit_url_view", TIMEOUT_MEDIUM.toLong())?.setText(textOrUrl)
            sleep(TIMEOUT_SHORT.toLong())
            enter()
        }
    }

    /**
     * 切换指定Feeds Tab
     */
    fun switchFeedsTab(tabName: String?) {
        getUiObject2s("android.widget.FrameLayout", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.1, 0.6)?.firstOrNull()?.click()
        sleep(TIMEOUT_SHORT.toLong())
        getUiObject2ByChildText("android.widget.FrameLayout", true, tabName, "android.widget.TextView")?.click()
        val done = waitUiObject2ByText("Done", TIMEOUT_SHORT)
        if (done != null) {
            done.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2ByChildText("android.widget.FrameLayout", true, tabName, "android.widget.TextView")?.click()
        }
        sleep(TIMEOUT_MEDIUM.toLong())
    }

    fun clickFeedsNews() {
        var news = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.1, 0.3, 0.0, 1.0, 0.02, 0.8)
        if (news.isEmpty()) {
            for (i in 0..2) {
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_MEDIUM.toLong())
                news = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.1, 0.3, 0.0, 1.0, 0.02, 0.8)
                if (news.isNotEmpty()) {
                    break
                }
            }
        }
        news.firstOrNull()?.click()
    }

    fun clickFeedsVideo() {
        val firstVideo = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.2, 0.8, 0.0, 1.0, 0.02, 0.9).getOrNull(0)
            ?: return
        val firstVideoBottom = getChildUiObject2(firstVideo, false, "android.widget.LinearLayout", 0.8, 1.0, 0.0, 0.1, 0.0, 1.0, 0.0, 1.0, false)
        firstVideoBottom?.click()
    }

    fun clickFeedsMiniVideo() {
        firstUiObject2("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.8, 0.0, 1.0, 0.02, 0.9)?.click()
        sleep(TIMEOUT_SHORT.toLong())
        waitUiObject2ByText("Swipe up for more", TIMEOUT_SHORT)?.click()
    }

    /**
     * 切换语言
     */
    fun switchLanguage(country: String?, language: String) {
        try {
            var me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM)
            if (me == null) {
                me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT)
            }
            if (me != null) {
                me.click()
            } else {
                waitUiObject2ByDesc("toolbar menu", TIMEOUT_VERY_SHORT.toLong())?.click()
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var settings = waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)
            if (settings == null) {
                settings = waitUiObject2ByText("الإعدادات", TIMEOUT_VERY_SHORT)
            }
            settings?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            // 跳过弹窗
            skipAppDialog()
            val searchEngine = waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM)
            if (searchEngine == null) {
                waitUiObject2ByText("محرك البحث", TIMEOUT_VERY_SHORT)
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var aboutPhoenix = waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM)
            if (aboutPhoenix == null) {
                aboutPhoenix = waitUiObject2ByText("حول Phoenix", TIMEOUT_VERY_SHORT)
            }
            aboutPhoenix?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var verDesc = waitUiObject2ByTextContains("Version", TIMEOUT_MEDIUM)
            if (verDesc == null) {
                verDesc = waitUiObject2ByTextContains("إصدار", TIMEOUT_VERY_SHORT)
            }
            for (i in 0..4) {
                verDesc?.click()
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var locale = waitUiObject2ByText("Locale Setting", TIMEOUT_MEDIUM)
            if (locale == null) {
                locale = waitUiObject2ByText("Locale Test", TIMEOUT_VERY_SHORT)
            }
            if (locale == null) {
                val phxIcon = getUiObject2s("android.widget.ImageView", true, 0.2, 0.5, 0.05, 0.5, 0.2, 0.8, 0.05, 0.5).getOrNull(0)
                for (i in 0..4) {
                    phxIcon?.click()
                }
                sleep(TIMEOUT_VERY_SHORT.toLong())
                locale = waitUiObject2ByText("Locale Setting", TIMEOUT_MEDIUM)
                if (locale == null) {
                    locale = waitUiObject2ByText("Locale Test", TIMEOUT_VERY_SHORT)
                }
            }
            locale?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val countryLanguage = waitUiObject2ByText(country + "-" + language, TIMEOUT_MEDIUM)
            if (countryLanguage != null) {
                countryLanguage.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            } else {
                if (waitUiObject2ByText("Type to search country", TIMEOUT_MEDIUM) == null) {
                    swip(0.5, 0.8, 0.5, 0.2)
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    waitUiObject2ByText("Custom Setting", TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
                waitUiObject2ByText("Type to search country", TIMEOUT_MEDIUM)?.setText(country)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                waitUiObject2ByTextContains(country + " |", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                for (i in 0..9) {
                    if (waitUiObject2ByText("About Phoenix", TIMEOUT_SHORT) == null && waitUiObject2ByText(
                            "حول Phoenix",
                            TIMEOUT_VERY_SHORT
                        ) == null
                    ) {
                        back()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                    } else {
                        break
                    }
                }
                sleep(TIMEOUT_VERY_SHORT.toLong())
                swip(0.5, 0.2, 0.5, 0.8)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                var languageSet = waitUiObject2ByText("Language", TIMEOUT_MEDIUM)
                if (languageSet == null) {
                    languageSet = waitUiObject2ByText("اللغة", TIMEOUT_VERY_SHORT)
                }
                languageSet?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                if (language == "en") {
                    waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "English", false)?.click()
                } else if (language == "fr") {
                    waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "français", false)?.click()
                } else if (language == "ar") {
                    waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "العربية", false)?.click()
                }
                sleep(TIMEOUT_VERY_SHORT.toLong())
                var restart = waitUiObject2ByText("Restart", TIMEOUT_MEDIUM)
                if (restart == null) {
                    restart = waitUiObject2ByText("إعادة التشغيل", TIMEOUT_VERY_SHORT)
                }
                if (restart != null) {
                    restart.click()
                    sleep(TIMEOUT_LONG.toLong())
                } else {
                    backToHome()
                }
            }

            // 强制停止后再重启
            forceStopApp(device, BROWSER_PHX, null)
            sleep(TIMEOUT_SHORT.toLong())
            startApp(BROWSER_PHX)
            sleep(TIMEOUT_LONG.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeAdDialog(pkgName: String) {
        val activity = getActivity(device, pkgName, null)
        if (pkgName == BROWSER_PHX) {
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
                    sleep(TIMEOUT_SHORT.toLong())
                    amStartApp(device, activity, null)
                }
                sleep(TIMEOUT_SHORT.toLong())
            } else {
                var closeBtns = getUiObject2s("android.widget.Button", true, 0.05, 0.3, 0.05, 0.3, 0.0, 1.0, 0.0, 0.3)
                if (closeBtns.isEmpty()) {
                    closeBtns = getUiObject2s("android.widget.ImageButton", true, 0.05, 0.3, 0.05, 0.3, 0.0, 1.0, 0.0, 0.3)
                }
                if (closeBtns.isNotEmpty()) {
                    // 处理uiautomator有时点击时会报异常
                    try {
                        closeBtns[0].click()
                    } catch (e: Exception) {
                        pressHome(device, null)
                        sleep(TIMEOUT_SHORT.toLong())
                        amStartApp(device, activity, null)
                    }
                    sleep(TIMEOUT_SHORT.toLong())
                }
            }
        }
    }

    private var mainMemThread: MemoryThread? = null
    private var subMemThread0: MemoryThread? = null
    private var subMemThread1: MemoryThread? = null
    private var subMemThread2: MemoryThread? = null
    private var subMemThread3: MemoryThread? = null
    private var subMemThread4: MemoryThread? = null
    private var subMemThread5: MemoryThread? = null

    fun startMonitorMainMem(resultFolder: File?, scenesName: String?, pkgName: String) {
        val mainMemFile = File(resultFolder, "mem_${scenesName}_${pkgName}.txt")
        deleteFile(mainMemFile)
        createFile(mainMemFile)
        mainMemThread = MemoryThread(0.5f, pkgName, device, mainMemFile, 1)
        mainMemThread?.start()
    }

    fun startMonitorSubMem(resultFolder: File?, scenesName: String?, pkgName: String) {
        val subProcess: MutableList<String> = ProcessInfo(device, pkgName).getSubProcess().toMutableList()
        for (i in subProcess.indices) {
            val tmpSubProcess = subProcess[i]
            val subMemFile = File(resultFolder, "mem_${scenesName}_${tmpSubProcess.replace(":".toRegex(), ".")}.txt")
            deleteFile(subMemFile)
            createFile(subMemFile)
            if (i == 0) {
                subMemThread0 = MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1)
                subMemThread0?.start()
            } else if (i == 1) {
                subMemThread1 = MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1)
                subMemThread1?.start()
            } else if (i == 2) {
                subMemThread2 = MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1)
                subMemThread2?.start()
            } else if (i == 3) {
                subMemThread3 = MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1)
                subMemThread3?.start()
            } else if (i == 4) {
                subMemThread4 = MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1)
                subMemThread4?.start()
            } else if (i == 5) {
                subMemThread5 = MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1)
                subMemThread5?.start()
            }
        }
    }

    fun stopMonitorMainMem() {
        try {
            if (mainMemThread != null) {
                mainMemThread?.setIsTimeOver(true)
                mainMemThread?.join()
            }
            mainMemThread = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopMonitorSubMem() {
        try {
            if (subMemThread0 != null) {
                subMemThread0?.setIsTimeOver(true)
                subMemThread0?.join()
            }
            if (subMemThread1 != null) {
                subMemThread1?.setIsTimeOver(true)
                subMemThread1?.join()
            }
            if (subMemThread2 != null) {
                subMemThread2?.setIsTimeOver(true)
                subMemThread2?.join()
            }
            if (subMemThread3 != null) {
                subMemThread3?.setIsTimeOver(true)
                subMemThread3?.join()
            }
            if (subMemThread4 != null) {
                subMemThread4?.setIsTimeOver(true)
                subMemThread4?.join()
            }
            if (subMemThread5 != null) {
                subMemThread5?.setIsTimeOver(true)
                subMemThread5?.join()
            }
            subMemThread0 = null
            subMemThread1 = null
            subMemThread2 = null
            subMemThread3 = null
            subMemThread4 = null
            subMemThread5 = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var cpuThread: CpuThread? = null

    fun startMonitorMainCpu(resultFolder: File?, scenesName: String?, pkgName: String) {
        val cpuResultFile = File(resultFolder, "cpu_${scenesName}_${pkgName}.txt")
        deleteFile(cpuResultFile)
        createFile(cpuResultFile)
        val processInfo = ProcessInfo(device, pkgName)
        val pid = processInfo.pid
        cpuThread = CpuThread(device, pid, cpuResultFile)
        cpuThread?.start()
    }

    fun stopMonitorMainCpu() {
        try {
            if (cpuThread != null) {
                cpuThread?.setIsEnd(true)
                cpuThread?.join()
            }
            cpuThread = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var fpsThread: FpsThread? = null

    fun startMonitorMainFps(resultFolder: File?, scenesName: String?, pkgName: String) {
        val fpsResultFile = File(resultFolder, "fps_${scenesName}_${pkgName}.txt")
        deleteFile(fpsResultFile)
        createFile(fpsResultFile)
        fpsThread = FpsThread(device, pkgName, fpsResultFile)
        fpsThread?.start()
    }

    fun stopMonitorMainFps() {
        try {
            if (fpsThread != null) {
                fpsThread?.setIsEnd(true)
                fpsThread?.join()
            }
            fpsThread = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var flowResultFile: File? = null
    private var uid = 0
    private var startFlow: Long = 0
    private var endFlow: Long = 0

    fun startMonitorMainFlow(resultFolder: File?, scenesName: String?, pkgName: String) {
        val currentFlowResultFile = File(resultFolder, "flow_${scenesName}_${pkgName}.txt")
        flowResultFile = currentFlowResultFile
        FileUtil.deleteFile(currentFlowResultFile)
        FileUtil.createFile(currentFlowResultFile)
        val processInfo = ProcessInfo(device, pkgName)
        val pid = processInfo.pid
        uid = processInfo.getUid(pid)
        FileUtil.writeStrToFile("uid:$uid\n", currentFlowResultFile)
        startFlow = Flowinfo(device, uid).getFlow()
        FileUtil.writeStrToFile("startFlow:$startFlow\n", currentFlowResultFile)
    }

    fun stopMonitorMainFlow() {
        // 获取结束流量,并计算(统计的是b，需转换为kb)
        val currentFlowResultFile = flowResultFile ?: return
        endFlow = Flowinfo(device, uid).getFlow()
        FileUtil.writeStrToFile("endFlow:$endFlow\n", currentFlowResultFile)
        val totalFlow = keepDecimalPoint((endFlow - startFlow).toDouble() / 1024, 1)
        FileUtil.writeStrToFile("totalFlow:$totalFlow\n", currentFlowResultFile)
        // 重置
        flowResultFile = null
        uid = 0
        startFlow = 0
        endFlow = 0
    }

    companion object {
        var BROWSER_PHX: String = "com.transsion.phoenix"
        var BROWSER_CHROME: String = "com.android.chrome"
        var BROWSER_OPERA: String = "com.opera.browser"
        var BROWSER_UC: String = "com.UCMobile.intl"
        var BROWSER_FIREFOX: String = "org.mozilla.firefox"

        var NEWS_OPERA: String = "com.opera.app.news"
        var NEWS_SCOOPER: String = "com.hatsune.eagleee"
        var NEWS_TOUTIAO: String = "com.ss.android.article.news"

        var SMALLVIDEO_VISKIT: String = "com.yomobigroup.chat"
        var SMALLVIDEO_LIKEE: String = "video.like"
        var SMALLVIDEO_TIKTOK: String = "com.zhiliaoapp.musically"

        var URL_GOOGLE: String = "https://www.google.com"
        var URL_GOOGLE_SEARCH: String = "https://www.google.com/search?q=test"
        var URL_YOUTUBE: String = "https://www.youtube.com"
        var URL_YAHOO: String = "https://www.yahoo.com"
        var URL_AMAZON: String = "https:// www.amazon.com"
        var URL_TIWTTER: String = "https://mobile.twitter.com"
        var URL_BAIDU: String = "https://m.baidu.com"
        var URL_BAIDU_SEARCH: String = "https://www.baidu.com/s?word=test"
        var URL_YOUKU: String = "https://www.youku.com"
        var URL_QQ: String = "https://www.qq.com"
    }
}
