package com.example.thermotrackcompanion.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thermotrackcompanion.model.SensorData
import com.example.thermotrackcompanion.viewmodel.HomeUiState
import com.example.thermotrackcompanion.viewmodel.HomeViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToAlerts: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tempUnit by viewModel.tempUnit.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ThermoTrack Status") },
                actions = {
                    IconButton(onClick = onNavigateToAlerts) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Alerts History")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Content for Loading/Success/Error State (Animation)
            AnimatedContent(targetState = uiState, label = "HomeStateAnimation") { state ->
                when (state) {
                    is HomeUiState.Loading -> LoadingState()
                    is HomeUiState.Success ->
                        CurrentDataCard(state.data, tempUnit)
                    is HomeUiState.Error -> ErrorState(state.message, onRetry = viewModel::fetchRealtimeData)
                }
            }

            Spacer(Modifier.height(32.dp))

            // Navigation Buttons
            NavigationButton(
                title = "Alerts History",
                onClick = onNavigateToAlerts,
                icon = Icons.Filled.Notifications
            )
            Spacer(Modifier.height(16.dp))
            NavigationButton(
                title = "Hardware Guide",
                onClick = onNavigateToGuide,
                icon = Icons.Filled.Info
            )

            // Display Unit Preference (DataStore integration display)
            Text(
                text = "Data displayed in: $tempUnit",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentDataCard(data: SensorData, unit: String) {
    // Extra Feature 1: Temp Conversion & Unit Display
    val temperature = if (unit == "F") {
        String.format("%.1f °F", data.temperatureC * 9 / 5 + 32)
    } else {
        String.format("%.1f °C", data.temperatureC)
    }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(24.dp)) {
            Text("Current Status", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            Text("Temperature: $temperature", style = MaterialTheme.typography.titleLarge)
            Text("Humidity: ${data.humidityPercent.toInt()}%", style = MaterialTheme.typography.titleLarge)

            // Highlight motion status based on reading
            Text("Motion: ${if (data.motionDetected) "DETECTED" else "Clear"}", style = MaterialTheme.typography.titleMedium,
                color = if (data.motionDetected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary)

            Spacer(Modifier.height(8.dp))
            Text("Last Updated: ${data.lastUpdated}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun LoadingState() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator()
        Text("Fetching realtime data...")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Card(onClick = onRetry) {
        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Error Loading Data", color = MaterialTheme.colorScheme.error)
            Text(message, style = MaterialTheme.typography.bodySmall)
            Button(onClick = onRetry) {
                Text("Retry Connection")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationButton(title: String, onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    OutlinedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}