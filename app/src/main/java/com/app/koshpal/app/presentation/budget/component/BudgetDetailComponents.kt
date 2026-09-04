package com.app.koshpal.app.presentation.budget.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.presentation.globalcomponents.RingChart
import com.app.koshpal.app.presentation.globalcomponents.SwipeOrHoldActions
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.LocalExtendedColors
import com.app.koshpal.ui.theme.Outfit
import kotlinx.coroutines.flow.Flow
import java.text.NumberFormat

@Composable
fun DetailedCategoryCard(
    category: Category,
    subCategories: List<Category>,
    allottedAmount: Double,
    usedAmount: Double,
    formatter: NumberFormat,
    isEditing: Boolean = false,
    isSelected: Boolean = false,
    hiddenCategoryIds: Set<String> = emptySet(),
    isIndividualEditing: String = "",
    updateIsIndividualEditing: (String) -> Unit = {},
    onDelete: () -> Unit = {},
    onToggleSelect: () -> Unit = {},
    onClick: (() -> Unit)? = null,
    getSubCategorySpent: (Category) -> Flow<Double>
) {
    var isExpanded by remember { mutableStateOf(false) }
    val amountLeft = (allottedAmount - usedAmount).coerceAtLeast(0.0)
    val progress = if (allottedAmount > 0) {
        (usedAmount / allottedAmount).toFloat().coerceIn(0f, 1f)
    } else 0f

    val baseColor = remember(category.colorHex) {
        try {
            if (category.colorHex.isNotEmpty()) Color(category.colorHex.toColorLong()) else Color(0xFF42A5F5)
        } catch (_: Exception) {
            Color(0xFF42A5F5)
        }
    }

    val isHidden = hiddenCategoryIds.contains(category.id)
    val alpha = if (isHidden) 0.4f else 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha)
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = alpha))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            if (isEditing && isIndividualEditing != category.id) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.12f).background(baseColor.copy(alpha = if (isHidden) 0.1f else 0.25f)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize()
                            .clickable{ onToggleSelect() },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(26.dp),
                            painter = painterResource(id = if (isSelected) R.drawable.check_circle_24px_2 else R.drawable.circle_24px_2),
                            contentDescription = "Select Category",
                            tint =  if (isSelected) baseColor.copy(alpha = alpha) else baseColor.copy(alpha = if (isHidden) 0.1f else 0.3f)
                        )
                    }
                }
                VerticalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = alpha * 0.5f))
            }
            AnimatedVisibility(
                visible = !isEditing && isIndividualEditing == category.id,
                enter = androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.12f)
                        .background(baseColor.copy(alpha = if (isHidden) 0.1f else 0.25f))
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    SwipeOrHoldActions(
                        icon = R.drawable.delete_24px,
                        onClick = { onDelete() }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onClick = {
                            if (isIndividualEditing == category.id) {
                                updateIsIndividualEditing("")
                            } else if (isEditing) {
                                onToggleSelect()
                            } else if (onClick != null) {
                                onClick()
                            }
                        },
                        onLongClick = {
                            if (!isEditing) updateIsIndividualEditing(category.id)
                        }
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val iconRes = category.iconResId?.toDrawableResId()
                                if (iconRes == null) {
                                    Text(
                                        text = category.title.getInitials(),
                                        color = baseColor,
                                        fontFamily = Jakarta,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = iconRes),
                                        contentDescription = null,
                                        tint = baseColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        Column {
                            Text(
                                text = category.title,
                                fontFamily = Jakarta,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "₹${formatter.format(allottedAmount)} Allotted",
                                fontFamily = Outfit,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.70f)
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${formatter.format(amountLeft)}",
                            fontFamily = Outfit,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = baseColor
                        )
                        Text(
                            text = "Amount left",
                            fontFamily = Outfit,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.70f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = if (progress > 0.8f) LocalExtendedColors.current.errorLight else baseColor,
                    trackColor = (if (progress > 0.8f) LocalExtendedColors.current.errorLight else baseColor).copy(alpha = 0.15f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontFamily = Outfit,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.70f)
                    )
                    Text(
                        text = "-₹${formatter.format(usedAmount)} ${if (usedAmount > 0) "used" else ""}",
                        fontFamily = Outfit,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (usedAmount > 0) LocalExtendedColors.current.errorDark else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.70f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sub-categories (${subCategories.size})",
                        fontFamily = Outfit,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Icon(
                        painter = painterResource(id = if (isExpanded) R.drawable.keyboard_arrow_up_24px else R.drawable.keyboard_arrow_down_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.70f)
                    )
                }
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (subCategories.isEmpty()) {
                            Text(
                                text = "No sub-categories assigned.",
                                fontSize = 12.sp,
                                fontFamily = Outfit,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.70f)
                            )
                        } else {
                            subCategories.forEach { sub ->
                                SubCategoryRow(
                                    sub = sub,
                                    formatter = formatter,
                                    baseColor = baseColor,
                                    getSubCategorySpent = getSubCategorySpent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubCategoryRow(
    sub: Category,
    formatter: NumberFormat,
    baseColor: Color,
    getSubCategorySpent: (Category) -> Flow<Double>
) {
    val subUsed by getSubCategorySpent(sub).collectAsStateWithLifecycle(initialValue = 0.0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconRes = sub.iconResId?.toDrawableResId()
            Card(
                modifier = Modifier.size(16.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (iconRes == null) {
                        Text(
                            text = sub.title.getInitials(),
                            color = baseColor,
                            fontFamily = Jakarta,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = baseColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Text(
                text = sub.title,
                fontSize = 13.sp,
                fontFamily = Outfit,
                color = Color.DarkGray
            )
        }
        Text(
            text = "₹${formatter.format(subUsed)}",
            fontSize = 13.sp,
            fontFamily = Outfit,
            fontWeight = FontWeight.SemiBold,
            color = if (subUsed > 0) baseColor else Color.Black
        )
    }
}

@Composable
fun BudgetOverviewCard(
    totalAmount: String,
    amountLeft: String,
    progressPercentage: Int,
    segments: List<RingChartSegment>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        RingChart(
            segments = segments,
            centerLabel = "Spent",
            centerValue = "$progressPercentage%",
            modifier = Modifier.size(110.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column {
                Text(
                    text = "TOTAL PLANNED EXPENSES",
                    fontSize = 10.sp,
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.70f)
                )
                Text(
                    text = "₹ $totalAmount",
                    fontSize = 22.sp,
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Column {
                Text(
                    text = "LEFT TO BUDGET",
                    fontSize = 10.sp,
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.70f)
                )
                Text(
                    text = "₹ $amountLeft",
                    fontSize = 20.sp,
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}
