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

class SquareRootButton : CalculatorButton("sqrt") {
    override fun press(engine: CalculatorEngine) {
        engine.applySquareRoot()
    }
}

class SquareButton : CalculatorButton("x2") {
    override fun press(engine: CalculatorEngine) {
        engine.applySquare()
    }
}

class ReciprocalButton : CalculatorButton("1/x") {
    override fun press(engine: CalculatorEngine) {
        engine.applyReciprocal()
    }
}

class NaturalLogButton : CalculatorButton("ln") {
    override fun press(engine: CalculatorEngine) {
        engine.applyNaturalLog()
    }
}

class SinButton : CalculatorButton("sin") {
    override fun press(engine: CalculatorEngine) {
        engine.applySin()
    }
}

class CosButton : CalculatorButton("cos") {
    override fun press(engine: CalculatorEngine) {
        engine.applyCos()
    }
}

class TanButton : CalculatorButton("tan") {
    override fun press(engine: CalculatorEngine) {
        engine.applyTan()
    }
}

class MemoryClearButton : CalculatorButton("MC") {
    override fun press(engine: CalculatorEngine) {
        engine.memoryClear()
    }
}

class MemoryRecallButton : CalculatorButton("MR") {
    override fun press(engine: CalculatorEngine) {
        engine.memoryRecall()
    }
}

class MemoryStoreButton : CalculatorButton("MS") {
    override fun press(engine: CalculatorEngine) {
        engine.memoryStore()
    }
}

class MemoryAddButton : CalculatorButton("M+") {
    override fun press(engine: CalculatorEngine) {
        engine.memoryAdd()
    }
}

class MemorySubtractButton : CalculatorButton("M-") {
    override fun press(engine: CalculatorEngine) {
        engine.memorySubtract()
    }
}

class DegreeModeButton : CalculatorButton("DEG") {
    override fun press(engine: CalculatorEngine) {
        engine.setDegreeMode()
    }
}

class RadianModeButton : CalculatorButton("RAD") {
    override fun press(engine: CalculatorEngine) {
        engine.setRadianMode()
    }
}
