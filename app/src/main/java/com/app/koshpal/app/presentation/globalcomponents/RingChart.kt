package com.app.koshpal.app.presentation.globalcomponents

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit

@Composable
fun RingChart(
    segments: List<RingChartSegment>,
    centerLabel: String,
    centerValue: String,
    modifier: Modifier = Modifier
) {
    val emptyColor = MaterialTheme.colorScheme.primaryContainer
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            var startAngle = -90f

            drawArc(
                color = emptyColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )

            segments.forEach { segment ->
                val sweepAngle = segment.percentage * 360f
                drawArc(
                    color = segment.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
                startAngle += sweepAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerValue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Jakarta,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = centerLabel,
                fontSize = 10.sp,
                fontFamily = Outfit,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.70f)
            )
        }
    }
}
