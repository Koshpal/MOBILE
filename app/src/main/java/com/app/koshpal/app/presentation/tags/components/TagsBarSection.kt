package com.app.koshpal.app.presentation.tags.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.app.koshpal.app.viewmodels.tagsviewmodel.TagsViewModel
import com.app.koshpal.core.presentation.util.truncateTitle
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.LocalExtendedColors
import com.app.koshpal.ui.theme.Outfit
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TagsBarSection(
    modifier: Modifier = Modifier,
    tagSummaries: List<TagSummary>,
    isEditing: Boolean,
    selectedItem: List<String>,
    viewModel: TagsViewModel,
    onToDetailedTag: () -> Unit = {},
) {
    if (tagSummaries.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No tags found",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = Outfit,
                style = MaterialTheme.typography.titleMedium
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(tagSummaries, key = { it.tag.id }) { summary ->
                val dismissState = rememberSwipeToDismissBoxState(
                    initialValue = SwipeToDismissBoxValue.Settled,
                    confirmValueChange = {
                        if (it != SwipeToDismissBoxValue.Settled) {
                            viewModel.deleteTag(summary.tag.id)
                            true
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
                    TagBar(
                        summary = summary,
                        isEditing = isEditing,
                        isSelected = selectedItem.contains(summary.tag.id),
                        onClick = {
                            if (isEditing) {
                                if (selectedItem.contains(summary.tag.id)) {
                                    viewModel.removeSelectedItem(summary.tag.id)
                                } else {
                                    viewModel.addSelectedItem(summary.tag.id)
                                }
                            } else {
                                viewModel.updateClickedTagId(summary.tag.id)
                                onToDetailedTag()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TagBar(
    summary: TagSummary,
    isEditing: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val tag = summary.tag
    val baseColor = Color(tag.colorHex.toColorLong())
    val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            if (isEditing) {
                Column(
                    modifier = Modifier
                        .clickable { onClick() }
                        .fillMaxHeight()
                        .fillMaxWidth(0.12f)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = if (isSelected) R.drawable.check_circle_24px_2 else R.drawable.circle_24px_2),
                        contentDescription = "Select",
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        modifier = Modifier.size(26.dp)
                    )
                }
                VerticalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            Column(modifier = Modifier.weight(1f).clickable { onClick() }) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(0.5f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .size(44.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#",
                                    color = baseColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = tag.name.truncateTitle(20),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = Jakarta,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            val sourceText = when {
                                summary.associatedCategories.isEmpty() && summary.goalCount > 0 -> "Goals: ${summary.goalCount}"
                                summary.associatedCategories.isNotEmpty() && summary.goalCount > 0 -> "Multiple Sources"
                                else -> "Transactions: ${summary.transactionCount}"
                            }
                            Text(
                                text = sourceText,
                                color = MaterialTheme.colorScheme.outline,
                                fontFamily = Outfit,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.weight(0.5f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${formatter.format(summary.totalSpent.toInt())}",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = Jakarta
                            )
                            if (summary.transactionCount > 0) {
                                Spacer(Modifier.width(6.dp))
                                Card(
                                    modifier = Modifier.size(28.dp),
                                    shape = CircleShape,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.call_made_24px),
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                            if (summary.goalCount > 0 || summary.totalIncoming > 0) {
                                Spacer(Modifier.width(6.dp))
                                Card(
                                    modifier = Modifier.size(28.dp),
                                    shape = CircleShape,
                                    colors = CardDefaults.cardColors(containerColor = LocalExtendedColors.current.successContainer.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.call_received_24px),
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = LocalExtendedColors.current.success
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                if (summary.associatedCategories.isNotEmpty() || summary.associatedGoals.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (summary.associatedCategories.isNotEmpty()) "Categories:" else "Goals:",
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
                            if (summary.associatedCategories.isNotEmpty()) {
                                val visibleCategories = summary.associatedCategories.take(3)
                                val remainingCount = summary.associatedCategories.size - visibleCategories.size
                                visibleCategories.forEach { category ->
                                    val catColor = Color(category.colorHex.toColorLong())
                                    Card(
                                        modifier = Modifier.size(32.dp),
                                        colors = CardDefaults.cardColors(containerColor = catColor.copy(alpha = 0.2f)),
                                        shape = CircleShape,
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val iconRes = category.iconResId?.toDrawableResId()
                                            if(iconRes == null){
                                                val words = category.title.trim().split("\\s+".toRegex())
                                                val initials = if (words.size >= 2) "${words[0].first()}${words[1].first()}" else "${words[0].first()}"
                                                Text(
                                                    text = initials.uppercase(),
                                                    color = catColor,
                                                    fontFamily = Jakarta,
                                                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }else{
                                                Icon(
                                                    modifier = Modifier.size(20.dp),
                                                    painter = painterResource(id = iconRes),
                                                    contentDescription = null,
                                                    tint = catColor
                                                )
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
                            } else {
                                val visibleGoals = summary.associatedGoals.take(3)
                                val remainingCount = summary.associatedGoals.size - visibleGoals.size
                                visibleGoals.forEach { goal ->
                                    val goalColor = Color(goal.colorHex.toColorLong())
                                    Card(
                                        modifier = Modifier.size(32.dp),
                                        colors = CardDefaults.cardColors(containerColor = goalColor.copy(alpha = 0.2f)),
                                        shape = CircleShape,
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val iconRes = goal.iconResId.toDrawableResId()
                                            if(iconRes == null){
                                                val words = goal.title.trim().split("\\s+".toRegex())
                                                val initials = if (words.size >= 2) "${words[0].first()}${words[1].first()}" else "${words[0].first()}"
                                                Text(
                                                    text = initials.uppercase(),
                                                    color = goalColor,
                                                    fontFamily = Jakarta,
                                                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }else{
                                                Icon(
                                                    modifier = Modifier.size(20.dp),
                                                    painter = painterResource(id = iconRes),
                                                    contentDescription = null,
                                                    tint = goalColor
                                                )
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
                if (summary.insightText.isNotEmpty()) {
                    Text(
                        text = summary.insightText,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontFamily = Outfit
                    )
                }
            }
        }
    }
}
