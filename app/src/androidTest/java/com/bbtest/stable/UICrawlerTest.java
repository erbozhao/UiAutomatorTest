package com.bbtest.stable;


import android.graphics.Rect;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;

/**
 * @author onuszhao
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class UICrawlerTest extends MonkeyCommon {

    public File resultFolder = new File(rootFolder, "crawler");
    public File crawlerFile = new File(resultFolder, "crawler.txt");
    private File monkeyInfoFile = new File(downloadFile, "monkey.txt");
    private String pkgName = "";
    private String activity = "";

    // 全局记录已遍历对象，避免重复点击，提高效率
    private static List<String> hasClickedElements;
    private static List<String> secondHasClickedElements;

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
        FileUtil.createFile(crawlerFile);
        // 获取需要跑的包相关信息
        pkgName = FileUtil.readFile(monkeyInfoFile).trim();
        if (pkgName.equals("")) {
            pkgName = "com.transsion.phoenix";
        }
        // 获取activity(解决一个应用存在多个主activity)
        List<String> mainActivities = ShellCommon.getActivities(device, pkgName, null);
        for (String mainActivity : mainActivities) {
            ShellCommon.amStartApp(device, mainActivity, crawlerFile);
            CommonUtil.sleep(5000);
            if (!ShellCommon.isAppBackstage(device, pkgName)) {
                activity = mainActivity;
                break;
            }
        }
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  " + pkgName + "\n", crawlerFile);
        // 初始化一点击元素
        hasClickedElements = new ArrayList<>();
        secondHasClickedElements = new ArrayList<>();
    }

    @Test
    public void testCrawler() {

    }

    @Test
    public void testHomeTool() {
        loopTraverseUI("HomeTool");
    }

    @Test
    public void testFeeds() {

    }

    @Test
    public void testFeedsShortVideo() {

    }

    @Test
    public void testFeedsSmallVideo() {

    }

    @Test
    public void testFeedsImage() {

    }

    @Test
    public void testDownloads() {

    }

    @Test
    public void testMe() {

    }

    @Test
    public void testNovel() {
        loopTraverseUI("Novel");
    }


    @Test
    public void testFiles() {
        loopTraverseUI("Novel");
    }

    @Test
    public void testFilesDocuments() {

    }

    @Test
    public void testFilesImages() {

    }

    @Test
    public void testFilesVideos() {

    }

    @Test
    public void testFilesMusic() {

    }

    @Test
    public void testMultiWindow() {

    }

    @Test
    public void testWebView() {

    }

    private void loopTraverseUI(String scenes) {
        try {
            // 循环遍历页面
            Log.i("onuszhao", "*********** start traverse! *************");
            traverseFirstPage(scenes);
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "CrawlerTest:Exception" + "\n", crawlerFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), crawlerFile);
            screenshot(resultFolder + "/crawler_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    /**
     * 只遍历一级菜单，然后再分场景，否则遍历层级太深，容易跳出场景
     */
    private void traverseFirstPage(String scenes) {
        UiObject2 entryUiObject2 = null;
        if (scenes.equals("HomeTool")) {
            entryUiObject2 = gotoHomeTool(pkgName, activity, crawlerFile);
        } else if (scenes.equals("Novel")) {
            entryUiObject2 = gotoNovel(pkgName, activity, crawlerFile);
        }

        //遍历所有控件
        sleep(TIMEOUT_MEDIUM);
        List<UiObject2> clickableUiObject2s = getClickableUiObject2s(getRootObject());
        Log.i("onuszhao", "FirstPage --> totalSize:" + clickableUiObject2s.size());
        try {
            for (int i = 0; i < clickableUiObject2s.size(); i++) {
                // 最后一个对象时，上滑页面
                if (i == clickableUiObject2s.size() - 1) {
                    swip(0.5, 0.7, 0.5, 0.3);
                    sleep(TIMEOUT_VERY_SHORT);
                }

                // 通过类名+宽高+文案定位元素
                UiObject2 clickableUiObject2 = clickableUiObject2s.get(i);
                String curObjectClass = clickableUiObject2.getClassName();
                Rect curUiObjectRect = clickableUiObject2.getVisibleBounds();
                int curObjectWidth = curUiObjectRect.right - curUiObjectRect.left;
                int curObjectHeight = curUiObjectRect.bottom - curUiObjectRect.top;
                String curObjectText = getText(clickableUiObject2, true);
                String curElement = curObjectClass + "-" + curObjectWidth + "-" + curObjectHeight + "-" + curObjectText;
                Log.i("onuszhao", "FirstPage --> curElement:" + curElement + ",hasClicked:" + hasClickedElements.contains(curElement) + ",hasClickedSize:" + hasClickedElements.size());
                // 点击未点击的对象
                if (!hasClickedElements.contains(curElement)) {
                    clickableUiObject2.click();
                    sleep(TIMEOUT_SHORT);

                    // 判断是否back回入口页面
                    if (isContainsUiobject2(entryUiObject2)) {
                        entryUiObject2.click();
                        sleep(TIMEOUT_SHORT);
                        continue;
                    }

                    // 记录已遍历对象
                    traverseSecondPage(scenes, clickableUiObject2, false);

                    // 返回，并记录已点击
                    back();
                    hasClickedElements.add(curElement);
                }
            }
        } catch (Exception e) {
            Log.i("onuszhao", "FirstPage --> traverse again");
            traverseFirstPage(scenes);
        }
    }

    /**
     * 只遍历一级菜单，然后再分场景，否则遍历层级太深，容易跳出场景
     */
    private void traverseSecondPage(String scenes, UiObject2 firstUiObject2, boolean isClickError) {
        // 二级页面遍历错误时，先回到一级界面
        if (isClickError) {
            if (scenes.equals("HomeTool")) {
                gotoHomeTool(pkgName, activity, crawlerFile);
            } else if (scenes.equals("Novel")) {
                gotoNovel(pkgName, activity, crawlerFile);
            }

            sleep(TIMEOUT_MEDIUM);
            List<UiObject2> clickableUiObject2s = getClickableUiObject2s(getRootObject());
            try {
                for (UiObject2 clickableUiObject2 : clickableUiObject2s) {
                    if (clickableUiObject2.equals(firstUiObject2)) {
                        clickableUiObject2.click();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //遍历所有控件
        sleep(TIMEOUT_MEDIUM);
        List<UiObject2> clickableUiObject2s = getClickableUiObject2s(getRootObject());
        Log.i("onuszhao", "SecondPage --> totalSize:" + clickableUiObject2s.size());
        try {
            for (int i = 0; i < clickableUiObject2s.size(); i++) {
                // 最后一个对象时，上滑页面
                if (i == clickableUiObject2s.size() - 1) {
                    swip(0.5, 0.7, 0.5, 0.3);
                    sleep(TIMEOUT_VERY_SHORT);
                }

                // 通过类名+宽高+文案定位元素
                UiObject2 clickableUiObject2 = clickableUiObject2s.get(i);
                String curObjectClass = clickableUiObject2.getClassName();
                Rect curUiObjectRect = clickableUiObject2.getVisibleBounds();
                int curObjectWidth = curUiObjectRect.right - curUiObjectRect.left;
                int curObjectHeight = curUiObjectRect.bottom - curUiObjectRect.top;
                String curObjectText = getText(clickableUiObject2, true);
                String curElement = curObjectClass + "-" + curObjectWidth + "-" + curObjectHeight + "-" + curObjectText;
                Log.i("onuszhao", "SecondPage --> curElement:" + curElement + ",hasClicked:" + secondHasClickedElements.contains(curElement) + ",hasClickedSize:" + secondHasClickedElements.size());
                // 点击未点击的对象
                if (!secondHasClickedElements.contains(curElement)) {
                    clickableUiObject2.click();
                    sleep(TIMEOUT_SHORT);

                    // 返回，并记录已点击
                    back();
                    secondHasClickedElements.add(curElement);
                }
            }
        } catch (Exception e) {
            Log.i("onuszhao", "SecondPage --> traverse again");
            traverseSecondPage(scenes, firstUiObject2, true);
        }
    }

    private boolean isContainsUiobject2(UiObject2 uiObject2) {
        boolean isContainsUiobject2 = false;
        sleep(TIMEOUT_SHORT);
        List<UiObject2> clickableUiObject2s = getClickableUiObject2s(getRootObject());
        try {
            for (UiObject2 clickableUiObject2 : clickableUiObject2s) {
                if (clickableUiObject2.equals(uiObject2)) {
                    isContainsUiobject2 = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isContainsUiobject2;
    }

    @After
    public void afterTest() {
        super.afterTest();
    }
}
