package com.wafeer.app.domain.model.smart

import java.math.BigDecimal

enum class AffordabilityVerdictLevel {
    SAFE,       // آمنة - تقدر تشتريها براحتك
    CAUTION,    // محتاج تشد الحزام
    DANGEROUS,  // هتكسر ميزانيتك
}

data class AffordabilityCalculation(
    val price: BigDecimal,
    val currentDailyAllowance: BigDecimal,
    val daysOfAllowanceCost: Float,
    val newDailyAllowanceAfter: BigDecimal,
    val verdict: AffordabilityVerdictLevel,
    val recommendation: String,
)
