package com.bbtest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.Random

class LocalService : Service() {
    private val binder: IBinder = LocalBinder()
    private val generator = Random()
    private var seed = 0L

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "BBTest"
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name: CharSequence = "BBTest"
            val description = "Just for test!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.enableLights(true)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)

            val id = 92601
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("BBTest")
                .setContentText("Just for test!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            startForeground(id, builder.build())
        }
    }

    override fun onBind(intent: Intent): IBinder {
        if (intent.hasExtra(SEED_KEY)) {
            seed = intent.getLongExtra(SEED_KEY, 0)
            generator.setSeed(seed)
        }
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    fun getRandomInt(): Int = generator.nextInt(100)

    inner class LocalBinder : Binder() {
        fun getService(): LocalService = this@LocalService
    }

    companion object {
        const val SEED_KEY = "SEED_KEY"
    }
}
