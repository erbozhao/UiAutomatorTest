package com.bbtest.common;

import android.util.Log;

import androidx.test.uiautomator.UiObject2;

import org.junit.After;
import org.junit.Before;

import java.util.List;

/**
 * @author onuszhao
 */
public class PhxCommon extends BaseCommon {

    public final String pkgName = "com.transsion.phoenix";
    public final String activity = "com.transsion.phoenix/com.proj.sun.activity.LaunchActivity";
    public final String topActivity = "com.transsion.phoenix/com.tencent.mtt.MoreProcessMainActivity";

    @Before
    public void beforeTest() {
        super.beforeTest();
    }

    @After
    public void afterTest() {
        super.afterTest();
    }

    /**
     * 判断是否需要跳过闪屏
     */
    public boolean isNeedSkipSplash() {
        boolean isNeedSkipSplash = false;
        String splashActivity = "com.transsion.phoenix/com.tencent.mtt.BlockActivity";
        // 获取顶部Activity
        String topActivity = ShellCommon.getTopActivity(device, null);
        if (topActivity.equals(splashActivity)) {
            isNeedSkipSplash = true;
        }
        return isNeedSkipSplash;
    }

    /**
     * 判断第三方调用是否成功
     */
    public boolean isThirdCallSuccess(String thirdCallFile) {
        boolean isThirdCallSuccess = false;

        String thirdCallActivity0 = "";
        String thirdCallActivity1 = "";
        if (thirdCallFile.equals("webpage")) {
            thirdCallActivity0 = "com.transsion.phoenix/com.tencent.mtt.MoreProcessMainActivity";
        } else if (thirdCallFile.equals("video")) {
            thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phx.video.H5VideoThirdCallActivity";
        } else if (thirdCallFile.equals("music")) {
            thirdCallActivity0 = "com.transsion.phoenix/com.tencent.bang.music.ui.MusicPlayerActivity";
        } else if (thirdCallFile.equals("doc")) {
            thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.DocReaderActivity";
            thirdCallActivity1 = "com.transsion.phoenix/.reader.DocReaderActivity";
        } else if (thirdCallFile.equals("ppt")) {
            thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.PPTReaderActivity";
            thirdCallActivity1 = "com.transsion.phoenix/.reader.PPTReaderActivity";
        } else if (thirdCallFile.equals("xls")) {
            thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.XSLReaderActivity";
            thirdCallActivity1 = "com.transsion.phoenix/.reader.XSLReaderActivity";
        } else if (thirdCallFile.equals("pdf")) {
            thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.PDFReaderActivity";
            thirdCallActivity1 = "com.transsion.phoenix/.reader.PDFReaderActivity";
        } else if (thirdCallFile.equals("epub")) {
            thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.EPUBReaderActivity";
            thirdCallActivity1 = "com.transsion.phoenix/.reader.EPUBReaderActivity";
        } else if (thirdCallFile.equals("image")) {
            thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.ImageReaderActivity";
            thirdCallActivity1 = "com.transsion.phoenix/.reader.ImageReaderActivity";
        } else if (thirdCallFile.equals("zip")) {
            thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.ZipReaderActivity";
            thirdCallActivity1 = "com.transsion.phoenix/.reader.ZipReaderActivity";
        } else if (thirdCallFile.equals("txt")) {
            thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.TXTReaderActivity";
            thirdCallActivity1 = "com.transsion.phoenix/.reader.TXTReaderActivity";
        }

        // 获取顶部Activity
        String topActivity = ShellCommon.getTopActivity(device, null);
        if (topActivity.equals(thirdCallActivity0)) {
            isThirdCallSuccess = true;
        } else {
            if (!thirdCallActivity1.equals("") && topActivity.equals(thirdCallActivity1)) {
                isThirdCallSuccess = true;
            }
        }
        return isThirdCallSuccess;
    }

