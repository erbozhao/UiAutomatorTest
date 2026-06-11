package com.bbtest.perform.base

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object GpuInfo {
    private var fileExists = false
    private var fileChecked = false
    private const val GPU_FILE = "sys/class/kgsl/kgsl-3d0/gpubusy"

    @JvmStatic
    fun getGpuUsage(): Double {
        var result = 0.0

        if (!fileChecked) {
            fileChecked = true
            fileExists = File(GPU_FILE).exists()
        }

        if (!fileExists) {
            return result
        }

        var fileReader: FileReader? = null
        var bufferedReader: BufferedReader? = null
        try {
            fileReader = FileReader(GPU_FILE)
            bufferedReader = BufferedReader(fileReader, 128)
            var cpu = bufferedReader.readLine()
            if (cpu != null) {
                cpu = cpu.trim().replace(" +".toRegex(), " ")
                val current = cpu.split(" ")[0].trim()
                val total = cpu.split(" ")[1].trim()
                if (current.isNotEmpty() && total.isNotEmpty() && !total.equals("0", ignoreCase = true)) {
                    result = current.toDouble() / total.toDouble()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            bufferedReader?.close()
            fileReader?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }
}
