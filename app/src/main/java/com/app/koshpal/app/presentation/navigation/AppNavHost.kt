package com.app.koshpal.app.presentation.navigation

import com.app.koshpal.app.presentation.home.MainHomeScreen
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.koshpal.app.domain.model.Transaction
import com.app.koshpal.app.fluxdeck.BudgetFluxDeck
import com.app.koshpal.app.fluxdeck.GoalFluxDeck
import com.app.koshpal.app.fluxdeck.DuesFluxDeck
import com.app.koshpal.core.data.entities.enums.NotificationType
import com.app.koshpal.app.viewmodels.HomeViewModel
import com.app.koshpal.app.viewmodels.profileviewmodel.ProfileViewModel
import com.app.koshpal.app.viewmodels.notificationsviewmodel.NotificationsViewModel
import com.app.koshpal.app.presentation.profile.ProfileScreen
import com.app.koshpal.app.presentation.notifications.NotificationsScreen
import com.app.koshpal.app.fluxdeck.TransactionsFluxDeck
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier,
    startDestination: String
) {
    val transactionsFluxDeck: TransactionsFluxDeck = koinInject()
    val budgetFluxDeck: BudgetFluxDeck = koinInject()
    val goalFluxDeck: GoalFluxDeck = koinInject()
    val duesFluxDeck: DuesFluxDeck = koinInject()

    LaunchedEffect(Unit) {
        transactionsFluxDeck.isFromNotification.collect { isFromNotification ->
            if (isFromNotification) {
                val transactionId = transactionsFluxDeck.transactionId.value
                if (transactionId != null) {
                    delay(500.milliseconds)
                    val foundTransaction = withTimeoutOrNull<Transaction?>(5.seconds) {
                        transactionsFluxDeck.allTransactions
                            .mapNotNull { list -> list.transactions.find { it.id == transactionId } }
                            .first()
                    }

                    val isClassified = foundTransaction?.let { (it.tagIds.isNotEmpty() || it.budgetId != null) } ?: false

                    if (isClassified) {
                        navController.navigate(Screen.DetailedTransaction.route) {
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Screen.CreateTransaction.route) {
                            launchSingleTop = true
                        }
                    }
                    transactionsFluxDeck.clearNotificationFlag()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        budgetFluxDeck.isItemClicked.collect { isClicked ->
            if (isClicked) {
                val budgetId = budgetFluxDeck.clickedBudgetId.value
                if (budgetId.isNotEmpty()) {
                    withTimeoutOrNull(5.seconds) {
                        budgetFluxDeck.allBudgets.mapNotNull { list -> list.find { it.id == budgetId } }.first()
                    }
                    navController.navigate(Screen.DetailedBudget.route) {
                        launchSingleTop = true
                    }
                    budgetFluxDeck.updateIsItemClicked(false)
                }
            }
        }
    }



    LaunchedEffect(Unit) {
        duesFluxDeck.isItemClicked.collect { isClicked ->
            if (isClicked) {
                val dueId = duesFluxDeck.clickedDueId.value
                if (dueId.isNotEmpty()) {
                    withTimeoutOrNull(5.seconds) {
                        duesFluxDeck.allDues.mapNotNull { list -> list.find { it.id == dueId } }.first()
                    }
                    navController.navigate(Screen.DetailedDue.route) {
                        launchSingleTop = true
                    }
                    duesFluxDeck.updateIsItemClicked(false)
                }
            }
        }
    }

    val enter: (AnimatedContentTransitionScope<*>.() -> EnterTransition) = {
        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
    }
    val exit: (AnimatedContentTransitionScope<*>.() -> ExitTransition) = {
        slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
    }
    val popEnter: (AnimatedContentTransitionScope<*>.() -> EnterTransition) = {
        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
    }
    val popExit: (AnimatedContentTransitionScope<*>.() -> ExitTransition) = {
        slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.MainRoot.route) {
            val viewModel: HomeViewModel = koinViewModel()

            MainHomeScreen(
                viewModel = viewModel,
                onToCreateBudget = { navController.navigate(Screen.CreateBudget.route) },
                onToProfile = { navController.navigate(Screen.Profile.route) },
                onToNotifications = { navController.navigate(Screen.Notifications.route) },
                onToBudgetDetails = { budgetId ->
                    budgetFluxDeck.updateClickedBudgetId(budgetId)
                    budgetFluxDeck.updateIsItemClicked(true)
                    navController.navigate(Screen.Graph.BUDGET) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    navController.navigate(Screen.DetailedBudget.route) {
                        launchSingleTop = true
                    }
                },
                onToAllDues = {
                    navController.navigate(Screen.Graph.DUES) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onToAllTransactions = {
                    navController.navigate(Screen.Graph.TRANSACTION) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onToAddDue = { navController.navigate(Screen.CreateDue.route) },
                onToTags = {
                    navController.navigate(Screen.Graph.TAGS) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onToGoals = {
                    navController.navigate(Screen.Graph.GOALS) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToBudgetFeature = {
                    viewModel.onMonthSummaryClick {
                        val context = viewModel.monthlyBudgetContext.value
                        navController.navigate(Screen.Graph.BUDGET) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        if (context.count == 1 && context.firstId != null) {
                            navController.navigate(Screen.DetailedBudget.route) {
                                launchSingleTop = true
                            }
                        }
                    }
                },
                onToTransactions = {
                    navController.navigate(Screen.Graph.TRANSACTION) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onTransactionClick = { transaction ->
                    transactionsFluxDeck.updateTransactionId(transaction.id)
                    val isClassified = transaction.tagIds.isNotEmpty() || transaction.budgetId != null
                    if (isClassified) {
                        navController.navigate(Screen.DetailedTransaction.route)
                    } else {
                        navController.navigate(Screen.CreateTransaction.route)
                    }
                },
                onToCashDashboard = { navController.navigate(Screen.Graph.CASH) },
                onToCashFlow = { navController.navigate(Screen.Graph.CASH_FLOW) },
                onAddCashEntry = {
                    transactionsFluxDeck.updateTransactionId(null)
                    navController.navigate(Screen.CreateCash.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            val viewModel: ProfileViewModel = koinViewModel()
            ProfileScreen(
                viewModel = viewModel,
                onToPreviousScreen = { navController.popBackStack() }
            )
        }

        composable(Screen.Notifications.route) {
            val viewModel: NotificationsViewModel = koinViewModel()
            NotificationsScreen(
                viewModel = viewModel,
                onToPreviousScreen = { navController.popBackStack() },
                onNotificationClick = { notification ->
                    when (notification.type) {
                        NotificationType.GOAL_INSIGHT -> {
                            notification.featureId?.let { id ->
                                goalFluxDeck.updateClickedGoalId(id)
                                navController.navigate(Screen.Graph.GOALS) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        NotificationType.BUDGET_WATCH -> {
                            notification.featureId?.let { id ->
                                budgetFluxDeck.updateClickedBudgetId(id)
                                budgetFluxDeck.updateIsItemClicked(true)
                                navController.navigate(Screen.Graph.BUDGET) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        NotificationType.DUE_REMINDER -> {
                            notification.featureId?.let { id ->
                                duesFluxDeck.updateClickedDueId(id)
                                duesFluxDeck.updateIsItemClicked(true)
                                navController.navigate(Screen.Graph.DUES) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        NotificationType.TRANSACTION_ALERT, NotificationType.ANOMALY_DETECTION -> {
                            if (notification.featureId != null) {
                                transactionsFluxDeck.updateTransactionId(notification.featureId, fromNotification = true)
                            } else {
                                navController.navigate(Screen.Graph.TRANSACTION) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                }
            )
        }
        
        cashMainGraph(
            navController = navController,
            modifier = Modifier,
            enter = enter,
            exit = exit,
            popEnter = popEnter,
            popExit = popExit
        )

        cashFlowMainGraph(
            navController = navController,
            modifier = Modifier,
            enter = enter,
            exit = exit,
            popEnter = popEnter,
            popExit = popExit
        )

        budgetMainGraph(
            navController = navController,
            modifier = Modifier,
            enter = enter,
            exit = exit,
            popEnter = popEnter,
            popExit = popExit
        )

        duesMainGraph(
            navController = navController,
            modifier = Modifier,
            enter = enter,
            exit = exit,
            popEnter = popEnter,
            popExit = popExit
        )

        transactionMainGraph(
            navController = navController,
            modifier = Modifier,
            enter = enter,
            exit = exit,
            popEnter = popEnter,
            popExit = popExit
        )

        tagsMainGraph(
            navController = navController,
            modifier = Modifier,
            enter = enter,
            exit = exit,
            popEnter = popEnter,
            popExit = popExit
        )

        goalsMainGraph(
            navController = navController,
            modifier = Modifier,
            enter = enter,
            exit = exit,
            popEnter = popEnter,
            popExit = popExit
        )

        authGraph(
            navController = navController,
            modifier = Modifier,
            enter = enter,
            exit = exit,
            popEnter = popEnter,
            popExit = popExit
        )
    }
}
