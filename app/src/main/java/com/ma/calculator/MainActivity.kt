package com.ma.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ma.calculator.ui.components.AboutDialog
import com.ma.calculator.ui.components.CalculatorDisplay
import com.ma.calculator.ui.components.CalculatorKeypad
import com.ma.calculator.ui.components.ConverterView
import com.ma.calculator.ui.components.HistorySheet
import com.ma.calculator.ui.components.VoiceInputDialog
import com.ma.calculator.ui.theme.LocalCalculatorColors
import com.ma.calculator.ui.theme.MyApplicationTheme
import com.ma.calculator.viewmodel.CalculatorMode
import com.ma.calculator.viewmodel.CalculatorViewModel
import com.ma.calculator.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val historyList by viewModel.history.collectAsStateWithLifecycle()
            var isAboutOpen by remember { mutableStateOf(false) }

            val systemInDark = isSystemInDarkTheme()
            val isDark = when (uiState.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> systemInDark
            }

            MyApplicationTheme(darkTheme = isDark) {
                val colors = LocalCalculatorColors.current

                // Handle back press
                BackHandler(enabled = isAboutOpen || uiState.currentMode == CalculatorMode.CONVERTER || uiState.isScientificExpanded) {
                    if (isAboutOpen) {
                        isAboutOpen = false
                    } else if (uiState.currentMode == CalculatorMode.CONVERTER) {
                        viewModel.setMode(CalculatorMode.STANDARD)
                    } else if (uiState.isScientificExpanded) {
                        viewModel.toggleScientific()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = colors.background,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                            val isLandscape = maxWidth > maxHeight

                            AnimatedContent(
                                targetState = uiState.currentMode,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "modeTransition"
                            ) { mode ->
                                when (mode) {
                                    CalculatorMode.STANDARD -> {
                                        if (isLandscape) {
                                            // Tablet / Landscape side-by-side
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(16.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CalculatorDisplay(
                                                        uiState = uiState,
                                                        onMicClick = { viewModel.setVoiceInputOpen(true) },
                                                        onThemeToggle = { viewModel.toggleTheme() },
                                                        onHistoryClick = { viewModel.setHistoryOpen(true) },
                                                        onScientificToggle = { viewModel.toggleScientific() },
                                                        onModeToggle = { viewModel.setMode(CalculatorMode.CONVERTER) },
                                                        onBackspace = { viewModel.onBackspace() },
                                                        onAboutClick = { isAboutOpen = true }
                                                    )
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .weight(1.2f)
                                                        .fillMaxHeight(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CalculatorKeypad(
                                                        uiState = uiState,
                                                        onDigit = { viewModel.onDigit(it) },
                                                        onDecimal = { viewModel.onDecimal() },
                                                        onOperator = { viewModel.onOperator(it) },
                                                        onEquals = { viewModel.onEquals() },
                                                        onClear = { viewModel.onClear() },
                                                        onBackspace = { viewModel.onBackspace() },
                                                        onToggleSign = { viewModel.onToggleSign() },
                                                        onPercentage = { viewModel.onPercentage() },
                                                        onScientificFunction = { viewModel.onScientificFunction(it) },
                                                        onToggleRadDeg = { viewModel.toggleRadDeg() },
                                                        onToggleScientific = { viewModel.toggleScientific() },
                                                        onToggleInverse = { viewModel.toggleInverse() }
                                                    )
                                                }
                                            }
                                        } else {
                                            // Portrait layout matching clean, simple, attractive design
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .widthIn(max = 540.dp)
                                                    .align(Alignment.Center),
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                CalculatorDisplay(
                                                    uiState = uiState,
                                                    onMicClick = { viewModel.setVoiceInputOpen(true) },
                                                    onThemeToggle = { viewModel.toggleTheme() },
                                                    onHistoryClick = { viewModel.setHistoryOpen(true) },
                                                    onScientificToggle = { viewModel.toggleScientific() },
                                                    onModeToggle = { viewModel.setMode(CalculatorMode.CONVERTER) },
                                                    onBackspace = { viewModel.onBackspace() },
                                                    onAboutClick = { isAboutOpen = true },
                                                    modifier = Modifier.weight(1f)
                                                )

                                                CalculatorKeypad(
                                                    uiState = uiState,
                                                    onDigit = { viewModel.onDigit(it) },
                                                    onDecimal = { viewModel.onDecimal() },
                                                    onOperator = { viewModel.onOperator(it) },
                                                    onEquals = { viewModel.onEquals() },
                                                    onClear = { viewModel.onClear() },
                                                    onBackspace = { viewModel.onBackspace() },
                                                    onToggleSign = { viewModel.onToggleSign() },
                                                    onPercentage = { viewModel.onPercentage() },
                                                    onScientificFunction = { viewModel.onScientificFunction(it) },
                                                    onToggleRadDeg = { viewModel.toggleRadDeg() },
                                                    onToggleScientific = { viewModel.toggleScientific() },
                                                    onToggleInverse = { viewModel.toggleInverse() },
                                                    modifier = Modifier.padding(bottom = 12.dp)
                                                )
                                            }
                                        }
                                    }
                                    CalculatorMode.CONVERTER -> {
                                        ConverterView(
                                            uiState = uiState,
                                            onBackToCalculator = { viewModel.setMode(CalculatorMode.STANDARD) },
                                            onCategorySelected = { viewModel.selectConverterCategory(it) },
                                            onFromUnitSelected = { viewModel.selectFromUnit(it) },
                                            onToUnitSelected = { viewModel.selectToUnit(it) },
                                            onSwapUnits = { viewModel.swapConverterUnits() },
                                            onInputChange = { viewModel.updateConverterInput(it) }
                                        )
                                    }
                                }
                            }
                        }

                        // History Bottom Sheet
                        HistorySheet(
                            historyList = historyList,
                            isOpen = uiState.isHistoryOpen,
                            onDismiss = { viewModel.setHistoryOpen(false) },
                            onSelectHistoryItem = { viewModel.reuseHistoryItem(it) },
                            onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
                            onClearAll = { viewModel.clearAllHistory() }
                        )

                        // Voice Input Dialog
                        VoiceInputDialog(
                            isOpen = uiState.isVoiceInputOpen,
                            onDismiss = { viewModel.setVoiceInputOpen(false) },
                            onApplySpokenMath = { viewModel.applyVoiceSpeech(it) }
                        )

                        // About & Privacy Dialog
                        AboutDialog(
                            isOpen = isAboutOpen,
                            onDismiss = { isAboutOpen = false }
                        )
                    }
                }
            }
        }
    }
}
