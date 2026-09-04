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
import com.app.koshpal.app.presentation.budget.BudgetCreationScreen
import com.app.koshpal.app.presentation.budget.BudgetHomeScreen
import com.app.koshpal.app.presentation.budget.BudgetSettingsScreen
import com.app.koshpal.app.presentation.budget.DetailedBudgetScreen
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetCreationViewModel
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetSettingsViewModel
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.budgetMainGraph(
    navController: NavHostController,
    modifier: Modifier,
    enter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    exit: (AnimatedContentTransitionScope<*>.() -> ExitTransition),
    popEnter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    popExit: (AnimatedContentTransitionScope<*>.() -> ExitTransition)
) {
    navigation(
        startDestination = Screen.BudgetHome.route,
        route = Screen.Graph.BUDGET
    ) {
        composable(
            route = Screen.BudgetHome.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Graph.BUDGET)
            }
            val budgetViewModel = koinViewModel<BudgetViewModel>(
                viewModelStoreOwner = parentEntry
            )
            BudgetHomeScreen(
                modifier = modifier,
                viewModel = budgetViewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onToCreateBudget = {
                    navController.navigate(Screen.CreateBudget.route) {
                        launchSingleTop = true
                    }
                },
                onToDetailedBudget = {
                    navController.navigate(Screen.DetailedBudget.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = Screen.CreateBudget.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val budgetViewModel = koinViewModel<BudgetCreationViewModel>()

            BudgetCreationScreen(
                modifier = modifier,
                viewModel = budgetViewModel,
                onToPreviousScreen = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.BudgetSettings.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) { backStackEntry ->
            val budgetSettingsViewModel = koinViewModel<BudgetSettingsViewModel>()
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Graph.BUDGET)
            }
            val budgetViewModel = koinViewModel<BudgetViewModel>(
                viewModelStoreOwner = parentEntry
            )
            BudgetSettingsScreen(
                onToPreviousScreen = { navController.popBackStack() },
                onDeleteSuccess = {
                    budgetViewModel.resetDetailedState()
                },
                viewModel = budgetSettingsViewModel
            )
        }
        composable(
            route = Screen.DetailedBudget.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Graph.BUDGET)
            }
            val budgetViewModel = koinViewModel<BudgetViewModel>(
                viewModelStoreOwner = parentEntry
            )
            DetailedBudgetScreen(
                viewModel = budgetViewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onToSettings = {
                    navController.navigate(Screen.BudgetSettings.route) {
                        launchSingleTop = true
                    }
                },
                onToCreateBudget = {
                    navController.navigate(Screen.CreateBudget.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
