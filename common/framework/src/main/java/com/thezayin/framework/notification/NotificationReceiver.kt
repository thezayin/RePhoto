package com.thezayin.framework.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.thezayin.framework.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val name = intent.getStringExtra("name") ?: "Friend"
        val day = intent.getIntExtra("day", 1)
        val month = intent.getIntExtra("month", 1)

        val notification = NotificationCompat.Builder(context, NotificationUtil.CHANNEL_ID)
            .setSmallIcon(com.google.firebase.inappmessaging.display.R.drawable.image_placeholder) // Ensure you have this drawable
            .setContentTitle("Birthday Reminder")
            .setContentText("$name's birthday is today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify((System.currentTimeMillis() % 10000).toInt(), notification)
        }
    }
}