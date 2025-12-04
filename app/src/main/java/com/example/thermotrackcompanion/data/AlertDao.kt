package com.example.thermotrackcompanion.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Data Access Object for Room database operations
@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    // Query for Extra Feature: Search functionality
    @Query("SELECT * FROM alerts WHERE type LIKE :query OR description LIKE :query ORDER BY timestamp DESC")
    fun searchAlerts(query: String): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity)
}