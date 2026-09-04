package com.app.koshpal.app.presentation.transactions.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.R
import com.app.koshpal.app.presentation.globalcomponents.FilterToggleCard
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterSection(
    typeFilter: String,
    onTypeFilterChange: (String) -> Unit,
    showBookmarked: Boolean,
    onShowBookmarkedChange: (Boolean) -> Unit,
    showCash: Boolean,
    onShowCashChange: (Boolean) -> Unit,
    showWithNotes: Boolean,
    onShowWithNotesChange: (Boolean) -> Unit,
    showWithReceipts: Boolean,
    onShowWithReceiptsChange: (Boolean) -> Unit,
    showWithoutPayorPayee: Boolean,
    onShowWithoutPayorPayeeChange: (Boolean) -> Unit,
    showExcludedFromCashFlow: Boolean,
    onShowExcludedFromCashFlowChange: (Boolean) -> Unit,
    startDate: Long?,
    endDate: Long?,
    availableBounds: Pair<Long, Long>,
    onDateRangeChange: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayDateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH) }
    val yearFormatter = remember { DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Transaction Type",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = Jakarta,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(6.dp)
        ) {
            listOf("Outgoing", "Incoming", "Both").forEach { option ->
                val isSelected = typeFilter == option
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                    ),
                    onClick = { onTypeFilterChange(option) },
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Show transactions",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = Jakarta,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterToggleCard(
                label = "Bookmarked",
                icon = R.drawable.bookmark_24px,
                checked = showBookmarked,
                onCheckedChange = onShowBookmarkedChange
            )
            FilterToggleCard(
                label = "Cash transactions",
                icon = R.drawable.universal_currency_alt_24px,
                checked = showCash,
                onCheckedChange = onShowCashChange
            )
            FilterToggleCard(
                label = "With notes",
                icon = R.drawable.description_24px,
                checked = showWithNotes,
                onCheckedChange = onShowWithNotesChange
            )
            FilterToggleCard(
                label = "With receipts",
                icon = R.drawable.receipt_long_24px,
                checked = showWithReceipts,
                onCheckedChange = onShowWithReceiptsChange
            )
            FilterToggleCard(
                label = "Without payor/payee",
                icon = R.drawable.account_circle_off_24px,
                checked = showWithoutPayorPayee,
                onCheckedChange = onShowWithoutPayorPayeeChange
            )
            FilterToggleCard(
                label = "Excluded from Cash Flow",
                icon = R.drawable.block_24px,
                checked = showExcludedFromCashFlow,
                onCheckedChange = onShowExcludedFromCashFlowChange
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Only show transactions",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = Jakarta,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(24.dp))
        val min = availableBounds.first.toFloat()
        val max = availableBounds.second.toFloat()

        if (min < max) {
            val currentStart = (startDate ?: availableBounds.first).toFloat().coerceIn(min, max)
            val currentEnd = (endDate ?: availableBounds.second).toFloat().coerceIn(min, max)
            
            RangeSlider(
                value = currentStart..currentEnd,
                onValueChange = { range ->
                    onDateRangeChange(range.start.toLong(), range.endInclusive.toLong())
                },
                valueRange = min..max,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                startThumb = {
                    DateRangeThumb(
                        timestamp = currentStart.toLong(),
                        displayFormatter = displayDateFormatter,
                        yearFormatter = yearFormatter
                    )
                },
                endThumb = {
                    DateRangeThumb(
                        timestamp = currentEnd.toLong(),
                        displayFormatter = displayDateFormatter,
                        yearFormatter = yearFormatter
                    )
                },
                track = { rangeSliderState ->
                    SliderDefaults.Track(
                        rangeSliderState = rangeSliderState,
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent
                        ),
                        modifier = Modifier.height(28.dp).clip(RoundedCornerShape(14.dp))
                    )
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (min == 0f) "No transactions found" else "Only one date available: " + Instant.ofEpochMilli(min.toLong()).atZone(ZoneId.systemDefault()).format(displayDateFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun DateRangeThumb(
    timestamp: Long,
    displayFormatter: DateTimeFormatter,
    yearFormatter: DateTimeFormatter
) {
    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
    val dayMonth = date.format(displayFormatter).uppercase()
    val year = date.format(yearFormatter)

    Card(
        modifier = Modifier.size(width = 80.dp, height = 56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayMonth,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Outfit
            )
            Text(
                text = year,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Outfit
            )
        }
    }
}
