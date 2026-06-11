package com.bbtest.perform.common.multiwindow;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class NewWindowThreePage extends PerCommon {

    private File resultFolder = new File(perFolder, "common");
    private String scenesName = "newwindow-threepage";

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testMemPhx() {
        testBrowser(BROWSER_PHX, true);
    }

    @Test
    public void testMemChrome() {
        testBrowser(BROWSER_CHROME, true);
    }

    @Test
    public void testMemOpera() {
        testBrowser(BROWSER_OPERA, true);
    }

    @Test
    public void testMemUc() {
        testBrowser(BROWSER_UC, true);
    }

    @Test
    public void testMemFirefox() {
        testBrowser(BROWSER_FIREFOX, true);
    }

    private void testBrowser(String pkgName, boolean isMem) {
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

            // 测试场景: 新建3个窗口并打开Google页面
            for (int i = 0; i < 3; i++) {
                searchOrUrl(pkgName, URL_GOOGLE, false);
                sleep(10 * 1000);
                if (i != 2) {
                    newTab(pkgName);
                }
            }

            // 测试后: 等待30s
            sleep(30 * 1000);
            screenshot(resultFolder + scenesName + "_" + pkgName + ".jpg");

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
