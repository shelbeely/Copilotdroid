package com.example.agenthq.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.agenthq.auth.TokenStore
import com.example.agenthq.data.local.AgentSessionDao
import com.example.agenthq.data.local.PullRequestDao
import com.example.agenthq.domain.usecase.SyncSessionsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncSessionsUseCase: SyncSessionsUseCase,
    private val tokenStore: TokenStore,
    private val notificationHelper: NotificationHelper,
    private val agentSessionDao: AgentSessionDao,
    private val pullRequestDao: PullRequestDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!tokenStore.hasToken()) return Result.success() // not logged in, skip

        // Snapshot session statuses BEFORE sync to detect transitions
        val statusBefore: Map<Long, String> = agentSessionDao.getAllOnce()
            .associate { it.id to it.status }

        return try {
            syncSessionsUseCase()

            // After sync, get updated sessions and drive live update notifications
            val sessionsAfter = agentSessionDao.getAllOnce()
            for (session in sessionsAfter) {
                val pr = pullRequestDao.getByNumber(
                    session.repoOwner, session.repoName, session.prNumber
                )
                val prTitle = pr?.title ?: "PR #${session.prNumber}"
                val repoFullName = "${session.repoOwner}/${session.repoName}"
                val previousStatus = statusBefore[session.id]

                when (session.status) {
                    "active" -> notificationHelper.postLiveAgentUpdate(
                        sessionId = session.id,
                        prTitle = prTitle,
                        repoFullName = repoFullName,
                        startedAtMs = session.inferredAt
                    )
                    "completed" -> {
                        // Replace the live progress bar with a one-shot "finished" notification
                        notificationHelper.cancelLiveAgentUpdate(session.id)
                        if (previousStatus == "active" || previousStatus == "paused") {
                            notificationHelper.postSessionCompleted(
                                sessionId = session.id,
                                prTitle = prTitle,
                                repoFullName = repoFullName,
                                succeeded = true
                            )
                        }
                    }
                    "failed" -> {
                        notificationHelper.cancelLiveAgentUpdate(session.id)
                        if (previousStatus == "active" || previousStatus == "paused") {
                            notificationHelper.postSessionCompleted(
                                sessionId = session.id,
                                prTitle = prTitle,
                                repoFullName = repoFullName,
                                succeeded = false
                            )
                        }
                    }
                    // "paused" or any future/unknown status: cancel the live bar
                    // but do not post a completion notice
                    else -> notificationHelper.cancelLiveAgentUpdate(session.id)
                }
            }

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val CHANNEL_ID = "agent_sync"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "AgentHQSync"

        fun schedulePeriodic(workManager: WorkManager) {
            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build()
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
