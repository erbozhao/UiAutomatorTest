package com.bbtest.other.likee;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PhxCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

/**
 * @author onuszhao
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class LikeeTest extends PhxCommon {

    String pkgName = "video.like";
    String activity = "video.like/com.yy.iheima.startup.MainActivity";
    String[] countries = null;

    private File resultFolder = new File(rootFolder, "likee");
    private File resultFile = new File(resultFolder, "country.txt");
    private File countryFile = new File(downloadFile, "country_list.txt");
    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.deleteFolder(resultFolder);
        FileUtil.createFolder(resultFolder);
        FileUtil.createFile(resultFile);
        // 若读取不到国家，则使用默认的

        String result = FileUtil.readFile(countryFile);
        String[] resultLines = result.split("\n");
        for (String resultLine : resultLines) {
            if (!resultLine.equals("")) {
                countries = resultLine.trim().split(",");
                break;
            }
        }
        if (countries == null || countries.length == 0) {
            countries = new String[]{"MA", "US", "KE", "NG", "GH", "UG", "TZ", "CI", "SN", "DZ", "EG", "UK"};
        }
    }

    @Test
    public void testLikeeSmallVideo() {
        try {
            while (true) {
                for (int i = 0; i < countries.length; i++) {
                    String country = countries[i];
                    FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  Country=" + country + "\n", resultFile);
                    startApp(pkgName);
                    sleep(TIMEOUT_LONG);
                    waitUiObject2ByRes("video.like:id/rl_avatar", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_SHORT);
                    swip(0.5, 0.8, 0.5, 0.2);
                    sleep(TIMEOUT_SHORT);
                    waitUiObject2ByRes("video.like:id/tv_text_setting", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_SHORT);
                    swip(0.5, 0.8, 0.5, 0.2);
                    sleep(TIMEOUT_SHORT);
                    waitUiObject2ByRes("video.like:id/btn_developer_options", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_SHORT);
                    waitScrollableUiObjectByText("android.widget.ListView", "设置国家地区", false).click();
                    sleep(TIMEOUT_SHORT);
                    waitUiObject2ByRes("video.like:id/et_country_name", TIMEOUT_MEDIUM).setText(country);
                    sleep(TIMEOUT_VERY_SHORT);
                    waitUiObject2ByRes("video.like:id/et_country_code", TIMEOUT_MEDIUM).setText(country);
                    sleep(TIMEOUT_VERY_SHORT);
                    waitUiObject2ByText("Save", TIMEOUT_MEDIUM).click();
                    sleep(TIMEOUT_SHORT);
                    for (int j = 0; j < 10; j++) {
                        if (waitUiObject2ByRes("video.like:id/rl_avatar", TIMEOUT_SHORT) == null) {
                            back();
                            sleep(TIMEOUT_VERY_SHORT);
                        } else {
                            break;
                        }
                    }
                    sleep(TIMEOUT_SHORT);
                    List<UiObject2> smallVideos = waitUiObject2sByRes("video.like:id/news_list", TIMEOUT_SHORT);
                    if (smallVideos.size() > 0) {
                        smallVideos.get(0).click();
                    } else {
                        click(width / 2, height / 2);
                    }
                    sleep(TIMEOUT_MEDIUM);
                    long startTime = System.currentTimeMillis();
                    long endTime = 0L;
                    while (true) {
                        endTime = System.currentTimeMillis();
                        long costTime = endTime - startTime;
                        if (costTime > 1 * 60 * 60 * 1000) {
                            back();
                            sleep(TIMEOUT_SHORT);
                            break;
                        } else {
                            swip(0.5, 0.6, 0.5, 0.3);
                            sleep(TIMEOUT_MEDIUM);
                        }
                    }
                    for (int j = 0; j < 3; j++) {
                        back();
                        sleep(TIMEOUT_VERY_SHORT);
                    }
                    ShellCommon.forceStopApp(device, pkgName, null);
                    sleep(TIMEOUT_SHORT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void afterTest() {
        super.afterTest();
    }

}
