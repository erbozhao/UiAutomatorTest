package com.bbtest.compatible;

import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PhxCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.ShellCommand;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class ChannelTest extends PhxCommon {

    private File resultFolder = new File(rootFolder, "compatible");

    private File channelFile = new File(resultFolder, "channel.txt");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
        FileUtil.deleteFile(channelFile);
        FileUtil.createFile(channelFile);
    }

    @Test
    public void testStartOldPhx() {
        try {
            // 启动应用(3.6以前版本)
            startApp(pkgName);
            sleep(TIMEOUT_MEDIUM);
            // 跳过升级弹窗
            UiObject2 update = waitUiObject2ByText("UPDATE", TIMEOUT_MEDIUM);
            if (update == null) {
                update = waitUiObject2ByText("Update", TIMEOUT_VERY_SHORT);
            }
            if (update != null) {
                update.click();
                sleep(TIMEOUT_SHORT);
                if (ShellCommon.isAppBackstage(device, pkgName)) {
                    ShellCommon.amStartApp(device, activity, null);
                    sleep(TIMEOUT_VERY_SHORT);
                }
            }
            // 退出应用
            UiObject2 menu = waitUiObject2ByRes("com.transsion.phoenix:id/vd", TIMEOUT_MEDIUM);
            if (menu != null) {
                menu.click();
                sleep(TIMEOUT_VERY_SHORT);
                waitUiObject2ByRes("com.transsion.phoenix:id/lw", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);
            }
            ShellCommand.execCmdByUiDevice(device, "am force-stop " + pkgName);
        } catch (Exception e) {
            e.printStackTrace();
            screenshot(resultFolder + "/startOldPhx_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    @Test
    public void testStartNewPhx() {
        try {
            // 启动应用(3.6及以后版本)
            startApp(pkgName);
            sleep(TIMEOUT_LONG);
            skipSplash();
            backToHome();
            backExitBrowser();
            ShellCommand.execCmdByUiDevice(device, "am force-stop " + pkgName);
        } catch (Exception e) {
            e.printStackTrace();
            screenshot(resultFolder + "/startNewPhx_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    @Test
    public void testGetChid() {
        try {
            // 启动应用(覆盖安装3.6、4.6等版本，会存在闪屏)
            startApp(pkgName);
            sleep(TIMEOUT_LONG);
            skipSplash();
            backToHome();
            getChid();
            backExitBrowser();
        } catch (Exception e) {
            e.printStackTrace();
            screenshot(resultFolder + "/getChid_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    @After
    public void afterTest() {
        super.afterTest();
    }

    /**
     * 获取渠道ID
     */
    public void getChid() {
        try {
            UiObject2 me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM);
            if (me == null) {
                me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT);
            }
            if (me != null) {
                me.click();
            } else {
                waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
            }
            UiObject2 settings = waitUiObject2ByText("Settings", TIMEOUT_MEDIUM);
            if (settings == null) {
                settings = waitUiObject2ByText("الإعدادات", TIMEOUT_VERY_SHORT);
            }
            settings.click();
            sleep(TIMEOUT_VERY_SHORT);
            skipAppDialog();
            UiObject2 searchEngine = waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM);
            if (searchEngine == null) {
                waitUiObject2ByText("محرك البحث", TIMEOUT_VERY_SHORT);
            }
            sleep(TIMEOUT_VERY_SHORT);
            swip(0.5, 0.8, 0.5, 0.2);
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 aboutPhoenix = waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM);
            if (aboutPhoenix == null) {
                aboutPhoenix = waitUiObject2ByText("حول Phoenix", TIMEOUT_VERY_SHORT);
            }
            aboutPhoenix.click();
            UiObject2 productFeatures = waitUiObject2ByText("Product features", TIMEOUT_MEDIUM);
            if (productFeatures == null) {
                waitUiObject2ByText("خواص المنتج", TIMEOUT_VERY_SHORT);
            }
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 phxIcon = getUiObject2s("android.widget.ImageView", true, 0.2, 0.5, 0.1, 0.5, 0.2, 0.8, 0.02, 0.5).get(0);
            for (int i = 0; i < 5; i++) {
                phxIcon.click();
            }
            waitUiObject2ByText("Basic Info", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            // 将获取到的渠道id存入本地
            String activeCHID = "";
            String currentCHID = "";
            String chidInfo = waitUiObject2ByTextContains("countrycode", TIMEOUT_MEDIUM).getText();
            String[] chidInfoParts = chidInfo.split("\\|");
            for (int i = 0; i < chidInfoParts.length; i++) {
                String chidInfoPart = chidInfoParts[i].trim();
                if (chidInfoPart.contains("activeCHID")) {
                    activeCHID = chidInfoPart.substring(chidInfoPart.indexOf("activeCHID=") + 11);
                } else if (chidInfoPart.contains("currentCHID")) {
                    currentCHID = chidInfoPart.substring(chidInfoPart.indexOf("currentCHID") + 11);
                }
            }
            FileUtil.writeStrToFile("activeCHID=" + activeCHID + ",currentCHID=" + currentCHID, channelFile);
            backToHome();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
