package com.wafeer.app.domain.usecase

import com.wafeer.app.domain.model.BudgetSettings
import com.wafeer.app.presentation.ui.budget.BudgetPeriodManager
import com.wafeer.app.presentation.ui.budget.PeriodBoundaryResult
import javax.inject.Inject

class PersistBudgetSettingsUseCase @Inject constructor(
    private val periodManager: BudgetPeriodManager,
) {
    suspend operator fun invoke(
        settings: BudgetSettings,
        forceNewPeriodBoundary: Boolean,
    ): PeriodBoundaryResult {
        return periodManager.persistBudgetSettings(
            settings = settings,
            forceNewPeriodBoundary = forceNewPeriodBoundary,
        )
    }
}
