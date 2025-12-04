package com.example.thermotrackcompanion.model

// Data fetched from the mocked API (Retrofit)
data class SensorData(
    val temperatureC: Double,
    val humidityPercent: Double,
    val lastUpdated: String,
    val motionDetected: Boolean
)

// Data model for the Hardware Guide list
data class HardwareGuide(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String // Coil loads this image
)