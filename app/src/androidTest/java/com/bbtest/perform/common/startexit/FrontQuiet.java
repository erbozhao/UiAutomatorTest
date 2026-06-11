package com.bbtest.perform.common.startexit;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class FrontQuiet extends PerCommon {

    private File resultFolder = new File(perFolder, "common");
    private String scenesName = "frontquiet";

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testCpuPhx() {
        testBrowser(BROWSER_PHX, true);
    }

    @Test
    public void testCpuChrome() {
        testBrowser(BROWSER_CHROME, true);
    }

    @Test
    public void testCpuOpera() {
        testBrowser(BROWSER_OPERA, true);
    }

    @Test
    public void testCpuUc() {
        testBrowser(BROWSER_UC, true);
    }

    @Test
    public void testCpuFirefox() {
        testBrowser(BROWSER_FIREFOX, true);
    }

    private void testBrowser(String pkgName, boolean isCpu) {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            // 启动浏览器
            startApp(pkgName);

            // 等待90s
            sleep(90 * 1000);

            // 再开始监控cpu、fps、flow等
            if (isCpu) {
                startMonitorMainCpu(resultFolder, scenesName, pkgName);
            }

            // 前台静置10m
            sleep(10 * 60 * 1000);

            // 先停止监控cpu、fps、flow等
            if (isCpu) {
                stopMonitorMainCpu();
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
