package com.app.koshpal.app.presentation.onboarding


import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.koshpal.R
import com.app.koshpal.app.Events
import com.app.koshpal.app.viewmodels.authviewmodel.AuthViewModel
import com.app.koshpal.core.presentation.util.ObserveAsEvents
import com.app.koshpal.ui.theme.BrandingBlue
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.SecondaryColor
import com.app.koshpal.ui.theme.SetStatusBarAppearance
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun OnBoardQuestionScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel,
    onToMain: () -> Unit = {}
){
    val hazeState = rememberHazeState()
    val currentStep = remember { mutableIntStateOf(1) }
    val options = listOf("Saving Money", "Managing Spending", "Financial Goals", "Managing EMIs", "Investing")
    val options1 = listOf("Spending Too Much", "Saving Too Little", "Managing Debt", "Tracking Money", "Financial Stress")
    val options2 = listOf("Track Regularly", "Use Finance Apps", "Use Excel or Sheets", "Check Bank Statements", "Don't Track")
    val options3 = listOf("Controlling Spending", "Building Savings", "Managing Budget", "Managing EMIs & Bills", "Overall Financial Health")
    
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                if (event.message == "Onboarding complete") {
                    onToMain()
                }
            }
            else -> {}
        }
    }
    
    SetStatusBarAppearance(isDarkIcons = false)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .background(BrandingBlue),
    ){
        Image(
            painter = painterResource(id = R.drawable.onboarding_bg),
            contentDescription = "bg",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 1.04f
                    scaleY = 1.04f
                }
                .hazeSource(
                    state = hazeState,
                    zIndex = 1f
                )
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(46.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.koshpal),
                            contentDescription = "koshpal",
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Koshpal",
                    fontFamily = Jakarta,
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.weight(0.2f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(0.5.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .background(SecondaryColor.copy(alpha = 0.11f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedContent(
                    targetState = currentStep.intValue,
                    modifier = Modifier,
                    label = "StepSwapper"
                ) { step ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        when (step) {
                            1 -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 36.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "What would you most like to improve?",
                                        fontFamily = Jakarta,
                                        color = MaterialTheme.colorScheme.surface,
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    thickness = 1.dp
                                )
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    items(options, key = {option -> options.indexOf(option)}){option ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 32.dp),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = option,
                                                fontFamily = Outfit,
                                                color = MaterialTheme.colorScheme.surface,
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        HorizontalDivider(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                            2 -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 36.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "What is your biggest financial challenge right now?",
                                        fontFamily = Jakarta,
                                        color = MaterialTheme.colorScheme.surface,
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    thickness = 1.dp
                                )
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    items(options1, key = {option -> options.indexOf(option)}){option ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 32.dp),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = option,
                                                fontFamily = Outfit,
                                                color = MaterialTheme.colorScheme.surface,
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        HorizontalDivider(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                            3 -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 36.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "How do you currently keep track of your money?",
                                        fontFamily = Jakarta,
                                        color = MaterialTheme.colorScheme.surface,
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    thickness = 1.dp
                                )
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    items(options2, key = {option -> options.indexOf(option)}){option ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 32.dp),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = option,
                                                fontFamily = Outfit,
                                                color = MaterialTheme.colorScheme.surface,
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        HorizontalDivider(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                            4 -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 36.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "What would you like Koshpal to help you with?",
                                        fontFamily = Jakarta,
                                        color = MaterialTheme.colorScheme.surface,
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    thickness = 1.dp
                                )
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    items(options3, key = {option -> options.indexOf(option)}){option ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 32.dp),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = option,
                                                fontFamily = Outfit,
                                                color = MaterialTheme.colorScheme.surface,
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        HorizontalDivider(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                            else -> {}
                        }
                    }

                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.10f)
                    .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    onClick = { currentStep.intValue -= 1 },
                    shape = CircleShape,
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PREVIOUS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = { 
                        if (currentStep.intValue < 4) {
                            currentStep.intValue += 1 
                        } else {
                            viewModel.onBoarding()
                        }
                    },
                    shape = CircleShape,
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONTINUE",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
        }
    }
}
