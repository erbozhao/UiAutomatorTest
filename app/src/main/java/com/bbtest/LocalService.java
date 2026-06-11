package com.bbtest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.util.Random;

public class LocalService extends Service {

    // Used as a key for the Intent.
    public static final String SEED_KEY = "SEED_KEY";

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    // Random number generator
    private Random mGenerator = new Random();

    private long mSeed;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 渠道id(channelId必须要一致，否者服务会被杀死)
            String channelId = "BBTest";

            // 获取通知管理器
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // 创建渠道
            CharSequence name = "BBTest";               // 渠道名称
            String description = "Just for test!";      // 渠道描述
            int importance = NotificationManager.IMPORTANCE_DEFAULT;    // 重要性级别(此处为默认)
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            mChannel.setDescription(description);       // 渠道描述
            mChannel.enableLights(true);                // 是否显示通知指示灯
            mChannel.enableVibration(true);             // 是否振动
            mNotificationManager.createNotificationChannel(mChannel);

            /**
             * 创建通知：
             * 1.以前台方式运行，避免被杀
             * 2.id设置为0时，隐藏不显示通知，服务也会被杀死
             */
            int id = 92601;
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher) //小图标
                    .setContentTitle("BBTest")
                    .setContentText("Just for test!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//            mNotificationManager.notify(id, mBuilder.build());  // 发起通知
            startForeground(id, mBuilder.build());              // 前台方式运行
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // If the Intent comes with a seed for the number generator, apply it.
        if (intent.hasExtra(SEED_KEY)) {
            mSeed = intent.getLongExtra(SEED_KEY, 0);
            mGenerator.setSeed(mSeed);
        }
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public LocalService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return LocalService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    /**
     * Returns a random integer in [0, 100).
     */
    public int getRandomInt() {
        return mGenerator.nextInt(100);
    }
}
