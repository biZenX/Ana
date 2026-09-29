package com.wafeer.app.domain.usecase

import com.wafeer.app.presentation.ui.budget.BudgetPeriodManager
import javax.inject.Inject

class FinishBudgetEarlyUseCase @Inject constructor(
    private val periodManager: BudgetPeriodManager,
) {
    suspend operator fun invoke() {
        periodManager.finishBudgetEarly()
    }
}
