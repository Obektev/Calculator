package me.obektev.calc

import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatorEngineTest {

    @Test
    fun shouldAddNumbers() {
        val engine = CalculatorEngine()

        engine.inputDigit(1)
        engine.inputDigit(2)
        engine.setOperation("+")
        engine.inputDigit(7)
        engine.evaluate()

        assertEquals("19", engine.display)
    }

    @Test
    fun shouldMultiplyDecimalNumbers() {
        val engine = CalculatorEngine()

        engine.inputDigit(1)
        engine.inputDecimalPoint()
        engine.inputDigit(5)
        engine.setOperation("*")
        engine.inputDigit(2)
        engine.evaluate()

        assertEquals("3", engine.display)
    }

    @Test
    fun shouldResetStateAfterClear() {
        val engine = CalculatorEngine()

        engine.inputDigit(9)
        engine.setOperation("-")
        engine.inputDigit(3)
        engine.clearAll()

        assertEquals("0", engine.display)
    }

    @Test
    fun shouldShowErrorOnDivisionByZero() {
        val engine = CalculatorEngine()

        engine.inputDigit(8)
        engine.setOperation("/")
        engine.inputDigit(0)
        engine.evaluate()

        assertEquals("Error", engine.display)
    }
}
