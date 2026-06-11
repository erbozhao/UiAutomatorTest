package com.bbtest.perform.base;


import androidx.test.uiautomator.UiDevice;

import com.bbtest.utils.ShellCommand;

public class MemInfo {

    private UiDevice device = null;
    private String pkgName = "";
    private long totalMem = 0;
    private long availabelMem = 0;
    private long totalPss = 0;
    private long dalvilkPss = 0;   //java heap
    private long nativePss = 0;      //native heap
    private long views = 0;
    private long webviews = 0;
    private long activities = 0;

    public MemInfo(UiDevice device, String pkgName) {
        this.device = device;
        this.pkgName = pkgName;
        readMeminfo();
        readRaminfo();
    }

    public long getTotalMem() {
        return totalMem;
    }

    public long getAvailabelMem() {
        return availabelMem;
    }

    public long getTotalPss() {
        return totalPss;
    }

    public long getDalvilkPss() {
        return dalvilkPss;
    }

    public long getNativePss() {
        return nativePss;
    }

    public long getViews() {
        return views;
    }

    public long getWebviews() {
        return webviews;
    }

    public long getActivities() {
        return activities;
    }

    private void readMeminfo() {
        String cmd = "dumpsys meminfo " + pkgName;
        String memInfo = ShellCommand.execCmdByUiDevice(device, cmd);
        String[] memInfoLines = memInfo.split("\n");
        for (String memInfoLine : memInfoLines) {
            memInfoLine = memInfoLine.trim();
            if (memInfoLine.startsWith("TOTAL") && !memInfoLine.contains("Size") && !memInfoLine
                    .contains("Free") && !memInfoLine.contains("PSS")) {
                totalPss = Long.parseLong(memInfoLine.split("\\s+")[1].trim());
            } else if (memInfoLine.startsWith("Dalvik") && memInfoLine.contains("Heap") && !memInfoLine
                    .contains(":")) {
                dalvilkPss = Long.parseLong(memInfoLine.split("\\s+")[2].trim());
            } else if (memInfoLine.startsWith("Native") && memInfoLine.contains("Heap") && !memInfoLine
                    .contains(":")) {
                nativePss = Long.parseLong(memInfoLine.split("\\s+")[2].trim());
            } else if (memInfoLine.startsWith("Views:")) {
                views = Long.parseLong(memInfoLine.split("\\s+")[1].trim());
            } else if (memInfoLine.startsWith("WebViews:")) {
                webviews = Long.parseLong(memInfoLine.split("\\s+")[1].trim());
            } else if (memInfoLine.startsWith("AppContexts:") && memInfoLine.contains("Activities:")) {
                activities = Long.parseLong(memInfoLine.split("\\s+")[3].trim());
            }
        }
    }

    private void readRaminfo() {
        String cmd = "cat /proc/meminfo";
        String ramInfo = ShellCommand.execCmdByUiDevice(device, cmd);
        String[] ramInfoLines = ramInfo.split("\n");
        for (String ramInfoLine : ramInfoLines) {
            ramInfoLine = ramInfoLine.trim();
            if (ramInfoLine.startsWith("MemTotal:")) {
                totalMem = Long.parseLong(ramInfoLine.trim().split("\\s+")[1].trim());
            } else if (ramInfoLine.startsWith("MemAvailable:") || ramInfoLine.startsWith("MemFree:")) {
                availabelMem = Long.parseLong(ramInfoLine.trim().split("\\s+")[1].trim());
            }
        }
    }

}
