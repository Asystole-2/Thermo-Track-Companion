package com.example.thermotrackcompanion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thermotrackcompanion.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val tempUnit by viewModel.tempUnit.collectAsStateWithLifecycle(initialValue = "C")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Temperature Unit Preference (DataStore persistence)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Temperature Unit", style = MaterialTheme.typography.titleMedium)
                SegmentedButtonRow(
                    selectedUnit = tempUnit,
                    onUnitSelected = viewModel::saveTemperatureUnit
                )
            }
            Divider(Modifier.padding(vertical = 8.dp))
            // Placeholder for future settings (e.g., Alert Thresholds)
            Text("Alert Thresholds (Future Feature)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("The mobile app could eventually allow the user to set motion/temperature limits.", style = MaterialTheme.typography.bodySmall)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
                val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()

                Switch(
                    checked = darkMode,
                    onCheckedChange = { viewModel.saveDarkMode(it) }
                )
            }
            Divider(Modifier.padding(vertical = 8.dp))

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedButtonRow(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit
) {
    val units = listOf("C", "F")
    SingleChoiceSegmentedButtonRow {
        units.forEachIndexed { index, unit ->
            SegmentedButton(
                selected = unit == selectedUnit,
                onClick = { onUnitSelected(unit) },
                shape = SegmentedButtonDefaults.baseShape,
                label = { Text(unit) }
            )
        }
    }
}