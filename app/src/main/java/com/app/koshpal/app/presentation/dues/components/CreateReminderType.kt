package com.app.koshpal.app.presentation.dues.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.availableCategoryColors
import com.app.koshpal.app.domain.model.availableCategoryIcons
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.ui.theme.Outfit

@Composable
fun CreateReminderType(
    modifier: Modifier = Modifier,
    typeColor: String,
    typeIcon: String,
    typeTitle: String,
    updateTypeColor: (String) -> Unit,
    updateTypeIcon: (String) -> Unit,
    updateTypeTitle: (String) -> Unit,
    activeColor: Color,
    onCreateClick: () -> Unit
) {
    val effectiveActiveColor = try {
        if (typeColor.isNotEmpty()) Color(typeColor.toColorLong()) else activeColor
    } catch (_: Exception) {
        activeColor
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight(0.8f)
    ) {
        Column {
            Text(
                text = "Name your Reminder Type",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                value = typeTitle,
                onValueChange = { updateTypeTitle(it) },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onCreateClick() }
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Type Color",
                fontFamily = Outfit,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                availableCategoryColors.forEach {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(it.toColorLong()).copy(alpha = 0.2f))
                            .clickable { updateTypeColor(it) },
                    ) {
                        if (typeColor == it) {
                            Icon(
                                painter = painterResource(id = R.drawable.check_24px),
                                contentDescription = null,
                                tint = Color(it.toColorLong()),
                                modifier = Modifier
                                    .size(12.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Type icon",
                fontFamily = Outfit,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                availableCategoryIcons.chunked(3).forEach { rowIcons ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowIcons.forEach { iconKey ->
                            val isSelected = iconKey.equals(typeIcon, ignoreCase = true)
                            val backgroundColor = if (isSelected) {
                                effectiveActiveColor.copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                            val iconTint = if (isSelected) {
                                effectiveActiveColor
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.5f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                                onClick = { updateTypeIcon(iconKey) }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val iconRes = iconKey.toDrawableResId()
                                    if (iconRes != null) {
                                        Icon(
                                            painter = painterResource(id = iconRes),
                                            contentDescription = null,
                                            tint = iconTint,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.block_24px),
                                            contentDescription = "No Icon",
                                            tint = iconTint,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                        repeat(3 - rowIcons.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            onClick = { onCreateClick() },
            shape = CircleShape,
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "CREATE",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
