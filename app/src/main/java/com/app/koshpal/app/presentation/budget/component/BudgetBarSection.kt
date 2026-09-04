package com.app.koshpal.app.presentation.budget.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.model.getInitials
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.app.presentation.globalcomponents.SwipeOrHoldActions
import com.app.koshpal.app.viewmodels.budgetviewmodel.BudgetViewModel
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.presentation.util.toDisplayDate
import com.app.koshpal.core.presentation.util.truncateTitle
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.LocalExtendedColors
import com.app.koshpal.ui.theme.Outfit
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetBarSection(
    budgets: List<Budget>,
    budgetType: String,
    isEditing: Boolean,
    updateIsItemClicked: (Boolean) -> Unit,
    updateClickedBudgetId: (String) -> Unit,
    addSelectedItem: (String) -> Unit,
    removeSelectedItem: (String) -> Unit,
    selectedItem: Set<String>,
    viewModel: BudgetViewModel
){
    val hiddenBudgetIds by viewModel.hiddenBudgetIds.collectAsStateWithLifecycle()
    val flaggedBudgetIds by viewModel.flaggedBudgetIds.collectAsStateWithLifecycle()
    val isIndividualEditing by viewModel.isIndividualEditing.collectAsStateWithLifecycle()
    val updateIsIndividualEditing = viewModel::updateIsIndividualEditing
    val toggleIndividualFlaggedState = viewModel::toggleIndividualFlaggedState
    val toggleIndividualHiddenState = viewModel::toggleIndividualHiddenState
    val deleteBudget = viewModel::deleteBudget
    
    val filteredBudgets = if (budgetType == "all") budgets else budgets.filter { it.budgetType.name.lowercase() == budgetType }

    if (filteredBudgets.isEmpty()) {
        val emptyMessage = when (budgetType) {
            "recurring" -> "No Recurring budgets found"
            "one_time" -> "No One Time budgets found"
            else -> "No budgets found"
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyMessage,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = Outfit,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(filteredBudgets, key = { it.id }) {
                BudgetBar(
                    budget = it,
                    isEditing = isEditing,
                    isIndividualEditing = isIndividualEditing,
                    updateIsIndividualEditing = updateIsIndividualEditing,
                    toggleIndividualFlaggedState = toggleIndividualFlaggedState,
                    toggleIndividualHiddenState = toggleIndividualHiddenState,
                    addSelectedItem = addSelectedItem,
                    removeSelectedItem = removeSelectedItem,
                    isSelected = selectedItem.contains(it.id),
                    isHidden = hiddenBudgetIds.contains(it.id),
                    isFlagged = flaggedBudgetIds.contains(it.id),
                    onUnflag = { viewModel.removeFlaggedBudget(it.id) },
                    updateIsItemClicked = updateIsItemClicked,
                    updateClickedBudgetId = updateClickedBudgetId,
                    deleteBudget = deleteBudget
                )
            }
        }
    }
}


@Composable
fun BudgetBar(
    budget: Budget,
    isEditing: Boolean,
    isIndividualEditing: String,
    updateIsIndividualEditing: (String) -> Unit,
    toggleIndividualFlaggedState: (String) -> Unit,
    toggleIndividualHiddenState: (String) -> Unit,
    addSelectedItem: (String) -> Unit,
    removeSelectedItem: (String) -> Unit,
    isSelected: Boolean,
    isHidden: Boolean,
    isFlagged: Boolean,
    onUnflag: () -> Unit,
    updateIsItemClicked: (Boolean) -> Unit,
    updateClickedBudgetId: (String) -> Unit,
    deleteBudget: (Budget) -> Unit
){
    val parentCategories = budget.categories.filter { it.parentCategoryId == null }
    val visibleCategories = parentCategories.take(3)
    val remainingCount = parentCategories.size - visibleCategories.size
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"))
    val formatted = formatter.format(budget.amount)
    val alpha = if (isHidden) 0.4f else 1f

    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
        ) {
            if (isEditing && isIndividualEditing != budget.id) {
                Column(
                    modifier = Modifier.clickable {
                        if (isSelected) removeSelectedItem(budget.id)
                        else addSelectedItem(budget.id)
                    }
                    .fillMaxHeight()
                    .fillMaxWidth(0.10f)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = alpha)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(id = if (isSelected) R.drawable.check_circle_24px_2 else R.drawable.circle_24px_2),
                        contentDescription = "Selection Icon",
                        tint = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = alpha) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f * alpha)
                    )
                }
                VerticalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f * alpha))
            }
            AnimatedVisibility(
                visible = !isEditing && isIndividualEditing == budget.id,
                enter = slideInHorizontally(
                    initialOffsetX = { -it }
                ) + fadeIn(),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it }
                ) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.12f)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = alpha))
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    SwipeOrHoldActions(
                        icon = R.drawable.flag_24px,
                        onClick = { toggleIndividualFlaggedState(budget.id) }
                    )
                    Spacer(Modifier.height(8.dp))
                    SwipeOrHoldActions(
                        icon = if (isHidden)  R.drawable.visibility_off_24px_2 else R.drawable.visibility_24px_2,
                        onClick = { toggleIndividualHiddenState(budget.id) }
                    )
                    Spacer(Modifier.height(8.dp))
                    SwipeOrHoldActions(
                        icon = R.drawable.delete_24px,
                        onClick = { deleteBudget(budget) }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f).combinedClickable(
                        onClick = {
                            if(isIndividualEditing != budget.id){
                                if (isEditing) {
                                    if (isSelected) removeSelectedItem(budget.id) else addSelectedItem(budget.id)
                                } else {
                                    updateClickedBudgetId(budget.id)
                                    updateIsItemClicked(true)
                                }
                            }else{
                                updateIsIndividualEditing("")
                            }
                        },
                        onLongClick = {
                            updateIsIndividualEditing(budget.id)
                        }
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Card(
                            modifier = Modifier.size(36.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            shape = CircleShape,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = budget.title.getInitials(),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                                    fontFamily = Jakarta,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = budget.title.truncateTitle(20),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                                fontFamily = Jakarta,
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = budget.startDate.toDisplayDate(),
                                color = MaterialTheme.colorScheme.outline,
                                fontFamily = Outfit,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text ="₹$formatted",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                                fontFamily = Jakarta,
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (budget.budgetType == BudgetType.ONE_TIME) "One Time" else "${budget.period}",
                                color = MaterialTheme.colorScheme.outline,
                                fontFamily = Outfit,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (isFlagged) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Card(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = LocalExtendedColors.current.deleteOuter),
                                onClick = { onUnflag() }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.flag_24px_2),
                                        contentDescription = "Unflag",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categories:",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = Jakarta,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        visibleCategories.forEach { category ->
                            val baseColor = Color(category.colorHex.toColorLong())
                            Card(
                                modifier = Modifier.size(32.dp),
                                colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.2f)),
                                shape = CircleShape,
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val iconRes = category.iconResId?.toDrawableResId()
                                    if(iconRes == null){
                                        Text(
                                            text = category.title.getInitials(),
                                            color = baseColor,
                                            fontFamily = Jakarta,
                                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }else{
                                        Icon(
                                            modifier = Modifier.size(20.dp),
                                            painter = painterResource(id = iconRes),
                                            contentDescription = null,
                                            tint = baseColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (remainingCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Card(
                            modifier = Modifier.size(32.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)),
                            shape = CircleShape,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "+$remainingCount",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontFamily = Outfit,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
