package com.app.koshpal.app.presentation.transactions.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.presentation.globalcomponents.SwipeOrHoldActions
import com.app.koshpal.app.viewmodels.transactionsviewmodel.TransactionsViewModel
import com.app.koshpal.core.data.entities.enums.toBankDisplayName
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.presentation.util.truncateTitle
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.TextGreen
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

@Composable
fun TransactionBarSection(
    modifier: Modifier = Modifier,
    groupedTransactions: Map<String, Transactions>,
    currencyFormatter: NumberFormat,
    isEditing: Boolean = false,
    selectedIds: Set<String> = emptySet(),
    onSelectItem: (String) -> Unit = {},
    onTransactionClick: (Transaction) -> Unit,
    viewModel: TransactionsViewModel
) {
    val isIndividualEditing by viewModel.isIndividualEditing.collectAsStateWithLifecycle()
    val updateIsIndividualEditing = viewModel::updateIsIndividualEditing
    val deleteTransaction = { id: String -> viewModel.deleteTransaction(id) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        groupedTransactions.forEach { (month, transactions) ->
            val monthTotal = transactions.transactions.sumOf {
                if (it.type == TransactionType.EXPENSE) -it.amount else it.amount 
            }

            item(key = month) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$month Month",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = Outfit,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (monthTotal >= 0) "+" else "-",
                                color = if (monthTotal >= 0) TextGreen else MaterialTheme.colorScheme.error,
                                fontFamily = Outfit,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = " ${currencyFormatter.format(abs(monthTotal))}",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            items(transactions.transactions, key = { it.id }) { transaction ->
                val categoryName = viewModel.getCategoryName(transaction.budgetId, transaction.categoryId)
                val tagName = viewModel.getTagName(transaction.tagIds.firstOrNull())
                val classificationName = transaction.resolveClassificationName(categoryName, tagName)

                TransactionBar(
                    transaction = transaction,
                    formatter = currencyFormatter,
                    isEditing = isEditing,
                    isSelected = selectedIds.contains(transaction.id),
                    isIndividualEditing = isIndividualEditing,
                    classificationName = classificationName,
                    onSelectItem = { onSelectItem(transaction.id) },
                    onTransactionClick = { onTransactionClick(transaction) },
                    updateIsIndividualEditing = updateIsIndividualEditing,
                    onDeleteTransaction = { deleteTransaction(transaction.id) }
                )
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TransactionBar(
    transaction: Transaction,
    formatter: NumberFormat,
    isEditing: Boolean = false,
    isSelected: Boolean = false,
    isIndividualEditing: String = "",
    classificationName: String,
    onSelectItem: () -> Unit = {},
    onTransactionClick: () -> Unit,
    updateIsIndividualEditing: (String) -> Unit = {},
    onDeleteTransaction: () -> Unit = {}
) {
    val isUncategorized = transaction.budgetId == null && transaction.tagIds.isEmpty()
    val timeFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, h:mm a", Locale.ENGLISH) }
    val timeStr = remember(transaction.transactionDate) {
        Instant.ofEpochMilli(transaction.transactionDate)
            .atZone(ZoneId.systemDefault())
            .format(timeFormatter)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            if (isEditing && isIndividualEditing != transaction.id) {
                Column(
                    modifier = Modifier
                        .clickable { onSelectItem() }
                        .fillMaxHeight()
                        .fillMaxWidth(0.10f)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(id = if (isSelected) R.drawable.check_circle_24px_2 else R.drawable.circle_24px_2),
                        contentDescription = "Selection Icon",
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                }
                VerticalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            AnimatedVisibility(
                visible = !isEditing && isIndividualEditing == transaction.id,
                enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.12f)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    SwipeOrHoldActions(
                        icon = R.drawable.delete_24px,
                        onClick = onDeleteTransaction
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onClick = {
                            if (isIndividualEditing == transaction.id) {
                                updateIsIndividualEditing("")
                            } else if (isEditing) {
                                onSelectItem()
                            } else {
                                onTransactionClick()
                            }
                        },
                        onLongClick = {
                            if (!isEditing) updateIsIndividualEditing(transaction.id)
                        }
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.size(32.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        shape = CircleShape
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
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    fontFamily = Outfit
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.call_made_24px),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .rotate(if (transaction.type == TransactionType.INCOME) 90f else 0f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val partyName = if(transaction.type == TransactionType.INCOME) transaction.senderName else transaction.receiverName
                        Text(
                            text = (transaction.contactName ?: partyName.ifBlank { "Unknown" }).truncateTitle(20),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Jakarta,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = transaction.bank.toBankDisplayName(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = Jakarta,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = timeStr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = Jakarta,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (transaction.isCash) "Cash Transaction" else (transaction.mode ?: "Netbanking"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = Jakarta,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.End
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (transaction.type == TransactionType.INCOME) "+" else "-",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (transaction.type == TransactionType.INCOME) TextGreen else MaterialTheme.colorScheme.error,
                            fontFamily = Outfit
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatter.format(transaction.amount),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = Jakarta
                        )
                    }
                    
                    val category = if (!isUncategorized) {
                        remember(classificationName) {
                            (defaultDialogCategories + defaultSubCategories).find { 
                                it.title.equals(classificationName, ignoreCase = true) ||
                                it.title.contains(classificationName, ignoreCase = true) ||
                                classificationName.contains(it.title, ignoreCase = true) ||
                                (it.title == "Cafe & Coffee" && classificationName.contains("Coffee", ignoreCase = true))
                            }
                        }
                    } else null
                    
                    val chipColor = MaterialTheme.colorScheme.primary

                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUncategorized) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f) else chipColor.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (!isUncategorized) {
                                val iconRes = category?.iconResId?.toDrawableResId()
                                if (iconRes != null) {
                                    Icon(
                                        painter = painterResource(id = iconRes),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = chipColor
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                } else if (category != null) {
                                    val words = category.title.trim().split("\\s+".toRegex())
                                    val initials = if (words.size >= 2) "${words[0].first()}${words[1].first()}" else "${words[0].first()}"
                                    Text(
                                        text = initials.uppercase(),
                                        color = chipColor,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Jakarta
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                            Text(
                                text = if (isUncategorized) "UNCATEGORIZED" else classificationName.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Outfit,
                                color = if (isUncategorized) MaterialTheme.colorScheme.onSurface else chipColor
                            )
                        }
                    }
                }
            }
        }
    }
}
