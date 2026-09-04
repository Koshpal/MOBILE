package com.app.koshpal

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.koshpal.app.presentation.navigation.AppNavHost
import com.app.koshpal.app.presentation.navigation.Screen
import com.app.koshpal.app.fluxdeck.TransactionsFluxDeck
import com.app.koshpal.app.data.repository.UserPreferences
import com.app.koshpal.app.presentation.globalcomponents.BottomBar
import com.app.koshpal.ui.theme.KoshpalTheme
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import org.koin.android.ext.android.inject
import android.content.Intent

class MainActivity : ComponentActivity() {
    private val transactionsFluxDeck: TransactionsFluxDeck by inject()
    private val userPreferences: UserPreferences by inject()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val transactionId = intent.getStringExtra("transaction_id")
        val navigateTo = intent.getStringExtra("navigate_to")
        if (navigateTo == "classify_transaction" && transactionId != null) {
            transactionsFluxDeck.updateTransactionId(transactionId, fromNotification = true)
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()
        setContent {
            KoshpalTheme {
                val permissionsLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { }

                val usernameState by userPreferences.username.collectAsState(initial = "loading")

                LaunchedEffect(usernameState) {
                    if (usernameState != "loading" && usernameState.isNotEmpty()) {
                        val permissions = mutableListOf(
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_CONTACTS
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        permissionsLauncher.launch(permissions.toTypedArray())
                    }
                }

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val hazeState = rememberHazeState()

                val bottomBarScreens = listOf(
                    Screen.MainRoot.route,
                    Screen.BudgetHome.route,
                    Screen.DetailedBudget.route,
                    Screen.GoalsHome.route,
                    Screen.DetailedGoal.route,
                    Screen.DuesHome.route,
                    Screen.DetailedDue.route,
                    Screen.TagsHome.route,
                    Screen.DetailedTag.route,
                    Screen.TransactionsHome.route,
                    Screen.DetailedTransaction.route,
                    Screen.CashHome.route,
                    Screen.Graph.BUDGET,
                    Screen.Graph.DUES,
                    Screen.Graph.TRANSACTION,
                    Screen.Graph.TAGS,
                    Screen.Graph.GOALS,
                    Screen.Graph.CASH
                )

                val isBottomBarVisible = currentRoute != null && (
                    bottomBarScreens.any { 
                        currentRoute == it || currentRoute.contains(it) 
                    }
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (usernameState != "loading") {
                            AppNavHost(
                                navController = navController,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .hazeSource(state = hazeState)
                                    .padding(bottom = innerPadding.calculateBottomPadding()),
                                startDestination = if (usernameState.isNotEmpty()) Screen.MainRoot.route else Screen.Graph.AUTH
                            )
                        }
                        if (isBottomBarVisible) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .navigationBarsPadding()
                            ) {
                                BottomBar(
                                    navController = navController,
                                    currentRoute = currentRoute,
                                    hazeState = hazeState
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
