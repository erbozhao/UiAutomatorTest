package com.bbtest.perform.common.multiwindow;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class CurWindowOnePage extends PerCommon {

    private File resultFolder = new File(perFolder, "common");
    private String scenesName = "curwindow-onepage";

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testMemFlowPhx() {
        testBrowser(BROWSER_PHX, true, true);
    }

    @Test
    public void testMemFlowChrome() {
        testBrowser(BROWSER_CHROME, true, true);
    }

    @Test
    public void testMemFlowOpera() {
        testBrowser(BROWSER_OPERA, true, true);
    }

    @Test
    public void testMemFlowUc() {
        testBrowser(BROWSER_UC, true, true);
    }

    @Test
    public void testMemFlowFirefox() {
        testBrowser(BROWSER_FIREFOX, true, true);
    }

    private void testBrowser(String pkgName, boolean isMem, boolean isFlow) {
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

            // 开始监控flow
            if (isFlow) {
                startMonitorMainFlow(resultFolder, scenesName, pkgName);
            }

            // 测试场景: 当前窗口访问google
            searchOrUrl(pkgName, URL_GOOGLE, false);
            sleep(10 * 1000);

            // 测试后: 压后台,等待30s
            sleep(30 * 1000);
            screenshot(resultFolder + scenesName + "_" + pkgName + ".jpg");

            // 结束监控flow
            if (isFlow) {
                stopMonitorMainFlow();
            }

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
            screenshot(resultFolder + scenesName + "_" + pkgName + ".jpg");
        }
    }

    @After
    public void afterTest() {
        super.afterTest();
    }
}
