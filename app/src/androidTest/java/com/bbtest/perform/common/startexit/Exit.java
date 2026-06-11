package com.bbtest.perform.common.startexit;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class Exit extends PerCommon {

    private File resultFolder = new File(perFolder, "common");
    private String scenesName = "exit";

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
        // 先强制停止
        ShellCommon.forceStopApp(device, pkgName, null);
        sleep(3000);

        // 启动浏览器
        startApp(pkgName);
        sleep(60 * 1000);

        // 开始监控
        startMonitorMainMem(resultFolder, scenesName, pkgName);
        startMonitorSubMem(resultFolder, scenesName, pkgName);

        // 等待30s
        sleep(30 * 1000);

        // 打开about:blank，并等待5s
        searchOrUrl(pkgName, "about:blank", false);
        sleep(5000);
        exitBrowser(pkgName);

        // 等待30s
        sleep(10 * 1000);

        // 结束监控
        stopMonitorMainMem();
        stopMonitorSubMem();

        // 强制停止
        ShellCommon.forceStopApp(device, pkgName, null);
    }

    @After
    public void afterTest() {
        super.afterTest();
    }
}
