package me.obektev.calc.mvvm

data class CalculatorUiState(
    val display: String = "0",
    val rows: List<List<String>> = emptyList(),
)
