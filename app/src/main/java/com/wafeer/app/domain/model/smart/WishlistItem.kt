package com.wafeer.app.domain.model.smart

import java.math.BigDecimal

enum class WishlistStatus {
    COOLING,
    SAVED_AND_DISMISSED,
    PURCHASED,
}

data class WishlistItem(
    val id: Long = 0,
    val title: String,
    val price: BigDecimal,
    val createdAt: Long = System.currentTimeMillis(),
    val coolingHours: Int = 48,
    val status: WishlistStatus = WishlistStatus.COOLING,
    val resolvedAt: Long? = null,
) {
    val elapsedMillis: Long
        get() = (System.currentTimeMillis() - createdAt).coerceAtLeast(0L)

    val totalCoolingMillis: Long
        get() = coolingHours * 3600_000L

    val remainingMillis: Long
        get() = (totalCoolingMillis - elapsedMillis).coerceAtLeast(0L)

    val isCoolingFinished: Boolean
        get() = remainingMillis == 0L

    val remainingHours: Int
        get() = (remainingMillis / 3600_000L).toInt()

    val remainingMinutes: Int
        get() = ((remainingMillis % 3600_000L) / 60_000L).toInt()
}
