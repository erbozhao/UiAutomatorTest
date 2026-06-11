package com.bbtest.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Random

object CommonUtil {
    @JvmStatic
    fun getFirstNum(str: String): Int {
        var num = -1
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
        if (tmpNum.isNotEmpty()) {
            num = tmpNum.toString().toInt()
        }
        return num
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
        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val numbers = "0123456789"
        val allCharacters = lower + upper + numbers
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
        return SimpleDateFormat("yyyy").format(Date())
    }

    @JvmStatic
    fun getCurTime(): String {
        return SimpleDateFormat("yyyyMMddHHmmss").format(Date())
    }

    @JvmStatic
    fun getCurTimeForFile(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    }

    @JvmStatic
    fun getCurTimeForLog(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(Date())
    }

    @JvmStatic
    fun getExceptionMsg(e: Exception): String {
        val exceptionMsg = StringBuilder()
        try {
            val msg = e.message
            if (msg != null) {
                exceptionMsg.append("   ").append(msg).append('\n')
            }
            val stacks = e.stackTrace
            for (stack in stacks) {
                exceptionMsg.append("   ").append(stack.toString()).append('\n')
            }
            val cause = e.cause
            if (cause != null) {
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
