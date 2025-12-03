package com.example.thermotrackcompanion


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.thermotrack.companion.data.AppContainer
import com.thermotrack.companion.data.DefaultAppContainer
import com.thermotrack.companion.ui.theme.ThermoTrackCompanionTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private lateinit var container: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Timber for Logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        container = DefaultAppContainer(this)

        setContent {
            ThermoTrackCompanionTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Start of Navigation
                    ThermoTrackApp(container)
                }
            }
        }
    }
}}