    /**
     * 跳过闪屏
     */
    public void skipSplash() {
        UiObject2 agreeStart = waitUiObject2ByText("Agree and Start", TIMEOUT_MEDIUM);
        if (agreeStart == null) {
            agreeStart = waitUiObject2ByText("أوافق، إبدأ الإستخدام", TIMEOUT_VERY_SHORT);
        }
        if (agreeStart != null) {
            agreeStart.click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 skip = waitUiObject2ByText("Skip", TIMEOUT_MEDIUM);
            if (skip != null) {
                skip.click();
                sleep(TIMEOUT_VERY_SHORT);
            } else {
                UiObject2 startNow = waitUiObject2ByText("Start Now", TIMEOUT_VERY_SHORT);
                if (startNow != null) {
                    startNow.click();
                    sleep(TIMEOUT_VERY_SHORT);
                }
            }
        }
    }

    /**
     * 跳过Files引导
     */
    public void skipFilesGuide() {
        UiObject2 files = waitUiObject2ByText("Files", TIMEOUT_MEDIUM);
        if (files == null) {
            files = waitUiObject2ByText("الملفّات", TIMEOUT_VERY_SHORT);
        }
        files.click();
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 continueFile = waitUiObject2ByRes("com.transsion.phoenix:id/continue_button", TIMEOUT_MEDIUM);
        if (continueFile != null) {
            continueFile.click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 switchBtn = waitUiObject2ByRes("android:id/switch_widget", TIMEOUT_MEDIUM);
            if (switchBtn != null && !switchBtn.isChecked()) {
                switchBtn.click();
                sleep(TIMEOUT_VERY_SHORT);
                backToHome();
            }
        }
        UiObject2 home = waitUiObject2ByText("Home", TIMEOUT_MEDIUM);
        if (home == null) {
            home = waitUiObject2ByText("الرئيسية", TIMEOUT_VERY_SHORT);
        }
        home.click();
        sleep(TIMEOUT_VERY_SHORT);
    }

    /**
     * 跳过Feeds上滑引导
     */
    public void skipFeedsGuide() {
        swip(0.5, 0.8, 0.5, 0.2);
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 home = waitUiObject2ByText("Home", TIMEOUT_MEDIUM);
        if (home == null) {
            home = waitUiObject2ByText("الرئيسية", TIMEOUT_VERY_SHORT);
        }
        home.click();
        sleep(TIMEOUT_VERY_SHORT);
    }

