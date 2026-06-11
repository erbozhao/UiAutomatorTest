package com.bbtest.stable.threads;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.common.ShellCommon;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.ShellCommand;

import java.io.File;

public class DumpheapThread extends Thread {

    private float runTime = -1;
    private String pkgName = "";
    private UiDevice device = null;
    private String model = "";
    private File resultFolder = null;

    private String endImgFile = "";
    private String endHprofFile = "";
    private boolean isTimeOver = false;

    public DumpheapThread(String pkgName, UiDevice device, String model, File resultFolder) {
        this.pkgName = pkgName;
        this.device = device;
        this.model = model;
        this.resultFolder = resultFolder;
    }

    public DumpheapThread(float runTime, String pkgName, UiDevice device, String model, File resultFolder) {
        this.runTime = runTime;
        this.pkgName = pkgName;
        this.device = device;
        this.model = model;
        this.resultFolder = resultFolder;
    }

    public void run() {
        try {
            // 初始化
            empty();
            openDumpheap();

            if (runTime <= 0) {
                // 结束时截图并生成Hprof文件
                endImgFile = "Img_" + model + "_" + pkgName.replaceAll(":", ".") + "_" + CommonUtil.getCurTimeForFile() + ".png";
                screencap(endImgFile);
                copy(endImgFile);
                del(endImgFile);
                endHprofFile = "Hprof_" + model + "_" + pkgName + "_" + CommonUtil.getCurTimeForFile() + ".hprof";
                dumpheap(endHprofFile);
                copy(endHprofFile);
                del(endHprofFile);
            } else {
                long startTime = System.currentTimeMillis();
                long curStartTime = startTime;
                long endTime = 0;
                while (true) {
                    //计算时间
                    endTime = System.currentTimeMillis();
                    long costTime = endTime - startTime;
                    long curCostTime = endTime - curStartTime;
                    if (runTime > 0 && costTime > (runTime * 60 * 60 * 1000)) {
                        // 结束时截图并生成Hprof文件
                        endImgFile = "Img_" + model + "_" + pkgName.replaceAll(":", ".") + "_" + CommonUtil.getCurTimeForFile() + ".png";
                        screencap(endImgFile);
                        copy(endImgFile);
                        del(endImgFile);
                        endHprofFile = "Hprof_" + model + "_" + pkgName + "_" + CommonUtil.getCurTimeForFile() + ".hprof";
                        dumpheap(endHprofFile);
//                        Thread.sleep(30 * 1000);
                        copy(endHprofFile);
                        del(endHprofFile);
                        break;
                    } else {
                        // 判断是否退出，则生成Hprof文件
                        if (isTimeOver) {
                            // 结束时截图并生成Hprof文件
                            endImgFile = "Img_" + model + "_" + pkgName.replaceAll(":", ".") + "_" + CommonUtil.getCurTimeForFile() + ".png";
                            screencap(endImgFile);
                            copy(endImgFile);
                            del(endImgFile);
                            endHprofFile = "Hprof_" + model + "_" + pkgName + "_" + CommonUtil.getCurTimeForFile() + ".hprof";
                            dumpheap(endHprofFile);
//                            Thread.sleep(30 * 1000);
                            copy(endHprofFile);
                            del(endHprofFile);
                            break;
                        } else {
                            // 判断是否间隔1小时，则生成Hprof文件
                            if (curCostTime > 1 * 60 * 60 * 1000) {
                                // 结束时截图并生成Hprof文件
                                endImgFile = "Img_" + model + "_" + pkgName.replaceAll(":", ".") + "_" + CommonUtil.getCurTimeForFile() + ".png";
                                screencap(endImgFile);
                                copy(endImgFile);
                                del(endImgFile);
                                endHprofFile = "Hprof_" + model + "_" + pkgName + "_" + CommonUtil.getCurTimeForFile() + ".hprof";
                                dumpheap(endHprofFile);
//                                Thread.sleep(30 * 1000);
                                copy(endHprofFile);
                                del(endHprofFile);
                                curStartTime = endTime;
                            }

                            // 等待1s(不加，会判断isTimeOver为false，无法正常跳出)
                            Thread.sleep(1000);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void empty() {
        ShellCommand.execCmdByUiDevice(device, "rm -rf /data/local/tmp/*");
    }

    private void openDumpheap() {
        ShellCommand.execCmdByUiDevice(device, "dumpsys activity log dumpheap on");
    }

    private void dumpheap(String fileName) {
        ShellCommand.execCmdByUiDevice(device, "am dumpheap " + pkgName + " /data/local/tmp/" + fileName);
    }

    private void screencap(String imgFile) {
//        ShellCommon.screenshot(device, "/data/local/tmp/" + imgFile);
        ShellCommand.execCmdByUiDevice(device, "screencap /data/local/tmp/" + imgFile);
    }

    private void copy(String fileName) {
        ShellCommand.execCmdByUiDevice(device, "cp /data/local/tmp/" + fileName + " " + resultFolder);
    }

    private void del(String fileName) {
        ShellCommand.execCmdByUiDevice(device, "rm -rf /data/local/tmp/" + fileName);
    }

    public void setIsTimeOver(boolean isTimeOver) {
        this.isTimeOver = isTimeOver;
    }

}