package com.app.koshpal.app.presentation.dues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.app.viewmodels.duesviewmodel.DuesViewModel
import com.app.koshpal.core.presentation.util.toDisplayDate
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.SetStatusBarAppearance
import java.text.NumberFormat
import java.util.*

@Composable
fun DetailedDueScreen(
    viewModel: DuesViewModel,
    onToPreviousScreen: () -> Unit,
    onToSettings: () -> Unit = {}
) {
    val dues by viewModel.dues.collectAsStateWithLifecycle()
    val clickedDueId by viewModel.clickedDueId.collectAsStateWithLifecycle()
    
    val due = remember(clickedDueId, dues) {
        dues.find { it.id == clickedDueId }
    }

    if (due == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }
    val baseColor = due.colorHex?.let { Color(it.toColorLong()) } ?: MaterialTheme.colorScheme.primary
    val screenBgColor = baseColor.copy(alpha = 0.1f)

    SetStatusBarAppearance(isDarkIcons = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                onClick = onToPreviousScreen
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                        contentDescription = "Back",
                        tint = baseColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = "Reminder Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = baseColor,
                fontFamily = Jakarta
            )
            Card(
                modifier = Modifier.size(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                onClick = onToSettings
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings_24px),
                        contentDescription = "Settings",
                        tint = baseColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.size(80.dp),
                colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.2f)),
                shape = CircleShape,
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val iconRes = (due.iconResId ?: due.reminderType)?.toDrawableResId() ?: R.drawable.notifications_24px
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = baseColor,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = due.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Jakarta),
                color = Color.Black
            )
            Text(
                text = due.reminderType ?: "Other",
                style = MaterialTheme.typography.bodyMedium,
                color = baseColor,
                fontFamily = Outfit
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize()
            ) {
                DetailItem(label = "Amount", value = "₹${formatter.format(due.amount)}", icon = R.drawable.payments_24px, color = baseColor)
                Spacer(modifier = Modifier.height(24.dp))
                DetailItem(label = "Due Date", value = due.date.toDisplayDate(), icon = R.drawable.calendar_month_24px, color = baseColor)
                Spacer(modifier = Modifier.height(24.dp))
                DetailItem(label = "Frequency", value = "Repeats ${due.frequency}", icon = R.drawable.history_24px, color = baseColor)
                Spacer(modifier = Modifier.height(24.dp))
                DetailItem(label = "Status", value = due.status, icon = if (due.isCompleted) R.drawable.check_circle_24px else R.drawable.calendar_clock_24px, color = if (due.isCompleted) Color(0xFF4CAF50) else Color(0xFFF44336))
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { viewModel.toggleDueCompletion(due.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (due.isCompleted) Color.Gray else baseColor)
                ) {
                    Text(
                        text = if (due.isCompleted) "Mark as Pending" else "Mark as Completed",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Jakarta
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, icon: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = painterResource(id = icon), contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray, fontFamily = Outfit)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = Jakarta)
        }
    }
}
