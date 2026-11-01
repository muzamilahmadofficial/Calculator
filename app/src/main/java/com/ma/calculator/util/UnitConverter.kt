package com.ma.calculator.util

enum class ConverterCategory(val title: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    TEMPERATURE("Temperature"),
    SPEED("Speed"),
    VOLUME("Volume")
}

data class ConversionUnit(val id: String, val name: String, val symbol: String, val factorToBase: Double = 1.0)

object UnitConverter {

    val unitsMap: Map<ConverterCategory, List<ConversionUnit>> = mapOf(
        ConverterCategory.LENGTH to listOf(
            ConversionUnit("m", "Meters", "m", 1.0),
            ConversionUnit("km", "Kilometers", "km", 1000.0),
            ConversionUnit("cm", "Centimeters", "cm", 0.01),
            ConversionUnit("mm", "Millimeters", "mm", 0.001),
            ConversionUnit("mi", "Miles", "mi", 1609.344),
            ConversionUnit("yd", "Yards", "yd", 0.9144),
            ConversionUnit("ft", "Feet", "ft", 0.3048),
            ConversionUnit("in", "Inches", "in", 0.0254)
        ),
        ConverterCategory.WEIGHT to listOf(
            ConversionUnit("kg", "Kilograms", "kg", 1.0),
            ConversionUnit("g", "Grams", "g", 0.001),
            ConversionUnit("mg", "Milligrams", "mg", 0.000001),
            ConversionUnit("lb", "Pounds", "lbs", 0.45359237),
            ConversionUnit("oz", "Ounces", "oz", 0.02834952),
            ConversionUnit("t", "Metric Ton", "t", 1000.0)
        ),
        ConverterCategory.TEMPERATURE to listOf(
            ConversionUnit("c", "Celsius", "°C"),
            ConversionUnit("f", "Fahrenheit", "°F"),
            ConversionUnit("k", "Kelvin", "K")
        ),
        ConverterCategory.SPEED to listOf(
            ConversionUnit("ms", "Meters/sec", "m/s", 1.0),
            ConversionUnit("kmh", "Km/hour", "km/h", 0.277778),
            ConversionUnit("mph", "Miles/hour", "mph", 0.44704),
            ConversionUnit("knot", "Knots", "kn", 0.514444)
        ),
        ConverterCategory.VOLUME to listOf(
            ConversionUnit("l", "Liters", "L", 1.0),
            ConversionUnit("ml", "Milliliters", "mL", 0.001),
            ConversionUnit("gal", "Gallons (US)", "gal", 3.78541),
            ConversionUnit("cup", "Cups (US)", "cup", 0.236588),
            ConversionUnit("floz", "Fluid Ounces", "fl oz", 0.0295735)
        )
    )

    fun convert(value: Double, from: ConversionUnit, to: ConversionUnit, category: ConverterCategory): Double {
        if (category == ConverterCategory.TEMPERATURE) {
            // Convert to Celsius first
            val inCelsius = when (from.id) {
                "c" -> value
                "f" -> (value - 32.0) * 5.0 / 9.0
                "k" -> value - 273.15
                else -> value
            }
            // Convert from Celsius to target
            return when (to.id) {
                "c" -> inCelsius
                "f" -> (inCelsius * 9.0 / 5.0) + 32.0
                "k" -> inCelsius + 273.15
                else -> inCelsius
            }
        }

        // Standard multiplicative factor
        val baseVal = value * from.factorToBase
        return baseVal / to.factorToBase
    }
}
