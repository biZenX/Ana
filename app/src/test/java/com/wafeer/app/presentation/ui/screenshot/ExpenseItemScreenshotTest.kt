package com.wafeer.app.presentation.ui.screenshot

import androidx.compose.material3.ExperimentalMaterial3Api
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.wafeer.app.domain.model.Transaction
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import com.wafeer.app.presentation.ui.theme.component.PaddedListItemPosition
import com.wafeer.app.presentation.ui.theme.component.expense.ExpenseItem
import com.wafeer.app.presentation.util.font.format.symbolOnlyCurrencyFormat
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Locale

class ExpenseItemScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 10.0,
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun expenseItemFirst() {
        Locale.setDefault(Locale.US)
        paparazzi.snapshot {
            WafeerTheme {
                ExpenseItem(
                    transaction = sampleTransaction(),
                    currencyFormat = symbolOnlyCurrencyFormat("USD"),
                    position = PaddedListItemPosition.First,
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun expenseItemLast() {
        Locale.setDefault(Locale.US)
        paparazzi.snapshot {
            WafeerTheme {
                ExpenseItem(
                    transaction = sampleTransaction(),
                    currencyFormat = symbolOnlyCurrencyFormat("USD"),
                    position = PaddedListItemPosition.Last,
                )
            }
        }
    }

    private fun sampleTransaction(): Transaction = Transaction(
        id = 1L,
        amount = BigDecimal("42.50"),
        comment = "Lunch with team",
        date = LocalDateTime.of(2026, 1, 15, 12, 30),
        periodId = 7L,
    )
}
