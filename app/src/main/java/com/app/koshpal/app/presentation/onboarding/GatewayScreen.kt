package com.app.koshpal.app.presentation.onboarding



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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.koshpal.R
import com.app.koshpal.ui.theme.BrandingBlue
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.SetStatusBarAppearance
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState



@Preview
@Composable
fun GatewayScreen(
    modifier: Modifier = Modifier,
    onToAuth: () -> Unit = {}
){

    val socials = mapOf(
        "insta" to R.drawable.insta_logo,
        "linkedin" to R.drawable.linkedin_logo
    )
    val hazeState = rememberHazeState()
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
                    zIndex = 0f
                )
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Koshpal",
                    fontFamily = Jakarta,
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    socials.forEach { social ->
                        Card(
                            modifier = Modifier.size(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = social.value),
                                    contentDescription = social.key,
                                )
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ){
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hazeSource(
                            state = hazeState,
                            zIndex = 1f
                        )
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 180.dp)
                        .align(Alignment.BottomCenter),
                    colors = CardDefaults.cardColors(
                        containerColor = BrandingBlue.copy(alpha = 0.65f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.surface)
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 86.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.70f)
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .background(Color(0xFF21366D))
                                .padding(vertical = 8.dp)
                            ,
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Earn it",
                                fontFamily = Jakarta,
                                color = MaterialTheme.colorScheme.surface,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.86f)
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Plan it",
                                fontFamily = Jakarta,
                                color = MaterialTheme.colorScheme.surface,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .background(Color(0xFF88AEEC))
                                .padding(vertical = 10.dp)
                            ,
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Track it",
                                fontFamily = Jakarta,
                                color = MaterialTheme.colorScheme.surface,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(46.dp))
                }
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            0.5.dp,
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(24.dp)
                        )
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .matchParentSize()
                            .hazeEffect(state = hazeState) {
                                blurEffect {
                                    blurRadius = 10.dp
                                    colorEffects =
                                        listOf(HazeColorEffect.tint(BrandingBlue.copy(alpha = 0.11f)))
                                    noiseFactor = 0.08f
                                }
                            }
                            .align(Alignment.TopCenter)
                    )
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
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
                            Spacer(modifier = Modifier.weight(1f))
                            Card(
                                modifier = Modifier
                                    .height(46.dp)
                                    .width(66.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = CircleShape,
                                onClick = { onToAuth() }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_forward_24px),
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "koshpal",
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(56.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Koshpal",
                                    fontFamily = Jakarta,
                                    color = MaterialTheme.colorScheme.surface,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Where Money Makes\nSense",
                                    fontFamily = Outfit,
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}