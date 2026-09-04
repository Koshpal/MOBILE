package com.app.koshpal.app.presentation.home.component


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.Transaction
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.ui.theme.Jakarta
import java.text.NumberFormat

@Composable
fun TransactionsCard(
    recentTransactions: Transactions,
    formatter: NumberFormat,
    onToAllTransactions: () -> Unit,
    transactionItem: @Composable (Transaction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Transactions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Jakarta
                )
                Card(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    onClick = onToAllTransactions,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
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
            Spacer(modifier = Modifier.height(16.dp))
            if (recentTransactions.transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No recent transactions", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                recentTransactions.transactions.forEach { transaction ->
                    transactionItem(transaction)
                    if (transaction != recentTransactions.transactions.last()) {
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}
