package com.example.thermotrackcompanion

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thermotrackcompanion.data.AppContainer
import com.example.thermotrackcompanion.ui.screens.AlertsHistoryScreen
import com.example.thermotrackcompanion.ui.screens.HardwareGuideScreen
import com.example.thermotrackcompanion.ui.screens.HomeScreen
import com.example.thermotrackcompanion.ui.screens.SettingsScreen
import com.example.thermotrackcompanion.viewmodel.AlertsViewModel
import com.example.thermotrackcompanion.viewmodel.HomeViewModel
import com.example.thermotrackcompanion.viewmodel.SettingsViewModel

object Destinations {
    const val HOME = "home"
    const val GUIDE = "guide"
    const val ALERTS = "alerts"
    const val SETTINGS = "settings"
}

@Composable
fun ThermoTrackApp(container: AppContainer) {
    val navController = rememberNavController()

    // Initialize ViewModels using the provided factory that needs the repository
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(container.repository))
    val alertsViewModel: AlertsViewModel = viewModel(factory = AlertsViewModel.Factory(container.repository))
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(container.repository))

    NavHost(navController = navController, startDestination = Destinations.HOME) {
        // Define navigation for each screen
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