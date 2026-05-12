package me.obektev.calc.config

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
)

enum class StyleProfile(
    val id: String,
    val description: String,
    val backgroundColor: String,
    val primaryButtonColor: String,
    val operatorButtonColor: String,
    val controlButtonColor: String,
    val memoryButtonColor: String,
    val engineeringButtonColor: String,
    val contentColor: String,
    val dark: Boolean,
) {
    LOW_VISION_LIGHT(
        id = "lowVisionLight",
        description = "Интерфейс для слабовидящих: светлая высококонтрастная тема",
        backgroundColor = "#FFFFFF",
        primaryButtonColor = "#101820",
        operatorButtonColor = "#0057B8",
        controlButtonColor = "#B00020",
        memoryButtonColor = "#34568B",
        engineeringButtonColor = "#00796B",
        contentColor = "#FFFFFF",
        dark = false,
    ),
    LOW_VISION_DARK(
        id = "lowVisionDark",
        description = "Интерфейс для слабовидящих: затемненная высококонтрастная тема",
        backgroundColor = "#121212",
        primaryButtonColor = "#2D2D2D",
        operatorButtonColor = "#F9A825",
        controlButtonColor = "#D32F2F",
        memoryButtonColor = "#1976D2",
        engineeringButtonColor = "#00897B",
        contentColor = "#FFFFFF",
        dark = true,
    ),
    MASCULINE(
        id = "masculine",
        description = "Интерфейс по гендерному признаку: мужской стиль",
        backgroundColor = "#F3F7FA",
        primaryButtonColor = "#263238",
        operatorButtonColor = "#1565C0",
        controlButtonColor = "#6D4C41",
        memoryButtonColor = "#455A64",
        engineeringButtonColor = "#00695C",
        contentColor = "#FFFFFF",
        dark = false,
    ),
    FEMININE(
        id = "feminine",
        description = "Интерфейс по гендерному признаку: женский стиль",
        backgroundColor = "#FFF5F8",
        primaryButtonColor = "#6A1B4D",
        operatorButtonColor = "#AD1457",
        controlButtonColor = "#C2185B",
        memoryButtonColor = "#7B1FA2",
        engineeringButtonColor = "#00838F",
        contentColor = "#FFFFFF",
        dark = false,
    ),
    CHILDREN(
        id = "children",
        description = "Интерфейс по возрасту: для детей",
        backgroundColor = "#FFFDE7",
        primaryButtonColor = "#00796B",
        operatorButtonColor = "#F57C00",
        controlButtonColor = "#D84315",
        memoryButtonColor = "#5E35B1",
        engineeringButtonColor = "#0288D1",
        contentColor = "#FFFFFF",
        dark = false,
    ),
    YOUTH(
        id = "youth",
        description = "Интерфейс по возрасту: для молодежи",
        backgroundColor = "#F5F7FB",
        primaryButtonColor = "#263238",
        operatorButtonColor = "#7B1FA2",
        controlButtonColor = "#C62828",
        memoryButtonColor = "#3949AB",
        engineeringButtonColor = "#00897B",
        contentColor = "#FFFFFF",
        dark = false,
    ),
    ADULT(
        id = "adult",
        description = "Интерфейс по возрасту: для среднего возраста",
        backgroundColor = "#FAFAFA",
        primaryButtonColor = "#37474F",
        operatorButtonColor = "#B05E27",
        controlButtonColor = "#8A3D3D",
        memoryButtonColor = "#4A6FA5",
        engineeringButtonColor = "#2D8F85",
        contentColor = "#FFFFFF",
        dark = false,
    ),
    SENIOR(
        id = "senior",
        description = "Интерфейс по возрасту: для пожилого возраста",
        backgroundColor = "#FFFFFF",
        primaryButtonColor = "#000000",
        operatorButtonColor = "#0D47A1",
        controlButtonColor = "#B71C1C",
        memoryButtonColor = "#1B5E20",
        engineeringButtonColor = "#4A148C",
        contentColor = "#FFFFFF",
        dark = false,
    );

    companion object {
        fun fromId(value: String?): StyleProfile? {
            return entries.firstOrNull { it.id.equals(value, ignoreCase = true) }
        }
    }
}

data class AppConfig(
    val windowWidth: Int,
    val windowHeight: Int,
    val backgroundColor: String,
    val fontFamily: String,
    val fontSize: Int,
    val styleProfile: StyleProfile,
    val database: DatabaseConfig,
) {
    companion object {
        fun default(): AppConfig {
            val style = StyleProfile.ADULT
            return AppConfig(
                windowWidth = 520,
                windowHeight = 760,
                backgroundColor = style.backgroundColor,
                fontFamily = "SansSerif",
                fontSize = 18,
                styleProfile = style,
                database = DatabaseConfig(
                    url = "jdbc:sqlite:calculator.db",
                    user = "calculator",
                    password = "",
                ),
            )
        }
    }
}

