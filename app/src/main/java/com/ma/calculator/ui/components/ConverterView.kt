package com.ma.calculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.calculator.ui.theme.LocalCalculatorColors
import com.ma.calculator.ui.theme.PlusJakartaSans
import com.ma.calculator.util.ConversionUnit
import com.ma.calculator.util.ConverterCategory
import com.ma.calculator.util.UnitConverter
import com.ma.calculator.viewmodel.CalculatorUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterView(
    uiState: CalculatorUiState,
    onBackToCalculator: () -> Unit,
    onCategorySelected: (ConverterCategory) -> Unit,
    onFromUnitSelected: (ConversionUnit) -> Unit,
    onToUnitSelected: (ConversionUnit) -> Unit,
    onSwapUnits: () -> Unit,
    onInputChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val currentUnits = UnitConverter.unitsMap[uiState.converterCategory] ?: emptyList()

    var showFromMenu by remember { mutableStateOf(false) }
    var showToMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToCalculator,
                    modifier = Modifier.testTag("btn_converter_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Calculator",
                        tint = colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Unit Converter",
                    color = colors.textPrimary,
                    fontSize = 22.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Categories Horizontal Pills
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ConverterCategory.values()) { category ->
                val isSelected = category == uiState.converterCategory
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) colors.accentOrangeStart else colors.functionButton,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) colors.accentOrangeEnd else colors.functionButtonBorder
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onCategorySelected(category) }
                        .testTag("cat_${category.name}")
                ) {
                    Text(
                        text = category.title,
                        color = if (isSelected) Color.White else colors.functionText,
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // From Unit Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colors.numberButton,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.numberButtonBorder),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = colors.shadowDark, spotColor = colors.shadowDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "From",
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaSans
                    )

                    // Unit dropdown selector
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.functionButton,
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.functionButtonBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showFromMenu = true }
                        ) {
                            Text(
                                text = "${uiState.converterFromUnit.name} (${uiState.converterFromUnit.symbol})",
                                color = colors.functionText,
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaSans,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showFromMenu,
                            onDismissRequest = { showFromMenu = false },
                            modifier = Modifier.background(colors.surface)
                        ) {
                            currentUnits.forEach { unit ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${unit.name} (${unit.symbol})",
                                            color = colors.textPrimary,
                                            fontFamily = PlusJakartaSans
                                        )
                                    },
                                    onClick = {
                                        onFromUnitSelected(unit)
                                        showFromMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.converterInput,
                    onValueChange = onInputChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = LocalTextStyle.current.copy(
                        color = colors.textPrimary,
                        fontSize = 32.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Start
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }

        // Swap Button in between
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onSwapUnits,
                modifier = Modifier
                    .size(48.dp)
                    .shadow(6.dp, CircleShape, ambientColor = colors.accentOrangeStart, spotColor = colors.accentOrangeStart)
                    .clip(CircleShape)
                    .background(colors.accentOrangeStart)
                    .testTag("btn_swap_units")
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap Units",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // To Unit Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colors.numberButton,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.numberButtonBorder),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = colors.shadowDark, spotColor = colors.shadowDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "To",
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaSans
                    )

                    // Unit dropdown selector
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.functionButton,
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.functionButtonBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showToMenu = true }
                        ) {
                            Text(
                                text = "${uiState.converterToUnit.name} (${uiState.converterToUnit.symbol})",
                                color = colors.functionText,
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaSans,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showToMenu,
                            onDismissRequest = { showToMenu = false },
                            modifier = Modifier.background(colors.surface)
                        ) {
                            currentUnits.forEach { unit ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${unit.name} (${unit.symbol})",
                                            color = colors.textPrimary,
                                            fontFamily = PlusJakartaSans
                                        )
                                    },
                                    onClick = {
                                        onToUnitSelected(unit)
                                        showToMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${uiState.converterOutput} ${uiState.converterToUnit.symbol}",
                    color = colors.accentOrangeStart,
                    fontSize = 32.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
