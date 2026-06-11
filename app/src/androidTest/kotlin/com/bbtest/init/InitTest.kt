package com.bbtest.init;


import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PhxCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.WifiTools;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author onuszhao
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class InitTest extends PhxCommon {

    private File resultFolder = new File(rootFolder, "init");
    private File initFile = new File(resultFolder, "init.txt");
    private File ignoreFile = new File(rootFolder, "ignore.txt");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录
        FileUtil.deleteFolder(resultFolder);
        FileUtil.createFolder(resultFolder);
        // 初始化网络
        WifiTools wifiTools = new WifiTools(device, getApplicationContext());
        if (!wifiTools.isNetworkConnected() && !wifiTools.isNetworkAvailable()) {
            wifiTools.openWifi();
            wifiTools.startScantWifi();
            CommonUtil.sleep(5000);
            wifiTools.connectWifi("YLKJ", "phxbrowser2020");
            CommonUtil.sleep(5000);
            if (!wifiTools.isNetworkConnected() && !wifiTools.isNetworkAvailable()) {
                wifiTools.connectWifi("YLKJ-2.4G", "phxbrowser2020");
                CommonUtil.sleep(5000);
            }
        }
    }

    @Test
    public void testInitBrowserDefault() {
        initCountryLanguage(null, null);
    }

    @Test
    public void testInitBrowserNG() {
        initCountryLanguage("NG", "en");
    }

    @Test
    public void testInitBrowserEG() {
        initCountryLanguage("EG", "ar");
    }

    @Test
    public void testInitReleaseEnv() {
        try {
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitReleaseEnv:启动应用" + "\n", initFile);
                startApp(pkgName);
                sleep(TIMEOUT_LONG);
            }
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitReleaseEnv:回到首页" + "\n", initFile);
            backToHome();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitReleaseEnv:切换正式环境" + "\n", initFile);
            switchGrayEnv(false);
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitReleaseEnv:启动应用" + "\n", initFile);
                startApp(pkgName);
                sleep(TIMEOUT_LONG);
            }
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitReleaseEnv:回到首页" + "\n", initFile);
            backToHome();
            screenshot(resultFolder + "/init_release_" + CommonUtil.getCurTimeForFile() + ".jpg");
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitReleaseEnv:back退出应用" + "\n", initFile);
            backExitBrowser();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitReleaseEnv:强杀进程" + "\n", initFile);
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitReleaseEnv:Success" + "\n", initFile);
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitReleaseEnv:Exception" + "\n", initFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), initFile);
            screenshot(resultFolder + "/init_release_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    @Test
    public void testInitGrayEnv() {
        try {
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitGrayEnv:启动应用" + "\n", initFile);
                startApp(pkgName);
                sleep(TIMEOUT_LONG);
            }
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitGrayEnv:回到首页" + "\n", initFile);
            backToHome();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitGrayEnv:切换灰度环境" + "\n", initFile);
            switchGrayEnv(true);
            if (ShellCommon.isAppBackstage(device, pkgName)) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitGrayEnv:启动应用" + "\n", initFile);
                startApp(pkgName);
                sleep(TIMEOUT_LONG);
            }
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitGrayEnv:回到首页" + "\n", initFile);
            backToHome();
            screenshot(resultFolder + "/init_gray_" + CommonUtil.getCurTimeForFile() + ".jpg");
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitGrayEnv:back退出应用" + "\n", initFile);
            backExitBrowser();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitGrayEnv:强杀进程" + "\n", initFile);
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitGrayEnv:Success" + "\n", initFile);
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitGrayEnv:Exception" + "\n", initFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), initFile);
            screenshot(resultFolder + "/init_gray_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    private void initCountryLanguage(String country, String language) {
        try {
            // 初始化文件
            FileUtil.createFile(initFile);

            //启动应用->跳过闪屏->切换语言->停止应用
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:启动应用" + "\n", initFile);
            startApp(pkgName);
            sleep(TIMEOUT_LONG);
            if (!ShellCommon.isAppBackstage(device, pkgName)) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:跳过闪屏" + "\n", initFile);
                skipSplash();
            }
            boolean isClickIgnoreLimit = false;
            for (int i = 0; i < 3; i++) {
                if (ShellCommon.isAppBackstage(device, pkgName)) {
                    startApp(pkgName);
                }

                if (!isClickIgnoreLimit) {
                    FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:忽略限制" + "\n", initFile);
                    UiObject2 cnLimit = waitUiObject2ByText("Sorry, the service is unavailable for policy reasons. The browser will quit in 3 seconds.", TIMEOUT_MEDIUM);
                    if (cnLimit == null) {
                        cnLimit = waitUiObject2ByText("Sorry, the service is unavailable for policy reasons. The browser will quit in 3 seconds.", TIMEOUT_MEDIUM);
                    }
                    if (cnLimit != null) {
                        int x = cnLimit.getVisibleBounds().centerX();
                        int y = cnLimit.getVisibleBounds().centerY();
                        for (int j = 0; j < 11; j++) {
                            click(x, y);
                        }
                        if (!ShellCommon.isAppBackstage(device, pkgName)) {
                            isClickIgnoreLimit = true;
                            sleep(TIMEOUT_SHORT);
                        }
                    } else {
                        if (isClickIgnoreLimit) {
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile);
            backToHome();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:跳过文件引导" + "\n", initFile);
            skipFilesGuide();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile);
            backToHome();
            if (country != null && language != null) {
                if (language.equals("en")) {
                    UiObject2 forYou = waitUiObject2ByText("For you", TIMEOUT_MEDIUM);
                    if (forYou == null) {
                        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:切换英语" + "\n", initFile);
                        switchLanguage(country, language);
                    }
                } else if (language.equals("ar")) {
                    UiObject2 forYou = waitUiObject2ByText("مُختار لك", TIMEOUT_MEDIUM);
                    if (forYou == null) {
                        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:切换阿语" + "\n", initFile);
                        switchLanguage(country, language);
                    }
                }
            }
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile);
            backToHome();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:back退出应用" + "\n", initFile);
            backExitBrowser();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:强杀进程" + "\n", initFile);
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            //再次启动应用->跳过Feeds上滑->停止应用
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:启动应用" + "\n", initFile);
            startApp(pkgName);
            sleep(TIMEOUT_LONG);
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile);
            backToHome();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:跳过Feeds引导" + "\n", initFile);
            skipFeedsGuide();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile);
            backToHome();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:back退出应用" + "\n", initFile);
            backExitBrowser();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:强杀进程" + "\n", initFile);
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            //再次启动应用->跳过视频嗅探引导->停止应用
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:启动应用" + "\n", initFile);
            startApp(pkgName);
            sleep(TIMEOUT_LONG);
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile);
            backToHome();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:跳过快链引导" + "\n", initFile);
            skipSniffVideosGuide();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:回到主页" + "\n", initFile);
            backToHome();
            screenshot(resultFolder + "/init_" + CommonUtil.getCurTimeForFile() + ".jpg");
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:back退出应用" + "\n", initFile);
            backExitBrowser();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:强杀进程" + "\n", initFile);
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:Success" + "\n", initFile);
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "InitCountryLanguage:Exception" + "\n", initFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), initFile);
            screenshot(resultFolder + "/init_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    @Test
    public void testIgnoringBatteryOptimization() {
        /**
         * 设置后台限制
         */
        try {
            // 初始化文件
            FileUtil.createFile(ignoreFile);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 初始化需要忽略app的包名
                List<String> ignorePkgNames = new ArrayList<>();
                ignorePkgNames.add(getApplicationContext().getPackageName());
                ignorePkgNames.add(getApplicationContext().getPackageName() + ".test");

                // 开始设置
                PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(getApplicationContext().POWER_SERVICE);
                for (String ignorePkgName : ignorePkgNames) {
                    //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框
                    boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(ignorePkgName);
                    if (!hasIgnored) {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + ignorePkgName));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                        sleep(TIMEOUT_VERY_SHORT);
                        UiObject2 allow = waitUiObject2ByText("ALLOW", TIMEOUT_MEDIUM);
                        if (allow == null) {
                            allow = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT);
                        }
                        allow.click();
                        sleep(TIMEOUT_SHORT);
                    }
                }
            }
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "backgroundRestriction:Success" + "\n", ignoreFile);
            screenshot(resultFolder + "/backgroundRestriction_" + CommonUtil.getCurTimeForFile() + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "backgroundRestriction:Exception" + "\n", ignoreFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), initFile);
            screenshot(resultFolder + "/backgroundRestriction_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }

        /**
         * 设置电池优化
         */
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 初始化需要忽略app的包名
                List<String> ignoreAppNames = new ArrayList<>();
                ignoreAppNames.add("BBTest");
                ignoreAppNames.add("BBTest Test");

                // 开始设置
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", getApplicationContext().getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                sleep(TIMEOUT_SHORT);
                String batteryScrollableClazz = getScrollableClazz();
                UiObject battery = waitScrollableUiObjectByText(batteryScrollableClazz, "Battery", false);
                if (battery == null) {
                    UiObject2 advanced = waitUiObject2ByText("Advanced", TIMEOUT_MEDIUM);
                    if (advanced != null) {
                        advanced.click();
                        sleep(TIMEOUT_SHORT);
                        battery = waitScrollableUiObjectByText(batteryScrollableClazz, "Battery", false);
                    }
                }
                battery.click();
                sleep(TIMEOUT_SHORT);
                UiObject batteryOptimization = waitScrollableUiObjectByText(batteryScrollableClazz, "Battery optimisation", false);
                if (batteryOptimization == null) {
                    batteryOptimization = waitScrollableUiObjectByText(batteryScrollableClazz, "Battery optimization", false);
                }
                if (batteryOptimization == null) {
                    batteryOptimization = waitScrollableUiObjectByText(batteryScrollableClazz, "Optimise battery usage", false);
                }
                batteryOptimization.click();
                sleep(TIMEOUT_SHORT);
                waitUiObject2ByRes("com.android.settings:id/filter_spinner", TIMEOUT_VERY_LONG).click();
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 allApps = waitUiObject2ByText("All apps", TIMEOUT_MEDIUM);
                if (allApps == null) {
                    allApps = waitUiObject2ByText("All", TIMEOUT_VERY_SHORT);
                }
                allApps.click();
                sleep(TIMEOUT_SHORT);
                String appScrollableClazz = getScrollableClazz();
                if (appScrollableClazz.equals("android.view.ViewGroup")) {
                    for (String ignoreAppName : ignoreAppNames) {
                        waitScrollableUiObjectByText(appScrollableClazz, ignoreAppName, false);
                        sleep(TIMEOUT_SHORT);
                        UiObject2 appSwitch = getUiObject2ByChildText("android.widget.LinearLayout", true, "BBTest", "android.widget.Switch");
                        if (appSwitch.isChecked()) {
                            appSwitch.click();
                            sleep(TIMEOUT_SHORT);
                        }
                    }
                } else {
                    for (String ignoreAppName : ignoreAppNames) {
                        waitScrollableUiObjectByText(appScrollableClazz, ignoreAppName, false).click();
                        sleep(TIMEOUT_VERY_SHORT);
                        UiObject2 notOptimized = waitUiObject2ByText("Don’t optimise", TIMEOUT_MEDIUM);
                        if (notOptimized == null) {
                            notOptimized = waitUiObject2ByText("Don’t optimize", TIMEOUT_VERY_SHORT);
                        }
                        if (notOptimized == null) {
                            notOptimized = waitUiObject2ByText("Not optimized", TIMEOUT_VERY_SHORT);
                        }
                        notOptimized.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        UiObject2 done = waitUiObject2ByText("Done", TIMEOUT_MEDIUM);
                        if (done == null) {
                            done = waitUiObject2ByText("DONE", TIMEOUT_VERY_SHORT);
                        }
                        done.click();
                        sleep(TIMEOUT_SHORT);
                    }
                }
            }

            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "batteryOptimization:Success" + "\n", ignoreFile);
            screenshot(resultFolder + "/batteryOptimization_" + CommonUtil.getCurTimeForFile() + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "batteryOptimization:Exception" + "\n", ignoreFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), ignoreFile);
            screenshot(resultFolder + "/batteryOptimization_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    @Test
    public void testClearAllNotifications() {
        clearAllNotifications();
    }

    @After
    public void afterTest() {
        super.afterTest();
    }


}
