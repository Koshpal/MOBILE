package com.app.koshpal.app.presentation.goals.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.ui.theme.Outfit

@Composable
fun GoalsBarSection(
    modifier: Modifier = Modifier,
    goals: List<Goal>,
    isEditing: Boolean,
    isIndividualEditing: String = "",
    updateIsIndividualEditing: (String) -> Unit = {},
    showHistory: Boolean,
    selectedItem: Set<String>,
    addSelectedItem: (String) -> Unit,
    removeSelectedItem: (String) -> Unit,
    onAddRemoveFunds: (Goal) -> Unit,
    onGoalClick: (Goal) -> Unit,
    onDeleteGoal: (Goal) -> Unit,
    onCreateSimilarGoal: (Goal) -> Unit = {},
) {
    if (goals.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No goals found",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = Outfit,
                fontSize = 18.sp
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(goals, key = { it.id }) { goal ->
                if (showHistory) {
                    PastGoalCard(
                        goal = goal,
                        onClick = { onGoalClick(goal) },
                        onCreateSimilar = onCreateSimilarGoal
                    )
                } else {
                    GoalCard(
                        goal = goal,
                        isEditing = isEditing,
                        isIndividualEditing = isIndividualEditing,
                        updateIsIndividualEditing = updateIsIndividualEditing,
                        isSelected = selectedItem.contains(goal.id),
                        addSelectedItem = addSelectedItem,
                        removeSelectedItem = removeSelectedItem,
                        onAddRemoveFunds = { onAddRemoveFunds(goal) },
                        onDeleteGoal = onDeleteGoal,
                        parent = "Goals",
                        onClick = { onGoalClick(goal) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}
