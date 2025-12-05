package com.example.thermotrackcompanion.data

import com.example.thermotrackcompanion.model.HardwareGuide
import com.example.thermotrackcompanion.model.SensorData
import com.example.thermotrackcompanion.network.MockData
import com.example.thermotrackcompanion.network.ThermoTrackApiService
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class DefaultThermoTrackRepository(
    private val apiService: ThermoTrackApiService,
    private val alertDao: AlertDao,
    private val dataStoreManager: DataStoreManager
) : ThermoTrackRepository {

    override suspend fun getRealtimeData(): SensorData {
        return try {
            val data = apiService.getRealtimeData()
            Timber.d("Fetched Realtime Data: $data")
            data
        } catch (e: Exception) {
            Timber.e(e, "Error fetching realtime data from API")
            // Default/error state on failure
            SensorData(0.0, 0.0, "N/A", false)
        }
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

}