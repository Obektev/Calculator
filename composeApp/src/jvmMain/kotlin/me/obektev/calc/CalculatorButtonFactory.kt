package me.obektev.calc

abstract class CalculatorButtonFactory {
    fun createRows(layout: List<List<String>>): List<List<CalculatorButton>> {
        return layout.map { row -> row.map { createButton(it) } }
    }

    protected abstract fun createButton(token: String): CalculatorButton
}

class StandardCalculatorButtonFactory : CalculatorButtonFactory() {
    override fun createButton(token: String): CalculatorButton {
        return when (token) {
            "0" -> DigitButton(0)
            "1" -> DigitButton(1)
            "2" -> DigitButton(2)
            "3" -> DigitButton(3)
            "4" -> DigitButton(4)
            "5" -> DigitButton(5)
            "6" -> DigitButton(6)
            "7" -> DigitButton(7)
            "8" -> DigitButton(8)
            "9" -> DigitButton(9)
            "+", "-", "*", "/" -> OperatorButton(token)
            "=" -> EqualsButton()
            "." -> DecimalButton()
            "C" -> ClearButton()
            "CE" -> ClearEntryButton()
            "%" -> PercentButton()
            "+/-" -> SignButton()
            else -> throw IllegalArgumentException("Unsupported token: $token")
        }
    }
}
