package com.bbtest.perform.common.startexit;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class Start extends PerCommon {

    private File resultFolder = new File(perFolder, "common");
    private String scenesName = "start";

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testMemPhx() {
        testBrowser(BROWSER_PHX);
    }

    @Test
    public void testMemChrome() {
        testBrowser(BROWSER_CHROME);
    }

    @Test
    public void testMemOpera() {
        testBrowser(BROWSER_OPERA);
    }

    @Test
    public void testMemUc() {
        testBrowser(BROWSER_UC);
    }

    @Test
    public void testMemFirefox() {
        testBrowser(BROWSER_FIREFOX);
    }

    private void testBrowser(String pkgName) {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            // 开始监控
            startMonitorMainMem(resultFolder, scenesName, pkgName);
            startMonitorSubMem(resultFolder, scenesName, pkgName);

            // 启动浏览器
            startApp(pkgName);

            // 等待90s
            sleep(90 * 1000);
            screenshot(resultFolder + scenesName + "_" + pkgName + ".jpg");

            // 结束监控
            stopMonitorMainMem();
            stopMonitorSubMem();

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
