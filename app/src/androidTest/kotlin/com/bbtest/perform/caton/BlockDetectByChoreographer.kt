package com.bbtest.perform.caton

import android.view.Choreographer
import android.view.Choreographer.FrameCallback
import java.util.concurrent.TimeUnit

object BlockDetectByChoreographer {
    fun start2() {
        Choreographer.getInstance().postFrameCallback(FPSFrameCallback(System.nanoTime()))
        BlockDetectByPrinter.start()
    }

    fun start() {
        Choreographer.getInstance().postFrameCallback(object : FrameCallback {
            var lastFrameTimeNanos: Long = 0
            var currentFrameTimeNanos: Long = 0

            override fun doFrame(frameTimeNanos: Long) {
                if (lastFrameTimeNanos == 0L) {
                    lastFrameTimeNanos = frameTimeNanos
                }
                currentFrameTimeNanos = frameTimeNanos
                val diffMs = TimeUnit.MILLISECONDS.convert(currentFrameTimeNanos - lastFrameTimeNanos, TimeUnit.NANOSECONDS)
                if (diffMs > 16.6f) {
                    val droppedCount = (diffMs / 16.6).toLong()
                }
                if (LogMonitor.instance.isMonitor) {
                    LogMonitor.instance.removeMonitor()
                }
                LogMonitor.instance.startMonitor()
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }
}
