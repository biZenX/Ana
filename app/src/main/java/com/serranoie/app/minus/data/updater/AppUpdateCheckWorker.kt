package com.serranoie.app.minus.data.updater

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.serranoie.app.minus.presentation.notification.NotificationHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import logcat.logcat
import java.util.concurrent.TimeUnit

class AppUpdateCheckWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UpdateWorkerEntryPoint {
        fun appUpdateManager(): AppUpdateManager
        fun notificationHelper(): NotificationHelper
    }

    override suspend fun doWork(): Result {
        logcat(TAG) { "AppUpdateCheckWorker checking for new version in background..." }

        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            UpdateWorkerEntryPoint::class.java,
        )
        val appUpdateManager = entryPoint.appUpdateManager()
        val notificationHelper = entryPoint.notificationHelper()

        return try {
            appUpdateManager.checkForUpdates().fold(
                onSuccess = { info ->
                    if (info != null) {
                        logcat(TAG) { "New update detected in background: v${info.versionName} (${info.versionCode})" }
                        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        val lastNotifiedVersion = prefs.getInt(KEY_LAST_NOTIFIED_VERSION, 0)

                        if (info.versionCode > lastNotifiedVersion) {
                            notificationHelper.showUpdateNotification(info)
                            prefs.edit().putInt(KEY_LAST_NOTIFIED_VERSION, info.versionCode).apply()
                        }
                    } else {
                        logcat(TAG) { "App is currently up to date" }
                    }
                    Result.success()
                },
                onFailure = { error ->
                    logcat(TAG) { "Background update check failed: ${error.message}" }
                    Result.retry()
                },
            )
        } catch (e: Exception) {
            logcat(TAG) { "Exception during update check: ${e.message}" }
            Result.success()
        }
    }

    companion object {
        private const val TAG = "AppUpdateCheckWorker"
        private const val WORK_NAME = "wafeer_periodic_update_check"
        private const val PREFS_NAME = "wafeer_update_check_prefs"
        private const val KEY_LAST_NOTIFIED_VERSION = "last_notified_update_version_code"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<AppUpdateCheckWorker>(
                12, TimeUnit.HOURS,
                2, TimeUnit.HOURS,
            )
                .setConstraints(constraints)
                .addTag(WORK_NAME)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest,
            )
            logcat(TAG) { "Scheduled periodic update check worker every 12 hours" }
        }
    }
}
