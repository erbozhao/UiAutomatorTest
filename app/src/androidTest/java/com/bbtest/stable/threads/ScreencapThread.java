package com.bbtest.stable.threads;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.ShellCommand;

import java.io.File;

public class ScreencapThread extends Thread {

    private float runTime = -1.0F;
    private UiDevice device = null;
    private String model = "";
    private File tmpFolder = null;
    private File resultFolder = null;
    private boolean isTimeOver = false;
    private boolean isCrash = false;

    public ScreencapThread(float runTime, UiDevice device, String model, String rootFolder, String resultFolder) {
        this.runTime = runTime;
        this.device = device;
        this.model = model;
        this.tmpFolder = new File(rootFolder, "img");
        this.resultFolder = new File(resultFolder, "img");
    }

    public void run() {
        try {
            // 初始化目录
            FileUtil.deleteFolder(tmpFolder);
            FileUtil.createFolder(tmpFolder);
            FileUtil.deleteFolder(resultFolder);
            FileUtil.createFolder(resultFolder);

            long startTime = System.currentTimeMillis();
            long curStartTime = startTime;
            long endTime = 0L;
            while (true) {
                // crash时截图
                String imgFile = "";
                if (isCrash) {
                    imgFile = "Img_" + model + "_" + CommonUtil.getCurTimeForFile() + ".png";
                    screencap(imgFile);
                    copyImg(imgFile);
                    isCrash = false;
                }

                // 删除截图
                if (!imgFile.equals("")) {
                    delImg(imgFile);
                }

                // 计算时间
                endTime = System.currentTimeMillis();
                long costTime = endTime - startTime;
                long curCostTime = endTime - curStartTime;
                if (runTime > 0 && costTime > runTime * 60 * 60 * 1000 - 10 * 1000) {
                    break;
                } else {
                    // 判断是否退出，则直接结束
                    if (isTimeOver) {
                        break;
                    } else {
                        if (curCostTime > 60 * 1000) {
                            curStartTime = endTime;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void screencap(String imgFile) {
        String tmpImgPath = tmpFolder + "/" + imgFile;
        ShellCommand.execCmdByUiDevice(device, "screencap " + tmpImgPath);
    }

    private void copyImg(String imgFile) {
        String srcImgPath = tmpFolder + "/" + imgFile;
        String dstImgPath = resultFolder + "/" + imgFile;
        ShellCommand.execCmdByUiDevice(device, "cp " + srcImgPath + " " + dstImgPath);
    }

    private void delImg(String imgFile) {
        String tmpImgPath = tmpFolder + "/" + imgFile;
        ShellCommand.execCmdByUiDevice(device, "rm -rf " + tmpImgPath);
    }

    public void setIsTimeOver(boolean isTimeOver) {
        this.isTimeOver = isTimeOver;
    }

    public void setIsCrash(boolean isCrash) {
        this.isCrash = isCrash;
    }

}