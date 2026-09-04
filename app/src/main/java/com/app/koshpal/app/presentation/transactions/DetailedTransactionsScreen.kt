package com.app.koshpal.app.presentation.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.app.koshpal.app.domain.model.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.R
import com.app.koshpal.app.presentation.transactions.component.TransactionBar
import com.app.koshpal.app.viewmodels.transactionsviewmodel.DetailedTransactionViewModel
import com.app.koshpal.core.data.entities.enums.toBankDisplayName
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.ui.theme.*
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DetailedTransactionsScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailedTransactionViewModel,
    onToPreviousScreen: () -> Unit,
    onEdit: (String) -> Unit
) {
    val transaction by viewModel.transaction.collectAsStateWithLifecycle()
    val partyActivityDates by viewModel.partyActivityDates.collectAsStateWithLifecycle()
    val selectedPartyDate by viewModel.selectedPartyDate.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredDetailedTransactions.collectAsStateWithLifecycle()
    val totalAmount by viewModel.detailedHeaderAmount.collectAsStateWithLifecycle()
    val partyInsight by viewModel.partyInsight.collectAsStateWithLifecycle()
    val displayDate = selectedPartyDate ?: transaction?.transactionDate ?: System.currentTimeMillis()
    val localDate = Instant.ofEpochMilli(displayDate).atZone(ZoneId.systemDefault()).toLocalDate()

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")).apply {
        maximumFractionDigits = 0
    }

    val dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)
    val dayOnlyFormatter = DateTimeFormatter.ofPattern("dd", Locale.ENGLISH)

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


    Column(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            .background( MaterialTheme.colorScheme.primary.copy(0.2f))
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Card(
                    modifier = Modifier.size(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(16.dp),
                    onClick = onToPreviousScreen
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                val partyName = if(transaction?.type == TransactionType.INCOME) transaction?.senderName else transaction?.receiverName
                
                Text(
                    text = (transaction?.contactName ?: partyName?: "Unknown").lowercase()
                        .split(" ")
                        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = Jakarta,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.size(26.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { transaction?.id?.let { onEdit(it) } }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit_24px),
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = localDate.format(dayFormatter),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Jakarta),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        val dayOfMonth = localDate.dayOfMonth
                        val suffix = when {
                            dayOfMonth in 11..13 -> "th"
                            dayOfMonth % 10 == 1 -> "st"
                            dayOfMonth % 10 == 2 -> "nd"
                            dayOfMonth % 10 == 3 -> "rd"
                            else -> "th"
                        }
                        val formattedDate = "$dayOfMonth$suffix ${localDate.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH))}"
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = Jakarta,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                onClick = { }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                                        contentDescription = "Prev",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Card(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                onClick = { }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_forward_ios_24px),
                                        contentDescription = "Next",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        partyActivityDates.filter {
                                val dateItem = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                                dateItem.month == localDate.month && dateItem.year == localDate.year
                            }.forEach { date ->
                                val isExplicitlySelected = selectedPartyDate == date
                                val isTransactionDate = transaction?.transactionDate?.let {
                                    val tDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                                    val dDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
                                    tDate == dDate
                                } ?: false
                                val isSelected = isExplicitlySelected || (selectedPartyDate == null && isTransactionDate)
                                val dateLocalDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()

                            Card(
                                modifier = Modifier.size(36.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                                ),
                                onClick = { viewModel.onDateSelected(if (isSelected) null else date) },
                                shape = CircleShape,
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dateLocalDate.format(dayOnlyFormatter),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
                                        fontFamily = Outfit
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currencyFormatter.format(totalAmount),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = Jakarta
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${transaction?.bank.toBankDisplayName()} • ${if (transaction?.isCash == true) "Cash" else (transaction?.mode ?: "Netbanking")} |",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        fontFamily = Outfit
                    )
                    if (transaction?.type == TransactionType.INCOME) {
                        Text(
                            text = " SENDER: ${transaction?.senderName?.uppercase() ?: "UNKNOWN"} | RECEIVER: ${transaction?.receiverName?.uppercase() ?: "ME"}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.outline,
                            fontFamily = Outfit
                        )
                    } else {
                        Text(
                            text = " RECEIVER: ${transaction?.receiverName?.uppercase() ?: "UNKNOWN"}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.outline,
                            fontFamily = Outfit
                        )
                    }
                }
            }
            HorizontalDivider(
                thickness = 0.5.dp, 
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (partyInsight != null) {
                val (categoryName, percentage) = partyInsight!!
                val category = remember(categoryName) {
                    (defaultDialogCategories + defaultSubCategories).find { it.title.equals(categoryName, ignoreCase = true) }
                }
                
                Card(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val iconRes = category?.iconResId?.toDrawableResId() ?: R.drawable.category_24px
                        val iconColor = category?.colorHex?.let { Color(it.toColorLong()) } ?: MaterialTheme.colorScheme.primary
                        
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = iconColor
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = Outfit
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.outline,
                            fontFamily = Outfit
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredTransactions) { txn ->
                    val categoryName = viewModel.getCategoryName(txn.budgetId, txn.categoryId)
                    val tagName = viewModel.getTagName(txn.tagIds.firstOrNull())
                    val classificationName = txn.resolveClassificationName(categoryName, tagName)

                    TransactionBar(
                        transaction = txn,
                        formatter = currencyFormatter,
                        classificationName = classificationName,
                        onTransactionClick = {}
                    )
                }
            }
        }
    }

}
