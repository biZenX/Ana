package com.wafeer.app.presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wafeer.app.data.repository.BudgetRepository
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
                val todayTransactions = budgetRepository.getTransactionsForPeriod(today, today).first()
                val hasTransactionToday = todayTransactions.any {
                    !it.isDeleted && !it.isRecurrent
                }
                if (spent <= BigDecimal.ZERO && !hasTransactionToday) {
                    logcat("DailyFinancialCheckReceiver") { "Zero spend today ($spent) -> Showing 75% day rich alert" }
                    notificationHelper.showDaily75PercentAlert()
                } else {
                    logcat("DailyFinancialCheckReceiver") { "User recorded spend today ($spent, hasTx=$hasTransactionToday) -> skipping reminder" }
                    notificationHelper.cancelDaily75PercentAlert()
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
