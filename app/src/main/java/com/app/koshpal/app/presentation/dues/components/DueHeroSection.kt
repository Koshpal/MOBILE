package com.app.koshpal.app.presentation.dues.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.app.presentation.util.toDrawableResId
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit

@Composable
fun DueHeroSection(
    due: Due,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            val iconRes = (due.iconResId ?: due.reminderType)?.toDrawableResId() ?: R.drawable.home_24px
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = due.title,
                tint = accentColor,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = due.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = Jakarta
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = due.reminderType ?: due.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = accentColor,
            fontFamily = Outfit
        )
    }
}
