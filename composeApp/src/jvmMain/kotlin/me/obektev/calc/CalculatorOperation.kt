package me.obektev.calc

interface CalculatorOperation {
    val symbol: String

    fun apply(left: Double, right: Double): Double
}

class AdditionOperation : CalculatorOperation {
    override val symbol: String = "+"

    override fun apply(left: Double, right: Double): Double = left + right
}

class SubtractionOperation : CalculatorOperation {
    override val symbol: String = "-"

    override fun apply(left: Double, right: Double): Double = left - right
}

class MultiplicationOperation : CalculatorOperation {
    override val symbol: String = "*"

    override fun apply(left: Double, right: Double): Double = left * right
}

class DivisionOperation : CalculatorOperation {
    override val symbol: String = "/"

    override fun apply(left: Double, right: Double): Double {
        if (right == 0.0) {
            throw ArithmeticException("Division by zero")
        }
        return left / right
    }
}

class OperationRegistry(
    private val operations: List<CalculatorOperation> = listOf(
        AdditionOperation(),
        SubtractionOperation(),
        MultiplicationOperation(),
        DivisionOperation(),
    ),
) {
    fun findBySymbol(symbol: String): CalculatorOperation? {
        return operations.firstOrNull { it.symbol == symbol }
    }
}
