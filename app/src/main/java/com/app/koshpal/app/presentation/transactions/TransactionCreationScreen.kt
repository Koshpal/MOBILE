package com.app.koshpal.app.presentation.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import com.app.koshpal.app.domain.model.Tag
import com.app.koshpal.app.domain.model.getInitials
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.app.presentation.budget.component.dialog.MonthlyStartDatePickerDialog
import androidx.compose.foundation.layout.FlowRow
import com.app.koshpal.app.presentation.transactions.component.NestedBudgetCreationSheet
import com.app.koshpal.app.presentation.transactions.component.dialog.BudgetSelectionDialog
import com.app.koshpal.app.presentation.transactions.component.dialog.BudgetTypeSelectionDialog
import com.app.koshpal.app.presentation.tags.TagsCreationScreen
import com.app.koshpal.app.viewmodels.transactionsviewmodel.TransactionCreationViewModel
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetCreationViewModel
import com.app.koshpal.app.viewmodels.tagsviewmodel.TagsCreationViewModel
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.presentation.util.ObserveAsEvents
import com.app.koshpal.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionCreationScreen(
    viewModel: TransactionCreationViewModel,
    onToPreviousScreen: () -> Unit
) {
    val budgetCreationViewModel: BudgetCreationViewModel = koinViewModel()
    val tagsCreationViewModel: TagsCreationViewModel = koinViewModel()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Hidden, skipHiddenState = false)
    )

    var activeSheet by remember { mutableStateOf("budget") }
    
    val context = LocalContext.current
    val transaction by viewModel.transaction.collectAsStateWithLifecycle()
    val amount by viewModel.amount.collectAsStateWithLifecycle()
    val date by viewModel.date.collectAsStateWithLifecycle()
    val senderName by viewModel.senderName.collectAsStateWithLifecycle()
    val receiverName by viewModel.receiverName.collectAsStateWithLifecycle()
    val contactName by viewModel.contactName.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val bank by viewModel.bank.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    
    val selectedBudgetType by viewModel.selectedBudgetType.collectAsStateWithLifecycle()
    val selectedBudgetId by viewModel.selectedBudgetId.collectAsStateWithLifecycle()
    val selectedParentCategoryId by viewModel.selectedParentCategoryId.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val selectedTagIds by viewModel.selectedTagIds.collectAsStateWithLifecycle()
    val selectedTransactionType by viewModel.selectedTransactionType.collectAsStateWithLifecycle()
    
    val isBookmarked by viewModel.isBookmarked.collectAsStateWithLifecycle()
    val isCash by viewModel.isCash.collectAsStateWithLifecycle()
    val hasReceipt by viewModel.hasReceipt.collectAsStateWithLifecycle()
    val isExcluded by viewModel.isExcludedFromCashFlow.collectAsStateWithLifecycle()
    val isFromNotification by viewModel.isFromNotification.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val availableBudgets by viewModel.availableBudgets.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    
    val tagColor by tagsCreationViewModel.tagColor.collectAsStateWithLifecycle()
    val tagName by tagsCreationViewModel.tagName.collectAsStateWithLifecycle()

    SetStatusBarAppearance(isDarkIcons = true)

    var showDatePicker by remember { mutableStateOf(false) }
    var showBudgetTypeDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showCategorySelectionDialog by remember { mutableStateOf(false) }
    var showSubCategorySelectionDialog by remember { mutableStateOf(false) }

    val displayDateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH) }

    androidx.compose.runtime.LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            budgetCreationViewModel.clearCategoryDraft()
            tagsCreationViewModel.clearForm()
        }
    }

    if (showDatePicker) {
        Dialog(onDismissRequest = { showDatePicker = false }) {
            MonthlyStartDatePickerDialog(
                onDismiss = { showDatePicker = false },
                onDateSelected = { dateStr ->
                    val parser = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
                    val parsedDate = try { 
                        LocalDate.parse(dateStr, parser)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    } catch (_: Exception) { System.currentTimeMillis() }
                    viewModel.onDateChange(parsedDate)
                    showDatePicker = false
                }
            )
        }
    }

    if (showBudgetTypeDialog) {
        BudgetTypeSelectionDialog(
            onDismiss = { showBudgetTypeDialog = false },
            onTypeSelected = { viewModel.onBudgetTypeSelect(it) }
        )
    }

    if (showBudgetDialog) {
        BudgetSelectionDialog(
            budgets = availableBudgets,
            onDismiss = { showBudgetDialog = false },
            onBudgetSelected = { viewModel.onBudgetSelect(it.id) },
            onCreateNewClick = {
                activeSheet = "budget"
                budgetCreationViewModel.clearBudgetDraft()
                budgetCreationViewModel.updateBudgetType(selectedBudgetType ?: BudgetType.RECURRING)
                budgetCreationViewModel.updateCreationStep(1)
                scope.launch { scaffoldState.bottomSheetState.expand() }
            }
        )
    }

    if (showCategorySelectionDialog) {
        val budget = availableBudgets.find { it.id == selectedBudgetId }
        val parentCategories = budget?.allocations?.mapNotNull { it.category }?.filter { it.parentCategoryId == null } ?: emptyList()
        
        Dialog(onDismissRequest = { showCategorySelectionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp).wrapContentHeight()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Select Category",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(parentCategories) { category ->
                            val baseColor = try { Color(category.colorHex.toColorLong()) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                            val iconRes = category.iconResId?.toDrawableResId()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.onParentCategorySelect(category.id)
                                        showCategorySelectionDialog = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Card(
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (iconRes != null) {
                                            Icon(
                                                painter = painterResource(id = iconRes),
                                                contentDescription = null,
                                                tint = baseColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else {
                                            Text(
                                                text = category.title.getInitials(),
                                                color = baseColor,
                                                fontFamily = Jakarta,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(category.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, fontFamily = Outfit)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSubCategorySelectionDialog) {
        val budget = availableBudgets.find { it.id == selectedBudgetId }
        val subCategories = budget?.allocations?.mapNotNull { it.category }?.filter { it.parentCategoryId == selectedParentCategoryId } ?: emptyList()

        Dialog(onDismissRequest = { showSubCategorySelectionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp).wrapContentHeight()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Select Sub-category",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(subCategories) { sub ->
                            val baseColor = try { Color(sub.colorHex.toColorLong()) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                            val iconRes = sub.iconResId?.toDrawableResId()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.onCategorySelect(sub.id)
                                        showSubCategorySelectionDialog = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Card(
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (iconRes != null) {
                                            Icon(
                                                painter = painterResource(id = iconRes),
                                                contentDescription = null,
                                                tint = baseColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else {
                                            Text(
                                                text = sub.title.getInitials(),
                                                color = baseColor,
                                                fontFamily = Jakarta,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(sub.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, fontFamily = Outfit)
                            }
                        }
                    }
                }
            }
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                if (event.message == "Transaction updated") {
                    onToPreviousScreen()
                }
            }
            else -> {}
        }
    }

    ObserveAsEvents(tagsCreationViewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                if (event.message == "Tag created") {
                    tagsCreationViewModel.lastCreatedTagId.value?.let { id ->
                        viewModel.onTagAdd(id)
                    }
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                }
            }
            else -> {}
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetDragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outline) },
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            if (activeSheet == "budget") {
                NestedBudgetCreationSheet(
                    viewModel = budgetCreationViewModel,
                    onBudgetCreated = { newBudgetId ->
                        viewModel.onBudgetSelect(newBudgetId)
                        scope.launch { scaffoldState.bottomSheetState.hide() }
                    }
                )
            } else {
                TagsCreationScreen(
                    tagColor = tagColor,
                    tagName = tagName,
                    updateTagColor = { tagsCreationViewModel.updateTagColor(it) },
                    updateTagName = { tagsCreationViewModel.updateTagName(it) },
                    viewModel = tagsCreationViewModel,
                    onCreateClick = {
                        tagsCreationViewModel.createTag()
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(padding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
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
                            tint = MaterialTheme.colorScheme.onSurface,
                            painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Transaction Wizard",
                    fontSize = 24.sp,
                    fontFamily = Outfit,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id =  R.drawable.bookmark_24px_2),
                    contentDescription = "Bookmark",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { viewModel.onBookmarkedToggle(!isBookmarked) },
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else IndigoMedium
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(6.dp)
                            .selectableGroup()
                    ) {
                        listOf(TransactionType.EXPENSE, TransactionType.INCOME).forEach { option ->
                            val isSelected = (selectedTransactionType ?: transaction?.type ?: TransactionType.EXPENSE) == option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color.White else Color.Transparent)
                                    .clickable { viewModel.onTransactionTypeSelect(option) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (!isFromNotification) {
                            Text(
                                text = "Contact Name",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = contactName,
                                onValueChange = { viewModel.onContactNameChange(it) },
                                placeholder = {
                                    Text(
                                        "Contact's display name",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        val currentType = selectedTransactionType ?: transaction?.type ?: TransactionType.EXPENSE
                        
                        if (currentType == TransactionType.INCOME) {
                            Text(
                                text = "Sender's Name",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = senderName,
                                onValueChange = { viewModel.onSenderNameChange(it) },
                                placeholder = { Text("Sender's name (e.g. Rahul Sharma)", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                                shape = RoundedCornerShape(16.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "Receiver's Name",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = receiverName,
                                onValueChange = { viewModel.onReceiverNameChange(it) },
                                placeholder = { Text("Receiver's name (e.g. Your Phone Number)", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                                shape = RoundedCornerShape(16.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                )
                            )
                        } else {
                            Text(
                                text = "Receiver's Name",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = receiverName,
                                onValueChange = { viewModel.onReceiverNameChange(it) },
                                placeholder = { Text("Receiver's name (e.g. Starbucks, Amazon)", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                                shape = RoundedCornerShape(16.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Bank",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = Outfit
                        )
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = bank,
                            onValueChange = { viewModel.onBankChange(it) },
                            placeholder = { Text("Bank name (e.g. HDFC, SBI)", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Transaction Mode",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = Outfit
                        )
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = mode,
                            onValueChange = { viewModel.onModeChange(it) },
                            placeholder = { Text("e.g. UPI, ATM Withdrawal, Card", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Notes",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!isCash) {
                                    Text(
                                        text = "Exclude from Cash Flow",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        fontFamily = Outfit
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Switch(
                                        checked = isExcluded,
                                        onCheckedChange = { viewModel.onExcludeToggle(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = MaterialTheme.colorScheme.surfaceVariant,
                                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                                            uncheckedThumbColor = MaterialTheme.colorScheme.surfaceVariant,
                                            uncheckedTrackColor = IndigoMedium,
                                            uncheckedBorderColor = Color.Transparent
                                        )
                                    )
                                }
                            }
                        }
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = notes,
                            onValueChange = { viewModel.onNotesChange(it) },
                            placeholder = { Text("Add any extra details...", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = isCash,
                                onClick = { viewModel.onCashToggle(!isCash) },
                                label = { Text("Cash") },
                                leadingIcon = {
                                    Icon(
                                        painterResource(id = R.drawable.payments_24px),
                                        tint = if(isCash) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isCash,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                                    selectedBorderColor = Color.Transparent
                                )
                            )
                            FilterChip(
                                selected = hasReceipt,
                                onClick = { viewModel.onReceiptToggle(!hasReceipt) },
                                label = { Text("Has Receipt") },
                                leadingIcon = {
                                    Icon(
                                        painterResource(id = R.drawable.receipt_24px),
                                        tint = if(hasReceipt) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = hasReceipt,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                                    selectedBorderColor = Color.Transparent
                                )
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Amount", 
                                fontSize = 15.sp, 
                                fontWeight = FontWeight.Medium, 
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = amount,
                                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) viewModel.onAmountChange(it) },
                                placeholder = { Text("₹ 0.00", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                                shape = RoundedCornerShape(16.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Budget Type", 
                                fontSize = 15.sp, 
                                fontWeight = FontWeight.Medium, 
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            SelectionBox(
                                text = selectedBudgetType?.name ?: "Select",
                                onClick = { showBudgetTypeDialog = true }
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Date", 
                                fontSize = 15.sp, 
                                fontWeight = FontWeight.Medium, 
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            val dateStr = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).format(displayDateFormatter)
                            SelectionBox(
                                text = dateStr,
                                icon = R.drawable.calendar_month_24px,
                                onClick = { showDatePicker = true }
                            )
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Select Budget", 
                                fontSize = 15.sp, 
                                fontWeight = FontWeight.Medium, 
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            val budgetName = availableBudgets.find { it.id == selectedBudgetId }?.title ?: "Select"
                            SelectionBox(
                                text = budgetName,
                                onClick = { if (selectedBudgetType != null) showBudgetDialog = true },
                                enabled = selectedBudgetType != null
                            )
                        }
                    }
                }

                if (selectedBudgetId != null) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Select Category", 
                                    fontSize = 15.sp, 
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = Outfit
                                )
                                val budget = availableBudgets.find { it.id == selectedBudgetId }
                                val categoryName = budget?.allocations?.mapNotNull { it.category }?.find { it.id == selectedParentCategoryId }?.title ?: "Select"
                                SelectionBox(
                                    text = categoryName,
                                    onClick = { showCategorySelectionDialog = true }
                                )
                            }

                            if (selectedParentCategoryId != null) {
                                val budget = availableBudgets.find { it.id == selectedBudgetId }
                                val subCategories = budget?.allocations?.mapNotNull { it.category }?.filter { it.parentCategoryId == selectedParentCategoryId } ?: emptyList()
                                
                                if (subCategories.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "Select Sub-category", 
                                            fontSize = 15.sp, 
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontFamily = Outfit
                                        )
                                        val subCategoryName = subCategories.find { it.id == selectedCategoryId }?.title ?: "Select"
                                        SelectionBox(
                                            text = subCategoryName,
                                            onClick = { showSubCategorySelectionDialog = true }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Add Tags", 
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = Outfit
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Card(
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                onClick = {
                                    activeSheet = "tag"
                                    tagsCreationViewModel.clearForm()
                                    scope.launch { scaffoldState.bottomSheetState.expand() }
                                }
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
                            VerticalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp).height(24.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            if (allTags.isNotEmpty()) {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    allTags.forEach { tag ->
                                        val isSelected = selectedTagIds.contains(tag.id)
                                        TagBadge(
                                            tag = tag,
                                            isSelected = isSelected,
                                            onClick = { viewModel.onTagToggle(tag.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }

            val imageLoader = ImageLoader.Builder(context).components { add(GifDecoder.Factory()) }.build()

            Card(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLoading) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                ),
                onClick = { if (!isLoading) viewModel.classifyTransaction() },
                shape = CircleShape,
                enabled = true,
                border = if (isLoading) BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline) else null
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (isLoading) {
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
                            text = "CONFIRM & SAVE",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = Outfit
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TagBadge(
    tag: Tag,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
    val badgeBackgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    val badgeTextColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "#${tag.name}",
            color = contentColor,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            fontFamily = Outfit,
            fontWeight = FontWeight.Medium
        )
        Card(
            modifier = Modifier.size(16.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = badgeBackgroundColor)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1",
                    color = badgeTextColor,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 10.sp
                )
            }
        }
    }
}

@Composable
fun SelectionBox(
    text: String,
    modifier: Modifier = Modifier,
    icon: Int = R.drawable.keyboard_arrow_down_24px,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        onClick = onClick,
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                color = if (text == "Select") MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                fontFamily = Outfit
            )
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

