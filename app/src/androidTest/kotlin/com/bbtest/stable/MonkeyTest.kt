package com.bbtest.stable

import android.Manifest
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.bbtest.common.MonkeyCommon
import com.bbtest.common.ShellCommon.amStartApp
import com.bbtest.common.ShellCommon.getActivities
import com.bbtest.common.ShellCommon.isAppBackstage
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.CommonUtil.getCurTimeForLog
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.readFile
import com.bbtest.utils.FileUtil.writeStrToFile
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Random

/**
 * @Author: onuszhao
 * @Date: 2021/1/8 11:37
 * @Description: 写长线程跑，容易被系统杀掉，且加入电池优化策略也没啥用，故切换至5分钟跑一次
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class MonkeyTest : MonkeyCommon() {
    var resultFolder: File = File(rootFolder, "monkey")
    var monkeyFile: File = File(resultFolder, "monkey.txt")
    private val monkeyInfoFile = File(downloadsDir, "monkey.txt")

    private var pkgName = ""
    private var activity = ""

    @Before
    public override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
        createFile(monkeyFile)
        // 获取需要跑的包相关信息
        val canWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        val grantState = PackageManager.PERMISSION_GRANTED
        val canRead = monkeyInfoFile.canRead()
        val testInfo = readFile(monkeyInfoFile).trim { it <= ' ' }
        Log.d("onuszhao", "canWrite=" + canWrite + "  grantState=" + grantState + "  canRead=" + canRead + "  testInfo=" + testInfo)
        val testInfoParts = testInfo.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        // 默认包名
        pkgName = testInfoParts[0]
        if (TextUtils.isEmpty(pkgName)) {
            pkgName = "com.transsion.phoenix"
        }
        // 获取activity(解决一个应用存在多个主activity)
        val mainActivities: List<String> = getActivities(device, pkgName, null)
        for (mainActivity in mainActivities) {
            amStartApp(device, mainActivity, monkeyFile)
            CommonUtil.sleep(5000)
            if (!isAppBackstage(device, pkgName)) {
                activity = mainActivity
                break
            }
        }
        writeStrToFile(getCurTimeForLog() + "  " + pkgName + "\n", monkeyFile)
    }

    @Test
    fun testMonkey() {
        try {
            /**
             * 初始化事件，控制概率
             */
            val events: List<String> = initRandomEvent(40, 12, 10, 12, 10, 10, 2, 2, 1, 1)

            val startTime = System.currentTimeMillis()
            var endTime: Long = 0
            while (true) {
                //  先确认是否在前台
                if (isAppBackstage(pkgName, monkeyFile)) {
                    //  再判断浏览器进程是否存在，并切到前台/启动浏览器
                    if (isProcessExist(pkgName, monkeyFile)) {
                        startActivity(1000, activity, monkeyFile)
                    } else {
                        startActivity(5000, activity, monkeyFile)
                    }

                    //  浏览器内弹窗，back返回
                    for (i in 0..2) {
                        if (isAppBackstage(pkgName, monkeyFile)) {
                            pressBack(monkeyFile)
                        } else {
                            break
                        }
                    }
                }

                // 发起模拟事件
                val event = events.get(Random().nextInt(events.size))
                if (event == "click") {
                    click(monkeyFile)
                } else if (event == "swipeUp") {
                    swipeUp(monkeyFile)
                } else if (event == "swipeDown") {
                    swipeDown(monkeyFile)
                } else if (event == "swipeLeft") {
                    swipeLeft(monkeyFile)
                } else if (event == "swipeRight") {
                    swipeRight(monkeyFile)
                } else if (event == "pressBack") {
                    pressBack(monkeyFile)
                } else if (event == "pressHome") {
                    pressHome(monkeyFile)
                    startActivity(1000, activity, monkeyFile)
                } else if (event == "longClick") {
                    longClick(monkeyFile)
                } else if (event == "type") {
                    type(monkeyFile)
                    dpadLeft(monkeyFile)
                    dpadRight(monkeyFile)
                    del(monkeyFile)
                } else if (event == "drag") {
                    drag(monkeyFile)
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
    public override fun afterTest() {
        super.afterTest()
    }

    companion object {
        // 设置Y初始化高度，排除通知栏
        private const val startY = 10
    }
}
