package com.example.trab3.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diet_table")
data class EntityDiet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "weight") val weight: Double,
    @ColumnInfo(name = "height") val height: Double,
    @ColumnInfo(name = "calories") val calories: Double,
    @ColumnInfo(name = "protein") val protein: Int,
    @ColumnInfo(name = "carbs") val carbs: Int,
    @ColumnInfo(name = "fat") val fat: Int
)
