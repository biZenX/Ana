package com.wafeer.app.presentation.ui.budget.mvi.intent

import com.wafeer.app.domain.model.Transaction
import com.wafeer.app.presentation.ui.budget.mvi.BudgetUiIntent

sealed interface BudgetTransactionIntent : BudgetUiIntent {
    data class DeleteTransactionTapped(val transaction: Transaction) : BudgetTransactionIntent
    data class RestoreTransactionTapped(val transaction: Transaction) : BudgetTransactionIntent
    data class EditTransactionTapped(val updatedTransaction: Transaction) : BudgetTransactionIntent
}
