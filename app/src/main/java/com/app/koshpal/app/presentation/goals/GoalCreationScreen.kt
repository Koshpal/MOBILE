package com.app.koshpal.app.presentation.goals


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import com.app.koshpal.R
import com.app.koshpal.app.Events
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.presentation.budget.component.dialog.MonthlyStartDatePickerDialog
import com.app.koshpal.app.presentation.globalcomponents.FilterToggleCard
import com.app.koshpal.app.presentation.tags.TagsCreationScreen
import androidx.compose.foundation.layout.FlowRow
import com.app.koshpal.app.viewmodels.goalsviewmodel.GoalCreationViewModel
import com.app.koshpal.app.viewmodels.tagsviewmodel.TagsCreationViewModel
import com.app.koshpal.core.presentation.util.ObserveAsEvents
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.SetStatusBarAppearance
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalCreationScreen(
    viewModel: GoalCreationViewModel,
    onToPreviousScreen: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val tagsCreationViewModel: TagsCreationViewModel = koinViewModel()
    
    val title by viewModel.title.collectAsStateWithLifecycle()
    val targetAmount by viewModel.targetAmount.collectAsStateWithLifecycle()
    val selectedTagId by viewModel.selectedTagId.collectAsStateWithLifecycle()
    val targetDate by viewModel.targetDate.collectAsStateWithLifecycle()
    val isDateEnabled by viewModel.isDateEnabled.collectAsStateWithLifecycle()
    val goalIcon by viewModel.goalIcon.collectAsStateWithLifecycle()
    val goalColor by viewModel.goalColor.collectAsStateWithLifecycle()
    val imageUri by viewModel.imageUri.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val isFormValid by viewModel.isFormValid.collectAsStateWithLifecycle()
    
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    
    val tagColor by tagsCreationViewModel.tagColor.collectAsStateWithLifecycle()
    val tagName by tagsCreationViewModel.tagName.collectAsStateWithLifecycle()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Hidden, skipHiddenState = false)
    )

    SetStatusBarAppearance(isDarkIcons = true)

    val imageLoader = ImageLoader.Builder(context).components { add(GifDecoder.Factory()) }.build()

    var showDatePicker by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateImageUri(uri?.toString())
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                Toast.makeText(context, event.message ?: "Success", Toast.LENGTH_SHORT).show()
                onToPreviousScreen()
            }
            is Events.Error -> {
                Toast.makeText(context, event.message ?: "Error", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    if (showDatePicker) {
        Dialog(onDismissRequest = { showDatePicker = false }) {
            MonthlyStartDatePickerDialog(
                onDismiss = { showDatePicker = false },
                onDateSelected = { dateStr ->
                    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
                    val parsedDate = try {
                        LocalDate.parse(dateStr, formatter)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    } catch (_: Exception) { System.currentTimeMillis() }
                    viewModel.updateDate(parsedDate)
                    showDatePicker = false
                }
            )
        }
    }

    ObserveAsEvents(tagsCreationViewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                if (event.message == "Tag created") {
                    tagsCreationViewModel.lastCreatedTagId.value?.let { id ->
                        viewModel.onTagSelect(id)
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(padding)
                .padding(horizontal = 16.dp)
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
                    onClick = {
                        viewModel.clearDraft()
                        onToPreviousScreen()
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Update Your Goal" else "Create new goal",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Outfit
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    val activeColor = try { Color(goalColor.toColorLong()) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(activeColor.copy(alpha = 0.1f))
                            .border(1.dp, activeColor.copy(alpha = 0.2f), CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Goal Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(id = goalIcon.toDrawableResId() ?: R.drawable.category_24px),
                                    contentDescription = null,
                                    tint = activeColor,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Upload\nimage",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "What are you saving for ?",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = title,
                                onValueChange = viewModel::updateTitle,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("e.g. Dream House, Car", color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)) },
                                shape = RoundedCornerShape(16.dp),
                                colors = goalTextFieldColors(),
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontFamily = Outfit
                                ),
                                singleLine = true
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Set your target",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = targetAmount,
                                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) viewModel.updateTargetAmount(it) },
                                modifier = Modifier.fillMaxWidth(),
                                prefix = { Text("₹ ", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                                placeholder = { Text("0", color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)) },
                                shape = RoundedCornerShape(16.dp),
                                colors = goalTextFieldColors(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontFamily = Outfit
                                ),
                                singleLine = true
                            )
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            FilterToggleCard(
                                label = "Add a target date",
                                icon = R.drawable.calendar_month_24px,
                                checked = isDateEnabled,
                                onCheckedChange = { viewModel.toggleDateEnabled(it) }
                            )

                            if (isDateEnabled) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .clickable { showDatePicker = true }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.calendar_month_24px),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(24.dp).padding(end = 8.dp)
                                    )
                                    val dateStr = Instant.ofEpochMilli(targetDate).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontFamily = Outfit
                                    )
                                }
                            }
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Goal Color",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                availableCategoryColors.forEach { colorStr ->
                                    val color = Color(colorStr.toColorLong())
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(color.copy(alpha = 0.2f))
                                            .clickable { viewModel.updateColor(colorStr) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (goalColor == colorStr) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.check_24px),
                                                contentDescription = null,
                                                tint = color,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Goal Icon",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Outfit
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            val activeColorValue = try { Color(goalColor.toColorLong()) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                availableCategoryIcons.chunked(5).forEach { rowIcons ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        rowIcons.forEach { iconKey ->
                                            val isSelected = iconKey == goalIcon
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSelected) activeColorValue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                    .clickable { viewModel.updateIcon(iconKey) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val iconRes = iconKey.toDrawableResId()
                                                if (iconRes != null) {
                                                    Icon(
                                                        painter = painterResource(id = iconRes),
                                                        contentDescription = null,
                                                        tint = if (isSelected) activeColorValue else MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                } else {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.block_24px),
                                                        contentDescription = "No Icon",
                                                        tint = if (isSelected) activeColorValue else MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                        }
                                        repeat(5 - rowIcons.size) { Spacer(modifier = Modifier.weight(1f)) }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Select tag for your goal",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = Outfit
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                onClick = {
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
                                        contentDescription = "Add Tag",
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
                                        val isSelected = selectedTagId == tag.id
                                        GoalTagBadge(
                                            tag = tag,
                                            isSelected = isSelected,
                                            onClick = { viewModel.onTagSelect(tag.id) }
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "No tags available",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                item {
                    Text(
                        text = "You can edit this anytime",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLoading) MaterialTheme.colorScheme.onPrimary else if (isFormValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                ),
                onClick = { if (isFormValid && !isLoading) viewModel.saveGoal() },
                shape = CircleShape,
                enabled = isFormValid,
                border = if (isLoading) BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline) else null
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            text = if (isEditing) "UPDATE" else "CREATE GOAL",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isFormValid) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun goalTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
)

@Composable
fun GoalTagBadge(
    tag: Tag,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val baseColor = try { Color(tag.colorHex.toColorLong()) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) baseColor else baseColor.copy(alpha = 0.1f))
            .border(0.5.dp, if (isSelected) Color.Transparent else baseColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "#${tag.name}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) Color.White else baseColor
        )
    }
}
