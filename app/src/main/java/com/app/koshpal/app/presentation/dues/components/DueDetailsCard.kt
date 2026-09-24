package com.app.koshpal.app.presentation.dues.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.app.koshpal.core.presentation.util.toDisplayDate
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DueDetailsCard(
    due: Due,
    accentColor: Color
) {
    val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }
    val formattedAmount = remember(due.amount) { "₹${formatter.format(due.amount)}" }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Amount
            DetailRow(
                iconRes = R.drawable.payments_24px,
                label = "Amount",
                value = formattedAmount,
                accentColor = accentColor,
                hasChevron = false
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFF1F5F9)
            )

            // Due Date
            DetailRow(
                iconRes = R.drawable.calendar_month_24px,
                label = "Due Date",
                value = due.date.toDisplayDate(),
                accentColor = accentColor,
                hasChevron = true
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFF1F5F9)
            )

            // Frequency
            DetailRow(
                iconRes = R.drawable.history_24px,
                label = "Frequency",
                value = if (due.frequency.isBlank()) "Does not repeat" else due.frequency,
                accentColor = accentColor,
                hasChevron = true
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFF1F5F9)
            )

            // Status
            StatusDetailRow(
                due = due,
                accentColor = accentColor
            )
        }
    }
}

@Composable
private fun DetailRow(
    iconRes: Int,
    label: String,
    value: String,
    accentColor: Color,
    hasChevron: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                fontFamily = Outfit
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Jakarta
            )
        }

        if (hasChevron) {
            Icon(
                painter = painterResource(id = R.drawable.keyboard_arrow_right_24px),
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StatusDetailRow(
    due: Due,
    accentColor: Color
) {
    val isCompleted = due.isCompleted
    val statusText = if (isCompleted) "Completed" else "Pending"
    val badgeBg = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFECE0)
    val badgeTextColor = if (isCompleted) Color(0xFF2E7D32) else Color(0xFFE65100)
    val badgeIcon = if (isCompleted) R.drawable.check_circle_24px else R.drawable.calendar_clock_24px

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.calendar_clock_24px),
                contentDescription = "Status",
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Status",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                fontFamily = Outfit
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = statusText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Jakarta
            )
        }

        // Right Status Badge Chip
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = badgeBg
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = badgeIcon),
                    contentDescription = null,
                    tint = badgeTextColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor,
                    fontFamily = Jakarta
                )
            }
        }
    }
}
