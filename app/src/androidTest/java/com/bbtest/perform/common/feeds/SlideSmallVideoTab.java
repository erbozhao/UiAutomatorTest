package com.bbtest.perform.common.feeds;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class SlideSmallVideoTab extends PerCommon {

    private File resultFolder = new File(perFolder, "common");
    private String scenesName = "slidetab_smallvideo";

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testCpuFpsPhx() {
        testBrowser(BROWSER_PHX, true, true);
    }

    @Test
    public void testCpuFpsLikee() {
        testBrowser(SMALLVIDEO_LIKEE, true, true);
    }

    private void testBrowser(String pkgName, boolean isCpu, boolean isFps) {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            // 启动浏览器
            startApp(pkgName);
            sleep(10 * 1000);

            // 先滑动四次切换到小视频Tab，再上下滑动预加载tab内容
            if (pkgName.equals(BROWSER_PHX)) {
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(3000);
                for (int i = 0; i < 4; i++) {
                    swip(0.7, 0.5, 0.3, 0.5);
                    sleep(3000);
                }
            }
            for (int i = 0; i < 40; i++) {
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(3000);
            }
            for (int i = 0; i < 20; i++) {
                swip(0.5, 0.3, 0.5, 0.7);
                sleep(3000);
            }

            // 等待90s
            sleep(90 * 1000);

            // 再开始监控cpu、fps、flow等
            if (isCpu) {
                startMonitorMainCpu(resultFolder, scenesName, pkgName);
            }
            if (isFps) {
                startMonitorMainFps(resultFolder, scenesName, pkgName);
            }

            // 先向上滑动10次，再向右滑动10次
            long startTime = System.currentTimeMillis();
            long endTime = 0L;
            while (true) {
                endTime = System.currentTimeMillis();
                long costTime = endTime - startTime;
                if (costTime > 3 * 60 * 1000) {
                    break;
                } else {
                    for (int i = 0; i < 10; i++) {
                        swip(0.5, 0.7, 0.5, 0.3);
                        sleep(300);
                    }
                    for (int i = 0; i < 10; i++) {
                        swip(0.5, 0.3, 0.5, 0.7);
                        sleep(300);
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
            screenshot(resultFolder + scenesName + "_" + pkgName + ".jpg");
        }
    }

    @After
    public void afterTest() {
        super.afterTest();
    }
}
