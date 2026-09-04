package com.app.koshpal.app.presentation.budget

import android.widget.Toast
import com.app.koshpal.app.domain.model.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.Events
import com.app.koshpal.app.presentation.budget.component.BudgetRow
import com.app.koshpal.app.presentation.budget.component.CreateCategory
import com.app.koshpal.app.presentation.budget.component.EditCategorySheet
import com.app.koshpal.app.presentation.budget.component.SectionHeader
import com.app.koshpal.app.presentation.budget.component.dialog.CategorySelectionDialog
import com.app.koshpal.app.presentation.budget.component.dialog.DeleteConfirmationDialog
import com.app.koshpal.app.presentation.budget.component.dialog.MonthlyStartDatePickerDialog
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetSettingsViewModel
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.presentation.util.ObserveAsEvents
import com.app.koshpal.core.presentation.util.toDisplayDate
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetSettingsScreen(
    onToPreviousScreen: () -> Unit,
    onDeleteSuccess: () -> Unit = {},
    viewModel: BudgetSettingsViewModel
) {
    val title by viewModel.title.collectAsStateWithLifecycle()
    val period by viewModel.period.collectAsStateWithLifecycle()
    val budgetType by viewModel.budgetType.collectAsStateWithLifecycle()
    val startDate by viewModel.startDate.collectAsStateWithLifecycle()
    val endDate by viewModel.endDate.collectAsStateWithLifecycle()
    val overallAmount by viewModel.overallAmountString.collectAsStateWithLifecycle()
    val allocations by viewModel.allocations.collectAsStateWithLifecycle()
    val overAllocatedAmount by viewModel.overAllocatedAmount.collectAsStateWithLifecycle(0.0)
    val editingCategory by viewModel.editingCategory.collectAsStateWithLifecycle(null)

    val categoryColor by viewModel.categoryColor.collectAsStateWithLifecycle(availableCategoryColors.first())
    val categoryIcon by viewModel.categoryIcon.collectAsStateWithLifecycle("category")
    val categoryTitle by viewModel.categoryName.collectAsStateWithLifecycle("")
    val subCategoryColor by viewModel.subCategoryColor.collectAsStateWithLifecycle(availableCategoryColors.first())
    val subCategoryIcon by viewModel.subCategoryIcon.collectAsStateWithLifecycle("category")
    val subCategoryTitle by viewModel.subCategoryName.collectAsStateWithLifecycle("")
    val subCategoryDrafts by viewModel.subCategoryDrafts.collectAsStateWithLifecycle(emptyList())
    val showZeroAmountAlert by viewModel.showZeroAmountAlert.collectAsStateWithLifecycle()

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showSubCategoryDialog by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden || 
            scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
            viewModel.stopEditingCategory()
        }
    }

    val activeColor = try {
        Color(categoryColor.toColorLong())
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    if (showStartDatePicker) {
        Dialog(onDismissRequest = { showStartDatePicker = false }) {
            MonthlyStartDatePickerDialog(onDismiss = {
                showStartDatePicker = false
            }, onDateSelected = {
                viewModel.updateStartDate(it)
                showStartDatePicker = false
            })
        }
    }

    if (showEndDatePicker) {
        Dialog(onDismissRequest = { showEndDatePicker = false }) {
            MonthlyStartDatePickerDialog(onDismiss = {
                showEndDatePicker = false
            }, onDateSelected = {
                viewModel.updateEndDate(it)
                showEndDatePicker = false
            })
        }
    }

    val context = LocalContext.current
    val listState = rememberLazyListState()


    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                if (event.message?.contains("Category added") == true) {
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                }
                scope.launch {
                    yield()
                    if (event.message == "Budget updated") {
                        onToPreviousScreen()
                    }
                    if (allocations.isNotEmpty() && event.message?.contains("Category added") == true) {
                        listState.animateScrollToItem(allocations.size - 1)
                    }
                }
            }
            is Events.Error -> { }
            else -> {}
        }
    }

    if (showCategoryDialog) {
        Dialog(onDismissRequest = { showCategoryDialog = false }) {
            CategorySelectionDialog(
                onDismiss = { showCategoryDialog = false },
                onCategorySelected = {
                    viewModel.addCategory(it)
                    showCategoryDialog = false
                },
                onCreateNewCategoryClick = {
                    showCategoryDialog = false
                    viewModel.clearCategoryDraft()
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                }
            )
        }
    }

    if (showSubCategoryDialog) {
        Dialog(onDismissRequest = { showSubCategoryDialog = false }) {
            CategorySelectionDialog(
                onDismiss = { showSubCategoryDialog = false },
                onCategorySelected = {
                    if (editingCategory != null) {
                        viewModel.addSubCategoryToExisting(it)
                    } else {
                        viewModel.createSubCategoryDraft(presetCategory = it)
                    }
                    showSubCategoryDialog = false
                },
                categories = defaultSubCategories,
                categoryType = "sub-category",
                categoryTitle = subCategoryTitle,
                categoryColor = subCategoryColor,
                categoryIcon = subCategoryIcon,
                updateCategoryTitle = { viewModel.updateSubCategoryName(it) },
                updateCategoryIcon = { viewModel.updateSubCategoryIcon(it) },
                activeColor = activeColor,
                onCreateNewCategoryClick = {
                    viewModel.prepareSubCategoryFor(
                        parentId = editingCategory?.category?.id ?: "",
                        inheritedColor = editingCategory?.category?.colorHex ?: categoryColor
                    )
                },
                onCreateClick = {
                    if (editingCategory != null) {
                        viewModel.buildSubCategoryDraft().let {
                            viewModel.addSubCategoryToExisting(it)
                        }
                    } else {
                        viewModel.createSubCategoryDraft()
                    }
                    showSubCategoryDialog = false
                }
            )
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteBudget { 
                    onDeleteSuccess()
                    onToPreviousScreen() 
                }
            }
        )
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            if (editingCategory != null) {
                EditCategorySheet(
                    parentAllocation = editingCategory!!,
                    subAllocations = allocations.filter { it.category.parentCategoryId == editingCategory?.category?.id },
                    onCategoryAmountChange = { catId, amt ->
                        viewModel.updateCategoryAmount(catId, amt)
                    },
                    onRemoveSubCategory = { subCatId ->
                        viewModel.removeSubCategoryFromExisting(subCatId)
                    },
                    onAddSubCategoryClick = { showSubCategoryDialog = true },
                    onDoneClick = {
                        viewModel.stopEditingCategory()
                        scope.launch { scaffoldState.bottomSheetState.hide() }
                    }
                )
            } else {
                CreateCategory(
                    categoryColor = categoryColor,
                    categoryIcon = categoryIcon,
                    categoryTitle = categoryTitle,
                    updateCategoryColor = { viewModel.updateCategoryColor(it) },
                    updateCategoryIcon = { viewModel.updateCategoryIcon(it) },
                    updateCategoryTitle = { viewModel.updateCategoryName(it) },
                    activeColor = activeColor,
                    showSubCategoryDialog = remember { mutableStateOf(showSubCategoryDialog) },
                    subAllocations = subCategoryDrafts,
                    onCategoryAmountChange = { catId, amt -> viewModel.updateCategoryAmount(catId, amt) },
                    onRemoveSubCategory = { viewModel.removeSubCategoryDraft(it) },
                    isError = overAllocatedAmount > 0.0,
                    onCreateClick = {
                        viewModel.createCategoryDraft()
                    }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            BudgetSettingsContent(
                title = title,
                period = period,
                budgetType = budgetType,
                startDate = startDate,
                endDate = endDate,
                overallAmount = overallAmount,
                allocations = allocations,
                overAllocatedAmount = overAllocatedAmount,
                showZeroAmountAlert = showZeroAmountAlert ,
                onToPreviousScreen = onToPreviousScreen,
                onTitleChange = { viewModel.updateTitle(it) },
                onPeriodChange = { viewModel.updatePeriod(it) },
                onStartDateClick = { showStartDatePicker = true },
                onEndDateClick = { showEndDatePicker = true },
                onOverallAmountChange = { viewModel.updateOverallAmount(it) },
                onCategoryClick = { allocation ->
                    viewModel.startEditingCategory(allocation)
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                },
                onRemoveCategory = { viewModel.removeCategory(it) },
                onCategoryAmountChange = { id, amt -> viewModel.updateCategoryAmount(id, amt) },
                onAddCategoryClick = { showCategoryDialog = true },
                onSaveClick = {
                    viewModel.saveBudgetChanges()
                },
                onDeleteClick = {
                    showDeleteDialog = true
                }
            )
        }
    }
}

