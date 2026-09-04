package com.app.koshpal.app.presentation.budget

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import com.app.koshpal.R
import com.app.koshpal.app.Events
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.presentation.budget.component.BudgetBasics
import com.app.koshpal.app.presentation.budget.component.BudgetPlanner
import com.app.koshpal.app.presentation.budget.component.BudgetTypeSelector
import com.app.koshpal.app.presentation.budget.component.CreateCategory
import com.app.koshpal.app.presentation.budget.component.EditCategorySheet
import com.app.koshpal.app.presentation.budget.component.dialog.CategorySelectionDialog
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetCreationViewModel
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.presentation.util.ObserveAsEvents
import com.app.koshpal.ui.theme.Jakarta
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetCreationScreen(
    modifier: Modifier = Modifier,
    onToPreviousScreen: () -> Unit = {},
    viewModel: BudgetCreationViewModel
) {
    val currentStep = remember { mutableIntStateOf(1) }
    val title by viewModel.title.collectAsStateWithLifecycle()
    val titleSuggestions by viewModel.titleSuggestions.collectAsStateWithLifecycle()
    val period by viewModel.period.collectAsStateWithLifecycle()
    val selectedType by viewModel.budgetType.collectAsStateWithLifecycle()
    val startDate by viewModel.startDate.collectAsStateWithLifecycle()
    val endDate by viewModel.endDate.collectAsStateWithLifecycle()
    val allocations by viewModel.allocations.collectAsStateWithLifecycle(emptyList())
    val overallAmount by viewModel.overallAmountString.collectAsStateWithLifecycle("")
    val overAllocatedAmount by viewModel.overAllocatedAmount.collectAsStateWithLifecycle(0.0)
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val isSubCategoryEditing by viewModel.isSubCategoryEditing.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val categoryColor by viewModel.categoryColor.collectAsStateWithLifecycle()
    val categoryIcon by viewModel.categoryIcon.collectAsStateWithLifecycle()
    val categoryTitle by viewModel.categoryName.collectAsStateWithLifecycle()
    val subCategoryColor by viewModel.subCategoryColor.collectAsStateWithLifecycle()
    val subCategoryIcon by viewModel.subCategoryIcon.collectAsStateWithLifecycle()
    val subCategoryTitle by viewModel.subCategoryName.collectAsStateWithLifecycle()
    val subCategoryDrafts by viewModel.subCategoryDrafts.collectAsStateWithLifecycle()
    val editingCategory by viewModel.editingCategory.collectAsStateWithLifecycle()
    val showCategoryDialog = remember { mutableStateOf(false) }
    val showSubCategoryDialog = remember { mutableStateOf(false) }
    val updateSelectedType = {newBudgetType: BudgetType -> viewModel.updateBudgetType(newBudgetType)}
    val updateTitle = {newTitle: String -> viewModel.updateTitle(newTitle)}
    val updatePeriod = {newPeriod: BudgetPeriod -> viewModel.updatePeriod(newPeriod)}
    val updateStartDate = {newStartDate: String -> viewModel.updateStartDate(newStartDate)}
    val updateIsEditing = {newIsEditing: Boolean -> viewModel.updateIsEditing(newIsEditing)}
    val updateIsSubCategoryEditing = {newIsEditing: Boolean -> viewModel.updateIsSubCategoryEditing(newIsEditing)}
    val updateCategoryTitle = {newTitle: String -> viewModel.updateCategoryName(newTitle)}
    val updateCategoryColor = {newColor: String -> viewModel.updateCategoryColor(newColor)}
    val updateCategoryIcon = {newIcon: String -> viewModel.updateCategoryIcon(newIcon)}
    val updateSubCategoryIcon = {newIcon: String -> viewModel.updateSubCategoryIcon(newIcon)}
    val updateSubCategoryTitle = {newTitle: String -> viewModel.updateSubCategoryName(newTitle)}

    BackHandler {
        viewModel.clearBudgetDraft()
        onToPreviousScreen()
    }

    val activeColor = try {
        Color(categoryColor.toColorLong())
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
            confirmValueChange = { newValue ->
                !(isEditing && newValue == SheetValue.Hidden) || !(isSubCategoryEditing)
            }
        )
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            viewModel.stopEditingCategory()
            updateIsEditing(false)
        }
    }

    if (showCategoryDialog.value){
        Dialog(onDismissRequest = { showCategoryDialog.value = false }) {
            CategorySelectionDialog(
                onDismiss = {
                    showCategoryDialog.value = false
                },
                onCategorySelected = {
                    viewModel.addCategory(it)
                    showCategoryDialog.value = false
                },
                onCreateNewCategoryClick = {
                    showCategoryDialog.value = false
                    viewModel.clearCategoryDraft()
                    viewModel.updateIsCreatingCategoryInSheet(true)
                    updateIsEditing(true)
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            )
        }
    }

    val context = LocalContext.current
    val listState = rememberLazyListState()
    val categoryScrollState = rememberScrollState()
    val imageLoader = ImageLoader.Builder(context).components { add(GifDecoder.Factory()) }.build()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                val message = event.message ?: return@ObserveAsEvents
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                if (message == "Budget created") {
                    onToPreviousScreen()
                    viewModel.clearBudgetDraft()
                }
                if (message.contains("Category added")) {
                    updateIsEditing(false)
                    scope.launch {
                        scaffoldState.bottomSheetState.hide()
                        delay(100.milliseconds)
                        val latestAllocations = viewModel.allocations.value
                        val categoryTitle = message.substringBefore(" Category added")
                        val index = latestAllocations.indexOfFirst { 
                            it.category.title.equals(categoryTitle, ignoreCase = true) && 
                            it.category.parentCategoryId == null 
                        }
                        if (index != -1) {
                            listState.animateScrollToItem(index)
                        } else {
                            val parentCount = latestAllocations.count { it.category.parentCategoryId == null }
                            if (parentCount > 0) listState.animateScrollToItem(parentCount - 1)
                        }
                    }
                }
                if (event.message == "Sub-category added") {
                    scope.launch {
                        delay(50.milliseconds)
                        categoryScrollState.animateScrollTo(Int.MAX_VALUE)
                    }
                }
            }
            is Events.Error -> { }
            else -> {}
        }
    }

    if (showSubCategoryDialog.value){
        Dialog(
            onDismissRequest = {
                showSubCategoryDialog.value = false
                updateIsSubCategoryEditing(false)
            }
        ) {
            CategorySelectionDialog(
                onDismiss = {
                    showSubCategoryDialog.value = false
                    updateIsSubCategoryEditing(false)
                },
                onCategorySelected = {
                    if (editingCategory != null) {
                        viewModel.addSubCategoryToExisting(it)
                    } else {
                        viewModel.createSubCategoryDraft(presetCategory = it)
                    }
                    showSubCategoryDialog.value = false
                    updateIsSubCategoryEditing(false)
                },
                categories = defaultSubCategories,
                categoryType = "sub-category",
                isEditing = isSubCategoryEditing,
                categoryTitle = subCategoryTitle,
                categoryColor = subCategoryColor,
                categoryIcon = subCategoryIcon,
                updateCategoryTitle = updateSubCategoryTitle,
                updateCategoryIcon = updateSubCategoryIcon,
                activeColor = activeColor,
                onCreateNewCategoryClick = {
                    viewModel.prepareSubCategoryFor(
                        parentId = editingCategory?.category?.id ?: "",
                        inheritedColor = editingCategory?.category?.colorHex ?: categoryColor
                    )
                    updateIsSubCategoryEditing(true)
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
                    updateIsSubCategoryEditing(false)
                }
            )
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 160.dp,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.outline
            )
        },
        sheetShadowElevation = 12.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            if (editingCategory != null) {
                EditCategorySheet(
                    modifier = modifier,
                    parentAllocation = editingCategory!!,
                    subAllocations = allocations.filter { it.category.parentCategoryId == editingCategory?.category?.id },
                    onCategoryAmountChange = { catId, amt ->
                        viewModel.updateCategoryAmount(catId, amt)
                    },
                    onRemoveSubCategory = { subCatId ->
                        viewModel.removeSubCategoryFromExisting(subCatId)
                    },
                    onAddSubCategoryClick = {
                        showSubCategoryDialog.value = true
                    },
                    onDoneClick = {
                        viewModel.stopEditingCategory()
                        scope.launch { scaffoldState.bottomSheetState.hide() }
                    }
                )
            } else {
                    CreateCategory(
                    modifier = modifier,
                    categoryColor = categoryColor,
                    categoryIcon = categoryIcon,
                    categoryTitle = categoryTitle,
                    updateCategoryColor = updateCategoryColor,
                    updateCategoryIcon = updateCategoryIcon,
                    updateCategoryTitle = updateCategoryTitle,
                    activeColor = activeColor,
                    showSubCategoryDialog = showSubCategoryDialog,
                    subAllocations = subCategoryDrafts,
                    onCategoryAmountChange = { catId, amt ->
                        viewModel.updateCategoryAmount(catId, amt)
                    },
                    onRemoveSubCategory = { catId ->
                        viewModel.removeSubCategoryDraft(catId)
                    },
                    isError = overAllocatedAmount > 0.0,
                    onCreateClick = {
                        viewModel.createCategoryDraft()
                    },
                    scrollState = categoryScrollState
                )
            }
        }
    ) { _ ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        onToPreviousScreen()
                        viewModel.clearBudgetDraft()
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onSurface,
                            painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                            contentDescription = "Localized description"
                        )
                    }
                }
                Text(
                    text = "Create New Budget",
                    fontSize = 24.sp,
                    fontFamily = Jakarta,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedContent(
                targetState = currentStep.intValue,
                modifier = Modifier,
                label = "StepSwapper"
            ) { step ->
                when (step) {
                    1 -> BudgetTypeSelector(
                        selectedType = selectedType,
                        updateSelectedType = updateSelectedType,
                    )
                    2 -> BudgetBasics(
                        title = title,
                        period = period,
                        startDate = startDate,
                        endDate = endDate,
                        budgetType = selectedType,
                        updateTitle = updateTitle,
                        updatePeriod = updatePeriod,
                        updateStartDate = updateStartDate,
                        updateEndDate = { viewModel.updateEndDate(it) },
                        titleSuggestions = titleSuggestions
                    )
                    3 -> BudgetPlanner(
                        overallAmount = overallAmount,
                        onOverallAmountChange = { viewModel.updateOverallAmount(it) },
                        allocations = allocations,
                        onCategoryAmountChange = { catId, amt ->
                            viewModel.updateCategoryAmount(catId, amt)
                        },
                        onRemoveCategory = { category ->
                            viewModel.removeCategory(category)
                        },
                        onCategoryClick = { allocation ->
                            viewModel.startEditingCategory(allocation)
                            scope.launch { scaffoldState.bottomSheetState.expand() }
                        },
                        showCategoryDialog = showCategoryDialog,
                        overAllocatedAmount = overAllocatedAmount,
                        viewModel = viewModel,
                        listState = listState
                    )
                    else -> {}
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep.intValue > 1) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = { currentStep.intValue -= 1 },
                        shape = CircleShape,
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PREVIOUS",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
                val isStep2Invalid = currentStep.intValue == 2 && (title.isEmpty() || startDate == "Select a date")
                if (isStep2Invalid) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        onClick = {},
                        shape = CircleShape,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CONTINUE",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    val createButtonColor = if (isLoading) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    val createButtonContentColor = if (isLoading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                    val createButtonBorder = if (isLoading) BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline) else null

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = createButtonColor
                        ),
                        onClick = {
                            if (!isLoading) {
                                if (currentStep.intValue < 3) {
                                    currentStep.intValue += 1
                                } else {
                                    viewModel.createBudget()
                                }
                            }
                        },
                        shape = CircleShape,
                        border = createButtonBorder
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isLoading && currentStep.intValue > 2) {
                                AsyncImage(
                                    modifier = Modifier.size(36.dp),
                                    model = ImageRequest.Builder(context)
                                        .data(R.drawable.loading_indicator)
                                        .build(),
                                    imageLoader = imageLoader,
                                    contentDescription = "Loading",
                                )
                            } else {
                                Text(
                                    text = if (currentStep.intValue > 2) "CREATE" else "CONTINUE",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = createButtonContentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
