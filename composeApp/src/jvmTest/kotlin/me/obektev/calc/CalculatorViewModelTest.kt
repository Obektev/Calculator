package me.obektev.calc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import me.obektev.calc.mvvm.CalculatorViewModel

class CalculatorViewModelTest {

    @Test
    fun shouldCalculateThroughViewModel() {
        val viewModel = CalculatorViewModel()

        viewModel.onToken("1")
        viewModel.onToken("0")
        viewModel.onToken("+")
        viewModel.onToken("5")

        assertEquals("10 + 5", viewModel.uiState.display)

        viewModel.onToken("=")

        assertEquals("15", viewModel.uiState.display)
    }

    @Test
    fun shouldApplyFunctionalButtonsThroughViewModel() {
        val viewModel = CalculatorViewModel()

        viewModel.onToken("9")
        viewModel.onToken("+/-")
        viewModel.onToken("%")

        assertEquals("-0.09", viewModel.uiState.display)
    }

    @Test
    fun shouldApplyEngineeringAndMemoryThroughViewModel() {
        val viewModel = CalculatorViewModel()

        viewModel.onToken("8")
        viewModel.onToken("1")
        viewModel.onToken("sqrt")
        viewModel.onToken("MS")
        viewModel.onToken("C")
        viewModel.onToken("MR")

        assertEquals("9", viewModel.uiState.display)
    }

    @Test
    fun shouldUpdateModeAndMemoryIndicators() {
        val viewModel = CalculatorViewModel()

        assertEquals("RAD", viewModel.uiState.angleMode)
        assertFalse(viewModel.uiState.memoryActive)

        viewModel.onToken("DEG")
        viewModel.onToken("9")
        viewModel.onToken("0")
        viewModel.onToken("sin")
        assertEquals("1", viewModel.uiState.display)
        assertEquals("DEG", viewModel.uiState.angleMode)

        viewModel.onToken("MS")
        assertTrue(viewModel.uiState.memoryActive)

        viewModel.onToken("MC")
        assertFalse(viewModel.uiState.memoryActive)
    }

    @Test
    fun shouldTrackHistoryAndUndoLastCommand() {
        val viewModel = CalculatorViewModel()

        viewModel.onToken("9")
        viewModel.onToken("+")
        viewModel.onToken("1")

        assertTrue(viewModel.uiState.commandHistory.isNotEmpty())
        assertTrue(viewModel.uiState.canUndo)
        assertEquals("9 + 1", viewModel.uiState.display)

        viewModel.onUndo()

        assertEquals("9 +", viewModel.uiState.display)
        assertEquals("+", viewModel.uiState.commandHistory.first())
    }

    @Test
    fun shouldToggleEngineeringMode() {
        val viewModel = CalculatorViewModel()

        assertFalse(viewModel.uiState.engineeringModeEnabled)

        viewModel.onToggleCalculatorMode()

        assertTrue(viewModel.uiState.engineeringModeEnabled)
    }
}
