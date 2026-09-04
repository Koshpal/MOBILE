package com.app.koshpal.app.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.Notification
import com.app.koshpal.app.presentation.notifications.components.NotificationBarSection
import com.app.koshpal.app.viewmodels.notificationsviewmodel.NotificationsViewModel
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.SetStatusBarAppearance
import com.app.koshpal.ui.theme.SetStatusBarVisibility
import com.app.koshpal.ui.theme.WarningOrange
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onToPreviousScreen: () -> Unit,
    onNotificationClick: (Notification) -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val last7Days by viewModel.last7Days.collectAsStateWithLifecycle()
    val groupedNotifications by viewModel.groupedNotifications.collectAsStateWithLifecycle()

    var isStatusBarVisible by remember { mutableStateOf(true) }

    SetStatusBarAppearance(isDarkIcons = false)
    SetStatusBarVisibility(isVisible = isStatusBarVisible)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToPreviousScreen) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Notifications",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontFamily = Jakarta
                )
                Spacer(Modifier.width(48.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(last7Days) { timestamp ->
                    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                    val isSelected = timestamp == selectedDate
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.onDateSelected(timestamp) }
                    ) {
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = Outfit
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern("dd")),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                                    fontFamily = Jakarta
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.size(36.dp),
                        colors = CardDefaults.cardColors(containerColor = WarningOrange.copy(alpha = 0.2f)),
                        shape = CircleShape,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.notifications_24px),
                                contentDescription = null,
                                tint = WarningOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "You can handle your Notification permissions either using app info or for better personalisation you can use notification settings in Profile.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        lineHeight = 18.sp,
                        fontFamily = Outfit
                    )
                }
            }
        }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            NotificationBarSection(
                groupedNotifications = groupedNotifications,
                onNotificationClick = onNotificationClick
            )
        }
    }
}
