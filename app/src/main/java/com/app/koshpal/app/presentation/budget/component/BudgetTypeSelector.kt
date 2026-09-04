package com.app.koshpal.app.presentation.budget.component


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.core.data.entities.enums.BudgetType


@Composable
fun BudgetTypeSelector(
    selectedType: BudgetType,
    updateSelectedType: (BudgetType) -> Unit
){

    Column{
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            SectionHeader(
                title = "Set your budget type",
                subtitle = "Choose how you want to track this budget. Something you repeat " +
                        "regularly or a one time plan like a trip or event.",
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            BudgetTypeOption(
                title = "Recurring budgets",
                description = "For everyday spending like food, rent, or bills. " +
                        "This resets automatically in a particular period.",
                selected = selectedType == BudgetType.RECURRING,
                onClick = { updateSelectedType(BudgetType.RECURRING) }
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 16.dp))
            BudgetTypeOption(
                title = "One time budget",
                description = "For specific plans like trips, shopping, or events. " +
                        "Tracks spending within a selected date range.",
                selected = selectedType == BudgetType.ONE_TIME,
                onClick = { updateSelectedType(BudgetType.ONE_TIME) }
            )
        }
    }
}

@Composable
private fun BudgetTypeOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 6.dp, end = 12.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
    }
}