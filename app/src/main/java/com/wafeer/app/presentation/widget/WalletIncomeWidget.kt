@file:OptIn(ExperimentalGlancePreviewApi::class)

package com.wafeer.app.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import com.wafeer.app.R
import logcat.logcat

class WalletIncomeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WalletIncomeWidget()
}

class WalletIncomeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
                val income = prefs[intPreferencesKey("extra_income")] ?: 0
                val currency = prefs[stringPreferencesKey("currency")] ?: "USD"
                WalletIncomeContent(extraIncome = income, currency = currency)
            }
        }
    }

    @Composable
    internal fun WalletIncomeContent(
        extraIncome: Int,
        currency: String,
        context: Context = LocalContext.current,
    ) {
        val formattedAmount = formatWidgetCurrency(currency, extraIncome)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(4.dp)
                .cornerRadius(24.dp)
                .background(GlanceTheme.colors.surface)
                .clickable(actionRunCallback<OpenAppAction>())
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.shape_soft_star_1),
                        contentDescription = null,
                        modifier = GlanceModifier.fillMaxSize(),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
                    )
                    Image(
                        provider = ImageProvider(R.drawable.ic_plus),
                        contentDescription = null,
                        modifier = GlanceModifier.size(18.dp),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onPrimary)
                    )
                }

                Spacer(modifier = GlanceModifier.width(10.dp))

                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WidgetThmanyahText(
                        text = context.getString(R.string.wallet_mode_title),
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    WidgetThmanyahText(
                        text = formattedAmount,
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        useDisplayFont = true,
                    )
                }
            }
        }
    }
}

suspend fun updateWalletIncomeWidget(context: Context, extraIncome: Int, currency: String) {
    logcat("WalletIncomeWidget") { "Updating WalletIncomeWidget: income=$extraIncome, currency=$currency" }
    val glanceIds = boundGlanceIds(context, WalletIncomeWidgetReceiver::class.java, WalletIncomeWidget::class.java)
    glanceIds.forEach { glanceId ->
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[intPreferencesKey("extra_income")] = extraIncome
            prefs[stringPreferencesKey("currency")] = currency
        }
        WalletIncomeWidget().update(context, glanceId)
    }
}

@Preview(widthDp = 180, heightDp = 60)
@Composable
private fun WalletIncomeWidgetPreview() {
    GlanceTheme {
        WalletIncomeWidget().WalletIncomeContent(
            extraIncome = 750,
            currency = "EGP"
        )
    }
}
