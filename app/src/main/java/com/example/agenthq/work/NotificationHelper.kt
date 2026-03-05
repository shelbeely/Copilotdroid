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
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_LIVE_UPDATE,
                "Agent Live Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Live progress updates for active Copilot agent sessions"
            }
        )
    }

    // -------------------------------------------------------------------------
    // Live Update Notifications
    // Implements the Android Live Updates pattern:
    // https://developer.android.com/develop/ui/views/notifications/live-update
    // -------------------------------------------------------------------------

    /**
     * Posts or refreshes an ongoing "live update" notification for an active agent session.
     * The notification shows an indeterminate progress bar and an elapsed-time chronometer,
     * both of which update automatically while the session is in progress.
     *
     * @param sessionId    Unique session identifier (used as the notification ID seed).
     * @param prTitle      Title of the pull request the agent is working on.
     * @param repoFullName "owner/repo" string shown in the notification title.
     * @param startedAtMs  Epoch milliseconds when the session began (drives the chronometer).
     */
    fun postLiveAgentUpdate(
        sessionId: Long,
        prTitle: String,
        repoFullName: String,
        startedAtMs: Long
    ) {
        val pendingIntent = sessionPendingIntent(sessionId)
        val notification = NotificationCompat.Builder(context, CHANNEL_LIVE_UPDATE)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("Copilot working · $repoFullName")
            .setContentText(prTitle)
            // Keep the notification pinned while the agent is active
            .setOngoing(true)
            // Fire heads-up only the first time; silently refresh on subsequent updates
            .setOnlyAlertOnce(true)
            // Show an indeterminate progress bar (we don't know % complete)
            .setProgress(0, 0, true)
            // Display elapsed time from session start instead of the notification post time
            .setUsesChronometer(true)
            .setWhen(startedAtMs)
            .setShowWhen(true)
            .setContentIntent(pendingIntent)
            .build()
        manager.notify(liveNotificationId(sessionId), notification)
    }

    /**
     * Cancels the live update notification for a session that is no longer active.
     * Call this before [postSessionCompleted] so the ongoing bar is replaced cleanly.
     */
    fun cancelLiveAgentUpdate(sessionId: Long) {
        manager.cancel(liveNotificationId(sessionId))
    }

    /**
     * Posts a one-shot summary notification when an agent session finishes.
     * The notification is auto-dismissible and tapping it opens the session detail screen.
     *
     * @param sessionId    Unique session identifier.
     * @param prTitle      Title of the pull request.
     * @param repoFullName "owner/repo" string.
     * @param succeeded    `true` for completed/merged, `false` for failed/cancelled.
     */
    fun postSessionCompleted(
        sessionId: Long,
        prTitle: String,
        repoFullName: String,
        succeeded: Boolean
    ) {
        val title = if (succeeded) {
            "✅ Copilot finished · $repoFullName"
        } else {
            "❌ Copilot stopped · $repoFullName"
        }
        val icon = if (succeeded) {
            android.R.drawable.ic_dialog_info
        } else {
            android.R.drawable.ic_dialog_alert
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ACTIVITY)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(prTitle)
            .setAutoCancel(true)
            .setContentIntent(sessionPendingIntent(sessionId))
            .build()
        manager.notify(liveNotificationId(sessionId), notification)
    }

    // -------------------------------------------------------------------------
    // Legacy helpers (kept for backward compatibility)
    // -------------------------------------------------------------------------

    fun notifyAgentActivity(prTitle: String, repoName: String, sessionId: Long) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ACTIVITY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Agent activity in $repoName")
            .setContentText(prTitle)
            .setAutoCancel(true)
            .setContentIntent(sessionPendingIntent(sessionId))
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

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun sessionPendingIntent(sessionId: Long): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_SESSION_ID, sessionId)
        }
        return PendingIntent.getActivity(
            context,
            sessionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val CHANNEL_ACTIVITY = "agent_activity"
        const val CHANNEL_LIVE_UPDATE = "agent_live_update"
        const val EXTRA_SESSION_ID = "session_id"

        private const val NOTIFICATION_STEERING_ID = 2001

        /** Offset ensures live-update IDs never collide with legacy notification IDs. */
        private const val LIVE_NOTIFICATION_BASE_ID = 3000

        /**
         * Computes the notification ID for a live-update session notification.
         * The session ID is reduced modulo `(Int.MAX_VALUE - LIVE_NOTIFICATION_BASE_ID)`
         * before adding the offset so the result always fits in the positive [Int] range.
         */
        fun liveNotificationId(sessionId: Long): Int {
            val maxPart = Int.MAX_VALUE - LIVE_NOTIFICATION_BASE_ID
            return LIVE_NOTIFICATION_BASE_ID + ((sessionId and Long.MAX_VALUE) % maxPart).toInt()
        }
    }
}
