package com.app.koshpal.app.presentation.cashflow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.resolveClassificationName
import com.app.koshpal.app.presentation.transactions.component.TransactionBar
import com.app.koshpal.app.viewmodels.cashflowviewmodel.CashFlowViewModel
import com.app.koshpal.ui.theme.AccentBlue
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.SetStatusBarAppearance
import com.app.koshpal.ui.theme.SetStatusBarVisibility
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomingTransactionsScreen(
    viewModel: CashFlowViewModel,
    onToPreviousScreen: () -> Unit,
    onTransactionClick: (String) -> Unit = {},
    onDeleteTransaction: (String) -> Unit = {},
) {
    SetStatusBarAppearance(isDarkIcons = false)
    SetStatusBarVisibility(isVisible = true)

    val incomeThisMonth by viewModel.incomeThisMonth.collectAsStateWithLifecycle()
    val incomingTransactions by viewModel.incomingTransactions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }
    val blueColor = AccentBlue

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(16.dp),
                    onClick = onToPreviousScreen
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Incoming",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Jakarta
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f))
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier.fillMaxSize(),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontFamily = Outfit
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        placeholder = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.search_24px),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Search payments, tags...",
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                    fontFamily = Outfit,
                                    fontSize = 16.sp
                                )
                            }
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.close_24px),
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                Card(
                    modifier = Modifier.size(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f)),
                    onClick = { }
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.tune_24px),
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(140.dp)
                            .offset(x = 30.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {}

                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Income this month",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = Outfit,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+ ₹${formatter.format(incomeThisMonth.toInt())}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = Jakarta
                            ),
                            color = blueColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val fillFraction = if (incomeThisMonth > 0) 1.0f else 0.0f
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = blueColor.copy(alpha = 0.2f)
                        ) {
                            if (fillFraction > 0f) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(fillFraction),
                                    shape = RoundedCornerShape(4.dp),
                                    color = blueColor
                                ) {}
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(8.dp),
                                shape = CircleShape,
                                color = blueColor
                            ) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Incoming",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = Outfit,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(incomingTransactions, key = { it.id }) { transaction ->
                    TransactionBar(
                        transaction = transaction,
                        formatter = formatter,
                        classificationName = transaction.resolveClassificationName(transaction.category, null),
                        onTransactionClick = { onTransactionClick(transaction.id) },
                        onDeleteTransaction = { onDeleteTransaction(transaction.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
