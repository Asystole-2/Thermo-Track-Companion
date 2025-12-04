package com.example.thermotrackcompanion.network

import com.example.thermotrackcompanion.model.SensorData
import retrofit2.http.GET

// This interface defines the contract for our API calls (even if mocked)
interface ThermoTrackApiService {
    @GET("realtime-data")
    suspend fun getRealtimeData(): SensorData
}