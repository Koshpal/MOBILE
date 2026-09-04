package com.app.koshpal.app.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.presentation.home.component.*
import com.app.koshpal.app.presentation.home.component.HomeTransactionItem
import com.app.koshpal.app.presentation.home.component.TagDashboardChip
import com.app.koshpal.app.presentation.home.component.DueItem
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.viewmodels.HomeViewModel
import com.app.koshpal.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MainHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onToCreateBudget: () -> Unit,
    onToProfile: () -> Unit,
    onToNotifications: () -> Unit,
    onToBudgetDetails: (String) -> Unit,
    onToAllDues: () -> Unit,
    onToAllTransactions: () -> Unit,
    onToAddDue: () -> Unit,
    onToTags: () -> Unit,
    onToGoals: () -> Unit,
    onNavigateToBudgetFeature: () -> Unit,
    onToTransactions: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onToCashDashboard: () -> Unit,
    onToCashFlow: () -> Unit = {},
    onAddCashEntry: () -> Unit,
) {
    val activeBudget by viewModel.activeMonthlyBudget.collectAsStateWithLifecycle()
    val spendingSummary by viewModel.spendingSummary.collectAsStateWithLifecycle()
    val untaggedAmount by viewModel.untaggedAmount.collectAsStateWithLifecycle()
    val topDues by viewModel.topDues.collectAsStateWithLifecycle()
    val budgetContext by viewModel.monthlyBudgetContext.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val tagsSummary by viewModel.tagsSummary.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val firstName by viewModel.firstName.collectAsStateWithLifecycle()

    var isStatusBarVisible by remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -15f && isStatusBarVisible) {
                    isStatusBarVisible = false
                }
                if (available.y > 15f && !isStatusBarVisible) {
                    isStatusBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    SetStatusBarAppearance(isDarkIcons = true)
    SetStatusBarVisibility(isVisible = isStatusBarVisible)

    val formatter = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")).apply {
            maximumFractionDigits = 0
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .nestedScroll(nestedScrollConnection)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        onClick = { onToProfile() },
                        border = BorderStroke(0.2.dp, MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(4.dp)
                                .clip(CircleShape).background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.person_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxSize(0.6f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = firstName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Jakarta,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.size(26.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(16.dp),
                        onClick = onToNotifications
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.notifications_24px),
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            BudgetOverviewCard(
                budgetContext = budgetContext,
                spendingSummary = spendingSummary,
                activeBudget = activeBudget,
                formatter = formatter,
                onMonthSummaryClick = { viewModel.onMonthSummaryClick(onNavigateToBudgetFeature) },
                onToTransactions = onToTransactions,
                onToCreateBudget = onToCreateBudget,
                onToBudgetDetails = onToBudgetDetails,
                onToCashDashboard = onToCashDashboard,
                onAddCashEntry = onAddCashEntry
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GoalsStatusCard(
                goals = goals,
                onToGoals = onToGoals
            )
            DuesCard(
                topDues = topDues,
                onToAddDue = onToAddDue,
                onToAllDues = onToAllDues,
                dueItem = { metadata -> 
                    DueItem(
                        metadata = metadata,
                        onToggleCompletion = { viewModel.toggleDueCompletion(metadata) }
                    ) 
                }
            )
            TransactionsCard(
                recentTransactions = recentTransactions,
                formatter = formatter,
                onToAllTransactions = onToAllTransactions,
                transactionItem = { transaction ->
                    val categoryName = viewModel.getCategoryName(transaction.budgetId, transaction.categoryId)
                    val tagName = viewModel.getTagName(transaction.tagIds.firstOrNull())
                    val classificationName = transaction.resolveClassificationName(categoryName, tagName)

                    HomeTransactionItem(
                        transaction = transaction, 
                        formatter = formatter,
                        classificationName = classificationName
                    ) {
                        onTransactionClick(transaction)
                    }
                }
            )
            CashFlowCard(
                incomeAmount = spendingSummary.incoming,
                expensesAmount = spendingSummary.outgoing,
                untaggedAmount = untaggedAmount,
                onToCashFlow = onToCashFlow
            )
            TagsCard(
                tagsSummary = tagsSummary,
                onToTags = onToTags,
                tagChip = { summary -> TagDashboardChip(summary) }
            )
        }
        Spacer(modifier = Modifier.height(76.dp))
    }
}
