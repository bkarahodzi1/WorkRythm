package com.example.worktimechecker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

class AlarmReceiver: BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "channelId"
        const val NOTIFICATION_ID = 1
    }
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: "No message"

        // Create a notification channel (required for Android 8.0+)
        createNotificationChannel(context)

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your app's icon
            .setContentTitle("Break reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel(context: Context) {
        val name = "Alarm Notifications"
        val descriptionText = "Notifications for alarms"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}