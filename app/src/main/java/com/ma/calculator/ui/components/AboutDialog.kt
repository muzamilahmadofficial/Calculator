package com.ma.calculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ma.calculator.BuildConfig
import com.ma.calculator.ui.theme.LocalCalculatorColors
import com.ma.calculator.ui.theme.PlusJakartaSans

@Composable
fun AboutDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val colors = LocalCalculatorColors.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.functionButtonBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "About",
                        color = colors.textPrimary,
                        fontSize = 20.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // App Icon badge
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.accentOrangeStart),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "MA Calculator",
                    color = colors.textPrimary,
                    fontSize = 22.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    fontFamily = PlusJakartaSans
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Privacy Commitment Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.functionButton.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.functionButtonBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = colors.accentOrangeStart,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "100% Private & Offline",
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "All calculations and history remain completely private on your device. No personal data is collected, stored, or transmitted.",
                            color = colors.textSecondary,
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaSans,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Feature Highlights
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "• Standard & Scientific functions (Trig, Log, Powers)",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans
                    )
                    Text(
                        text = "• Unit Converter (Length, Weight, Temp, Speed, Volume)",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans
                    )
                    Text(
                        text = "• Calculation History with local SQLite persistence",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans
                    )
                    Text(
                        text = "• Pure Dark & Light mode theme support",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_close_about"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accentOrangeStart,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Done",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
