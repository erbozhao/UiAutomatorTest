package com.bbtest.perform.common.startexit;

import com.bbtest.common.PerCommon;
import com.bbtest.common.ShellCommon;
import com.bbtest.perform.base.Flowinfo;
import com.bbtest.perform.base.Tcpdump;
import com.bbtest.tools.ProcessInfo;
import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BackstageQuiet extends PerCommon {

    private File resultFolder = new File(perFolder, "common");
    private String scenesName = "backstagequiet";

    @Before
    public void beforeTest() {
        super.beforeTest();
        // 初始化目录及文件
        FileUtil.createFolder(resultFolder);
    }

    @Test
    public void testCpuPhx() {
        testBrowser(BROWSER_CHROME, true);
    }

    @Test
    public void testCpuChrome() {
        testBrowser(BROWSER_CHROME, true);
    }

    @Test
    public void testCpuOpera() {
        testBrowser(BROWSER_OPERA, true);
    }

    @Test
    public void testCpuUc() {
        testBrowser(BROWSER_UC, true);
    }

    @Test
    public void testCpuFirefox() {
        testBrowser(BROWSER_FIREFOX, true);
    }

    private void testBrowser(String pkgName, boolean isCpu) {
        try {
            // 先强制停止
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);

            // 启动浏览器
            startApp(pkgName);
            sleep(10 * 1000);

            // 等待90s
            home();
            sleep(90 * 1000);

            // 再开始监控cpu、fps、flow等
            if (isCpu) {
                startMonitorMainCpu(resultFolder, scenesName, pkgName);
            }

            // 后台静置10m
            sleep(10 * 60 * 1000);

            // 先停止监控cpu、fps、flow等
            if (isCpu) {
                stopMonitorMainCpu();
            }

            // 测试后: 截图
            screenshot(resultFolder + scenesName + "_" + pkgName + ".jpg");

            // 切到前台
            String activity = ShellCommon.getActivity(device, pkgName, null);
            ShellCommon.amStartApp(device, activity, null);
            sleep(5000);

            // 关闭所有多窗口->退出浏览器->强制停止
            closeAllTabs(pkgName);
            exitBrowser(pkgName);
            ShellCommon.forceStopApp(device, pkgName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFlow() {
        // 初始化目录及文件
        File flowResultFile = new File(resultFolder, "flow_" + scenesName + ".txt");
        FileUtil.deleteFile(flowResultFile);
        FileUtil.createFile(flowResultFile);

        // 初始化包名
        List<String> pkgNames = new ArrayList<>();
        pkgNames.add(BROWSER_CHROME);
        pkgNames.add(BROWSER_OPERA);
        pkgNames.add(BROWSER_PHX);
        pkgNames.add(BROWSER_UC);

        // 先强制停止-再启动应用
        int chromeUid = 0;
        int operaUid = 0;
        int phxUid = 0;
        int ucUid = 0;
        for (String pkgName : pkgNames) {
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);
            startApp(pkgName);
            sleep(10 * 1000);
            home();
            ProcessInfo processInfo = new ProcessInfo(device, pkgName);
            int pid = processInfo.getPid();
            if (pkgName.equals(BROWSER_CHROME)) {
                chromeUid = processInfo.getUid(pid);
            } else if (pkgName.equals(BROWSER_OPERA)) {
                operaUid = processInfo.getUid(pid);
            } else if (pkgName.equals(BROWSER_PHX)) {
                phxUid = processInfo.getUid(pid);
            } else if (pkgName.equals(BROWSER_UC)) {
                ucUid = processInfo.getUid(pid);
            }
        }

        // 等待90s
        sleep(90 * 1000);

        long phxStartTotalFlow = new Flowinfo(device, phxUid).getFlow();
        long chromeStartTotalFlow = new Flowinfo(device, chromeUid).getFlow();
        long operaStartTotalFlow = new Flowinfo(device, operaUid).getFlow();
        long ucStartTotalFlow = new Flowinfo(device, ucUid).getFlow();

        // 开始抓包
        Tcpdump tcpdump = new Tcpdump(device);
        tcpdump.startTcpdump();

        // 后台静置24h
        sleep(24 * 60 * 60 * 1000);

        // 结束抓包
        tcpdump.endTcpdump();
        FileUtil.copyFile("/sdcard/capture.pcap", "/sdcard/bbtest/perform/flow_backstagequiet.pcap");

        // 记录结束流量
        long phxEndTotalFlow = new Flowinfo(device, phxUid).getFlow();
        long chromeEndTotalFlow = new Flowinfo(device, chromeUid).getFlow();
        long operaEndTotalFlow = new Flowinfo(device, operaUid).getFlow();
        long ucEndTotalFlow = new Flowinfo(device, ucUid).getFlow();

        // 计算流量(统计的是b，需转换为kb)
        double phxTotalFlow = CommonUtil.keepDecimalPoint((double) (phxEndTotalFlow - phxStartTotalFlow) / 1024, 1);
        double chromeTotalFlow = CommonUtil.keepDecimalPoint((double) (chromeEndTotalFlow - chromeStartTotalFlow) / 1024, 1);
        double operaTotalFlow = CommonUtil.keepDecimalPoint((double) (operaEndTotalFlow - operaStartTotalFlow) / 1024, 1);
        double ucTotalFlow = CommonUtil.keepDecimalPoint((double) (ucEndTotalFlow - ucStartTotalFlow) / 1024, 1);
        FileUtil.writeStrToFile(" phxTotalFlow=" + phxTotalFlow + ",chromeTotalFlow=" + chromeTotalFlow
                + ",operaTotalFlow=" + operaTotalFlow + ",ucTotalFlow=" + ucTotalFlow + "\n", flowResultFile);

        // 切到前台-关闭所有多窗口->退出浏览器->强制停止
        for (String pkgName : pkgNames) {
            String activity = ShellCommon.getActivity(device, pkgName, null);
            ShellCommon.amStartApp(device, activity, null);
            sleep(5 * 1000);
            closeAllTabs(pkgName);
            exitBrowser(pkgName);
            ShellCommon.forceStopApp(device, pkgName, null);
            sleep(3000);
        }
    }

    @After
    public void afterTest() {
        super.afterTest();
    }
}
