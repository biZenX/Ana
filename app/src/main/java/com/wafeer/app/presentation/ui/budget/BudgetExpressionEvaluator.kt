package com.wafeer.app.presentation.ui.budget

import com.wafeer.app.domain.calculator.evaluateExpression
import javax.inject.Inject

class BudgetExpressionEvaluator @Inject constructor() {

    fun evaluate(input: String): String? = evaluateExpression(input)
}
