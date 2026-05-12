package me.obektev.calc.developers

import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path

data class DeveloperLibraryResult(
    val text: String,
    val loadedDynamically: Boolean,
)

object DynamicDeveloperLibraryLoader {
    private const val PROVIDER_CLASS = "me.obektev.calc.developers.CalculatorDeveloperInfoProvider"

    fun load(path: Path = defaultLibraryPath()): DeveloperLibraryResult {
        if (Files.notExists(path)) {
            return DeveloperLibraryResult(
                text = "Динамическая библиотека не найдена: ${path.toAbsolutePath()}\n" +
                    "Соберите модуль командой ./gradlew :calculator-developers:jvmJar и скопируйте JAR в dynamic-libs.",
                loadedDynamically = false,
            )
        }

        val classLoader = URLClassLoader(
            arrayOf(path.toUri().toURL()),
            DeveloperInfoProvider::class.java.classLoader,
        )
        return classLoader.use { loader ->
            val provider = loader
                .loadClass(PROVIDER_CLASS)
                .getDeclaredConstructor()
                .newInstance() as DeveloperInfoProvider
            DeveloperLibraryResult(
                text = buildString {
                    appendLine(provider.projectInfo())
                    appendLine()
                    appendLine("Разработчики:")
                    provider.developers().forEach { appendLine("- $it") }
                }.trim(),
                loadedDynamically = true,
            )
        }
    }

    private fun defaultLibraryPath(): Path {
        return listOf(
            Path.of("dynamic-libs", "calculator-developers.jar"),
            Path.of("composeApp", "dynamic-libs", "calculator-developers.jar"),
            Path.of("calculator-developers", "build", "libs", "calculator-developers-jvm.jar"),
        ).firstOrNull(Files::exists) ?: Path.of("dynamic-libs", "calculator-developers.jar")
    }
}
