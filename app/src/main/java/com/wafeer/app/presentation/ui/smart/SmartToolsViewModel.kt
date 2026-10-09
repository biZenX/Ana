package com.wafeer.app.presentation.ui.smart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafeer.app.data.repository.SmartToolsRepository
import com.wafeer.app.domain.model.smart.AffordabilityCalculation
import com.wafeer.app.domain.model.smart.MicroGoal
import com.wafeer.app.domain.model.smart.WishlistItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

enum class SmartToolTab {
    CALCULATOR,
    MICRO_GOALS,
    WISHLIST_48H,
    FUN_FUND,
}

data class SmartToolsUiState(
    val selectedTab: SmartToolTab = SmartToolTab.CALCULATOR,
    val goals: List<MicroGoal> = emptyList(),
    val wishlist: List<WishlistItem> = emptyList(),
    val totalWishlistSavings: BigDecimal = BigDecimal.ZERO,
    val funFundRatio: Float = 15f,
    val calculationInputPrice: String = "",
    val calculationResult: AffordabilityCalculation? = null,
)

@HiltViewModel
class SmartToolsViewModel @Inject constructor(
    private val repository: SmartToolsRepository,
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(SmartToolTab.CALCULATOR)
    private val _calculationInputPrice = MutableStateFlow("")
    private val _calculationResult = MutableStateFlow<AffordabilityCalculation?>(null)

    private data class IntermediateState(
        val goals: List<MicroGoal>,
        val wishlist: List<WishlistItem>,
        val savings: BigDecimal,
        val funRatio: Float,
    )

    val uiState: StateFlow<SmartToolsUiState> = combine(
        _selectedTab,
        _calculationInputPrice,
        _calculationResult,
        combine(
            repository.observeGoals(),
            repository.observeWishlist(),
            repository.observeTotalWishlistSavings(),
            repository.observeFunFundRatio(),
        ) { goals, wishlist, savings, funRatio ->
            IntermediateState(goals, wishlist, savings, funRatio)
        }
    ) { tab, priceInput, calcResult, intermediate ->
        SmartToolsUiState(
            selectedTab = tab,
            goals = intermediate.goals,
            wishlist = intermediate.wishlist,
            totalWishlistSavings = intermediate.savings,
            funFundRatio = intermediate.funRatio,
            calculationInputPrice = priceInput,
            calculationResult = calcResult,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SmartToolsUiState(),
    )

    fun selectTab(tab: SmartToolTab) {
        _selectedTab.value = tab
    }

    fun onPriceInputChanged(
        priceInput: String,
        currentDailyAllowance: BigDecimal,
        remainingBudget: BigDecimal,
        remainingDays: Int,
    ) {
        _calculationInputPrice.value = priceInput
        val price = priceInput.toBigDecimalOrNull()
        if (price != null && price > BigDecimal.ZERO) {
            _calculationResult.value = repository.calculateAffordability(
                price = price,
                currentDailyAllowance = currentDailyAllowance,
                remainingBudget = remainingBudget,
                remainingDays = remainingDays,
            )
        } else {
            _calculationResult.value = null
        }
    }

    fun addMicroGoal(title: String, targetAmount: BigDecimal) {
        if (title.isBlank() || targetAmount <= BigDecimal.ZERO) return
        viewModelScope.launch {
            repository.addGoal(title, targetAmount)
        }
    }

    fun depositToGoal(goal: MicroGoal, amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) return
        viewModelScope.launch {
            repository.depositToGoal(goal, amount)
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }

    fun addWishlistItem(title: String, price: BigDecimal, coolingHours: Int = 48) {
        if (title.isBlank() || price <= BigDecimal.ZERO) return
        viewModelScope.launch {
            repository.addToWishlist(title, price, coolingHours)
        }
    }

    fun markWishlistSaved(item: WishlistItem) {
        viewModelScope.launch {
            repository.markWishlistSaved(item)
        }
    }

    fun markWishlistPurchased(item: WishlistItem) {
        viewModelScope.launch {
            repository.markWishlistPurchased(item)
        }
    }

    fun deleteWishlistItem(id: Long) {
        viewModelScope.launch {
            repository.deleteWishlistItem(id)
        }
    }

    fun setFunFundRatio(ratio: Float) {
        viewModelScope.launch {
            repository.setFunFundRatio(ratio)
        }
    }
}
