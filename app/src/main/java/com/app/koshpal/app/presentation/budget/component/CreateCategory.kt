package com.app.koshpal.app.presentation.budget.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.app.domain.model.CategoryAllocationUiState
import com.app.koshpal.app.domain.model.availableCategoryColors
import com.app.koshpal.app.domain.model.availableCategoryIcons
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.ui.theme.Outfit

@Composable
fun CreateCategory(
    modifier: Modifier = Modifier,
    categoryColor: String,
    categoryIcon: String,
    categoryTitle: String,
    updateCategoryColor: (String) -> Unit,
    updateCategoryIcon: (String) -> Unit,
    updateCategoryTitle: (String) -> Unit,
    activeColor: Color,
    categoryType: String = "category",
    onCreateClick: () -> Unit = {},
    subAllocations: List<CategoryAllocationUiState> = emptyList(),
    onCategoryAmountChange: (String, String) -> Unit = { _, _ -> },
    onRemoveSubCategory: (String) -> Unit = {},
    isError: Boolean = false,
    showSubCategoryDialog: MutableState<Boolean> = mutableStateOf(false),
    scrollState: ScrollState = rememberScrollState()
) {
    val effectiveActiveColor = try {
        if (categoryColor.isNotEmpty()) Color(categoryColor.toColorLong()) else activeColor
    } catch (_: Exception) {
        activeColor
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .then(if (categoryType == "sub-category") Modifier.wrapContentHeight() else Modifier.fillMaxHeight(0.9f))
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if(categoryType != "sub-category")  "Name your Category" else "Name your Sub-category",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                value = categoryTitle,
                onValueChange = { updateCategoryTitle(it) },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {}
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

            if(categoryType != "sub-category"){
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Category Color",
                    fontFamily = Outfit,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    availableCategoryColors.forEach {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(it.toColorLong()).copy(alpha = 0.2f))
                                .clickable { updateCategoryColor(it) },
                            contentAlignment = Alignment.Center
                        ){
                            if (categoryColor == it) {
                                Icon(
                                    painter = painterResource(id = R.drawable.check_24px),
                                    contentDescription = null,
                                    tint = Color(it.toColorLong()),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if(categoryType != "sub-category") "Category icon" else "Sub-category icon",
                fontFamily = Outfit,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 210.dp)
                    .padding(vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                availableCategoryIcons.chunked(3).forEach { rowIcons ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowIcons.forEach { iconKey ->
                            val isSelected = iconKey.equals(categoryIcon, ignoreCase = true)
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
                                    .height(56.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                                onClick = { updateCategoryIcon(iconKey) }
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
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.block_24px),
                                            contentDescription = "No Icon",
                                            tint = iconTint,
                                            modifier = Modifier.size(24.dp)
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

            if(categoryType != "sub-category"){
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    subAllocations.forEach { item ->
                        val subColor = try {
                            Color(item.category.colorHex.toColorLong())
                        } catch (_: Exception) {
                            effectiveActiveColor
                        }
                        val dismissState = rememberSwipeToDismissBoxState(
                            initialValue = SwipeToDismissBoxValue.Settled,
                            confirmValueChange = {
                                if (it != SwipeToDismissBoxValue.Settled) {
                                    onRemoveSubCategory(item.category.id)
                                    true
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.errorContainer),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.delete_24px),
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                }
                            }
                        ) {
                            BudgetRow(
                                icon = item.category.iconResId?.toDrawableResId(),
                                iconBackground = subColor.copy(alpha = 0.2f),
                                iconTint = subColor,
                                label = item.category.title,
                                amount = item.amountString,
                                onAmountChange = { newText ->
                                    onCategoryAmountChange(item.category.id, newText)
                                },
                                isError = isError
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .clickable { showSubCategoryDialog.value = true }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Card(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        onClick = { showSubCategoryDialog.value = true }
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
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = "Add Sub-category",
                        fontFamily = Outfit,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(56.dp),
            onClick = { onCreateClick() },
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "CREATE",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
