@file:OptIn(ExperimentalGlancePreviewApi::class)

package com.serranoie.app.minus.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import com.serranoie.app.minus.R
import logcat.logcat
import java.time.LocalDate

class FinancialCoachWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FinancialCoachWidget()
}

class FinancialCoachWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
                val tipIndex = prefs[intPreferencesKey("tip_index")] ?: (LocalDate.now().dayOfMonth % 13)
                FinancialCoachContent(tipIndex = tipIndex)
            }
        }
    }

    @Composable
    internal fun FinancialCoachContent(
        tipIndex: Int,
        context: Context = LocalContext.current,
    ) {
        val (titleRes, bodyRes) = getFinancialTipResources(tipIndex)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(4.dp)
                .cornerRadius(24.dp)
                .background(GlanceTheme.colors.surface)
                .clickable(actionRunCallback<OpenAppAction>())
                .padding(14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier.size(22.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.shape_soft_star_1),
                            contentDescription = null,
                            modifier = GlanceModifier.fillMaxSize(),
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    WidgetThmanyahText(
                        text = context.getString(R.string.widget_coach_title),
                        color = GlanceTheme.colors.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Tip Title
                WidgetThmanyahText(
                    text = context.getString(titleRes),
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    useDisplayFont = true,
                )

                Spacer(modifier = GlanceModifier.height(6.dp))

                // Tip Body (rendered with Thmanyah font and multiline wrapping)
                WidgetThmanyahText(
                    text = context.getString(bodyRes),
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    maxWidthDp = 300.dp,
                    maxLines = 5,
                )
            }
        }
    }
}

internal fun getFinancialTipResources(index: Int): Pair<Int, Int> {
    val normalized = (index % 13).let { if (it < 0) it + 13 else it }
    return when (normalized) {
        0 -> R.string.financial_tip_1_title to R.string.financial_tip_1_body
        1 -> R.string.financial_tip_2_title to R.string.financial_tip_2_body
        2 -> R.string.financial_tip_3_title to R.string.financial_tip_3_body
        3 -> R.string.financial_tip_4_title to R.string.financial_tip_4_body
        4 -> R.string.financial_tip_5_title to R.string.financial_tip_5_body
        5 -> R.string.financial_tip_6_title to R.string.financial_tip_6_body
        6 -> R.string.financial_tip_7_title to R.string.financial_tip_7_body
        7 -> R.string.financial_tip_8_title to R.string.financial_tip_8_body
        8 -> R.string.financial_tip_9_title to R.string.financial_tip_9_body
        9 -> R.string.financial_tip_10_title to R.string.financial_tip_10_body
        10 -> R.string.financial_tip_11_title to R.string.financial_tip_11_body
        11 -> R.string.financial_tip_12_title to R.string.financial_tip_12_body
        else -> R.string.financial_tip_13_title to R.string.financial_tip_13_body
    }
}

suspend fun updateFinancialCoachWidget(context: Context) {
    val dayIndex = LocalDate.now().dayOfMonth % 13
    logcat("FinancialCoachWidget") { "Updating FinancialCoachWidget: tipIndex=$dayIndex" }
    val glanceIds = boundGlanceIds(context, FinancialCoachWidgetReceiver::class.java, FinancialCoachWidget::class.java)
    glanceIds.forEach { glanceId ->
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[intPreferencesKey("tip_index")] = dayIndex
        }
        FinancialCoachWidget().update(context, glanceId)
    }
}

@Preview(widthDp = 240, heightDp = 120)
@Composable
private fun FinancialCoachWidgetPreview() {
    GlanceTheme {
        FinancialCoachWidget().FinancialCoachContent(
            tipIndex = 7
        )
    }
}
