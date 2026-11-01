package com.ma.calculator.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.calculator.ui.theme.LocalCalculatorColors
import com.ma.calculator.ui.theme.PlusJakartaSans
import com.ma.calculator.viewmodel.CalculatorUiState

@Composable
fun CalculatorKeypad(
    uiState: CalculatorUiState,
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onOperator: (String) -> Unit,
    onEquals: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onToggleSign: () -> Unit,
    onPercentage: () -> Unit,
    onScientificFunction: (String) -> Unit,
    onToggleRadDeg: () -> Unit,
    onToggleScientific: () -> Unit,
    onToggleInverse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val buttonSpacing = 12.dp
    val buttonHeight = if (uiState.isScientificExpanded) 60.dp else 68.dp
    val scientificButtonHeight = 46.dp
    val cornerRadius = 22.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        // --- Scientific Toggle Header Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Badges: RAD/DEG and INV
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // RAD/DEG chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (uiState.isRadMode) colors.functionButton else colors.functionButton.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.functionButtonBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onToggleRadDeg() }
                        .testTag("chip_rad_deg")
                ) {
                    Text(
                        text = if (uiState.isRadMode) "RAD" else "DEG",
                        color = colors.functionText,
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // INV (Inverse) chip when scientific is open
                if (uiState.isScientificExpanded) {
                    val invBg by animateColorAsState(
                        targetValue = if (uiState.isInverseMode) colors.accentOrangeStart else colors.functionButton,
                        animationSpec = tween(200),
                        label = "invBg"
                    )
                    val invText = if (uiState.isInverseMode) Color.White else colors.functionText

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = invBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (uiState.isInverseMode) colors.accentOrangeEnd else colors.functionButtonBorder
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onToggleInverse() }
                            .testTag("chip_inv")
                    ) {
                        Text(
                            text = "2nd",
                            color = invText,
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Right Segmented Switch: [ Basic | Scientific fx ]
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = colors.functionButton,
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.functionButtonBorder),
                modifier = Modifier.testTag("toggle_scientific_bar")
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Basic Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .background(
                                if (!uiState.isScientificExpanded) colors.numberButton else Color.Transparent
                            )
                            .clickable {
                                if (uiState.isScientificExpanded) onToggleScientific()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = if (!uiState.isScientificExpanded) colors.accentOrangeStart else colors.textSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Basic",
                                color = if (!uiState.isScientificExpanded) colors.textPrimary else colors.textSecondary,
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaSans,
                                fontWeight = if (!uiState.isScientificExpanded) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }

                    // Scientific Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .background(
                                if (uiState.isScientificExpanded) colors.accentOrangeStart else Color.Transparent
                            )
                            .clickable {
                                if (!uiState.isScientificExpanded) onToggleScientific()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Functions,
                                contentDescription = null,
                                tint = if (uiState.isScientificExpanded) Color.White else colors.textSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Scientific",
                                color = if (uiState.isScientificExpanded) Color.White else colors.textSecondary,
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaSans,
                                fontWeight = if (uiState.isScientificExpanded) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // --- Expandable Scientific Functions Grid ---
        AnimatedVisibility(
            visible = uiState.isScientificExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                // Scientific Row 1: Trigonometry & Constants (sin, cos, tan, π, e)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sinLabel = if (uiState.isInverseMode) "sin⁻¹" else "sin"
                    val sinFn = if (uiState.isInverseMode) "asin" else "sin"
                    NeumorphicButton(
                        text = sinLabel,
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction(sinFn) },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 15,
                        testTag = "btn_sin"
                    )

                    val cosLabel = if (uiState.isInverseMode) "cos⁻¹" else "cos"
                    val cosFn = if (uiState.isInverseMode) "acos" else "cos"
                    NeumorphicButton(
                        text = cosLabel,
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction(cosFn) },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 15,
                        testTag = "btn_cos"
                    )

                    val tanLabel = if (uiState.isInverseMode) "tan⁻¹" else "tan"
                    val tanFn = if (uiState.isInverseMode) "atan" else "tan"
                    NeumorphicButton(
                        text = tanLabel,
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction(tanFn) },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 15,
                        testTag = "btn_tan"
                    )

                    NeumorphicButton(
                        text = "π",
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction("π") },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 16,
                        testTag = "btn_pi"
                    )

                    NeumorphicButton(
                        text = "e",
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction("e") },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 16,
                        testTag = "btn_e"
                    )
                }

                // Scientific Row 2: Logarithms & Powers (ln, log, xʸ, x², √)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val lnLabel = if (uiState.isInverseMode) "eˣ" else "ln"
                    val lnFn = if (uiState.isInverseMode) "e^x" else "ln"
                    NeumorphicButton(
                        text = lnLabel,
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction(lnFn) },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 15,
                        testTag = "btn_ln"
                    )

                    val logLabel = if (uiState.isInverseMode) "10ˣ" else "log"
                    val logFn = if (uiState.isInverseMode) "10^x" else "log"
                    NeumorphicButton(
                        text = logLabel,
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction(logFn) },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 15,
                        testTag = "btn_log"
                    )

                    NeumorphicButton(
                        text = "xʸ",
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction("^") },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 16,
                        testTag = "btn_power"
                    )

                    val sqrLabel = if (uiState.isInverseMode) "x³" else "x²"
                    val sqrFn = if (uiState.isInverseMode) "x³" else "x²"
                    NeumorphicButton(
                        text = sqrLabel,
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction(sqrFn) },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 15,
                        testTag = "btn_sqr"
                    )

                    val sqrtLabel = if (uiState.isInverseMode) "∛" else "√"
                    val sqrtFn = if (uiState.isInverseMode) "∛" else "√"
                    NeumorphicButton(
                        text = sqrtLabel,
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction(sqrtFn) },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 16,
                        testTag = "btn_sqrt"
                    )
                }

                // Scientific Row 3: Parentheses, Reciprocal, Factorial, Backspace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeumorphicButton(
                        text = "(",
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction("(") },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 16,
                        testTag = "btn_open_paren"
                    )

                    NeumorphicButton(
                        text = ")",
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction(")") },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 16,
                        testTag = "btn_close_paren"
                    )

                    NeumorphicButton(
                        text = "1/x",
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction("1/x") },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 14,
                        testTag = "btn_reciprocal"
                    )

                    NeumorphicButton(
                        text = "x!",
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = { onScientificFunction("!") },
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 15,
                        testTag = "btn_factorial"
                    )

                    NeumorphicButton(
                        text = "⌫",
                        styleType = ButtonStyleType.SCIENTIFIC,
                        onClick = onBackspace,
                        modifier = Modifier.weight(1f),
                        height = scientificButtonHeight,
                        cornerRadius = 14.dp,
                        fontSize = 16,
                        testTag = "btn_sci_backspace"
                    )
                }
            }
        }

        // --- Standard Primary Keypad Rows ---

        // Row 1: C, +/-, %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            val clearText = if (uiState.rawExpression.isNotEmpty() || uiState.displayResult != "0") "C" else "AC"
            NeumorphicButton(
                text = clearText,
                styleType = ButtonStyleType.FUNCTION,
                onClick = onClear,
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_clear"
            )
            NeumorphicButton(
                text = "+/-",
                styleType = ButtonStyleType.FUNCTION,
                onClick = onToggleSign,
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_plus_minus"
            )
            NeumorphicButton(
                text = "%",
                styleType = ButtonStyleType.FUNCTION,
                onClick = onPercentage,
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_percent"
            )
            NeumorphicButton(
                text = "÷",
                styleType = ButtonStyleType.OPERATOR,
                onClick = { onOperator("÷") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_divide"
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            NeumorphicButton(
                text = "7",
                styleType = ButtonStyleType.NUMBER,
                onClick = { onDigit("7") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_7"
            )
            NeumorphicButton(
                text = "8",
                styleType = ButtonStyleType.NUMBER,
                onClick = { onDigit("8") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_8"
            )
            NeumorphicButton(
                text = "9",
                styleType = ButtonStyleType.NUMBER,
                onClick = { onDigit("9") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_9"
            )
            NeumorphicButton(
                text = "×",
                styleType = ButtonStyleType.OPERATOR,
                onClick = { onOperator("×") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_multiply"
            )
        }

        // Row 3: 4, 5, 6, -
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            NeumorphicButton(
                text = "4",
                styleType = ButtonStyleType.NUMBER,
                onClick = { onDigit("4") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_4"
            )
            NeumorphicButton(
                text = "5",
                styleType = ButtonStyleType.NUMBER,
                onClick = { onDigit("5") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_5"
            )
            NeumorphicButton(
                text = "6",
                styleType = ButtonStyleType.NUMBER,
                onClick = { onDigit("6") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_6"
            )
            NeumorphicButton(
                text = "-",
                styleType = ButtonStyleType.OPERATOR,
                onClick = { onOperator("-") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_subtract"
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            NeumorphicButton(
                text = "1",
                styleType = ButtonStyleType.NUMBER,
                onClick = { onDigit("1") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_1"
            )
            NeumorphicButton(
                text = "2",
                styleType = ButtonStyleType.NUMBER,
                onClick = { onDigit("2") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_2"
            )
            NeumorphicButton(
                text = "3",
                styleType = ButtonStyleType.NUMBER,
                onClick = { onDigit("3") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_3"
            )
            NeumorphicButton(
                text = "+",
                styleType = ButtonStyleType.OPERATOR,
                onClick = { onOperator("+") },
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_add"
            )
        }

        // Row 5: 0 (Double-width pill spanning 2 columns), ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            NeumorphicButton(
                text = "0",
                styleType = ButtonStyleType.NUMBER,
                onClick = { onDigit("0") },
                modifier = Modifier.weight(2f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_0"
            )
            NeumorphicButton(
                text = ".",
                styleType = ButtonStyleType.NUMBER,
                onClick = onDecimal,
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_dot"
            )
            NeumorphicButton(
                text = "=",
                styleType = ButtonStyleType.OPERATOR,
                onClick = onEquals,
                modifier = Modifier.weight(1f),
                height = buttonHeight,
                cornerRadius = cornerRadius,
                testTag = "btn_equals"
            )
        }
    }
}
