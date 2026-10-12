package com.example.util

import android.Manifest
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
import com.example.MainActivity
import com.example.R

object CurioNotificationHelper {

    const val CHANNEL_ID = "curio_researches_channel"
    private const val PREFS_NAME = "curio_notifications_prefs"
    private const val KEY_WELCOME_SENT = "key_welcome_notif_sent"
    private const val KEY_SEEN_RESEARCH_IDS = "key_seen_research_ids"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Sends an automatic welcoming notification to new readers when they launch Curio
     * or start exploring for the first time.
     */
    fun checkAndSendWelcomeNotification(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val alreadySent = prefs.getBoolean(KEY_WELCOME_SENT, false)
        if (alreadySent) return

        if (!hasNotificationPermission(context)) return

        initNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_curio)
            .setContentTitle("Welcome to Curio!")
            .setContentText("Explore verified deep-dive researches written in clear everyday English.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Welcome to Curio! Explore verified deep-dive research stories written like news in clear everyday English. Read freely as a guest or sign in anytime.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(101, notification)
            prefs.edit().putBoolean(KEY_WELCOME_SENT, true).apply()
        } catch (_: SecurityException) {
            // Permission check caught above
        }
    }

    /**
     * Sends an automatic notification when a new Curio research post is published or discovered.
     */
    fun notifyNewPost(context: Context, postId: String, title: String, overview: String, category: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val seenIds = prefs.getStringSet(KEY_SEEN_RESEARCH_IDS, emptySet())?.toMutableSet() ?: mutableSetOf()

        if (seenIds.contains(postId)) {
            return
        }

        // Add to seen so we don't duplicate notify
        seenIds.add(postId)
        prefs.edit().putStringSet(KEY_SEEN_RESEARCH_IDS, seenIds).apply()

        // If this is the initial cold launch batch, don't spam notifications for all starter posts,
        // unless it's explicitly identified as a fresh publication.
        if (seenIds.size <= 2) {
            // First run, register as seen quietly so only genuinely new items trigger alerts
            return
        }

        if (!hasNotificationPermission(context)) return

        initNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("selected_story_id", postId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            postId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_curio)
            .setContentTitle("New Curio Research: $category")
            .setContentText(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("New Research: $title")
                    .bigText(if (overview.isNotBlank()) overview else "Tap to read the latest breakdown on Curio.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(postId.hashCode(), notification)
        } catch (_: SecurityException) {
            // Handled
        }
    }
}
