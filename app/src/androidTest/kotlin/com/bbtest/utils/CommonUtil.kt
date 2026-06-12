package com.bbtest.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Random

object CommonUtil {
    private const val FILE_TIME_PATTERN = "yyyyMMdd_HHmmss"
    private const val LOG_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS"
    private const val FULL_TIME_PATTERN = "yyyyMMddHHmmss"
    private const val YEAR_PATTERN = "yyyy"

    @JvmStatic
    fun getFirstNum(str: String): Int {
        val tmpNum = StringBuilder()
        var isStart = false
        for (char in str) {
            if (char.isDigit()) {
                isStart = true
                tmpNum.append(char)
            } else if (isStart) {
                break
            }
        }
        return tmpNum.toString().toIntOrNull() ?: -1
    }

    @JvmStatic
    fun isNumer(str: String): Boolean {
        for (char in str) {
            if (!char.isDigit()) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun keepDecimalPoint(number: Double, point: Int): Double {
        return String.format("%.${point}f", number).toDouble()
    }

    @JvmStatic
    fun randomStr(length: Int): String {
        val allCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val randomString = StringBuilder()
        val random = Random()
        for (i in 0 until length) {
            val randomIndex = random.nextInt(allCharacters.length)
            randomString.append(allCharacters[randomIndex])
        }
        return randomString.toString()
    }

    @JvmStatic
    fun randomInt(startNum: Int, endNum: Int): Int {
        return Random().nextInt(endNum - startNum + 1) + startNum
    }

    @JvmStatic
    fun getCurYear(): String {
        return SimpleDateFormat(YEAR_PATTERN).format(Date())
    }

    @JvmStatic
    fun getCurTime(): String {
        return SimpleDateFormat(FULL_TIME_PATTERN).format(Date())
    }

    @JvmStatic
    fun getCurTimeForFile(): String {
        return SimpleDateFormat(FILE_TIME_PATTERN).format(Date())
    }

    @JvmStatic
    fun getCurTimeForLog(): String {
        return SimpleDateFormat(LOG_TIME_PATTERN).format(Date())
    }

    @JvmStatic
    fun getExceptionMsg(e: Exception): String {
        val exceptionMsg = StringBuilder()
        try {
            e.message?.let { msg ->
                exceptionMsg.append("   ").append(msg).append('\n')
            }
            e.stackTrace.forEach { stack ->
                exceptionMsg.append("   ").append(stack).append('\n')
            }
            e.cause?.let { cause ->
                exceptionMsg.append("   Caused by: ").append(cause.message).append('\n')
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        return exceptionMsg.toString()
    }

    @JvmStatic
    fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
