package com.bbtest.other.monitor;

import android.os.Build;

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
import java.util.List;

public class DurationTest extends PhxCommon {
    private File resultFolder = new File(rootFolder, "monitor");
    private File durationFile = new File(resultFolder, "duration.txt");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
        FileUtil.createFile(durationFile);
    }

    @After
    public void afterTest() {
        super.afterTest();
    }

    @Test
    public void testHome() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testHome ******************" + "\n", durationFile);
        long startTime = 0;
        long endTime = 0;
        try {
            startTime = System.currentTimeMillis();
            startApp(pkgName);
            sleep(TIMEOUT_LONG);
            waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
            endTime = System.currentTimeMillis();
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }

        // 获取时长
        double testTime = getTestTime(startTime, endTime);
        double countTime = getCountTime("end|home|main");
        FileUtil.writeStrToFile("场景:主页,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testHome ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testWeather() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testWeather ******************" + "\n", durationFile);
        long startTime = 0;
        long endTime = 0;
        try {
            List<UiObject2> weathers = getUiObject2s("android.widget.LinearLayout", true, 0, 0.4, 0, 0.2, 0, 0.4, 0.02, 0.2);
            if (weathers == null || weathers.size() == 0) {
                backToHome();
                sleep(TIMEOUT_LONG);
                weathers = getUiObject2s("android.widget.LinearLayout", true, 0, 0.4, 0, 0.2, 0, 0.4, 0.02, 0.2);
            }
            if (weathers != null && weathers.size() > 0) {
                startTime = System.currentTimeMillis();
                weathers.get(0).click();
                sleep(TIMEOUT_LONG);
                waitUiObject2ByText("Air quality", TIMEOUT_MEDIUM);
                back();
                endTime = System.currentTimeMillis();
                sleep(TIMEOUT_MEDIUM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }

        // 获取时长
        double testTime = getTestTime(startTime, endTime);
        double countTime = getCountTime("end|weather");
        FileUtil.writeStrToFile("场景:天气,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testWeather ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testSearch() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testSearch ******************" + "\n", durationFile);
        long startTime = 0;
        long endTime = 0;
        try {
            startTime = System.currentTimeMillis();
            clickSearchBox(false);
            sleep(TIMEOUT_MEDIUM);
            back();
            endTime = System.currentTimeMillis();
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }

        // 获取时长
        double testTime = getTestTime(startTime, endTime);
        double countTime = getCountTime("end|search");
        FileUtil.writeStrToFile("场景:搜索,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testSearch ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testBrowser() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testBrowser ******************" + "\n", durationFile);
        long startTime = 0;
        long endTime = 0;
        try {
            startTime = System.currentTimeMillis();
            ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com");
            sleep(TIMEOUT_MEDIUM);
            back();
            endTime = System.currentTimeMillis();
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }

        // 获取时长
        double testTime = getTestTime(startTime, endTime);
        double countTime = getCountTime("end|browser");
        FileUtil.writeStrToFile("场景:浏览,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testBrowser ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testCamera() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testCamera ******************" + "\n", durationFile);
        long startTime = 0;
        long endTime = 0;
        try {
            startTime = System.currentTimeMillis();
            waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_MEDIUM);
            back();
            endTime = System.currentTimeMillis();
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }

        // 获取时长
        double testTime = getTestTime(startTime, endTime);
        double countTime = getCountTime("end|camera");
        FileUtil.writeStrToFile("场景:扫一扫,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testCamera ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFeedsForyou() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFeedsForyou ******************" + "\n", durationFile);
        double testTime = testFeedsTab("For you");

        // 获取时长
        double countTime = getCountTime("end|home|130001");
        FileUtil.writeStrToFile("场景:Feeds-For you-Tab,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFeedsForyou ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFeedsForyouNews() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFeedsForyouNews ******************" + "\n", durationFile);
        double testTime = testFeedsNews("For you");

        // 获取时长
        double countTime = getCountTime("end|news|130001");
        FileUtil.writeStrToFile("场景:Feeds-For you-News,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFeedsForyouNews ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFeedsShortVideo() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFeedsShortVideo ******************" + "\n", durationFile);
        double testTime = testFeedsTab("Short Video");

        // 获取时长
        double countTime = getCountTime("end|home|150006");
        FileUtil.writeStrToFile("场景:Feeds-Short Video-Tab,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFeedsShortVideo ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFeedsShortVideoDetail() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFeedsShortVideoDetail ******************" + "\n", durationFile);
        double testTime = testFeedsMiniVideo("Short Video");

        // 获取时长
        double countTime = getCountTime("end|minivideo|150006");
        FileUtil.writeStrToFile("场景:Feeds-Short Video-Detail,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFeedsShortVideoDetail ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFeedsVideo() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFeedsVideo ******************" + "\n", durationFile);
        double testTime = testFeedsTab("Video");

        // 获取上报时长
        double countTime = getCountTime("end|home|130008");
        FileUtil.writeStrToFile("场景:Feeds-Video-Tab,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFeedsVideo ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFeedsVideoDetail() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFeedsVideoDetail ******************" + "\n", durationFile);
        double testTime = testFeedsVideo("Video");

        // 获取时长
        double countTime = getCountTime("end|feedsvideo_detail|130008");
        FileUtil.writeStrToFile("场景:Feeds-Video-Detail,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFeedsVideoDetail ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFeedsHotGirl() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFeedsHotGirl ******************" + "\n", durationFile);
        double testTime = testFeedsTab("Hot Girl");

        // 获取时长
        double countTime = getCountTime("end|home|130027");
        FileUtil.writeStrToFile("场景:Feeds-Hot Girl-Tab,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFeedsHotGirl ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFeedsHotGirlDetail() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFeedsHotGirlDetail ******************" + "\n", durationFile);
        double testTime = testFeedsImg("Hot Girl");

        // 获取时长
        double countTime = getCountTime("end|image_detail|130027");
        FileUtil.writeStrToFile("场景:Feeds-Hot Girl-Detail,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFeedsHotGirlDetail ******************" + "\n", durationFile);
        backToHome();
    }


    @Test
    public void testDownload() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testDownload ******************" + "\n", durationFile);
        long startTime = 0;
        long endTime = 0;
        try {
            startTime = System.currentTimeMillis();
            waitUiObject2ByText("Downloads", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_MEDIUM);
            back();
            endTime = System.currentTimeMillis();
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }

        // 获取时长
        double testTime = getTestTime(startTime, endTime);
        double countTime = getCountTime("end|download");
        FileUtil.writeStrToFile("场景:下载,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testDownload ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testMe() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testMe ******************" + "\n", durationFile);
        double testTime = testMeItems("Me");

        // 获取时长
        double countTime = getCountTime("end|user_center|main");
        FileUtil.writeStrToFile("场景:个人中心,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testMe ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testMeUser() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testMeUser ******************" + "\n", durationFile);
        double testTime = testMeItems("User");

        // 获取时长
        double countTime = getCountTime("end|user_center|user");
        FileUtil.writeStrToFile("场景:个人中心-用户,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testMeUser ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testMeMsg() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testMeMsg ******************" + "\n", durationFile);
        double testTime = testMeItems("Msg");

        // 获取时长
        double countTime = getCountTime("end|user_center|message_center");
        FileUtil.writeStrToFile("场景:个人中心-消息,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testMeMsg ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testMeBookmarks() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testMeBookmarks ******************" + "\n", durationFile);
        double testTime = testMeItems("Bookmarks");

        // 获取时长
        double countTime = getCountTime("end|user_center|bookmark");
        FileUtil.writeStrToFile("场景:个人中心-书签,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testMeBookmarks ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testMeHistory() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testMeHistory ******************" + "\n", durationFile);
        double testTime = testMeItems("History");

        // 获取时长
        double countTime = getCountTime("end|user_center|history");
        FileUtil.writeStrToFile("场景:个人中心-历史,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testMeHistory ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testMeFavorites() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testMeFavorites ******************" + "\n", durationFile);
        double testTime = testMeItems("Favorites");

        // 获取时长
        double countTime = getCountTime("end|user_center|favorites");
        FileUtil.writeStrToFile("场景:个人中心-收藏,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testMeFavorites ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testMeMyVideo() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testMeMyVideo ******************" + "\n", durationFile);
        double testTime = testMeItems("My Video");

        // 获取时长
        double countTime = getCountTime("end|user_center|myvideo");
        FileUtil.writeStrToFile("场景:个人中心-我的视频,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testMeMyVideo ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testMeMyMusic() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testMeMyMusic ******************" + "\n", durationFile);
        double testTime = testMeItems("My Music");

        // 获取时长
        double countTime = getCountTime("end|user_center|mymusic");
        FileUtil.writeStrToFile("场景:个人中心-我的音乐,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testMeMyMusic ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testMeAdsBlocked() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testMeAdsBlocked ******************" + "\n", durationFile);
        double testTime = testMeItems("Adblocker");

        // 获取时长
        double countTime = getCountTime("end|settings|adblock");
        FileUtil.writeStrToFile("场景:个人中心-广告过滤,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testMeAdsBlocked ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testMeSettings() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testMeSettings ******************" + "\n", durationFile);
        double testTime = testMeItems("Settings");

        // 获取时长
        double countTime = getCountTime("end|settings|null");
        FileUtil.writeStrToFile("场景:个人中心-设置,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testMeSettings ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFiles() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFiles ******************" + "\n", durationFile);
        double testTime = testFilesItems("Files");

        // 获取时长
        double countTime = getCountTime("end|file|main");
        FileUtil.writeStrToFile("场景:文件,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFiles ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesStatusSaver() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesStatusSaver ******************" + "\n", durationFile);
        double testTime = testFilesItems("Status & Sticker");

        // 获取时长
        double countTime = getCountTime("end|file|status saver");
        FileUtil.writeStrToFile("场景:文件-Status & Sticker,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesStatusSaver ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesWhatsapp() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesWhatsapp ******************" + "\n", durationFile);
        double testTime = testFilesItems("WhatsApp");

        // 获取时长
        double countTime = getCountTime("end|file|whatsapp");
        FileUtil.writeStrToFile("场景:文件-WhatsApp,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesWhatsapp ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesVideos() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesVideos ******************" + "\n", durationFile);
        double testTime = testFilesItems("Videos");

        // 获取上报时长
        double countTime = getCountTime("end|file|video");
        FileUtil.writeStrToFile("场景:文件-Videos,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesVideos ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesVideoPlayer() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesVideoPlayer ******************" + "\n", durationFile);
        double testTime = testFilesItems("VideoPlayer");

        // 获取上报时长
        double countTime = getCountTime("end|videoplayer");
        FileUtil.writeStrToFile("场景:文件-Videos-视频播放器,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesVideoPlayer ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesMusic() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesMusic ******************" + "\n", durationFile);
        double testTime = testFilesItems("Music");

        // 获取上报时长
        double countTime = getCountTime("end|file|music");
        FileUtil.writeStrToFile("场景:文件-Music,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesMusic ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesMusicPlayer() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesMusicPlayer ******************" + "\n", durationFile);
        double testTime = testFilesItems("MusicPlayer");

        // 获取上报时长
        double countTime = getCountTime("end|music_player");
        FileUtil.writeStrToFile("场景:文件-Music-音乐播放器,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesMusicPlayer ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesImages() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesImages ******************" + "\n", durationFile);
        double testTime = testFilesItems("Images");

        // 获取上报时长
        double countTime = getCountTime("end|file|images");
        FileUtil.writeStrToFile("场景:文件-Images,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesImages ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesImageReader() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesImageReader ******************" + "\n", durationFile);
        double testTime = testFilesItems("ImageReader");

        // 获取上报时长
        double countTime = getCountTime("end|image_reader");
        FileUtil.writeStrToFile("场景:文件-Images-图片查看器,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesImageReader ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesDocuments() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesDocuments ******************" + "\n", durationFile);
        double testTime = testFilesItems("Documents");

        // 获取上报时长
        double countTime = getCountTime("end|file|documents");
        FileUtil.writeStrToFile("场景:文件-Documents,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesDocuments ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesDocumentsDOC() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesDocumentsDOC ******************" + "\n", durationFile);
        double testTime = testFilesItems("Documents-DOC");

        // 获取上报时长
        double countTime = getCountTime("end|file_reader|word");
        FileUtil.writeStrToFile("场景:文件-Documents-DOC,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesDocumentsDOC ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesDocumentsPDF() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesDocumentsPDF ******************" + "\n", durationFile);
        double testTime = testFilesItems("Documents-PDF");

        // 获取上报时长
        double countTime = getCountTime("end|file_reader|pdf");
        FileUtil.writeStrToFile("场景:文件-Documents-PDF,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesDocumentsPDF ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesDocumentsTXT() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesDocumentsTXT ******************" + "\n", durationFile);
        double testTime = testFilesItems("Documents-TXT");

        // 获取上报时长
        double countTime = getCountTime("end|file_reader|txt");
        FileUtil.writeStrToFile("场景:文件-Documents-TXT,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesDocumentsTXT ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesDocumentsXLS() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesDocumentsXLS ******************" + "\n", durationFile);
        double testTime = testFilesItems("Documents-XLS");

        // 获取上报时长
        double countTime = getCountTime("end|file_reader|excle");
        FileUtil.writeStrToFile("场景:文件-Documents-XLS,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesDocumentsXLS ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesDocumentsPPT() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesDocumentsPPT ******************" + "\n", durationFile);
        double testTime = testFilesItems("Documents-PPT");

        // 获取上报时长
        double countTime = getCountTime("end|file_reader|ppt");
        FileUtil.writeStrToFile("场景:文件-Documents-PPT,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesDocumentsPPT ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesStorage() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesStorage ******************" + "\n", durationFile);
        double testTime = testFilesItems("Storage");

        // 获取上报时长
        double countTime = getCountTime("end|file|storage");
        FileUtil.writeStrToFile("场景:文件-Storage,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesStorage ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesArchives() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesArchives ******************" + "\n", durationFile);
        double testTime = testFilesItems("Archives");

        // 获取上报时长
        double countTime = getCountTime("end|file|archives");
        FileUtil.writeStrToFile("场景:文件-Archives,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesArchives ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesArchivesUnzip() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesArchivesUnzip ******************" + "\n", durationFile);
        double testTime = testFilesItems("Archives-Unzip");

        // 获取上报时长
        double countTime = getCountTime("end|file|unzip");
        FileUtil.writeStrToFile("场景:文件-Archives-Unzip,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesArchivesUnzip ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesInstagram() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesInstagram ******************" + "\n", durationFile);
        double testTime = testFilesItems("Instagram");

        // 获取上报时长
        double countTime = getCountTime("end|file|instagram");
        FileUtil.writeStrToFile("场景:文件-Instagram,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesInstagram ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesOfflinePage() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesOfflinePage ******************" + "\n", durationFile);
        double testTime = testFilesItems("Offline pages");

        // 获取上报时长
        double countTime = getCountTime("end|file|offline pages");
        FileUtil.writeStrToFile("场景:文件-Offline pages,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesOfflinePage ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesApps() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesApps ******************" + "\n", durationFile);
        double testTime = testFilesItems("Apps");

        // 获取上报时长
        double countTime = getCountTime("end|file|apps");
        FileUtil.writeStrToFile("场景:文件-Apps,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesApps ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesOthers() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesOthers ******************" + "\n", durationFile);
        double testTime = testFilesItems("Others");

        // 获取上报时长
        double countTime = getCountTime("end|file|others");
        FileUtil.writeStrToFile("场景:文件-Others,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesOthers ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesOthersReader() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesOthersReader ******************" + "\n", durationFile);
        double testTime = testFilesItems("Others-Reader");

        // 获取上报时长
        double countTime = getCountTime("end|file_reader|other");
        FileUtil.writeStrToFile("场景:文件-Others-Reader,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesOthersReader ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesJunkFiles() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesJunkFiles ******************" + "\n", durationFile);
        double testTime = testFilesItems("Junk files");

        // 获取上报时长
        double countTime = getCountTime("end|cleaner|basics");
        FileUtil.writeStrToFile("场景:文件-Junk files,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesJunkFiles ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesPhoneBoost() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesPhoneBoost ******************" + "\n", durationFile);
        double testTime = testFilesItems("Phone boost");

        // 获取上报时长
        double countTime = getCountTime("end|cleaner|phoneboost");
        FileUtil.writeStrToFile("场景:文件-Phone boost,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesPhoneBoost ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesCleanVideos() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesCleanVideos ******************" + "\n", durationFile);
        double testTime = testFilesItems("Clean Up Videos");

        // 获取上报时长
        double countTime = getCountTime("end|cleaner|video");
        FileUtil.writeStrToFile("场景:文件-Clean Up Videos,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesCleanVideos ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesCleanPhoenix() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesCleanPhoenix ******************" + "\n", durationFile);
        double testTime = testFilesItems("Clean Up Phoenix");

        // 获取上报时长
        double countTime = getCountTime("end|cleaner|browser");
        FileUtil.writeStrToFile("场景:文件-Clean Up Phoenix,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesCleanPhoenix ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesRecentDocuments() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesRecentDocuments ******************" + "\n", durationFile);
        double testTime = testFilesItems("Recent Documents");

        // 获取上报时长
        double countTime = getCountTime("end|file|recent documents");
        FileUtil.writeStrToFile("场景:文件-Recent Documents,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesRecentDocuments ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesWallpaper() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesWallpaper ******************" + "\n", durationFile);
        double testTime = testFilesItems("Wallpaper");

        // 获取上报时长
        double countTime = getCountTime("end|file|wallpaper");
        FileUtil.writeStrToFile("场景:文件-Wallpaper,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesWallpaper ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesRingtones() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesRingtones ******************" + "\n", durationFile);
        double testTime = testFilesItems("Ringtones");

        // 获取上报时长
        double countTime = getCountTime("end|file|Ringtones");
        FileUtil.writeStrToFile("场景:文件-Ringtones,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesRingtones ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesCompressFiles() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesCompressFiles ******************" + "\n", durationFile);
        double testTime = testFilesItems("Compress files");

        // 获取上报时长
        double countTime = getCountTime("end|file|compression");
        FileUtil.writeStrToFile("场景:文件-Compress files,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesCompressFiles ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesCompressFilesSelector() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesCompressFilesSelector ******************" + "\n", durationFile);
        double testTime = testFilesItems("CompressFiles-selector");

        // 获取上报时长
        double countTime = getCountTime("end|file|selector");
        FileUtil.writeStrToFile("场景:文件-Compress files-Selector,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesCompressFilesSelector ******************" + "\n", durationFile);
        backToHome();
    }

    @Test
    public void testFilesUnzipFiles() {
        // 先清理日志
        ShellCommon.clearBufferCache(device, null);
        sleep(TIMEOUT_SHORT);

        // 执行场景
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** start testFilesUnzipFiles ******************" + "\n", durationFile);
        double testTime = testFilesItems("Unzip files");

        // 获取上报时长
        double countTime = getCountTime("end|file|unzip");
        FileUtil.writeStrToFile("场景:文件-Unzip files,测试时长:" + testTime + "s,统计时长:" + countTime + "s" + "\n", durationFile);
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ****************** end testFilesUnzipFiles ******************" + "\n", durationFile);
        backToHome();
    }

    /**
     * 计算测试时长
     */
    private double getTestTime(long startTime, long endTime) {
        return CommonUtil.keepDecimalPoint((double) (endTime - startTime) / 1000, 2);
    }

    /**
     * 根据场景获取上报的时长
     */
    private double getCountTime(String scenes) {
        double countTime = 0;
        String result = "";
        int sdk = Build.VERSION.SDK_INT;
        if (sdk <= 23) {
            result = ShellCommand.execCmdByUiDevice(device, "logcat -d -v time -s UnitTime:D");
        } else {
            result = ShellCommand.execCmdByUiDevice(device, "logcat -d -v time -v year -s UnitTime:D");
        }
        String[] resultLines = result.split("\n");
        for (String resultLine : resultLines) {
            if (!resultLine.equals("")) {
                if (!resultLine.contains("--------- beginning")) {
                    FileUtil.writeStrToFile(resultLine + "\n", durationFile);
                }

                if (resultLine.contains(scenes)) {
                    String[] resultLineParts = resultLine.split("\\|");
                    countTime = CommonUtil.keepDecimalPoint((double) Long.parseLong(resultLineParts[resultLineParts.length - 1].trim()) / 1000, 2);
                    break;
                }
            }
        }
        return countTime;
    }

    private double testFeedsTab(String tabName) {
        long startTime = 0;
        long endTime = 0;
        try {
            startTime = System.currentTimeMillis();
            switchFeedsTab(tabName);
            back();
            endTime = System.currentTimeMillis();
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }
        return getTestTime(startTime, endTime);
    }

    private double testFeedsNews(String tabName) {
        long startTime = 0;
        long endTime = 0;
        try {
            switchFeedsTab(tabName);
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
            startTime = System.currentTimeMillis();
            news.get(0).click();
            sleep(TIMEOUT_MEDIUM);
            back();
            endTime = System.currentTimeMillis();
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }
        return getTestTime(startTime, endTime);
    }

    private double testFeedsVideo(String tabName) {
        long startTime = 0;
        long endTime = 0;
        try {
            switchFeedsTab(tabName);
            UiObject2 firstVideo = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.3, 0.6, 0, 1, 0.02, 0.9).get(0);
            UiObject2 firstVideoBottom = getChildUiObject2(firstVideo, false, "android.widget.LinearLayout", 0.8, 1, 0, 0.1, 0, 1, 0, 1, false);
            startTime = System.currentTimeMillis();
            firstVideoBottom.click();
            sleep(TIMEOUT_MEDIUM);
            back();
            endTime = System.currentTimeMillis();
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }
        return getTestTime(startTime, endTime);
    }

    private double testFeedsMiniVideo(String tabName) {
        long startTime = 0;
        long endTime = 0;
        try {
            switchFeedsTab(tabName);
            startTime = System.currentTimeMillis();
            getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.8, 0, 1, 0.02, 0.9).get(0).click();
            sleep(TIMEOUT_SHORT);
            UiObject2 swipeToast = waitUiObject2ByText("Swipe up for more", TIMEOUT_SHORT);
            if (swipeToast != null) {
                swipeToast.click();
            }
            sleep(TIMEOUT_MEDIUM);
            back();
            endTime = System.currentTimeMillis();
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }
        return getTestTime(startTime, endTime);
    }

    private double testFeedsImg(String tabName) {
        long startTime = 0;
        long endTime = 0;
        try {
            switchFeedsTab(tabName);
            startTime = System.currentTimeMillis();
            List<UiObject2> linearLayouts = waitUiObject2s("android.widget.LinearLayout", true, 0.9, 1, 0.2, 0.8, 0, 1, 0.02, 1, 3);
            linearLayouts.get(0).click();
            sleep(TIMEOUT_MEDIUM);
            back();
            endTime = System.currentTimeMillis();
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }
        return getTestTime(startTime, endTime);
    }

    private double testMeItems(String item) {
        long startTime = 0;
        long endTime = 0;
        try {
            if (item.equals("Me")) {
                startTime = System.currentTimeMillis();
                waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_MEDIUM);
            } else {
                waitUiObject2ByText("Me", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);

                // 判断是否需要登录
                if (item.equals("User") || item.equals("Msg")) {
                    UiObject2 login = waitUiObject2ByText("Login", TIMEOUT_MEDIUM);
                    if (login != null) {
                        login.click();
                        sleep(TIMEOUT_VERY_SHORT);
                        waitUiObject2ByText("Continue with Google", TIMEOUT_MEDIUM).click();
                        sleep(TIMEOUT_SHORT);
                        waitUiObject2ByTextContains("@gmail.com", TIMEOUT_LONG).click();
                        sleep(TIMEOUT_SHORT);
                        waitUiObject2ByText("Signed in with Google", TIMEOUT_LONG);
                        sleep(TIMEOUT_VERY_SHORT);
                    }
                }

                // 处理各场景
                startTime = System.currentTimeMillis();
                if (item.equals("User")) {
                    waitUiObject2ByText("Signed in with Google", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_MEDIUM);
                } else if (item.equals("Msg")) {
                    getUiObject2s("android.widget.ImageView", true, 0.05, 0.2, 0.01, 0.2, 0.8, 1, 0.02, 0.2).get(0).click();
                    sleep(TIMEOUT_MEDIUM);
                } else {
                    waitUiObject2ByText(item, TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_MEDIUM);
                }
            }

            back();
            endTime = System.currentTimeMillis();
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }
        return getTestTime(startTime, endTime);
    }

    private double testFilesItems(String item) {
        long startTime = 0;
        long endTime = 0;
        try {
            if (item.equals("Files")) {
                startTime = System.currentTimeMillis();
                waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_MEDIUM);
            } else {
                waitUiObject2ByText("Files", TIMEOUT_MEDIUM).click();
                sleep(TIMEOUT_SHORT);

                // 判断是否需要上滑
                if (item.equals("Clean Up Videos") || item.equals("Clean Up Phoenix") || item.equals("Recent Documents") || item.equals("Wallpaper")
                        || item.equals("Ringtones") || item.equals("Compress files") || item.equals("CompressFiles-selector") || item.equals("Unzip files")) {
                    swip(0.5, 0.8, 0.5, 0.2);
                    sleep(TIMEOUT_SHORT);
                } else {
                    swip(0.5, 0.2, 0.5, 0.8);
                    sleep(TIMEOUT_SHORT);
                }

                // 处理各场景
                if (item.equals("VideoPlayer")) {
                    waitUiObject2ByText("Videos", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_SHORT);
                    startTime = System.currentTimeMillis();
                    getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.5, 0, 1, 0.1, 0.9).get(0).click();
                    sleep(TIMEOUT_MEDIUM);
                } else if (item.equals("MusicPlayer")) {
                    waitUiObject2ByText("Music", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_SHORT);
                    startTime = System.currentTimeMillis();
                    getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.3, 0, 1, 0.1, 0.9).get(0).click();
                    sleep(TIMEOUT_MEDIUM);
                    getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3).get(1).click();
                    endTime = System.currentTimeMillis();
                    sleep(TIMEOUT_MEDIUM);
                } else if (item.equals("ImageReader")) {
                    waitUiObject2ByText("Images", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_SHORT);
                    startTime = System.currentTimeMillis();
                    getUiObject2s("android.widget.FrameLayout", true, 0.2, 0.4, 0.1, 0.3, 0, 1, 0.1, 0.9).get(0).click();
                    sleep(TIMEOUT_MEDIUM);
                } else if (item.contains("Documents-")) {
                    int slideNum = 0;
                    String type = item.split("-")[1];
                    if (type.equals("DOC")) {
                        slideNum = 1;
                    } else if (type.equals("PDF")) {
                        slideNum = 2;
                    } else if (type.equals("TXT")) {
                        slideNum = 3;
                    } else if (type.equals("XLS")) {
                        slideNum = 4;
                    } else if (type.equals("PPT")) {
                        slideNum = 5;
                    } else if (type.equals("EPUB")) {
                        slideNum = 6;
                    }
                    waitUiObject2ByText("Documents", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_SHORT);
                    for (int i = 0; i < slideNum; i++) {
                        swip(0.7, 0.5, 0.3, 0.5);
                        sleep(TIMEOUT_VERY_SHORT);
                    }
                    startTime = System.currentTimeMillis();
                    getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.3, 0, 1, 0.1, 0.9).get(0).click();
                    sleep(TIMEOUT_LONG);
                    back();
                    sleep(TIMEOUT_VERY_SHORT);
                } else if (item.equals("Archives-Unzip")) {
                    UiObject2 archives = waitUiObject2ByText("Archives", TIMEOUT_MEDIUM);
                    if (archives == null) {
                        waitUiObject2ByText("More", TIMEOUT_MEDIUM).click();
                        sleep(TIMEOUT_VERY_SHORT);
                        archives = waitUiObject2ByText(item, TIMEOUT_MEDIUM);
                    }
                    archives.click();
                    sleep(TIMEOUT_SHORT);
                    startTime = System.currentTimeMillis();
                    getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.3, 0, 1, 0.1, 0.9).get(0).click();
                    sleep(TIMEOUT_MEDIUM);
                } else if (item.equals("Archives") || item.equals("Instagram") || item.equals("Offline pages") || item.equals("Apps") || item.equals("Others")) {
                    UiObject2 itemUiObject2 = waitUiObject2ByText(item, TIMEOUT_MEDIUM);
                    if (itemUiObject2 == null) {
                        waitUiObject2ByText("More", TIMEOUT_MEDIUM).click();
                        sleep(TIMEOUT_VERY_SHORT);
                        itemUiObject2 = waitUiObject2ByText(item, TIMEOUT_MEDIUM);
                    }
                    startTime = System.currentTimeMillis();
                    itemUiObject2.click();
                    sleep(TIMEOUT_MEDIUM);
                } else if (item.equals("Others-Reader")) {
                    UiObject2 others = waitUiObject2ByText("Others", TIMEOUT_MEDIUM);
                    if (others == null) {
                        waitUiObject2ByText("More", TIMEOUT_MEDIUM).click();
                        sleep(TIMEOUT_VERY_SHORT);
                        others = waitUiObject2ByText(item, TIMEOUT_MEDIUM);
                    }
                    others.click();
                    sleep(TIMEOUT_SHORT);
                    UiObject2 otherSvg = waitUiObject2ByText("other_svg_30kB.svg", TIMEOUT_MEDIUM);
                    if (otherSvg == null) {
                        otherSvg = getUiObject2s("android.widget.LinearLayout", true, 0.8, 1, 0.05, 0.3, 0, 1, 0.1, 0.9).get(0);

                    }
                    startTime = System.currentTimeMillis();
                    otherSvg.click();
                    sleep(TIMEOUT_MEDIUM);
                } else if (item.equals("Recent Documents")) {
                    startTime = System.currentTimeMillis();
                    waitUiObject2ByText("More", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_MEDIUM);
                } else if (item.equals("CompressFiles-selector")) {
                    waitUiObject2ByText("Compress files", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_SHORT);
                    startTime = System.currentTimeMillis();
                    waitUiObject2ByText("Select files", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_MEDIUM);
                } else if (item.equals("Phone boost")) {
                    startTime = System.currentTimeMillis();
                    waitUiObject2ByText(item, TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_LONG);
                    // 关闭广告
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
                            sleep(3000);
                            ShellCommon.amStartApp(device, activity, null);
                        }
                        sleep(3000);
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
                                sleep(3000);
                                ShellCommon.amStartApp(device, activity, null);
                            }
                            sleep(3000);
                        }
                    }
                    // 处理添加至主页弹窗
                    if (waitUiObject2ByText("Add", TIMEOUT_MEDIUM) != null) {
                        back();
                        sleep(TIMEOUT_VERY_SHORT);
                    }
                    for (int i = 0; i < 10; i++) {
                        List<UiObject2> boostBacks = getUiObject2s("android.widget.ImageView", true, 0, 0.2, 0, 0.2, 0, 0.3, 0.02, 0.3);
                        if (boostBacks == null || boostBacks.size() == 0) {
                            back();
                        } else {
                            // uiautomator有时点击时会报异常
                            try {
                                boostBacks.get(0).click();
                            } catch (Exception e) {
                                back();
                            }
                        }
                        sleep(TIMEOUT_VERY_SHORT);
                        if (waitUiObject2ByTextContains("Me", TIMEOUT_VERY_SHORT) != null) {
                            break;
                        }
                    }
                } else {
                    startTime = System.currentTimeMillis();
                    waitUiObject2ByText(item, TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_MEDIUM);
                }
            }

            back();
            if (item.equals("Junk files")) {
                UiObject2 exit = waitUiObject2ByText("Exit", TIMEOUT_MEDIUM);
                if (exit != null) {
                    exit.click();
                }
            }
            if (!item.equals("MusicPlayer")) {
                endTime = System.currentTimeMillis();
            }
            sleep(TIMEOUT_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
            backToApp();
            backToHome();
        }
        return getTestTime(startTime, endTime);
    }

}
