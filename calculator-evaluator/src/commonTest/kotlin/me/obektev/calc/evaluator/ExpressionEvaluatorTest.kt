package me.obektev.calc.evaluator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExpressionEvaluatorTest {
    private val evaluator = ExpressionEvaluator()

    @Test
    fun evaluatesExpressionWithParenthesesAndPriority() {
        assertEquals(11.0, evaluator.evaluate("2 + 3 * (4 - 1)"))
    }

    @Test
    fun supportsUnaryMinus() {
        assertEquals(-14.0, evaluator.evaluate("-(2 + 5) * 2"))
    }

    @Test
    fun reportsDivisionByZero() {
        assertFailsWith<ArithmeticException> {
            evaluator.evaluate("10 / (5 - 5)")
        }
    }
}
