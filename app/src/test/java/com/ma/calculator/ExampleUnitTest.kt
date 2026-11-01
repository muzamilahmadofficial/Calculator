package com.ma.calculator

import com.ma.calculator.util.EvaluationResult
import com.ma.calculator.util.MathEvaluator
import com.ma.calculator.util.UnitConverter
import com.ma.calculator.util.ConverterCategory
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testDarkModeScreenshotCalculation() {
        val eval = MathEvaluator.evaluate("4900 + 15910")
        assertTrue(eval is EvaluationResult.Success)
        val success = eval as EvaluationResult.Success
        assertEquals(20810.0, success.value, 0.001)
        assertEquals("20,810", success.formattedText)
    }

    @Test
    fun testLightModeScreenshotCalculation() {
        val eval = MathEvaluator.evaluate("30820 + 9205")
        assertTrue(eval is EvaluationResult.Success)
        val success = eval as EvaluationResult.Success
        assertEquals(40025.0, success.value, 0.001)
        assertEquals("40,025", success.formattedText)
    }

    @Test
    fun testTrigonometricFunctions() {
        // sin(30) in DEG mode = 0.5
        val sinDeg = MathEvaluator.evaluate("sin(30)", isRadMode = false)
        assertTrue(sinDeg is EvaluationResult.Success)
        assertEquals(0.5, (sinDeg as EvaluationResult.Success).value, 0.001)

        // cos(0) = 1.0
        val cos0 = MathEvaluator.evaluate("cos(0)", isRadMode = true)
        assertTrue(cos0 is EvaluationResult.Success)
        assertEquals(1.0, (cos0 as EvaluationResult.Success).value, 0.001)

        // tan(45) in DEG mode = 1.0
        val tan45 = MathEvaluator.evaluate("tan(45)", isRadMode = false)
        assertTrue(tan45 is EvaluationResult.Success)
        assertEquals(1.0, (tan45 as EvaluationResult.Success).value, 0.001)

        // asin(0.5) in DEG mode = 30.0
        val asinDeg = MathEvaluator.evaluate("asin(0.5)", isRadMode = false)
        assertTrue(asinDeg is EvaluationResult.Success)
        assertEquals(30.0, (asinDeg as EvaluationResult.Success).value, 0.001)
    }

    @Test
    fun testLogarithmicFunctions() {
        // log10(100) = 2
        val log100 = MathEvaluator.evaluate("log(100)")
        assertTrue(log100 is EvaluationResult.Success)
        assertEquals(2.0, (log100 as EvaluationResult.Success).value, 0.001)

        // ln(e) = 1
        val lne = MathEvaluator.evaluate("ln(e)")
        assertTrue(lne is EvaluationResult.Success)
        assertEquals(1.0, (lne as EvaluationResult.Success).value, 0.001)
    }

    @Test
    fun testPowersAndRoots() {
        // 2^3 = 8
        val powEval = MathEvaluator.evaluate("2^3")
        assertTrue(powEval is EvaluationResult.Success)
        assertEquals(8.0, (powEval as EvaluationResult.Success).value, 0.001)

        // sqrt(16) = 4
        val sqrtEval = MathEvaluator.evaluate("√(16)")
        assertTrue(sqrtEval is EvaluationResult.Success)
        assertEquals(4.0, (sqrtEval as EvaluationResult.Success).value, 0.001)

        // cbrt(27) = 3
        val cbrtEval = MathEvaluator.evaluate("∛(27)")
        assertTrue(cbrtEval is EvaluationResult.Success)
        assertEquals(3.0, (cbrtEval as EvaluationResult.Success).value, 0.001)
    }

    @Test
    fun testMultiplicationAndDivision() {
        val eval = MathEvaluator.evaluate("25 × 4 ÷ 2")
        assertTrue(eval is EvaluationResult.Success)
        val success = eval as EvaluationResult.Success
        assertEquals(50.0, success.value, 0.001)
    }

    @Test
    fun testPercentages() {
        val eval = MathEvaluator.evaluate("500 * 20%")
        assertTrue(eval is EvaluationResult.Success)
        val success = eval as EvaluationResult.Success
        assertEquals(100.0, success.value, 0.001)
    }

    @Test
    fun testVoiceSpeechParsing() {
        val parsed = MathEvaluator.parseVoiceToExpression("fifteen hundred plus thirty")
        assertEquals("1500 + 30", parsed)

        val parsedPercent = MathEvaluator.parseVoiceToExpression("twenty percent of four hundred")
        assertEquals("400 × 20%", parsedPercent)
    }

    @Test
    fun testUnitConverter() {
        val lengthUnits = UnitConverter.unitsMap[ConverterCategory.LENGTH]!!
        val meters = lengthUnits.first { it.id == "m" }
        val km = lengthUnits.first { it.id == "km" }

        val converted = UnitConverter.convert(2500.0, meters, km, ConverterCategory.LENGTH)
        assertEquals(2.5, converted, 0.001)
    }
}
