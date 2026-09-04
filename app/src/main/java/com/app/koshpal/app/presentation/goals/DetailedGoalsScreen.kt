package com.app.koshpal.app.presentation.goals

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.viewmodels.goalsviewmodel.GoalViewModel
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import java.text.NumberFormat
import java.util.*
import com.app.koshpal.app.presentation.goals.component.dialog.AddRemoveFundsDialog

@Composable
fun DetailedGoalsScreen(
    viewModel: GoalViewModel,
    onToPreviousScreen: () -> Unit,
    onEditGoal: (Goal) -> Unit = {}
) {
    val goal by viewModel.activeGoal.collectAsStateWithLifecycle()
    val tag by viewModel.activeGoalTag.collectAsStateWithLifecycle()

    var showAddRemoveDialog by remember { mutableStateOf(false) }

    val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }

    if (showAddRemoveDialog && goal != null) {
        AddRemoveFundsDialog(
            goal = goal!!,
            availableBalance = 100000.0,
            onDismiss = { showAddRemoveDialog = false },
            onConfirm = { amount, isAdding ->
                if (isAdding) {
                    viewModel.addFunds(goal!!, amount)
                } else {
                    viewModel.removeFunds(goal!!, amount)
                }
                showAddRemoveDialog = false
            }
        )
    }

    if (goal == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentGoal = goal!!
    val goalColor = Color(currentGoal.colorHex.toColorLong())
    val headerBgColor =  Color(currentGoal.colorHex.toColorLong()).copy(0.2f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(headerBgColor)
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
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                        contentDescription = "Back",
                        tint = goalColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Card(
                modifier = Modifier.size(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                onClick = { onEditGoal(currentGoal) }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings_24px),
                        contentDescription = "Settings",
                        tint = goalColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.size(100.dp),
                colors = CardDefaults.cardColors(containerColor = goalColor.copy(alpha = 0.2f)),
                shape = CircleShape,
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentGoal.imageUri != null) {
                        AsyncImage(
                            model = currentGoal.imageUri,
                            contentDescription = "Goal Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        val iconRes = currentGoal.iconResId.toDrawableResId()
                        if (iconRes != null) {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                tint = goalColor,
                                modifier = Modifier.size(50.dp)
                            )
                        } else {
                            val words = currentGoal.title.trim().split("\\s+".toRegex())
                            val initials = if (words.size >= 2) "${words[0].first()}${words[1].first()}" else "${words[0].first()}"
                            Text(
                                text = initials.uppercase(),
                                color = goalColor,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Jakarta
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = currentGoal.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Jakarta),
                color = goalColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = goalColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = viewModel.getTimeRemaining(currentGoal),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Jakarta),
                    color = goalColor
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = CardDefaults.cardColors(containerColor = goalColor),
                shape = CircleShape,
                onClick = { showAddRemoveDialog = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add/ Remove Funds",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "You've already saved",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = Outfit
                            )
                            Text(
                                text = "₹${formatter.format(currentGoal.savedAmount)}",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black,
                                fontFamily = Outfit
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Target Amount", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text("₹${formatter.format(currentGoal.targetAmount)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Recommended to save/ day", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(viewModel.getRecommendedPerDay(currentGoal), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Remaining to Save",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Text(
                            "₹${formatter.format((currentGoal.targetAmount - currentGoal.savedAmount).coerceAtLeast(0.0))}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { currentGoal.progress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = goalColor,
                                trackColor = Color.LightGray.copy(alpha = 0.3f),
                                strokeCap = StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("${currentGoal.progressPercentage}%", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (currentGoal.progress < 0.5) "You need to speed-up your savings" else "Almost there! You will reach your goal soon",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(76.dp))
                }
            }
        }
    }
}

