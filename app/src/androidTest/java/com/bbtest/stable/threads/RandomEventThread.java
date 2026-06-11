package com.bbtest.stable.threads;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.common.ShellCommon;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomEventThread extends Thread {

    private float runTime = -1;
    private String pkgName = "";
    private String activity = "";
    private UiDevice device = null;
    private int width = -1;
    private int height = -1;
    private File runLog;
    private boolean isCrash = false;
    private boolean isAnr = false;

    private String topActivity = "";

    public RandomEventThread(float runTime, String pkgName, String activity, UiDevice device, int width, int height, File runLog) {
        this.runTime = runTime;
        this.pkgName = pkgName;
        this.activity = activity;
        this.device = device;
        this.width = width;
        this.height = height;
        this.runLog = runLog;
    }

    public void run() {
        try {
            /**
             * 初始化事件，控制概率
             */
            List<String> events = new ArrayList<>();
            // 点击事件30%
            for (int i = 0; i < 30 * 2; i++) {
                events.add("click");
            }
            // 上滑事件12.5%
            for (int i = 0; i < 12.5 * 2; i++) {
                events.add("swipeUp");
            }
            // 下滑事件12.5%
            for (int i = 0; i < 12.5 * 2; i++) {
                events.add("swipeDown");
            }
            // 左滑事件12.5%
            for (int i = 0; i < 12.5 * 2; i++) {
                events.add("swipeLeft");
            }
            // 右滑事件12.5%
            for (int i = 0; i < 12.5 * 2; i++) {
                events.add("swipeRight");
            }
            // back事件10%
            for (int i = 0; i < 10 * 2; i++) {
                events.add("pressBack");
            }
            // home事件4%
            for (int i = 0; i < 4 * 2; i++) {
                events.add("pressHome");
            }
            // 长按事件3%
            for (int i = 0; i < 3 * 2; i++) {
                events.add("longClick");
            }
            // 输入事件2%
            for (int i = 0; i < 2 * 2; i++) {
                events.add("type");
            }
            // 拖动事件1%
            for (int i = 0; i < 1 * 2; i++) {
                events.add("drag");
            }
            // 打乱事件顺序
            Collections.shuffle(events);

            // 先杀掉所有后台进程
            ShellCommon.killAllApp(device, runLog);

            //启动浏览器(等待20s，保证已跳过闪屏)
            startActivity(30 * 1000);
            // 获取topActivity
            topActivity = ShellCommon.getTopActivity(device, runLog);

            //发送随机事件
            long startTime = System.currentTimeMillis();
            long endTime = 0;
            while (true) {
                // 如果crash或者anr，则杀掉进程(会吧anr弹窗也去掉)
                if (isAnr || isCrash) {
                    stopApp(3000);
                    isAnr = false;
                    isCrash = false;
                }

                //先确认是否在前台
                if (isAppBackstage()) {
                    //再判断浏览器进程是否存在，并切到前台/启动浏览器
                    if (isProcessExist()) {
                        startActivity(1000);
                    } else {
                        startActivity(5000);
                    }

                    //浏览器内弹窗，back返回
                    for (int i = 0; i < 3; i++) {
                        if (isAppBackstage()) {
                            pressBack();
                        } else {
                            break;
                        }
                    }
                }

                // 发起模拟事件
                String event = events.get(new Random().nextInt(events.size()));
                if (event.equals("click")) {
                    click();
                } else if (event.equals("swipeUp")) {
                    swipeUp();
                } else if (event.equals("swipeDown")) {
                    swipeDown();
                } else if (event.equals("swipeLeft")) {
                    swipeLeft();
                } else if (event.equals("swipeRight")) {
                    swipeRight();
                } else if (event.equals("drag")) {
                    drag();
                } else if (event.equals("longClick")) {
                    longClick();
                } else if (event.equals("type")) {
                    type();
                    dpadLeft();
                    dpadRight();
                    del();
                } else if (event.equals("pressHome")) {
                    pressHome();
                    startActivity(1000);
                } else if (event.equals("pressBack")) {
                    pressBack();
                }

                // 计算时间
                endTime = System.currentTimeMillis();
                long costTime = endTime - startTime;
                if (costTime > runTime * 60 * 60 * 1000) {
                    break;
                }
            }

            // 不关闭APP，否则dumpheap会报错
//            stopApp(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void click() {
        int x = width * CommonUtil.randomInt(0, 100) / 100;
        int y = height * CommonUtil.randomInt(8, 100) / 100;
        try {
            device.click(x, y);
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  click (" + x + "," + y + ")" + "\n", runLog);
            }
//            ShellCommon.click(device, x, y, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swipeUp() {
        int x = width * CommonUtil.randomInt(0, 100) / 100;
        int fromY = height * CommonUtil.randomInt(60, 100) / 100;
        int toY = height * CommonUtil.randomInt(8, 40) / 100;
        try {
            device.swipe(x, fromY, x, toY, 10);
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  swipe (" + x + "," + fromY + ") up to (" + x + "," + toY + ")" + "\n", runLog);
            }
//            ShellCommon.swipe(device, x, fromY, x, toY, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swipeDown() {
        int x = width * CommonUtil.randomInt(0, 100) / 100;
        int fromY = height * CommonUtil.randomInt(8, 40) / 100;
        int toY = height * CommonUtil.randomInt(60, 100) / 100;
        try {
            device.swipe(x, fromY, x, toY, 10);
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  swipe (" + x + "," + fromY + ") down to (" + x + "," + toY + ")" + "\n", runLog);
            }
//            ShellCommon.swipe(device, x, fromY, x, toY, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swipeLeft() {
        int y = height * CommonUtil.randomInt(8, 100) / 100;
        int fromX = width * CommonUtil.randomInt(60, 100) / 100;
        int toX = width * CommonUtil.randomInt(0, 40) / 100;
        try {
            device.swipe(fromX, y, toX, y, 10);
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  swipe (" + fromX + "," + y + ") left to (" + toX + "," + y + ")" + "\n", runLog);
            }
//            ShellCommon.swipe(device, fromX, y, toX, y, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swipeRight() {
        int y = height * CommonUtil.randomInt(8, 100) / 100;
        int fromX = width * CommonUtil.randomInt(0, 40) / 100;
        int toX = width * CommonUtil.randomInt(60, 100) / 100;
        try {
            device.swipe(fromX, y, toX, y, 10);
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  swipe (" + fromX + "," + y + ") right to (" + toX + "," + y + ")" + "\n", runLog);
            }
//            ShellCommon.swipe(device, fromX, y, toX, y, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drag() {
        int fromX = width * CommonUtil.randomInt(0, 100) / 100;
        int fromY = height * CommonUtil.randomInt(8, 100) / 100;
        int toX = width * CommonUtil.randomInt(0, 100) / 100;
        int toY = height * CommonUtil.randomInt(8, 100) / 100;
        try {
            device.drag(fromX, fromY, toX, toY, 10);
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  drag (" + fromX + "," + fromY + ") to (" + toX + "," + toY + ")" + "\n", runLog);
            }
//            ShellCommon.drag(device, fromX, fromY, toX, toY, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void longClick() {
        int x = width * CommonUtil.randomInt(0, 100) / 100;
        int y = height * CommonUtil.randomInt(8, 100) / 100;
        try {
            device.swipe(x, y, x, y, 300);
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  longClick (" + x + "," + y + ")" + "\n", runLog);
            }
//            ShellCommon.longClick(device, x, y, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void type() {
        try {
            ShellCommon.type(device, CommonUtil.randomStr(50), runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pressHome() {
        try {
            device.pressHome();
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  press home" + "\n", runLog);
            }
//            ShellCommon.pressHome(device, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pressBack() {
        try {
            device.pressBack();
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  press back" + "\n", runLog);
            }
//            ShellCommon.pressBack(device, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dpadLeft() {
        try {
            device.pressDPadLeft();
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  move the cursor to the left" + "\n", runLog);
            }
//            ShellCommon.dpadLeft(device, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dpadRight() {
        try {
            device.pressDPadRight();
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  move the cursor to the right" + "\n", runLog);
            }
//            ShellCommon.dpadRight(device, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void del() {
        try {
            device.pressDelete();
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  del text" + "\n", runLog);
            }
//            ShellCommon.del(device, runLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startActivity(long waitMills) {
        try {
            ShellCommon.amStartApp(device, activity, runLog);
            Thread.sleep(waitMills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopApp(long waitMills) {
        try {
            ShellCommon.forceStopApp(device, pkgName, runLog);
            Thread.sleep(waitMills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAppBackstage() {
        return ShellCommon.isAppBackstage(device, pkgName);
    }

    private boolean isProcessExist() {
        return ShellCommon.isProcessExist(device, pkgName);
    }


    public void setIsCrash(boolean isCrash) {
        this.isCrash = isCrash;
    }

    public void setIsAnr(boolean isAnr) {
        this.isAnr = isAnr;
    }

}


