package com.serranoie.app.minus.presentation.ui.editor.sheets.split

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.math.BigDecimal

class DynamicAllocationsTest {

    private val totalBudget = BigDecimal("20326.00")
    private val totalSpent = BigDecimal("14839.27")

    @Test
    fun `spec example - 6 days remaining gives 914_46 daily`() {
        // 5486.73 / 6 = 914.455 exactly. With HALF_UP rounding to 2 dp the
        // 3rd-decimal 5 rounds up to 914.46. (The spec's "914.45" is an
        // approximate example; we follow the codebase's HALF_UP convention
        // used by the existing SplitBudgetTest for consistency.)
        val alloc = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 6,
        )

        assertThat(alloc.dailyAllocation).isEqualTo(BigDecimal("914.46"))
    }

    @Test
    fun `spec example - 6 days remaining gives 5486_73 for weekly final block`() {
        // 6 < 7 -> blocks = 1 -> allocation = 5486.73 / 1 = 5486.73
        val alloc = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 6,
        )

        assertThat(alloc.weeklyAllocation).isEqualTo(BigDecimal("5486.73"))
    }

    @Test
    fun `spec example - 6 days remaining gives 5486_73 for monthly final block`() {
        // 6 < 30 -> blocks = 1 -> allocation = 5486.73 / 1 = 5486.73
        val alloc = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 6,
        )

        assertThat(alloc.monthlyAllocation).isEqualTo(BigDecimal("5486.73"))
    }

    @Test
    fun `14 days remaining - weekly is half the remaining balance`() {
        // 5486.73 / ceil(14/7)=2 = 2743.365 -> 2743.37
        val alloc = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 14,
        )
        assertThat(alloc.weeklyAllocation).isEqualTo(BigDecimal("2743.37"))
    }

    @Test
    fun `14 days remaining - biweekly is the full remaining balance`() {
        // 14 / 14 = 1 -> blocks = 1 -> 5486.73 / 1 = 5486.73
        val alloc = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 14,
        )
        assertThat(alloc.biweeklyAllocation).isEqualTo(BigDecimal("5486.73"))
    }

    @Test
    fun `15 days remaining - weekly is seven days of the daily rate`() {
        // 5486.73 * 7 / 15 = 2560.474 -> 2560.47
        val alloc = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 15,
        )
        assertThat(alloc.weeklyAllocation).isEqualTo(BigDecimal("2560.47"))
    }

    @Test
    fun `7 days remaining - weekly block is the full balance`() {
        // 7 / 7 = 1 -> blocks = 1 -> 5486.73
        val alloc = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 7,
        )
        assertThat(alloc.weeklyAllocation).isEqualTo(BigDecimal("5486.73"))
    }

    @Test
    fun `daily allocation decreases as days remaining decreases`() {
        // The whole point of dynamic mode: more time = smaller daily cap.
        val a = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 30,
        ).dailyAllocation
        val b = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 6,
        ).dailyAllocation
        assertThat(b).isGreaterThan(a)
    }

    @Test
    fun `period fully spent - all allocations are zero and over flag is true`() {
        val alloc = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalBudget, // exactly at limit
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 6,
        )
        assertThat(alloc.dailyAllocation).isEqualTo(BigDecimal.ZERO)
        assertThat(alloc.weeklyAllocation).isEqualTo(BigDecimal.ZERO)
        assertThat(alloc.biweeklyAllocation).isEqualTo(BigDecimal.ZERO)
        assertThat(alloc.monthlyAllocation).isEqualTo(BigDecimal.ZERO)
        assertThat(alloc.isTodayOverDailyAllocation).isTrue()
    }

    @Test
    fun `overspent - all allocations are zero and over flag is true`() {
        val alloc = computeDynamicAllocations(
            totalBudget = BigDecimal("1000"),
            totalSpentInPeriod = BigDecimal("1500"),
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 6,
        )
        assertThat(alloc.dailyAllocation).isEqualTo(BigDecimal.ZERO)
        assertThat(alloc.isTodayOverDailyAllocation).isTrue()
    }

    @Test
    fun `zero total budget - everything is zero, over flag follows today spend`() {
        val alloc = computeDynamicAllocations(
            totalBudget = BigDecimal.ZERO,
            totalSpentInPeriod = BigDecimal.ZERO,
            totalSpentToday = BigDecimal("50"),
            daysRemaining = 6,
        )
        assertThat(alloc.dailyAllocation).isEqualTo(BigDecimal.ZERO)
        assertThat(alloc.isTodayOverDailyAllocation).isTrue()
    }

    @Test
    fun `zero days remaining - everything is zero, over flag follows today spend`() {
        val alloc = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal("1.00"),
            daysRemaining = 0,
        )
        assertThat(alloc.dailyAllocation).isEqualTo(BigDecimal.ZERO)
        assertThat(alloc.isTodayOverDailyAllocation).isTrue()
    }

    @Test
    fun `today's spend exceeds daily allocation - over flag is true`() {
        // 1000 budget, 500 spent (today), 10 days remaining
        //   pool at start of today = 1000, daily = 1000/10 = 100.00
        //   totalSpentToday = 500 > 100.00 -> over
        val alloc = computeDynamicAllocations(
            totalBudget = BigDecimal("1000"),
            totalSpentInPeriod = BigDecimal("500"),
            totalSpentToday = BigDecimal("500"),
            daysRemaining = 10,
        )
        assertThat(alloc.dailyAllocation).isEqualTo(BigDecimal("100.00"))
        assertThat(alloc.isTodayOverDailyAllocation).isTrue()
    }

    @Test
    fun `today's spend is within daily allocation - over flag is false`() {
        // 1000 budget, 50 spent (today), 10 days remaining
        //   pool at start of today = 1000, daily = 100.00
        //   totalSpentToday = 50 < 100.00 -> not over
        val alloc = computeDynamicAllocations(
            totalBudget = BigDecimal("1000"),
            totalSpentInPeriod = BigDecimal("50"),
            totalSpentToday = BigDecimal("50"),
            daysRemaining = 10,
        )
        assertThat(alloc.dailyAllocation).isEqualTo(BigDecimal("100.00"))
        assertThat(alloc.isTodayOverDailyAllocation).isFalse()
    }

    @Test
    fun `spending exactly today's allocation is not over and keeps tomorrow's allocation unchanged`() {
        val before = computeDynamicAllocations(
            totalBudget = BigDecimal("1000"),
            totalSpentInPeriod = BigDecimal.ZERO,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 10,
        )
        val after = computeDynamicAllocations(
            totalBudget = BigDecimal("1000"),
            totalSpentInPeriod = before.dailyAllocation,
            totalSpentToday = before.dailyAllocation,
            daysRemaining = 10,
        )
        assertThat(after.dailyAllocation).isEqualTo(before.dailyAllocation)
        assertThat(after.isTodayOverDailyAllocation).isFalse()
        assertThat(
            computeNextBlockAllocations(
                totalBudget = BigDecimal("1000"),
                totalSpentInPeriod = before.dailyAllocation,
                totalDays = 10,
                daysRemaining = 10,
            ).dailyAllocation
        ).isEqualTo(before.dailyAllocation)
    }

    @Test
    fun `mid week - the weekly allocation is fixed from the start of the week`() {
        // 30 day period, 21 left -> 9 days elapsed, week 2 started on day 7, 23 days from its start
        // pool at week start = 1000 - 100 + 100 = 1000 -> 1000 * 7 / 23 = 304.35
        val alloc = computeDynamicAllocations(
            totalBudget = BigDecimal("1000"),
            totalSpentInPeriod = BigDecimal("100"),
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 21,
            totalDays = 30,
            totalSpentThisWeek = BigDecimal("100"),
        )
        assertThat(alloc.weeklyAllocation).isEqualTo(BigDecimal("304.35"))
    }

    @Test
    fun `weekly allocation adds back this week's spend`() {
        val alloc = computeDynamicAllocations(
            totalBudget = BigDecimal("1400"),
            totalSpentInPeriod = BigDecimal("300"),
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 14,
            totalSpentThisWeek = BigDecimal("300"),
        )
        assertThat(alloc.weeklyAllocation).isEqualTo(BigDecimal("700.00"))
    }

    @Test
    fun `no spend today - over flag is false`() {
        val alloc = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 6,
        )
        assertThat(alloc.isTodayOverDailyAllocation).isFalse()
    }

    @Test
    fun `forPeriod returns the matching allocation`() {
        val alloc = computeDynamicAllocations(
            totalBudget = totalBudget,
            totalSpentInPeriod = totalSpent,
            totalSpentToday = BigDecimal.ZERO,
            daysRemaining = 6,
        )
        assertThat(alloc.forPeriod(com.serranoie.app.minus.domain.model.BudgetPeriod.DAILY))
            .isEqualTo(alloc.dailyAllocation)
        assertThat(alloc.forPeriod(com.serranoie.app.minus.domain.model.BudgetPeriod.WEEKLY))
            .isEqualTo(alloc.weeklyAllocation)
        assertThat(alloc.forPeriod(com.serranoie.app.minus.domain.model.BudgetPeriod.BIWEEKLY))
            .isEqualTo(alloc.biweeklyAllocation)
        assertThat(alloc.forPeriod(com.serranoie.app.minus.domain.model.BudgetPeriod.MONTHLY))
            .isEqualTo(alloc.monthlyAllocation)
    }
}
