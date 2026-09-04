package com.app.koshpal.app.presentation.navigation


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.app.koshpal.app.fluxdeck.TransactionsFluxDeck
import com.app.koshpal.app.presentation.cash.CashCreationScreen
import com.app.koshpal.app.presentation.cash.CashHomeScreen
import com.app.koshpal.app.viewmodels.CashViewModel
import com.app.koshpal.app.viewmodels.transactionsviewmodel.TransactionCreationViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

fun NavGraphBuilder.cashMainGraph(
    navController: NavHostController,
    modifier: Modifier,
    enter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    exit: (AnimatedContentTransitionScope<*>.() -> ExitTransition),
    popEnter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    popExit: (AnimatedContentTransitionScope<*>.() -> ExitTransition)
) {
    navigation(
        startDestination = Screen.CashHome.route,
        route = Screen.Graph.CASH
    ) {
        composable(
            route = Screen.CashHome.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val cashViewModel: CashViewModel = koinViewModel()
            val transactionsFluxDeck: TransactionsFluxDeck = koinInject()
            
            CashHomeScreen(
                viewModel = cashViewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onAddCash = { 
                    transactionsFluxDeck.updateTransactionId(null)
                    navController.navigate(Screen.CreateCash.route) 
                },
                onTransactionClick = { id: String ->
                    transactionsFluxDeck.updateTransactionId(id)
                    navController.navigate(Screen.CreateCash.route)
                }
            )
        }

        composable(
            route = Screen.CreateCash.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val creationViewModel: TransactionCreationViewModel = koinViewModel()
            CashCreationScreen(
                viewModel = creationViewModel,
                onToPreviousScreen = { navController.popBackStack() }
            )
        }
    }
}
