package me.obektev.calc

import kotlin.test.Test
import kotlin.test.assertEquals
import me.obektev.calc.mvvm.CalculatorViewModel

class CalculatorViewModelTest {

    @Test
    fun shouldCalculateThroughViewModel() {
        val viewModel = CalculatorViewModel()

        viewModel.onToken("1")
        viewModel.onToken("0")
        viewModel.onToken("+")
        viewModel.onToken("5")
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
}
