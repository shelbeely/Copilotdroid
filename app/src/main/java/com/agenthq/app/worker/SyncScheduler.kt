package com.agenthq.app.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val PERIODIC_SYNC_WORK = "periodic_sync"
        const val ONE_TIME_SYNC_WORK = "one_time_sync"
    }

    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun schedulePeriodicSync() {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(networkConstraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun enqueueOneTimeSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(networkConstraints)
            .addTag(ONE_TIME_SYNC_WORK)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    fun cancelAllSync() {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_SYNC_WORK)
        WorkManager.getInstance(context).cancelAllWorkByTag(ONE_TIME_SYNC_WORK)
    }
}
