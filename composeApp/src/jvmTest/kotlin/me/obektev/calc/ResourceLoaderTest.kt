package me.obektev.calc

import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertTrue
import me.obektev.calc.resources.ResourceLoader

class ResourceLoaderTest {
    @Test
    fun shouldLoadDynamicTextResource() {
        val path = createTempFile(prefix = "resource-loader", suffix = ".txt")
        path.writeText("Динамический ресурс загружен")

        try {
            assertTrue(ResourceLoader.dynamicResourceExists(path))

            val loadedText = ResourceLoader.loadDynamicText(path)

            assertTrue(loadedText.contains("Динамический ресурс"))
        } finally {
            path.deleteIfExists()
        }
    }
}
