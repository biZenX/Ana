package com.wafeer.app.presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wafeer.app.data.repository.BudgetRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat

class NotificationRescheduleReceiver : BroadcastReceiver() {
    companion object {
        val ALLOWED_SYSTEM_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
        )
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NotificationRescheduleReceiverEntryPoint {
        fun budgetRepository(): BudgetRepository
        fun notificationScheduler(): NotificationScheduler
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action !in ALLOWED_SYSTEM_ACTIONS) {
            logcat { "Ignoring unauthorized or unhandled broadcast action: $action" }
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                logcat { "Rescheduling notifications after system event: $action" }
                com.wafeer.app.presentation.notification.scan.ExpenseNotificationListener.requestRebindIfGranted(context.applicationContext)
                val entryPoint = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    NotificationRescheduleReceiverEntryPoint::class.java
                )
                val scheduler = entryPoint.notificationScheduler()
                scheduler.initializeNotifications()
            } catch (e: Exception) {
                logcat { "Error rescheduling notifications\n${e.asLog()}" }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
