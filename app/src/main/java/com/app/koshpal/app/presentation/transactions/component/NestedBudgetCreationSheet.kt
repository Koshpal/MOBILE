package com.app.koshpal.app.presentation.transactions.component


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.presentation.budget.component.BudgetBasics
import com.app.koshpal.app.presentation.budget.component.BudgetPlanner
import com.app.koshpal.app.presentation.budget.component.CreateCategory
import com.app.koshpal.app.presentation.budget.component.EditCategorySheet
import com.app.koshpal.app.presentation.budget.component.dialog.CategorySelectionDialog
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetCreationViewModel
import androidx.compose.ui.window.Dialog
import com.app.koshpal.app.Events
import com.app.koshpal.core.presentation.util.ObserveAsEvents
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun NestedBudgetCreationSheet(
    viewModel: BudgetCreationViewModel,
    onBudgetCreated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val creationStep by viewModel.creationStep.collectAsStateWithLifecycle(1)
    val isCreatingCategory by viewModel.isCreatingCategoryInSheet.collectAsStateWithLifecycle(false)
    val editingCategory by viewModel.editingCategory.collectAsStateWithLifecycle()
    val isSubCategoryEditing by viewModel.isSubCategoryEditing.collectAsStateWithLifecycle()
    
    val title by viewModel.title.collectAsStateWithLifecycle()
    val period by viewModel.period.collectAsStateWithLifecycle()
    val startDate by viewModel.startDate.collectAsStateWithLifecycle()
    val endDate by viewModel.endDate.collectAsStateWithLifecycle()
    val budgetType by viewModel.budgetType.collectAsStateWithLifecycle()
    val titleSuggestions by viewModel.titleSuggestions.collectAsStateWithLifecycle()
    
    val overallAmount by viewModel.overallAmountString.collectAsStateWithLifecycle("")
    val allocations by viewModel.allocations.collectAsStateWithLifecycle(emptyList())
    val overAllocatedAmount by viewModel.overAllocatedAmount.collectAsStateWithLifecycle(0.0)
    
    val categoryName by viewModel.categoryName.collectAsStateWithLifecycle()
    val categoryIcon by viewModel.categoryIcon.collectAsStateWithLifecycle()
    val categoryColor by viewModel.categoryColor.collectAsStateWithLifecycle()
    val subCategoryDrafts by viewModel.subCategoryDrafts.collectAsStateWithLifecycle()
    val subCategoryColor by viewModel.subCategoryColor.collectAsStateWithLifecycle()
    val subCategoryIcon by viewModel.subCategoryIcon.collectAsStateWithLifecycle()
    val subCategoryTitle by viewModel.subCategoryName.collectAsStateWithLifecycle()

    val showCategoryDialog = remember { mutableStateOf(false) }
    val showSubCategoryDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val listState = rememberLazyListState()
    val categoryScrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                event.message?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
                if (event.message?.contains("Category added") == true) {
                    viewModel.updateIsCreatingCategoryInSheet(false)
                    scope.launch {
                        delay(100.milliseconds)
                        val parentCount = viewModel.allocations.value.count { it.category.parentCategoryId == null }
                        if (parentCount > 0) {
                            listState.animateScrollToItem(parentCount - 1)
                        }
                    }
                }
                if (event.message == "Budget created") {
                    viewModel.lastCreatedBudgetId.value?.let { id ->
                        onBudgetCreated(id)
                    }
                }
                if (event.message == "Sub-category added") {
                    scope.launch {
                        delay(50.milliseconds)
                        categoryScrollState.animateScrollTo(Int.MAX_VALUE)
                    }
                }
            }
            is Events.Error -> {
                event.message?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(isCreatingCategory) {
        if (!isCreatingCategory && editingCategory == null) {
            viewModel.clearCategoryDraft()
        }
    }

    if (showCategoryDialog.value) {
        Dialog(onDismissRequest = { showCategoryDialog.value = false }) {
            CategorySelectionDialog(
                onDismiss = { showCategoryDialog.value = false },
                onCategorySelected = {
                    viewModel.addCategory(it)
                    showCategoryDialog.value = false
                },
                onCreateNewCategoryClick = {
                    showCategoryDialog.value = false
                    viewModel.clearCategoryDraft()
                    viewModel.updateIsCreatingCategoryInSheet(true)
                }
            )
        }
    }

    if (showSubCategoryDialog.value) {
        val activeColor = try { Color(categoryColor.toColorLong()) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
        Dialog(
            onDismissRequest = {
                showSubCategoryDialog.value = false
                viewModel.updateIsSubCategoryEditing(false)
            }
        ) {
            CategorySelectionDialog(
                onDismiss = {
                    showSubCategoryDialog.value = false
                    viewModel.updateIsSubCategoryEditing(false)
                },
                onCategorySelected = {
                    if (editingCategory != null) {
                        viewModel.addSubCategoryToExisting(it)
                    } else {
                        viewModel.createSubCategoryDraft(presetCategory = it)
                    }
                    showSubCategoryDialog.value = false
                    viewModel.updateIsSubCategoryEditing(false)
                },
                categories = defaultSubCategories,
                categoryType = "sub-category",
                isEditing = isSubCategoryEditing,
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
                    viewModel.updateIsSubCategoryEditing(true)
                },
                onCreateClick = {
                    if (editingCategory != null) {
                        viewModel.buildSubCategoryDraft().let {
                            viewModel.addSubCategoryToExisting(it)
                        }
                    } else {
                        viewModel.createSubCategoryDraft()
                    }
                    showSubCategoryDialog.value = false
                    viewModel.updateIsSubCategoryEditing(false)
                }
            )
        }
    }

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        if (editingCategory != null) {
            EditCategorySheet(
                parentAllocation = editingCategory!!,
                subAllocations = allocations.filter { it.category.parentCategoryId == editingCategory?.category?.id },
                onCategoryAmountChange = { catId, amt -> viewModel.updateCategoryAmount(catId, amt) },
                onRemoveSubCategory = { subCatId -> viewModel.removeSubCategoryFromExisting(subCatId) },
                onAddSubCategoryClick = { showSubCategoryDialog.value = true },
                onDoneClick = { viewModel.stopEditingCategory() }
            )
        } else if (isCreatingCategory) {
            val activeColor = try { Color(categoryColor.toColorLong()) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
            CreateCategory(
                categoryColor = categoryColor,
                categoryIcon = categoryIcon,
                categoryTitle = categoryName,
                updateCategoryColor = { viewModel.updateCategoryColor(it) },
                updateCategoryIcon = { viewModel.updateCategoryIcon(it) },
                updateCategoryTitle = { viewModel.updateCategoryName(it) },
                activeColor = activeColor,
                showSubCategoryDialog = showSubCategoryDialog,
                subAllocations = subCategoryDrafts,
                onCategoryAmountChange = { catId, amt -> viewModel.updateCategoryAmount(catId, amt) },
                onRemoveSubCategory = { catId -> viewModel.removeSubCategoryDraft(catId) },
                onCreateClick = {
                    viewModel.createCategoryDraft()
                },
                scrollState = categoryScrollState
            )
        } else {
            when (creationStep) {
                1 -> {
                    BudgetBasics(
                        title = title,
                        period = period,
                        startDate = startDate,
                        endDate = endDate,
                        budgetType = budgetType,
                        updateTitle = { viewModel.updateTitle(it) },
                        updatePeriod = { viewModel.updatePeriod(it) },
                        updateStartDate = { viewModel.updateStartDate(it) },
                        updateEndDate = { viewModel.updateEndDate(it) },
                        titleSuggestions = titleSuggestions
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (title.isNotBlank() && startDate != "Select a date") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        onClick = { if (title.isNotBlank() && startDate != "Select a date") viewModel.updateCreationStep(2) },
                        enabled = title.isNotBlank() && startDate != "Select a date"
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "CONTINUE",
                                color = if (title.isNotBlank() && startDate != "Select a date") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
                2 -> {
                    BudgetPlanner(
                        overallAmount = overallAmount,
                        onOverallAmountChange = { viewModel.updateOverallAmount(it) },
                        allocations = allocations,
                        onCategoryAmountChange = { id, amt -> viewModel.updateCategoryAmount(id, amt) },
                        onRemoveCategory = { viewModel.removeCategory(it) },
                        onCategoryClick = { viewModel.startEditingCategory(it) },
                        showCategoryDialog = showCategoryDialog,
                        overAllocatedAmount = overAllocatedAmount,
                        viewModel = viewModel,
                        listState = listState
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                            onClick = { viewModel.updateCreationStep(1) },
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "BACK",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            onClick = {
                                viewModel.createBudget()
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "CREATE",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
