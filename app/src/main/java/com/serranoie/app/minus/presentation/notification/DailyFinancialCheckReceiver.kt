package com.serranoie.app.minus.presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.serranoie.app.minus.data.repository.BudgetRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.logcat
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class DailyFinancialCheckReceiver : BroadcastReceiver() {

    @Inject
    lateinit var budgetRepository: BudgetRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        logcat("DailyFinancialCheckReceiver") { "onReceive called for 75% daily check" }
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val today = LocalDate.now()
                val spent = budgetRepository.getSpentForDate(today).first()
                if (spent <= BigDecimal.ZERO) {
                    logcat("DailyFinancialCheckReceiver") { "Zero spend today ($spent) -> Showing 75% day rich alert" }
                    notificationHelper.showDaily75PercentAlert()
                } else {
                    logcat("DailyFinancialCheckReceiver") { "User recorded spend today ($spent) -> skipping reminder" }
                }
                notificationScheduler.scheduleDaily75PercentCheck()
            } catch (e: Exception) {
                logcat("DailyFinancialCheckReceiver") { "Error in daily check: ${e.message}" }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
