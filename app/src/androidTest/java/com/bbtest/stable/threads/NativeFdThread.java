package com.bbtest.stable.threads;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.tools.ProcessInfo;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.ShellCommand;

import java.io.File;

public class NativeFdThread extends Thread {

    private float runTime = -1;
    private String pkgName = "";
    private UiDevice device = null;
    private String model = "";
    private String resultFolder = "";

    private boolean isTimeOver = false;
    private boolean isCrash = false;

    private ScreenrecordThread screenrecordThread = null;

    public NativeFdThread(float runTime, String pkgName, UiDevice device, String model, String resultFolder, ScreenrecordThread screenrecordThread) {
        this.runTime = runTime;
        this.pkgName = pkgName;
        this.device = device;
        this.model = model;
        this.resultFolder = resultFolder;
        this.screenrecordThread = screenrecordThread;
    }

    public void run() {
        try {
            //创建文件
            File fdFile = new File(resultFolder, "Fd_" + model + "_" + CommonUtil.getCurTimeForFile() + ".log");
            FileUtil.createFile(fdFile);

            long startTime = System.currentTimeMillis();
            long endTime = 0;
            int appThreshold = 300;
            int appServiceThreshold = 200;
            while (true) {
                // 判断是否crash，否则重置阈值
                if (isCrash) {
                    appThreshold = 300;
                    appServiceThreshold = 200;
                    isCrash = false;
                }

                // 获取应用pid
                int pid = new ProcessInfo(device, pkgName).getPid();
                // 监控fd数，超过的打印日志并导出视频
                int fdNum = getFdNum(pid);
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  pkgName=" + pkgName + ",pid=" + pid + ",fdNum=" + fdNum + "\n", fdFile);
                if (fdNum > appThreshold) {
                    getFdInfo(pid, fdFile);
                    screenrecordThread.setFdNum(fdNum);
                    appThreshold += 100;
                }

                // 获取应用服务进程的pid
                int servicePid = new ProcessInfo(device, pkgName + ":service").getPid();
                // 监控fd数，超过的打印日志并导出视频
                int serviceFdNum = getFdNum(servicePid);
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  pkgName=" + pkgName + ":service,pid=" + servicePid + ",fdNum=" + serviceFdNum + "\n", fdFile);
                if (serviceFdNum > appServiceThreshold) {
                    getFdInfo(pid, fdFile);
                    screenrecordThread.setFdNum(fdNum);
                    appServiceThreshold += 100;
                }

                // 计算时间
                endTime = System.currentTimeMillis();
                long costTime = endTime - startTime;
                if (runTime > 0 && costTime > runTime * 60 * 60 * 1000) {
                    break;
                } else {
                    // 判断是否退出，则直接结束
                    if (isTimeOver) {
                        break;
                    } else {
                        // 每间隔60s打印一次
                        Thread.sleep(60 * 1000);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getFdNum(int pid) {
        int fdNum = 0;
        // 列表(或fdinfo)：adb shell "su -c 'ls /proc/pid/fd'"
        // 计数(或fdinfo)：adb shell "su -c 'ls /proc/pid/fd | wc -l'"
        String result = ShellCommand.execCmdBySu("ls /proc/" + pid + "/fd");
        String[] resultLines = result.split("\\n|\\s+");
        for (String resultLine : resultLines) {
            if (!resultLine.trim().equals("")) {
                fdNum++;
            }
        }
        return fdNum;
    }

    private void getFdInfo(int pid, File filePath) {
        // 列表(或fdinfo)：adb shell "su -c 'ls -al /proc/pid/fd'"
        String result = ShellCommand.execCmdBySu("ls -al /proc/" + pid + "/fd");
        String[] resultLines = result.split("\\n");
        for (int i = 0; i < resultLines.length; i++) {
            String resultLine = resultLines[i];
            if (!resultLine.equals("")) {
                FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", filePath);
            }
        }
    }

    public void setIsTimeOver(boolean isTimeOver) {
        this.isTimeOver = isTimeOver;
    }

    public void setIsCrash(boolean isCrash) {
        this.isCrash = isCrash;
    }
}
