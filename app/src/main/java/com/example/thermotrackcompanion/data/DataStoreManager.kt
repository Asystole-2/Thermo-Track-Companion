package com.example.thermotrackcompanion.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Key for storing the temperature unit (C or F)
private val TEMP_UNIT_KEY = stringPreferencesKey("temp_unit")

// Singleton DataStore instance tied to the application context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    // Flow to read the saved temperature unit. Defaults to "C".
    val temperatureUnit: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[TEMP_UNIT_KEY] ?: "C"
        }

    // Suspend function to save the new temperature unit
    suspend fun saveTemperatureUnit(unit: String) {
        context.dataStore.edit { settings ->
            settings[TEMP_UNIT_KEY] = unit
        }
    }
}