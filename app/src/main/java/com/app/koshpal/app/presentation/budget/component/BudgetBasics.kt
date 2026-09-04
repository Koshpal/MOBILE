package com.app.koshpal.app.presentation.budget.component


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.presentation.budget.component.dialog.MonthlyStartDatePickerDialog
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.presentation.util.toDisplayDate
import java.time.Instant
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetBasics(
    title: String,
    period: BudgetPeriod,
    startDate: String,
    endDate: String? = null,
    budgetType: BudgetType,
    updateTitle: (String) -> Unit,
    updatePeriod: (BudgetPeriod) -> Unit,
    updateStartDate: (String) -> Unit,
    updateEndDate: (String) -> Unit = {},
    titleSuggestions: List<String> = emptyList()
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val todayMillis = remember {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.timeInMillis
    }

    val startSelectableDates = remember(budgetType) {
        if (budgetType == BudgetType.ONE_TIME) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= todayMillis
                }
            }
        } else {
            DatePickerDefaults.AllDates
        }
    }

    val endSelectableDates = remember(budgetType, startDate) {
        if (budgetType == BudgetType.ONE_TIME) {
            val startMillis = try {
                if (startDate != "Select a date" && startDate.isNotBlank()) {
                    Instant.parse(startDate).toEpochMilli()
                } else todayMillis
            } catch (_: Exception) {
                todayMillis
            }
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= startMillis
                }
            }
        } else {
            DatePickerDefaults.AllDates
        }
    }

    if (showStartDatePicker){
        Dialog(onDismissRequest = { showStartDatePicker = false }) {
            MonthlyStartDatePickerDialog(
                onDismiss = { showStartDatePicker = false },
                onDateSelected = {
                    updateStartDate(it)
                    showStartDatePicker = false
                },
                selectableDates = startSelectableDates
            )
        }
    }

    if (showEndDatePicker){
        Dialog(onDismissRequest = { showEndDatePicker = false }) {
            MonthlyStartDatePickerDialog(
                onDismiss = { showEndDatePicker = false },
                onDateSelected = {
                    updateEndDate(it)
                    showEndDatePicker = false
                },
                selectableDates = endSelectableDates
            )
        }
    }

    Column {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            SectionHeader(
                title = "Start with the basics",
                subtitle =  "We recommend using the same budget period as your regular income."
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Column(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(0.5.dp, MaterialTheme.colorScheme.outline,RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
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
                    onValueChange = { updateTitle(it) },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {}
                    ),
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

                if (title.isEmpty() && titleSuggestions.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(titleSuggestions) { suggestion ->
                            AssistChip(
                                onClick = { updateTitle(suggestion) },
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
            }

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
                                    .selectable(selected = isSelected, onClick = { updatePeriod(option) })
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
                
                Column(modifier = Modifier.padding(top = 10.dp)){
                    Text(
                        text = "${period.name.lowercase().replaceFirstChar { it.uppercase() }} start date",
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
                            .clickable {
                                showStartDatePicker = true
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar_month_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 10.dp)
                        )
                        Text(
                            text = startDate.toDisplayDate(),
                            fontSize = 14.sp,
                            color = if (startDate != "Select a date") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

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
                                .clickable { showStartDatePicker = true }
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
                                .clickable { showEndDatePicker = true }
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
                            text = if (endDate != "Select a date" && endDate != null) endDate.toDisplayDate() else "Select a date",
                            fontSize = 12.sp,
                            color = if (endDate != "Select a date" && endDate != null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        }
                    }
                }
            }
        }
    }
}
