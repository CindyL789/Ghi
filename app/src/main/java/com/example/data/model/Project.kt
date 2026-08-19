package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String = "Blanket", // Amigurumi, Wearables, Blankets, Accessories, Home
    val status: String = "Active", // Active, On Hold, Completed
    val currentRow: Int = 1,
    val targetRows: Int = 40,
    val currentStitchInRow: Int = 0,
    val targetStitchesInRow: Int = 0,
    val repeatCount: Int = 1,
    val targetRepeats: Int = 1,
    val hookSize: String = "4.0 mm (G-6)",
    val yarnDetails: String = "Worsted Weight / Warm Terracotta",
    val notes: String = "",
    val patternText: String = "",
    val currentPatternStepIndex: Int = 0,
    val totalMinutesSpent: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    val progressFraction: Float
        get() = if (targetRows > 0) (currentRow.toFloat() / targetRows).coerceIn(0f, 1f) else 0f
}
