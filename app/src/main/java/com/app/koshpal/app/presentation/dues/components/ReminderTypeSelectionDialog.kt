package com.app.koshpal.app.presentation.dues.components


import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.ReminderType
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId

@Composable
fun ReminderTypeSelectionDialog(
    onDismiss: () -> Unit,
    onTypeSelected: (ReminderType) -> Unit,
    onCreateNewTypeClick: () -> Unit,
    types: List<ReminderType>,
    isCreating: Boolean = false,
    typeTitle: String = "",
    typeColor: String = "",
    typeIcon: String = "",
    updateTypeTitle: (String) -> Unit = {},
    updateTypeColor: (String) -> Unit = {},
    updateTypeIcon: (String) -> Unit = {},
    onCreateClick: () -> Unit = {},
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            if (isCreating) {
                CreateReminderType(
                    typeColor = typeColor,
                    typeIcon = typeIcon,
                    typeTitle = typeTitle,
                    updateTypeColor = updateTypeColor,
                    updateTypeIcon = updateTypeIcon,
                    updateTypeTitle = updateTypeTitle,
                    onCreateClick = onCreateClick,
                    activeColor = activeColor
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onCreateNewTypeClick() },
                        color = Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                onClick = onCreateNewTypeClick
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.add_2_24px),
                                        contentDescription = "Create new reminder type",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Create new reminder type",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(types) { type ->
                            ReminderTypeRowItem(
                                type = type,
                                onClick = { onTypeSelected(type) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderTypeRowItem(
    type: ReminderType,
    onClick: () -> Unit
) {
    val baseColor = Color(type.colorHex.toColorLong())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(baseColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = type.iconResId!!.toDrawableResId() ?: R.drawable.notifications_24px),
                contentDescription = type.name,
                tint = baseColor,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = type.name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
