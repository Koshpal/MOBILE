package com.app.koshpal.app.presentation.cashflow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.R
import com.app.koshpal.app.fluxdeck.CashFlowFluxDeck.CashFlowPoint
import com.app.koshpal.app.viewmodels.cashflowviewmodel.CashFlowViewModel
import com.app.koshpal.ui.theme.AccentBlue
import com.app.koshpal.ui.theme.AccentTeal
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.SetStatusBarAppearance
import com.app.koshpal.ui.theme.SetStatusBarVisibility
import java.text.NumberFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashFlowHomeScreen(
    viewModel: CashFlowViewModel,
    onToPreviousScreen: () -> Unit,
    onToIncoming: () -> Unit,
    onToOutgoing: () -> Unit,
    onToAddTransaction: () -> Unit = {},
) {
    SetStatusBarAppearance(isDarkIcons = false)
    SetStatusBarVisibility(isVisible = true)

    val incomeThisMonth by viewModel.incomeThisMonth.collectAsStateWithLifecycle()
    val expenseThisMonth by viewModel.expenseThisMonth.collectAsStateWithLifecycle()
    val leftThisMonth by viewModel.leftThisMonth.collectAsStateWithLifecycle()
    val investedThisMonth by viewModel.investedThisMonth.collectAsStateWithLifecycle()
    val trendData by viewModel.dualLineTrendData.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()

    val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }
    val selectedMonthStr = remember(selectedMonth) {
        selectedMonth?.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)) ?: "All Time"
    }

    val blueColor = AccentBlue
    val tealColor = AccentTeal

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
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
                        text = "Cash Flow",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = Jakarta
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }


            }

            Spacer(modifier = Modifier.height(12.dp))

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
                            .size(160.dp)
                            .offset(x = 40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {}

                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Left this month",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = Outfit,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹${formatter.format(leftThisMonth.toInt())}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = Jakarta
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "after ₹${formatter.format(expenseThisMonth.toInt())} spent and ₹${formatter.format(investedThisMonth.toInt())} invested",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = Outfit,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val totalRatio = (incomeThisMonth + expenseThisMonth).coerceAtLeast(1.0)
                        val outgoingRatio = (expenseThisMonth / totalRatio).toFloat().coerceIn(0.05f, 0.95f)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(outgoingRatio),
                                color = tealColor
                            ) {}
                            Surface(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f - outgoingRatio),
                                color = blueColor
                            ) {}
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(8.dp),
                                    shape = CircleShape,
                                    color = tealColor
                                ) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Outgoing",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = Outfit,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

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
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.selectPreviousMonth() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                                    contentDescription = "Previous Month",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = selectedMonthStr,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = Jakarta,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(
                                onClick = { viewModel.selectNextMonth() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_forward_ios_24px),
                                    contentDescription = "Next Month",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        DualLineChart(
                            trendData = trendData,
                            selectedMonth = selectedMonth,
                            onSelectMonth = { viewModel.onSelectedMonthChange(it) },
                            blueColor = blueColor,
                            tealColor = tealColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(10.dp),
                                    shape = CircleShape,
                                    color = blueColor
                                ) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Incoming", style = MaterialTheme.typography.labelMedium, fontFamily = Outfit)
                            }
                            Spacer(modifier = Modifier.width(24.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(10.dp),
                                    shape = CircleShape,
                                    color = tealColor
                                ) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Outgoing", style = MaterialTheme.typography.labelMedium, fontFamily = Outfit)
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    onClick = onToIncoming
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Incoming transactions",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Jakarta
                            )
                            Text(
                                text = selectedMonthStr,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = Outfit,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_forward_ios_24px),
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    onClick = onToOutgoing
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Outgoing transactions",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Jakarta
                            )
                            Text(
                                text = selectedMonthStr,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = Outfit,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_forward_ios_24px),
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DualLineChart(
    trendData: List<CashFlowPoint>,
    selectedMonth: YearMonth?,
    onSelectMonth: (YearMonth) -> Unit,
    blueColor: Color,
    tealColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(modifier = modifier) {
            if (trendData.isEmpty()) return@Canvas

            val maxVal = (trendData.flatMap { listOf(it.incoming, it.outgoing) }.maxOrNull() ?: 1000.0).coerceAtLeast(100.0)
            val width = size.width
            val height = size.height

            val pointSpacing = width / (trendData.size - 1).coerceAtLeast(1)

            val greenPath = Path()
            val orangePath = Path()

            trendData.forEachIndexed { i, pt ->
                val x = i * pointSpacing
                val greenY = height - ((pt.incoming / maxVal) * height).toFloat().coerceIn(10f, height - 10f)
                val orangeY = height - ((pt.outgoing / maxVal) * height).toFloat().coerceIn(10f, height - 10f)

                if (i == 0) {
                    greenPath.moveTo(x, greenY)
                    orangePath.moveTo(x, orangeY)
                } else {
                    val prevX = (i - 1) * pointSpacing
                    val prevGreenPt = trendData[i - 1]
                    val prevOrangePt = trendData[i - 1]
                    val prevGreenY = height - ((prevGreenPt.incoming / maxVal) * height).toFloat().coerceIn(10f, height - 10f)
                    val prevOrangeY = height - ((prevOrangePt.outgoing / maxVal) * height).toFloat().coerceIn(10f, height - 10f)

                    greenPath.cubicTo(
                        (prevX + x) / 2f, prevGreenY,
                        (prevX + x) / 2f, greenY,
                        x, greenY
                    )
                    orangePath.cubicTo(
                        (prevX + x) / 2f, prevOrangeY,
                        (prevX + x) / 2f, orangeY,
                        x, orangeY
                    )
                }
            }

            drawPath(path = greenPath, color = blueColor, style = Stroke(width = 2.5.dp.toPx()))
            drawPath(path = orangePath, color = tealColor, style = Stroke(width = 2.5.dp.toPx()))

            trendData.forEachIndexed { i, pt ->
                val x = i * pointSpacing
                val greenY = height - ((pt.incoming / maxVal) * height).toFloat().coerceIn(10f, height - 10f)
                val orangeY = height - ((pt.outgoing / maxVal) * height).toFloat().coerceIn(10f, height - 10f)
                val isSelected = pt.yearMonth == selectedMonth

                val radius = if (isSelected) 5.5.dp.toPx() else 3.5.dp.toPx()
                drawCircle(color = blueColor, radius = radius, center = Offset(x, greenY))
                drawCircle(color = tealColor, radius = radius, center = Offset(x, orangeY))
            }
        }

        if (trendData.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                trendData.forEach { pt ->
                    val isSelected = pt.yearMonth == selectedMonth
                    Text(
                        text = pt.monthLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = Outfit,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { onSelectMonth(pt.yearMonth) }
                    )
                }
            }
        }
    }
}
