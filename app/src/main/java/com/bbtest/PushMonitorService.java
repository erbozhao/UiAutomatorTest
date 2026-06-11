package com.bbtest;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * @Author: onuszhao
 * @Date: 2021-11-23 11:51
 * @Description:
 */
public class PushMonitorService extends NotificationListenerService {

    /**
     * 当连接成功时调用，一般在开启监听后会回调一次该方法
     */
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    /**
     * 当收到一条消息时回调，sbn里面带有这条消息的具体信息
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // 获取通知消息的包名
        String pkg = sbn.getPackageName();
        if (pkg != null && (pkg.equals("com.bbtest"))) {
            // 获取通知消息的时间
            String time = String.valueOf(sbn.getPostTime());
            Bundle extras = sbn.getNotification().extras;
            String title = "";
            // 获取通知消息的title
            if ((extras.getString(Notification.EXTRA_TITLE)) != null) {
                title = extras.getString(Notification.EXTRA_TITLE);
            }
            String text = "";
            // 获取通知消息的内容
            if (extras.getCharSequence(Notification.EXTRA_TEXT) != null) {
                text = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
            }
            Log.i("BBTest", "title=" + title + ",text=" + text);
            if (title.equals("BBTest") && text.equals("Clear all notifications!")) {
                Log.i("BBTest", "清理所有通知!");
                cancelAllNotifications();
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // 当移除一条消息的时候回调，sbn是被移除的消息
        super.onNotificationRemoved(sbn);
    }

}
