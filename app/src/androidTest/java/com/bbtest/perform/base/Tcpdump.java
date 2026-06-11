package com.bbtest.perform.base;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.tools.ProcessInfo;
import com.bbtest.utils.ShellCommand;

/**
 * 抓取流量包(需root)
 */
public class Tcpdump {

    private UiDevice device = null;

    public Tcpdump(UiDevice device) {
        this.device = device;
    }


    // 开始抓包
    public void startTcpdump() {
        new TcpdumpThread(device).start();
    }

    // 结束抓包
    public void endTcpdump() {
        // 获取pid
        ProcessInfo processInfo = new ProcessInfo(device, "tcpdump");
        int pid = processInfo.getPid();

        //杀掉抓包进程
        String cmd = "kill " + pid;
        ShellCommand.execCmdBySu(cmd);
    }

}

class TcpdumpThread extends Thread {
    private UiDevice device = null;

    public TcpdumpThread(UiDevice device) {
        this.device = device;
    }

    @Override
    public void run() {
        // 先删除抓包文件
        String rmCmd = "rm -rf /sdcard/Download/capture.pcap";
        ShellCommand.execCmdByUiDevice(device, rmCmd);

        // 再开始抓包
        String captureCmd = "/data/local/tcpdump -i any -p -vv -s 0 -w /sdcard/Download/capture.pcap";
        ShellCommand.execCmdBySu(captureCmd);
    }
}
