package com.app.koshpal.app.presentation.goals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.presentation.globalcomponents.RingChart
import com.app.koshpal.app.presentation.globalcomponents.LocalBottomBarVisibility
import com.app.koshpal.app.presentation.goals.component.GoalFilterSection
import com.app.koshpal.app.presentation.goals.component.GoalsBarSection
import com.app.koshpal.app.presentation.goals.component.dialog.AddRemoveFundsDialog
import com.app.koshpal.app.viewmodels.goalsviewmodel.GoalViewModel
import com.app.koshpal.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsHomeScreen(
    modifier: Modifier = Modifier,
    onToPreviousScreen: () -> Unit = {},
    onToCreateGoal: () -> Unit = {},
    onToDetailedGoal: () -> Unit = {},
    viewModel: GoalViewModel
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val totalAmountSaved by viewModel.totalAmountSaved.collectAsStateWithLifecycle()
    val achievementPercentage by viewModel.achievementPercentage.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val isIndividualEditing by viewModel.isIndividualEditing.collectAsStateWithLifecycle()
    val isFilterVisible by viewModel.isFilterVisible.collectAsStateWithLifecycle()
    val selectedItem by viewModel.selectedItem.collectAsStateWithLifecycle()
    val selectAll by viewModel.selectAll.collectAsStateWithLifecycle()
    val totalSavedOfSelected by viewModel.totalSavedOfSelected.collectAsStateWithLifecycle()
    val showHistory by viewModel.showHistory.collectAsStateWithLifecycle()
    val filterPeriod by viewModel.filterPeriod.collectAsStateWithLifecycle()
    val filterDate by viewModel.filterDate.collectAsStateWithLifecycle()
    val isBottomSheetActive by viewModel.isBottomSheetActive.collectAsStateWithLifecycle()
    
    val bottomBarVisibility = LocalBottomBarVisibility.current
    LaunchedEffect(isBottomSheetActive) {
        bottomBarVisibility.value = !isBottomSheetActive
    }
    
    DisposableEffect(Unit) {
        onDispose {
            bottomBarVisibility.value = true
        }
    }

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
    
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")) }
    val scope = rememberCoroutineScope()
    
    var showAddRemoveDialog by remember { mutableStateOf(false) }
    var selectedGoalForFunds by remember { mutableStateOf<Goal?>(null) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            viewModel.resetEditingState()
        }
    }

    val options = remember(selectAll) {
        listOf(
            SelectedOptions(
                title = if (selectAll) "Deselect All" else "Select All",
                icon = if (selectAll) R.drawable.cancel_24px else R.drawable.check_circle_24px,
                action = { viewModel.updateSelectAll(!selectAll) }
            ),
            SelectedOptions(
                title = "Delete the selected goals.",
                icon = R.drawable.delete_24px_2,
                action = {
                    viewModel.deleteSelectedGoals()
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                }
            )
        )
    }

    if (showAddRemoveDialog && selectedGoalForFunds != null) {
        AddRemoveFundsDialog(
            goal = selectedGoalForFunds!!,
            availableBalance = 100000.0,
            onDismiss = {
                showAddRemoveDialog = false
                selectedGoalForFunds = null
            },
            onConfirm = { amount, isAdding ->
                if (isAdding) {
                    viewModel.addFunds(selectedGoalForFunds!!, amount)
                } else {
                    viewModel.removeFunds(selectedGoalForFunds!!, amount)
                }
                showAddRemoveDialog = false
                selectedGoalForFunds = null
            }
        )
    }

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
            if (isFilterVisible) {
                GoalFilterSection(
                    selectedPeriod = filterPeriod,
                    onPeriodSelected = { viewModel.updateFilterPeriod(it) },
                    selectedDate = filterDate,
                    onDateSelected = { viewModel.updateFilterDate(it) }
                )
            } else if (isEditing) {
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
                                    .wrapContentWidth()
                                    .padding(vertical = 10.dp, horizontal = 24.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${selectedItem.size} ${if(selectedItem.size == 1) "Goal" else "Goals"} Selected",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color =  MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = formatter.format(totalSavedOfSelected),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
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
                                modifier = Modifier
                                    .fillMaxSize()
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
            } else {
                Box(modifier = Modifier.height(1.dp))
            }
        }
    ) { _ ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .nestedScroll(nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.size(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { 
                                if (showHistory) {
                                    viewModel.toggleHistory()
                                } else {
                                    onToPreviousScreen()
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
                                painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                                contentDescription = "Back"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (showHistory) "Past Goals" else "Goals",
                        fontSize = 24.sp,
                        fontFamily = Jakarta,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Card(
                            modifier = Modifier.size(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp),
                            onClick = onToCreateGoal
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.add_circle_24px),
                                    contentDescription = "Add Goal",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        if (!showHistory) {
                            Card(
                                modifier = Modifier.size(26.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                shape = RoundedCornerShape(16.dp),
                                onClick = { viewModel.toggleHistory() }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.history_24px),
                                        contentDescription = "History",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                        Card(
                            modifier = Modifier.size(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp),
                            onClick = { 
                                if (isEditing) {
                                    viewModel.resetEditingState()
                                    scope.launch { scaffoldState.bottomSheetState.hide() }
                                } else {
                                    viewModel.updateIsEditing(true)
                                    scope.launch { scaffoldState.bottomSheetState.expand() }
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isEditing) R.drawable.close_24px else R.drawable.edit_24px),
                                    contentDescription = "Edit Goals",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))
                            .border(
                                BorderStroke(
                                    0.6.dp,
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f)
                                ),
                                RoundedCornerShape(16.dp)
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
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            placeholder = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.search_24px),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Search Goals",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontFamily = Outfit,
                                        fontSize = 18.sp
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
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
                        )
                    }
                    Card(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)),
                        border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f)),
                        onClick = { 
                            if (isFilterVisible) {
                                viewModel.resetEditingState()
                                scope.launch { scaffoldState.bottomSheetState.hide() }
                            } else {
                                viewModel.updateIsFilterVisible(true)
                                scope.launch { scaffoldState.bottomSheetState.expand() }
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = if (!isFilterVisible) R.drawable.tune_24px else R.drawable.close_24px),
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (showHistory) {
                    val stats by viewModel.historyStats.collectAsStateWithLifecycle()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HistoryStatItem("Goals completed", stats.completedCount.toString())
                        VerticalDivider(modifier = Modifier
                            .height(40.dp)
                            .width(0.5.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        HistoryStatItem("Total achieved", "₹${formatter.format(stats.totalAchieved)}")
                        VerticalDivider(modifier = Modifier
                            .height(40.dp)
                            .width(0.5.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        HistoryStatItem("Avg. completion", "${String.format(Locale.ENGLISH, "%.1f", stats.avgCompletionMonths)} mo")
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val segments = remember(goals) {
                            val totalTarget = goals.sumOf { it.targetAmount }
                            if (totalTarget > 0) {
                                goals.map { goal ->
                                    RingChartSegment(
                                        color = Color(goal.colorHex.toColorLong()),
                                        percentage = (goal.targetAmount / totalTarget).toFloat()
                                    )
                                }
                            } else emptyList()
                        }
                        RingChart(
                            segments = segments,
                            centerLabel = "Achieved",
                            centerValue = "$achievementPercentage%",
                            modifier = Modifier.size(110.dp)
                        )
                        Spacer(modifier = Modifier.width(36.dp))
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "TOTAL AMOUNT SAVED",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatter.format(totalAmountSaved),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Jakarta
                            )
                        }
                    }
                }
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                GoalsBarSection(
                    modifier = Modifier.fillMaxSize(),
                    goals = goals,
                    isEditing = isEditing,
                    isIndividualEditing = isIndividualEditing,
                    updateIsIndividualEditing = viewModel::updateIsIndividualEditing,
                    showHistory = showHistory,
                    selectedItem = selectedItem,
                    addSelectedItem = viewModel::addSelectedItem,
                    removeSelectedItem = viewModel::removeSelectedItem,
                    onAddRemoveFunds = { goal ->
                        selectedGoalForFunds = goal
                        showAddRemoveDialog = true
                    },
                    onDeleteGoal = { goal -> viewModel.deleteGoal(goal) },
                    onGoalClick = { goal ->
                        viewModel.updateClickedGoalId(goal.id)
                        onToDetailedGoal()
                    },
                    onCreateSimilarGoal = { goal ->
                        viewModel.prepareEditGoal(goal)
                        onToCreateGoal()
                    }
                )
            }
        }
    }
}

@Composable
fun HistoryStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = Outfit,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Jakarta,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
