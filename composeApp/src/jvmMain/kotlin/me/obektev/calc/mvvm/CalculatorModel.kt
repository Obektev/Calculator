package me.obektev.calc.mvvm

import me.obektev.calc.CalculatorEngine
import me.obektev.calc.CalculatorButtonFactory
import me.obektev.calc.StandardCalculatorButtonFactory

class CalculatorModel(
    private val engine: CalculatorEngine = CalculatorEngine(),
    buttonFactory: CalculatorButtonFactory = StandardCalculatorButtonFactory(),
    private val layout: List<List<String>> = DEFAULT_LAYOUT,
) {
    private val buttons = buttonFactory.createRows(layout)
    private val buttonLookup = buttons.flatten().associateBy { it.label }
    private val commandHistory = mutableListOf<CalculatorCommand>()
    private val commandLabels = mutableListOf<String>()
    private val commandContext = CommandContext(engine) { token ->
        buttonLookup[token]?.press(engine)
        buttonLookup.containsKey(token)
    }

    fun displayText(): String = engine.expressionDisplay

    fun angleModeText(): String = engine.angleMode.name

    fun hasMemoryIndicator(): Boolean = engine.hasMemoryValue

    fun rows(): List<List<String>> = layout

    fun history(): List<String> = commandLabels.asReversed()

    fun canUndo(): Boolean = commandHistory.isNotEmpty()

    fun handleToken(token: String) {
        val command = TokenCommand(token)
        if (command.execute(commandContext)) {
            commandHistory.add(command)
            commandLabels.add(command.label)
        }
    }

    fun undoLastCommand() {
        val command = commandHistory.removeLastOrNull() ?: return
        command.undo(commandContext)
        commandLabels.removeLastOrNull()
    }

    companion object {
        val DEFAULT_LAYOUT = listOf(
            listOf("DEG", "RAD", "MC", "MR"),
            listOf("MS", "M+", "M-", "%"),
            listOf("sin", "cos", "tan", "ln"),
            listOf("sqrt", "x2", "1/x", "%"),
            listOf("CE", "C", "/", "="),
            listOf("7", "8", "9", "*"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("+/-", "0", ".", "="),
        )
    }
}