    /**
     * 跳过应用弹窗
     */
    public void skipAppDialog() {
        UiObject2 defaultBrowserDialog = waitUiObject2ByText("Continue", TIMEOUT_VERY_SHORT);
        if (defaultBrowserDialog != null) {
            back();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 rateUs = waitUiObject2ByText("Rate us 5 stars", TIMEOUT_VERY_SHORT);
        if (rateUs != null) {
            back();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 offline = waitUiObject2ByText("The network is not connected, check the offline content right now.", TIMEOUT_VERY_SHORT);
        if (offline != null) {
            back();
            sleep(TIMEOUT_VERY_SHORT);
        }
    }

    /**
     * 跳过其他弹窗
     */
    public void skipOtherDialog() {
        UiObject2 agree = waitUiObject2ByText("AGREE", TIMEOUT_VERY_SHORT);
        if (agree != null) {
            agree.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 accept = waitUiObject2ByText("Accept", TIMEOUT_VERY_SHORT);
        if (accept != null) {
            accept.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 allow = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT);
        if (allow != null) {
            allow.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 cancel = waitUiObject2ByText("取消", TIMEOUT_VERY_SHORT);
        if (cancel != null) {
            cancel.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
        UiObject2 ok = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT);
        if (ok != null) {
            ok.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
    }

    /**
     * 切换语言
     */
    public void switchLanguage(String country, String language) {
        try {
            UiObject2 me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM);
            if (me == null) {
                me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT);
            }
            if (me != null) {
                me.click();
            } else {
                waitUiObject2ByDesc("toolbar menu", TIMEOUT_VERY_SHORT).click();
            }
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 settings = waitUiObject2ByText("Settings", TIMEOUT_MEDIUM);
            if (settings == null) {
                settings = waitUiObject2ByText("الإعدادات", TIMEOUT_VERY_SHORT);
            }
            settings.click();
            sleep(TIMEOUT_VERY_SHORT);
            // 跳过弹窗
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
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 verDesc = waitUiObject2ByTextContains("Version", TIMEOUT_MEDIUM);
            if (verDesc == null) {
                verDesc = waitUiObject2ByTextContains("إصدار", TIMEOUT_VERY_SHORT);
            }
            for (int i = 0; i < 5; i++) {
                verDesc.click();
            }
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 locale = waitUiObject2ByText("Locale Setting", TIMEOUT_MEDIUM);
            if (locale == null) {
                locale = waitUiObject2ByText("Locale Test", TIMEOUT_VERY_SHORT);
            }
            if (locale == null) {
                UiObject2 phxIcon = getUiObject2s("android.widget.ImageView", true, 0.2, 0.5, 0.05, 0.5, 0.2, 0.8, 0.05, 0.5).get(0);
                for (int i = 0; i < 5; i++) {
                    phxIcon.click();
                }
                sleep(TIMEOUT_VERY_SHORT);
                locale = waitUiObject2ByText("Locale Setting", TIMEOUT_MEDIUM);
                if (locale == null) {
                    locale = waitUiObject2ByText("Locale Test", TIMEOUT_VERY_SHORT);
                }
            }
            locale.click();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 countryLanguage = waitUiObject2ByText(country + "-" + language, TIMEOUT_MEDIUM);
            if (countryLanguage != null) {
                countryLanguage.click();
                sleep(TIMEOUT_VERY_SHORT);
            } else {
                if (waitUiObject2ByText("Type to search country", TIMEOUT_MEDIUM) == null) {
                    swip(0.5, 0.8, 0.5, 0.2);
                    sleep(TIMEOUT_VERY_SHORT);
                    waitUiObject2ByText("Custom Setting", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_VERY_SHORT);
                }
                waitUiObject2ByText("Type to search country", TIMEOUT_MEDIUM).setText(country);
                sleep(TIMEOUT_VERY_SHORT);
                waitUiObject2ByTextContains(country + " |", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_VERY_SHORT);
                for (int i = 0; i < 10; i++) {
                    if (waitUiObject2ByText("About Phoenix", TIMEOUT_SHORT) == null && waitUiObject2ByText("حول Phoenix", TIMEOUT_VERY_SHORT) == null) {
                        back();
                        sleep(TIMEOUT_VERY_SHORT);
                    } else {
                        break;
                    }
                }
                sleep(TIMEOUT_VERY_SHORT);
                swip(0.5, 0.2, 0.5, 0.8);
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 languageSet = waitUiObject2ByText("Language", TIMEOUT_MEDIUM);
                if (languageSet == null) {
                    languageSet = waitUiObject2ByText("اللغة", TIMEOUT_VERY_SHORT);
                }
                languageSet.click();
                sleep(TIMEOUT_VERY_SHORT);
                if (language.equals("en")) {
                    waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "English", false).click();
                } else if (language.equals("fr")) {
                    waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "français", false).click();
                } else if (language.equals("ar")) {
                    waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "العربية", false).click();
                }
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 restart = waitUiObject2ByText("Restart", TIMEOUT_MEDIUM);
                if (restart == null) {
                    restart = waitUiObject2ByText("إعادة التشغيل", TIMEOUT_VERY_SHORT);
                }
                if (restart != null) {
                    restart.click();
                    sleep(TIMEOUT_LONG);
                } else {
                    backToHome();
                }
            }

            // 强制停止后再重启
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(TIMEOUT_SHORT);
            startApp(pkgName);
            sleep(TIMEOUT_LONG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchGrayEnv(boolean isGrayEnv) {
        try {
            UiObject2 me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM);
            if (me == null) {
                me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT);
            }
            if (me != null) {
                me.click();
            } else {
                waitUiObject2ByDesc("toolbar menu", TIMEOUT_VERY_SHORT).click();
            }
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 settings = waitUiObject2ByText("Settings", TIMEOUT_MEDIUM);
            if (settings == null) {
                settings = waitUiObject2ByText("الإعدادات", TIMEOUT_VERY_SHORT);
            }
            settings.click();
            sleep(TIMEOUT_VERY_SHORT);
            // 跳过弹窗
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
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 verDesc = waitUiObject2ByTextContains("Version", TIMEOUT_MEDIUM);
            if (verDesc == null) {
                verDesc = waitUiObject2ByTextContains("إصدار", TIMEOUT_VERY_SHORT);
            }
            for (int i = 0; i < 5; i++) {
                verDesc.click();
            }
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 locale = waitUiObject2ByText("Locale Setting", TIMEOUT_MEDIUM);
            if (locale == null) {
                locale = waitUiObject2ByText("Locale Test", TIMEOUT_VERY_SHORT);
            }
            if (locale == null) {
                UiObject2 phxIcon = getUiObject2s("android.widget.ImageView", true, 0.2, 0.5, 0.05, 0.5, 0.2, 0.8, 0.05, 0.5).get(0);
                for (int i = 0; i < 5; i++) {
                    phxIcon.click();
                }
                sleep(TIMEOUT_VERY_SHORT);
            }
            UiObject2 tupEnv = waitUiObject2ByText("TUP Environment", TIMEOUT_MEDIUM);
            if (tupEnv != null) {
                tupEnv.click();
                sleep(TIMEOUT_VERY_SHORT);
                if (isGrayEnv) {
                    UiObject2 preEnv = waitUiObject2ByText("Pre-production Environment", TIMEOUT_MEDIUM);
                    if (preEnv != null) {
                        preEnv.click();
                        sleep(TIMEOUT_VERY_SHORT);
                    }
                } else {
                    UiObject2 proEnv = waitUiObject2ByText("Production Environment", TIMEOUT_MEDIUM);
                    if (proEnv != null) {
                        proEnv.click();
                        sleep(TIMEOUT_VERY_SHORT);
                    }
                }
            }
            backToHome();
            backExitBrowser();

            // 强制停止后再重启
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(TIMEOUT_SHORT);
            startApp(pkgName);
            sleep(TIMEOUT_LONG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳过视频嗅探引导
     */
    public void skipSniffVideosGuide() {
        UiObject2 guide = waitUiObject2ByTextContains("Download videos from", TIMEOUT_MEDIUM);
        if (guide != null) {
            guide.click();
            sleep(TIMEOUT_VERY_SHORT);
        } else {
            UiObject2 facebook = waitUiObject2ByText("Facebook", TIMEOUT_MEDIUM);
            if (facebook == null) {
                facebook = waitUiObject2ByText("فيسبوك", TIMEOUT_VERY_SHORT);
            }
            if (facebook != null) {
                facebook.click();
                sleep(TIMEOUT_SHORT);
                back();
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 guide2 = waitUiObject2ByTextContains("Download videos from", TIMEOUT_MEDIUM);
                if (guide2 != null) {
                    guide2.click();
                    sleep(TIMEOUT_VERY_SHORT);
                }
            }
        }
    }

    /**
     * 保证回到APP界面(如应用anr弹窗遮挡、点击push跳转其他app等)
     */
    public void backToApp() {
        // 去掉Anr弹窗，避免遮挡
        UiObject2 anrDialog = waitUiObject2ByText("Close app", TIMEOUT_MEDIUM);
        if (anrDialog != null) {
            anrDialog.click();
            sleep(TIMEOUT_SHORT);
        }

        // 重启应用
        if (ShellCommon.isAppBackstage(device, pkgName)) {
            ShellCommon.amStartApp(device, activity, null);
            sleep(TIMEOUT_MEDIUM);
        }
    }

    /**
     * 保证返回到主页
     * 注：只判断扫一扫，当页面无此控件或者多层页面底层存在此控件时，无法实现返回首页
     */
    public void backToHome() {
        for (int i = 0; i < 30; i++) {
            // 先判断，若先返回容易出现弹窗界面去判断
            if (waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_SHORT) != null) {
                // 返回判断是否有退出弹窗
                back();
                sleep(TIMEOUT_VERY_SHORT);
                UiObject2 sureExit0 = waitUiObject2ByText("Exit Phoenix?", TIMEOUT_VERY_SHORT);
                if (sureExit0 == null) {
                    sureExit0 = waitUiObject2ByText("تأكيد الخروج الآن ؟", TIMEOUT_VERY_SHORT);
                }
                if (sureExit0 != null) {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                    break;
                }
                UiObject2 sureExit1 = waitUiObject2ByText("Do you have any difficulties While using Phoenix Browser？", TIMEOUT_VERY_SHORT);
                if (sureExit1 == null) {
                    sureExit1 = waitUiObject2ByText("هل واجهتط صعوبات أثناء رحلتك مع فينيكس", TIMEOUT_VERY_SHORT);
                }
                if (sureExit1 != null) {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                    break;
                }
                UiObject2 clearExit0 = waitUiObject2ByText("Clear history on exit?", TIMEOUT_VERY_SHORT);
                if (clearExit0 != null) {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                    break;
                }
                UiObject2 clearExit1 = waitUiObject2ByText("Clear History When Exit ?", TIMEOUT_VERY_SHORT);
                if (clearExit1 != null) {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                    break;
                }
            } else {
                if (i > 8) {
                    UiObject2 dialog = waitUiObject2ByText("OK", TIMEOUT_SHORT);
                    if (dialog == null) {
                        dialog = waitUiObject2ByText("موافق", TIMEOUT_VERY_SHORT);
                    }
                    if (dialog != null) {
                        dialog.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    UiObject2 yesDialog = waitUiObject2ByText("Yes", TIMEOUT_VERY_SHORT);
                    if (yesDialog == null) {
                        yesDialog = waitUiObject2ByText("نعم", TIMEOUT_VERY_SHORT);
                    }
                    if (yesDialog != null) {
                        yesDialog.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    UiObject2 continueDialog = waitUiObject2ByText("Continue", TIMEOUT_VERY_SHORT);
                    if (continueDialog == null) {
                        continueDialog = waitUiObject2ByText("متابعة", TIMEOUT_VERY_SHORT);
                    }
                    if (continueDialog != null) {
                        continueDialog.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    UiObject2 allowDialog = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT);
                    if (allowDialog == null) {
                        allowDialog = waitUiObject2ByText("السماح", TIMEOUT_VERY_SHORT);
                    }
                    if (allowDialog != null) {
                        allowDialog.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    UiObject2 customDialog = waitUiObject2ByText("You can customize your news feeds in just two steps", TIMEOUT_VERY_SHORT);
                    if (customDialog == null) {
                        customDialog = waitUiObject2ByText("يمكنك تخصيص موجز الأخبار الخاص بك فى خطوتين", TIMEOUT_VERY_SHORT);
                    }
                    if (customDialog != null) {
                        getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 1, 0.1, 0.5).get(0).click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    UiObject2 skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/closeButton", TIMEOUT_VERY_SHORT);
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/close", TIMEOUT_VERY_SHORT);
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/trybuttom", TIMEOUT_VERY_SHORT);
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByText("Try", TIMEOUT_VERY_SHORT);
                    }
                    if (skipButton == null) {
                        skipButton = waitUiObject2ByText("جَرّبه الآن", TIMEOUT_VERY_SHORT);
                    }
                    if (skipButton != null) {
                        skipButton.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        continue;
                    }
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                } else {
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                }
            }
        }
    }

    /**
     * 退出浏览器
     */
    public void menuExitBrowser() {
        UiObject2 me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM);
        if (me == null) {
            me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT);
        }
        if (me != null) {
            me.click();
        } else {
            waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
        }
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 exit = waitUiObject2ByText("Exit", TIMEOUT_MEDIUM);
        if (exit == null) {
            exit = waitUiObject2ByText("خروج", TIMEOUT_VERY_SHORT);
        }
        exit.click();
        sleep(TIMEOUT_SHORT);
    }

    /**
     * Back退出浏览器
     */
    public boolean backExitBrowser() {
        boolean isBackExitSuccess = false;
        String topActivity = ShellCommon.getTopActivity(device, null);
        for (int i = 0; i < 10; i++) {
            back();
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 exit = waitUiObject2ByText("Exit", TIMEOUT_SHORT);
            if (exit == null) {
                exit = waitUiObject2ByText("خروج", TIMEOUT_VERY_SHORT);
            }
            if (exit != null) {
                exit.click();
            } else {
                back();
            }
            sleep(TIMEOUT_SHORT);
            String curActivity = ShellCommon.getTopActivity(device, null);
            if (!curActivity.equals(topActivity)) {
                isBackExitSuccess = true;
                break;
            }
        }
        return isBackExitSuccess;
    }

    /**
     * 点击搜索框
     */
    public void clickSearchBox(boolean isOpenedPage) {
        List<UiObject2> searchBoxs = null;
        if (isOpenedPage) {
            searchBoxs = getUiObject2sByChildClazz("android.widget.LinearLayout", true, "android.widget.TextView", 0.5, 1, 0.04, 0.5, 0, 1, 0.3, 0.7);
        } else {
            searchBoxs = getUiObject2s("android.widget.TextSwitcher", false, 0.5, 1, 0.01, 0.5, 0, 1, 0.3, 0.7);
            if (searchBoxs == null || searchBoxs.size() == 0) {
                searchBoxs = getUiObject2s("android.widget.TextView", false, 0.5, 1, 0.01, 0.5, 0, 1, 0.01, 0.5);
            }
        }
        searchBoxs.get(0).click();
        sleep(TIMEOUT_VERY_SHORT);
    }

    /**
     * 搜索框输入文字
     */
    public void setTextAndGo(String text) {
        List<UiObject2> searchBoxs = getUiObject2s("android.widget.EditText", true, 0.5, 1, 0.01, 0.5, 0, 1, 0.02, 0.4);
        searchBoxs.get(0).setText(text);
        sleep(TIMEOUT_VERY_SHORT);
        List<UiObject2> goSearch = getUiObject2s("android.widget.ImageView", true, 0.01, 0.3, 0.01, 0.3, 0.5, 1, 0, 0.3);
        goSearch.get(1).click();
        sleep(TIMEOUT_VERY_SHORT);
//        UiObject2 go = waitUiObject2ByDesc("addressbar go", TIMEOUT_MEDIUM);
//        if (go == null) {
//            go = waitUiObject2ByDesc("addressbar search", TIMEOUT_SHORT);
//        }
//        go.click();
//        sleep(TIMEOUT_VERY_SHORT);
    }

    /**
     * 切换指定Feeds Tab
     */
    public void switchFeedsTab(String tabName) {
        getUiObject2s("android.widget.FrameLayout", true, 0, 0.2, 0, 0.2, 0.8, 1, 0.1, 0.6).get(0).click();
        sleep(TIMEOUT_SHORT);
        getUiObject2ByChildText("android.widget.FrameLayout", true, tabName, "android.widget.TextView").click();
        UiObject2 done = waitUiObject2ByText("Done", TIMEOUT_SHORT);
        if (done != null) {
            done.click();
            sleep(TIMEOUT_VERY_SHORT);
            getUiObject2ByChildText("android.widget.FrameLayout", true, tabName, "android.widget.TextView").click();
        }
        sleep(TIMEOUT_MEDIUM);
    }

    public void clickFeedsNews() {
        List<UiObject2> news = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.1, 0.3, 0, 1, 0.02, 0.8);
        if (news == null || news.size() == 0) {
            for (int i = 0; i < 3; i++) {
                swip(0.5, 0.7, 0.5, 0.3);
                sleep(TIMEOUT_MEDIUM);
                news = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.1, 0.3, 0, 1, 0.02, 0.8);
                if (news != null) {
                    break;
                }
            }
        }
        news.get(0).click();
        sleep(TIMEOUT_VERY_SHORT);
    }

    public void clickFeedsVideo() {
        UiObject2 firstVideo = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.2, 0.8, 0, 1, 0.02, 0.9).get(0);
        UiObject2 firstVideoBottom = getChildUiObject2(firstVideo, false, "android.widget.LinearLayout", 0.8, 1, 0, 0.1, 0, 1, 0, 1, false);
        firstVideoBottom.click();
        sleep(TIMEOUT_VERY_SHORT);
    }

