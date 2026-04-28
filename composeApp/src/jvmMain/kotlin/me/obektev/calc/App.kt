package me.obektev.calc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.obektev.calc.mvvm.CalculatorViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = remember { CalculatorViewModel() }
        val uiState = viewModel.uiState
        val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        fun handleInput(token: String) {
            viewModel.onToken(token)
        }

        val buttonSpacing = 8.dp

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(modifier = Modifier.fillMaxHeight().padding(12.dp)) {
                    Text("История команд", style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = { viewModel.onUndo() },
                        enabled = uiState.canUndo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 8.dp),
                    ) {
                        Text("Отменить последнюю")
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(uiState.commandHistory) { item ->
                            Text(item, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
        ) {
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

                            Key.Z -> {
                                if (event.isCtrlPressed) {
                                    viewModel.onUndo()
                                    true
                                } else {
                                    false
                                }
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
                                } else if (char == '=') {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Mode: ${uiState.angleMode}")
                    Text(if (uiState.memoryActive) "Memory: M" else "Memory: -")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing),
                ) {
                    Button(onClick = { scope.launch { drawerState.open() } }, modifier = Modifier.weight(1f)) {
                        Text("История")
                    }
                    Button(onClick = { viewModel.onUndo() }, enabled = uiState.canUndo, modifier = Modifier.weight(1f)) {
                        Text("Undo")
                    }
                }

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
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White,
                                ),
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