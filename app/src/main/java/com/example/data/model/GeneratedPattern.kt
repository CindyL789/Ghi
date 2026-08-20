package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

data class GeneratedPattern(
    val title: String,
    val description: String,
    val category: String = "Pattern",
    val skillLevel: String = "Intermediate",
    val hookSize: String = "4.0mm (G/6)",
    val yarnWeight: String = "Worsted (Weight 4)",
    val estimatedHours: Int = 3,
    val stitchesUsed: List<String> = listOf("sc", "ch", "sl st"),
    val patternNotes: String = "",
    val instructions: List<PatternStepInstruction> = emptyList(),
    @DrawableRes val sampleImageRes: Int? = null,
    val generatedAt: Long = System.currentTimeMillis()
)

data class PatternStepInstruction(
    val roundOrRow: Int,
    val instruction: String,
    val stitchCount: Int = 0
)
