package com.wafeer.wear.sync

interface SyncRepository {
    suspend fun syncPendingExpenses(): Boolean
}
