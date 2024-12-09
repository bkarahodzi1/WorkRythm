package com.example.worktimechecker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class NotificationApp: Application() {

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "channelId",
            "Channel name",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}