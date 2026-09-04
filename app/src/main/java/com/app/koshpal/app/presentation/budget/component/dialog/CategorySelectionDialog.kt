package com.app.koshpal.app.presentation.budget.component.dialog


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
import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.app.domain.model.defaultDialogCategories
import com.app.koshpal.app.domain.model.getInitials
import com.app.koshpal.app.domain.model.toColorLong
import com.app.koshpal.app.domain.model.toDrawableResId
import com.app.koshpal.app.presentation.budget.component.CreateCategory
import com.app.koshpal.ui.theme.Jakarta


@Composable
fun CategorySelectionDialog(
    onDismiss: () -> Unit,
    onCategorySelected: (Category) -> Unit,
    onCreateNewCategoryClick: () -> Unit = {},
    categories: List<Category> = defaultDialogCategories,
    categoryType: String = "category",
    isEditing: Boolean = false,
    categoryTitle: String = "",
    categoryColor: String = "",
    categoryIcon: String = "",
    updateCategoryTitle: (String) -> Unit = {},
    updateCategoryColor: (String) -> Unit = {},
    updateCategoryIcon: (String) -> Unit = {},
    onCreateClick: () -> Unit = {},
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 24.dp)
        ) {
            if(isEditing){
                Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                    CreateCategory(
                        categoryColor = categoryColor,
                        categoryIcon = categoryIcon,
                        categoryTitle = categoryTitle,
                        updateCategoryColor = updateCategoryColor,
                        updateCategoryIcon = updateCategoryIcon,
                        updateCategoryTitle = updateCategoryTitle,
                        categoryType = categoryType,
                        onCreateClick = onCreateClick,
                        activeColor = activeColor
                    )
                }
            }else{
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
                            .clickable { onCreateNewCategoryClick() },
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
                                onClick = onCreateNewCategoryClick
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.add_2_24px),
                                        contentDescription = if(categoryType == "category") "Create new sub-category" else "Create new category",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = if(categoryType == "category") "Create new category" else "Create new sub-category",
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
                        items(categories) { category ->
                            CategoryRowItem(
                                category = category,
                                onClick = { onCategorySelected(category) }
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun CategoryRowItem(
    category: Category,
    onClick: () -> Unit
) {
    val baseColor = Color(category.colorHex.toColorLong())
    val iconRes = category.iconResId?.toDrawableResId()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = category.title,
                        tint = baseColor,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = category.title.getInitials(),
                        color = baseColor,
                        fontFamily = Jakarta,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = category.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}