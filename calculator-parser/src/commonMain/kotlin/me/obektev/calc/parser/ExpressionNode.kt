package me.obektev.calc.parser

sealed interface ExpressionNode

data class NumberNode(val value: Double) : ExpressionNode

data class UnaryNode(
    val operator: String,
    val operand: ExpressionNode,
) : ExpressionNode

data class BinaryNode(
    val operator: String,
    val left: ExpressionNode,
    val right: ExpressionNode,
) : ExpressionNode
