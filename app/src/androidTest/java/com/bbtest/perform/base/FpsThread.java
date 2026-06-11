package com.bbtest.perform.base;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;

import java.io.File;

public class FpsThread extends Thread {

    private UiDevice device = null;
    private String pkgName = "";
    private File resultFile = null;


    private boolean isEnd = false;

    public FpsThread(UiDevice device, String pkgName, File resultFile) {
        this.device = device;
        this.pkgName = pkgName;
        this.resultFile = resultFile;
    }

    @Override
    public void run() {
        while (!isEnd) {
            CommonUtil.sleep(2000);
            FileUtil.writeStrToFile(new FpsInfo(device, pkgName).getFps() + "\n", resultFile);
        }
    }

    public void setIsEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }
}
