package com.app.koshpal.app.presentation.budget.component

import com.app.koshpal.app.domain.model.*

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.ui.theme.LocalExtendedColors
import com.app.koshpal.ui.theme.Outfit

@Composable
fun BudgetTrendSection() {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
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
                    color = MaterialTheme.colorScheme.onSurface
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
                    TrendHeader()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                                append("₹4,000")
                            }
                            withStyle(style = SpanStyle(color = Color.Gray)) {
                                append(" / ₹5,000")
                            }
                        },
                        fontFamily = Outfit,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    BudgetTrendChart(LocalExtendedColors.current.success)
                }
            }
        }
    }
}

@Composable
private fun TrendHeader() {
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
                text = "+11.2%",
                fontFamily = Outfit,
                fontSize = 12.sp,
                color = LocalExtendedColors.current.success,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "vs last month",
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
private fun BudgetTrendChart(successColor: Color) {
    val textMeasurer = rememberTextMeasurer()
    val primaryColor = MaterialTheme.colorScheme.primary
    val gridColor = Color.LightGray.copy(alpha = 0.5f)
    
    val labels = listOf("10k", "5k", "1k", "500", "100", "0")
    val months = listOf("Feb'26", "Mar'26", "Apr'26", "May'26", "Jun'26")
    val values = listOf(0.15f, 0.85f, 0.05f, 0.05f, 0.05f)
    
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
            
            val isHighlighted = index == 1
            val color = if (isHighlighted) primaryColor else primaryColor.copy(alpha = 0.3f)
            
            drawRoundRect(
                color = color,
                topLeft = Offset(x - barWidth / 2, chartHeight - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            
            if (isHighlighted) {
                val bubbleWidth = 30.dp.toPx()
                val bubbleHeight = 18.dp.toPx()
                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.1f),
                    topLeft = Offset(x - bubbleWidth / 2, chartHeight - barHeight - bubbleHeight - 8.dp.toPx()),
                    size = Size(bubbleWidth, bubbleHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                
                val percentText = "80%"
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
