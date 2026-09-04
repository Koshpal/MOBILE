package com.app.koshpal.core.presentation.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun getImeAwarePadding(): Dp {
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val paddingAboveKeyboard: Dp = if (imeBottom > 0) 16.dp else 0.dp
    return paddingAboveKeyboard
}