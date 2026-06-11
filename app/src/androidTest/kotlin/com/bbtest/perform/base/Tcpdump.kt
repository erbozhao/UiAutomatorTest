package com.bbtest.perform.base

import androidx.test.uiautomator.UiDevice
import com.bbtest.tools.ProcessInfo
import com.bbtest.utils.ShellCommand

class Tcpdump(private val device: UiDevice) {
    fun startTcpdump() {
        TcpdumpThread(device).start()
    }

    fun endTcpdump() {
        val processInfo = ProcessInfo(device, "tcpdump")
        val pid = processInfo.pid
        val cmd = "kill $pid"
        ShellCommand.execCmdBySu(cmd)
    }
}

private class TcpdumpThread(private val device: UiDevice) : Thread() {
    override fun run() {
        val rmCmd = "rm -rf /sdcard/Download/capture.pcap"
        ShellCommand.execCmdByUiDevice(device, rmCmd)

        val captureCmd = "/data/local/tcpdump -i any -p -vv -s 0 -w /sdcard/Download/capture.pcap"
        ShellCommand.execCmdBySu(captureCmd)
    }
}
