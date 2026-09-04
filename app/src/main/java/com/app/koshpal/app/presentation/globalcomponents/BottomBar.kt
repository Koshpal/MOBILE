package com.app.koshpal.app.presentation.globalcomponents


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.app.koshpal.R
import com.app.koshpal.app.presentation.navigation.Screen
import com.app.koshpal.ui.theme.SubSecondaryColor
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect

@Composable
fun BottomBar(
    navController: NavHostController,
    currentRoute: String?,
    hazeState: HazeState
) {
    val items = listOf(
        BottomBarItem.Home,
        BottomBarItem.Budget,
        BottomBarItem.Goals,
        BottomBarItem.Dues,
        BottomBarItem.Transactions
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.08f)
                .hazeEffect(state = hazeState){
                    blurEffect {
                        blurRadius = 2.5.dp
                        colorEffects = listOf(HazeColorEffect.tint(SubSecondaryColor.copy(alpha = 0.08f)))
                        noiseFactor = 0.07f
                    }
                }
                .align(Alignment.TopCenter)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.02f)
                .hazeEffect(state = hazeState){
                    blurEffect {
                        blurRadius = 4.dp
                        colorEffects = listOf(HazeColorEffect.tint(SubSecondaryColor.copy(alpha = 0.08f)))
                        noiseFactor = 0.08f
                    }
                }
                .align(Alignment.TopCenter)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight(0.08f)
                .fillMaxWidth(0.02f)
                .hazeEffect(state = hazeState){
                    blurEffect {
                        blurRadius = 4.dp
                        colorEffects = listOf(HazeColorEffect.tint(SubSecondaryColor.copy(alpha = 0.08f)))
                        noiseFactor = 0.08f
                    }
                }
                .align(Alignment.CenterStart)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight(0.08f)
                .fillMaxWidth(0.02f)
                .hazeEffect(state = hazeState){
                    blurEffect {
                        blurRadius = 4.dp
                        colorEffects = listOf(HazeColorEffect.tint(SubSecondaryColor.copy(alpha = 0.08f)))
                        noiseFactor = 0.08f
                    }
                }
                .align(Alignment.CenterEnd)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.02f)
                .hazeEffect(state = hazeState){
                    blurEffect {
                        blurRadius = 4.dp
                        colorEffects = listOf(HazeColorEffect.tint(SubSecondaryColor.copy(alpha = 0.08f)))
                        noiseFactor = 0.08f
                    }
                }
                .align(Alignment.BottomCenter)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.08f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                .border(
                    BorderStroke(0.6.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.76f)),
                    CircleShape
                )
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route || when (item) {
                    BottomBarItem.Budget -> currentRoute?.contains("budget", ignoreCase = true) == true
                    BottomBarItem.Goals -> currentRoute?.contains("goal", ignoreCase = true) == true
                    BottomBarItem.Dues -> currentRoute?.contains("due", ignoreCase = true) == true
                    BottomBarItem.Transactions -> currentRoute?.contains("transaction", ignoreCase = true) == true
                    else -> false
                }
                BottomNavItem(
                    modifier = Modifier.weight(1f),
                    item = item,
                    isSelected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    modifier: Modifier = Modifier,
    item: BottomBarItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
        label = "bgColor"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        label = "iconColor"
    )

    val widthFraction by animateFloatAsState(
        targetValue = if (isSelected) 0.98f else 0.85f,
        animationSpec = tween(330),
        label = ""
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(widthFraction)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = item.title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

sealed class BottomBarItem(
    val route: String,
    val title: String,
    val icon: Int
) {
    object Home : BottomBarItem(Screen.MainRoot.route, "Home", R.drawable.home_24px)
    object Budget: BottomBarItem(Screen.Graph.BUDGET, "Budget", R.drawable.account_balance_wallet_24px)
    object Goals : BottomBarItem(Screen.Graph.GOALS, "Goals", R.drawable.target_24px_2)
    object Dues : BottomBarItem(Screen.Graph.DUES, "Dues", R.drawable.calendar_clock_24px)
    object Transactions : BottomBarItem(Screen.Graph.TRANSACTION, "Transactions", R.drawable.account_balance_24px)
}
