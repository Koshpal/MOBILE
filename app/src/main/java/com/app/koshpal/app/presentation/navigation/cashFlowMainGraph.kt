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
import com.app.koshpal.app.presentation.cashflow.CashFlowHomeScreen
import com.app.koshpal.app.presentation.cashflow.IncomingTransactionsScreen
import com.app.koshpal.app.presentation.cashflow.OutgoingTransactionsScreen
import com.app.koshpal.app.viewmodels.cashflowviewmodel.CashFlowViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

fun NavGraphBuilder.cashFlowMainGraph(
    navController: NavHostController,
    modifier: Modifier,
    enter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    exit: (AnimatedContentTransitionScope<*>.() -> ExitTransition),
    popEnter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    popExit: (AnimatedContentTransitionScope<*>.() -> ExitTransition),
) {
    navigation(
        startDestination = Screen.CashFlowHome.route,
        route = Screen.Graph.CASH_FLOW
    ) {
        composable(
            route = Screen.CashFlowHome.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val viewModel: CashFlowViewModel = koinViewModel()
            CashFlowHomeScreen(
                viewModel = viewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onToIncoming = { navController.navigate(Screen.IncomingTransactions.route) },
                onToOutgoing = { navController.navigate(Screen.OutgoingTransactions.route) },
                onToAddTransaction = { navController.navigate(Screen.CreateTransaction.route) }
            )
        }

        composable(
            route = Screen.IncomingTransactions.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val viewModel: CashFlowViewModel = koinViewModel()
            val transactionsFluxDeck: TransactionsFluxDeck = koinInject()
            IncomingTransactionsScreen(
                viewModel = viewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onTransactionClick = { transactionId ->
                    transactionsFluxDeck.updateTransactionId(transactionId)
                    navController.navigate(Screen.DetailedTransaction.route)
                }
            )
        }

        composable(
            route = Screen.OutgoingTransactions.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val viewModel: CashFlowViewModel = koinViewModel()
            val transactionsFluxDeck: TransactionsFluxDeck = koinInject()
            OutgoingTransactionsScreen(
                viewModel = viewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onTransactionClick = { transactionId ->
                    transactionsFluxDeck.updateTransactionId(transactionId)
                    navController.navigate(Screen.DetailedTransaction.route)
                }
            )
        }
    }
}
