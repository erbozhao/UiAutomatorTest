package com.bbtest.common;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import android.app.Instrumentation;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.ServiceTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.Configurator;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.bbtest.LocalService;
import com.bbtest.R;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.PermissionUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author onuszhao
 * 注：1.单个test脚本尽量控制时间在5分钟以内，以避免被手机杀死
 * 2.操作完后尽量等待下,再查找下一个界面元素
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 21)
public class BaseCommon {
    public Context context = null;
    private Instrumentation instrumentation = null;
    public UiAutomation uiAutomation = null;

    public UiDevice device = null;

    public int width = 0;
    public int height = 0;

    public final int TIMEOUT_VERY_LONG = 20 * 1000;
    public final int TIMEOUT_LONG = 10 * 1000;
    public final int TIMEOUT_MEDIUM = 5000;
    public final int TIMEOUT_SHORT = 3000;
    public final int TIMEOUT_VERY_SHORT = 1000;

    public final File downloadFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    public final File rootFolder = new File(downloadFile, "bbtest");
    public final String TAG = "BBTest";

    @Before
    public void beforeTest() {
        initCommonObject();
//        bindService();
        initUiDevice();
        assertThat(device, notNullValue());
        // 初始化目录及文件
//        PermissionUtil.getInstance().requestFilePermissionIfNeed(context, this);
        FileUtil.createFolder(rootFolder);
    }

    private void initCommonObject() {
        if (context == null) {
            context = ApplicationProvider.getApplicationContext();
        }
        if (instrumentation == null) {
            /** new for androidx.test */
            instrumentation = getInstrumentation();
            /** old for com.android.support.test*/
//            instrumentation = InstrumentationRegistry.getInstrumentation();
        }
        if (uiAutomation == null) {
            uiAutomation = instrumentation.getUiAutomation();
        }
    }

    private void initUiDevice() {
        if (device == null) {
            /** 获取设备信息 */
            device = UiDevice.getInstance(instrumentation);
            width = device.getDisplayWidth();
            height = device.getDisplayHeight();

            /** 授权测试应用 */
            grantTestPermission();

            /** 配置超时 */
            Configurator conf = Configurator.getInstance();
            // 动作超时: 默认3000ms
            conf.setActionAcknowledgmentTimeout(1000); // 1000
            // 键盘输入延时: 默认0ms
            conf.setKeyInjectionDelay(100);  // 1500
            // 滚动超时: 默认200ms
            conf.setScrollAcknowledgmentTimeout(200); // 2000
            // 空闲超时: 默认10000ms，配置成100ms，动态页面及正常查找控件都会快很多
            conf.setWaitForIdleTimeout(100); // 2500
            // 组件查找超时: 默认10000ms
            conf.setWaitForSelectorTimeout(100); //3000
        }
    }

    private void grantTestPermission() {
        // 授权当前测试应用
        String curPkgName = context.getPackageName();
        grantRuntimePermission(curPkgName, "android.permission.READ_EXTERNAL_STORAGE");
        grantRuntimePermission(curPkgName, "android.permission.WRITE_EXTERNAL_STORAGE");
        grantRuntimePermission(curPkgName, "android.permission.READ_PHONE_STATE");
        grantRuntimePermission(curPkgName, "android.permission.ACCESS_WIFI_STATE");
        grantRuntimePermission(curPkgName, "android.permission.CHANGE_WIFI_STATE");
        grantRuntimePermission(curPkgName, "android.permission.ACCESS_NETWORK_STATE");
        grantRuntimePermission(curPkgName, "android.permission.CHANGE_NETWORK_STATE");
        grantRuntimePermission(curPkgName, "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE");
    }

