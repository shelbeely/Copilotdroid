package com.example.agenthq.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.agenthq.auth.TokenStore
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
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!tokenStore.hasToken()) return Result.success() // not logged in, skip
        return try {
            syncSessionsUseCase()
            notificationHelper.notifySyncComplete()
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
