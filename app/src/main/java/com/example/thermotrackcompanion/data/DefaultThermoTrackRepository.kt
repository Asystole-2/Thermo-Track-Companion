package com.example.thermotrackcompanion.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.thermotrackcompanion.model.HardwareGuide
import com.example.thermotrackcompanion.model.SensorData
import com.example.thermotrackcompanion.network.MockData
import com.example.thermotrackcompanion.network.ThermoTrackApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
class DefaultThermoTrackRepository(
    private val apiService: ThermoTrackApiService,
    private val alertDao: AlertDao,
    private val dataStoreManager: DataStoreManager
) : ThermoTrackRepository {
    private var currentMockState: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)

    private val normalData = SensorData(
        temperatureC = 20.5,
        humidityPercent = 55.0,
        lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
        motionDetected = false
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private val warningData = SensorData(
        temperatureC = 26.2,
        humidityPercent = 75.8,
        lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
        motionDetected = false
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getRealtimeData(): SensorData {
        // --- START SIMULATION LOGIC ---
        // 1. Simulate network delay to show the loading spinner (Animation requirement)
        delay(800)

        // 2. Toggle the state
        currentMockState = 1 - currentMockState

        // 3. Return the toggled data set
        val data = if (currentMockState == 1) {
            normalData.copy(lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        } else {
            warningData.copy(lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        }

        Timber.d("SIMULATION: Returning State $currentMockState: T=${data.temperatureC}°C")
        return data
        // --- END SIMULATION LOGIC ---

        /* // --- REAL API CALL (COMMENTED OUT FOR SIMULATION) ---
        return try {
            val data = apiService.getRealtimeData()
            Timber.d("Fetched Realtime Data: $data")
            data
        } catch (e: Exception) {
            Timber.e(e, "Error fetching realtime data from API")
            SensorData(0.0, 0.0, "N/A", false)
        }
        // ---------------------------------------------------
        */
    }

    override fun getHardwareGuides(): List<HardwareGuide> = MockData.mockHardwareGuides

    // DataStore delegation
    override fun getTemperatureUnit(): Flow<String> = dataStoreManager.temperatureUnit
    override suspend fun saveTemperatureUnit(unit: String) = dataStoreManager.saveTemperatureUnit(unit)

    // Room DB delegation
    override fun getAllAlerts(): Flow<List<AlertEntity>> = alertDao.getAllAlerts()
    override fun searchAlerts(query: String): Flow<List<AlertEntity>> = alertDao.searchAlerts(query)
    override suspend fun insertAlert(alert: AlertEntity) = alertDao.insert(alert)

    //dark mode
    override fun getDarkMode(): Flow<Boolean> =
        dataStoreManager.darkMode

    override suspend fun saveDarkMode(enabled: Boolean) =
        dataStoreManager.saveDarkMode(enabled)

    override suspend fun deleteAlert(alert: AlertEntity) {
        alertDao.delete(alert)
    }

}