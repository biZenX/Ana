package com.wafeer.app.data.repository

import com.wafeer.app.domain.model.AppColorScheme
import com.wafeer.app.domain.model.BudgetPeriod
import com.wafeer.app.domain.model.ContrastMode
import com.wafeer.app.domain.model.FirstLaunchTutorialStage
import com.wafeer.app.domain.model.LeftoverChoice
import com.wafeer.app.domain.model.PeriodMappingMode
import com.wafeer.app.domain.model.RemainingBudgetStrategy
import com.wafeer.app.domain.model.SavingsPreferences
import com.wafeer.app.domain.model.ThemeMode
import com.wafeer.app.domain.model.TypographyMode
import com.wafeer.app.domain.model.UserSettings
import com.wafeer.app.presentation.ui.history.RecurrentPaymentsViewMode
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDate

interface SettingsRepository {

    fun observeSettings(): Flow<UserSettings>

    fun observeCurrentPeriodRollover(): Flow<Pair<BigDecimal, Boolean>>

    fun observeCurrentPeriodBoundary(): Flow<Pair<Long, Long>>

    suspend fun getCurrentPeriodId(): Long

    suspend fun getSettings(): UserSettings

    suspend fun setOnboardingCompleted(completed: Boolean)

    suspend fun setEarlyFinishActive(active: Boolean, actualDate: Long, originalEndDate: Long)

    suspend fun setPeriodEndAlreadyHandled(handled: Boolean)

    suspend fun setCurrentPeriod(periodId: Long, startedAt: Long)

    suspend fun setCurrentPeriodRollover(amount: BigDecimal, carryForward: Boolean)

    suspend fun setPendingRollover(amount: BigDecimal, strategy: RemainingBudgetStrategy)

    suspend fun clearPendingRollover()

    suspend fun setNotificationTime(hour: Int, minute: Int)

    suspend fun setRecurrentNotificationTime(hour: Int, minute: Int)

    suspend fun setThemeMode(mode: ThemeMode)

    suspend fun setTypographyMode(mode: TypographyMode)

    suspend fun setContrastMode(mode: ContrastMode)

    suspend fun setAppColorScheme(colorScheme: AppColorScheme)

    suspend fun setLanguage(language: String)

    suspend fun setDynamicColorEnabled(enabled: Boolean)

    suspend fun setRoundedFontEnabled(enabled: Boolean)

    suspend fun setAmoledEnabled(enabled: Boolean)

    suspend fun setCreditQuickToggleEnabled(enabled: Boolean)

    suspend fun setShowPastTransactions(enabled: Boolean)

    suspend fun setCategoryPickerDirectPopupEnabled(enabled: Boolean)

    suspend fun setCategoryGridModeEnabled(enabled: Boolean)

    suspend fun setExtraNoteEnabled(enabled: Boolean)

    suspend fun setReserveUpcomingChargesEnabled(enabled: Boolean)

    suspend fun setNewCategoryTagEnabled(enabled: Boolean)

    suspend fun setTutorialBoxCompleted(completed: Boolean)

    suspend fun setAnalyticsTutorialCompleted(completed: Boolean)

    suspend fun setAnalyticsSpendsTutorialCompleted(completed: Boolean)

    suspend fun setPeriodMappingMode(mode: PeriodMappingMode)

    suspend fun setFirstLaunchTutorialStage(stage: FirstLaunchTutorialStage)

    suspend fun setRecurrentPaymentsViewMode(mode: RecurrentPaymentsViewMode)

    suspend fun setBudgetSplitViewPeriod(period: BudgetPeriod)

    fun observeBudgetEndDate(): Flow<Long?>

    suspend fun setBudgetEndDate(millis: Long?)

    fun observeMidnightTransitionOccurred(): Flow<Boolean>

    suspend fun setMidnightTransitionOccurred(occurred: Boolean)

    suspend fun persistLastPeriodSnapshot(periodEndDateMillis: Long, remainingAmount: BigDecimal)

    suspend fun getLastPeriodEnd(): Long?

    suspend fun getRemainingFromLastPeriod(): BigDecimal

    suspend fun getPendingRollover(): Pair<BigDecimal, RemainingBudgetStrategy?>

    fun observePendingRollover(): Flow<Pair<BigDecimal, RemainingBudgetStrategy?>>

    suspend fun markSurplusUnresolved(amount: BigDecimal)

    suspend fun setLeftoverChoice(date: LocalDate, choice: LeftoverChoice, keepFrom: LocalDate)

    suspend fun setSavingsPreferences(prefs: SavingsPreferences)

    suspend fun clearEarlyFinish()

    suspend fun resetTutorials()

    suspend fun getString(key: String): String?

    suspend fun setString(key: String, value: String)

    suspend fun getLastSeenVersionCode(): Long?

    suspend fun setLastSeenVersionCode(code: Long)

    suspend fun resetLastSeenVersionCode()

    suspend fun clearLastPeriodSnapshot()

    suspend fun setAllowanceDaysEnabled(enabled: Boolean)

    suspend fun setActiveSpendingDays(days: Set<Int>)

    suspend fun setBudgetAlertThreshold(percent: Int)

    suspend fun setFinancialTipsEnabled(enabled: Boolean)

    suspend fun dismissFinancialTip(tipId: Int)

    suspend fun resetDismissedFinancialTips()

    suspend fun setDemoModeActive(active: Boolean)

    suspend fun setDemoMissionCompleted(completed: Boolean)
}
