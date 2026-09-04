package com.app.koshpal.app.presentation.budget.component


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.ui.theme.LocalExtendedColors
import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.core.presentation.util.truncateTitle
import com.app.koshpal.ui.theme.Outfit

@Composable
fun FlaggedBudgetRow(
    flaggedBudgets: List<Budget>,
    onBudgetClick: (Budget) -> Unit,
    onRemoveClick: (String) -> Unit
) {
    if (flaggedBudgets.isEmpty()) return

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(flaggedBudgets, key = { it.id }) { budget ->
            FlaggedBudgetCard(
                budget = budget,
                onClick = { onBudgetClick(budget) },
                onRemoveClick = { onRemoveClick(budget.id) }
            )
        }
    }
}

@Composable
fun FlaggedBudgetCard(
    modifier: Modifier = Modifier,
    budget: Budget,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val parentCategories = budget.categories.filter { it.parentCategoryId == null }
    val visibleCategories = parentCategories.take(2)
    val remainingCount = parentCategories.size - visibleCategories.size

    Card(
        modifier = modifier
            .width(180.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = budget.title.truncateTitle(20),
                        fontSize = 14.sp,
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = budget.startDate,
                        fontSize = 11.sp,
                        fontFamily = Outfit,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                Card(
                    modifier = Modifier
                        .size(24.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = LocalExtendedColors.current.deleteOuter),
                    onClick = { onRemoveClick() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.flag_24px_2),
                            modifier = Modifier.size(14.dp),
                            contentDescription = "Remove",
                            tint = LocalExtendedColors.current.deleteInner,
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                visibleCategories.forEach { category ->
                    val baseColor = Color(category.colorHex.toColorLong())
                    Card(
                        modifier = Modifier
                            .size(26.dp),
                        colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.15f)),
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(14.dp),
                                painter = painterResource(id = category.iconResId!!.toDrawableResId() ?: R.drawable.category_24px),
                                contentDescription = null,
                                tint = baseColor
                            )
                        }
                    }
                }
                if (remainingCount > 0) {
                    Card(
                        modifier = Modifier
                            .size(26.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.3f)),
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "+$remainingCount",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Outfit
                            )
                        }
                    }
                }
            }
        }
    }
}
