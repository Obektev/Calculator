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

data class EngineSnapshot(
    val display: String,
    val leftOperand: Double?,
    val pendingOperationSymbol: String?,
    val startNewInput: Boolean,
    val memory: Double,
    val hasMemory: Boolean,
    val currentAngleMode: AngleMode,
    val expressionTokens: List<String>,
    val resultCommitted: Boolean,
)

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
    private var expressionTokens: MutableList<String> = mutableListOf()
    private var resultCommitted: Boolean = false

    val expressionDisplay: String
        get() = buildExpressionDisplay()

    fun inputDigit(digit: Int) {
        if (resultCommitted) {
            expressionTokens.clear()
            resultCommitted = false
        }

        if (display == "Error" || startNewInput || display == "0") {
            display = digit.toString()
        } else {
            display += digit
        }
        startNewInput = false
    }

    fun inputDecimalPoint() {
        if (resultCommitted) {
            expressionTokens.clear()
            resultCommitted = false
        }

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

        if (resultCommitted) {
            expressionTokens.clear()
            resultCommitted = false
        }

        if (pendingOperation != null && !startNewInput) {
            evaluateInternal(currentValue)
            expressionTokens.clear()
            expressionTokens.add(display)
        } else {
            leftOperand = currentValue
            if (expressionTokens.isEmpty()) {
                expressionTokens.add(formatNumber(currentValue))
            }
        }

        pendingOperation = operation
        if (expressionTokens.isNotEmpty() && isOperatorToken(expressionTokens.last())) {
            expressionTokens[expressionTokens.lastIndex] = symbol
        } else {
            expressionTokens.add(symbol)
        }
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
        resultCommitted = true
        expressionTokens.clear()
    }

    fun clearAll() {
        display = "0"
        leftOperand = null
        pendingOperation = null
        startNewInput = true
        expressionTokens.clear()
        resultCommitted = false
    }

    fun clearEntry() {
        display = "0"
        startNewInput = true
        if (resultCommitted) {
            expressionTokens.clear()
            resultCommitted = false
        }
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

    fun saveSnapshot(): EngineSnapshot {
        return EngineSnapshot(
            display = display,
            leftOperand = leftOperand,
            pendingOperationSymbol = pendingOperation?.symbol,
            startNewInput = startNewInput,
            memory = memory,
            hasMemory = hasMemory,
            currentAngleMode = currentAngleMode,
            expressionTokens = expressionTokens.toList(),
            resultCommitted = resultCommitted,
        )
    }

    fun restoreSnapshot(snapshot: EngineSnapshot) {
        display = snapshot.display
        leftOperand = snapshot.leftOperand
        pendingOperation = snapshot.pendingOperationSymbol?.let { operationRegistry.findBySymbol(it) }
        startNewInput = snapshot.startNewInput
        memory = snapshot.memory
        hasMemory = snapshot.hasMemory
        currentAngleMode = snapshot.currentAngleMode
        expressionTokens = snapshot.expressionTokens.toMutableList()
        resultCommitted = snapshot.resultCommitted
    }

    private fun evaluateInternal(rightOperand: Double) {
        val operation = pendingOperation ?: return
        val left = leftOperand ?: rightOperand

        try {
            val result = operation.apply(left, rightOperand)
            display = formatNumber(result)
            leftOperand = result
            resultCommitted = false
        } catch (_: ArithmeticException) {
            display = "Error"
            leftOperand = null
            pendingOperation = null
            startNewInput = true
            expressionTokens.clear()
            resultCommitted = true
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
            resultCommitted = false
        } catch (_: ArithmeticException) {
            display = "Error"
            leftOperand = null
            pendingOperation = null
            startNewInput = true
            expressionTokens.clear()
            resultCommitted = true
        }
    }

    private fun isOperatorToken(token: String): Boolean {
        return token == "+" || token == "-" || token == "*" || token == "/"
    }

    private fun buildExpressionDisplay(): String {
        if (resultCommitted || expressionTokens.isEmpty()) {
            return display
        }

        val expression = expressionTokens.joinToString(" ")
        return if (startNewInput) {
            expression
        } else {
            "$expression $display"
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
