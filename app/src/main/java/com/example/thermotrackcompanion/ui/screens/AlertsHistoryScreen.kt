package com.example.thermotrackcompanion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thermotrackcompanion.data.AlertEntity
import com.example.thermotrackcompanion.viewmodel.AlertsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsHistoryScreen(
    viewModel: AlertsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope() // Needed for async delete operation

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alerts History") },
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
        ) {
            // Search Bar (Extra Feature 2)
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { newText -> viewModel.setSearchQuery(newText) },
                label = { Text("Search Alerts (e.g., Motion, Temp Spike)") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    capitalization = KeyboardCapitalization.None
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // LazyColumn (Scrollable List with Card UI)
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.filteredAlerts, key = { it.id }) { alert ->
                    // Pass the AlertEntity and the delete function to the card
                    AlertCard(
                        alert = alert,
                        onDelete = {
                            coroutineScope.launch {
                                viewModel.deleteAlert(alert)
                            }
                        }
                    )
                }
                if (uiState.filteredAlerts.isEmpty()) {
                    item {
                        Text(
                            text = if (uiState.searchQuery.isBlank()) "No alerts recorded." else "No results for \"${uiState.searchQuery}\"",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertCard(alert: AlertEntity, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.type == "Motion" || alert.type.contains("Temp"))
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main Alert Content
            Column(modifier = Modifier.weight(1f)) {
                Text(alert.type, style = MaterialTheme.typography.titleMedium)
                Text(alert.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Text(alert.timestamp, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete Alert",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}