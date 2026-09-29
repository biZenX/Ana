package com.wafeer.app.presentation.ui.screenshot

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import com.wafeer.app.presentation.ui.theme.component.date.HistoryDateDivider
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Locale

class HistoryDateDividerScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 10.0,
    )

    @Test
    fun historyDateDividerExpanded() {
        Locale.setDefault(Locale.US)
        paparazzi.snapshot {
            WafeerTheme {
                HistoryDateDivider(
                    date = LocalDate.of(2026, 1, 15),
                    isExpanded = true,
                    onToggleClick = {},
                    totalAmount = BigDecimal("125.75"),
                    currencyCode = "\$"
                )
            }
        }
    }
}