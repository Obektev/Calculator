package me.obektev.calc.mvvm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CalculatorViewModel(
    private val model: CalculatorModel = CalculatorModel(),
) {
    var uiState by mutableStateOf(
        CalculatorUiState(
            display = model.displayText(),
            angleMode = model.angleModeText(),
            memoryActive = model.hasMemoryIndicator(),
            rows = model.rows(),
        ),
    )
        private set

    fun onToken(token: String) {
        model.handleToken(token)
        uiState = uiState.copy(
            display = model.displayText(),
            angleMode = model.angleModeText(),
            memoryActive = model.hasMemoryIndicator(),
        )
    }
}
