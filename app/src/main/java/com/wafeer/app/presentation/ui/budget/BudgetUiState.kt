package com.wafeer.app.presentation.ui.budget

import com.wafeer.app.domain.model.BudgetPeriod
import com.wafeer.app.domain.model.BudgetSettings
import com.wafeer.app.domain.model.BudgetState
import com.wafeer.app.domain.model.LeftoverChoice
import com.wafeer.app.domain.model.Transaction
import com.wafeer.app.presentation.ui.editor.AnimState
import com.wafeer.app.presentation.ui.editor.EditMode
import java.math.BigDecimal
import java.time.LocalDate

data class BudgetUiState(
    val isLoading: Boolean = false,
    val budgetSettings: BudgetSettings? = null,
    val budgetState: BudgetState? = null,
    val transactions: List<Transaction> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val error: String? = null,
    val numpadInput: String = "",
    val isNumpadValid: Boolean = false,
    val editMode: EditMode = EditMode.ADD,
    val animState: AnimState = AnimState.IDLE,
    val currentComment: String = "",
    val currentNote: String = "",
    val tags: List<String> = emptyList(),
    val isFirstLaunch: Boolean = true,
    val isRecurrentEnabled: Boolean = false,
    val isCreditEnabled: Boolean = false,
    val showRecurrentDialog: Boolean = false,
    val showCreditCutoffDialog: Boolean = false,
    val pendingRecurrentAmount: BigDecimal? = null,
    val pendingRecurrentComment: String = "",
    val currentPeriodStartedAtMillis: Long = 0L,
    val currentPeriodId: Long = 0L,
    val isCalculation: Boolean = false,
    val dragProgress: Float = 0f,
    val lockSwipeable: Boolean = false,
    val lockDraggable: Boolean = false,
    val pendingExpensesForNextPeriod: List<Transaction> = emptyList(),
    val creditOwed: BigDecimal = BigDecimal.ZERO,
    val debtAdjustedBalance: BigDecimal = BigDecimal.ZERO,
    val calculationPreview: String? = null,
    val numpadDraftAmount: BigDecimal? = null,
    val hasUnresolvedRolloverSurplus: Boolean = false,
    val unresolvedSurplusAmount: BigDecimal? = null,
    val lastLeftoverChoice: LeftoverChoice? = null,
    /**
     * The period the user picked for the budget pill's view mode, as persisted in settings. Null
     * until they choose one, in which case the budget's own [BudgetSettings.period] stands in.
     * Kept here so a view-mode change alone still re-emits this state and refreshes the widgets.
     */
    val selectedViewPeriod: BudgetPeriod? = null,
    val allowanceDaysEnabled: Boolean = false,
    val activeSpendingDays: Set<Int> = setOf(7, 1, 2, 3, 4),
    val budgetAlertThresholdPercent: Int = 80,
    val financialTipsEnabled: Boolean = true,
    val dismissedFinancialTipIds: Set<Int> = emptySet(),
    val isWalletIncomeEnabled: Boolean = false,
) {
    companion object {
        val INITIAL = BudgetUiState()
    }
}
