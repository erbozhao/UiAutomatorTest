package com.bbtest.perform.base;

import android.util.Log;
import android.view.Choreographer;

import androidx.test.internal.util.ReflectionUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class FpsInfoBak {

    private static FpsInfoBak INS = null;

    private AtomicInteger i = new AtomicInteger(0);
    private boolean pause = false;
    private static Thread m_t = null;

    private static int m_sm = 0;
    private static int m_fps = 0;
    private static int m_tryFpsCount = 0;
    private static boolean m_soLoaded = false;

    private static long mLastFrameTime = -1L;
    private static boolean mWarningFrameSkip = false;
    private static int Frame_Skip_Timeout = 100000000;

    private static Object mUserActionStatManager;

    public static boolean isWarningFrameSkip() {
        return mWarningFrameSkip;
    }

    public static void setWarningFrameSkip(boolean val) {
        mWarningFrameSkip = val;
    }

    public static int getCurrentSM() {
        return m_sm;
    }

    public static int getCurrentFPS() {
        return m_fps;
    }

    Choreographer.FrameCallback c = new Choreographer.FrameCallback() {

        public void doFrame(long frameTimeNanos) {
            if (FpsInfoBak.isWarningFrameSkip()) {
                warningFrameSkip(frameTimeNanos);
            } else {
                recordFps(frameTimeNanos);
            }
        }

        private void recordFps(long frameTimeNanos) {
            if (!FpsInfoBak.this.pause) {
                Choreographer.getInstance().postFrameCallback(this);
            }
            FpsInfoBak.this.i.incrementAndGet();
        }

        private void warningFrameSkip(long frameTimeNanos) {
            if (FpsInfoBak.this.pause) {
                return;
            }

            long eTime = System.nanoTime() - FpsInfoBak.mLastFrameTime;

            if ((eTime > FpsInfoBak.Frame_Skip_Timeout) && (eTime < 1000000000L)) {
                Log.e("QQDriverMonitor",
                        "！！！！掉帧！！！etime = " + eTime / 1000000L);
                if (FpsInfoBak.mUserActionStatManager == null) {
//                    FpsInfoBak.mUserActionStatManager = ReflectionUtil.invokeStaticMethod("com.tencent.mtt.base.stat.UserActionStatManager", "getInstance", null);
                }
                if (FpsInfoBak.mUserActionStatManager != null) {
//                    Log.e("QQDriverMonitor", "user action " + ReflectionUtil.invokeMethod(FpsInfoBak.mUserActionStatManager, "generateUserAcList", new Object[]{Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(false)}));
                }
            }

            FpsInfoBak.mLastFrameTime = frameTimeNanos;
            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    Runnable r = new Runnable() {

        public void run() {

            while (!FpsInfoBak.this.pause) {

                FpsInfoBak.m_sm = FpsInfoBak.this.i.getAndSet(0);
//                FpsInfoBak.m_fps = FpsInfoBak.access$8();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException localInterruptedException) {
                }
            }
        }
    };


    public static FpsInfoBak getIns() {
        if (INS == null) {
            INS = new FpsInfoBak();
        }
        return INS;
    }


    public void CallinOnCreate() {
        if (m_t == null) {
            this.pause = false;

            Choreographer.getInstance().postFrameCallback(this.c);

            m_t = new Thread(this.r);
            m_t.start();
            m_t.setName("SM calculator");
        }
    }


    public void CallinOnPause() {
        this.pause = true;
    }

    private static int getFPSIfPossible() {
        int fps = 0;
        if (m_soLoaded) {
            try {
                fps = getFPS();
            } catch (Throwable localThrowable) {
            }
        }
        return fps;
    }

    public static void setSOLoaded(boolean loaded) {
        m_soLoaded = loaded;
    }

    public static native int getFPS();
}
