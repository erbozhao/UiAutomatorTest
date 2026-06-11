package com.bbtest.common;

import androidx.test.uiautomator.UiObject2;

import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.ShellCommand;

import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @Author: onuszhao
 * @Date: 2021-10-13 18:50
 * @Description:
 */
public class MonkeyCommon extends BaseCommon {

    // 设置Y初始化高度，排除通知栏
    private static int startY = 10;

    @Before
    public void beforeTest() {
        super.beforeTest();
    }

    @After
    public void afterTest() {
        super.afterTest();
    }

    public UiObject2 gotoHomeTool(String pkgName, String activity, File logFile) {
        //  先确认是否在前台
        if (isAppBackstage(pkgName, logFile)) {
            //  再判断浏览器进程是否存在，并切到前台/启动浏览器
            if (isProcessExist(pkgName, logFile)) {
                startActivity(1000, activity, logFile);
            } else {
                startActivity(5000, activity, logFile);
            }
        }

        // 先回到主页
        backToHome();

        // 从主页进入指定场景
        UiObject2 tools = waitUiObject2ByText("Tools", TIMEOUT_MEDIUM);
        if (tools == null) {
            tools = waitUiObject2ByText("أدوات", TIMEOUT_VERY_SHORT);
        }
        if (tools == null) {
            UiObject2 me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM);
            if (me == null) {
                me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT);
            }
            me.click();
            UiObject2 settings = waitUiObject2ByText("Settings", TIMEOUT_MEDIUM);
            if (settings == null) {
                settings = waitUiObject2ByText("الإعدادات", TIMEOUT_VERY_SHORT);
            }
            settings.click();
            UiObject2 homepage = waitUiObject2ByText("Homepage", TIMEOUT_MEDIUM);
            if (homepage == null) {
                homepage = waitUiObject2ByText("الصفحة الرئيسية", TIMEOUT_VERY_SHORT);
            }
            homepage.click();
            UiObject2 homeSwitch = waitUiObject2sByClazz("android.widget.Switch", TIMEOUT_MEDIUM).get(0);
            if (homeSwitch.isChecked()) {
                homeSwitch.click();
            }
            backToHome();
        }

        UiObject2 home = waitUiObject2ByText("Home", TIMEOUT_MEDIUM);
        if (home != null) {
            home.click();
        }
        return home;
    }

    public void gotoSscenes(String scenes, File logFile) {
        if (scenes.equals("notification")) {
            // 亮屏
            wakeUp();
            sleep(TIMEOUT_SHORT);
        } else {
            // 通过qb地址定位场景，如qb://home/files
            String cmd = "am start -a android.intent.action.VIEW -p com.transsion.phoenix -d " + scenes;
            ShellCommand.execCmdByUiDevice(device, cmd);
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  " + cmd + "\n", logFile);
        }
    }

    public UiObject2 gotoNovel(String pkgName, String activity, File logFile) {
        //  先确认是否在前台
        if (isAppBackstage(pkgName, logFile)) {
            //  再判断浏览器进程是否存在，并切到前台/启动浏览器
            if (isProcessExist(pkgName, logFile)) {
                startActivity(1000, activity, logFile);
            } else {
                startActivity(5000, activity, logFile);
            }
        }

        // 先回到主页
        backToHome();

        // 从主页进入指定场景
        UiObject2 me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM);
        if (me == null) {
            me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT);
        }
        me.click();

        UiObject2 novel = waitUiObject2ByRes("com.transsion.phoenix:id/it_novel_more", TIMEOUT_MEDIUM);
        if (novel != null) {
            novel.click();
        }
        return novel;
    }

    public void backToHome() {
        for (int i = 0; i < 30; i++) {
            // 先判断，若先返回容易出现弹窗界面去判断
            if (waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_SHORT) != null) {
                // 返回判断是否有退出弹窗
                back();
                UiObject2 sureExit0 = waitUiObject2ByText("Sure to exit now?", TIMEOUT_SHORT);
                if (sureExit0 == null) {
                    sureExit0 = waitUiObject2ByText("تأكيد الخروج الآن ؟", TIMEOUT_VERY_SHORT);
                }
                if (sureExit0 != null) {
                    back();
                    break;
                }
                UiObject2 sureExit1 = waitUiObject2ByText("Do you have any difficulties While using Phoenix Browser？", TIMEOUT_VERY_SHORT);
                if (sureExit1 == null) {
                    sureExit1 = waitUiObject2ByText("هل واجهتط صعوبات أثناء رحلتك مع فينيكس", TIMEOUT_VERY_SHORT);
                }
                if (sureExit1 != null) {
                    back();
                    break;
                }
                UiObject2 clearExit0 = waitUiObject2ByText("Clear history on exit?", TIMEOUT_VERY_SHORT);
                if (clearExit0 != null) {
                    back();
                    break;
                }
                UiObject2 clearExit1 = waitUiObject2ByText("Clear History When Exit ?", TIMEOUT_VERY_SHORT);
                if (clearExit1 != null) {
                    back();
                    break;
                }
            } else {
                if (i > 8) {
                    UiObject2 dialog = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT);
                    if (dialog == null) {
                        dialog = waitUiObject2ByText("موافق", TIMEOUT_VERY_SHORT);
                    }
                    if (dialog != null) {
                        dialog.click();
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
                        continue;
                    }
                    back();
                } else {
                    back();
                }
            }
        }
    }

    public void skipDialog() {
        UiObject2 cancel = waitUiObject2ByText("Cancel", TIMEOUT_VERY_SHORT);
        if (cancel != null) {
            cancel.click();
            return;
        }
        UiObject2 ok = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT);
        if (ok != null) {
            ok.click();
            return;
        }

        UiObject2 deny = waitUiObject2ByText("Deny", TIMEOUT_VERY_SHORT);
        if (deny != null) {
            deny.click();
            return;
        }
        UiObject2 accept = waitUiObject2ByText("Accept", TIMEOUT_VERY_SHORT);
        if (accept != null) {
            accept.click();
            return;
        }
        UiObject2 agree = waitUiObject2ByText("AGREE", TIMEOUT_VERY_SHORT);
        if (agree != null) {
            agree.click();
            return;
        }
        UiObject2 allow = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT);
        if (allow != null) {
            allow.click();
            return;
        }
    }

    public List<String> initRandomEvent(int clickRate, int swipeUpRate, int swipeDownRate, int swipeLeftRate, int swipeRightRate, int pressBackRate, int pressHomeRate, int longClickRate, int typeRate, int dragRate) {
        List<String> events = new ArrayList<>();
        // 点击事件
        for (int i = 0; i < clickRate * 2; i++) {
            events.add("click");
        }
        // 上滑事件
        for (int i = 0; i < swipeUpRate * 2; i++) {
            events.add("swipeUp");
        }
        // 下滑事件
        for (int i = 0; i < swipeDownRate * 2; i++) {
            events.add("swipeDown");
        }
        // 左滑事件
        for (int i = 0; i < swipeLeftRate * 2; i++) {
            events.add("swipeLeft");
        }
        // 右滑事件
        for (int i = 0; i < swipeRightRate * 2; i++) {
            events.add("swipeRight");
        }
        // back事件
        for (int i = 0; i < pressBackRate * 2; i++) {
            events.add("pressBack");
        }
        // home事件
        for (int i = 0; i < pressHomeRate * 2; i++) {
            events.add("pressHome");
        }
        // 长按事件
        for (int i = 0; i < longClickRate * 2; i++) {
            events.add("longClick");
        }
        // 输入事件
        for (int i = 0; i < typeRate * 2; i++) {
            events.add("type");
        }
        // 拖动事件
        for (int i = 0; i < dragRate * 2; i++) {
            events.add("drag");
        }
        // 打乱事件顺序
        Collections.shuffle(events);
        return events;
    }

    public void clickByUiObject(File logFile) {
        boolean isClickError = false;
        try {
            List<UiObject2> clickableUiObject2s = getClickableUiObject2s(getRootObject(), new ArrayList<UiObject2>());
            int clickIndeex = new Random().nextInt(clickableUiObject2s.size());
            UiObject2 clickUiObject2s = clickableUiObject2s.get(clickIndeex);
            if (clickUiObject2s != null) {
                clickUiObject2s.click();
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  click " + clickUiObject2s.getVisibleBounds().toString() + "\n", logFile);
            } else {
                isClickError = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isClickError = true;
        }
        if (isClickError) {
            click(logFile);
        }
    }

    public void click(File logFile) {
        int x = width * CommonUtil.randomInt(0, 100) / 100;
        int y = height * CommonUtil.randomInt(startY, 100) / 100;
        try {
            device.click(x, y);
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  click (" + x + "," + y + ")" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void swipeUp(File logFile) {
        int x = width * CommonUtil.randomInt(0, 100) / 100;
        int fromY = height * CommonUtil.randomInt(60, 100) / 100;
        int toY = height * CommonUtil.randomInt(startY, 40) / 100;
        try {
            device.swipe(x, fromY, x, toY, 10);
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  swipe (" + x + "," + fromY + ") up to (" + x + "," + toY + ")" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void swipeDown(File logFile) {
        int x = width * CommonUtil.randomInt(0, 100) / 100;
        int fromY = height * CommonUtil.randomInt(startY, 40) / 100;
        int toY = height * CommonUtil.randomInt(60, 100) / 100;
        try {
            device.swipe(x, fromY, x, toY, 10);
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  swipe (" + x + "," + fromY + ") down to (" + x + "," + toY + ")" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void swipeLeft(File logFile) {
        int y = height * CommonUtil.randomInt(startY, 100) / 100;
        int fromX = width * CommonUtil.randomInt(60, 100) / 100;
        int toX = width * CommonUtil.randomInt(0, 40) / 100;
        try {
            device.swipe(fromX, y, toX, y, 10);
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  swipe (" + fromX + "," + y + ") left to (" + toX + "," + y + ")" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void swipeRight(File logFile) {
        int y = height * CommonUtil.randomInt(startY, 100) / 100;
        int fromX = width * CommonUtil.randomInt(0, 40) / 100;
        int toX = width * CommonUtil.randomInt(60, 100) / 100;
        try {
            device.swipe(fromX, y, toX, y, 10);
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  swipe (" + fromX + "," + y + ") right to (" + toX + "," + y + ")" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drag(File logFile) {
        int fromX = width * CommonUtil.randomInt(0, 100) / 100;
        int fromY = height * CommonUtil.randomInt(startY, 100) / 100;
        int toX = width * CommonUtil.randomInt(0, 100) / 100;
        int toY = height * CommonUtil.randomInt(8, 100) / 100;
        try {
            device.drag(fromX, fromY, toX, toY, 10);
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  drag (" + fromX + "," + fromY + ") to (" + toX + "," + toY + ")" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void longClick(File logFile) {
        int x = width * CommonUtil.randomInt(0, 100) / 100;
        int y = height * CommonUtil.randomInt(startY, 100) / 100;
        try {
            device.swipe(x, y, x, y, 300);
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  longClick (" + x + "," + y + ")" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void type(File logFile) {
        try {
            ShellCommon.type(device, CommonUtil.randomStr(50), logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pressHome(File logFile) {
        try {
            device.pressHome();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  press home" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pressBack(File logFile) {
        try {
            device.pressBack();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  press back" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dpadLeft(File logFile) {
        try {
            device.pressDPadLeft();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  move the cursor to the left" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dpadRight(File logFile) {
        try {
            device.pressDPadRight();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  move the cursor to the right" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void del(File logFile) {
        try {
            device.pressDelete();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  del text" + "\n", logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startActivity(long waitMills, String activity, File logFile) {
        try {
            ShellCommon.amStartApp(device, activity, logFile);
            Thread.sleep(waitMills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAppBackstage(String pkgName, File logFile) {
        boolean isAppBackstage = ShellCommon.isAppBackstage(device, pkgName);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  isAppBackstage=" + isAppBackstage + "\n", logFile);
        return isAppBackstage;
    }

    public boolean isProcessExist(String pkgName, File logFile) {
        boolean isProcessExist = ShellCommon.isProcessExist(device, pkgName);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  isProcessExist=" + isProcessExist + "\n", logFile);
        return isProcessExist;
    }
}
