package com.example.thermotrackcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.thermotrackcompanion.data.AlertEntity
import com.example.thermotrackcompanion.data.ThermoTrackRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

data class AlertsUiState(
    val searchQuery: String = "",
    val filteredAlerts: List<AlertEntity> = emptyList()
)

class AlertsViewModel(private val repository: ThermoTrackRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    // All alerts from DB as a flow
    private val alertsFlow: Flow<List<AlertEntity>> = repository.getAllAlerts()

    // Combine searchQuery + DB alerts into UI state
    val uiState: StateFlow<AlertsUiState> =
        combine(_searchQuery, alertsFlow) { query, alerts ->

            val filtered = if (query.isBlank()) {
                alerts
            } else {
                alerts.filter { alert ->
                    alert.type.contains(query, ignoreCase = true) ||
                            alert.description.contains(query, ignoreCase = true)
                }
            }

            AlertsUiState(
                searchQuery = query,
                filteredAlerts = filtered
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AlertsUiState()
        )

    fun setSearchQuery(query: String) {
        Timber.d("Search query updated: $query")
        _searchQuery.value = query
    }

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
