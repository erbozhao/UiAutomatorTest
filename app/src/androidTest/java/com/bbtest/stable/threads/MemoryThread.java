package com.bbtest.stable.threads;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.tools.DeviceInfo;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.ShellCommand;

import java.io.File;

public class MemoryThread extends Thread {

    private float runTime = -1;
    private String pkgName = "";
    private UiDevice device = null;
    private File resultFile;
    private String service = "";
    private File serviceFile;
    private int intervalSecond;
    private boolean isTimeOver = false;

    public MemoryThread(float runTime, String pkgName, UiDevice device, File resultFile, int intervalSecond) {
        this.runTime = runTime;
        this.pkgName = pkgName;
        this.device = device;
        this.resultFile = resultFile;
        FileUtil.createFile(resultFile);
        this.intervalSecond = intervalSecond;
    }

    public MemoryThread(float runTime, String pkgName, UiDevice device, String model, File resultFolder, int intervalSecond, boolean isOpenService) {
        this.runTime = runTime;
        this.pkgName = pkgName;
        this.device = device;
        this.resultFile = new File(resultFolder, "Memoinfo_" + model + "_" + pkgName + "_" + CommonUtil.getCurTimeForFile() + ".txt");
        FileUtil.createFile(resultFile);
        if (isOpenService) {
            service = pkgName + ":service";
            serviceFile = new File(resultFolder, "Memoinfo_" + model + "_" + service.replaceAll(":", ".") + "_" + CommonUtil.getCurTimeForFile() + ".txt");
            FileUtil.createFile(serviceFile);
        }
        this.intervalSecond = intervalSecond;
    }

    public void run() {
        try {
            // 记录开始日志
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ******************start record memory info******************" + "\n", resultFile);
            if (serviceFile != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ******************start record memory info******************" + "\n", serviceFile);
            }

            long startTime = System.currentTimeMillis();
            long endTime = 0;
            label:
            while (true) {
                // 获取内存信息
                dumpMem(pkgName, resultFile);
                if (serviceFile != null) {
                    dumpMem(service, serviceFile);
                }

                // 计算时间
                endTime = System.currentTimeMillis();
                long costTime = endTime - startTime;
                if (runTime > 0 && costTime > runTime * 60 * 60 * 1000) {
                    dumpMem(pkgName, resultFile);
                    if (serviceFile != null) {
                        dumpMem(service, serviceFile);
                    }
                    break;
                } else {
                    // 当等于-1或者0，专用于monkey判断音量大小，则大概10s获取一次内存
                    if (intervalSecond == -1 || intervalSecond == 0) {
                        for (int i = 0; i < 8; i++) {
                            // 判断是否退出
                            if (isTimeOver) {
                                dumpMem(pkgName, resultFile);
                                if (serviceFile != null) {
                                    dumpMem(service, serviceFile);
                                }
                                break label;
                            } else {
                                // 判断浏览器音量，保持调整为最小
                                DeviceInfo deviceInfo = new DeviceInfo(device);
                                int mediaVolume = deviceInfo.getMediaVolume();
                                if (mediaVolume > 0) {
                                    deviceInfo.setMediaVolumeZero();
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < intervalSecond; i++) {
                            // 判断是否退出
                            if (isTimeOver) {
                                dumpMem(pkgName, resultFile);
                                if (serviceFile != null) {
                                    dumpMem(service, serviceFile);
                                }
                                break label;
                            } else {
                                // 等待1s
                                Thread.sleep(1000);
                            }
                        }
                    }
                }
            }

            // 记录结束日志
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ******************end record memory info******************" + "\n", resultFile);
            if (serviceFile != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  ******************end record memory info******************" + "\n", serviceFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dumpMem(String pkgName, File memFilePath) {
        // 记录每次dump内存时间
        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  dumpsys meminfo " + pkgName + "\n", memFilePath);
        //获取内存信息
        String memoInfo = ShellCommand.execCmdByUiDevice(device, "dumpsys meminfo " + pkgName);
        String[] memoInfoLines = memoInfo.split("\\n");
        for (String memoInfoLine : memoInfoLines) {
            FileUtil.writeStrToFile(memoInfoLine + "\n", memFilePath);
        }
        // 换行间隔(4行)显示
        for (int i = 0; i <= 3; i++) {
            FileUtil.writeStrToFile("\r\n", memFilePath);
        }
    }

    public void setIsTimeOver(boolean isTimeOver) {
        this.isTimeOver = isTimeOver;
    }


}
