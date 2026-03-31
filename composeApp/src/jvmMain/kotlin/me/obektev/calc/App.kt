package me.obektev.calc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val calculatorRows = listOf(
    listOf("7", "8", "9", "/"),
    listOf("4", "5", "6", "*"),
    listOf("1", "2", "3", "-"),
    listOf("0", ".", "C", "+"),
    listOf("="),
)

@Composable
@Preview
fun App() {
    MaterialTheme {
        val calculatorEngine = remember { CalculatorEngine() }
        var display by remember { mutableStateOf(calculatorEngine.display) }

        fun handleInput(input: String) {
            when {
                input.all { it.isDigit() } -> calculatorEngine.inputDigit(input.toInt())
                input == "." -> calculatorEngine.inputDecimalPoint()
                input in listOf("+", "-", "*", "/") -> calculatorEngine.setOperation(input)
                input == "=" -> calculatorEngine.evaluate()
                input == "C" -> calculatorEngine.clearAll()
            }
            display = calculatorEngine.display
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val buttonSpacing = 8.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) {
                            return@onPreviewKeyEvent false
                        }

                        val handled = when (event.key) {
                            Key.Enter -> {
                                handleInput("=")
                                true
                            }

                            Key.Escape -> {
                                handleInput("C")
                                true
                            }

                            else -> {
                                val char = event.utf16CodePoint.toChar()
                                if (char in '0'..'9') {
                                    handleInput(char.toString())
                                    true
                                } else if (char in charArrayOf('+', '-', '*', '/')) {
                                    handleInput(char.toString())
                                    true
                                } else if (char == '.' || char == ',') {
                                    handleInput(".")
                                    true
                                } else if (char == '=' ) {
                                    handleInput("=")
                                    true
                                } else {
                                    false
                                }
                            }
                        }
                        handled
                    },
                verticalArrangement = Arrangement.spacedBy(buttonSpacing),
            ) {
                OutlinedTextField(
                    value = display,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.End),
                )

                calculatorRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing),
                    ) {
                        row.forEach { label ->
                            Button(
                                onClick = { handleInput(label) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 2.dp),
                            ) {
                                Text(label)
                            }
                        }
                    }
                }
            }
        }
    }
}