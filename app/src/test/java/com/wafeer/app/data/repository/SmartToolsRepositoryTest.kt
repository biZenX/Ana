package com.wafeer.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.wafeer.app.data.local.dao.MicroGoalDao
import com.wafeer.app.data.local.dao.WishlistDao
import com.wafeer.app.data.local.entity.MicroGoalEntity
import com.wafeer.app.data.local.entity.WishlistItemEntity
import com.wafeer.app.domain.model.smart.AffordabilityVerdictLevel
import com.wafeer.app.domain.model.smart.MicroGoal
import com.wafeer.app.domain.model.smart.WishlistItem
import com.wafeer.app.domain.model.smart.WishlistStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class SmartToolsRepositoryTest {

    private val microGoalDao: MicroGoalDao = mockk(relaxed = true)
    private val wishlistDao: WishlistDao = mockk(relaxed = true)
    private val dataStore: DataStore<Preferences> = mockk(relaxed = true)

    private lateinit var repository: SmartToolsRepository

    @Before
    fun setup() {
        every { dataStore.data } returns flowOf(emptyPreferences())
        repository = SmartToolsRepository(microGoalDao, wishlistDao, dataStore)
    }

    @Test
    fun addGoal_insertsEntityProperly() = runTest {
        coEvery { microGoalDao.insertGoal(any()) } returns 1L

        val id = repository.addGoal("AirPods", BigDecimal("1500"))

        assertEquals(1L, id)
        coVerify {
            microGoalDao.insertGoal(
                match {
                    it.title == "AirPods" && it.targetAmount == "1500" && it.savedAmount == "0"
                }
            )
        }
    }

    @Test
    fun depositToGoal_updatesSavedAmountAndMarksCompleted() = runTest {
        val goal = MicroGoal(
            id = 2L,
            title = "Course",
            targetAmount = BigDecimal("500"),
            savedAmount = BigDecimal("450"),
        )

        repository.depositToGoal(goal, BigDecimal("100"))

        coVerify {
            microGoalDao.updateGoal(
                match {
                    it.savedAmount == "550" && it.isCompleted
                }
            )
        }
    }

    @Test
    fun calculateAffordability_safeVerdict() {
        val result = repository.calculateAffordability(
            price = BigDecimal("50"),
            currentDailyAllowance = BigDecimal("200"),
            remainingBudget = BigDecimal("2000"),
            remainingDays = 10,
        )

        assertEquals(AffordabilityVerdictLevel.SAFE, result.verdict)
        assertTrue(result.recommendation.contains("مطمن"))
    }

    @Test
    fun calculateAffordability_cautionVerdict_whenSevereImpactOnAllowance() {
        val result = repository.calculateAffordability(
            price = BigDecimal("1500"),
            currentDailyAllowance = BigDecimal("200"),
            remainingBudget = BigDecimal("1800"),
            remainingDays = 10,
        )

        assertEquals(AffordabilityVerdictLevel.CAUTION, result.verdict)
        assertTrue(result.recommendation.contains("تشد الحزام"))
    }

    @Test
    fun calculateAffordability_dangerousVerdict_whenExceedsBudget() {
        val result = repository.calculateAffordability(
            price = BigDecimal("2500"),
            currentDailyAllowance = BigDecimal("100"),
            remainingBudget = BigDecimal("1000"),
            remainingDays = 10,
        )

        assertEquals(AffordabilityVerdictLevel.DANGEROUS, result.verdict)
        assertTrue(result.recommendation.contains("تكسر الميزانية"))
    }
}