    /**
     * 启动应用：进程会被杀掉后重启
     *
     * @param pkgName
     */
    public void startApp(String pkgName) {
        // Start from the home screen
        device.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), TIMEOUT_MEDIUM);

        // Launch the blueprint app
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(pkgName).depth(0)), TIMEOUT_MEDIUM);
    }

    public void grantPermission(String pkgName) {
        // 授权目标测试应用
        grantRuntimePermission(pkgName, "android.permission.CAMERA");
        grantRuntimePermission(pkgName, "android.permission.READ_EXTERNAL_STORAGE");
        grantRuntimePermission(pkgName, "android.permission.WRITE_EXTERNAL_STORAGE");
        grantRuntimePermission(pkgName, "android.permission.ACCESS_FINE_LOCATION");            // GPS定位权限
        grantRuntimePermission(pkgName, "android.permission.ACCESS_COARSE_LOCATION");          // WiFi定位权限
        grantRuntimePermission(pkgName, "android.permission.READ_PHONE_STATE");                // 运营商定位权限
        grantRuntimePermission(pkgName, "android.permission.WRITE_SETTINGS");                  // 桌面数据库访问权限
        grantRuntimePermission(pkgName, "android.permission.SYSTEM_ALERT_WINDOW");             // 悬浮框权限
        grantRuntimePermission(pkgName, "android.permission.REQUEST_INSTALL_PACKAGES");        //未知来源安装权限
        grantRuntimePermission(pkgName, "android.permission.AUTHENTICATE_ACCOUNTS");           //系统ACCOUNT认证权限
        grantRuntimePermission(pkgName, "android.permission.MANAGE_EXTERNAL_STORAGE");         // 所有文件权限(targetSDK升级30后)
    }

    private void grantRuntimePermission(String pkgName, String permission) {
        try {
            uiAutomation.grantRuntimePermission(pkgName, permission);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @After
    public void afterTest() {
//        unbindService();
    }

    public UiObject getUiObjectByText(String text) {
        try {
            return device.findObject(new UiSelector().text(text));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject getUiObjectByTextContains(String text) {
        try {
            return device.findObject(new UiSelector().textContains(text));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject getUiObjectByDesc(String desc) {
        try {
            return device.findObject(new UiSelector().description(desc));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject getUiObjectByDescContains(String desc) {
        try {
            return device.findObject(new UiSelector().descriptionContains(desc));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject getUiObjectByRes(String res) {
        try {
            return device.findObject(new UiSelector().resourceId(res));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject2 getUiObject2ByText(String text) {
        try {
            return device.findObject(By.text(text));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject2 getUiObject2ByTextContains(String text) {
        try {
            return device.findObject(By.textContains(text));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject2 getUiObject2ByDesc(String desc) {
        try {
            return device.findObject(By.desc(desc));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject2 getUiObject2ByDescContains(String desc) {
        try {
            return device.findObject(By.descContains(desc));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject2 getUiObject2ByRes(String res) {
        try {
            return device.findObject(By.res(res));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> getUiObject2sByText(String text) {
        try {
            return device.findObjects(By.text(text));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> getUiObject2sByTextContains(String text) {
        try {
            return device.findObjects(By.textContains(text));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> getUiObject2sByDesc(String desc) {
        try {
            return device.findObjects(By.desc(desc));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> getUiObject2sByDescContains(String desc) {
        try {
            return device.findObjects(By.descContains(desc));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> getUiObject2sByRes(String res) {
        try {
            return device.findObjects(By.res(res));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> getUiObject2sByClazz(String clazz) {
        try {
            return device.findObjects(By.clazz(clazz));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param text  寻找对象的文本内容
     * @param clazz 寻找对象的class属性
     */
    public UiObject2 getUiObject2(String text, String clazz, double minWidth, double minHeight) {
        UiObject2 uiObject2 = null;
        try {
            List<UiObject2> textObjects = getUiObject2sByText(text);
            for (UiObject2 textObject : textObjects) {
                if (textObject.getClassName().equals(clazz)) {
                    int textObjectWidth = textObject.getVisibleBounds().right - textObject.getVisibleBounds().left;
                    int textObjectHeight = textObject.getVisibleBounds().bottom - textObject.getVisibleBounds().top;
                    if (textObjectWidth >= (width * minWidth) && textObjectHeight >= (height * minHeight)) {
                        uiObject2 = textObject;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uiObject2;
    }

    /**
     * @param srcClazz  要找的class对象
     * @param childText class对象子模块中包含的文本
     */
    public UiObject2 getUiObject2ByChildText(String srcClazz, String childText) {
        UiObject2 uiObject2 = null;
        try {
            List<UiObject2> clazzViews = getUiObject2sByClazz(srcClazz);
            for (UiObject2 clazzView : clazzViews) {
                if (isContainChildText(clazzView, childText)) {
                    uiObject2 = clazzView;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uiObject2;
    }

    /**
     * @param srcClazz       再srcClazz的对象中查找
     * @param isSrcClickable srcClazz的对象是否可点击
     * @param childText      srcClazz对象的子模块中包含的文本
     * @param dstClazz       找到dstClazz的对象
     */
    public UiObject2 getUiObject2ByChildText(String srcClazz, boolean isSrcClickable, String childText, String dstClazz) {
        UiObject2 uiObject2 = null;
        try {
            List<UiObject2> clazzViews = getUiObject2sByClazz(srcClazz);
            for (UiObject2 clazzView : clazzViews) {
                // 避免报StaleObjectException(底层控件被销毁)错误而获取不到控件，建议重复获取
                try {
                    if (isSrcClickable && !clazzView.isClickable()) {
                        continue;
                    }
                } catch (StaleObjectException e) {
                    continue;
                }

                if (isContainChildText(clazzView, childText)) {
                    uiObject2 = getContainUiObject2(clazzView, dstClazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uiObject2;
    }

    public UiObject2 getUiObject2ByChildClazz(String srcClazz, boolean isSrcClickable, String childClazz) {
        UiObject2 uiObject2 = null;
        try {
            List<UiObject2> clazzViews = getUiObject2sByClazz(srcClazz);
            for (UiObject2 clazzView : clazzViews) {
                // 避免报StaleObjectException(底层控件被销毁)错误而获取不到控件，建议重复获取
                try {
                    if (isSrcClickable && !clazzView.isClickable()) {
                        continue;
                    }
                } catch (StaleObjectException e) {
                    continue;
                }

                if (isContainChildClazz(clazzView, childClazz)) {
                    uiObject2 = clazzView;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uiObject2;
    }

    public UiObject2 getContainUiObject2(UiObject2 srcUiObject2, String dstClazz) {
        UiObject2 dstUiObject2 = null;
        try {
            List<UiObject2> childrens = srcUiObject2.getChildren();
            for (UiObject2 children : childrens) {
                if (children.getClassName().equals(dstClazz)) {
                    dstUiObject2 = children;
                    break;
                }

                if (children.getChildCount() > 0) {
                    dstUiObject2 = getContainUiObject2(children, dstClazz);
                    if (dstUiObject2 != null) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dstUiObject2;
    }

    /**
     * @param srcUiObject2 在指定对象下找
     * @param dstClazz     找为clazz的对象
     */
    public UiObject2 getChildUiObject2(UiObject2 srcUiObject2, boolean isDstClickable, String dstClazz, double minWidth, double maxWidth,
                                       double minHeight, double maxHeight, double minX, double maxX, double minY, double maxY, boolean isAllChild) {
        UiObject2 childUiObject2 = null;
        try {
            List<UiObject2> childrens = srcUiObject2.getChildren();
            for (UiObject2 children : childrens) {
                // 避免报StaleObjectException(底层控件被销毁)错误而获取不到控件，建议重复获取
                try {
                    if (isDstClickable && !children.isClickable()) {
                        continue;
                    }
                } catch (StaleObjectException e) {
                    continue;
                }

                // class匹配不上，直接下一个
                if (children.getClassName().equals(dstClazz)) {
                    int childrenLeft = children.getVisibleBounds().left;
                    int childrenRight = children.getVisibleBounds().right;
                    int childrenTop = children.getVisibleBounds().top;
                    int childrenBottom = children.getVisibleBounds().bottom;
                    int childrenWidth = childrenRight - childrenLeft;
                    int childrenHeight = childrenBottom - childrenTop;
                    if (childrenWidth >= (width * minWidth) && childrenWidth <= (width * maxWidth)
                            && childrenHeight >= (height * minHeight) && childrenHeight <= (height * maxHeight)
                            && childrenLeft >= (width * minX) && childrenRight <= (width * maxX)
                            && childrenTop >= (height * minY) && childrenBottom <= (height * maxY)) {
                        childUiObject2 = children;
                        break;
                    }
                }

                if (isAllChild) {
                    if (children.getChildCount() > 0) {
                        childUiObject2 = getChildUiObject2(children, isDstClickable, dstClazz, minWidth, maxWidth, minHeight, maxHeight, minX, maxX, minY, maxY, isAllChild);
                        if (childUiObject2 != null) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return childUiObject2;
    }

    public UiObject2 getScrollableUiObject2(String clazz) {
        UiObject2 scrollableUiObject2 = null;
        try {
            List<UiObject2> clazzObjects = getUiObject2sByClazz(clazz);
            for (UiObject2 clazzObject : clazzObjects) {
                if (clazzObject.isScrollable()) {
                    scrollableUiObject2 = clazzObject;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scrollableUiObject2;
    }

    public boolean isContainChildClazz(UiObject2 srcUiObject2, String childClazz) {
        boolean isFound = false;
        try {
            List<UiObject2> childrens = srcUiObject2.getChildren();
            for (UiObject2 children : childrens) {
                String tmpChildClazz = children.getClassName();
                if (tmpChildClazz != null && tmpChildClazz.equals(childClazz)) {
                    isFound = true;
                    break;
                }

                if (children.getChildCount() > 0) {
                    isFound = isContainChildClazz(children, childClazz);
                    if (isFound) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isFound;
    }

    public boolean isContainChildText(UiObject2 srcUiObject2, String childText) {
        boolean isFound = false;
        try {
            List<UiObject2> childrens = srcUiObject2.getChildren();
            for (UiObject2 children : childrens) {
                String tmpChildText = children.getText();
                if (tmpChildText != null && tmpChildText.equals(childText)) {
                    isFound = true;
                    break;
                }

                if (children.getChildCount() > 0) {
                    isFound = isContainChildText(children, childText);
                    if (isFound) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isFound;
    }

    public String getText(UiObject2 uiObject2, boolean isContainChild) {
        String text = "";
        try {
            text = uiObject2.getText();
            if ((text == null || text.equals("")) && isContainChild) {
                List<UiObject2> childrens = uiObject2.getChildren();
                for (UiObject2 children : childrens) {
                    text = getTextOrDesc(children, isContainChild);

                    if (text != null && !text.equals("")) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    // 获取页面所有文本信息
    public String getTextOrDesc(UiObject2 uiObject2, boolean isContainChild) {
        String text = "";
        try {
            text = uiObject2.getText();
            if (text == null || text.equals("")) {
                text = uiObject2.getContentDescription();
            }
            if ((text == null || text.equals("")) && isContainChild) {
                List<UiObject2> childrens = uiObject2.getChildren();
                for (UiObject2 children : childrens) {
                    text = getTextOrDesc(children, isContainChild);

                    if (text != null && !text.equals("")) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public UiObject2 getRootObject() {
        UiObject2 rootContent = waitUiObject2ByRes("android:id/content", TIMEOUT_MEDIUM);
        Rect rootRect = rootContent.getVisibleBounds();
        UiObject2 latestRootObject = rootContent;
        while (true) {
            boolean isFound = false;
            List<UiObject2> latestChildrens = latestRootObject.getChildren();
            for (UiObject2 latestChildren : latestChildrens) {
                Rect latestChildrenRect = latestChildren.getVisibleBounds();
                if (!latestChildrenRect.equals(rootRect)) {
                    isFound = true;
                    break;
                }
            }

            if (isFound) {
                break;
            } else {
                latestRootObject = latestChildrens.get(latestChildrens.size() - 1);
            }
        }

        return latestRootObject;
    }

    // 获取所有可操作控件
    public List<UiObject2> getAllUiObject2s(UiObject2 uiObject) {
        List<UiObject2> uiObjects = new ArrayList<>();
        try {
            List<UiObject2> childrens = uiObject.getChildren();
            for (UiObject2 children : childrens) {
                uiObjects.add(children);
                uiObjects.addAll(getAllUiObject2s(children));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uiObjects;
    }

    public List<UiObject2> getClickableUiObject2s(UiObject2 uiObject) {
        List<UiObject2> uiObjects = new ArrayList<>();
        try {
            List<UiObject2> childrens = uiObject.getChildren();
            for (UiObject2 children : childrens) {
                if (children.isClickable()) {
                    uiObjects.add(children);
                }

                if (children.getChildCount() > 0) {
                    uiObjects.addAll(getClickableUiObject2s(children));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uiObjects;
    }

    public int count = 0;

    public List<UiObject2> getClickableUiObject2s(UiObject2 uiObject, List<UiObject2> uiObjectList) {
        try {
            List<UiObject2> childrens = uiObject.getChildren();
            for (UiObject2 children : childrens) {
                count++;
                if (children.isClickable()) {
                    uiObjectList.add(children);
                }

                if (children.getChildCount() > 0) {
                    getClickableUiObject2s(children, uiObjectList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uiObjectList;
    }

    public List<UiObject2> getUiObject2s(String clazz, double minWidth, double maxWidth, double minHeight, double maxHeight,
                                         double minX, double maxX, double minY, double maxY) {
        List<UiObject2> uiObject2s = getUiObject2s(clazz, true, minWidth, maxWidth, minHeight, maxHeight, minX, maxX, minY, maxY);
        if (uiObject2s == null || uiObject2s.size() <= 0) {
            uiObject2s = getUiObject2s(clazz, false, minWidth, maxWidth, minHeight, maxHeight, minX, maxX, minY, maxY);
        }
        return uiObject2s;
    }

    /**
     * @param clazz       要找的class对象
     * @param isClickable class对象是否可点击
     * @param minWidth    class对象最小宽度
     * @param maxWidth    class对象最大宽度
     * @param minHeight   class对象最小高度
     * @param maxHeight   class对象最大高度
     * @param minX        class对象最小x坐标
     * @param maxX        class对象最大x坐标
     * @param minY        class对象最小y坐标
     * @param maxY        class对象最大y坐标
     */
    public List<UiObject2> getUiObject2s(String clazz, boolean isClickable, double minWidth, double maxWidth, double minHeight, double maxHeight,
                                         double minX, double maxX, double minY, double maxY) {
        List<UiObject2> uiObject2s = new ArrayList<>();
        try {
            uiObject2s = getUiObject2sByClazz(clazz);
            for (int i = 0; i < uiObject2s.size(); i++) {
                UiObject2 uiObject2 = uiObject2s.get(i);
                // 避免报StaleObjectException(底层控件被销毁)错误而获取不到控件，建议重复获取
                try {
                    if (isClickable && !uiObject2.isClickable()) {
                        uiObject2s.remove(i);
                        i--;
                        continue;
                    }
                } catch (StaleObjectException e) {
                    uiObject2s.remove(i);
                    i--;
                    continue;
                }
                int uiObject2Left = uiObject2.getVisibleBounds().left;
                int uiObject2Right = uiObject2.getVisibleBounds().right;
                int uiObject2Top = uiObject2.getVisibleBounds().top;
                int uiObject2Bottom = uiObject2.getVisibleBounds().bottom;
                int uiObject2Width = uiObject2Right - uiObject2Left;
                int uiObject2Height = uiObject2Bottom - uiObject2Top;
                if (uiObject2Width < (width * minWidth) || uiObject2Width > (width * maxWidth)
                        || uiObject2Height < (height * minHeight) || uiObject2Height > (height * maxHeight)
                        || uiObject2Left < (width * minX) || uiObject2Right > (width * maxX)
                        || uiObject2Top < (height * minY) || uiObject2Bottom > (height * maxY)) {
                    uiObject2s.remove(i);
                    i--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uiObject2s;
    }

    public List<UiObject2> getUiObject2sByChildClazz(String clazz, boolean isClickable, String childClazz, double minWidth, double maxWidth,
                                                     double minHeight, double maxHeight, double minX, double maxX, double minY, double maxY) {
        List<UiObject2> uiObject2s = new ArrayList<>();
        try {
            uiObject2s = getUiObject2sByClazz(clazz);
            for (int i = 0; i < uiObject2s.size(); i++) {
                UiObject2 uiObject2 = uiObject2s.get(i);
                // 避免报StaleObjectException(底层控件被销毁)错误而获取不到控件，建议重复获取
                try {
                    if (isClickable && !uiObject2.isClickable()) {
                        uiObject2s.remove(i);
                        i--;
                        continue;
                    }
                } catch (StaleObjectException e) {
                    uiObject2s.remove(i);
                    i--;
                    continue;
                }

                if (!isContainChildClazz(uiObject2, childClazz)) {
                    continue;
                }

                int uiObject2Left = uiObject2.getVisibleBounds().left;
                int uiObject2Right = uiObject2.getVisibleBounds().right;
                int uiObject2Top = uiObject2.getVisibleBounds().top;
                int uiObject2Bottom = uiObject2.getVisibleBounds().bottom;
                int uiObject2Width = uiObject2Right - uiObject2Left;
                int uiObject2Height = uiObject2Bottom - uiObject2Top;
                if (uiObject2Width < (width * minWidth) || uiObject2Width > (width * maxWidth)
                        || uiObject2Height < (height * minHeight) || uiObject2Height > (height * maxHeight)
                        || uiObject2Left < (width * minX) || uiObject2Right > (width * maxX)
                        || uiObject2Top < (height * minY) || uiObject2Bottom > (height * maxY)) {
                    uiObject2s.remove(i);
                    i--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uiObject2s;
    }

    // 排序可操作控件：相比冒泡排序，直接插入的稳定、及耗时更优
    public List<UiObject2> sortUiObject2s(List<UiObject2> uiObjects) {
        try {
            // 先根据x排序
            for (int i = 1; i < uiObjects.size(); i++) {
                UiObject2 uiObject1 = uiObjects.get(i); // 取出下一个元素，在已经排序的元素序列中从后向前扫描
                for (int j = i; j >= 0; j--) {
                    if (j > 0 && uiObjects.get(j - 1).getVisibleBounds().left > uiObject1.getVisibleBounds().left) {
                        uiObjects.set(j, uiObjects.get(j - 1)); // 如果该元素（已排序）大于取出的元素temp，将该元素移到下一位置
                    } else {
                        // 将新元素插入到该位置后
                        uiObjects.set(j, uiObject1);
                        break;
                    }
                }
            }
            // 再根据y排序
            for (int i = 1; i < uiObjects.size(); i++) {
                UiObject2 uiObject1 = uiObjects.get(i); // 取出下一个元素，在已经排序的元素序列中从后向前扫描
                for (int j = i; j >= 0; j--) {
                    if (j > 0 && uiObjects.get(j - 1).getVisibleBounds().top > uiObject1.getVisibleBounds().top) {
                        uiObjects.set(j, uiObjects.get(j - 1)); // 如果该元素（已排序）大于取出的元素temp，将该元素移到下一位置
                    } else {
                        // 将新元素插入到该位置后
                        uiObjects.set(j, uiObject1);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uiObjects;
    }

    // 获取页面所有文本信息
    public List<String> getTexts(List<UiObject2> uiObjects) {
        List<String> texts = new ArrayList<>();
        try {
            for (UiObject2 uiObject : uiObjects) {
                String text = uiObject.getText();
                if (text == null || text.trim().equals("")) {
                    text = uiObject.getContentDescription();
                }
                if (text != null && !text.trim().equals("")) {
                    texts.add(text);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return texts;
    }

    public List<UiObject2> waitUiObject2s(String clazz, boolean isClickable, double minWidth, double maxWidth, double minHeight, double maxHeight,
                                          double minX, double maxX, double minY, double maxY, int number) {
        List<UiObject2> uiObject2s = getUiObject2s(clazz, isClickable, minWidth, maxWidth, minHeight, maxHeight, minX, maxX, minY, maxY);
        for (int i = 0; i < number; i++) {
            if (uiObject2s == null || uiObject2s.size() == 0) {
                sleep(TIMEOUT_SHORT);
                uiObject2s = getUiObject2s(clazz, isClickable, minWidth, maxWidth, minHeight, maxHeight, minX, maxX, minY, maxY);
            } else {
                break;
            }
        }
        return uiObject2s;
    }

    public UiObject2 waitUiObject2ByText(String text, int mills) {
        try {
            Log.i("onuszhao", CommonUtil.getCurTimeForLog() + "     wait " + text + " found");
            return device.wait(Until.findObject(By.text(text)), mills);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject2 waitUiObject2ByTextContains(String text, int mills) {
        try {
            return device.wait(Until.findObject(By.textContains(text)), mills);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject2 waitUiObject2ByDesc(String desc, long mills) {
        try {
            return device.wait(Until.findObject(By.desc(desc)), mills);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject2 waitUiObject2ByDescContains(String desc, long mills) {
        try {
            return device.wait(Until.findObject(By.descContains(desc)), mills);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject2 waitUiObject2ByRes(String res, long mills) {
        try {
            return device.wait(Until.findObject(By.res(res)), mills);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> waitUiObject2sByText(String text, int mills) {
        try {
            return device.wait(Until.findObjects(By.text(text)), mills);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> waitUiObject2sByTextContains(String text, int mills) {
        try {
            return device.wait(Until.findObjects(By.textContains(text)), mills);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> waitUiObject2sByDesc(String desc, long mills) {
        try {
            return device.wait(Until.findObjects(By.desc(desc)), mills);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> waitUiObject2sByDescContains(String desc, long mills) {
        try {
            return device.wait(Until.findObjects(By.descContains(desc)), mills);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> waitUiObject2sByRes(String res, long mills) {
        try {
            return device.wait(Until.findObjects(By.res(res)), mills);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UiObject2> waitUiObject2sByClazz(String clazz, long mills) {
        try {
            sleep(TIMEOUT_VERY_SHORT);  // 统一等1s，避免元素查找失败
            return device.wait(Until.findObjects(By.clazz(clazz)), mills);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 自动滚动查找元素
     *
     * @param scrollableClazz 可以滚动的大控件
     * @param text            要找元素的文本
     * @return
     */
    public UiObject waitScrollableUiObjectByText(String scrollableClazz, String text, boolean isHorizontal) {
        try {
            UiScrollable uiScrollable = new UiScrollable(new UiSelector().className(scrollableClazz).scrollable(true).instance(0));
            if (isHorizontal) {
                uiScrollable.setAsHorizontalList();
            }
//            uiScrollable.scrollIntoView(new UiSelector().text(text));
//            UiObject childByText = getUiObjectByText(text);
            UiObject childByText = uiScrollable.getChildByText(new UiSelector().text(text), text, true);
            return childByText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UiObject waitScrollableUiObjectByTextContains(String parentClazz, String text, boolean isHorizontal) {
        try {
            UiScrollable uiScrollable = new UiScrollable(new UiSelector().className(parentClazz).scrollable(true).instance(0));
            if (isHorizontal) {
                uiScrollable.setAsHorizontalList();
            }
            uiScrollable.scrollIntoView(new UiSelector().textContains(text));
            UiObject childByText = getUiObjectByTextContains(text);
            return childByText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isUiObject2ExistByText(String text, int mills) {
        if (waitUiObject2ByText(text, mills) == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isUiObject2ExistByTextContains(String text, int mills) {
        if (waitUiObject2ByTextContains(text, mills) == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isUiObject2ExistByDesc(String desc, long mills) {
        if (waitUiObject2ByDesc(desc, mills) == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isUiObject2ExistByDescContains(String desc, long mills) {
        if (waitUiObject2ByDescContains(desc, mills) == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isUiObject2ExistByRes(String res, long mills) {
        if (waitUiObject2ByRes(res, mills) == null) {
            return false;
        } else {
            return true;
        }
    }

    // 根据文本判断页面是否变化
    public boolean isSamePage(List<String> preTexts, List<String> curTexts) {
        boolean isSamePage = false;
        try {
            // 相差数很大则为不同页面；否则为相同页面
            int diffCount = Math.abs(preTexts.size() - curTexts.size());
            if (diffCount <= 5) {
                // 若重复数
                int repeatCount = 0;
                for (String preText : preTexts) {
                    if (curTexts.contains(preText)) {
                        repeatCount++;
                    }
                }
                if (repeatCount == 0) {
                    isSamePage = false;
                } else {
                    // 计算差异数及相似度
                    int repeatRate = repeatCount * 100 / preTexts.size();
                    if (preTexts.size() >= 10 && repeatRate >= 70) {
                        isSamePage = true;
                    } else if (preTexts.size() < 10 && repeatRate >= 50) {
                        isSamePage = true;
                    }
                }
            } else {
                isSamePage = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSamePage;
    }

    public void click(int x, int y) {
        try {
            device.click(x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickUpperLeft(UiObject2 uiObject2) {
        try {
            int upperLeftX = uiObject2.getVisibleBounds().left;
            int upperLeftY = uiObject2.getVisibleBounds().top;
            device.click(upperLeftX, upperLeftY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void longClick(int x, int y) {
        try {
            device.swipe(x, y, x, y, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void longClick(UiObject2 uiObject2) {
        try {
            int centerX = uiObject2.getVisibleCenter().x;
            int centerY = uiObject2.getVisibleCenter().y;
            device.swipe(centerX, centerY, centerX, centerY, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void swip(UiObject2 object2, String direction) {
        try {
            if (object2 != null) {
                if (direction.equals("right")) {
                    // 向右滑动
                    int centerY = object2.getVisibleCenter().y;
                    int objectLeft = object2.getVisibleBounds().left;
                    int objectRight = object2.getVisibleBounds().right;
                    int swipLeft = objectLeft + (int) ((objectRight - objectLeft) * 0.2);
                    int swipRight = objectRight + (int) ((width - objectRight) * 0.8);
                    device.swipe(swipLeft, centerY, swipRight, centerY, 10);
                } else if (direction.equals("up")) {
                    // 向上滑动
                    int centerX = object2.getVisibleCenter().x;
                    int objectTop = object2.getVisibleBounds().top;
                    int objectBottom = object2.getVisibleBounds().bottom;
                    int swipBottom = objectBottom - (int) ((objectBottom - objectTop) * 0.2);
                    int swipTop = objectTop - (int) (objectTop * 0.8);
                    device.swipe(centerX, swipBottom, centerX, swipTop, 10);
                } else if (direction.equals("down")) {
                    // 向下滑动
                    int centerX = object2.getVisibleCenter().x;
                    int objectTop = object2.getVisibleBounds().top;
                    int objectBottom = object2.getVisibleBounds().bottom;
                    int swipTop = objectTop + (int) ((objectBottom - objectTop) * 0.2);
                    int swipBottom = objectBottom + (int) ((height - objectBottom) * 0.8);
                    device.swipe(centerX, swipTop, centerX, swipBottom, 10);
                } else {
                    // 默认向左滑动
                    int centerY = object2.getVisibleCenter().y;
                    int objectLeft = object2.getVisibleBounds().left;
                    int objectRight = object2.getVisibleBounds().right;
                    int swipRight = objectRight - (int) ((objectRight - objectLeft) * 0.2);
                    int swipLeft = objectLeft - (int) (objectLeft * 0.8);
                    device.swipe(swipRight, centerY, swipLeft, centerY, 10);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void swip(double startX, double startY, double endX, double endY) {
        try {
            device.swipe((int) (width * startX), (int) (height * startY), (int) (width * endX),
                    (int) (height * endY), 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drag(double startX, double startY, double endX, double endY) {
        try {
            device.drag((int) (width * startX), (int) (height * startY), (int) (width * endX),
                    (int) (height * endY), 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void back() {
        try {
            device.pressBack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void home() {
        try {
            device.pressHome();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        try {
            device.pressDelete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enter() {
        try {
            device.pressEnter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void power() {
        try {
            device.pressKeyCode(26);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void wakeUp() {
        try {
            if (!device.isScreenOn()) {
                device.wakeUp();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openNotification() {
        try {
            device.openNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过监听NotificationListenerService，接收到清理通知消息时则清理
     */
    public void clearAllNotifications() {
        // 等待PushMonitorService启动起来
        sleep(TIMEOUT_VERY_LONG);
        // 渠道id(channelId必须要一致，否者服务会被杀死)
        String channelId = "BBTest";

        // 获取通知管理器
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 创建渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "BBTest";               // 渠道名称
            String description = "Clear all notifications!";      // 渠道描述
            int importance = NotificationManager.IMPORTANCE_DEFAULT;    // 重要性级别(此处为默认)
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            mChannel.setDescription(description);       // 渠道描述
            mChannel.enableLights(true);                // 是否显示通知指示灯
            mChannel.enableVibration(true);             // 是否振动
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // 创建通知
        int id = 92602;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) //小图标
                .setContentTitle("BBTest")
                .setContentText("Clear all notifications!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mNotificationManager.notify(id, mBuilder.build());  // 发起通知
        // 等待PushMonitorService处理完
        sleep(TIMEOUT_MEDIUM);
    }

    public void openRecentApps() {
        try {
            device.pressRecentApps();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void horizontalScreen() {
        try {
            device.setOrientationLeft();
            sleep(TIMEOUT_SHORT);
            device.unfreezeRotation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void screenshot(String filePath) {
        File file = new File(filePath);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            device.takeScreenshot(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waitUIRefresh(String pkgName, long timeoutMillis) {
        device.waitForWindowUpdate(pkgName, timeoutMillis);
    }

    public void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 常用方法
     */
    private void demo() throws RemoteException {
        device.isScreenOn();        //屏幕是否休眠
        device.wakeUp();            //点亮屏幕
        device.pressBack();         //点击硬件back
        device.pressHome();         //点击硬件home
        device.pressSearch();       //点击查找功能键
        device.pressDelete();       //点击删除键
        device.pressEnter();        //点击回车键
        device.pressMenu();         //点击菜单键
        device.pressRecentApps();   //点击在运行的APP键
        device.openNotification();  //展开通知栏
        device.openQuickSettings(); //展开快速设置栏
        device.setOrientationLeft();    //旋转屏幕
        device.unfreezeRotation();      //解冻屏幕
        device.waitForWindowUpdate(null, 5000);  // 等待加载完成
        device.pressDPadLeft();                             //方向键，向左(可编辑框移动光标等)
        device.pressDPadRight();                            //方向键，向右
        device.pressDPadDown();                             //方向键，向下
        device.pressDPadUp();                               //方向键，向上
        device.pressDPadCenter();                           //方向键，中心点，并非pressEnter()
        device.pressKeyCode(26);                             //发送KeyEvent:26-电源键
        device.pressKeyCode(5, 8);      //发送配有组合键（ALT,SHIFT）的KeyEvent。
        /**
         *  1.如果timeout内，未能完成动作，抛出异常，但等待动作完成
         *  2.如果timeout内，完成动作，不抛异常，之后执行接下来的动作
         *  3.timeout必须大于500ms，否则无意义
         */
        device.waitForIdle(550);
        /**
         *  1.出现WindowContentUpdate事件便停止等待，若packageName为null，则立即结束等待
         *  2.核心监听AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
         *  释:represents the event of change in the content of a window. This change can be adding/removing view, changing a view size
         */
        device.waitForWindowUpdate("pkg", 500);
        /**
         *  1.若newWindow事件若发生，程序将不再等待，继续执行后续动作
         *  2.若scroll事件发生，程序将等待timeout，然后在继续后续动作
         */
        device.performActionAndWait(new Runnable() {
            @Override
            public void run() {

            }
        }, Until.newWindow(), 5000);
        // 若找到符合查询条件的UI，则Wait结束，否则继续Wait，直至超时。
        Until.findObject(By.text(""));
        Until.findObjects(By.text(""));
        Until.hasObject(By.text(""));
        // 若找到符合查询条件的UI，则继续Wait，直至超时，否则，Wait结束。
        Until.gone(By.text(""));
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        /* new for androidx.test*/
        PackageManager pm = getApplicationContext().getPackageManager();
        /* old for com.android.support.test*/
//        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    public void bindService() {
        try {
            // Create the service Intent.
            Intent serviceIntent = new Intent(context, LocalService.class);

            // Data can be passed to the service via the Intent.
//            serviceIntent.putExtra(LocalService.SEED_KEY, 42L);

            // Bind the service and grab a reference to the binder.
//            IBinder binder = mServiceRule.bindService(serviceIntent);
            ServiceConnection connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    System.out.println("onServiceConnected");
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    System.out.println("onServiceDisconnected");
                }
            };
            IBinder binder = mServiceRule.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

            // Get the reference to the service, or you can call
            // public methods on the binder directly.
            LocalService service = ((LocalService.LocalBinder) binder).getService();

            // Verify that the service is working correctly.
            assertThat(service.getRandomInt(), is(any(Integer.class)));

//            getApplicationContext().startService(serviceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unbindService() {
        mServiceRule.unbindService();
    }

}
