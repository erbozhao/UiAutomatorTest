package com.bbtest.perform.caton;

import android.os.Looper;
import android.util.Printer;

/**
 * 利用UI执行绪的Looper列印的日志匹配：
 * 1.Android使用讯息机制进行UI更新，UI执行绪有个Looper，在其loop方法中会不断取出message，呼叫其系结的Handler在UI执行绪执行，如果在handler的dispatchMesaage方法里有耗时操作，就会发生卡顿
 * 2.如果设定了logging，会分别打印出”>>>>> Dispatching to “ 和”<<<<< Finished to “ 这样的日志，这样我们就可以通过两次log的时间差值，来计算dispatchMessage的执行时间，从而设定阈值判断是否发生了卡顿
 * 3.Looper的mLogging是私有的，并且提供了setMessageLogging(@Nullable Printer printer) 方法，所以我们可以自己实现一个Printer，在通过setMessageLogging()方法传入即可
 */
public class BlockDetectByPrinter {
    public static void start() {
        Looper.getMainLooper().setMessageLogging(new Printer() {
            private static final String START = ">>>>> Dispatching";
            private static final String END = "<<<<< Finished";

            @Override
            public void println(String x) {
                if (x.startsWith(START)) {
                    LogMonitor.getInstance().startMonitor();
                }
                if (x.startsWith(END)) {
                    LogMonitor.getInstance().removeMonitor();
                }
            }
        });
    }
}
