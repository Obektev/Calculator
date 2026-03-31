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
import androidx.compose.runtime.remember
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
import me.obektev.calc.mvvm.CalculatorViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = remember { CalculatorViewModel() }
        val uiState = viewModel.uiState

        fun handleInput(token: String) {
            viewModel.onToken(token)
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

                            Key.Backspace -> {
                                handleInput("CE")
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
                                } else if (char == '%') {
                                    handleInput("%")
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
                    value = uiState.display,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.End),
                )

                uiState.rows.forEach { row ->
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