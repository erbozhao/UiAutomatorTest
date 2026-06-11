package com.bbtest.perform.base

import androidx.test.uiautomator.UiDevice
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.FileUtil
import java.io.File

class CpuThread(
    private val device: UiDevice,
    private val pid: Int,
    private val resultFile: File,
) : Thread() {
    private var isEnd = false

    override fun run() {
        while (!isEnd) {
            CommonUtil.sleep(1000)
            FileUtil.writeStrToFile(CpuInfo(device, pid).getProCpu().toString() + "%\n", resultFile)
        }
    }

    fun setIsEnd(isEnd: Boolean) {
        this.isEnd = isEnd
    }
}
