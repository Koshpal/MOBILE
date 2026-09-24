package com.app.koshpal.app.presentation.goals.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.app.domain.model.GoalSavingsSummary
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SavingsSummaryCard(
    modifier: Modifier = Modifier,
    summary: GoalSavingsSummary,
    monthlyColor: Color = Color(0xFF3F51B5),
    manualColor: Color = Color(0xFF00897B)
) {
    val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }

    Card(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Savings Summary",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = Jakarta
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Semicircular Savings Chart (180 degree arc)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                ) {
                    val strokeWidth = 22.dp.toPx()

                    // The chart is a semicircle, so its diameter should fit the width
                    // while only half of that diameter is required vertically.
                    val diameter = size.width - strokeWidth
                    val chartHeight = diameter / 2f

                    val top = size.height - chartHeight - strokeWidth / 2f
                    val left = strokeWidth / 2f

                    val monthlySweep =
                        (summary.monthlyDepositPercentage / 100f) * 180f

                    val manualSweep =
                        (summary.manualTopUpPercentage / 100f) * 180f

                    drawArc(
                        color = monthlyColor,
                        startAngle = 180f,
                        sweepAngle = monthlySweep,
                        useCenter = false,
                        topLeft = Offset(left, top),
                        size = Size(diameter, diameter),
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Butt
                        )
                    )

                    drawArc(
                        color = manualColor,
                        startAngle = 180f + monthlySweep,
                        sweepAngle = manualSweep,
                        useCenter = false,
                        topLeft = Offset(left, top),
                        size = Size(diameter, diameter),
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Butt
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-8).dp)
                ) {
                    Text(
                        text = "${summary.totalTimesSaving}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Jakarta,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "times saving",
                        fontSize = 12.sp,
                        fontFamily = Outfit,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(10.dp),
                    shape = CircleShape,
                    color = monthlyColor
                ) {}
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Monthly Deposit",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Outfit,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${summary.monthlyDepositTimes} times",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = Outfit,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${formatter.format(summary.monthlyDepositAmount.toInt())}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Jakarta,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${summary.monthlyDepositPercentage}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = Outfit,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(10.dp),
                    shape = CircleShape,
                    color = manualColor
                ) {}
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Manually Top Up",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Outfit,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${summary.manualTopUpTimes} times",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = Outfit,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${formatter.format(summary.manualTopUpAmount.toInt())}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Jakarta,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${summary.manualTopUpPercentage}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = Outfit,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
