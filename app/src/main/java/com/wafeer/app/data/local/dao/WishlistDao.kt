package com.wafeer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wafeer.app.data.local.entity.WishlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items ORDER BY CASE status WHEN 'COOLING' THEN 0 WHEN 'PURCHASED' THEN 1 ELSE 2 END ASC, createdAt DESC")
    fun getAllItems(): Flow<List<WishlistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WishlistItemEntity): Long

    @Update
    suspend fun updateItem(item: WishlistItemEntity)

    @Delete
    suspend fun deleteItem(item: WishlistItemEntity)

    @Query("DELETE FROM wishlist_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)
}
