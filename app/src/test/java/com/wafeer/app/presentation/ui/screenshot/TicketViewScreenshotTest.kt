package com.wafeer.app.presentation.ui.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import com.wafeer.app.presentation.ui.theme.component.ticket.TicketView
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class TicketViewScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 10.0,
    )

    @Test
    fun ticketViewDefault() {
        Locale.setDefault(Locale.US)
        paparazzi.snapshot {
            WafeerTheme {
                TicketView(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.Unspecified,
                    teethWidthDp = 15f,
                    teethHeightDp = 3f,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                    ) { /* placeholder content */ }
                }
            }
        }
    }
}
