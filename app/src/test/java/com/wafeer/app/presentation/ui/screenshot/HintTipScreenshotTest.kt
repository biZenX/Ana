package com.wafeer.app.presentation.ui.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import com.wafeer.app.presentation.ui.theme.component.tooltip.AnchorPosition
import com.wafeer.app.presentation.ui.theme.component.tooltip.HintTip
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class HintTipScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 10.0,
    )

    @Test
    fun hintTipCenterPosition() {
        Locale.setDefault(Locale.US)
        paparazzi.snapshot {
            WafeerTheme {
                Box(modifier = Modifier.padding(50.dp)) {
                    HintTip(
                        position = AnchorPosition.Center,
                        content = { Text("Tap to continue") }
                    )
                }
            }
        }
    }
}