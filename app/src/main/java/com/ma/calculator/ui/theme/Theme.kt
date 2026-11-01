package com.ma.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CalculatorCustomColors(
    val background: Color,
    val backgroundWarmGlow: Color,
    val surface: Color,
    val numberButton: Color,
    val numberButtonBorder: Color,
    val numberText: Color,
    val functionButton: Color,
    val functionButtonBorder: Color,
    val functionText: Color,
    val accentOrangeStart: Color,
    val accentOrangeEnd: Color,
    val accentGlow: Color,
    val accentText: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val shadowDark: Color,
    val shadowLight: Color,
    val isDark: Boolean
)

val LocalCalculatorColors = staticCompositionLocalOf {
    CalculatorCustomColors(
        background = DarkBackground,
        backgroundWarmGlow = DarkBackgroundWarmGlow,
        surface = DarkSurface,
        numberButton = DarkNumberButton,
        numberButtonBorder = DarkNumberButtonBorder,
        numberText = DarkTextPrimary,
        functionButton = DarkFunctionButton,
        functionButtonBorder = DarkFunctionButtonBorder,
        functionText = DarkTextFunction,
        accentOrangeStart = DarkAccentOrangeStart,
        accentOrangeEnd = DarkAccentOrangeEnd,
        accentGlow = DarkAccentGlow,
        accentText = Color.White,
        textPrimary = DarkTextPrimary,
        textSecondary = DarkTextSecondary,
        shadowDark = DarkShadowDark,
        shadowLight = DarkShadowLight,
        isDark = true
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccentOrangeStart,
    secondary = DarkFunctionButton,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = DarkTextFunction,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = LightAccentOrangeStart,
    secondary = LightFunctionButton,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = LightTextFunction,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val calculatorColors = if (darkTheme) {
        CalculatorCustomColors(
            background = DarkBackground,
            backgroundWarmGlow = DarkBackgroundWarmGlow,
            surface = DarkSurface,
            numberButton = DarkNumberButton,
            numberButtonBorder = DarkNumberButtonBorder,
            numberText = DarkTextPrimary,
            functionButton = DarkFunctionButton,
            functionButtonBorder = DarkFunctionButtonBorder,
            functionText = DarkTextFunction,
            accentOrangeStart = DarkAccentOrangeStart,
            accentOrangeEnd = DarkAccentOrangeEnd,
            accentGlow = DarkAccentGlow,
            accentText = Color.White,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            shadowDark = DarkShadowDark,
            shadowLight = DarkShadowLight,
            isDark = true
        )
    } else {
        CalculatorCustomColors(
            background = LightBackground,
            backgroundWarmGlow = LightBackgroundWarmGlow,
            surface = LightSurface,
            numberButton = LightNumberButton,
            numberButtonBorder = LightNumberButtonBorder,
            numberText = LightTextPrimary,
            functionButton = LightFunctionButton,
            functionButtonBorder = LightFunctionButtonBorder,
            functionText = LightTextFunction,
            accentOrangeStart = LightAccentOrangeStart,
            accentOrangeEnd = LightAccentOrangeEnd,
            accentGlow = LightAccentGlow,
            accentText = Color.White,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            shadowDark = LightShadowDark,
            shadowLight = LightShadowLight,
            isDark = false
        )
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalCalculatorColors provides calculatorColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
