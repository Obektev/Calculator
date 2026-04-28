package me.obektev.calc.mvvm

data class CalculatorUiState(
    val display: String = "0",
    val angleMode: String = "RAD",
    val memoryActive: Boolean = false,
    val rows: List<List<String>> = emptyList(),
    val commandHistory: List<String> = emptyList(),
    val canUndo: Boolean = false,
)
