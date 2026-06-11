package com.bbtest.compatible;

import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PhxCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.ShellCommand;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainPathTest extends PhxCommon {

    private File resultFolder = new File(rootFolder, "compatible");
    private File mainPathFile = new File(resultFolder, "mainpath.txt");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
        FileUtil.deleteFile(mainPathFile);
    }

    @Test
    public void testStartApp() {
        try {
            // 先授权
            ShellCommon.grantApkPermission(device, pkgName);

            // 再启动应用->跳过闪屏->切换语言->停止应用
            startApp(pkgName);
            sleep(TIMEOUT_LONG);
            backToHome();
            skipFilesGuide();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 forYou = waitUiObject2ByText("For you", TIMEOUT_MEDIUM);
            if (forYou == null) {
                switchLanguage("NG", "en");
            }
            backToHome();
            backExitBrowser();
            ShellCommon.forceStopApp(device, pkgName, null);

            // 再次启动应用->跳过Feeds上滑->停止应用
            startApp(pkgName);
            sleep(TIMEOUT_LONG);
            backToHome();
            skipFeedsGuide();
            backToHome();
            backExitBrowser();
            ShellCommon.forceStopApp(device, pkgName, null);

            //启动应用
            startApp(pkgName);
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("StartApp:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/StartApp_" + CommonUtil.getCurTimeForFile() + ".jpg");
            e.printStackTrace();
        }
    }

    /**
     * 设置默认浏览器
     */
    @Test
    public void testSetDefaultBrowser() {
        try {
            UiObject2 me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM);
            if (me == null) {
                me = waitUiObject2ByDesc("toolbar menu", TIMEOUT_SHORT);
            }
            me.click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            swip(0.5, 0.8, 0.5, 0.2);
            sleep(TIMEOUT_VERY_SHORT);
            boolean isSetDefaultSuccess = getUiObject2ByChildText("android.widget.LinearLayout", true, "Set as default browser", "android.widget.Switch").isChecked();
            if (!isSetDefaultSuccess) {
                waitUiObject2ByText("Set as default browser", TIMEOUT_MEDIUM).click();
                UiObject2 next = waitUiObject2ByText("Continue", TIMEOUT_MEDIUM);
                if (next == null) {
                    next = waitUiObject2ByText("Next", TIMEOUT_SHORT);
                }
                next.click();
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 dialogPHXBrowser = waitUiObject2ByText("Phoenix", TIMEOUT_MEDIUM);
                if (dialogPHXBrowser != null) {
                    dialogPHXBrowser.click();
                    UiObject2 setAsDefault = waitUiObject2ByText("Set as default", TIMEOUT_SHORT);
                    if (setAsDefault == null) {
                        setAsDefault = waitUiObject2ByText("SET AS DEFAULT", TIMEOUT_SHORT);
                    }
                    setAsDefault.click();
                    sleep(TIMEOUT_VERY_SHORT);
                    waitUiObject2ByText("Set as default browser", TIMEOUT_MEDIUM);
                } else {
                    UiObject2 browserApp = waitUiObject2ByText("Browser app", TIMEOUT_MEDIUM);
                    if (browserApp != null) {
                        browserApp.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        UiObject2 phx = waitUiObject2ByText("Phoenix", TIMEOUT_MEDIUM);
                        if (phx == null) {
                            phx = waitUiObject2ByText("PHX Browser", TIMEOUT_MEDIUM);
                        }
                        phx.click();
                        if (ShellCommon.isAppBackstage(device, pkgName)) {
                            ShellCommon.amStartApp(device, activity, null);
                        }
                    }
                }
                isSetDefaultSuccess = getUiObject2ByChildText("android.widget.LinearLayout", true, "Set as default browser", "android.widget.Switch").isChecked();
            }
            if (isSetDefaultSuccess) {
                FileUtil.writeStrToFile("SetDefaultBrowser:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("SetDefaultBrowser:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/SetDefaultBrowser_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SetDefaultBrowser:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SetDefaultBrowser_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-登录
     */
    @Test
    public void testMeLogin() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Login", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Continue with Google", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            waitUiObject2ByTextContains("@gmail.com", TIMEOUT_LONG).click();
            sleep(TIMEOUT_SHORT);
            boolean isSigned = isUiObject2ExistByText("Signed in with Google", TIMEOUT_LONG);
            if (isSigned) {
                FileUtil.writeStrToFile("MeLogin:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("MeLogin:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/MeLogin_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeLogin:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeLogin_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 常规操作天气
     */
    @Test
    public void testOperateWeather() {
        try {
            List<UiObject2> weathers = getUiObject2s("android.widget.LinearLayout", true, 0, 0.4, 0, 0.2, 0, 0.4, 0.02, 0.2);
            if (weathers == null || weathers.size() == 0) {
                backToHome();
                sleep(TIMEOUT_LONG);
                weathers = getUiObject2s("android.widget.LinearLayout", true, 0, 0.4, 0, 0.2, 0, 0.4, 0.02, 0.2);
            }
            if (weathers != null && weathers.size() > 0) {
                weathers.get(0).click();
                sleep(TIMEOUT_MEDIUM);
                waitUiObject2ByText("Air quality", TIMEOUT_LONG);
                List<UiObject2> images = getUiObject2s("android.widget.ImageView", true, 0, 0.5, 0, 0.5, 0.8, 1, 0.02, 0.2);
                if (images == null || images.size() == 0) {
                    images = getUiObject2s("android.widget.ImageView", true, 0, 0.5, 0, 0.5, 0.8, 1, 0.02, 0.2);
                }
                images.get(0).click();
                waitUiObject2ByText("Manage city", TIMEOUT_MEDIUM).click();
                waitUiObject2ByText("Add city", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);
                device.click(width / 2, height / 2);
                sleep(TIMEOUT_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                swip(0.5, 0.3, 0.5, 0.7);
                sleep(TIMEOUT_VERY_SHORT);
                swip(0.7, 0.5, 0.3, 0.5);
                sleep(TIMEOUT_VERY_SHORT);
                swip(0.3, 0.5, 0.7, 0.5);
                sleep(TIMEOUT_SHORT);
                List<UiObject2> imageViews = getUiObject2s("android.widget.ImageView", true, 0, 0.5, 0, 0.5, 0, 0.2, 0.02, 0.2);
                for (int i = 0; i < 3; i++) {
                    if (imageViews != null && imageViews.size() > 0) {
                        break;
                    } else {
                        sleep(TIMEOUT_SHORT);
                        imageViews = getUiObject2s("android.widget.ImageView", true, 0, 0.5, 0, 0.5, 0, 0.2, 0.02, 0.2);
                    }
                }
                imageViews.get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
                FileUtil.writeStrToFile("OperateWeather:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("OperateWeather:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/OperateWeather_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("OperateWeather:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/OperateWeather_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 搜索关键词
     */
    @Test
    public void testSearchKeywords() {
        try {
            clickSearchBox(false);
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.5, 0, 0.5, 0, 0.2, 0.02, 0.2).get(0).click();
            waitUiObject2ByText("Yahoo", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.LinearLayout", true, 0.3, 0.5, 0, 0.5, 0, 0.6, 0.1, 0.4).get(0).click();
            sleep(TIMEOUT_SHORT);
            boolean isSearchKeywordSuccess = isUiObject2ExistByDesc("toolbar menu", TIMEOUT_MEDIUM);
            if (isSearchKeywordSuccess) {
                FileUtil.writeStrToFile("SearchKeywords:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("SearchKeywords:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/SearchKeywords_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SearchKeywords:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SearchKeywords_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 打开网页
     */
    @Test
    public void testOpenWebpage() {
        try {
            clickSearchBox(false);
            sleep(TIMEOUT_SHORT);
            setTextAndGo("www.qq.com");
            sleep(TIMEOUT_SHORT);
            skipAppDialog();
            skipOtherDialog();
            FileUtil.writeStrToFile("OpenWebpage:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("OpenWebpage:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/OpenWebpage_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 网页长按弹框
     */
    @Test
    public void testWebviewLongpressDialog() {
        try {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com");
            sleep(TIMEOUT_LONG);

            // 网页长按弹窗
            int index = 5;
            for (int i = 7; i < 3; i--) {
                longClick(width / 2, height * i / 9);
                if (waitUiObject2ByText("Open in new tab", TIMEOUT_SHORT) != null) {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                    index = i;
                    break;
                }
            }
            longClick(width / 2, height * index / 9);
            waitUiObject2ByText("Open in new tab", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            longClick(width / 2, height * index / 9);
            waitUiObject2ByText("Open in incognito tab", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            longClick(width / 2, height * index / 9);
            waitUiObject2ByText("Copy link", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            longClick(width / 2, height * index / 9);
            waitUiObject2ByText("Copy link text", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            longClick(width / 2, height * index / 9);
            waitUiObject2ByText("Share link", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("WebviewLongpressDialog:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("WebviewLongpressDialog:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/WebviewLongpressDialog_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 网页更多菜单
     */
    @Test
    public void testWebpageMoreMenu() {
        try {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com");
            sleep(TIMEOUT_LONG);

            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Add to bookmark", TIMEOUT_MEDIUM).click();
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Add to speed dial", TIMEOUT_MEDIUM).click();
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Add to home screen", TIMEOUT_MEDIUM).click();
            UiObject2 addHomeOk = waitUiObject2ByText("Add automatically", TIMEOUT_SHORT);
            if (addHomeOk == null) {
                addHomeOk = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT);
            }
            if (addHomeOk == null) {
                addHomeOk = waitUiObject2ByText("ADD", TIMEOUT_VERY_SHORT);
            }
            if (addHomeOk != null) {
                addHomeOk.click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Share", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Save page for offline", TIMEOUT_MEDIUM).click();
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Save as PDF", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_LONG).click();
            waitUiObject2ByText("Find in page", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Cancel", TIMEOUT_MEDIUM).click();
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Translate page", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Switch to desktop site", TIMEOUT_MEDIUM).click();
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Switch to mobile site", TIMEOUT_MEDIUM).click();
            waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Screenshot", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Crop region", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.5, 0, 0.5, 0.7, 1, 0.02, 0.15).get(0).click();
            waitUiObject2ByText("Save", TIMEOUT_MEDIUM).click();
            FileUtil.writeStrToFile("WebpageMoreMenu:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("WebpageMoreMenu:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/WebpageMoreMenu_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 网页工具栏(含主菜单)
     */
    @Test
    public void testWebpageMenu() {
        try {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com");
            sleep(TIMEOUT_LONG);

            // 前进后退
            sleep(TIMEOUT_SHORT);
            click(width / 2, height * 5 / 9);
            sleep(TIMEOUT_SHORT);
            waitUiObject2ByDesc("toolbar back", TIMEOUT_MEDIUM).click();
            waitUiObject2ByDesc("toolbar forward", TIMEOUT_MEDIUM).click();

            // 进入菜单-书签
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            UiObject2 bookmark = waitUiObject2ByText("Bookmarks", TIMEOUT_MEDIUM);
            if (bookmark == null) {
                bookmark = waitUiObject2ByText("Bookmark", TIMEOUT_SHORT);
            }
            bookmark.click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            // 进入历史
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("History", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);

            // 进入下载
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);

            // 进入文件
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);

            // 进入我的视频
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("My video", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);

            // 进入我的音乐
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("My music", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);

            // 进入广告拦截
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            UiObject2 adBlock = waitUiObject2ByRes("com.transsion.phoenix:id/adBlocked", TIMEOUT_MEDIUM);
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Adblocker", TIMEOUT_VERY_SHORT);
            }
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Ads blocked", TIMEOUT_VERY_SHORT);
            }
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Ad block", TIMEOUT_VERY_SHORT);
            }
            adBlock.click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);

            // 进入日(夜)间模式
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            UiObject2 darkLight = waitUiObject2ByText("Dark", TIMEOUT_SHORT);
            if (darkLight == null) {
                darkLight = waitUiObject2ByText("Light", TIMEOUT_SHORT);
            }
            darkLight.click();
            sleep(TIMEOUT_VERY_SHORT);

            // 设置
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_SHORT).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);

            // 多窗口
            waitUiObject2ByDesc("toolbar multiWindow", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.7, 1).get(0).click();
            FileUtil.writeStrToFile("WebpageMenu:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("WebpageMenu:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/WebpageMenu_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 扫一扫
     */
    @Test
    public void testScan() {
        try {
            waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.5, 0, 0.5, 0, 0.3, 0.8, 1).get(0).click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.5, 0.15, 0.5, 0, 1, 0.15, 1).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("Scan:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("Scan:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/Scan_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 快链跳转
     */
    @Test
    public void testSpeedDialAccess() {
        try {
            // 点击快链进入
            waitUiObject2ByText("All Sites", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_MEDIUM);
            for (int i = 0; i < 3; i++) {
                List<UiObject2> allSites = getUiObject2s("android.view.View", true, 0.4, 0.6, 0.02, 0.2, 0, 1, 0.2, 0.9);
                if (allSites != null && allSites.size() > 0) {
                    allSites.get(0).click();
                    sleep(TIMEOUT_SHORT);
                    waitUiObject2ByDesc("toolbar home", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_VERY_SHORT);
                    FileUtil.writeStrToFile("SpeedDialAccess:PASS" + "\n", mainPathFile);
                    break;
                } else {
                    sleep(TIMEOUT_SHORT);
                    if (i == 2) {
                        back();
                        sleep(TIMEOUT_VERY_SHORT);
                        FileUtil.writeStrToFile("SpeedDialAccess:FAILED" + "\n", mainPathFile);
                        screenshot(resultFolder + "/SpeedDialAccess_" + CommonUtil.getCurTimeForFile() + ".jpg");
                    }
                }
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SpeedDialAccess:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SpeedDialAccess_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 添加满快链
     */
    @Test
    public void testSpeedDialAdds() {
        try {
            List<UiObject2> curSpeedDials = getUiObject2sByClazz("android.widget.TextView");
            List<String> curSpeedDialTexts = getTexts(curSpeedDials);
            for (int i = 0; i < 20; i++) {
                // 进入添加快链
                List<UiObject2> linearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0.15, 0.25, 0.02, 0.5, 0, 1, 0.2, 0.8);
                UiObject2 lastLinearLayout = linearLayouts.get(linearLayouts.size() - 1);
                String lastLinearLayoutText = getTextOrDesc(lastLinearLayout, true);
                if (lastLinearLayoutText == null || lastLinearLayoutText.equals("") || lastLinearLayoutText.equals("Add")) {
                    lastLinearLayout.click();
                    sleep(TIMEOUT_SHORT);
                } else {
                    break;
                }

                // 点击未添加快链添加
                List<UiObject2> addSites = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    if (getUiObject2sByClazz("android.view.View").size() > 0) {
                        addSites = getUiObject2s("android.view.View", true, 0.4, 0.6, 0.02, 0.2, 0, 1, 0.2, 0.9);
                        break;
                    }
                }
                boolean isClickAddSite = false;
                for (UiObject2 addSite : addSites) {
                    String addSiteText = getTextOrDesc(addSite, true);
                    if (!curSpeedDialTexts.contains(addSiteText)) {
                        addSite.click();
                        sleep(TIMEOUT_SHORT);
                        curSpeedDialTexts.add(addSiteText);
                        isClickAddSite = true;
                        break;
                    }
                }
                if (!isClickAddSite) {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                    break;
                }

                // 判断是否添加满
                UiObject2 gotIt = waitUiObject2ByText("Got it", TIMEOUT_VERY_SHORT);
                if (gotIt != null) {
                    gotIt.click();
                    sleep(TIMEOUT_VERY_SHORT);
                    break;
                }
            }
            FileUtil.writeStrToFile("SpeedDialAdds:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SpeedDialAdds:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SpeedDialAdds_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 删除所有快链
     */
    @Test
    public void testSpeedDialDels() {
        try {
            for (int i = 0; i < 19; i++) {
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 secondSpeedDial = getUiObject2s("android.widget.LinearLayout", true, 0.15, 0.25, 0.02, 0.5, 0, 1, 0.2, 0.8).get(1);
                String secondSpeedDialText = getTextOrDesc(secondSpeedDial, true);
                if (secondSpeedDialText == null || secondSpeedDialText.equals("") || secondSpeedDialText.equals("Add")) {
                    break;
                } else {
                    longClick(secondSpeedDial);
                    sleep(TIMEOUT_VERY_SHORT);
                    waitUiObject2ByText("Delete", TIMEOUT_SHORT).click();
                    sleep(TIMEOUT_VERY_SHORT);
                }
            }
            FileUtil.writeStrToFile("SpeedDialDels:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SpeedDialDels:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SpeedDialDels_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * Feeds删除所有tab
     */
    @Test
    public void testFeedsTabDels() {
        try {
            // 删除所有tab
            getUiObject2s("android.widget.FrameLayout", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.1, 0.8).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Edit", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            for (int i = 0; i < 20; i++) {
                UiObject2 addChannel = waitUiObject2ByText("Add channel", TIMEOUT_SHORT);
                double maxY = 0.9;
                if (addChannel != null) {
                    maxY = (double) addChannel.getVisibleBounds().top / height;
                }
                List<UiObject2> tabs = getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0, 0.2, 0, 1, 0.1, maxY);
                UiObject2 lastTab = tabs.get(tabs.size() - 1);
                String lastTabText = getText(lastTab, true);
                if (lastTabText != null && lastTabText.equals("Video")) {
                    break;
                } else {
                    lastTab.click();
                }
            }
            waitUiObject2ByText("Done", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.1, 0.3, 0.05, 0.2, 0, 0.5, 0.02, 0.5).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FeedsTabDels:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FeedsTabDels:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FeedsTabDels_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * Feeds添加所有tab
     */
    @Test
    public void testFeedsTabAdds() {
        try {
            getUiObject2s("android.widget.FrameLayout", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.1, 0.8).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            for (int i = 0; i < 20; i++) {
                UiObject2 addChannel = waitUiObject2ByText("Add channel", TIMEOUT_SHORT);
                if (addChannel == null) {
                    break;
                } else {
                    double minY = (double) addChannel.getVisibleBounds().bottom / height;
                    List<UiObject2> tabs = getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0, 0.2, 0, 1, minY, 1);
                    System.out.println(tabs.size());
                    for (UiObject2 tab : tabs) {
                        String tabTxt = getText(tab, true);
                        System.out.println(tab.getVisibleBounds().toString() + "," + tabTxt);
                        if (tabTxt != null && !tabTxt.equals("")) {
                            tab.click();
                            break;
                        }
                    }
                }
            }
            waitUiObject2ByText("Done", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FeedsTabAdds:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FeedsTabAdds:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FeedsTabAdds_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * Feeds新闻
     */
    @Test
    public void testFeedsNews() {
        try {
            // 打开Feeds新闻
            switchFeedsTab("Lifestyle");
            sleep(TIMEOUT_MEDIUM);
            getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.1, 0.5, 0, 1, 0.02, 0.8).get(0).click();
            sleep(TIMEOUT_MEDIUM);

            // Feeds新闻-省流
            List<UiObject2> topRightImgs = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0.02, 0.2, 0.6, 1, 0.02, 0.3);
            for (int i = 0; i < 3; i++) {
                if (topRightImgs != null && topRightImgs.size() >= 2) {
                    break;
                } else {
                    sleep(TIMEOUT_SHORT);
                    topRightImgs = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0.02, 0.2, 0.6, 1, 0.02, 0.3);
                }
            }
            topRightImgs.get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2ByChildText("android.widget.LinearLayout", true, "Prompt saving result", "android.widget.Switch").click();
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.2, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);

            // Feeds新闻-更多按钮
            if (waitUiObject2ByText("Home", TIMEOUT_SHORT) != null) {
                getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.1, 0.5, 0, 1, 0.02, 0.8).get(0).click();
                sleep(TIMEOUT_MEDIUM);
            }
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.02, 0.2, 0.6, 1, 0.02, 0.3).get(1).click();
            waitUiObject2ByText("Add to favorites", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.02, 0.2, 0.6, 1, 0.02, 0.3).get(1).click();
            waitUiObject2ByText("Share", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.02, 0.2, 0.6, 1, 0.02, 0.3).get(1).click();
            UiObject2 dartLight = waitUiObject2ByText("Dark mode", TIMEOUT_SHORT);
            if (dartLight == null) {
                dartLight = waitUiObject2ByText("Light mode", TIMEOUT_SHORT);
            }
            dartLight.click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.02, 0.2, 0.6, 1, 0.02, 0.3).get(1).click();
            waitUiObject2ByText("Report", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.02, 0.2, 0.6, 1, 0.02, 0.3).get(1).click();
            waitUiObject2ByText("Font size", TIMEOUT_MEDIUM).click();
            UiObject2 seekBar = waitUiObject2sByClazz("android.widget.SeekBar", TIMEOUT_MEDIUM).get(0);
            swip(seekBar, "right");
            back();
            sleep(TIMEOUT_VERY_SHORT);

            // Feeds新闻-底部工具栏
            List<UiObject2> frameLayouts = getUiObject2s("android.widget.FrameLayout", true, 0, 0.19, 0, 0.2, 0, 1, 0.8, 1);
            if (frameLayouts != null) {
                for (int i = 0; i < frameLayouts.size(); i++) {
                    frameLayouts.get(i).click();
                    sleep(TIMEOUT_VERY_SHORT);
                    if (i == 0) {
                        back();
                        sleep(TIMEOUT_VERY_SHORT);
                    } else if (i == 1) {
                        swip(0.5, 0.6, 0.5, 0.8);
                        sleep(TIMEOUT_VERY_SHORT);
                        break;
                    }
                }
            }
            List<UiObject2> linearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0, 0.8, 0, 0.2, 0, 1, 0.8, 1);
            if (linearLayouts != null && linearLayouts.size() > 0) {
                linearLayouts.get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }

            List<UiObject2> imageViews = getUiObject2s("android.widget.ImageView", true, 0, 0.19, 0, 0.2, 0, 1, 0.88, 1);
            if (imageViews != null) {
                imageViews.get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }
            FileUtil.writeStrToFile("FeedsNews:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FeedsNews:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FeedsNews_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * Feeds图片
     */
    @Test
    public void testFeedsImg() {
        try {
            switchFeedsTab("Hot Girl");
            sleep(TIMEOUT_MEDIUM);
            List<UiObject2> bigImgLinearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.2, 0.8, 0, 1, 0.02, 1);
            if (bigImgLinearLayouts == null || bigImgLinearLayouts.size() == 0) {
                bigImgLinearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0.4, 0.6, 0.1, 0.5, 0, 1, 0.02, 1);
            }
            bigImgLinearLayouts.get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            if (waitUiObject2ByText("Save", TIMEOUT_SHORT) == null) {
                List<UiObject2> bigImgs = getUiObject2s("android.widget.FrameLayout", true, 0.8, 1, 0.5, 0.8, 0, 1, 0.02, 1);
                if (bigImgs != null && bigImgs.size() > 0) {
                    bigImgs.get(0).click();
                    sleep(TIMEOUT_VERY_SHORT);
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                }

                // 底部工具栏
                List<UiObject2> frameLayouts = getUiObject2s("android.widget.FrameLayout", true, 0, 0.19, 0, 0.2, 0, 1, 0.8, 1);
                if (frameLayouts != null) {
                    for (int i = 0; i < frameLayouts.size(); i++) {
                        frameLayouts.get(i).click();
                        sleep(TIMEOUT_VERY_SHORT);
                        if (i == 0) {
                            back();
                            sleep(TIMEOUT_VERY_SHORT);
                        } else if (i == 1) {
                            swip(0.5, 0.6, 0.5, 0.8);
                            sleep(TIMEOUT_VERY_SHORT);
                            break;
                        }
                    }
                }
                List<UiObject2> linearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0, 0.8, 0, 0.2, 0, 1, 0.8, 1);
                if (linearLayouts != null && linearLayouts.size() > 0) {
                    linearLayouts.get(0).click();
                    sleep(TIMEOUT_VERY_SHORT);
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                }

                List<UiObject2> imageViews = getUiObject2s("android.widget.ImageView", true, 0, 0.19, 0, 0.2, 0, 1, 0.88, 1);
                if (imageViews != null && imageViews.size() > 0) {
                    imageViews.get(0).click();
                    sleep(TIMEOUT_VERY_SHORT);
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                }
            } else {
                for (int i = 0; i < 11; i++) {
                    swip(0.7, 0.5, 0.3, 0.5);
                    sleep(TIMEOUT_VERY_SHORT);
                }
            }
            FileUtil.writeStrToFile("FeedsImg:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FeedsImg:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FeedsImg_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * Feeds视频
     */
    @Test
    public void testFeedsVideos() {
        try {
            // Feeds视频-打开
            switchFeedsTab("Video");
            sleep(TIMEOUT_MEDIUM);
            UiObject2 firstVideo = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.2, 0.8, 0, 1, 0.02, 0.9).get(0);
            UiObject2 firstVideoBottom = getChildUiObject2(firstVideo, false, "android.widget.LinearLayout", 0.8, 1, 0, 0.1, 0, 1, 0, 1, false);
            firstVideoBottom.click();
            sleep(TIMEOUT_SHORT);

            // Feeds视频-底部工具栏
            List<UiObject2> frameLayouts = getUiObject2s("android.widget.FrameLayout", true, 0, 0.19, 0, 0.2, 0, 1, 0.8, 1);
            if (frameLayouts != null) {
                for (int i = 0; i < frameLayouts.size(); i++) {
                    frameLayouts.get(i).click();
                    sleep(TIMEOUT_VERY_SHORT);
                    if (i == 0) {
                        back();
                        sleep(TIMEOUT_VERY_SHORT);
                    } else if (i == 1) {
                        swip(0.5, 0.6, 0.5, 0.8);
                        sleep(TIMEOUT_VERY_SHORT);
                        break;
                    }
                }
            }
            List<UiObject2> linearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0, 0.8, 0, 0.2, 0, 1, 0.8, 1);
            if (linearLayouts != null && linearLayouts.size() > 0) {
                linearLayouts.get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }

            List<UiObject2> imageViews = getUiObject2s("android.widget.ImageView", true, 0, 0.19, 0, 0.2, 0, 1, 0.88, 1);
            if (imageViews != null) {
                imageViews.get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }
            FileUtil.writeStrToFile("FeedsVideos:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FeedsVideos:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FeedsVideos_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * Feeds小视频
     */
    @Test
    public void testFeedsSmallVideo() {
        try {
            // Feeds小视频
            switchFeedsTab("Short Video");
            sleep(TIMEOUT_MEDIUM);
            getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.8, 0, 1, 0.02, 0.9).get(0).click();
            sleep(TIMEOUT_SHORT);
            UiObject2 swipeToast = waitUiObject2ByText("Swipe up for more", TIMEOUT_SHORT);
            if (swipeToast != null) {
                swipeToast.click();
            }
            swip(0.5, 0.7, 0.5, 0.3);
            sleep(TIMEOUT_SHORT);
            swip(0.5, 0.3, 0.5, 0.7);
            sleep(TIMEOUT_SHORT);

            // Feeds小视频-右侧工具栏
            getUiObject2s("android.widget.LinearLayout", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.3, 1).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.LinearLayout", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.3, 1).get(1).click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.2, 1).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.LinearLayout", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.3, 1).get(2).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.LinearLayout", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.3, 1).get(3).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Download", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            UiObject2 downloadDialog = waitUiObject2ByText("Video saved", TIMEOUT_VERY_LONG);
            if (downloadDialog != null) {
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }
            FileUtil.writeStrToFile("FeedsSmallVideo:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FeedsSmallVideo:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FeedsSmallVideo_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-消息
     */
    @Test
    public void testMeMsg() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            UiObject2 login = waitUiObject2ByText("Login", TIMEOUT_MEDIUM);
            if (login != null) {
                login.click();
                sleep(TIMEOUT_VERY_SHORT);
                waitUiObject2ByText("Continue with Google", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);
                waitUiObject2ByTextContains("@gmail.com", TIMEOUT_LONG).click();
                sleep(TIMEOUT_SHORT);
                waitUiObject2ByText("Signed in with Google", TIMEOUT_LONG);
                sleep(TIMEOUT_VERY_SHORT);
            }
            getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.01, 0.2, 0.8, 1, 0.02, 0.2).get(0).click();
            sleep(TIMEOUT_SHORT);
            List<UiObject2> comments = getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.1, 0.8, 0, 1, 0.02, 0.9);
            for (int i = 0; i < 3; i++) {
                if (comments == null || comments.size() == 0) {
                    back();
                    sleep(TIMEOUT_SHORT);
                    getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.01, 0.2, 0.8, 1, 0.02, 0.2).get(0).click();
                    sleep(TIMEOUT_SHORT);
                    comments = getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.1, 0.8, 0, 1, 0.02, 0.9);
                } else {
                    break;
                }
            }
            comments.get(0).click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.FrameLayout", true, 0.8, 1, 0.1, 0.8, 0, 1, 0.02, 0.9).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            for (int i = 0; i < 5; i++) {
                if (waitUiObject2ByText("Share Phoenix", TIMEOUT_SHORT) != null) {
                    break;
                } else {
                    getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                    sleep(TIMEOUT_VERY_SHORT);
                }
            }
            FileUtil.writeStrToFile("MeMsg:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeMsg:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeMsg_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-书签
     */
    @Test
    public void testMeBookmark() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 bookmark = waitUiObject2ByText("Bookmarks", TIMEOUT_MEDIUM);
            if (bookmark == null) {
                bookmark = waitUiObject2ByText("Bookmark", TIMEOUT_SHORT);
            }
            bookmark.click();
            waitUiObject2ByText("New Folder", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Title", TIMEOUT_MEDIUM).setText("Test");
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 save = waitUiObject2ByText("Save", TIMEOUT_MEDIUM);
            if (save == null) {
                save = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.02, 0.2).get(0);
            }
            save.click();
            sleep(TIMEOUT_VERY_SHORT);
            longClick(waitUiObject2ByText("Test", TIMEOUT_MEDIUM));
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            List<UiObject2> bookmarkMore = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.02, 0.9);
            if (bookmarkMore != null && bookmarkMore.size() > 0) {
                bookmarkMore.get(0).click();
                waitUiObject2ByText("Delete", TIMEOUT_MEDIUM).click();
            }
            waitUiObject2ByText("Sync", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            if (waitUiObject2ByText("Continue with Google", TIMEOUT_SHORT) != null) {
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }
            getUiObject2s("android.widget.ImageView", true, 0, 0.3, 0, 0.3, 0, 0.5, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("MeBookmark:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeBookmark:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeBookmark_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-历史
     */
    @Test
    public void testMeHistory() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("History", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.02, 0.9).get(0).click();
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Clear", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Clear", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            boolean isCleared = isUiObject2ExistByText("No history", TIMEOUT_MEDIUM);
            getUiObject2s("android.widget.ImageView", true, 0, 0.3, 0, 0.3, 0, 0.5, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            if (isCleared) {
                FileUtil.writeStrToFile("MeHistory:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("MeHistory:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/meHistory_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeHistory:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeHistory_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-我的视频
     */
    @Test
    public void testMeMyVideo() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("My Video", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Local videos", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.8, 0, 1, 0.02, 0.9).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            click(width / 2, height / 2);
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            longClick(waitUiObject2ByTextContains("Watched", TIMEOUT_MEDIUM));
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Remove", TIMEOUT_MEDIUM).click();
            boolean isRemoved = isUiObject2ExistByTextContains("No history", TIMEOUT_MEDIUM);
            getUiObject2s("android.widget.ImageView", true, 0, 0.3, 0, 0.3, 0, 0.5, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            if (isRemoved) {
                FileUtil.writeStrToFile("MeMyVideo:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("MeMyVideo:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/meMyVideo_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeMyVideo:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeMyVideo_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-我的音乐
     */
    @Test
    public void testMeMyMusic() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("My Music", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Local music", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.5, 0, 1, 0.02, 0.9).get(0).click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(1).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("New playlist", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2("New playlist", "android.widget.EditText", 0, 0).setText("Test");
            waitUiObject2ByText("Done", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Add songs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.1, 0.5, 0, 1, 0.02, 0.9).get(0).click();
            waitUiObject2ByText("Add", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.0, 0.3, 0, 0.3, 0, 0.5, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            longClick(waitUiObject2ByText("Test", TIMEOUT_MEDIUM));
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.3, 0, 0.3, 0, 0.5, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("MeMyMusic:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeMyMusic:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeMyMusic_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-广告过滤
     */
    @Test
    public void testMeAdblock() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 adBlock = waitUiObject2ByRes("com.transsion.phoenix:id/adBlocked", TIMEOUT_MEDIUM);
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Adblocker", TIMEOUT_VERY_SHORT);
            }
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Ads blocked", TIMEOUT_VERY_SHORT);
            }
            if (adBlock == null) {
                adBlock = waitUiObject2ByText("Ad block", TIMEOUT_VERY_SHORT);
            }
            adBlock.click();
            getChildUiObject2(waitUiObject2ByDesc("Ad block", TIMEOUT_MEDIUM), true, "android.widget.Switch", 0, 1, 0, 1, 0.5, 1, 0.5, 1, true).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Clear", TIMEOUT_MEDIUM).click();
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("MeAdblock:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeAdblock:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeAdblock_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-日(夜)间模式
     */
    @Test
    public void testMeDarkLight() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 darkLight = waitUiObject2ByText("Dark", TIMEOUT_MEDIUM);
            if (darkLight == null) {
                darkLight = waitUiObject2ByText("Light", TIMEOUT_MEDIUM);
            }
            darkLight.click();
            FileUtil.writeStrToFile("MeDarkLight:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeDarkLight:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeDarkLight_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-Facebook
     */
    @Test
    public void testMeFacebook() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Like us on Facebook", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                ShellCommon.amStartApp(device, activity, null);
                sleep(TIMEOUT_VERY_SHORT);
            } else {
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }
            FileUtil.writeStrToFile("MeFacebook:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeFacebook:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeFacebook_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-分享
     */
    @Test
    public void testMeShare() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Share Phoenix", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            for (int i = 0; i < 5; i++) {
                back();
                sleep(TIMEOUT_VERY_SHORT);
                if (waitUiObject2ByText("Share via", TIMEOUT_SHORT) == null) {
                    break;
                }
            }
            FileUtil.writeStrToFile("MeShare:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeShare:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeShare_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-帮助反馈
     */
    @Test
    public void testMeHelpFeedback() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            // 上滑
            swip(0.5, 0.7, 0.5, 0.3);
            sleep(TIMEOUT_VERY_SHORT);

            // 帮助中心
            waitUiObject2ByText("Help and feedback", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_LONG);
            List<UiObject2> views = getUiObject2s("android.view.View", true, 0.8, 1, 0.05, 0.2, 0, 1, 0.02, 0.9);
            for (int i = 0; i < 3; i++) {
                if (views == null) {
                    sleep(TIMEOUT_SHORT);
                    views = getUiObject2s("android.view.View", true, 0.8, 1, 0.05, 0.2, 0, 1, 0.02, 0.9);
                } else {
                    break;
                }
            }
            views.get(1).click();
            sleep(TIMEOUT_VERY_SHORT);
            for (int i = 0; i < 10; i++) {
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                if (waitUiObject2ByText("Helpful", TIMEOUT_SHORT) != null) {
                    break;
                }
            }
            swip(0.5, 0.7, 0.5, 0.3);
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 helpful = waitUiObject2ByText("Helpful", TIMEOUT_SHORT);
            if (helpful == null) {
                helpful = waitUiObject2ByDesc("Helpful", TIMEOUT_VERY_SHORT);
            }
            helpful.click();
            UiObject2 whatsAppGroup = waitUiObject2ByText("Join WhatsApp group to feedback", TIMEOUT_SHORT);
            if (whatsAppGroup == null) {
                whatsAppGroup = waitUiObject2ByDesc("Join WhatsApp group to feedback", TIMEOUT_VERY_SHORT);
            }
            whatsAppGroup.click();
            sleep(TIMEOUT_SHORT);
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                ShellCommon.amStartApp(device, activity, null);
                sleep(TIMEOUT_VERY_SHORT);
            }
            UiObject2 likeFacebook = waitUiObject2ByText("Like us on Facebook", TIMEOUT_SHORT);
            if (likeFacebook == null) {
                likeFacebook = waitUiObject2ByDesc("Like us on Facebook", TIMEOUT_VERY_SHORT);
            }
            likeFacebook.click();
            sleep(TIMEOUT_SHORT);
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                ShellCommon.amStartApp(device, activity, null);
                sleep(TIMEOUT_VERY_SHORT);
            }
            waitUiObject2ByDesc("toolbar home", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Help and feedback", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_MEDIUM);
            waitUiObject2ByRes("J_Feedback_Btn", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("J_Email", TIMEOUT_MEDIUM).setText("test@qq.com");
            waitUiObject2ByRes("J_Desc", TIMEOUT_MEDIUM).setText("test");
            swip(0.5, 0.7, 0.5, 0.3);
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("J_UploadTrigger", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0.1, 0.3, 0, 1, 0.02, 0.9).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByRes("J_Btn", TIMEOUT_MEDIUM).click();
            boolean isSuccess = isUiObject2ExistByText("Thank you for the feedback!", TIMEOUT_MEDIUM);
            if (isSuccess) {
                FileUtil.writeStrToFile("MeHelpFeedback:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("MeHelpFeedback:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/MeHelpFeedback_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeHelpFeedback:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeHelpFeedback_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-打赏
     */
    @Test
    public void testMeReward() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Support developer", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Coffee", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            boolean isCallSuccess = isUiObject2ExistByRes("com.android.vending:id/0_resource_name_obfuscated", TIMEOUT_LONG);
            if (isCallSuccess) {
                FileUtil.writeStrToFile("MeReward:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("MeReward:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/MeReward_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeReward:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeReward_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-搜索引擎
     */
    @Test
    public void testSettingSearchEngine() {
        try {
            // 进入设置
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            // 搜索引擎
            waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Bing", TIMEOUT_MEDIUM).click();
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            FileUtil.writeStrToFile("SettingSearchEngine:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingSearchEngine:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingSearchEngine_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-图片
     */
    @Test
    public void testSettingImage() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            // 图片
            waitUiObject2ByText("Image", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Always no image", TIMEOUT_MEDIUM).click();
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            FileUtil.writeStrToFile("SettingImage:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingImage:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingImage_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-字体大小
     */
    @Test
    public void testSettingFontSize() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            // 字体大小
            waitUiObject2ByText("Font size", TIMEOUT_MEDIUM).click();
            UiObject2 seekBar = waitUiObject2sByClazz("android.widget.SeekBar", TIMEOUT_MEDIUM).get(0);
            swip(seekBar, "left");
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            FileUtil.writeStrToFile("SettingFontSize:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingFontSize:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingFontSize_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-语言
     */
    @Test
    public void testSettingLanguage() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            // 语言
            waitUiObject2ByText("Language", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            FileUtil.writeStrToFile("SettingLanguage:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingLanguage:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingLanguage_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-主页(含最常访问)
     */
    @Test
    public void testSettingHomepage() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            // 主页
            waitUiObject2ByText("Homepage", TIMEOUT_MEDIUM).click();
            UiObject2 homeSwitch = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM).get(0);
            if (homeSwitch.isChecked()) {
                homeSwitch.click();
            }
            backToHome();
            UiObject2 mostVisited = waitUiObject2ByText("Most Visited", TIMEOUT_SHORT);
            double topY = 0.3;
            double bottomY = 0.5;
            if (mostVisited != null) {
                topY = (double) mostVisited.getVisibleBounds().top / height;
                bottomY = (double) mostVisited.getVisibleBounds().bottom / height;
            }
            longClick(getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.3, 0, 1, bottomY, 0.9).get(0));
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.3, 0, 0.3, 0.8, 1, topY - 0.02, bottomY + 0.02).get(0).click();
            waitUiObject2ByText("Clear", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("SettingHomepage:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingHomepage:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingHomepage_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-下载
     */
    @Test
    public void testSettingDownloads() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            // 下载
            waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Concurrent downloads", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("6", TIMEOUT_MEDIUM).click();
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            waitUiObject2ByText("Download folder", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 storage = getUiObject2("Internal Storage", "android.widget.TextView", 0.8, 0);
            if (storage != null) {
                storage.click();
            }
            waitUiObject2ByText("Choose it", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            FileUtil.writeStrToFile("SettingDownloads:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingDownloads:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingDownloads_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-通知栏
     */
    @Test
    public void testSettingNotification() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            // 通知栏
            waitUiObject2ByText("Notification", TIMEOUT_MEDIUM).click();
            List<UiObject2> notificationSwitchs = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM);
            for (UiObject2 notificationSwitch : notificationSwitchs) {
                if (notificationSwitch.isChecked()) {
                    notificationSwitch.click();
                }
            }
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            FileUtil.writeStrToFile("SettingNotification:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingNotification:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingNotification_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-清理数据
     */
    @Test
    public void testSettingClearData() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            // 清理数据
            waitUiObject2ByText("Clear data", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Account and password", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Video and Document browsing history", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            UiObject2 cleanPhoenix = null;
            List<UiObject2> tmpCleanPhoenixs = waitUiObject2sByTextContains("Clean up", TIMEOUT_MEDIUM);
            for (UiObject2 tmpCleanPhoenix : tmpCleanPhoenixs) {
                if (tmpCleanPhoenix.isClickable()) {
                    cleanPhoenix = tmpCleanPhoenix;
                    break;
                }
            }
            if (cleanPhoenix != null && cleanPhoenix.isEnabled()) {
                cleanPhoenix.click();
                for (int i = 0; i < 10; i++) {
                    sleep(TIMEOUT_VERY_SHORT);
                    List<UiObject2> cleanPhoenixBacks = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3);
                    if (cleanPhoenixBacks == null || cleanPhoenixBacks.size() == 0) {
                        back();
                    } else {
                        // uiautomator有时点击时会报异常
                        try {
                            cleanPhoenixBacks.get(0).click();
                        } catch (Exception e) {
                            back();
                        }
                    }

                    if (waitUiObject2ByText("Clear data", TIMEOUT_VERY_SHORT) != null) {
                        break;
                    }
                }
            } else {
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            }
            FileUtil.writeStrToFile("SettingClearData:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingClearData:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingClearData_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-检查更新
     */
    @Test
    public void testSettingCheckUpdates() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2);
            sleep(TIMEOUT_VERY_SHORT);
            // 检查更新
            waitUiObject2ByText("Check for updates", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            if (waitUiObject2ByText("Check for updates", TIMEOUT_SHORT) == null) {
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }
            FileUtil.writeStrToFile("SettingCheckUpdates:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingCheckUpdates:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingCheckUpdates_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-关于
     */
    @Test
    public void testSettingAbout() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2);
            sleep(TIMEOUT_VERY_SHORT);
            // 关于
            waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Product features", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            List<UiObject2> aboutSwitchs = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM);
            for (UiObject2 aboutSwitch : aboutSwitchs) {
                if (aboutSwitch.isChecked()) {
                    aboutSwitch.click();
                }
            }
            waitUiObject2ByTextContains("Terms of service", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByTextContains("Privacy policy", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            FileUtil.writeStrToFile("SettingAbout:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingAbout:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingAbout_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-Facebook
     */
    @Test
    public void testSettingFacebook() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2);
            sleep(TIMEOUT_VERY_SHORT);
            // Facebook
            waitUiObject2ByText("Like us on Facebook", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                ShellCommon.amStartApp(device, activity, null);
                sleep(TIMEOUT_VERY_SHORT);
            } else {
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }
            FileUtil.writeStrToFile("SettingFacebook:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingFacebook:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingFacebook_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-Feedback
     */
    @Test
    public void testSettingFeedback() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2);
            sleep(TIMEOUT_VERY_SHORT);
            // 用户反馈
            waitUiObject2ByText("Feedback", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("SettingFeedback:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingFeedback:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingFeedback_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-评分
     */
    @Test
    public void testSettingRateUs() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2);
            sleep(TIMEOUT_VERY_SHORT);
            // 评分
            waitUiObject2ByText("Rate us", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                ShellCommon.amStartApp(device, activity, null);
                sleep(TIMEOUT_VERY_SHORT);
            }
            FileUtil.writeStrToFile("SettingRateUs:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingRateUs:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingRateUs_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 设置-恢复默认设置
     */
    @Test
    public void testSettingReset() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            // 上滑
            swip(0.5, 0.8, 0.5, 0.2);
            sleep(TIMEOUT_VERY_SHORT);
            // 恢复默认
            waitUiObject2ByText("Reset to default settings", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Restore", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            backToHome();
            FileUtil.writeStrToFile("SettingReset:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("SettingReset:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/SettingReset_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 多窗口-普通窗口
     */
    @Test
    public void testTabsNormal() {
        try {
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 normal = waitUiObject2ByText("Normal", TIMEOUT_SHORT);
            UiObject2 incognito = waitUiObject2ByText("Incognito", TIMEOUT_SHORT);
            if (normal != null && incognito != null) {
                normal.click();
                sleep(TIMEOUT_VERY_SHORT);
            } else {
                if (normal == null) {
                    swip(0.3, 0.5, 0.7, 0.5);
                    sleep(TIMEOUT_VERY_SHORT);
                }
            }
            getUiObject2s("android.widget.ImageView", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.7, 1).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            if (normal != null && incognito != null) {
                getUiObject2s("android.widget.ImageView", true, 0.01, 0.3, 0.01, 0.5, 0.5, 1, 0.7, 1).get(0).click();
            } else {
                getUiObject2s("android.widget.FrameLayout", false, 0.01, 0.3, 0.01, 0.5, 0.5, 1, 0.7, 1).get(0).click();
            }
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            swip(0.5, 0.7, 0.5, 0.3);
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.5, 0.3, 0.5, 0.7);
            sleep(TIMEOUT_VERY_SHORT);
            List<UiObject2> normalImageViews = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0.7, 1, 0.2, 0.8);
            if (normalImageViews != null && normalImageViews.size() > 0) {
                normalImageViews.get(0).click();
            } else {
                swip(0.3, 0.5, 0.7, 0.5);
            }
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("TabsNormal:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("TabsNormal:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/TabsNormal_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 多窗口-隐私窗口
     */
    @Test
    public void testTabsIncognito() {
        try {
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 normal = waitUiObject2ByText("Normal", TIMEOUT_SHORT);
            UiObject2 incognito = waitUiObject2ByText("Incognito", TIMEOUT_SHORT);
            if (normal != null && incognito != null) {
                incognito.click();
                sleep(TIMEOUT_VERY_SHORT);
            } else {
                getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1, 0.03, 0.15).get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
                waitUiObject2ByText("New incognito tab", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);
                UiObject2 dialog = waitUiObject2ByText("OK", TIMEOUT_SHORT);
                if (dialog != null) {
                    dialog.click();
                    sleep(TIMEOUT_VERY_SHORT);
                }
                waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            getUiObject2s("android.widget.ImageView", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.7, 1).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            if (normal != null && incognito != null) {
                getUiObject2s("android.widget.ImageView", true, 0.01, 0.3, 0.01, 0.5, 0.5, 1, 0.7, 1).get(0).click();
            } else {
                getUiObject2s("android.widget.FrameLayout", false, 0.01, 0.3, 0.01, 0.5, 0.5, 1, 0.7, 1).get(0).click();
            }
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            swip(0.5, 0.7, 0.5, 0.3);
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.5, 0.3, 0.5, 0.7);
            sleep(TIMEOUT_VERY_SHORT);
            List<UiObject2> incognitoImageViews = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0.7, 1, 0.2, 0.8);
            if (incognitoImageViews != null && incognitoImageViews.size() > 0) {
                incognitoImageViews.get(0).click();
            } else {
                swip(0.7, 0.5, 0.3, 0.5);
            }
            sleep(TIMEOUT_VERY_SHORT);
            if (normal != null && incognito != null) {
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }
            FileUtil.writeStrToFile("TabsIncognito:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("TabsIncognito:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/TabsIncognito_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 多窗口-更多操作
     */
    @Test
    public void testTabsMore() {
        try {
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 normal = waitUiObject2ByText("Normal", TIMEOUT_SHORT);
            UiObject2 incognito = waitUiObject2ByText("Incognito", TIMEOUT_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1, 0.03, 0.15).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("New normal tab", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1, 0.03, 0.15).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("New incognito tab", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_MEDIUM);
            waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1, 0.03, 0.15).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Close all incognito tabs", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            if (normal == null || incognito == null) {
                waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1, 0.03, 0.15).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Close all tabs", TIMEOUT_MEDIUM).click();
            FileUtil.writeStrToFile("TabsMore:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("TabsMore:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/TabsMore_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-下载
     */
    @Test
    public void testFilesDownloads() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);

            // 下滑
            swip(0.5, 0.3, 0.5, 0.7);
            sleep(TIMEOUT_VERY_SHORT);

            // 下载
            waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1, 0.03, 0.15).get(0).click();
            waitUiObject2ByText("Add download link", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2sByClazz("android.widget.EditText", TIMEOUT_MEDIUM).get(0).setText("https://dldir1.qq.com/weixin/android/weixin7021android1800_arm64.apk");
            waitUiObject2ByText("Download", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Download", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1, 0.03, 0.15).get(0).click();
            waitUiObject2ByText("Start all", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1, 0.03, 0.15).get(0).click();
            waitUiObject2ByText("Pause all", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1, 0.03, 0.15).get(0).click();
            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesDownloads:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesDownloads:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesDownloads_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Status saver
     */
    @Test
    public void testFilesStatusSaver() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 statusSaver = waitUiObject2ByText("Status saver", TIMEOUT_MEDIUM);
            if (statusSaver == null) {
                statusSaver = waitUiObject2ByText("Status & Sticker", TIMEOUT_VERY_SHORT);
            }
            statusSaver.click();
            sleep(TIMEOUT_VERY_SHORT);
            for (int i = 0; i < 3; i++) {
                swip(0.7, 0.5, 0.3, 0.5);
                sleep(TIMEOUT_VERY_SHORT);
            }
            UiObject2 whatsAppTips = waitUiObject2ByText("Manage WhatsApp files here", TIMEOUT_SHORT);
            if (whatsAppTips != null) {
                whatsAppTips.click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesStatusSaver:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesStatusSaver:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesStatusSaver_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-WhatsApp
     */
    @Test
    public void testFilesWhatsApp() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("WhatsApp", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            for (int i = 0; i < 3; i++) {
                swip(0.7, 0.5, 0.3, 0.5);
                sleep(TIMEOUT_VERY_SHORT);
            }
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesWhatsApp:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesWhatsApp:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesWhatsApp_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Telegram
     */
    @Test
    public void testFilesTelegram() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 telegram = waitUiObject2ByText("Telegram", TIMEOUT_MEDIUM);
            if (telegram != null) {
                telegram.click();
                sleep(TIMEOUT_VERY_SHORT);
                for (int i = 0; i < 3; i++) {
                    swip(0.7, 0.5, 0.3, 0.5);
                    sleep(TIMEOUT_VERY_SHORT);
                }
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
                FileUtil.writeStrToFile("FilesTelegram:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FilesTelegram:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/FilesTelegram_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesTelegram:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesTelegram_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Videos
     */
    @Test
    public void testFilesVideos() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Videos", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.7, 0.5, 0.3, 0.5);
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.3, 0.5, 0.7, 0.5);
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.5, 0, 1, 0.1, 0.9).get(0).click();
            sleep(TIMEOUT_SHORT);
            horizontalScreen();
            back();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesVideos:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesVideos:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesVideos_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Music
     */
    @Test
    public void testFilesMusic() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Music", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.7, 0.5, 0.3, 0.5);
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.3, 0.5, 0.7, 0.5);
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.3, 0, 1, 0.1, 0.9).get(0).click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(1).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesMusic:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesMusic:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesMusic_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Images
     */
    @Test
    public void testFilesImages() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Images", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.7, 0.5, 0.3, 0.5);
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.3, 0.5, 0.7, 0.5);
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0.1, 0.3, 0, 1, 0.1, 0.9).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            for (int i = 0; i < 10; i++) {
                swip(0.7, 0.5, 0.3, 0.5);
                sleep(TIMEOUT_VERY_SHORT);
            }
            back();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesImages:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesImages:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesImages_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Documents
     */
    @Test
    public void testFilesDocuments() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Documents", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            for (int i = 0; i < 6; i++) {
                swip(0.7, 0.5, 0.3, 0.5);
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 firstDoc = getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.3, 0, 1, 0.1, 0.9).get(0);
                if (firstDoc != null) {
                    firstDoc.click();
                    sleep(TIMEOUT_SHORT);
                    getUiObject2s("android.widget.LinearLayout", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                    sleep(TIMEOUT_VERY_SHORT);
                }
            }
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesDocuments:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesDocuments:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesDocuments_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Storage
     */
    @Test
    public void testFilesStorage() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 storage = waitUiObject2ByText("Storage", TIMEOUT_MEDIUM);
            if (storage == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
                storage = waitUiObject2ByText("Storage", TIMEOUT_SHORT);
            }
            storage.click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesStorage:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesStorage:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesStorage_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Archives
     */
    @Test
    public void testFilesArchives() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 archives = waitUiObject2ByText("Archives", TIMEOUT_MEDIUM);
            if (archives == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
                archives = waitUiObject2ByText("Archives", TIMEOUT_SHORT);
            }
            archives.click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.3, 0, 1, 0.1, 0.9).get(0).click();
            sleep(TIMEOUT_MEDIUM);
            getUiObject2s("android.widget.LinearLayout", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesArchives:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesArchives:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesArchives_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Instagram
     */
    @Test
    public void testFilesInstagram() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 instagram = waitUiObject2ByText("Instagram", TIMEOUT_MEDIUM);
            if (instagram == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
                instagram = waitUiObject2ByText("Instagram", TIMEOUT_SHORT);
            }
            instagram.click();
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.7, 0.5, 0.3, 0.5);
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesInstagram:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesInstagram:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesInstagram_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Offline pages
     */
    @Test
    public void testFilesOfflinePages() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 offlinePages = waitUiObject2ByText("Offline pages", TIMEOUT_MEDIUM);
            if (offlinePages == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
                offlinePages = waitUiObject2ByText("Offline pages", TIMEOUT_SHORT);
            }
            offlinePages.click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesOfflinePages:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesOfflinePages:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesOfflinePages_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Apps
     */
    @Test
    public void testFilesApps() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 apps = waitUiObject2ByText("Apps", TIMEOUT_MEDIUM);
            if (apps == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
                apps = waitUiObject2ByText("Apps", TIMEOUT_SHORT);
            }
            apps.click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesApps:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesApps:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesApps_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Others
     */
    @Test
    public void testFilesOthers() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 others = waitUiObject2ByText("Others", TIMEOUT_MEDIUM);
            if (others == null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
                others = waitUiObject2ByText("Others", TIMEOUT_SHORT);
            }
            others.click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesOthers:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesOthers:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesOthers_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Junk files
     */
    @Test
    public void testFilesJunkfiles() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Junk files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 cleanJunkFiles = waitUiObject2ByTextContains("Clean up", TIMEOUT_MEDIUM);
            if (cleanJunkFiles == null) {
                cleanJunkFiles = waitUiObject2ByTextContains("Safe clean", TIMEOUT_MEDIUM);
            }
            if (cleanJunkFiles != null && cleanJunkFiles.isEnabled()) {
                cleanJunkFiles.click();
                sleep(TIMEOUT_LONG);
                // 关闭广告
                closeAdDialog();
                for (int i = 0; i < 10; i++) {
                    // 返回
                    List<UiObject2> junkFilesBacks = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3);
                    if (junkFilesBacks == null || junkFilesBacks.size() == 0) {
                        // 处理退出清理确认弹窗
                        UiObject2 exit = waitUiObject2ByText("Exit", TIMEOUT_SHORT);
                        if (exit != null) {
                            exit.click();
                            sleep(TIMEOUT_VERY_SHORT);
                        } else {
                            back();
                        }
                    } else {
                        // uiautomator有时点击时会报异常
                        try {
                            junkFilesBacks.get(0).click();
                        } catch (Exception e) {
                            back();
                        }
                    }
                    sleep(TIMEOUT_VERY_SHORT);

                    if (waitUiObject2ByText("Me", TIMEOUT_VERY_SHORT) != null) {
                        break;
                    }
                }
            } else {
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
                // 处理退出确认弹窗
                UiObject2 exit = waitUiObject2ByText("Exit", TIMEOUT_SHORT);
                if (exit != null) {
                    exit.click();
                    sleep(TIMEOUT_VERY_SHORT);
                }
            }
            FileUtil.writeStrToFile("FilesJunkfiles:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesJunkfiles:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesJunkfiles_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Phone boost
     */
    @Test
    public void testFilesPhoneBoost() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Phone boost", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_LONG);
            // 关闭广告
            closeAdDialog();
            // 处理添加至主页弹窗
            if (waitUiObject2ByText("Add", TIMEOUT_MEDIUM) != null) {
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }
            for (int i = 0; i < 10; i++) {
                List<UiObject2> boostBacks = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3);
                if (boostBacks == null || boostBacks.size() == 0) {
                    back();
                } else {
                    // uiautomator有时点击时会报异常
                    try {
                        boostBacks.get(0).click();
                    } catch (Exception e) {
                        back();
                    }
                }
                sleep(TIMEOUT_VERY_SHORT);
                if (waitUiObject2ByTextContains("Me", TIMEOUT_VERY_SHORT) != null) {
                    break;
                }
            }
            FileUtil.writeStrToFile("FilesPhoneBoost:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesPhoneBoost:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesPhoneBoost_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Clean Up Phoenix
     */
    @Test
    public void testFilesCleanPhoenix() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);

            UiObject2 cleanPhoenix = waitUiObject2ByText("Clean Up Phoenix", TIMEOUT_MEDIUM);
            if (cleanPhoenix == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                cleanPhoenix = waitUiObject2ByText("Clean Up Phoenix", TIMEOUT_MEDIUM);
            }
            cleanPhoenix.click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Account and password", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            UiObject2 cleanUp = null;
            List<UiObject2> tmpCleanPhoenixs = waitUiObject2sByTextContains("Clean up", TIMEOUT_MEDIUM);
            for (UiObject2 tmpCleanPhoenix : tmpCleanPhoenixs) {
                if (tmpCleanPhoenix.isClickable()) {
                    cleanUp = tmpCleanPhoenix;
                    break;
                }
            }
            if (cleanUp != null && cleanUp.isEnabled()) {
                cleanUp.click();
                sleep(TIMEOUT_LONG);
                // 关闭广告
                closeAdDialog();
                for (int i = 0; i < 10; i++) {
                    List<UiObject2> whatsAppBacks = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3);
                    if (whatsAppBacks == null || whatsAppBacks.size() == 0) {
                        back();
                    } else {
                        // uiautomator有时点击时会报异常
                        try {
                            whatsAppBacks.get(0).click();
                        } catch (Exception e) {
                            back();
                        }
                    }
                    sleep(TIMEOUT_VERY_SHORT);
                    if (waitUiObject2ByText("Me", TIMEOUT_VERY_SHORT) != null) {
                        break;
                    }
                }
            } else {
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            FileUtil.writeStrToFile("FilesCleanPhoenix:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesCleanPhoenix:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesCleanPhoenix_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Large File Cleanup
     */
    @Test
    public void testFilesCleanLargeFile() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 cleanLargeFile = waitUiObject2ByText("Large File Cleanup", TIMEOUT_MEDIUM);
            if (cleanLargeFile == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                cleanLargeFile = waitUiObject2ByText("Large File Cleanup", TIMEOUT_MEDIUM);
            }
            cleanLargeFile.click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 cleanUp = waitUiObject2ByTextContains("Clean up", TIMEOUT_MEDIUM);
            if (cleanUp != null && !cleanUp.isEnabled()) {
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0.5, 1, 0.02, 0.5).get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
                cleanUp = waitUiObject2ByTextContains("Clean up", TIMEOUT_MEDIUM);
            }
            cleanUp.click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Clean", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_LONG);
            // 关闭广告
            closeAdDialog();
            for (int i = 0; i < 10; i++) {
                List<UiObject2> videosBacks = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3);
                if (videosBacks == null || videosBacks.size() == 0) {
                    back();
                } else {
                    // uiautomator有时点击时会报异常
                    try {
                        videosBacks.get(0).click();
                    } catch (Exception e) {
                        back();
                    }
                }

                sleep(TIMEOUT_VERY_SHORT);
                if (waitUiObject2ByText("Me", TIMEOUT_VERY_SHORT) != null) {
                    break;
                }
            }
            FileUtil.writeStrToFile("FilesCleanLargeFile:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesCleanLargeFile:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesCleanLargeFile_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Clean Up Videos
     */
    @Test
    public void testFilesCleanVideos() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 cleanVideos = waitUiObject2ByText("Clean Up Videos", TIMEOUT_MEDIUM);
            if (cleanVideos == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                cleanVideos = waitUiObject2ByText("Clean Up WhatsApp", TIMEOUT_MEDIUM);
            }
            cleanVideos.click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Other videos", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 firstVideo = getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0.1, 0.5, 0, 1, 0.1, 0.9).get(0);
            if (firstVideo != null) {
                firstVideo.click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 checkBox = waitUiObject2sByClazz("android.widget.CheckBox", TIMEOUT_MEDIUM).get(0);
                if (!checkBox.isChecked()) {
                    checkBox.click();
                    sleep(TIMEOUT_VERY_SHORT);
                }
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            } else {
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            }
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 cleanUp = waitUiObject2ByTextContains("Clean up", TIMEOUT_MEDIUM);
            if (cleanUp != null && cleanUp.isEnabled()) {
                cleanUp.click();
                sleep(TIMEOUT_VERY_SHORT);
                waitUiObject2ByText("Delete", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_LONG);
                // 关闭广告
                closeAdDialog();
                for (int i = 0; i < 10; i++) {
                    List<UiObject2> videosBacks = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3);
                    if (videosBacks == null || videosBacks.size() == 0) {
                        back();
                    } else {
                        // uiautomator有时点击时会报异常
                        try {
                            videosBacks.get(0).click();
                        } catch (Exception e) {
                            back();
                        }
                    }

                    sleep(TIMEOUT_VERY_SHORT);
                    if (waitUiObject2ByText("Me", TIMEOUT_VERY_SHORT) != null) {
                        break;
                    }
                }
            } else {
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            FileUtil.writeStrToFile("FilesCleanVideos:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesCleanVideos:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesCleanVideos_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Clean Up WhatsApp
     */
    @Test
    public void testFilesCleanWhatsApp() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 cleanWhatsApp = waitUiObject2ByText("Clean Up WhatsApp", TIMEOUT_MEDIUM);
            if (cleanWhatsApp == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                cleanWhatsApp = waitUiObject2ByText("Clean Up WhatsApp", TIMEOUT_MEDIUM);
            }
            if (cleanWhatsApp != null) {
                cleanWhatsApp.click();
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 cleanUp = waitUiObject2ByTextContains("Clean up", TIMEOUT_MEDIUM);
                if (cleanUp != null && cleanUp.isEnabled()) {
                    cleanUp.click();
                    sleep(TIMEOUT_VERY_SHORT);
                    for (int i = 0; i < 10; i++) {
                        List<UiObject2> whatsAppBacks = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3);
                        if (whatsAppBacks == null || whatsAppBacks.size() == 0) {
                            back();
                        } else {
                            // uiautomator有时点击时会报异常
                            try {
                                whatsAppBacks.get(0).click();
                            } catch (Exception e) {
                                back();
                            }
                        }

                        sleep(TIMEOUT_VERY_SHORT);
                        if (waitUiObject2ByText("Me", TIMEOUT_VERY_SHORT) != null) {
                            break;
                        }
                    }
                } else {
                    getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                    sleep(TIMEOUT_VERY_SHORT);
                }
                FileUtil.writeStrToFile("FilesCleanWhatsApp:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FilesCleanWhatsApp:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/FilesCleanWhatsApp_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesCleanWhatsApp:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesCleanWhatsApp_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Clean Telegram
     */
    @Test
    public void testFilesCleanTelegram() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 cleanTelegram = waitUiObject2ByText("Clean Telegram", TIMEOUT_MEDIUM);
            if (cleanTelegram == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                cleanTelegram = waitUiObject2ByText("Clean Telegram", TIMEOUT_MEDIUM);
            }
            if (cleanTelegram != null) {
                cleanTelegram.click();
                sleep(TIMEOUT_VERY_SHORT);
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                FileUtil.writeStrToFile("FilesCleanTelegram:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FilesCleanTelegram:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/filesCleanTelegram_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesCleanTelegram:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesCleanTelegram_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Recent documents
     */
    @Test
    public void testFilesRecentDocuments() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 recentDocuments = waitUiObject2ByText("Recent documents", TIMEOUT_MEDIUM);
            if (recentDocuments == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                recentDocuments = waitUiObject2ByText("Recent documents", TIMEOUT_MEDIUM);
            }
            // 最近打开文档
            if (recentDocuments != null) {
                waitUiObject2ByText("More", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
                FileUtil.writeStrToFile("FilesRecentDocuments:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FilesRecentDocuments:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/FilesRecentDocuments_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesRecentDocuments:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesRecentDocuments_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Wallpaper
     */
    @Test
    public void testFilesWallpaper() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 wallpaper = waitUiObject2ByText("Wallpaper", TIMEOUT_MEDIUM);
            if (wallpaper == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                wallpaper = waitUiObject2ByText("Wallpaper", TIMEOUT_MEDIUM);
            }
            wallpaper.click();
            waitUiObject2ByText("Select wallpaper", TIMEOUT_MEDIUM).click();
            for (int i = 0; i < 20; i++) {
                sleep(TIMEOUT_SHORT);
                List<UiObject2> firstImgs = getUiObject2s("android.view.View", true, 0.2, 0.4, 0.2, 0.5, 0, 1, 0.05, 0.9);
                if (firstImgs != null && firstImgs.size() > 6) {
                    firstImgs.get(0).click();
                    sleep(TIMEOUT_MEDIUM);
                    break;
                }
            }
            waitUiObject2ByText("Set as", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Both", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            waitUiObject2ByText("Download", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesWallpaper:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesWallpaper:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesWallpaper_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Ringtones
     */
    @Test
    public void testFilesRingtones() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 ringtones = waitUiObject2ByText("Ringtones", TIMEOUT_MEDIUM);
            if (ringtones == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                ringtones = waitUiObject2ByText("Ringtones", TIMEOUT_MEDIUM);
            }
            ringtones.click();
            waitUiObject2ByText("Select ringtone", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.3, 0, 1, 0.1, 0.9).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            List<UiObject2> uiObject2s = waitUiObject2sByText("Set", TIMEOUT_MEDIUM);
            if (uiObject2s != null && uiObject2s.size() > 0) {
                for (int i = 0; i < uiObject2s.size(); i++) {
                    UiObject2 set = uiObject2s.get(i);
                    if (i == 0) {
                        set.click();
                        UiObject2 goSetting = waitUiObject2ByText("Go to settings", TIMEOUT_SHORT);
                        if (goSetting != null) {
                            goSetting.click();
                            UiObject2 settingSwitch = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM).get(0);
                            if (!settingSwitch.isChecked()) {
                                settingSwitch.click();
                            }
                            back();
                            sleep(TIMEOUT_VERY_SHORT);
                        }
                    } else {
                        set.click();
                    }
                }
            }
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("FilesRingtones:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesRingtones:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesRingtones_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Compress files
     */
    @Test
    public void testFilesCompressFiles() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 compressFiles = waitUiObject2ByText("Compress files", TIMEOUT_MEDIUM);
            if (compressFiles == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                compressFiles = waitUiObject2ByText("Compress files", TIMEOUT_MEDIUM);
            }
            compressFiles.click();
            waitUiObject2ByText("Select files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Images", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 checkBox = waitUiObject2sByClazz("android.widget.CheckBox", TIMEOUT_MEDIUM).get(0);
            if (!checkBox.isChecked()) {
                checkBox.click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Compress 1 file", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Done", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_MEDIUM);
            UiObject2 compressed = waitUiObject2ByTextContains("have been compressed", TIMEOUT_LONG);
            if (compressed != null) {
                FileUtil.writeStrToFile("FilesCompressFiles:PASS" + "\n", mainPathFile);
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
            } else {
                FileUtil.writeStrToFile("FilesCompressFiles:FAILED" + "\n", mainPathFile);
                screenshot(resultFolder + "/FilesCompressFiles_" + CommonUtil.getCurTimeForFile() + ".jpg");
            }
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesCompressFiles:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesCompressFiles_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 文件-Unzip files
     */
    @Test
    public void testFilesUnzipFiles() {
        try {
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 unzipFiles = waitUiObject2ByText("Unzip files", TIMEOUT_MEDIUM);
            if (unzipFiles == null) {
                // 上滑
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_VERY_SHORT);
                unzipFiles = waitUiObject2ByText("Unzip files", TIMEOUT_MEDIUM);
            }
            unzipFiles.click();
            waitUiObject2ByText("Select files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.3, 0, 1, 0.1, 0.9).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 unzip = waitUiObject2ByText("Unzip", TIMEOUT_MEDIUM);
            if (unzip != null) {
                unzip.click();
                sleep(TIMEOUT_SHORT);
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            UiObject2 unzipView = waitUiObject2ByText("Unzip and view", TIMEOUT_MEDIUM);
            if (unzipView != null && unzipView.isEnabled()) {
                unzipView.click();
                sleep(TIMEOUT_SHORT);
                getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            getUiObject2s("android.widget.LinearLayout", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.5, 0.3, 0.5, 0.7);
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Unzipped files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            longClick(getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.3, 0, 1, 0.1, 0.9).get(0));
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 selectAll = waitUiObject2ByText("Select all", TIMEOUT_SHORT);
            if (selectAll != null) {
                selectAll.click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Delete", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Home", TIMEOUT_MEDIUM).click();
            FileUtil.writeStrToFile("FilesUnzipFiles:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FilesUnzipFiles:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FilesUnzipFiles_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 个人中心-退出登录
     */
    @Test
    public void testMeLogout() {
        try {
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);

            // 下滑
            swip(0.5, 0.3, 0.5, 0.7);
            sleep(TIMEOUT_VERY_SHORT);

            // 退出登录
            waitUiObject2ByText("Signed in with Google", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.02, 0.2).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Sign out", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Sign out", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(0).click();
            sleep(TIMEOUT_VERY_SHORT);
            FileUtil.writeStrToFile("MeLogout:PASS" + "\n", mainPathFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeLogout:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeLogout_" + CommonUtil.getCurTimeForFile() + ".jpg");
            backToApp();
            backToHome();
        }
    }

    /**
     * 退出-网页工具栏
     */
    @Test
    public void testWebpageMenuExit() {
        try {
            // 启动浏览器
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                startApp(pkgName);
                sleep(TIMEOUT_MEDIUM);
            }

            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com");
            sleep(TIMEOUT_SHORT);
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Exit", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            ShellCommand.execCmdByUiDevice(device, "am force-stop " + pkgName);
            FileUtil.writeStrToFile("WebpageMenuExit:PASS" + "\n", mainPathFile);
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("WebpageMenuExit:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/WebpageMenuExit_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 退出-Me
     */
    @Test
    public void testMeExit() {
        try {
            // 启动浏览器
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                startApp(pkgName);
                sleep(TIMEOUT_MEDIUM);
            }

            // 个人中心退出
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            waitUiObject2ByText("Exit", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            ShellCommand.execCmdByUiDevice(device, "am force-stop " + pkgName);
            FileUtil.writeStrToFile("MeExit:PASS" + "\n", mainPathFile);
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("MeExit:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/MeExit_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 退出-Back
     */
    @Test
    public void testBackExit() {
        try {
            // 启动浏览器
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                startApp(pkgName);
                sleep(TIMEOUT_MEDIUM);
            }

            // 硬件back弹窗退出
            backToHome();
            backExitBrowser();
            FileUtil.writeStrToFile("BackExit:PASS" + "\n", mainPathFile);
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("BackExit:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/BackExit_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开网页
     */
    @Test
    public void testFirstThirdCallWebpage() {
        try {
            boolean isSuccess = testThirdCall("webpage", true);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/FirstThirdCallWebpage_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("FirstThirdCallWebpage:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FirstThirdCallWebpage:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/FirstThirdCallWebpage_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FirstThirdCallWebpage:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FirstThirdCallWebpage_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开视频
     */
    @Test
    public void testFirstThirdCallVideo() {
        try {
            boolean isSuccess = testThirdCall("video", true);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/FirstThirdCallVideo_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("FirstThirdCallVideo:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FirstThirdCallVideo:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/FirstThirdCallVideo_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FirstThirdCallVideo:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FirstThirdCallVideo_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开音乐
     */
    @Test
    public void testFirstThirdCallMusic() {
        try {
            boolean isSuccess = testThirdCall("music", true);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/FirstThirdCallMusic_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("FirstThirdCallMusic:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FirstThirdCallMusic:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/FirstThirdCallMusic_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FirstThirdCallMusic:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FirstThirdCallMusic_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开doc
     */
    @Test
    public void testFirstThirdCallDoc() {
        try {
            boolean isSuccess = testThirdCall("doc", true);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/FirstThirdCallDoc_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("FirstThirdCallDoc:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FirstThirdCallDoc:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/FirstThirdCallDoc_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FirstThirdCallDoc:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FirstThirdCallDoc_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开ppt
     */
    @Test
    public void testFirstThirdCallPpt() {
        try {
            boolean isSuccess = testThirdCall("ppt", true);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/FirstThirdCallPpt_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("FirstThirdCallPpt:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FirstThirdCallPpt:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/FirstThirdCallPpt_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FirstThirdCallPpt:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FirstThirdCallPpt_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开xls
     */
    @Test
    public void testFirstThirdCallXls() {
        try {
            boolean isSuccess = testThirdCall("xls", true);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/FirstThirdCallXls_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("FirstThirdCallXls:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FirstThirdCallXls:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/FirstThirdCallXls_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FirstThirdCallXls:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FirstThirdCallXls_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开pdf
     */
    @Test
    public void testFirstThirdCallPdf() {
        try {
            boolean isSuccess = testThirdCall("pdf", true);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/FirstThirdCallPdf_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("FirstThirdCallPdf:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FirstThirdCallPdf:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/FirstThirdCallPdf_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FirstThirdCallPdf:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FirstThirdCallPdf_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开epub
     */
    @Test
    public void testFirstThirdCallEpub() {
        try {
            boolean isSuccess = testThirdCall("epub", true);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/FirstThirdCallEpub_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("FirstThirdCallEpub:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FirstThirdCallEpub:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/FirstThirdCallEpub_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FirstThirdCallEpub:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FirstThirdCallEpub_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开img
     */
    @Test
    public void testFirstThirdCallImg() {
        try {
            boolean isSuccess = testThirdCall("img", true);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/FirstThirdCallImg_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("FirstThirdCallImg:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FirstThirdCallImg:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/FirstThirdCallImg_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FirstThirdCallImg:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FirstThirdCallImg_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开zip
     */
    @Test
    public void testFirstThirdCallZip() {
        try {
            boolean isSuccess = testThirdCall("zip", true);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/FirstThirdCallZip_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("FirstThirdCallZip:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FirstThirdCallZip:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/FirstThirdCallZip_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FirstThirdCallZip:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FirstThirdCallZip_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开txt
     */
    @Test
    public void testFirstThirdCallTxt() {
        try {
            boolean isSuccess = testThirdCall("txt", true);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/FirstThirdCallTxt_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("FirstThirdCallTxt:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("FirstThirdCallTxt:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/FirstThirdCallTxt_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("FirstThirdCallTxt:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/FirstThirdCallTxt_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方打开网页
     */
    @Test
    public void testThirdCallWebpage() {
        try {
            boolean isSuccess = testThirdCall("webpage", false);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/ThirdCallWebpage_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("ThirdCallWebpage:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("ThirdCallWebpage:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/ThirdCallWebpage_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("ThirdCallWebpage:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/ThirdCallWebpage_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开视频
     */
    @Test
    public void testThirdCallVideo() {
        try {
            boolean isSuccess = testThirdCall("video", false);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/ThirdCallVideo_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("ThirdCallVideo:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("ThirdCallVideo:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/ThirdCallVideo_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("ThirdCallVideo:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/ThirdCallVideo_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开音乐
     */
    @Test
    public void testThirdCallMusic() {
        try {
            boolean isSuccess = testThirdCall("music", false);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/ThirdCallMusic_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("ThirdCallMusic:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("ThirdCallMusic:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/ThirdCallMusic_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("ThirdCallMusic:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/ThirdCallMusic_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开doc
     */
    @Test
    public void testThirdCallDoc() {
        try {
            boolean isSuccess = testThirdCall("doc", false);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/ThirdCallMusic_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("ThirdCallDoc:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("ThirdCallDoc:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/ThirdCallDoc_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("ThirdCallDoc:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/ThirdCallDoc_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开ppt
     */
    @Test
    public void testThirdCallPpt() {
        try {
            boolean isSuccess = testThirdCall("ppt", false);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/ThirdCallPpt_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("ThirdCallPpt:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("ThirdCallPpt:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/ThirdCallPpt_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("ThirdCallPpt:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/ThirdCallPpt_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开xls
     */
    @Test
    public void testThirdCallXls() {
        try {
            boolean isSuccess = testThirdCall("xls", false);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/ThirdCallXls_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("ThirdCallXls:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("ThirdCallXls:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/ThirdCallXls_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("ThirdCallXls:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/ThirdCallXls_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开pdf
     */
    @Test
    public void testThirdCallPdf() {
        try {
            boolean isSuccess = testThirdCall("pdf", false);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/ThirdCallXls_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("ThirdCallPdf:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("ThirdCallPdf:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/ThirdCallPdf_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("ThirdCallPdf:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/ThirdCallPdf_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开epub
     */
    @Test
    public void testThirdCallEpub() {
        try {
            boolean isSuccess = testThirdCall("epub", false);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/ThirdCallEpub_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("ThirdCallEpub:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("ThirdCallEpub:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/ThirdCallEpub_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("ThirdCallEpub:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/ThirdCallEpub_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开img
     */
    @Test
    public void testThirdCallImg() {
        try {
            boolean isSuccess = testThirdCall("img", false);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/ThirdCallImg_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("ThirdCallImg:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("ThirdCallImg:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/ThirdCallImg_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("ThirdCallImg:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/ThirdCallImg_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开zip
     */
    @Test
    public void testThirdCallZip() {
        try {
            boolean isSuccess = testThirdCall("zip", false);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/ThirdCallZip_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("ThirdCallZip:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("ThirdCallZip:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/ThirdCallZip_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("ThirdCallZip:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/ThirdCallZip_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 第三方首次打开txt
     */
    @Test
    public void testThirdCallTxt() {
        try {
            boolean isSuccess = testThirdCall("txt", false);

            // 失败截图
            boolean isScreenshot = false;
            if (!isSuccess) {
                screenshot(resultFolder + "/ThirdCallTxt_" + CommonUtil.getCurTimeForFile() + ".jpg");
                isScreenshot = true;
            }

            // 回到首页->退出浏览器
            backToHome();
            if (isSuccess) {
                isSuccess = backExitBrowser();
            } else {
                backExitBrowser();
            }

            // 处理日志信息
            if (isSuccess) {
                FileUtil.writeStrToFile("ThirdCallTxt:PASS" + "\n", mainPathFile);
            } else {
                FileUtil.writeStrToFile("ThirdCallTxt:FAILED" + "\n", mainPathFile);
                if (!isScreenshot) {
                    screenshot(resultFolder + "/ThirdCallTxt_" + CommonUtil.getCurTimeForFile() + ".jpg");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile("ThirdCallTxt:Exception" + "\n", mainPathFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), mainPathFile);
            screenshot(resultFolder + "/ThirdCallTxt_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    private boolean testThirdCall(String file, boolean isFirst) {
        // 强制结束，避免anr弹窗遮挡
        ShellCommand.execCmdByUiDevice(device, "am force-stop " + pkgName);

        if (isFirst) {
            // 清理数据
            ShellCommand.execCmdByUiDevice(device, "pm clear " + pkgName);
        }

        // 执行第三方调用
        if (file.equals("webpage")) {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -d https://qq.com");
        } else if (file.equals("video")) {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -t video/* -d file:///sdcard/testfile/video_mp4_youku.mp4");
        } else if (file.equals("music")) {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -t audio/flac -d file:///sdcard/testfile/music_flac_不为谁而作的歌.flac");
        } else if (file.equals("doc")) {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/msword -d file:///sdcard/testfile/document_docx_1MB.docx");
        } else if (file.equals("ppt")) {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/vnd.ms-powerpoint -d file:///sdcard/testfile/document_pptx.pptx");
        } else if (file.equals("xls")) {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/vnd.ms-excel -d file:///sdcard/testfile/document_xlsx_5000.xlsx");
        } else if (file.equals("pdf")) {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/pdf -d file:///sdcard/testfile/document_pdf_keph101.pdf");
        } else if (file.equals("epub")) {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/epub+zip -d file:///sdcard/testfile/document_epub_doupocangqiong.epub");
        } else if (file.equals("img")) {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -t image/png -d file:///sdcard/testfile/img_png_640x960_529k.png");
        } else if (file.equals("zip")) {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -t application/zip -d file:///sdcard/testfile/archive_zip_10MB.zip");
        } else if (file.equals("txt")) {
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -t text/richtext -d file:///sdcard/testfile/document_txt_1MB.txt");
        }
        sleep(TIMEOUT_LONG);

        // 先跳过闪屏
        if (isNeedSkipSplash()) {
            skipSplash();
        }
        sleep(TIMEOUT_SHORT);

        // 再处理各业务
        boolean isSuccess = false;
        if (file.equals("webpage")) {
            if (waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM) != null) {
                isSuccess = true;
            }
        } else {
            if (isFirst) {
                // 处理弹窗
                UiObject2 continueBtn = waitUiObject2ByText("Continue", TIMEOUT_MEDIUM);
                if (continueBtn == null) {
                    continueBtn = waitUiObject2ByText("متابعة", TIMEOUT_VERY_SHORT);
                }
                continueBtn.click();
                UiObject2 allow = waitUiObject2ByText("Allow", TIMEOUT_MEDIUM);
                if (allow == null) {
                    allow = waitUiObject2ByText("ALLOW", TIMEOUT_MEDIUM);
                }
                allow.click();
                sleep(TIMEOUT_SHORT);
            }

            // 再处理业务
            if (file.equals("video")) {
                sleep(TIMEOUT_LONG);
                horizontalScreen();
                isSuccess = isThirdCallSuccess("video");
            } else if (file.equals("music")) {
                sleep(TIMEOUT_LONG);
                isSuccess = isThirdCallSuccess("music");
            } else if (file.equals("doc")) {
                // Edit Fit screen  Search  Save as
                UiObject2 fitScreen = waitUiObject2ByText("Fit screen", TIMEOUT_VERY_LONG);
                if (fitScreen == null) {
                    fitScreen = waitUiObject2ByText("ملائمة الشاشة", TIMEOUT_VERY_SHORT);
                }
                if (fitScreen != null) {
                    isSuccess = true;
                }
            } else if (file.equals("ppt")) {
                // Edit Fullscreen  Save as
                UiObject2 fullScreen = waitUiObject2ByText("Fullscreen", TIMEOUT_VERY_LONG);
                if (fullScreen == null) {
                    fullScreen = waitUiObject2ByText("ملء الشاشة", TIMEOUT_VERY_SHORT);
                }
                if (fullScreen != null) {
                    isSuccess = true;
                }
            } else if (file.equals("xls")) {
                // Edit Search  Save as
                UiObject2 search = waitUiObject2ByText("Search", TIMEOUT_VERY_LONG);
                if (search == null) {
                    search = waitUiObject2ByText("بحث", TIMEOUT_VERY_SHORT);
                }
                if (search != null) {
                    isSuccess = true;
                }
            } else if (file.equals("pdf")) {
                // Search Fullscreen
                UiObject2 fullScreen = waitUiObject2ByText("Fullscreen", TIMEOUT_VERY_LONG);
                if (fullScreen == null) {
                    fullScreen = waitUiObject2ByText("ملء الشاشة", TIMEOUT_VERY_SHORT);
                }
                if (fullScreen != null) {
                    isSuccess = true;
                }
            } else if (file.equals("epub")) {
                sleep(TIMEOUT_LONG);
                isSuccess = isThirdCallSuccess("epub");
            } else if (file.equals("img")) {
                // Share Info Delete
                UiObject2 info = waitUiObject2ByText("Info", TIMEOUT_VERY_LONG);
                if (info == null) {
                    info = waitUiObject2ByText("معلومات", TIMEOUT_VERY_SHORT);
                }
                if (info != null) {
                    isSuccess = true;
                }
            } else if (file.equals("zip")) {
                UiObject2 unzipView = waitUiObject2ByText("Unzip and view", TIMEOUT_VERY_LONG);
                if (unzipView == null) {
                    unzipView = waitUiObject2ByText("قم بفك الضغط ثم العرض", TIMEOUT_VERY_SHORT);
                }
                if (unzipView != null) {
                    isSuccess = true;
                }
            } else if (file.equals("txt")) {
                sleep(TIMEOUT_LONG);
                isSuccess = isThirdCallSuccess("txt");
            }
        }
        return isSuccess;
    }

    @After
    public void afterTest() {
        super.afterTest();
    }

}
