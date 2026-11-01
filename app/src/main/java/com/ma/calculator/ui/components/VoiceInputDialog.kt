package com.ma.calculator.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ma.calculator.ui.theme.LocalCalculatorColors
import com.ma.calculator.ui.theme.PlusJakartaSans
import java.util.Locale

@Composable
fun VoiceInputDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onApplySpokenMath: (String) -> Unit
) {
    if (!isOpen) return

    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    var customSpokenInput by remember { mutableStateOf("") }

    // Speech recognizer launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                onApplySpokenMath(spoken)
            }
        }
    }

    // Pulsing animation for the microphone
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.functionButtonBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Voice Calculator",
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

                Spacer(modifier = Modifier.height(24.dp))

                // Pulsing Mic Button
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(pulseScale)
                        .shadow(12.dp, CircleShape, ambientColor = colors.accentOrangeStart, spotColor = colors.accentOrangeStart)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(colors.accentOrangeStart, colors.accentOrangeEnd)
                            )
                        )
                        .clickable {
                            try {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak a math equation (e.g., 4,900 plus 15,910)...")
                                }
                                speechLauncher.launch(intent)
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(
                                    context,
                                    "Speech recognition not available on this device. Please type or use quick examples below.",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        .testTag("btn_voice_listen"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Tap to speak",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Tap mic to speak or choose a quick prompt",
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    fontFamily = PlusJakartaSans,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Spoken Math Presets
                Text(
                    text = "Quick math speech examples:",
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val samplePhrases = listOf(
                    "4900 + 15910",
                    "30820 + 9205",
                    "20 percent of 800",
                    "1500 divided by 3",
                    "square root of 144",
                    "45 times 12"
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    samplePhrases.chunked(2).forEach { rowPhrases ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowPhrases.forEach { phrase ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = colors.functionButton,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.functionButtonBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            onApplySpokenMath(phrase)
                                        }
                                ) {
                                    Text(
                                        text = "\"$phrase\"",
                                        color = colors.functionText,
                                        fontSize = 12.sp,
                                        fontFamily = PlusJakartaSans,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Manual Input Field fallback
                OutlinedTextField(
                    value = customSpokenInput,
                    onValueChange = { customSpokenInput = it },
                    placeholder = {
                        Text(
                            text = "e.g. 500 times 12",
                            color = colors.textSecondary.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaSans
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_speech_text"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accentOrangeStart,
                        unfocusedBorderColor = colors.numberButtonBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (customSpokenInput.isNotBlank()) {
                            onApplySpokenMath(customSpokenInput)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_apply_speech"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accentOrangeStart,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Calculate",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
