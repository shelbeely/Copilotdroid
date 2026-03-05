package com.example.agenthq.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.agenthq.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ACTIVITY, "Agent Activity", NotificationManager.IMPORTANCE_HIGH)
        )
        manager.createNotificationChannel(
            NotificationChannel(SyncWorker.CHANNEL_ID, "Agent Sync", NotificationManager.IMPORTANCE_LOW)
        )
    }

    fun notifyAgentActivity(prTitle: String, repoName: String, sessionId: Long) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_SESSION_ID, sessionId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            sessionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ACTIVITY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Agent activity in $repoName")
            .setContentText(prTitle)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        manager.notify(sessionId.toInt(), notification)
    }

    fun notifyNewSteeringReply(prTitle: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ACTIVITY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Agent HQ – New Reply")
            .setContentText("Agent responded to your comment on: $prTitle")
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICATION_STEERING_ID, notification)
    }

    fun notifySyncComplete() {
        val notification = NotificationCompat.Builder(context, SyncWorker.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("Agent HQ")
            .setContentText("Agent sessions synced")
            .setAutoCancel(true)
            .build()
        manager.notify(SyncWorker.NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ACTIVITY = "agent_activity"
        const val EXTRA_SESSION_ID = "session_id"
        private const val NOTIFICATION_STEERING_ID = 2001
    }
}
