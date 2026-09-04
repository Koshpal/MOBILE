package com.app.koshpal.app.presentation.tags


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.availableCategoryColors
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.viewmodels.tagsviewmodel.TagsCreationViewModel
import com.app.koshpal.ui.theme.Outfit

@Composable
fun TagsCreationScreen(
    modifier: Modifier = Modifier,
    tagColor: String,
    tagName: String,
    updateTagColor: (String) -> Unit,
    updateTagName: (String) -> Unit,
    viewModel: TagsCreationViewModel,
    onCreateClick: () -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).components { add(GifDecoder.Factory()) }.build()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight(0.45f)
    ) {
        Column {
            Text(
                text = "Name your Tag",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                value = tagName,
                onValueChange = { updateTagName(it) },
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

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tag Color",
                fontFamily = Outfit,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                availableCategoryColors.forEach { colorHex ->
                    val color = Color(colorHex.toColorLong())
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f))
                            .clickable { updateTagColor(colorHex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (tagColor == colorHex) {
                            Icon(
                                painter = painterResource(id = R.drawable.check_24px),
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isLoading) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
            ),
            onClick = { if (!isLoading) onCreateClick() },
            shape = CircleShape,
            border = if (isLoading) BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline) else null
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoading) {
                    AsyncImage(
                        modifier = Modifier.size(24.dp),
                        model = ImageRequest.Builder(context)
                            .data(R.drawable.loading_indicator)
                            .build(),
                        imageLoader = imageLoader,
                        contentDescription = "Loading",
                    )
                } else {
                    Text(
                        text = "CREATE",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
