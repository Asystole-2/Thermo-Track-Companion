package com.example.thermotrackcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.thermotrackcompanion.data.AlertEntity
import com.example.thermotrackcompanion.data.ThermoTrackRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AlertsUiState(
    val searchQuery: String = "",
    val filteredAlerts: List<AlertEntity> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class AlertsViewModel(private val repository: ThermoTrackRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<AlertsUiState> = _searchQuery
        .debounce(300) // Debounce search input (performance optimization)
        .flatMapLatest { query ->
            // Use search query if present, otherwise fetch all alerts
            if (query.isBlank()) {
                repository.getAllAlerts()
            } else {
                // Pass query with wildcards to the DAO for SQL LIKE search
                repository.searchAlerts("%$query%")
            }
        }
        .map { alerts ->
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
        _searchQuery.value = query
    }

    // Deletes an alert using the repository
    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }

    // Function to pre-populate mock data for testing Room DB and search feature
    init {
        viewModelScope.launch {
            repository.insertAlert(AlertEntity(type = "Motion", description = "Motion detected in main area.", timestamp = "2025-12-03 15:30:00"))
            repository.insertAlert(AlertEntity(type = "Temp Spike", description = "Abnormal temp reading 28.5°C.", timestamp = "2025-12-03 14:00:00"))
            repository.insertAlert(AlertEntity(type = "System", description = "Sensor check initiated successfully.", timestamp = "2025-12-03 13:00:00"))
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