data class AppConfigResult(
    val config: AppConfig,
    val messages: List<String>,
    val source: Path?,
) {
    val hasErrors: Boolean
        get() = messages.isNotEmpty()
}

object AppConfigLoader {
    private val hexColorPattern = Regex("^#[0-9A-Fa-f]{6}$")

    fun load(path: Path? = null): AppConfigResult {
        val resolvedPath = path ?: findDefaultConfigPath()
        if (resolvedPath == null || Files.notExists(resolvedPath)) {
            return AppConfigResult(
                config = AppConfig.default(),
                messages = listOf("Конфигурационный файл не найден. Использованы настройки по умолчанию."),
                source = resolvedPath,
            )
        }

        val properties = Properties()
        return try {
            Files.newInputStream(resolvedPath).use(properties::load)
            parse(properties, resolvedPath)
        } catch (exception: IOException) {
            AppConfigResult(
                config = AppConfig.default(),
                messages = listOf("Не удалось прочитать конфигурационный файл: ${exception.message}"),
                source = resolvedPath,
            )
        } catch (exception: IllegalArgumentException) {
            AppConfigResult(
                config = AppConfig.default(),
                messages = listOf("Неверный формат конфигурационного файла: ${exception.message}"),
                source = resolvedPath,
            )
        }
    }

    private fun parse(properties: Properties, source: Path): AppConfigResult {
        val defaults = AppConfig.default()
        val messages = mutableListOf<String>()
        val style = parseStyle(properties, defaults.styleProfile, messages)

        val config = AppConfig(
            windowWidth = readInt(properties, "window.width", defaults.windowWidth, 320, 1800, messages),
            windowHeight = readInt(properties, "window.height", defaults.windowHeight, 420, 1400, messages),
            backgroundColor = readColor(properties, "ui.background", style.backgroundColor, messages),
            fontFamily = readText(properties, "ui.font.family", defaults.fontFamily, messages),
            fontSize = readInt(properties, "ui.font.size", defaults.fontSize, 12, 36, messages),
            styleProfile = style,
            database = DatabaseConfig(
                url = readText(properties, "db.url", defaults.database.url, messages),
                user = readText(properties, "db.user", defaults.database.user, messages),
                password = properties.getProperty("db.password", defaults.database.password),
            ),
        )

        return AppConfigResult(config = config, messages = messages, source = source)
    }

    private fun parseStyle(
        properties: Properties,
        defaultValue: StyleProfile,
        messages: MutableList<String>,
    ): StyleProfile {
        val rawValue = properties.getProperty("ui.style")
        if (rawValue.isNullOrBlank()) {
            messages += "Отсутствует параметр ui.style. Использован стиль ${defaultValue.id}."
            return defaultValue
        }

        return StyleProfile.fromId(rawValue) ?: run {
            messages += "Неизвестный стиль ui.style=$rawValue. Использован стиль ${defaultValue.id}."
            defaultValue
        }
    }

    private fun readText(
        properties: Properties,
        key: String,
        defaultValue: String,
        messages: MutableList<String>,
    ): String {
        val value = properties.getProperty(key)
        if (value.isNullOrBlank()) {
            messages += "Отсутствует параметр $key. Использовано значение $defaultValue."
            return defaultValue
        }
        return value.trim()
    }

    private fun readInt(
        properties: Properties,
        key: String,
        defaultValue: Int,
        min: Int,
        max: Int,
        messages: MutableList<String>,
    ): Int {
        val value = properties.getProperty(key)
        if (value.isNullOrBlank()) {
            messages += "Отсутствует параметр $key. Использовано значение $defaultValue."
            return defaultValue
        }

        val parsed = value.toIntOrNull()
        if (parsed == null || parsed !in min..max) {
            messages += "Параметр $key должен быть числом от $min до $max. Использовано значение $defaultValue."
            return defaultValue
        }
        return parsed
    }

    private fun readColor(
        properties: Properties,
        key: String,
        defaultValue: String,
        messages: MutableList<String>,
    ): String {
        val value = properties.getProperty(key)
        if (value.isNullOrBlank()) {
            messages += "Отсутствует параметр $key. Использовано значение $defaultValue."
            return defaultValue
        }
        if (!hexColorPattern.matches(value.trim())) {
            messages += "Параметр $key должен иметь формат #RRGGBB. Использовано значение $defaultValue."
            return defaultValue
        }
        return value.trim()
    }

    private fun findDefaultConfigPath(): Path? {
        val explicitPath = System.getProperty("calculator.config")?.takeIf { it.isNotBlank() }
        if (explicitPath != null) {
            return Path.of(explicitPath)
        }

        val envPath = System.getenv("CALCULATOR_CONFIG")?.takeIf { it.isNotBlank() }
        if (envPath != null) {
            return Path.of(envPath)
        }

        return listOf(
            Path.of("config", "application.properties"),
            Path.of("composeApp", "config", "application.properties"),
        ).firstOrNull(Files::exists) ?: Path.of("config", "application.properties")
    }
}
