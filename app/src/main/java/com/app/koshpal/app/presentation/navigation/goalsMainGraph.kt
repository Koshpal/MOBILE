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
import com.app.koshpal.app.presentation.goals.DetailedGoalsScreen
import com.app.koshpal.app.presentation.goals.GoalCreationScreen
import com.app.koshpal.app.presentation.goals.GoalsHomeScreen
import com.app.koshpal.app.viewmodels.goalsviewmodel.GoalCreationViewModel
import com.app.koshpal.app.viewmodels.goalsviewmodel.GoalViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.goalsMainGraph(
    navController: NavHostController,
    modifier: Modifier,
    enter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    exit: (AnimatedContentTransitionScope<*>.() -> ExitTransition),
    popEnter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    popExit: (AnimatedContentTransitionScope<*>.() -> ExitTransition)
) {
    navigation(
        startDestination = Screen.GoalsHome.route,
        route = Screen.Graph.GOALS
    ) {
        composable(
            route = Screen.GoalsHome.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Graph.GOALS)
            }
            val goalViewModel = koinViewModel<GoalViewModel>(
                viewModelStoreOwner = parentEntry
            )
            GoalsHomeScreen(
                modifier = modifier,
                viewModel = goalViewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onToCreateGoal = { 
                    navController.navigate(Screen.CreateGoal.route) 
                },
                onToDetailedGoal = {
                    navController.navigate(Screen.DetailedGoal.route)
                }
            )
        }

        composable(
            route = Screen.DetailedGoal.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Graph.GOALS)
            }
            val goalViewModel = koinViewModel<GoalViewModel>(
                viewModelStoreOwner = parentEntry
            )
            DetailedGoalsScreen(
                viewModel = goalViewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onEditGoal = { goal ->
                    goalViewModel.prepareEditGoal(goal)
                    navController.navigate(Screen.CreateGoal.route)
                }
            )
        }

        composable(
            route = Screen.CreateGoal.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val viewModel: GoalCreationViewModel = koinViewModel()
            GoalCreationScreen(
                viewModel = viewModel,
                onToPreviousScreen = { navController.popBackStack() }
            )
        }
    }
}
