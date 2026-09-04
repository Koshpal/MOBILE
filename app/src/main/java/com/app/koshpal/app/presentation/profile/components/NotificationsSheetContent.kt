package com.app.koshpal.app.presentation.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.R
import com.app.koshpal.app.presentation.globalcomponents.FilterToggleCard
import com.app.koshpal.app.viewmodels.profileviewmodel.ProfileViewModel
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit

@Composable
fun NotificationsSheetContent(viewModel: ProfileViewModel, onClose: () -> Unit) {
    val incomingTransactionsNotif by viewModel.incomingTransactionsNotif.collectAsStateWithLifecycle(initialValue = true)
    val budgetAlertsNotif by viewModel.budgetAlertsNotif.collectAsStateWithLifecycle(initialValue = true)
    val duesRemindersNotif by viewModel.duesRemindersNotif.collectAsStateWithLifecycle(initialValue = true)
    val goalsProgressNotif by viewModel.goalsProgressNotif.collectAsStateWithLifecycle(initialValue = true)

    val allEnabled = remember(incomingTransactionsNotif, budgetAlertsNotif, duesRemindersNotif, goalsProgressNotif) {
        incomingTransactionsNotif && budgetAlertsNotif && duesRemindersNotif && goalsProgressNotif
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
        .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = Jakarta,
                textAlign = TextAlign.Center
            )
            Card(
                modifier = Modifier.size(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                onClick = { onClose() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(R.drawable.close_24px),
                        contentDescription = null
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Column(
            Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (allEnabled) "You can modify app's notification permissions here" 
                       else "You have disallowed Koshpal from sending you notifications.",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Jakarta,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            if (!allEnabled) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Allow notifications from koshpal",
                            fontFamily = Outfit,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        FilterToggleCard(
            label = "Transactions",
            icon = R.drawable.account_balance_24px,
            checked = incomingTransactionsNotif,
            onCheckedChange = { viewModel.toggleIncomingTransactionsNotif(it) }
        )
        Spacer(Modifier.height(12.dp))
        FilterToggleCard(
            label = "Budget related",
            icon = R.drawable.account_balance_wallet_24px, 
            checked = budgetAlertsNotif,
            onCheckedChange = { viewModel.toggleBudgetAlertsNotif(it) }
        )
        Spacer(Modifier.height(12.dp))
        FilterToggleCard(
            label = "Dues & Reminders",
            icon = R.drawable.notifications_24px, 
            checked = duesRemindersNotif,
            onCheckedChange = { viewModel.toggleDuesRemindersNotif(it) }
        )
        Spacer(Modifier.height(12.dp))
        FilterToggleCard(
            label = "Goals progress",
            icon = R.drawable.flag_24px, 
            checked = goalsProgressNotif,
            onCheckedChange = { viewModel.toggleGoalsProgressNotif(it) }
        )
        Spacer(Modifier.height(32.dp))
    }
}
