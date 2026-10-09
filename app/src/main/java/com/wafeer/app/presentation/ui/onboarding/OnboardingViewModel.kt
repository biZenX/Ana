package com.wafeer.app.presentation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafeer.app.data.repository.BudgetRepository
import com.wafeer.app.data.repository.SettingsRepository
import com.wafeer.app.domain.model.BudgetSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat
import java.util.Locale
import javax.inject.Inject

private const val TAG = "Onboarding"

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val budgetRepository: BudgetRepository,
) : ViewModel() {

    private val _localState = MutableStateFlow(OnboardingLocalState())

    private val _effects = MutableSharedFlow<OnboardingUiEffect>()
    val effects: SharedFlow<OnboardingUiEffect> = _effects.asSharedFlow()

    val uiState: StateFlow<OnboardingUiState> = _localState.map { local ->
        OnboardingUiState(isCompleted = local.isCompleted)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = OnboardingUiState()
    )

    fun processIntent(intent: OnboardingUiIntent) {
        logcat(TAG) { "processIntent: $intent (state before: isCompleted=${_localState.value.isCompleted})" }
        when (intent) {
            is OnboardingUiIntent.OnWelcomeDismissed -> handleWelcomeDismissed()
            is OnboardingUiIntent.OnSurveyCompleted -> handleSurveyCompleted(intent)
        }
    }

    private fun handleSurveyCompleted(intent: OnboardingUiIntent.OnSurveyCompleted) {
        logcat(TAG) { "handleSurveyCompleted: budgetType=${intent.budgetType}, role=${intent.role}, categories=${intent.selectedCategoryTitles.size}" }
        viewModelScope.launch {
            try {
                for (catTitle in intent.selectedCategoryTitles) {
                    if (catTitle.isNotBlank()) {
                        budgetRepository.findOrCreateCategory(catTitle)
                    }
                }

                val existing = budgetRepository.getBudgetSettingsSync()
                val (period, splitMode, allowanceEnabled) = when (intent.budgetType) {
                    SurveyBudgetType.ALLOWANCE -> Triple(
                        com.wafeer.app.domain.model.BudgetPeriod.DAILY,
                        com.wafeer.app.domain.model.BudgetSplitMode.DYNAMIC,
                        true
                    )
                    SurveyBudgetType.SALARY -> Triple(
                        com.wafeer.app.domain.model.BudgetPeriod.MONTHLY,
                        com.wafeer.app.domain.model.BudgetSplitMode.STATIC,
                        false
                    )
                    SurveyBudgetType.HOUSEHOLD -> Triple(
                        com.wafeer.app.domain.model.BudgetPeriod.MONTHLY,
                        com.wafeer.app.domain.model.BudgetSplitMode.DYNAMIC,
                        false
                    )
                }

                val sampleBudget = when (intent.budgetType) {
                    SurveyBudgetType.ALLOWANCE -> java.math.BigDecimal("1200.00")
                    SurveyBudgetType.SALARY, SurveyBudgetType.HOUSEHOLD -> java.math.BigDecimal("5000.00")
                }

                val currentTotal = existing?.totalBudget ?: sampleBudget
                val currency = existing?.currencyCode ?: "EGP"

                val updatedSettings = (existing ?: com.wafeer.app.domain.model.BudgetSettings(
                    totalBudget = sampleBudget,
                    period = period,
                    startDate = java.time.LocalDate.now().withDayOfMonth(1),
                    currencyCode = currency,
                )).copy(
                    totalBudget = sampleBudget,
                    period = period,
                    splitMode = splitMode,
                )

                budgetRepository.saveBudgetSettings(updatedSettings)
                if (allowanceEnabled) {
                    settingsRepository.setAllowanceDaysEnabled(true)
                }

                // Seed interactive simulation mock transactions
                budgetRepository.seedDemoData(currencyCode = currency, sampleBudget = sampleBudget)
                settingsRepository.setDemoModeActive(true)
                settingsRepository.setDemoMissionCompleted(false)

                settingsRepository.setOnboardingCompleted(true)
                _localState.update { it.copy(isCompleted = true) }
                _effects.emit(OnboardingUiEffect.OnboardingCompleted)
            } catch (e: Exception) {
                logcat(TAG) { "handleSurveyCompleted failed: ${e.message}" }
                _effects.emit(OnboardingUiEffect.OnboardingFailed(e.message ?: "Unknown error"))
            }
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            try {
                settingsRepository.setLanguage(language)
                val appLocale: androidx.core.os.LocaleListCompat = if (language == "system") {
                    androidx.core.os.LocaleListCompat.getEmptyLocaleList()
                } else {
                    androidx.core.os.LocaleListCompat.forLanguageTags(language)
                }
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)

                val isAr = language.startsWith("ar") || (language == "system" && Locale.getDefault().language == "ar")
                if (isAr) {
                    val currentSettings = budgetRepository.getBudgetSettingsSync()
                    if (currentSettings != null && currentSettings.currencyCode == "USD") {
                        budgetRepository.saveBudgetSettings(currentSettings.copy(currencyCode = "EGP"))
                    }
                }
            } catch (e: Exception) {
                logcat(TAG) { "setLanguage failed: ${e.message}" }
            }
        }
    }

    private fun handleWelcomeDismissed() {
        logcat(TAG) { "handleWelcomeDismissed: setting onboarding_completed=true" }
        viewModelScope.launch {
            try {
                settingsRepository.setOnboardingCompleted(true)
                _localState.update { it.copy(isCompleted = true) }
                logcat(TAG) { "handleWelcomeDismissed: emitted OnboardingCompleted" }
                _effects.emit(OnboardingUiEffect.OnboardingCompleted)
            } catch (e: Exception) {
                logcat(TAG) { "handleWelcomeDismissed failed: ${e.message}" }
                _effects.emit(OnboardingUiEffect.OnboardingFailed(e.message ?: "Unknown error"))
            }
        }
    }
}

private data class OnboardingLocalState(
    val isCompleted: Boolean = false,
)
