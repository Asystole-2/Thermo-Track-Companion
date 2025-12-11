package com.example.thermotrackcompanion.ui.screens

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
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
                .verticalScroll(rememberScrollState())  // Make the screen scrollable
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
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
    val tempMax = if (unit == "F") 100f else 40f
    val tempCurrent = if (unit == "F") (tempC * 9 / 5 + 32).toFloat() else tempC.toFloat()
    val currentHumidity = data.humidityPercent.toFloat()

    // Use LocalConfiguration for screen size detection
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isWideScreen = screenWidth >= 600

    // Different styling for wide screens vs compact screens
    if (isWideScreen) {
        // WIDE SCREEN MODE: Circular card with side-by-side gauges
        Card(
            shape = CircleShape,
            modifier = Modifier
                .size(420.dp)
                .wrapContentWidth(Alignment.CenterHorizontally),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Current Readings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Side-by-side gauges in the center
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularGaugeIndicator(
                        value = tempCurrent,
                        maxValue = tempMax,
                        label = "Temperature",
                        unitLabel = "°$unit",
                        icon = Icons.Filled.Thermostat,
                        modifier = Modifier.size(140.dp)
                    )

                    CircularGaugeIndicator(
                        value = currentHumidity,
                        maxValue = 100f,
                        label = "Humidity",
                        unitLabel = "%",
                        icon = Icons.Filled.WaterDrop,
                        modifier = Modifier.size(140.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Motion: Clear",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Updated: ${data.lastUpdated}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        // COMPACT SCREEN MODE: Original rectangular card, vertical stacking
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current Readings",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(16.dp))

                // Vertical stacking for compact screens
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CompactGaugeIndicator(
                        value = tempCurrent,
                        maxValue = tempMax,
                        label = "Temperature",
                        unitLabel = "°$unit",
                        icon = Icons.Filled.Thermostat,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .aspectRatio(1f)
                    )
                    Spacer(Modifier.height(16.dp))
                    CompactGaugeIndicator(
                        value = currentHumidity,
                        maxValue = 100f,
                        label = "Humidity",
                        unitLabel = "%",
                        icon = Icons.Filled.WaterDrop,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .aspectRatio(1f)
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Motion: Clear",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Updated: ${data.lastUpdated}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Circular Gauge Indicator for wide screen mode
@Composable
fun CircularGaugeIndicator(
    value: Float,
    maxValue: Float,
    label: String,
    unitLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    val sweepAngle = animateFloatAsState(
        targetValue = (value / maxValue).coerceIn(0f, 1f) * 360f,
        label = "circularGaugeAnimation"
    ).value

    // Get colors outside of Canvas scope
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val indicatorColor = when {
        label == "Temperature" && (value > 25 && unitLabel == "°C" || value > 77 && unitLabel == "°F") -> Color.Red
        label == "Humidity" && value > 70f -> Color(0xFF00BCD4)
        else -> MaterialTheme.colorScheme.primary
    }

    val displayValue = if (value < 0.1f) 0.0f else value

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Background circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = backgroundColor,
                radius = size.minDimension / 2.2f
            )
        }

        // Progress ring
        Canvas(modifier = Modifier.fillMaxSize(0.9f)) {
            drawArc(
                color = indicatorColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Center content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = indicatorColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = String.format("%.1f", displayValue) + unitLabel,
                style = MaterialTheme.typography.headlineSmall,
                color = indicatorColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Compact version of GaugeIndicator for compact screens
@Composable
fun CompactGaugeIndicator(
    value: Float,
    maxValue: Float,
    label: String,
    unitLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    val sweepAngle = animateFloatAsState(
        targetValue = (value / maxValue).coerceIn(0f, 1f) * 270f,
        label = "gaugeAnimation"
    ).value

    // Get colors outside of Canvas scope
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val indicatorColor = when {
        label == "Temperature" && (value > 25 && unitLabel == "°C" || value > 77 && unitLabel == "°F") -> Color.Red
        label == "Humidity" && value > 70f -> Color(0xFF00BCD4)
        else -> MaterialTheme.colorScheme.primary
    }

    val displayValue = if (value < 0.1f) 0.0f else value

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize(0.75f)) {
            drawArc(
                color = backgroundColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = indicatorColor,
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null,
                tint = indicatorColor,
                modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(2.dp))
            Text(
                text = String.format("%.1f", displayValue) + unitLabel,
                style = MaterialTheme.typography.titleMedium,
                color = indicatorColor
            )
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun QuickNavigationRow(onNavigateToGuide: () -> Unit, onNavigateToAlerts: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    if (screenWidth < 600) { // Small screen (Z Flip 6) - stack vertically
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavigationButton(
                title = "Alerts History",
                onClick = onNavigateToAlerts,
                icon = Icons.Filled.Notifications,
                modifier = Modifier.fillMaxWidth()
            )
            NavigationButton(
                title = "Hardware Guide",
                onClick = onNavigateToGuide,
                icon = Icons.Filled.Info,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else { // Larger screen - horizontal layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NavigationButton(
                title = "Alerts Status",
                onClick = onNavigateToAlerts,
                icon = Icons.Filled.Notifications,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            NavigationButton(
                title = "Setup Guide",
                onClick = onNavigateToGuide,
                icon = Icons.Filled.Info,
                modifier = Modifier.weight(1f)
            )
        }
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