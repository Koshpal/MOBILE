package com.app.koshpal.app.presentation.home.component


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.presentation.util.toDisplayDate
import com.app.koshpal.ui.theme.ErrorRedDark
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.SuccessGreen
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

@Composable
fun HomeTransactionItem(
    transaction: Transaction,
    formatter: NumberFormat,
    classificationName: String,
    onClick: () -> Unit
) {
    val isUncategorized = transaction.budgetId == null && transaction.tagIds.isEmpty()
    val partyName = if(transaction.type == TransactionType.INCOME) transaction.senderName else transaction.receiverName
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryFixed)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .size(40.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isUncategorized) {
                    Text(
                        text = "!",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        fontFamily = Jakarta
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.call_made_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp).rotate(if (transaction.type == TransactionType.INCOME) 90f else 0f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.contactName ?: partyName.ifBlank { "Unknown" },
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Jakarta,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isUncategorized) transaction.bank else classificationName,
                fontSize = 12.sp,
                fontFamily = Outfit,
                color = MaterialTheme.colorScheme.outline
            )
        }
        Text(
            text = if (transaction.type == TransactionType.EXPENSE) "- ${formatter.format(transaction.amount)}" else "+ ${formatter.format(transaction.amount)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Jakarta,
            color = if (transaction.type == TransactionType.EXPENSE) ErrorRedDark else SuccessGreen
        )
    }
}

@Composable
fun TagDashboardChip(summary: HomeTagSummary) {
    val tagColor = Color(summary.tag.colorHex.toColorLong())
    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = tagColor.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, tagColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#",
                color = tagColor,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Jakarta
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = summary.tag.name,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Jakarta,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DueItem(
    metadata: DueWithMetadata,
    onToggleCompletion: () -> Unit = {}
) {
    val due = metadata.due
    val isExpense = due.type == TransactionType.EXPENSE.name
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")) }
    val formattedAmount = formatter.format(due.amount)
    
    val prefix = if (isExpense) "To" else "From"
    val sign = if (isExpense) "-" else "+"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.swap_horiz_24px),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = due.date.toDisplayDate(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = Jakarta,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                val daysText = when {
                    metadata.daysToGo > 0 -> "${metadata.daysToGo} days to go"
                    metadata.daysToGo < 0 -> "${abs(metadata.daysToGo)} days overdue"
                    else -> "Due today"
                }
                Text(
                    text = daysText,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = Outfit,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Text(
                text = "$prefix ${due.title}",
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = Jakarta,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$sign $formattedAmount",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Jakarta,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = if (due.isCompleted) SuccessGreen else Color.Transparent),
                    border = BorderStroke(1.dp, SuccessGreen),
                    onClick = onToggleCompletion
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.check_24px),
                            contentDescription = null,
                            tint = if (due.isCompleted) Color.White else SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
