package com.bbtest.other;

import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class MemMeTraverse extends PerCommon {

    private File resultFolder = new File(perFolder, "common");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testMeTraverseMem() {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, BROWSER_PHX, null);
            sleep(3000);

            // 启动浏览器
            startApp(BROWSER_PHX);
            sleep(60 * 1000);

            // 开始监控
            startMonitorMainMem(resultFolder, "mem_metraverse", BROWSER_PHX);
            startMonitorSubMem(resultFolder, "mem_metraverse", BROWSER_PHX);

            // 测试前: 等待30s
            sleep(30 * 1000);

            // 测试场景:遍历Me界面
            UiObject2 me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM);
            if (me != null) {
                me.click();
                sleep(TIMEOUT_SHORT);
                UiObject2 bookmark = waitUiObject2ByText("Bookmark", TIMEOUT_SHORT);
                if (bookmark == null) {
                    bookmark = waitUiObject2ByText("Bookmarks", TIMEOUT_VERY_SHORT);
                }
                bookmark.click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
                waitUiObject2ByText("History", TIMEOUT_SHORT).click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 myVideo = waitUiObject2ByText("My Video", TIMEOUT_SHORT);
                if (myVideo == null) {
                    myVideo = waitUiObject2ByText("My video", TIMEOUT_VERY_SHORT);
                }
                myVideo.click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 myMusic = waitUiObject2ByText("My Music", TIMEOUT_SHORT);
                if (myMusic == null) {
                    myMusic = waitUiObject2ByText("My music", TIMEOUT_VERY_SHORT);
                }
                myMusic.click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 adBlock = waitUiObject2ByRes("com.transsion.phoenix:id/adBlocked", TIMEOUT_SHORT);
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
                UiObject2 darkLight = waitUiObject2ByText("Dark", TIMEOUT_SHORT);
                if (darkLight == null) {
                    darkLight = waitUiObject2ByText("Light", TIMEOUT_VERY_SHORT);
                }
                darkLight.click();
                sleep(TIMEOUT_VERY_SHORT);
                waitUiObject2ByText("Settings", TIMEOUT_SHORT).click();
                sleep(TIMEOUT_VERY_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
            }

            // 测试后: 等待30s
            sleep(30 * 1000);
            screenshot(resultFolder + "mem_metraverse_" + BROWSER_PHX + ".jpg");

            // 结束监控
            stopMonitorMainMem();
            stopMonitorSubMem();

            // 回到首页
            backToHome();

            // 关闭所有多窗口->退出浏览器->强制停止
            closeAllTabs(BROWSER_PHX);
            exitBrowser(BROWSER_PHX);
            ShellCommon.forceStopApp(device, BROWSER_PHX, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void afterTest() {
        super.afterTest();
    }
}
