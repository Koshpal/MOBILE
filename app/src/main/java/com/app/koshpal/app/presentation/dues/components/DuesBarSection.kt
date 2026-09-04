package com.app.koshpal.app.presentation.dues.components

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
import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.core.presentation.util.toDisplayDate
import com.app.koshpal.core.presentation.util.truncateTitle
import com.app.koshpal.app.presentation.globalcomponents.SwipeOrHoldActions
import com.app.koshpal.app.viewmodels.duesviewmodel.DuesViewModel
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import java.text.NumberFormat
import java.util.*

@Composable
fun DuesBarSection(
    dues: List<Due>,
    viewModel: DuesViewModel,
    onToDetailedDue: () -> Unit = {}
) {
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val selectedItems by viewModel.selectedItem.collectAsStateWithLifecycle()
    val isIndividualEditing by viewModel.isIndividualEditing.collectAsStateWithLifecycle()
    
    val onAddSelectedItem = viewModel::addSelectedItem
    val onRemoveSelectedItem = viewModel::removeSelectedItem
    val onToggleCompletion = viewModel::toggleDueCompletion
    val updateIsIndividualEditing = viewModel::updateIsIndividualEditing
    val deleteDue: (String) -> Unit = { id -> viewModel.deleteDue(id) }

    if (dues.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No reminders found",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = Outfit,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(dues, key = { it.id }) { due ->
                DuesBar(
                    due = due,
                    isEditing = isEditing,
                    isSelected = selectedItems.contains(due.id),
                    isIndividualEditing = isIndividualEditing,
                    onAddSelectedItem = onAddSelectedItem,
                    onRemoveSelectedItem = onRemoveSelectedItem,
                    onToggleCompletion = onToggleCompletion,
                    updateIsIndividualEditing = updateIsIndividualEditing,
                    deleteDue = deleteDue,
                    onToDetailedDue = onToDetailedDue,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun DuesBar(
    due: Due,
    isEditing: Boolean,
    isSelected: Boolean,
    isIndividualEditing: String,
    onAddSelectedItem: (String) -> Unit,
    onRemoveSelectedItem: (String) -> Unit,
    onToggleCompletion: (String) -> Unit,
    updateIsIndividualEditing: (String) -> Unit,
    deleteDue: (String) -> Unit,
    onToDetailedDue: () -> Unit = {},
    viewModel: DuesViewModel
) {
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"))
    val formattedAmount = formatter.format(due.amount)
    val baseColor = due.colorHex?.let { Color(it.toColorLong()) } ?: MaterialTheme.colorScheme.primary
    val selectionIcon = if (isSelected) R.drawable.check_circle_24px_2 else R.drawable.circle_24px_2

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
        ) {
            if (isEditing && isIndividualEditing != due.id) {
                Column(
                    modifier = Modifier
                        .clickable { if (isSelected) onRemoveSelectedItem(due.id) else onAddSelectedItem(due.id) }
                        .fillMaxHeight()
                        .fillMaxWidth(0.10f)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = selectionIcon),
                        contentDescription = "Select",
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
                VerticalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
            AnimatedVisibility(
                visible = !isEditing && isIndividualEditing == due.id,
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
                        onClick = {
                            deleteDue(due.id)
                            updateIsIndividualEditing("")
                        }
                    )
                }
            }
            Column(modifier = Modifier.weight(1f).combinedClickable(
                onClick = {
                    if (isIndividualEditing == due.id) {
                        updateIsIndividualEditing("")
                    } else if (isEditing) {
                        if (isSelected) onRemoveSelectedItem(due.id) else onAddSelectedItem(due.id)
                    } else {
                        viewModel.updateClickedDueId(due.id)
                        onToDetailedDue()
                    }
                },
                onLongClick = {
                    if (!isEditing) updateIsIndividualEditing(due.id)
                }
            )) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .size(44.dp),
                        colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.15f)),
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val iconRes = (due.iconResId ?: due.reminderType)?.toDrawableResId() ?: R.drawable.notifications_24px
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                tint = baseColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = due.title.truncateTitle(20),
                            fontFamily = Jakarta,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = due.date.toDisplayDate(),
                            fontFamily = Jakarta,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹$formattedAmount",
                            fontFamily = Jakarta,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = due.status,
                            fontFamily = Outfit,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Repeats ${due.frequency}",
                            fontFamily = Outfit,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (due.overdueInfo != null) {
                            Text(
                                text = "${due.overdueInfo} >",
                                fontFamily = Outfit,
                                fontSize = 13.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .size(32.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (due.isCompleted) MaterialTheme.colorScheme.primary else Color.Transparent
                        ),
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = if (due.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        onClick = {
                            onToggleCompletion(due.id)
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (due.isCompleted) {
                                Icon(
                                    painter = painterResource(id = R.drawable.check_24px),
                                    contentDescription = "Completed",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
