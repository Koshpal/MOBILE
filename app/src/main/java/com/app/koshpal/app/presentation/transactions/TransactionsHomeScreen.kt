package com.app.koshpal.app.presentation.transactions

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.Events
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.states.SyncStatus
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.app.presentation.transactions.component.TransactionBarSection
import com.app.koshpal.app.presentation.transactions.component.TransactionFilterSection
import com.app.koshpal.app.viewmodels.transactionsviewmodel.TransactionsViewModel
import com.app.koshpal.ui.theme.*
import java.text.NumberFormat
import java.util.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionsHomeScreen(
    viewModel: TransactionsViewModel,
    onToPreviousScreen: () -> Unit,
    onTransactionClick: (Transaction) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val groupedTransactions by viewModel.groupedTransactions.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedIds.collectAsStateWithLifecycle()
    val selectAll by viewModel.selectAll.collectAsStateWithLifecycle()
    
    val isFilterVisible by viewModel.isFilterVisible.collectAsStateWithLifecycle()
    val typeFilter by viewModel.typeFilter.collectAsStateWithLifecycle()
    val showBookmarked by viewModel.showBookmarked.collectAsStateWithLifecycle()
    val showCash by viewModel.showCash.collectAsStateWithLifecycle()
    val showWithNotes by viewModel.showWithNotes.collectAsStateWithLifecycle()
    val showWithReceipts by viewModel.showWithReceipts.collectAsStateWithLifecycle()
    val showWithoutPayorPayee by viewModel.showWithoutPayorPayee.collectAsStateWithLifecycle()
    val showExcludedFromCashFlow by viewModel.showExcludedFromCashFlow.collectAsStateWithLifecycle()
    val startDate by viewModel.startDate.collectAsStateWithLifecycle()
    val endDate by viewModel.endDate.collectAsStateWithLifecycle()
    val availableBounds by viewModel.availableDateBounds.collectAsStateWithLifecycle()

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

    SetStatusBarAppearance(isDarkIcons = false)
    SetStatusBarVisibility(isVisible = isStatusBarVisible)

    val infiniteTransition = rememberInfiniteTransition(label = "SyncRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RotationAngle"
    )

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")).apply {
        maximumFractionDigits = 0
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            viewModel.updateIsEditing(false)
            viewModel.updateIsFilterVisible(false)
        }
    }
    
    val context = LocalContext.current
    LaunchedEffect(viewModel.events) {
        viewModel.events.collectLatest { event ->
            val message = when(event) {
                is Events.Success -> event.message
                is Events.Error -> event.message
                else -> null
            }
            message?.let { msg ->
                Toast.makeText(context, msg as CharSequence, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(isEditing, isFilterVisible) {
        if (isEditing || isFilterVisible) {
            scaffoldState.bottomSheetState.expand()
        } else {
            scaffoldState.bottomSheetState.hide()
        }
    }

    val options = listOf(
        SelectedOptions(
            title = if (selectAll) "Deselect all" else "Select All",
            icon = if (selectAll) R.drawable.close_24px else R.drawable.check_circle_24px,
            action = {
                viewModel.updateSelectAll(!selectAll)
            }
        ),
        SelectedOptions(
            title = "Delete the selected transactions.",
            icon = R.drawable.delete_24px,
            action = {
                viewModel.deleteSelection()
            }
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 160.dp,
        sheetDragHandle = {
            if (isEditing || isFilterVisible) {
                BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outline)
            }
        },
        sheetShadowElevation = 12.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            if (isEditing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${selectedIds.size} ${if(selectedIds.size== 1) "Transaction" else "Transactions"} Selected",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color =  MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    options.forEach { option ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            ),
                            onClick = { option.action() },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize()
                                    .padding(horizontal = 14.dp),
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
            } else if (isFilterVisible) {
                TransactionFilterSection(
                    typeFilter = typeFilter,
                    onTypeFilterChange = viewModel::updateTypeFilter,
                    showBookmarked = showBookmarked,
                    onShowBookmarkedChange = viewModel::updateShowBookmarked,
                    showCash = showCash,
                    onShowCashChange = viewModel::updateShowCash,
                    showWithNotes = showWithNotes,
                    onShowWithNotesChange = viewModel::updateShowWithNotes,
                    showWithReceipts = showWithReceipts,
                    onShowWithReceiptsChange = viewModel::updateShowWithReceipts,
                    showWithoutPayorPayee = showWithoutPayorPayee,
                    onShowWithoutPayorPayeeChange = viewModel::updateShowWithoutPayorPayee,
                    showExcludedFromCashFlow = showExcludedFromCashFlow,
                    onShowExcludedFromCashFlowChange = viewModel::updateShowExcludedFromCashFlow,
                    startDate = startDate,
                    endDate = endDate,
                    availableBounds = availableBounds,
                    onDateRangeChange = viewModel::updateDateRange,
                    modifier = Modifier.fillMaxHeight(0.8f)
                )
                Spacer(modifier = Modifier.fillMaxHeight(0.45f))
            } else {
                Box(modifier = Modifier.height(1.dp))
            }
        },
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .nestedScroll(nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
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
                                    contentDescription = "Back",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "Transactions",
                            fontSize = 24.sp,
                            fontFamily = Jakarta,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.size(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp),
                            onClick = { viewModel.syncSmsTransactions() }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    painter = painterResource(id = R.drawable.refresh_24px),
                                    contentDescription = "Sync SMS",
                                    modifier = if (syncStatus is SyncStatus.Loading) {
                                        Modifier.size(24.dp).rotate(rotation)
                                    } else {
                                        Modifier.size(24.dp)
                                    }
                                )
                            }
                        }
                        Card(
                            modifier = Modifier.size(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp),
                            onClick = { onTransactionClick(Transaction(id = "", accountId = "", amount = 0.0, type = TransactionType.EXPENSE, category = "Uncategorized", subCategory = "", source = "Manual", description = "", transactionDate = System.currentTimeMillis(), senderName = "", receiverName = "", bank = "", maskedAccountNo = 0, provider = "Manual")) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    painter = painterResource(id = R.drawable.add_circle_24px),
                                    contentDescription = "Add Transaction",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Card(
                            modifier = Modifier.size(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp),
                            onClick = { 
                                val newEditing = !isEditing
                                viewModel.updateIsEditing(newEditing)
                                scope.launch {
                                    if (newEditing) scaffoldState.bottomSheetState.expand()
                                    else scaffoldState.bottomSheetState.hide()
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    painter = painterResource(id = if (isEditing) R.drawable.close_24px else R.drawable.edit_24px),
                                    contentDescription = "Edit",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))
                            .border(
                                BorderStroke(
                                    0.5.dp,
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f)
                                ), RoundedCornerShape(16.dp)
                            ),
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            modifier = Modifier.fillMaxSize(),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 18.sp,
                                fontFamily = Outfit
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            placeholder = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.search_24px),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Search payments, tags, notes...",
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                        fontFamily = Outfit,
                                        fontSize = 16.sp
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
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = { }
                            ),
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Card(
                        modifier = Modifier
                            .size(52.dp)
                            .clickable {
                                val newFilter = !isFilterVisible
                                viewModel.updateIsFilterVisible(newFilter)
                                scope.launch {
                                    if (newFilter) scaffoldState.bottomSheetState.expand()
                                    else scaffoldState.bottomSheetState.hide()
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = if(isFilterVisible) R.drawable.close_24px else R.drawable.tune_24px),
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(4.dp)
                ) {
                    listOf("All", "By Tags").forEach { tab ->
                        val isSelected = selectedTab == tab
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { viewModel.onTabSelect(tab) },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = Outfit
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp),
            ) {
                TransactionBarSection(
                    groupedTransactions = groupedTransactions,
                    currencyFormatter = currencyFormatter,
                    isEditing = isEditing,
                    selectedIds = selectedIds,
                    onSelectItem = { id -> 
                        if (selectedIds.contains(id)) viewModel.removeSelectedItem(id)
                        else viewModel.addSelectedItem(id)
                    },
                    onTransactionClick = onTransactionClick,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
