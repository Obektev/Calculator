package me.obektev.calc

import kotlin.test.Test
import kotlin.test.assertTrue
import me.obektev.calc.developers.DynamicDeveloperLibraryLoader

class DynamicDeveloperLibraryLoaderTest {
    @Test
    fun reportsMissingDynamicLibraryWithoutCrashing() {
        val result = DynamicDeveloperLibraryLoader.load(
            java.nio.file.Path.of("missing", "calculator-developers.jar"),
        )

        assertTrue(result.text.contains("Динамическая библиотека не найдена"))
    }
}
