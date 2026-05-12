package me.obektev.calc

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.unit.dp
import me.obektev.calc.config.AppConfigLoader

fun main() {
    val configResult = AppConfigLoader.load()

    application {
        val windowState = rememberWindowState(
            width = configResult.config.windowWidth.dp,
            height = configResult.config.windowHeight.dp,
        )

        Window(
            onCloseRequest = ::exitApplication,
            title = "Simple Calculator",
            state = windowState,
        ) {
            MenuBar {
                Menu("Приложение") {
                    Item("Выход", onClick = ::exitApplication)
                }
            }
            App(
                config = configResult.config,
                configurationMessages = configResult.messages,
                configurationSource = configResult.source?.toAbsolutePath()?.toString() ?: "не найден",
            )
        }
    }
}
