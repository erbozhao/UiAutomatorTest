package com.bbtest.perform.caton;

import android.util.Log;
import android.view.Choreographer;

/**
 * Android系统每隔16ms发出VSYNC讯号，来通知介面进行重绘、渲染，每一次同步的周期为16.6ms，代表一帧的重新整理频率
 * SDK中包含了一个相关类，以及相关回调。理论上来说两次回拨的时间周期应该在16ms，如果超过了16ms我们则认为发生了卡顿，利用两次回拨间的时间周期来判断是否发生卡顿（这个方案是Android 4.1 API 16以上才支持）
 * 方案:通过Choreographer类设定它的FrameCallback函式，当每一帧被渲染时会触发回拨FrameCallback， FrameCallback回拨void doFrame (long frameTimeNanos)函式，一次介面渲染会回拨doFrame方法，如果两次doFrame之间的间隔大于16.6ms说明发生了卡顿
 * 注:每隔16.67ms在异步线程获取一下主线程的堆栈然后保存起来，在卡顿发生时，把这些周期性采集的堆栈当做卡顿时的堆栈
 */
public class FPSFrameCallback implements Choreographer.FrameCallback {
    private static final String TAG = "FPS_TEST";
    private long mLastFrameTimeNanos = 0;
    private long mFrameIntervalNanos;

    public FPSFrameCallback(long lastFrameTimeNanos) {
        mLastFrameTimeNanos = lastFrameTimeNanos;
        // 1s 60 幀
        mFrameIntervalNanos = (long) (1000000000 / 60.0);
    }

    @Override
    public void doFrame(long frameTimeNanos) {

        //初始化時間
        if (mLastFrameTimeNanos == 0) {
            mLastFrameTimeNanos = frameTimeNanos;
        }
        final long jitterNanos = frameTimeNanos - mLastFrameTimeNanos;
        if (jitterNanos >= mFrameIntervalNanos) {
            final long skippedFrames = jitterNanos / mFrameIntervalNanos;
            if (skippedFrames > 30) {
                Log.i(TAG, "Skipped " + skippedFrames + " frames!  " + "The application may be doing too much work on its main thread.");
            }
        }
        mLastFrameTimeNanos = frameTimeNanos;
        //註冊下一幀回撥
        Choreographer.getInstance().postFrameCallback(this);
    }
}
