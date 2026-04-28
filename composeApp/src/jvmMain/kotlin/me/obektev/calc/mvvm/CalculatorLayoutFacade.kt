package me.obektev.calc.mvvm

enum class ButtonGroup {
    MEMORY,
    ENGINEERING,
    OPERATOR,
    DIGIT,
    CONTROL,
}

interface CalculatorLayoutFacade {
    fun basicRows(): List<List<String>>

    fun engineeringRows(): List<List<String>>

    fun buttonGroup(token: String): ButtonGroup
}

class DefaultCalculatorLayoutFacade : CalculatorLayoutFacade {
    override fun basicRows(): List<List<String>> {
        return BASIC_ROWS
    }

    override fun engineeringRows(): List<List<String>> {
        return ENGINEERING_ROWS
    }

    override fun buttonGroup(token: String): ButtonGroup {
        return when (token) {
            "MC", "MR", "MS", "M+", "M-" -> ButtonGroup.MEMORY
            "sin", "cos", "tan", "ln", "sqrt", "x2", "1/x", "DEG", "RAD" -> ButtonGroup.ENGINEERING
            "+", "-", "*", "/", "=", "%", "+/-" -> ButtonGroup.OPERATOR
            "C", "CE" -> ButtonGroup.CONTROL
            else -> ButtonGroup.DIGIT
        }
    }

    companion object {
        private val BASIC_ROWS = listOf(
            listOf("MC", "MR", "MS", "M+"),
            listOf("M-", "%", "CE", "C"),
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("+/-", "0", ".", "+"),
            listOf("="),
        )

        private val ENGINEERING_ROWS = listOf(
            listOf("DEG", "RAD", "sin", "cos"),
            listOf("tan", "ln", "sqrt", "x2"),
            listOf("1/x"),
        )
    }
}
