package com.bbtest.common

import android.app.Instrumentation
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.UiAutomation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ServiceTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Configurator
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.bbtest.LocalService
import com.bbtest.LocalService.LocalBinder
import com.bbtest.R
import com.bbtest.utils.CommonUtil.getCurTimeForLog
import com.bbtest.utils.FileUtil.createFolder
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.io.File
import kotlin.math.abs

/**
 * @author onuszhao
 * 注：1.单个test脚本尽量控制时间在5分钟以内，以避免被手机杀死
 * 2.操作完后尽量等待下,再查找下一个界面元素
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 21)
open class BaseCommon {

    lateinit var context: Context
    private var instrumentation: Instrumentation? = null
    var uiAutomation: UiAutomation? = null

    lateinit var device: UiDevice

    @JvmField
    var width: Int = 0

    @JvmField
    var height: Int = 0

    val TIMEOUT_VERY_LONG: Int = 20 * 1000
    val TIMEOUT_LONG: Int = 10 * 1000

    @JvmField
    val TIMEOUT_MEDIUM: Int = 5000

    @JvmField
    val TIMEOUT_SHORT: Int = 3000

    @JvmField
    val TIMEOUT_VERY_SHORT: Int = 1000

    @JvmField
    val downloadsDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    @JvmField
    val rootFolder: File = File(downloadsDir, "bbtest")

    @Before
    open fun beforeTest() {
        initCommonObject()
        //        bindService();
        initUiDevice()
        Assert.assertThat(device, CoreMatchers.notNullValue())
        // 初始化目录及文件
//        PermissionUtil.getInstance().requestFilePermissionIfNeed(context, this);
        createFolder(rootFolder)
    }

    private fun initCommonObject() {
        if (!::context.isInitialized) {
            context = ApplicationProvider.getApplicationContext<Context>()
        }
        if (instrumentation == null) {
            /** new for androidx.test  */
            instrumentation = InstrumentationRegistry.getInstrumentation()
            /** old for com.android.support.test */
