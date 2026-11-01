package com.ma.calculator.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.calculator.ui.theme.LocalCalculatorColors
import com.ma.calculator.ui.theme.PlusJakartaSans

enum class ButtonStyleType {
    NUMBER, FUNCTION, OPERATOR, SCIENTIFIC
}

@Composable
fun NeumorphicButton(
    text: String,
    styleType: ButtonStyleType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 70.dp,
    cornerRadius: Dp = 22.dp,
    fontSize: Int = 26,
    testTag: String = "btn_$text"
) {
    val colors = LocalCalculatorColors.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "btnScale"
    )

    val shape = RoundedCornerShape(cornerRadius)

    val (bgBrush, textColor, borderColor, shadowElevation) = when (styleType) {
        ButtonStyleType.OPERATOR -> {
            val brush = Brush.verticalGradient(
                listOf(colors.accentOrangeStart, colors.accentOrangeEnd)
            )
            ButtonVisualConfig(
                brush,
                Color.White,
                if (colors.isDark) Color(0x33FFFFFF) else Color(0x20000000),
                if (isPressed) 1.dp else 3.dp
            )
        }
        ButtonStyleType.SCIENTIFIC -> {
            val brush = Brush.verticalGradient(
                listOf(colors.surface, colors.numberButton)
            )
            ButtonVisualConfig(
                brush,
                colors.textPrimary,
                colors.numberButtonBorder,
                if (isPressed) 0.dp else 2.dp
            )
        }
        ButtonStyleType.FUNCTION -> {
            val brush = Brush.verticalGradient(
                listOf(colors.functionButton, colors.functionButton.copy(alpha = 0.92f))
            )
            ButtonVisualConfig(
                brush,
                colors.functionText,
                colors.functionButtonBorder,
                if (isPressed) 0.dp else 2.dp
            )
        }
        ButtonStyleType.NUMBER -> {
            val brush = Brush.verticalGradient(
                listOf(colors.numberButton, colors.numberButton.copy(alpha = 0.92f))
            )
            ButtonVisualConfig(
                brush,
                colors.numberText,
                colors.numberButtonBorder,
                if (isPressed) 0.dp else 2.dp
            )
        }
    }

    Box(
        modifier = modifier
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                ambientColor = colors.shadowDark,
                spotColor = colors.shadowDark
            )
            .clip(shape)
            .background(bgBrush)
            .border(
                width = if (colors.isDark) 1.dp else 1.2.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            )
            .semantics { contentDescription = "Calculator button $text" }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize.sp,
            fontFamily = PlusJakartaSans,
            fontWeight = if (styleType == ButtonStyleType.OPERATOR) FontWeight.Medium else FontWeight.Normal
        )
    }
}

private data class ButtonVisualConfig(
    val brush: Brush,
    val textColor: Color,
    val borderColor: Color,
    val elevation: Dp
)
