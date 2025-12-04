package com.example.thermotrackcompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room Entity for storing historical alerts
@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String, // e.g., "Motion", "High Temp", "System"
    val description: String,
    val timestamp: String // Used for sorting and display
)