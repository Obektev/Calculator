package me.obektev.calc

import kotlin.io.path.createTempFile
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import me.obektev.calc.config.AppConfigLoader
import me.obektev.calc.config.StyleProfile

class AppConfigLoaderTest {
    @Test
    fun loadsValidPropertiesFile() {
        val path = createTempFile(suffix = ".properties")
        path.writeText(
            """
                window.width=640
                window.height=720
                ui.style=senior
                ui.background=#FFFFFF
                ui.font.family=Serif
                ui.font.size=22
                db.url=jdbc:sqlite:test.db
                db.user=tester
                db.password=secret
            """.trimIndent(),
        )

        val result = AppConfigLoader.load(path)

        assertFalse(result.hasErrors)
        assertEquals(640, result.config.windowWidth)
        assertEquals(720, result.config.windowHeight)
        assertEquals(StyleProfile.SENIOR, result.config.styleProfile)
        assertEquals("jdbc:sqlite:test.db", result.config.database.url)
    }

    @Test
    fun reportsInvalidValuesAndUsesDefaults() {
        val path = createTempFile(suffix = ".properties")
        path.writeText(
            """
                window.width=wide
                window.height=100
                ui.style=unknown
                ui.background=black
                ui.font.family=
                ui.font.size=200
                db.url=
                db.user=
            """.trimIndent(),
        )

        val result = AppConfigLoader.load(path)

        assertTrue(result.hasErrors)
        assertEquals(520, result.config.windowWidth)
        assertEquals(760, result.config.windowHeight)
        assertEquals(StyleProfile.ADULT, result.config.styleProfile)
        assertEquals("#B05E27", result.config.styleProfile.operatorButtonColor)
    }
}
