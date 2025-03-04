package com.example.myandroidapp.services.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.myandroidapp.MainActivity
import com.example.myandroidapp.R

fun createNotificationChannel(channelId: String, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "MyChannel"
        val descriptionText = "My important channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun showSimpleNotification(
    context: Context,
    channelId: String,
    textTitle: String,
    textContent: String,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED)
        return

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_clapperboard)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(priority)
        .build()

    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}

// shows a simple notification with a tap action to show an activity
fun showSimpleNotificationWithTapAction(
    context: Context,
    channelId: String,
    textTitle: String,
    textContent: String,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED)
        return

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_clapperboard)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(priority)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}