@Composable
fun BudgetSettingsContent(
    title: String,
    period: BudgetPeriod,
    budgetType: BudgetType,
    startDate: String,
    endDate: String,
    overallAmount: String,
    allocations: List<CategoryAllocationUiState>,
    overAllocatedAmount: Double,
    onToPreviousScreen: () -> Unit,
    onTitleChange: (String) -> Unit,
    onPeriodChange: (BudgetPeriod) -> Unit,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit = {},
    onOverallAmountChange: (String) -> Unit,
    onCategoryClick: (CategoryAllocationUiState) -> Unit,
    onRemoveCategory: (Category) -> Unit,
    onCategoryAmountChange: (String, String) -> Unit,
    onAddCategoryClick: () -> Unit,
    onSaveClick: () -> Unit,
    showZeroAmountAlert: Boolean = false,
    onDeleteClick: () -> Unit
) {
    val isError = overAllocatedAmount > 0.0
    val parentAllocations = remember(allocations) {
        allocations.filter { it.category.parentCategoryId == null }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.size(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { onToPreviousScreen() }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                tint = MaterialTheme.colorScheme.onSurface,
                                painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                                contentDescription = "Back"
                            )
                        }
                    }
                    Text(
                        text = "Budgets settings",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    modifier = Modifier.size(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { onDeleteClick() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onSurface,
                            painter = painterResource(id = R.drawable.delete_24px),
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        }

        item {
            Column{
                Text(
                    text = "Name your Budget",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = title,
                    onValueChange = { onTitleChange(it) },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                    ,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
            }
        }

        item {
            if (budgetType == BudgetType.RECURRING) {
                Column{
                    Text(
                        text = "Budget period",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(6.dp)
                            .selectableGroup()
                    ) {
                        BudgetPeriod.entries.forEach { option ->
                            val isSelected = option == period
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color.White else Color.Transparent)
                                    .selectable(selected = isSelected, onClick = { onPeriodChange(option) })
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option.name.uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            if (budgetType == BudgetType.ONE_TIME) {
                Column{
                    Text(
                        text = "Select date",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable { onStartDateClick() }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.calendar_month_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp).padding(end = 8.dp)
                            )
                            Text(
                                text = startDate.toDisplayDate(),
                                fontSize = 12.sp,
                                color = if (startDate != "Select a date") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable { onEndDateClick() }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.calendar_month_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp).padding(end = 8.dp)
                            )
                            Text(
                                text = if (endDate != "Select a date") endDate.toDisplayDate() else "Select a date",
                                fontSize = 12.sp,
                                color = if (endDate != "Select a date") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            } else {
                Column {
                    Text("${period.name.lowercase().replaceFirstChar { it.uppercase() }} start date", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer).clickable { onStartDateClick() }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(id = R.drawable.calendar_month_24px), contentDescription = null, modifier = Modifier.size(24.dp).padding(end = 10.dp))
                        Text(text = startDate.toDisplayDate(), fontSize = 14.sp)
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Overall budget",
                subtitle = "Set total budget without categorization",
                titleFontSize = MaterialTheme.typography.bodyMedium.fontSize,
                subtitleFontSize = MaterialTheme.typography.bodySmall.fontSize
            )
            BudgetRow(
                modifier = Modifier.padding(top = 10.dp),
                icon = R.drawable.grid_view_24px,
                iconBackground = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                label = "Overall budget",
                amount = overallAmount,
                onAmountChange = { onOverallAmountChange(it) },
            )
        }

        item {
            SectionHeader(
                title = "Category wise budget",
                subtitle = "Set budget by categories and sub-categories",
                titleFontSize = MaterialTheme.typography.bodyMedium.fontSize,
                subtitleFontSize = MaterialTheme.typography.bodySmall.fontSize
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isError) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠️ Exceeds overall budget by \u20B9${overAllocatedAmount.toLong()}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Jakarta),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            if (showZeroAmountAlert) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠️ Please enter an overall budget or category amounts before creating.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Jakarta),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        items(parentAllocations, key = { it.category.id }) { item ->
            val baseColor = try {
                Color(item.category.colorHex.toColorLong())
            } catch (_: Exception) {
                MaterialTheme.colorScheme.primary
            }

            val dismissState = rememberSwipeToDismissBoxState(
                initialValue = SwipeToDismissBoxValue.Settled,
                confirmValueChange = {
                    if (it != SwipeToDismissBoxValue.Settled) {
                        onRemoveCategory(item.category)
                        false
                    } else false
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.errorContainer),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete_24px),
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            ) {
                    val iconRes = item.category.iconResId?.toDrawableResId()
                    BudgetRow(
                        modifier = Modifier.clickable {
                            onCategoryClick(item)
                        },
                        icon = iconRes,
                    iconBackground = baseColor.copy(alpha = 0.2f),
                    iconTint = baseColor,
                    label = item.category.title,
                    amount = item.amountString,
                    onAmountChange = { newText ->
                        onCategoryAmountChange(item.category.id, newText)
                    },
                    isError = isError
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onAddCategoryClick() }.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = onAddCategoryClick
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_2_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = "Add category",
                    modifier = Modifier.padding(start = 12.dp),
                    fontFamily = Outfit,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                onClick = onSaveClick,
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SAVE CHANGES",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
