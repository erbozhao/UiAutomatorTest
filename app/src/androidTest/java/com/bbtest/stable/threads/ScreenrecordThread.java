package com.bbtest.stable.threads;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.ShellCommand;

import java.io.File;

public class ScreenrecordThread extends Thread {

    private float runTime = -1;
    private UiDevice device = null;
    private String model = "";
    private File tmpFolder = null;
    private File resultFolder = null;
    private File fdFolder = null;

    private boolean isTimeOver = false;
    private boolean isCrash = false;
    private int fdNum = -1;

    private String videoFile = "";

    public ScreenrecordThread(float runTime, UiDevice device, String model, String rootFolder, String resultFolder) {
        this.runTime = runTime;
        this.device = device;
        this.model = model;
        this.tmpFolder = new File(rootFolder, "video");
        this.resultFolder = new File(resultFolder, "video");
        this.fdFolder = new File(resultFolder, "fdvideo");
    }

    public void run() {
        try {
            //初始化目录
            FileUtil.deleteFolder(tmpFolder);
            FileUtil.createFolder(tmpFolder);
            FileUtil.deleteFolder(resultFolder);
            FileUtil.createFolder(resultFolder);
            FileUtil.deleteFolder(fdFolder);
            FileUtil.createFolder(fdFolder);

            long startTime = System.currentTimeMillis();
            long endTime = 0;
            while (true) {
                //录制视频
                videoFile = "Video_" + model + "_" + CommonUtil.getCurTimeForFile() + ".mp4";
                screenrecord(videoFile);

                // 判断Native Fd是否超了
                if (fdNum != -1) {
                    // 导出视频
                    copyFdVideo(videoFile, fdNum);
                    fdNum = -1;
                }

                // 判断是否crash，则导出文件后删除
                if (isCrash) {
                    copyVideo(videoFile);
                    isCrash = false;
                }
                delVideo(videoFile);

                // 计算时间
                endTime = System.currentTimeMillis();
                long costTime = endTime - startTime;
                if (runTime > 0 && costTime > runTime * 60 * 60 * 1000 - 10 * 1000) {
                    break;
                } else {
                    // 判断是否退出，则直接结束
                    if (isTimeOver) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void screenrecord(String videoFile) {
        String tmpVideoPath = tmpFolder + "/" + videoFile;
        // 录屏三分钟(目前支持最大)
        ShellCommand.execCmdByUiDevice(device, "screenrecord --size 480x800 --time-limit 180 " + tmpVideoPath);
    }

    private void copyVideo(String videoFile) {
        String srcVideoPath = tmpFolder + "/" + videoFile;
        String dstVideoPath = resultFolder + "/" + videoFile;
        ShellCommand.execCmdByUiDevice(device, "cp " + srcVideoPath + " " + dstVideoPath);
//        FileUtil.copyFile(srcVideoPath, dstVideoPath);
    }

    private void copyFdVideo(String videoFile, int fdNum) {
        String srcVideoPath = tmpFolder + "/" + videoFile;
        String dstVideoPath = fdFolder + "/" + videoFile.substring(0, videoFile.lastIndexOf(".")).trim() + "_" + fdNum + ".mp4";
        ShellCommand.execCmdByUiDevice(device, "cp " + srcVideoPath + " " + dstVideoPath);
//        FileUtil.copyFile(srcVideoPath, dstVideoPath);
    }

    private void delVideo(String videoFile) {
        String tmpVideoPath = tmpFolder + "/" + videoFile;
        ShellCommand.execCmdByUiDevice(device, "rm -rf " + tmpVideoPath);
//        FileUtil.deleteFile(tmpVideoPath);
    }

    public void setIsTimeOver(boolean isTimeOver) {
        this.isTimeOver = isTimeOver;
    }

    public void setIsCrash(boolean isCrash) {
        this.isCrash = isCrash;
    }

    public void setFdNum(int fdNum) {
        this.fdNum = fdNum;
    }

}
