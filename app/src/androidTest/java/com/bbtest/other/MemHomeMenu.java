package com.bbtest.other;

import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.stable.threads.MemoryThread;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class MemHomeMenu extends PerCommon {
    private File resultFolder = new File(perFolder, "common");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testMemHomeMenu() {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, BROWSER_PHX, null);
            sleep(3000);

            // 启动浏览器
            startApp(BROWSER_PHX);
            sleep(60 * 1000);

            // 开始监控
            startMonitorMainMem(resultFolder, "mem_home-menu", BROWSER_PHX);
            startMonitorSubMem(resultFolder, "mem_home-menu", BROWSER_PHX);

            // 测试前: 等待30s
            sleep(30 * 1000);

            // 测试场景:遍历菜单
            UiObject2 toolbarMenu = waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM);
            if (toolbarMenu == null) {
                waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);
                waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);
                waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);
                waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);
                back();
                sleep(TIMEOUT_SHORT);
            } else {
                waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);
                back();
                sleep(TIMEOUT_SHORT);
                waitUiObject2ByDesc("toolbar multiWindow", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);
                back();
                sleep(TIMEOUT_SHORT);
            }

            // 测试后: 等待30s
            sleep(30 * 1000);
            screenshot(resultFolder + "mem_home-menu_" + BROWSER_PHX + ".jpg");

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
