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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsHistoryScreen(
    viewModel: AlertsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var searchText by remember { mutableStateOf(searchQuery) }
    val coroutineScope = rememberCoroutineScope()

    // Update search text when query changes (e.g., from clearing)
    LaunchedEffect(searchQuery) {
        searchText = searchQuery
    }

    // Debounce local updates to ViewModel for better typing experience
    LaunchedEffect(searchText) {
        if (searchText != searchQuery) {
            delay(50) // Small delay for typing comfort
            viewModel.setSearchQuery(searchText)
        }
    }

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
                // Use local state for immediate response
                value = searchText,
                // Update immediately for smooth typing
                onValueChange = { newText ->
                    searchText = newText
                },
                label = { Text("Search Alerts (e.g., Motion, Temp Spike)") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                keyboardOptions = KeyboardOptions.Default.copy(
//                    autoCorrect = false,
                    capitalization = KeyboardCapitalization.None
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // LazyColumn (Scrollable List with Card UI)
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.searchQuery.isBlank())
                                    "No alerts recorded."
                                else
                                    "No results for \"${uiState.searchQuery}\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertCard(alert: AlertEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                alert.type.contains("Motion", ignoreCase = true) ->
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                alert.type.contains("Temp", ignoreCase = true) ->
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                else ->
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        onClick = { /* Optional: Add click action if needed */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Main Alert Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    alert.type,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    alert.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    alert.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.padding(start = 8.dp)
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