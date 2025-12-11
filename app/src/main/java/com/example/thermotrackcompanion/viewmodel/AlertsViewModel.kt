package com.example.thermotrackcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.thermotrackcompanion.data.AlertEntity
import com.example.thermotrackcompanion.data.ThermoTrackRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

data class AlertsUiState(
    val searchQuery: String = "",
    val filteredAlerts: List<AlertEntity> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class AlertsViewModel(private val repository: ThermoTrackRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    // Exposed flow for the search query to be observed by the UI
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Reactive stream combining search query and database access
    val uiState: StateFlow<AlertsUiState> = _searchQuery
        .debounce(150) // Reduced from 300ms to 150ms for better responsiveness
        .flatMapLatest { query ->
            // Decide whether to fetch all alerts or filtered alerts
            if (query.isBlank()) {
                repository.getAllAlerts()
            } else {
                // Call the search function which uses SQL LIKE %query%
                repository.searchAlerts("%$query%")
            }
        }
        .map { alerts ->
            // Map the resulting alert list into the UI state
            AlertsUiState(
                searchQuery = _searchQuery.value,
                filteredAlerts = alerts
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AlertsUiState()
        )

    fun setSearchQuery(query: String) {
        // Update the source of the search stream
        _searchQuery.value = query
    }

    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
            Timber.d("Deleted alert with ID: ${alert.id}")
        }
    }

    // Function to pre-populate mock data for testing Room DB and search feature
    init {
        viewModelScope.launch {
            // Clear existing alerts first to avoid duplicates
//            repository.clearAllAlerts()

            // Insert new mock data
            repository.insertAlert(AlertEntity(type = "Motion", description = "Motion detected in main area.", timestamp = "2025-12-03 15:30:00"))
            repository.insertAlert(AlertEntity(type = "Temp Spike", description = "Abnormal temp reading 28.5°C.", timestamp = "2025-12-03 14:00:00"))
            repository.insertAlert(AlertEntity(type = "System", description = "Sensor check initiated successfully.", timestamp = "2025-12-03 13:00:00"))
            repository.insertAlert(AlertEntity(type = "Motion", description = "Motion detected at back door.", timestamp = "2025-12-03 12:45:00"))
            repository.insertAlert(AlertEntity(type = "Temp Low", description = "Temperature dropped below threshold: 15.2°C.", timestamp = "2025-12-03 11:30:00"))
            repository.insertAlert(AlertEntity(type = "Humidity", description = "High humidity detected: 85%.", timestamp = "2025-12-03 10:15:00"))

            Timber.d("Mock alert data initialized")
        }
    }

    companion object {
        val Factory: (ThermoTrackRepository) -> ViewModelProvider.Factory = { repository ->
            viewModelFactory {
                initializer { AlertsViewModel(repository) }
            }
        }
    }
}