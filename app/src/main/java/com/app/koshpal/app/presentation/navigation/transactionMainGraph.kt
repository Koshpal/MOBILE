package com.app.koshpal.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.app.koshpal.app.presentation.transactions.DetailedTransactionsScreen
import com.app.koshpal.app.presentation.transactions.TransactionCreationScreen
import com.app.koshpal.app.presentation.transactions.TransactionsHomeScreen
import com.app.koshpal.app.viewmodels.transactionsviewmodel.TransactionCreationViewModel
import com.app.koshpal.app.viewmodels.transactionsviewmodel.TransactionsViewModel
import com.app.koshpal.app.viewmodels.transactionsviewmodel.DetailedTransactionViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.transactionMainGraph(
    navController: NavHostController,
    modifier: Modifier,
    enter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    exit: (AnimatedContentTransitionScope<*>.() -> ExitTransition),
    popEnter: (AnimatedContentTransitionScope<*>.() -> EnterTransition),
    popExit: (AnimatedContentTransitionScope<*>.() -> ExitTransition)
) {
    navigation(
        startDestination = Screen.TransactionsHome.route,
        route = Screen.Graph.TRANSACTION
    ) {
        composable(
            route = Screen.TransactionsHome.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val viewModel: TransactionsViewModel = koinViewModel()
            TransactionsHomeScreen(
                viewModel = viewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onTransactionClick = { transaction ->
                    viewModel.updateClickedTransactionId(transaction.id)
                    val isClassified = transaction.tagIds.isNotEmpty() || transaction.budgetId != null
                    if (isClassified) {
                        navController.navigate(Screen.DetailedTransaction.route)
                    } else {
                        navController.navigate(Screen.CreateTransaction.route)
                    }
                }
            )
        }

        composable(
            route = Screen.DetailedTransaction.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val viewModel: DetailedTransactionViewModel = koinViewModel()
            DetailedTransactionsScreen(
                modifier = modifier,
                viewModel = viewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onEdit = {
                    navController.navigate(Screen.CreateTransaction.route)
                }
            )
        }

        composable(
            route = Screen.CreateTransaction.route,
            enterTransition = enter,
            exitTransition = exit,
            popEnterTransition = popEnter,
            popExitTransition = popExit
        ) {
            val viewModel: TransactionCreationViewModel = koinViewModel()
            TransactionCreationScreen(
                viewModel = viewModel,
                onToPreviousScreen = { navController.popBackStack() }
            )
        }
    }
}
