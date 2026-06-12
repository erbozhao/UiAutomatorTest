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
    private inline fun readProcessOutput(block: () -> java.io.InputStream): String {
        val result = StringBuilder()
        try {
            BufferedReader(InputStreamReader(block())).use { reader ->
                reader.lineSequence().forEach { line ->
                    result.append(line).append('\n')
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result.toString()
    }

    @JvmStatic
    fun execCmd(cmd: String): String = readProcessOutput {
        Runtime.getRuntime().exec(cmd).inputStream
    }

    @JvmStatic
    fun execCmd(cmds: Array<String>): String = readProcessOutput {
        ProcessBuilder(*cmds)
            .redirectErrorStream(true)
            .start()
            .inputStream
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
    fun execCmdByNoRoot(cmd: String): String = readProcessOutput {
        val instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation()
        val parcelFileDescriptor: ParcelFileDescriptor = instrumentation.uiAutomation.executeShellCommand(cmd)
        ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor)
    }

    @JvmStatic
    fun execCmdBySh(cmd: String): String {
        val result = StringBuilder()
        try {
            val process = Runtime.getRuntime().exec("sh")
            DataOutputStream(process.outputStream).use { outputStream ->
                outputStream.writeBytes("$cmd\n")
                outputStream.flush()
            }
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                reader.lineSequence()
                    .filter { it.isNotEmpty() }
                    .forEach { line -> result.append(line).append('\n') }
            }
            process.waitFor()
            process.destroy()
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
            DataOutputStream(process.outputStream).use { outputStream ->
                outputStream.writeBytes("$cmd\n")
                outputStream.flush()
                outputStream.writeBytes("exit\n")
                outputStream.flush()
            }
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                reader.lineSequence()
                    .filter { it.isNotEmpty() }
                    .forEach { line -> result.append(line).append('\n') }
            }
            process.waitFor()
            process.destroy()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result.toString()
    }
}
