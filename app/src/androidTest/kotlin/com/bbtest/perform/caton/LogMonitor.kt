package com.bbtest.perform.caton

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log

/**
 * 当匹配到>>>>> Dispatching时，执行startMonitor，会在200ms（设定的卡顿阈值）后执行任务，这个任务负责在子执行绪（非UI执行绪）列印UI执行绪的堆叠资讯
 * 如果讯息低于200ms 内执行完成，就可以匹配到<<<<< Finished 日志，那么在列印堆叠任务启动前执行removeMonitor取消了这个任务，则认为没有卡顿的发生
 * 如果讯息超过200ms 才执行完毕，此时认为发生了卡顿，并列印UI 执行绪的堆叠资讯
 * 注：有些手机或Rom的Loop函式不打印">>>>> Dispatching to"和"<<<<< Finished to"这样的日志Loop，故优化卡顿的判断改为第一句log时当作startMonitor，输出下一句log时当作end时刻来解决这个问题。
 */
class LogMonitor private constructor() {
    private val mLogThread = HandlerThread("log")
    private val mIoHandler: Handler

    init {
        mLogThread.start()
        mIoHandler = Handler(mLogThread.getLooper())
    }

    val isMonitor: Boolean
        get() = mIoHandler.hasCallbacks(mLogRunnable)

    fun startMonitor() {
        mIoHandler.postDelayed(mLogRunnable, TIME_BLOCK)
    }

    fun removeMonitor() {
        mIoHandler.removeCallbacks(mLogRunnable)
    }

    companion object {
        private const val TAG = "LogMonitor"
        val instance: LogMonitor = LogMonitor()
        private const val TIME_BLOCK = 200L

        private val mLogRunnable: Runnable = object : Runnable {
            override fun run() {
                val sb = StringBuilder()
                val stackTrace = Looper.getMainLooper().getThread().getStackTrace()
                for (s in stackTrace) {
                    sb.append("$s\n")
                }
                Log.e(TAG, sb.toString())
            }
        }
    }
}
