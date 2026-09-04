package com.app.koshpal.app.presentation.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.koshpal.R
import com.app.koshpal.ui.theme.AccentBlue
import com.app.koshpal.ui.theme.AccentTeal
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.LightBlack
import com.app.koshpal.ui.theme.Outfit
import java.text.NumberFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CashFlowCard(
    modifier: Modifier = Modifier,
    incomeAmount: Double = 273563.0,
    expensesAmount: Double = 134565.0,
    untaggedAmount: Double = 12405.0,
    onToCashFlow: () -> Unit = {},
) {
    val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }
    val currentMonthStr = remember {
        YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM\nyyyy", Locale.ENGLISH))
    }

    val blueColor = AccentBlue
    val tealColor = AccentTeal
    val grayColor = LightBlack

    val total = (incomeAmount + expensesAmount + untaggedAmount).coerceAtLeast(1.0)
    val incomePct = ((incomeAmount / total) * 100).toInt()
    val expensesPct = ((expensesAmount / total) * 100).toInt()
    val untaggedPct = (100 - incomePct - expensesPct).coerceAtLeast(0)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onToCashFlow
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cash Flow",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = Jakarta
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "+12.2%",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = Outfit,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF27AE60)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "vs past period",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = Outfit,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "This week",
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = Outfit,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Card(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        onClick = onToCashFlow,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.keyboard_arrow_right_24px),
                                contentDescription = "View Cash Flow",
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(130.dp)) {
                        val strokeWidth = 24.dp.toPx()
                        val startAngle = -90f

                        val incomeSweep = (incomePct / 100f) * 360f
                        val expensesSweep = (expensesPct / 100f) * 360f
                        val untaggedSweep = (untaggedPct / 100f) * 360f

                        drawArc(
                            color = blueColor,
                            startAngle = startAngle,
                            sweepAngle = incomeSweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )

                        drawArc(
                            color = tealColor,
                            startAngle = startAngle + incomeSweep,
                            sweepAngle = expensesSweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )

                        drawArc(
                            color = grayColor,
                            startAngle = startAngle + incomeSweep + expensesSweep,
                            sweepAngle = untaggedSweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentMonthStr,
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = Jakarta,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("$incomePct%", style = MaterialTheme.typography.labelSmall, color = blueColor, fontFamily = Outfit, fontWeight = FontWeight.Bold)
                    Text("$expensesPct%", style = MaterialTheme.typography.labelSmall, color = tealColor, fontFamily = Outfit, fontWeight = FontWeight.Bold)
                    Text("$untaggedPct%", style = MaterialTheme.typography.labelSmall, color = grayColor, fontFamily = Outfit, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    Surface(modifier = Modifier.weight((incomePct / 100f).coerceAtLeast(0.01f)).fillMaxHeight(), color = blueColor) {}
                    Surface(modifier = Modifier.weight((expensesPct / 100f).coerceAtLeast(0.01f)).fillMaxHeight(), color = tealColor) {}
                    Surface(modifier = Modifier.weight((untaggedPct / 100f).coerceAtLeast(0.01f)).fillMaxHeight(), color = grayColor) {}
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(10.dp), shape = RoundedCornerShape(2.dp), color = blueColor) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Income", style = MaterialTheme.typography.bodyMedium, fontFamily = Outfit, color = MaterialTheme.colorScheme.onSurface)
                }
                Text("₹${formatter.format(incomeAmount.toInt())}", style = MaterialTheme.typography.bodyMedium, fontFamily = Jakarta, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(10.dp), shape = RoundedCornerShape(2.dp), color = tealColor) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Expenses", style = MaterialTheme.typography.bodyMedium, fontFamily = Outfit, color = MaterialTheme.colorScheme.onSurface)
                }
                Text("₹${formatter.format(expensesAmount.toInt())}", style = MaterialTheme.typography.bodyMedium, fontFamily = Jakarta, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(10.dp), shape = RoundedCornerShape(2.dp), color = grayColor) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Untagged", style = MaterialTheme.typography.bodyMedium, fontFamily = Outfit, color = MaterialTheme.colorScheme.onSurface)
                }
                Text("₹${formatter.format(untaggedAmount.toInt())}", style = MaterialTheme.typography.bodyMedium, fontFamily = Jakarta, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
