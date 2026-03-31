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
        var display by remember { mutableStateOf("0") }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val buttonSpacing = 8.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
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
                                onClick = {
                                    display = when {
                                        label == "C" -> "0"
                                        display == "0" -> label
                                        else -> display + label
                                    }
                                },
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