package me.obektev.calc

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.MenuBar

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Simple Calculator",
    ) {
        MenuBar {
            Menu("Приложение") {
                Item("Выход", onClick = ::exitApplication)
            }
        }
        App()
    }
}