package com.wafeer.app.presentation.ui.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import com.wafeer.app.presentation.ui.theme.component.WavyDivider
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class WavyDividerScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 10.0,
    )

    @Test
    fun wavyDivider() {
        Locale.setDefault(Locale.US)
        paparazzi.snapshot {
            WafeerTheme {
                Box(modifier = Modifier.padding(16.dp)) {
                    WavyDivider(
                        text = "Recurrent payments next period",
                        modifier = Modifier.fillMaxWidth(),
                        horizontalPadding = 16.dp,
                        amplitude = 4f,
                        wavelength = 45f,
                        strokeWidth = 4f
                    )
                }
            }
        }
    }
}