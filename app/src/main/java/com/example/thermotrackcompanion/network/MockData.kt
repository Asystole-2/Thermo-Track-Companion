package com.example.thermotrackcompanion.network

import com.example.thermotrackcompanion.R
import com.example.thermotrackcompanion.model.HardwareGuide
import com.example.thermotrackcompanion.model.SensorData

object MockData {

    val mockSensorData = SensorData(
        temperatureC = 23.5,
        humidityPercent = 65.2,
        lastUpdated = "2025-12-03 15:45:00",
        motionDetected = true
    )

    val mockHardwareGuides = listOf(
        HardwareGuide(
            id = 1,
            name = "Raspberry Pi 4",
            description = "The core processing unit for the UDP system. Handles sensor polling and network communication.",
            imageRes = R.drawable.pi
        ),
        HardwareGuide(
            id = 2,
            name = "DHT22 Sensor",
            description = "Measures temperature and humidity (core component).",
            imageRes = R.drawable.dht22
        ),
        HardwareGuide(
            id = 3,
            name = "PIR Motion Sensor",
            description = "Detects movement in the monitored area (core component).",
            imageRes = R.drawable.pir
        ),
        HardwareGuide(
            id = 4,
            name = "Fritzing Wiring Diagram",
            description = "Refer to the Fritzing files provided in the UDP project for detailed wiring.",
            imageRes = R.drawable.frit
        )
    )
}
