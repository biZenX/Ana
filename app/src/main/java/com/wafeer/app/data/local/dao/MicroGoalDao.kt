package com.wafeer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wafeer.app.data.local.entity.MicroGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MicroGoalDao {
    @Query("SELECT * FROM micro_goals ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllGoals(): Flow<List<MicroGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: MicroGoalEntity): Long

    @Update
    suspend fun updateGoal(goal: MicroGoalEntity)

    @Delete
    suspend fun deleteGoal(goal: MicroGoalEntity)

    @Query("DELETE FROM micro_goals WHERE id = :id")
    suspend fun deleteGoalById(id: Long)
}
