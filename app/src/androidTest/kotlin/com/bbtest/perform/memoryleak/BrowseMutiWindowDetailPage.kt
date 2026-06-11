package com.bbtest.perform.memoryleak;

import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.stable.threads.DumpheapThread;
import com.bbtest.stable.threads.MemoryThread;
import com.bbtest.tools.DeviceInfo;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class BrowseMutiWindowDetailPage extends PerCommon {

    private File resultFolder = new File(perFolder, "memoryleak");
    private File resultFile = new File(resultFolder, "memoryleak_49_browsemutiwindow-detailpage.txt");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
        FileUtil.deleteFile(resultFile);
        FileUtil.createFile(resultFile);
    }

    @Test
    public void testBrowseMutiWindowDetailPage() {
        // 获取设备名
        DeviceInfo deviceInfo = new DeviceInfo(device);
        String model = deviceInfo.getModel();
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, BROWSER_PHX, null);
            sleep(3000);

            // 启动浏览器
            startApp(BROWSER_PHX);
            sleep(60 * 1000);

            // 开始监控
            MemoryThread memoryThread = new MemoryThread(0.5f, BROWSER_PHX, device, resultFile, 1);
            memoryThread.start();
            DumpheapThread startDumpheapThread = new DumpheapThread(BROWSER_PHX, device, model + "_BrowseMutiWindow-DetailPage-Start", resultFolder);
            startDumpheapThread.start();
            startDumpheapThread.join();

            // 测试前: 等待30s
            sleep(30 * 1000);

            // 测试场景: 单窗口连续浏览网页30次
            for (int i = 0; i < 20; i++) {
                if (i > 0) {
                    UiObject2 multiWindow1 = waitUiObject2ByText("Tabs", TIMEOUT_MEDIUM);
                    if (multiWindow1 == null) {
                        multiWindow1 = waitUiObject2ByDesc("toolbar multiWindow", TIMEOUT_SHORT);
                    }
                    multiWindow1.click();
                    sleep(3000);
                    getUiObject2s("android.widget.ImageView", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.7, 1).get(0).click();
                    sleep(3000);
                }
                clickSearchBox(BROWSER_PHX, false);
                sleep(3000);
                setTextAndGo(BROWSER_PHX, "www.baidu.com");
                sleep(5000);
                if (i == 0) {
                    skipAppDialog();
                    skipOtherDialog();
                }
            }
            closeAllTabs(BROWSER_PHX);

            // 测试后: 压后台，并等待30s
            sleep(30 * 1000);
            ShellCommon.pressHome(device, null);
            sleep(30 * 1000);

            // 结束监控
            memoryThread.setIsTimeOver(true);
            memoryThread.join();
            DumpheapThread endDumpheapThread = new DumpheapThread(BROWSER_PHX, device, model + "_BrowseMutiWindow-DetailPage-End", resultFolder);
            endDumpheapThread.start();
            endDumpheapThread.join();

            // 调回前台
            String activity = ShellCommon.getActivity(device, BROWSER_PHX, null);
            ShellCommon.amStartApp(device, activity, null);
            sleep(3000);

            // 回到主页->关闭所有多窗口->退出浏览器->强制停止
            backToHome();
            exitBrowser(BROWSER_PHX);
            ShellCommon.forceStopApp(device, BROWSER_PHX, null);
        } catch (Exception e) {
            e.printStackTrace();
            screenshot(resultFolder + "/Img_" + model + "_BrowseMutiWindow-DetailPage-Exception_" + BROWSER_PHX + "_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }

    }

    @After
    public void afterTest() {
        super.afterTest();
    }
}
