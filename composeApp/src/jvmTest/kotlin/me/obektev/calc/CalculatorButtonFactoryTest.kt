package me.obektev.calc

import kotlin.test.Test
import kotlin.test.assertTrue

class CalculatorButtonFactoryTest {

    private val factory = StandardCalculatorButtonFactory()

    @Test
    fun shouldCreateDigitButton() {
        val button = factory.createRows(listOf(listOf("7"))).first().first()
        assertTrue(button is DigitButton)
    }

    @Test
    fun shouldCreateOperatorButton() {
        val button = factory.createRows(listOf(listOf("+"))).first().first()
        assertTrue(button is OperatorButton)
    }

    @Test
    fun shouldCreateFunctionalButton() {
        val button = factory.createRows(listOf(listOf("CE"))).first().first()
        assertTrue(button is ClearEntryButton)
    }

    @Test
    fun shouldCreateScientificButton() {
        val button = factory.createRows(listOf(listOf("sqrt"))).first().first()
        assertTrue(button is SquareRootButton)
    }

    @Test
    fun shouldCreateMemoryButton() {
        val button = factory.createRows(listOf(listOf("MR"))).first().first()
        assertTrue(button is MemoryRecallButton)
    }
}
