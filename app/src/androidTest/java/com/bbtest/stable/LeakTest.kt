package com.bbtest.stable

import android.Manifest
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.bbtest.common.MonkeyCommon
import com.bbtest.common.ShellCommon
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.FileUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * @Author: onuszhao
 * @Date: 2021/1/8 11:37
 * @Description: 写长线程跑，容易被系统杀掉，且加入电池优化策略也没啥用，故切换至5分钟跑一次
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class LeakTest : MonkeyCommon() {
    var resultFolder: File = File(rootFolder, "leak")
    var leakFile: File = File(resultFolder, "leak.txt")
    private val leakInfoFile = File(downloadFile, "leak.txt")

    private var pkgName = ""
    private var activity = ""

    @Before
    override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder)
        FileUtil.createFile(leakFile)
        // 获取需要跑的包相关信息
        val canWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        val grantState = PackageManager.PERMISSION_GRANTED
        val canRead = leakInfoFile.canRead()
        val testInfo = FileUtil.readFile(leakInfoFile).trim { it <= ' ' }
        Log.d("onuszhao", "canWrite=$canWrite  grantState=$grantState  canRead=$canRead  testInfo=$testInfo")
        val testInfoParts = testInfo.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        // 默认包名
        pkgName = testInfoParts.getOrNull(0) ?: ""
        if (TextUtils.isEmpty(pkgName)) {
            pkgName = "com.cloudview.novel"
        }
        // 获取activity(解决一个应用存在多个主activity)
        val mainActivities = ShellCommon.getActivities(device, pkgName, null)
        for (mainActivity in mainActivities) {
            ShellCommon.amStartApp(device, mainActivity, leakFile)
            CommonUtil.sleep(5000)
            if (!ShellCommon.isAppBackstage(device, pkgName)) {
                activity = mainActivity
                break
            }
        }
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  " + pkgName + "\n", leakFile)
    }

    @Test
    fun testLeak() {
        try {
            val startTime = System.currentTimeMillis()
            var endTime: Long = 0
            while (true) {
                //  先确认是否在前台
                if (isAppBackstage(pkgName, leakFile)) {
                    //  再判断浏览器进程是否存在，并切到前台/启动浏览器
                    if (isProcessExist(pkgName, leakFile)) {
                        startActivity(1000, activity, leakFile)
                    } else {
                        startActivity(5000, activity, leakFile)
                    }

                    //  浏览器内弹窗，back返回
                    for (i in 0..2) {
                        if (isAppBackstage(pkgName, leakFile)) {
                            pressBack(leakFile)
                        } else {
                            break
                        }
                    }
                }

                val adButtons = getUiObject2sByClazz("android.widget.Button")
                val adImageClose = getUiObject2s("android.widget.ImageView", true, 0.1, 0.2, 0.0, 0.2, 0.0, 0.3, 0.04, 0.3).getOrNull(0)
                if (adButtons != null && adButtons.size > 0) {
                    adButtons.getOrNull(0)?.let {
                        it.click()
                        sleep(500)
                    }
                } else if (adImageClose != null && !adImageClose.contentDescription.equals("back")) {
                    adImageClose.click()
                    sleep(500)
                } else {
                    swipeLeft(leakFile)
                    sleep(500)
                }

                // 计算时间
                endTime = System.currentTimeMillis()
                val costTime = endTime - startTime
                if (costTime > 5 * 60 * 1000) {
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }
}
