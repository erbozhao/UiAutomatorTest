package com.bbtest.perform.base

import android.util.Log
import android.view.Choreographer
import java.util.concurrent.atomic.AtomicInteger

class FpsInfoBak {
    private val i = AtomicInteger(0)
    private var pause = false

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (isWarningFrameSkip()) {
                warningFrameSkip(frameTimeNanos)
            } else {
                recordFps()
            }
        }

        private fun recordFps() {
            if (!pause) {
                Choreographer.getInstance().postFrameCallback(this)
            }
            i.incrementAndGet()
        }

        private fun warningFrameSkip(frameTimeNanos: Long) {
            if (pause) {
                return
            }

            val eTime = System.nanoTime() - mLastFrameTime
            if (eTime > frameSkipTimeout && eTime < 1000000000L) {
                Log.e("QQDriverMonitor", "！！！！掉帧！！！etime = ${eTime / 1000000L}")
            }

            mLastFrameTime = frameTimeNanos
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    private val runnable = Runnable {
        while (!pause) {
            m_sm = i.getAndSet(0)
            try {
                Thread.sleep(1000L)
            } catch (_: InterruptedException) {
            }
        }
    }

    fun CallinOnCreate() {
        if (m_t == null) {
            pause = false
            Choreographer.getInstance().postFrameCallback(frameCallback)
            m_t = Thread(runnable)
            m_t?.start()
            m_t?.name = "SM calculator"
        }
    }

    fun CallinOnPause() {
        pause = true
    }

    companion object {
        private var INS: FpsInfoBak? = null
        private var m_t: Thread? = null
        private var m_sm = 0
        private var m_fps = 0
        private var m_soLoaded = false
        private var mLastFrameTime = -1L
        private var mWarningFrameSkip = false
        private var frameSkipTimeout = 100000000

        @JvmStatic
        fun isWarningFrameSkip(): Boolean = mWarningFrameSkip

        @JvmStatic
        fun setWarningFrameSkip(value: Boolean) {
            mWarningFrameSkip = value
        }

        @JvmStatic
        fun getCurrentSM(): Int = m_sm

        @JvmStatic
        fun getCurrentFPS(): Int = m_fps

        @JvmStatic
        fun getIns(): FpsInfoBak {
            if (INS == null) {
                INS = FpsInfoBak()
            }
            return requireNotNull(INS)
        }

        private fun getFPSIfPossible(): Int {
            var fps = 0
            if (m_soLoaded) {
                try {
                    fps = getFPS()
                } catch (_: Throwable) {
                }
            }
            return fps
        }

        @JvmStatic
        fun setSOLoaded(loaded: Boolean) {
            m_soLoaded = loaded
        }

        @JvmStatic
        external fun getFPS(): Int
    }
}
