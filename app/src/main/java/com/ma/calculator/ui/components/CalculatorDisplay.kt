package com.ma.calculator.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.calculator.ui.theme.LocalCalculatorColors
import com.ma.calculator.ui.theme.PlusJakartaSans
import com.ma.calculator.viewmodel.CalculatorMode
import com.ma.calculator.viewmodel.CalculatorUiState
import com.ma.calculator.viewmodel.ThemeMode

@Composable
fun CalculatorDisplay(
    uiState: CalculatorUiState,
    onMicClick: () -> Unit,
    onThemeToggle: () -> Unit,
    onHistoryClick: () -> Unit,
    onScientificToggle: () -> Unit,
    onModeToggle: () -> Unit,
    onBackspace: () -> Unit,
    onAboutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    var copiedFeedbackVisible by remember { mutableStateOf(false) }

    // Auto dismiss copy feedback
    LaunchedEffect(copiedFeedbackVisible) {
        if (copiedFeedbackVisible) {
            kotlinx.coroutines.delay(1600)
            copiedFeedbackVisible = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectHorizontalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                    },
                    onDragEnd = {
                        // Swipe left to delete character
                        if (totalDrag < -60f) {
                            onBackspace()
                        }
                    }
                )
            }
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Microphone Button (in pill/squircle matching screenshot)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = if (colors.isDark) 4.dp else 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = colors.shadowDark,
                        spotColor = colors.shadowDark
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.functionButton)
                    .border(1.dp, colors.functionButtonBorder, RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onMicClick
                    )
                    .testTag("btn_mic"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = colors.functionText,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Right Quick Actions (Theme, Mode, History, Scientific)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Scientific Mode Toggle
                IconButton(
                    onClick = onScientificToggle,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (uiState.isScientificExpanded) colors.accentOrangeStart.copy(alpha = 0.2f) else Color.Transparent)
                        .testTag("btn_scientific_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Functions,
                        contentDescription = "Scientific Functions",
                        tint = if (uiState.isScientificExpanded) colors.accentOrangeStart else colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Converter / Standard Mode Switcher
                IconButton(
                    onClick = onModeToggle,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_mode_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Converter Mode",
                        tint = if (uiState.currentMode == CalculatorMode.CONVERTER) colors.accentOrangeStart else colors.textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // History Button
                IconButton(
                    onClick = onHistoryClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_history")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Calculation History",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Theme Toggle Button
                IconButton(
                    onClick = onThemeToggle,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_theme_toggle")
                ) {
                    Icon(
                        imageVector = if (colors.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Dark/Light Mode",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Info / About Button
                IconButton(
                    onClick = onAboutClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_about")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About Calculator",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Copied Feedback Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = copiedFeedbackVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.functionButton,
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.functionButtonBorder),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "Result copied to clipboard",
                        color = colors.functionText,
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Expression Line (matching "4,900 + 15,910" from screenshot)
        val displayExpression = if (uiState.formattedExpression.isNotEmpty()) {
            uiState.formattedExpression
        } else {
            " "
        }

        Text(
            text = displayExpression,
            color = colors.textSecondary,
            fontSize = 24.sp,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("txt_expression")
        )

        // Main Result Line (matching "20,810" large crisp typography)
        val resultFontSize = when {
            uiState.displayResult.length > 13 -> 34.sp
            uiState.displayResult.length > 9 -> 44.sp
            else -> 56.sp
        }

        Text(
            text = uiState.displayResult,
            color = colors.textPrimary,
            fontSize = resultFontSize,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable {
                    // Copy result to clipboard
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Calculator Result", uiState.displayResult)
                    clipboard.setPrimaryClip(clip)
                    copiedFeedbackVisible = true
                }
                .testTag("txt_result")
        )
    }
}
