package com.example.domain.smart

import java.text.DecimalFormat

object KeyboardCalculatorEngine {

    data class CalcResult(
        val success: Boolean,
        val resultNumber: Double? = null,
        val formattedResult: String = "",
        val formattedResultWithCommas: String = "",
        val persianWords: String = ""
    )

    /**
     * Evaluates standard arithmetic expressions: +, -, *, /, %, ^, parentheses.
     * Supports both English and Persian digits.
     */
    fun evaluate(expression: String): CalcResult {
        if (expression.isBlank()) return CalcResult(success = false)

        try {
            val engExpr = PersianNumberIntelligence.toEnglishDigits(expression)
                .replace("×", "*")
                .replace("÷", "/")
                .replace("π", "3.14159265")
                .replace("√", "sqrt")
                .replace("²", "^2")
                .replace("³", "^3")
                .replace(",", "")
                .replace(" ", "")

            val resultVal = evalMathExpression(engExpr)
            if (resultVal.isNaN() || resultVal.isInfinite()) {
                return CalcResult(success = false, formattedResult = "خطای محاسبه")
            }

            val df = DecimalFormat("#.######")
            val formatted = df.format(resultVal)
            val withCommas = if (resultVal % 1.0 == 0.0) {
                PersianNumberIntelligence.formatWithCommas(resultVal.toLong().toString())
            } else {
                PersianNumberIntelligence.toPersianDigits(formatted)
            }

            val words = if (resultVal % 1.0 == 0.0) {
                PersianNumberIntelligence.numberToPersianWords(resultVal.toLong().toString())
            } else ""

            return CalcResult(
                success = true,
                resultNumber = resultVal,
                formattedResult = PersianNumberIntelligence.toPersianDigits(formatted),
                formattedResultWithCommas = withCommas,
                persianWords = words
            )
        } catch (e: Exception) {
            return CalcResult(success = false, formattedResult = "خطا در عبارت")
        }
    }

    private fun evalMathExpression(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm()
                        eat('-'.code) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*'.code) -> x *= parseFactor()
                        eat('/'.code) -> {
                            val denominator = parseFactor()
                            if (denominator == 0.0) throw ArithmeticException("Division by zero")
                            x /= denominator
                        }
                        eat('%'.code) -> x %= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return +parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch in 'a'.code..'z'.code) {
                    while (ch in 'a'.code..'z'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    x = parseFactor()
                    x = when (func) {
                        "sqrt" -> Math.sqrt(x)
                        "sin" -> Math.sin(Math.toRadians(x))
                        "cos" -> Math.cos(Math.toRadians(x))
                        "tan" -> Math.tan(Math.toRadians(x))
                        "abs" -> Math.abs(x)
                        "ln" -> if (x > 0) Math.log(x) else Double.NaN
                        "log" -> if (x > 0) Math.log10(x) else Double.NaN
                        else -> throw RuntimeException("Unknown function: $func")
                    }
                } else if (ch in '0'.code..'9'.code || ch == '.'.code) {
                    while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }

                if (eat('^'.code)) x = Math.pow(x, parseFactor())

                return x
            }
        }.parse()
    }
}
