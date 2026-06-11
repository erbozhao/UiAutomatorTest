package com.bbtest.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.utils.ShellCommand;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

public class ProcessInfo {

    private UiDevice device = null;
    private String pkgName = "";

    public ProcessInfo(UiDevice device, String pkgName) {
        this.device = device;
        this.pkgName = pkgName;
    }

    public int getPid() {
        int pid = 0;
        try {
            // shell无法识别管道符，故不直接执行ps -ef | grep com.transsion.phoenix
            String result = ShellCommand.execCmdByUiDevice(device, "ps -ef");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("") && resultLine.contains(pkgName)) {
                    String[] parts = resultLine.trim().split("\\s+");
                    if (parts[parts.length - 1].trim().equals(pkgName)) {
                        pid = Integer.parseInt(parts[1].trim());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pid;
    }

    /*
     *方法1：根据pid获取其uid，如下
     *方法2：通过pkg获取，需权限：adb shell "su -c 'cat /data/system/packages.list'";
     */
    public int getUid(int pid) {
        int uid = 0;
        try {
            String result = ShellCommand.execCmdByUiDevice(device, "cat /proc/" + pid + "/status");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("") && resultLine.contains("Uid:")) {
                    uid = Integer.parseInt(resultLine.split("\\s+")[1].trim());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uid;
    }

    /**
     * 通过包名获取uid
     */
    public int getUid() {
        int uid = -1;
        Context context = getApplicationContext();
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, PackageManager.GET_META_DATA);
            uid = packageInfo.applicationInfo.uid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uid;
    }

    public List<String> getSubProcess() {
        List<String> subProcess = new ArrayList<>();
        try {
            // shell无法识别管道符，故不直接执行ps -ef | grep com.transsion.phoenix
            String result = ShellCommand.execCmdByUiDevice(device, "ps -ef");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("") && resultLine.contains(pkgName)) {
                    String[] parts = resultLine.trim().split("\\s+");
                    if (!parts[parts.length - 1].trim().equals(pkgName)) {
                        subProcess.add(parts[parts.length - 1].trim());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subProcess;
    }
}
