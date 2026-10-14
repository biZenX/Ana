package com.wafeer.app.domain.usecase

import com.wafeer.app.data.repository.BudgetRepository
import com.wafeer.app.domain.model.Transaction
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    /** Returns the id the inserted row was given. */
    suspend operator fun invoke(transaction: Transaction): Long =
        budgetRepository.addTransaction(transaction)
}
