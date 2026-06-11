package com.bbtest.perform.common.feeds;

import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class NormalUse extends PerCommon {

    private File resultFolder = new File(perFolder, "common");
    private String scenesName = "normaluse";

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

    private void testBrowser(String pkgName, boolean isCpu) {
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
            sleep(90 * 1000);

            // 再开始监控cpu、fps、flow等
            if (isCpu) {
                startMonitorMainCpu(resultFolder, scenesName, pkgName);
            }

            // 访问资讯正文
            switchFeedsTab("Lifestyle");
            for (int j = 0; j < 5; j++) {
                List<UiObject2> uiObject2s = null;
                if (pkgName.equals(NEWS_OPERA)) {
                    uiObject2s = getUiObject2s("android.widget.FrameLayout", true, 0.9, 1, 0.1, 0.5, 0, 1, 0.02, 0.8);
                } else {
                    uiObject2s = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.1, 0.5, 0, 1, 0.02, 0.8);
                }
                uiObject2s.get(0).click();
                sleep(3000);
                swipUp(5, 1000);
                swipDown(5, 1000);
                back();
                sleep(3000);
                swip(0.5, 0.3, 0.5, 0.7);
                sleep(3000);
            }

            // 访问视频
            switchFeedsTab("Video");
            for (int j = 0; j < 5; j++) {
                List<UiObject2> uiObject2s = null;
                if (pkgName.equals(NEWS_TOUTIAO)) {
                    uiObject2s = getUiObject2s("android.widget.RelativeLayout", true, 0.9, 1, 0.1, 0.5, 0, 1, 0.02, 0.8);
                    UiObject2 firstVideo = uiObject2s.get(0);
                    int centerX = firstVideo.getVisibleBounds().left + (firstVideo.getVisibleBounds().right - firstVideo.getVisibleBounds().left) / 2;
                    int bottomY = firstVideo.getVisibleBounds().bottom - (firstVideo.getVisibleBounds().bottom - firstVideo.getVisibleBounds().top) / 10;
                    click(centerX, bottomY);
                } else {
                    uiObject2s = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.1, 0.5, 0, 1, 0.02, 0.9);
                    if (pkgName.equals(BROWSER_PHX)) {
                        UiObject2 firstVideoBottom = getChildUiObject2(uiObject2s.get(0), false, "android.widget.LinearLayout", 0.8, 1, 0, 0.1, 0, 1, 0, 1, false);
                        firstVideoBottom.click();
                    } else {
                        uiObject2s.get(0).click();
                    }
                }
                sleep(3000);
                swipUp(5, 1000);
                back();
                sleep(3000);
                swip(0.5, 0.3, 0.5, 0.7);
                sleep(3000);
            }

            // 访问小视频
            switchFeedsTab("Short Video");
            for (int j = 0; j < 5; j++) {
                List<UiObject2> uiObject2s = null;
                if (pkgName.equals(BROWSER_PHX)) {
                    uiObject2s = getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.8, 0, 1, 0.02, 0.9);
                    uiObject2s.get(0).click();
                    sleep(3000);
                    swipUp(5, 1000);
                    swipDown(5, 1000);
                } else if (pkgName.equals(NEWS_TOUTIAO)) {
                    uiObject2s = getUiObject2s("android.widget.RelativeLayout", true, 0.4, 0.6, 0.2, 0.8, 0, 1, 0.02, 0.9);
                    uiObject2s.get(0).click();
                    sleep(3000);
                    swipLeft(5, 1000);
                    swipRight(5, 1000);
                } else {
                    uiObject2s = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.1, 0.5, 0, 1, 0.02, 0.8);
                    uiObject2s.get(0).click();
                    sleep(3000);
                    swipUp(5, 1000);
                    swipDown(5, 1000);
                }
                back();
                sleep(3000);
                swip(0.5, 0.3, 0.5, 0.7);
                sleep(3000);
            }

            // 访问图片
            switchFeedsTab("Hot Girl");
            for (int j = 0; j < 5; j++) {
                List<UiObject2> uiObject2s = null;
                if (pkgName.equals(NEWS_OPERA)) {
                    uiObject2s = getUiObject2s("android.view.View", true, 0.9, 1, 0.4, 1, 0, 1, 0.02, 1);
                    if (uiObject2s == null || uiObject2s.size() == 0) {
                        click(width / 2, height / 2);
                    }
                    sleep(3000);
                    swipUp(5, 1000);
                    swipDown(5, 1000);
                } else {
                    uiObject2s = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.4, 1, 0, 1, 0.02, 1);
                    uiObject2s.get(0).click();
                    sleep(3000);
                    if (pkgName.equals(BROWSER_PHX)) {
                        swipLeft(5, 1000);
                        swipRight(5, 1000);
                    } else {
                        swipUp(5, 1000);
                        swipDown(5, 1000);
                    }
                    back();
                    sleep(3000);
                }
                swip(0.5, 0.3, 0.5, 0.7);
                sleep(3000);
            }

            // 先停止监控cpu、fps、flow等
            if (isCpu) {
                stopMonitorMainCpu();
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
