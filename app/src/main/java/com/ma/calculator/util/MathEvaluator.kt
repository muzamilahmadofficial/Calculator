package com.ma.calculator.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Stack
import kotlin.math.*

object MathEvaluator {

    private val symbols = DecimalFormatSymbols(Locale.US)
    private val decimalFormat = DecimalFormat("#,##0.##########", symbols)

    /**
     * Formats a raw arithmetic expression with thousands separators on numbers
     * while preserving operators like +, -, ×, ÷, %, ^, etc.
     */
    fun formatExpression(expression: String): String {
        if (expression.isEmpty()) return ""

        val regex = Regex("(\\d+\\.?\\d*|[-+×÷%^()*/]|asin|acos|atan|sin|cos|tan|log|ln|sqrt|cbrt|√|∛|π|e|!)")
        val tokens = regex.findAll(expression).map { it.value }.toList()

        val sb = StringBuilder()
        for (token in tokens) {
            val num = token.toDoubleOrNull()
            if (num != null && !token.endsWith(".")) {
                val formattedNum = formatNumber(num, token.contains("."))
                sb.append(formattedNum)
            } else if (token.endsWith(".")) {
                val base = token.dropLast(1).toDoubleOrNull()
                if (base != null) {
                    sb.append(formatNumber(base, false)).append(".")
                } else {
                    sb.append(token)
                }
            } else {
                when (token) {
                    "+", "-", "×", "÷", "^", "*" -> {
                        sb.append(" ").append(token).append(" ")
                    }
                    else -> sb.append(token)
                }
            }
        }
        return sb.toString().trim()
    }

    fun formatNumber(number: Double, preserveDecimals: Boolean = true): String {
        if (number.isNaN()) return "Error"
        if (number.isInfinite()) return if (number > 0) "∞" else "-∞"

        // For very large or very small numbers, use scientific notation
        if (abs(number) >= 1e12 || (abs(number) < 1e-6 && abs(number) > 0)) {
            val sciFormat = DecimalFormat("0.######E0", symbols)
            return sciFormat.format(number)
        }

        // Avoid -0.0
        val safeNum = if (abs(number) == 0.0) 0.0 else number
        return decimalFormat.format(safeNum)
    }

    /**
     * Evaluates an arithmetic expression with optional RAD/DEG mode.
     */
    fun evaluate(expression: String, isRadMode: Boolean = true): EvaluationResult {
        if (expression.isBlank()) return EvaluationResult.Success(0.0, "0")

        try {
            var sanitized = expression
                .replace("×", "*")
                .replace("÷", "/")
                .replace(" ", "")
                .replace(",", "")
                .replace("√", "sqrt")
                .replace("∛", "cbrt")
                .replace("π", Math.PI.toString())
                .replace("e", Math.E.toString())

            sanitized = preprocessPercentages(sanitized)

            val rpn = toRPN(sanitized)
            val result = evalRPN(rpn, isRadMode)
            if (result.isNaN() || result.isInfinite()) {
                return EvaluationResult.Error("Error")
            }
            val formatted = formatNumber(result)
            return EvaluationResult.Success(result, formatted)
        } catch (e: Exception) {
            return EvaluationResult.Error("Invalid expression")
        }
    }

    private fun preprocessPercentages(expr: String): String {
        val regex = Regex("(\\d+\\.?\\d*)%")
        return regex.replace(expr) { matchResult ->
            val numStr = matchResult.groupValues[1]
            val num = numStr.toDoubleOrNull() ?: 0.0
            (num / 100.0).toString()
        }
    }

    private fun isOperator(c: Char): Boolean = c in "+-*/^"

    private fun precedence(op: String): Int = when (op) {
        "+", "-" -> 1
        "*", "/" -> 2
        "^" -> 3
        "sin", "cos", "tan", "asin", "acos", "atan", "log", "ln", "sqrt", "cbrt" -> 4
        else -> 0
    }

    private val validFunctions = listOf("sin", "cos", "tan", "asin", "acos", "atan", "log", "ln", "sqrt", "cbrt")

