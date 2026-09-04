package com.app.koshpal.app.presentation.budget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.presentation.budget.component.BudgetOverviewCard
import com.app.koshpal.app.presentation.budget.component.DetailedCategoryCard
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetViewModel
import com.app.koshpal.core.data.entities.enums.toReadableString
import com.app.koshpal.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedBudgetScreen(
    modifier: Modifier = Modifier,
    viewModel: BudgetViewModel,
    onToPreviousScreen: () -> Unit,
    onToSettings: () -> Unit,
    onToCreateBudget: () -> Unit = {}
) {
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()
    val historyBudgets by viewModel.historyBudgets.collectAsStateWithLifecycle()
    val clickedBudgetId by viewModel.clickedBudgetId.collectAsStateWithLifecycle()
    val showHistory by viewModel.showHistory.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val selectAll by viewModel.selectAll.collectAsStateWithLifecycle()
    val selectedCategories by viewModel.selectedCategories.collectAsStateWithLifecycle()

    val budget = remember(clickedBudgetId, budgets, historyBudgets) {
        (budgets + historyBudgets).find { it.id == clickedBudgetId }
    }

    if (budget == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val totalAmount = budget.amount
    val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }
    val range = remember(budget) { budget.getDateRange() }
    val showHidden by viewModel.showHidden.collectAsStateWithLifecycle()
    val hiddenCategoryIds by viewModel.hiddenCategoryIds.collectAsStateWithLifecycle()
    val excludedCategoryIds by viewModel.excludedCategoryIds.collectAsStateWithLifecycle()
    val isIndividualEditing by viewModel.isIndividualEditing.collectAsStateWithLifecycle()
    val updateIsIndividualEditing = viewModel::updateIsIndividualEditing

    val totalSpent by if (range != null) {
        viewModel.getSpentAmountForBudget(budget).collectAsStateWithLifecycle(initialValue = 0.0)
    } else {
        remember { mutableDoubleStateOf(0.0) }
    }

    val amountLeft = (totalAmount - totalSpent).coerceAtLeast(0.0)
    val progressPercentage = if (totalAmount > 0) ((totalSpent / totalAmount) * 100).toInt().coerceIn(0, 100) else 0
    val parentCategories = budget.categories.filter { it.parentCategoryId == null }
    val segments = parentCategories.mapNotNull { category ->
        val allocation = budget.allocations.find { it.categoryId == category.id }
        if (allocation != null && budget.amount > 0) {
            RingChartSegment(
                color = Color(category.colorHex.toColorLong()),
                percentage = (allocation.allocatedAmount / budget.amount).toFloat()
            )
        } else null
    }


    val isAnySelectedHidden by viewModel.isAnySelectedHidden.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )
    val scope = rememberCoroutineScope()
    val options = remember(selectAll, isAnySelectedHidden, selectedCategories.size) {
        listOf(
            SelectedOptions(
                title = if(selectAll) "Deselect all" else "Select All",
                icon = if(selectAll) R.drawable.cancel_24px else R.drawable.check_circle_24px,
                action = {
                    viewModel.updateSelectAll(!selectAll)
                }
            ),
            SelectedOptions(
                title = "Delete the selected categories.",
                icon = R.drawable.delete_24px_2,
                action = {
                    viewModel.excludeSelection()
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                }
            )
        )
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

    SetStatusBarAppearance(isDarkIcons = true)
    SetStatusBarVisibility(isVisible = isStatusBarVisible)

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 160.dp,
        sheetDragHandle = {
            if (isEditing) {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.outline
                )
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
                        .fillMaxHeight(0.5f)
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
                                    text = "${selectedCategories.size} Categories Selected",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color =  MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "₹${budget.amount.toLong()}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = Outfit,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
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
            } else {
                Box(Modifier.fillMaxWidth().height(1.dp))
            }
        }
    ) { _ ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(0.2f))
                .nestedScroll(nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.size(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp),
                        onClick = {
                            viewModel.resetEditingState()
                            onToPreviousScreen()
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                tint = MaterialTheme.colorScheme.primary,
                                painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                                contentDescription = "Back"
                            )
                        }
                    }
                    Text(
                        text = budget.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = Jakarta
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (showHistory) {
                            Card(
                                modifier = Modifier.size(26.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                onClick = {
                                    viewModel.prepareCloneBudget(budget.id)
                                    onToCreateBudget()
                                }
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.primary,
                                    painter = painterResource(id = R.drawable.repeat_one_24px),
                                    contentDescription = "Clone"
                                )
                            }
                        } else {
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
                                        tint = MaterialTheme.colorScheme.primary,
                                        painter = painterResource(id = if (isEditing) R.drawable.close_24px else R.drawable.edit_24px),
                                        contentDescription = "Edit"
                                    )
                                }
                            }
                            Card(
                                modifier = Modifier.size(26.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                onClick = { onToSettings() }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        tint = MaterialTheme.colorScheme.primary,
                                        painter = painterResource(id = R.drawable.settings_24px),
                                        contentDescription = "Settings",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.size(100.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    shape = CircleShape,
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = budget.title.getInitials(),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = Jakarta
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = budget.budgetType.toReadableString(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = Jakarta
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    budget.categories.filter { it.parentCategoryId == null }.take(5).forEach { category ->
                        val baseColor = try {
                            Color(category.colorHex.toColorLong())
                        } catch (_: Exception) {
                            MaterialTheme.colorScheme.primary
                        }
                        Surface(
                            modifier = Modifier.size(32.dp),
                            color = baseColor.copy(alpha = 0.2f),
                            shape = CircleShape,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val iconRes = category.iconResId?.toDrawableResId()
                                if (iconRes == null) {
                                    Text(
                                        text = category.title.getInitials(),
                                        color = baseColor,
                                        fontFamily = Jakarta,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        painter = painterResource(id = iconRes),
                                        contentDescription = null,
                                        tint = baseColor
                                    )
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
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                BudgetOverviewCard(
                    totalAmount = formatter.format(totalAmount),
                    amountLeft = formatter.format(amountLeft),
                    progressPercentage = progressPercentage,
                    segments = segments
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val visibleParentCategories = budget.categories.filter { category ->
                        category.parentCategoryId == null &&
                                !excludedCategoryIds.contains(category.id) &&
                                (showHidden || !hiddenCategoryIds.contains(category.id))
                    }

                    items(visibleParentCategories, key = { it.id }) { category ->
                        val subCategories = budget.categories.filter { it.parentCategoryId == category.id }
                        val parentAllocation = budget.allocations.find { it.categoryId == category.id }
                        val allottedAmount = parentAllocation?.allocatedAmount ?: 0.0
                        val usedAmount by viewModel.getSpentAmountForCategory(category).collectAsStateWithLifecycle(initialValue = 0.0)

                        DetailedCategoryCard(
                            category = category,
                            subCategories = subCategories,
                            allottedAmount = allottedAmount,
                            usedAmount = usedAmount,
                            formatter = formatter,
                            isEditing = isEditing,
                            isSelected = selectedCategories.contains(category.id),
                            hiddenCategoryIds = hiddenCategoryIds,
                            isIndividualEditing = isIndividualEditing,
                            updateIsIndividualEditing = updateIsIndividualEditing,
                            onDelete = { viewModel.excludeIndividualCategory(category.id) },
                            onToggleSelect = {
                                if (selectedCategories.contains(category.id)) viewModel.removeSelectedCategory(category.id)
                                else viewModel.addSelectedCategory(category.id)
                            },
                            getSubCategorySpent = { viewModel.getSpentAmountForSubCategory(it) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}
