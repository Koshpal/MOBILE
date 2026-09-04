package com.app.koshpal.app.presentation.home.component


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.ui.theme.Jakarta
import java.text.NumberFormat

@Composable
fun BudgetOverviewCard(
    budgetContext: BudgetContext,
    spendingSummary: SpendingSummary,
    activeBudget: Budget?,
    formatter: NumberFormat,
    onMonthSummaryClick: () -> Unit,
    onToTransactions: () -> Unit,
    onToCreateBudget: () -> Unit,
    onToBudgetDetails: (String) -> Unit,
    onToCashDashboard: () -> Unit,
    onAddCashEntry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { onMonthSummaryClick() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "SPENT IN ${budgetContext.monthName.uppercase()}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.keyboard_arrow_right_24px),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToTransactions() },
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SummaryMetric(
                    label = "Outgoing",
                    amount = formatter.format(spendingSummary.outgoing),
                    icon = R.drawable.call_made_24px,
                    iconColor = MaterialTheme.colorScheme.primary
                )
                VerticalDivider(
                    modifier = Modifier.height(70.dp).padding(vertical = 10.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline
                )
                SummaryMetric(
                    label = "Incoming",
                    amount = formatter.format(spendingSummary.incoming),
                    icon = R.drawable.call_received_24px,
                    iconColor = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            activeBudget?.let { budget ->
                val used = spendingSummary.budgetUsed
                val total = budget.amount
                val progress = if (total > 0) (used / total).toFloat().coerceIn(0f, 1f) else 0f
                val percentage = (progress * 100).toInt()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.account_balance_wallet_24px_2),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Budget used",
                            fontFamily = Jakarta,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        "$percentage % of Monthly Budget",
                        fontWeight = FontWeight.Medium,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.secondaryContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${100 - percentage}% remaining",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        "view budget >",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Jakarta,
                        modifier = Modifier.clickable { onToBudgetDetails(budget.id) }
                    )
                }
            } ?: run {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = { onToCreateBudget() },
                    shape = CircleShape,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Set Monthly Budget",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color =  MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
        DashedDivider()
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.payments_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Cash on hand",
                fontWeight = FontWeight.SemiBold,
                fontFamily = Jakarta,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Card(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                onClick = onAddCashEntry,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_2_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Card(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                onClick = onToCashDashboard,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.keyboard_arrow_right_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
