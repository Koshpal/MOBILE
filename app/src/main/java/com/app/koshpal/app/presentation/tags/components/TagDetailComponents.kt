package com.app.koshpal.app.presentation.tags.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import java.text.NumberFormat
import java.util.Locale

val ChartColors = listOf(Color(0xFF7986CB), Color(0xFFFFB74D), Color(0xFFE57373), Color(0xFF4DB6AC), Color(0xFF90A4AE))

@Composable
fun TagCategoryChip(name: String, percentage: Int, tint: Color, icon: Int) {
    Card(
        shape = CircleShape,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon), 
                contentDescription = null, 
                modifier = Modifier.size(16.dp), 
                tint = tint
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = name, 
                fontSize = 13.sp, 
                fontWeight = FontWeight.Bold, 
                fontFamily = Outfit,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$percentage%", 
                fontSize = 13.sp, 
                color = MaterialTheme.colorScheme.outline, 
                fontFamily = Outfit
            )
        }
    }
}

@Composable
fun TagSummaryItem(label: String, value: String, valueColor: Color = Color.Black) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            fontFamily = Outfit,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.70f)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            fontFamily = Jakarta
        )
    }
}

@Composable
fun LegendItem(name: String, percent: Double, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Card(
            modifier = Modifier.size(10.dp),
            colors = CardDefaults.cardColors(containerColor = color),
            shape = CircleShape
        ) {
            Row(modifier = Modifier.fillMaxSize()) {}
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(name, fontSize = 13.sp, color = Color.Gray, fontFamily = Jakarta)
            Text("${percent.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = Jakarta, color = Color.Black)
        }
    }
}
