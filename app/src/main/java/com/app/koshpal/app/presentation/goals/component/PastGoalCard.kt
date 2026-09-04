package com.app.koshpal.app.presentation.goals.component


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.core.presentation.util.truncateTitle
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import java.text.NumberFormat
import java.util.*

@Composable
fun PastGoalCard(
    goal: Goal,
    onClick: () -> Unit,
    onCreateSimilar: (Goal) -> Unit = {}
) {
    val baseColor = Color(goal.colorHex.toColorLong())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .size(48.dp),
                        colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.2f)),
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (goal.imageUri != null) {
                                AsyncImage(
                                    model = goal.imageUri,
                                    contentDescription = "Goal Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                val iconRes = goal.iconResId.toDrawableResId()
                                if (iconRes != null) {
                                    Icon(
                                        painter = painterResource(id = iconRes),
                                        contentDescription = null,
                                        tint = baseColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    val words = goal.title.trim().split("\\s+".toRegex())
                                    val initials = if (words.size >= 2) "${words[0].first()}${words[1].first()}" else "${words[0].first()}"
                                    Text(
                                        text = initials.uppercase(),
                                        color = baseColor,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Jakarta
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Column {
                        Text(
                            text = goal.title.truncateTitle(20),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = Jakarta,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "₹${NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")).format(goal.targetAmount)}",
                            color = MaterialTheme.colorScheme.outline,
                            fontFamily = Outfit,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Icon(
                    painter = painterResource(id = R.drawable.check_circle_24px),
                    contentDescription = "Completed",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Completed on ${goal.creationDate}", 
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    fontFamily = Outfit
                ),
                color = Color.Gray
            )

            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "Completed in ${goal.durationMonths} months",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Outfit
                    ),
                    color = Color(0xFF2E7D32)
                )
            }
            Card(
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                onClick = { onCreateSimilar(goal) }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create similar goal",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