//            instrumentation = InstrumentationRegistry.getInstrumentation();
        }
        if (uiAutomation == null) {
            uiAutomation = requireNotNull(instrumentation).uiAutomation
        }
    }

    private fun initUiDevice() {
        if (!::device.isInitialized) {
            /** 获取设备信息  */
            device = UiDevice.getInstance(requireNotNull(instrumentation))
            width = device.displayWidth
            height = device.displayHeight

            /** 授权测试应用  */
            grantTestPermission()

            /** 配置超时  */
            val conf = Configurator.getInstance()
            // 动作超时: 默认3000ms
            conf.setActionAcknowledgmentTimeout(1000) // 1000
            // 键盘输入延时: 默认0ms
            conf.setKeyInjectionDelay(100) // 1500
            // 滚动超时: 默认200ms
            conf.setScrollAcknowledgmentTimeout(200) // 2000
            // 空闲超时: 默认10000ms，配置成100ms，动态页面及正常查找控件都会快很多
            conf.setWaitForIdleTimeout(100) // 2500
            // 组件查找超时: 默认10000ms
            conf.setWaitForSelectorTimeout(100) //3000
        }
    }

    private fun grantTestPermission() {
        // 授权当前测试应用
        val curPkgName = context.getPackageName()
        grantRuntimePermission(curPkgName, "android.permission.READ_EXTERNAL_STORAGE")
        grantRuntimePermission(curPkgName, "android.permission.WRITE_EXTERNAL_STORAGE")
        grantRuntimePermission(curPkgName, "android.permission.READ_PHONE_STATE")
        grantRuntimePermission(curPkgName, "android.permission.ACCESS_WIFI_STATE")
        grantRuntimePermission(curPkgName, "android.permission.CHANGE_WIFI_STATE")
        grantRuntimePermission(curPkgName, "android.permission.ACCESS_NETWORK_STATE")
        grantRuntimePermission(curPkgName, "android.permission.CHANGE_NETWORK_STATE")
        grantRuntimePermission(curPkgName, "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE")
    }

    /**
     * 启动应用：进程会被杀掉后重启
     * 
     * @param pkgName
     */
    fun startApp(pkgName: String) {
        // Start from the home screen
        device!!.pressHome()

        // Wait for launcher
        val launcherPackage = this.launcherPackageName
        Assert.assertThat<String?>(launcherPackage, CoreMatchers.notNullValue())
        device!!.wait<Boolean?>(Until.hasObject(By.pkg(launcherPackage).depth(0)), TIMEOUT_MEDIUM.toLong())

        // Launch the blueprint app
        val intent = context.getPackageManager().getLaunchIntentForPackage(pkgName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // Clear out any previous instances
        context.startActivity(intent)

        // Wait for the app to appear
        device!!.wait<Boolean?>(Until.hasObject(By.pkg(pkgName).depth(0)), TIMEOUT_MEDIUM.toLong())
    }

    fun grantPermission(pkgName: String?) {
        // 授权目标测试应用
        grantRuntimePermission(pkgName, "android.permission.CAMERA")
        grantRuntimePermission(pkgName, "android.permission.READ_EXTERNAL_STORAGE")
        grantRuntimePermission(pkgName, "android.permission.WRITE_EXTERNAL_STORAGE")
        grantRuntimePermission(pkgName, "android.permission.ACCESS_FINE_LOCATION") // GPS定位权限
        grantRuntimePermission(pkgName, "android.permission.ACCESS_COARSE_LOCATION") // WiFi定位权限
        grantRuntimePermission(pkgName, "android.permission.READ_PHONE_STATE") // 运营商定位权限
        grantRuntimePermission(pkgName, "android.permission.WRITE_SETTINGS") // 桌面数据库访问权限
        grantRuntimePermission(pkgName, "android.permission.SYSTEM_ALERT_WINDOW") // 悬浮框权限
        grantRuntimePermission(pkgName, "android.permission.REQUEST_INSTALL_PACKAGES") //未知来源安装权限
        grantRuntimePermission(pkgName, "android.permission.AUTHENTICATE_ACCOUNTS") //系统ACCOUNT认证权限
        grantRuntimePermission(pkgName, "android.permission.MANAGE_EXTERNAL_STORAGE") // 所有文件权限(targetSDK升级30后)
    }

    private fun grantRuntimePermission(pkgName: String?, permission: String?) {
        try {
            uiAutomation!!.grantRuntimePermission(pkgName, permission)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    @After
    open fun afterTest() {
//        unbindService();
    }

    fun getUiObjectByText(text: String): UiObject? {
        try {
            return device!!.findObject(UiSelector().text(text))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getUiObjectByTextContains(text: String): UiObject? {
        try {
            return device!!.findObject(UiSelector().textContains(text))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getUiObjectByDesc(desc: String): UiObject? {
        try {
            return device!!.findObject(UiSelector().description(desc))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getUiObjectByDescContains(desc: String): UiObject? {
        try {
            return device!!.findObject(UiSelector().descriptionContains(desc))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getUiObjectByRes(res: String): UiObject? {
        try {
            return device!!.findObject(UiSelector().resourceId(res))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getUiObject2ByText(text: String): UiObject2? {
        try {
            return device!!.findObject(By.text(text))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getUiObject2ByTextContains(text: String): UiObject2? {
        try {
            return device!!.findObject(By.textContains(text))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getUiObject2ByDesc(desc: String): UiObject2? {
        try {
            return device!!.findObject(By.desc(desc))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getUiObject2ByDescContains(desc: String): UiObject2? {
        try {
            return device!!.findObject(By.descContains(desc))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getUiObject2ByRes(res: String): UiObject2? {
        try {
            return device!!.findObject(By.res(res))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getUiObject2sByText(text: String): MutableList<UiObject2> {
        try {
            return device!!.findObjects(By.text(text))
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    fun getUiObject2sByTextContains(text: String): MutableList<UiObject2> {
        try {
            return device!!.findObjects(By.textContains(text))
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    fun getUiObject2sByDesc(desc: String): MutableList<UiObject2> {
        try {
            return device!!.findObjects(By.desc(desc))
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    fun getUiObject2sByDescContains(desc: String): MutableList<UiObject2> {
        try {
            return device!!.findObjects(By.descContains(desc))
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    fun getUiObject2sByRes(res: String): MutableList<UiObject2> {
        try {
            return device!!.findObjects(By.res(res))
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    fun getUiObject2sByClazz(clazz: String): MutableList<UiObject2> {
        try {
            return device!!.findObjects(By.clazz(clazz))
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    /**
     * @param text  寻找对象的文本内容
     * @param clazz 寻找对象的class属性
     */
    fun getUiObject2(text: String, clazz: String?, minWidth: Double, minHeight: Double): UiObject2? {
        var uiObject2: UiObject2? = null
        try {
            val textObjects = getUiObject2sByText(text)
            for (textObject in textObjects) {
                if (textObject.getClassName() == clazz) {
                    val textObjectWidth = textObject.getVisibleBounds().right - textObject.getVisibleBounds().left
                    val textObjectHeight = textObject.getVisibleBounds().bottom - textObject.getVisibleBounds().top
                    if (textObjectWidth >= (width * minWidth) && textObjectHeight >= (height * minHeight)) {
                        uiObject2 = textObject
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uiObject2
    }

    /**
     * @param srcClazz  要找的class对象
     * @param childText class对象子模块中包含的文本
     */
    fun getUiObject2ByChildText(srcClazz: String, childText: String?): UiObject2? {
        var uiObject2: UiObject2? = null
        try {
            val clazzViews = getUiObject2sByClazz(srcClazz)
            for (clazzView in clazzViews) {
                if (isContainChildText(clazzView, childText)) {
                    uiObject2 = clazzView
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uiObject2
    }

    /**
     * @param srcClazz       再srcClazz的对象中查找
     * @param isSrcClickable srcClazz的对象是否可点击
     * @param childText      srcClazz对象的子模块中包含的文本
     * @param dstClazz       找到dstClazz的对象
     */
    fun getUiObject2ByChildText(srcClazz: String, isSrcClickable: Boolean, childText: String?, dstClazz: String?): UiObject2? {
        var uiObject2: UiObject2? = null
        try {
            val clazzViews = getUiObject2sByClazz(srcClazz)
            for (clazzView in clazzViews) {
                // 避免报StaleObjectException(底层控件被销毁)错误而获取不到控件，建议重复获取
                try {
                    if (isSrcClickable && !clazzView.isClickable()) {
                        continue
                    }
                } catch (e: StaleObjectException) {
                    continue
                }

                if (isContainChildText(clazzView, childText)) {
                    uiObject2 = getContainUiObject2(clazzView, dstClazz)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uiObject2
    }

    fun getUiObject2ByChildClazz(srcClazz: String, isSrcClickable: Boolean, childClazz: String?): UiObject2? {
        var uiObject2: UiObject2? = null
        try {
            val clazzViews = getUiObject2sByClazz(srcClazz)
            for (clazzView in clazzViews) {
                // 避免报StaleObjectException(底层控件被销毁)错误而获取不到控件，建议重复获取
                try {
                    if (isSrcClickable && !clazzView.isClickable()) {
                        continue
                    }
                } catch (e: StaleObjectException) {
                    continue
                }

                if (isContainChildClazz(clazzView, childClazz)) {
                    uiObject2 = clazzView
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uiObject2
    }

    fun getContainUiObject2(srcUiObject2: UiObject2, dstClazz: String?): UiObject2? {
        var dstUiObject2: UiObject2? = null
        try {
            val childrens = srcUiObject2.getChildren()
            for (children in childrens) {
                if (children.getClassName() == dstClazz) {
                    dstUiObject2 = children
                    break
                }

                if (children.getChildCount() > 0) {
                    dstUiObject2 = getContainUiObject2(children, dstClazz)
                    if (dstUiObject2 != null) {
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dstUiObject2
    }

    /**
     * @param srcUiObject2 在指定对象下找
     * @param dstClazz     找为clazz的对象
     */
    fun getChildUiObject2(
        srcUiObject2: UiObject2, isDstClickable: Boolean, dstClazz: String?, minWidth: Double, maxWidth: Double,
        minHeight: Double, maxHeight: Double, minX: Double, maxX: Double, minY: Double, maxY: Double, isAllChild: Boolean
    ): UiObject2? {
        var childUiObject2: UiObject2? = null
        try {
            val childrens = srcUiObject2.getChildren()
            for (children in childrens) {
                // 避免报StaleObjectException(底层控件被销毁)错误而获取不到控件，建议重复获取
                try {
                    if (isDstClickable && !children.isClickable()) {
                        continue
                    }
                } catch (e: StaleObjectException) {
                    continue
                }

                // class匹配不上，直接下一个
                if (children.getClassName() == dstClazz) {
                    val childrenLeft = children.getVisibleBounds().left
                    val childrenRight = children.getVisibleBounds().right
                    val childrenTop = children.getVisibleBounds().top
                    val childrenBottom = children.getVisibleBounds().bottom
                    val childrenWidth = childrenRight - childrenLeft
                    val childrenHeight = childrenBottom - childrenTop
                    if (childrenWidth >= (width * minWidth) && childrenWidth <= (width * maxWidth) && childrenHeight >= (height * minHeight) && childrenHeight <= (height * maxHeight) && childrenLeft >= (width * minX) && childrenRight <= (width * maxX) && childrenTop >= (height * minY) && childrenBottom <= (height * maxY)) {
                        childUiObject2 = children
                        break
                    }
                }

                if (isAllChild) {
                    if (children.getChildCount() > 0) {
                        childUiObject2 = getChildUiObject2(
                            children,
                            isDstClickable,
                            dstClazz,
                            minWidth,
                            maxWidth,
                            minHeight,
                            maxHeight,
                            minX,
                            maxX,
                            minY,
                            maxY,
                            isAllChild
                        )
                        if (childUiObject2 != null) {
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return childUiObject2
    }

    fun getScrollableUiObject2(clazz: String): UiObject2? {
        var scrollableUiObject2: UiObject2? = null
        try {
            val clazzObjects = getUiObject2sByClazz(clazz)
            for (clazzObject in clazzObjects) {
                if (clazzObject.isScrollable()) {
                    scrollableUiObject2 = clazzObject
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return scrollableUiObject2
    }

    fun isContainChildClazz(srcUiObject2: UiObject2, childClazz: String?): Boolean {
        var isFound = false
        try {
            val childrens = srcUiObject2.getChildren()
            for (children in childrens) {
                val tmpChildClazz = children.getClassName()
                if (tmpChildClazz != null && tmpChildClazz == childClazz) {
                    isFound = true
                    break
                }

                if (children.getChildCount() > 0) {
                    isFound = isContainChildClazz(children, childClazz)
                    if (isFound) {
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isFound
    }

    fun isContainChildText(srcUiObject2: UiObject2, childText: String?): Boolean {
        var isFound = false
        try {
            val childrens = srcUiObject2.getChildren()
            for (children in childrens) {
                val tmpChildText = children.getText()
                if (tmpChildText != null && tmpChildText == childText) {
                    isFound = true
                    break
                }

                if (children.getChildCount() > 0) {
                    isFound = isContainChildText(children, childText)
                    if (isFound) {
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isFound
    }

    fun getText(uiObject2: UiObject2, isContainChild: Boolean): String? {
        var text: String? = ""
        try {
            text = uiObject2.getText()
            if ((text == null || text == "") && isContainChild) {
                val childrens = uiObject2.getChildren()
                for (children in childrens) {
                    text = getTextOrDesc(children, isContainChild)

                    if (text != null && text != "") {
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return text
    }

    // 获取页面所有文本信息
    fun getTextOrDesc(uiObject2: UiObject2, isContainChild: Boolean): String? {
        var text: String? = ""
        try {
            text = uiObject2.getText()
            if (text == null || text == "") {
                text = uiObject2.getContentDescription()
            }
            if ((text == null || text == "") && isContainChild) {
                val childrens = uiObject2.getChildren()
                for (children in childrens) {
                    text = getTextOrDesc(children, isContainChild)

                    if (text != null && text != "") {
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return text
    }

    fun getRootObject(): UiObject2 = requireNotNull(rootUiObject)

    private val rootUiObject: UiObject2?
        get() {
            val rootContent = waitUiObject2ByRes("android:id/content", TIMEOUT_MEDIUM.toLong())
            rootContent?.let {
                val rootRect = it.getVisibleBounds()
                var latestRootObject = it
                while (true) {
                    var isFound = false
                    val latestChildrens = latestRootObject.getChildren()
                    for (latestChildren in latestChildrens) {
                        val latestChildrenRect = latestChildren.getVisibleBounds()
                        if (latestChildrenRect != rootRect) {
                            isFound = true
                            break
                        }
                    }

                    if (isFound) {
                        break
                    } else {
                        latestRootObject = latestChildrens.get(latestChildrens.size - 1)
                    }
                }
                return latestRootObject
            }
            return null
        }

    // 获取所有可操作控件
    fun getAllUiObject2s(uiObject: UiObject2): MutableList<UiObject2> {
        val uiObjects: MutableList<UiObject2> = ArrayList<UiObject2>()
        try {
            val childrens = uiObject.getChildren()
            for (children in childrens) {
                uiObjects.add(children)
                uiObjects.addAll(getAllUiObject2s(children))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uiObjects
    }

    fun getClickableUiObject2s(uiObject: UiObject2): MutableList<UiObject2> {
        val uiObjects: MutableList<UiObject2> = ArrayList<UiObject2>()
        try {
            val childrens = uiObject.getChildren()
            for (children in childrens) {
                if (children.isClickable()) {
                    uiObjects.add(children)
                }

                if (children.getChildCount() > 0) {
                    uiObjects.addAll(getClickableUiObject2s(children))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uiObjects
    }

    var count: Int = 0

    fun getClickableUiObject2s(uiObject: UiObject2, uiObjectList: MutableList<UiObject2>): MutableList<UiObject2> {
        try {
            val childrens = uiObject.getChildren()
            for (children in childrens) {
                count++
                if (children.isClickable()) {
                    uiObjectList.add(children)
                }

                if (children.getChildCount() > 0) {
                    getClickableUiObject2s(children, uiObjectList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uiObjectList
    }

    fun getUiObject2s(
        clazz: String, minWidth: Double, maxWidth: Double, minHeight: Double, maxHeight: Double,
        minX: Double, maxX: Double, minY: Double, maxY: Double
    ): MutableList<UiObject2> {
        var uiObject2s = getUiObject2s(clazz, true, minWidth, maxWidth, minHeight, maxHeight, minX, maxX, minY, maxY)
        if (uiObject2s.isEmpty()) {
            uiObject2s = getUiObject2s(clazz, false, minWidth, maxWidth, minHeight, maxHeight, minX, maxX, minY, maxY)
        }
        return uiObject2s
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
    fun getUiObject2s(
        clazz: String, isClickable: Boolean, minWidth: Double, maxWidth: Double, minHeight: Double, maxHeight: Double,
        minX: Double, maxX: Double, minY: Double, maxY: Double
    ): MutableList<UiObject2> {
        var uiObject2s: MutableList<UiObject2> = ArrayList()
        try {
            uiObject2s = getUiObject2sByClazz(clazz)
            var i = 0
            while (i < uiObject2s.size) {
                val uiObject2 = uiObject2s.get(i)
                // 避免报StaleObjectException(底层控件被销毁)错误而获取不到控件，建议重复获取
                try {
                    if (isClickable && !uiObject2.isClickable()) {
                        uiObject2s.removeAt(i)
                        i--
                        i++
                        continue
                    }
                } catch (e: StaleObjectException) {
                    uiObject2s.removeAt(i)
                    i--
                    i++
                    continue
                }
                val uiObject2Left = uiObject2.getVisibleBounds().left
                val uiObject2Right = uiObject2.getVisibleBounds().right
                val uiObject2Top = uiObject2.getVisibleBounds().top
                val uiObject2Bottom = uiObject2.getVisibleBounds().bottom
                val uiObject2Width = uiObject2Right - uiObject2Left
                val uiObject2Height = uiObject2Bottom - uiObject2Top
                if (uiObject2Width < (width * minWidth) || uiObject2Width > (width * maxWidth) || uiObject2Height < (height * minHeight) || uiObject2Height > (height * maxHeight) || uiObject2Left < (width * minX) || uiObject2Right > (width * maxX) || uiObject2Top < (height * minY) || uiObject2Bottom > (height * maxY)) {
                    uiObject2s.removeAt(i)
                    i--
                }
                i++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uiObject2s
    }

    fun getUiObject2sByChildClazz(
        clazz: String, isClickable: Boolean, childClazz: String?, minWidth: Double, maxWidth: Double,
        minHeight: Double, maxHeight: Double, minX: Double, maxX: Double, minY: Double, maxY: Double
    ): MutableList<UiObject2> {
        var uiObject2s: MutableList<UiObject2> = ArrayList()
        try {
            uiObject2s = getUiObject2sByClazz(clazz)
            var i = 0
            while (i < uiObject2s.size) {
                val uiObject2 = uiObject2s.get(i)
                // 避免报StaleObjectException(底层控件被销毁)错误而获取不到控件，建议重复获取
                try {
                    if (isClickable && !uiObject2.isClickable()) {
                        uiObject2s.removeAt(i)
                        i--
                        i++
                        continue
                    }
                } catch (e: StaleObjectException) {
                    uiObject2s.removeAt(i)
                    i--
                    i++
                    continue
                }

                if (!isContainChildClazz(uiObject2, childClazz)) {
                    i++
                    continue
                }

                val uiObject2Left = uiObject2.getVisibleBounds().left
                val uiObject2Right = uiObject2.getVisibleBounds().right
                val uiObject2Top = uiObject2.getVisibleBounds().top
                val uiObject2Bottom = uiObject2.getVisibleBounds().bottom
                val uiObject2Width = uiObject2Right - uiObject2Left
                val uiObject2Height = uiObject2Bottom - uiObject2Top
                if (uiObject2Width < (width * minWidth) || uiObject2Width > (width * maxWidth) || uiObject2Height < (height * minHeight) || uiObject2Height > (height * maxHeight) || uiObject2Left < (width * minX) || uiObject2Right > (width * maxX) || uiObject2Top < (height * minY) || uiObject2Bottom > (height * maxY)) {
                    uiObject2s.removeAt(i)
                    i--
                }
                i++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uiObject2s
    }

    // 排序可操作控件：相比冒泡排序，直接插入的稳定、及耗时更优
    fun sortUiObject2s(uiObjects: MutableList<UiObject2>): MutableList<UiObject2>? {
        try {
            // 先根据x排序
            for (i in 1..<uiObjects.size) {
                val uiObject1 = uiObjects.get(i) // 取出下一个元素，在已经排序的元素序列中从后向前扫描
                for (j in i downTo 0) {
                    if (j > 0 && uiObjects.get(j - 1).getVisibleBounds().left > uiObject1.getVisibleBounds().left) {
                        uiObjects.set(j, uiObjects.get(j - 1)) // 如果该元素（已排序）大于取出的元素temp，将该元素移到下一位置
                    } else {
                        // 将新元素插入到该位置后
                        uiObjects.set(j, uiObject1)
                        break
                    }
                }
            }
            // 再根据y排序
            for (i in 1..<uiObjects.size) {
                val uiObject1 = uiObjects.get(i) // 取出下一个元素，在已经排序的元素序列中从后向前扫描
                for (j in i downTo 0) {
                    if (j > 0 && uiObjects.get(j - 1).getVisibleBounds().top > uiObject1.getVisibleBounds().top) {
                        uiObjects.set(j, uiObjects.get(j - 1)) // 如果该元素（已排序）大于取出的元素temp，将该元素移到下一位置
                    } else {
                        // 将新元素插入到该位置后
                        uiObjects.set(j, uiObject1)
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uiObjects
    }

    // 获取页面所有文本信息
    fun getTexts(uiObjects: MutableList<UiObject2>): MutableList<String> {
        val texts: MutableList<String> = ArrayList<String>()
        try {
            for (uiObject in uiObjects) {
                var text = uiObject.getText()
                if (text == null || text.trim { it <= ' ' } == "") {
                    text = uiObject.getContentDescription()
                }
                if (text != null && text.trim { it <= ' ' } != "") {
                    texts.add(text)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return texts
    }

    fun waitUiObject2s(
        clazz: String, isClickable: Boolean, minWidth: Double, maxWidth: Double, minHeight: Double, maxHeight: Double,
        minX: Double, maxX: Double, minY: Double, maxY: Double, number: Int
    ): MutableList<UiObject2> {
        var uiObject2s = getUiObject2s(clazz, isClickable, minWidth, maxWidth, minHeight, maxHeight, minX, maxX, minY, maxY)
        for (i in 0..<number) {
            if (uiObject2s.isEmpty()) {
                sleep(TIMEOUT_SHORT.toLong())
                uiObject2s = getUiObject2s(clazz, isClickable, minWidth, maxWidth, minHeight, maxHeight, minX, maxX, minY, maxY)
            } else {
                break
            }
        }
        return uiObject2s
    }

    fun waitUiObject2ByText(text: String, mills: Int): UiObject2? {
        try {
            Log.i("onuszhao", getCurTimeForLog() + "     wait " + text + " found")
            return device!!.wait<UiObject2?>(Until.findObject(By.text(text)), mills.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun waitUiObject2ByTextContains(text: String, mills: Int): UiObject2? {
        try {
            return device!!.wait<UiObject2?>(Until.findObject(By.textContains(text)), mills.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun waitUiObject2ByDesc(desc: String, mills: Long): UiObject2? {
        try {
            return device!!.wait<UiObject2?>(Until.findObject(By.desc(desc)), mills)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun waitUiObject2ByDescContains(desc: String, mills: Long): UiObject2? {
        try {
            return device!!.wait<UiObject2?>(Until.findObject(By.descContains(desc)), mills)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun waitUiObject2ByRes(res: String, mills: Long): UiObject2? {
        try {
            return device!!.wait<UiObject2>(Until.findObject(By.res(res)), mills)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun waitUiObject2sByText(text: String, mills: Int): MutableList<UiObject2> {
        try {
            return device!!.wait<MutableList<UiObject2>?>(Until.findObjects(By.text(text)), mills.toLong()) ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    fun waitUiObject2sByTextContains(text: String, mills: Int): MutableList<UiObject2> {
        try {
            return device!!.wait<MutableList<UiObject2>?>(Until.findObjects(By.textContains(text)), mills.toLong()) ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    fun waitUiObject2sByDesc(desc: String, mills: Long): MutableList<UiObject2> {
        try {
            return device!!.wait<MutableList<UiObject2>?>(Until.findObjects(By.desc(desc)), mills) ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    fun waitUiObject2sByDescContains(desc: String, mills: Long): MutableList<UiObject2> {
        try {
            return device!!.wait<MutableList<UiObject2>?>(Until.findObjects(By.descContains(desc)), mills) ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    fun waitUiObject2sByRes(res: String, mills: Long): MutableList<UiObject2> {
        try {
            return device!!.wait<MutableList<UiObject2>?>(Until.findObjects(By.res(res)), mills) ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    fun waitUiObject2sByClazz(clazz: String, mills: Long): MutableList<UiObject2> {
        try {
            sleep(TIMEOUT_VERY_SHORT.toLong()) // 统一等1s，避免元素查找失败
            return device!!.wait<MutableList<UiObject2>?>(Until.findObjects(By.clazz(clazz)), mills) ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    /**
     * 自动滚动查找元素
     * 
     * @param scrollableClazz 可以滚动的大控件
     * @param text            要找元素的文本
     * @return
     */
    fun waitScrollableUiObjectByText(scrollableClazz: String, text: String?, isHorizontal: Boolean): UiObject? {
        try {
            val uiScrollable = UiScrollable(UiSelector().className(scrollableClazz).scrollable(true).instance(0))
            if (isHorizontal) {
                uiScrollable.setAsHorizontalList()
            }
            //            uiScrollable.scrollIntoView(new UiSelector().text(text));
//            UiObject childByText = getUiObjectByText(text);
            val childByText = uiScrollable.getChildByText(UiSelector().text(text), text, true)
            return childByText
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun waitScrollableUiObjectByTextContains(parentClazz: String, text: String, isHorizontal: Boolean): UiObject? {
        try {
            val uiScrollable = UiScrollable(UiSelector().className(parentClazz).scrollable(true).instance(0))
            if (isHorizontal) {
                uiScrollable.setAsHorizontalList()
            }
            uiScrollable.scrollIntoView(UiSelector().textContains(text))
            val childByText = getUiObjectByTextContains(text)
            return childByText
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun isUiObject2ExistByText(text: String, mills: Int): Boolean {
        if (waitUiObject2ByText(text, mills) == null) {
            return false
        } else {
            return true
        }
    }

    fun isUiObject2ExistByTextContains(text: String, mills: Int): Boolean {
        if (waitUiObject2ByTextContains(text, mills) == null) {
            return false
        } else {
            return true
        }
    }

    fun isUiObject2ExistByDesc(desc: String, mills: Long): Boolean {
        if (waitUiObject2ByDesc(desc, mills) == null) {
            return false
        } else {
            return true
        }
    }

    fun isUiObject2ExistByDescContains(desc: String, mills: Long): Boolean {
        if (waitUiObject2ByDescContains(desc, mills) == null) {
            return false
        } else {
            return true
        }
    }

    fun isUiObject2ExistByRes(res: String, mills: Long): Boolean {
        if (waitUiObject2ByRes(res, mills) == null) {
            return false
        } else {
            return true
        }
    }

    // 根据文本判断页面是否变化
    fun isSamePage(preTexts: MutableList<String>, curTexts: MutableList<String>): Boolean {
        var isSamePage = false
        try {
            // 相差数很大则为不同页面；否则为相同页面
            val diffCount = abs(preTexts.size - curTexts.size)
            if (diffCount <= 5) {
                // 若重复数
                var repeatCount = 0
                for (preText in preTexts) {
                    if (curTexts.contains(preText)) {
                        repeatCount++
                    }
                }
                if (repeatCount == 0) {
                    isSamePage = false
                } else {
                    // 计算差异数及相似度
                    val repeatRate = repeatCount * 100 / preTexts.size
                    if (preTexts.size >= 10 && repeatRate >= 70) {
                        isSamePage = true
                    } else if (preTexts.size < 10 && repeatRate >= 50) {
                        isSamePage = true
                    }
                }
            } else {
                isSamePage = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isSamePage
    }

    fun click(x: Int, y: Int) {
        try {
            device!!.click(x, y)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clickUpperLeft(uiObject2: UiObject2) {
        try {
            val upperLeftX = uiObject2.getVisibleBounds().left
            val upperLeftY = uiObject2.getVisibleBounds().top
            device!!.click(upperLeftX, upperLeftY)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun longClick(x: Int, y: Int) {
        try {
            device!!.swipe(x, y, x, y, 300)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun longClick(uiObject2: UiObject2) {
        try {
            val centerX = uiObject2.getVisibleCenter().x
            val centerY = uiObject2.getVisibleCenter().y
            device!!.swipe(centerX, centerY, centerX, centerY, 300)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun swip(object2: UiObject2?, direction: String) {
        try {
            if (object2 != null) {
                if (direction == "right") {
                    // 向右滑动
                    val centerY = object2.getVisibleCenter().y
                    val objectLeft = object2.getVisibleBounds().left
                    val objectRight = object2.getVisibleBounds().right
                    val swipLeft = objectLeft + ((objectRight - objectLeft) * 0.2).toInt()
                    val swipRight = objectRight + ((width - objectRight) * 0.8).toInt()
                    device!!.swipe(swipLeft, centerY, swipRight, centerY, 10)
                } else if (direction == "up") {
                    // 向上滑动
                    val centerX = object2.getVisibleCenter().x
                    val objectTop = object2.getVisibleBounds().top
                    val objectBottom = object2.getVisibleBounds().bottom
                    val swipBottom = objectBottom - ((objectBottom - objectTop) * 0.2).toInt()
                    val swipTop = objectTop - (objectTop * 0.8).toInt()
                    device!!.swipe(centerX, swipBottom, centerX, swipTop, 10)
                } else if (direction == "down") {
                    // 向下滑动
                    val centerX = object2.getVisibleCenter().x
                    val objectTop = object2.getVisibleBounds().top
                    val objectBottom = object2.getVisibleBounds().bottom
                    val swipTop = objectTop + ((objectBottom - objectTop) * 0.2).toInt()
                    val swipBottom = objectBottom + ((height - objectBottom) * 0.8).toInt()
                    device!!.swipe(centerX, swipTop, centerX, swipBottom, 10)
                } else {
                    // 默认向左滑动
                    val centerY = object2.getVisibleCenter().y
                    val objectLeft = object2.getVisibleBounds().left
                    val objectRight = object2.getVisibleBounds().right
                    val swipRight = objectRight - ((objectRight - objectLeft) * 0.2).toInt()
                    val swipLeft = objectLeft - (objectLeft * 0.8).toInt()
                    device!!.swipe(swipRight, centerY, swipLeft, centerY, 10)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun swip(startX: Double, startY: Double, endX: Double, endY: Double) {
        try {
            device!!.swipe(
                (width * startX).toInt(), (height * startY).toInt(), (width * endX).toInt(),
                (height * endY).toInt(), 10
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun drag(startX: Double, startY: Double, endX: Double, endY: Double) {
        try {
            device!!.drag(
                (width * startX).toInt(), (height * startY).toInt(), (width * endX).toInt(),
                (height * endY).toInt(), 10
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun back() {
        try {
            device!!.pressBack()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun home() {
        try {
            device!!.pressHome()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun delete() {
        try {
            device!!.pressDelete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun enter() {
        try {
            device!!.pressEnter()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun power() {
        try {
            device!!.pressKeyCode(26)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun wakeUp() {
        try {
            if (!device!!.isScreenOn()) {
                device!!.wakeUp()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openNotification() {
        try {
            device!!.openNotification()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 通过监听NotificationListenerService，接收到清理通知消息时则清理
     */
    fun clearAllNotifications() {
        // 等待PushMonitorService启动起来
        sleep(TIMEOUT_VERY_LONG.toLong())
        // 渠道id(channelId必须要一致，否者服务会被杀死)
        val channelId = "BBTest"

        // 获取通知管理器
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 创建渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "BBTest" // 渠道名称
            val description = "Clear all notifications!" // 渠道描述
            val importance = NotificationManager.IMPORTANCE_DEFAULT // 重要性级别(此处为默认)
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.setDescription(description) // 渠道描述
            mChannel.enableLights(true) // 是否显示通知指示灯
            mChannel.enableVibration(true) // 是否振动
            mNotificationManager.createNotificationChannel(mChannel)
        }

        // 创建通知
        val id = 92602
        val mBuilder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) //小图标
            .setContentTitle("BBTest")
            .setContentText("Clear all notifications!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        mNotificationManager.notify(id, mBuilder.build()) // 发起通知
        // 等待PushMonitorService处理完
        sleep(TIMEOUT_MEDIUM.toLong())
    }

    fun openRecentApps() {
        try {
            device!!.pressRecentApps()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun horizontalScreen() {
        try {
            device!!.setOrientationLeft()
            sleep(TIMEOUT_SHORT.toLong())
            device!!.unfreezeRotation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun screenshot(filePath: String) {
        val file = File(filePath)
        try {
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            device!!.takeScreenshot(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun waitUIRefresh(pkgName: String?, timeoutMillis: Long) {
        device!!.waitForWindowUpdate(pkgName, timeoutMillis)
    }

    fun sleep(mills: Long) {
        try {
            Thread.sleep(mills)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 常用方法
     */
    @Throws(RemoteException::class)
    private fun demo() {
        device!!.isScreenOn() //屏幕是否休眠
        device!!.wakeUp() //点亮屏幕
        device!!.pressBack() //点击硬件back
        device!!.pressHome() //点击硬件home
        device!!.pressSearch() //点击查找功能键
        device!!.pressDelete() //点击删除键
        device!!.pressEnter() //点击回车键
        device!!.pressMenu() //点击菜单键
        device!!.pressRecentApps() //点击在运行的APP键
        device!!.openNotification() //展开通知栏
        device!!.openQuickSettings() //展开快速设置栏
        device!!.setOrientationLeft() //旋转屏幕
        device!!.unfreezeRotation() //解冻屏幕
        device!!.waitForWindowUpdate(null, 5000) // 等待加载完成
        device!!.pressDPadLeft() //方向键，向左(可编辑框移动光标等)
        device!!.pressDPadRight() //方向键，向右
        device!!.pressDPadDown() //方向键，向下
        device!!.pressDPadUp() //方向键，向上
        device!!.pressDPadCenter() //方向键，中心点，并非pressEnter()
        device!!.pressKeyCode(26) //发送KeyEvent:26-电源键
        device!!.pressKeyCode(5, 8) //发送配有组合键（ALT,SHIFT）的KeyEvent。
        /**
         * 1.如果timeout内，未能完成动作，抛出异常，但等待动作完成
         * 2.如果timeout内，完成动作，不抛异常，之后执行接下来的动作
         * 3.timeout必须大于500ms，否则无意义
         */
        device!!.waitForIdle(550)
        /**
         * 1.出现WindowContentUpdate事件便停止等待，若packageName为null，则立即结束等待
         * 2.核心监听AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
         * 释:represents the event of change in the content of a window. This change can be adding/removing view, changing a view size
         */
        device!!.waitForWindowUpdate("pkg", 500)
        /**
         * 1.若newWindow事件若发生，程序将不再等待，继续执行后续动作
         * 2.若scroll事件发生，程序将等待timeout，然后在继续后续动作
         */
        device!!.performActionAndWait<Boolean?>(object : Runnable {
            override fun run() {
            }
        }, Until.newWindow(), 5000)
        // 若找到符合查询条件的UI，则Wait结束，否则继续Wait，直至超时。
        Until.findObject(By.text(""))
        Until.findObjects(By.text(""))
        Until.hasObject(By.text(""))
        // 若找到符合查询条件的UI，则继续Wait，直至超时，否则，Wait结束。
        Until.gone(By.text(""))
    }

    private val launcherPackageName: String?
        /**
         * Uses package manager to find the package name of the device launcher. Usually this package
         * is "com.android.launcher" but can be different at times. This is a generic solution which
         * works on all platforms.`
         */
        get() {
            // Create launcher Intent
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)

            // Use PackageManager to get the launcher package name
            /* new for androidx.test*/
            val pm = ApplicationProvider.getApplicationContext<Context>().getPackageManager()
            /* old for com.android.support.test*/
//        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
            val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            return resolveInfo!!.activityInfo.packageName
        }

    @Rule
    @JvmField
    val mServiceRule: ServiceTestRule = ServiceTestRule()

    fun bindService() {
        try {
            // Create the service Intent.
            val serviceIntent = Intent(context, LocalService::class.java)

            // Data can be passed to the service via the Intent.
//            serviceIntent.putExtra(LocalService.SEED_KEY, 42L);

            // Bind the service and grab a reference to the binder.
//            IBinder binder = mServiceRule.bindService(serviceIntent);
            val connection: ServiceConnection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    println("onServiceConnected")
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    println("onServiceDisconnected")
                }
            }
            val binder = mServiceRule.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)

            // Get the reference to the service, or you can call
            // public methods on the binder directly.
            val service = (binder as LocalBinder).getService()

            // Verify that the service is working correctly.
            Assert.assertThat<Int?>(service.getRandomInt(), CoreMatchers.`is`<Int?>(CoreMatchers.any<Int?>(Int::class.java)))

            //            getApplicationContext().startService(serviceIntent);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unbindService() {
        mServiceRule.unbindService()
    }
}
