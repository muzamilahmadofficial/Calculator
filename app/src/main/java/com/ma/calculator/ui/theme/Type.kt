package com.ma.calculator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ma.calculator.R

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Light,
        fontSize = 58.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 42.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 22.sp
    )
)
