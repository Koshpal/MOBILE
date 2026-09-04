package com.app.koshpal.app.presentation.dues

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.app.koshpal.app.domain.model.SelectedOptions
import com.app.koshpal.app.presentation.dues.components.DuesBarSection
import com.app.koshpal.app.presentation.dues.components.DuesFilterSection
import com.app.koshpal.app.viewmodels.duesviewmodel.DuesViewModel
import com.app.koshpal.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import com.app.koshpal.app.Events
import com.app.koshpal.app.presentation.globalcomponents.LocalBottomBarVisibility
import com.app.koshpal.core.presentation.util.ObserveAsEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuesHomeScreen(
    viewModel: DuesViewModel,
    onToPreviousScreen: () -> Unit,
    onToAddDue: () -> Unit,
    onToDetailedDue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                Toast.makeText(context, event.message ?: "Success", Toast.LENGTH_SHORT).show()
            }
            is Events.Error -> {
                Toast.makeText(context, event.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
    val isBottomSheetActive by viewModel.isBottomSheetActive.collectAsStateWithLifecycle()
    val bottomBarVisibility = LocalBottomBarVisibility.current
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val filteredDues by viewModel.filteredDues.collectAsStateWithLifecycle()
    val totalUpcomingAmount by viewModel.totalUpcomingAmount.collectAsStateWithLifecycle()
    val totalOverdueAmount by viewModel.totalOverdueAmount.collectAsStateWithLifecycle()
    val selectedItem by viewModel.selectedItem.collectAsStateWithLifecycle()
    val selectAll by viewModel.selectAll.collectAsStateWithLifecycle()
    val showCompletedReminders by viewModel.showCompletedReminders.collectAsStateWithLifecycle()
    val isFilterVisible by viewModel.isFilterVisible.collectAsStateWithLifecycle()
    val filterDate by viewModel.filterDate.collectAsStateWithLifecycle()
    val suggestions by viewModel.searchSuggestions.collectAsStateWithLifecycle()

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

    val options = listOf(
        SelectedOptions(
            title = if(selectAll) "Deselect all" else "Select All",
            icon = if(selectAll) R.drawable.cancel_24px else R.drawable.check_circle_24px,
            action = {
                viewModel.updateSelectAll(!selectAll)
            }
        ),
        SelectedOptions(
            title = "Delete the selected reminders.",
            icon = R.drawable.delete_24px_2,
            action = {
                viewModel.excludeSelection()
                scope.launch { scaffoldState.bottomSheetState.hide() }
            }
        )
    )

    LaunchedEffect(isBottomSheetActive) {
        bottomBarVisibility.value = !isBottomSheetActive
    }


    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 160.dp,
        sheetDragHandle = {
            if (isEditing || isFilterVisible) {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        sheetShadowElevation = 12.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            if (isFilterVisible) {
                DuesFilterSection(
                    selectedDate = filterDate,
                    onDateSelected = { viewModel.updateFilterDate(it) },
                    showCompleted = showCompletedReminders,
                    onToggleCompleted = { viewModel.toggleShowCompletedReminders() }
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
                                    text = "${selectedItem.size} ${if(selectedItem.size == 1) "Reminder" else "Reminders"} Selected",
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
                    modifier = Modifier
                        .fillMaxWidth(),
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
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Dues & Reminder",
                        fontSize = 24.sp,
                        fontFamily = Jakarta,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.size(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            onClick = {
                                viewModel.clearReminderForm()
                                onToAddDue()
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    painter = painterResource(id = R.drawable.add_circle_24px),
                                    contentDescription = "Add Due"
                                )
                            }
                        }
                        Card(
                            modifier = Modifier.size(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            onClick = {
                                if (isEditing) {
                                    viewModel.resetEditingState()
                                    scope.launch { scaffoldState.bottomSheetState.hide() }
                                } else {
                                    viewModel.updateIsEditing(true)
                                    scope.launch { scaffoldState.bottomSheetState.partialExpand() }
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
                                    contentDescription = "Edit"
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                        0.6.dp,
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f)
                                    ),
                                    RoundedCornerShape(16.dp)
                                ),
                        ) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                modifier = Modifier.fillMaxSize(),
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 18.sp,
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
                                            "Search dues & reminders",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontFamily = Outfit,
                                            fontSize = 18.sp
                                        )
                                    }
                                },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
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
                            modifier = Modifier.size(52.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                            ),
                            onClick = {
                                if (isFilterVisible) {
                                    viewModel.updateIsFilterVisible(false)
                                    scope.launch { scaffoldState.bottomSheetState.hide() }
                                } else {
                                    viewModel.updateIsFilterVisible(true)
                                    scope.launch { scaffoldState.bottomSheetState.expand() }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(id = if (isFilterVisible) R.drawable.close_24px else R.drawable.tune_24px),
                                    contentDescription = "Filter",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                    if (suggestions.isNotEmpty() && searchQuery.isEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(suggestions) { suggestion ->
                                AssistChip(
                                    onClick = { viewModel.updateSearchQuery(suggestion) },
                                    label = {
                                        Text(
                                            text = suggestion,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                                        labelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    border = AssistChipDefaults.assistChipBorder(
                                        enabled = true,
                                        borderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(if(searchQuery.isEmpty()) 8.dp else 16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(4.dp)
                ) {
                    listOf("upcoming" to "Upcoming", "overdue" to "Overdue").forEach { (tab, label) ->
                        val isSelected = selectedTab == tab
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { viewModel.updateSelectedTab(tab) },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
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
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val amountText = if (selectedTab == "upcoming") "Total Upcoming Amount" else "Total Overdue Amount"
                    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"))
                    Text(
                        text = amountText,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = Outfit,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "₹${formatter.format(if (selectedTab == "upcoming") totalUpcomingAmount else totalOverdueAmount)}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = Outfit,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                DuesBarSection(
                    dues = filteredDues,
                    viewModel = viewModel,
                    onToDetailedDue = onToDetailedDue
                )
            }
        }
    }
}
