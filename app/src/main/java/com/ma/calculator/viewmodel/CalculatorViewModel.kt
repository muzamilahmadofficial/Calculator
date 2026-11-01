package com.ma.calculator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ma.calculator.data.local.AppDatabase
import com.ma.calculator.data.local.CalculationEntity
import com.ma.calculator.util.ConverterCategory
import com.ma.calculator.util.ConversionUnit
import com.ma.calculator.util.EvaluationResult
import com.ma.calculator.util.MathEvaluator
import com.ma.calculator.util.UnitConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ThemeMode {
    DARK, LIGHT, SYSTEM
}

enum class CalculatorMode {
    STANDARD, CONVERTER
}

data class CalculatorUiState(
    val rawExpression: String = "",
    val formattedExpression: String = "",
    val displayResult: String = "0",
    val isEvaluated: Boolean = false,
    val isScientificExpanded: Boolean = false,
    val isInverseMode: Boolean = false,
    val isRadMode: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val currentMode: CalculatorMode = CalculatorMode.STANDARD,
    val isHistoryOpen: Boolean = false,
    val isVoiceInputOpen: Boolean = false,
    // Converter state
    val converterCategory: ConverterCategory = ConverterCategory.LENGTH,
    val converterFromUnit: ConversionUnit = UnitConverter.unitsMap[ConverterCategory.LENGTH]!![0],
    val converterToUnit: ConversionUnit = UnitConverter.unitsMap[ConverterCategory.LENGTH]!![1],
    val converterInput: String = "1",
    val converterOutput: String = "0.001"
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.calculationDao()

    val history = dao.getAllHistory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onDigit(digit: String) {
        _uiState.update { current ->
            val newExpr = if (current.isEvaluated) {
                digit
            } else {
                if (current.rawExpression == "0" && digit != ".") digit else current.rawExpression + digit
            }

            val formatted = MathEvaluator.formatExpression(newExpr)
            val preview = calculatePreview(newExpr, current.isRadMode)

            current.copy(
                rawExpression = newExpr,
                formattedExpression = formatted,
                displayResult = preview,
                isEvaluated = false
            )
        }
    }

    fun onDecimal() {
        _uiState.update { current ->
            if (current.isEvaluated) {
                current.copy(
                    rawExpression = "0.",
                    formattedExpression = "0.",
                    displayResult = "0",
                    isEvaluated = false
                )
            } else {
                // Find last number token
                val lastNum = current.rawExpression.takeLastWhile { it.isDigit() || it == '.' }
                if (!lastNum.contains(".")) {
                    val newExpr = if (lastNum.isEmpty()) current.rawExpression + "0." else current.rawExpression + "."
                    val formatted = MathEvaluator.formatExpression(newExpr)
                    current.copy(
                        rawExpression = newExpr,
                        formattedExpression = formatted,
                        isEvaluated = false
                    )
                } else {
                    current
                }
            }
        }
    }

    fun onOperator(op: String) {
        _uiState.update { current ->
            var baseExpr = if (current.isEvaluated) {
                current.displayResult.replace(",", "")
            } else {
                current.rawExpression
            }

            if (baseExpr.isEmpty()) {
                if (op == "-") baseExpr = "-" else return@update current
            } else {
                // If last char is operator, replace it
                val lastChar = baseExpr.last()
                if (lastChar in "+-×÷^") {
                    baseExpr = baseExpr.dropLast(1)
                }
                baseExpr += op
            }

            val formatted = MathEvaluator.formatExpression(baseExpr)
            val preview = calculatePreview(baseExpr, current.isRadMode)

            current.copy(
                rawExpression = baseExpr,
                formattedExpression = formatted,
                displayResult = if (preview.isNotEmpty() && preview != "0") preview else current.displayResult,
                isEvaluated = false
            )
        }
    }

    fun onEquals() {
        val current = _uiState.value
        if (current.rawExpression.isBlank()) return

        when (val eval = MathEvaluator.evaluate(current.rawExpression, current.isRadMode)) {
            is EvaluationResult.Success -> {
                val finalFormatted = eval.formattedText
                val expressionToSave = current.formattedExpression.ifEmpty { current.rawExpression }

                // Save to Room history
                viewModelScope.launch {
                    dao.insertCalculation(
                        CalculationEntity(
                            expression = expressionToSave,
                            result = finalFormatted
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        formattedExpression = expressionToSave,
                        displayResult = finalFormatted,
                        rawExpression = finalFormatted.replace(",", ""),
                        isEvaluated = true
                    )
                }
            }
            is EvaluationResult.Error -> {
                _uiState.update {
                    it.copy(
                        displayResult = eval.message,
                        isEvaluated = true
                    )
                }
            }
        }
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                rawExpression = "",
                formattedExpression = "",
                displayResult = "0",
                isEvaluated = false
            )
        }
    }

    fun onBackspace() {
        _uiState.update { current ->
            if (current.isEvaluated) {
                current.copy(
                    rawExpression = "",
                    formattedExpression = "",
                    displayResult = "0",
                    isEvaluated = false
                )
            } else if (current.rawExpression.isNotEmpty()) {
                val newExpr = current.rawExpression.dropLast(1)
                val formatted = MathEvaluator.formatExpression(newExpr)
                val preview = if (newExpr.isEmpty()) "0" else calculatePreview(newExpr, current.isRadMode)
                current.copy(
                    rawExpression = newExpr,
                    formattedExpression = formatted,
                    displayResult = preview
                )
            } else {
                current
            }
        }
    }

    fun onToggleSign() {
        _uiState.update { current ->
            if (current.rawExpression.isEmpty()) {
                current.copy(rawExpression = "-", formattedExpression = "-", isEvaluated = false)
            } else if (current.isEvaluated) {
                val num = current.displayResult.replace(",", "").toDoubleOrNull() ?: 0.0
                val toggled = -num
                val formatted = MathEvaluator.formatNumber(toggled)
                current.copy(
                    rawExpression = toggled.toString(),
                    formattedExpression = formatted,
                    displayResult = formatted,
                    isEvaluated = true
                )
            } else {
                // Wrap or toggle the last number
                val regex = Regex("(-?\\d+\\.?\\d*)$")
                val match = regex.find(current.rawExpression)
                if (match != null) {
                    val numStr = match.value
                    val replacement = if (numStr.startsWith("-")) numStr.drop(1) else "-$numStr"
                    val newExpr = current.rawExpression.substring(0, match.range.first) + replacement
                    val formatted = MathEvaluator.formatExpression(newExpr)
                    val preview = calculatePreview(newExpr, current.isRadMode)
                    current.copy(
                        rawExpression = newExpr,
                        formattedExpression = formatted,
                        displayResult = preview
                    )
                } else {
                    current
                }
            }
        }
    }

    fun onPercentage() {
        onOperator("%")
    }

    fun onScientificFunction(fn: String) {
        _uiState.update { current ->
            val newExpr = when (fn) {
                "π" -> current.rawExpression + "π"
                "e" -> current.rawExpression + "e"
                "√" -> current.rawExpression + "√("
                "∛" -> current.rawExpression + "∛("
                "sin" -> current.rawExpression + "sin("
                "cos" -> current.rawExpression + "cos("
                "tan" -> current.rawExpression + "tan("
                "asin" -> current.rawExpression + "asin("
                "acos" -> current.rawExpression + "acos("
                "atan" -> current.rawExpression + "atan("
                "log" -> current.rawExpression + "log("
                "ln" -> current.rawExpression + "ln("
                "10^x" -> current.rawExpression + "10^"
                "e^x" -> current.rawExpression + "e^"
                "x²" -> current.rawExpression + "^2"
                "x³" -> current.rawExpression + "^3"
                "1/x" -> if (current.rawExpression.isNotEmpty()) "1/(" + current.rawExpression + ")" else "1/"
                "!" -> current.rawExpression + "!"
                "^" -> current.rawExpression + "^"
                "(" -> current.rawExpression + "("
                ")" -> current.rawExpression + ")"
                else -> current.rawExpression
            }
            val formatted = MathEvaluator.formatExpression(newExpr)
            val preview = calculatePreview(newExpr, current.isRadMode)
            current.copy(
                rawExpression = newExpr,
                formattedExpression = formatted,
                displayResult = preview,
                isEvaluated = false
            )
        }
    }

    fun toggleScientific() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun setScientificExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isScientificExpanded = expanded) }
    }

    fun toggleInverse() {
        _uiState.update { it.copy(isInverseMode = !it.isInverseMode) }
    }

    fun toggleRadDeg() {
        _uiState.update {
            val newRad = !it.isRadMode
            val preview = calculatePreview(it.rawExpression, newRad)
            it.copy(isRadMode = newRad, displayResult = preview)
        }
    }

    fun toggleTheme() {
        _uiState.update {
            val nextTheme = when (it.themeMode) {
                ThemeMode.DARK -> ThemeMode.LIGHT
                ThemeMode.LIGHT -> ThemeMode.DARK
                ThemeMode.SYSTEM -> ThemeMode.DARK
            }
            it.copy(themeMode = nextTheme)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setMode(mode: CalculatorMode) {
        _uiState.update { it.copy(currentMode = mode) }
    }

    fun setHistoryOpen(open: Boolean) {
        _uiState.update { it.copy(isHistoryOpen = open) }
    }

    fun setVoiceInputOpen(open: Boolean) {
        _uiState.update { it.copy(isVoiceInputOpen = open) }
    }

    fun applyVoiceSpeech(speechText: String) {
        val parsed = MathEvaluator.parseVoiceToExpression(speechText)
        if (parsed.isNotBlank()) {
            val formatted = MathEvaluator.formatExpression(parsed)
            val eval = MathEvaluator.evaluate(parsed, _uiState.value.isRadMode)
            val result = when (eval) {
                is EvaluationResult.Success -> eval.formattedText
                is EvaluationResult.Error -> "0"
            }
            _uiState.update {
                it.copy(
                    rawExpression = parsed,
                    formattedExpression = formatted,
                    displayResult = result,
                    isEvaluated = false,
                    isVoiceInputOpen = false
                )
            }
        }
    }

    fun reuseHistoryItem(item: CalculationEntity) {
        _uiState.update {
            it.copy(
                rawExpression = item.result.replace(",", ""),
                formattedExpression = item.expression,
                displayResult = item.result,
                isEvaluated = true,
                isHistoryOpen = false
            )
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            dao.deleteById(id)
        }
    }

    // --- Converter Functions ---
    fun selectConverterCategory(category: ConverterCategory) {
        val units = UnitConverter.unitsMap[category] ?: return
        val from = units[0]
        val to = if (units.size > 1) units[1] else units[0]
        _uiState.update {
            val out = calculateConverterOutput(it.converterInput, from, to, category)
            it.copy(
                converterCategory = category,
                converterFromUnit = from,
                converterToUnit = to,
                converterOutput = out
            )
        }
    }

    fun selectFromUnit(unit: ConversionUnit) {
        _uiState.update {
            val out = calculateConverterOutput(it.converterInput, unit, it.converterToUnit, it.converterCategory)
            it.copy(converterFromUnit = unit, converterOutput = out)
        }
    }

    fun selectToUnit(unit: ConversionUnit) {
        _uiState.update {
            val out = calculateConverterOutput(it.converterInput, it.converterFromUnit, unit, it.converterCategory)
            it.copy(converterToUnit = unit, converterOutput = out)
        }
    }

    fun swapConverterUnits() {
        _uiState.update {
            val newFrom = it.converterToUnit
            val newTo = it.converterFromUnit
            val out = calculateConverterOutput(it.converterInput, newFrom, newTo, it.converterCategory)
            it.copy(converterFromUnit = newFrom, converterToUnit = newTo, converterOutput = out)
        }
    }

    fun updateConverterInput(newInput: String) {
        val sanitized = if (newInput.isEmpty()) "0" else newInput
        _uiState.update {
            val out = calculateConverterOutput(sanitized, it.converterFromUnit, it.converterToUnit, it.converterCategory)
            it.copy(converterInput = newInput, converterOutput = out)
        }
    }

    private fun calculateConverterOutput(
        input: String,
        from: ConversionUnit,
        to: ConversionUnit,
        category: ConverterCategory
    ): String {
        val value = input.toDoubleOrNull() ?: return "0"
        val converted = UnitConverter.convert(value, from, to, category)
        return MathEvaluator.formatNumber(converted)
    }

    private fun calculatePreview(expr: String, isRad: Boolean): String {
        if (expr.isBlank()) return "0"
        return when (val eval = MathEvaluator.evaluate(expr, isRad)) {
            is EvaluationResult.Success -> eval.formattedText
            is EvaluationResult.Error -> ""
        }
    }
}
