package com.app.koshpal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.fluxdeck.TransactionsFluxDeck
import com.app.koshpal.app.presentation.globalcomponents.BottomBar
import com.app.koshpal.app.presentation.globalcomponents.LocalBottomBarVisibility
import com.app.koshpal.app.presentation.navigation.AppNavHost
import com.app.koshpal.app.presentation.navigation.Screen
import com.app.koshpal.ui.theme.KoshpalTheme
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import org.koin.android.ext.android.inject

class MainActivity : FragmentActivity() {
    private val transactionsFluxDeck: TransactionsFluxDeck by inject()
    private val userPreferences: UserPreferences by inject()
    private var isAuthenticated by mutableStateOf(false)

    private fun checkBiometricStatus(): Int {
        val biometricManager =BiometricManager.from(this)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
    }

    private fun showBiometricPrompt() {
        val status = checkBiometricStatus()
        if (status != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(
                this,
                "Biometric not set up. Please configure security in your phone settings.",
                Toast.LENGTH_LONG
            ).show()

            isAuthenticated = true
            return
        }


        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    finish()
                }
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isAuthenticated = true
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Koshpal Secure Access")
            .setSubtitle("Authenticate to continue")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

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
            val isBiometricEnabled by userPreferences.isBiometricEnabled.collectAsState(initial = null)
            
            LaunchedEffect(isBiometricEnabled) {
                if (isBiometricEnabled == true) {
                    showBiometricPrompt()
                } else if (isBiometricEnabled == false) {
                    isAuthenticated = true
                }
            }

            if (isAuthenticated) {
                KoshpalTheme {
                    val context = LocalContext.current
                    val scope = rememberCoroutineScope()
                    val hasRequestedPermissions by userPreferences.hasRequestedPermissions.collectAsState(initial = null)

                    val permissionsLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestMultiplePermissions()
                    ) {
                        scope.launch {
                            userPreferences.setHasRequestedPermissions(true)
                        }
                    }

                    val usernameState by userPreferences.username.collectAsState(initial = "loading")

                    LaunchedEffect(usernameState, hasRequestedPermissions) {
                        if (usernameState != "loading" && usernameState.isNotEmpty() && hasRequestedPermissions != null) {
                            val permissions = mutableListOf(
                                Manifest.permission.READ_SMS,
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_CONTACTS
                            )
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                            }

                            val missingPermissions = permissions.filter {
                                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
                            }

                            if (missingPermissions.isNotEmpty()) {
                                if (!hasRequestedPermissions!!) {
                                    permissionsLauncher.launch(missingPermissions.toTypedArray())
                                } else {
                                    try {
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts("package", context.packageName, null)
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                }
                            }
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

                    val bottomBarVisibility = remember { mutableStateOf(true) }

                    CompositionLocalProvider(LocalBottomBarVisibility provides bottomBarVisibility) {
                        val isBottomBarVisible = currentRoute != null && bottomBarVisibility.value && (
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
    }
}
