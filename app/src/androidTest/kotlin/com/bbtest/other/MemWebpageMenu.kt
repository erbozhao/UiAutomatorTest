package com.bbtest.other;

import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.stable.threads.MemoryThread;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.ShellCommand;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class MemWebpageMenu extends PerCommon {

    private File resultFile = new File(perFolder, "mem_webpage-menu.txt");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.deleteFile(resultFile);
        FileUtil.createFile(resultFile);
    }

    @Test
    public void testMemWebpageMenu() {
        testBrowser(BROWSER_PHX);
    }

    private void testBrowser(String pkgName) {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            // 启动浏览器
            startApp(pkgName);
            sleep(60 * 1000);

            // 开始监控
            MemoryThread memoryThread = new MemoryThread(0.5f, BROWSER_PHX, device, resultFile, 1);
            memoryThread.start();

            // 测试前: 等待30s
            sleep(30 * 1000);

            // 测试场景:打开网页->遍历工具栏
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com");
            sleep(TIMEOUT_MEDIUM);
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 bookmarkHistory = waitUiObject2ByText("Bookmark\n" + "&history", TIMEOUT_MEDIUM);
            if (bookmarkHistory == null) {
                UiObject2 bookmark = waitUiObject2ByText("Bookmark", TIMEOUT_MEDIUM);
                if (bookmark == null) {
                    bookmark = waitUiObject2ByText("Bookmarks", TIMEOUT_SHORT);
                }
                bookmark.click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
                waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
                waitUiObject2ByText("History", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
            } else {
                bookmarkHistory.click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("My video", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("My music", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
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
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 darkLight = waitUiObject2ByText("Dark", TIMEOUT_MEDIUM);
            if (darkLight == null) {
                darkLight = waitUiObject2ByText("Light", TIMEOUT_SHORT);
            }
            if (darkLight == null) {
                darkLight = waitUiObject2ByText("Night", TIMEOUT_SHORT);
                if (darkLight == null) {
                    darkLight = waitUiObject2ByText("Normal", TIMEOUT_SHORT);
                }
                if (darkLight != null) {
                    darkLight.click();
                    sleep(TIMEOUT_VERY_SHORT);
                    waitUiObject2ByText("Dark theme", TIMEOUT_SHORT).click();
                    sleep(TIMEOUT_VERY_SHORT);
                    if (ShellCommon.isAppBackstage(device, pkgName)) {
                        String activity = ShellCommon.getActivity(device, pkgName, null);
                        ShellCommon.amStartApp(device, activity, null);
                        sleep(TIMEOUT_VERY_SHORT);
                    }
                }
            } else {
                darkLight.click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_VERY_SHORT);
            waitUiObject2ByText("Settings", TIMEOUT_SHORT).click();
            sleep(TIMEOUT_VERY_SHORT);
            back();
            sleep(TIMEOUT_VERY_SHORT);

            // 测试后: 等待30s
            sleep(30 * 1000);

            // 结束监控
            memoryThread.setIsTimeOver(true);
            memoryThread.join();

            // 回到首页
            backToHome();

            // 关闭所有多窗口->退出浏览器->强制停止
            closeAllTabs(pkgName);
            exitBrowser(pkgName);
            ShellCommon.forceStopApp(device, pkgName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void afterTest() {
        super.afterTest();
    }
}
