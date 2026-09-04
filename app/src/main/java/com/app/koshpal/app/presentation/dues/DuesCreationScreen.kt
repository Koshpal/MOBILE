package com.app.koshpal.app.presentation.dues


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.util.Locale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.app.presentation.budget.component.dialog.MonthlyStartDatePickerDialog
import com.app.koshpal.app.presentation.dues.components.ReminderFrequencyDialog
import com.app.koshpal.app.presentation.dues.components.ReminderTimeDialog
import com.app.koshpal.app.presentation.dues.components.ReminderTypeSelectionDialog
import com.app.koshpal.app.viewmodels.duesviewmodel.DuesCreationViewModel
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.app.Events
import com.app.koshpal.core.presentation.util.ObserveAsEvents
import com.app.koshpal.app.domain.model.ReminderType

@Composable
fun DuesCreationScreen(
    viewModel: DuesCreationViewModel,
    onToPreviousScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                Toast.makeText(context, event.message ?: "Success", Toast.LENGTH_SHORT).show()
                if (event.message == "Due added") {
                    onToPreviousScreen()
                }
            }
            is Events.Error -> {
                Toast.makeText(context, event.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    val title by viewModel.reminderTitle.collectAsStateWithLifecycle()
    val titleSuggestions by viewModel.titleSuggestions.collectAsStateWithLifecycle()
    val amount by viewModel.reminderAmount.collectAsStateWithLifecycle()
    val date by viewModel.reminderDate.collectAsStateWithLifecycle()
    val frequency by viewModel.reminderFrequency.collectAsStateWithLifecycle()
    val customDays by viewModel.customFrequencyDays.collectAsStateWithLifecycle()
    val selectedHour by viewModel.reminderHour.collectAsStateWithLifecycle()
    val selectedMinute by viewModel.reminderMinute.collectAsStateWithLifecycle()
    val selectedReminderType by viewModel.selectedReminderType.collectAsStateWithLifecycle()
    val transactionType by viewModel.transactionType.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val reminderTypes by viewModel.reminderTypes.collectAsStateWithLifecycle()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showTypeDialog by remember { mutableStateOf(false) }
    var showFrequencyDialog by remember { mutableStateOf(false) }
    var isCreatingType by remember { mutableStateOf(false) }

    var newTypeTitle by remember { mutableStateOf("") }
    var newTypeColor by remember { mutableStateOf("0xFFC45100") }
    var newTypeIcon by remember { mutableStateOf("category") }

    if (showDatePicker) {
        Dialog(onDismissRequest = { showDatePicker = false }) {
            MonthlyStartDatePickerDialog(
                onDismiss = { showDatePicker = false },
                onDateSelected = {
                    viewModel.updateReminderDate(it)
                    showDatePicker = false
                }
            )
        }
    }

    if (showTypeDialog) {
        ReminderTypeSelectionDialog(
            onDismiss = {
                showTypeDialog = false
                isCreatingType = false
            },
            onTypeSelected = {
                viewModel.updateSelectedReminderType(it)
                showTypeDialog = false
            },
            onCreateNewTypeClick = { isCreatingType = true },
            types = reminderTypes,
            isCreating = isCreatingType,
            typeTitle = newTypeTitle,
            typeColor = newTypeColor,
            typeIcon = newTypeIcon,
            updateTypeTitle = { newTypeTitle = it },
            updateTypeColor = { newTypeColor = it },
            updateTypeIcon = { newTypeIcon = it },
            onCreateClick = {
                val newType = ReminderType(
                    name = newTypeTitle,
                    iconResId = newTypeIcon,
                    colorHex = newTypeColor
                )
                viewModel.insertReminderType(newType)
                viewModel.updateSelectedReminderType(newType)
                showTypeDialog = false
                isCreatingType = false
                newTypeTitle = ""
            }
        )
    }

    if (showFrequencyDialog) {
        ReminderFrequencyDialog(
            onDismiss = { showFrequencyDialog = false },
            onFrequencySelected = { freq, days -> 
                viewModel.updateReminderFrequency(freq)
                viewModel.updateCustomFrequencyDays(days)
            },
            selectedFrequency = frequency,
            customDays = customDays
        )
    }

    if (showTimePicker) {
        ReminderTimeDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = { h, m -> viewModel.updateReminderTime(h, m) },
            initialHour = selectedHour,
            initialMinute = selectedMinute
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
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
                        contentDescription = "Back"
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Add Reminder",
                fontSize = 24.sp,
                fontFamily = Outfit,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(6.dp)
                .selectableGroup()
        ) {
            listOf(TransactionType.EXPENSE, TransactionType.INCOME).forEach { option ->
                val isSelected = option == transactionType
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .selectable(
                            selected = isSelected,
                            onClick = { viewModel.updateTransactionType(option) }
                        )
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "For What?",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = title,
            onValueChange = { viewModel.updateReminderTitle(it) },
            placeholder = { Text("e.g. Electricity Bill, Rent", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
            ),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        if (title.isEmpty() && titleSuggestions.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(titleSuggestions) { suggestion ->
                    AssistChip(
                        onClick = { viewModel.updateReminderTitle(suggestion) },
                        label = {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Amount",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = amount,
                    onValueChange = { viewModel.updateReminderAmount(it) },
                    placeholder = { Text("₹ Select", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Reminder Type",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { showTypeDialog = true }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedReminderType != null) {
                        val baseColor = Color(selectedReminderType!!.colorHex.toColorLong())
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(baseColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val iconRes = selectedReminderType!!.iconResId!!.toDrawableResId() ?: R.drawable.notifications_24px
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                tint = baseColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedReminderType!!.name,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    } else {
                        Text(
                            text = "Select",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Add a target date",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
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
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = date,
                        fontSize = 14.sp,
                        color = if (date != "Select date") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Reminder Frequency",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { showFrequencyDialog = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.repeat_one_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = frequency,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Reminder Time",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { showTimePicker = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            val timeText = String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute)
            Text(
                text = timeText,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You can edit this anytime",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))

        val imageLoader = ImageLoader.Builder(context).components { add(GifDecoder.Factory()) }.build()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isLoading) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
            ),
            onClick = {
                if (!isLoading) viewModel.insertDue()
            },
            shape = CircleShape,
            border = if (isLoading) BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline) else null
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
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
                        text = "SAVE REMINDER",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
