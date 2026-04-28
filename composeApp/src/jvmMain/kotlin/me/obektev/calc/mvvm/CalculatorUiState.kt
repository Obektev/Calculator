package me.obektev.calc.mvvm

data class CalculatorUiState(
    val display: String = "0",
    val angleMode: String = "RAD",
    val memoryActive: Boolean = false,
    val basicRows: List<List<String>> = emptyList(),
    val engineeringRows: List<List<String>> = emptyList(),
    val engineeringModeEnabled: Boolean = false,
    val commandHistory: List<String> = emptyList(),
    val canUndo: Boolean = false,
)
