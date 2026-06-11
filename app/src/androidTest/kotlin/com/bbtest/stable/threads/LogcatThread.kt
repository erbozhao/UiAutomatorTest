package com.bbtest.stable.threads

import android.app.UiAutomation
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.test.uiautomator.UiDevice
import com.bbtest.common.ShellCommon
import com.bbtest.utils.CommonUtil.getCurTimeForFile
import com.bbtest.utils.CommonUtil.getCurYear
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.writeStrToFile
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Locale
import java.util.regex.Pattern

class LogcatThread(
    runTime: Float,
    pkgName: String,
    device: UiDevice?,
    uiAutomation: UiAutomation?,
    model: String?,
    resultFolder: String?,
    screencapThread: ScreencapThread?,
    screenrecordThread: ScreenrecordThread?,
    randomEventThread: RandomEventThread?,
    nativeFdThread: NativeFdThread?
) : Thread() {
    private var runTime = -1f
    private var pkgName = ""
    private var device: UiDevice? = null
    private var uiAutomation: UiAutomation? = null
    private var model: String? = ""
    private var resultFolder: String? = ""
    private var isTimeOver = false

    private var logCatcher: LogCatcher? = null
    private var screencapThread: ScreencapThread? = null
    private var screenrecordThread: ScreenrecordThread? = null
    private var randomEventThread: RandomEventThread? = null
    private var nativeFdThread: NativeFdThread? = null
    private var isLogcatCrash = false

    init {
        this.runTime = runTime
        this.pkgName = pkgName
        this.device = device
        this.uiAutomation = uiAutomation
        this.model = model
        this.resultFolder = resultFolder
        this.screencapThread = screencapThread
        this.screenrecordThread = screenrecordThread
        this.randomEventThread = randomEventThread
        this.nativeFdThread = nativeFdThread
    }

    override fun run() {
        try {
            // 初始化目录
            val tmpLog = File(resultFolder, "Tmplog_" + model + "_" + getCurTimeForFile() + ".txt")
            createFile(tmpLog)

            // 设置logcat的buffer大小(通过命令设置buffersize也会受手机设置buffersize的限制)
            ShellCommon.setBufferSize(device!!, 16, tmpLog)

            val startTime = System.currentTimeMillis()
            var curStartTime = startTime
            var endTime: Long = 0
            var logcatFile: File? = null
            while (true) {
                //创建文件
                if (logCatcher == null) {
                    logcatFile = File(resultFolder, "Logcat_" + model + "_" + getCurTimeForFile() + ".log")
                    createFile(logcatFile)
                    ShellCommon.clearBufferCache(device!!, tmpLog)
                    logCatcher =
                        LogCatcher(uiAutomation, pkgName, logcatFile, screencapThread, screenrecordThread, randomEventThread, nativeFdThread, this)
                    logCatcher!!.start()
                }

                // logcat挂了则重启
                if (isLogcatCrash) {
                    ShellCommon.killLogcat(device!!, tmpLog)
                    if (logCatcher!!.isAlive()) {
                        logCatcher!!.interrupt()
                        logCatcher!!.stop()
                    }
                    logCatcher = null
                    ShellCommon.clearBufferCache(device!!, tmpLog)
                    logCatcher =
                        LogCatcher(uiAutomation, pkgName, logcatFile!!, screencapThread, screenrecordThread, randomEventThread, nativeFdThread, this)
                    logCatcher!!.start()
                    isLogcatCrash = false
                }

                // 计算时间
                endTime = System.currentTimeMillis()
                val costTime = endTime - startTime
                val curCostTime = endTime - curStartTime
                if (runTime > 0 && costTime > runTime * 60 * 60 * 1000) {
                    ShellCommon.killLogcat(device!!, tmpLog)
                    if (logCatcher!!.isAlive()) {
                        logCatcher!!.interrupt()
                        logCatcher!!.stop()
                    }
                    logCatcher = null
                    break
                } else {
                    // 判断是否退出，则直接结束
                    if (isTimeOver) {
                        ShellCommon.killLogcat(device!!, tmpLog)
                        if (logCatcher!!.isAlive()) {
                            logCatcher!!.interrupt()
                            logCatcher!!.stop()
                        }
                        logCatcher = null
                        break
                    } else {
                        // 判断是否间隔30分钟，则重新抓取日志
                        if (curCostTime > 30 * 60 * 1000) {
                            ShellCommon.killLogcat(device!!, tmpLog)
                            if (logCatcher!!.isAlive()) {
                                logCatcher!!.interrupt()
                                logCatcher!!.stop()
                            }
                            logCatcher = null
                            curStartTime = endTime
                        } else {
                            // 判断logcat是否挂了
                            isLogcatCrash = ShellCommon.isLogcatCrash(device!!, tmpLog)
                        }

                        // 如果buffer快满了，则清除缓存buffer
                        if (ShellCommon.isNearMaxBufferSize(device!!, tmpLog)) {
                            ShellCommon.clearBufferCache(device!!, tmpLog)
                        }

                        // 等待1s(不加，会判断isTimeOver为false，无法正常跳出)
                        sleep(1000)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setIsTimeOver(isTimeOver: Boolean) {
        this.isTimeOver = isTimeOver
    }

    fun setIsLogcatCrash(isLogcatCrash: Boolean) {
        this.isLogcatCrash = isLogcatCrash
    }
}

internal class LogCatcher(
    uiAutomation: UiAutomation?, pkgName: String, logcatFile: File, screencapThread: ScreencapThread?, screenrecordThread: ScreenrecordThread?,
    randomEventThread: RandomEventThread?, nativeFdThread: NativeFdThread?, logcatThread: LogcatThread?
) : Thread() {
    private var uiAutomation: UiAutomation? = null
    private var pkgName = ""
    private val logcatFile: File
    private var screencapThread: ScreencapThread? = null
    private var screenrecordThread: ScreenrecordThread? = null
    private var randomEventThread: RandomEventThread? = null
    private var nativeFdThread: NativeFdThread? = null
    private var logcatThread: LogcatThread? = null

    init {
        this.uiAutomation = uiAutomation
        this.pkgName = pkgName
        this.logcatFile = logcatFile
        this.screencapThread = screencapThread
        this.screenrecordThread = screenrecordThread
        this.randomEventThread = randomEventThread
        this.nativeFdThread = nativeFdThread
        this.logcatThread = logcatThread
    }

    /**
     * 解决Logcat Buffer不足而读取日志错误问题：read: unexpected EOF!
     * 1.Settings->Developer options->Logger buffer调至最大
     * 2.然后通过过滤减少输出日志信息，避免某一时间日志量超过buffer
     * 3.最后实时监控buffer使用情况
     */
    override fun run() {
        try {
            // 初始化crash关键词
            val crashKeywords1 = ".*FATAL\\s+EXCEPTION:\\s+.*"
            val crashKeywords1_1 = ".*Process:\\s+com.transsion.phoenix.*"
            val crashKeywords2 = ".*Fatal\\s+signal.*" + pkgName.substring(pkgName.lastIndexOf(".") + 1) + ".*"
            val crashKeywords3 = ".*CRASH:\\s+" + pkgName + ".*"
            val crashKeywords4 = ".*system_app_crash.*Process:\\s+" + pkgName + ".*"
            val crashKeywords5 = ".*Exception\\s+Type:.*"
            val crashKeywords5_1 = ".*" + pkgName + ".*"
            val crashKeywords6 = ".*pid.*" + pkgName + ".*"
            val crashKeywords6_1 = ".*signal\\s+\\d+\\s+\\(.*\\(.*"
            val anrKeywords1 = ".*ANR\\s+in.*" + pkgName + ".*"

            // 判断后两行是否出现对应crashKeywords
            var isMayAppCrash1 = false
            var mayAppCrashCount1 = 0
            var isMayAppCrash5 = false
            var mayAppCrashCount5 = 0
            var isMayAppCrash6 = false
            var mayAppCrashCount6 = 0

            // 优先级：V(详细) < D(调试) < I(信息) < W(警告) < E(错误) < F(严重错误) < S(静默-不会输出任何内容)
            // 过滤等级为D以上的日志：adb shell logcat -v time -v year -v color *:I
            // 过滤crash相关日志：logcat -v time -v year -v color -s libc:F DEBUG:F ActivityManager:E AndroidRuntime:E AnrManager:I PhxPageMemoryChecker:E
            var pfd: ParcelFileDescriptor? = null
            val sdk = Build.VERSION.SDK_INT
            if (sdk <= 23) {
//                pfd = getInstrumentation().getUiAutomation().executeShellCommand("logcat -v time -v year *:I");
                pfd =
                    uiAutomation!!.executeShellCommand("logcat -v time -s libc:F DEBUG:F ActivityManager:E AndroidRuntime:E AnrManager:I PhxPageMemoryChecker:E")
            } else {
//                pfd = getInstrumentation().getUiAutomation().executeShellCommand("logcat -v time -v year *:I");
                pfd =
                    uiAutomation!!.executeShellCommand("logcat -v time -v year -s libc:F DEBUG:F ActivityManager:E AndroidRuntime:E AnrManager:I PhxPageMemoryChecker:E")
            }

            val fis: FileInputStream = ParcelFileDescriptor.AutoCloseInputStream(pfd)
            val bfr = BufferedReader(InputStreamReader(fis))
            var line: String? = ""
            while ((bfr.readLine().also { line = it }) != null) {
                // 判断后几行，是否出现关键词
                if (isMayAppCrash1) {
                    if (Pattern.compile(crashKeywords1_1).matcher(line).find()) {
                        if (screencapThread != null) {
                            screencapThread!!.setIsCrash(true)
                        }
                        if (screenrecordThread != null) {
                            screenrecordThread!!.setIsCrash(true)
                        }
                        if (randomEventThread != null) {
                            randomEventThread!!.setIsCrash(true)
                        }
                        if (nativeFdThread != null) {
                            nativeFdThread!!.setIsCrash(true)
                        }
                        isMayAppCrash1 = false
                        mayAppCrashCount1 = 0
                    } else {
                        if (mayAppCrashCount1 > 3) {
                            isMayAppCrash1 = false
                            mayAppCrashCount1 = 0
                        }
                    }
                    mayAppCrashCount1++
                }
                // 判断后几行，是否出现关键词
                if (isMayAppCrash5) {
                    if (Pattern.compile(crashKeywords5_1).matcher(line).find()) {
                        if (screencapThread != null) {
                            screencapThread!!.setIsCrash(true)
                        }
                        if (screenrecordThread != null) {
                            screenrecordThread!!.setIsCrash(true)
                        }
                        if (randomEventThread != null) {
                            randomEventThread!!.setIsCrash(true)
                        }
                        if (nativeFdThread != null) {
                            nativeFdThread!!.setIsCrash(true)
                        }
                        isMayAppCrash5 = false
                        mayAppCrashCount5 = 0
                    } else {
                        if (mayAppCrashCount5 > 5) {
                            isMayAppCrash5 = false
                            mayAppCrashCount5 = 0
                        }
                    }
                    mayAppCrashCount5++
                }
                // 判断后几行，是否出现关键词
                if (isMayAppCrash6) {
                    if (Pattern.compile(crashKeywords6_1).matcher(line).find()) {
                        if (screencapThread != null) {
                            screencapThread!!.setIsCrash(true)
                        }
                        if (screenrecordThread != null) {
                            screenrecordThread!!.setIsCrash(true)
                        }
                        if (randomEventThread != null) {
                            randomEventThread!!.setIsCrash(true)
                        }
                        if (nativeFdThread != null) {
                            nativeFdThread!!.setIsCrash(true)
                        }
                        isMayAppCrash6 = false
                        mayAppCrashCount6 = 0
                    } else {
                        if (mayAppCrashCount6 > 5) {
                            isMayAppCrash6 = false
                            mayAppCrashCount6 = 0
                        }
                    }
                    mayAppCrashCount6++
                }

                // 判断crash则通知随机事件线程
                if (Pattern.compile(crashKeywords1).matcher(line).find()) {
                    isMayAppCrash1 = true
                } else if (Pattern.compile(crashKeywords2).matcher(line).find()) {
                    if (screencapThread != null) {
                        screencapThread!!.setIsCrash(true)
                    }
                    if (screenrecordThread != null) {
                        screenrecordThread!!.setIsCrash(true)
                    }
                    if (randomEventThread != null) {
                        randomEventThread!!.setIsAnr(true)
                    }
                    if (nativeFdThread != null) {
                        nativeFdThread!!.setIsCrash(true)
                    }
                } else if (Pattern.compile(crashKeywords3).matcher(line).find()) {
                    if (screencapThread != null) {
                        screencapThread!!.setIsCrash(true)
                    }
                    if (screenrecordThread != null) {
                        screenrecordThread!!.setIsCrash(true)
                    }
                    if (randomEventThread != null) {
                        randomEventThread!!.setIsAnr(true)
                    }
                    if (nativeFdThread != null) {
                        nativeFdThread!!.setIsCrash(true)
                    }
                } else if (Pattern.compile(crashKeywords4).matcher(line).find()) {
                    if (screencapThread != null) {
                        screencapThread!!.setIsCrash(true)
                    }
                    if (screenrecordThread != null) {
                        screenrecordThread!!.setIsCrash(true)
                    }
                    if (randomEventThread != null) {
                        randomEventThread!!.setIsCrash(true)
                    }
                    if (nativeFdThread != null) {
                        nativeFdThread!!.setIsCrash(true)
                    }
                } else if (Pattern.compile(crashKeywords5).matcher(line).find()) {
                    isMayAppCrash5 = true
                } else if (Pattern.compile(crashKeywords6).matcher(line).find()) {
                    isMayAppCrash6 = true
                } else if (Pattern.compile(anrKeywords1).matcher(line).find()) {
                    if (screencapThread != null) {
                        screencapThread!!.setIsCrash(true)
                    }
                    if (screenrecordThread != null) {
                        screenrecordThread!!.setIsCrash(true)
                    }
                    if (randomEventThread != null) {
                        randomEventThread!!.setIsCrash(true)
                    }
                    if (nativeFdThread != null) {
                        nativeFdThread!!.setIsCrash(true)
                    }
                }

                // 判断logcat因buffer过大而挂掉了
                if (line!!.lowercase(Locale.getDefault()).contains("unexpected eof")) {
                    logcatThread!!.setIsLogcatCrash(true)
                }

                //记录文件
                if (sdk <= 23) {
                    writeStrToFile(getCurYear() + "-" + line + "\n", logcatFile)
                } else {
                    writeStrToFile(line + "\n", logcatFile)
                }
            }
            (pfd as Object).wait()
            pfd.close()
            bfr.close()

            // 若执行到此，说明logcat停止了，则需重启
            logcatThread!!.setIsLogcatCrash(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
