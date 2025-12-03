package com.example.thermotrackcompanion

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thermotrack.companion.data.AppContainer
import com.thermotrack.companion.ui.screens.AlertsHistoryScreen
import com.thermotrack.companion.ui.screens.HardwareGuideScreen
import com.thermotrack.companion.ui.screens.HomeScreen
import com.thermotrack.companion.ui.screens.SettingsScreen
import com.thermotrack.companion.viewmodel.AlertsViewModel
import com.thermotrack.companion.viewmodel.HomeViewModel
import com.thermotrack.companion.viewmodel.SettingsViewModel

object Destinations {
    const val HOME = "home"
    const val GUIDE = "guide"
    const val ALERTS = "alerts"
    const val SETTINGS = "settings"
}

@Composable
fun ThermoTrackApp(container: AppContainer) {
    val navController = rememberNavController()

    // Base ViewModel Factories (for ViewModels that need the repository)
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(container.repository))
    val alertsViewModel: AlertsViewModel = viewModel(factory = AlertsViewModel.Factory(container.repository))
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(container.repository))

    NavHost(navController = navController, startDestination = Destinations.HOME) {
        composable(Destinations.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToSettings = { navController.navigate(Destinations.SETTINGS) },
                onNavigateToGuide = { navController.navigate(Destinations.GUIDE) },
                onNavigateToAlerts = { navController.navigate(Destinations.ALERTS) }
            )
        }
        composable(Destinations.GUIDE) {
            HardwareGuideScreen(
                viewModel = homeViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.ALERTS) {
            AlertsHistoryScreen(
                viewModel = alertsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.SETTINGS) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}