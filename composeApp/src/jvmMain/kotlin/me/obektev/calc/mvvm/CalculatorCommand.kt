package me.obektev.calc.mvvm

import me.obektev.calc.CalculatorEngine
import me.obektev.calc.EngineSnapshot

interface CalculatorCommand {
    val label: String

    fun execute(context: CommandContext): Boolean

    fun undo(context: CommandContext)
}

class CommandContext(
    val engine: CalculatorEngine,
    private val tokenHandler: (String) -> Boolean,
) {
    fun dispatch(token: String): Boolean = tokenHandler(token)
}

class TokenCommand(
    override val label: String,
) : CalculatorCommand {
    private var previousSnapshot: EngineSnapshot? = null

    override fun execute(context: CommandContext): Boolean {
        previousSnapshot = context.engine.saveSnapshot()
        return context.dispatch(label)
    }

    override fun undo(context: CommandContext) {
        previousSnapshot?.let { context.engine.restoreSnapshot(it) }
    }
}