    public void clickFeedsMiniVideo() {
        getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.8, 0, 1, 0.02, 0.9).get(0).click();
        sleep(TIMEOUT_SHORT);
        UiObject2 swipeToast = waitUiObject2ByText("Swipe up for more", TIMEOUT_SHORT);
        if (swipeToast != null) {
            swipeToast.click();
            sleep(TIMEOUT_VERY_SHORT);
        }
    }

    public String getScrollableClazz() {
        String scrollableClazz = "";
        if (getScrollableUiObject2("androidx.recyclerview.widget.RecyclerView") != null) {
            scrollableClazz = "androidx.recyclerview.widget.RecyclerView";
        } else if (getScrollableUiObject2("android.support.v7.widget.RecyclerView") != null) {
            scrollableClazz = "android.support.v7.widget.RecyclerView";
        } else if (getScrollableUiObject2("android.webkit.WebView") != null) {
            scrollableClazz = "android.webkit.WebView";
        } else if (getScrollableUiObject2("android.widget.ListView") != null) {
            scrollableClazz = "android.widget.ListView";
        } else if (getScrollableUiObject2("android.view.ViewGroup") != null) {
            scrollableClazz = "android.view.ViewGroup";
        } else {
            List<UiObject2> uiObject2s = getAllUiObject2s(getRootObject());
            for (UiObject2 uiObject2 : uiObject2s) {
                if (uiObject2.isScrollable()) {
                    scrollableClazz = uiObject2.getClassName();
                    break;
                }
            }
        }
        return scrollableClazz;
    }

    public void closeAdDialog() {
        UiObject2 closeBtn = waitUiObject2ByRes("close-button-container", TIMEOUT_MEDIUM);
        if (closeBtn == null) {
            closeBtn = waitUiObject2ByText("CLOSE", TIMEOUT_VERY_SHORT);
        }
        if (closeBtn != null) {
            // 处理uiautomator有时点击时会报异常
            try {
                closeBtn.click();
            } catch (Exception e) {
                ShellCommon.pressHome(device, null);
                sleep(TIMEOUT_SHORT);
                ShellCommon.amStartApp(device, activity, null);
            }
            sleep(TIMEOUT_SHORT);
        } else {
            List<UiObject2> closeBtns = getUiObject2s("android.widget.Button", true, 0.05, 0.3, 0.05, 0.3, 0, 1, 0, 0.3);
            if (closeBtns == null || closeBtns.size() == 0) {
                closeBtns = getUiObject2s("android.widget.ImageButton", true, 0.05, 0.3, 0.05, 0.3, 0, 1, 0, 0.3);
            }
            if (closeBtns != null && closeBtns.size() > 0) {
                // 处理uiautomator有时点击时会报异常
                try {
                    closeBtns.get(0).click();
                } catch (Exception e) {
                    ShellCommon.pressHome(device, null);
                    sleep(TIMEOUT_SHORT);
                    ShellCommon.amStartApp(device, activity, null);
                }
                sleep(TIMEOUT_SHORT);
            }
        }
    }
}
