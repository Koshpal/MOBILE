package com.app.koshpal.app.presentation.globalcomponents

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf

val LocalBottomBarVisibility = compositionLocalOf<MutableState<Boolean>> {
    error("No BottomBarVisibility provided")
}
