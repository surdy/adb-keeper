package com.surdy.adbkeeper

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Cheap periodic nudge in case the framework clears the flags while running (e.g. after a
 * deep-sleep teardown). 15 min is the WorkManager minimum period. No network, no wakelock.
 */
class KeepWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        AdbEnabler.enable(applicationContext, "worker")
        return Result.success()
    }

    companion object {
        private const val NAME = "adb-keeper-periodic"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<KeepWorker>(15, TimeUnit.MINUTES)
                .setConstraints(Constraints.NONE)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
