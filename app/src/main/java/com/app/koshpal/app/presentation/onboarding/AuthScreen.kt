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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.koshpal.R
import com.app.koshpal.ui.theme.BrandingBlue
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import com.app.koshpal.app.Events
import com.app.koshpal.app.viewmodels.authviewmodel.AuthViewModel
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.presentation.util.ObserveAsEvents
import com.app.koshpal.core.presentation.util.toString
import com.app.koshpal.ui.theme.SetStatusBarAppearance


@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel,
    onToOnBoard: () -> Unit = {},
    onToMain: () -> Unit = {}
){
    val hazeState = rememberHazeState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).components { add(GifDecoder.Factory()) }.build()
    var isPasswordVisible by remember { mutableStateOf(false) }
    val passwordFocusRequester = remember { FocusRequester() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is Events.Success -> {
                if (event.message == "guest_login") {
                    onToMain()
                } else {
                    onToOnBoard()
                }
            }
            is Events.Error -> {
                val message = if (event.error is NetworkError) {
                    event.error.toString(context)
                } else {
                    event.message ?: "An unknown error occurred"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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
            modifier = modifier
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
            modifier = Modifier
                .fillMaxSize(),
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Where Money Makes\nSense",
                    fontFamily = Outfit,
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.weight(0.74f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Login",
                            fontFamily = Jakarta,
                            color = MaterialTheme.colorScheme.surface,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Card(
                            modifier = Modifier
                                .height(46.dp)
                                .width(66.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = CircleShape,
                            onClick = {
                                viewModel.login()
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if(isLoading){
                                    AsyncImage(
                                        modifier = Modifier.size(36.dp),
                                        model = ImageRequest.Builder(context)
                                            .data(R.drawable.loading_indicator)
                                            .build(),
                                        imageLoader = imageLoader,
                                        contentDescription = "Loading",
                                    )
                                }else{
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_forward_24px),
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "koshpal",
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Email",
                                fontFamily = Outfit,
                                color = MaterialTheme.colorScheme.surface,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))
                                    .border(
                                        BorderStroke(
                                            0.5.dp,
                                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f)
                                        ), RoundedCornerShape(16.dp)
                                    ),
                            ) {
                                TextField(
                                    value = email,
                                    onValueChange = viewModel::onEmailChange,
                                    modifier = Modifier.fillMaxSize(),
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 18.sp,
                                        fontFamily = Outfit
                                    ),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    placeholder = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "",
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontFamily = Outfit,
                                                fontSize = 18.sp
                                            )
                                        }
                                    },
                                    trailingIcon = {
                                        if (email.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.onEmailChange("") }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.close_24px),
                                                    contentDescription = "Clear",
                                                    tint = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { passwordFocusRequester.requestFocus() }
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    )
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Password",
                                fontFamily = Outfit,
                                color = MaterialTheme.colorScheme.surface,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))
                                    .border(
                                        BorderStroke(
                                            0.5.dp,
                                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f)
                                        ), RoundedCornerShape(16.dp)
                                    ),
                            ) {
                                TextField(
                                    value = password,
                                    onValueChange = viewModel::onPasswordChange,
                                    modifier = Modifier.fillMaxSize().focusRequester(passwordFocusRequester),
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 18.sp,
                                        fontFamily = Outfit
                                    ),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    placeholder = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "",
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontFamily = Outfit,
                                                fontSize = 18.sp
                                            )
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { viewModel.login() }
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    ),
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                            Icon(
                                                painter = painterResource(id = if (isPasswordVisible) R.drawable.visibility_24px else R.drawable.visibility_off_24px),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
