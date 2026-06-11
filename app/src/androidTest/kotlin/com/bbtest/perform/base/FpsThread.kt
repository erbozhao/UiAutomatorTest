package com.bbtest.perform.base

import androidx.test.uiautomator.UiDevice
import com.bbtest.utils.CommonUtil
import com.bbtest.utils.FileUtil
import java.io.File

class FpsThread(
    private val device: UiDevice,
    private val pkgName: String,
    private val resultFile: File,
) : Thread() {
    private var isEnd = false

    override fun run() {
        while (!isEnd) {
            CommonUtil.sleep(2000)
            FileUtil.writeStrToFile(FpsInfo(device, pkgName).getFps().toString() + "\n", resultFile)
        }
    }

    fun setIsEnd(isEnd: Boolean) {
        this.isEnd = isEnd
    }
}
