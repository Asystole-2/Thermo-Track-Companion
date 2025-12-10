package com.example.thermotrackcompanion.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

// Extended state to include status information
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val data: SensorData, val systemStatus: SystemStatus) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

// Data class to easily pass system status (for new badge feature)
data class SystemStatus(
    val title: String,
    val color: Color,
    val details: String
)

class HomeViewModel(
    private val repository: ThermoTrackRepository,
    private val application: Application
) : ViewModel() {

    // 🛠️ FIX 1: Use MutableStateFlow for explicit state control
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val tempUnit: StateFlow<String> = repository.getTemperatureUnit().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "C"
    )

    init {
        fetchRealtimeData()
    }

    private fun calculateSystemStatus(tempC: Double): SystemStatus {
        val criticalMax = 28.0
        val warningMax = 25.0
        val idealMin = 18.0

        return when {
            tempC >= criticalMax -> SystemStatus("CRITICAL OVERHEAT", Color.Red, "Immediate action required. Temperature is above 28°C.")
            tempC >= warningMax -> SystemStatus("WARNING: High Temp", Color(0xFFFF9800), "Temperature is rising above ideal range (25°C).")
            tempC < idealMin -> SystemStatus("LOW TEMP WARNING", Color(0xFF00BCD4), "Temperature is below ideal range (18°C).")
            else -> SystemStatus("OPERATING NORMALLY", Color.Green, "Temperature is stable within optimal range.")
        }
    }

    fun fetchRealtimeData() {
        viewModelScope.launch {
            // 🛠️ FIX 2: Explicitly emit LOADING state immediately when refresh is clicked
            _uiState.value = HomeUiState.Loading

            try {
                // Wait for the temperature unit to determine potential alert levels
                val unit = tempUnit.value

                // This call includes the 800ms delay and data toggling simulation
                val data = repository.getRealtimeData()

                val systemStatus = calculateSystemStatus(data.temperatureC)

                // Critical Alert Logic
                if (systemStatus.title.contains("CRITICAL")) {
                    val alert = AlertEntity(type = "CRITICAL", description = systemStatus.details, timestamp = data.lastUpdated)
                    repository.insertAlert(alert)
                    NotificationHelper.triggerCriticalAlert(application.applicationContext, systemStatus.title)
                }

                // 🛠️ FIX 3: Emit SUCCESS state after asynchronous work completes
                _uiState.value = HomeUiState.Success(data, systemStatus)

            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch realtime data")
                // Emit ERROR state on failure
                _uiState.value = HomeUiState.Error("Failed to fetch data: Check server connectivity.")
            }
        }
    }

    fun getHardwareGuides(): List<HardwareGuide> = repository.getHardwareGuides()

    companion object {
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