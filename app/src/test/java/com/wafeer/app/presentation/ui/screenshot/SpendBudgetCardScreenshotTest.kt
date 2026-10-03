package com.wafeer.app.presentation.ui.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import com.wafeer.app.presentation.ui.theme.component.budget.SpendBudgetCard
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.util.Locale

class SpendBudgetCardScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 10.0,
    )

    @Test
    fun spendBudgetCard() {
        Locale.setDefault(Locale.US)
        paparazzi.snapshot {
            WafeerTheme {
                Box(modifier = Modifier.padding(16.dp)) {
                    SpendBudgetCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        budget = BigDecimal("60000"),
                        spend = BigDecimal("30740")
                    )
                }
            }
        }
    }
}