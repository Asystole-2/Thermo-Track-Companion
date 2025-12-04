package com.example.thermotrackcompanion.network

import com.example.thermotrackcompanion.model.HardwareGuide
import com.example.thermotrackcompanion.model.SensorData

object MockData {
    val mockSensorData = SensorData(
        temperatureC = 23.5,
        humidityPercent = 65.2,
        lastUpdated = "2025-12-03 15:45:00",
        motionDetected = true
    )

    // Using placeholder images. In a real project, these would be hosted.
    val mockHardwareGuides = listOf(
        HardwareGuide(1, "Raspberry Pi 4", "The core processing unit for the UDP system. Handles sensor polling and network communication.", "https://picsum.photos/seed/rpi/200"),
        HardwareGuide(2, "DHT22 Sensor", "Measures temperature and humidity (core component).", "https://picsum.photos/seed/dht22/200"),
        HardwareGuide(3, "PIR Motion Sensor", "Detects movement in the monitored area (core component).", "https://picsum.photos/seed/pir/200"),
        HardwareGuide(4, "Fritzing Wiring Diagram", "Refer to the Fritzing files provided in the UDP project for detailed wiring.", "https://picsum.photos/seed/wiring/200")
    )
}