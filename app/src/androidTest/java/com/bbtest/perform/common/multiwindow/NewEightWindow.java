package com.bbtest.perform.common.multiwindow;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class NewEightWindow extends PerCommon {

    private File resultFolder = new File(perFolder, "common");
    private String scenesName = "neweightwindow";

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testMemPhx() {
        testBrowserMemFlow(BROWSER_PHX, true);
    }

    @Test
    public void testMemChrome() {
        testBrowserMemFlow(BROWSER_CHROME, true);
    }

    @Test
    public void testMemOpera() {
        testBrowserMemFlow(BROWSER_OPERA, true);
    }

    @Test
    public void testMemUc() {
        testBrowserMemFlow(BROWSER_UC, true);
    }

    @Test
    public void testMemFirefox() {
        testBrowserMemFlow(BROWSER_FIREFOX, true);
    }

    private void testBrowserMemFlow(String pkgName, boolean isMem) {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            // 启动浏览器，并初始化场景
            startApp(pkgName);
            sleep(60 * 1000);

            // 开始监控mem
            if (isMem) {
                startMonitorMainMem(resultFolder, scenesName, pkgName);
                startMonitorSubMem(resultFolder, scenesName, pkgName);
            }

            // 测试前: 等待30s
            sleep(30 * 1000);

            // 测试场景: 连续新建至8个窗口，并等待10s
            for (int i = 0; i < 7; i++) {
                if (pkgName.equals(BROWSER_FIREFOX) && i == 0) {
                    searchOrUrl(pkgName, "about:blank", false);
                }
                newTab(pkgName);
            }

            // 测试后: 压后台,等待30s
            sleep(30 * 1000);
            screenshot(resultFolder + scenesName + "1_" + pkgName + ".jpg");

            // 结束监控mem
            if (isMem) {
                stopMonitorMainMem();
                stopMonitorSubMem();
            }

            // 关闭所有多窗口->退出浏览器->强制停止
            closeAllTabs(pkgName);
            exitBrowser(pkgName);
            ShellCommon.forceStopApp(device, pkgName, null);
        } catch (Exception e) {
            e.printStackTrace();
            screenshot(resultFolder + scenesName + "1_" + pkgName + ".jpg");
        }
    }

    @Test
    public void testCpuFpsPhx() {
        testBrowserCpuFps(BROWSER_PHX, true, true);
    }

    @Test
    public void testCpuFpsChrome() {
        testBrowserCpuFps(BROWSER_CHROME, true, true);
    }

    @Test
    public void testCpuFpsOpera() {
        testBrowserCpuFps(BROWSER_OPERA, true, true);
    }

    @Test
    public void testCpuFpsUc() {
        testBrowserCpuFps(BROWSER_UC, true, true);
    }

    @Test
    public void testCpuFpsFirefox() {
        testBrowserCpuFps(BROWSER_FIREFOX, true, true);
    }

    private void testBrowserCpuFps(String pkgName, boolean isCpu, boolean isFps) {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            // 启动浏览器，并初始化场景
            startApp(pkgName);
            sleep(90 * 1000);

            // 再开始监控cpu、fps、flow等
            if (isCpu) {
                startMonitorMainCpu(resultFolder, scenesName, pkgName);
            }
            if (isFps) {
                startMonitorMainFps(resultFolder, scenesName, pkgName);
            }

            // 连续新建8个窗口,并向上滑动5次，再向下滑动5次
            for (int i = 0; i < 7; i++) {
                newTab(pkgName);
            }
            goinTab(pkgName);
            if (pkgName.equals(BROWSER_OPERA) || pkgName.equals(BROWSER_UC)) {
                for (int i = 0; i < 5; i++) {
                    swip(0.7, 0.5, 0.3, 0.5);
                    sleep(1000);
                }
                for (int i = 0; i < 5; i++) {
                    swip(0.3, 0.5, 0.7, 0.5);
                    sleep(1000);
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    swip(0.5, 0.7, 0.5, 0.3);
                    sleep(1000);
                }
                for (int i = 0; i < 5; i++) {
                    swip(0.5, 0.3, 0.5, 0.7);
                    sleep(1000);
                }
            }

            // 先停止监控cpu、fps、flow等
            if (isCpu) {
                stopMonitorMainCpu();
            }
            if (isFps) {
                stopMonitorMainFps();
            }

            // 测试后: 截图
            screenshot(resultFolder + scenesName + "2_" + pkgName + ".jpg");

            // 退出多窗口
            back();
            sleep(3000);

            // 关闭所有多窗口->退出浏览器->强制停止
            closeAllTabs(pkgName);
            exitBrowser(pkgName);
            ShellCommon.forceStopApp(device, pkgName, null);
        } catch (Exception e) {
            e.printStackTrace();
            screenshot(resultFolder + scenesName + "2_" + pkgName + ".jpg");
        }
    }

    @After
    public void afterTest() {
        super.afterTest();
    }
}