    private fun toRPN(expr: String): List<String> {
        val output = mutableListOf<String>()
        val stack = Stack<String>()
        var i = 0

        while (i < expr.length) {
            val c = expr[i]

            // Numbers
            if (c.isDigit() || c == '.') {
                val num = StringBuilder()
                while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                    num.append(expr[i])
                    i++
                }
                output.add(num.toString())
                continue
            }

            // Unary minus
            if (c == '-' && (i == 0 || expr[i - 1] in "+-*/(^")) {
                val num = StringBuilder("-")
                i++
                while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                    num.append(expr[i])
                    i++
                }
                output.add(num.toString())
                continue
            }

            // Functions
            if (c.isLetter()) {
                val fn = StringBuilder()
                while (i < expr.length && expr[i].isLetter()) {
                    fn.append(expr[i])
                    i++
                }
                stack.push(fn.toString())
                continue
            }

            // Factorial
            if (c == '!') {
                output.add("!")
                i++
                continue
            }

            if (isOperator(c)) {
                val op = c.toString()
                while (stack.isNotEmpty() && stack.peek() != "(" && precedence(stack.peek()) >= precedence(op)) {
                    output.add(stack.pop())
                }
                stack.push(op)
                i++
                continue
            }

            if (c == '(') {
                stack.push("(")
                i++
                continue
            }

            if (c == ')') {
                while (stack.isNotEmpty() && stack.peek() != "(") {
                    output.add(stack.pop())
                }
                if (stack.isNotEmpty() && stack.peek() == "(") {
                    stack.pop()
                }
                if (stack.isNotEmpty() && stack.peek() in validFunctions) {
                    output.add(stack.pop())
                }
                i++
                continue
            }

