package com.app.koshpal.app.presentation.budget.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.ui.theme.LocalExtendedColors
import com.app.koshpal.ui.theme.Outfit
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetTrendSection(
    modifier: Modifier = Modifier,
    budget: Budget? = null,
    totalSpent: Double = 0.0,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val totalAmount = budget?.amount ?: 5000.0
    val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }
    val spentText = "₹${formatter.format(totalSpent.toLong())}"
    val plannedText = " / ₹${formatter.format(totalAmount.toLong())}"
    val percentage = if (totalAmount > 0) ((totalSpent / totalAmount) * 100).toInt() else 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Budget Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
                Icon(
                    painter = painterResource(id = if (isExpanded) R.drawable.keyboard_arrow_up_24px else R.drawable.keyboard_arrow_down_24px),
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.70f)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    TrendHeader(percentage = percentage)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                                append(spentText)
                            }
                            withStyle(style = SpanStyle(color = Color.Gray)) {
                                append(plannedText)
                            }
                        },
                        fontFamily = Outfit,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    BudgetTrendChart(
                        successColor = LocalExtendedColors.current.success,
                        percentage = percentage
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendHeader(percentage: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "Budget Trend",
                fontFamily = Outfit,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$percentage%",
                fontFamily = Outfit,
                fontSize = 12.sp,
                color = if (percentage >= 100) MaterialTheme.colorScheme.error else LocalExtendedColors.current.success,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "used",
                fontFamily = Outfit,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
        
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(0.5.dp, Color.LightGray),
            modifier = Modifier.height(32.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Month", fontSize = 12.sp, fontFamily = Outfit)
                Icon(painterResource(id = R.drawable.keyboard_arrow_down_24px), contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun BudgetTrendChart(successColor: Color, percentage: Int) {
    val textMeasurer = rememberTextMeasurer()
    val primaryColor = MaterialTheme.colorScheme.primary
    val gridColor = Color.LightGray.copy(alpha = 0.5f)
    
    val labels = listOf("10k", "5k", "1k", "500", "100", "0")
    val months = listOf("Feb'26", "Mar'26", "Apr'26", "May'26", "Jun'26")
    val activeRatio = (percentage / 100f).coerceIn(0.05f, 1.0f)
    val values = listOf(0.15f, 0.45f, 0.30f, 0.50f, activeRatio)
    
    val outfitStyle = TextStyle(
        fontFamily = Outfit,
        fontSize = 10.sp,
        color = Color.Gray
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val width = size.width
        val height = size.height
        val paddingRight = 40.dp.toPx()
        val bottomPadding = 30.dp.toPx()
        val chartWidth = width - paddingRight
        val chartHeight = height - bottomPadding
        
        val lineCount = labels.size
        val lineSpacing = chartHeight / (lineCount - 1)
        
        labels.forEachIndexed { index, label ->
            val y = index * lineSpacing
            
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(chartWidth, y),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
            
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                style = outfitStyle,
                topLeft = Offset(chartWidth + 8.dp.toPx(), y - 8.dp.toPx())
            )
        }
        
        val barWidth = 10.dp.toPx()
        val barSpacing = chartWidth / (months.size + 1)
        
        months.forEachIndexed { index, month ->
            val x = (index + 1) * barSpacing
            val barHeight = values[index] * chartHeight
            
            val isHighlighted = index == months.size - 1
            val color = if (isHighlighted) primaryColor else primaryColor.copy(alpha = 0.3f)
            
            drawRoundRect(
                color = color,
                topLeft = Offset(x - barWidth / 2, chartHeight - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            
            if (isHighlighted) {
                val bubbleWidth = 34.dp.toPx()
                val bubbleHeight = 18.dp.toPx()
                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.1f),
                    topLeft = Offset(x - bubbleWidth / 2, chartHeight - barHeight - bubbleHeight - 8.dp.toPx()),
                    size = Size(bubbleWidth, bubbleHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                
                val percentText = "$percentage%"
                val textLayout = textMeasurer.measure(
                    text = percentText,
                    style = outfitStyle.copy(color = primaryColor, fontWeight = FontWeight.Bold)
                )
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        x - textLayout.size.width / 2,
                        chartHeight - barHeight - bubbleHeight - 8.dp.toPx() + (bubbleHeight - textLayout.size.height) / 2
                    )
                )
                
                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, chartHeight)
                )
            } else {
                drawCircle(
                    color = successColor,
                    radius = 3.dp.toPx(),
                    center = Offset(x, chartHeight)
                )
            }
            
            val textLayout = textMeasurer.measure(text = month, style = outfitStyle)
            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(x - textLayout.size.width / 2, chartHeight + 8.dp.toPx())
            )
        }
        
        drawLine(
            color = successColor,
            start = Offset(0f, chartHeight),
            end = Offset(chartWidth, chartHeight),
            strokeWidth = 2.dp.toPx()
        )
    }
}
