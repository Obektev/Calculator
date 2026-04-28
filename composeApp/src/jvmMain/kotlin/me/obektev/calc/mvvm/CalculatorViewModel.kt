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
            basicRows = model.basicRows(),
            engineeringRows = model.engineeringRows(),
            engineeringModeEnabled = model.isEngineeringMode(),
            commandHistory = model.history(),
            canUndo = model.canUndo(),
        ),
    )
        private set

    fun onToken(token: String) {
        model.handleToken(token)
        syncState()
    }

    fun onUndo() {
        model.undoLastCommand()
        syncState()
    }

    fun onToggleCalculatorMode() {
        model.toggleMode()
        syncState()
    }

    fun buttonGroup(token: String): ButtonGroup {
        return model.buttonGroup(token)
    }

    private fun syncState() {
        uiState = uiState.copy(
            display = model.displayText(),
            angleMode = model.angleModeText(),
            memoryActive = model.hasMemoryIndicator(),
            basicRows = model.basicRows(),
            engineeringRows = model.engineeringRows(),
            engineeringModeEnabled = model.isEngineeringMode(),
            commandHistory = model.history(),
            canUndo = model.canUndo(),
        )
    }
}
