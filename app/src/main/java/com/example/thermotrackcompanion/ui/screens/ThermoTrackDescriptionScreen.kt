package com.example.thermotrackcompanion.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThermoTrackDescriptionScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val externalUrl = "https://thtrack.live/"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About ThermoTrack") },
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
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "The Full ThermoTrack Experience",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "The full ThermoTrack platform is designed for complete room environment management and actuation, focusing on real-time control, multi-room monitoring, and advanced reporting.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Key Differences Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("This Companion App vs. Full Web App", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Companion App:",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text("• Simple, read-only monitoring of a single room.", style = MaterialTheme.typography.bodyMedium)
                    Text("• Historical alerts viewing.", style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Full Web App (thtrack.live):",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("• Management of multiple rooms and devices.", style = MaterialTheme.typography.bodyMedium)
                    Text("• **Actuation:** Allows changing room conditions (e.g., fan control, setpoint adjustment).", style = MaterialTheme.typography.bodyMedium)
                    Text("• Comprehensive data reporting and user management.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(24.dp))

            // External Link Button
            OutlinedButton(
                onClick = {
                    // Open the external URL in a browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(externalUrl))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Link, contentDescription = "Open Website")
                Spacer(Modifier.width(8.dp))
                Text("Visit Full ThermoTrack Platform")
            }
        }
    }
}