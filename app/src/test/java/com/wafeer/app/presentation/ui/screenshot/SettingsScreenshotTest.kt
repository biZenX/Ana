package com.wafeer.app.presentation.ui.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.wafeer.app.domain.model.PeriodMappingMode
import com.wafeer.app.presentation.ui.history.RecurrentPaymentsViewMode
import com.wafeer.app.presentation.ui.settings.Settings
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class SettingsScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 10.0,
    )

    @Test
    fun settingsDefaultState() {
        Locale.setDefault(Locale.US)

        paparazzi.snapshot {
            WafeerTheme {
                SettingsPreview(
                    recurrentPaymentsViewMode = RecurrentPaymentsViewMode.HORIZONTAL_LIST,
                    notificationHour = 19,
                    notificationMinute = 0,
                    recurrentNotificationHour = 8,
                    recurrentNotificationMinute = 0,
                    exactAlarmEnabled = false,
                    periodMappingMode = PeriodMappingMode.ACTIVE_BUDGET,
                )
            }
        }
    }

    @Test
    fun settingsDarkThemeWithCreditFeature() {
        Locale.setDefault(Locale.US)

        paparazzi.snapshot {
            WafeerTheme {
                SettingsPreview(
                    recurrentPaymentsViewMode = RecurrentPaymentsViewMode.VERTICAL_LIST,
                    notificationHour = 20,
                    notificationMinute = 30,
                    recurrentNotificationHour = 9,
                    recurrentNotificationMinute = 0,
                    exactAlarmEnabled = true,
                    periodMappingMode = PeriodMappingMode.CALENDAR_BUCKET,
                )
            }
        }
    }

    @Composable
    private fun SettingsPreview(
        recurrentPaymentsViewMode: RecurrentPaymentsViewMode,
        notificationHour: Int,
        notificationMinute: Int,
        recurrentNotificationHour: Int,
        recurrentNotificationMinute: Int,
        exactAlarmEnabled: Boolean,
        periodMappingMode: PeriodMappingMode,
    ) {
        WafeerTheme {
            Settings(
                modifier = Modifier.fillMaxSize(),
                recurrentPaymentsViewMode = recurrentPaymentsViewMode,
                notificationHour = notificationHour,
                notificationMinute = notificationMinute,
                recurrentNotificationHour = recurrentNotificationHour,
                recurrentNotificationMinute = recurrentNotificationMinute,
                exactAlarmEnabled = exactAlarmEnabled,
                onRecurrentPaymentsViewModeChange = {},
                onNotificationTimeChange = { _, _ -> },
                onRecurrentNotificationTimeChange = { _, _ -> },
                onOpenExactAlarmSettings = {},
                periodMappingMode = periodMappingMode,
                onPeriodMappingModeChange = {},
                onExportCsv = {},
                onImportCsv = {},
                onResetTutorial = {},
                onBugReportClick = {},
                onBack = {},
                isCensored = false,
                notificationPermissionGranted = true,
                onOpenNotificationSettings = {},
                onNavigateToChangelog = {},
            )
        }
    }
}
