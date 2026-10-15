package com.wafeer.app.data.repository

import androidx.room.withTransaction
import com.wafeer.app.data.local.AppDatabase
import com.wafeer.app.data.local.dao.ArchivedBudgetDao
import com.wafeer.app.data.local.dao.BudgetSettingsDao
import com.wafeer.app.data.local.dao.CategoryDao
import com.wafeer.app.data.local.dao.PaidRecurrentOccurrenceDao
import com.wafeer.app.data.local.dao.QueuedTransactionDao
import com.wafeer.app.data.local.dao.TransactionDao
import com.wafeer.app.data.local.entity.ArchivedBudgetEntity
import com.wafeer.app.data.local.entity.BudgetSettingsEntity
import com.wafeer.app.data.local.entity.CategoryEntity
import com.wafeer.app.data.local.entity.PaidRecurrentOccurrenceEntity
import com.wafeer.app.data.local.entity.QueuedTransactionEntity
import com.wafeer.app.data.local.entity.TransactionEntity
import com.wafeer.app.domain.model.ArchivedBudget
import com.wafeer.app.domain.model.BudgetPeriod
import com.wafeer.app.domain.model.BudgetSettings
import com.wafeer.app.domain.model.BudgetSplitMode
import com.wafeer.app.domain.model.BudgetState
import com.wafeer.app.domain.model.Category
import com.wafeer.app.domain.model.PaidRecurrentOccurrence
import com.wafeer.app.domain.model.RecurrentFrequency
import com.wafeer.app.domain.model.RecurrentOccurrenceStatus
import com.wafeer.app.domain.model.RemainingBudgetStrategy
import com.wafeer.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import logcat.logcat
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val transactionDao: TransactionDao,
    private val settingsDao: BudgetSettingsDao,
    private val archivedBudgetDao: ArchivedBudgetDao,
    private val categoryDao: CategoryDao,
    private val queuedTransactionDao: QueuedTransactionDao,
    private val paidRecurrentOccurrenceDao: PaidRecurrentOccurrenceDao,
    private val settingsRepository: SettingsRepository? = null,
) : BudgetRepository {
    private fun TransactionEntity.toDomain(): Transaction = Transaction(
        id = this.id,
        amount = BigDecimal(this.amount),
        comment = this.comment,
        note = this.note,
        date = LocalDateTime.ofEpochSecond(this.date / 1000, 0, ZoneOffset.UTC),
        createdAt = this.createdAt,
        clientGeneratedId = this.clientGeneratedId,
        periodId = this.periodId,
        isDeleted = false,
        isRecurrent = this.isRecurrent,
        recurrentFrequency = this.recurrentFrequency?.let {
            try {
                RecurrentFrequency.valueOf(it)
            } catch (_: Exception) {
                null
            }
        },
        recurrentEndDate = this.recurrentEndDate?.let {
            LocalDateTime.ofEpochSecond(it / 1000, 0, ZoneOffset.UTC)
        },
        subscriptionDay = this.subscriptionDay,
        categoryId = this.categoryId,
        isCredit = this.isCredit,
        isCreditPaid = this.isCreditPaid,
        isAdjustment = this.isAdjustment
    )

    private fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
        id = if (this.id == 0L) 0 else this.id,
        amount = this.amount.toPlainString(),
        comment = this.comment,
        note = this.note,
        date = this.date!!.toEpochSecond(ZoneOffset.UTC) * 1000,
        createdAt = this.createdAt,
        clientGeneratedId = this.clientGeneratedId,
        periodId = this.periodId,
        isRecurrent = this.isRecurrent,
        recurrentFrequency = this.recurrentFrequency?.name,
        recurrentEndDate = this.recurrentEndDate?.toEpochSecond(ZoneOffset.UTC)?.times(1000),
        subscriptionDay = this.subscriptionDay,
        categoryId = this.categoryId,
        isCredit = this.isCredit,
        isCreditPaid = this.isCreditPaid,
        isAdjustment = this.isAdjustment
    )

    private fun QueuedTransactionEntity.toDomain(): Transaction = Transaction(
        id = this.id,
        amount = BigDecimal(this.amount),
        comment = this.comment,
        note = this.note,
        date = LocalDateTime.ofEpochSecond(this.date / 1000, 0, ZoneOffset.UTC),
        createdAt = this.createdAt,
        clientGeneratedId = null,
        periodId = 0L,
        isDeleted = false,
        isRecurrent = false,
        recurrentFrequency = null,
        recurrentEndDate = null,
        subscriptionDay = null,
        categoryId = this.categoryId,
        isCredit = this.isCredit,
        isCreditPaid = this.isCreditPaid,
        isAdjustment = this.isAdjustment
    )

    private fun Transaction.toQueuedEntity(): QueuedTransactionEntity = QueuedTransactionEntity(
        id = if (this.id == 0L) 0 else this.id,
        amount = this.amount.toPlainString(),
        comment = this.comment,
        note = this.note,
        date = this.date!!.toEpochSecond(ZoneOffset.UTC) * 1000,
        createdAt = this.createdAt,
        categoryId = this.categoryId,
        isCredit = this.isCredit,
        isCreditPaid = this.isCreditPaid,
        isAdjustment = this.isAdjustment
    )

    private fun BudgetSettingsEntity.toDomain(): BudgetSettings {
        val domain = BudgetSettings(
            totalBudget = BigDecimal(this.totalBudget),
            period = BudgetPeriod.valueOf(this.period),
            startDate = LocalDate.ofEpochDay(this.startDate / 86400000),
            endDate = this.endDate?.let { LocalDate.ofEpochDay(it / 86400000) },
            currencyCode = this.currencyCode,
            daysInPeriod = this.daysInPeriod,
            rollOverEnabled = this.rollOverEnabled,
            rollOverCarryForward = this.rollOverCarryForward,
            rollOverLimit = this.rollOverLimit?.let { BigDecimal(it) },
            remainingBudgetStrategy = try {
                RemainingBudgetStrategy.valueOf(this.remainingBudgetStrategy)
            } catch (_: Exception) {
                RemainingBudgetStrategy.ASK_ALWAYS
            },
            creditCardCutoffDay = this.creditCardCutoffDay,
            splitMode = try {
                BudgetSplitMode.valueOf(this.splitMode)
            } catch (_: Exception) {
                BudgetSplitMode.STATIC
            },
            rollOverAppliedDate = this.rollOverAppliedDate?.let { LocalDate.ofEpochDay(it / 86400000) },
        )
        logcat { "toDomain: entity=$this -> domain=$domain" }
        return domain
    }

    private fun BudgetSettings.toEntity(): BudgetSettingsEntity {
        val entity = BudgetSettingsEntity(
            id = 1,
            totalBudget = this.totalBudget.toPlainString(),
            period = this.period.name,
            startDate = this.startDate.toEpochDay() * 86400000,
            endDate = this.endDate?.toEpochDay()?.times(86400000),
            currencyCode = this.currencyCode,
            daysInPeriod = this.daysInPeriod,
            rollOverEnabled = this.rollOverEnabled,
            rollOverCarryForward = this.rollOverCarryForward,
            rollOverLimit = this.rollOverLimit?.toPlainString(),
            remainingBudgetStrategy = this.remainingBudgetStrategy.name,
            creditCardCutoffDay = this.creditCardCutoffDay,
            splitMode = this.splitMode.name,
            rollOverAppliedDate = this.rollOverAppliedDate?.toEpochDay()?.times(86400000),
        )
        logcat { "toEntity: domain=$this -> entity=$entity" }
        return entity
    }

    private fun CategoryEntity.toDomain(): Category = Category(
        id = this.id,
        name = this.name,
        isHidden = this.isHidden,
        usageCount = this.usageCount,
        lastUsedAt = this.lastUsedAt,
        createdAt = this.createdAt
    )

    private fun Category.toEntity(): CategoryEntity = CategoryEntity(
        id = if (this.id == 0L) 0 else this.id,
        name = this.name,
        isHidden = this.isHidden,
        usageCount = this.usageCount,
        lastUsedAt = this.lastUsedAt,
        createdAt = this.createdAt
    )

    private fun ArchivedBudgetEntity.toDomain(): ArchivedBudget = ArchivedBudget(
        periodId = this.periodId,
        totalBudget = BigDecimal(this.totalBudget),
        spentAmount = BigDecimal(this.spentAmount),
        startDate = LocalDate.ofEpochDay(this.startDate / 86400000),
        endDate = LocalDate.ofEpochDay(this.endDate / 86400000),
        currencyCode = this.currencyCode,
        periodType = BudgetPeriod.valueOf(this.periodType),
        createdAt = this.createdAt
    )

    private fun ArchivedBudget.toEntity(): ArchivedBudgetEntity = ArchivedBudgetEntity(
        periodId = periodId,
        totalBudget = totalBudget.toPlainString(),
        spentAmount = spentAmount.toPlainString(),
        startDate = startDate.toEpochDay() * 86400000,
        endDate = endDate.toEpochDay() * 86400000,
        currencyCode = currencyCode,
        periodType = periodType.name,
        createdAt = createdAt
    )

    override fun getTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getQueuedTransactions(): Flow<List<Transaction>> {
        return queuedTransactionDao.getAllQueuedTransactions()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTransactionsForPeriod(
        start: LocalDate,
        end: LocalDate
    ): Flow<List<Transaction>> {
        val startMillis = start.toEpochDay() * 86400000
        val endMillis = end.toEpochDay() * 86400000
        return transactionDao.getTransactionsForDateRange(startMillis, endMillis)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun addTransaction(transaction: Transaction): Long =
        transactionDao.insert(transaction.toEntity())

    override suspend fun addQueuedTransaction(transaction: Transaction) {
        queuedTransactionDao.insert(transaction.toQueuedEntity())
    }

    override suspend fun addTransactionIfAbsent(transaction: Transaction): Boolean {
        val rowId = transactionDao.insertIgnore(transaction.toEntity())
        return rowId != -1L
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
    }

    override suspend fun assignQueuedTransactionsToPeriod(periodId: Long) {
        appDatabase.withTransaction {
            val queuedTransactions = queuedTransactionDao.getAllQueuedTransactionsSync()
            if (queuedTransactions.isEmpty()) return@withTransaction

            val transactions = queuedTransactions.map { queued ->
                queued.toDomain().copy(
                    id = 0L,
                    periodId = periodId
                )
            }

            transactionDao.insertAllOrReplace(transactions.map { it.toEntity() })
            queuedTransactionDao.clearAll()
        }
    }

    override suspend fun upsertTransactions(transactions: List<Transaction>) {
        val entities = transactions.map { it.toEntity() }
        transactionDao.insertAllOrReplace(entities)
    }

    override suspend fun existsTransactionByClientGeneratedId(clientGeneratedId: String): Boolean {
        return transactionDao.existsByClientGeneratedId(clientGeneratedId)
    }

    override suspend fun getRecentTransactions(limit: Int): List<Transaction> {
        return transactionDao.getRecentTransactions(limit).map { it.toDomain() }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction.toEntity())
        paidRecurrentOccurrenceDao.deleteAllForTransaction(transaction.id)
    }

    override fun getSpentForDate(date: LocalDate): Flow<BigDecimal> {
        val startOfDay = date.toEpochDay() * 86400000
        val endOfDay = (date.plusDays(1)).toEpochDay() * 86400000
        return transactionDao.getTotalSpentForDay(startOfDay, endOfDay)
            .map { it?.let { BigDecimal(it) } ?: BigDecimal.ZERO }
    }

    override fun getSpentForPeriod(start: LocalDate, end: LocalDate): Flow<BigDecimal> {
        val startMillis = start.toEpochDay() * 86400000
        val endMillis = end.toEpochDay() * 86400000
        return transactionDao.getTotalSpentForPeriod(startMillis, endMillis)
            .map { it?.let { BigDecimal(it) } ?: BigDecimal.ZERO }
    }

    override fun getBudgetSettings(): Flow<BudgetSettings?> {
        return settingsDao.getSettings().map { it?.toDomain() }
    }

    override suspend fun saveBudgetSettings(settings: BudgetSettings) {
        logcat { "saveBudgetSettings: settings=$settings" }
        val entity = settings.toEntity()
        logcat { "saveBudgetSettings: inserting entity with endDate=${entity.endDate}" }
        settingsDao.insert(entity)
        logcat { "saveBudgetSettings: insert complete" }
    }

    override suspend fun getBudgetSettingsSync(): BudgetSettings? {
        return settingsDao.getSettingsSync()?.toDomain()
    }

    override suspend fun getTransactionById(transactionId: Long): Transaction? {
        return transactionDao.getTransactionById(transactionId)?.toDomain()
    }

    override fun getActiveCategories(): Flow<List<Category>> {
        return categoryDao.getActiveCategories().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun findOrCreateCategory(name: String): Category {
        val existing = categoryDao.getCategoryByName(name)
        return if (existing != null) {
            // If hidden, unhide it
            if (existing.isHidden) {
                categoryDao.unhideCategory(name)
            }
            // Increment usage count
            categoryDao.incrementUsage(name)
            // Return updated category
            categoryDao.getCategoryByName(name)?.toDomain() ?: existing.toDomain()
                .copy(usageCount = existing.usageCount + 1)
        } else {
            val newCategory = CategoryEntity(
                name = name,
                isHidden = false,
                usageCount = 1,
                lastUsedAt = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            )
            val id = categoryDao.insertCategory(newCategory)
            newCategory.copy(id = id).toDomain()
        }
    }

    override suspend fun createCategory(name: String) {
        val existing = categoryDao.getCategoryByName(name)
        if (existing == null) {
            categoryDao.insertCategory(CategoryEntity(name = name, usageCount = 0))
        } else if (existing.isHidden) {
            categoryDao.unhideCategory(name)
        }
    }

    override suspend fun hideCategory(name: String) {
        categoryDao.hideCategory(name)
    }

    override suspend fun incrementCategoryUsage(name: String) {
        categoryDao.incrementUsage(name)
    }

    override suspend fun getPeriodCount(): Int {
        return transactionDao.countDistinctPeriods()
    }

    override suspend fun markCreditTransactionsAsPaid(start: LocalDate, end: LocalDate) {
        val startMillis = start.toEpochDay() * 86400000
        val endMillis = end.toEpochDay() * 86400000
        transactionDao.markCreditAsPaidInRange(startMillis, endMillis)
    }

    override suspend fun markAllCreditTransactionsAsPaid() {
        transactionDao.markAllCreditAsPaid()
    }

    override suspend fun markTransactionAsPaid(transactionId: Long) {
        transactionDao.markTransactionAsPaid(transactionId)
    }

    override fun getPaidRecurrentOccurrences(): Flow<Set<PaidRecurrentOccurrence>> {
        return paidRecurrentOccurrenceDao.getAllPaidOccurrences().map { entities ->
            entities.map {
                PaidRecurrentOccurrence(
                    transactionId = it.transactionId,
                    occurrenceDate = LocalDate.ofEpochDay(it.occurrenceDateEpochDay),
                    status = runCatching { RecurrentOccurrenceStatus.valueOf(it.status) }
                        .getOrDefault(RecurrentOccurrenceStatus.PAID),
                )
            }.toSet()
        }
    }

    override suspend fun markRecurrentOccurrencePaid(
        transactionId: Long,
        occurrenceDate: LocalDate,
        status: RecurrentOccurrenceStatus,
    ) {
        paidRecurrentOccurrenceDao.markPaid(
            PaidRecurrentOccurrenceEntity(
                transactionId = transactionId,
                occurrenceDateEpochDay = occurrenceDate.toEpochDay(),
                status = status.name,
            )
        )
    }

    override suspend fun getPaidOccurrenceDatesFor(transactionId: Long): Set<LocalDate> {
        return paidRecurrentOccurrenceDao.getPaidOccurrenceDatesFor(transactionId)
            .map { LocalDate.ofEpochDay(it) }
            .toSet()
    }

    override fun getArchivedBudgets(): Flow<List<ArchivedBudget>> {
        return archivedBudgetDao.getAllArchivedBudgets().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun upsertArchivedBudgets(archivedBudgets: List<ArchivedBudget>) {
        val entities = archivedBudgets.map { it.toEntity() }
        archivedBudgetDao.insertAll(entities)
    }

    override suspend fun archiveCurrentPeriod(
        periodId: Long,
        settings: BudgetSettings,
        spentAmount: BigDecimal
    ) {
        val entity = ArchivedBudgetEntity(
            periodId = periodId,
            totalBudget = settings.totalBudget.toPlainString(),
            spentAmount = spentAmount.toPlainString(),
            startDate = settings.startDate.toEpochDay() * 86400000,
            endDate = settings.getPeriodEndDate().toEpochDay() * 86400000,
            currencyCode = settings.currencyCode,
            periodType = settings.period.name
        )
        archivedBudgetDao.insert(entity)
        logcat { "Archived period $periodId: total=${settings.totalBudget}, spent=$spentAmount" }
    }

    override suspend fun deleteArchivedBudget(periodId: Long) {
        appDatabase.withTransaction {
            transactionDao.deleteOneTimeByPeriodId(periodId)
            archivedBudgetDao.deleteById(periodId)
        }
    }

    override suspend fun deleteDemoTransactions() {
        appDatabase.withTransaction {
            transactionDao.deleteDemoTransactions()
            queuedTransactionDao.clearAll()
        }
    }

    private val defaultCategoryPairs = listOf(
        "سوبرماركت" to "Groceries",
        "مواصلات" to "Transit",
        "فواتير" to "Bills",
        "فواتير وخدمات" to "Bills",
        "قهوة" to "Coffee",
        "الألبان والبيض" to "Dairy & Eggs",
        "اللحوم والدواجن" to "Meat & Poultry",
        "البقوليات والحبوب" to "Grains & Pantry",
        "الخضروات" to "Vegetables",
        "الفواكه" to "Fruits",
        "التوابل والقهوة" to "Coffee & Spices",
        "المنظفات والمنزل" to "Household Supplies",
        "المواصلات والوقود" to "Transit & Fuel",
        "الفواتير والخدمات" to "Bills & Utilities",
        "مصروف شخصي" to "Personal Spending",
        "إيداع محفظة" to "Wallet Deposit",
    )

    private suspend fun migrateDefaultCategoriesInPlace(isArabic: Boolean) {
        for ((arName, enName) in defaultCategoryPairs) {
            val fromName = if (isArabic) enName else arName
            val toName = if (isArabic) arName else enName
            if (fromName == toName) continue
            val fromCat = categoryDao.getCategoryByName(fromName) ?: continue
            val existingToCat = categoryDao.getCategoryByName(toName)
            if (existingToCat != null) {
                transactionDao.migrateTransactionCategory(
                    oldName = fromName,
                    oldCategoryId = fromCat.id,
                    newName = toName,
                    newCategoryId = existingToCat.id,
                )
                categoryDao.deleteCategoryById(fromCat.id)
            } else {
                categoryDao.updateCategory(fromCat.copy(name = toName))
                transactionDao.migrateTransactionCategory(
                    oldName = fromName,
                    oldCategoryId = fromCat.id,
                    newName = toName,
                    newCategoryId = fromCat.id,
                )
            }
        }
    }

    override suspend fun localizeDefaultCategoriesAndDemoData(
        isArabic: Boolean,
        isDemoMode: Boolean,
    ) {
        migrateDefaultCategoriesInPlace(isArabic)
        if (isDemoMode) {
            val currentSettings = getBudgetSettingsSync()
            if (currentSettings != null) {
                val targetCurrency = if (isArabic) "EGP" else "USD"
                val isOppositeDemoCurrency = (isArabic && currentSettings.currencyCode == "USD") ||
                    (!isArabic && currentSettings.currencyCode == "EGP")
                val updatedBudget = when {
                    isArabic && currentSettings.totalBudget.compareTo(BigDecimal("1200.00")) == 0 && currentSettings.currencyCode == "USD" -> BigDecimal("5000.00")
                    isArabic && currentSettings.totalBudget.compareTo(BigDecimal("300.00")) == 0 && currentSettings.currencyCode == "USD" -> BigDecimal("1200.00")
                    isArabic && currentSettings.totalBudget.compareTo(BigDecimal("900.00")) == 0 && currentSettings.currencyCode == "USD" -> BigDecimal("5000.00")
                    !isArabic && currentSettings.totalBudget.compareTo(BigDecimal("5000.00")) == 0 && currentSettings.currencyCode == "EGP" -> BigDecimal("1200.00")
                    !isArabic && currentSettings.totalBudget.compareTo(BigDecimal("1200.00")) == 0 && currentSettings.currencyCode == "EGP" -> BigDecimal("300.00")
                    else -> currentSettings.totalBudget
                }
                val newCurrency = if (isOppositeDemoCurrency) targetCurrency else currentSettings.currencyCode
                if (newCurrency != currentSettings.currencyCode || updatedBudget != currentSettings.totalBudget) {
                    saveBudgetSettings(
                        currentSettings.copy(
                            currencyCode = newCurrency,
                            totalBudget = updatedBudget,
                        )
                    )
                }
            }
            seedDemoDataInternal(isArabic)
        }
    }

    override suspend fun seedDemoData(
        currencyCode: String,
        sampleBudget: BigDecimal,
    ) {
        val currentSettings = getBudgetSettingsSync()
        if (currentSettings == null) {
            val defaultSettings = BudgetSettings(
                totalBudget = sampleBudget,
                period = BudgetPeriod.MONTHLY,
                startDate = LocalDate.now().withDayOfMonth(1),
                currencyCode = currencyCode,
            )
            saveBudgetSettings(defaultSettings)
        }
        val isArabic = com.wafeer.app.presentation.util.LocaleHelper.isArabic()
        seedDemoDataInternal(isArabic)
    }

    private suspend fun seedDemoDataInternal(isArabic: Boolean) {
        transactionDao.deleteDemoTransactions()
        migrateDefaultCategoriesInPlace(isArabic)

        val groceryTitle = if (isArabic) "سوبرماركت" else "Groceries"
        val transportTitle = if (isArabic) "مواصلات" else "Transit"
        val billsTitle = if (isArabic) "فواتير" else "Bills"

        val groceryCat = findOrCreateCategory(groceryTitle)
        val transportCat = findOrCreateCategory(transportTitle)
        val billsCat = findOrCreateCategory(billsTitle)

        val currentSettings = getBudgetSettingsSync()
        val activePeriodStart = currentSettings?.startDate ?: LocalDate.now().withDayOfMonth(1)
        val today = LocalDate.now()
        val now = LocalDateTime.now()

        fun dateFor(daysAgo: Long, hoursAgoFallback: Long): Long {
            val targetDate = today.minusDays(daysAgo)
            val dt = if (!targetDate.isBefore(activePeriodStart)) {
                now.minusDays(daysAgo)
            } else {
                val startOfDay = activePeriodStart.atStartOfDay().plusHours(hoursAgoFallback.coerceIn(1, 12))
                if (now.isAfter(startOfDay)) {
                    now.minusHours(hoursAgoFallback.coerceAtLeast(1))
                        .takeIf { !it.isBefore(activePeriodStart.atStartOfDay()) }
                        ?: startOfDay
                } else {
                    startOfDay
                }
            }
            return dt.toEpochSecond(ZoneOffset.UTC) * 1000
        }

        val activePeriodId = settingsRepository?.getCurrentPeriodId() ?: 0L

        val sampleTransactions = if (isArabic) {
            listOf(
                TransactionEntity(
                    amount = "420.00",
                    comment = groceryTitle,
                    note = "مشتريات للبيت",
                    date = dateFor(daysAgo = 2, hoursAgoFallback = 2),
                    clientGeneratedId = "demo_seed_grocery",
                    categoryId = groceryCat.id,
                    periodId = activePeriodId,
                ),
                TransactionEntity(
                    amount = "120.00",
                    comment = transportTitle,
                    note = "مواصلات وبنزين",
                    date = dateFor(daysAgo = 1, hoursAgoFallback = 4),
                    clientGeneratedId = "demo_seed_transport",
                    categoryId = transportCat.id,
                    periodId = activePeriodId,
                ),
                TransactionEntity(
                    amount = "350.00",
                    comment = billsTitle,
                    note = "فاتورة دورية",
                    date = dateFor(daysAgo = 3, hoursAgoFallback = 6),
                    clientGeneratedId = "demo_seed_bills",
                    categoryId = billsCat.id,
                    periodId = activePeriodId,
                ),
            )
        } else {
            listOf(
                TransactionEntity(
                    amount = "42.00",
                    comment = groceryTitle,
                    note = "Weekly groceries",
                    date = dateFor(daysAgo = 2, hoursAgoFallback = 2),
                    clientGeneratedId = "demo_seed_grocery",
                    categoryId = groceryCat.id,
                    periodId = activePeriodId,
                ),
                TransactionEntity(
                    amount = "12.00",
                    comment = transportTitle,
                    note = "Subway commute",
                    date = dateFor(daysAgo = 1, hoursAgoFallback = 4),
                    clientGeneratedId = "demo_seed_transport",
                    categoryId = transportCat.id,
                    periodId = activePeriodId,
                ),
                TransactionEntity(
                    amount = "35.00",
                    comment = billsTitle,
                    note = "Utility bill",
                    date = dateFor(daysAgo = 3, hoursAgoFallback = 6),
                    clientGeneratedId = "demo_seed_bills",
                    categoryId = billsCat.id,
                    periodId = activePeriodId,
                ),
            )
        }

        for (tx in sampleTransactions) {
            transactionDao.insertOrReplace(tx)
        }
    }
}
