package com.app.koshpal.app.presentation.navigation


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.app.koshpal.app.presentation.dues.DetailedDueScreen
import com.app.koshpal.app.presentation.dues.DuesCreationScreen
import com.app.koshpal.app.presentation.dues.DuesHomeScreen
import com.app.koshpal.app.viewmodels.duesviewmodel.DuesCreationViewModel
import com.app.koshpal.app.viewmodels.duesviewmodel.DuesViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.duesMainGraph(
    navController: NavHostController,
    modifier: Modifier,
    enter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    exit: (AnimatedContentTransitionScope<*>.() -> ExitTransition),
    popEnter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    popExit: (AnimatedContentTransitionScope<*>.() -> ExitTransition)
) {
    navigation(
        startDestination = Screen.DuesHome.route,
        route = Screen.Graph.DUES
    ) {
        composable(
            route = Screen.DuesHome.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Graph.DUES)
            }
            val duesViewModel = koinViewModel<DuesViewModel>(
                viewModelStoreOwner = parentEntry
            )
            DuesHomeScreen(
                viewModel = duesViewModel,
                onToPreviousScreen = {
                    navController.popBackStack()
                },
                onToAddDue = {
                    navController.navigate(Screen.CreateDue.route)
                },
                onToDetailedDue = {
                    navController.navigate(Screen.DetailedDue.route)
                },
                modifier = modifier
            )
        }
        composable(
            route = Screen.DetailedDue.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Graph.DUES)
            }
            val duesViewModel = koinViewModel<DuesViewModel>(
                viewModelStoreOwner = parentEntry
            )
            DetailedDueScreen(
                viewModel = duesViewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onToSettings = {
                }
            )
        }
        composable(
            route = Screen.CreateDue.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val duesViewModel = koinViewModel<DuesCreationViewModel>()
            DuesCreationScreen(
                viewModel = duesViewModel,
                onToPreviousScreen = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
    }
}
