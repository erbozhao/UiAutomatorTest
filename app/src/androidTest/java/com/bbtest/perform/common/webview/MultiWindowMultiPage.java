package com.bbtest.perform.common.webview;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MultiWindowMultiPage extends PerCommon {

    private File resultFolder = new File(perFolder, "common");
    private String scenesName = "multiwindow-multipage";

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testCpuFpsPhx() {
        testBrowserCpuFps(BROWSER_PHX, true, true);
    }

    @Test
    public void testCpuFpsChrome() {
        testBrowserCpuFps(BROWSER_PHX, true, true);
    }

    @Test
    public void testCpuFpsOpera() {
        testBrowserCpuFps(BROWSER_PHX, true, true);
    }

    @Test
    public void testCpuFpsUc() {
        testBrowserCpuFps(BROWSER_PHX, true, true);
    }

    @Test
    public void testCpuFpsFirefox() {
        testBrowserCpuFps(BROWSER_PHX, true, true);
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

            // 连续访问多个网站，且每个网站等待10s,先向上滑动10次，再向下滑动10次
            List<String> urls = new ArrayList<>();
            urls.add(URL_GOOGLE_SEARCH);
            urls.add(URL_YOUTUBE);
            urls.add(URL_YAHOO);
            urls.add(URL_BAIDU_SEARCH);
            urls.add(URL_QQ);
            for (String url : urls) {
                ShellCommon.visitWebsite(device, pkgName, url, null);
                sleep(10 * 1000);
                for (int i = 0; i < 5; i++) {
                    swip(0.5, 0.8, 0.5, 0.2);
                    sleep(1000);
                }
                for (int i = 0; i < 5; i++) {
                    swip(0.5, 0.2, 0.5, 0.8);
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
            screenshot(resultFolder + scenesName + "_" + pkgName + ".jpg");

            // 关闭所有多窗口->退出浏览器->强制停止
            closeAllTabs(pkgName);
            exitBrowser(pkgName);
            ShellCommon.forceStopApp(device, pkgName, null);
        } catch (Exception e) {
            e.printStackTrace();
            screenshot(resultFolder + scenesName + "_" + pkgName + ".jpg");
        }
    }

    @After
    public void afterTest() {
        super.afterTest();
    }
}