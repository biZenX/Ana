package com.wafeer.app.domain.calculator

import com.wafeer.app.domain.model.BudgetPeriod
import com.wafeer.app.domain.model.BudgetSettings
import com.wafeer.app.domain.model.PeriodMappingMode
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class TransactionPeriodMapperTest {

    private val settings = BudgetSettings(
        totalBudget = BigDecimal("1000"),
        period = BudgetPeriod.WEEKLY,
        startDate = LocalDate.of(2026, 1, 5)
    )

    @Test
    fun mapDate_activeBudget_usesBudgetAlignedWeek() {
        val key = TransactionPeriodMapper.mapDate(
            date = LocalDate.of(2026, 1, 10),
            budgetSettings = settings,
            mappingMode = PeriodMappingMode.ACTIVE_BUDGET
        )

        assertEquals(LocalDate.of(2026, 1, 5), key.startDate)
        assertEquals(LocalDate.of(2026, 1, 11), key.endDate)
    }

    @Test
    fun mapDate_calendarBucket_usesCalendarWeekStartMonday() {
        val key = TransactionPeriodMapper.mapDate(
            date = LocalDate.of(2026, 1, 10),
            budgetSettings = settings,
            mappingMode = PeriodMappingMode.CALENDAR_BUCKET
        )

        assertEquals(LocalDate.of(2026, 1, 5), key.startDate)
        assertEquals(LocalDate.of(2026, 1, 11), key.endDate)
    }
}
