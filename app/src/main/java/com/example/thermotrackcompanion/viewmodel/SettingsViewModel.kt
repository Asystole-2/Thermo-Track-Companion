package com.example.thermotrackcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.thermotrackcompanion.data.ThermoTrackRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsViewModel(private val repository: ThermoTrackRepository) : ViewModel() {

    // Flow to expose the current temperature unit from DataStore
    val tempUnit: StateFlow<String> = repository.getTemperatureUnit().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "C"
    )

    fun saveTemperatureUnit(unit: String) {
        viewModelScope.launch {
            Timber.d("Saving temperature unit: $unit")
            repository.saveTemperatureUnit(unit)
        }
    }

    companion object {
        val Factory: (ThermoTrackRepository) -> ViewModelProvider.Factory = { repository ->
            viewModelFactory {
                initializer { SettingsViewModel(repository) }
            }
        }
    }
}