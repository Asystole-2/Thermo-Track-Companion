package com.example.thermotrackcompanion // CORRECTED: Your confirmed root package

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.thermotrackcompanion.data.AppContainer // CORRECTED Import
import com.example.thermotrackcompanion.data.DefaultAppContainer // CORRECTED Import
import com.example.thermotrackcompanion.ui.theme.ThermoTrackCompanionTheme
import com.example.thermotrackcompanion.NotificationHelper // CORRECTED Import
import com.example.thermotrackcompanion.ThermoTrackApp // CORRECTED Import
import timber.log.Timber
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
class MainActivity : ComponentActivity() {
    private lateinit var container: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        //Splash Screen API
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Initialize Timber for Logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        container = DefaultAppContainer(this)
        NotificationHelper.createNotificationChannel(this)

        setContent {
            ThermoTrackCompanionTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Start of Navigation
                    ThermoTrackApp(container)
                }
            }
        }
    }
}