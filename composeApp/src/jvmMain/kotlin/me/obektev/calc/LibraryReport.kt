package me.obektev.calc

import me.obektev.calc.evaluator.ExpressionEvaluator

object LibraryReport {
    fun staticLibraryDemo(): String {
        val expression = "2 + 3 * (4 - 1)"
        val result = ExpressionEvaluator().evaluate(expression)
        return "Статическая библиотека вычислений: $expression = ${formatNumber(result)}"
    }

    private fun formatNumber(value: Double): String {
        val asLong = value.toLong()
        return if (value == asLong.toDouble()) {
            asLong.toString()
        } else {
            value.toString()
        }
    }
}
