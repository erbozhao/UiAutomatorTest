package com.bbtest.perform.base;

import android.util.Log;
import android.view.Choreographer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SmInfo {

    private static long mTime = 0;

    private static boolean isGetSM = true;
    private static AtomicInteger count = new AtomicInteger(0);
    private static int sm = 0;


    // 计算一帧的时间，Android手机屏幕是60Hz的刷新频率，就是16.67ms
    private long mFrameIntervalNanos = (long) (1000000000 / 60.0);
    private final List<Long> droppedFrames = new ArrayList<>();

    private long totalFrames = 0;       // 统计的总帧数
    private long jankFrames = 0;        // jank次数，跳帧数
    private int extraJankTimes = 0;     // 额外的垂直同步次数
    private double fps;                  // fps值
    private double lostFrameRate = 0;    // 丢帧率

    public static void main(String[] args) {
        System.out.println(getCurrentSM());
    }

    public static int getCurrentSM() {
        startSM();
        Log.d("BBTest", "sm=" + sm);
        return sm;
    }

    static Choreographer.FrameCallback fc = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            Choreographer.getInstance().postFrameCallback(this);
            count.incrementAndGet();
        }
    };

    public static void startSM() {
        Choreographer.getInstance().postFrameCallback(fc);
        Thread thread = new Thread() {
            public void run() {
                while (isGetSM) {
                    if (System.currentTimeMillis() - mTime >= 1000) {
                        mTime = System.currentTimeMillis();
                        sm = count.getAndSet(0);
                    }
                }
            }
        };
        thread.start();
    }

    public static void stopSM() {
        isGetSM = false;
        Choreographer.getInstance().removeFrameCallback(fc);
    }


    Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        long mLastFrameTimeNanos = 0;
        long mCurrentFrameTimeNanos = 0;

        @Override
        public void doFrame(long frameTimeNanos) {
            if (mLastFrameTimeNanos == 0) {
                mLastFrameTimeNanos = frameTimeNanos;
            }
            // 开始执行doFrame的时间
            mCurrentFrameTimeNanos = System.nanoTime();
            // 接收VSYNC任务和实际开始执行的时间差
            final long jitterNanos = mCurrentFrameTimeNanos - mLastFrameTimeNanos;
            // 时间差大于16ms，则出现掉帧
            if (jitterNanos > mFrameIntervalNanos) {
                // 计算掉帧数
                droppedFrames.add(jitterNanos / mFrameIntervalNanos);
            }
            mLastFrameTimeNanos = frameTimeNanos;
            count.incrementAndGet();
            // 注册下一帧的回调
            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    public void startSM2() {
        Choreographer.getInstance().postFrameCallback(frameCallback);
        Thread thread = new Thread() {
            public void run() {
                long startTime = System.currentTimeMillis();
                long endTime = 0;
                while (true) {
                    endTime = System.currentTimeMillis();
                    long costTime = endTime - startTime;
                    if (costTime >= 1000) {
                        fps = count.getAndSet(0);
                        for (long droppedFrame : droppedFrames) {
                            jankFrames += droppedFrame;
                        }
//                fps = (double) totalFrames / (totalFrames + extraJankTimes) * 60;
                        lostFrameRate = (double) jankFrames / 60;
                        break;
                    }
                }
            }
        };
        thread.start();
    }

    private void getFpsByChoreographer() {
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        final AtomicInteger count = new AtomicInteger(0);
        final List<Long> droppedFrames = new ArrayList<>();

        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            long mLastFrameTimeNanos = 0;
            long mCurrentFrameTimeNanos = 0;
            // 计算一帧的时间，Android手机屏幕是60Hz的刷新频率，就是16.67ms
            long mFrameIntervalNanos = (long) (1000000000 / 60.0);

            @Override
            public void doFrame(long frameTimeNanos) {
                if (mLastFrameTimeNanos == 0) {
                    mLastFrameTimeNanos = frameTimeNanos;
                }
                // 开始执行doFrame的时间
                mCurrentFrameTimeNanos = System.nanoTime();
                // 接收VSYNC任务和实际开始执行的时间差
                final long jitterNanos = mCurrentFrameTimeNanos - mLastFrameTimeNanos;
                // 时间差大于16ms，则出现掉帧
                if (jitterNanos > mFrameIntervalNanos) {
                    // 计算掉帧数
                    droppedFrames.add(jitterNanos / mFrameIntervalNanos);
//                    Log.d(TAG, "The jitterNanos is " + jitterNanos * 0.000001f + "ms");
                }
                mLastFrameTimeNanos = frameTimeNanos;
//                mLastStartNanos = mCurrentFrameTimeNanos;
                count.incrementAndGet();
                // 注册下一帧的回调
                Choreographer.getInstance().postFrameCallback(this);

//                if (mLastFrameTimeNanos == 0) {
//                    mLastFrameTimeNanos = frameTimeNanos;
//                }
//                mCurrentFrameTimeNanos = frameTimeNanos;
//                long diffMs = TimeUnit.MILLISECONDS.convert(mCurrentFrameTimeNanos - mLastFrameTimeNanos, TimeUnit.NANOSECONDS);
//                if (diffMs > 16.6f) {
//                    long droppedCount = (long) (diffMs / 16.6);
//                }
//                if (LogMonitor.getInstance().isMonitor()) {
//                    LogMonitor.getInstance().removeMonitor();
//                }
//                LogMonitor.getInstance().startMonitor();
//                mLastFrameTimeNanos = frameTimeNanos;
//                Choreographer.getInstance().postFrameCallback(this);
//                count.incrementAndGet();
            }
        });

        while (true) {
            endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;
            if (costTime > 1000) {
                fps = count.getAndSet(0);
                for (long droppedFrame : droppedFrames) {
                    jankFrames += droppedFrame;
                }
//                fps = (double) totalFrames / (totalFrames + extraJankTimes) * 60;
                lostFrameRate = (double) jankFrames / 60;
                break;
            }
        }
    }

}
