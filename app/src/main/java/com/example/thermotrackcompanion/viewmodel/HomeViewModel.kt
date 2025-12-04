package com.example.thermotrackcompanion.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.thermotrackcompanion.NotificationHelper
import com.example.thermotrackcompanion.data.AlertEntity
import com.example.thermotrackcompanion.data.ThermoTrackRepository
import com.example.thermotrackcompanion.model.HardwareGuide
import com.example.thermotrackcompanion.model.SensorData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val data: SensorData) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: ThermoTrackRepository,
    private val application: Application // Required for NotificationHelper
) : ViewModel() {

    // Initial StateFlow (will be updated in fetchRealtimeData)
    var uiState: StateFlow<HomeUiState> = repository.getTemperatureUnit()
        .map { HomeUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )

    // Flow for Temperature Unit (Extra Feature 1)
    val tempUnit: StateFlow<String> = repository.getTemperatureUnit().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "C"
    )

    init {
        fetchRealtimeData()
    }

    fun fetchRealtimeData() {
        viewModelScope.launch {
            uiState = repository.getTemperatureUnit().map { unit ->
                try {
                    val data = repository.getRealtimeData()

                    // Critical Alert Logic (Extra Feature 3: Local Notification & Room DB insert)
                    if (data.motionDetected || data.temperatureC > 30.0) { // Example trigger
                        val alertType = if (data.motionDetected) "Motion" else "High Temp"
                        val alertDesc = if (data.motionDetected) "Motion detected!" else "Temperature critical: ${data.temperatureC}°C"

                        val alert = AlertEntity(type = alertType, description = alertDesc, timestamp = data.lastUpdated)
                        repository.insertAlert(alert)
                        NotificationHelper.triggerCriticalAlert(application.applicationContext, alertDesc)
                    }

                    HomeUiState.Success(data)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to fetch realtime data")
                    HomeUiState.Error("Failed to fetch data: ${e.message}")
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading
            )
        }
    }

    fun getHardwareGuides(): List<HardwareGuide> = repository.getHardwareGuides()

    companion object {
        // Factory is required for ViewModels that need application context or arguments
        fun Factory(repository: ThermoTrackRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                HomeViewModel(
                    repository = repository,
                    application = application
                )
            }
        }
    }
}