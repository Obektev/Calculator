package me.obektev.calc.mvvm

import me.obektev.calc.CalculatorEngine
import me.obektev.calc.CalculatorButtonFactory
import me.obektev.calc.StandardCalculatorButtonFactory

class CalculatorModel(
    private val engine: CalculatorEngine = CalculatorEngine(),
    buttonFactory: CalculatorButtonFactory = StandardCalculatorButtonFactory(),
    private val layoutFacade: CalculatorLayoutFacade = DefaultCalculatorLayoutFacade(),
) {
    private val buttons = buttonFactory.createRows(layoutFacade.basicRows() + layoutFacade.engineeringRows())
    private val buttonLookup = buttons.flatten().associateBy { it.label }
    private val commandHistory = mutableListOf<CalculatorCommand>()
    private val commandLabels = mutableListOf<String>()
    private val commandContext = CommandContext(engine) { token ->
        buttonLookup[token]?.press(engine)
        buttonLookup.containsKey(token)
    }
    private var engineeringModeEnabled: Boolean = false

    fun displayText(): String = engine.expressionDisplay

    fun angleModeText(): String = engine.angleMode.name

    fun hasMemoryIndicator(): Boolean = engine.hasMemoryValue

    fun basicRows(): List<List<String>> = layoutFacade.basicRows()

    fun engineeringRows(): List<List<String>> = layoutFacade.engineeringRows()

    fun isEngineeringMode(): Boolean = engineeringModeEnabled

    fun buttonGroup(token: String): ButtonGroup = layoutFacade.buttonGroup(token)

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

    fun toggleMode() {
        engineeringModeEnabled = !engineeringModeEnabled
    }
}
