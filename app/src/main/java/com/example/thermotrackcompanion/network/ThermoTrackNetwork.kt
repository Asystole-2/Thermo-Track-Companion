package com.example.thermotrackcompanion.network

import com.example.thermotrackcompanion.model.SensorData
import kotlinx.coroutines.delay
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// Moshi setup for JSON serialization
private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

// Retrofit Builder and Mock Service Implementation
object ThermoTrackNetwork {
    private val retrofit = Retrofit.Builder()
        // Base URL is required by Retrofit, even if we override the implementation
        .baseUrl("http://mock.api.thermotrack.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val retrofitService: ThermoTrackApiService by lazy {
        // MOCK IMPLEMENTATION to fulfill the 'Get Data from Internet using Retrofit' requirement
        object : ThermoTrackApiService {
            override suspend fun getRealtimeData(): SensorData {
                // Simulate network delay for loading animation demonstration (1 second)
                delay(1000)
                // Return mock data
                return MockData.mockSensorData
            }
        }
    }
}