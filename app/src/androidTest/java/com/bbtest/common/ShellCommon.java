package com.bbtest.common;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.ShellCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ShellCommon {

    /**
     * 通过am启动应用，不会杀掉进程而启动应用
     */
    public static void amStartApp(UiDevice device, String activity, File runLog) {
        try {
            String result = ShellCommand.execCmdByUiDevice(device, "am start -W -n " + activity);
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  am start -W -n " + activity + "\n", runLog);
                String[] resultLines = result.split("\n");
                for (String resultLine : resultLines) {
                    if (!resultLine.equals("")) {
                        FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 强制退出浏览器
     */
    public static void forceStopApp(UiDevice device, String pkgName, File runLog) {
        try {
            String result = ShellCommand.execCmdByUiDevice(device, "am force-stop " + pkgName);
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  am force-stop " + pkgName + "\n", runLog);
                String[] resultLines = result.split("\n");
                for (String resultLine : resultLines) {
                    if (!resultLine.equals("")) {
                        FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空后台运行任务
     */
    public static void killAllApp(UiDevice device, File runLog) {
        try {
            // 通过am kill-all杀所有后台进程
            String killallResult = ShellCommand.execCmdByUiDevice(device, "am kill-all ");
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  am kill-all" + "\n", runLog);
                String[] killallResultLines = killallResult.split("\n");
                for (String killallResultLine : killallResultLines) {
                    if (!killallResultLine.equals("")) {
                        FileUtil.writeStrToFile("                         " + killallResultLine.trim() + "\n", runLog);
                    }
                }
            }

            // 通过查找后台所有在运行的程序的包名
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  dumpsys activity | grep 'Run #'" + "\n", runLog);
            }
            //执行shell无法识别管道符，故分开执行，如:dumpsys activity | grep 'Run #'
            String curRunResult = ShellCommand.execCmdByUiDevice(device, "dumpsys activity activities");
            String[] curRunResultLines = curRunResult.split("\\n");
            for (String curRunResultLine : curRunResultLines) {
                if (!curRunResultLine.equals("") && curRunResultLine.contains("Run #")) {
                    if (runLog != null) {
                        FileUtil.writeStrToFile("                         " + curRunResultLine.trim() + "\n", runLog);
                    }
                    String id = "";
                    String tmpPkgName = "";
                    String[] curRunResultLineParts = curRunResultLine.trim().split("\\s+");
                    for (String curRunResultLinePart : curRunResultLineParts) {
                        if (curRunResultLinePart.trim().startsWith("t") && curRunResultLinePart.trim().endsWith("}")) {
                            id = curRunResultLinePart.substring(curRunResultLinePart.indexOf("t") + 1, curRunResultLinePart.lastIndexOf("}")).trim();
                        } else if (curRunResultLinePart.trim().contains("/")) {
                            tmpPkgName = curRunResultLinePart.trim();
                        }
                    }
                    if (!id.equals("")) {
                        if (Integer.parseInt(id) > 300) {
                            forceStopApp(device, tmpPkgName, runLog);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取APK的主Activity
     * 方案1：dumpsys package com.transsion.phoenix
     * 方案2：dumpsys activity | grep "pkgName" --需要启动过
     */
    public static String getActivity(UiDevice device, String pkgName, File runLog) {
        String activity = "";
        try {
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  dumpsys package " + pkgName + "\n", runLog);
            }
            String result = ShellCommand.execCmdByUiDevice(device, "dumpsys package " + pkgName);
            String[] resultLines = result.split("\\n");
            boolean isFound = false;
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    if (!isFound) {
                        if (resultLine.contains("android.intent.action.MAIN:")) {
                            isFound = true;
                        }
                    } else {
                        if (resultLine.contains(pkgName)) {
                            if (runLog != null) {
                                FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                            }
                            String[] temp = resultLine.trim().split("\\s+");
                            activity = temp[1];
                        }

                        // 找到activity则退出
                        if (activity.matches(pkgName + "/\\D+[.\\D+]+")) {
                            break;
                        }
                        // 匹配到下一个解析时退出
//                        if (resultLine.matches("\\D+[.\\D+]+:")) {
//                            break;
//                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }

    public static List<String> getActivities(UiDevice device, String pkgName, File runLog) {
        List<String> activities = new ArrayList<>();
        try {
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  dumpsys package " + pkgName + "\n", runLog);
            }
            String result = ShellCommand.execCmdByUiDevice(device, "dumpsys package " + pkgName);
            String[] resultLines = result.split("\\n");
            boolean isFound = false;
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    if (!isFound) {
                        if (resultLine.contains("android.intent.action.MAIN:")) {
                            isFound = true;
                        }
                    } else {
                        if (resultLine.contains(pkgName)) {
                            if (runLog != null) {
                                FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                            }
                            String[] temp = resultLine.trim().split("\\s+");
                            activities.add(temp[1]);
                        }

                        // 匹配到下一个解析时退出
                        if (resultLine.matches("\\D+[.\\D+]+:")) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activities;
    }

    /**
     * 获取顶部Activity
     * 注：用dumpsys window | grep mCurrentFocus发现有弹窗时存在mFocusedApp的误差
     */
    public static String getTopActivity(UiDevice device, File runLog) {
        // 获取顶部Activity
        String topActivity = "";
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  dumpsys activity top | grep ACTIVITY" + "\n", runLog);
        }
        //执行shell无法识别管道符，故分开执行，如:dumpsys activity top | grep ACTIVITY
        String result = ShellCommand.execCmdByUiDevice(device, "dumpsys activity top");
        String[] resultLines = result.split("\\n");
        label:
        for (int i = resultLines.length - 1; i >= 0; i--) {
            String resultLine = resultLines[i];
            if (!resultLine.equals("") && Pattern.compile(".*ACTIVITY\\s+.*\\/.*").matcher(resultLine).find()) {
                if (runLog != null) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
                String[] resultLineParts = resultLine.trim().split("\\s+");
                for (String resultLinePart : resultLineParts) {
                    if (resultLinePart.contains("/")) {
                        topActivity = resultLinePart.trim();
                        break label;
                    }
                }
            }
        }
        return topActivity;
    }

    /**
     * 获取顶层activity(存在误差，先用另一种方式)
     */
    public static String getTopActivity2(UiDevice device, File runLog) {
        String topActivity = "";
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  dumpsys SurfaceFlinger --list" + "\n", runLog);
        }
        String result = ShellCommand.execCmdByUiDevice(device, "dumpsys SurfaceFlinger --list");
        String[] resultLines = result.split("\\n");
        for (int i = resultLines.length - 1; i >= 0; i--) {
            String resultLine = resultLines[i];
            if (resultLine.matches(".*\\D+[.\\D+]+/\\D+[.\\D+]+.*")) {
                if (runLog != null) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
                String[] linePart = resultLine.trim().split("\\s+");
                if (resultLine.contains("#")) {
                    if (linePart.length > 1) {
                        topActivity = linePart[1].split("#")[0].trim();
                    } else {
                        topActivity = linePart[0].split("#")[0].trim();
                    }
                } else {
                    topActivity = linePart[0].trim();
                }

                if (topActivity.contains("[")) {
                    topActivity = topActivity.substring(0, topActivity.indexOf("[")).trim();
                }
                break;
            }
        }
        return topActivity;
    }

    /**
     * 判断是否后台
     */
    public static boolean isAppBackstage(UiDevice device, String pkgName) {
        boolean isAppBackstage = false;
        try {
            String curActivity = ShellCommon.getTopActivity(device, null);
            String curPkgName = "";
            if (curActivity.contains("/")) {
                curPkgName = curActivity.split("/")[0];
            } else {
                curPkgName = curActivity;
            }
            if (curPkgName.equals(pkgName)) {
                isAppBackstage = false;
            } else {
                isAppBackstage = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAppBackstage;
    }

    public static boolean isAppBackstage(UiDevice device, String activity, String topActivity) {
        boolean isAppBackstage = false;
        try {
            String curActivity = ShellCommon.getTopActivity(device, null);
            if (curActivity.equals(activity) || curActivity.equals(topActivity)) {
                isAppBackstage = false;
            } else {
                isAppBackstage = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAppBackstage;
    }

    /**
     * 判断进程是否存在
     */
    public static boolean isProcessExist(UiDevice device, String pkgName) {
        boolean isProcessExist = false;
        try {
            //执行shell无法识别管道符，故分开执行，如:ps -ef | grep pkgName
            String result = ShellCommand.execCmdByUiDevice(device, "ps -ef");
            String[] resultLines = result.split("\\n");
            for (int i = 0; i < resultLines.length; i++) {
                String resultLine = resultLines[i];
                if (!resultLine.equals("") && resultLine.contains(pkgName)) {
                    String[] parts = resultLine.trim().split("\\s+");
                    if (parts[parts.length - 1].trim().equals(pkgName)) {
                        isProcessExist = true;
                        break;
                    } else {
                        isProcessExist = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isProcessExist;
    }

    /**
     * 模拟点击事件
     */
    public static void click(UiDevice device, int x, int y, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "input tap " + String.valueOf(x) + " " + String.valueOf(y));
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  click (" + x + "," + y + ")" + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }


    /**
     * 模拟滑动事件
     */
    public static void swipe(UiDevice device, int fromX, int fromY, int toX, int toY, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "input swipe " + String.valueOf(fromX) + " " + String.valueOf(fromY) + " " + String.valueOf(toX) + " " + String.valueOf(toY));
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  swipe (" + fromX + "," + fromY + ") up to (" + toX + "," + toY + ")" + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }

    /**
     * 模拟拖动事件
     */
    public static void drag(UiDevice device, int fromX, int fromY, int toX, int toY, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "input draganddrop " + String.valueOf(fromX) + " " + String.valueOf(fromY) + " " + String.valueOf(toX) + " " + String.valueOf(toY));
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  drag (" + fromX + "," + fromY + ") to (" + toX + "," + toY + ")" + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }

    /**
     * 模拟长按事件
     */
    public static void longClick(UiDevice device, int x, int y, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "input swipe " + String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(x) + " " + String.valueOf(y) + " 600");
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  longClick (" + x + "," + y + ")" + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }

    /**
     * 模拟输入文字事件
     */
    public static void type(UiDevice device, String str, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "input text " + str);
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  type text" + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }

    /**
     * 模拟硬件home
     */
    public static void pressHome(UiDevice device, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "input keyevent 3");
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  press home" + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }

    /**
     * 模拟硬件back
     */
    public static void pressBack(UiDevice device, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "input keyevent 4");
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  press back" + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }

    /**
     * 模拟光标左移
     */
    public static void dpadLeft(UiDevice device, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "input keyevent 21");
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  move the cursor to the left" + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }

    /**
     * 模拟光标右移
     */
    public static void dpadRight(UiDevice device, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "input keyevent 22");
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  move the cursor to the right" + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }

    /**
     * 模拟软键盘Enter键
     */
    public static void pressEnter(UiDevice device, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "input keyevent 66");
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  press enter" + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }

    /**
     * 访问网站
     */
    public static void visitWebsite(UiDevice device, String pkgName, String url, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p " + pkgName + " -d " + url);
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  open " + url + "with " + pkgName + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }

    /**
     * 模拟删除
     */
    public static void del(UiDevice device, File runLog) {
        String result = ShellCommand.execCmdByUiDevice(device, "input keyevent 67");
        if (runLog != null) {
            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  del text" + "\n", runLog);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    FileUtil.writeStrToFile("                         " + resultLine.trim() + "\n", runLog);
                }
            }
        }
    }

    /**
     * 授予权限
     */
    public static void grantApkPermission(UiDevice device, String pkgName) {
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.READ_CALENDAR");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.WRITE_CALENDAR");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.CAMERA");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.READ_CONTACTS");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.WRITE_CONTACTS");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.GET_ACCOUNTS");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.ACCESS_FINE_LOCATION");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.ACCESS_COARSE_LOCATION");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.READ_PHONE_STATE");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.MODIFY_PHONE_STATE");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.CALL_PHONE");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.READ_CALL_LOG");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.WRITE_CALL_LOG");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.ADD_VOICEMAIL");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.USE_SIP");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.PROCESS_OUTGOING_CALLS");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.READ_EXTERNAL_STORAGE");
        ShellCommand.execCmdByUiDevice(device, "pm grant " + pkgName + " android.permission.WRITE_EXTERNAL_STORAGE");
    }

    public static void screenshot(UiDevice device, File file) {
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            device.takeScreenshot(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过命令设置buffersize也会受手机设置buffersize的限制，故以手机设置buffersize为准
     */
    public static void setBufferSize(UiDevice device, int mb, File runLog) {
        while (true) {
            boolean isSetSuccess = true;
            String result = ShellCommand.execCmdByUiDevice(device, "logcat -G " + mb + "M");
            String[] lines = result.split("\\n");
            for (String line : lines) {
                if (!line.trim().equals("") || line.contains("failed")) {
                    isSetSuccess = false;
                    break;
                }
            }
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  setBufferSize=" + mb + "M,isSetSuccess=" + isSetSuccess + ",result=" + result.trim().replaceAll("\n", "") + "\n", runLog);
            }
            if (isSetSuccess) {
                break;
            } else {
                mb = mb - 1;
            }
        }
    }

    public static boolean isNearMaxBufferSize(UiDevice device, File runLog) {
        boolean isNearMaxBufferSize = false;
        String result = ShellCommand.execCmdByUiDevice(device, "logcat -g");
        String[] lines = result.split("\\n");
        for (String line : lines) {
            if (!line.equals("") && line.contains("main:")) {
                String tmpMaxBufferSize = line.substring(line.indexOf("is") + 2, line.indexOf("(")).replaceAll("\\s+", "");
                String tmpCurBufferSize = line.substring(line.indexOf("(") + 1, line.indexOf("consumed")).replaceAll("\\s+", "");
                int maxBufferSize = CommonUtil.getFirstNum(tmpMaxBufferSize);
                int curBufferSize = CommonUtil.getFirstNum(tmpCurBufferSize);
                String maxBufferSizeUnit = tmpMaxBufferSize.replaceAll(String.valueOf(maxBufferSize), "").trim();
                String curBufferSizeUnit = tmpCurBufferSize.replaceAll(String.valueOf(curBufferSize), "").trim();
                // buffer比较大时，一半就开始清理；比较小时2/3才清理
                long convMaxBufferSize = -1;
                if (maxBufferSizeUnit.startsWith("G")) {
                    convMaxBufferSize = maxBufferSize * 1024 * 1024 * 1024 * 2 / 3;
                } else if (maxBufferSizeUnit.startsWith("M")) {
                    convMaxBufferSize = maxBufferSize * 1024 * 1024 * 2 / 3;
                } else if (maxBufferSizeUnit.startsWith("K")) {
                    convMaxBufferSize = maxBufferSize * 1024 * 2 / 3;
                } else {
                    convMaxBufferSize = maxBufferSize * 2 / 3;
                }
                long convCurBufferSize = -1;
                if (curBufferSizeUnit.startsWith("G")) {
                    convCurBufferSize = curBufferSize * 1024 * 1024 * 1024;
                } else if (curBufferSizeUnit.startsWith("M")) {
                    convCurBufferSize = curBufferSize * 1024 * 1024;
                } else if (curBufferSizeUnit.startsWith("K")) {
                    convCurBufferSize = curBufferSize * 1024;
                } else {
                    convCurBufferSize = curBufferSize;
                }

                if (convCurBufferSize >= convMaxBufferSize) {
                    isNearMaxBufferSize = true;
                }
                //记录文件
                if (runLog != null) {
                    FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  maxBufferSize=" + maxBufferSize + ",maxBufferSizeUnit=" + maxBufferSizeUnit
                            + ",curBufferSize=" + curBufferSize + ",curBufferSizeUnit=" + curBufferSizeUnit + ",convMaxBufferSize=" + convMaxBufferSize
                            + ",convCurBufferSize=" + convCurBufferSize + ",isNearMaxBufferSize=" + isNearMaxBufferSize + "\n", runLog);
                }
                break;
            }
        }
        return isNearMaxBufferSize;
    }

    public static void clearBufferCache(UiDevice device, File runLog) {
        while (true) {
            boolean isClearSuccess = true;
            String result = ShellCommand.execCmdByUiDevice(device, "logcat -c");
            String[] lines = result.split("\\n");
            for (String line : lines) {
                if (!line.trim().equals("") || line.contains("failed")) {
                    isClearSuccess = false;
                    break;
                }
            }
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  isClearSuccess=" + isClearSuccess + ",result=" + result.trim().replaceAll("\n", "") + "\n", runLog);
            }
            if (isClearSuccess) {
                break;
            }
        }
    }

    public static void killLogcat(UiDevice device, File runLog) {
        while (true) {
            boolean isKillLogcatSuccess = true;
            // 通过killall -9 logcat杀掉进程
            String killallResult = ShellCommand.execCmdByUiDevice(device, "killall -9 logcat");
            String[] killalllines = killallResult.split("\\n");
            for (String killallline : killalllines) {
                if (!killallline.trim().equals("") || killallline.contains("failed")) {
                    isKillLogcatSuccess = false;
                    break;
                }
            }
            if (runLog != null) {
                FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  isKillLogcatSuccess=" + isKillLogcatSuccess
                        + ",result=" + killallResult.trim().replaceAll("\n", "") + "\n", runLog);
            }

            // 通过kill -9 pid杀掉进程
            if (!isKillLogcatSuccess) {
                List<String> logcatPids = new ArrayList<>();
                //执行shell无法识别管道符，故分开执行，如:ps -ef | grep logcat
                String lsLogcatResult = ShellCommand.execCmdByUiDevice(device, "ps -ef");
                String[] lsLogcatlines = lsLogcatResult.split("\\n");
                for (String lsLogcatline : lsLogcatlines) {
                    if (!lsLogcatline.equals("") && lsLogcatline.contains("logcat")) {
                        if (runLog != null) {
                            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  lsLogcatline=" + lsLogcatline.trim() + "\n", runLog);
                        }
                        if (lsLogcatline.contains("logcat -v time")) {
                            String[] parts = lsLogcatline.trim().split("\\s+");
                            logcatPids.add(parts[1].trim());
                        }
                    }
                }

                if (logcatPids.size() > 0) {
                    for (String logcatPid : logcatPids) {
                        String killResult = ShellCommand.execCmdByUiDevice(device, "kill -9 " + logcatPid);
                        String[] killlines = killResult.split("\\n");
                        for (String killline : killlines) {
                            if (!killline.trim().equals("") || killline.contains("failed")) {
                                isKillLogcatSuccess = false;
                                break;
                            }
                        }
                        if (runLog != null) {
                            FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  isKillLogcatSuccess=" + isKillLogcatSuccess + ",logcatPid=" + logcatPid
                                    + ",result=" + killResult.trim().replaceAll("\n", "") + "\n", runLog);
                        }
                    }
                } else {
                    isKillLogcatSuccess = true;
                    if (runLog != null) {
                        FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  isKillLogcatSuccess=" + isKillLogcatSuccess + ",logcatPids=" + logcatPids.toString() + "\n", runLog);
                    }
                }
            }

            if (isKillLogcatSuccess) {
                break;
            }
        }
    }

    public static boolean isLogcatCrash(UiDevice device, File runLog) {
        boolean isLogcatCrash = true;
        //执行shell无法识别管道符，故分开执行，如:ps -ef | grep logcat
        String lsLogcatResult = ShellCommand.execCmdByUiDevice(device, "ps -ef");
        String[] lsLogcatlines = lsLogcatResult.split("\\n");
        for (String lsLogcatline : lsLogcatlines) {
            if (!lsLogcatline.equals("") && lsLogcatline.contains("logcat -v time")) {
                if (runLog != null) {
                    FileUtil.writeStrToFile(CommonUtil.getCurTimeForLog() + "  isLogcat=" + lsLogcatline.trim() + "\n", runLog);
                }
                isLogcatCrash = false;
                break;
            }
        }
        return isLogcatCrash;
    }

}
