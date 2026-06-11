package com.bbtest.perform.common.feeds;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class SwitchTab extends PerCommon {

    private File resultFolder = new File(perFolder, "common");
    private String scenesName = "switchtab";

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

    private void testBrowserMemFlow(String pkgName, boolean isMem) {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            // 初始化浏览器，并等待90s
            startApp(pkgName);
            sleep(10 * 1000);
            if (pkgName.equals(BROWSER_PHX)) {
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(3000);
            }
            sleep(60 * 1000);

            // 开始监控mem
            if (isMem) {
                startMonitorMainMem(resultFolder, scenesName, pkgName);
                startMonitorSubMem(resultFolder, scenesName, pkgName);
            }

            // 测试前: 等待30s
            sleep(30 * 1000);

            // 滑动切换tab，并等待10s
            for (int i = 0; i < 4; i++) {
                swip(0.7, 0.5, 0.3, 0.5);
                sleep(3000);
            }
            sleep(10 * 1000);

            // 测试后: 压后台,等待30s
            sleep(30 * 1000);
            screenshot(resultFolder + scenesName + "_" + pkgName + ".jpg");

            // 结束监控mem
            if (isMem) {
                stopMonitorMainMem();
                stopMonitorSubMem();
            }

            // 关闭所有多窗口->退出浏览器->强制停止
            if (pkgName.equals(BROWSER_PHX)) {
                closeAllTabs(pkgName);
            }
            exitBrowser(pkgName);
            ShellCommon.forceStopApp(device, pkgName, null);
        } catch (Exception e) {
            e.printStackTrace();
            screenshot(resultFolder + scenesName + "_" + pkgName + ".jpg");
        }
    }

    @Test
    public void testCpuFpsPhx() {
        testBrowserCpuFps(BROWSER_PHX, true, true);
    }

    private void testBrowserCpuFps(String pkgName, boolean isCpu, boolean isFps) {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            // 启动浏览器
            startApp(pkgName);
            sleep(10 * 1000);

            // 预加载tab内容(先切换到未加载tab，再切回来)
            if (pkgName.equals(BROWSER_PHX)) {
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(3000);
            }
            for (int i = 0; i < 4; i++) {
                swip(0.7, 0.5, 0.3, 0.5);
                sleep(10 * 1000);
            }
            for (int i = 0; i < 4; i++) {
                swip(0.3, 0.5, 0.7, 0.5);
                sleep(5000);
            }

            // 等待30s
            sleep(30 * 1000);

            // 再开始监控cpu、fps、flow等
            if (isCpu) {
                startMonitorMainCpu(resultFolder, scenesName, pkgName);
            }
            if (isFps) {
                startMonitorMainFps(resultFolder, scenesName, pkgName);
            }

            // 先向左滑动4次，再向右滑动4次
            long startTime = System.currentTimeMillis();
            long endTime = 0L;
            while (true) {
                endTime = System.currentTimeMillis();
                long costTime = endTime - startTime;
                if (costTime > 3 * 60 * 1000) {
                    break;
                } else {
                    for (int i = 0; i < 4; i++) {
                        swip(0.7, 0.5, 0.3, 0.5);
                        sleep(250);
                    }
                    for (int i = 0; i < 4; i++) {
                        swip(0.3, 0.5, 0.7, 0.5);
                        sleep(250);
                    }
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
            screenshot(resultFolder + scenesName + "_" + pkgName + ".jpg");

            // 关闭所有多窗口->退出浏览器->强制停止
            if (pkgName.equals(BROWSER_PHX)) {
                closeAllTabs(pkgName);
            }
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
