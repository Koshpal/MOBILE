package com.app.koshpal.app.presentation.cash


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.SelectedOptions
import com.app.koshpal.app.domain.model.resolveClassificationName
import com.app.koshpal.app.presentation.transactions.component.TransactionBar
import com.app.koshpal.app.viewmodels.CashViewModel
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.SetStatusBarAppearance
import com.app.koshpal.ui.theme.SetStatusBarVisibility
import com.app.koshpal.ui.theme.TextGreen
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.text.NumberFormat
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashHomeScreen(
    viewModel: CashViewModel,
    onToPreviousScreen: () -> Unit,
    onAddCash: () -> Unit,
    onTransactionClick: (String) -> Unit
) {
    val balance by viewModel.cashBalance.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterPeriod by viewModel.filterPeriod.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedIds.collectAsStateWithLifecycle()
    val selectAll by viewModel.selectAll.collectAsStateWithLifecycle()
    val cashTrend by viewModel.cashTrend.collectAsStateWithLifecycle()
    val trendDateRange by viewModel.trendDateRange.collectAsStateWithLifecycle()

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

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Hidden, skipHiddenState = false)
    )
    val scope = rememberCoroutineScope()

    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")).apply { maximumFractionDigits = 0 } }

    var showPeriodMenu by remember { mutableStateOf(false) }

    val groupedTransactions = remember(filteredTransactions) {
        filteredTransactions.groupBy {
            val date = Instant.ofEpochMilli(it.transactionDate).atZone(ZoneId.systemDefault()).toLocalDate()
            when (date) {
                LocalDate.now() -> "Today"
                LocalDate.now().minusDays(1) -> "Yesterday"
                else -> date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH))
            }
        }
    }

    SetStatusBarAppearance(isDarkIcons = false)
    SetStatusBarVisibility(isVisible = isStatusBarVisible)

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            if (isEditing) viewModel.updateIsEditing(false)
        }
    }

    LaunchedEffect(isEditing) {
        if (isEditing) scaffoldState.bottomSheetState.expand()
        else scaffoldState.bottomSheetState.hide()
    }

    val options = remember(selectAll, selectedIds.size) {
        listOf(
            SelectedOptions(
                title = if (selectAll) "Deselect all" else "Select All",
                icon = if (selectAll) R.drawable.close_24px else R.drawable.check_circle_24px,
                action = { viewModel.updateSelectAll(!selectAll) }
            ),
            SelectedOptions(
                title = "Delete the selected transactions.",
                icon = R.drawable.delete_24px_2,
                action = { 
                    viewModel.deleteSelection()
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                }
            )
        )
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        sheetPeekHeight = 160.dp,
        sheetDragHandle = { if (isEditing) BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outline) },
        sheetShadowElevation = 12.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            if (isEditing) {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.45f).padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = CircleShape,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.5f).padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${selectedIds.size} Selected",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontFamily = Outfit
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    options.forEach { option ->
                        Card(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            onClick = { option.action() },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.outline,
                                    painter = painterResource(id = option.icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = option.title,
                                    fontSize = 14.sp,
                                    fontFamily = Outfit,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else {
                Box(modifier = Modifier.height(1.dp))
            }
        }
    ) { _ ->
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                                    contentDescription = "Back"
                                )
                            }
                        }
                        Text(
                            text = "Cash on hand",
                            fontSize = 24.sp,
                            fontFamily = Jakarta,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.size(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp),
                            onClick = onAddCash
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    painter = painterResource(id = R.drawable.add_circle_24px),
                                    contentDescription = "Add"
                                )
                            }
                        }
                        Card(
                            modifier = Modifier.size(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp),
                            onClick = { viewModel.updateIsEditing(!isEditing) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    painter = painterResource(id = if (isEditing) R.drawable.close_24px else R.drawable.edit_24px),
                                    contentDescription = "Edit"
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)),
                    border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                ) {
                    Column {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Current Balance",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = Outfit
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = currencyFormatter.format(balance),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = Jakarta
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(70.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                if (cashTrend.size > 1) {
                                    val path = Path()
                                    val fillPath = Path()
                                    val min = cashTrend.minOrNull() ?: 0.0
                                    val max = cashTrend.maxOrNull() ?: 0.0
                                    val range = (max - min).coerceAtLeast(1.0)
                                    cashTrend.forEachIndexed { i, value ->
                                        val x = i * (size.width / (cashTrend.size - 1))
                                        val y = size.height - ((value - min) / range * size.height).toFloat()
                                        if (i == 0) { path.moveTo(x, y); fillPath.moveTo(x, size.height); fillPath.lineTo(x, y) }
                                        else { path.lineTo(x, y); fillPath.lineTo(x, y) }
                                        if (i == cashTrend.lastIndex) { fillPath.lineTo(x, size.height); fillPath.close() }
                                    }
                                    drawPath(path = fillPath, brush = Brush.verticalGradient(colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent), startY = 0f, endY = size.height))
                                    drawPath(path = path, color = Color.White, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
                                } else {
                                    drawLine(color = Color.White.copy(alpha = 0.3f), start = Offset(0f, size.height), end = Offset(size.width, size.height), strokeWidth = 0.8.dp.toPx())
                                }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(start= 12.dp, end = 12.dp, top = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(trendDateRange.first, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary, fontFamily = Outfit)
                            Text(trendDateRange.second, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary, fontFamily = Outfit)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))
                                    .border(BorderStroke(0.5.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f)), RoundedCornerShape(16.dp)),
                            ) {
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.onSearchQueryChange(it) },
                                    modifier = Modifier.fillMaxSize(),
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize =  MaterialTheme.typography.bodyLarge.fontSize,
                                        fontFamily = Outfit
                                    ),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    placeholder = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.search_24px),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                "Search Transactions",
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontFamily = Outfit,
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                            )
                                        }
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.close_24px),
                                                    contentDescription = "Clear",
                                                    tint = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                        }
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    )
                                )
                            }
                            Box {
                                Surface(
                                    modifier = Modifier.height(46.dp).width(86.dp).clickable { showPeriodMenu = true },
                                    shape = RoundedCornerShape(28.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = filterPeriod,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = Jakarta,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                        Icon(painter = painterResource(id = R.drawable.keyboard_arrow_down_24px), contentDescription = null, modifier = Modifier.size(20.dp))
                                    }
                                }
                                DropdownMenu(
                                    expanded = showPeriodMenu,
                                    onDismissRequest = { showPeriodMenu = false },
                                    modifier = Modifier.width(140.dp)
                                        .background(MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    listOf("All", "This week", "This Month", "This year").forEach { period ->
                                        DropdownMenuItem(
                                            text = { Text(period, fontFamily = Outfit, fontSize = 14.sp) },
                                            onClick = {
                                                viewModel.onFilterPeriodChange(period)
                                                showPeriodMenu = false
                                            },
                                            colors = MenuDefaults.itemColors(
                                                textColor = if(filterPeriod == period) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).fillMaxSize().background(MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (filteredTransactions.isEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth().height(400.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(text = "No transactions found", color = MaterialTheme.colorScheme.onSurface, fontFamily = Outfit, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    } else {
                        groupedTransactions.forEach { (month, transactions) ->
                            val monthTotal = transactions.sumOf {
                                if (it.type == TransactionType.EXPENSE) -it.amount else it.amount
                            }
                            item {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 24.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                            text = month,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontFamily = Jakarta,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
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
                            items(transactions, key = { it.id }) { entry ->
                                val categoryName = viewModel.getCategoryName(entry.budgetId, entry.categoryId)
                                val tagName = viewModel.getTagName(entry.tagIds.firstOrNull())
                                val classificationName = entry.resolveClassificationName(categoryName, tagName)

                                TransactionBar(
                                    transaction = entry,
                                    formatter = currencyFormatter,
                                    isEditing = isEditing,
                                    isSelected = selectedIds.contains(entry.id),
                                    classificationName = classificationName,
                                    onTransactionClick = {
                                        if (isEditing) {
                                            if (selectedIds.contains(entry.id)) viewModel.removeSelectedItem(entry.id)
                                            else viewModel.addSelectedItem(entry.id)
                                        } else {
                                            onTransactionClick(entry.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
