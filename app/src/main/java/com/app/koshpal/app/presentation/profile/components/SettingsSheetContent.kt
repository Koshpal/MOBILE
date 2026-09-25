package com.app.koshpal.app.presentation.profile.components

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.koshpal.R
import com.app.koshpal.app.presentation.globalcomponents.FilterToggleCard
import com.app.koshpal.app.presentation.profile.LegalDocumentScreen
import com.app.koshpal.app.viewmodels.profileviewmodel.ProfileViewModel
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit

@Composable
fun SettingsSheetContent(
    viewModel: ProfileViewModel,
    onClose: () -> Unit,
    onToLegalDocument: (String) -> Unit = {}
) {
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle(initialValue = false)
    val context = LocalContext.current
    var activeDialogType by remember { mutableStateOf<String?>(null) }

    val openPlayStore = {
        val packageName = context.packageName
        val playStoreIntent = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        }
        try {
            context.startActivity(playStoreIntent)
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri())
            try {
                context.startActivity(webIntent)
            } catch (_: Exception) {}
        }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = Jakarta,
                textAlign = TextAlign.Center
            )
            Card(
                modifier = Modifier.size(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                onClick = { onClose() }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(R.drawable.close_24px),
                        contentDescription = null
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        FilterToggleCard(
            label = "Unlock using biometric",
            icon = R.drawable.fingerprint_24px, 
            checked = isBiometricEnabled,
            onCheckedChange = { viewModel.toggleBiometric(it) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Help & support", 
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            fontFamily = Outfit
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
            onClick = { activeDialogType = "support" }
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.contact_support_24px),
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Support", 
                    modifier = Modifier.weight(1f), 
                    fontFamily = Jakarta,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    painter = painterResource(R.drawable.arrow_forward_ios_24px), 
                    contentDescription = null, 
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Account settings", 
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            fontFamily = Outfit
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.surface),
            onClick = { viewModel.logout() }
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Log out", 
                    style = MaterialTheme.typography.bodyLarge, 
                    fontWeight = FontWeight.Bold,
                    fontFamily = Jakarta,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.surface
                )
                Icon(
                    painter = painterResource(id = R.drawable.exit_to_app_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
            onClick = { openPlayStore() }
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.favorite_24px), 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Rate us on Play Store", 
                    style = MaterialTheme.typography.bodyLarge, 
                    fontWeight = FontWeight.Medium, 
                    fontFamily = Jakarta,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
            onClick = { openPlayStore() }
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_24px), 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "How are we doing?", 
                    style = MaterialTheme.typography.bodyLarge, 
                    fontWeight = FontWeight.Medium, 
                    fontFamily = Jakarta,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        if (activeDialogType != null) {
            when (activeDialogType) {
                "support" -> {
                    AlertDialog(
                        onDismissRequest = { activeDialogType = null },
                        title = {
                            Text(
                                text = "Customer Support",
                                fontFamily = Jakarta,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        text = {
                            Text(
                                text = "Need help or have questions regarding your account or financial logs?\n\nContact Koshpal's official support team at support@koshpal.com. Our team typically responds within 24 hours.",
                                fontFamily = Outfit,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    activeDialogType = null
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = "mailto:support@koshpal.com".toUri()
                                        putExtra(Intent.EXTRA_SUBJECT, "Koshpal Support Request")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                }
                            ) {
                                Text("Send Email", fontFamily = Jakarta, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { activeDialogType = null }) {
                                Text("Close", fontFamily = Jakarta)
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }

                "terms", "privacy", "compliance" -> {
                    LegalDocumentScreen(
                        document = activeDialogType!!,
                        onNavigateBack = { activeDialogType = null }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Terms of Services",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Outfit,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onToLegalDocument("terms") }
            )
            Text(
                text = "  •  ",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Outfit,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Outfit,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onToLegalDocument("privacy") }
            )
            Text(
                text = "  •  ",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Outfit,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Data Compliance",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Outfit,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onToLegalDocument("compliance") }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
