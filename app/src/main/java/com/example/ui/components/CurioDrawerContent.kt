package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CurioGold
import com.example.ui.theme.CurioPrimary

enum class LegalDialogType {
    NONE,
    ABOUT_US,
    PRIVACY_POLICY,
    TERMS_AND_CONDITIONS
}

@Composable
fun CurioDrawerContent(
    userEmail: String?,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onPublishClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignOut: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeDialog by remember { mutableStateOf(LegalDialogType.NONE) }

    when (activeDialog) {
        LegalDialogType.ABOUT_US -> {
            AlertDialog(
                onDismissRequest = { activeDialog = LegalDialogType.NONE },
                title = {
                    Text(
                        text = "About Curio",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Curio is an independent daily research publication.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "People read Curio like the news—to discover deeply researched answers to fascinating questions from science and the human brain to sports controversies, global history, and technological breakthroughs.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Every story is broken down into clear, everyday English with verified facts, sources, and questions to explore next.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { activeDialog = LegalDialogType.NONE }) {
                        Text("Close", color = CurioPrimary)
                    }
                }
            )
        }
        LegalDialogType.PRIVACY_POLICY -> {
            AlertDialog(
                onDismissRequest = { activeDialog = LegalDialogType.NONE },
                title = {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Your Privacy is Respected",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "1. Open Reading: Anyone can read research posts on Curio without creating an account or sharing personal info.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "2. Author Authentication: Authors and publishers authenticate securely through Google Identity Services.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "3. Online Storage: Published research is securely hosted online with instant real-time updates for all readers.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "4. Zero Selling: We never sell or share reader information with advertising third parties.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { activeDialog = LegalDialogType.NONE }) {
                        Text("I Understand", color = CurioPrimary)
                    }
                }
            )
        }
        LegalDialogType.TERMS_AND_CONDITIONS -> {
            AlertDialog(
                onDismissRequest = { activeDialog = LegalDialogType.NONE },
                title = {
                    Text(
                        text = "Terms and Conditions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Curio Publication Terms",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "1. Content Integrity: Researches published on Curio aim to provide factual, verified insights with cited sources.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "2. Author Ownership: Authors retain ownership and copyright over their published research articles.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "3. Reader License: Readers are free to read, bookmark, and share links to Curio research stories.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { activeDialog = LegalDialogType.NONE }) {
                        Text("Agree", color = CurioPrimary)
                    }
                }
            )
        }
        LegalDialogType.NONE -> {}
    }

    ModalDrawerSheet(
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Header with Logo and Brand Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CurioPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = CurioPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Curio",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Research & News Publication",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CurioPrimary
                            )
                        )
                    }
                }

                // Author / Publisher Portal Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (userEmail != null) {
                            Text(
                                text = "PUBLISHER ACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            )
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Button(
                                onClick = {
                                    onCloseDrawer()
                                    onPublishClick()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CurioPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Publish New Research")
                            }
                        } else {
                            Text(
                                text = "READER MODE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = "Reading public publication",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = {
                                    onCloseDrawer()
                                    onSignInClick()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CurioPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Publisher Sign In")
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))

                // Theme Toggle Item (Light / Night)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (isDarkTheme) CurioGold else CurioPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = if (isDarkTheme) "Night Theme" else "Light Theme",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isDarkTheme) "Dark view active" else "White view active",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onToggleTheme,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CurioGold,
                            checkedTrackColor = CurioPrimary.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("theme_switch")
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // About Us Navigation Item
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CurioPrimary
                        )
                    },
                    label = { Text("About Curio Publication") },
                    selected = false,
                    onClick = {
                        activeDialog = LegalDialogType.ABOUT_US
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("drawer_item_about")
                )

                // Privacy Policy Navigation Item
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = null,
                            tint = CurioPrimary
                        )
                    },
                    label = { Text("Privacy Policy") },
                    selected = false,
                    onClick = {
                        activeDialog = LegalDialogType.PRIVACY_POLICY
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("drawer_item_privacy")
                )

                // Terms and Conditions Navigation Item
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = CurioPrimary
                        )
                    },
                    label = { Text("Terms and Conditions") },
                    selected = false,
                    onClick = {
                        activeDialog = LegalDialogType.TERMS_AND_CONDITIONS
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("drawer_item_terms")
                )
            }

            // Bottom Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (userEmail != null) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onCloseDrawer()
                                onSignOut()
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Sign Out Publisher",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Text(
                    text = "Curio • Live Research Publication",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}
