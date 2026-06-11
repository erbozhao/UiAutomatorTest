package com.bbtest.tools;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.ShellCommand;

public class DeviceInfo {

    private UiDevice device = null;

    public DeviceInfo(UiDevice device) {
        this.device = device;
    }

    /*
    获取手机设备型号
    adb shell getprop ro.product.model
    */
    public String getModel() {
        String model = "";
        try {
            String result = ShellCommand.execCmdByUiDevice(device, "getprop ro.product.model");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    model = resultLine.trim().replaceAll("\\s", "-");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    /*
    获取手机系统版本
    adb shell getprop ro.build.version.release
    */
    public String getVersion() {
        String version = "";
        try {
            String result = ShellCommand.execCmdByUiDevice(device, "getprop ro.build.version.release");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    version = resultLine.trim().replaceAll("\\s", "_");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /*
      获取手机分辨率:
      adb shell "dumpsys window | grep mUnrestrictedScreen"
      adb shell wm size
      获取手机屏幕信息,以下两种方式部分手机只支持一种
      adb shell dumpsys display
      adb shell dumpsys window displays
    */
    public String getResolution() {
        String resolution = "";
        try {
            // shell无法识别管道符，故不直接执行dumpsys window | grep init
            String result = ShellCommand.execCmdByUiDevice(device, "dumpsys window");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (resultLine.contains("init") && resultLine.contains("cur") && resultLine.contains("app")) {
                    String cur = resultLine.trim().split("\\s+")[2].trim();
                    resolution = cur.split("\\=")[1].trim().replaceAll("\\s", "_");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resolution;
    }

    /*
      获取手机分辨率:
      adb shell "dumpsys window | grep mUnrestrictedScreen"
      adb shell wm size
      获取手机屏幕信息,以下两种方式部分手机只支持一种
      adb shell dumpsys display
      adb shell dumpsys window displays
    */
    public String getRam() {
        String ram = "";
        try {
            String result = ShellCommand.execCmdByUiDevice(device, "cat /proc/meminfo | grep MemTotal");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                String[] resultLineParts = resultLine.trim().split("\\s+");
                if (resultLineParts.length > 1) {
                    String tmpRam = resultLineParts[1].trim();
                    if (CommonUtil.isNumer(tmpRam)) {
                        ram = String.valueOf(Long.parseLong(tmpRam) / 1024);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ram;
    }

    /**
     * 获取手机sdk版本
     */
    public int getSdk() {
        int sdk = 0;
        try {
            String result = ShellCommand.execCmdByUiDevice(device, "getprop ro.build.version.sdk");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    if (CommonUtil.isNumer(resultLine.trim())) {
                        sdk = Integer.parseInt(resultLine.trim());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdk;
    }

    public void setMediaVolumeMute() {
        ShellCommand.execCmdByUiDevice(device, "input keyevent 164");
    }

    public void setMediaVolumeZero() {
        int sdk = getSdk();
        if (sdk == 22) {
            ShellCommand.execCmdByUiDevice(device, "service call audio 4 i32 3 i32 0 i32 1");
        } else if (sdk >= 23 && sdk <= 25) {
            ShellCommand.execCmdByUiDevice(device, "service call audio 3 i32 3 i32 0 i32 1");
        } else if (sdk == 30) {
            // i32 3--手机媒体音量，i32 0--音量为0，i32 1--音量为0，
            ShellCommand.execCmdByUiDevice(device, "service call audio 10 i32 3 i32 0 i32 1");
        } else {
            ShellCommand.execCmdByUiDevice(device, "media volume --set 0");
        }
    }

    public int getMediaVolume() {
        int mediaVolume = -1;

        int sdk = getSdk();
        // i32 2-手机铃声;3-手机媒体音量
        if (sdk == 22) {
            String result = ShellCommand.execCmdByUiDevice(device, "service call audio 13 i32 3");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("") && resultLine.contains("Result")) {
                    String[] parts = resultLine.trim().split("\\s+");
                    // 16进制转10进制
                    mediaVolume = Integer.parseInt(parts[2].trim(), 16);
                    break;
                }
            }
        } else if (sdk == 23) {
            String result = ShellCommand.execCmdByUiDevice(device, "service call audio 9 i32 3");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("") && resultLine.contains("Result")) {
                    String[] parts = resultLine.trim().split("\\s+");
                    // 16进制转10进制
                    mediaVolume = Integer.parseInt(parts[2].trim(), 16);
                    break;
                }
            }
        } else if (sdk == 24) {
            String result = ShellCommand.execCmdByUiDevice(device, "service call audio 8 i32 3");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("") && resultLine.contains("Result")) {
                    String[] parts = resultLine.trim().split("\\s+");
                    // 16进制转10进制
                    mediaVolume = Integer.parseInt(parts[2].trim(), 16);
                    break;
                }
            }
        } else if (sdk == 30) {
            String result = ShellCommand.execCmdByUiDevice(device, "service call audio 16 i32 3");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("") && resultLine.contains("Result")) {
                    String[] parts = resultLine.trim().split("\\s+");
                    // 16进制转10进制
                    mediaVolume = Integer.parseInt(parts[2].trim(), 16);
                    break;
                }
            }
        } else {
            String result = ShellCommand.execCmdByUiDevice(device, "media volume --get");
            String[] resultLines = result.split("\\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("") && resultLine.contains("volume") && resultLine.contains("in range")) {
                    String[] parts = resultLine.trim().split("\\s+");
                    String tmpValue = parts[3].trim();
                    if (CommonUtil.isNumer(tmpValue)) {
                        mediaVolume = Integer.parseInt(tmpValue);
                    }
                    break;
                }
            }
        }
        return mediaVolume;
    }

    public boolean isRoot() {
        boolean isRoot = false;
        String result = ShellCommand.execCmdBySh("su -v");
        String[] resultLines = result.split("\\n");
        for (String resultLine : resultLines) {
            if (!resultLine.equals("")) {
                isRoot = true;
                break;
            }
        }
        return isRoot;
    }

    public void unlockScreen(int width, int height) {
        try {
            // 点亮屏幕
            ShellCommand.execCmdByUiDevice(device, "input keyevent 224");
            Thread.sleep(5000);
            // 点击home键(避免有弹窗)
            ShellCommand.execCmdByUiDevice(device, "input keyevent 3");
            Thread.sleep(5000);
            // 解锁屏
            ShellCommand.execCmdByUiDevice(device, "input keyevent 82");
            Thread.sleep(5000);
            // 通过向上滑动解锁屏幕
            ShellCommand.execCmdByUiDevice(device, "input swipe " + (width / 2) + " " + (height * 4 / 5) + " " + (width / 2) + " " + (height * 1 / 5));
            Thread.sleep(5000);
            // 通过向左滑动解锁屏幕
            ShellCommand.execCmdByUiDevice(device, "input swipe " + (width * 4 / 5) + " " + (height / 2) + " " + (width * 1 / 5) + " " + (height / 2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backToHomeScreen() {
        try {
            // 先点击返回
            ShellCommand.execCmdByUiDevice(device, "input keyevent 4");
            Thread.sleep(5000);
            // 再点击home键
            ShellCommand.execCmdByUiDevice(device, "input keyevent 3");
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
