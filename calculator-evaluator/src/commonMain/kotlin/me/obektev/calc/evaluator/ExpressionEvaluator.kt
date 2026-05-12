package me.obektev.calc.evaluator

import me.obektev.calc.parser.BinaryNode
import me.obektev.calc.parser.ExpressionNode
import me.obektev.calc.parser.ExpressionParser
import me.obektev.calc.parser.NumberNode
import me.obektev.calc.parser.UnaryNode

class ExpressionEvaluator(
    private val parser: ExpressionParser = ExpressionParser(),
    private val operations: OperationRegistry = OperationRegistry(),
) {
    fun evaluate(expression: String): Double {
        return evaluate(parser.parse(expression))
    }

    fun evaluate(node: ExpressionNode): Double {
        return when (node) {
            is NumberNode -> node.value
            is UnaryNode -> evaluateUnary(node)
            is BinaryNode -> evaluateBinary(node)
        }
    }

    private fun evaluateUnary(node: UnaryNode): Double {
        val value = evaluate(node.operand)
        return when (node.operator) {
            "-" -> -value
            else -> throw ArithmeticException("Unsupported unary operator: ${node.operator}")
        }
    }

    private fun evaluateBinary(node: BinaryNode): Double {
        val operation = operations.findBySymbol(node.operator)
            ?: throw ArithmeticException("Unsupported binary operator: ${node.operator}")
        return operation.apply(evaluate(node.left), evaluate(node.right))
    }
}
