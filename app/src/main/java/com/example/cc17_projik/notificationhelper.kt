package com.example.cc17_projik

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

/**
 * Handles the creation and display of Android notifications.
 */
object NotificationHelper {

    private const val CHANNEL_ID = "gadgetloop_notifications"
    private const val CHANNEL_NAME = "GadgetLoop Notifications"

    /**
     * Creates a notification channel for Android 8.0 and newer.
     */
    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about GadgetLoop activities"
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Displays a notification when a product is successfully posted.
     */
    fun showProductPostedNotification(
        context: Context,
        productTitle: String
    ) {

        // Check notification permission on Android 13 and newer.
        if (
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        createNotificationChannel(context)

        // Opens MarketActivity when the notification is tapped.
        val intent = Intent(context, MarketActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_home)
            .setContentTitle("Product Posted Successfully!")
            .setContentText("Your $productTitle has been listed on GadgetLoop.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Your $productTitle has been successfully listed on GadgetLoop.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}