package com.wafeer.app.presentation.widget

import com.wafeer.app.presentation.util.font.format.formatCurrencySymbolOnly
import java.math.BigDecimal

fun formatWidgetCurrency(currency: String, amount: Int): String {
    return formatCurrencySymbolOnly(
        value = BigDecimal(amount),
        currencyCode = currency,
        minimumFractionDigits = 0,
    )
}
