package com.bbtest

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class PushMonitorService : NotificationListenerService() {
    override fun onListenerConnected() {
        super.onListenerConnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg = sbn.packageName
        if (pkg != null && pkg == "com.bbtest") {
            val time = sbn.postTime.toString()
            val extras: Bundle = sbn.notification.extras
            var title = ""
            if (extras.getString(Notification.EXTRA_TITLE) != null) {
                title = extras.getString(Notification.EXTRA_TITLE).toString()
            }
            var text = ""
            if (extras.getCharSequence(Notification.EXTRA_TEXT) != null) {
                text = extras.getCharSequence(Notification.EXTRA_TEXT).toString()
            }
            Log.i("BBTest", "title=$title,text=$text")
            if (title == "BBTest" && text == "Clear all notifications!") {
                Log.i("BBTest", "清理所有通知! time=$time")
                cancelAllNotifications()
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
    }
}
