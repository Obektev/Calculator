package me.obektev.calc.resources

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

object ResourceLoader {
    fun loadDynamicText(path: Path): String {
        return Files.newInputStream(path).use { stream ->
            String(stream.readAllBytes(), StandardCharsets.UTF_8)
        }
    }

    fun dynamicResourceExists(path: Path): Boolean {
        return Files.exists(path)
    }
}
