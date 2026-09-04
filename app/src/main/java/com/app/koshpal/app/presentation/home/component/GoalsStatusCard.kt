package com.app.koshpal.app.presentation.home.component


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit

@Composable
fun GoalsStatusCard(
    goals: List<Goal>,
    onToGoals: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToGoals,
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
                    "Your Goals Status",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Jakarta
                )
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "This week",
                            fontSize = 12.sp,
                            fontFamily = Outfit,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Card(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    onClick = onToGoals,
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
            Spacer(modifier = Modifier.height(24.dp))
            if (goals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No goals tracked yet", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    goals.take(6).forEach { goal ->
                        GoalStatusItem(goal)
                    }
                }
            }
        }
    }
}

@Composable
fun GoalStatusItem(goal: Goal) {
    val baseColor = Color(goal.colorHex.toColorLong())
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = goal.title,
            modifier = Modifier.width(120.dp),
            fontSize = 14.sp,
            fontFamily = Outfit,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(goal.progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(baseColor.copy(0.2f))
            )
        }
        Text(
            text = "${goal.progressPercentage}%",
            modifier = Modifier.padding(start = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Jakarta,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
