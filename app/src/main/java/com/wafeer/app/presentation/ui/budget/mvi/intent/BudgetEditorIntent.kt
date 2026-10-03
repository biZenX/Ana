package com.wafeer.app.presentation.ui.budget.mvi.intent

import com.wafeer.app.domain.model.BudgetSettings
import com.wafeer.app.domain.model.RecurrentFrequency
import com.wafeer.app.presentation.ui.budget.mvi.BudgetUiIntent
import com.wafeer.app.presentation.ui.editor.AnimState
import com.wafeer.app.presentation.ui.editor.EditMode
import java.time.LocalDate

sealed interface BudgetEditorIntent : BudgetUiIntent {
    data class DateSelected(val date: LocalDate) : BudgetEditorIntent
    data class UpdateSettings(val settings: BudgetSettings) : BudgetEditorIntent
    data class SetEditMode(val mode: EditMode) : BudgetEditorIntent
    data class SetAnimState(val state: AnimState) : BudgetEditorIntent
    data class CommentUpdated(val comment: String) : BudgetEditorIntent
    data class NoteUpdated(val note: String) : BudgetEditorIntent
    data class DeleteTag(val tag: String) : BudgetEditorIntent
    data class CreateCategory(val name: String) : BudgetEditorIntent
    data class SetRecurrentEnabled(val enabled: Boolean) : BudgetEditorIntent
    data class SetCreditEnabled(val enabled: Boolean) : BudgetEditorIntent
    data object DismissRecurrentDialog : BudgetEditorIntent
    data object DismissCreditCutoffDialog : BudgetEditorIntent
    data class RecurrentExpenseApplied(
        val frequency: RecurrentFrequency,
        val endDate: LocalDate,
        val subscriptionDay: Int? = null,
        val fallbackComment: String,
    ) : BudgetEditorIntent
    data class CreditCutoffDayConfirmed(val cutoffDay: Int) : BudgetEditorIntent
    data object FinishBudgetEarly : BudgetEditorIntent
    data class UpdateAllowanceDays(val enabled: Boolean, val days: Set<Int>) : BudgetEditorIntent
    data class SetWalletIncomeEnabled(val enabled: Boolean) : BudgetEditorIntent
    data class DismissFinancialTip(val tipId: Int) : BudgetEditorIntent
}
