package com.bbtest.utils

import android.app.Instrumentation
import android.os.ParcelFileDescriptor
import androidx.test.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.InputStreamReader

object ShellCommand {
    @JvmStatic
    fun execCmd(cmd: String): String {
        val result = StringBuilder()
        try {
            val process = Runtime.getRuntime().exec(cmd)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result.append(line).append('\n')
            }
            reader.close()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result.toString()
    }

    @JvmStatic
    fun execCmd(cmds: Array<String>): String {
        val result = StringBuilder()
        try {
            val processBuilder = ProcessBuilder(*cmds)
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result.append(line).append('\n')
            }
            reader.close()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result.toString()
    }

    @JvmStatic
    fun execCmdByUiDevice(device: UiDevice, cmd: String): String {
        return try {
            device.executeShellCommand(cmd)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @JvmStatic
    fun execCmdByNoRoot(cmd: String): String {
        val result = StringBuilder()
        try {
            val instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation()
            val parcelFileDescriptor: ParcelFileDescriptor = instrumentation.uiAutomation.executeShellCommand(cmd)
            val inputStream = ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result.append(line).append('\n')
            }
            inputStream.close()
            reader.close()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result.toString()
    }

    @JvmStatic
    fun execCmdBySh(cmd: String): String {
        val result = StringBuilder()
        try {
            val process = Runtime.getRuntime().exec("sh")
            val outputStream = DataOutputStream(process.outputStream)
            outputStream.writeBytes("$cmd\n")
            outputStream.flush()
            outputStream.close()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (!line.isNullOrEmpty()) {
                    result.append(line).append('\n')
                }
            }
            process.waitFor()
            process.destroy()
            reader.close()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result.toString()
    }

    @JvmStatic
    fun execCmdBySu(cmd: String): String {
        val result = StringBuilder()
        try {
            val process = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(process.outputStream)
            outputStream.writeBytes("$cmd\n")
            outputStream.flush()
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (!line.isNullOrEmpty()) {
                    result.append(line).append('\n')
                }
            }
            process.waitFor()
            process.destroy()
            reader.close()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result.toString()
    }
}
