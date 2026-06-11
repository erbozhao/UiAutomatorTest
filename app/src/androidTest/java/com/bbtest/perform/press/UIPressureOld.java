package com.bbtest.perform.press;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PhxCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.ShellCommand;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

/**
 * @author onuszhao
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class UIPressureOld extends PhxCommon {
    private File perFolder = new File(rootFolder, "perform");
    private File resultFolder = new File(perFolder, "press");
    public File resultFile = new File(resultFolder, "press.txt");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(perFolder);
        FileUtil.createFolder(resultFolder);
        FileUtil.createFile(resultFile);
    }

    /**
     * 调试时用gradle，正式则用adb，需在Build -> Testing -> Run Android Instrumented Tests using Gradle下切换
     * gradle app:testUIPressure --tests=com.bbtest.perform.press.UIPressure
     */
    @Test
    public void testUIPressure() {
        try {
//           testUIPressureOld();
            testUIPressureNew();
//            testUIPressureNovel();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  PressureTest:Exception" + "\n", resultFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), resultFile);
            screenshot(resultFolder + "/press_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    private void testUIPressureNew() {
        // 启动应用
        ShellCommon.amStartApp(device, activity, null);
        sleep(TIMEOUT_LONG);
        ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -d https://www.qq.com");
        sleep(TIMEOUT_MEDIUM);
        getUiObject2ByDesc("toolbar menu").click();
        sleep(TIMEOUT_SHORT);
        getUiObject2ByText("Add to bookmark").click();
        sleep(TIMEOUT_MEDIUM);
        getUiObject2ByDesc("toolbar menu").click();
        sleep(TIMEOUT_SHORT);
        getUiObject2ByText("Add to speed dial").click();
        sleep(TIMEOUT_MEDIUM);
        getUiObject2ByDesc("toolbar menu").click();
        sleep(TIMEOUT_SHORT);
        getUiObject2ByText("Add to home screen").click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        getUiObject2ByText("Explore").click();
        sleep(TIMEOUT_MEDIUM);
        getUiObject2ByText("Files").click();
        sleep(TIMEOUT_MEDIUM);
        getUiObject2ByText("Images").click();
        sleep(TIMEOUT_MEDIUM);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        getUiObject2ByText("Documents").click();
        sleep(TIMEOUT_MEDIUM);
        getUiObject2ByText("DOC").click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        getUiObject2ByText("Me").click();
        sleep(TIMEOUT_MEDIUM);
        getUiObject2ByText("History").click();
        sleep(TIMEOUT_MEDIUM);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        getUiObject2ByText("Tabs").click();
        sleep(TIMEOUT_MEDIUM);
//            getUiObject2s("android.widget.ImageView", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.7, 1).get(0).click();
//            sleep(TIMEOUT_MEDIUM);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        getUiObject2ByText("Home").click();
        sleep(TIMEOUT_MEDIUM);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        back();
        sleep(TIMEOUT_MEDIUM);
    }

    private void testUIPressureOld() {
        // 先保证回到主页
        backToHome();

        // 打开扫一扫
        waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        skipAppDialog();

        // 打开快链HotSites
        waitUiObject2ByText("Google", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_MEDIUM);
        back();
        sleep(TIMEOUT_SHORT);
        skipAppDialog();

        // 打开网址百度
        clickSearchBox(false);
        sleep(TIMEOUT_SHORT);
        setTextAndGo("www.baidu.com");
        sleep(TIMEOUT_MEDIUM);
        UiObject2 accept = waitUiObject2ByText("Accept", TIMEOUT_SHORT);
        if (accept != null) {
            accept.click();
        }
        UiObject2 continueDialog = waitUiObject2ByText("Continue", TIMEOUT_SHORT);
        if (continueDialog != null) {
            back();
        }
        UiObject2 rateUs5star = waitUiObject2ByText("Rate us 5 stars", TIMEOUT_SHORT);
        if (rateUs5star != null) {
            back();
        }
        sleep(TIMEOUT_VERY_SHORT);
        waitUiObject2ByRes("index-kw", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        waitUiObject2ByRes("index-kw", TIMEOUT_MEDIUM).setText("test");
        sleep(TIMEOUT_SHORT);
        waitUiObject2ByRes("index-bn", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        waitUiObject2ByText("Share", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        skipAppDialog();
        skipOtherDialog();
        // 后退
        waitUiObject2ByDesc("toolbar back", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        backToHome();

        // 进入书签
        waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        waitUiObject2ByText("Bookmarks", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 进入历史
        waitUiObject2ByText("History", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 进入我的视频
        waitUiObject2ByText("My Video", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 进入我的音乐
        waitUiObject2ByText("My Music", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 进入广告拦截
        UiObject2 adBlock = waitUiObject2ByRes("com.transsion.phoenix:id/adBlocked", TIMEOUT_MEDIUM);
        if (adBlock == null) {
            adBlock = waitUiObject2ByText("Ad Blocker", TIMEOUT_VERY_SHORT);
        }
        adBlock.click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);

        // 进入设置
        waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        // 搜索引擎
        waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        waitUiObject2ByText("Bing", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 图片
        waitUiObject2ByText("Image", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        waitUiObject2ByText("Always no image", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 字体大小
        waitUiObject2ByText("Font size", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 seekBar = waitUiObject2sByClazz("android.widget.SeekBar", TIMEOUT_MEDIUM).get(0);
        swip(seekBar, "left");
        sleep(TIMEOUT_VERY_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 语言
        waitUiObject2ByText("Language", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 主页
        waitUiObject2ByText("Homepage", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 homeSwitch = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM).get(0);
        if (homeSwitch.isChecked()) {
            homeSwitch.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 下载
        waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        waitUiObject2ByText("Download simultaneously", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        waitUiObject2ByText("6", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        waitUiObject2ByText("Download location", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 storage = waitUiObject2ByText("Internal Storage", TIMEOUT_SHORT);
        if (storage != null) {
            storage.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
        waitUiObject2ByText("Choose it", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 通知栏
        waitUiObject2ByText("Notifications", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        List<UiObject2> notificationSwitchs = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM);
        for (UiObject2 notificationSwitch : notificationSwitchs) {
            if (notificationSwitch.isChecked()) {
                notificationSwitch.click();
            }
        }
        sleep(TIMEOUT_VERY_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 清理数据
        waitUiObject2ByText("Clear data", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        waitUiObject2ByText("Account and password", TIMEOUT_MEDIUM).click();
        waitUiObject2ByText("Video and Document browsing history", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        UiObject2 cleanPhoenix = null;
        List<UiObject2> tmpCleanPhoenixs = waitUiObject2sByTextContains("Clean Up", TIMEOUT_MEDIUM);
        for (UiObject2 tmpCleanPhoenix : tmpCleanPhoenixs) {
            if (tmpCleanPhoenix.isClickable()) {
                cleanPhoenix = tmpCleanPhoenix;
                break;
            }
        }
        if (cleanPhoenix != null && cleanPhoenix.isEnabled()) {
            cleanPhoenix.click();
            sleep(TIMEOUT_LONG);
            // 关闭广告
            closeAdDialog();
            for (int i = 0; i < 10; i++) {
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
        // 设置默认浏览器
        waitUiObject2ByText("Set as default browser", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        for (int i = 0; i < 3; i++) {
            if (waitUiObject2ByText("Set as default browser", TIMEOUT_SHORT) == null) {
                back();
                sleep(TIMEOUT_VERY_SHORT);
            } else {
                break;
            }
        }
        // 上滑
        swip(0.5, 0.8, 0.5, 0.2);
        sleep(TIMEOUT_SHORT);
        // 检查更新
        waitUiObject2ByText("Check for updates", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_MEDIUM);
        for (int i = 0; i < 3; i++) {
            if (waitUiObject2ByText("Check for updates", TIMEOUT_SHORT) == null) {
                back();
                sleep(TIMEOUT_VERY_SHORT);
            } else {
                break;
            }
        }

        // 关于
        waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // Facebook
        waitUiObject2ByTextContains("Join our Facebook page", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        if (ShellCommon.isAppBackstage(device, pkgName)) {
            ShellCommon.amStartApp(device, activity, null);
            sleep(TIMEOUT_VERY_SHORT);
        } else {
            back();
            sleep(TIMEOUT_VERY_SHORT);
        }
        // 用户反馈
        waitUiObject2ByTextContains("Feedback", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 评分
        waitUiObject2ByTextContains("Rate us", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        if (ShellCommon.isAppBackstage(device, pkgName)) {
            ShellCommon.amStartApp(device, activity, null);
            sleep(TIMEOUT_VERY_SHORT);
        }
        // 恢复默认
        waitUiObject2ByTextContains("Reset to default settings", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_VERY_SHORT);
        waitUiObject2ByTextContains("Restore", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);

        // 进入文件
        waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        waitUiObject2ByText("Documents", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        waitUiObject2ByText("Images", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);

        // 多窗口
        waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        waitUiObject2ByText("Home", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        getUiObject2s("android.widget.ImageView", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.7, 1).get(0).click();
        sleep(TIMEOUT_VERY_SHORT);
        skipAppDialog();

        // 退出浏览器
        backExitBrowser();
    }

    private void testUIPressureNovel() {
        String pkgName = "com.cloudview.novel";
        String activity = "com.cloudview.novel/com.cloudview.novel.MainActivity";

        // 启动应用
//        ShellCommon.amStartApp(device, activity, null);
//        sleep(TIMEOUT_LONG);
//        click(width * 2 / 5, height * 2 / 5);
//        sleep(TIMEOUT_MEDIUM);
        getUiObject2ByText("Add to library").click();
        sleep(TIMEOUT_SHORT);
        swip(0.7, 0.5, 0.3, 0.5);
        sleep(TIMEOUT_SHORT);
        back();
        sleep(TIMEOUT_VERY_SHORT);
        getUiObject2ByText("Genres").click();
        sleep(TIMEOUT_MEDIUM);
//        repeat(3) { index ->
//                println("This is iteration $index")
//        }
        swip(0.5, 0.7, 0.5, 0.3);
        sleep(TIMEOUT_SHORT);
        swip(0.5, 0.7, 0.5, 0.3);
        sleep(TIMEOUT_SHORT);
    }

    @After
    public void afterTest() {
        super.afterTest();
    }

}
