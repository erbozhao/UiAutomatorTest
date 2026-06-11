package com.bbtest.stable.threads;

import android.app.UiAutomation;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.common.ShellCommon;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class LogcatThread extends Thread {

    private float runTime = -1;
    private String pkgName = "";
    private UiDevice device = null;
    private UiAutomation uiAutomation = null;
    private String model = "";
    private String resultFolder = "";
    private boolean isTimeOver = false;

    private LogCatcher logCatcher = null;
    private ScreencapThread screencapThread = null;
    private ScreenrecordThread screenrecordThread = null;
    private RandomEventThread randomEventThread = null;
    private NativeFdThread nativeFdThread = null;
    private boolean isLogcatCrash = false;

    public LogcatThread(float runTime, String pkgName, UiDevice device, UiAutomation uiAutomation, String model, String resultFolder, ScreencapThread screencapThread,
                        ScreenrecordThread screenrecordThread, RandomEventThread randomEventThread, NativeFdThread nativeFdThread) {
        this.runTime = runTime;
        this.pkgName = pkgName;
        this.device = device;
        this.uiAutomation = uiAutomation;
        this.model = model;
        this.resultFolder = resultFolder;
        this.screencapThread = screencapThread;
        this.screenrecordThread = screenrecordThread;
        this.randomEventThread = randomEventThread;
        this.nativeFdThread = nativeFdThread;
    }

    public void run() {
        try {
            // 初始化目录
            File tmpLog = new File(resultFolder,"Tmplog_" + model + "_" + CommonUtil.getCurTimeForFile() + ".txt");
            FileUtil.createFile(tmpLog);

            // 设置logcat的buffer大小(通过命令设置buffersize也会受手机设置buffersize的限制)
            ShellCommon.setBufferSize(device, 16, tmpLog);

            long startTime = System.currentTimeMillis();
            long curStartTime = startTime;
            long endTime = 0;
            File logcatFile = null;
            while (true) {
                //创建文件
                if (logCatcher == null) {
                    logcatFile = new File(resultFolder, "Logcat_" + model + "_" + CommonUtil.getCurTimeForFile() + ".log");
                    FileUtil.createFile(logcatFile);
                    ShellCommon.clearBufferCache(device, tmpLog);
                    logCatcher = new LogCatcher(uiAutomation, pkgName, logcatFile, screencapThread, screenrecordThread, randomEventThread, nativeFdThread, this);
                    logCatcher.start();
                }

                // logcat挂了则重启
                if (isLogcatCrash) {
                    ShellCommon.killLogcat(device, tmpLog);
                    if (logCatcher.isAlive()) {
                        logCatcher.interrupt();
                        logCatcher.stop();
                    }
                    logCatcher = null;
                    ShellCommon.clearBufferCache(device, tmpLog);
                    logCatcher = new LogCatcher(uiAutomation, pkgName, logcatFile, screencapThread, screenrecordThread, randomEventThread, nativeFdThread, this);
                    logCatcher.start();
                    isLogcatCrash = false;
                }

                // 计算时间
                endTime = System.currentTimeMillis();
                long costTime = endTime - startTime;
                long curCostTime = endTime - curStartTime;
                if (runTime > 0 && costTime > runTime * 60 * 60 * 1000) {
                    ShellCommon.killLogcat(device, tmpLog);
                    if (logCatcher.isAlive()) {
                        logCatcher.interrupt();
                        logCatcher.stop();
                    }
                    logCatcher = null;
                    break;
                } else {
                    // 判断是否退出，则直接结束
                    if (isTimeOver) {
                        ShellCommon.killLogcat(device, tmpLog);
                        if (logCatcher.isAlive()) {
                            logCatcher.interrupt();
                            logCatcher.stop();
                        }
                        logCatcher = null;
                        break;
                    } else {
                        // 判断是否间隔30分钟，则重新抓取日志
                        if (curCostTime > 30 * 60 * 1000) {
                            ShellCommon.killLogcat(device, tmpLog);
                            if (logCatcher.isAlive()) {
                                logCatcher.interrupt();
                                logCatcher.stop();
                            }
                            logCatcher = null;
                            curStartTime = endTime;
                        } else {
                            // 判断logcat是否挂了
                            isLogcatCrash = ShellCommon.isLogcatCrash(device, tmpLog);
                        }

                        // 如果buffer快满了，则清除缓存buffer
                        if (ShellCommon.isNearMaxBufferSize(device, tmpLog)) {
                            ShellCommon.clearBufferCache(device, tmpLog);
                        }

                        // 等待1s(不加，会判断isTimeOver为false，无法正常跳出)
                        Thread.sleep(1000);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIsTimeOver(boolean isTimeOver) {
        this.isTimeOver = isTimeOver;
    }

    public void setIsLogcatCrash(boolean isLogcatCrash) {
        this.isLogcatCrash = isLogcatCrash;
    }
}

class LogCatcher extends Thread {

    private UiAutomation uiAutomation = null;
    private String pkgName = "";
    private File logcatFile;
    private ScreencapThread screencapThread = null;
    private ScreenrecordThread screenrecordThread = null;
    private RandomEventThread randomEventThread = null;
    private NativeFdThread nativeFdThread = null;
    private LogcatThread logcatThread = null;

    public LogCatcher(UiAutomation uiAutomation, String pkgName, File logcatFile, ScreencapThread screencapThread, ScreenrecordThread screenrecordThread,
                      RandomEventThread randomEventThread, NativeFdThread nativeFdThread, LogcatThread logcatThread) {
        this.uiAutomation = uiAutomation;
        this.pkgName = pkgName;
        this.logcatFile = logcatFile;
        this.screencapThread = screencapThread;
        this.screenrecordThread = screenrecordThread;
        this.randomEventThread = randomEventThread;
        this.nativeFdThread = nativeFdThread;
        this.logcatThread = logcatThread;
    }

    /**
     * 解决Logcat Buffer不足而读取日志错误问题：read: unexpected EOF!
     * 1.Settings->Developer options->Logger buffer调至最大
     * 2.然后通过过滤减少输出日志信息，避免某一时间日志量超过buffer
     * 3.最后实时监控buffer使用情况
     */
    public void run() {
        try {
            // 初始化crash关键词
            String crashKeywords1 = ".*FATAL\\s+EXCEPTION:\\s+.*";
            String crashKeywords1_1 = ".*Process:\\s+com.transsion.phoenix.*";
            String crashKeywords2 = ".*Fatal\\s+signal.*" + pkgName.substring(pkgName.lastIndexOf(".") + 1) + ".*";
            String crashKeywords3 = ".*CRASH:\\s+" + pkgName + ".*";
            String crashKeywords4 = ".*system_app_crash.*Process:\\s+" + pkgName + ".*";
            String crashKeywords5 = ".*Exception\\s+Type:.*";
            String crashKeywords5_1 = ".*" + pkgName + ".*";
            String crashKeywords6 = ".*pid.*" + pkgName + ".*";
            String crashKeywords6_1 = ".*signal\\s+\\d+\\s+\\(.*\\(.*";
            String anrKeywords1 = ".*ANR\\s+in.*" + pkgName + ".*";

            // 判断后两行是否出现对应crashKeywords
            boolean isMayAppCrash1 = false;
            int mayAppCrashCount1 = 0;
            boolean isMayAppCrash5 = false;
            int mayAppCrashCount5 = 0;
            boolean isMayAppCrash6 = false;
            int mayAppCrashCount6 = 0;

            // 优先级：V(详细) < D(调试) < I(信息) < W(警告) < E(错误) < F(严重错误) < S(静默-不会输出任何内容)
            // 过滤等级为D以上的日志：adb shell logcat -v time -v year -v color *:I
            // 过滤crash相关日志：logcat -v time -v year -v color -s libc:F DEBUG:F ActivityManager:E AndroidRuntime:E AnrManager:I PhxPageMemoryChecker:E
            ParcelFileDescriptor pfd = null;
            int sdk = Build.VERSION.SDK_INT;
            if (sdk <= 23) {
//                pfd = getInstrumentation().getUiAutomation().executeShellCommand("logcat -v time -v year *:I");
                pfd = uiAutomation.executeShellCommand("logcat -v time -s libc:F DEBUG:F ActivityManager:E AndroidRuntime:E AnrManager:I PhxPageMemoryChecker:E");
            } else {
//                pfd = getInstrumentation().getUiAutomation().executeShellCommand("logcat -v time -v year *:I");
                pfd = uiAutomation.executeShellCommand("logcat -v time -v year -s libc:F DEBUG:F ActivityManager:E AndroidRuntime:E AnrManager:I PhxPageMemoryChecker:E");
            }

            FileInputStream fis = new ParcelFileDescriptor.AutoCloseInputStream(pfd);
            BufferedReader bfr = new BufferedReader(new InputStreamReader(fis));
            String line = "";
            while ((line = bfr.readLine()) != null) {
                // 判断后几行，是否出现关键词
                if (isMayAppCrash1) {
                    if (Pattern.compile(crashKeywords1_1).matcher(line).find()) {
                        if (screencapThread != null) {
                            screencapThread.setIsCrash(true);
                        }
                        if (screenrecordThread != null) {
                            screenrecordThread.setIsCrash(true);
                        }
                        if (randomEventThread != null) {
                            randomEventThread.setIsCrash(true);
                        }
                        if (nativeFdThread != null) {
                            nativeFdThread.setIsCrash(true);
                        }
                        isMayAppCrash1 = false;
                        mayAppCrashCount1 = 0;
                    } else {
                        if (mayAppCrashCount1 > 3) {
                            isMayAppCrash1 = false;
                            mayAppCrashCount1 = 0;
                        }
                    }
                    mayAppCrashCount1++;
                }
                // 判断后几行，是否出现关键词
                if (isMayAppCrash5) {
                    if (Pattern.compile(crashKeywords5_1).matcher(line).find()) {
                        if (screencapThread != null) {
                            screencapThread.setIsCrash(true);
                        }
                        if (screenrecordThread != null) {
                            screenrecordThread.setIsCrash(true);
                        }
                        if (randomEventThread != null) {
                            randomEventThread.setIsCrash(true);
                        }
                        if (nativeFdThread != null) {
                            nativeFdThread.setIsCrash(true);
                        }
                        isMayAppCrash5 = false;
                        mayAppCrashCount5 = 0;
                    } else {
                        if (mayAppCrashCount5 > 5) {
                            isMayAppCrash5 = false;
                            mayAppCrashCount5 = 0;
                        }
                    }
                    mayAppCrashCount5++;
                }
                // 判断后几行，是否出现关键词
                if (isMayAppCrash6) {
                    if (Pattern.compile(crashKeywords6_1).matcher(line).find()) {
                        if (screencapThread != null) {
                            screencapThread.setIsCrash(true);
                        }
                        if (screenrecordThread != null) {
                            screenrecordThread.setIsCrash(true);
                        }
                        if (randomEventThread != null) {
                            randomEventThread.setIsCrash(true);
                        }
                        if (nativeFdThread != null) {
                            nativeFdThread.setIsCrash(true);
                        }
                        isMayAppCrash6 = false;
                        mayAppCrashCount6 = 0;
                    } else {
                        if (mayAppCrashCount6 > 5) {
                            isMayAppCrash6 = false;
                            mayAppCrashCount6 = 0;
                        }
                    }
                    mayAppCrashCount6++;
                }

                // 判断crash则通知随机事件线程
                if (Pattern.compile(crashKeywords1).matcher(line).find()) {
                    isMayAppCrash1 = true;
                } else if (Pattern.compile(crashKeywords2).matcher(line).find()) {
                    if (screencapThread != null) {
                        screencapThread.setIsCrash(true);
                    }
                    if (screenrecordThread != null) {
                        screenrecordThread.setIsCrash(true);
                    }
                    if (randomEventThread != null) {
                        randomEventThread.setIsAnr(true);
                    }
                    if (nativeFdThread != null) {
                        nativeFdThread.setIsCrash(true);
                    }
                } else if (Pattern.compile(crashKeywords3).matcher(line).find()) {
                    if (screencapThread != null) {
                        screencapThread.setIsCrash(true);
                    }
                    if (screenrecordThread != null) {
                        screenrecordThread.setIsCrash(true);
                    }
                    if (randomEventThread != null) {
                        randomEventThread.setIsAnr(true);
                    }
                    if (nativeFdThread != null) {
                        nativeFdThread.setIsCrash(true);
                    }
                } else if (Pattern.compile(crashKeywords4).matcher(line).find()) {
                    if (screencapThread != null) {
                        screencapThread.setIsCrash(true);
                    }
                    if (screenrecordThread != null) {
                        screenrecordThread.setIsCrash(true);
                    }
                    if (randomEventThread != null) {
                        randomEventThread.setIsCrash(true);
                    }
                    if (nativeFdThread != null) {
                        nativeFdThread.setIsCrash(true);
                    }
                } else if (Pattern.compile(crashKeywords5).matcher(line).find()) {
                    isMayAppCrash5 = true;
                } else if (Pattern.compile(crashKeywords6).matcher(line).find()) {
                    isMayAppCrash6 = true;
                } else if (Pattern.compile(anrKeywords1).matcher(line).find()) {
                    if (screencapThread != null) {
                        screencapThread.setIsCrash(true);
                    }
                    if (screenrecordThread != null) {
                        screenrecordThread.setIsCrash(true);
                    }
                    if (randomEventThread != null) {
                        randomEventThread.setIsCrash(true);
                    }
                    if (nativeFdThread != null) {
                        nativeFdThread.setIsCrash(true);
                    }
                }

                // 判断logcat因buffer过大而挂掉了
                if (line.toLowerCase().contains("unexpected eof")) {
                    logcatThread.setIsLogcatCrash(true);
                }

                //记录文件
                if (sdk <= 23) {
                    FileUtil.writeStrToFile(CommonUtil.getCurYear() + "-" + line + "\n", logcatFile);
                } else {
                    FileUtil.writeStrToFile(line + "\n", logcatFile);
                }
            }
            pfd.wait();
            pfd.close();
            bfr.close();

            // 若执行到此，说明logcat停止了，则需重启
            logcatThread.setIsLogcatCrash(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
