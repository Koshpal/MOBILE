package com.app.koshpal.app.presentation.tags.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.koshpal.R
import com.app.koshpal.app.presentation.globalcomponents.FilterToggleCard

@Composable
fun TagsFilterSection(
    modifier: Modifier = Modifier,
    showHidden: Boolean,
    onToggleHidden: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        FilterToggleCard(
            label = "Show hidden items",
            icon = R.drawable.visibility_off_24px,
            checked = showHidden,
            onCheckedChange = { onToggleHidden() }
        )
    }
}
