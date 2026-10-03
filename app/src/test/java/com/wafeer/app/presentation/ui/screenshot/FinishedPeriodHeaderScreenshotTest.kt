package com.wafeer.app.presentation.ui.screenshot

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import com.wafeer.app.presentation.ui.theme.component.FinishedPeriodHeader
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class FinishedPeriodHeaderScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 10.0,
    )

    @Test
    fun finishedPeriodHeaderRender() {
        Locale.setDefault(Locale.US)
        paparazzi.snapshot {
            WafeerTheme {
                FinishedPeriodHeader(
                    modifier = Modifier,
                    scrollState = rememberScrollState(),
                    hasSpends = true,
                    isOverBudget = false
                )
            }
        }
    }
}