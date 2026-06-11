package com.bbtest.other;

import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class MemFeedsSlideView extends PerCommon {

    private File resultFolder = new File(perFolder, "common");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testFeedsSlideViewMem() {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, BROWSER_PHX, null);

            // 启动浏览器
            startApp(BROWSER_PHX);

            // 等待90s
            sleep(60 * 1000);

            // 开始监控
            startMonitorMainMem(resultFolder, "mem_feeds-slideview", BROWSER_PHX);
            startMonitorSubMem(resultFolder, "mem_feeds-slideview", BROWSER_PHX);

            // 测试前: 等待30s
            sleep(30 * 1000);

            // 测试场景: 滑动查看
            for (int i = 0; i < 20; i++) {
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(3000);
                List<UiObject2> linearLayouts = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.1, 0.8, 0, 1, 0.02, 0.9);
                if (linearLayouts != null && linearLayouts.size() > 0) {
                    UiObject2 firstlinearLayout = linearLayouts.get(0);
                    UiObject2 firstVideo = getChildUiObject2(firstlinearLayout, false, "android.widget.ImageView", 0.8, 1, 0, 1, 0, 1, 0, 1, true);
                    if (firstVideo != null) {
                        UiObject2 firstVideoBottom = getChildUiObject2(firstlinearLayout, false, "android.widget.LinearLayout", 0.8, 1, 0, 0.1, 0, 1, 0, 1, false);
                        firstVideoBottom.click();
                    } else {
                        UiObject2 firstSmallVideo = getChildUiObject2(firstlinearLayout, false, "android.widget.ImageView", 0.4, 0.6, 0, 1, 0, 1, 0, 1, true);
                        if (firstSmallVideo != null) {
                            firstSmallVideo.click();
                        } else {
                            firstlinearLayout.click();
                        }
                    }
                }
                sleep(5000);
                back();
                sleep(3000);
            }

            // 测试后: 等待30s
            sleep(30 * 1000);
            screenshot(resultFolder + "mem_feeds-slideview_" + BROWSER_PHX + ".jpg");

            // 结束监控
            stopMonitorMainMem();
            stopMonitorSubMem();

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
