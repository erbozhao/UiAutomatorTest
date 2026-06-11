package com.bbtest.stable;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;

import com.bbtest.common.BaseCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.stable.threads.RandomEventThread;
import com.bbtest.tools.DeviceInfo;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * @author onuszhao
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class MonkeyTestBak extends BaseCommon {

    public File resultFolder = new File(rootFolder, "monkey");
    public File monkeyFile = new File(resultFolder, "monkey.txt");
    private File monkeyInfoFile = new File(downloadFile, "monkey.txt");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.deleteFolder(resultFolder);
        FileUtil.createFolder(resultFolder);
        FileUtil.createFile(monkeyFile);
    }

    @Test
    public void testMonkey() {
        try {
            // 获取设备信息
            DeviceInfo deviceInfo = new DeviceInfo(device);
            String model = deviceInfo.getModel();

            // 初始化日志文件
            File runLog = new File(resultFolder, "Runlog.txt");
            FileUtil.createFile(runLog);

            // 获取需要跑的包相关信息
            float runTime = 0.0f;
            String pkgName = "";
            String result = FileUtil.readFile(monkeyInfoFile);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    String[] resultLineParts = resultLine.trim().split(",");
                    if (resultLineParts.length == 2) {
                        runTime = Float.parseFloat(resultLineParts[0].trim());
                        pkgName = resultLineParts[1].trim();
                    }
                    break;
                }
            }
            if (runTime == 0 && pkgName.equals("")) {
                runTime = 0.05f;
                pkgName = "com.transsion.phoenix";
            }
            String activity = ShellCommon.getActivity(device, pkgName, runLog);
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  start:" + runTime + "," + pkgName + "\n", runLog);

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
            RandomEventThread randomEventThread = new RandomEventThread(runTime, pkgName, activity, device, width, height, runLog);
            randomEventThread.start();

            //记录日志
//            LogcatThread logcatThread = new LogcatThread(runTime, pkgName, device, model, resultFolder, null, screenrecordThread, randomEventThread, nativeFdThread);
//            logcatThread.start();

            //运行dumpheap
//            DumpheapThread dumpheapThread = new DumpheapThread(runTime, pkgName, device, model, resultFolder);
//            dumpheapThread.start();

            // 等待模拟事件执行完成，并通知其他线程该结束了
            randomEventThread.join();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  randomEventThread end" + "\n", runLog);
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
            ShellCommon.forceStopApp(device, pkgName, runLog);

            // 若无异常,则删除文件
            FileUtil.deleteFile(monkeyFile);
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "MonkeyTest:Exception" + "\n", monkeyFile);
            FileUtil.writeStrToFile(CommonUtil.getExceptionMsg(e), monkeyFile);
            screenshot(resultFolder + "/monkey_" + CommonUtil.getCurTimeForFile() + ".jpg");
        }
    }

    @After
    public void afterTest() {
        super.afterTest();
    }

}