            i++
        }

        while (stack.isNotEmpty()) {
            output.add(stack.pop())
        }

        return output
    }

    private fun evalRPN(tokens: List<String>, isRadMode: Boolean): Double {
        val stack = Stack<Double>()

        for (token in tokens) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                stack.push(num)
            } else {
                when (token) {
                    "+" -> {
                        val b = if (stack.isNotEmpty()) stack.pop() else 0.0
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        stack.push(a + b)
                    }
                    "-" -> {
                        val b = if (stack.isNotEmpty()) stack.pop() else 0.0
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        stack.push(a - b)
                    }
                    "*" -> {
                        val b = if (stack.isNotEmpty()) stack.pop() else 1.0
                        val a = if (stack.isNotEmpty()) stack.pop() else 1.0
                        stack.push(a * b)
                    }
                    "/" -> {
                        val b = if (stack.isNotEmpty()) stack.pop() else 1.0
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        if (b == 0.0) return Double.NaN
                        stack.push(a / b)
                    }
                    "^" -> {
                        val b = if (stack.isNotEmpty()) stack.pop() else 1.0
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        stack.push(a.pow(b))
                    }
                    "sqrt" -> {
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        if (a < 0) return Double.NaN
                        stack.push(sqrt(a))
                    }
                    "cbrt" -> {
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        stack.push(cbrt(a))
                    }
                    "sin" -> {
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        val angle = if (isRadMode) a else Math.toRadians(a)
                        stack.push(sin(angle))
                    }
                    "cos" -> {
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        val angle = if (isRadMode) a else Math.toRadians(a)
                        stack.push(cos(angle))
                    }
                    "tan" -> {
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        val angle = if (isRadMode) a else Math.toRadians(a)
                        stack.push(tan(angle))
                    }
                    "asin" -> {
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        if (a < -1.0 || a > 1.0) return Double.NaN
                        val rad = asin(a)
                        stack.push(if (isRadMode) rad else Math.toDegrees(rad))
                    }
                    "acos" -> {
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        if (a < -1.0 || a > 1.0) return Double.NaN
                        val rad = acos(a)
                        stack.push(if (isRadMode) rad else Math.toDegrees(rad))
                    }
                    "atan" -> {
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        val rad = atan(a)
                        stack.push(if (isRadMode) rad else Math.toDegrees(rad))
                    }
                    "log" -> {
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        if (a <= 0) return Double.NaN
                        stack.push(log10(a))
                    }
                    "ln" -> {
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        if (a <= 0) return Double.NaN
                        stack.push(ln(a))
                    }
                    "!" -> {
                        val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                        stack.push(factorial(a.toInt()).toDouble())
                    }
                }
            }
        }

        return if (stack.isNotEmpty()) stack.pop() else 0.0
    }

    private fun factorial(n: Int): Long {
        if (n < 0) return 0
        if (n <= 1) return 1
        var res = 1L
        for (i in 2..min(n, 20)) {
            res *= i
        }
        return res
    }

    /**
     * Translates natural language speech inputs into a clean arithmetic expression.
     */
    fun parseVoiceToExpression(spokenText: String): String {
        var text = spokenText.lowercase().trim()

        val tensMap = mapOf(
            "twenty" to 20L, "thirty" to 30L, "forty" to 40L,
            "fifty" to 50L, "sixty" to 60L, "seventy" to 70L,
            "eighty" to 80L, "ninety" to 90L
        )

        val percentOfRegex = Regex("([a-z0-9\\s]+?)\\s*(?:percent|%)\\s*(?:of)\\s*([a-z0-9\\s]+)")
        val match = percentOfRegex.find(text)
        if (match != null) {
            val pVal = parseSpokenNumber(match.groupValues[1])
            val totalVal = parseSpokenNumber(match.groupValues[2])
            return "$totalVal × $pVal%"
        }

        text = text.replace("plus", " + ")
            .replace("add", " + ")
            .replace("minus", " - ")
            .replace("subtract", " - ")
            .replace("times", " × ")
            .replace("multiplied by", " × ")
            .replace(" x ", " × ")
            .replace("divided by", " ÷ ")
            .replace("over", " ÷ ")
            .replace("divide", " ÷ ")
            .replace("percent", " % ")
            .replace("percentage", " % ")
            .replace("square root of", " √ ")
            .replace("square root", " √ ")
            .replace("point", " . ")
            .replace("equals", "")
            .replace("what is", "")
            .replace("calculate", "")

        val tokens = text.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        val result = mutableListOf<String>()
        val numTokens = mutableListOf<String>()

        fun flushNumTokens() {
            if (numTokens.isNotEmpty()) {
                val phrase = numTokens.joinToString(" ")
                val parsedNum = parseSpokenNumber(phrase)
                result.add(parsedNum)
                numTokens.clear()
            }
        }

        for (token in tokens) {
            if (token in listOf("+", "-", "×", "÷", "%", "√", "(", ")")) {
                flushNumTokens()
                result.add(token)
            } else {
                numTokens.add(token)
            }
        }
        flushNumTokens()

        return result.joinToString(" ")
    }

    private fun parseSpokenNumber(phrase: String): String {
        val trimmed = phrase.trim()
        trimmed.toLongOrNull()?.let { return it.toString() }
        trimmed.toDoubleOrNull()?.let { return it.toString() }

        val smallNumbers = mapOf(
            "zero" to 0L, "one" to 1L, "two" to 2L, "three" to 3L,
            "four" to 4L, "five" to 5L, "six" to 6L, "seven" to 7L,
            "eight" to 8L, "nine" to 9L, "ten" to 10L,
            "eleven" to 11L, "twelve" to 12L, "thirteen" to 13L,
            "fourteen" to 14L, "fifteen" to 15L, "sixteen" to 16L,
            "seventeen" to 17L, "eighteen" to 18L, "nineteen" to 19L,
            "twenty" to 20L, "thirty" to 30L, "forty" to 40L,
            "fifty" to 50L, "sixty" to 60L, "seventy" to 70L,
            "eighty" to 80L, "ninety" to 90L
        )

        val words = trimmed.split(" ").filter { it.isNotEmpty() && it != "and" }
        var current = 0L
        var total = 0L
        var hasValidWord = false

        for (word in words) {
            val num = smallNumbers[word]
            if (num != null) {
                current += num
                hasValidWord = true
            } else if (word == "hundred") {
                current = if (current == 0L) 100L else current * 100L
                hasValidWord = true
            } else if (word == "thousand") {
                current = if (current == 0L) 1000L else current * 1000L
                total += current
                current = 0L
                hasValidWord = true
            } else if (word == "million") {
                current = if (current == 0L) 1000000L else current * 1000000L
                total += current
                current = 0L
                hasValidWord = true
            } else {
                val directNum = word.toLongOrNull()
                if (directNum != null) {
                    current += directNum
                    hasValidWord = true
                }
            }
        }
        total += current

        return if (hasValidWord) total.toString() else trimmed
    }
}

sealed class EvaluationResult {
    data class Success(val value: Double, val formattedText: String) : EvaluationResult()
    data class Error(val message: String) : EvaluationResult()
}
