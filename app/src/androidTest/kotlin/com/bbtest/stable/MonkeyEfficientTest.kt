package com.bbtest.stable;

import android.text.TextUtils;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.MonkeyCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * @Author: onuszhao
 * @Date: 2021/1/8 11:37
 * @Description: 写长线程跑，容易被系统杀掉，且加入电池优化策略也没啥用，故切换至5分钟跑一次
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class MonkeyEfficientTest extends MonkeyCommon {
    public File resultFolder = new File(rootFolder, "monkey");
    public File monkeyFile = new File(resultFolder, "monkey.txt");
    private File monkeyInfoFile = new File(downloadFile, "monkey.txt");

    private String pkgName = "";
    private String activity = "";
    // 定义场景
    private String scenes = "";
    // 定义是否坐标点击
    private boolean isPointClick = false;

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
        FileUtil.createFile(monkeyFile);
        // 获取需要跑的包相关信息
        String testInfo = FileUtil.readFile(monkeyInfoFile).trim();
        if (TextUtils.isEmpty(testInfo)) {
            pkgName = "com.transsion.phoenix";
            scenes = "qb://home/feeds";
        } else {
            String[] testInfoParts = testInfo.split(",");
            // 默认包名
            pkgName = testInfoParts[0];
            if (TextUtils.isEmpty(pkgName)) {
                pkgName = "com.transsion.phoenix";
            }
            // 默认场景：主页
            if (testInfoParts.length >= 2) {
                scenes = testInfoParts[1];
            } else {
                scenes = "qb://home/feeds";
            }
            // 默认坐标点击
            if (testInfoParts.length >= 3) {
                try {
                    isPointClick = Boolean.parseBoolean(testInfoParts[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // 获取activity(解决一个应用存在多个主activity)
        List<String> mainActivities = ShellCommon.getActivities(device, pkgName, null);
        for (String mainActivity : mainActivities) {
            ShellCommon.amStartApp(device, mainActivity, monkeyFile);
            CommonUtil.sleep(5000);
            if (!ShellCommon.isAppBackstage(device, pkgName)) {
                activity = mainActivity;
                break;
            }
        }
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  " + pkgName + "\n", monkeyFile);
    }

    @Test
    public void testMonkey() {
        scenes = "qb://mymusic"; // qb://camera,qb://bookmark,qb://download_add_link,qb://video/feedsvideo,qb://video/minivideo
        startMonkey(3);
    }

    @Test
    public void testMonkeyShort() {
        startMonkey(1);
    }

    @Test
    public void testMonkeyMedium() {
        startMonkey(3);
    }

    @Test
    public void testMonkeyLong() {
        startMonkey(5);
    }

    private void startMonkey(long minute) {
        try {
            /**
             * 初始化事件，控制概率
             */
            List<String> events = initRandomEvent(60, 10, 8, 10, 8, 0, 0, 4, 0, 0);

            // 进入指定场景
            gotoSscenes(scenes, monkeyFile);

            if (scenes.equals("notification")) {
                testNotification();
            } else {
                // 网页通过点击坐标测试
                if (scenes.startsWith("https://") || scenes.startsWith("http://")) {
                    isPointClick = true;
                }

                long startTime = System.currentTimeMillis();
                long endTime = 0;
                int preScenesNum = 0;
                while (true) {
                    //  先确认是否在前台
                    if (isAppBackstage(pkgName, monkeyFile)) {
                        //  再判断浏览器进程是否存在，并切到前台/启动浏览器
                        if (isProcessExist(pkgName, monkeyFile)) {
                            startActivity(1000, activity, monkeyFile);
                        } else {
                            startActivity(5000, activity, monkeyFile);
                        }
                    }

                    // 发起模拟事件
                    String event = events.get(new Random().nextInt(events.size()));
                    if (event.equals("click")) {
                        if (isPointClick) {
                            click(monkeyFile);
                        } else {
                            clickByUiObject(monkeyFile);
                        }
                    } else if (event.equals("swipeUp")) {
                        swipeUp(monkeyFile);
                    } else if (event.equals("swipeDown")) {
                        swipeDown(monkeyFile);
                    } else if (event.equals("swipeLeft")) {
                        swipeLeft(monkeyFile);
                    } else if (event.equals("swipeRight")) {
                        swipeRight(monkeyFile);
                    } else if (event.equals("longClick")) {
                        longClick(monkeyFile);
                    }

                    // 计算时间
                    endTime = System.currentTimeMillis();
                    long costTime = endTime - startTime;
                    if (costTime > minute * 60 * 1000) {
                        break;
                    } else {
                        int curScenesNum = 0;
                        if (isPointClick) {
                            curScenesNum = (int) (costTime / (10 * 1000));
                        } else {
                            curScenesNum = (int) (costTime / (20 * 1000));
                        }
                        if (curScenesNum > preScenesNum) {
                            // 进入指定场景
                            gotoSscenes(scenes, monkeyFile);
                            preScenesNum = curScenesNum;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testNotification() {
        // 常驻新闻通知
        openNotification();
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 residentNews = waitUiObject2ByRes("com.transsion.phoenix:id/news_frame", TIMEOUT_MEDIUM);
        if (residentNews != null) {
            swip(residentNews, "down");
            sleep(TIMEOUT_VERY_SHORT);
            UiObject2 newsPre = waitUiObject2ByRes("com.transsion.phoenix:id/news_pre", TIMEOUT_MEDIUM);
            if (newsPre != null) {
                newsPre.click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            UiObject2 newsNext = waitUiObject2ByRes("com.transsion.phoenix:id/news_next", TIMEOUT_MEDIUM);
            if (newsNext != null) {
                newsNext.click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            UiObject2 newsContent = waitUiObject2ByRes("com.transsion.phoenix:id/news_content", TIMEOUT_MEDIUM);
            if (newsContent != null) {
                newsContent.click();
                sleep(TIMEOUT_SHORT);
            }
        }
        // 常驻清理通知
        openNotification();
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 newWeatherContainer2 = waitUiObject2ByRes("com.transsion.phoenix:id/newWeatherContainer2", TIMEOUT_MEDIUM);
        if (newWeatherContainer2 != null) {
            newWeatherContainer2.click();
            sleep(TIMEOUT_SHORT);
        }
        openNotification();
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 cleanContainer = waitUiObject2ByRes("com.transsion.phoenix:id/cleanContainer", TIMEOUT_MEDIUM);
        if (cleanContainer != null) {
            cleanContainer.click();
            sleep(TIMEOUT_SHORT);
        }
        openNotification();
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 status = waitUiObject2ByRes("com.transsion.phoenix:id/tv_status_text", TIMEOUT_MEDIUM);
        if (status != null) {
            status.click();
            sleep(TIMEOUT_SHORT);
        }
        openNotification();
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 sticker = waitUiObject2ByRes("com.transsion.phoenix:id/layout_sticker_part_normal", TIMEOUT_MEDIUM);
        if (sticker != null) {
            sticker.click();
            sleep(TIMEOUT_SHORT);
        }
        // 清理通知
        openNotification();
        sleep(TIMEOUT_VERY_SHORT);
        UiObject2 fileCleanJunkInfo = waitUiObject2ByRes("com.transsion.phoenix:id/file_clean_tv_junk_info", TIMEOUT_MEDIUM);
        if (fileCleanJunkInfo != null) {
            fileCleanJunkInfo.click();
            sleep(TIMEOUT_SHORT);
        }
        // 退出通知栏
        back();
        sleep(TIMEOUT_VERY_SHORT);
        // 回到主页
        if (!isAppBackstage(pkgName, monkeyFile)) {
            backToHome();
        }
    }

    @After
    public void afterTest() {
        super.afterTest();
    }
}
