package com.example.thermotrackcompanion.data

import com.example.thermotrackcompanion.model.HardwareGuide
import com.example.thermotrackcompanion.model.SensorData
import kotlinx.coroutines.flow.Flow

// Central interface for all data operations (Network, Room, DataStore)
interface ThermoTrackRepository {
    // Network/Retrofit
    suspend fun getRealtimeData(): SensorData

    // Mock Hardware Data
    fun getHardwareGuides(): List<HardwareGuide>

    // DataStore (Extra Feature 1)
    fun getTemperatureUnit(): Flow<String>
    suspend fun saveTemperatureUnit(unit: String)

    // Room DB (Alerts History)
    fun getAllAlerts(): Flow<List<AlertEntity>>
    fun searchAlerts(query: String): Flow<List<AlertEntity>>
    suspend fun insertAlert(alert: AlertEntity)
    // dark mode
    fun getDarkMode(): Flow<Boolean>
    suspend fun saveDarkMode(enabled: Boolean)
    suspend fun deleteAlert(alert: AlertEntity)
}