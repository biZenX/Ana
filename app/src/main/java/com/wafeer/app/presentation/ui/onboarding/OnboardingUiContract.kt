package com.wafeer.app.presentation.ui.onboarding

data class OnboardingUiState(
    val isCompleted: Boolean = false,
)

enum class SurveyBudgetType {
    HOUSEHOLD,
    SALARY,
    ALLOWANCE,
}

enum class SurveyRole {
    HEAD_OF_HOUSEHOLD,
    HOMEMAKER,
    INDIVIDUAL,
}

sealed interface OnboardingUiIntent {
    data object OnWelcomeDismissed : OnboardingUiIntent
    data class OnSurveyCompleted(
        val budgetType: SurveyBudgetType,
        val role: SurveyRole,
        val selectedCategoryTitles: List<String>,
    ) : OnboardingUiIntent
}

sealed interface OnboardingUiEffect {
    data object OnboardingCompleted : OnboardingUiEffect

    data class OnboardingFailed(val message: String) : OnboardingUiEffect
}
