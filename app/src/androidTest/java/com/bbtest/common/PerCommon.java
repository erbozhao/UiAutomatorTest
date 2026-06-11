package com.bbtest.common;

import androidx.test.uiautomator.UiObject2;

import com.bbtest.perform.base.CpuThread;
import com.bbtest.perform.base.Flowinfo;
import com.bbtest.perform.base.FpsThread;
import com.bbtest.stable.threads.MemoryThread;
import com.bbtest.tools.ProcessInfo;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.util.List;

/**
 * @author onuszhao
 */
public class PerCommon extends BaseCommon {

    public static String BROWSER_PHX = "com.transsion.phoenix";
    public static String BROWSER_CHROME = "com.android.chrome";
    public static String BROWSER_OPERA = "com.opera.browser";
    public static String BROWSER_UC = "com.UCMobile.intl";
    public static String BROWSER_FIREFOX = "org.mozilla.firefox";

    public static String NEWS_OPERA = "com.opera.app.news";
    public static String NEWS_SCOOPER = "com.hatsune.eagleee";
    public static String NEWS_TOUTIAO = "com.ss.android.article.news";

    public static String SMALLVIDEO_VISKIT = "com.yomobigroup.chat";
    public static String SMALLVIDEO_LIKEE = "video.like";
    public static String SMALLVIDEO_TIKTOK = "com.zhiliaoapp.musically";

    public static String URL_GOOGLE = "https://www.google.com";
    public static String URL_GOOGLE_SEARCH = "https://www.google.com/search?q=test";
    public static String URL_YOUTUBE = "https://www.youtube.com";
    public static String URL_YAHOO = "https://www.yahoo.com";
    public static String URL_AMAZON = "https:// www.amazon.com";
    public static String URL_TIWTTER = "https://mobile.twitter.com";
    public static String URL_BAIDU = "https://m.baidu.com";
    public static String URL_BAIDU_SEARCH = "https://www.baidu.com/s?word=test";
    public static String URL_YOUKU = "https://www.youku.com";
    public static String URL_QQ = "https://www.qq.com";

