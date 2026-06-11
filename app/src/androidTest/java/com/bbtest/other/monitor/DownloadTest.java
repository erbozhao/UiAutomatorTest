package com.bbtest.other.monitor;

import com.bbtest.common.PhxCommon;
import com.bbtest.utils.FileUtil;
import com.bbtest.utils.ShellCommand;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class DownloadTest extends PhxCommon {

    private File resultFolder = new File(rootFolder, "monitor");
    private File downloadFile = new File(resultFolder, "download.txt");

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
        FileUtil.createFile(downloadFile);
    }

    @After
    public void afterTest() {
        super.afterTest();
    }

    @Test
    public void testDuration() {
        ShellCommand.execCmdByUiDevice(device, "logcat -c");

        startApp(pkgName);
        sleep(TIMEOUT_LONG);

        // logcat -v time -v year | grep CacheDownload
        String result = ShellCommand.execCmdByUiDevice(device, "logcat -v time -v year -s VideoCacheDownloadProxyTask:I");
        System.out.println(result);
        String[] resultLines = result.split("\n");
        for (String resultLine : resultLines) {
            if (!resultLine.equals("")) {
                System.out.println(resultLine);
//                model = resultLine.trim().replaceAll("\\s", "-");
                break;
            }
        }

        FileUtil.writeStrToFile("主页: 测试时长-,统计时长-" + "\n", downloadFile);
    }
}
