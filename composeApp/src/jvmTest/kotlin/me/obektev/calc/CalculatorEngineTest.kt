package me.obektev.calc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

    @Test
    fun shouldToggleSign() {
        val engine = CalculatorEngine()

        engine.inputDigit(2)
        engine.inputDigit(5)
        engine.toggleSign()

        assertEquals("-25", engine.display)
    }

    @Test
    fun shouldApplyPercent() {
        val engine = CalculatorEngine()

        engine.inputDigit(5)
        engine.inputDigit(0)
        engine.applyPercent()

        assertEquals("0.5", engine.display)
    }

    @Test
    fun shouldClearEntryOnly() {
        val engine = CalculatorEngine()

        engine.inputDigit(9)
        engine.inputDigit(9)
        engine.setOperation("+")
        engine.inputDigit(1)
        engine.inputDigit(2)
        engine.clearEntry()
        engine.inputDigit(3)
        engine.evaluate()

        assertEquals("102", engine.display)
    }

    @Test
    fun shouldCalculateSquareRoot() {
        val engine = CalculatorEngine()

        engine.inputDigit(8)
        engine.inputDigit(1)
        engine.applySquareRoot()

        assertEquals("9", engine.display)
    }

    @Test
    fun shouldCalculateNaturalLog() {
        val engine = CalculatorEngine()

        engine.inputDigit(1)
        engine.applyNaturalLog()

        assertEquals("0", engine.display)
    }

    @Test
    fun shouldUseMemoryStoreAndRecall() {
        val engine = CalculatorEngine()

        engine.inputDigit(4)
        engine.inputDigit(2)
        engine.memoryStore()
        engine.clearAll()
        engine.memoryRecall()

        assertEquals("42", engine.display)
    }

    @Test
    fun shouldUseMemoryAddAndSubtract() {
        val engine = CalculatorEngine()

        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.memoryStore()
        engine.inputDigit(5)
        engine.memoryAdd()
        engine.inputDigit(3)
        engine.memorySubtract()
        engine.memoryRecall()

        assertEquals("12", engine.display)
    }

    @Test
    fun shouldApplyTrigInDegreeMode() {
        val engine = CalculatorEngine()

        engine.setDegreeMode()
        engine.inputDigit(9)
        engine.inputDigit(0)
        engine.applySin()

        assertEquals("1", engine.display)
    }

    @Test
    fun shouldApplyTrigInRadianMode() {
        val engine = CalculatorEngine()

        engine.setRadianMode()
        engine.inputDigit(9)
        engine.inputDigit(0)
        engine.applySin()

        assertTrue(engine.display != "1")
    }

    @Test
    fun shouldTrackMemoryIndicatorState() {
        val engine = CalculatorEngine()

        assertFalse(engine.hasMemoryValue)

        engine.inputDigit(7)
        engine.memoryStore()
        assertTrue(engine.hasMemoryValue)

        engine.memoryClear()
        assertFalse(engine.hasMemoryValue)
    }

    @Test
    fun shouldShowExpressionThenResult() {
        val engine = CalculatorEngine()

        engine.inputDigit(1)
        engine.inputDigit(2)
        engine.setOperation("+")
        engine.inputDigit(7)

        assertEquals("12 + 7", engine.expressionDisplay)

        engine.evaluate()

        assertEquals("19", engine.expressionDisplay)
    }

    @Test
    fun shouldRestoreSnapshot() {
        val engine = CalculatorEngine()

        engine.inputDigit(4)
        val snapshot = engine.saveSnapshot()
        engine.inputDigit(2)

        assertEquals("42", engine.display)

        engine.restoreSnapshot(snapshot)

        assertEquals("4", engine.display)
    }

    @Test
    fun shouldShowRecalledMemoryAsRightOperand() {
        val engine = CalculatorEngine()

        engine.inputDigit(8)
        engine.memoryStore()
        engine.clearAll()
        engine.inputDigit(1)
        engine.setOperation("+")
        engine.memoryRecall()

        assertEquals("1 + 8", engine.expressionDisplay)
    }

    @Test
    fun shouldShowUnaryResultAfterOperator() {
        val engine = CalculatorEngine()

        engine.setDegreeMode()
        engine.inputDigit(2)
        engine.setOperation("+")
        engine.inputDigit(9)
        engine.inputDigit(0)
        engine.applySin()

        assertEquals("2 + 1", engine.expressionDisplay)

        engine.evaluate()

        assertEquals("3", engine.expressionDisplay)
    }
}