    public File perFolder = new File(rootFolder, "perform");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(perFolder);
    }

    @After
    public void afterTest() {
        super.afterTest();
    }

    /**
     * 搜索或访问网址
     */
    public void searchOrUrl(String pkgName, String textOrUrl, boolean isOpenedPage) {
        if (pkgName.equals(BROWSER_CHROME)) {
            UiObject2 searchBox = null;
            if (isOpenedPage) {
                searchBox = waitUiObject2ByRes("com.android.chrome:id/url_bar", TIMEOUT_MEDIUM);
            } else {
                searchBox = waitUiObject2ByRes("com.android.chrome:id/search_box_text", TIMEOUT_MEDIUM);
            }
            searchBox.click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("com.android.chrome:id/url_bar", TIMEOUT_MEDIUM).setText(textOrUrl);
            sleep(TIMEOUT_VERY_SHORT);
            ShellCommon.pressEnter(device, null);
        } else if (pkgName.equals(BROWSER_OPERA)) {
            waitUiObject2ByRes("com.opera.browser:id/url_field", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("com.opera.browser:id/url_field", TIMEOUT_MEDIUM).setText(textOrUrl);
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("com.opera.browser:id/right_state_button", TIMEOUT_MEDIUM).click();
        } else if (pkgName.equals(BROWSER_PHX)) {
            List<UiObject2> searchBoxs = null;
            if (isOpenedPage) {
                searchBoxs = getUiObject2s("android.widget.LinearLayout", true, 0.5, 1, 0.04, 0.5, 0, 1, 0.02, 0.4);
            } else {
                searchBoxs = getUiObject2s("android.widget.TextSwitcher", false, 0.5, 1, 0.01, 0.5, 0, 1, 0.02, 0.4);
                if (searchBoxs == null || searchBoxs.size() == 0) {
                    searchBoxs = getUiObject2s("android.widget.TextView", false, 0.5, 1, 0.01, 0.5, 0, 1, 0.02, 0.4);
                }
            }
            searchBoxs.get(0).click();
            sleep(TIMEOUT_SHORT);
            List<UiObject2> textBoxs = getUiObject2s("android.widget.EditText", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.02, 0.4);
            textBoxs.get(0).setText(textOrUrl);
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 go = waitUiObject2ByDesc("addressbar go", TIMEOUT_MEDIUM);
            if (go == null) {
                go = waitUiObject2ByDesc("addressbar search", TIMEOUT_SHORT);
            }
            go.click();
        } else if (pkgName.equals(BROWSER_UC)) {
            List<UiObject2> searchBoxs = getUiObject2s("android.widget.TextView", true, 0.5, 1, 0.04, 0.5, 0, 1, 0.02, 0.4);
            searchBoxs.get(0).click();
            sleep(TIMEOUT_SHORT);
            List<UiObject2> textBoxs = getUiObject2s("android.widget.EditText", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.02, 0.4);
            textBoxs.get(0).setText(textOrUrl);
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 go = waitUiObject2ByRes("com.UCMobile.intl:id/address_bar_go_search", TIMEOUT_MEDIUM);
            if (go == null) {
                go = waitUiObject2ByDesc("Go button", TIMEOUT_SHORT);
            }
            go.click();
        } else if (pkgName.equals(BROWSER_FIREFOX)) {
            if (isOpenedPage) {
                waitUiObject2ByRes("org.mozilla.firefox:id/mozac_browser_toolbar_url_view", TIMEOUT_MEDIUM).click();
            } else {
                waitUiObject2ByRes("org.mozilla.firefox:id/toolbar", TIMEOUT_MEDIUM).click();
            }
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("org.mozilla.firefox:id/mozac_browser_toolbar_edit_url_view", TIMEOUT_MEDIUM).setText(textOrUrl);
            sleep(TIMEOUT_SHORT);
            enter();
        }
    }

    /**
     * 进入多窗口
     */
    public void goinTab(String pkgName) {
        if (pkgName.equals(BROWSER_CHROME)) {
            waitUiObject2ByRes("com.android.chrome:id/tab_switcher_button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
        } else if (pkgName.equals(BROWSER_OPERA)) {
            waitUiObject2ByRes("com.opera.browser:id/bottom_navigation_bar_tab_count_button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
        } else if (pkgName.equals(BROWSER_PHX)) {
            UiObject2 multiWindow = waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM);
            if (multiWindow == null) {
                multiWindow = waitUiObject2ByText("التبويب", TIMEOUT_MEDIUM);
            }
            if (multiWindow == null) {
                multiWindow = waitUiObject2ByDesc("toolbar multiWindow", TIMEOUT_SHORT);
            }
            multiWindow.click();
            sleep(TIMEOUT_SHORT);
        } else if (pkgName.equals(BROWSER_UC)) {
            waitUiObject2ByDescContains("tap to switch button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
        } else if (pkgName.equals(BROWSER_FIREFOX)) {
            waitUiObject2ByRes("org.mozilla.firefox:id/counter_root", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
        }
    }

    /**
     * 新建一个窗口
     */
    public void newTab(String pkgName) {
        if (pkgName.equals(BROWSER_CHROME)) {
            waitUiObject2ByRes("com.android.chrome:id/tab_switcher_button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("com.android.chrome:id/new_tab_view", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
        } else if (pkgName.equals(BROWSER_OPERA)) {
            waitUiObject2ByRes("com.opera.browser:id/bottom_navigation_bar_tab_count_button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("com.opera.browser:id/tab_menu_add_tab", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
        } else if (pkgName.equals(BROWSER_PHX)) {
            UiObject2 multiWindow = waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM);
            if (multiWindow == null) {
                multiWindow = waitUiObject2ByText("التبويب", TIMEOUT_MEDIUM);
            }
            if (multiWindow == null) {
                multiWindow = waitUiObject2ByDesc("toolbar multiWindow", TIMEOUT_SHORT);
            }
            multiWindow.click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.7, 1).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
        } else if (pkgName.equals(BROWSER_UC)) {
            waitUiObject2ByDescContains("tap to switch button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            List<UiObject2> addImgs = getUiObject2s("android.widget.ImageView", true, 0, 0.3, 0, 0.3, 0, 1, 0.8, 1);
            addImgs.get(1).click();
            sleep(TIMEOUT_VERY_SHORT);
        } else if (pkgName.equals(BROWSER_FIREFOX)) {
            waitUiObject2ByRes("org.mozilla.firefox:id/counter_root", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("org.mozilla.firefox:id/new_tab_button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("org.mozilla.firefox:id/toolbar", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("org.mozilla.firefox:id/mozac_browser_toolbar_edit_url_view", TIMEOUT_MEDIUM).setText("about:blank");
            sleep(TIMEOUT_SHORT);
            enter();
            sleep(TIMEOUT_VERY_SHORT);
        }
    }

    /**
     * 关闭所有窗口
     */
    public void closeAllTabs(String pkgName) {
        if (pkgName.equals(BROWSER_CHROME)) {
            waitUiObject2ByRes("com.android.chrome:id/tab_switcher_button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("com.android.chrome:id/menu_button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Close all tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("com.android.chrome:id/new_tab_view", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
        } else if (pkgName.equals(BROWSER_OPERA)) {
            waitUiObject2ByRes("com.opera.browser:id/bottom_navigation_bar_tab_count_button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("com.opera.browser:id/tab_menu_menu_button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Close all tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("CLOSE", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
        } else if (pkgName.equals(BROWSER_PHX)) {
            UiObject2 multiWindow = waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM);
            if (multiWindow == null) {
                multiWindow = waitUiObject2ByText("التبويب", TIMEOUT_MEDIUM);
            }
            if (multiWindow == null) {
                multiWindow = waitUiObject2ByDesc("toolbar multiWindow", TIMEOUT_SHORT);
            }
            multiWindow.click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1, 0.03, 0.15).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Close all tabs", TIMEOUT_SHORT).click();
            sleep(TIMEOUT_VERY_SHORT);
        } else if (pkgName.equals(BROWSER_UC)) {
            waitUiObject2ByDescContains("tap to switch button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            List<UiObject2> moreIcons = getUiObject2s("android.widget.FrameLayout", true, 0, 0.3, 0, 0.3, 0.8, 1, 0.02, 0.3);
            if (moreIcons == null || moreIcons.size() == 0) {
                getUiObject2s("android.widget.ImageView", true, 0, 0.3, 0, 0.3, 0.8, 1, 0.02, 0.3).get(0).click();
            }
            moreIcons.get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Close All Tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
        } else if (pkgName.equals(BROWSER_FIREFOX)) {
            waitUiObject2ByRes("org.mozilla.firefox:id/counter_root", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("org.mozilla.firefox:id/tab_tray_overflow", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 closeAllTabs = waitUiObject2ByText("Close all tabs", TIMEOUT_MEDIUM);
            if (closeAllTabs != null) {
                closeAllTabs.click();
            } else {
                back();
                sleep(TIMEOUT_VERY_SHORT);
                back();
            }
            sleep(TIMEOUT_VERY_SHORT);
        }
    }

    /**
     * 退出浏览器
     */
    public void exitBrowser(String pkgName) {
        String topActivity = ShellCommon.getTopActivity(device, null);
        for (int i = 0; i < 10; i++) {
            back();
            if (pkgName.equals(BROWSER_PHX)) {
                UiObject2 exit = waitUiObject2ByText("Exit", TIMEOUT_VERY_SHORT);
                if (exit != null) {
                    exit.click();
                } else {
                    back();
                }
            } else if (pkgName.equals(BROWSER_UC)) {
                UiObject2 doNotAskAgain = waitUiObject2ByText("Do not ask again", TIMEOUT_VERY_SHORT);
                if (doNotAskAgain != null) {
                    doNotAskAgain.click();
                    sleep(TIMEOUT_VERY_SHORT);
                    waitUiObject2ByText("Exit", TIMEOUT_VERY_SHORT).click();
                } else {
                    back();
                }
            }
            sleep(TIMEOUT_SHORT);
            String curActivity = ShellCommon.getTopActivity(device, null);
            if (!curActivity.equals(topActivity)) {
                break;
            }
        }
    }

    /**
     * 向上滑动
     */
    public void swipUp(int num, long millis) {
        for (int j = 0; j < num; j++) {
            swip(0.5, 0.7, 0.5, 0.3);
            sleep(millis);
        }
    }

    /**
     * 向下滑动
     */
    public void swipDown(int num, long millis) {
        for (int j = 0; j < num; j++) {
            swip(0.5, 0.3, 0.5, 0.7);
            sleep(millis);
        }
    }

    /**
     * 向左滑动
     */
    public void swipLeft(int num, long millis) {
        for (int j = 0; j < num; j++) {
            swip(0.7, 0.5, 0.3, 0.5);
            sleep(millis);
        }
    }

    /**
     * 向右滑动
     */
    public void swipRight(int num, long millis) {
        for (int j = 0; j < num; j++) {
            swip(0.3, 0.5, 0.7, 0.5);
            sleep(millis);
        }
    }

    /**
     * 保证返回到主页
     */
    public void backToHome() {
        for (int i = 0; i < 30; i++) {
            // 先判断，若先返回容易出现弹窗界面去判断
            if (waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_SHORT) != null) {
                // 返回判断是否有退出弹窗
                back();
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 sureExit0 = waitUiObject2ByText("Sure to exit now?", TIMEOUT_VERY_SHORT);
                if (sureExit0 == null) {
                    sureExit0 = waitUiObject2ByText("تأكيد الخروج الآن ؟", TIMEOUT_VERY_SHORT);
                }
                if (sureExit0 != null) {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                    break;
                }
                UiObject2 sureExit1 = waitUiObject2ByText("Do you have any difficulties While using Phoenix Browser？", TIMEOUT_VERY_SHORT);
                if (sureExit1 == null) {
                    sureExit1 = waitUiObject2ByText("هل واجهتط صعوبات أثناء رحلتك مع فينيكس", TIMEOUT_VERY_SHORT);
                }
                if (sureExit1 != null) {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                    break;
                }
                UiObject2 clearExit0 = waitUiObject2ByText("Clear history on exit?", TIMEOUT_VERY_SHORT);
                if (clearExit0 != null) {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                    break;
                }
                UiObject2 clearExit1 = waitUiObject2ByText("Clear History When Exit ?", TIMEOUT_VERY_SHORT);
                if (clearExit1 != null) {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                    break;
                }
            } else {
                if (i > 8) {
                    UiObject2 dialog = waitUiObject2ByText("OK", TIMEOUT_SHORT);
                    if (dialog == null) {
                        dialog = waitUiObject2ByText("موافق", TIMEOUT_VERY_SHORT);
                    }
                    if (dialog != null) {
                        dialog.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    UiObject2 yesDialog = waitUiObject2ByText("Yes", TIMEOUT_VERY_SHORT);
                    if (yesDialog == null) {
                        yesDialog = waitUiObject2ByText("نعم", TIMEOUT_VERY_SHORT);
                    }
                    if (yesDialog != null) {
                        yesDialog.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    UiObject2 continueDialog = waitUiObject2ByText("Continue", TIMEOUT_VERY_SHORT);
                    if (continueDialog == null) {
                        continueDialog = waitUiObject2ByText("متابعة", TIMEOUT_VERY_SHORT);
                    }
                    if (continueDialog != null) {
                        continueDialog.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    UiObject2 allowDialog = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT);
                    if (allowDialog == null) {
                        allowDialog = waitUiObject2ByText("السماح", TIMEOUT_VERY_SHORT);
                    }
                    if (allowDialog != null) {
                        allowDialog.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    UiObject2 customDialog = waitUiObject2ByText("You can customize your news feeds in just two steps", TIMEOUT_SHORT);
                    if (customDialog == null) {
                        customDialog = waitUiObject2ByText("يمكنك تخصيص موجز الأخبار الخاص بك فى خطوتين", TIMEOUT_VERY_SHORT);
                    }
                    if (customDialog != null) {
                        getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 1, 0.1, 0.5).get(0).click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    UiObject2 skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/closeButton", TIMEOUT_VERY_SHORT);
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/close", TIMEOUT_VERY_SHORT);
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/trybuttom", TIMEOUT_VERY_SHORT);
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByText("Try", TIMEOUT_VERY_SHORT);
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByText("جَرّبه الآن", TIMEOUT_VERY_SHORT);
                    }
                    if (skipButton != null) {
                        skipButton.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                } else {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                }
            }
        }
    }

    /**
     * 跳过应用弹窗
     */
    public void skipAppDialog() {
        UiObject2 defaultBrowserDialog = waitUiObject2ByText("Continue", TIMEOUT_VERY_SHORT);
        if (defaultBrowserDialog != null) {
            back();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 rateUs = waitUiObject2ByText("Rate us 5 stars", TIMEOUT_VERY_SHORT);
        if (rateUs != null) {
            back();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 offline = waitUiObject2ByText("The network is not connected, check the offline content right now.", TIMEOUT_VERY_SHORT);
        if (offline != null) {
            back();
            sleep(TIMEOUT_VERY_SHORT);
        }
    }

    /**
     * 跳过其他弹窗
     */
    public void skipOtherDialog() {
        UiObject2 agree = waitUiObject2ByText("AGREE", TIMEOUT_VERY_SHORT);
        if (agree != null) {
            agree.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 accept = waitUiObject2ByText("Accept", TIMEOUT_VERY_SHORT);
        if (accept != null) {
            accept.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 allow = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT);
        if (allow != null) {
            allow.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 ok = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT);
        if (ok != null) {
            ok.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 later = waitUiObject2ByText("Later", TIMEOUT_VERY_SHORT);
        if (later != null) {
            later.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
    }

    public String getScrollableClazz() {
        String scrollableClazz = "";
        if (getScrollableUiObject2("androidx.recyclerview.widget.RecyclerView") != null) {
            scrollableClazz = "androidx.recyclerview.widget.RecyclerView";
        } else if (getScrollableUiObject2("android.support.v7.widget.RecyclerView") != null) {
            scrollableClazz = "android.support.v7.widget.RecyclerView";
        } else if (getScrollableUiObject2("android.webkit.WebView") != null) {
            scrollableClazz = "android.webkit.WebView";
        } else if (getScrollableUiObject2("android.widget.ListView") != null) {
            scrollableClazz = "android.widget.ListView";
        } else if (getScrollableUiObject2("android.view.ViewGroup") != null) {
            scrollableClazz = "android.view.ViewGroup";
        } else {
            List<UiObject2> uiObject2s = getAllUiObject2s(getRootObject());
            for (UiObject2 uiObject2 : uiObject2s) {
                if (uiObject2.isScrollable()) {
                    scrollableClazz = uiObject2.getClassName();
                    break;
                }
            }
        }
        return scrollableClazz;
    }

    /**
     * 点击搜索框
     */
    public void clickSearchBox(String pkgName, boolean isOpenedPage) {
        if (pkgName.equals(BROWSER_CHROME)) {
            UiObject2 searchBox = null;
            if (isOpenedPage) {
                searchBox = waitUiObject2ByRes("com.android.chrome:id/url_bar", TIMEOUT_MEDIUM);
            } else {
                searchBox = waitUiObject2ByRes("com.android.chrome:id/search_box_text", TIMEOUT_MEDIUM);
            }
            searchBox.click();
        } else if (pkgName.equals(BROWSER_OPERA)) {
            waitUiObject2ByRes("com.opera.browser:id/url_field", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
        } else if (pkgName.equals(BROWSER_PHX)) {
            List<UiObject2> searchBoxs = null;
            if (isOpenedPage) {
                searchBoxs = getUiObject2s("android.widget.LinearLayout", true, 0.5, 1, 0.04, 0.5, 0, 1, 0.02, 0.4);
            } else {
                searchBoxs = getUiObject2s("android.widget.TextSwitcher", false, 0.5, 1, 0.01, 0.5, 0, 1, 0.02, 0.4);
                if (searchBoxs == null || searchBoxs.size() == 0) {
                    searchBoxs = getUiObject2s("android.widget.TextView", false, 0.5, 1, 0.01, 0.5, 0, 1, 0.02, 0.4);
                }
            }
            searchBoxs.get(0).click();
        } else if (pkgName.equals(BROWSER_UC)) {
            List<UiObject2> searchBoxs = getUiObject2s("android.widget.TextView", true, 0.5, 1, 0.04, 0.5, 0, 1, 0.02, 0.4);
            searchBoxs.get(0).click();
        } else if (pkgName.equals(BROWSER_FIREFOX)) {
            if (isOpenedPage) {
                waitUiObject2ByRes("org.mozilla.firefox:id/mozac_browser_toolbar_url_view", TIMEOUT_MEDIUM).click();
            } else {
                waitUiObject2ByRes("org.mozilla.firefox:id/toolbar", TIMEOUT_MEDIUM).click();
            }
        }

    }

    /**
     * 搜索框输入文字
     */
    public void setTextAndGo(String pkgName, String textOrUrl) {
        if (pkgName.equals(BROWSER_CHROME)) {
            waitUiObject2ByRes("com.android.chrome:id/url_bar", TIMEOUT_MEDIUM).setText(textOrUrl);
            sleep(TIMEOUT_VERY_SHORT);
            ShellCommon.pressEnter(device, null);
        } else if (pkgName.equals(BROWSER_OPERA)) {
            waitUiObject2ByRes("com.opera.browser:id/url_field", TIMEOUT_MEDIUM).setText(textOrUrl);
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("com.opera.browser:id/right_state_button", TIMEOUT_MEDIUM).click();
        } else if (pkgName.equals(BROWSER_PHX)) {
            List<UiObject2> textBoxs = getUiObject2s("android.widget.EditText", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.02, 0.4);
            textBoxs.get(0).setText(textOrUrl);
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 go = waitUiObject2ByDesc("addressbar go", TIMEOUT_MEDIUM);
            if (go == null) {
                go = waitUiObject2ByDesc("addressbar search", TIMEOUT_SHORT);
            }
            go.click();
        } else if (pkgName.equals(BROWSER_UC)) {
            List<UiObject2> textBoxs = getUiObject2s("android.widget.EditText", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.02, 0.4);
            textBoxs.get(0).setText(textOrUrl);
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 go = waitUiObject2ByRes("com.UCMobile.intl:id/address_bar_go_search", TIMEOUT_MEDIUM);
            if (go == null) {
                go = waitUiObject2ByDesc("Go button", TIMEOUT_SHORT);
            }
            go.click();
        } else if (pkgName.equals(BROWSER_FIREFOX)) {
            waitUiObject2ByRes("org.mozilla.firefox:id/mozac_browser_toolbar_edit_url_view", TIMEOUT_MEDIUM).setText(textOrUrl);
            sleep(TIMEOUT_SHORT);
            enter();
        }
    }

    /**
     * 切换指定Feeds Tab
     */
    public void switchFeedsTab(String tabName) {
        getUiObject2s("android.widget.FrameLayout", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.1, 0.6).get(0).click();
        sleep(TIMEOUT_SHORT);
        getUiObject2ByChildText("android.widget.FrameLayout", true, tabName, "android.widget.TextView").click();
        UiObject2 done = waitUiObject2ByText("Done", TIMEOUT_SHORT);
        if (done != null) {
            done.click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2ByChildText("android.widget.FrameLayout", true, tabName, "android.widget.TextView").click();
        }
        sleep(TIMEOUT_MEDIUM);
    }

    public void clickFeedsNews() {
        List<UiObject2> news = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.1, 0.3, 0, 1, 0.02, 0.8);
        if (news == null || news.size() == 0) {
            for (int i = 0; i < 3; i++) {
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_MEDIUM);
                news = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.1, 0.3, 0, 1, 0.02, 0.8);
                if (news != null) {
                    break;
                }
            }
        }
        news.get(0).click();
    }

    public void clickFeedsVideo() {
        UiObject2 firstVideo = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.2, 0.8, 0, 1, 0.02, 0.9).get(0);
        UiObject2 firstVideoBottom = getChildUiObject2(firstVideo, false, "android.widget.LinearLayout", 0.8, 1, 0, 0.1, 0, 1, 0, 1, false);
        firstVideoBottom.click();
    }

    public void clickFeedsMiniVideo() {
        getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.8, 0, 1, 0.02, 0.9).get(0).click();
        sleep(TIMEOUT_SHORT);
        UiObject2 swipeToast = waitUiObject2ByText("Swipe up for more", TIMEOUT_SHORT);
        if (swipeToast != null) {
            swipeToast.click();
        }
    }

    /**
     * 切换语言
     */
    public void switchLanguage(String country, String language) {
        try {
            UiObject2 me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM);
            if (me == null) {
                me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT);
            }
            if (me != null) {
                me.click();
            } else {
                waitUiObject2ByDesc("toolbar menu", TIMEOUT_VERY_SHORT).click();
            }
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 settings = waitUiObject2ByText("Settings", TIMEOUT_MEDIUM);
            if (settings == null) {
                settings = waitUiObject2ByText("الإعدادات", TIMEOUT_VERY_SHORT);
            }
            settings.click();
            sleep(TIMEOUT_VERY_SHORT);
            // 跳过弹窗
            skipAppDialog();
            UiObject2 searchEngine = waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM);
            if (searchEngine == null) {
                waitUiObject2ByText("محرك البحث", TIMEOUT_VERY_SHORT);
            }
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.5, 0.8, 0.5, 0.2);
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 aboutPhoenix = waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM);
            if (aboutPhoenix == null) {
                aboutPhoenix = waitUiObject2ByText("حول Phoenix", TIMEOUT_VERY_SHORT);
            }
            aboutPhoenix.click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 verDesc = waitUiObject2ByTextContains("Version", TIMEOUT_MEDIUM);
            if (verDesc == null) {
                verDesc = waitUiObject2ByTextContains("إصدار", TIMEOUT_VERY_SHORT);
            }
            for (int i = 0; i < 5; i++) {
                verDesc.click();
            }
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 locale = waitUiObject2ByText("Locale Setting", TIMEOUT_MEDIUM);
            if (locale == null) {
                locale = waitUiObject2ByText("Locale Test", TIMEOUT_VERY_SHORT);
            }
            if (locale == null) {
                UiObject2 phxIcon = getUiObject2s("android.widget.ImageView", true, 0.2, 0.5, 0.05, 0.5, 0.2, 0.8, 0.05, 0.5).get(0);
                for (int i = 0; i < 5; i++) {
                    phxIcon.click();
                }
                sleep(TIMEOUT_VERY_SHORT);
                locale = waitUiObject2ByText("Locale Setting", TIMEOUT_MEDIUM);
                if (locale == null) {
                    locale = waitUiObject2ByText("Locale Test", TIMEOUT_VERY_SHORT);
                }
            }
            locale.click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 countryLanguage = waitUiObject2ByText(country + "-" + language, TIMEOUT_MEDIUM);
            if (countryLanguage != null) {
                countryLanguage.click();
                sleep(TIMEOUT_VERY_SHORT);
            } else {
                if (waitUiObject2ByText("Type to search country", TIMEOUT_MEDIUM) == null) {
                    swip(0.5, 0.8, 0.5, 0.2);
                    sleep(TIMEOUT_VERY_SHORT);
                    waitUiObject2ByText("Custom Setting", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_VERY_SHORT);
                }
                waitUiObject2ByText("Type to search country", TIMEOUT_MEDIUM).setText(country);
                sleep(TIMEOUT_VERY_SHORT);
                waitUiObject2ByTextContains(country + " |", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
                for (int i = 0; i < 10; i++) {
                    if (waitUiObject2ByText("About Phoenix", TIMEOUT_SHORT) == null && waitUiObject2ByText("حول Phoenix", TIMEOUT_VERY_SHORT) == null) {
                        back();
                        sleep(TIMEOUT_VERY_SHORT);
                    } else {
                        break;
                    }
                }
                sleep(TIMEOUT_VERY_SHORT);
                swip(0.5, 0.2, 0.5, 0.8);
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 languageSet = waitUiObject2ByText("Language", TIMEOUT_MEDIUM);
                if (languageSet == null) {
                    languageSet = waitUiObject2ByText("اللغة", TIMEOUT_VERY_SHORT);
                }
                languageSet.click();
                sleep(TIMEOUT_VERY_SHORT);
                if (language.equals("en")) {
                    waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "English", false).click();
                } else if (language.equals("fr")) {
                    waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "français", false).click();
                } else if (language.equals("ar")) {
                    waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "العربية", false).click();
                }
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 restart = waitUiObject2ByText("Restart", TIMEOUT_MEDIUM);
                if (restart == null) {
                    restart = waitUiObject2ByText("إعادة التشغيل", TIMEOUT_VERY_SHORT);
                }
                if (restart != null) {
                    restart.click();
                    sleep(TIMEOUT_LONG);
                } else {
                    backToHome();
                }
            }

            // 强制停止后再重启
            ShellCommon.forceStopApp(device, BROWSER_PHX, null);
            sleep(TIMEOUT_SHORT);
            startApp(BROWSER_PHX);
            sleep(TIMEOUT_LONG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeAdDialog(String pkgName) {
        String activity = ShellCommon.getActivity(device, pkgName, null);
        if (pkgName.equals(BROWSER_PHX)) {
            UiObject2 closeBtn = waitUiObject2ByRes("close-button-container", TIMEOUT_MEDIUM);
            if (closeBtn == null) {
                closeBtn = waitUiObject2ByText("CLOSE", TIMEOUT_VERY_SHORT);
            }
            if (closeBtn != null) {
                // 处理uiautomator有时点击时会报异常
                try {
                    closeBtn.click();
                } catch (Exception e) {
                    ShellCommon.pressHome(device, null);
                    sleep(TIMEOUT_SHORT);
                    ShellCommon.amStartApp(device, activity, null);
                }
                sleep(TIMEOUT_SHORT);
            } else {
                List<UiObject2> closeBtns = getUiObject2s("android.widget.Button", true, 0.05, 0.3, 0.05, 0.3, 0, 1, 0, 0.3);
                if (closeBtns == null || closeBtns.size() == 0) {
                    closeBtns = getUiObject2s("android.widget.ImageButton", true, 0.05, 0.3, 0.05, 0.3, 0, 1, 0, 0.3);
                }
                if (closeBtns != null && closeBtns.size() > 0) {
                    // 处理uiautomator有时点击时会报异常
                    try {
                        closeBtns.get(0).click();
                    } catch (Exception e) {
                        ShellCommon.pressHome(device, null);
                        sleep(TIMEOUT_SHORT);
                        ShellCommon.amStartApp(device, activity, null);
                    }
                    sleep(TIMEOUT_SHORT);
                }
            }
        }
    }

    private MemoryThread mainMemThread = null;
    private MemoryThread subMemThread0 = null;
    private MemoryThread subMemThread1 = null;
    private MemoryThread subMemThread2 = null;
    private MemoryThread subMemThread3 = null;
    private MemoryThread subMemThread4 = null;
    private MemoryThread subMemThread5 = null;

    public void startMonitorMainMem(File resultFolder, String scenesName, String pkgName) {
        File mainMemFile = new File(resultFolder, "mem_" + scenesName + "_" + pkgName + ".txt");
        FileUtil.deleteFile(mainMemFile);
        FileUtil.createFile(mainMemFile);
        mainMemThread = new MemoryThread(0.5f, pkgName, device, mainMemFile, 1);
        mainMemThread.start();
    }

    public void startMonitorSubMem(File resultFolder, String scenesName, String pkgName) {
        List<String> subProcess = new ProcessInfo(device, pkgName).getSubProcess();
        for (int i = 0; i < subProcess.size(); i++) {
            String tmpSubProcess = subProcess.get(i);
            File subMemFile = new File(resultFolder, "mem_" + scenesName + "_" + tmpSubProcess.replaceAll(":", ".") + ".txt");
            FileUtil.deleteFile(subMemFile);
            FileUtil.createFile(subMemFile);
            if (i == 0) {
                subMemThread0 = new MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1);
                subMemThread0.start();
            } else if (i == 1) {
                subMemThread1 = new MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1);
                subMemThread1.start();
            } else if (i == 2) {
                subMemThread2 = new MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1);
                subMemThread2.start();
            } else if (i == 3) {
                subMemThread3 = new MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1);
                subMemThread3.start();
            } else if (i == 4) {
                subMemThread4 = new MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1);
                subMemThread4.start();
            } else if (i == 5) {
                subMemThread5 = new MemoryThread(0.5f, tmpSubProcess, device, subMemFile, 1);
                subMemThread5.start();
            }
        }
    }

    public void stopMonitorMainMem() {
        try {
            if (mainMemThread != null) {
                mainMemThread.setIsTimeOver(true);
                mainMemThread.join();
            }
            mainMemThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMonitorSubMem() {
        try {
            if (subMemThread0 != null) {
                subMemThread0.setIsTimeOver(true);
                subMemThread0.join();
            }
            if (subMemThread1 != null) {
                subMemThread1.setIsTimeOver(true);
                subMemThread1.join();
            }
            if (subMemThread2 != null) {
                subMemThread2.setIsTimeOver(true);
                subMemThread2.join();
            }
            if (subMemThread3 != null) {
                subMemThread3.setIsTimeOver(true);
                subMemThread3.join();
            }
            if (subMemThread4 != null) {
                subMemThread4.setIsTimeOver(true);
                subMemThread4.join();
            }
            if (subMemThread5 != null) {
                subMemThread5.setIsTimeOver(true);
                subMemThread5.join();
            }
            subMemThread0 = null;
            subMemThread1 = null;
            subMemThread2 = null;
            subMemThread3 = null;
            subMemThread4 = null;
            subMemThread5 = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CpuThread cpuThread = null;

    public void startMonitorMainCpu(File resultFolder, String scenesName, String pkgName) {
        File cpuResultFile = new File(resultFolder, "cpu_" + scenesName + "_" + pkgName + ".txt");
        FileUtil.deleteFile(cpuResultFile);
        FileUtil.createFile(cpuResultFile);
        ProcessInfo processInfo = new ProcessInfo(device, pkgName);
        int pid = processInfo.getPid();
        cpuThread = new CpuThread(device, pid, cpuResultFile);
        cpuThread.start();
    }

    public void stopMonitorMainCpu() {
        try {
            if (cpuThread != null) {
                cpuThread.setIsEnd(true);
                cpuThread.join();
            }
            cpuThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FpsThread fpsThread = null;

    public void startMonitorMainFps(File resultFolder, String scenesName, String pkgName) {
        File fpsResultFile = new File(resultFolder, "fps_" + scenesName + "_" + pkgName + ".txt");
        FileUtil.deleteFile(fpsResultFile);
        FileUtil.createFile(fpsResultFile);
        fpsThread = new FpsThread(device, pkgName, fpsResultFile);
        fpsThread.start();
    }

    public void stopMonitorMainFps() {
        try {
            if (fpsThread != null) {
                fpsThread.setIsEnd(true);
                fpsThread.join();
            }
            fpsThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File flowResultFile;
    private int uid = 0;
    private long startFlow = 0;
    private long endFlow = 0;

    public void startMonitorMainFlow(File resultFolder, String scenesName, String pkgName) {
        flowResultFile = new File(resultFolder,"flow_" + scenesName + "_" + pkgName + ".txt");
        FileUtil.deleteFile(flowResultFile);
        FileUtil.createFile(flowResultFile);
        ProcessInfo processInfo = new ProcessInfo(device, pkgName);
        int pid = processInfo.getPid();
        uid = processInfo.getUid(pid);
        FileUtil.writeStrToFile("uid:" + uid + "\n", flowResultFile);
        startFlow = new Flowinfo(device, uid).getFlow();
        FileUtil.writeStrToFile("startFlow:" + startFlow + "\n", flowResultFile);
    }

    public void stopMonitorMainFlow() {
        // 获取结束流量,并计算(统计的是b，需转换为kb)
        endFlow = new Flowinfo(device, uid).getFlow();
        FileUtil.writeStrToFile("endFlow:" + endFlow + "\n", flowResultFile);
        double totalFlow = CommonUtil.keepDecimalPoint((double) (endFlow - startFlow) / 1024, 1);
        FileUtil.writeStrToFile("totalFlow:" + totalFlow + "\n", flowResultFile);
        // 重置
        flowResultFile = null;
        uid = 0;
        startFlow = 0;
        endFlow = 0;
    }
}
