package com.app.koshpal.app.presentation.tags


import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.app.koshpal.app.Events
import com.app.koshpal.app.domain.model.SelectedOptions
import com.app.koshpal.app.viewmodels.tagsviewmodel.TagsCreationViewModel
import com.app.koshpal.app.viewmodels.tagsviewmodel.TagsViewModel
import com.app.koshpal.app.presentation.globalcomponents.LocalBottomBarVisibility
import com.app.koshpal.app.presentation.tags.components.TagsFilterSection
import com.app.koshpal.app.presentation.tags.components.TagsBarSection
import com.app.koshpal.core.presentation.util.ObserveAsEvents
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.SetStatusBarAppearance
import com.app.koshpal.ui.theme.SetStatusBarVisibility
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsHomeScreen(
    viewModel: TagsViewModel,
    onToPreviousScreen: () -> Unit,
    onToDetailedTag: () -> Unit = {}
) {
    val tagSummaries by viewModel.filteredTags.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val showHidden by viewModel.showHidden.collectAsStateWithLifecycle()
    val isFilterVisible by viewModel.isFilterVisible.collectAsStateWithLifecycle()
    val selectedItem by viewModel.selectedItem.collectAsStateWithLifecycle()
    val selectAll by viewModel.selectAll.collectAsStateWithLifecycle()
    val isAnySelectedHidden by viewModel.isAnySelectedHidden.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val activeSheet by viewModel.activeSheet.collectAsStateWithLifecycle()
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
    
    val creationViewModel: TagsCreationViewModel = koinViewModel()

    SetStatusBarAppearance(isDarkIcons = false)
    SetStatusBarVisibility(isVisible = isStatusBarVisible)

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    ObserveAsEvents(creationViewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                if (event.message == "Tag created") {
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            viewModel.resetEditingState()
            creationViewModel.clearForm()
        }
    }

    val options = listOf(
        SelectedOptions(
            title = if(selectAll) "Deselect all" else "Select All",
            icon = if(selectAll) R.drawable.close_24px else R.drawable.check_circle_24px,
            action = {
                viewModel.updateSelectAll(!selectAll)
            }
        ),
        SelectedOptions(
            title = if (isAnySelectedHidden) "Unhide the selected tags." else "Hide the selected tags.",
            icon = if (isAnySelectedHidden) R.drawable.visibility_24px else R.drawable.visibility_off_24px,
            action = {
                viewModel.toggleSelectionHiddenState()
                scope.launch { scaffoldState.bottomSheetState.hide() }
            }
        ),
        SelectedOptions(
            title = "Delete the selected tags.",
            icon = R.drawable.delete_24px,
            action = {
                viewModel.excludeSelection()
                scope.launch { scaffoldState.bottomSheetState.hide() }
            }
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight =  160.dp,
        sheetDragHandle = {
            if (isEditing || isFilterVisible || activeSheet == "create") {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            if (activeSheet == "create") {
                val tagNameForm by creationViewModel.tagName.collectAsStateWithLifecycle()
                val tagColorForm by creationViewModel.tagColor.collectAsStateWithLifecycle()
                TagsCreationScreen(
                    tagColor = tagColorForm,
                    tagName = tagNameForm,
                    updateTagColor = { creationViewModel.updateTagColor(it) },
                    updateTagName = { creationViewModel.updateTagName(it) },
                    viewModel = creationViewModel,
                    onCreateClick = {
                        creationViewModel.createTag()
                    }
                )
            } else if (isFilterVisible) {
                TagsFilterSection(
                    showHidden = showHidden,
                    onToggleHidden = { viewModel.toggleShowHidden() }
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
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
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
                                    text = "${selectedItem.size} ${if(selectedItem.size == 1) "Tag" else "Tags"} Selected",
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
            modifier = Modifier
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
                        .fillMaxWidth()
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
                            onClick = {
                                scope.launch { scaffoldState.bottomSheetState.hide() }
                                onToPreviousScreen()
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
                        Text(
                            text = "#Tags",
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
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent,
                            ),
                            shape = RoundedCornerShape(16.dp),
                            onClick = {
                                if (activeSheet == "create") {
                                    viewModel.updateActiveSheet("")
                                    scope.launch { scaffoldState.bottomSheetState.hide() }
                                } else {
                                    viewModel.updateActiveSheet("create")
                                    creationViewModel.clearForm()
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
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    painter = painterResource(id = if (activeSheet == "create") R.drawable.close_24px else R.drawable.add_circle_24px),
                                    contentDescription = if (activeSheet == "create") "Close" else "Add"
                                )
                            }
                        }
                        Card(
                            modifier = Modifier.size(26.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent,
                            ),
                            shape = RoundedCornerShape(16.dp),
                            onClick = {
                                if (isEditing) {
                                    viewModel.updateIsEditing(false)
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
                Column(modifier = Modifier.padding(top = 16.dp)) {
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
                                            "Search tags",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontFamily = Outfit,
                                            fontSize = 18.sp
                                        )
                                    }
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
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
                                    if (isFilterVisible) {
                                        viewModel.updateIsFilterVisible(false)
                                        scope.launch { scaffoldState.bottomSheetState.hide() }
                                    } else {
                                        viewModel.updateIsFilterVisible(true)
                                        scope.launch { scaffoldState.bottomSheetState.expand() }
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
                                    painter = painterResource(id = if (isFilterVisible) R.drawable.close_24px else R.drawable.tune_24px),
                                    contentDescription = "Filter",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(4.dp)
                    ) {
                        listOf("All", "This Month", "This Week", "Last 3 Months").forEach { period ->
                            val isSelected = period == selectedPeriod
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(45.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else Color.Transparent)
                                    .clickable { viewModel.updateSelectedPeriod(period) },
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = period,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = Outfit,
                                    maxLines = 1,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                TagsBarSection(
                    modifier = Modifier.padding(16.dp),
                    tagSummaries = tagSummaries,
                    isEditing = isEditing,
                    selectedItem = selectedItem,
                    viewModel = viewModel,
                    onToDetailedTag = onToDetailedTag
                )
            }
        }
    }
}
