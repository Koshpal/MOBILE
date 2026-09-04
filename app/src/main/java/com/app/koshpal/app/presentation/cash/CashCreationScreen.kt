package com.app.koshpal.app.presentation.cash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.app.koshpal.app.presentation.transactions.TransactionCreationScreen
import com.app.koshpal.app.viewmodels.transactionsviewmodel.TransactionCreationViewModel

@Composable
fun CashCreationScreen(
    viewModel: TransactionCreationViewModel,
    onToPreviousScreen: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.onCashToggle(true)
        viewModel.onModeChange("Cash")
    }

    TransactionCreationScreen(
        viewModel = viewModel,
        onToPreviousScreen = onToPreviousScreen
    )
}
