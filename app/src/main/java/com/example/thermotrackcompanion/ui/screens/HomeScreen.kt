package com.example.thermotrackcompanion.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thermotrackcompanion.model.SensorData
import com.example.thermotrackcompanion.viewmodel.HomeUiState
import com.example.thermotrackcompanion.viewmodel.HomeViewModel
import com.example.thermotrackcompanion.viewmodel.SystemStatus
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tempUnit by viewModel.tempUnit.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ThermoTrack Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToAlerts) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Alerts History")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        // Floating action button for manual refresh (Interactable Element)
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::fetchRealtimeData) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh Data")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Greeting Display
            WelcomeGreeting()

            // Companion App Card (Description & Advertising)
            CompanionAppCard(onNavigateToAbout)

            Spacer(Modifier.height(16.dp))

            // Animated Content for Loading/Success/Error State (Animation)
            AnimatedContent(targetState = uiState, label = "HomeStateAnimation") { state ->
                when (state) {
                    is HomeUiState.Loading -> LoadingState()
                    is HomeUiState.Success -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            SystemStatusBadge(state.systemStatus)
                            Spacer(Modifier.height(16.dp))
                            CurrentDataDisplay(state.data, tempUnit)
                            Spacer(Modifier.height(16.dp))
                            QuickNavigationRow(onNavigateToGuide, onNavigateToAlerts)
                        }
                    }
                    is HomeUiState.Error -> ErrorState(state.message, onRetry = viewModel::fetchRealtimeData)
                }
            }

            Spacer(Modifier.height(16.dp))
            // Display Unit Preference (DataStore Integration Display)
            Text(
                text = "Unit Preference: $tempUnit",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WelcomeGreeting() {
    val currentHour = LocalTime.now().hour
    val greeting = when (currentHour) {
        in 5..11 -> "Good Morning!"
        in 12..16 -> "Good Afternoon!"
        else -> "Good Evening!"
    }

    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp)) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Welcome to your room monitoring dashboard.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Companion App Description Card (Interactable Element)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionAppCard(onNavigateToAbout: () -> Unit) {
    Card(
        onClick = onNavigateToAbout,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.MonitorHeart,
                contentDescription = "Companion Info",
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(36.dp).padding(end = 8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ThermoTrack Management Platform",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "View full room management features.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }

            // "More" Button
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "More Info",
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}
@Composable
fun SystemStatusBadge(status: SystemStatus) {
    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = status.color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = "System Status",
                tint = status.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(status.title, style = MaterialTheme.typography.titleMedium.copy(color = status.color))
                Text(status.details, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


// Enhanced Data Display with Gauges and Cards
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentDataDisplay(data: SensorData, unit: String) {
    val tempC = data.temperatureC

    // Convert temperature for display and gauge max based on user unit
    val tempMax = if (unit == "F") 100f else 40f
    val tempCurrent = if (unit == "F") (tempC * 9 / 5 + 32).toFloat() else tempC.toFloat()

    val currentHumidity = data.humidityPercent.toFloat()

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Current Readings", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Temperature Visual (Gauge)
                GaugeIndicator(
                    value = tempCurrent,
                    maxValue = tempMax,
                    label = "Temperature",
                    unitLabel = "°$unit",
                    icon = Icons.Filled.Thermostat, // Resolved
                    modifier = Modifier.weight(1f).aspectRatio(1f)
                )

                Spacer(Modifier.width(16.dp))

                // Humidity Visual (Gauge)
                GaugeIndicator(
                    value = currentHumidity,
                    maxValue = 100f,
                    label = "Humidity",
                    unitLabel = "%",
                    icon = Icons.Filled.WaterDrop, // Resolved
                    modifier = Modifier.weight(1f).aspectRatio(1f)
                )
            }

            Spacer(Modifier.height(16.dp))
            // Motion Status (Fixed to Clear as per API simplification)
            Text(
                "Motion Status: Clear",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(Modifier.height(8.dp))
            Text("Last Update: ${data.lastUpdated}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// Gauge Indicator (Visuals/Animation)
@Composable

fun GaugeIndicator(
    value: Float,
    maxValue: Float,
    label: String,
    unitLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    // Animation for sweep angle
    val sweepAngle = animateFloatAsState(targetValue = (value / maxValue).coerceIn(0f, 1f) * 270f, label = "gaugeAnimation").value
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    // Custom color logic for urgency (matches ViewModel status logic)
    val indicatorColor = when {
        // Temperature warning zones (approximated for F)
        label == "Temperature" && (value > 25 && unitLabel == "°C" || value > 77 && unitLabel == "°F") -> Color.Red
        // Humidity exceeding 70% threshold
        label == "Humidity" && value > 70f -> Color(0xFF00BCD4)
        else -> MaterialTheme.colorScheme.primary
    }

    // Ensure value is displayed correctly even if data is null/default (0.0)
    val displayValue = if (value < 0.1f) 0.0f else value

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Background track
        Canvas(modifier = Modifier.fillMaxSize(0.8f)) {
            drawArc(
                color = backgroundColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )
            // Animated progress arc
            drawArc(
                color = indicatorColor,
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Center content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = indicatorColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(
                text = String.format("%.1f", displayValue) + unitLabel,
                style = MaterialTheme.typography.titleLarge,
                color = indicatorColor
            )
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun QuickNavigationRow(onNavigateToGuide: () -> Unit, onNavigateToAlerts: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        NavigationButton(
            title = "Alerts History",
            onClick = onNavigateToAlerts,
            icon = Icons.Filled.Notifications,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        NavigationButton(
            title = "Hardware Guide",
            onClick = onNavigateToGuide,
            icon = Icons.Filled.Info,
            modifier = Modifier.weight(1f)
        )
    }
}

// Re-used existing NavigationButton, adding modifier parameter
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationButton(title: String, onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    OutlinedCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun LoadingState() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp)) {
        CircularProgressIndicator()
        Text("Fetching live data...", modifier = Modifier.padding(top = 16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Card(onClick = onRetry, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Connection Error", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
            Text(message, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 8.dp))
            Button(onClick = onRetry) {
                Text("Retry Connection")
            }
        }
    }
}