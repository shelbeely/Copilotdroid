package com.agenthq.app.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.agenthq.app.data.local.dao.PullRequestDao
import com.agenthq.app.data.local.entities.CachedPullRequest
import com.agenthq.app.data.repository.GitHubRepository
import com.agenthq.app.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: GitHubRepository,
    private val pullRequestDao: PullRequestDao,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "SyncWorker"
        private const val NOTIFICATION_ID_BASE = 2000
    }

    override suspend fun doWork(): Result {
        return try {
            val oldSessions = pullRequestDao.getCopilotSessionsSnapshot()
                .associateBy { it.id }

            repository.syncRepositories()
            repository.syncAllPullRequests()

            val newSessions = pullRequestDao.getCopilotSessionsSnapshot()
            checkForStatusChanges(oldSessions, newSessions)

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            Result.failure()
        }
    }

    private fun checkForStatusChanges(
        oldSessions: Map<Long, CachedPullRequest>,
        newSessions: List<CachedPullRequest>
    ) {
        var notificationIndex = 0
        for (session in newSessions) {
            val old = oldSessions[session.id]
            if (old == null) {
                notificationHelper.showSessionUpdateNotification(
                    title = "New Copilot Session",
                    message = "${session.repoFullName}#${session.number}: ${session.title}",
                    notificationId = NOTIFICATION_ID_BASE + notificationIndex++
                )
            } else if (old.state != session.state) {
                val statusLabel = session.state.replaceFirstChar { it.uppercase() }
                notificationHelper.showSessionUpdateNotification(
                    title = "Session $statusLabel",
                    message = "${session.repoFullName}#${session.number}: ${session.title}",
                    notificationId = NOTIFICATION_ID_BASE + notificationIndex++
                )
            }
        }
    }
}
