package me.obektev.calc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import calculator.composeapp.generated.resources.Res
import calculator.composeapp.generated.resources.app_badge
import calculator.composeapp.generated.resources.kolomiets_tp
import kotlinx.coroutines.launch
import me.obektev.calc.config.AppConfig
import me.obektev.calc.config.StyleProfile
import me.obektev.calc.developers.DynamicDeveloperLibraryLoader
import me.obektev.calc.mvvm.ButtonGroup
import me.obektev.calc.mvvm.CalculatorViewModel
import me.obektev.calc.resources.ResourceLoader
import org.jetbrains.compose.resources.painterResource
import java.nio.file.Path

@Composable
@Preview
fun App(
    config: AppConfig = AppConfig.default(),
    configurationMessages: List<String> = emptyList(),
    configurationSource: String = "preview",
) {
    val palette = remember(config) { AppPalette.from(config) }
    MaterialTheme(
        colorScheme = colorScheme(config.styleProfile, palette),
        typography = appTypography(config),
    ) {
        val viewModel = remember { CalculatorViewModel() }
        val uiState = viewModel.uiState
        val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var showResourceDialog by remember { mutableStateOf(false) }
        var showDevelopersDialog by remember { mutableStateOf(false) }
        var dynamicResourceText by remember { mutableStateOf("Динамический ресурс не загружен") }

        fun handleInput(token: String) {
            SoundEffects.playForToken(token)
            viewModel.onToken(token)
        }

        val buttonSpacing = 8.dp

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = palette.background,
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(modifier = Modifier.fillMaxHeight().padding(12.dp)) {
                        Text("История команд", style = MaterialTheme.typography.titleMedium)
                        Button(
                            onClick = {
                                SoundEffects.playUndo()
                                viewModel.onUndo()
                            },
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

                            when (event.key) {
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
                                        SoundEffects.playUndo()
                                        viewModel.onUndo()
                                        true
                                    } else {
                                        false
                                    }
                                }
                                else -> {
                                    val char = event.utf16CodePoint.toChar()
                                    when {
                                        char in '0'..'9' -> {
                                            handleInput(char.toString())
                                            true
                                        }
                                        char in charArrayOf('+', '-', '*', '/') -> {
                                            handleInput(char.toString())
                                            true
                                        }
                                        char == '.' || char == ',' -> {
                                            handleInput(".")
                                            true
                                        }
                                        char == '=' -> {
                                            handleInput("=")
                                            true
                                        }
                                        char == '%' -> {
                                            handleInput("%")
                                            true
                                        }
                                        else -> false
                                    }
                                }
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(buttonSpacing),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        androidx.compose.material3.Icon(
                            painter = painterResource(Res.drawable.app_badge),
                            contentDescription = "Calculator icon",
                            tint = Color.Unspecified,
                        )
                        Text("Mode: ${uiState.angleMode}")
                        Text(if (uiState.memoryActive) "Memory: M" else "Memory: -")
                    }

                    Button(
                        onClick = {
                            SoundEffects.playModeSwitch()
                            viewModel.onToggleCalculatorMode()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.primaryButton,
                            contentColor = palette.content,
                        ),
                        modifier = Modifier.fillMaxWidth().pointerHoverIcon(PointerIcon.Hand),
                    ) {
                        Text(if (uiState.engineeringModeEnabled) "Переключить на обычный" else "Переключить на инженерный")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing),
                    ) {
                        Button(
                            onClick = {
                                SoundEffects.playMenu()
                                val dynamicPath = Path.of("reports", "lab6_dynamic_note.txt")
                                dynamicResourceText = if (ResourceLoader.dynamicResourceExists(dynamicPath)) {
                                    ResourceLoader.loadDynamicText(dynamicPath)
                                } else {
                                    "Файл динамического ресурса не найден: ${dynamicPath.toAbsolutePath()}"
                                }
                                showResourceDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primaryButton,
                                contentColor = palette.content,
                            ),
                            modifier = Modifier.weight(1f).pointerHoverIcon(PointerIcon.Hand),
                        ) {
                            Text("Загрузить ресурс")
                        }
                        Button(
                            onClick = {
                                SoundEffects.playMenu()
                                dynamicResourceText = configurationText(
                                    config,
                                    configurationMessages,
                                    configurationSource,
                                )
                                showResourceDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primaryButton,
                                contentColor = palette.content,
                            ),
                            modifier = Modifier.weight(1f).pointerHoverIcon(PointerIcon.Hand),
                        ) {
                            Text("Конфигурация")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing),
                    ) {
                        Button(
                            onClick = {
                                SoundEffects.playMenu()
                                scope.launch { drawerState.open() }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primaryButton,
                                contentColor = palette.content,
                            ),
                            modifier = Modifier.weight(1f).pointerHoverIcon(PointerIcon.Hand),
                        ) {
                            Text("История")
                        }
                        Button(
                            onClick = {
                                SoundEffects.playUndo()
                                viewModel.onUndo()
                            },
                            enabled = uiState.canUndo,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primaryButton,
                                contentColor = palette.content,
                            ),
                            modifier = Modifier.weight(1f).pointerHoverIcon(PointerIcon.Hand),
                        ) {
                            Text("Undo")
                        }
                        Button(
                            onClick = {
                                SoundEffects.playMenu()
                                showDevelopersDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primaryButton,
                                contentColor = palette.content,
                            ),
                            modifier = Modifier.weight(1f).pointerHoverIcon(PointerIcon.Hand),
                        ) {
                            Text("Разработчики")
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

                    uiState.basicRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(buttonSpacing),
                        ) {
                            row.forEach { label ->
                                val buttonGroup = viewModel.buttonGroup(label)
                                Button(
                                    onClick = { handleInput(label) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = buttonColor(buttonGroup, palette),
                                        contentColor = palette.content,
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .pointerHoverIcon(PointerIcon.Hand)
                                        .padding(vertical = 2.dp),
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState.engineeringModeEnabled,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                            uiState.engineeringRows.forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing),
                                ) {
                                    row.forEach { label ->
                                        val buttonGroup = viewModel.buttonGroup(label)
                                        Button(
                                            onClick = { handleInput(label) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = buttonColor(buttonGroup, palette),
                                                contentColor = palette.content,
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .pointerHoverIcon(PointerIcon.Hand)
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

                if (showResourceDialog) {
                    AlertDialog(
                        onDismissRequest = { showResourceDialog = false },
                        title = { Text("Ресурсы приложения") },
                        text = { Text(dynamicResourceText) },
                        confirmButton = {
                            TextButton(onClick = { showResourceDialog = false }) {
                                Text("Закрыть")
                            }
                        },
                    )
                }

                if (showDevelopersDialog) {
                    DevelopersDialog(
                        onDismissRequest = { showDevelopersDialog = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun DevelopersDialog(onDismissRequest: () -> Unit) {
    val developerInfo = remember {
        LibraryReport.staticLibraryDemo() + "\n" + DynamicDeveloperLibraryLoader.load().text
    }
    val developers = remember {
        listOf(
            DeveloperCardData(
                name = "Коломиец Т. П.",
                description = "Коломиец Т. П., Иванов А. И., студенты группы 10701124, руководитель: Станкевич С.Н.",
                hasPhoto = true,
                placeholder = "КП",
            ),
            DeveloperCardData(
                name = "Иванов А. И.",
                description = "Коломиец Т. П., Иванов А. И., студенты группы 10701124, руководитель: Станкевич С.Н.",
                hasPhoto = false,
                placeholder = "ИИ",
            ),
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Разработчики") },
        text = {
            LazyColumn(
                modifier = Modifier.height(440.dp).fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(developers) { developer ->
                    DeveloperRow(developer)
                }
                item {
                    Text(
                        text = developerInfo,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Закрыть")
            }
        },
    )
}

@Composable
private fun DeveloperRow(developer: DeveloperCardData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (developer.hasPhoto) {
            Image(
                painter = painterResource(Res.drawable.kolomiets_tp),
                contentDescription = developer.name,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = developer.placeholder,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = developer.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = developer.description,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private data class DeveloperCardData(
    val name: String,
    val description: String,
    val hasPhoto: Boolean,
    val placeholder: String,
)

private data class AppPalette(
    val background: Color,
    val primaryButton: Color,
    val operatorButton: Color,
    val controlButton: Color,
    val memoryButton: Color,
    val engineeringButton: Color,
    val content: Color,
) {
    companion object {
        fun from(config: AppConfig): AppPalette {
            val style = config.styleProfile
            return AppPalette(
                background = parseColor(config.backgroundColor, parseColor(style.backgroundColor, Color.White)),
                primaryButton = parseColor(style.primaryButtonColor, Color(0xFF3A3A3A)),
                operatorButton = parseColor(style.operatorButtonColor, Color(0xFFB05E27)),
                controlButton = parseColor(style.controlButtonColor, Color(0xFF8A3D3D)),
                memoryButton = parseColor(style.memoryButtonColor, Color(0xFF4A6FA5)),
                engineeringButton = parseColor(style.engineeringButtonColor, Color(0xFF2D8F85)),
                content = parseColor(style.contentColor, Color.White),
            )
        }
    }
}

private fun buttonColor(group: ButtonGroup, palette: AppPalette): Color {
    return when (group) {
        ButtonGroup.MEMORY -> palette.memoryButton
        ButtonGroup.ENGINEERING -> palette.engineeringButton
        ButtonGroup.OPERATOR -> palette.operatorButton
        ButtonGroup.DIGIT -> palette.primaryButton
        ButtonGroup.CONTROL -> palette.controlButton
    }
}

private fun colorScheme(style: StyleProfile, palette: AppPalette): ColorScheme {
    return if (style.dark) {
        darkColorScheme(
            primary = palette.operatorButton,
            surface = palette.background,
            background = palette.background,
        )
    } else {
        lightColorScheme(
            primary = palette.operatorButton,
            surface = palette.background,
            background = palette.background,
        )
    }
}

private fun appTypography(config: AppConfig): Typography {
    val family = when (config.fontFamily.lowercase()) {
        "serif" -> FontFamily.Serif
        "monospace" -> FontFamily.Monospace
        else -> FontFamily.SansSerif
    }
    val base = TextStyle(
        fontFamily = family,
        fontSize = config.fontSize.sp,
    )
    return Typography(
        bodyMedium = base,
        titleMedium = base.copy(fontSize = (config.fontSize + 2).sp),
        headlineMedium = base.copy(fontSize = (config.fontSize + 12).sp),
        labelLarge = base.copy(fontSize = config.fontSize.sp),
    )
}

private fun parseColor(hex: String, fallback: Color): Color {
    val cleanHex = hex.removePrefix("#")
    return cleanHex.toLongOrNull(16)?.let { Color(0xFF000000 or it) } ?: fallback
}

private fun configurationText(
    config: AppConfig,
    messages: List<String>,
    source: String,
): String {
    val diagnostics = if (messages.isEmpty()) {
        "Ошибок конфигурации нет."
    } else {
        messages.joinToString(separator = "\n")
    }

    return """
        Файл: $source
        Стиль: ${config.styleProfile.id} (${config.styleProfile.description})
        Размер окна: ${config.windowWidth}x${config.windowHeight}
        Фон: ${config.backgroundColor}
        Шрифт: ${config.fontFamily}, ${config.fontSize}
        База данных: ${config.database.url}, пользователь ${config.database.user}

        Диагностика:
        $diagnostics
    """.trimIndent()
}
