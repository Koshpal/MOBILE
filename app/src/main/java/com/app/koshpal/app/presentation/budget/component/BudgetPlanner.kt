package com.app.koshpal.app.presentation.budget.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.app.domain.model.*
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetCreationViewModel
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit

@Composable
fun BudgetPlanner(
    overallAmount: String,
    onOverallAmountChange: (String) -> Unit,
    allocations: List<CategoryAllocationUiState>,
    onCategoryAmountChange: (String, String) -> Unit,
    onRemoveCategory: (Category) -> Unit = {},
    onCategoryClick: (CategoryAllocationUiState) -> Unit = {},
    showCategoryDialog: MutableState<Boolean>,
    overAllocatedAmount: Double,
    viewModel: BudgetCreationViewModel,
    listState: LazyListState
) {
    val isError = overAllocatedAmount > 0.0
    val showZeroAmountAlert by viewModel.showZeroAmountAlert.collectAsStateWithLifecycle()
    val parentAllocations = remember(allocations) {
        allocations.filter { it.category.parentCategoryId == null }
    }

    Column {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionHeader(
                title = "Budget Planner",
                subtitle = "Plan how you want to distribute your money for this period."
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Column(
            modifier = Modifier
                .heightIn(max = 460.dp)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            SectionHeader(
                title = "Overall budget",
                subtitle = "Set total budget without categorization",
                titleFontSize = MaterialTheme.typography.bodyMedium.fontSize,
                subtitleFontSize = MaterialTheme.typography.bodySmall.fontSize
            )
            BudgetRow(
                modifier = Modifier.padding(top = 10.dp, bottom = 14.dp),
                icon = R.drawable.grid_view_24px,
                iconBackground = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                label = "Overall budget",
                amount = overallAmount,
                onAmountChange = { onOverallAmountChange(it) },
            )
            SectionHeader(
                title = "Category wise budget",
                subtitle = "Set budget by categories and sub-categories",
                titleFontSize = MaterialTheme.typography.bodyMedium.fontSize,
                subtitleFontSize = MaterialTheme.typography.bodySmall.fontSize
            )
            Spacer(modifier = Modifier.height(10.dp))
            val totalAllotted by viewModel.totalCategorySum.collectAsStateWithLifecycle()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Total Allocated: \u20B9${totalAllotted.toLong()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Medium
                )
            }
            if (isError) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️ Exceeds overall budget by \u20B9${overAllocatedAmount.toLong()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            if (showZeroAmountAlert) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️ Please enter an overall budget or category amounts before creating.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = listState
            ) {
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
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .clickable { showCategoryDialog.value = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Card(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                onClick = { showCategoryDialog.value = true }
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
                fontFamily = Outfit,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
fun BudgetRow(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    iconBackground: Color,
    iconTint: Color,
    label: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    isError: Boolean = false,
) {
    val containerColor = if (isError)
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
    else
        MaterialTheme.colorScheme.primaryContainer

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor, RoundedCornerShape(14.dp))
            .border(
                width = if (isError) 0.5.dp else 0.dp,
                color = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = iconBackground)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        val words = label.trim().split("\\s+".toRegex())
                        val initials = if (words.size >= 2) "${words[0].first()}${words[1].first()}" else "${words[0].first()}"
                        Text(
                            text = initials.uppercase(),
                            color = iconTint,
                            fontFamily = Jakarta,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Text(
                text = label,
                fontFamily = Outfit,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.weight(1f))
            TextField(
                value = amount,
                onValueChange = onAmountChange,
                modifier = Modifier.width(120.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = Outfit,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                ),
                prefix = { Text("\u20B9", fontFamily = Outfit) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    errorCursorColor = MaterialTheme.colorScheme.error,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )
        }
    }
}