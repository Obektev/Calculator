package me.obektev.calc

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

enum class AngleMode {
    DEG,
    RAD,
}

class CalculatorEngine(
    private val operationRegistry: OperationRegistry = OperationRegistry(),
) {
    var display: String = "0"
        private set

    val angleMode: AngleMode
        get() = currentAngleMode

    val hasMemoryValue: Boolean
        get() = hasMemory

    private var leftOperand: Double? = null
    private var pendingOperation: CalculatorOperation? = null
    private var startNewInput: Boolean = true
    private var memory: Double = 0.0
    private var hasMemory: Boolean = false
    private var currentAngleMode: AngleMode = AngleMode.RAD

    fun inputDigit(digit: Int) {
        if (display == "Error" || startNewInput || display == "0") {
            display = digit.toString()
        } else {
            display += digit
        }
        startNewInput = false
    }

    fun inputDecimalPoint() {
        if (display == "Error" || startNewInput) {
            display = "0."
            startNewInput = false
            return
        }
        if (!display.contains('.')) {
            display += "."
        }
    }

    fun setOperation(symbol: String) {
        val operation = operationRegistry.findBySymbol(symbol) ?: return
        val currentValue = display.toDoubleOrNull() ?: return

        if (pendingOperation != null && !startNewInput) {
            evaluateInternal(currentValue)
        } else {
            leftOperand = currentValue
        }

        pendingOperation = operation
        startNewInput = true
    }

    fun evaluate() {
        val currentValue = display.toDoubleOrNull() ?: return
        if (pendingOperation == null) {
            return
        }

        evaluateInternal(currentValue)
        pendingOperation = null
        startNewInput = true
    }

    fun clearAll() {
        display = "0"
        leftOperand = null
        pendingOperation = null
        startNewInput = true
    }

    fun clearEntry() {
        display = "0"
        startNewInput = true
    }

    fun toggleSign() {
        val currentValue = display.toDoubleOrNull() ?: return
        display = formatNumber(-currentValue)
    }

    fun applyPercent() {
        val currentValue = display.toDoubleOrNull() ?: return
        display = formatNumber(currentValue / 100.0)
    }

    fun applySquareRoot() {
        applyUnaryOperation { value ->
            if (value < 0.0) {
                throw ArithmeticException("Square root from negative value")
            }
            sqrt(value)
        }
    }

    fun applySquare() {
        applyUnaryOperation { value -> value * value }
    }

    fun applyReciprocal() {
        applyUnaryOperation { value ->
            if (value == 0.0) {
                throw ArithmeticException("Division by zero")
            }
            1.0 / value
        }
    }

    fun applyNaturalLog() {
        applyUnaryOperation { value ->
            if (value <= 0.0) {
                throw ArithmeticException("Log of non-positive value")
            }
            ln(value)
        }
    }

    fun applySin() {
        applyUnaryOperation { value -> sin(toRadiansIfNeeded(value)) }
    }

    fun applyCos() {
        applyUnaryOperation { value -> cos(toRadiansIfNeeded(value)) }
    }

    fun applyTan() {
        applyUnaryOperation { value -> tan(toRadiansIfNeeded(value)) }
    }

    fun setDegreeMode() {
        currentAngleMode = AngleMode.DEG
    }

    fun setRadianMode() {
        currentAngleMode = AngleMode.RAD
    }

    fun memoryStore() {
        val currentValue = display.toDoubleOrNull() ?: return
        memory = currentValue
        hasMemory = true
        startNewInput = true
    }

    fun memoryRecall() {
        display = formatNumber(memory)
        startNewInput = true
    }

    fun memoryClear() {
        memory = 0.0
        hasMemory = false
    }

    fun memoryAdd() {
        val currentValue = display.toDoubleOrNull() ?: return
        memory += currentValue
        hasMemory = true
        startNewInput = true
    }

    fun memorySubtract() {
        val currentValue = display.toDoubleOrNull() ?: return
        memory -= currentValue
        hasMemory = true
        startNewInput = true
    }

    private fun evaluateInternal(rightOperand: Double) {
        val operation = pendingOperation ?: return
        val left = leftOperand ?: rightOperand

        try {
            val result = operation.apply(left, rightOperand)
            display = formatNumber(result)
            leftOperand = result
        } catch (_: ArithmeticException) {
            display = "Error"
            leftOperand = null
            pendingOperation = null
            startNewInput = true
        }
    }

    private fun applyUnaryOperation(transform: (Double) -> Double) {
        val currentValue = display.toDoubleOrNull() ?: return
        try {
            val result = transform(currentValue)
            if (!result.isFinite()) {
                throw ArithmeticException("Non-finite result")
            }
            display = formatNumber(result)
            startNewInput = true
        } catch (_: ArithmeticException) {
            display = "Error"
            leftOperand = null
            pendingOperation = null
            startNewInput = true
        }
    }

    private fun toRadiansIfNeeded(value: Double): Double {
        return if (currentAngleMode == AngleMode.DEG) {
            Math.toRadians(value)
        } else {
            value
        }
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
