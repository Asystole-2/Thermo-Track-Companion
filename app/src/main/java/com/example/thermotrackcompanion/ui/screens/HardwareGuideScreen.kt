package com.example.thermotrackcompanion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.thermotrackcompanion.model.HardwareGuide
import com.example.thermotrackcompanion.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HardwareGuideScreen(
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit
) {
    val guides = viewModel.getHardwareGuides()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hardware Guide") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        // Adapting for Screen Sizes: Use BoxWithConstraints to determine layout
        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            // Extra Feature 4: Adaptive Layout (switch to grid if screen is wide enough)
            if (maxWidth > 600.dp) {
                // Tablet/Landscape Layout: Use a grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(guides) { guide ->
                        HardwareGuideCard(guide)
                    }
                }
            } else {
                // Phone/Portrait Layout: Use a simple column
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(guides) { guide ->
                        HardwareGuideCard(guide)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HardwareGuideCard(guide: HardwareGuide) {
    Card(Modifier.fillMaxWidth()) {
        Column {
            // Coil for Image Loading (Load and Display Images using Coil)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(guide.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = guide.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Column(Modifier.padding(16.dp)) {
                Text(guide.name, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(guide.description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}