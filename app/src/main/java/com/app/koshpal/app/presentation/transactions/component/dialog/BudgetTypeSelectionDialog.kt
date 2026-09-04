package com.app.koshpal.app.presentation.transactions.component.dialog


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.ui.theme.*

@Composable
fun BudgetTypeSelectionDialog(
    onDismiss: () -> Unit,
    onTypeSelected: (BudgetType) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Select Budget Type",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Bold
                )

                BudgetTypeOption(
                    title = "Recurring",
                    subtitle = "Monthly, weekly cycles",
                    icon = R.drawable.event_repeat_24px,
                    color = SkyBlue,
                    onClick = {
                        onTypeSelected(BudgetType.RECURRING)
                        onDismiss()
                    }
                )

                BudgetTypeOption(
                    title = "One time",
                    subtitle = "Events, trips, projects",
                    icon = R.drawable.counter_1_24px,
                    color = SuccessGreen,
                    onClick = {
                        onTypeSelected(BudgetType.ONE_TIME)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun BudgetTypeOption(
    title: String,
    subtitle: String,
    icon: Int,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.05f))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(id = icon), contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = Outfit)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray, fontFamily = Outfit)
        }
    }
}
