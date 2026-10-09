package com.wafeer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wafeer.app.domain.model.smart.MicroGoal
import java.math.BigDecimal

@Entity(tableName = "micro_goals")
data class MicroGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetAmount: String,
    val savedAmount: String = "0",
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
) {
    fun toDomain(): MicroGoal = MicroGoal(
        id = id,
        title = title,
        targetAmount = BigDecimal(targetAmount),
        savedAmount = BigDecimal(savedAmount),
        createdAt = createdAt,
        isCompleted = isCompleted,
    )

    companion object {
        fun fromDomain(goal: MicroGoal): MicroGoalEntity = MicroGoalEntity(
            id = goal.id,
            title = goal.title,
            targetAmount = goal.targetAmount.toPlainString(),
            savedAmount = goal.savedAmount.toPlainString(),
            createdAt = goal.createdAt,
            isCompleted = goal.isCompleted,
        )
    }
}
