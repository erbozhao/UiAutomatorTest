package com.bbtest.perform.caton;


import android.view.Choreographer;

import java.util.concurrent.TimeUnit;

public class BlockDetectByChoreographer {
    public static void start2() {
        Choreographer.getInstance().postFrameCallback(new FPSFrameCallback(System.nanoTime()));
        BlockDetectByPrinter.start();
    }

    public static void start() {
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            long lastFrameTimeNanos = 0;
            long currentFrameTimeNanos = 0;

            @Override
            public void doFrame(long frameTimeNanos) {
                if (lastFrameTimeNanos == 0) {
                    lastFrameTimeNanos = frameTimeNanos;
                }
                currentFrameTimeNanos = frameTimeNanos;
                long diffMs = TimeUnit.MILLISECONDS.convert(currentFrameTimeNanos - lastFrameTimeNanos, TimeUnit.NANOSECONDS);
                if (diffMs > 16.6f) {
                    long droppedCount = (long) (diffMs / 16.6);
                }
                if (LogMonitor.getInstance().isMonitor()) {
                    LogMonitor.getInstance().removeMonitor();
                }
                LogMonitor.getInstance().startMonitor();
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }
}
