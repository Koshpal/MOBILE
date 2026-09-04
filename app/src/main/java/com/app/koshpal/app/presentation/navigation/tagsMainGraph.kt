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
import com.app.koshpal.app.presentation.tags.DetailedTagScreen
import com.app.koshpal.app.presentation.tags.TagsHomeScreen
import com.app.koshpal.app.viewmodels.tagsviewmodel.TagsViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.tagsMainGraph(
    navController: NavHostController,
    modifier: Modifier,
    enter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    exit: (AnimatedContentTransitionScope<*>.() -> ExitTransition),
    popEnter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    popExit: (AnimatedContentTransitionScope<*>.() -> ExitTransition)
) {
    navigation(
        startDestination = Screen.TagsHome.route,
        route = Screen.Graph.TAGS
    ) {
        composable(
            route = Screen.TagsHome.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Graph.TAGS)
            }
            val tagsViewModel = koinViewModel<TagsViewModel>(
                viewModelStoreOwner = parentEntry
            )
            TagsHomeScreen(
                viewModel = tagsViewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onToDetailedTag = {
                    navController.navigate(Screen.DetailedTag.route)
                }
            )
        }

        composable(
            route = Screen.DetailedTag.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Graph.TAGS)
            }
            val tagsViewModel = koinViewModel<TagsViewModel>(
                viewModelStoreOwner = parentEntry
            )
            DetailedTagScreen(
                viewModel = tagsViewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onToDetailedGoal = { goalId ->
                    tagsViewModel.updateClickedGoalId(goalId)
                    navController.navigate(Screen.DetailedGoal.route)
                }
            )
        }
    }
}
