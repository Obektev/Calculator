package me.obektev.calc

abstract class CalculatorButton(val label: String) {
    abstract fun press(engine: CalculatorEngine)
}

class DigitButton(private val digit: Int) : CalculatorButton(digit.toString()) {
    override fun press(engine: CalculatorEngine) {
        engine.inputDigit(digit)
    }
}

class OperatorButton(private val operation: String) : CalculatorButton(operation) {
    override fun press(engine: CalculatorEngine) {
        engine.setOperation(operation)
    }
}

class EqualsButton : CalculatorButton("=") {
    override fun press(engine: CalculatorEngine) {
        engine.evaluate()
    }
}

class DecimalButton : CalculatorButton(".") {
    override fun press(engine: CalculatorEngine) {
        engine.inputDecimalPoint()
    }
}

class ClearButton : CalculatorButton("C") {
    override fun press(engine: CalculatorEngine) {
        engine.clearAll()
    }
}

class ClearEntryButton : CalculatorButton("CE") {
    override fun press(engine: CalculatorEngine) {
        engine.clearEntry()
    }
}

class PercentButton : CalculatorButton("%") {
    override fun press(engine: CalculatorEngine) {
        engine.applyPercent()
    }
}

class SignButton : CalculatorButton("+/-") {
    override fun press(engine: CalculatorEngine) {
        engine.toggleSign()
    }
}
