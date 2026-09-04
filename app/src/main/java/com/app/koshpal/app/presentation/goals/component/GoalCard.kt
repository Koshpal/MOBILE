package com.app.koshpal.app.presentation.goals.component

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import coil3.compose.AsyncImage
import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.app.presentation.globalcomponents.SwipeOrHoldActions
import com.app.koshpal.core.presentation.util.truncateTitle
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.LocalExtendedColors
import com.app.koshpal.ui.theme.Outfit
import java.text.NumberFormat
import java.util.*

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun GoalCard(
    goal: Goal,
    isEditing: Boolean,
    isIndividualEditing: String,
    updateIsIndividualEditing: (String) -> Unit,
    isSelected: Boolean,
    addSelectedItem: (String) -> Unit,
    removeSelectedItem: (String) -> Unit,
    onAddRemoveFunds: () -> Unit = {},
    onDeleteGoal: (Goal) -> Unit,
    parent: String = "Goals",
    onClick: () -> Unit
) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")) }
    val baseColor = Color(goal.colorHex.toColorLong())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
        ) {
            if (isEditing && isIndividualEditing != goal.id) {
                Column(
                    modifier = Modifier.clickable {
                        if (isSelected) removeSelectedItem(goal.id)
                        else addSelectedItem(goal.id)
                    }
                    .fillMaxHeight()
                    .fillMaxWidth(0.10f)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(id = if (isSelected) R.drawable.check_circle_24px_2 else R.drawable.circle_24px_2),
                        contentDescription = "Selection Icon",
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                }
                VerticalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            AnimatedVisibility(
                visible = !isEditing && isIndividualEditing == goal.id,
                enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.12f)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    SwipeOrHoldActions(
                        icon = R.drawable.delete_24px,
                        onClick = { onDeleteGoal(goal) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onClick = {
                            if (isIndividualEditing != goal.id) {
                                if (isEditing) {
                                    if (isSelected) removeSelectedItem(goal.id) else addSelectedItem(goal.id)
                                } else {
                                    onClick()
                                }
                            } else {
                                updateIsIndividualEditing("")
                            }
                        },
                        onLongClick = {
                            updateIsIndividualEditing(goal.id)
                        }
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .size(48.dp),
                            colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.2f)),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (goal.imageUri != null) {
                                    AsyncImage(
                                        model = goal.imageUri,
                                        contentDescription = "Goal Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    val iconRes = goal.iconResId.toDrawableResId()
                                    if (iconRes != null) {
                                        Icon(
                                            painter = painterResource(id = iconRes),
                                            contentDescription = null,
                                            tint = baseColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        val words = goal.title.trim().split("\\s+".toRegex())
                                        val initials = if (words.size >= 2) "${words[0].first()}${words[1].first()}" else "${words[0].first()}"
                                        Text(
                                            text = initials.uppercase(),
                                            color = baseColor,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = Jakarta
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        Column {
                            Text(
                                text = goal.title.truncateTitle(20),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Jakarta
                            )
                            Text(
                                text = "${goal.durationMonths} Months",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                fontFamily = Outfit
                            )
                        }
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatter.format(goal.targetAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Jakarta
                        )
                        Text(
                            text = "Target Amount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontFamily = Outfit
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LinearProgressIndicator(
                        progress = { goal.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = if (goal.progress  > 0.8f) LocalExtendedColors.current.errorLight else baseColor,
                        trackColor = (if (goal.progress  > 0.8f) LocalExtendedColors.current.errorLight else baseColor).copy(alpha = 0.15f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "${goal.progressPercentage}% achieved",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontFamily = Outfit
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Monthly Savings",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        fontFamily = Outfit
                    )
                    Text(
                        text = formatter.format(goal.monthlySavings),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Jakarta
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Saved Amount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        fontFamily = Outfit
                    )
                    Text(
                        text = formatter.format(goal.savedAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = Jakarta
                    )
                }

                if (!isEditing && isIndividualEditing != goal.id && parent != "Tags") {
                    Card(
                        onClick = onAddRemoveFunds,
                        modifier = Modifier.fillMaxWidth(),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = baseColor.copy(0.2f),
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Add / Remove Funds",
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Jakarta,
                                color = baseColor
                            )
                        }
                    }
                }
            }
        }
    }
}
