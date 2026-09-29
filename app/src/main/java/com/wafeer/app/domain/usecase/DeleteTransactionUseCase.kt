package com.wafeer.app.domain.usecase

import com.wafeer.app.data.repository.BudgetRepository
import com.wafeer.app.domain.model.Transaction
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        budgetRepository.deleteTransaction(transaction)
    }
}
