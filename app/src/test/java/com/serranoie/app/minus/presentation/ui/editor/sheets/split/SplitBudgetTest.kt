package com.serranoie.app.minus.presentation.ui.editor.sheets.split

import com.google.common.truth.Truth.assertThat
import com.serranoie.app.minus.domain.model.BudgetPeriod
import org.junit.Test
import java.math.BigDecimal

class SplitBudgetTest {

    private val totalBudget = BigDecimal("16644.45")

    @Test
    fun `daily on a 30 day period divides the budget equally`() {
        // 16644.45 / 30 = 554.815 -> half-up = 554.82
        val result = staticBlockBudget(totalBudget, totalDays = 30, daysRemaining = 30, period = BudgetPeriod.DAILY)

        assertThat(result).isEqualTo(BigDecimal("554.82"))
    }

    @Test
    fun `weekly on a 30 day period is seven days of the daily rate`() {
        // 16644.45 * 7 / 30 = 3883.705 -> 3883.71
        val result = staticBlockBudget(totalBudget, totalDays = 30, daysRemaining = 30, period = BudgetPeriod.WEEKLY)

        assertThat(result).isEqualTo(BigDecimal("3883.71"))
    }

    @Test
    fun `biweekly on a 30 day period is fourteen days of the daily rate`() {
        // 16644.45 * 14 / 30 = 7767.41
        val result = staticBlockBudget(totalBudget, totalDays = 30, daysRemaining = 30, period = BudgetPeriod.BIWEEKLY)

        assertThat(result).isEqualTo(BigDecimal("7767.41"))
    }

    @Test
    fun `weekly in the partial last week is only the days that are left`() {
        // 30 day period, day 29 -> last block is days 28..29 (2 days) -> 16644.45 * 2 / 30 = 1109.63
        val result = staticBlockBudget(totalBudget, totalDays = 30, daysRemaining = 1, period = BudgetPeriod.WEEKLY)

        assertThat(result).isEqualTo(BigDecimal("1109.63"))
    }

    @Test
    fun `monthly on a 30 day period returns the full budget`() {
        val result = staticBlockBudget(totalBudget, totalDays = 30, daysRemaining = 30, period = BudgetPeriod.MONTHLY)

        assertThat(result).isEqualTo(BigDecimal("16644.45"))
    }

    @Test
    fun `returns zero when total budget is zero`() {
        val result = staticBlockBudget(BigDecimal.ZERO, totalDays = 30, daysRemaining = 30, period = BudgetPeriod.DAILY)

        assertThat(result).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `returns zero when total days is non-positive`() {
        val result = staticBlockBudget(BigDecimal("1000"), totalDays = 0, daysRemaining = 0, period = BudgetPeriod.DAILY)

        assertThat(result).isEqualTo(BigDecimal.ZERO)
    }
}
