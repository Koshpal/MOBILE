package com.app.koshpal.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.app.koshpal.app.presentation.profile.LegalDocumentScreen
import com.app.koshpal.app.presentation.profile.ProfileScreen
import com.app.koshpal.app.viewmodels.profileviewmodel.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.profileMainGraph(
    navController: NavHostController,
    modifier: Modifier,
    enter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    exit: (AnimatedContentTransitionScope<*>.() -> ExitTransition),
    popEnter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    popExit: (AnimatedContentTransitionScope<*>.() -> ExitTransition)
) {
    navigation(
        startDestination = Screen.Profile.route,
        route = Screen.Graph.PROFILE
    ) {
        composable(
            route = Screen.Profile.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Graph.PROFILE)
            }
            val profileViewModel = koinViewModel<ProfileViewModel>(
                viewModelStoreOwner = parentEntry
            )
            ProfileScreen(
                viewModel = profileViewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onToLegalDocument = { document ->
                    navController.navigate(Screen.LegalDocument.createRoute(document)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.LegalDocument.route,
            arguments = listOf(
                navArgument("document") { type = NavType.StringType }
            ),
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) { backStackEntry ->
            val document = backStackEntry.arguments?.getString("document") ?: "terms"
            LegalDocumentScreen(
                document = document,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
