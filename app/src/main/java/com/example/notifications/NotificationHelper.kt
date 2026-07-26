package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.annotation.SuppressLint
import androidx.core.app.NotificationCompat
import com.example.MainActivity

object NotificationHelper {
    const val CHANNEL_BOOKINGS = "bookings"
    const val CHANNEL_MESSAGES = "messages"
    const val CHANNEL_PROMOTIONS = "promotions"
    const val CHANNEL_SYSTEM = "system"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel(CHANNEL_BOOKINGS, "Réservations", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Notifications de réservation"
                },
                NotificationChannel(CHANNEL_MESSAGES, "Messages", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Nouveaux messages"
                },
                NotificationChannel(CHANNEL_PROMOTIONS, "Promotions", NotificationManager.IMPORTANCE_LOW).apply {
                    description = "Offres et promotions"
                },
                NotificationChannel(CHANNEL_SYSTEM, "Système", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Notifications système"
                }
            )

            manager.createNotificationChannels(channels)
        }
    }

    @SuppressLint("NotificationPermission")
    fun showNotification(context: Context, channel: String, title: String, message: String, notificationId: Int = System.currentTimeMillis().toInt()) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return
        manager.notify(notificationId, notification)
    }

    fun showMockBookingNotification(context: Context) {
        showNotification(context, CHANNEL_BOOKINGS, "Nouvelle réservation", "Votre appartement a été réservé pour le 15 mars")
    }

    fun showMockMessageNotification(context: Context) {
        showNotification(context, CHANNEL_MESSAGES, "Nouveau message", "Marie vous a envoyé un message")
    }

    fun showMockPromoNotification(context: Context) {
        showNotification(context, CHANNEL_PROMOTIONS, "Offre spéciale", "-20% sur les studios ce mois-ci !")
    }

    fun showMockSystemNotification(context: Context) {
        showNotification(context, CHANNEL_SYSTEM, "Mise à jour", "LocAll a été mis à jour avec succès")
    }
}
