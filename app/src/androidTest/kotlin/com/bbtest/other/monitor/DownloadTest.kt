package com.bbtest.other.monitor

import com.bbtest.common.PhxCommon
import com.bbtest.utils.FileUtil.createFile
import com.bbtest.utils.FileUtil.createFolder
import com.bbtest.utils.FileUtil.writeStrToFile
import com.bbtest.utils.ShellCommand.execCmdByUiDevice
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class DownloadTest : PhxCommon() {
    private val resultFolder = File(rootFolder, "monitor")
    private val downloadFile = File(resultFolder, "download.txt")

    @Before
    public override fun beforeTest() {
        super.beforeTest()
        // 初始化目录及文件
        createFolder(resultFolder)
        createFile(downloadFile)
    }

    @After
    public override fun afterTest() {
        super.afterTest()
    }

    @Test
    fun testDuration() {
        execCmdByUiDevice(device, "logcat -c")

        startApp(pkgName)
        sleep(TIMEOUT_LONG.toLong())

        // logcat -v time -v year | grep CacheDownload
        val result = execCmdByUiDevice(device, "logcat -v time -v year -s VideoCacheDownloadProxyTask:I")
        println(result)
        val resultLines = result.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (resultLine in resultLines) {
            if (resultLine != "") {
                println(resultLine)
                //                model = resultLine.trim().replaceAll("\\s", "-");
                break
            }
        }

        writeStrToFile("主页: 测试时长-,统计时长-" + "\n", downloadFile)
    }
}
