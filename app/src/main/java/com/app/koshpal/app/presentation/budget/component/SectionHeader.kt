package com.app.koshpal.app.presentation.budget.component


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    titleFontSize: TextUnit = MaterialTheme.typography.titleSmall.fontSize,
    subtitleFontSize: TextUnit = MaterialTheme.typography.bodySmall.fontSize
) {
    Text(
        text = title,
        fontFamily = Jakarta,
        fontSize = titleFontSize,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
        text = subtitle,
        fontFamily = Outfit,
        fontSize = subtitleFontSize,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        lineHeight = 20.sp,
    )
}