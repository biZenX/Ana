package com.serranoie.app.minus.presentation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.app.minus.data.repository.BudgetRepository
import com.serranoie.app.minus.data.repository.SettingsRepository
import com.serranoie.app.minus.domain.model.BudgetSettings
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
