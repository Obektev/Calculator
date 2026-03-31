package me.obektev.calc

class CalculatorEngine(
    private val operationRegistry: OperationRegistry = OperationRegistry(),
) {
    var display: String = "0"
        private set

    private var leftOperand: Double? = null
    private var pendingOperation: CalculatorOperation? = null
    private var startNewInput: Boolean = true

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

    private fun formatNumber(value: Double): String {
        val asLong = value.toLong()
        return if (value == asLong.toDouble()) {
            asLong.toString()
        } else {
            value.toString()
        }
    }
}
