package com.bbtest.perform.base;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;

import java.io.File;

public class CpuThread extends Thread {

    private UiDevice device = null;
    private int pid = 0;
    private File resultFile = null;

    private boolean isEnd = false;

    public CpuThread(UiDevice device, int pid, File resultFile) {
        this.device = device;
        this.pid = pid;
        this.resultFile = resultFile;
    }

    @Override
    public void run() {
        while (!isEnd) {
            CommonUtil.sleep(1000);
            FileUtil.writeStrToFile(new CpuInfo(device, pid).getProCpu() + "%" + "\n", resultFile);
        }
    }

    public void setIsEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }
}
