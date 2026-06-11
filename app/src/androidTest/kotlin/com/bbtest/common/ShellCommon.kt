package com.bbtest.common

import androidx.test.uiautomator.UiDevice
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.FileUtil
import com.bbtest.utils.ShellCommand
import java.io.File
import java.util.regex.Pattern

object ShellCommon {
    @JvmStatic
    fun amStartApp(device: UiDevice, activity: String, runLog: File?) {
        try {
            val result = ShellCommand.execCmdByUiDevice(device, "am start -W -n $activity")
            if (runLog != null) {
                FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  am start -W -n $activity\n", runLog)
                val resultLines = result.split("\n")
                for (resultLine in resultLines) {
                    if (resultLine != "") {
                        FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun forceStopApp(device: UiDevice, pkgName: String, runLog: File?) {
        try {
            val result = ShellCommand.execCmdByUiDevice(device, "am force-stop $pkgName")
            if (runLog != null) {
                FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  am force-stop $pkgName\n", runLog)
                val resultLines = result.split("\n")
                for (resultLine in resultLines) {
                    if (resultLine != "") {
                        FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun killAllApp(device: UiDevice, runLog: File?) {
        try {
            val killallResult = ShellCommand.execCmdByUiDevice(device, "am kill-all ")
            if (runLog != null) {
                FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  am kill-all\n", runLog)
                val killallResultLines = killallResult.split("\n")
                for (killallResultLine in killallResultLines) {
                    if (killallResultLine != "") {
                        FileUtil.writeStrToFile("                         ${killallResultLine.trim()}\n", runLog)
                    }
                }
            }

            if (runLog != null) {
                FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  dumpsys activity | grep 'Run #'\n", runLog)
            }
            val curRunResult = ShellCommand.execCmdByUiDevice(device, "dumpsys activity activities")
            val curRunResultLines = curRunResult.split("\n")
            for (curRunResultLine in curRunResultLines) {
                if (curRunResultLine != "" && curRunResultLine.contains("Run #")) {
                    if (runLog != null) {
                        FileUtil.writeStrToFile("                         ${curRunResultLine.trim()}\n", runLog)
                    }
                    var id = ""
                    var tmpPkgName = ""
                    val curRunResultLineParts = curRunResultLine.trim().split("\\s+".toRegex())
                    for (curRunResultLinePart in curRunResultLineParts) {
                        if (curRunResultLinePart.trim().startsWith("t") && curRunResultLinePart.trim().endsWith("}")) {
                            id = curRunResultLinePart.substring(
                                curRunResultLinePart.indexOf("t") + 1,
                                curRunResultLinePart.lastIndexOf("}"),
                            ).trim()
                        } else if (curRunResultLinePart.trim().contains("/")) {
                            tmpPkgName = curRunResultLinePart.trim()
                        }
                    }
                    if (id != "" && id.toInt() > 300) {
                        forceStopApp(device, tmpPkgName, runLog)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getActivity(device: UiDevice, pkgName: String, runLog: File?): String {
        var activity = ""
        try {
            if (runLog != null) {
                FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  dumpsys package $pkgName\n", runLog)
            }
            val result = ShellCommand.execCmdByUiDevice(device, "dumpsys package $pkgName")
            val resultLines = result.split("\n")
            var isFound = false
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    if (!isFound) {
                        if (resultLine.contains("android.intent.action.MAIN:")) {
                            isFound = true
                        }
                    } else {
                        if (resultLine.contains(pkgName)) {
                            if (runLog != null) {
                                FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                            }
                            val temp = resultLine.trim().split("\\s+".toRegex())
                            activity = temp[1]
                        }
                        if (activity.matches((pkgName + "/\\D+[.\\D+]+").toRegex())) {
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return activity
    }

    @JvmStatic
    fun getActivities(device: UiDevice, pkgName: String, runLog: File?): List<String> {
        val activities = mutableListOf<String>()
        try {
            if (runLog != null) {
                FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  dumpsys package $pkgName\n", runLog)
            }
            val result = ShellCommand.execCmdByUiDevice(device, "dumpsys package $pkgName")
            val resultLines = result.split("\n")
            var isFound = false
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    if (!isFound) {
                        if (resultLine.contains("android.intent.action.MAIN:")) {
                            isFound = true
                        }
                    } else {
                        if (resultLine.contains(pkgName)) {
                            if (runLog != null) {
                                FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                            }
                            val temp = resultLine.trim().split("\\s+".toRegex())
                            activities.add(temp[1])
                        }
                        if (resultLine.matches("\\D+[.\\D+]+:".toRegex())) {
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return activities
    }

    @JvmStatic
    fun getTopActivity(device: UiDevice, runLog: File?): String {
        var topActivity = ""
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  dumpsys activity top | grep ACTIVITY\n", runLog)
        }
        val result = ShellCommand.execCmdByUiDevice(device, "dumpsys activity top")
        val resultLines = result.split("\n")
        loop@ for (i in resultLines.size - 1 downTo 0) {
            val resultLine = resultLines[i]
            if (resultLine != "" && Pattern.compile(".*ACTIVITY\\s+.*\\/.*").matcher(resultLine).find()) {
                if (runLog != null) {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
                val resultLineParts = resultLine.trim().split("\\s+".toRegex())
                for (resultLinePart in resultLineParts) {
                    if (resultLinePart.contains("/")) {
                        topActivity = resultLinePart.trim()
                        break@loop
                    }
                }
            }
        }
        return topActivity
    }

    @JvmStatic
    fun getTopActivity2(device: UiDevice, runLog: File?): String {
        var topActivity = ""
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  dumpsys SurfaceFlinger --list\n", runLog)
        }
        val result = ShellCommand.execCmdByUiDevice(device, "dumpsys SurfaceFlinger --list")
        val resultLines = result.split("\n")
        for (i in resultLines.size - 1 downTo 0) {
            val resultLine = resultLines[i]
            if (resultLine.matches(".*\\D+[.\\D+]+/\\D+[.\\D+]+.*".toRegex())) {
                if (runLog != null) {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
                val linePart = resultLine.trim().split("\\s+".toRegex())
                topActivity =
                    if (resultLine.contains("#")) {
                        if (linePart.size > 1) linePart[1].split("#")[0].trim() else linePart[0].split("#")[0].trim()
                    } else {
                        linePart[0].trim()
                    }
                if (topActivity.contains("[")) {
                    topActivity = topActivity.substring(0, topActivity.indexOf("[")).trim()
                }
                break
            }
        }
        return topActivity
    }

    @JvmStatic
    fun isAppBackstage(device: UiDevice, pkgName: String): Boolean {
        var isAppBackstage = false
        try {
            val curActivity = getTopActivity(device, null)
            val curPkgName = if (curActivity.contains("/")) curActivity.split("/")[0] else curActivity
            isAppBackstage = curPkgName != pkgName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isAppBackstage
    }

    @JvmStatic
    fun isAppBackstage(device: UiDevice, activity: String, topActivity: String): Boolean {
        var isAppBackstage = false
        try {
            val curActivity = getTopActivity(device, null)
            isAppBackstage = curActivity != activity && curActivity != topActivity
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isAppBackstage
    }

    @JvmStatic
    fun isProcessExist(device: UiDevice, pkgName: String): Boolean {
        var isProcessExist = false
        try {
            val result = ShellCommand.execCmdByUiDevice(device, "ps -ef")
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "" && resultLine.contains(pkgName)) {
                    val parts = resultLine.trim().split("\\s+".toRegex())
                    if (parts[parts.size - 1].trim() == pkgName) {
                        isProcessExist = true
                        break
                    } else {
                        isProcessExist = false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isProcessExist
    }

    @JvmStatic
    fun click(device: UiDevice, x: Int, y: Int, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "input tap ${x} ${y}")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  click (${x},${y})\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun swipe(device: UiDevice, fromX: Int, fromY: Int, toX: Int, toY: Int, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "input swipe ${fromX} ${fromY} ${toX} ${toY}")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  swipe (${fromX},${fromY}) up to (${toX},${toY})\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun drag(device: UiDevice, fromX: Int, fromY: Int, toX: Int, toY: Int, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "input draganddrop ${fromX} ${fromY} ${toX} ${toY}")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  drag (${fromX},${fromY}) to (${toX},${toY})\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun longClick(device: UiDevice, x: Int, y: Int, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "input swipe ${x} ${y} ${x} ${y} 600")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  longClick (${x},${y})\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun type(device: UiDevice, str: String, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "input text $str")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  type text\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun pressHome(device: UiDevice, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "input keyevent 3")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  press home\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun pressBack(device: UiDevice, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "input keyevent 4")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  press back\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun dpadLeft(device: UiDevice, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "input keyevent 21")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  move the cursor to the left\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun dpadRight(device: UiDevice, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "input keyevent 22")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  move the cursor to the right\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun pressEnter(device: UiDevice, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "input keyevent 66")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  press enter\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun visitWebsite(device: UiDevice, pkgName: String, url: String, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "am start -a android.intent.action.VIEW -p $pkgName -d $url")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  open ${url}with ${pkgName}\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun del(device: UiDevice, runLog: File?) {
        val result = ShellCommand.execCmdByUiDevice(device, "input keyevent 67")
        if (runLog != null) {
            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  del text\n", runLog)
            val resultLines = result.split("\n")
            for (resultLine in resultLines) {
                if (resultLine != "") {
                    FileUtil.writeStrToFile("                         ${resultLine.trim()}\n", runLog)
                }
            }
        }
    }

    @JvmStatic
    fun grantApkPermission(device: UiDevice, pkgName: String) {
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.READ_CALENDAR")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.WRITE_CALENDAR")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.CAMERA")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.READ_CONTACTS")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.WRITE_CONTACTS")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.GET_ACCOUNTS")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.ACCESS_FINE_LOCATION")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.ACCESS_COARSE_LOCATION")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.READ_PHONE_STATE")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.MODIFY_PHONE_STATE")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.CALL_PHONE")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.READ_CALL_LOG")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.WRITE_CALL_LOG")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.ADD_VOICEMAIL")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.USE_SIP")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.PROCESS_OUTGOING_CALLS")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.READ_EXTERNAL_STORAGE")
        ShellCommand.execCmdByUiDevice(device, "pm grant $pkgName android.permission.WRITE_EXTERNAL_STORAGE")
    }

    @JvmStatic
    fun screenshot(device: UiDevice, file: File) {
        try {
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            device.takeScreenshot(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun setBufferSize(device: UiDevice, mb: Int, runLog: File?) {
        var bufferSize = mb
        while (true) {
            var isSetSuccess = true
            val result = ShellCommand.execCmdByUiDevice(device, "logcat -G ${bufferSize}M")
            val lines = result.split("\n")
            for (line in lines) {
                if (line.trim() != "" || line.contains("failed")) {
                    isSetSuccess = false
                    break
                }
            }
            if (runLog != null) {
                FileUtil.writeStrToFile(
                    "${CommonUtil.getCurTimeForLog()}  setBufferSize=${bufferSize}M,isSetSuccess=${isSetSuccess},result=${result.trim().replace("\n", "")}\n",
                    runLog,
                )
            }
            if (isSetSuccess) {
                break
            } else {
                bufferSize -= 1
            }
        }
    }

    @JvmStatic
    fun isNearMaxBufferSize(device: UiDevice, runLog: File?): Boolean {
        var isNearMaxBufferSize = false
        val result = ShellCommand.execCmdByUiDevice(device, "logcat -g")
        val lines = result.split("\n")
        for (line in lines) {
            if (line != "" && line.contains("main:")) {
                val tmpMaxBufferSize = line.substring(line.indexOf("is") + 2, line.indexOf("(")).replace("\\s+".toRegex(), "")
                val tmpCurBufferSize = line.substring(line.indexOf("(") + 1, line.indexOf("consumed")).replace("\\s+".toRegex(), "")
                val maxBufferSize = CommonUtil.getFirstNum(tmpMaxBufferSize)
                val curBufferSize = CommonUtil.getFirstNum(tmpCurBufferSize)
                val maxBufferSizeUnit = tmpMaxBufferSize.replace(maxBufferSize.toString(), "").trim()
                val curBufferSizeUnit = tmpCurBufferSize.replace(curBufferSize.toString(), "").trim()
                val convMaxBufferSize =
                    when {
                        maxBufferSizeUnit.startsWith("G") -> maxBufferSize.toLong() * 1024 * 1024 * 1024 * 2 / 3
                        maxBufferSizeUnit.startsWith("M") -> maxBufferSize.toLong() * 1024 * 1024 * 2 / 3
                        maxBufferSizeUnit.startsWith("K") -> maxBufferSize.toLong() * 1024 * 2 / 3
                        else -> maxBufferSize.toLong() * 2 / 3
                    }
                val convCurBufferSize =
                    when {
                        curBufferSizeUnit.startsWith("G") -> curBufferSize.toLong() * 1024 * 1024 * 1024
                        curBufferSizeUnit.startsWith("M") -> curBufferSize.toLong() * 1024 * 1024
                        curBufferSizeUnit.startsWith("K") -> curBufferSize.toLong() * 1024
                        else -> curBufferSize.toLong()
                    }

                if (convCurBufferSize >= convMaxBufferSize) {
                    isNearMaxBufferSize = true
                }
                if (runLog != null) {
                    FileUtil.writeStrToFile(
                        "${CommonUtil.getCurTimeForLog()}  maxBufferSize=${maxBufferSize},maxBufferSizeUnit=${maxBufferSizeUnit},curBufferSize=${curBufferSize},curBufferSizeUnit=${curBufferSizeUnit},convMaxBufferSize=${convMaxBufferSize},convCurBufferSize=${convCurBufferSize},isNearMaxBufferSize=${isNearMaxBufferSize}\n",
                        runLog,
                    )
                }
                break
            }
        }
        return isNearMaxBufferSize
    }

    @JvmStatic
    fun clearBufferCache(device: UiDevice, runLog: File?) {
        while (true) {
            var isClearSuccess = true
            val result = ShellCommand.execCmdByUiDevice(device, "logcat -c")
            val lines = result.split("\n")
            for (line in lines) {
                if (line.trim() != "" || line.contains("failed")) {
                    isClearSuccess = false
                    break
                }
            }
            if (runLog != null) {
                FileUtil.writeStrToFile(
                    "${CommonUtil.getCurTimeForLog()}  isClearSuccess=${isClearSuccess},result=${result.trim().replace("\n", "")}\n",
                    runLog,
                )
            }
            if (isClearSuccess) {
                break
            }
        }
    }

    @JvmStatic
    fun killLogcat(device: UiDevice, runLog: File?) {
        while (true) {
            var isKillLogcatSuccess = true
            val killallResult = ShellCommand.execCmdByUiDevice(device, "killall -9 logcat")
            val killallLines = killallResult.split("\n")
            for (killallLine in killallLines) {
                if (killallLine.trim() != "" || killallLine.contains("failed")) {
                    isKillLogcatSuccess = false
                    break
                }
            }
            if (runLog != null) {
                FileUtil.writeStrToFile(
                    "${CommonUtil.getCurTimeForLog()}  isKillLogcatSuccess=${isKillLogcatSuccess},result=${killallResult.trim().replace("\n", "")}\n",
                    runLog,
                )
            }

            if (!isKillLogcatSuccess) {
                val logcatPids = mutableListOf<String>()
                val lsLogcatResult = ShellCommand.execCmdByUiDevice(device, "ps -ef")
                val lsLogcatLines = lsLogcatResult.split("\n")
                for (lsLogcatLine in lsLogcatLines) {
                    if (lsLogcatLine != "" && lsLogcatLine.contains("logcat")) {
                        if (runLog != null) {
                            FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  lsLogcatline=${lsLogcatLine.trim()}\n", runLog)
                        }
                        if (lsLogcatLine.contains("logcat -v time")) {
                            val parts = lsLogcatLine.trim().split("\\s+".toRegex())
                            logcatPids.add(parts[1].trim())
                        }
                    }
                }

                if (logcatPids.isNotEmpty()) {
                    for (logcatPid in logcatPids) {
                        val killResult = ShellCommand.execCmdByUiDevice(device, "kill -9 $logcatPid")
                        val killLines = killResult.split("\n")
                        for (killLine in killLines) {
                            if (killLine.trim() != "" || killLine.contains("failed")) {
                                isKillLogcatSuccess = false
                                break
                            }
                        }
                        if (runLog != null) {
                            FileUtil.writeStrToFile(
                                "${CommonUtil.getCurTimeForLog()}  isKillLogcatSuccess=${isKillLogcatSuccess},logcatPid=${logcatPid},result=${killResult.trim().replace("\n", "")}\n",
                                runLog,
                            )
                        }
                    }
                } else {
                    isKillLogcatSuccess = true
                    if (runLog != null) {
                        FileUtil.writeStrToFile(
                            "${CommonUtil.getCurTimeForLog()}  isKillLogcatSuccess=${isKillLogcatSuccess},logcatPids=${logcatPids}\n",
                            runLog,
                        )
                    }
                }
            }

            if (isKillLogcatSuccess) {
                break
            }
        }
    }

    @JvmStatic
    fun isLogcatCrash(device: UiDevice, runLog: File?): Boolean {
        var isLogcatCrash = true
        val lsLogcatResult = ShellCommand.execCmdByUiDevice(device, "ps -ef")
        val lsLogcatLines = lsLogcatResult.split("\n")
        for (lsLogcatLine in lsLogcatLines) {
            if (lsLogcatLine != "" && lsLogcatLine.contains("logcat -v time")) {
                if (runLog != null) {
                    FileUtil.writeStrToFile("${CommonUtil.getCurTimeForLog()}  isLogcat=${lsLogcatLine.trim()}\n", runLog)
                }
                isLogcatCrash = false
                break
            }
        }
        return isLogcatCrash
    }
}
