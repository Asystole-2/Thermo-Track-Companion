package com.example.thermotrackcompanion.data

import android.content.Context
import com.example.thermotrackcompanion.network.ThermoTrackNetwork
import com.example.thermotrackcompanion.network.ThermoTrackApiService

class DefaultAppContainer(private val context: Context) : AppContainer {

    // Initialize API Service
    private val apiService: ThermoTrackApiService by lazy {
        ThermoTrackNetwork.retrofitService
    }

    // Initialize Data Access Objects
    private val alertDao by lazy {
        // ThermoTrackDatabase is defined below
        ThermoTrackDatabase.getDatabase(context).alertDao()
    }

    // Initialize DataStore Manager
    private val dataStoreManager by lazy {
        // DataStoreManager is defined below
        DataStoreManager(context)
    }

    // Implement the Repository using all initialized dependencies
    override val repository: ThermoTrackRepository by lazy {
        DefaultThermoTrackRepository(
            apiService = apiService,
            alertDao = alertDao,
            dataStoreManager = dataStoreManager
        )
    }
}