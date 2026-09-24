package com.app.koshpal.app.presentation.dues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.presentation.dues.components.*
import com.app.koshpal.app.viewmodels.duesviewmodel.DuesViewModel
import com.app.koshpal.ui.theme.SetStatusBarAppearance

@Composable
fun DetailedDueScreen(
    viewModel: DuesViewModel,
    onToPreviousScreen: () -> Unit,
    onEditDue: () -> Unit = {}
) {
    val dues by viewModel.dues.collectAsStateWithLifecycle()
    val clickedDueId by viewModel.clickedDueId.collectAsStateWithLifecycle()

    val due = remember(clickedDueId, dues) {
        dues.find { it.id == clickedDueId }
    }

    if (due == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val accentColor = remember(due.colorHex) {
        due.colorHex?.let { Color(it.toColorLong()) } ?: Color(0xFFE65100)
    }

    SetStatusBarAppearance(isDarkIcons = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF7))
    ) {
        DetailedDueHeader(
            title = "Reminder Details",
            accentColor = accentColor,
            onBackClick = onToPreviousScreen,
            onEditClick = {
                viewModel.prepareEditDue(due)
                onEditDue()
            },
            onDeleteClick = {
                viewModel.deleteDue(due.id)
                onToPreviousScreen()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            DueHeroSection(
                due = due,
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            DueDetailsCard(
                due = due,
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            DueReminderCard(
                due = due,
                onReminderToggleChanged = { _ -> }
            )

            Spacer(modifier = Modifier.height(10.dp))

            DueRelatedTransactionsCard(
                transactionCount = 1,
                onClick = { }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DueActionsSection(
                isCompleted = due.isCompleted,
                onToggleCompletion = {
                    viewModel.toggleDueCompletion(due.id)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
