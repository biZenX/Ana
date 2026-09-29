package com.wafeer.app.presentation.ui.subscriptions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafeer.app.R
import com.wafeer.app.domain.model.RecurrentFrequency

@Composable
internal fun frequencyText(frequency: RecurrentFrequency?): String = when (frequency) {
    RecurrentFrequency.WEEKLY -> stringResource(R.string.recurrent_frequency_weekly)
    RecurrentFrequency.BIWEEKLY -> stringResource(R.string.recurrent_frequency_biweekly)
    RecurrentFrequency.MONTHLY -> stringResource(R.string.recurrent_frequency_monthly)
    null -> ""
}
