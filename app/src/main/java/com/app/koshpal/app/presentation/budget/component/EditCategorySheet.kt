package com.app.koshpal.app.presentation.budget.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit


@Composable
fun EditCategorySheet(
    modifier: Modifier = Modifier,
    parentAllocation: CategoryAllocationUiState,
    subAllocations: List<CategoryAllocationUiState>,
    onCategoryAmountChange: (String, String) -> Unit,
    onRemoveSubCategory: (String) -> Unit,
    onAddSubCategoryClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    val baseColor = try {
        Color(parentAllocation.category.colorHex.toColorLong())
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight(0.8f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(baseColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    val iconRes = parentAllocation.category.iconResId?.toDrawableResId()
                    if (iconRes != null) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = baseColor,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        val words = parentAllocation.category.title.trim().split("\\s+".toRegex())
                        val initials = if (words.size >= 2) "${words[0].first()}${words[1].first()}" else "${words[0].first()}"
                        Text(
                            text = initials.uppercase(),
                            color = baseColor,
                            fontFamily = Jakarta,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = parentAllocation.category.title,
                    fontFamily = Outfit,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    text = "₹${parentAllocation.amountString.ifEmpty { "0" }}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontFamily = Outfit,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = "These expenses should be fairly regular from period to period",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(subAllocations, key = { it.category.id }) { item ->
                val subColor = try {
                    Color(item.category.colorHex.toColorLong())
                } catch (_: Exception) {
                    baseColor
                }
                val dismissState = rememberSwipeToDismissBoxState(
                    initialValue = SwipeToDismissBoxValue.Settled,
                    confirmValueChange = {
                        if (it != SwipeToDismissBoxValue.Settled) {
                            onRemoveSubCategory(item.category.id)
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
                        icon = iconRes,
                        iconBackground = subColor.copy(alpha = 0.2f),
                        iconTint = subColor,
                        label = item.category.title,
                        amount = item.amountString,
                        onAmountChange = { onCategoryAmountChange(item.category.id, it) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .clickable { onAddSubCategoryClick() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Card(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        onClick = { onAddSubCategoryClick() }
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
                        text = "Add Sub-category",
                        fontFamily = Outfit,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            onClick = onDoneClick,
            shape = CircleShape
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "DONE",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}
