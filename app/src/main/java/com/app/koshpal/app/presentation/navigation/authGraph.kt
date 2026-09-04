package com.app.koshpal.app.presentation.navigation


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.app.koshpal.app.presentation.onboarding.AuthScreen
import com.app.koshpal.app.presentation.onboarding.GatewayScreen
import com.app.koshpal.app.presentation.onboarding.OnBoardQuestionScreen
import com.app.koshpal.app.presentation.onboarding.OnBoardScreen
import com.app.koshpal.app.viewmodels.authviewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    modifier: Modifier,
    enter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    exit: (AnimatedContentTransitionScope<*>.() -> ExitTransition),
    popEnter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    popExit: (AnimatedContentTransitionScope<*>.() -> ExitTransition)
) {
    navigation(
        startDestination = Screen.Gateway.route,
        route = Screen.Graph.AUTH
    ) {
        composable(
            route = Screen.Gateway.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            GatewayScreen(
                modifier = modifier,
                onToAuth = {
                    navController.navigate(Screen.Auth.route)
                }
            )
        }
        composable(
            route = Screen.Auth.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val viewModel: AuthViewModel = koinViewModel()
            AuthScreen(
                modifier = modifier,
                viewModel = viewModel,
                onToOnBoard = {
                    navController.navigate(Screen.OnBoard.route) {
                        popUpTo(Screen.Auth.route) {
                            inclusive = true
                        }
                    }
                },
                onToMain = {
                    navController.navigate(Screen.MainRoot.route) {
                        popUpTo(Screen.Graph.AUTH) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.OnBoard.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            OnBoardScreen(
                modifier = modifier,
                onToQuestions = {
                    navController.navigate(Screen.OnBoardQuestion.route)
                }
            )
        }
        composable(
            route = Screen.OnBoardQuestion.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val viewModel: AuthViewModel = koinViewModel()
            OnBoardQuestionScreen(
                modifier = modifier,
                viewModel = viewModel,
                onToMain = {
                    navController.navigate(Screen.MainRoot.route) {
                        popUpTo(Screen.Graph.AUTH) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
