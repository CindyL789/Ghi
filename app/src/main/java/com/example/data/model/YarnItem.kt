package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "yarn_stash")
data class YarnItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val colorway: String,
    val weight: String = "Worsted (4)", // Lace (0), Super Fine (1), Fine (2), Light/DK (3), Worsted (4), Bulky (5), Super Bulky (6)
    val skeins: Float = 1.0f,
    val gramsPerSkein: Int = 100,
    val yardsPerSkein: Int = 200,
    val fiberContent: String = "100% Wool",
    val colorHex: String = "#E07A5F",
    val notes: String = "",
    val lotNumber: String = ""
)
