package com.wafeer.app.data.updater

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class AppUpdateCheckWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = Result.success()

    companion object {
        fun schedule(context: Context) {
            // No-op for Google Play builds: app updates are managed automatically by Google Play Store
        }
    }
}
