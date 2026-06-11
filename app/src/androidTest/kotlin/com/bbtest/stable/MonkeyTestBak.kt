package com.bbtest.stable

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.bbtest.common.BaseCommon
import com.bbtest.common.ShellCommon.forceStopApp
import com.bbtest.common.ShellCommon.getActivity
import com.bbtest.stable.threads.RandomEventThread
import com.bbtest.tools.DeviceInfo
import com.bbtest.utils.CommonUtil.getCurTimeForFile
import com.bbtest.utils.CommonUtil.getCurTimeForLog
import com.bbtest.utils.CommonUtil.getExceptionMsg
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.deleteFile
import com.bbtest.utils.FileUtil.deleteFolder
import com.bbtest.utils.FileUtil.readFile
import com.bbtest.utils.FileUtil.writeStrToFile
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * @author onuszhao
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class MonkeyTestBak : BaseCommon() {
    var resultFolder: File = File(rootFolder, "monkey")
    var monkeyFile: File = File(resultFolder, "monkey.txt")
    private val monkeyInfoFile = File(downloadsDir, "monkey.txt")

    @Before
    public override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        deleteFolder(resultFolder)
        createFolder(resultFolder)
        createFile(monkeyFile)
    }

    @Test
    fun testMonkey() {
        try {
            // 获取设备信息
            val deviceInfo = DeviceInfo(device)
            val model = deviceInfo.model

            // 初始化日志文件
            val runLog = File(resultFolder, "Runlog.txt")
            createFile(runLog)

            // 获取需要跑的包相关信息
            var runTime = 0.0f
            var pkgName = ""
            val result = readFile(monkeyInfoFile)
            val resultLines = result.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    val resultLineParts = resultLine.trim { it <= ' ' }.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (resultLineParts.size == 2) {
                        runTime = resultLineParts[0].trim { it <= ' ' }.toFloat()
                        pkgName = resultLineParts[1].trim { it <= ' ' }
                    }
                    break
                }
            }
            if (runTime == 0f && pkgName == "") {
                runTime = 0.05f
                pkgName = "com.transsion.phoenix"
            }
            val activity = getActivity(device, pkgName, runLog)
            writeStrToFile(getCurTimeForLog() + "  start:" + runTime + "," + pkgName + "\n", runLog)

            //录制视频
//            ScreenrecordThread screenrecordThread = new ScreenrecordThread(runTime, device, model, rootFolder, resultFolder);
//            screenrecordThread.start();

            //运行内存
//            MemoryThread memoryThread = new MemoryThread(runTime, pkgName, device, model, resultFolder, -1, true);
//            memoryThread.start();

            //NaticieFd内存泄漏监控
//            boolean isRoot = deviceInfo.isRoot();
//            NativeFdThread nativeFdThread = null;
//            if (isRoot) {
//                nativeFdThread = new NativeFdThread(runTime, pkgName, device, model, resultFolder, screenrecordThread);
//                nativeFdThread.start();
//            }

            //模拟发起随机事件
            val randomEventThread = RandomEventThread(runTime, pkgName, activity, device, width, height, runLog)
            randomEventThread.start()

            //记录日志
//            LogcatThread logcatThread = new LogcatThread(runTime, pkgName, device, model, resultFolder, null, screenrecordThread, randomEventThread, nativeFdThread);
//            logcatThread.start();

            //运行dumpheap
//            DumpheapThread dumpheapThread = new DumpheapThread(runTime, pkgName, device, model, resultFolder);
//            dumpheapThread.start();

            // 等待模拟事件执行完成，并通知其他线程该结束了
            randomEventThread.join()
            writeStrToFile(getCurTimeForLog() + "  randomEventThread end" + "\n", runLog)

            //            screenrecordThread.setIsTimeOver(true);
//            memoryThread.setIsTimeOver(true);
//            if (nativeFdThread != null) {
//                nativeFdThread.setIsTimeOver(true);
//            }
//            logcatThread.setIsTimeOver(true);
//            dumpheapThread.setIsTimeOver(true);

            //等待其他线程执行结束
//            screenrecordThread.join();
//            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  screenrecordThread end" + "\n", runLog);
//            memoryThread.join();
//            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  memoryThread end" + "\n", runLog);
//            if (nativeFdThread != null) {
//                nativeFdThread.join();
//                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  nativeFdThread end" + "\n", runLog);
//            }
//            logcatThread.join();
//            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  logcatThread end" + "\n", runLog);
//            dumpheapThread.join();
//            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  dumpheapThread end" + "\n", runLog);
            // dumpheap结束后，强制关闭app
            forceStopApp(device, pkgName, runLog)

            // 若无异常,则删除文件
            deleteFile(monkeyFile)
        } catch (e: Exception) {
            e.printStackTrace()
            writeStrToFile(getCurTimeForLog() + "MonkeyTest:Exception" + "\n", monkeyFile)
            writeStrToFile(getExceptionMsg(e), monkeyFile)
            screenshot(resultFolder.toString() + "/monkey_" + getCurTimeForFile() + ".jpg")
        }
    }

    @After
    public override fun afterTest() {
        super.afterTest()
    }
}
