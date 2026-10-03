package com.wafeer.app.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        TransactionEntity::class,
        BudgetSettingsEntity::class,
        CategoryEntity::class,
        QueuedTransactionEntity::class,
        ArchivedBudgetEntity::class,
        PaidRecurrentOccurrenceEntity::class
    ],
    version = 21,
    autoMigrations = [
        AutoMigration(from = 16, to = 17),
        AutoMigration(from = 17, to = 18),
        AutoMigration(from = 18, to = 19)
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    abstract fun budgetSettingsDao(): BudgetSettingsDao

    abstract fun archivedBudgetDao(): ArchivedBudgetDao

    abstract fun categoryDao(): CategoryDao

    abstract fun queuedTransactionDao(): QueuedTransactionDao

    abstract fun paidRecurrentOccurrenceDao(): PaidRecurrentOccurrenceDao

    companion object {
        const val DATABASE_NAME = "wafeer_budget.db"
    }
}
