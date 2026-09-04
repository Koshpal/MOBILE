package com.app.koshpal.app.presentation.budget.component

import java.util.Locale
import com.app.koshpal.app.domain.model.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.presentation.globalcomponents.FilterToggleCard
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.ui.theme.Outfit
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetFilterSection(
    modifier: Modifier = Modifier,
    selectedPeriod: BudgetPeriod?,
    onPeriodSelected: (BudgetPeriod?) -> Unit,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    showHidden: Boolean,
    onToggleHidden: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth().fillMaxHeight(0.5f)
            .padding(16.dp)
    ) {
        FilterToggleCard(
            label = "Show hidden items",
            icon = R.drawable.visibility_off_24px,
            checked = showHidden,
            onCheckedChange = { onToggleHidden() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Budget period",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = Outfit,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BudgetPeriod.entries.forEach { period ->
                    val isSelected = selectedPeriod == period
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color.White else Color.Transparent
                        ),
                        onClick = { onPeriodSelected(if (isSelected) null else period) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = period.name.uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Monthly start date",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = Outfit,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            onClick = { showDatePicker = true },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.calendar_month_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selectedDate?.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)) ?: "Select a date",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedDate != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000)))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
