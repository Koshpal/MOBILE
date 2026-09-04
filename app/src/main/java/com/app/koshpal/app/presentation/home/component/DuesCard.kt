package com.app.koshpal.app.presentation.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.ui.theme.Jakarta

@Composable
fun DuesCard(
    topDues: Map<String, List<DueWithMetadata>>,
    onToAddDue: () -> Unit,
    onToAllDues: () -> Unit,
    dueItem: @Composable (DueWithMetadata) -> Unit
) {
    var selectedTab by remember { mutableStateOf("To Pay") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToAllDues,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Dues & Reminders",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Jakarta
                )
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    onClick = onToAddDue,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_2_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Card(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    onClick = onToAllDues,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.keyboard_arrow_right_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                DashboardTabChip(
                    label = "To Pay",
                    isSelected = selectedTab == "To Pay"
                ) { selectedTab = "To Pay" }
                Spacer(modifier = Modifier.width(12.dp))
                DashboardTabChip(
                    label = "To Receive",
                    isSelected = selectedTab == "To Receive"
                ) { selectedTab = "To Receive" }
            }
            Spacer(modifier = Modifier.height(16.dp))
            val currentDues = topDues[selectedTab] ?: emptyList()
            if (currentDues.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No upcoming dues", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                currentDues.forEach { metadata ->
                    dueItem(metadata)
                    if (metadata != currentDues.last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
