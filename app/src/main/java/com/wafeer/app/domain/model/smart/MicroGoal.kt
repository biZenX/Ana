package com.wafeer.app.domain.model.smart

import java.math.BigDecimal

data class MicroGoal(
    val id: Long = 0,
    val title: String,
    val targetAmount: BigDecimal,
    val savedAmount: BigDecimal = BigDecimal.ZERO,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
) {
    val remainingAmount: BigDecimal
        get() = (targetAmount - savedAmount).coerceAtLeast(BigDecimal.ZERO)

    val progressRatio: Float
        get() = if (targetAmount > BigDecimal.ZERO) {
            (savedAmount.toFloat() / targetAmount.toFloat()).coerceIn(0f, 1f)
        } else 1f

    fun daysToTarget(dailySavingRate: BigDecimal): Int {
        if (dailySavingRate <= BigDecimal.ZERO || remainingAmount <= BigDecimal.ZERO) return 0
        return (remainingAmount.toDouble() / dailySavingRate.toDouble()).toInt().coerceAtLeast(1)
    }
}
