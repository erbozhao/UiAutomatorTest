package com.bbtest.perform.base

import android.util.Log
import android.view.Choreographer
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger

class SmInfo {
    private val frameIntervalNanos = (1000000000 / 60.0).toLong()
    private val droppedFrames = ArrayList<Long>()

    private var totalFrames = 0L
    private var jankFrames = 0L
    private var extraJankTimes = 0
    private var fps = 0.0
    private var lostFrameRate = 0.0

    private val frameCallback = object : Choreographer.FrameCallback {
        var lastFrameTimeNanos = 0L
        var currentFrameTimeNanos = 0L

        override fun doFrame(frameTimeNanos: Long) {
            if (lastFrameTimeNanos == 0L) {
                lastFrameTimeNanos = frameTimeNanos
            }
            currentFrameTimeNanos = System.nanoTime()
            val jitterNanos = currentFrameTimeNanos - lastFrameTimeNanos
            if (jitterNanos > frameIntervalNanos) {
                droppedFrames.add(jitterNanos / frameIntervalNanos)
            }
            lastFrameTimeNanos = frameTimeNanos
            count.incrementAndGet()
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun startSM2() {
        Choreographer.getInstance().postFrameCallback(frameCallback)
        val thread = object : Thread() {
            override fun run() {
                val startTime = System.currentTimeMillis()
                while (true) {
                    val endTime = System.currentTimeMillis()
                    val costTime = endTime - startTime
                    if (costTime >= 1000) {
                        fps = count.getAndSet(0).toDouble()
                        for (droppedFrame in droppedFrames) {
                            jankFrames += droppedFrame
                        }
                        lostFrameRate = jankFrames.toDouble() / 60
                        break
                    }
                }
            }
        }
        thread.start()
    }

    private fun getFpsByChoreographer() {
        val startTime = System.currentTimeMillis()
        val count = AtomicInteger(0)
        val droppedFrames = ArrayList<Long>()

        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            var lastFrameTimeNanos = 0L
            var currentFrameTimeNanos = 0L
            val frameIntervalNanos = (1000000000 / 60.0).toLong()

            override fun doFrame(frameTimeNanos: Long) {
                if (lastFrameTimeNanos == 0L) {
                    lastFrameTimeNanos = frameTimeNanos
                }
                currentFrameTimeNanos = System.nanoTime()
                val jitterNanos = currentFrameTimeNanos - lastFrameTimeNanos
                if (jitterNanos > frameIntervalNanos) {
                    droppedFrames.add(jitterNanos / frameIntervalNanos)
                }
                lastFrameTimeNanos = frameTimeNanos
                count.incrementAndGet()
                Choreographer.getInstance().postFrameCallback(this)
            }
        })

        while (true) {
            val endTime = System.currentTimeMillis()
            val costTime = endTime - startTime
            if (costTime > 1000) {
                fps = count.getAndSet(0).toDouble()
                for (droppedFrame in droppedFrames) {
                    jankFrames += droppedFrame
                }
                lostFrameRate = jankFrames.toDouble() / 60
                break
            }
        }
    }

    companion object {
        private var mTime = 0L
        private var isGetSM = true
        private val count = AtomicInteger(0)
        private var sm = 0

        @JvmStatic
        fun main(args: Array<String>) {
            println(getCurrentSM())
        }

        @JvmStatic
        fun getCurrentSM(): Int {
            startSM()
            Log.d("BBTest", "sm=$sm")
            return sm
        }

        private val fc = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                Choreographer.getInstance().postFrameCallback(this)
                count.incrementAndGet()
            }
        }

        @JvmStatic
        fun startSM() {
            Choreographer.getInstance().postFrameCallback(fc)
            val thread = object : Thread() {
                override fun run() {
                    while (isGetSM) {
                        if (System.currentTimeMillis() - mTime >= 1000) {
                            mTime = System.currentTimeMillis()
                            sm = count.getAndSet(0)
                        }
                    }
                }
            }
            thread.start()
        }

        @JvmStatic
        fun stopSM() {
            isGetSM = false
            Choreographer.getInstance().removeFrameCallback(fc)
        }
    }
}
