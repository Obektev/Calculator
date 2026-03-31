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

    fun displayText(): String = engine.display

    fun rows(): List<List<String>> = layout

    fun handleToken(token: String) {
        buttonLookup[token]?.press(engine)
    }

    companion object {
        val DEFAULT_LAYOUT = listOf(
            listOf("CE", "C", "%", "/"),
            listOf("7", "8", "9", "*"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("+/-", "0", ".", "="),
        )
    }
}
