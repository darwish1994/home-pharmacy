package com.dwa.fridgepharmacy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dwa.fridgepharmacy.core.data.preferences.AppPreferences
import com.dwa.fridgepharmacy.core.data.preferences.PreferenceKeys
import com.dwa.fridgepharmacy.feature.medication.ui.addedit.AddEditMedicationScreen
import com.dwa.fridgepharmacy.feature.medication.ui.detail.MedicationDetailScreen
import com.dwa.fridgepharmacy.feature.medication.ui.list.MedicationListScreen
import com.dwa.fridgepharmacy.feature.medication.ui.list.MedicationListViewModel
import com.dwa.fridgepharmacy.feature.onboarding.ui.OnboardingScreen
import com.dwa.fridgepharmacy.feature.scanner.ui.PrescriptionScannerScreen
import com.dwa.fridgepharmacy.feature.settings.ui.SettingsScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Serializable object OnboardingRoute
@Serializable object MedicationListRoute
@Serializable data class MedicationDetailRoute(val medicationId: Long)
@Serializable data class AddEditMedicationRoute(val medicationId: Long = -1L)
@Serializable object PrescriptionScannerRoute
@Serializable object SettingsRoute

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val preferences: AppPreferences = koinInject()
    val onboardingCompleted = preferences.getBoolean(PreferenceKeys.ONBOARDING_COMPLETED, false)

    val startDestination: Any = if (onboardingCompleted) MedicationListRoute else OnboardingRoute

    NavHost(navController = navController, startDestination = startDestination) {
        composable<OnboardingRoute> {
            OnboardingScreen(
                onComplete = {
                    preferences.putBoolean(PreferenceKeys.ONBOARDING_COMPLETED, true)
                    navController.navigate(MedicationListRoute) {
                        popUpTo(OnboardingRoute) { inclusive = true }
                    }
                }
            )
        }
        composable<MedicationListRoute> {
            val viewModel: MedicationListViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            MedicationListScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onAddClick = { navController.navigate(AddEditMedicationRoute()) },
                onMedicationClick = { id -> navController.navigate(MedicationDetailRoute(id)) },
                onScanClick = { navController.navigate(PrescriptionScannerRoute) },
                onSettingsClick = { navController.navigate(SettingsRoute) }
            )
        }
        composable<MedicationDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<MedicationDetailRoute>()
            MedicationDetailScreen(
                medicationId = route.medicationId,
                onNavigateBack = { navController.popBackStack() },
                onEditClick = { id -> navController.navigate(AddEditMedicationRoute(id)) }
            )
        }
        composable<AddEditMedicationRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<AddEditMedicationRoute>()
            val medicationId = if (route.medicationId == -1L) null else route.medicationId
            AddEditMedicationScreen(medicationId = medicationId, onNavigateBack = { navController.popBackStack() })
        }
        composable<PrescriptionScannerRoute> {
            PrescriptionScannerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<SettingsRoute> {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
