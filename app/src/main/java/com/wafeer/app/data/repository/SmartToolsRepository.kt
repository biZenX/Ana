package com.wafeer.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.wafeer.app.data.local.dao.MicroGoalDao
import com.wafeer.app.data.local.dao.WishlistDao
import com.wafeer.app.data.local.entity.MicroGoalEntity
import com.wafeer.app.data.local.entity.WishlistItemEntity
import com.wafeer.app.domain.model.smart.AffordabilityCalculation
import com.wafeer.app.domain.model.smart.AffordabilityVerdictLevel
import com.wafeer.app.domain.model.smart.MicroGoal
import com.wafeer.app.domain.model.smart.WishlistItem
import com.wafeer.app.domain.model.smart.WishlistStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartToolsRepository @Inject constructor(
    private val microGoalDao: MicroGoalDao,
    private val wishlistDao: WishlistDao,
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        private val KEY_FUN_FUND_RATIO = floatPreferencesKey("smart_fun_fund_ratio")
        private val KEY_TOTAL_WISHLIST_SAVINGS = stringPreferencesKey("smart_total_wishlist_savings")
    }

    // --- Micro Goals ---
    fun observeGoals(): Flow<List<MicroGoal>> =
        microGoalDao.getAllGoals().map { list -> list.map { it.toDomain() } }

    suspend fun addGoal(title: String, targetAmount: BigDecimal): Long {
        val entity = MicroGoalEntity(
            title = title.trim(),
            targetAmount = targetAmount.toPlainString(),
            savedAmount = "0",
            createdAt = System.currentTimeMillis(),
            isCompleted = false,
        )
        return microGoalDao.insertGoal(entity)
    }

    suspend fun depositToGoal(goal: MicroGoal, depositAmount: BigDecimal) {
        val newSaved = (goal.savedAmount + depositAmount).coerceAtLeast(BigDecimal.ZERO)
        val completed = newSaved >= goal.targetAmount
        val updated = MicroGoalEntity.fromDomain(
            goal.copy(
                savedAmount = newSaved,
                isCompleted = completed,
            )
        )
        microGoalDao.updateGoal(updated)
    }

    suspend fun deleteGoal(id: Long) {
        microGoalDao.deleteGoalById(id)
    }

    // --- 48-Hour Wishlist ---
    fun observeWishlist(): Flow<List<WishlistItem>> =
        wishlistDao.getAllItems().map { list -> list.map { it.toDomain() } }

    suspend fun addToWishlist(title: String, price: BigDecimal, coolingHours: Int = 48): Long {
        val entity = WishlistItemEntity(
            title = title.trim(),
            price = price.toPlainString(),
            createdAt = System.currentTimeMillis(),
            coolingHours = coolingHours,
            status = WishlistStatus.COOLING.name,
        )
        return wishlistDao.insertItem(entity)
    }

    suspend fun markWishlistSaved(item: WishlistItem) {
        val updated = WishlistItemEntity.fromDomain(
            item.copy(
                status = WishlistStatus.SAVED_AND_DISMISSED,
                resolvedAt = System.currentTimeMillis(),
            )
        )
        wishlistDao.updateItem(updated)

        // Increment total savings
        dataStore.edit { prefs ->
            val currentStr = prefs[KEY_TOTAL_WISHLIST_SAVINGS] ?: "0"
            val current = runCatching { BigDecimal(currentStr) }.getOrDefault(BigDecimal.ZERO)
            val newTotal = current + item.price
            prefs[KEY_TOTAL_WISHLIST_SAVINGS] = newTotal.toPlainString()
        }
    }

    suspend fun markWishlistPurchased(item: WishlistItem) {
        val updated = WishlistItemEntity.fromDomain(
            item.copy(
                status = WishlistStatus.PURCHASED,
                resolvedAt = System.currentTimeMillis(),
            )
        )
        wishlistDao.updateItem(updated)
    }

    suspend fun deleteWishlistItem(id: Long) {
        wishlistDao.deleteItemById(id)
    }

    fun observeTotalWishlistSavings(): Flow<BigDecimal> =
        dataStore.data.map { prefs ->
            val raw = prefs[KEY_TOTAL_WISHLIST_SAVINGS] ?: "0"
            runCatching { BigDecimal(raw) }.getOrDefault(BigDecimal.ZERO)
        }

    // --- Fun Fund Ratio ---
    fun observeFunFundRatio(): Flow<Float> =
        dataStore.data.map { prefs ->
            prefs[KEY_FUN_FUND_RATIO] ?: 15f
        }

    suspend fun setFunFundRatio(ratioPercent: Float) {
        dataStore.edit { prefs ->
            prefs[KEY_FUN_FUND_RATIO] = ratioPercent.coerceIn(5f, 40f)
        }
    }

    // --- Can I Afford It? Calculator ---
    fun calculateAffordability(
        price: BigDecimal,
        currentDailyAllowance: BigDecimal,
        remainingBudget: BigDecimal,
        remainingDays: Int,
        currencyCode: String = "USD",
    ): AffordabilityCalculation {
        val days = remainingDays.coerceAtLeast(1)
        val daily = currentDailyAllowance.coerceAtLeast(BigDecimal.ZERO)

        val daysCost = if (daily > BigDecimal.ZERO) {
            (price.toDouble() / daily.toDouble()).toFloat()
        } else {
            0f
        }

        val budgetAfter = remainingBudget - price
        val newDailyAfter = if (days > 0) {
            budgetAfter.divide(BigDecimal(days), 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        val isAr = com.wafeer.app.presentation.util.LocaleHelper.isArabic()
        val (verdict, recommendation) = when {
            price > remainingBudget -> {
                val rec = if (isAr) {
                    "السعر أكبر من ميزانيتك المتبقية (${remainingBudget.toPlainString()} $currencyCode). لو اشتريتها هتكسر الميزانية أو تضطر تستلف!"
                } else {
                    "The price exceeds your remaining budget ($currencyCode ${remainingBudget.toPlainString()}). Buying this will put you over budget!"
                }
                Pair(AffordabilityVerdictLevel.DANGEROUS, rec)
            }
            newDailyAfter < (daily * BigDecimal("0.5")) -> {
                val rec = if (isAr) {
                    "هتاكل منك مصروف ${String.format(java.util.Locale.US, "%.1f", daysCost)} يوم! مصروفك اليومي هينزل من ${daily.toInt()} إلى ${newDailyAfter.coerceAtLeast(BigDecimal.ZERO).toInt()} $currencyCode. محتاج تشد الحزام باقي الأيام."
                } else {
                    "This equals ${String.format(java.util.Locale.US, "%.1f", daysCost)} days of allowance! Your daily budget will drop from ${daily.toInt()} to ${newDailyAfter.coerceAtLeast(BigDecimal.ZERO).toInt()} $currencyCode. You'll need to tighten your belt."
                }
                Pair(AffordabilityVerdictLevel.CAUTION, rec)
            }
            else -> {
                val rec = if (isAr) {
                    "تقدر تشتريها وأنت مطمن! هتكلفك تقريباً ${String.format(java.util.Locale.US, "%.1f", daysCost)} يوم من المصروف، وميزانيتك اليومية هتفضل مستقرة عند ${newDailyAfter.toInt()} $currencyCode."
                } else {
                    "Safe to buy! It costs about ${String.format(java.util.Locale.US, "%.1f", daysCost)} days of allowance, and your daily budget will remain stable at ${newDailyAfter.toInt()} $currencyCode."
                }
                Pair(AffordabilityVerdictLevel.SAFE, rec)
            }
        }

        return AffordabilityCalculation(
            price = price,
            currentDailyAllowance = daily,
            daysOfAllowanceCost = daysCost,
            newDailyAllowanceAfter = newDailyAfter.coerceAtLeast(BigDecimal.ZERO),
            verdict = verdict,
            recommendation = recommendation,
        )
    }
}
