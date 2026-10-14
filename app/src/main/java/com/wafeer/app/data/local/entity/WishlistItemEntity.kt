package com.wafeer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wafeer.app.domain.model.smart.WishlistItem
import com.wafeer.app.domain.model.smart.WishlistStatus
import java.math.BigDecimal

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val price: String,
    val createdAt: Long = System.currentTimeMillis(),
    val coolingHours: Int = 48,
    val status: String = "COOLING",
    val resolvedAt: Long? = null,
) {
    fun toDomain(): WishlistItem = WishlistItem(
        id = id,
        title = title,
        price = BigDecimal(price),
        createdAt = createdAt,
        coolingHours = coolingHours,
        status = runCatching { WishlistStatus.valueOf(status) }.getOrDefault(WishlistStatus.COOLING),
        resolvedAt = resolvedAt,
    )

    companion object {
        fun fromDomain(item: WishlistItem): WishlistItemEntity = WishlistItemEntity(
            id = item.id,
            title = item.title,
            price = item.price.toPlainString(),
            createdAt = item.createdAt,
            coolingHours = item.coolingHours,
            status = item.status.name,
            resolvedAt = item.resolvedAt,
        )
    }
}
