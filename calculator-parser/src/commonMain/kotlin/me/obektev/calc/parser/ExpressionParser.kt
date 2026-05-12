package me.obektev.calc.parser

class ExpressionParseException(message: String) : IllegalArgumentException(message)

class ExpressionParser {
    fun parse(expression: String): ExpressionNode {
        val tokenizer = Tokenizer(expression)
        val parser = Parser(tokenizer.tokenize())
        return parser.parse()
    }
}

private sealed interface Token {
    data class Number(val value: Double) : Token
    data class Operator(val value: String) : Token
    data object LeftParenthesis : Token
    data object RightParenthesis : Token
    data object End : Token
}

private class Tokenizer(private val expression: String) {
    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        var index = 0

        while (index < expression.length) {
            val char = expression[index]
            when {
                char.isWhitespace() -> index++
                char.isDigit() || char == '.' || char == ',' -> {
                    val start = index
                    index++
                    while (index < expression.length) {
                        val current = expression[index]
                        if (!current.isDigit() && current != '.' && current != ',') {
                            break
                        }
                        index++
                    }
                    val text = expression.substring(start, index).replace(',', '.')
                    val value = text.toDoubleOrNull()
                        ?: throw ExpressionParseException("Некорректное число: $text")
                    tokens += Token.Number(value)
                }
                char in charArrayOf('+', '-', '*', '/') -> {
                    tokens += Token.Operator(char.toString())
                    index++
                }
                char == '(' -> {
                    tokens += Token.LeftParenthesis
                    index++
                }
                char == ')' -> {
                    tokens += Token.RightParenthesis
                    index++
                }
                else -> throw ExpressionParseException("Недопустимый символ: $char")
            }
        }

        tokens += Token.End
        return tokens
    }
}

private class Parser(private val tokens: List<Token>) {
    private var position = 0

    fun parse(): ExpressionNode {
        val expression = parseExpression()
        if (current() !is Token.End) {
            throw ExpressionParseException("Лишний токен после конца выражения")
        }
        return expression
    }

    private fun parseExpression(): ExpressionNode {
        var node = parseTerm()
        while (matchOperator("+") || matchOperator("-")) {
            val operator = previousOperator()
            val right = parseTerm()
            node = BinaryNode(operator, node, right)
        }
        return node
    }

    private fun parseTerm(): ExpressionNode {
        var node = parseFactor()
        while (matchOperator("*") || matchOperator("/")) {
            val operator = previousOperator()
            val right = parseFactor()
            node = BinaryNode(operator, node, right)
        }
        return node
    }

    private fun parseFactor(): ExpressionNode {
        if (matchOperator("+")) {
            return parseFactor()
        }
        if (matchOperator("-")) {
            return UnaryNode("-", parseFactor())
        }

        return when (val token = current()) {
            is Token.Number -> {
                advance()
                NumberNode(token.value)
            }
            Token.LeftParenthesis -> {
                advance()
                val node = parseExpression()
                if (current() !is Token.RightParenthesis) {
                    throw ExpressionParseException("Не закрыта скобка")
                }
                advance()
                node
            }
            else -> throw ExpressionParseException("Ожидалось число или скобка")
        }
    }

    private fun matchOperator(operator: String): Boolean {
        val token = current()
        if (token is Token.Operator && token.value == operator) {
            advance()
            return true
        }
        return false
    }

    private fun previousOperator(): String {
        val token = tokens[position - 1]
        return (token as Token.Operator).value
    }

    private fun current(): Token = tokens[position]

    private fun advance() {
        if (position < tokens.lastIndex) {
            position++
        }
    }
}
