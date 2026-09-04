package com.app.koshpal.app.presentation.notifications.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.Notification
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.core.data.entities.enums.NotificationType
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit

@Composable
fun NotificationBarSection(
    modifier: Modifier = Modifier,
    groupedNotifications: Map<NotificationType, List<Notification>>,
    onNotificationClick: (Notification) -> Unit
) {
    if (groupedNotifications.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "No notifications for this day",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
                fontFamily = Outfit
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            groupedNotifications.forEach { (type, notifications) ->
                item {
                    Column {
                        Text(
                            text = when(type) {
                                NotificationType.GOAL_INSIGHT -> "Goals"
                                NotificationType.TRANSACTION_ALERT -> "Transactions"
                                NotificationType.DUE_REMINDER -> "Dues & Reminders"
                                NotificationType.BUDGET_WATCH -> "Budgets"
                                NotificationType.ANOMALY_DETECTION -> "Insights"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Jakarta
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
                items(notifications, key = { it.id }) { notification ->
                    NotificationItemCard(
                        notification = notification,
                        onClick = { onNotificationClick(notification) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun NotificationItemCard(
    notification: Notification,
    onClick: () -> Unit
) {
    val iconResId = notification.iconResId?.toDrawableResId() ?: when(notification.type) {
        NotificationType.GOAL_INSIGHT -> R.drawable.flag_24px
        NotificationType.BUDGET_WATCH -> R.drawable.account_balance_wallet_24px
        NotificationType.TRANSACTION_ALERT -> R.drawable.call_made_24px
        NotificationType.DUE_REMINDER -> R.drawable.notifications_24px
        NotificationType.ANOMALY_DETECTION -> R.drawable.contact_support_24px
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = when(notification.type) {
                    NotificationType.GOAL_INSIGHT -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.primaryContainer
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = null,
                        tint = when(notification.type) {
                            NotificationType.GOAL_INSIGHT -> Color.Red.copy(alpha = 0.6f)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Outfit,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}
