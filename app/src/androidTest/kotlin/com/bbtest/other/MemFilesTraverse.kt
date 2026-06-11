package com.bbtest.other;

import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MemFilesTraverse extends PerCommon {

    private File resultFolder = new File(perFolder, "common");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testFilesTraverseMem() {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, BROWSER_PHX, null);
            sleep(3000);

            // 启动浏览器
            startApp(BROWSER_PHX);
            sleep(60 * 1000);

            // 开始监控
            startMonitorMainMem(resultFolder, "mem_filestraverse", BROWSER_PHX);
            startMonitorSubMem(resultFolder, "mem_filestraverse", BROWSER_PHX);

            // 测试前: 等待30s
            sleep(30 * 1000);

            // 测试场景:遍历Files界面
            List<String> texts = new ArrayList<>();
            UiObject2 toolbarMenu = waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM);
            if (toolbarMenu != null) {
                toolbarMenu.click();
                sleep(TIMEOUT_SHORT);
                texts.add("Internal Storage");
                texts.add("WhatsApp");
                texts.add("Instagram");
                texts.add("Videos");
                texts.add("Music");
                texts.add("Images");
                texts.add("Documents");
                texts.add("Apps");
                texts.add("Archives");
                texts.add("Offline pages");
                texts.add("Others");
                texts.add("junk files");
                texts.add("Cleaner for WhatsApp");
                texts.add("WhatsApp status saver");
            } else {
                texts.add("Downloads");
                texts.add("Status saver");
                texts.add("WhatsApp");
                texts.add("Telegram");
                texts.add("Videos");
                texts.add("Music");
                texts.add("Images");
                texts.add("Documents");
                texts.add("Storage");
                texts.add("More");
                texts.add("Archives");
                texts.add("Instagram");
                texts.add("Offline pages");
                texts.add("Apps");
                texts.add("Others");
                texts.add("Junk files");
                texts.add("Phone boost");
                texts.add("Clean Up WhatsApp");
                texts.add("Clean Up Videos");
                texts.add("Clean Up Phoenix");
                texts.add("Wallpaper");
                texts.add("Ringtones");
                texts.add("Compress files");
                texts.add("Unzip files");
            }
            List<UiObject2> files = waitUiObject2sByText("Files", TIMEOUT_MEDIUM);
            if (files.size() > 1) {
                files.get(1).click();
            } else {
                files.get(0).click();
            }
            sleep(TIMEOUT_SHORT);
            for (String text : texts) {
                if (text.equals("Clean Up WhatsApp")) {
                    swip(0.5, 0.7, 0.5, 0.3);
                    sleep(TIMEOUT_SHORT);
                }
                UiObject2 textUiObject2 = waitUiObject2ByText(text, TIMEOUT_SHORT);
                if (textUiObject2 == null) {
                    textUiObject2 = waitUiObject2ByTextContains(text, TIMEOUT_VERY_SHORT);
                }
                if (textUiObject2 != null) {
                    textUiObject2.click();
                    sleep(TIMEOUT_SHORT);
                    if (!text.equals("More")) {
                        back();
                        sleep(TIMEOUT_VERY_SHORT);
                    }
                }
            }

            // 测试后: 等待30s
            sleep(30 * 1000);
            screenshot(resultFolder + "mem_filestraverse_" + BROWSER_PHX + ".jpg");

            // 结束监控
            stopMonitorMainMem();
            stopMonitorSubMem();

            // 回到首页
            backToHome();

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
