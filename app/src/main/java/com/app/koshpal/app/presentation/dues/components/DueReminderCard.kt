package com.app.koshpal.app.presentation.dues.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Composable
fun DueReminderCard(
    due: Due,
    onReminderToggleChanged: (Boolean) -> Unit = {}
) {
    var isReminderOn by remember(due.id) { mutableStateOf(!due.isCompleted) }

    val formattedTime = remember(due.reminderTime) {
        val reminderTime = due.reminderTime
        if (reminderTime != null && reminderTime > 0) {
            try {
                val dateTime = Instant.fromEpochMilliseconds(reminderTime)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                formatTime12Hour(dateTime.hour, dateTime.minute)
            } catch (_: Exception) {
                "9:00 AM"
            }
        } else {
            "9:00 AM"
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFEFF4FF)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDBE7FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.notifications_24px),
                    contentDescription = "Reminder",
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Reminder",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontFamily = Outfit
                )
                Spacer(modifier = Modifier.height(2.dp))

                val annotatedSubtitle = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF64748B), fontFamily = Outfit, fontSize = 14.sp)) {
                        append("1 day before • ")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF2563EB), fontFamily = Jakarta, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
                        append(formattedTime)
                    }
                }

                Text(text = annotatedSubtitle)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Switch(
                    checked = isReminderOn,
                    onCheckedChange = {
                        isReminderOn = it
                        onReminderToggleChanged(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2563EB),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFCBD5E1)
                    )
                )

                Text(
                    text = if (isReminderOn) "On" else "Off",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontFamily = Outfit
                )
            }
        }
    }
}

private fun formatTime12Hour(hour: Int, minute: Int): String {
    val amPm = if (hour >= 12) "PM" else "AM"
    val hour12 = when (hour) {
        0 -> 12
        in 1..12 -> hour
        else -> hour - 12
    }
    val minuteStr = minute.toString().padStart(2, '0')
    return "$hour12:$minuteStr $amPm"
}
