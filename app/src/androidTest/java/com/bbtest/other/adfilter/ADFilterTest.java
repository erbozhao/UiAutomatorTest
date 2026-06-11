package com.bbtest.other.adfilter;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.PhxCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.ShellCommand;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @author onuszhao
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class ADFilterTest extends PhxCommon {

    private String imgFolder = "/sdcard/Download/PHXDownloads";

    @Before
    public void beforeTest() {
        super.beforeTest();
    }

    @Test
    public void testLongScreenshot1() {
        // 先等待5s，并跳过闪屏
        sleep(TIMEOUT_MEDIUM);
        skipSplash();

        // 再等待5s，跳过应用弹窗
        sleep(TIMEOUT_MEDIUM);
        skipAppDialog();
        skipOtherDialog();

        // 最后等待10s，跳过页面内弹窗
        sleep(TIMEOUT_LONG);
        List<UiObject2> clickableUiObjects = getClickableUiObject2s(getRootObject());
        for (int i = 0; i < clickableUiObjects.size(); i++) {
            UiObject2 clickableUiObject = clickableUiObjects.get(i);
            String clickableUiObjectClass = clickableUiObject.getClassName();
            String clickableUiObjectText = clickableUiObject.getText();
            if (clickableUiObjectText == null || clickableUiObjectText.equals("")) {
                clickableUiObjectText = clickableUiObject.getContentDescription();
            }
            if (clickableUiObjectText != null && clickableUiObjectText.equals("Quit Tour")) {
                clickableUiObject.click();
                sleep(TIMEOUT_SHORT);
                break;
            } else if (clickableUiObjectText != null && clickableUiObjectText.equals("Not Now")) {
                clickableUiObject.click();
                sleep(TIMEOUT_SHORT);
                swip(0.5, 0.2, 0.5, 0.8);
                sleep(TIMEOUT_SHORT);
                break;
            } else if (clickableUiObjectText != null && clickableUiObjectText.equals("I'm Over 18")) {
                clickableUiObject.click();
                sleep(TIMEOUT_SHORT);
                swip(0.5, 0.2, 0.5, 0.8);
                sleep(TIMEOUT_SHORT);
                break;
            } else if (clickableUiObjectText != null && clickableUiObjectText.equals("YES")) {
                clickableUiObject.click();
                sleep(TIMEOUT_SHORT);
                swip(0.5, 0.2, 0.5, 0.8);
                sleep(TIMEOUT_SHORT);
                break;
            } else if (!clickableUiObjectClass.equals("android.webkit.WebView") && !clickableUiObjectClass.equals("android.widget.EditText")
                    && clickableUiObjectText != null && (clickableUiObjectText.equals("استمرار") || clickableUiObjectText.equals("CONTINUER"))) {
                clickableUiObject.click();
                sleep(TIMEOUT_SHORT);
                break;
            }
        }

        //长截图
        UiObject2 addressbar = waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT);
        if (addressbar == null) {
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    device.click((int) (width * 0.9), (int) (height * 0.5));  // 点击靠右边边缘区域，取消遮罩弹窗等
                    sleep(TIMEOUT_SHORT);
                } else if (i == 1) {
                    swip(0.5, 0.3, 0.5, 0.6);       // 下滑
                    sleep(TIMEOUT_SHORT);
                } else {
                    back();                     // 弹到其他应用，则back回到页面
                    sleep(TIMEOUT_SHORT);
                }

                addressbar = waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT);
                if (addressbar != null) {
                    break;
                }
            }
        }
        if (addressbar != null) {
            addressbar.click();
            sleep(TIMEOUT_SHORT);
            UiObject2 snapshot = null;
            for (int i = 0; i < 3; i++) {
                swip(0.8, 0.7, 0.8, 0.3);
                sleep(TIMEOUT_SHORT);
                snapshot = waitUiObject2ByText("Snapshot whole page", TIMEOUT_SHORT);
                if (snapshot == null) {
                    back();
                    sleep(TIMEOUT_SHORT);
                    openSnapshot();
                } else {
                    break;
                }
            }
            snapshot.click();
            sleep(TIMEOUT_VERY_LONG);

            // 长截图失败时，截屏
            if (!isLongScreenSuccess()) {
                screenshot("/sdcard/Download/PHXDownloads/" + CommonUtil.getCurTime() + ".jpg");
            }
        } else {
            screenshot("/sdcard/Download/PHXDownloads/" + CommonUtil.getCurTime() + ".jpg");
        }
    }

    @Test
    public void testLongScreenshot2() {
        // 上滑
        swip(0.5, 0.8, 0.5, 0.2);
        sleep(TIMEOUT_LONG);

        // 判断滑动成功没
        boolean isSlideSuccess = false;
        if (waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT) == null) {
            isSlideSuccess = true;
        }

        // 点击跳转二级页面
        UiObject2 preRootContent = getRootObject();
        List<UiObject2> clickableUiObjects = getClickableUiObject2s(preRootContent);
        List<UiObject2> allUiObjects;
        List<String> preAllTexts = new ArrayList<>();
        if (!isSlideSuccess) {
            allUiObjects = getAllUiObject2s(preRootContent);
            preAllTexts = getTexts(allUiObjects);
        }

        boolean isClick = false;
        // 先找较宽，且略高的点击
        for (int i = 0; i < clickableUiObjects.size(); i++) {
            UiObject2 uiObject = clickableUiObjects.get(i);
            String uiObjectClass = uiObject.getClassName();
            int uiObjectTop = uiObject.getVisibleBounds().top;
            int uiObjectWidth = uiObject.getVisibleBounds().right - uiObject.getVisibleBounds().left;
            int uiObjectHeight = uiObject.getVisibleBounds().bottom - uiObjectTop;
            // 过滤通知栏以下
            if (!uiObjectClass.equals("android.webkit.WebView") && !uiObjectClass.equals("android.widget.EditText")
                    && uiObjectTop >= (height * 0.2) && uiObjectWidth >= (width * 0.8) && uiObjectHeight >= (height * 0.3)) {
                uiObject.click();
                sleep(TIMEOUT_VERY_LONG);
                // 判断浏览器是否在前台
                if (ShellCommon.isAppBackstage(device, pkgName)) {
                    sleep(TIMEOUT_VERY_SHORT);
                    ShellCommon.amStartApp(device, activity, null);
                    sleep(TIMEOUT_SHORT);
                }
                if (!isSlideSuccess) {
                    List<UiObject2> curAllUiObjects = getAllUiObject2s(getRootObject());
                    List<String> curAllTexts = getTexts(curAllUiObjects);
                    if (isSamePage(preAllTexts, curAllTexts)) {
                        continue;
                    } else {
                        isClick = true;
                        break;
                    }
                } else {
                    if (waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT) == null) {
                        continue;
                    } else {
                        break;
                    }
                }
            }
        }

        // 再找略宽，且略高的点击
        if (!isClick) {
            for (int i = 0; i < clickableUiObjects.size(); i++) {
                UiObject2 uiObject = clickableUiObjects.get(i);
                String uiObjectClass = uiObject.getClassName();
                int uiObjectTop = uiObject.getVisibleBounds().top;
                int uiObjectWidth = uiObject.getVisibleBounds().right - uiObject.getVisibleBounds().left;
                int uiObjectHeight = uiObject.getVisibleBounds().bottom - uiObjectTop;
                // 过滤通知栏以下
                if (!uiObjectClass.equals("android.webkit.WebView") && !uiObjectClass.equals("android.widget.EditText")
                        && uiObjectTop >= (height * 0.2) && uiObjectWidth >= (width * 0.4) && uiObjectHeight >= (height * 0.15)) {
                    uiObject.click();
                    sleep(TIMEOUT_VERY_LONG);
                    // 判断浏览器是否在前台
                    if (ShellCommon.isAppBackstage(device, pkgName)) {
                        sleep(TIMEOUT_VERY_SHORT);
                        ShellCommon.amStartApp(device, activity, null);
                        sleep(TIMEOUT_SHORT);
                    }
                    if (!isSlideSuccess) {
                        List<UiObject2> curAllUiObjects = getAllUiObject2s(getRootObject());
                        List<String> curAllTexts = getTexts(curAllUiObjects);
                        if (isSamePage(preAllTexts, curAllTexts)) {
                            continue;
                        } else {
                            isClick = true;
                            break;
                        }
                    } else {
                        if (waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT) == null) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        // 找不到则点击坐标
        if (!isClick) {
            for (int i = 1; i < 4; i++) {
                device.click((int) (width * 0.6), (int) (height * (0.4 + 0.1 * i)));
                sleep(TIMEOUT_VERY_LONG);
                // 判断浏览器是否在前台
                if (ShellCommon.isAppBackstage(device, pkgName)) {
                    sleep(TIMEOUT_VERY_SHORT);
                    ShellCommon.amStartApp(device, activity, null);
                    sleep(TIMEOUT_SHORT);
                }
                if (!isSlideSuccess) {
                    List<UiObject2> curAllUiObjects = getAllUiObject2s(getRootObject());
                    List<String> curAllTexts = getTexts(curAllUiObjects);
                    if (isSamePage(preAllTexts, curAllTexts)) {
                        continue;
                    } else {
                        break;
                    }
                } else {
                    if (waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT) == null) {
                        continue;
                    } else {
                        break;
                    }
                }
            }
        }

        //判断弹窗
        skipAppDialog();
        skipOtherDialog();
        for (int i = 0; i < 10; i++) {
            UiObject2 ok = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT);
            if (ok != null) {
                ok.click();
                sleep(TIMEOUT_VERY_SHORT);
                break;
            }
        }

        // 长截图
        UiObject2 addressbar = waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT);
        if (addressbar == null) {
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    device.click((int) (width * 0.9), (int) (height * 0.5));  // 点击靠右边边缘区域，取消遮罩弹窗等
                    sleep(TIMEOUT_SHORT);
                } else if (i == 1) {
                    swip(0.5, 0.3, 0.5, 0.6);       // 下滑
                    sleep(TIMEOUT_SHORT);
                } else {
                    back();                     // 弹到其他应用，则back回到页面
                    sleep(TIMEOUT_SHORT);
                }

                addressbar = waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT);
                if (addressbar != null) {
                    break;
                }
            }
        }
        if (addressbar != null) {
            addressbar.click();
            sleep(TIMEOUT_SHORT);
            UiObject2 snapshot = null;
            for (int i = 0; i < 3; i++) {
                swip(0.8, 0.7, 0.8, 0.3);
                sleep(TIMEOUT_SHORT);
                snapshot = waitUiObject2ByText("Snapshot whole page", TIMEOUT_SHORT);
                if (snapshot == null) {
                    back();
                    sleep(TIMEOUT_SHORT);
                    openSnapshot();
                } else {
                    break;
                }
            }
            snapshot.click();
            sleep(TIMEOUT_VERY_LONG);

            // 长截图失败时，截屏
            if (!isLongScreenSuccess()) {
                screenshot("/sdcard/Download/PHXDownloads/" + CommonUtil.getCurTime() + ".jpg");
            }
        } else {
            screenshot("/sdcard/Download/PHXDownloads/" + CommonUtil.getCurTime() + ".jpg");
        }
    }

    @Test
    public void testOpenSnapshot() {
        ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -d phxbrowser://qq.com");
        sleep(TIMEOUT_MEDIUM);
        waitUiObject2ByDesc("addressbar menu", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        UiObject2 snapshot = null;
        for (int i = 0; i < 3; i++) {
            swip(0.8, 0.7, 0.8, 0.3);
            sleep(TIMEOUT_SHORT);
            snapshot = waitUiObject2ByText("Snapshot whole page", TIMEOUT_SHORT);
            if (snapshot == null) {
                back();
                sleep(TIMEOUT_SHORT);
                openSnapshot();
            } else {
                back();
                sleep(TIMEOUT_SHORT);
                break;
            }
        }
        // 退出浏览器
        waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
        waitUiObject2ByText("Exit", TIMEOUT_MEDIUM).click();
        sleep(TIMEOUT_SHORT);
    }

    @Test
    public void testCloseAllTabs() {
        UiObject2 toolbarMultiWindow = waitUiObject2ByDesc("toolbar multiWindow", TIMEOUT_SHORT);
        if (toolbarMultiWindow == null) {
            toolbarMultiWindow = waitUiObject2ByText("Tabs", TIMEOUT_SHORT);
        }
        if (toolbarMultiWindow != null) {
            toolbarMultiWindow.click();
            sleep(TIMEOUT_SHORT);
            skipAppDialog();
            skipOtherDialog();
            UiObject2 moreIcon = getUiObject2s("android.widget.ImageView", true, 0.04, 0.5, 0.02, 0.5, 0.7, 1, 0.03, 0.15).get(0);
            if (moreIcon != null) {
                moreIcon.click();
                sleep(TIMEOUT_SHORT);
                waitUiObject2ByText("Close all tabs", TIMEOUT_SHORT).click();
                sleep(TIMEOUT_SHORT);
            }
        }
    }

    @Test
    public void testExitBrowser() {
        backExitBrowser();
        ShellCommon.forceStopApp(device, pkgName, null);
        sleep(3000);
    }

    @After
    public void afterTest() {
        super.afterTest();
    }

    private void openSnapshot() {
        UiObject2 toolbarMenu = waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM);
        if (toolbarMenu != null) {
            toolbarMenu.click();
            sleep(TIMEOUT_SHORT);
            // 由于页面中有Settings，直接通过文本找，会存在问题
//            waitUiObject2ByText("Settings", TIMEOUT_MEDIUM).click();
            getUiObject2ByChildText("android.widget.LinearLayout", "Settings").click();
            sleep(TIMEOUT_SHORT);
            waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM);
            sleep(TIMEOUT_SHORT);
            swip(0.5, 0.8, 0.5, 0.2);
            sleep(TIMEOUT_SHORT);
            waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM).click();
            sleep(TIMEOUT_SHORT);
            waitUiObject2ByText("Product features", TIMEOUT_MEDIUM);
            sleep(TIMEOUT_SHORT);
            UiObject2 phxIcon = getUiObject2s("android.widget.ImageView", true, 0.2, 0.5, 0.1, 0.5, 0.2, 0.8, 0.02, 0.5).get(0);
            for (int i = 0; i < 5; i++) {
                phxIcon.click();
                sleep(TIMEOUT_VERY_SHORT);
            }
            sleep(TIMEOUT_SHORT);
            UiObject2 developerOptions = waitUiObject2ByText("Developer Options", TIMEOUT_MEDIUM);
            if (developerOptions != null) {
                developerOptions.click();
                sleep(TIMEOUT_SHORT);
            }
            UiObject2 snapshotSwitch = getUiObject2ByChildText("android.widget.LinearLayout", true, "Snapshot Whole Page", "android.widget.Switch");
            if (!snapshotSwitch.isChecked()) {
                snapshotSwitch.click();
                sleep(TIMEOUT_SHORT);
            }
            UiObject2 addressbar = null;
            for (int i = 0; i < 10; i++) {
                addressbar = waitUiObject2ByDesc("addressbar menu", TIMEOUT_SHORT);
                if (addressbar == null) {
                    back();
                    sleep(TIMEOUT_SHORT);
                } else {
                    break;
                }
            }
            addressbar.click();
            sleep(TIMEOUT_SHORT);
        }
    }

    private boolean isLongScreenSuccess() {
        boolean isLongScreenSuccess = false;
        String result = ShellCommand.execCmdByUiDevice(device, "ls " + imgFolder);
        String[] resultLines = result.split("\\n");
        for (String resultLine : resultLines) {
            if (!resultLine.equals("") && resultLine.endsWith(".png")) {
                isLongScreenSuccess = true;
                break;
            }
        }
        return isLongScreenSuccess;
    }

}
