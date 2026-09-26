package com.example.domain.smart

import java.text.DecimalFormat

object UnitConverterEngine {

    enum class UnitCategory(val titlePersian: String) {
        LENGTH("طول و مسافت"),
        WEIGHT("وزن و جرم"),
        TEMPERATURE("دما"),
        VOLUME("حجم و مایعات"),
        SPEED("سرعت"),
        AREA("مساحت"),
        TIME("زمان")
    }

    data class UnitItem(
        val id: String,
        val namePersian: String,
        val symbol: String,
        val factorToBase: Double // conversion factor to standard base unit (meter, kg, etc.)
    )

    data class ConversionResult(
        val category: UnitCategory,
        val fromValue: Double,
        val fromUnit: UnitItem,
        val toUnit: UnitItem,
        val resultValue: Double,
        val displayText: String
    )

    val LENGTH_UNITS = listOf(
        UnitItem("m", "متر", "m", 1.0),
        UnitItem("km", "کیلومتر", "km", 1000.0),
        UnitItem("cm", "سانتی‌متر", "cm", 0.01),
        UnitItem("mm", "میلی‌متر", "mm", 0.001),
        UnitItem("inch", "اینچ", "in", 0.0254),
        UnitItem("foot", "فوت", "ft", 0.3048),
        UnitItem("mile", "مایل", "mi", 1609.344)
    )

    val WEIGHT_UNITS = listOf(
        UnitItem("kg", "کیلوگرم", "kg", 1.0),
        UnitItem("g", "گرم", "g", 0.001),
        UnitItem("mg", "میلی‌گرم", "mg", 0.000001),
        UnitItem("ton", "تن", "ton", 1000.0),
        UnitItem("lb", "پوند", "lb", 0.45359237),
        UnitItem("oz", "اونس", "oz", 0.028349523)
    )

    val VOLUME_UNITS = listOf(
        UnitItem("l", "لیتر", "L", 1.0),
        UnitItem("ml", "میلی‌لیتر", "mL", 0.001),
        UnitItem("m3", "متر مکعب", "m³", 1000.0),
        UnitItem("gal", "گالن آمریکایی", "gal", 3.78541)
    )

    val SPEED_UNITS = listOf(
        UnitItem("kmh", "کیلومتر بر ساعت", "km/h", 1.0),
        UnitItem("ms", "متر بر ثانیه", "m/s", 3.6),
        UnitItem("mph", "مایل بر ساعت", "mph", 1.60934)
    )

    val AREA_UNITS = listOf(
        UnitItem("m2", "متر مربع", "m²", 1.0),
        UnitItem("hectare", "هکتار", "ha", 10000.0),
        UnitItem("km2", "کیلومتر مربع", "km²", 1000000.0),
        UnitItem("sqft", "فوت مربع", "sq ft", 0.092903)
    )

    val TIME_UNITS = listOf(
        UnitItem("s", "ثانیه", "s", 1.0),
        UnitItem("min", "دقیقه", "min", 60.0),
        UnitItem("h", "ساعت", "h", 3600.0),
        UnitItem("d", "روز", "d", 86400.0)
    )

    fun convert(
        category: UnitCategory,
        value: Double,
        fromUnitId: String,
        toUnitId: String
    ): ConversionResult? {
        val df = DecimalFormat("#.####")

        if (category == UnitCategory.TEMPERATURE) {
            val result = convertTemperature(value, fromUnitId, toUnitId)
            val fromName = when (fromUnitId) { "C" -> "سانتی‌گراد" "F" -> "فارنهایت" else -> "کلوین" }
            val toName = when (toUnitId) { "C" -> "سانتی‌گراد" "F" -> "فارنهایت" else -> "کلوین" }
            val formatted = PersianNumberIntelligence.toPersianDigits(df.format(result))
            val faVal = PersianNumberIntelligence.toPersianDigits(df.format(value))
            return ConversionResult(
                category = category,
                fromValue = value,
                fromUnit = UnitItem(fromUnitId, fromName, fromUnitId, 1.0),
                toUnit = UnitItem(toUnitId, toName, toUnitId, 1.0),
                resultValue = result,
                displayText = "$faVal $fromName = $formatted $toName"
            )
        }

        val units = when (category) {
            UnitCategory.LENGTH -> LENGTH_UNITS
            UnitCategory.WEIGHT -> WEIGHT_UNITS
            UnitCategory.VOLUME -> VOLUME_UNITS
            UnitCategory.SPEED -> SPEED_UNITS
            UnitCategory.AREA -> AREA_UNITS
            UnitCategory.TIME -> TIME_UNITS
            else -> LENGTH_UNITS
        }

        val from = units.find { it.id == fromUnitId } ?: units.first()
        val to = units.find { it.id == toUnitId } ?: units[1]

        val inBase = value * from.factorToBase
        val result = inBase / to.factorToBase

        val formatted = PersianNumberIntelligence.toPersianDigits(df.format(result))
        val faVal = PersianNumberIntelligence.toPersianDigits(df.format(value))

        return ConversionResult(
            category = category,
            fromValue = value,
            fromUnit = from,
            toUnit = to,
            resultValue = result,
            displayText = "$faVal ${from.namePersian} = $formatted ${to.namePersian}"
        )
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        val inCelsius = when (from) {
            "C" -> value
            "F" -> (value - 32) * 5 / 9
            "K" -> value - 273.15
            else -> value
        }
        return when (to) {
            "C" -> inCelsius
            "F" -> inCelsius * 9 / 5 + 32
            "K" -> inCelsius + 273.15
            else -> inCelsius
        }
    }
}
